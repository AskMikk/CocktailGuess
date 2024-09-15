import { Component, OnInit } from '@angular/core';
import { GameState } from '../../models/game-state.model';
import { GameService } from '../../services/game.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ScoreComponent } from '../score/score.component';
import { CocktailDetailComponent } from '../cocktail-detail/cocktail-detail.component';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { GameOverDialogComponent } from '../game-over-dialog/game-over-dialog.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-game',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    ScoreComponent,
    CocktailDetailComponent,
    MatCardModule,
    MatButtonModule,
    MatInputModule,
    GameOverDialogComponent,
  ],
  templateUrl: './game.component.html',
  styleUrl: './game.component.css',
})
export class GameComponent implements OnInit {
  gameState!: GameState;
  guess: string = '';
  successMessage: string | null = null;

  private wordsCache: (string | null)[][] | null = null;
  private previousRevealedName: string[] | null = null;

  constructor(private gameService: GameService, public dialog: MatDialog) {}

  ngOnInit(): void {
    this.restartGame();
  }

  makeGuess(): void {
    if (!this.guess.trim()) return;
    this.gameService.makeGuess(this.guess).subscribe(
      (state) => {
        this.gameState = state;
        this.guess = '';
        if (this.gameState.message && !this.gameState.gameOver) {
          this.successMessage = this.gameState.message;
          setTimeout(() => {
            this.successMessage = null;
          }, 3000);
        }
        if (this.gameState.gameOver) {
          this.openGameOverDialog();
        }
      },
      (error) => {
        console.error('Error making guess:', error);
      }
    );
  }

  getWords(revealedName: string[]): (string | null)[][] {
    if (this.wordsCache && this.previousRevealedName === revealedName) {
      return this.wordsCache;
    }

    this.previousRevealedName = revealedName;
    const words: (string | null)[][] = [];
    let currentWord: (string | null)[] = [];

    revealedName.forEach((letter) => {
      if (letter === ' ') {
        if (currentWord.length > 0) {
          words.push([...currentWord]);
          currentWord = [];
        }
        words.push([null]);
      } else {
        currentWord.push(letter);
      }
    });

    if (currentWord.length > 0) {
      words.push([...currentWord]);
    }

    this.wordsCache = words;
    return words;
  }

  openGameOverDialog(): void {
    const dialogRef = this.dialog.open(GameOverDialogComponent, {
      data: { cocktailName: this.gameState.revealedName.join('') },
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((restart: boolean) => {
      if (restart) {
        this.restartGame();
      }
    });
  }

  restartGame(): void {
    this.gameService.restartGame().subscribe(
      (state) => {
        this.gameState = state;
        this.guess = '';
      },
      (error) => {
        console.error('Error restarting game:', error);
      }
    );
  }
}
