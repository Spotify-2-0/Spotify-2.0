import { Component, OnInit } from '@angular/core';
import { Step } from '../../stepper/step';
import { image2base64 } from '../../../shared/functions';
import { CropResult } from '../../avatar-cropper/avatar-cropper.component';
import { User } from "../../../models/models";
import { UserService } from "../../../services/user.service";
import { Observable } from "rxjs";
import { map } from "rxjs/operators";

@Component({
  selector: 'app-step-user-profile',
  templateUrl: './step-user-profile.component.html',
})
export class StepUserProfileComponent implements Step, OnInit {
  public modalOpened = false;
  public dataUri!: string;
  public width!: number;
  public height!: number;
  public cropped: CropResult | null = null;
  user?: User;

  constructor(
    private readonly userService: UserService
  ) {}

  public ngOnInit(): void {
    this.user = this.userService.currentUser()!;
  }

  public selectFile($event: Event): void {
    const input = $event.target as HTMLInputElement;
    const file = input.files?.item(0)!;

    image2base64(file, (base64: string, width: number, height: number) => {
      this.dataUri = base64;
      this.width = width;
      this.height = height;
      this.modalOpened = true;
      input.value = '';
    });
  }

  public getUserProfileUrl(): string {
    return this.userService.getUserProfileUrl(this.user!.id);
  }

  public canProceed(): boolean | Observable<boolean>  {
    if (this.cropped) {
      return this.userService.uploadAvatar(this.cropped.blob)
        .pipe(map(_ => true)); // todo: ?
    }
    return true;
  }
}
