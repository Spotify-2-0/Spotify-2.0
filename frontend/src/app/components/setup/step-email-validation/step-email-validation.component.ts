import { Component, OnInit } from '@angular/core';
import { Step } from '../../stepper/step';
import { UserService } from "../../../services/user.service";
import { User } from "../../../models/models";
import { Observable, pipe } from "rxjs";

@Component({
  selector: 'app-step-email-validation',
  templateUrl: './step-email-validation.component.html',
})
export class StepEmailValidationComponent implements Step, OnInit {

  user?: User;
  pin: string | null = null;

  constructor(private userService: UserService) {}

  public ngOnInit() {
    this.user = this.userService.currentUser()!;
  }

  public canProceed(): boolean | Observable<boolean> {
    if (!this.pin) {
      return false;
    }

    return this.userService.confirmEmail(this.pin);
  }

  pinCodeCompleted($event: string[]) {
    this.pin = $event.join("");
  }

  sendEmail() {
    this.userService.sendConfirmationEmail().subscribe();
  }
}
