import { Component, Input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-score',
  standalone: true,
  imports: [MatCardModule],
  templateUrl: './score.component.html',
  styleUrl: './score.component.css'
})
export class ScoreComponent {
  @Input() score?: number;
  @Input() highScore?: number;
}
