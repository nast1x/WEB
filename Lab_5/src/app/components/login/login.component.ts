import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  username = '';
  password = '';
  errorMessage = '';
  loginFailed = false;

  constructor(private authService: AuthService, private router: Router) {}

  login() {
    this.loginFailed = false;
    this.errorMessage = '';

    const tempToken = btoa(`${this.username}:${this.password}`);

    this.authService.getMe(tempToken).subscribe({
      next: (user) => {
        sessionStorage.setItem('basicAuth', tempToken);
        sessionStorage.setItem('userId', user.id.toString());
        sessionStorage.setItem('username', user.username);
        sessionStorage.setItem('roles', JSON.stringify(user.roles));

        this.router.navigate(['/tasks']);
      },
      error: () => {
        this.loginFailed = true;
        this.errorMessage = 'Неверный логин или пароль. Проверьте данные и повторите попытку.';
      }
    });
  }
}
