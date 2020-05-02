import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';

import { UserService } from '../services/user.service';
import { UserInfo } from '../models/UserInfo';
import { Role } from '../models/Role';

@Injectable({
  providedIn: 'root'
})
export class AuthGuardAdmin implements CanActivate {
  constructor(
    private router: Router,
    private userService: UserService
) {}

canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    if (this.userService.isAdmin()) return true;
    
    this.router.navigate(['/unauthorized'], { });
    return false;
}
  
}
