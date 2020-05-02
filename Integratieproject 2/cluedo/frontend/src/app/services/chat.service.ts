import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';


@Injectable({
    providedIn: 'root'
})
export class ChatService {
    private readonly CHAT_URL_BASE = `${environment.apiUrl}/chat/`;

    constructor(private http: HttpClient) { }

    getMessages (cluedoId) {
        return this.http.get(this.CHAT_URL_BASE + cluedoId);
    }

    sendMessage (cluedoId, msg, playerId) {
        return this.http.post(this.CHAT_URL_BASE + "send",null, {
            params: {
                cluedoId, msg, playerId
            },
        })
    }
}
