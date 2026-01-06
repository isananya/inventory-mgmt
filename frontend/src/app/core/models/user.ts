export type UserRole = 'ADMIN' | 'SALES_EXECUTIVE' | 'WAREHOUSE_MANAGER' | 'FINANCE_OFFICER' | 'CUSTOMER';

export interface UserProfileResponse {
  id: number;
  name: string;
  email: string;
  role: UserRole;
  active: boolean;
}

export interface UserRequest {
  email: string;
  password: string;
  name: string;
  role: UserRole;
}

export interface ChangeRoleRequest {
  role: UserRole;
}