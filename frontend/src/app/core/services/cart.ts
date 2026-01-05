import { Injectable, signal } from '@angular/core';
import { Product } from '../models/product';

export interface CartItem {
  product: Product;
  quantity: number;
}

@Injectable({ providedIn: 'root' })
export class CartService {
  cartItems = signal<CartItem[]>(this.loadCart());

  private loadCart(): CartItem[] {
    const saved = localStorage.getItem('cart');
    return saved ? JSON.parse(saved) : [];
  }

  private saveCart(items: CartItem[]) {
    localStorage.setItem('cart', JSON.stringify(items));
    this.cartItems.set(items);
  }

  addToCart(product: Product) {
    const current = this.cartItems();
    const existing = current.find(item => item.product.id === product.id);

    if (existing) {
      existing.quantity += 1;
      this.saveCart([...current]);
    } else {
      this.saveCart([...current, { product, quantity: 1 }]);
    }
  }

  removeFromCart(productId: number) {
    const current = this.cartItems().filter(item => item.product.id !== productId);
    this.saveCart(current);
  }

  updateQuantity(productId: number, quantity: number) {
    const current = this.cartItems();
    const item = current.find(i => i.product.id === productId);
    
    if (item) {
      if (quantity <= 0) {
        this.removeFromCart(productId);
      } else {
        item.quantity = quantity;
        this.saveCart([...current]);
      }
    }
  }

  getQuantity(productId: number): number {
    return this.cartItems().find(i => i.product.id === productId)?.quantity || 0;
  }
}