import { Component, Inject, Output, EventEmitter } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { ReportReason } from '../../models/enums';
import { Player } from "../../models/Player";
import { ReportService } from '../../services/report.service';
import { CluedoExceptionType } from '../../models/CluedoExceptionType';

@Component({
  selector: 'report-dialog',
  templateUrl: './report.dialog.html',
  styleUrls: ['./report.dialog.css']
})
export class ReportDialog {

  @Output() resultEmitter = new EventEmitter<CluedoExceptionType>();

  reportReasons = ReportReason;
  reasons = [];

  constructor(
    public dialogRef: MatDialogRef<ReportDialog>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private reportService: ReportService,
    ) { }

  send() {
    this.reportService.report(this.data.cluedoId, this.data.player.playerId, this.reasons).subscribe(
      () => this.resultEmitter.emit(null),
      err => {
        this.resultEmitter.emit(err);
      }
    )
  }

  close() {
    this.dialogRef.close();
  }



}