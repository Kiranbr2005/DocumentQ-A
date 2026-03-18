import { Component } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule
  ],
  template: `
    <mat-toolbar color="primary">
      <mat-icon style="margin-right: 8px">description</mat-icon>
      <span>Document Q&A System</span>
      <span style="flex: 1"></span>
      <button mat-button routerLink="/upload">
        <mat-icon>upload_file</mat-icon> Upload
      </button>
      <button mat-button routerLink="/chat">
        <mat-icon>chat</mat-icon> Chat
      </button>
    </mat-toolbar>
    <div style="padding: 24px; max-width: 900px; margin: 0 auto;">
      <router-outlet></router-outlet>
    </div>
  `
})
export class AppComponent {}