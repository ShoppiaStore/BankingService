import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  email = '';
  password = '';
  error = '';
  loading = false;

  constructor(private auth: AuthService, private router: Router) {}

  onSubmit() {
    this.error = '';
    this.loading = true;
    this.auth.login(this.email, this.password).subscribe({
      next: (res) => {
        this.loading = false;
        console.log('Login response:', res);
        localStorage.setItem('accessToken', res.accessToken);
        this.router.navigate(['/accounts']);
      },
      error: (err) => {
        this.loading = false;
        this.error = err.error?.message || 'Login failed';
      }
    });
  }

  goToRegister() {
    this.router.navigate(['/register']);
  }

  goToVerify() {
    this.router.navigate(['/verify']);
  }
}
