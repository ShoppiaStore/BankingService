import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-verify',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './verify.component.html',
  styleUrl: './verify.component.css'
})
export class VerifyComponent implements OnInit {
  email = '';
  emailInput = '';
  otp = '';
  error = '';
  success = '';
  loading = false;

  constructor(private auth: AuthService, private router: Router) {}

  ngOnInit() {
    this.email = localStorage.getItem('pendingEmail') || '';
  }

  onSubmit() {
    this.error = '';
    this.success = '';
    this.loading = true;

    // Use email from localStorage or from input
    const emailToUse = this.email || this.emailInput;

    if (!emailToUse) {
      this.error = 'Please enter your email';
      this.loading = false;
      return;
    }

    this.auth.verifyOtp(emailToUse, this.otp).subscribe({
      next: (response) => {
        this.loading = false;
        localStorage.removeItem('pendingEmail');
        // إذا كان الرد فيه text فيه verified
        if (
          (typeof response === 'string' && response.toLowerCase().includes('verified')) ||
          (response && typeof response.text === 'string' && response.text.toLowerCase().includes('verified'))
        ) {
          this.success = 'OTP verified successfully! Redirecting to login...';
          this.error = '';
          this.router.navigate(['/login']);
        } else {
          // لو الرد object فيه رسالة خطأ
          this.error = (response && response.message) ? response.message : 'Verification failed';
        }
      },
      error: (err) => {
        this.loading = false;
        let msg = err.error?.message || err.error || 'Verification failed';
        if (typeof msg === 'object') {
          // لو فيه text جوا object
          if (msg.text && typeof msg.text === 'string' && msg.text.toLowerCase().includes('verified')) {
            this.success = 'OTP verified successfully! Redirecting to login...';
            this.error = '';
            this.router.navigate(['/login']);
            return;
          }
          msg = JSON.stringify(msg);
        }
        if (
          typeof msg === 'string' &&
          msg.toLowerCase().includes('user verified successfully')
        ) {
          this.success = 'OTP verified successfully! Redirecting to login...';
          this.error = '';
          this.router.navigate(['/login']);
        } else {
          this.error = msg;
        }
      }
    });
  }

  goToLogin() {
    localStorage.removeItem('pendingEmail');
    this.router.navigate(['/login']);
  }
}
