import { Component, OnInit, EventEmitter, Output, Inject } from "@angular/core";
import { ActivatedRoute, Router } from "@angular/router";
import { LobbyService } from "../../services/lobby.service";
import { LobbyDetails } from "../../models/LobbyDetails";
import { Player } from "../../models/Player";
import { MatSnackBar } from "@angular/material/snack-bar";
import { SnackBar } from "../snackbar/snackbar.component";
import { CluedoExceptionType } from "src/app/models/CluedoExceptionType";
import { WebsocketService } from "../../services/websocket.service";
import {
  MatDialog,
  MatDialogConfig,
  MAT_DIALOG_DATA,
  MatDialogRef
} from "@angular/material/dialog";
import { ReportDialog } from "../report/report.dialog";
import { User } from "src/app/models/User";
import { UserService } from "src/app/services/user.service";
import {filter, map} from 'rxjs/operators';
import {Observable} from 'rxjs';

@Component({
  selector: "app-lobby",
  templateUrl: "./lobby.component.html",
  styleUrls: ["./lobby.component.css"]
})
export class LobbyComponent implements OnInit {
  lobbyDetails: LobbyDetails;
  playerId: number;
  error: string;
  wsclient: any;
  private state$: Observable<object>;



  get isHost(): boolean {
    return (
      this.lobbyDetails &&
      this.lobbyDetails.hostPlayerId === this.lobbyDetails.playerId
    );
  }

  constructor(
    private route: ActivatedRoute,
    private lobbyService: LobbyService,
    private router: Router,
    private userService: UserService,
    private _snackBar: MatSnackBar,
    private websocketService: WebsocketService,
    public matDialog: MatDialog
  ) {}

  ngOnInit() {
    this.lobbyDetails = <LobbyDetails>history.state.lobbyDetails;
    if (!this.lobbyDetails) {
      this.router.navigate(["/lobbies"]);
      return;
    }
    this.playerId = this.lobbyDetails.playerId;
    this.connect();
  }

  ngOnDestroy() {
    this.websocketService.disconnect();
  }

  leave() {
    this.lobbyService
      .leaveLobby(this.lobbyDetails.cluedoId, this.lobbyDetails.playerId)
      .subscribe(
        () => {},
        () => {
          this.showDefaultError();
        }
      );
  }

  back() {
    // this.websocketService.disconnect();
    this.router.navigate(["/lobbies"]);
  }

  kick(player) {
    this.lobbyService
      .kickFromLobby(this.lobbyDetails.cluedoId, player.playerId)
      .subscribe(() => {});
  }

  start() {
    this.lobbyService.startGame(this.lobbyDetails.cluedoId).subscribe(
      () => this.connect(),
      error => this.handleStartError(error.error.message)
    );
  }

  report(player: Player) {
    const modalDialog = this.createReportdialog(player);

    modalDialog.componentInstance.resultEmitter.subscribe(r => {
      if (!r) {
        this._snackBar.open("Thank your for the report.", "Close", {
          duration: 5000
        });
        modalDialog.componentInstance.close();
      } else this.handleReportError(r);
    });
  }

  createReportdialog(player) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.height = "350px";
    dialogConfig.width = "600px";
    dialogConfig.data = { player, cluedoId: this.lobbyDetails.cluedoId };

    return this.matDialog.open(ReportDialog, dialogConfig);
  }

  updateSettings() {
    this.lobbyService.updateGameSettings(this.lobbyDetails).subscribe(
      r => {},
      error => this.showDefaultError()
    );
  }

  // Websockets
  connect() {
    this.websocketService.connect(client => {
      client.subscribe(`/lobby/${this.lobbyDetails.cluedoId}`, msg => {
        this.lobbyDetails = JSON.parse(msg.body);
        this.lobbyDetails.playerId = this.playerId;
        this.checkStillInLobby();
        this.checkGameStart();
      });
    });
  }

  checkStillInLobby() {
    if (
      this.lobbyDetails.players.filter(
        p => p.playerId === this.lobbyDetails.playerId
      ).length === 0
    ) {
      // this.websocketService.disconnect();
      this.router.navigate(["/lobbies"]);
    }
  }

  checkGameStart() {
    if (this.lobbyDetails.active) {
      this.router.navigate(["/game-screen"], {
        state: { lobbyDetails: this.lobbyDetails }
      });
    }
  }

  // Errors
  handleStartError(error) {
    const exceptionType =
      CluedoExceptionType[error as keyof typeof CluedoExceptionType];
    switch (exceptionType) {
      case CluedoExceptionType.LOBBY_START_PLAYER_COUNT:
        this.error = error;
        break;
      case CluedoExceptionType.LOBBY_FULL:
        this.error = error;
        break;
      default:
        this.showDefaultError();
        break;
    }
  }

  handleReportError(error) {
    const exceptionType =
      CluedoExceptionType[error as keyof typeof CluedoExceptionType];
    switch (exceptionType) {
      default:
        this.showDefaultError();
        break;
    }
  }

  showDefaultError() {
    this._snackBar.openFromComponent(SnackBar, {
      duration: 5000,
      data: CluedoExceptionType[CluedoExceptionType.UNEXPECTED]
    });
  }

  invite() {
    this.userService.getAvailableFriends(this.lobbyDetails.cluedoId).subscribe(
      resp => {
        const dialogData = {
          availableFriends: resp,
          isCreate: false
        };
        const modalDialog = this.createFriendDialog(dialogData);

        modalDialog.componentInstance.friendSelector.subscribe(
          (friend: string) => {
            this.userService
              .inviteFriend(this.lobbyDetails.cluedoId, friend)
              .subscribe(
                () => {
                  modalDialog.close();
                },
                error => {
                  this.showDefaultError;
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

  createFriendDialog(data) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.height = "350px";
    dialogConfig.width = "600px";
    dialogConfig.data = data;
    return this.matDialog.open(FriendselectModalComponent, dialogConfig);
  }

  watchStatistics(playerId: number, cluedoId: number) {
    //this.userService.setStatisticsInfo(playerId, cluedoId);
  }
}

interface friendSelectData {
  availableFriends: String[];
  isCreate: Boolean;
}

@Component({
  selector: "app-friendselect-modal",
  templateUrl: "./lobby.friend-select.dialog.html",
  styleUrls: ["./lobby.component.css"]
})
export class FriendselectModalComponent {
  @Output() friendSelector = new EventEmitter<string>();

  // friends: Array<User>;
  selectedFriend: string;
  error: string;

  constructor(
    public dialogRef: MatDialogRef<FriendselectModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: friendSelectData
  ) {
  }

  isAvailable(user) {
    if (this.data.isCreate) return true;
    return this.data.availableFriends.includes(user);
  }

  close() {
    this.dialogRef.close();
  }

  select() {
    this.friendSelector.emit(this.selectedFriend);
  }

  showError(error) {
    this.error = error;
  }

  refreshCharacters(availableFriends) {
    this.data.availableFriends = availableFriends;
    this.selectedFriend = undefined;
  }
}
