import { Component, ChangeDetectorRef, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatListModule } from '@angular/material/list';
import { MatChipsModule } from '@angular/material/chips';
import { DocumentService } from '../../services/document';

@Component({
  selector: 'app-upload',
  standalone: true,
  imports: [
    CommonModule, FormsModule, MatCardModule,
    MatButtonModule, MatIconModule,
    MatProgressBarModule, MatSnackBarModule,
    MatListModule, MatChipsModule
  ],
  templateUrl: './upload.html',
  styleUrls: ['./upload.scss']
})
export class UploadComponent implements OnInit {

  selectedFile: File | null = null;
  uploading = false;
  uploadDone = false;
  documentId = '';
  uploadedDocuments: any[] = [];
  activeDocumentId: string | null = null;

  constructor(
    public docService: DocumentService,
    private snackBar: MatSnackBar,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    // Load existing documents when page opens
    this.loadDocuments();
    // Restore active document from session
    this.activeDocumentId =
      sessionStorage.getItem('active-doc-id');
  }

  loadDocuments() {
    this.docService.getDocuments().subscribe({
      next: (docs) => {
        this.uploadedDocuments = docs;
        this.cdr.detectChanges();
      },
      error: () => {}
    });
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      this.selectedFile = input.files[0];
      this.uploadDone = false;
      this.documentId = '';
    }
  }

  onDragOver(event: DragEvent) { event.preventDefault(); }

  onDrop(event: DragEvent) {
    event.preventDefault();
    const file = event.dataTransfer?.files[0];
    if (file) {
      this.selectedFile = file;
      this.uploadDone = false;
      this.documentId = '';
    }
  }

  upload() {
    if (!this.selectedFile) return;
    this.uploading = true;

    this.docService.uploadFile(this.selectedFile).subscribe({
      next: (response) => {
        const match = response.match(/ID:\s*(\S+)/);
        if (match) {
          this.documentId = match[1];
          // Save active document
          this.activeDocumentId = this.documentId;
          sessionStorage.setItem(
            'active-doc-id', this.documentId);
          sessionStorage.setItem(
            'active-doc-name',
            this.selectedFile!.name);
        }
        this.uploading = false;
        this.uploadDone = true;
        // Reload documents list
        this.loadDocuments();
        this.snackBar.open(
          'Document uploaded successfully!',
          'Close', { duration: 3000 }
        );
        this.cdr.detectChanges();
      },
      error: () => {
        this.uploading = false;
        this.snackBar.open(
          'Upload failed. Try again.',
          'Close', { duration: 3000 }
        );
        this.cdr.detectChanges();
      }
    });
  }

  setActiveDocument(docId: string, docName: string) {
    this.activeDocumentId = docId;
    sessionStorage.setItem('active-doc-id', docId);
    sessionStorage.setItem('active-doc-name', docName);
    this.snackBar.open(
      `Active: ${docName}`, 'Close', { duration: 2000 });
  }

  deleteDocument(docId: string, event: Event) {
    event.stopPropagation();
    this.docService.deleteDocument(docId).subscribe({
      next: () => {
        if (this.activeDocumentId === docId) {
          this.activeDocumentId = null;
          sessionStorage.removeItem('active-doc-id');
          sessionStorage.removeItem('active-doc-name');
        }
        this.loadDocuments();
        this.snackBar.open(
          'Document deleted.', 'Close', { duration: 2000 });
      }
    });
  }

  uploadAnother() {
    this.selectedFile = null;
    this.uploadDone = false;
    this.documentId = '';
  }

  goToChat() {
    this.router.navigate(['/chat']);
  }
}