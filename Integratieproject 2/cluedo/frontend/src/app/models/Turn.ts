import {Player} from './Player';

export interface Turn {
  player: Player;
  timeRemaining: number;
}
