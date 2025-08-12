import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Transaction, TransactionRequest, ApiResponse } from '../models/transaction.model';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private baseUrl = 'http://localhost:8081/api/transactions';

  constructor(private http: HttpClient) {}

  // Deposit money
  deposit(request: TransactionRequest): Observable<ApiResponse<Transaction>> {
    return this.http.post<ApiResponse<Transaction>>(`${this.baseUrl}/deposit`, request);
  }

  // Withdraw money
  withdraw(request: TransactionRequest): Observable<ApiResponse<Transaction>> {
    return this.http.post<ApiResponse<Transaction>>(`${this.baseUrl}/withdraw`, request);
  }

  // Get transaction history
  getTransactionHistory(accountId: number): Observable<ApiResponse<Transaction[]>> {
    return this.http.get<ApiResponse<Transaction[]>>(`${this.baseUrl}/history/${accountId}`);
  }
}
