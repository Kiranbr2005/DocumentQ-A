import { Routes } from '@angular/router';
import { UploadComponent } from './components/upload/upload';
import { ChatComponent } from './components/chat/chat';

export const routes: Routes = [
  { path: '', redirectTo: 'upload', pathMatch: 'full' },
  { path: 'upload', component: UploadComponent },
  { path: 'chat', component: ChatComponent }
];