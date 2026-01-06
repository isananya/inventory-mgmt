import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CategoryRequest } from '../../../core/models/inventory';
import { AdminService } from '../../../core/services/admin';
import { ProductService } from '../../../core/services/product';

@Component({
  selector: 'app-add-category',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './add-category.html',
  styleUrl: './add-category.css',
})

export class AddCategoryComponent {
  data: CategoryRequest = { 
    name: '', 
    description: '', 
    iconUrl: '' 
  };

  constructor(private productService :ProductService, private cd: ChangeDetectorRef){};

  onSubmit() {
    this.productService.createCategory(this.data).subscribe({
      next: () => { 
        alert('Category Added!'); 
        this.data = { name: '', description: '', iconUrl: '' }; 
        this.cd.detectChanges();
      },
      error: (err) => alert('Error: ' + (err.error?.message || 'Server Error'))
    });
  }
}
