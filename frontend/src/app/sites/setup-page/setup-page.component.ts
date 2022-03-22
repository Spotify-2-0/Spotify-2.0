import { Component, OnInit } from '@angular/core';
import { StepEmailValidationComponent } from '../../components/setup/step-email-validation/step-email-validation.component';
import { StepUserProfileComponent } from '../../components/setup/step-user-profile/step-user-profile.component';
import { StepUserInterestsComponent } from '../../components/setup/step-user-interests/step-user-interests.component';
import { ThemeService } from '../../services/theme.service';
import { StepFinalComponent } from '../../components/setup/step-final/step-final.component';

@Component({
  selector: 'app-setup-page',
  templateUrl: './setup-page.component.html'
})
export class SetupPageComponent {
  public finalView = StepFinalComponent;
  public steps = [
    {
      name: 'Confirm your email',
      component: StepEmailValidationComponent,
    },
    {
      name: 'How others see you',
      component: StepUserProfileComponent,
    },
    {
      name: 'Let us know you',
      component: StepUserInterestsComponent,
    },
  ];

  constructor(private readonly themeService: ThemeService) {}

  public toggleTheme(): void {
    this.themeService.toggleTheme();
  }
}
