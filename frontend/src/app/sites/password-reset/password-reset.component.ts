import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from "@angular/forms";
import { emailValidator, passwordValidator } from "../../shared/validators";
import { ThemeService } from "../../services/theme.service";
import { UserService } from "../../services/user.service";
import { Router } from "@angular/router";
import { PinCodeComponent } from "../../components/pin-code/pin-code.component";
import { catchError } from "rxjs/operators";
import { EMPTY } from "rxjs";
import { ApiError } from "../../models/models";
import { HttpErrorResponse } from "@angular/common/http";

@Component({
  selector: 'app-password-reset',
  templateUrl: './password-reset.component.html',
})
export class PasswordResetComponent {
  @ViewChild('pinCodeComponent') pinCodeComponent!: PinCodeComponent;

  step = 1;
  pin: string | null = null;
  passwordResetKey!: string;

  passwordResetForm: FormGroup = this.fb.group({
    email: ['', [emailValidator()]],
  });

  newPasswordForm: FormGroup = this.fb.group({
    password: ['', [passwordValidator()]],
  });

  public constructor(
    private readonly themeService: ThemeService,
    private readonly userService: UserService,
    private readonly router: Router,
    private readonly fb: FormBuilder
  ) {}

  public toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  public sendEmail(): void {
    this.passwordResetForm.markAllAsTouched();
    if (this.passwordResetForm.valid) {
      const email = this.passwordResetForm.controls['email'].value;
      this.userService.sendEmailPasswordReset(email).subscribe(_ => {
        this.step++;
      });
    }
  }

  public onPinCodeComplete($event: string[]) {
    this.pin = $event.join("");
  }

  public submitPinCode(): void {
    const email = this.passwordResetForm.controls['email'].value;
    this.userService.getPasswordChangeKeyFromPinCode(email, this.pin!)
      .pipe(catchError(err => {
        const apiError = err.error as ApiError;
        console.log(apiError)
        console.log(apiError.statusCode)
        this.pinCodeComponent.setErrorMessage(apiError.message);
        this.pinCodeComponent.markInvalid();
        return EMPTY;
      }))
      .subscribe(key => {
        this.step++;
        this.passwordResetKey = key
      });
  }

  public submitChangePassword(): void {
    this.newPasswordForm.markAllAsTouched();
    if (this.newPasswordForm.valid) {
      const newPassword = this.newPasswordForm.controls['password'].value;
      this.userService.passwordResetKeyFromPinCode(this.passwordResetKey, newPassword)
        .subscribe(_ => {
          this.step++;
        });
    }
  }
}
