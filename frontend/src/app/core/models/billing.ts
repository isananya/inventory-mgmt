export type PaymentMode = 'UPI' | 'CARD' | 'COD';

export interface InvoiceRequest {
  paymentMode: PaymentMode;
  email: string;
}