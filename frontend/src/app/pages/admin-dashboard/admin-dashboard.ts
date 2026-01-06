import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { DashboardStats } from '../../core/models/admin';
import { AdminService } from '../../core/services/admin';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';

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

  constructor(private adminService: AdminService, private cd:ChangeDetectorRef){}

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
      datasets: [
        { 
          data: data, 
          label: 'Monthly Sales', 
          backgroundColor: '#0dc1a6', 
          borderRadius: 4
        }
      ]
    };
  }
}