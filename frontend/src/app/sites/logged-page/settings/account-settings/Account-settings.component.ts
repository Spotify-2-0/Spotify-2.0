import { Component, OnInit } from "@angular/core";
import { UserService } from "src/app/services/user.service";
import { User } from "src/app/models/models";
import { EmailValidator, FormBuilder, FormControl, FormGroup, Validators } from "@angular/forms";
import { displayNameValidator, emailValidator, firstNameValidator, lastNameValidator } from "src/app/shared/validators";


@Component({
  selector: 'app-account-settings',
  templateUrl: './Account-settings.component.html'
})
export class AccountSettingsComponent implements OnInit {
  firstName!: string;
  lastName!: string;
  displayName!: string;
  email!: string;
  originalUser!: User;
  accountSettingsForm!: FormGroup;

  constructor(private readonly userService: UserService,
    private readonly fb: FormBuilder) {
      this.accountSettingsForm = this.fb.group({
        firstName: ['', [firstNameValidator()]],
        lastName: ['', [lastNameValidator()]],
        displayName: ['', [displayNameValidator()]],
        email: ['', [emailValidator()]]
      })

    }

  ngOnInit(): void {
    this.userService.me().subscribe(user => {
      this.originalUser = user;
      this.firstName = user.firstName;
      this.lastName = user.lastName;
      this.displayName = user.displayName;
      this.email = user.email;
    });
  }

  onSubmit(){
    this.firstName = this.accountSettingsForm.controls['firstName'].value;
    this.lastName = this.accountSettingsForm.controls['lastName'].value;
    this.displayName = this.accountSettingsForm.controls['displayName'].value;
    this.email = this.accountSettingsForm.controls['email'].value;

    this.sendData();
  }

  sendData(){
    this.updateFirstNameIfNew();
    this.updateLastNameIfNew();
    this.updateDisplayNameIfNew();
    this.updateEmailIfNew();
  }

  updateFirstNameIfNew(){
    if(this.firstName != this.originalUser.firstName){
      this.userService.updateFirstName(this.firstName);
    }
  }

  updateLastNameIfNew(){
    if(this.lastName != this.originalUser.lastName){
      this.userService.updateLastName(this.lastName);
    }
  }

  updateDisplayNameIfNew(){
    if(this.displayName != this.originalUser.displayName){
      this.userService.updateDispalyName(this.displayName);
    }
  }

  updateEmailIfNew(){
    if(this.email != this.originalUser.email){
      this.userService.updateEmail(this.email);
    }
  }
}
