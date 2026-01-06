import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { InventoryRequest } from '../../../core/models/inventory';
import { Product } from '../../../core/models/product';
import { InventoryService } from '../../../core/services/inventory';
import { ProductService } from '../../../core/services/product';

@Component({
  selector: 'app-add-inventory',
  imports: [CommonModule, FormsModule],
  templateUrl: './add-inventory.html',
  styleUrl: './add-inventory.css',
})

export class AddInventoryComponent implements OnInit {
  data: InventoryRequest = {
    productId: 0,
    warehouseId: 0,
    quantity: 0,
    lowStockThreshold: 10
  };

  products: Product[] = []; 
  warehouses: any[] = [];
  
  constructor(private inventoryService: InventoryService, private productService: ProductService,
    private cd: ChangeDetectorRef){}

  ngOnInit() {
    this.inventoryService.getAllWarehouses().subscribe({
      next: (res) => {
        this.warehouses = res;
        this.cd.detectChanges();
      },
      error: (err) => console.error('Error loading warehouses', err)
    });

    this.productService.getProducts(0, 100).subscribe({
      next: (response) => {
        this.products = response.content || [];
        this.cd.detectChanges();
      },
      error: (err) => console.error('Error loading products', err)
    });
  }

  onSubmit() {
    if (this.data.productId && this.data.warehouseId) {
      this.inventoryService.addInventory(this.data).subscribe({
        next: () => { 
          alert('Inventory Updated Successfully!');
          this.data.quantity = 1;
          this.cd.detectChanges();
        },
        error: (err) => alert('Failed: ' + (err.error?.message || 'Server Error'))
      });
    }
  }
}
