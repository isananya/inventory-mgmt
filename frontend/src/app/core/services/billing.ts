import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { InvoiceRequest } from '../models/billing';

@Injectable({
  providedIn: 'root',
})
export class BillingService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  generateInvoice(orderId: number, invoice: InvoiceRequest) {
    return this.http.post(`${this.apiUrl}/billing/order/${orderId}`, invoice);
  }
}
