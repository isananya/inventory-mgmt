import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../../core/services/user';
import { UserProfileResponse, UserRole } from '../../../core/models/user';

@Component({
  selector: 'app-manage-users',
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './manage-users.html',
  styleUrl: './manage-users.css',
})

export class ManageUsersComponent implements OnInit {
  
  users: UserProfileResponse[] = [];
  isModalOpen = false;
  userForm!: FormGroup;

  availableRoles: UserRole[] = [
    'ADMIN', 
    'SALES_EXECUTIVE', 
    'WAREHOUSE_MANAGER', 
    'FINANCE_OFFICER', 
    'CUSTOMER'
  ];

  isFormModalOpen = false;      
  isConfirmModalOpen = false; 
  
  confirmMessage = '';
  pendingAction: (() => void) | null = null;

  toast = { message: '', type: 'success', visible: false };

  constructor(private userService : UserService, private fb: FormBuilder, private cd: ChangeDetectorRef){}

  ngOnInit() {
    this.initForm();
    this.loadUsers();
  }

  private initForm() {
    this.userForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      role: ['CUSTOMER', [Validators.required]]
    });
  }

  loadUsers() {
    this.userService.getAllUsers().subscribe({
      next: (data) => {
        this.users = data;
        console.log(this.users);
        this.cd.detectChanges();
      },
      error: (err) => console.error('Failed to load users', err)
    });
  }

  onDeleteClick(user: UserProfileResponse) {
    this.confirmMessage = `Are you sure you want to permanently delete user "${user.name}"?`;
    this.isConfirmModalOpen = true;
    // this.cd.detectChanges();
    this.pendingAction = () => {
      this.userService.deleteUser(user.id).subscribe({
        next: () => {
          // this.users = this.users.filter(u => u.id !== user.id);
          this.showToast('User deleted successfully', 'success');
          this.loadUsers();
          this.cd.detectChanges();
        },
        error: (err) => this.showToast(err.message || 'Delete failed', 'error')
      });
    };
  }

  onRoleChange(user: UserProfileResponse, event: Event) {
    const selectElement = event.target as HTMLSelectElement;
    const newRole = selectElement.value as UserRole;
    console.log('Selected Value:', newRole);
    const oldRole = user.role;

    console.log('Role Change Detected:', newRole); 

    if (newRole === oldRole) return;

    this.confirmMessage = `Change role of "${user.name}" to ${newRole}?`;
    this.isConfirmModalOpen = true;

    this.pendingAction = () => {
      this.userService.updateUserRole(user.id, newRole).subscribe({
        next: () => {
          user.role = newRole;
          this.showToast('Role updated successfully', 'success');
        },
        error: (err) => {
          this.showToast('Failed to update role', 'error');
          selectElement.value = oldRole;
        }
      });
    };
  }

  onSubmitUser() {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched(); 
      return;
    }
    this.userService.addUser(this.userForm.value).subscribe({
      next: () => {
        this.showToast('User created successfully!', 'success');        
        this.closeFormModal();
        this.loadUsers();
      },
      error: (err) => alert('Error creating user: ' + (err.error?.message || err.message))
    });
  }

  onConfirmYes() {
    if (this.pendingAction) this.pendingAction();
    this.isConfirmModalOpen = false;
    this.pendingAction = null;
  }

  onConfirmNo() {
    this.isConfirmModalOpen = false;
    this.pendingAction = null;
    this.loadUsers();
    this.cd.detectChanges();
  }

  showToast(message: string, type: 'success' | 'error') {
    this.toast = { message, type, visible: true };
    setTimeout(() => {
      this.toast.visible = false;
      this.cd.detectChanges();
    }, 3000);
  }

  openFormModal() {
    this.userForm.reset({ role: 'CUSTOMER' });
    this.isFormModalOpen = true;
    this.cd.detectChanges();
  }

  closeFormModal() {
    this.isFormModalOpen = false;
    this.cd.detectChanges();
  }
}
