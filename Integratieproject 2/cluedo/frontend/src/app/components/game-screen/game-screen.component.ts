import { delay } from 'rxjs/operators';
import { ActivatedRoute, Router } from '@angular/router';
import {Component, EventEmitter, Inject, OnInit, Output} from '@angular/core';
import {LobbyDetails} from '../../models/LobbyDetails';
import {NoteService} from '../../services/note.service';
import {Turn} from '../../models/Turn';
import {TurnService} from '../../services/turn.service';
import {interval} from 'rxjs';
import {
  MatDialog,
  MatDialogConfig,
  MAT_DIALOG_DATA,
  MatDialogRef
} from "@angular/material/dialog";
import {DialogData} from '../note/note.component';
import {Card} from '../make-suggestion/make-suggestion.component';
import {CardType, CharacterType} from '../../models/enums';
import {WebsocketService} from '../../services/websocket.service';
import {Player} from '../../models/Player';
import {MakeSuggestionService} from '../../services/make-suggestion.service';
import {SceneDto} from '../../models/SceneDto';
import {AccusationDto} from '../../models/AccusationDto';
import { ReportDialog } from "../report/report.dialog";

@Component({
  selector: 'app-game-screen',
  templateUrl: './game-screen.component.html',
  styleUrls: ['./game-screen.component.css']
})
export class GameScreenComponent implements OnInit {

  lobbyDetails: LobbyDetails;
  currentTurn: Turn;
  timer = interval(10000);
  readonly CARD_PATH = 'assets/img/cards';
  selectCardToShow = false;
  suggestionCall = false;
  suggestionReplyPlayer: Player = {
    name: 'undefined',
    playerId: 0,
    characterType: CharacterType.BLUE
  };
  noOneHasSuggestionCard= false;
  scene: SceneDto;
  accusationInfo: AccusationDto;
  gameHasEnded = false;
  accusationText = '';
  isAccusation= false;
  accusationPlayer: Player = {
    name: "undefined",
    characterType: CharacterType.BLUE,
    playerId: 0
  };

  timeLeft;
  interval;

  isNotepadOpen = false;


  suggestionCards: Card[] = [
    {
      cardId: 0,
      cardType: CardType.CHARACTER,
      type: "",
      url: `${this.CARD_PATH}/character-card.png`
    },
    {
      cardId: 1,
      cardType: CardType.WEAPON,
      type: "",
      url: `${this.CARD_PATH}/weapon-card.png`
    }
    ];


  constructor(
    private router: Router,
    private noteService: NoteService,
    private turnService: TurnService,
    private dialog: MatDialog,
    private websocketService: WebsocketService,
    private suggestionService: MakeSuggestionService
  ) { }

  ngOnInit () {
    this.lobbyDetails = <LobbyDetails>history.state.lobbyDetails;
    if (!this.lobbyDetails){
      this.router.navigate(['/lobbies']);
      return;
    }

    this.getTurn()
    this.connect()
  }

  get timeRemaningFormatted(){
    const minutes = Math.floor(this.timeLeft / 60);
    const seconds = this.timeLeft - minutes * 60;
    return `${minutes}:${seconds}`;
  }

  getTurn () {
    this.noOneHasSuggestionCard = false;
    // this.resetAll();
    this.turnService.getTurn(this.lobbyDetails.cluedoId).subscribe(data => this.updateTurn(<Turn>data));
  }

  updateTurn(turnData: Turn) {
    this.currentTurn = turnData;
    if(this.timeLeft) this.clearTimer();
    this.startTimer(this.currentTurn.timeRemaining);
  }

  startTimer(time) {
    this.timeLeft = time*60;
    this.interval = setInterval(() => {
      if(this.timeLeft > 0) {
        this.timeLeft--;
      }
    },1000)
  }

  clearTimer() {
    clearInterval(this.interval);
  }


  openDialog() {
    const dialogRef = this.dialog.open(ChooseCardToShow, {
      width: "available",
      panelClass: "my-dialog",
      data: this.suggestionCards
    });

    dialogRef.componentInstance.cardPick.subscribe((card: Card) => {
      this.selectCardToShow = false;
      // stuur de gekozen kaart terug naar backend zodat hij een websocket kan sturen naar de speler die suggestie heeft gemaakt
      this.suggestionService.replySuggestion(card, this.lobbyDetails.cluedoId).subscribe();
    });
  }

  private connect() {
    this.websocketService.connect(client => {
      client.subscribe(`/suggestionCards/${this.lobbyDetails.cluedoId}/playerId/${this.lobbyDetails.playerId}`, msg => {
        this.scene = JSON.parse(msg.body);
        this.suggestionCards = this.scene.cards;
        this.checkScene();
      });
    });
    this.websocketService.connect(client => {
      client.subscribe(`/suggestionReply/${this.lobbyDetails.cluedoId}`, msg => {
        this.suggestionReplyPlayer = JSON.parse(msg.body);
        if (this.suggestionReplyPlayer.name  === '') {
          this.accusationText = 'game.suggestion.no-card';
          this.noOneHasSuggestionCard = true;
        } else {
          this.suggestionCall = true;
          this.accusationText = 'game.suggestion.reply';
        }
      });
    });
    this.websocketService.connect(client => {
      client.subscribe(`/accusation/${this.lobbyDetails.cluedoId}`, msg => {
        this.accusationInfo = JSON.parse(msg.body);
        this.checkAccusationInfo();
      });
    });
    this.websocketService.connect(client => {
      client.subscribe(`/newTurn/${this.lobbyDetails.cluedoId}`, msg => {
        // this.resetAll();
      });
    });
  }

  private checkAccusationInfo() {
    this.isAccusation = true;
    this.accusationPlayer = this.accusationInfo.winningPlayer;
    if (this.accusationInfo.accusationOutcome) {
      this.gameHasEnded = true;
      this.accusationText = 'game.accusation.right';
    } else {
      if (this.accusationInfo.gameHasEnded) {
        this.gameHasEnded = true;
        this.accusationText = 'game.accusation.wrongEnd';
      } else {
        this.accusationText = 'game.accusation.wrong';
        this.suggestionCall = true;
      }
    }
  }

  private checkScene() {
    if (!this.scene.accusation) {
      if (this.scene.cards.length === 0) {
        this.noOneHasSuggestionCard = true;
      } else {
        this.noOneHasSuggestionCard = false;
        this.openDialog();
        this.selectCardToShow = true;
      }
    }
  }

  private resetAll() {
    this.suggestionCall = false;
    this.noOneHasSuggestionCard = false;
    this.accusationText = '';
    this.selectCardToShow = false;
  }


  toggleNotepad() {
    this.isNotepadOpen = !this.isNotepadOpen;
  }

  report(player: Player) {
    const modalDialog = this.createReportdialog(player);

    modalDialog.componentInstance.resultEmitter.subscribe(r => {
      modalDialog.componentInstance.close();
    });
  }

  createReportdialog(player) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.height = "350px";
    dialogConfig.width = "600px";
    dialogConfig.data = { player, cluedoId: this.lobbyDetails.cluedoId };

    return this.dialog.open(ReportDialog, dialogConfig);
  }
}

@Component({
  selector: "choose-card-to-show",
  templateUrl: "../make-suggestion/make-suggestion.card-dialog.html"
})
export class ChooseCardToShow {
  @Output() cardPick = new EventEmitter<Card>();

  constructor(
    public dialogRef: MatDialogRef<ChooseCardToShow>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData
  ) {
    dialogRef.disableClose = true;
  }

  cardPicker(card) {
    this.cardPick.emit(card);
    this.dialogRef.close();
  }

  onNoClick(): void {
    this.dialogRef.close();
  }
}
