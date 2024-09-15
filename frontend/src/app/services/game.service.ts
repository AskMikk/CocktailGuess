import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { GameState } from '../models/game-state.model';

@Injectable({
  providedIn: 'root',
})
export class GameService {
  private apiUrl = 'http://localhost:8080/api/game';

  constructor(private http: HttpClient) {}

  makeGuess(guess: string): Observable<GameState> {
    return this.http.post<GameState>(
      `${this.apiUrl}/guess`,
      { guess }
    );
  }

  restartGame(): Observable<GameState> {
    return this.http.get<GameState>(`${this.apiUrl}/restart`);
  }
}
