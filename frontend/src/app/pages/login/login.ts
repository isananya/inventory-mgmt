import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMsg = '';
  isLoading = false;

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router, private cd: ChangeDetectorRef) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required]
    });
  }

  onSubmit() {
    this.errorMsg = '';

    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.auth.login(this.loginForm.value).subscribe({
      next: () => {
        this.isLoading = false;
        const role = this.auth.getRole();
        if (role === 'WAREHOUSE_MANAGER') this.router.navigate(['/inventory']);
        else if (role === 'ADMIN') this.router.navigate(['/admin-dashboard']);
        else this.router.navigate(['/shop']);      },
      error: (err) => {
        this.errorMsg = 'Invalid email or password';
        this.isLoading = false;
        this.cd.detectChanges();
      }
    });
  }
}
