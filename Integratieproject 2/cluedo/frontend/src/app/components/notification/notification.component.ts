import { Component, OnInit, Output, EventEmitter } from '@angular/core';
import { UserInfo } from "../../models/UserInfo";
import { WebsocketService } from "../../services/websocket.service";
import { UserService } from "src/app/services/user.service";
import { InviteDto } from "../../models/InviteDto"
import { Router } from "@angular/router";

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.css']
})
export class NotificationComponent implements OnInit {
  userInfo: UserInfo;
  invitations: Array<InviteDto> = [];
  @Output() emitter = new EventEmitter<number>();

  constructor(private websocketService: WebsocketService, private userService: UserService, private router: Router, ) { }

  ngOnInit () {
    this.connect();
  }

  connect () {
    this.websocketService.connect(client => {
      this.userInfo = this.userService.currentUserValue;
      client.subscribe(`/invite/${this.userInfo.username}`, msg => {
        let data = <InviteDto>(JSON.parse(msg.body));
        this.invitations.push(data);
        this.emitter.emit(this.invitations.length)
      });
    })
  }

  join (id) {
    this.router.navigate(["/lobbies"], { state: { cluedoId: id } });
  }

}
