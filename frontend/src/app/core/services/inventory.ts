import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { InventoryResponse, StockCheckResponse, StockRequest } from '../models/inventory';

@Injectable({
  providedIn: 'root',
})
export class InventoryService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  getInventoryByProduct(productId: number) {
    return this.http.get<InventoryResponse[]>(`${this.apiUrl}/inventory/product/${productId}`);
  }

  checkStock(stockRequests: StockRequest[]) {
    return this.http.post<StockCheckResponse[]>(`${this.apiUrl}/inventory/check`, stockRequests);
  }
}
