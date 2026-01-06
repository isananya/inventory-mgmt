import { CommonModule, DatePipe } from '@angular/common';
import { ChangeDetectorRef, Component, inject, OnInit } from '@angular/core';
import { OrderService } from '../../core/services/order';
import { AuthService } from '../../core/services/auth';
import { Router } from '@angular/router';
import { OrderResponse } from '../../core/models/order';

@Component({
  selector: 'app-my-orders',
  imports: [CommonModule],
  templateUrl: './my-orders.html',
  styleUrl: './my-orders.css',
})

export class MyOrdersComponent implements OnInit {
  orders: OrderResponse[] = [];
  isLoading = true;
  userId: number | null = null;
  
  currentPage = 0;
  totalPages = 0;
  pageSize = 10;

  showConfirmModal = false;
  showSuccessModal = false;
  selectedOrderId: number | null = null;

  constructor(private orderService: OrderService, private authService: AuthService, 
    private router: Router, private cd: ChangeDetectorRef){}

  ngOnInit() {
    this.authService.getProfile().subscribe({
      next: (user) => {
        this.userId = user.id;
        this.loadOrders();
      },
      error: () => this.router.navigate(['/login'])
    });
  }

  loadOrders() {
    if (!this.userId) return;
    this.isLoading = true;

    this.orderService.getOrdersByCustomer(this.userId, this.currentPage, this.pageSize)
      .subscribe({
        next: (res) => {
          this.orders = res.content;
          this.totalPages = res.totalPages;
          this.isLoading = false;
          this.cd.detectChanges();
        },
        error: (err) => {
          console.error('Failed to load orders', err);
          this.isLoading = false;
          this.cd.detectChanges();
        }
      });
  }

  initiateCancel(orderId: number) {
    this.selectedOrderId = orderId;
    this.showConfirmModal = true;
    this.cd.detectChanges();
  }

  proceedCancel() {
    if (!this.selectedOrderId) return;

    this.orderService.cancelOrder(this.selectedOrderId).subscribe({
      next: () => {
        this.showConfirmModal = false;
        this.loadOrders(); 
        this.showSuccessModal = true;
        this.cd.detectChanges();
      },
      error: (err) => {
        this.showConfirmModal = false;
        alert('Failed: ' + (err.error?.message || 'Server Error'));
        this.cd.detectChanges();
      }
    });
  }

  closeModals() {
    this.showConfirmModal = false;
    this.showSuccessModal = false;
    this.selectedOrderId = null;
  }

  changePage(delta: number) {
    this.currentPage += delta;
    this.loadOrders();
  }
  
  getStatusColor(status: string): string {
    switch (status) {
      case 'DELIVERED': return '#28a745';
      case 'CANCELLED': return '#dc3545';
      case 'SHIPPED': return '#007bff';
      case 'PACKED': return '#17a2b8'; 
      default: return '#ffc107';  
    }
  }
}
