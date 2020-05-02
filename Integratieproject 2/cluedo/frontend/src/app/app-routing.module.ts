import { NgModule } from "@angular/core";
import { Routes, RouterModule } from "@angular/router";

import { TurnComponent } from './components/turn/turn.component';
import { LobbyListComponent } from './components/lobby-list/lobby-list.component';
import { GameboardComponent } from './components/gameboard/gameboard.component';
import { LobbyComponent } from './components/lobby/lobby.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { AuthGuard } from './helpers/auth.guard';
import {GameScreenComponent} from './components/game-screen/game-screen.component';
import {ManageAccountComponent} from './components/manage-account/manage-account.component';
import {PortalComponent } from './components/portal/portal.component'
import { AuthGuardAdmin } from './helpers/admin.guard';
import { AdminpanelComponent } from './components/adminpanel/adminpanel.component';
import { UnauthorizedComponent } from './components/unauthorized/unauthorized.component';
import {StatisticsComponent} from './components/statistics/statistics.component';

const routes: Routes = [
  { path: "", redirectTo: "/lobbies", pathMatch: "full" },
  {
    path: "game-screen",
    component: GameScreenComponent,
    canActivate: [AuthGuard]
  },
  { path: "lobbies", component: LobbyListComponent, canActivate: [AuthGuard] },
  {
    path: "gameboard",
    component: GameboardComponent,
    canActivate: [AuthGuard]
  },
  { path: "lobby", component: LobbyComponent, canActivate: [AuthGuard] },
  { path: "turn", component: TurnComponent, canActivate: [AuthGuard] },
  {
    path: "manage-account",
    component: ManageAccountComponent,
    canActivate: [AuthGuard]
  },
  {
    path: "admin",
    component: AdminpanelComponent,
    canActivate: [AuthGuard, AuthGuardAdmin]
  },
  { path: "unauthorized", component: UnauthorizedComponent },
  { path: "login", component: PortalComponent },
  { path: 'statistics', component: StatisticsComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {
    onSameUrlNavigation: 'reload'
  })],
  exports: [RouterModule]
})
export class AppRoutingModule {}
