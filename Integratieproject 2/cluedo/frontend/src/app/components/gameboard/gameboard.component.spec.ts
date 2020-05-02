import {TestBed} from '@angular/core/testing';
import {GameboardComponent} from './gameboard.component';
import {TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {HttpLoaderFactory} from 'src/app/app.module';
import {HttpClient, HttpClientModule} from '@angular/common/http';
import {MakeSuggestionComponent} from '../make-suggestion/make-suggestion.component';
import {TurnComponent} from '../turn/turn.component';
import {DiceComponent} from '../dice/dice.component';
import {MaterialModule} from 'src/app/material.module';
import {CharacterType} from '../../models/enums';

describe('GameboardComponent', () => {
  function getCompiled(){
    const fixture = TestBed.createComponent(GameboardComponent);
    const component = fixture.debugElement.componentInstance;
    component.gameboard = {rooms: [], spawnTiles: [], tiles:[]};
    component.currentTurn = {player: {name:"lol", characterType: CharacterType.BLUE, playerId:1}, timeRemaining: 10};
    component.dimensions = {width: 10, height: 10, squareCount: 100};
    fixture.detectChanges();
    return fixture.debugElement.nativeElement;
  }

  beforeEach((() => {
    TestBed.configureTestingModule({
      declarations: [
        GameboardComponent,
        MakeSuggestionComponent,
        TurnComponent,
        DiceComponent,
      ],
      imports: [
        HttpClientModule,
        MaterialModule,
        TranslateModule.forRoot({
          loader: {
              provide: TranslateLoader,
              useFactory: HttpLoaderFactory,
              deps: [HttpClient]
          }
        })
      ]
    }).compileComponents();
  }));

  it('should create the component', () => {
    const fixture = TestBed.createComponent(GameboardComponent);
    const component = fixture.debugElement.componentInstance;
    expect(component).toBeTruthy();
  });

  it('should have a suggestion button', ()=>{
    const compiled = getCompiled();
    const inners = [];
    compiled.querySelectorAll('span>button').forEach(e => inners.concat(e.innerHTML.toLowerCase()));
    expect(inners.includes('suggestion'))
  });

  it('should have an accusation button', ()=>{
    const compiled = getCompiled();
    const inners = [];
    compiled.querySelectorAll('span>button').forEach(e => inners.concat(e.innerHTML.toLowerCase()));
    expect(inners.includes('accusation'))
  });

  it('should have an end turn button', ()=>{
    const compiled = getCompiled();
    const inners = [];
    compiled.querySelectorAll('span>button').forEach(e => inners.concat(e.innerHTML.toLowerCase()));
    expect(inners.includes('end turn'))
  });

  it('should have a gameboard', ()=>{
    const compiled = getCompiled();
    expect(compiled.querySelector('.gameboard')).not.toBeNull()
  });

  it('should have a suggestion box', ()=>{
    const compiled = getCompiled();
    expect(compiled.querySelector('.choiceBox')).not.toBeNull()
  });

  it('should have a turn box', ()=>{
    const compiled = getCompiled();
    expect(compiled.querySelector('turn')).not.toBeNull()
  });

  it('should show all buttons when it is their characters turn', ()=>{
    const fixture = TestBed.createComponent(GameboardComponent);
    const component = fixture.debugElement.componentInstance;
    component.gameboard = {rooms: [], spawnTiles: [], tiles:[]};
    component.currentTurn = {player: {name:"lol", characterType: CharacterType.BLUE, playerId:1}, timeRemaining: 10};
    component.character = CharacterType.BLUE;
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('#buttons')).not.toBeNull();
  });

  it('should show turn when visibleSuggestionScreen is false', ()=>{
    const fixture = TestBed.createComponent(GameboardComponent);
    const component = fixture.debugElement.componentInstance;
    component.gameboard = {rooms: [], spawnTiles: [], tiles:[]};
    component.currentTurn = {player: {name:"lol", characterType: CharacterType.BLUE, playerId:1}, timeRemaining: 10};
    component.character = CharacterType.BLUE;
    component.visibleSuggestionScreen = false;
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('#turn')).not.toBeNull();
  });

  it('should show app-make-suggestion when visibleSuggestionScreen is true', ()=>{
    const fixture = TestBed.createComponent(GameboardComponent);
    const component = fixture.debugElement.componentInstance;
    component.gameboard = {rooms: [], spawnTiles: [], tiles:[]};
    component.dimensions = {width: 10, height: 10, squareCount: 100};
    component.currentTurn = {player: {name:"lol", characterType: CharacterType.BLUE, playerId:1}, timeRemaining: 10};
    component.character = CharacterType.BLUE;
    component.visibleSuggestionScreen = true;
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('#scenarioScreen')).not.toBeNull();
  });

  it('should create a span with an bg-img when the tile is used by a character', ()=>{
    const fixture = TestBed.createComponent(GameboardComponent);
    const component = fixture.debugElement.componentInstance;
    component.gameboard = {rooms: [], spawnTiles: [], tiles:[{xcoord: 1, ycoord:1}], characters: [{characterType: 'BLUE', gameId:1, position: {xcoord: 1, ycoord:1}}]};
    component.dimensions = {width: 10, height: 10, squareCount: 100};
    component.gwidth = 5;
    component.currentTurn = {player: {name:"lol", characterType: CharacterType.BLUE, playerId:1}, timeRemaining: 10};
    component.character = CharacterType.BLUE;
    component.visibleSuggestionScreen = true;
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('.BLUE')).not.toBeNull();
  });

  it('should create a span for every room in gameboard', ()=>{
    const fixture = TestBed.createComponent(GameboardComponent);
    const component = fixture.debugElement.componentInstance;
    component.gameboard = {rooms: [{xcoord: 1, ycoord:1, roomType:'room1', width: 2, height: 2},{xcoord: 5, ycoord:5, roomType:'room2', width: 2, height: 2}], spawnTiles: [], tiles:[], characters: []};
    component.dimensions = {width: 10, height: 10, squareCount: 100};
    component.currentTurn = {player: {name:"lol", characterType: CharacterType.BLUE, playerId:1}, timeRemaining: 10};
    component.gwidth = 5;
    component.character = CharacterType.BLUE;
    component.visibleSuggestionScreen = true;
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelectorAll('.room').length).toBe(2);
  });

  it('should create a span for every spawntile in gameboard', ()=>{
    const fixture = TestBed.createComponent(GameboardComponent);
    const component = fixture.debugElement.componentInstance;
    component.gameboard = {rooms: [], spawnTiles: [{xcoord: 1, ycoord:1, characterType: CharacterType.BLUE},{xcoord: 1, ycoord:2, characterType: CharacterType.YELLOW},{xcoord: 2, ycoord:1, characterType: CharacterType.GREEN},{xcoord: 3, ycoord:1, characterType: CharacterType.RED}], tiles:[], characters: []};
    component.dimensions = {width: 10, height: 10, squareCount: 100};
    component.currentTurn = {player: {name:"lol", characterType: CharacterType.BLUE, playerId:1}, timeRemaining: 10};
    component.character = CharacterType.BLUE;
    component.visibleSuggestionScreen = true;
    component.gwidth = 5;
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelectorAll('.spawntile').length).toBe(4);
  });

  it('should create a span for every tile in gameboard', ()=>{
    const fixture = TestBed.createComponent(GameboardComponent);
    const component = fixture.debugElement.componentInstance;
    component.gameboard = {rooms: [], spawnTiles: [], tiles:[{xcoord: 1, ycoord:1},{xcoord: 2, ycoord:2},{xcoord: 3, ycoord:3},{xcoord: 4, ycoord:4}], characters: []};
    component.dimensions = {width: 10, height: 10, squareCount: 100};
    component.currentTurn = {player: {name:"lol", characterType: CharacterType.BLUE, playerId:1}, timeRemaining: 10};
    component.character = CharacterType.BLUE;
    component.visibleSuggestionScreen = true;
    component.tempChoices = undefined;
    component.gwidth = 5;
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelectorAll('.gameTile').length).toBe(4);
  });

});
