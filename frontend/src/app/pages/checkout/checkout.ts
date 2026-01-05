import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, computed, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CartService } from '../../core/services/cart';
import { OrderService } from '../../core/services/order';
import { AuthService } from '../../core/services/auth';
import { Router, RouterLink } from '@angular/router';
import { BillingService } from '../../core/services/billing';
import { switchMap } from 'rxjs';

@Component({
  selector: 'app-checkout',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './checkout.html',
  styleUrl: './checkout.css',
})

export class CheckoutComponent {
  private fb = inject(FormBuilder);
  private cartService = inject(CartService);
  private orderService = inject(OrderService);
  private billingService = inject(BillingService);
  private authService = inject(AuthService);
  private router = inject(Router);
  private cd = inject(ChangeDetectorRef);

  isLoading = false;
  orderPlaced = false;
  checkoutForm: FormGroup;
  cartItems = this.cartService.cartItems;
  userId: number = 1;

  totalPrice = computed(() => {
    return this.cartItems().reduce((total, item) => {
      return total + (item.product.price * item.quantity);
    }, 0);
  });

  constructor() {
    if (this.cartItems().length === 0) {
      this.router.navigate(['/cart']);
    }

    this.checkoutForm = this.fb.group({
      line1: ['', Validators.required],
      city: ['', Validators.required],
      state: ['', Validators.required],
      pincode: ['', [Validators.required, Validators.pattern('^[0-9]{6}$')]],
      paymentMode: ['CARD', Validators.required],
      email: [this.authService.getEmail() || '', [Validators.required, Validators.email]],
      cardNumber: [''],
      upiId: ['']
    });
  }

  ngOnInit() {
    this.authService.getProfile().subscribe({
      next: (user) => {
        this.userId = user.id;
      },
      error: (err) => {
        console.log(err.error?.message || err.statusText)
      }
    });
  }

  onSubmit() {
    if (this.checkoutForm.invalid) return;

    this.isLoading = true;
    const formData = this.checkoutForm.value;

    const orderRequest = {
      customerId: this.userId,
      address: {
        line1: formData.line1,
        city: formData.city,
        state: formData.state,
        pincode: formData.pincode
      },
      items: this.cartItems().map(i => ({ productId: i.product.id, quantity: i.quantity }))
    };

    this.orderService.placeOrder(orderRequest).pipe(
      switchMap((res: any) => {
        const orderId = res; 
        
        const invoiceRequest = {
          paymentMode: formData.paymentMode,
          email: formData.email
        };
        return this.billingService.generateInvoice(orderId, invoiceRequest);
      })
    ).subscribe({
      next: () => {
        this.cartService.cartItems.set([]);
        localStorage.removeItem('cart');
        this.orderPlaced = true;
        this.isLoading = false;
        this.cd.detectChanges();
      },
      error: (err) => {
        const msg = err.error?.message || err.statusText || 'Server Error';
        alert('Order Failed: ' + msg);
        this.isLoading = false;
      }
    });
  }
}