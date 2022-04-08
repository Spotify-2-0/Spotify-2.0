import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { Step } from '../../stepper/step';
import { image2base64 } from '../../../shared/functions';
import { CropResult } from '../../avatar-cropper/avatar-cropper.component';
import { User } from "../../../models/models";
import { UserService } from "../../../services/user.service";
import { Observable, Subject } from "rxjs";
import { map } from "rxjs/operators";
import { AvatarService } from 'src/app/services/avatar.service';
import { ModalComponent } from "../../modal/modal.component";

@Component({
  selector: 'app-step-user-profile',
  templateUrl: './step-user-profile.component.html',
})
export class StepUserProfileComponent implements Step, OnInit {
  @ViewChild('modal') modal!: ModalComponent;
  public dataUri!: string;
  public width!: number;
  public height!: number;
  public cropped: CropResult | null = null;
  user?: User;
  deleted!: boolean;

  @Input() settings: boolean = false;

  @Output() imageApplied: EventEmitter<Blob | null> = new EventEmitter();

  constructor(
    private readonly userService: UserService,
    private readonly avatarService: AvatarService
  ) {}

  public ngOnInit(): void {
    this.user = this.userService.currentUser()!;

    if(this.settings) {
      this.getUserProfileUrl();
    }

    this.avatarService.delete.subscribe(value => {
      this.deleted = value

      if(value){
        this.cropped = null;
      }
    })
  }

  public selectFile($event: Event): void {
    const input = $event.target as HTMLInputElement;
    const file = input.files?.item(0)!;

    image2base64(file, (base64: string, width: number, height: number) => {
      this.dataUri = base64;
      this.width = width;
      this.height = height;
      this.modal.open();
      input.value = '';
    });
  }

  public emitImageApplied(){
    this.imageApplied.emit(this.cropped?.blob);
    this.modal.close();
    this.deleted = false;
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
