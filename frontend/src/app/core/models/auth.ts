export interface LoginRequest {
  email?: string;
  password?: string;
}

export interface SignupRequest {
  name: string;
  email: string;
  password: string;
  role?: string; 
}

export interface LoginResponse {
  email: string;
  name: string;
  role: string;
}

export interface User {
  id: number;
  name: string;
  email: string;
  role: 'ADMIN' | 'SALES_EXECUTIVE' | 'WAREHOUSE_MANAGER' | 'FINANCE_OFFICER' | 'CUSTOMER';
  active: boolean;
}