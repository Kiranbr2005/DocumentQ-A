import { Component, AfterViewChecked, ElementRef, ViewChild, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { DocumentService, Message } from '../../services/document';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule,FormsModule,MatCardModule,MatButtonModule,MatIconModule,MatInputModule,MatFormFieldModule],
  templateUrl: './chat.html',
  styleUrls: ['./chat.scss']
})
export class ChatComponent implements AfterViewChecked {

  @ViewChild('messagesEnd') messagesEnd!: ElementRef;

  question = '';
  loading = false;

  // Use service messages (survives hot reload)
  get messages(): Message[] {
    return this.docService.messages;
  }

  constructor(
    private docService: DocumentService,
     private cdr: ChangeDetectorRef
  ) {}

  ngAfterViewChecked() {
    // this.messagesEnd?.nativeElement?.scrollIntoView({ behavior: 'smooth' });
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

    this.docService.ask(userMsg).subscribe({
      next: (answer) => {
        this.messages.push({
          role: 'ai',
          text: answer,
          time: new Date().toLocaleTimeString()
          
        });
        this.loading = false;
         this.cdr.detectChanges(); 
      },
      error: () => {
        this.messages.push({
          role: 'ai',
          text: 'Error. Please try again.',
          time: new Date().toLocaleTimeString()
        });
        this.loading = false;
      }
    });
  }

  clearChat() {
    this.docService.clearMessages();
  }

  onKeyDown(event: KeyboardEvent) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.send();
    }
  }
}