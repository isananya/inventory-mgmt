import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login';
import { SignupComponent } from './pages/signup/signup';
import { ShopComponent } from './pages/shop/shop';
import { CartComponent } from './pages/cart/cart';
import { CheckoutComponent } from './pages/checkout/checkout';

export const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'signup', component: SignupComponent },
    { path: 'shop', component: ShopComponent },
    { path: 'cart', component: CartComponent},
    { path: 'checkout', component: CheckoutComponent}
];
