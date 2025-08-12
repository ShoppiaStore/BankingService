import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  name = '';
  email = '';
  phone = '';
  password = '';
  error = '';
  loading = false;

  constructor(private auth: AuthService, private router: Router) {}

  onSubmit() {
    this.error = '';
    this.loading = true;
    
    console.log('Registering user:', { name: this.name, email: this.email, phone: this.phone });
    
    this.auth.register(this.name, this.email, this.phone, this.password).subscribe({
      next: (response) => {
        console.log('Registration response:', response);
        this.loading = false;
        // Store email for verification page
        localStorage.setItem('pendingEmail', this.email);
        console.log('Stored pending email:', this.email);
        this.router.navigate(['/verify']);
      },
      error: (err) => {
        console.error('Registration error:', err);
        this.loading = false;
        this.error = err.error?.message || 'Register failed';
      }
    });
  }

  goToLogin() {
    this.router.navigate(['/login']);
  }
}
