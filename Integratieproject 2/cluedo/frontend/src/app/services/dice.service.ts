import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import DiceDto from '../models/DiceDto';

@Injectable({
  providedIn: 'root'
})
export class DiceService {
  private readonly DICE_URL = `${environment.apiUrl}/dice`

  constructor(private httpClient: HttpClient) { }

  getDiceValues(singleDice: boolean) {
    if (singleDice) {
      return this.httpClient.get<DiceDto>(`${this.DICE_URL}/one`);
    }

    return this.httpClient.get<DiceDto>(`${this.DICE_URL}/two`);
  }

}
