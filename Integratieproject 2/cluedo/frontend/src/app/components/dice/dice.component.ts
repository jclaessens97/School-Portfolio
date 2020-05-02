import {
  Component, Directive, EventEmitter,
  Input, Output,
} from '@angular/core';
import { DiceService } from '../../services//dice.service';
import DiceDto from '../../models/DiceDto';
import Utils from '../../../util';

@Component({
  selector: 'app-dice',
  templateUrl: './dice.component.html',
  styleUrls: ['./dice.component.css'],
  providers: [DiceService],
})
export class DiceComponent {
  // Properties
  @Input() singleDice = false;
  @Input() canRoll:boolean;

  // Local data
  readonly DICE_ASSET_PATH = 'assets/img/dice';
  private diceValue1 = 1;
  private diceValue2 = 1;
  rolling = false;
  total: number;
  @Output() rolledTotal = new EventEmitter<number>();
  private diceDto: DiceDto;

  private _animationId: number;
  private _randomDuration: number;
  private _animationStartTime = null;

  // Hooks
  constructor(
    private diceService: DiceService,
  ) { }

  // Computed Properties

  get getTotal() {
    if(this.total) return this.total;
    return " "
  }

  get dice1() {
    return `${this.DICE_ASSET_PATH}/${this.diceValue1}.png`;
  }

  get dice2() {
    return `${this.DICE_ASSET_PATH}/${this.diceValue2}.png`;
  }

  // Methods
  onRollClick() {
    this.rolling = true;
    this._randomDuration = Utils.getRandomNumberBetween(1000, 2250);
    this._animationId = requestAnimationFrame(this.animateDiceRoll.bind(this));

    // Perform API call and cache the dto to read when animation ended
    this.diceService
      .getDiceValues(this.singleDice)
      .subscribe((data: DiceDto) =>{
        this.diceDto = data;
      } );
  }

  private animateDiceRoll(timestamp) {
    if (!this._animationStartTime) {
      this._animationStartTime = timestamp;
    }

    this.diceValue1 = this.getRandomDiceValue();
    if (!this.singleDice) {
      this.diceValue2 = this.getRandomDiceValue();
    }

    const progress = timestamp - this._animationStartTime;
    if (progress > this._randomDuration) {
      // Animation ended
      this._animationStartTime = timestamp;
      cancelAnimationFrame(this._animationId);
      this.rolling = false;

      // Set definitive values from api request
      this.diceValue1 = this.diceDto.roll1;

      if (!this.singleDice) {
        this.diceValue2 = this.diceDto.roll2;
      }

      this.total = this.diceDto.total;
      this.rolledTotal.emit(this.total);
      return;
    }

    this._animationId = requestAnimationFrame(this.animateDiceRoll.bind(this));
  }

  private getRandomDiceValue() {
    return Utils.getRandomNumberBetween(1, 6);
  }
}
