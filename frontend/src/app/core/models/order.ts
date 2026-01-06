export interface Address {
  line1: string;
  city: string;
  state: string;
  pincode: string;
}

export interface OrderItemRequest {
  productId: number;
  quantity: number;
}

export interface OrderRequest {
  customerId: number;
  address: Address;
  items: OrderItemRequest[];
}
export interface OrderResponse {
  orderId: number;
  customerId: number;
  status: string;  
  address: Address;
  totalAmount: number;
  createdAt: string;
}

export interface PageOrderResponse {
  content: OrderResponse[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}