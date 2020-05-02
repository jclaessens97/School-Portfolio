import { Component } from "@angular/core";
import { TranslateService } from "@ngx-translate/core";
import { UserService } from "../services/user.service";
import { Router } from "@angular/router";

import { AuthenticationService } from "../services/authentication.service";
import { Language } from "../models/enums";
import { UserInfo } from "../models/UserInfo";

const LANGUAGE_STORAGE_STRING = "language";
const MOBILE_WIDTH = 630;

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.scss"]
})
export class AppComponent {
  currentToken: string;
  drawerOpen: boolean = true;
  friendsOpen: boolean = false;
  notificationsOpen: boolean = true;
  notificationsNumber: number = 0;

  get userInfo(): UserInfo {
    return this.userService.currentUserValue;
  }

  get isAdmin(): Boolean {
    return this.userService.isAdmin();
  }

  get languageEnum() {
    return Language;
  }

  constructor(
    private translate: TranslateService,
    private userService: UserService,
    private router: Router,
    private authenticationService: AuthenticationService
  ) {
    this.initLanguage();
    this.authenticationService.currentToken.subscribe(
      x => (this.currentToken = x)
    );
    if (this.authenticationService.isLoggedIn()) this.getUserInfo();
    this.drawerOpen = window.innerWidth >= MOBILE_WIDTH;
  }

  getUserInfo() {
    this.userService.getUserInfo().subscribe(() => {});
  }

  initLanguage() {
    const language = localStorage.getItem(LANGUAGE_STORAGE_STRING);
    if (language) this.setLanguage(language);
    else this.setLanguage(Language.EN);
  }

  setLanguage(language) {
    this.translate.setDefaultLang(language);
    this.translate.use(language);
    localStorage.setItem(LANGUAGE_STORAGE_STRING, language);
  }

  logout() {
    this.authenticationService.logout();
    this.router.navigate(["/login"]);
    this.navAction();
  }

  goToAccount() {}

  // state: 0 = toggle, 1 = open, 2 = close
  toggleDrawer(state = 0) {
    switch(state){
      case 0:
        this.drawerOpen = !this.drawerOpen;
        break;
      case 1:
        this.drawerOpen = true;
        break;
      case 2:
        this.drawerOpen = false;
        break;
    }
  }

  toggleFriends(state = 0) {
    switch(state){
      case 0:
        this.friendsOpen = !this.friendsOpen;
        break;
      case 1:
        this.friendsOpen = true;
        break;
      case 2:
        this.friendsOpen = false;
        break;
    }
  }

  toggleNotifications(){
    this.notificationsOpen = !this.notificationsOpen;
  }

  navAction() {
    if (window.innerWidth < MOBILE_WIDTH) {
      this.toggleDrawer();
    }
  }

  routeStatistics() {
    this.userService.getUserInfo().subscribe((data: UserInfo) => {
      this.router.navigate(["/statistics"], { state: { username: data.username } });
    });
  }

  receiveNumber ($event) {
    this.notificationsNumber = $event
  }
}
