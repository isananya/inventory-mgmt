import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { InventoryService } from '../../core/services/inventory';
import { OrderService } from '../../core/services/order';
import { InventoryRequest, InventoryResponse, WarehouseResponse } from '../../core/models/inventory';
import { forkJoin } from 'rxjs';
import { LocationStat, WarehouseStat } from '../../core/models/warehouse';
import { ProductService } from '../../core/services/product';
import { PageProductResponse, Product } from '../../core/models/product';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-warehouse-dashboard',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './warehouse-dashboard.html',
  styleUrl: './warehouse-dashboard.css',
})

export class WarehouseDashboardComponent implements OnInit {

  activeWarehouses: WarehouseResponse[] = [];
  lowStockItems: InventoryResponse[] = [];
  locationStats: LocationStat[] = [];

  totalActiveWarehouses = 0;
  totalOrdersToPack = 0;
  totalLowStock = 0;

  isLoading = true;

  products: Product[] = [];
  showModal = false;
  isSubmitting = false;

  formData: InventoryRequest = {
    productId: 0,
    warehouseId: 0,
    quantity: 0
  };

  constructor(private inventoryService: InventoryService, private orderService: OrderService, 
    private cd: ChangeDetectorRef, private productService: ProductService) { }

  ngOnInit() {
    this.loadDashboardData();
  }

  loadDashboardData() {
    this.isLoading = true;
    this.cd.detectChanges();

    forkJoin({
      warehouses: this.inventoryService.getActiveWarehouses(),
      lowStock: this.inventoryService.getLowStock(),
      allOrdersPage: this.orderService.getAllOrders(0, 1000)
    }).subscribe({
      next: (data) => {
        this.activeWarehouses = data.warehouses;
        this.lowStockItems = data.lowStock;
        this.totalLowStock = data.lowStock.length;
        this.totalActiveWarehouses = data.warehouses.length;

        const allOrders = data.allOrdersPage.content || [];
        const ordersToPack = allOrders.filter(order => order.status === 'APPROVED' || order.status === 'CREATED');

        this.totalOrdersToPack = ordersToPack.length;

        this.calculateLocationStats(data.warehouses);

        this.isLoading = false;
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error('Dashboard Error:', err);
        this.isLoading = false;
        this.cd.detectChanges();
      }
    });
  }

  calculateLocationStats(warehouses: WarehouseResponse[]) {
    const statsMap = new Map<string, number>();

    warehouses.forEach(w => {
      const loc = w.location || 'Unknown';
      statsMap.set(loc, (statsMap.get(loc) || 0) + 1);
    });

    const total = warehouses.length;
    this.locationStats = [];

    statsMap.forEach((count, city) => {
      this.locationStats.push({
        name: city,
        count: count,
        percentage: total > 0 ? (count / total) * 100 : 0
      });
    });

    this.locationStats.sort((a, b) => b.count - a.count);
  }

  openAddInventoryModal() {
    this.showModal = true;
    this.cd.detectChanges();
    if (this.products.length === 0) {
      this.productService.getProducts().subscribe(data => {
        this.products = data.content;
      });
    }
    this.cd.detectChanges();
  }

  closeModal() {
    this.showModal = false;
    this.cd.detectChanges();
    this.formData = { productId: 0, warehouseId: 0, quantity: 0 };
    this.cd.detectChanges();
  }

  submitInventory() {
    if (this.formData.quantity <= 0 || this.formData.productId === 0 || this.formData.warehouseId === 0) {
      alert("Please fill all fields correctly.");
      return;
    }

    this.isSubmitting = true;
    this.cd.detectChanges();
    
    this.inventoryService.addInventory(this.formData).subscribe({
      next: () => {
        alert("Inventory Added Successfully!");
        this.isSubmitting = false;
        this.cd.detectChanges();
        this.closeModal();
        this.loadDashboardData(); 
      },
      error: (err) => {
        console.error(err);
        alert("Failed to add inventory.");
        this.isSubmitting = false;
      }
    });
  }
}
