import { Product } from "./product";

export interface StockRequest {
  productId: number;
  warehouseId: number;
  quantity: number;
}

export interface StockCheckResponse {
  productId: number;
  available: boolean;
  warehouseId: number;
}

export interface InventoryResponse {
  id: number;
  product: Product;
  warehouse: { id: number; name: string };
  quantity: number;
}

export interface CategoryRequest {
  name: string;
  description?: string;
  iconUrl?: string;
}

export interface ProductRequest {
  productCode: string; 
  name: string;
  brand: string; 
  price: number;
  description?: string;
  specifications?: Record<string, any>;
  imageUrl?: string;
  categoryId: number;
}

export interface WarehouseRequest {
  name: string;
  location: string;
}

export interface InventoryRequest {
  productId: number;
  warehouseId: number;
  quantity: number;
  lowStockThreshold?: number;
}

export interface WarehouseResponse {
  id: number;
  name: string;
  location: string;
  active: boolean;
}