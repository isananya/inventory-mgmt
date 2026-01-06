import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { InventoryService } from '../../../core/services/inventory';
import { WarehouseResponse } from '../../../core/models/inventory';

@Component({
  selector: 'app-manage-warehouse',
  imports: [CommonModule],
  templateUrl: './manage-warehouse.html',
  styleUrl: './manage-warehouse.css',
})

export class ManageWarehousesComponent implements OnInit {
  warehouses: WarehouseResponse[] = [];
  
  constructor(private inventoryService: InventoryService, private cd: ChangeDetectorRef){}

  ngOnInit() {
    this.loadWarehouses();
  }

  loadWarehouses() {
    this.inventoryService.getAllWarehouses().subscribe({
      next: (data) => {
        this.warehouses = data;
        this.cd.detectChanges();
      },
      error: (err) => console.error('Error fetching warehouses:', err)
    });
  }

  toggleStatus(warehouse: WarehouseResponse) {
    if (warehouse.active) {
      if(confirm(`Are you sure you want to deactivate "${warehouse.name}"?`)) {
        this.inventoryService.deactivateWarehouse(warehouse.id).subscribe({
          next: () => {
            warehouse.active = false;
            this.cd.detectChanges();
          },
          error: (err) => alert('Failed to deactivate: ' + err.message)
        });
      }
    } else {
      this.inventoryService.activateWarehouse(warehouse.id).subscribe({
        next: () => {
          warehouse.active = true;
          this.cd.detectChanges();
        },
        error: (err) => alert('Failed to activate: ' + err.message)
      });
    }
  }
}
