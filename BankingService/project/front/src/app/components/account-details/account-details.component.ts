import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AccountService } from '../../services/account.service';
import { TransactionService } from '../../services/transaction.service';
import { Account, ApiResponse } from '../../models/account.model';
import { Transaction } from '../../models/transaction.model';

@Component({
  selector: 'app-account-details',
  templateUrl: './account-details.component.html',
  styleUrls: ['./account-details.component.css'],
  standalone: true,
  imports: [CommonModule, RouterModule]
})
export class AccountDetailsComponent implements OnInit {
  account: Account | null = null;
  transactions: Transaction[] = [];
  loading = false;
  errorMessage = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private accountService: AccountService,
    private transactionService: TransactionService
  ) {}

  ngOnInit(): void {
    const accountId = this.route.snapshot.paramMap.get('id');
    if (accountId) {
      this.loadAccountDetails(+accountId);
      this.loadTransactionHistory(+accountId);
    }
  }

  loadAccountDetails(accountId: number): void {
    this.loading = true;
    this.errorMessage = '';

    this.accountService.getAccountById(accountId).subscribe({
      next: (response: ApiResponse<Account>) => {
        this.loading = false;
        if (response.success) {
          this.account = response.data;
        } else {
          this.errorMessage = response.message;
        }
      },
      error: (error) => {
        this.loading = false;
        this.errorMessage = error.error?.message || 'Error loading account details';
      }
    });
  }

  loadTransactionHistory(accountId: number): void {
    this.transactionService.getTransactionHistory(accountId).subscribe({
      next: (response: ApiResponse<Transaction[]>) => {
        if (response.success) {
          this.transactions = response.data;
        }
      },
      error: (error) => {
        console.error('Error loading transaction history:', error);
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/dashboard']);
  }

  maskCardNumber(cardNumber: string): string {
    if (!cardNumber) return '';
    // Show full card number instead of masking
    return cardNumber;
  }

  getAccountTypeText(type: string): string {
    const types: { [key: string]: string } = {
      'SAVINGS': 'توفير',
      'CHECKING': 'جاري',
      'BUSINESS': 'تجاري',
      'CREDIT': 'ائتمان',
      'Dollar': 'دولار'
    };
    return types[type] || type;
  }

  getTransactionTypeText(type: string): string {
    return type === 'DEPOSIT' ? 'Deposit' : 'Withdraw';
  }

  getTransactionTypeClass(type: string): string {
    return type === 'DEPOSIT' ? 'deposit' : 'withdraw';
  }
}
