import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class GameboardService {
  private readonly BOARD_URL_BASE = `${environment.apiUrl}`;
  private readonly BOARD_URL  = `${this.BOARD_URL_BASE}/board`;

  constructor(private http: HttpClient) { }

  getGame(gameId) {
    return this.http.get(`${this.BOARD_URL}/game/` + gameId);
  }

  getCharacterType(playerId) {
    return this.http.get(`${this.BOARD_URL}/characterType?id=${playerId}`)
  }

  getChoices(characterType, gameId) {
    const type = characterType.toString().split('.');
    return this.http.get(`${this.BOARD_URL}/possibilities?type=${type[type.length-1]}&game=${gameId}`);
  }

  getMovablePositions(characterType, gameId, roll) {
    const type = characterType.toString().split('.');
    return this.http.get(`${this.BOARD_URL}/positions?type=${type[type.length-1]}&game=${gameId}&roll=${roll}`);
  }

  move(type, gameId, xCo, yCo) {
    const body = {type: type, gameId: gameId, location:{x: xCo, y: yCo}};
    const httpOptions = {headers: new HttpHeaders({'Content-Type': 'application/json', 'Authorization': 'my-auth-token'})};
    return this.http.put(`${this.BOARD_URL}/move`,body,httpOptions);
  }

  takePassage(characterType, gameId) {
    const body = {type: characterType, gameId: gameId};
    return this.http.put(`${this.BOARD_URL}/takePassage`, body);
  }

  endTurn(characterType, gameId) {
    const body = {type: characterType, gameId:gameId};
    return this.http.put(`${this.BOARD_URL}/endTurn`, body);
  }
}
