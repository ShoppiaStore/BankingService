import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { LoginRequest, RegisterRequest, AuthResponse } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = 'http://localhost:8080/rest/auth';
  private tokenKey = 'auth_token';

  constructor(private http: HttpClient, private router: Router) {}

  // Login
  login(credentials: LoginRequest): Observable<AuthResponse> {
    console.log('🔐 Attempting login for:', credentials.email);
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, credentials);
  }

  // Register
  register(userData: RegisterRequest): Observable<AuthResponse> {
    console.log('🔐 Attempting registration for:', userData.email);
    return this.http.post<AuthResponse>(`${this.baseUrl}/register`, userData)
      .pipe(
        tap(response => {
          console.log('✅ Registration response:', response);
        }),
        catchError(error => {
          console.error('❌ Registration error:', error);
          return throwError(() => error);
        })
      );
  }

  verifyAccount(email: string, otp: string): Observable<AuthResponse> {
    console.log('🔐 Attempting account verification for:', email);
    return this.http.post<AuthResponse>(`${this.baseUrl}/verify?email=${email}&otp=${otp}`, {})
      .pipe(
        tap(response => {
          console.log('✅ Verification response:', response);
        }),
        catchError(error => {
          console.error('❌ Verification error:', error);
          return throwError(() => error);
        })
      );
  }

  // Store token with proper Bearer format
  setToken(token: string): void {
    const bearerToken = `Bearer ${token}`;
    localStorage.setItem(this.tokenKey, bearerToken);
    console.log('💾 Token stored:', bearerToken);
  }

  // Get token
  getToken(): string | null {
    const token = localStorage.getItem(this.tokenKey);
    console.log('🔍 Retrieved token:', token);
    return token;
  }

  // Check if user is logged in
  isLoggedIn(): boolean {
    const hasToken = !!this.getToken();
    console.log('🔐 User logged in:', hasToken);
    return hasToken;
  }

  // Add token validation method
  isTokenValid(): boolean {
    const token = this.getToken();
    if (!token) {
      console.log('❌ No token found');
      return false;
    }
    
    // Check if token has Bearer prefix
    if (!token.startsWith('Bearer ')) {
      console.log('❌ Token missing Bearer prefix');
      return false;
    }
    
    console.log('✅ Token appears valid');
    return true;
  }

  // Logout
  logout(): void {
    console.log('🚪 Logging out user');
    localStorage.removeItem(this.tokenKey);
    this.router.navigate(['/login']);
  }
}
