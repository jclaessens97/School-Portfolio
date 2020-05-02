import { CharacterType } from './enums';

export interface Player {
    name: string,
    characterType: CharacterType,
    playerId: number,
  }