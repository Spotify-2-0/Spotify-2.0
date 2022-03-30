import { Component } from '@angular/core';
import { Step } from '../../stepper/step';
import { image2base64 } from '../../../shared/functions';
import { CropResult } from '../../avatar-cropper/avatar-cropper.component';

@Component({
  selector: 'app-step-user-profile',
  templateUrl: './step-user-profile.component.html',
})
export class StepUserProfileComponent implements Step {
  public modalOpened = false;
  public dataUri!: string;
  public width!: number;
  public height!: number;
  public cropped: CropResult | null = null;

  constructor() {}

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
  public getUserInitials(): string {
    //TODO
    return 'LF';
  }

  public canProceed(): boolean {
    return true;
  }
}
