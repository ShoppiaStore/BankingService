import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { Account, ApiResponse } from '../models/account.model';

@Injectable({
  providedIn: 'root'
})
export class AccountService {
  private baseUrl = 'http://localhost:8081/api/accounts';

  constructor(private http: HttpClient) {}

  // Get user accounts with debugging
  getUserAccounts(): Observable<ApiResponse<Account[]>> {
    console.log('🔍 Fetching user accounts from:', `${this.baseUrl}/user`);
    
    // Log the request details
    const headers = new HttpHeaders();
    console.log('📋 Request headers:', headers);
    
    return this.http.get<ApiResponse<Account[]>>(`${this.baseUrl}/user`)
      .pipe(
        tap(response => {
          console.log('✅ Accounts API response:', response);
        }),
        catchError(error => {
          console.error('❌ Accounts API error:', error);
          console.error('❌ Error status:', error.status);
          console.error('❌ Error message:', error.message);
          console.error('❌ Error details:', error.error);
          return throwError(() => error);
        })
      );
  }

  // Get account by ID
  getAccountById(id: number): Observable<ApiResponse<Account>> {
    console.log('🔍 Fetching account by ID:', id);
    return this.http.get<ApiResponse<Account>>(`${this.baseUrl}/${id}`)
      .pipe(
        tap(response => {
          console.log('✅ Account details response:', response);
        }),
        catchError(error => {
          console.error('❌ Account details error:', error);
          return throwError(() => error);
        })
      );
  }

  // Create account with debugging
  createAccount(account: Account): Observable<ApiResponse<Account>> {
    console.log('➕ Creating account:', account);
    return this.http.post<ApiResponse<Account>>(this.baseUrl, account)
      .pipe(
        tap(response => {
          console.log('✅ Account created successfully:', response);
        }),
        catchError(error => {
          console.error('❌ Create account error:', error);
          return throwError(() => error);
        })
      );
  }
}
