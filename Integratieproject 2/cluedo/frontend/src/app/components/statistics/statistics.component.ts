import { Component, OnInit } from '@angular/core';
import {UserService} from '../../services/user.service';
import {Statistics} from '../../models/Statistics';
import {LobbyDetails} from '../../models/LobbyDetails';
import {Observable} from 'rxjs';
import {filter, map} from 'rxjs/operators';
import {NavigationStart, Router} from '@angular/router';


@Component({
  selector: 'app-statistics',
  templateUrl: './statistics.component.html',
  styleUrls: ['./statistics.component.css']
})
export class StatisticsComponent implements OnInit {
  statistics: Statistics = {
    wins: 0,
    losses: 0,
    amountOfTurns: 0,
    rightAccusations: 0,
    wrongAccusations: 0,
    winsRatio: 0,
    accusationRatio: 0
  };
  username: string;
  private state$: Observable<object>;


  public chartTypeWins = 'pie';

  chartDatasetsWins = [ {  data: [this.statistics.wins, this.statistics.losses], label: 'Wins and losses' } ];
  chartDatasetsAccusations = [ { data: [this.statistics.rightAccusations, this.statistics.wrongAccusations], label: 'Accusations' } ];

  public chartLabelsWins: Array<any> = ['Wins', 'Losses'];
  public chartLabelsAccusations: Array<any> = ['Right', 'Wrong'];

  get statisticsFormatted() {
    return [
      {name: 'Amount of games played', amount: this.statistics.wins + this.statistics.losses},
      {name: 'Amount of turns played', amount: this.statistics.amountOfTurns},
      {name: 'Wins', amount: this.statistics.wins},
      {name: 'Losses', amount: this.statistics.losses},
      {name: 'win/loss ratio', amount: this.statistics.winsRatio},
      {name: 'Right Accusations', amount: this.statistics.rightAccusations},
      {name: 'Wrong Accusations', amount: this.statistics.wrongAccusations},
      {name: 'Accusations ratio', amount: this.statistics.accusationRatio}
    ];
  }

  public chartColors: Array<any> = [
    {
      backgroundColor: ['#46BFBD', '#F7464A'],
      hoverBackgroundColor: ['#5AD3D1', '#FF5A5E'],
      borderWidth: 2,
    }
  ];

  public chartOptions: any = {
    responsive: true
  };
  public chartClicked(e: any): void { }
  public chartHovered(e: any): void { }

  constructor(private userService: UserService,
              private router: Router) { }

  ngOnInit() {

    this.userService.getParamStatisticsUserName(<string>history.state.username).subscribe((data: Statistics) => {
      this.statistics = data;
      this.chartDatasetsWins = [ {  data: [this.statistics.wins, this.statistics.losses], label: 'Wins and losses' } ];
      this.chartDatasetsAccusations = [ { data: [this.statistics.rightAccusations, this.statistics.wrongAccusations], label: 'Accusations' } ];
    });

  }
}
