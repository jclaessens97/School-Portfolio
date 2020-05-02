import {Component, EventEmitter, Inject, Input, OnInit, Output, OnChanges} from '@angular/core';
import { DialogData } from "../note/note.component";
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from "@angular/material";
import { MakeSuggestionService } from "../../services/make-suggestion.service";
import { CardType } from "../../models/enums";

@Component({
  selector: "app-make-suggestion",
  templateUrl: "./make-suggestion.component.html",
  styleUrls: ["./make-suggestion.component.css"]
})
export class MakeSuggestionComponent implements OnInit, OnChanges {
  readonly CARD_PATH = 'assets/img/cards';
  readonly errorString = 'Cards can not be loaded';
  rightAccusation = false;
  @Input() asAccusation: boolean;
  @Input() gameId: number;
  @Input() character: any;
  @Input() room: any;
  @Output() canceller = new EventEmitter();
  @Output() madeSuggestion = new EventEmitter();
  constructor(
    public dialog: MatDialog,
    public suggestionService: MakeSuggestionService

  ) {}



  suggestionCards: Card[] = [
    {
      cardId: 0,
      cardType: CardType.CHARACTER,
      type: "",
      url: `${this.CARD_PATH}/character-card.png`
    },
    {
      cardId: 0,
      cardType: CardType.WEAPON,
      type: "",
      url: `${this.CARD_PATH}/weapon-card.png`
    },
    {
      cardId: 0,
      cardType: CardType.ROOM,
      type: "",
      url: `${this.CARD_PATH}/room-card.png`
    }
  ];
  allCards: Card[];

  ngOnInit() {
    this.suggestionService.getAllCards().subscribe((data: Card[]) => {
      this.allCards = data;
      // this.suggestionCards[2] = data.filter(card => card.type.toUpperCase() === this.room.roomType.toUpperCase())[0];
    });
  }

  ngOnChanges() {
    if (this.room) {
      this.suggestionCards[2].cardId = this.allCards.filter(card => card.type.toUpperCase() === this.room.roomType.toUpperCase())[0].cardId;
      this.suggestionCards[2].type = this.room.roomType;
      this.suggestionCards[2].url = `${this.CARD_PATH}/${this.room.roomType.toLowerCase()}.png`;
    }
  }

  get getCards() {
    return this.suggestionCards;
  }

  selectCard(type: CardType): void {
    if (this.allCards.filter(card => CardType[card.cardType as keyof typeof CardType] === type).length < 6) {
      this.dialog.open(ErrorScreen, {
        width: "available",
        panelClass: "my-dialog",
        data: this.errorString
      });
      throw new Error("Cards can not be loaded");
    }

    const dialogRef = this.dialog.open(ChooseCard, {
      width: "available",
      panelClass: "my-dialog",
      data: this.allCards.filter(
        card => CardType[card.cardType as keyof typeof CardType] === type
      )
    });

    dialogRef.componentInstance.cardPick.subscribe((card: Card) => {
      let index = 0;
      if (type === CardType.WEAPON) {
        index = 1;
      }
      if (type === CardType.ROOM) {
        index = 2;
      }
      this.suggestionCards[index] = card;
    });
  }

  sendSuggestion() {
    if (this.asAccusation) {
      this.suggestionService.makeAccusation(this.gameId, this.suggestionCards).subscribe((data: boolean) => {
        this.rightAccusation = true;
      });
    } else {
      this.suggestionService.makeSuggestion(this.gameId, this.suggestionCards).subscribe();
      this.madeSuggestion.emit();
    }
    this.cancel();
  }

  cancel(){
    this.canceller.emit();
  }
}






export interface Card {
  cardId: number;
  cardType: CardType;
  type: string;
  url: string;
}

@Component({
  selector: "choose-card",
  templateUrl: "make-suggestion.card-dialog.html"
})
export class ChooseCard {
  @Output() cardPick = new EventEmitter<Card>();

  constructor(
    public dialogRef: MatDialogRef<ChooseCard>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

  cardPicker(card) {
    this.cardPick.emit(card);
    this.dialogRef.close();
  }
}






@Component({
  selector: "error-screen",
  templateUrl: "error-screen.card-dialog.html"
})
export class ErrorScreen {
  constructor(
    public dialogRef: MatDialogRef<ErrorScreen>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData
  ) {}

  onNoClick(): void {
    this.dialogRef.close();
  }
}
