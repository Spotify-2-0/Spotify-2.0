import { Component } from "@angular/core";
import { FormBuilder, FormGroup } from "@angular/forms";
import { passwordValidator } from "src/app/shared/validators";

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html'
})
export class ChangePasswordComponent {

  changePasswordForm!: FormGroup;

  constructor(private readonly fb: FormBuilder){
    this.changePasswordForm = this.fb.group({
      oldPassword: [''],
      newPassword: ['', passwordValidator()],
      repeatedNewPassword: ['']
    });
  }

  onSubmit() {
    //TODO: [Backend-Frontend]
  }
}
