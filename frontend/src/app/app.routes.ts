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
import { AddProductComponent } from './pages/admin/add-product/add-product';
import { AddWarehouseComponent } from './pages/admin/add-warehouse/add-warehouse';
import { AddInventoryComponent } from './pages/admin/add-inventory/add-inventory';
import { ManageWarehousesComponent } from './pages/admin/manage-warehouse/manage-warehouse';
import { ManageUsersComponent } from './pages/admin/manage-users/manage-users';
import { AllOrdersComponent } from './pages/all-orders/all-orders';
import { WarehouseDashboardComponent } from './pages/warehouse-dashboard/warehouse-dashboard';

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
            {path: 'add-category', component: AddCategoryComponent},
            {path: 'add-product', component: AddProductComponent},
            {path: 'add-warehouse', component: AddWarehouseComponent},
            {path: 'add-inventory', component: AddInventoryComponent},
            {path: 'manage-warehouses', component: ManageWarehousesComponent}
        ]
    },
    { path: 'admin/users', component: ManageUsersComponent},
    { path: 'all-orders', component: AllOrdersComponent},
    { path: 'warehouse-dashboard', component: WarehouseDashboardComponent}
];
