import { Component } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { passwordValidator, repeatPasswordValidator, requiredValidator } from 'src/app/shared/validators';
import { PasswordChangeRequest } from 'src/app/models/models';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
})
export class ChangePasswordComponent {
  changePasswordForm!: FormGroup;
  passwordChangeBody!: PasswordChangeRequest;

  constructor(
    private readonly fb: FormBuilder,
    private readonly userService: UserService
  ) {
    this.changePasswordForm = this.fb.group({
      oldPassword: ['', requiredValidator()],
      newPassword: ['', passwordValidator()],
      repeatedNewPassword: [''],
    },
    { validator: repeatPasswordValidator('newPassword', 'repeatedNewPassword')});
  }

  onSubmit() {
    this.changePasswordForm.markAllAsTouched();

    this.passwordChangeBody = {
      oldPassword: this.changePasswordForm.controls['oldPassword']!.value,
      newPassword: this.changePasswordForm.controls['newPassword']!.value,
      repeatedNewPassword: this.changePasswordForm.controls['repeatedNewPassword']!.value,
    };

    this.userService.changePassword(this.passwordChangeBody).subscribe();
  }
}
