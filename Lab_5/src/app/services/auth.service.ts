import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserResponseDto } from '../models/user.model';
import { environment } from '../../environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private cachedIsAdmin: boolean | null = null;

  constructor(private http: HttpClient) {}


  getMe(tempToken?: string): Observable<UserResponseDto> {
    if (tempToken) {

      const headers = new HttpHeaders().set('Authorization', `Basic ${tempToken}`);
      return this.http.get<UserResponseDto>(`${environment.apiUrl}/auth/me`, { headers });
    }


    return this.http.get<UserResponseDto>(`${environment.apiUrl}/auth/me`);
  }

  isAdmin(): boolean {
    if (this.cachedIsAdmin !== null) {
      return this.cachedIsAdmin;
    }

    const rolesStr = sessionStorage.getItem('roles');
    return rolesStr ? JSON.parse(rolesStr).includes('ROLE_ADMIN') : false;
  }

  isLoggedIn(): boolean {
    return !!sessionStorage.getItem('basicAuth');
  }

  getUsername(): string {
    return sessionStorage.getItem('username') || 'Гость';
  }

  getCurrentUserId(): number {
    const id = sessionStorage.getItem('userId');
    return id ? parseInt(id, 10) : 0;
  }

  logout(): void {
    sessionStorage.clear();
    this.cachedIsAdmin = null;
  }
}
