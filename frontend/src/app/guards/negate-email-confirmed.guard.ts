import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthGuard } from "./auth.guard";
import { EmailConfirmedGuard } from "./email-confirmed.guard";
import { UserService } from "../services/user.service";

@Injectable({
  providedIn: 'root'
})
export class NegateEmailConfirmedGuard implements CanActivate {

  constructor(private router: Router, private userService: UserService) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {

    if (!this.userService.currentUser()?.emailConfirmed) {
      return true;
    }

    let redirection = route.data.emailRouteTo;
    if (redirection) {
      this.router.navigate([redirection]);
    }
    return false;
  }

}
