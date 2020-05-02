import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})

export class TurnService {
  private readonly URL_BASE = `${environment.apiUrl}/game`;
  private readonly GET_TURN_URL = `${this.URL_BASE}/turn`;

  constructor(private http: HttpClient) { }

  getTurn(gameId: number){return this.http.get(`${this.GET_TURN_URL}?gameId=${gameId}`)}
}
