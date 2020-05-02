import { Component, OnInit, Output, EventEmitter, Inject, OnDestroy } from "@angular/core";
import { LobbyService } from "../../services/lobby.service";
import { Lobby } from "../../models/Lobby";
import { Router, NavigationEnd } from "@angular/router";
import { CharacterType } from "../../models/enums";
import {
  MatDialog,
  MatDialogConfig,
  MAT_DIALOG_DATA,
  MatDialogRef
} from "@angular/material/dialog";
import { CluedoExceptionType } from "src/app/models/CluedoExceptionType";
import { MatSnackBar } from "@angular/material/snack-bar";

@Component({
  selector: "lobby-list",
  templateUrl: "./lobby-list.component.html",
  styleUrls: ["./lobby-list.component.css"]
})
export class LobbyListComponent implements OnInit, OnDestroy {
  lobbiesJoined: Array<Lobby>;
  lobbiesNotJoined: Array<Lobby>;
  games: Array<Lobby>;
  navigationSubscription;   

  displayedColumns: string[] = ["name", "host", "players", "action"];

  constructor(
    private lobbyService: LobbyService,
    private router: Router,
    public matDialog: MatDialog,
    private _snackBar: MatSnackBar
  ) {
    this.navigationSubscription = this.router.events.subscribe((e: any) => {
      // If it is a NavigationEnd event re-initalise the component
      if (e instanceof NavigationEnd) {
        this.ngOnInit();
      }
    });
  }

  ngOnInit() {
    this.refresh();
    const cluedoId = history.state.cluedoId;
    if (cluedoId) {
      this.join(cluedoId);
    }
  }

  ngOnDestroy() {
    if (this.navigationSubscription) {
      this.navigationSubscription.unsubscribe();
    }
  }

  create() {
    const dialogData = {
      isCreate: true
    };
    const modalDialog = this.createCharacterDialog(dialogData);

    modalDialog.componentInstance.characterSelector.subscribe(
      (character: string) => {
        this.lobbyService.createLobby(character).subscribe(
          resp => {
            modalDialog.close();
            this.open(resp);
          },
          error => {
            this.handleCreateError(error.error.message, modalDialog);
          }
        );
      }
    );
  }

  join(cluedoId) {
    this.lobbyService.getFreeCharacters(cluedoId).subscribe(
      resp => {
        const dialogData = {
          availableCharacters: resp,
          isCreate: false
        };
        const modalDialog = this.createCharacterDialog(dialogData);

        modalDialog.componentInstance.characterSelector.subscribe(
          (character: string) => {
            this.lobbyService.joinLobby(cluedoId, character).subscribe(
              () => {
                modalDialog.close();
                this.open(cluedoId);
              },
              error => {
                this.handleJoinError(
                  error.error.message,
                  cluedoId,
                  modalDialog
                );
              }
            );
          }
        );
      },
      () => {
        this.showDefaultError();
      }
    );
  }

  handleCreateError(error, modalDialog) {
    const exceptionType =
      CluedoExceptionType[error as keyof typeof CluedoExceptionType];
    switch (exceptionType) {
      default:
        this.showDefaultError();
        break;
    }
  }

  handleJoinError(error, cluedoId, modalDialog) {
    const exceptionType =
      CluedoExceptionType[error as keyof typeof CluedoExceptionType];
    switch (exceptionType) {
      case CluedoExceptionType.CHARACTERTYPE_TAKEN:
        modalDialog.componentInstance.showError(error);
        this.lobbyService.getFreeCharacters(cluedoId).subscribe(resp => {
          modalDialog.componentInstance.refreshCharacters(resp);
        });
        break;
      case CluedoExceptionType.LOBBY_FULL:
        this._snackBar.open("Unable to join jobby: lobby full", "Close", {
          duration: 5000
        });
        modalDialog.componentInstance.close();
        break;
      case CluedoExceptionType.PLAYER_ALREADY_IN_LOBBY:
        this._snackBar.open("You are already in this lobby", "Close", {
          duration: 5000
        });
        modalDialog.componentInstance.close();
        break;
      default:
        this.showDefaultError();
        break;
    }
  }

  showDefaultError() {
    this._snackBar.open("An unexpected error has occured.", "Close", {
      duration: 5000
    });
  }

  createCharacterDialog(data) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.height = "350px";
    dialogConfig.width = "600px";
    dialogConfig.data = data;

    return this.matDialog.open(CharacterselectModalComponent, dialogConfig);
  }

  refresh() {
    this.lobbyService.getLobbiesJoined().subscribe(lobbies => {
      this.lobbiesJoined = <Array<Lobby>>lobbies;
    });

    this.lobbyService.getLobbiesNotJoined().subscribe(lobbies => {
      this.lobbiesNotJoined = <Array<Lobby>>lobbies;
    });

    this.lobbyService.getOpenGames().subscribe(games => {
      this.games = <Array<Lobby>>games;
    });
  }

  open(cluedoId) {
    this.lobbyService.getLobbyDetails(cluedoId).subscribe(resp => {
      this.router.navigate(["/lobby"], { state: { lobbyDetails: resp } });
    });
  }

  openGame(cluedoId) {
    this.lobbyService.getLobbyDetails(cluedoId).subscribe(resp => {
      this.router.navigate(["/game-screen"], { state: { lobbyDetails: resp } });
    });
  }
}

interface characterSelectData {
  availableCharacters: CharacterType[];
  isCreate: boolean;
}

@Component({
  selector: "app-characterselect-modal",
  templateUrl: "./lobby-list.character-select.dialog.html",
  styleUrls: ["./lobby-list.component.css"]
})
export class CharacterselectModalComponent {
  @Output() characterSelector = new EventEmitter<string>();

  characters = CharacterType;
  selectedCharacter: string;
  error: string;

  constructor(
    public dialogRef: MatDialogRef<CharacterselectModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: characterSelectData
  ) {}

  isAvailable(character) {
    if (this.data.isCreate) return true;
    return this.data.availableCharacters.includes(character);
  }

  close() {
    this.dialogRef.close();
  }

  select() {
    this.characterSelector.emit(this.selectedCharacter);
  }

  showError(error) {
    this.error = error;
  }

  refreshCharacters(availableCharacters) {
    this.data.availableCharacters = availableCharacters;
    this.selectedCharacter = undefined;
  }
}
