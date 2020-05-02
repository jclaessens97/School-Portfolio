import { Component, OnInit, ViewChild, Input, } from "@angular/core";
import { LobbyDetails } from "../../models/LobbyDetails";
import { Router } from "@angular/router";
import { CdkVirtualScrollViewport } from "@angular/cdk/scrolling";
import { ChatService } from "../../services/chat.service";
import { WebsocketService } from "../../services/websocket.service";

@Component({
  selector: "app-chat",
  templateUrl: "./chat.component.html",
  styleUrls: ["./chat.component.css"]
})
export class ChatComponent implements OnInit {
  messages: any;
  msg: string;
  wsclient: any;
  lobbyDetails: LobbyDetails;
  systemColor: string = 'joined';
  @Input() playerId: any;
  @Input() cluedoId: number;
  @ViewChild(CdkVirtualScrollViewport, { static: false }) viewport: CdkVirtualScrollViewport;


  constructor(private chatService: ChatService, private websocketService: WebsocketService, private router: Router, ) {
    this.wsclient = websocketService.getClient();
  }

  ngOnInit () {
    this.lobbyDetails = <LobbyDetails>history.state.lobbyDetails;
    if (!this.lobbyDetails) {
      this.router.navigate(["/lobbies"]);
      return;
    }
    this.loadMessages(this.cluedoId);
    this.websocketService.connect((client) => {
      client.subscribe(`/chat/${this.cluedoId}`, (msg) => {
        this.showMessage(JSON.parse(msg.body));

      });
    })
  }

  ngOnDestroy () {
    this.websocketService.disconnect();
  }


  sendMsg () {
    this.chatService.sendMessage(this.cluedoId, this.msg, this.playerId).subscribe(() => { })
    this.msg = ''
  }

  showMessage (message) {
    if (message.content.includes('joined')){
      this.systemColor = 'joined';
    } else {
      this.systemColor = 'left';
    }
    this.messages.push(message);
    this.messages = [...this.messages];
    this.viewport.scrollToIndex(this.messages.length);
  }

  loadMessages (cluedoId) {
    this.chatService.getMessages(cluedoId).subscribe(data => {
      this.messages = data;
    });
  }

}
