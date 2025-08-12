import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-verify',
  templateUrl: './verify.component.html',
  styleUrls: ['./verify.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule]
})
export class VerifyComponent implements OnInit {
  verifyForm: FormGroup;
  loading = false;
  errorMessage = '';
  successMessage = '';
  email: string = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.verifyForm = this.fb.group({
      otp: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]]
    });
  }

  ngOnInit(): void {
    this.email = this.route.snapshot.queryParams['email'] || '';
    if (!this.email) {
      this.router.navigate(['/register']);
    }
  }

  onSubmit(): void {
    if (this.verifyForm.valid && this.email) {
      this.loading = true;
      this.errorMessage = '';
      this.successMessage = '';

      const otp = this.verifyForm.get('otp')?.value;

      this.authService.verifyAccount(this.email, otp).subscribe({
        next: (response) => {
          this.loading = false;
          this.successMessage = 'تم تفعيل الحساب بنجاح! سيتم توجيهك لصفحة تسجيل الدخول.';
          setTimeout(() => {
            this.router.navigate(['/login']);
          }, 2000);
        },
        error: (error) => {
          this.loading = false;
          this.errorMessage = error.error?.message || 'رمز التحقق غير صحيح';
        }
      });
    }
  }

  resendOtp(): void {
    // You can add resend OTP functionality here if needed
    this.errorMessage = 'سيتم إرسال رمز جديد قريباً';
  }
}
