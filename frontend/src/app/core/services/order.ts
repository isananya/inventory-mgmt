import { inject, Injectable } from '@angular/core';
import { OrderRequest, OrderResponse, PageOrderResponse } from '../models/order';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  placeOrder(order: OrderRequest) {
    return this.http.post<any>(`${this.apiUrl}/order`, order);
  }

  getOrdersByCustomer(customerId: number, page = 0, size = 10) {
    return this.http.get<PageOrderResponse>(`${this.apiUrl}/order/customer/${customerId}`, {
      params: { page, size }
    });
  }

  cancelOrder(orderId: number) {
    return this.http.put(`${this.apiUrl}/order/${orderId}/cancel`, {});
  }

  getAllOrders(page = 0, size = 10): Observable<PageOrderResponse> {
    return this.http.get<PageOrderResponse>(`${this.apiUrl}/order`, {
      params: { page, size }
    });
  }
}
