import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login';
import { SignupComponent } from './pages/signup/signup';
import { ShopComponent } from './pages/shop/shop';
import { CartComponent } from './pages/cart/cart';
import { CheckoutComponent } from './pages/checkout/checkout';
import { MyOrdersComponent } from './pages/my-orders/my-orders';
import { AdminDashboardComponent } from './pages/admin-dashboard/admin-dashboard';
import { ManageInventoryComponent } from './pages/admin/manage-inventory/manage-inventory';
import { AddCategoryComponent } from './pages/admin/add-category/add-category';

export const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'signup', component: SignupComponent },
    { path: 'shop', component: ShopComponent },
    { path: 'cart', component: CartComponent},
    { path: 'checkout', component: CheckoutComponent},
    { path: 'my-orders', component: MyOrdersComponent},
    { path: 'admin-dashboard', component: AdminDashboardComponent},
    { path: 'admin/inventory', component:ManageInventoryComponent,
        children: [
            {path: 'add-category', component: AddCategoryComponent}
        ]
    }
];
