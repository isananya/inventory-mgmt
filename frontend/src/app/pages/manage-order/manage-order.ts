import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { OrderResponse, PageOrderResponse } from '../../core/models/order';
import { OrderService } from '../../core/services/order';

@Component({
  selector: 'app-manage-order',
  imports: [CommonModule, FormsModule],
  templateUrl: './manage-order.html',
  styleUrl: './manage-order.css',
})
export class ManageOrderComponent {

  orders: OrderResponse[] = [];
  
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;
  isLoading = false;

  showModal = false;
  isSubmitting = false;
  selectedOrder: OrderResponse | null = null;
  newStatus = '';
  
  statusOptions = ['CREATED', 'APPROVED', 'PACKED', 'SHIPPED', 'DELIVERED', 'CANCELLED'];

  constructor(private orderService: OrderService, private cd: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    this.isLoading = true;
    this.orderService.getAllOrders(this.currentPage, this.pageSize).subscribe({
      next: (response: PageOrderResponse) => {
        this.orders = response.content; 
        this.totalPages = response.totalPages; 
        this.totalElements = response.totalElements;
        this.isLoading = false;
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error('Error fetching orders:', err);
        this.isLoading = false;
        this.cd.detectChanges();
      }
    });
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadOrders();
    }
  }

  prevPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadOrders();
    }
  }

  formatAddress(addr: any): string {
    if (!addr) return 'N/A';
    return typeof addr === 'string' ? addr : `${addr.city}, ${addr.state}`;
  }

  openUpdateModal(order: OrderResponse) {
    this.selectedOrder = order;
    this.newStatus = order.status.toString(); 
    this.showModal = true;
    this.cd.detectChanges();
  }

  closeModal() {
    this.showModal = false;
    this.selectedOrder = null;
    this.cd.detectChanges();
  }

  confirmStatusUpdate() {
    if (!this.selectedOrder || !this.newStatus) return;

    if (this.newStatus === this.selectedOrder.status.toString()) {
      this.closeModal();
      return;
    }

    this.isSubmitting = true;

    this.orderService.updateOrderStatus(this.selectedOrder.orderId, this.newStatus).subscribe({
      next: () => {
        alert(`Order #${this.selectedOrder?.orderId} updated to ${this.newStatus}`);
        
        if (this.selectedOrder) {
          this.selectedOrder.status = this.newStatus as any; 
        }
        
        this.isSubmitting = false;
        this.cd.detectChanges();
        this.closeModal();
      },
      error: (err) => {
        console.error('Update failed', err);
        alert("Failed to update status");
        this.isSubmitting = false;
        this.cd.detectChanges();
      }
    });
  }
}
