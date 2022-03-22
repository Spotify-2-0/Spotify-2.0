import { AfterViewInit, Component, OnDestroy } from '@angular/core';
import { animate, style, transition, trigger } from "@angular/animations";
import { positions } from "./world-map.constants"
import { Subscription, timer } from "rxjs";

@Component({
  selector: 'app-world-map',
  templateUrl: './world-map.component.html',
  animations: [
    trigger('fadeIn', [
      transition('void => *', [
        style({
          opacity: 0,
        }),
        animate('2s'),
      ])
    ])
  ]
})
export class WorldMapComponent implements AfterViewInit, OnDestroy {

  positions = positions;
  private subscriptions: Subscription[] = [];

  constructor() { }

  public ngAfterViewInit(): void {
    let affected: HTMLElement;
    const svg = document.getElementById('svg')!;
    const points = Array.prototype.slice.call(svg.getElementsByTagName('circle')!);

    const timer$ = timer(0, 2500)
      .subscribe(_ => {
        affected?.classList?.remove("pulse")
        const random = Math.floor((Math.random() * points.length));
        affected = points[random];
        affected.classList.add("pulse");
      });

    this.subscriptions.push(timer$)
  }

  public ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

}
