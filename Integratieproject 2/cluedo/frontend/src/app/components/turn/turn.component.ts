import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CardType, CharacterType} from '../../models/enums';
import {PossibilitiesDto} from '../../models/PossibilitiesDto';
import {Turn} from '../../models/Turn';
import {Card} from '../make-suggestion/make-suggestion.component';
import {WebsocketService} from '../../services/websocket.service';

@Component({
  selector: 'turn',
  templateUrl: './turn.component.html',
  styleUrls: ['./turn.component.css']
})

export class TurnComponent{
  @Input() possibilities: PossibilitiesDto;
  @Input() currentTurn: Turn;
  @Output() passage = new EventEmitter<boolean>();
  @Output() roll = new EventEmitter<number>();
  @Input() gameId : number;
  @Input() character: CharacterType;




  getRoll(roll: number){if(this.possibilities.movesPossible)this.roll.emit(roll)}

  takePassage(){if(this.possibilities.roomWithPassage != undefined)this.passage.emit(true)}

}
