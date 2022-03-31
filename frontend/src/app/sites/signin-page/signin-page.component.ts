import { Component, OnInit } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormGroup,
  Validators,
} from '@angular/forms';
import { ThemeService } from 'src/app/services/theme.service';
import { emailValidator, requiredValidator } from 'src/app/shared/validators';
import { UserService } from "../../services/user.service";
import { Router } from "@angular/router";

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
    private readonly userService: UserService,
    private readonly router: Router,
    private readonly fb: FormBuilder
  ) {}

  onSubmit(): void {
    this.signinForm.markAllAsTouched();
    if (this.signinForm.valid) {
      this.userService.signIn({
        email: this.signinForm.controls['email'].value,
        password: this.signinForm.controls['password'].value,
      })
      .subscribe(_ => this.router.navigate(['/app']));
    }
  }

  public toggleTheme(): void {
    this.themeService.toggleTheme();
  }
}
