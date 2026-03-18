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

  // Store messages here so they survive hot reload
  messages: Message[] = [];

  constructor(private http: HttpClient) {}

  uploadFile(file: File): Observable<string> {
    const form = new FormData();
    form.append('file', file);
    return this.http.post(this.api + '/upload', form, { responseType: 'text' });
  }

  ask(question: string): Observable<string> {
    return this.http.post(
      this.api + '/ask',
      { question },
      {
        headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
        responseType: 'text'
      }
    );
  }

  clearMessages() {
    this.messages = [];
  }
}