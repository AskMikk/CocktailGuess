import { Component, Inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-game-over-dialog',
  standalone: true,
  imports: [MatDialogModule, MatButtonModule],
  templateUrl: './game-over-dialog.component.html',
  styleUrl: './game-over-dialog.component.css',
})
export class GameOverDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<GameOverDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { cocktailName: string }
  ) {}

  onRestart(): void {
    this.dialogRef.close(true);
  }
}
