import { BrowserModule } from "@angular/platform-browser";
import { NgModule } from "@angular/core";
import { ReactiveFormsModule } from "@angular/forms";
import { TranslateLoader, TranslateModule } from "@ngx-translate/core";
import { TranslateHttpLoader } from "@ngx-translate/http-loader";
import {
  HttpClient,
  HttpClientModule,
  HTTP_INTERCEPTORS
} from "@angular/common/http";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { ChartsModule} from 'ng2-charts';

import { MaterialModule } from "./material.module";
import { AppRoutingModule } from "./app-routing.module";
import { AppComponent } from "./components/app.component";
import { NoteComponent, Popup } from "./components/note/note.component";
import { FormsModule } from "@angular/forms";

import { DiceComponent } from "./components/dice/dice.component";
import { GameboardComponent } from "./components/gameboard/gameboard.component";
import {
  LobbyListComponent,
  CharacterselectModalComponent
} from "./components/lobby-list/lobby-list.component";
import {
  LobbyComponent
} from "./components/lobby/lobby.component";
import { LoginComponent } from "./components/login/login.component";
import { AlertComponent } from "./components/alert/alert.component";
import { RegisterComponent } from "./components/register/register.component";
import { TurnComponent } from "./components/turn/turn.component";
import {
  ChooseCard,
  MakeSuggestionComponent,
  ErrorScreen
} from "./components/make-suggestion/make-suggestion.component";
import { JwtInterceptor } from "./helpers/jwt.interceptor";
import { ErrorInterceptor } from "./helpers/error.interceptor";
import { EnumToArrayPipe } from "./helpers/enumToArray";
import { GameScreenComponent } from "./components/game-screen/game-screen.component";
import { ChatComponent } from "./components/chat/chat.component";
import { ManageAccountComponent } from "./components/manage-account/manage-account.component";
import { FriendListComponent } from "./components/friend/friend-list.component";
import { ReportDialog } from "./components/report/report.dialog";
import { SnackBar } from "./components/snackbar/snackbar.component";
import { UnauthorizedComponent } from "./components/unauthorized/unauthorized.component";
import { AdminpanelComponent } from "./components/adminpanel/adminpanel.component";
import {
  ReportPanelComponent,
  ReportDetailDialogComponent
} from "./components/report-panel/report-panel.component";
import { PortalComponent } from "./components/portal/portal.component";
import { ChooseCardToShow} from './components/game-screen/game-screen.component';

import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";
import { ServiceWorkerModule } from '@angular/service-worker';
import { environment } from '../environments/environment';
import { StatisticsComponent } from './components/statistics/statistics.component';
import {FriendselectModalComponent} from './components/lobby/lobby.component';
import { NotificationComponent } from './components/notification/notification.component';

@NgModule({
  declarations: [
    AdminpanelComponent,
    AlertComponent,
    AppComponent,
    CharacterselectModalComponent,
    FriendselectModalComponent,
    ChatComponent,
    ChooseCard,
    ChooseCardToShow,
    DiceComponent,
    EnumToArrayPipe,
    ErrorScreen,
    FriendListComponent,
    GameboardComponent,
    GameScreenComponent,
    LobbyComponent,
    LobbyListComponent,
    LoginComponent,
    MakeSuggestionComponent,
    ManageAccountComponent,
    NoteComponent,
    Popup,
    RegisterComponent,
    ReportDetailDialogComponent,
    ReportDialog,
    ReportPanelComponent,
    SnackBar,
    TurnComponent,
    UnauthorizedComponent,
    PortalComponent,
    StatisticsComponent,
    NotificationComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    MaterialModule,
    HttpClientModule,
    FormsModule,
    ChartsModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      }
    }),
    ServiceWorkerModule.register('ngsw-worker.js', { enabled: environment.production })
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    { provide: MAT_DIALOG_DATA, useValue: {} },
    { provide: MatDialogRef, useValue: {} }
  ],
  bootstrap: [AppComponent],
  entryComponents: [
    Popup,
    CharacterselectModalComponent,
    FriendselectModalComponent,
    ChooseCard,
    ChooseCardToShow,
    ErrorScreen,
    ReportDialog,
    SnackBar,
    ReportDetailDialogComponent
  ]
})
export class AppModule {}

// required for AOT compilation
export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http);
}
