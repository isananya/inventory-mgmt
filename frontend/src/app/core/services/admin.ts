import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { DashboardStats } from '../models/admin';
import { OrderResponse, PageOrderResponse } from '../models/order';
import { PageProductResponse, Product } from '../models/product';
import { forkJoin, map, Observable } from 'rxjs';

interface UserResponse { id: number; }
interface InventoryResponse { id: number; }

@Injectable({ providedIn: 'root' })
export class AdminService {
  private http = inject(HttpClient);
  private apiUrl = environment.apiUrl;

  getDashboardData(): Observable<DashboardStats> {
    const orders$ = this.http.get<PageOrderResponse>(`${this.apiUrl}/order?page=0&size=1000`);
    const products$ = this.http.get<PageProductResponse>(`${this.apiUrl}/products?page=0&size=1`);
    const users$ = this.http.get<UserResponse[]>(`${this.apiUrl}/users`);
    const lowStock$ = this.http.get<InventoryResponse[]>(`${this.apiUrl}/inventory/low-stock`);

    return forkJoin({
      ordersPage: orders$,
      productsPage: products$,
      users: users$,
      lowStock: lowStock$
    }).pipe(
      map((results) => {
        const orderList = results.ordersPage.content || [];
        const { users, lowStock } = results;

        const totalRevenue = orderList.reduce((sum, order) => sum + order.totalAmount, 0);
        const monthlySales = this.calculateMonthlySales(orderList);

        const recentOrders = orderList
          .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
          .slice(0, 5)
          .map(o => ({
            id: o.orderId,
            customerName: `Customer #${o.customerId}`,
            amount: o.totalAmount,
            status: o.status,
            date: o.createdAt
          }));

        const statusMap = new Map<string, number>();
        orderList.forEach(o => {
          const status = o.status || 'UNKNOWN';
          statusMap.set(status, (statusMap.get(status) || 0) + 1);
        });

        const ordersByStatus = Array.from(statusMap, ([status, count]) => ({ status, count }));

        return {
          totalRevenue,
          totalOrders: results.ordersPage.totalElements,
          totalProducts: results.productsPage.totalElements,
          totalUsers: users.length,
          lowStockCount: lowStock.length,
          monthlySales,
          recentOrders,
          ordersByStatus
        } as DashboardStats;
      })
    );
  }

  private calculateMonthlySales(orders: OrderResponse[]) {
    const salesMap = new Map<string, number>();
    const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

    const today = new Date();
    for (let i = 5; i >= 0; i--) {
      const d = new Date(today.getFullYear(), today.getMonth() - i, 1);
      const key = `${months[d.getMonth()]}`;
      salesMap.set(key, 0);
    }

    orders.forEach(order => {
      if (order.status !== 'CANCELLED') {
        const date = new Date(order.createdAt);
        const key = months[date.getMonth()];
        if (salesMap.has(key)) {
          salesMap.set(key, (salesMap.get(key) || 0) + order.totalAmount);
        }
      }
    });

    return Array.from(salesMap, ([month, amount]) => ({ month, amount }));
  }
}