import { Component, OnInit } from '@angular/core';
import { Step } from "../../stepper/step";

@Component({
  selector: 'app-step-email-validation',
  templateUrl: './step-email-validation.component.html',
  styleUrls: ['./step-email-validation.component.scss']
})
export class StepEmailValidationComponent implements Step{

  constructor() { }

  public canProceed(): boolean {
    //TODO verify email
    return true;
  }

}
