import { WarehouseResponse } from "./inventory";
import { Product } from "./product";

export interface WarehouseStat extends WarehouseResponse {
  pendingOrders: number; 
  performance: number; 
}

export interface LocationStat {
  name: string;  
  count: number;
  percentage: number;
}
