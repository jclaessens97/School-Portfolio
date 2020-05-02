import { Component, Inject } from '@angular/core';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material';

@Component({
  selector: 'snackbar',
  templateUrl: 'snackbar.component.html'
})
export class SnackBar {
  constructor(
    @Inject(MAT_SNACK_BAR_DATA) public data: any,
    private snackBarRef: MatSnackBarRef<SnackBar>
  ) { }

  close() {
    this.snackBarRef.dismiss();
  }
}