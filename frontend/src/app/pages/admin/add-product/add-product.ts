import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ProductRequest } from '../../../core/models/inventory';
import { ProductService } from '../../../core/services/product';

@Component({
  selector: 'app-add-product',
  imports: [CommonModule, FormsModule],
  templateUrl: './add-product.html',
  styleUrl: './add-product.css',
})

export class AddProductComponent implements OnInit {
  data: ProductRequest = {
    productCode: '',
    name: '',
    brand: '',
    price: 0,
    description: '',
    categoryId: 0,
    imageUrl: '',
    specifications: {}
  };
  
  categories: any[] = [];
  constructor(private productService: ProductService, private cd: ChangeDetectorRef){}

  ngOnInit() {
    this.productService.getCategories().subscribe({
      next: (res) => this.categories = res,
      error: () => alert('Failed to load categories')
    });
  }

  onSubmit() {
    this.productService.createProduct(this.data).subscribe({
      next: () => { 
        alert('Product Created Successfully!');
        this.data = { 
          productCode: '', name: '', brand: '', price: 0, categoryId: 0, specifications: {} };
        this.cd.detectChanges();
      },
      error: (err) => alert('Failed: ' + (err.error?.message || err.message))
    });
  }
}
