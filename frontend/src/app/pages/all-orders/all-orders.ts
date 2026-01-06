import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { OrderResponse, PageOrderResponse } from '../../core/models/order';
import { OrderService } from '../../core/services/order';

@Component({
  selector: 'app-all-orders',
  imports: [CommonModule],
  templateUrl: './all-orders.html',
  styleUrl: './all-orders.css',
})

export class AllOrdersComponent implements OnInit {

  orders: OrderResponse[] = [];
  
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;
  isLoading = false;

  constructor(private orderService: OrderService, private cd: ChangeDetectorRef){}

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
      this.cd.detectChanges();
      this.loadOrders();
    }
  }

  prevPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.cd.detectChanges();
      this.loadOrders();
    }
  }

  formatAddress(addr: any): string {
    if (!addr) return 'N/A';
    return typeof addr === 'string' ? addr : `${addr.city}, ${addr.state}`;
  }
}
