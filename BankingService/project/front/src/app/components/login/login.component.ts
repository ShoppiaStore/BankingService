import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { LoginRequest } from '../../models/user.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule]
})
export class LoginComponent {
  loginForm: FormGroup;
  loading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.loading = true;
      this.errorMessage = '';

      const credentials: LoginRequest = this.loginForm.value;

      this.authService.login(credentials).subscribe({
        next: (response) => {
          console.log('✅ Login response:', response);
          let token = '';
          if (response && response.token) {
            token = response.token;
          } else if (response && response.data && response.data.token) {
            token = response.data.token;
          } else if (response && response.accessToken) {
            token = response.accessToken;
          } else if (response && response.jwt) {
            token = response.jwt;
          } else {
            console.error('❌ No token found in response:', response);
            this.errorMessage = 'Invalid response from server';
            this.loading = false;
            return;
          }
          console.log('🔑 Extracted token:', token);
          this.authService.setToken(token);
          this.loading = false;
          this.router.navigate(['/dashboard']);
        },
        error: (error) => {
          console.error('❌ Login error:', error);
          this.loading = false;
          this.errorMessage = error.error?.message || 'Error during login';
        }
      });
    }
  }
}
