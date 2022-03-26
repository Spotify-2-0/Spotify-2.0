import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { ThemeService } from 'src/app/services/theme.service';
import { fromEvent, Subscription } from "rxjs";

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
})
export class NavigationComponent implements OnInit, OnDestroy {
  @Input() isOnHomePage: boolean = false;
  @Input() isAlwaysWhite: boolean = false;
  @Input() fixed: boolean = false;
  @Input() background?: string;
  @Input() backgroundAfterScroll: boolean = true
  @Input() iconThemeColor?: string;
  isScrolled = false;
  private subscription?: Subscription;

  constructor(private readonly themeService: ThemeService) {}

  public ngOnInit(): void {
    if (this.fixed) {
      this.subscription = fromEvent(window, 'scroll').subscribe(_ => {
        this.isScrolled = window.scrollY > 200;
      });
    }
  }

  public ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  public toggleTheme(): void {
    this.themeService.toggleTheme();
  }
}
