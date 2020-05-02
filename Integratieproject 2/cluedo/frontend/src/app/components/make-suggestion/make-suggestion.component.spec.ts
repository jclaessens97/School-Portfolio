import { TestBed, fakeAsync, tick } from '@angular/core/testing';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { HttpLoaderFactory } from 'src/app/app.module';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { MaterialModule } from 'src/app/material.module';
import { MakeSuggestionComponent } from './make-suggestion.component';

describe('MakeSuggestionComponent', () => {
  beforeEach((() => {
    TestBed.configureTestingModule({
      declarations: [MakeSuggestionComponent],
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
    const fixture = TestBed.createComponent(MakeSuggestionComponent);
    const component = fixture.debugElement.componentInstance;
    expect(component).toBeTruthy();
  });
});
