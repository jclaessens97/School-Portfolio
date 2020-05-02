import { Injectable } from "@angular/core";
import { HttpClient, HttpHeaders } from "@angular/common/http";
import { environment } from "../../environments/environment";
import { Me } from "../models/Me";
import { UserInfo } from "../models/UserInfo";
import { AuthenticationService } from "./authentication.service";
import { User } from "../models/User";
import { BehaviorSubject, Observable } from "rxjs";
import { map } from "rxjs/operators";
import { Password } from "../models/Password";
import {PlayerInfo} from '../models/PlayerInfo';

@Injectable({ providedIn: "root" })
export class UserService {
  private readonly USERS_URL_BASE = `${environment.apiUrl}/users`;
  private readonly GET_FRIENDS_LIST_URL = `${this.USERS_URL_BASE}/friends`;
  private readonly GET_AVAILABLE_FRIENDS_LIST_URL = `${this.USERS_URL_BASE}/available_friends`;
  private readonly GET_BLOCKED_LIST_URL = `${this.USERS_URL_BASE}/blocked`;
  private readonly GET_PENDING_LIST_URL = `${this.USERS_URL_BASE}/pending`;
  private readonly ADD_FRIEND_URL = `${this.USERS_URL_BASE}/add`;
  private readonly CONFIRM_FRIEND_URL = `${this.USERS_URL_BASE}/confirm`;
  private readonly DELETE_PENDING_FRIEND_URL = `${this.USERS_URL_BASE}/delete_pending`;
  private readonly BLOCK_FRIEND_URL = `${this.USERS_URL_BASE}/block`;
  private readonly DELETE_FRIEND_URL = `${this.USERS_URL_BASE}/delete_friend`;
  private readonly INVITE_FRIEND_URL = `${this.USERS_URL_BASE}/invite`;
  private readonly USER_STATISTICS_URL_ID = `${this.USERS_URL_BASE}/statistics/id`;
  private readonly USER_STATISTICS_URL_USERNAME = `${this.USERS_URL_BASE}/statistics/username`;


  private currentUserSubject: BehaviorSubject<UserInfo>;

  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<UserInfo>(
      JSON.parse(localStorage.getItem("currentUser"))
    );
  }

  public get currentUserValue(): UserInfo {
    return this.currentUserSubject.value;
  }

  public isAdmin(): boolean {
    if (!this.currentUserValue) return false;
    return this.currentUserValue.roles.includes("ADMIN");
  }

  setCurrentUserValue(userInfo: UserInfo) {
    this.currentUserSubject.next(userInfo);
    localStorage.setItem("currentUser", JSON.stringify(userInfo));
  }

  register(user: Me) {
    return this.http.post(`${environment.authUrl}/register`, user);
  }

  updateInformation(userInfo: UserInfo) {
    return this.http.put(`${environment.authUrl}/updateUser`, userInfo);
  }

  getUserInfo() {
    return this.http.get(`${environment.authUrl}/userDetails`, {}).pipe(
      map(resp => {
        this.setCurrentUserValue(<UserInfo>resp);
        return resp;
      })
    );
  }

  clearInformation() {
    localStorage.removeItem("currentUser");
    this.currentUserSubject.next(null);
  }

  changePass(pass: Password) {
    return this.http.put(`${environment.authUrl}/changePass`, pass);
  }

  getFriends() {
    return this.http.get(this.GET_FRIENDS_LIST_URL);
  }

  getPendings() {
    return this.http.get(this.GET_PENDING_LIST_URL);
  }

  confirmFriend(friend: User) {
    return this.http.put(this.CONFIRM_FRIEND_URL, null, {
      params: {
        friendUserName: friend.userName
      }
    });
  }

  deletePending (friend: User) {
    return this.http.delete(this.DELETE_PENDING_FRIEND_URL, {
      params: {
        friendUserName: friend.userName
      }
    });
  }

  blockFriend (friend: User) {
    return this.http.put(this.BLOCK_FRIEND_URL, null, {
      params: {
        friendUserName: friend.userName
      }
    });
  }

  addFriend(userName: string) {
    return this.http.post(this.ADD_FRIEND_URL, null, {
      params: {
        friendUserName: userName
      }
    });
  }

  delete (friend: User) {
    return this.http.delete(this.DELETE_FRIEND_URL, {
      params: {
        friendUserName: friend.userName
      }
    });
  }


  getParamStatisticsPlayerInfo(playerId, cluedoId) {
    return this.http.get(`${this.USER_STATISTICS_URL_ID}?cluedoId=${cluedoId}&playerId=${playerId}`);
  }

  getParamStatisticsUserName(userName: string) {
    return this.http.get(this.USER_STATISTICS_URL_USERNAME, {
      params: {
        userName
      }
    });
  }

  getAvailableFriends(cluedoId) {
    return this.http.get(this.GET_AVAILABLE_FRIENDS_LIST_URL, {
      params: {
        cluedoId
      }
    });
  }

  inviteFriend(cluedoId, userName) {
    return this.http.post(this.INVITE_FRIEND_URL, null, {
      params: {
        cluedoId,
        userName
      }
    });
  }
}
