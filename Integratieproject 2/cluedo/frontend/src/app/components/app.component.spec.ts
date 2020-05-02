import { TestBed, async } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AppComponent } from './app.component';
import { DiceComponent } from './dice/dice.component';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { NoteComponent } from './note/note.component';
import { MaterialModule } from '../material.module';
import { AlertComponent } from './alert/alert.component';
import { TranslateModule, TranslateLoader } from '@ngx-translate/core';
import { HttpLoaderFactory } from '../app.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { FriendListComponent } from './friend/friend-list.component';
import { NotificationComponent } from './notification/notification.component';

describe('AppComponent', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent,
        DiceComponent,
        NoteComponent,
        AlertComponent,
        FriendListComponent,
        NotificationComponent,
      ],
      imports: [
        RouterTestingModule,
        HttpClientModule,
        MaterialModule,
        FormsModule,
        ReactiveFormsModule,
        BrowserAnimationsModule,
        TranslateModule.forRoot({
          loader: {
              provide: TranslateLoader,
              useFactory: HttpLoaderFactory,
              deps: [HttpClient]
          }
        })
      ],
    })
    .compileComponents()
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    fixture.detectChanges();
    const app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  });
});
