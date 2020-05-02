import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ReportService {
  private readonly REPORT_URL_BASE = `${environment.apiUrl}/report`;
  private readonly REPORT_LIST_URL = `${this.REPORT_URL_BASE}/top`;
  private readonly REPORT_COUNT_URL = `${this.REPORT_URL_BASE}/count`;
  private readonly REPORT_USER_URL = `${this.REPORT_URL_BASE}/for`;

  constructor(private http: HttpClient) { }

  report(cluedoId, playerId, reasons) {
    return this.http.post(`${this.REPORT_URL_BASE}`, {
      cluedoId,
      playerId,
      reportReasons: reasons,
    }, { },
    );
  }

  getMostReportedUsers(page, pageSize){
    return this.http.get(this.REPORT_LIST_URL, 
      { 
        params: {
          page,
          pageSize
        }
      })
  }

  getReportedCount() {
    return this.http.get(this.REPORT_COUNT_URL,{})
  }

  getReportsForUser(userId) {
    return this.http.get(this.REPORT_USER_URL,{
      params: {
        userId
      }
    })
  }

}
