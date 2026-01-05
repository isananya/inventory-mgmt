import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { LoginRequest, SignupRequest, LoginResponse, User } from '../models/auth';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private baseUrl = environment.apiUrl + '/auth';

  private userEmailSubject = new BehaviorSubject<string | null>(localStorage.getItem('userEmail'));
  public currentUserEmail$ = this.userEmailSubject.asObservable();

  private userRoleSubject = new BehaviorSubject<string | null>(localStorage.getItem('userRole'));
  public currentUserRole$ = this.userRoleSubject.asObservable();

  private userNameSubject = new BehaviorSubject<string | null>(localStorage.getItem('userName'));
  public currentUserName$ = this.userNameSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) { }

  login(data: LoginRequest) {
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, data).pipe(
      tap((response) => {
        if (response) {
          this.setUser(response.email, response.role, response.name);
        }
      })
    );
  }

  signup(data: SignupRequest) {
    return this.http.post(`${this.baseUrl}/signup`, data);
  }

  logout() {
    localStorage.clear();
    
    this.userEmailSubject.next(null);
    this.userRoleSubject.next(null);
    this.userNameSubject.next(null);

    this.http.post(`${this.baseUrl}/logout`, {}).subscribe({
      next: () => this.router.navigate(['/login']),
      error: () => this.router.navigate(['/login'])
    });
  }

  getProfile(): Observable<User> {
    return this.http.get<User>(`${this.baseUrl}/profile`);
  }

  private setUser(email: string, role: string, name: string) {
    localStorage.setItem('userEmail', email);
    localStorage.setItem('userRole', role);
    localStorage.setItem('userName', name || '');

    this.userEmailSubject.next(email);
    this.userRoleSubject.next(role);
    this.userNameSubject.next(name || '');
  }

  getRole(): string {
    return localStorage.getItem('userRole') || '';
  }

  getEmail(): string {
    return localStorage.getItem('userEmail') || '';
  }

  isLoggedIn(): boolean {
    return !!localStorage.getItem('userEmail');
  }

  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }
}