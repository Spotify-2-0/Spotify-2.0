import { Component } from '@angular/core';
import { ThemeService } from '../../services/theme.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import {
  displayNameValidator,
  firstNameValidator,
  lastNameValidator,
  passwordValidator,
  tosValidator,
} from '../../shared/validators';
import { UserService } from "../../services/user.service";
import { ValidationService } from "../../services/validation.service";
import { pipe } from "rxjs";
import { catchError } from "rxjs/operators";
import { Router } from "@angular/router";

@Component({
  selector: 'app-signup-page',
  templateUrl: './signup-page.component.html',
})
export class SignupPageComponent {
  public submitted: boolean = false;

  form: FormGroup = this.fb.group(
    {
      firstName: ['', [firstNameValidator()]],
      lastName: ['', [lastNameValidator()]],
      displayName: ['', [displayNameValidator()]],
      email: ['', [], [this.validationService.emailValidatorAsync()]],
      password: ['', [passwordValidator()]],
      tos: [false, [tosValidator()]],
    }
  );

  constructor(
    private readonly validationService: ValidationService,
    private readonly themeService: ThemeService,
    private readonly userService: UserService,
    private readonly router: Router,
    private readonly fb: FormBuilder
  ) {}

  public toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  public onSubmit(): void {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      this.userService.signUp({
        firstName: this.form.controls['firstName'].value,
        lastName: this.form.controls['lastName'].value,
        displayName: this.form.controls['displayName'].value,
        email: this.form.controls['email'].value,
        password: this.form.controls['password'].value,
      }).subscribe(_ => this.router.navigate(['/setup']));
    }
  }
}
