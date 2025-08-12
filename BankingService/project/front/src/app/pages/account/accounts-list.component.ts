import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AccountService } from '../../services/account.service';

@Component({
  selector: 'app-accounts-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="accounts-list-container">
      <button *ngIf="!showAccounts" (click)="showAccounts = true" class="show-btn">Show My Accounts</button>
      <ng-container *ngIf="showAccounts">
        <h2>Your Accounts</h2>
        <table *ngIf="accounts && accounts.length > 0; else noAccounts">
          <thead>
            <tr>
              <th>Card Number</th>
              <th>Name</th>
              <th>Balance</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let acc of accounts" (click)="selectAccount(acc)" [class.selected]="selectedAccount && selectedAccount.id === acc.id" style="cursor:pointer;">
              <td>{{ acc.cardNumber }}</td>
              <td>{{ acc.name }}</td>
              <td>{{ acc.balance | number:'1.2-2' }}</td>
            </tr>
          </tbody>
        </table>
        <ng-template #noAccounts>
          <div class="no-accounts">No accounts found.</div>
        </ng-template>
        <div *ngIf="error" class="error">{{ error }}</div>

        <div *ngIf="selectedAccount">
          <h3>Transactions for {{ selectedAccount.name }} ({{ selectedAccount.cardNumber }})</h3>
          <table *ngIf="transactions && transactions.length > 0; else noTransactions">
            <thead>
              <tr>
                <th>Type</th>
                <th>Amount</th>
                <th>Notes</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let t of transactions">
                <td>{{ t.type || t.transactionType }}</td>
                <td>{{ t.amount }}</td>
                <td>{{ t.notes }}</td>
                <td>{{ t.date || t.timestamp || t.createdAt | date:'short' }}</td>
              </tr>
            </tbody>
          </table>
          <ng-template #noTransactions>
            <div class="no-accounts">No transactions found.</div>
          </ng-template>
        </div>
      </ng-container>
    </div>
  `,
  styles: [`
    .accounts-list-container { max-width: 700px; margin: 40px auto; background: #fff; border-radius: 10px; box-shadow: 0 2px 12px #0001; padding: 32px 24px; }
    h2 { text-align: center; margin-bottom: 24px; }
    table { width: 100%; border-collapse: collapse; margin-bottom: 16px; }
    th, td { padding: 10px 8px; border-bottom: 1px solid #eee; text-align: left; }
    th { background: #f5f5f5; }
    .no-accounts { color: #888; text-align: center; margin-top: 24px; }
    .error { color: #d32f2f; text-align: center; margin-top: 16px; }
    .show-btn { display: block; margin: 40px auto 0 auto; font-size: 1.2rem; padding: 12px 32px; background: #1976d2; color: #fff; border: none; border-radius: 8px; cursor: pointer; }
    .show-btn:hover { background: #1256a3; }
    tr.selected { background: #e3f0ff; }
  `]
})
export class AccountsListComponent {
  accounts: any[] = [];
  error = '';
  selectedAccount: any = null;
  transactions: any[] = [];
  private _showAccounts = false;

  constructor(private accountService: AccountService) {}

  get showAccounts() {
    return this._showAccounts;
  }
  set showAccounts(val: boolean) {
    if (val && this.accounts.length === 0) {
      this.fetchAccounts();
    }
    this._showAccounts = val;
  }

  selectAccount(acc: any) {
    this.selectedAccount = acc;
    this.transactions = [];
    this.accountService.getAccountTransactions(acc.id || acc.accountId).subscribe({
      next: (response) => {
        console.log('Transaction response:', response);
        this.transactions = Array.isArray(response?.data)
        ? response.data.map((t: any) => ({
            ...t,
            displayDate: t.date || t.timestamp || t.createdAt || null
          }))
        : [];
      
          
      },
      error: (err) => {
        this.transactions = [];
        console.error('Error loading transactions:', err);
      }
    });
  }

  fetchAccounts() {
    this.accountService.getAccountsForCurrentUser().subscribe({
      next: (data) => {
      this.accounts = Array.isArray(data?.data) ? data.data : [];

      },
      error: (err) => {
        this.error = err.error?.message || 'Failed to load accounts.';
      }
    });
  }
} 