import { ChangeDetectorRef, Component, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { CartService, CartItem } from '../../core/services/cart';
import { InventoryService } from '../../core/services/inventory';
import { StockCheckResponse, StockRequest } from '../../core/models/inventory';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './cart.html',
  styleUrls: ['./cart.css']
})
export class CartComponent {

  private cartService = inject(CartService);
  private router = inject(Router);

  cartItems = this.cartService.cartItems;

  totalPrice = computed(() => {
    return this.cartItems().reduce((total, item) => {
      return total + (item.product.price * item.quantity);
    }, 0);
  });

  constructor(private inventoryService: InventoryService, private cd: ChangeDetectorRef) {}

  isCheckingStock = false;
  globalError: string | null = null;
  outOfStockIds = new Set<number>();

  updateQuantity(itemId: number, newQty: number) {
    this.globalError = null; 
    this.outOfStockIds.clear();
    this.cartService.updateQuantity(itemId, newQty);
  }

  removeItem(itemId: number) {
    this.globalError = null;
    this.outOfStockIds.clear();
    this.cartService.removeFromCart(itemId);
  }

  proceedToCheckout() {
    const items = this.cartItems();
    if (items.length === 0) return;

    this.isCheckingStock = true;
    this.globalError = null;
    this.outOfStockIds.clear();

    const requestPayload: StockRequest[] = items.map(item => ({
      productId: item.product.id,
      warehouseId: 1, 
      quantity: item.quantity
    }));

    this.inventoryService.checkStock(requestPayload).subscribe({
      next: (responses: StockCheckResponse[]) => {
        const failedItems = responses.filter(r => !r.available);

        if (failedItems.length > 0) {
          failedItems.forEach(f => this.outOfStockIds.add(f.productId));
          this.globalError = "Some items are out of stock. Please reduce quantity or remove them.";
          this.isCheckingStock = false;
          this.cd.detectChanges();
        } else {
          this.isCheckingStock = false;
          this.cd.detectChanges();
          this.router.navigate(['/checkout']);
        }
      },
      error: (err) => {
        console.error("Stock check failed", err);
        this.globalError = "System error checking stock. Please try again later.";
        this.isCheckingStock = false;
        this.cd.detectChanges();
      }
    });
  }

  isOutOfStock(productId: number): boolean {
    return this.outOfStockIds.has(productId);
  }
}