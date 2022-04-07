import {  Component, OnInit } from '@angular/core';
import { UserService } from 'src/app/services/user.service';
import { UpdateRequest, User } from 'src/app/models/models';
import {
  FormBuilder,
  FormGroup,
} from '@angular/forms';
import {
  displayNameValidator,
  firstNameValidator,
  lastNameValidator,
} from 'src/app/shared/validators';
import { CropResult } from 'src/app/components/avatar-cropper/avatar-cropper.component';
import { AvatarService } from 'src/app/services/avatar.service';

@Component({
  selector: 'app-account-settings',
  templateUrl: './Account-settings.component.html',
})
export class AccountSettingsComponent implements OnInit {
  userId!: number;
  firstName!: string;
  lastName!: string;
  displayName!: string;
  email!: string;
  originalUser!: User;
  accountSettingsForm!: FormGroup;
  updateRequest: UpdateRequest = {};
  imgBlob: Blob | null = null;
  imgDeleted!: boolean;

  constructor(
    private readonly userService: UserService,
    private readonly fb: FormBuilder,
    private readonly avatarService: AvatarService
  ) {
    this.accountSettingsForm = this.fb.group({
      firstName: ['', [firstNameValidator()]],
      lastName: ['', [lastNameValidator()]],
      displayName: ['', [displayNameValidator()]],
      email: [''],
    });
  }

  ngOnInit(): void {
    this.userService.me().subscribe((user) => {
      this.originalUser = user;
      this.userId = user.id;
      this.firstName = user.firstName;
      this.lastName = user.lastName;
      this.displayName = user.displayName;
      this.email = user.email;

      this.accountSettingsForm.setValue({
        firstName: this.firstName,
        lastName: this.lastName,
        displayName: this.displayName,
        email: this.email,
      });
    });

    this.avatarService.delete.subscribe(value => {
      this.imgDeleted = value;
    })
  }

  onSubmit() {
    this.firstName = this.accountSettingsForm.controls['firstName'].value;
    this.lastName = this.accountSettingsForm.controls['lastName'].value;
    this.displayName = this.accountSettingsForm.controls['displayName'].value;
    this.email = this.accountSettingsForm.controls['email'].value;

    this.sendData();
  }

  onDeleteImg() {
    this.avatarService.emitDeleteImage(true);
  }

  sendData() {
    this.updateFirstNameIfNew();
    this.updateLastNameIfNew();
    this.updateDisplayNameIfNew();

    if (this.updateRequest) {
      this.userService.updateUserDetails(this.updateRequest).subscribe();
    }

    if (this.imgBlob != null) {
      this.userService.uploadAvatar(this.imgBlob).subscribe();
    }

    if (this.imgDeleted) {
      this.userService.setDefaultAvatarForCurrentUser().subscribe();
      this.imgDeleted = false;
    }
  }

  updateFirstNameIfNew() {
    if (this.firstName !== this.originalUser.firstName) {
      this.updateRequest.firstName = this.firstName;
    }
  }

  updateLastNameIfNew() {
    if (this.lastName !== this.originalUser.lastName) {
      this.updateRequest.lastName = this.lastName;
    }
  }

  updateDisplayNameIfNew() {
    if (this.displayName !== this.originalUser.displayName) {
      this.updateRequest.displayName = this.displayName;
    }
  }
}
