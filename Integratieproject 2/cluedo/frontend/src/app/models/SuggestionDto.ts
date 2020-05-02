import {CharacterType} from './enums';
import { Player} from './Player';
import {Card} from '../components/make-suggestion/make-suggestion.component';

export interface SuggestionDto {
  askingPlayer: Player;
  respondingPlayer: Player;
  cards: Card[];
}
