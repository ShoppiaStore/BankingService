import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AccountService } from '../../services/account.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-home',
  standalone: true,
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
  imports: [CommonModule, FormsModule]
})
export class HomeComponent {
  account: any = null;
  showAddAccount = false;
  addCardNumber = '';
  addName = '';
  addBalance: number | null = null;
  addMessage = '';
  addError = '';

  constructor(private router: Router, private accountService: AccountService) {
    this.loadAccount();
  }

  loadAccount() {
    // جلب بيانات الحساب (هنا نفترض أن اليوزر له حساب واحد فقط)
    const userId = +(localStorage.getItem('userId') || '');
    if (!userId) return;
    this.accountService.getAccount(userId).subscribe({
      next: (res: any) => {
        this.account = res.data || res;
      },
      error: () => {
        this.account = null;
      }
    });
  }

  goToAccount() {
    this.router.navigate(['/account']);
  }

  openAddAccount() {
    this.showAddAccount = true;
    this.addCardNumber = '';
    this.addName = '';
    this.addBalance = null;
    this.addMessage = '';
    this.addError = '';
  }

  closeAddAccount() {
    this.showAddAccount = false;
  }

  submitAddAccount() {
    this.addMessage = '';
    this.addError = '';
    const userId = +(localStorage.getItem('userId') || '');
    if (!this.addCardNumber || !this.addName || !this.addBalance) {
      this.addError = 'All fields are required';
      return;
    }
    this.accountService.createAccount({
      cardNumber: this.addCardNumber,
      name: this.addName,
      balance: this.addBalance,
      userId
    }).subscribe({
      next: (res: any) => {
        this.addMessage = res.message || 'Account created successfully';
        this.showAddAccount = false;
        this.loadAccount();
      },
      error: () => {
        this.addError = 'Failed to create account';
      }
    });
  }
}
