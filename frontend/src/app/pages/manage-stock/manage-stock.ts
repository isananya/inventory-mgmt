import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { InventoryResponse, StockRequest, WarehouseResponse } from '../../core/models/inventory';
import { InventoryService } from '../../core/services/inventory';

@Component({
  selector: 'app-manage-stock',
  imports: [CommonModule, FormsModule],
  templateUrl: './manage-stock.html',
  styleUrl: './manage-stock.css',
})

export class ManageStockComponent implements OnInit {

  warehouses: WarehouseResponse[] = [];
  
  expandedWarehouseId: number | null = null;
  
  inventoryMap: { [key: number]: InventoryResponse[] } = {};
  isLoadingInventory = false;

  showModal = false;
  modalAction: 'ADD' | 'DEDUCT' = 'ADD';
  selectedItem: InventoryResponse | null = null; 
  quantityInput: number = 0;
  isSubmitting = false;

  constructor(private inventoryService: InventoryService, private cd: ChangeDetectorRef){}

  ngOnInit() {
    this.loadWarehouses();
  }

  loadWarehouses() {
    this.inventoryService.getActiveWarehouses().subscribe({
      next: (data) =>{
        this.warehouses = data;
        this.cd.detectChanges()
      },
      error: (err) => console.error('Failed to load warehouses', err)
    });
  }

  toggleWarehouse(id: number) {
    if (this.expandedWarehouseId === id) {
      this.expandedWarehouseId = null;
      this.cd.detectChanges();
      return;
    }

    this.expandedWarehouseId = id;
    this.cd.detectChanges();

    if (!this.inventoryMap[id]) {
      this.isLoadingInventory = true;
      this.inventoryService.getInventoryByWarehouse(id).subscribe({
        next: (data) => {
          this.inventoryMap[id] = data;
          this.isLoadingInventory = false;
          this.cd.detectChanges();
        },
        error: (err) => {
          console.error(err);
          this.isLoadingInventory = false;
          this.cd.detectChanges();
        }
      });
    }
  }

  openActionModal(action: 'ADD' | 'DEDUCT' , item: InventoryResponse) {
    this.modalAction = action;
    this.selectedItem = item;
    this.quantityInput = 0;
    this.showModal = true;
    this.cd.detectChanges();
  }

  closeModal() {
    this.showModal = false;
    this.selectedItem = null;
    this.cd.detectChanges();
  }

  submitStockChange() {
    if (!this.selectedItem || this.quantityInput <= 0) return;

    this.isSubmitting = true;

    const request: StockRequest = {
      productId: this.selectedItem.product.id,
      warehouseId: this.selectedItem.warehouse.id,
      quantity: this.quantityInput
    };

    let apiCall;
    if (this.modalAction === 'ADD') apiCall = this.inventoryService.addStock([request]);
    else apiCall = this.inventoryService.deductStock([request]);

    apiCall.subscribe({
      next: () => {
        alert('Stock updated successfully!');
        this.isSubmitting = false;
        this.closeModal();
        this.cd.detectChanges();
        
        const wId = this.selectedItem!.warehouse.id;
        delete this.inventoryMap[wId]; 
        this.toggleWarehouse(wId); 
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error(err);
        alert('Failed to update stock.');
        this.isSubmitting = false;
        this.cd.detectChanges();
      }
    });
  }
}
