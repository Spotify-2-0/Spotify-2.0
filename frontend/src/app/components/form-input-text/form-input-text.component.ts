import { Component, Input } from '@angular/core';
import { FormGroup, ValidationErrors } from '@angular/forms';

@Component({
  selector: 'app-form-input-text',
  templateUrl: './form-input-text.component.html',
})
export class FormInputTextComponent {
  @Input() public formGroup!: FormGroup;
  @Input() public formName!: string;
  @Input() public label?: string;
  @Input() public hideInput: boolean = false;

  public value: string = '';

  constructor() {}

  public isError(): boolean {
    const control = this.formGroup.controls[this.formName];
    return control.touched && control.invalid;
  }

  public getErrorMessage(): string {
    const control = this.formGroup.controls[this.formName];
    const errors: ValidationErrors = control.errors!;
    return errors[Object.keys(errors)[0]];
  }
}
