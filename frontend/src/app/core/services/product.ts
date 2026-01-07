import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Category, PageProductResponse } from '../models/product';
import { CategoryRequest, ProductRequest } from '../models/inventory';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) { }

  getProducts(page = 0, size = 30) {
    return this.http.get<PageProductResponse>(`${this.apiUrl}/products`, {
      params: new HttpParams().set('page', page).set('size', size)
    });
  }

  getCategories() {
    return this.http.get<Category[]>(`${this.apiUrl}/category`);
  }

  getProductsByCategory(catId: number, page = 0) {
    return this.http.get<any>(`${this.apiUrl}/products/category/${catId}`, {
      params: new HttpParams().set('page', page)
    });
  }

  searchProducts(name: string, page = 0) {
    return this.http.get<any>(`${this.apiUrl}/products/search/${name}`, {
      params: new HttpParams().set('page', page)
    });
  }

  createCategory(data: CategoryRequest) {
    return this.http.post(`${this.apiUrl}/category`, data);
  }

  createProduct(data: ProductRequest) {
    return this.http.post(`${this.apiUrl}/products`, data);
  }
}