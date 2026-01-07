export type PaymentMode = 'UPI' | 'CARD' | 'COD';

export interface InvoiceRequest {
  paymentMode: PaymentMode;
  email: string;
}

export interface Invoice {
  id: number;
  orderId: number;
  customerId: number;
  totalAmount: number;
  paymentMode: 'UPI' | 'CARD' | 'COD';
  paymentStatus: 'PENDING' | 'PAID' | 'FAILED' | 'REFUNDED';
  createdAt: string;
}

export interface PageInvoiceResponse {
  content: Invoice[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}