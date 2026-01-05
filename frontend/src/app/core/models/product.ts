export interface Category {
  id: number;
  name: string;
  description?: string;
  iconUrl?: string;
}

export interface Product {
  id: number;
  productCode: string;
  name: string;
  brand: string;
  price: number;
  description?: string;
  specifications?: { [key: string]: any }; 
  imageUrl?: string;
  category: Category;
}

export interface PageProductResponse {
  content: Product[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  sort?: any;
}