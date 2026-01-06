import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { WarehouseRequest } from '../../../core/models/inventory';
import { InventoryService } from '../../../core/services/inventory';

@Component({
  selector: 'app-add-warehouse',
  imports: [CommonModule, FormsModule],
  templateUrl: './add-warehouse.html',
  styleUrl: './add-warehouse.css',
})

export class AddWarehouseComponent {
  data: WarehouseRequest = { name: '', location: '' };

  constructor(private inventoryService: InventoryService, private cd: ChangeDetectorRef) { }

  onSubmit() {
    this.inventoryService.createWarehouse(this.data).subscribe({
      next: () => { 
        alert('Warehouse Added!'); 
        this.data = { name: '', location: '' }; 
        this.cd.detectChanges();
      },
      error: (err) => alert('Failed: ' + err.message)
    });
  }
}
