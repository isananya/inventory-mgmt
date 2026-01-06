import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { InventoryRequest, InventoryResponse, StockCheckResponse, StockRequest, WarehouseRequest, WarehouseResponse } from '../models/inventory';
import { Observable } from 'rxjs';

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

  getLowStock(): Observable<InventoryResponse[]> {
    return this.http.get<InventoryResponse[]>(`${this.apiUrl}/inventory/low-stock`);
  }

  createWarehouse(data: WarehouseRequest) {
    return this.http.post(`${this.apiUrl}/warehouse`, data);
  }

  getAllWarehouses(): Observable<WarehouseResponse[]> {
    return this.http.get<WarehouseResponse[]>(`${this.apiUrl}/warehouse`);
  }

  addInventory(data: InventoryRequest) {
    return this.http.post(`${this.apiUrl}/inventory`, data); 
  }

  activateWarehouse(id: number) {
    return this.http.patch(`${this.apiUrl}/warehouse/${id}/activate`, {});
  }

  deactivateWarehouse(id: number) {
    return this.http.patch(`${this.apiUrl}/warehouse/${id}/deactivate`, {});
  }

  getInventoryByWarehouse(warehouseId: number): Observable<InventoryResponse[]> {
    return this.http.get<InventoryResponse[]>(`${this.apiUrl}/inventory/warehouse/${warehouseId}`);
  }

  addStock(requests: StockRequest[]): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/inventory/stock/add`, requests);
  }

  deductStock(requests: StockRequest[]): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/inventory/stock/deduct`, requests);
  }

  getActiveWarehouses(): Observable<WarehouseResponse[]> {
    return this.http.get<WarehouseResponse[]>(`${this.apiUrl}/warehouse/active`);
  }
  
  getWarehouseById(id: number): Observable<WarehouseResponse> {
    return this.http.get<WarehouseResponse>(`${this.apiUrl}/warehouse/${id}`);
  }
}
