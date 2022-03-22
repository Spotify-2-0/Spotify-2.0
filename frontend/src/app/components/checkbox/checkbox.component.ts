import { Component, Input } from '@angular/core';
import { FormGroup } from "@angular/forms";

@Component({
  selector: 'app-checkbox',
  templateUrl: './checkbox.component.html'
})
export class CheckboxComponent {

  @Input() form!: FormGroup;
  @Input() formName!: string;
  public isChecked = false;

  constructor() { }

  public isError(): boolean {
    const control = this.form.controls[this.formName];
    return control.touched && control.invalid;
  }

  public onClick(): void {
    this.isChecked = !this.isChecked
  }
}
