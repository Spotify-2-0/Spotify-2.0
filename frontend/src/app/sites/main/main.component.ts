import { Component, ViewEncapsulation } from '@angular/core';
import { ThemeService } from "../../services/theme.service";
import { animate, style, transition, trigger } from "@angular/animations";

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  animations: [
    trigger('ani', [
      transition(':enter', [
        style({
          opacity: 0,
        }),
        animate('2s'),
      ])
    ]),
  ]
})
export class MainComponent {

  constructor(private readonly themeService: ThemeService) { }

  toggleTheme() {
    this.themeService.toggleTheme();
  }

}
