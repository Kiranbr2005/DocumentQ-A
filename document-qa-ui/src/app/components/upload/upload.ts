import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { DocumentService } from '../../services/document';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-upload',
  standalone: true,
  imports: [CommonModule,FormsModule,MatCardModule,MatButtonModule,MatIconModule,MatProgressBarModule,MatSnackBarModule
  ],
  templateUrl: './upload.html',
  styleUrls: ['./upload.scss']
})
export class UploadComponent {

  selectedFile: File | null = null;
  uploading = false;
  uploadDone = false;

  constructor(
    private docService: DocumentService,
    private snackBar: MatSnackBar,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.selectedFile = input.files[0];
      this.uploadDone = false;
    }
  }

  onDragOver(event: DragEvent) { event.preventDefault(); }

  onDrop(event: DragEvent) {
    event.preventDefault();
    const file = event.dataTransfer?.files[0];
    if (file) { this.selectedFile = file; this.uploadDone = false; }
  }

  upload() {
    if (!this.selectedFile) return;
    this.uploading = true;
    this.docService.uploadFile(this.selectedFile).subscribe({
      next: () => {
  
        this.snackBar.open('Document uploaded successfully!', 'Close', { duration: 3000 });
            this.uploading = false;
        this.uploadDone = true;
           this.cdr.detectChanges(); 
      },
      error: () => {
        this.uploading = false;
        this.snackBar.open('Upload failed. Try again.', 'Close', { duration: 3000 });
        this.cdr.detectChanges();
      }
    });
  }

  goToChat() { this.router.navigate(['/chat']); }
}