import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { BehaviorSubject, Observable } from "rxjs";
import { map } from "rxjs/operators";
import { environment } from "../../environments/environment";
import { UserService } from "./user.service";

import { UserInfo } from "../models/UserInfo";

@Injectable({ providedIn: "root" })
export class AuthenticationService {
  private currentTokenSubject: BehaviorSubject<string>;
  public currentToken: Observable<string>;
  private userInfo: UserInfo;

  constructor(private http: HttpClient, private userService: UserService) {
    this.currentTokenSubject = new BehaviorSubject<string>(
      JSON.parse(localStorage.getItem("currentToken"))
    );
    this.currentToken = this.currentTokenSubject.asObservable();
  }

  public get currentTokenValue(): string {
    return this.currentTokenSubject.value;
  }

  login(username, password) {
    return this.http
      .post<any>(
        `${environment.authUrl}/authenticate`,
        { username, password },
        { observe: "response" }
      )
      .pipe(
        map(resp => {
          const jwtToken = resp.headers
            .get("Authorization")
            .replace("Bearer", "")
            .trim();
          localStorage.setItem("currentToken", JSON.stringify(jwtToken));
          this.currentTokenSubject.next(jwtToken);
          this.userService.getUserInfo().subscribe(() => {});
          return jwtToken;
        })
      );
  }

  logout() {
    localStorage.removeItem("currentToken");
    this.currentTokenSubject.next(null);
    this.userService.clearInformation();
  }

  isLoggedIn() {
    return this.currentTokenValue !== null;
  }
}
