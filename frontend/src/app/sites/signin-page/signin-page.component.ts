import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { ThemeService } from 'src/app/services/theme.service';
import { emailValidator, requiredValidator } from 'src/app/shared/validators';

@Component({
  selector: 'app-signin-page',
  templateUrl: './signin-page.component.html',
})
export class SigninPageComponent implements OnInit {
  signinForm!: FormGroup;

  ngOnInit(): void {
    this.signinForm = this.fb.group({
      email: ['', [emailValidator()]],
      password: ['', [requiredValidator()]],
    });
  }

  public constructor(
    private readonly themeService: ThemeService,
    private readonly fb: FormBuilder
  ) {}

  onSubmit(): void {
    this.signinForm.markAllAsTouched();
    //TODO: send request to server
  }

  public toggleTheme(): void {
    this.themeService.toggleTheme();
  }
}
