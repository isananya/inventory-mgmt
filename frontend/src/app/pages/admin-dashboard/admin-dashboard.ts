import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { DashboardStats } from '../../core/models/admin';
import { AdminService } from '../../core/services/admin';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartOptions } from 'chart.js';

@Component({
  selector: 'app-admin-dashboard',
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.css',
})

export class AdminDashboardComponent implements OnInit {

  stats: DashboardStats | null = null;
  isLoading = true;

  public barChartLegend = true;
  public barChartPlugins = [];

  public barChartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [
      { data: [], label: 'Monthly Sales ($)', backgroundColor: '#0dc1a6', hoverBackgroundColor: '#0056b3' }
    ]
  };

  public barChartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          callback: (value) => 'Rs' + value
        }
      }
    }
  };

  public pieChartData: ChartConfiguration<'pie'>['data'] = {
    labels: [],
    datasets: [{ data: [], backgroundColor: [] }]
  };

  public pieChartOptions: ChartOptions<'pie'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'right' }
    }
  };

  constructor(private adminService: AdminService, private cd: ChangeDetectorRef) { }

  ngOnInit() {
    this.loadStats();
  }

  loadStats() {
    this.isLoading = true;
    this.adminService.getDashboardData().subscribe({
      next: (data) => {
        this.stats = data;
        this.updateChartData();
        this.isLoading = false;
        this.cd.detectChanges();
      },
      error: (err) => {
        console.error('API Error:', err);
        this.isLoading = false;
        this.cd.detectChanges();
      }
    });
  }

  private updateChartData() {
    if (!this.stats) return;

    const labels = this.stats.monthlySales.map(s => s.month);
    const data = this.stats.monthlySales.map(s => s.amount);
    this.barChartData = {
      labels: labels,
      datasets: [{
        data: data,
        label: 'Monthly Sales',
        backgroundColor: '#0dc1a6',
        borderRadius: 4
      }]
    };

    const statusLabels = this.stats.ordersByStatus.map(s => s.status);
    const statusData = this.stats.ordersByStatus.map(s => s.count);
    
    const statusColors = statusLabels.map(status => {
      switch(status) {
        case 'CREATED': return '#0dc1a6';
        case 'APPROVED': return '#0c9c86ff'
        case 'DELIVERED': return '#36ebd0ff';
        case 'SHIPPED': return '#156a5dff';
        case 'PACKED': return '#0e6457ff';
        case 'CANCELLED': return '#0a4d43ff';
        default: return '#6c757d';
      }
    });

    this.pieChartData = {
      labels: statusLabels,
      datasets: [{
        data: statusData,
        backgroundColor: statusColors,
        hoverOffset: 4
      }]
    };
  }
}