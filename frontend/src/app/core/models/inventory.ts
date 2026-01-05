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