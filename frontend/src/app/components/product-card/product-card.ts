import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { CartService } from '../../core/services/cart';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './product-card.html',
  styleUrl: './product-card.css',
})
export class ProductCardComponent {
  @Input() product: any;

  constructor(private cart: CartService) {}

  quantity() {
    return this.cart.getQuantity(this.product.id);
  }

  add() {
    this.cart.addToCart(this.product);
  }

  update(change: number) {
    const newQty = this.quantity() + change;
    this.cart.updateQuantity(this.product.id, newQty);
  }
}
