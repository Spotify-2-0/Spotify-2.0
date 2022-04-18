import { Component, OnInit } from '@angular/core';
import { ThemeService } from 'src/app/services/theme.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-logged-page',
  templateUrl: './logged-page.component.html',
})
export class LoggedPageComponent implements OnInit {
  avatarBlob!: string;
  firstName!: string;
  lastName!: string

  constructor(
    private readonly themeService: ThemeService,
    private readonly userService: UserService
  ) {}

  ngOnInit(): void {
    this.userService
      .me()
      .subscribe(user => {
        this.avatarBlob = this.userService.getUserProfileUrl(user.id)
        this.firstName = user.firstName;
        this.lastName = user.lastName;
      });
  }

  public toggleTheme(): void {
    this.themeService.toggleTheme();
  }
}
