import { Component, ViewEncapsulation } from '@angular/core';
import { animate, style, transition, trigger } from '@angular/animations';

@Component({
  selector: 'app-main',
  templateUrl: './home.component.html',
  animations: [
    trigger('ani', [
      transition(':enter', [
        style({
          opacity: 0,
        }),
        animate('2s'),
      ]),
    ]),
  ],
})
export class HomeComponent {
  constructor() {}
}
