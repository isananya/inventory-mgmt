import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../core/services/product';
import { ProductCardComponent } from '../../components/product-card/product-card';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-shop',
  standalone: true,
  imports: [CommonModule, ProductCardComponent, FormsModule],
  templateUrl: './shop.html',
  styleUrls: ['./shop.css']
})
export class ShopComponent implements OnInit {
  sidebarOpen = false;
  categories: any[] = [];
  products: any[] = [];
  searchQuery = '';
  
  currentPage = 0;
  totalPages = 0;
  currentCategoryId: number | null = null;

  constructor(private productService: ProductService, private cd: ChangeDetectorRef) {}

  ngOnInit() {
    this.loadCategories();
    this.loadProducts();
  }

  toggleSidebar() {
    this.sidebarOpen = !this.sidebarOpen;
  }

  loadCategories() {
    this.productService.getCategories().subscribe(cats => this.categories = cats);
  }

  loadProducts() {
    if (this.currentCategoryId) {
      this.productService.getProductsByCategory(this.currentCategoryId, this.currentPage)
        .subscribe(res => this.handleResponse(res));
    } else if (this.searchQuery) {
      this.productService.searchProducts(this.searchQuery, this.currentPage)
        .subscribe(res => this.handleResponse(res));
    } else {
      this.productService.getProducts(this.currentPage)
        .subscribe(res => this.handleResponse(res));
    }
  }

  handleResponse(response: any) {
    this.products = response.content;
    this.totalPages = response.totalPages;
    this.cd.detectChanges();
  }

  selectCategory(id: number) {
    this.currentCategoryId = id;
    this.searchQuery = '';
    this.currentPage = 0;
    this.sidebarOpen = false; 
    this.loadProducts();
  }

  onSearch() {
    this.currentCategoryId = null;
    this.currentPage = 0;
    this.loadProducts();
  }

  changePage(delta: number) {
    this.currentPage += delta;
    this.loadProducts();
  }
}