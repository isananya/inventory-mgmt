import { inject, Injectable } from '@angular/core';
import { OrderRequest } from '../models/order';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  placeOrder(order: OrderRequest) {
    return this.http.post<any>(`${this.apiUrl}/order`, order); 
  }
}
