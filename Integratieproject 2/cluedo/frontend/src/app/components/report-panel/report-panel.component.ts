import { Component, OnInit, Inject } from '@angular/core';
import { ReportService } from '../../services/report.service';
import { ReportDetail } from '../../models/ReportDetail';
import { ReportReason } from '../../models/enums';
import { Report } from '../../models/Report';
import { MatDialog, MatDialogConfig, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'report-panel',
  templateUrl: './report-panel.component.html',
  styleUrls: ['./report-panel.component.css']
})
export class ReportPanelComponent implements OnInit {

  page: number = 1;
  pageSize: number = 10;

  count: number = 0;

  reportDetails: ReportDetail[];

  displayedColumns: string[] = ['username', 'userId', 'count', 'details'];


  constructor(private reportService: ReportService, public matDialog: MatDialog,) { }

  ngOnInit() {
    this.get();
  }

  async get() {
    this.getCount();
    this.reportService.getMostReportedUsers(this.page, this.pageSize).subscribe(
      r => {
        this.reportDetails = <ReportDetail[]>r;
      },
      err => {

      }
    )
  }

  async getCount() {
    this.reportService.getReportedCount().subscribe(
      r => {
        this.count = <number>r;
      },
      err => {

      }
    )
  }

  openDetails(userId){
    this.reportService.getReportsForUser(userId).subscribe(
      resp => {
        const reportReasonCounts = this.getReportReasonCounts(<Report[]>resp);
        this.createDetailDialog(reportReasonCounts);
      },
      err => { }
    )
  }

  getReportReasonCounts(reports : Report[]) {
    const reportReasonCounts = new Map();
        for (let reason in ReportReason) {
          if (isNaN(Number(reason))) {
            reportReasonCounts.set(reason, 0);
            reports.forEach(r => { 
              if (r.reportReasons.includes(reason)){
                reportReasonCounts.set(reason, reportReasonCounts.get(reason)+1);
              }
             });
          }
        }

    return reportReasonCounts;
  }
  
  createDetailDialog(data) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.disableClose = false;
    dialogConfig.id = "modal-component";
    dialogConfig.height = "350px";
    dialogConfig.width = "600px";
    dialogConfig.data = data;

    return this.matDialog.open(ReportDetailDialogComponent, dialogConfig);
  }

}

@Component({
  selector: 'report-detail-dialog',
  templateUrl: './report-panel.dialog.html',
  styleUrls: ['./report-panel.component.css']
})
export class ReportDetailDialogComponent {

  displayedColumns: string[] = ['reason', 'count'];

  constructor(public dialogRef: MatDialogRef<ReportDetailDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any){
    }
}