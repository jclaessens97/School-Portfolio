import { Injectable } from '@angular/core';
import * as Stomp from 'stompjs';
import SockJs from 'sockjs-client';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class WebsocketService {
    private readonly WEBSOCKET_URL = `${environment.websocket}`;
    private wsclient: Stomp;
    private socket = new SockJs(this.WEBSOCKET_URL);
    private headers = {
        'Authorization': `Bearer ${this.authenticationService.currentTokenValue}`,
    };

    constructor(private authenticationService: AuthenticationService) {

        this.wsclient = Stomp.over(this.socket);
        this.initialize();
    }

    initialize () {
      const self = this;
      this.wsclient.connect(this.headers, (frame) => {
          self.wsclient.subscribe('/errors', (msg) => {
              console.error(msg);
          });
      }, (err) => {
          console.error(err);
      });
    }

    async connect (method) {
      while(!this.wsclient.connected) {
        console.log('Waiting 500ms for connection');
        await this.delay(500);
      } 
      method(this.wsclient);
    }
    

    disconnect() {
      for (const sub in this.wsclient.subscriptions) {
        if (this.wsclient.subscriptions.hasOwnProperty(sub)) {
          this.wsclient.unsubscribe(sub);
        }
      }
    }

    getClient () {
        return this.wsclient;
    }


    delay(ms: number) {
      return new Promise( resolve => setTimeout(resolve, ms) );
  }
  
}