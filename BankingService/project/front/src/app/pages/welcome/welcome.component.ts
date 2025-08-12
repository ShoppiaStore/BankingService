import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-welcome',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './welcome.component.html',
  styleUrl: './welcome.component.css'
})
export class WelcomeComponent {
  userEmail: string | null = null;
  ngOnInit() {
    const email = localStorage.getItem('userEmail');
    this.userEmail = email;
  }
  constructor(private auth: AuthService, private router: Router) {}

  logout() {
    this.auth.logout();
  }
}
