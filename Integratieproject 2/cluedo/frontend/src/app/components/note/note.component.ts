import { Component, OnInit, Inject, Input, Output, EventEmitter } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { NoteService } from '../../services/note.service';
import { NotationSymbol, CardType } from '../../models/enums';
import { Notebook, NoteLine } from '../../models/Notebook';

export interface DialogData {
  cardType: number;
  x: number;
  y: number;
}

interface NotationSymbolVisual {
  icon: String,
  class: String
}

function GetNotationSymbolVisual(notationSymbol: NotationSymbol): NotationSymbolVisual {
  switch (notationSymbol) {
      case NotationSymbol.EMPTY:
          return {icon: 'check_box_outline_blank', class: ''};
      case NotationSymbol.CROSS:
          return {icon: 'clear', class: 'red'};
      case NotationSymbol.CHECKMARK:
          return {icon: 'check', class: 'green'};
      case NotationSymbol.QUESTIONMARK:
        return {icon: '?', class: 'black'};
      case NotationSymbol.EXCLAMATIONMARK:
        return {icon: '!', class: 'black'};
  }
}

@Component({
  selector: 'app-note',
  templateUrl: './note.component.html',
  styleUrls: ['./note.component.css']
})

export class NoteComponent implements OnInit {
  @Output() toggleNoteBookEmitter = new EventEmitter();
  @Input() cluedoId: number;
  notebook: Notebook;

  get getCardType() { return CardType; }

  constructor(public dialog: MatDialog, private api: NoteService) {
  }

  ngOnInit () {
    this.getNoteBook(this.cluedoId);
  }

  getNoteBook(cluedoId) {
    this.api.getNoteBook(cluedoId).subscribe(
      resp => {
        this.notebook = <Notebook>resp
        this.refreshIcons();
      },
      errors => {
        console.error(errors);
      }
    );
  }

  toggle (cardType: CardType, x, y): void {
    const dialogRef = this.dialog.open(Popup);

    const sub = dialogRef.componentInstance.iconChanger.subscribe((notationSymbol : NotationSymbol) => {
      this.changeIcon(cardType, x, y, notationSymbol);
      this.api.updateNotebookColumn(this.notebook.notebookId, CardType[cardType], x, y, notationSymbol).subscribe(
        () => { },
        errors => {
        }
        );
    });

    dialogRef.afterClosed().subscribe(() => { });
  }

  toggleCrossed(cardType: CardType, x): void {
    let crossed : boolean;
    switch (cardType) {
      case CardType.CHARACTER:
        crossed = !this.notebook.characters[x].crossed;
        this.notebook.characters[x].crossed = crossed;
        break;
      case CardType.WEAPON:
        crossed = !this.notebook.weapons[x].crossed;
        this.notebook.weapons[x].crossed = crossed;
        break;
      case CardType.ROOM:
        crossed = !this.notebook.rooms[x].crossed;
        this.notebook.rooms[x].crossed = crossed;
        break;
    }
    this.api.updateNoteBookLineCrossed(this.notebook.notebookId, CardType[cardType], x, crossed).subscribe();
  }

  changeIcon (cardType: CardType, x, y, notationSymbol : NotationSymbol) {
    switch (cardType) {
      case CardType.CHARACTER:
        this.notebook.characters[x].columns[y] = notationSymbol;
        break;
      case CardType.WEAPON:
        this.notebook.weapons[x].columns[y] = notationSymbol;
        break;
      case CardType.ROOM:
        this.notebook.rooms[x].columns[y] = notationSymbol;
        break;
    }
    this.refreshIcons();
  }

  refreshIcons () {
    this.notebook.characters.forEach(element => {
      this.setIcon(element);
    });
    this.notebook.weapons.forEach(element => {
      this.setIcon(element);
    });
    this.notebook.rooms.forEach(element => {
      this.setIcon(element);
    });
  }

  setIcon (element) {
    element.icons = [];
    element.fieldClass = [];
    element.columns.forEach(column=> {
      let visual = GetNotationSymbolVisual(NotationSymbol[<string>column]);
      element.icons.push(visual.icon);
      element.fieldClass.push(visual.class)
    });
  }


  toggleNotebook() {
    this.toggleNoteBookEmitter.emit();
  }

}

@Component({
  selector: 'popup',
  templateUrl: './note.component.dialog.html',
  styleUrls: ['./note.component.css']
})
export class Popup {
  @Output() iconChanger = new EventEmitter<String>();

  constructor(
    public dialogRef: MatDialogRef<Popup>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) { }

  getIcons() : Array<String> {
    return Object.keys(NotationSymbol).filter(key => isNaN(Number(key))).map((c) => {
      return GetNotationSymbolVisual(NotationSymbol[c]).icon
    });
  }

  onNoClick (): void {
    this.dialogRef.close();
  }
  changeIcon (index) {
    this.iconChanger.emit(NotationSymbol[index]);
    this.dialogRef.close();
  }

}
