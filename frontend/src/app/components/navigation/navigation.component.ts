import { Component } from "@angular/core";
import { ThemeService } from "src/app/services/theme.service";

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html'
})
export class NavigationComponent {
  constructor(private readonly themeService: ThemeService) { }

  toggleTheme() {
    this.themeService.toggleTheme();
  }
}
