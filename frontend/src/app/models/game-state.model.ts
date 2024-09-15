export interface GameState {
  score: number;
  highScore: number;
  attemptsLeft: number;
  revealedName: string[];
  additionalInfo: string[];
  instructions: string;
  gameOver: boolean;
  message: string;
  imageUrl: string;
}
