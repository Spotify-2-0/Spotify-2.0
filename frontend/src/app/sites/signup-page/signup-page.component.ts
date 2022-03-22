import { Component } from '@angular/core';
import { ThemeService } from "../../services/theme.service";
import {FormBuilder, FormGroup} from "@angular/forms";
import {
  ConfirmedValidator,
  emailValidator,
  firstNameValidator,
  lastNameValidator,
  passwordValidator, tosValidator
} from "../../shared/validators";

@Component({
  selector: 'app-signup-page',
  templateUrl: './signup-page.component.html'
})
export class SignupPageComponent {

  public submitted: boolean = false;

  form: FormGroup = this.fb.group({
    firstName: ['', [firstNameValidator()]],
    lastName: ['', [lastNameValidator()]],
    email: ['', [emailValidator()]],
    password: ['', [passwordValidator()]],
    rePassword: ['', []],
    tos: [false, [tosValidator()]]
  }, {
    validator: ConfirmedValidator('password', 'rePassword')
  })

  constructor(
    private readonly themeService: ThemeService,
    private readonly fb: FormBuilder
  ) { }

  public toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  public onSubmit(): void {
    this.form.markAllAsTouched();
  }
}
