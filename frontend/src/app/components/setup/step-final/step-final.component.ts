import { Component, OnInit } from '@angular/core';
import { Subscription, timer } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-step-final',
  templateUrl: './step-final.component.html',
})
export class StepFinalComponent implements OnInit {
  progress = 0;
  sub!: Subscription;
  constructor(private readonly router: Router) {}

  ngOnInit(): void {
    this.sub = timer(30, 30).subscribe((_) => {
      this.progress++;
      if (this.progress >= 100) {
        this.router.navigate(['/app']);
        this.sub.unsubscribe();
      }
    });
  }
}
