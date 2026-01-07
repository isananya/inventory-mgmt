import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { InvoiceRequest, PageInvoiceResponse } from '../models/billing';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class BillingService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  generateInvoice(orderId: number, invoice: InvoiceRequest) {
    return this.http.post(`${this.apiUrl}/billing/order/${orderId}`, invoice);
  }

  getAllInvoices(page: number, size: number): Observable<PageInvoiceResponse> {
    const url = `${this.apiUrl}/billing?page=${page}&size=${size}&sortBy=createdAt&direction=desc`;
    return this.http.get<PageInvoiceResponse>(url);
  }
}
