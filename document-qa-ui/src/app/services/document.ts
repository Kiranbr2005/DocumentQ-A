import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Message {
  role: 'user' | 'ai';
  text: string;
  time: string;
}

@Injectable({ providedIn: 'root' })
export class DocumentService {

  private api = 'http://localhost:8089/api';
  messages: Message[] = [];

  // Generate once per browser session
  private sessionId: string = this.getOrCreateSession();

  constructor(private http: HttpClient) {}

  private getOrCreateSession(): string {
    let id = sessionStorage.getItem('docqa-session');
    if (!id) {
      id = crypto.randomUUID();
      sessionStorage.setItem('docqa-session', id);
    }
    return id;
  }

  getSessionId(): string {
    return this.sessionId;
  }

  // Headers with session ID for every request
  private headers(): HttpHeaders {
    return new HttpHeaders({
      'X-Session-Id': this.sessionId
    });
  }

  uploadFile(file: File): Observable<string> {
    const form = new FormData();
    form.append('file', file);
    return this.http.post(
      this.api + '/upload',
      form,
      { headers: this.headers(), responseType: 'text' }
    );
  }

 ask(question: string): Observable<string> {
  const activeDocId =
    sessionStorage.getItem('active-doc-id');
  return this.http.post(
    this.api + '/ask',
    { question, documentId: activeDocId },
    {
      headers: this.headers().set(
        'Content-Type', 'application/json'),
      responseType: 'text'
    }
  );
}

deleteDocument(documentId: string): Observable<string> {
  return this.http.delete(
    `${this.api}/documents/${documentId}`,
    { headers: this.headers(), responseType: 'text' }
  );
}

  getDocuments(): Observable<any[]> {
    return this.http.get<any[]>(
      this.api + '/documents',
      { headers: this.headers() }
    );
  }

  clearSession(): Observable<string> {
    return this.http.delete(
      this.api + '/session',
      { headers: this.headers(), responseType: 'text' }
    );
  }

  clearMessages() {
    this.messages = [];
  }
}