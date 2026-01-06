import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-manage-inventory',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './manage-inventory.html',
  styleUrl: './manage-inventory.css',
})
export class ManageInventoryComponent {
  constructor(private router: Router) { }
  isRoot(): boolean {
    return this.router.url === '/admin/inventory';
  }
}
