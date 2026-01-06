export interface DashboardStats {
  totalRevenue: number;
  totalOrders: number;
  totalProducts: number;
  totalUsers: number;
  lowStockCount: number;
  monthlySales: { month: string; amount: number }[]; // For the graph
  recentOrders: {
    id: number;
    customerName: string;
    amount: number;
    status: string;
    date: string;
  }[];
  ordersByStatus: { status: string; count: number }[];
}