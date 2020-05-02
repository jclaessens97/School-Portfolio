import { Component, EventEmitter, Input, OnInit, Output } from "@angular/core";
import { GameboardService } from "../../services/gameboard.service";
import { UserService } from "src/app/services/user.service";
import { User } from "src/app/models/User";
import { MatSnackBar } from "@angular/material/snack-bar";
import { FormGroup, FormBuilder } from "@angular/forms";
import { UserInfo } from "../../models/UserInfo";
import { WebsocketService } from "../../services/websocket.service";
import {Router} from '@angular/router';

@Component({
  selector: "friend-list",
  templateUrl: "./friend-list.component.html",
  styleUrls: ["./friend-list.component.css"]
})
export class FriendListComponent implements OnInit {
  friends: Array<User>;
  hasFriends: Boolean = false;
  requests: Array<User>;
  hasRequests: Boolean = false;
  addFriendForm: FormGroup;
  userInfo: UserInfo;

  constructor(
    private userService: UserService,
    private formBuilder: FormBuilder,
    private _snackBar: MatSnackBar,
    private router: Router,
    private websocketService: WebsocketService
  ) { }

  ngOnInit () {
    this.refresh();
    this.addFriendForm = this.formBuilder.group({
      username: [""]
    });
    this.connect();
  }

  refresh () {
    this.userService.getFriends().subscribe(f => {
      this.friends = <Array<User>>f;
      this.friends.length > 0 ? this.hasFriends = true : this.hasFriends = false;
    });
    this.userService.getPendings().subscribe(r => {
      this.requests = <Array<User>>r;
      this.requests.length > 0 ? this.hasRequests = true : this.hasRequests = false;
    });

  }

  open (friend: User) { }

  confirm (friend: User) {
    this.userService.confirmFriend(friend).subscribe(
      () => {
        this.refresh();
        this.hasRequests = false;
      },
      error =>
        this._snackBar.open("An unexpected error has occured.", "Close", {
          duration: 5000
        })
    );
  }
  deletePending (friend: User) {
    this.userService.deletePending(friend).subscribe(
      () => this.refresh(),
      error =>
        this._snackBar.open("An unexpected error has occured.", "Close", {
          duration: 5000
        })
    );
  }

  block (friend: User) {
    this.userService.blockFriend(friend).subscribe(
      () => this.refresh(),
      error =>
        this._snackBar.open("An unexpected error has occured.", "Close", {
          duration: 5000
        })
    );
  }

  delete (friend: User) {
    this.userService.delete(friend).subscribe(
      () => this.refresh(),
      error =>
        this._snackBar.open("An unexpected error has occured.", "Close", {
          duration: 5000
        })
    );
  }

  add () {
    const friend = this.addFriendForm.controls.username.value;
    this.userService.addFriend(friend).subscribe(
      () =>
        this._snackBar.open("A request has been sent to " + friend, "Close", {
          duration: 5000
        }),
      error =>
        this._snackBar.open("This user does not exist.", "Close", {
          duration: 5000
        })
    );
  }


  routeStatistics (userName: string) {
    this.router.navigate(["/statistics"], { state: { username: userName } });
  }

  connect () {
    this.websocketService.connect(client => {
      this.userInfo = this.userService.currentUserValue;
      client.subscribe(`/friendRequests/${this.userInfo.username}`, msg => {
        this.refresh();
      });
    })

  }
}
