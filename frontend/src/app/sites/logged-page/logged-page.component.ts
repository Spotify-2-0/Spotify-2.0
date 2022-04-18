import { Component, OnInit } from '@angular/core';
import { AvatarService } from 'src/app/services/avatar.service';
import { ThemeService } from 'src/app/services/theme.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-logged-page',
  templateUrl: './logged-page.component.html',
})
export class LoggedPageComponent implements OnInit {
  avatarBlob!: string;
  userId!: number;
  firstName!: string;
  lastName!: string

  constructor(
    private readonly themeService: ThemeService,
    private readonly avatarService: AvatarService,
    private readonly userService: UserService
  ) {}

  ngOnInit(): void {
    this.userService
      .me()
      .subscribe(user => {
        this.userId = user.id;
        this.avatarBlob = this.userService.getUserProfileUrl(this.userId)
        this.firstName = user.firstName;
        this.lastName = user.lastName;
      });
      this.avatarService.change.subscribe(() => this.avatarBlob = this.userService.getUserProfileUrl(this.userId));
  }

  public toggleTheme(): void {
    this.themeService.toggleTheme();
  }
}
