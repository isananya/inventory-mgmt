import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';
import { ChangeRoleRequest, UserProfileResponse, UserRequest, UserRole } from '../models/user';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  getAllUsers(): Observable<UserProfileResponse[]> {
    return this.http.get<UserProfileResponse[]>(`${this.apiUrl}/users`);
  }

  addUser(data: UserRequest): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/signup`, data);
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/users/${id}`);
  }

  updateUserRole(id: number, newRole: UserRole): Observable<any> {
    const body: ChangeRoleRequest = { role: newRole };
    return this.http.put(`${this.apiUrl}/users/${id}/role`, body);
  }
}