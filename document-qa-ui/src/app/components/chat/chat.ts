import {
  Component, AfterViewChecked,
  ElementRef, ViewChild, ChangeDetectorRef
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { DocumentService, Message } from '../../services/document';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [
    CommonModule, FormsModule,
    MatCardModule, MatButtonModule,
    MatIconModule, MatInputModule,
    MatFormFieldModule, MatTooltipModule,
    MatSnackBarModule
  ],
  templateUrl: './chat.html',
  styleUrls: ['./chat.scss']
})
export class ChatComponent implements AfterViewChecked {

  @ViewChild('messagesEnd') messagesEnd!: ElementRef;

  question = '';
  loading = false;
  private shouldScroll = false;

  get messages(): Message[] {
    return this.docService.messages;
  }

  constructor(
    public docService: DocumentService,
    private cdr: ChangeDetectorRef,
    private snackBar: MatSnackBar
  ) {}

  ngAfterViewChecked() {
    if (this.shouldScroll) {
      this.messagesEnd?.nativeElement
          ?.scrollIntoView({ behavior: 'smooth' });
      this.shouldScroll = false;
    }
  }

  send() {
    if (!this.question.trim() || this.loading) return;

    const userMsg = this.question.trim();
    this.messages.push({
      role: 'user',
      text: userMsg,
      time: new Date().toLocaleTimeString()
    });
    this.question = '';
    this.loading = true;
    this.shouldScroll = true;

    this.docService.ask(userMsg).subscribe({
      next: (answer) => {
        this.messages.push({
          role: 'ai',
          text: answer,
          time: new Date().toLocaleTimeString()
        });
        this.loading = false;
        this.shouldScroll = true;
        this.cdr.detectChanges();
      },
      error: () => {
        this.messages.push({
          role: 'ai',
          text: 'Error connecting to server. Please try again.',
          time: new Date().toLocaleTimeString()
        });
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  clearChat() {
    this.docService.clearMessages();
  }

  clearSession() {
    this.docService.clearSession().subscribe({
      next: () => {
        this.docService.clearMessages();
        this.snackBar.open(
          'Session cleared. Upload a new document.',
          'Close', { duration: 3000 }
        );
      }
    });
  }

  onKeyDown(event: KeyboardEvent) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.send();
    }
  }
  get activeDocName(): string {
  return sessionStorage.getItem('active-doc-name') || '';
}
}
