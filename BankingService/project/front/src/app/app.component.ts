import { Component } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, MatIconModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'angular_17_Login';
  showAccountMenu = false;
  userName = '';
  showWelcomeMsg = false;
  isLoginPage = false;

  constructor(private router: Router) {
    this.userName = localStorage.getItem('userName') || localStorage.getItem('name') || localStorage.getItem('userEmail') || 'User';
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.showAccountMenu = false;
        this.showWelcomeMsg = false;
        this.isLoginPage = event.urlAfterRedirects === '/login';
      }
    });
  }

  toggleAccountMenu() {
    this.showAccountMenu = !this.showAccountMenu;
  }

  logout() {
    localStorage.clear();
    this.router.navigate(['/login']);
  }

  showWelcome() {
    this.showWelcomeMsg = true;
  }

  goToHome() {
    this.router.navigate(['/home']);
  }

  goToAccount() {
    this.router.navigate(['/account']);
  }
}
