import { Component, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { CartService, CartItem } from '../../core/services/cart';

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

  constructor() {}
  updateQuantity(itemId: number, newQty: number) {
    this.cartService.updateQuantity(itemId, newQty);
  }

  removeItem(itemId: number) {
    this.cartService.removeFromCart(itemId);
  }

  proceedToCheckout() {
    if (this.cartItems().length === 0) return;
    this.router.navigate(['/checkout']);
  }
}