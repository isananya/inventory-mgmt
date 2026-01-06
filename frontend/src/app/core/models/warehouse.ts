import { WarehouseResponse } from "./inventory";

export interface WarehouseStat extends WarehouseResponse {
  pendingOrders: number; 
  performance: number; 
}

export interface LocationStat {
  name: string;  
  count: number;
  percentage: number;
}
