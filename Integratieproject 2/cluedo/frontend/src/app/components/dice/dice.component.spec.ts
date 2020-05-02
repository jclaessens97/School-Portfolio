import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { DiceComponent } from './dice.component';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { DiceService } from '../../services/dice.service';
import { of } from 'rxjs';
import DiceDto from '../../models/DiceDto';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { HttpLoaderFactory } from 'src/app/app.module';
import { MaterialModule } from 'src/app/material.module';

describe('DiceComponent', () => {
  beforeEach((() => {
    TestBed.configureTestingModule({
      declarations: [DiceComponent],
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
    const fixture = TestBed.createComponent(DiceComponent);
    fixture.detectChanges();
    const component = fixture.debugElement.componentInstance;
    expect(component).toBeTruthy();
  });

  it('should have a roll button', () => {
    const fixture = TestBed.createComponent(DiceComponent);
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;
    expect(compiled.querySelector('button').textContent.toLowerCase()).toContain('roll');
  });

  it('should have 2 non-draggable img tags with default dice images as src', () => {
    const fixture = TestBed.createComponent(DiceComponent);
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;

    const images = compiled.querySelectorAll('img');

    expect(images.length).toEqual(2);
    expect(images[0].draggable).toBe(false);
    expect(images[1].draggable).toBe(false);
    expect(images[0].src).toContain('assets/img/dice/1.png');
    expect(images[1].src).toContain('assets/img/dice/1.png');
  });

  it('should have 2 non-draggable img tags with default dice images as src', () => {
    const fixture = TestBed.createComponent(DiceComponent);
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;

    const images = compiled.querySelectorAll('img');

    expect(images.length).toEqual(2);
    expect(images[0].draggable).toBe(false);
    expect(images[1].draggable).toBe(false);
    expect(images[0].src).toContain('assets/img/dice/1.png');
    expect(images[1].src).toContain('assets/img/dice/1.png');
  });

  it('should disable 1 dice if singleDice is true', () => {
    const fixture = TestBed.createComponent(DiceComponent);
    const component = fixture.debugElement.componentInstance;
    component.singleDice = true;
    fixture.detectChanges();
    const compiled = fixture.debugElement.nativeElement;

    const disabledImage = compiled.querySelector('.disabled-img');
    expect(disabledImage).toBeTruthy();
  });

  it('should have a diceService', () => {
    const fixture = TestBed.createComponent(DiceComponent);
    const diceService = fixture.debugElement.injector.get(DiceService);
    fixture.detectChanges();
    expect(diceService).toBeTruthy();
  });

  it('should set both dice on the result returned from the DiceService', fakeAsync(() => {
    const fixture = TestBed.createComponent(DiceComponent);
    const component = fixture.debugElement.componentInstance;
    const compiled = fixture.debugElement.nativeElement;
    const diceService = fixture.debugElement.injector.get(DiceService);
    fixture.detectChanges();

    const diceDto: DiceDto = {roll1: 3, roll2: 5, total: 8};
    spyOn(diceService, 'getDiceValues')
      .and.returnValue(of(diceDto));

    component.onRollClick();
    tick(3000);
    fixture.detectChanges();

    fixture.whenStable()
    .then(() => {
      const images = compiled.querySelectorAll('img');
      expect(images[0].src).toContain('assets/img/dice/3.png');
      expect(images[1].src).toContain('assets/img/dice/5.png');
      expect(component.total).toEqual(8);
    });
  }));
});
