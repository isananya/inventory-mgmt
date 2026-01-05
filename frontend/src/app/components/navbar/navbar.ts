import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})

export class NavbarComponent {
  isLoggedIn$: Observable<boolean>; 
  userRole$: Observable<string | null>;
  userName$: Observable<string | null>;
  
  showDropdown = false;

  constructor(public auth: AuthService, private router: Router) {
    this.userRole$ = this.auth.currentUserRole$;
    this.userName$ = this.auth.currentUserName$;
    
    this.isLoggedIn$ = new Observable(subscriber => {
      this.auth.currentUserEmail$.subscribe(email => {
        subscriber.next(!!email);
      });
    });
  }

  toggleDropdown() {
    this.showDropdown = !this.showDropdown;
  }

  logout() {
    this.auth.logout();
    this.showDropdown = false;
    this.router.navigate(['/login']);
  }

  closeDropdown() {
    this.showDropdown = false;
  }
}
