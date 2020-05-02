import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class NoteService {
  private readonly BASE_URL = `${environment.apiUrl}/notes`
  private readonly UPDATE_COLUMN = `${this.BASE_URL}/update/column`
  private readonly UPDATE_LINE = `${this.BASE_URL}/update/line`
  private readonly GET = `${this.BASE_URL}/get`

  constructor(private http: HttpClient) { }


  getNoteBook(cluedoId) {
    return this.http.get(`${this.GET}/${cluedoId}`);
  }

  updateNotebookColumn(notebookId , cardType, line, column, notationSymbol){
    return this.http.put(
      this.UPDATE_COLUMN,
      {
        notebookId,
        cardType,
        line,
        column,
        notationSymbol
      },
    );
  }

  updateNoteBookLineCrossed(notebookId, cardType, line, crossed){
    return this.http.put(this.UPDATE_LINE, 
      {
        notebookId,
        cardType,
        line,
        crossed
      }
    );
  }
}
