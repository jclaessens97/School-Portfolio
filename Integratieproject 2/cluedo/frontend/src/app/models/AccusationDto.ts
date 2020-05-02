import {Player} from './Player';


export interface AccusationDto {
  accusationOutcome: boolean,
  winningPlayer: Player,
  gameHasEnded: boolean,
}
