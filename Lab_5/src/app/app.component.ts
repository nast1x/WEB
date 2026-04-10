import { Component, HostListener } from '@angular/core';
import { RouterOutlet, RouterLink, Router } from '@angular/router';
import { AuthService } from './services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  showLogoutModal = false;

  constructor(public authService: AuthService, private router: Router) {}

  @HostListener('window:beforeunload')
  clearSession() {
    sessionStorage.clear();
  }


  triggerLogout() {
    this.showLogoutModal = true;
  }


  confirmLogout() {
    this.showLogoutModal = false;
    this.authService.logout();
    this.router.navigate(['/login']);
  }


  cancelLogout() {
    this.showLogoutModal = false;
  }
}
