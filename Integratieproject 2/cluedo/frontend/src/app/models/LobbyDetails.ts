import { Player } from "./Player";

export interface LobbyDetails {
  cluedoId: number;
  lobbyName: string;
  players: Array<Player>;
  maxPlayers: number;
  turnDuration: number;
  playerId: number;
  hostPlayerId: number;
  active: boolean;
}
