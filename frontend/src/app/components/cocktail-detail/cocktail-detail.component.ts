import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-cocktail-detail',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  templateUrl: './cocktail-detail.component.html',
  styleUrl: './cocktail-detail.component.css'
})
export class CocktailDetailComponent {
  @Input() additionalInfo!: string[];
  @Input() imageUrl!: string;
}