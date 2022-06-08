import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { UserService } from "../services/user.service";

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private router: Router, private auth: UserService) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {

    if (localStorage.getItem('access_token') && this.auth.currentUser()) {
      return true;
    }

    let redirection = route.data.authRouteTo;
    if (redirection) {
      this.router.navigate([redirection]);
    }
    return false;
  }

}
