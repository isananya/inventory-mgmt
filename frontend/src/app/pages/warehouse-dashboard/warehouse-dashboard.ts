import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { InventoryService } from '../../core/services/inventory';
import { OrderService } from '../../core/services/order';
import { InventoryResponse, WarehouseResponse } from '../../core/models/inventory';
import { forkJoin } from 'rxjs';
import { LocationStat, WarehouseStat } from '../../core/models/warehouse';

@Component({
  selector: 'app-warehouse-dashboard',
  imports: [CommonModule, RouterModule],
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

  constructor(private inventoryService: InventoryService, private orderService: OrderService, private cd: ChangeDetectorRef) { }

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
}
