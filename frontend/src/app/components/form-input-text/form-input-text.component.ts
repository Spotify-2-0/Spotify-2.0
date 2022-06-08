import { AfterViewInit, Component, ElementRef, Input, OnInit, ViewChild } from '@angular/core';
import { FormGroup, ValidationErrors } from '@angular/forms';

@Component({
  selector: 'app-form-input-text',
  templateUrl: './form-input-text.component.html',
})
export class FormInputTextComponent implements AfterViewInit {
  @Input() public formGroup!: FormGroup;
  @Input() public formName!: string;
  @Input() public label?: string;
  @Input() public hideInput: boolean = false;
  @Input() public triggerValidationOnLostFocus?: boolean = false;
  @Input() public value?: string;
  @Input() public disable: boolean = false;
  @Input() public isSelector: boolean = false;
  @Input() public selectorData: string[] = [];

  private selectorWasOpened: boolean = false;

  @ViewChild('selector')
  public selectorRef!: ElementRef;

  constructor() {}

  ngAfterViewInit(): void {
    if (this.isSelector) {
      this.setUpSelectorListeners();
    }
  }

  public isError(): boolean {
    const control = this.formGroup.controls[this.formName];
    return control.touched && control.invalid;
  }

  public getErrorMessage(): string {
    const control = this.formGroup.controls[this.formName];
    const errors: ValidationErrors = control.errors!;
    return errors[Object.keys(errors)[0]];
  }

  onFocusOut() {
    if (this.triggerValidationOnLostFocus) {
      console.log('validating');
      this.formGroup.controls[this.formName].updateValueAndValidity();
    }
  }

  private setUpSelectorListeners() {
    const selector: HTMLSelectElement = this.selectorRef.nativeElement;

    selector.addEventListener('mousedown', this.onMouseDownHandler);
    selector.addEventListener('click', this.onClickHandler);
  }

  private onMouseDownHandler(event: Event) {
    const element = (<HTMLElement>event.currentTarget);

    if(!this.selectorWasOpened){
      event.preventDefault();
      this.selectorWasOpened = true;
      element.click();
    }

    if (element.hasAttribute('size') && element.getAttribute('size') === '1') {
      event.preventDefault();
    }
  }

  private onClickHandler(event: Event) {
    const element = <HTMLElement>event.currentTarget;

    if(!this.selectorWasOpened){
      event.preventDefault();
    }

    if (element.getAttribute('size') === '1') {
      element.setAttribute('size', '3');
    } else {
      element.setAttribute('size', '1');
    }
  }
}
