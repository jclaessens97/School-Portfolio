import { TestBed } from '@angular/core/testing';

import { HttpClientModule, HttpClient } from '@angular/common/http';

import { DiceService } from '../dice.service';

describe('DiceService', () => {
  beforeEach(() => TestBed.configureTestingModule({
    imports: [
      HttpClientModule,
    ]
  }));

  it('should be created', () => {
    const service: DiceService = TestBed.get(DiceService);
    expect(service).toBeTruthy();
  });
});
