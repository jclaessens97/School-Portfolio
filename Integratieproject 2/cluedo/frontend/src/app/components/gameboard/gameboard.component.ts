import {Component, Input, OnInit, Output, EventEmitter, OnChanges, SimpleChanges} from '@angular/core';
import {GameboardService} from '../../services/gameboard.service';
import {CardType, CharacterType} from '../../models/enums';
import {Turn} from '../../models/Turn';
import {PossibilitiesDto} from '../../models/PossibilitiesDto';
import {Card} from '../make-suggestion/make-suggestion.component';
import {WebsocketService} from '../../services/websocket.service';
import {SceneDto} from '../../models/SceneDto';
import {MakeSuggestionService} from '../../services/make-suggestion.service';

interface GameboardDimensions {
  width: number,
  height: number,
  squareCount: number
}
interface CardContainer {
  description: string;
  cardList: Card[];
  showButton: boolean;
  buttonTxt: string
}
@Component({
  selector: 'app-gameboard',
  templateUrl: './gameboard.component.html',
  styleUrls: ['./gameboard.component.css']
})

export class GameboardComponent implements OnInit {
  @Output() refreshGame = new EventEmitter<string>();

  gameboard: any;
  @Input() gameId: number;
  @Input() playerId: number;
  @Input() currentTurn: Turn;
  dimensions: GameboardDimensions;
  gwidth: number;
  suggestionCall = false;
  showSuggestionText = 'game.suggestion.enemy';


  readonly CARD_PATH = 'assets/img/cards';
  myCards: Card[]
  suggestionShownCard;
  scene : SceneDto;
  cardsToShowText: CardContainer[] = [{
    description: 'game.suggestion.own-cards', cardList: [], showButton: false, buttonTxt: 'game.suggestion.btn-own'
  },
    { description: 'game.suggestion.enemy', cardList: [], showButton: false, buttonTxt: 'game.suggestion.btn-suggestion'}
  ];
  tempChoices: any[];
  asAccusation: boolean = false;
  possibilities: PossibilitiesDto;
  visibleSuggestionScreen: boolean = false;
  visibleSuggestionCard = false;

  private _character: CharacterType;
  set character (value: CharacterType) {
    this._character = value;
    if (value != undefined) {
      this.getPossibilities();
    }
  }
  get character (): CharacterType {
    return this._character;
  }

  constructor(private api: GameboardService,
              private websocketService: WebsocketService,
              private gameService: MakeSuggestionService) {
    this.possibilities = {hasTurn:false, movesPossible: false, roomWithPassage: undefined, currentLocation: {x: null, y: null}, thrownDice:0};
    this.setSuggestionCards();
  }

  ngOnInit() {
    this.connect()
    this.getCharacterType();
    this.updateGame();

    this.gameService.getOwnCards(this.gameId, this.playerId).subscribe( (data: Card[]) => {
      this.myCards = data;
      this.cardsToShowText[0].cardList = data;
    });
  };


  //suggestion enabler
  startSuggestion (accusation) {
    this.asAccusation = accusation;
    this.visibleSuggestionScreen = true;
  }


  //moves
  moveRoom (x, y, roomType) {
    if (this.character != this.currentTurn.player.characterType) return;
    this.move(x, y);
    this.startSuggestion(false);
  }

  move (x, y) {
    if (this.character != this.currentTurn.player.characterType) return;
    let choice = this.tempChoices == undefined ? this.tempChoices : this.tempChoices.filter(t => this.comparePositions(t, x, y));
    if (choice == undefined || choice.length > 0) {
      this.api.move(this.character, this.gameId, x, y).subscribe(d => {
        this.tempChoices = [];
        this.updateGame()
      });
    }
  }

  takePassage () {
    this.api.takePassage(this.character, this.gameId).subscribe(d => {
      this.startSuggestion(false);
      this.updateGame();
    });
  }

  endTurn () {
    this.api.endTurn(this.character, this.gameId).subscribe(res => {
      this.updateGame();
    })
  }


  //necessary functions to call
  updateGame () {
    this.getGameboard();
    this.getPossibilities();
  }

  getGameboard () {
    this.api.getGame(this.gameId).subscribe(data => {
     this.updateGameBoard(data);
    });
  }

  updateGameBoard (data) {
    this.gameboard = data;
    let width = Math.max.apply(Math, this.gameboard.tiles.map(t => t.xcoord).concat(this.gameboard.spawnTiles.map(t => t.xcoord))) + 2;
    let height = Math.max.apply(Math, this.gameboard.tiles.map(t => t.ycoord).concat(this.gameboard.spawnTiles.map(t => t.ycoord))) + 2;
    this.gwidth = (100 / (width));
    this.dimensions = { height: height, width: width, squareCount: height * width };
  }

  getPossibilities () {
    if (this.character != undefined) {
      this.api.getChoices(this.character, this.gameId).subscribe(data => {
        this.possibilities = (<PossibilitiesDto>data);
        if (this.possibilities.thrownDice > 0 && this.possibilities.currentLocation.x == null) this.getPositions(this.possibilities.thrownDice);
      })
    }
  }

  // updatePossibilities (data) {
  //   this.possibilities = (<PossibilitiesDto>data);
  //   if (this.possibilities.thrownDice > 0 && this.possibilities.currentLocation.x == null) this.getPositions(this.possibilities.thrownDice);
  // }


  //helper functions
  convertToUnit (numb) {
    return `${numb}%`;
  }

  comparePositions (p, x, y) {
    return p.xcoord == x && p.ycoord == y
  }

  getRoom (xcoord: any, ycoord: any, roomType: any) {
    return `${roomType} ${this.getChoice(xcoord, ycoord)}`
  }

  getCharacterType () {
    this.api.getCharacterType(this.playerId).subscribe(data => this.character = (<CharacterType>data));
  }

  getCharacters (x, y) {
    return this.gameboard.characters.filter(c => this.comparePositions(c.position, x, y)).map(c => c.characterType);
  }

  getSpawnTile (spawntile: any) {
    let capitalize = (s) => s.toUpperCase().charAt(0) + s.toLowerCase().slice(1);
    return `tile${capitalize(spawntile.characterType.toString())}  ${this.getChoice(spawntile.xcoord, spawntile.ycoord)}`
  }

  getChoice (x, y) {
    let choice = this.tempChoices == undefined ? this.tempChoices : this.tempChoices.find(t => this.comparePositions(t, x, y));
    if (choice != undefined) return 'choice ';
    return ''
  }

  getPositions (roll) {
    this.api.getMovablePositions(this.character, this.gameId, roll).subscribe(data => {
      this.tempChoices = (<any[]>data);
      this.possibilities.movesPossible = false;
    })
  }

  isInRoom () {
    let location = this.possibilities.currentLocation;
    if (location == undefined) return false;
    return this.gameboard.rooms.filter(r => r.xcoord == location.x && r.ycoord == location.y).length > 0;
  }

  getRoomForSuggestion() {
    let location = this.possibilities.currentLocation;
    if(location == undefined) return false;
    return this.gameboard.rooms.find(r => r.xcoord == location.x && r.ycoord == location.y);
  }


  private connect() {
    this.websocketService.connect(client => {
      if (!this.gameId) return;
      client.subscribe(`/refresh/${this.gameId}`, msg => {
        this.updateGame();
        this.refreshGame.emit();
      })
    }).then(() => {
      
    });


    this.websocketService.connect(client => {
      client.subscribe(`/suggestionCards/${this.gameId}`, msg => {
        this.scene = JSON.parse(msg.body);
        this.cardsToShowText[1].cardList = this.scene.cards;
          if (this.scene.accusation) {
            this.showSuggestionText = 'game.accusation.enemy';
          } else {
            this.showSuggestionText = 'game.suggestion.enemy';
          }
      });
    });
    this.websocketService.connect(client => {
      client.subscribe(`/suggestionReply/${this.gameId}/playerId/${this.playerId}`, msg => {
        this.suggestionShownCard = JSON.parse(msg.body);
        this.visibleSuggestionCard = true;
        this.suggestionCall = true;
      });
    });
    this.websocketService.connect(client => {
      client.subscribe(`/newTurn/${this.gameId}`, msg => {
        this.resetAll();
      });
    });
  }

  makeCallFalse() {
    this.visibleSuggestionCard = false;
  }

  private resetAll() {
    this.suggestionCall = false;
    this.visibleSuggestionCard = false;
    this.setSuggestionCards();
  }

  private setSuggestionCards() {
    this.cardsToShowText[1].cardList =  [
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
    this.suggestionShownCard = this.cardsToShowText[1].cardList[0];
  }


  showCards(i: number) {
    this.cardsToShowText[i].showButton = !this.cardsToShowText[i].showButton;
  }
}

