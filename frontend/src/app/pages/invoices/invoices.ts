import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Invoice, PageInvoiceResponse } from '../../core/models/billing';
import { BillingService } from '../../core/services/billing';

@Component({
  selector: 'app-invoices',
  imports: [CommonModule],
  templateUrl: './invoices.html',
  styleUrl: './invoices.css',
})

export class InvoicesComponent implements OnInit {
  invoices: Invoice[] = [];
  
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  totalElements = 0;
  isLoading = true;

  constructor(private billingService: BillingService, private cd: ChangeDetectorRef){}

  ngOnInit() {
    this.loadInvoices();
  }

  loadInvoices() {
    this.isLoading = true;
    this.billingService.getAllInvoices(this.currentPage, this.pageSize).subscribe({
      next: (response: PageInvoiceResponse) => {
        this.invoices = response.content;
        this.totalPages = response.totalPages;
        this.totalElements = response.totalElements;
        this.isLoading = false;
        this.cd.detectChanges();        
      },
      error: (err) => {
        console.error('Error fetching invoices:', err);
        this.isLoading = false;
        this.cd.detectChanges();
      }
    });
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.cd.detectChanges();
      this.loadInvoices();
    }
  }

  prevPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.cd.detectChanges();
      this.loadInvoices();
    }
  }
}
