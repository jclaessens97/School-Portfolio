import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LobbyService {
  private readonly LOBBY_URL_BASE = `${environment.apiUrl}/lobby`;
  private readonly CREATE_LOBBY_URL = `${this.LOBBY_URL_BASE}/create`;
  private readonly START_GAME_URL = `${this.LOBBY_URL_BASE}/start`;
  private readonly CHANGE_GAME_SETTINGS_URL = `${this.LOBBY_URL_BASE}/update`;
  private readonly JOIN_LOBBY_URL = `${this.LOBBY_URL_BASE}/join`;
  private readonly GET_LOBBY_LIST_URL = `${this.LOBBY_URL_BASE}/list`;
  private readonly GET_LOBBY_LIST_URL_NEW = `${this.LOBBY_URL_BASE}/list/new`;
  private readonly GET_LOBBY_LIST_URL_JOINED = `${this.LOBBY_URL_BASE}/list/joined`;
  private readonly GET_OPEN_GAMES_URL = `${this.LOBBY_URL_BASE}/games`;
  private readonly GET_LOBBY_DETAILS_URL = `${this.LOBBY_URL_BASE}/details`;
  private readonly LEAVE_LOBBY_URL = `${this.LOBBY_URL_BASE}/leave`;
  private readonly KICK_FROM_LOBBY_URL = `${this.LOBBY_URL_BASE}/kick`;
  private readonly GET_FREE_CHARACTERS_URL = `${this.LOBBY_URL_BASE}/free-characters`;


  constructor(private http: HttpClient) { }

  createLobby(characterType) {
    return this.http.post(this.CREATE_LOBBY_URL, null, {
      params: {
        characterType,
      },
    });
  }

  joinLobby(cluedoId, characterType) {
    return this.http.post(this.JOIN_LOBBY_URL, null, {
      params: {
        cluedoId,
        characterType,
      },
    });
  }

  leaveLobby(cluedoId, playerId) {
    return this.http.delete(this.LEAVE_LOBBY_URL, {
      params: {
        cluedoId,
        playerId,
      },
    });
  }

  kickFromLobby(cluedoId, playerId) {
    return this.http.delete(this.KICK_FROM_LOBBY_URL, {
      params: {
        cluedoId,
        playerId,
      },
    });
  }

  getLobbiesJoined() {
    return this.http.get(this.GET_LOBBY_LIST_URL_JOINED);
  }

  getLobbiesNotJoined() {
    return this.http.get(this.GET_LOBBY_LIST_URL_NEW);
  }

  getOpenGames() {
    return this.http.get(this.GET_OPEN_GAMES_URL);
  }

  getLobbyDetails(cluedoId) {
    return this.http.get(this.GET_LOBBY_DETAILS_URL, {
      params: {
        cluedoId
      },
    });
  }

  startGame(cluedoId) {
    return this.http.put(this.START_GAME_URL, null, {
      params: {
        cluedoId,
      }
    })
  }

  getFreeCharacters(cluedoId) {
    return this.http.get(this.GET_FREE_CHARACTERS_URL, {
      params: {
        cluedoId,
      }
    })
  }

  updateGameSettings(lobbyDetails) {
    return this.http.put(this.CHANGE_GAME_SETTINGS_URL, null, {
      params: {
        cluedoId: lobbyDetails.cluedoId,
        lobbyName: lobbyDetails.lobbyName,
        turnDuration: lobbyDetails.turnDuration,
        maxPlayers: lobbyDetails.maxPlayers,
      }
    });
  }

}
