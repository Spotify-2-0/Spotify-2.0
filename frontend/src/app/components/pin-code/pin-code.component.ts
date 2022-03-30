import {
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
  QueryList,
  ViewChildren
} from '@angular/core';

@Component({
  selector: 'app-pin-code',
  templateUrl: './pin-code.component.html'
})
export class PinCodeComponent implements OnInit{

  @Input() numOfDigits = 5;
  @Output() pinCodeComplete = new EventEmitter<string[]>();

  @ViewChildren('inputs') inputs!: QueryList<any>;

  values: (string|null)[] = [];

  constructor() {
    console.log()
  }

  ngOnInit() {
    for (let i = 0; i< this.numOfDigits; i++) {
      this.values.push(null);
    }
  }

  check(index: number, event: KeyboardEvent) {
    if (event.ctrlKey && event.key === 'v') {
      return;
    }

    event.preventDefault();
    if (isNaN(parseInt(event.key, 10)) && event.key !== 'Backspace') {
      return;
    }

    if (event.key === 'Backspace') {
      this.values[index] = null;
      this.inputs.get(index).nativeElement.value = null
      if (index > 0) {
        this.inputs.get(index - 1).nativeElement.focus();
      }
      return;
    }

    this.values[index] = event.key;
    this.inputs.get(index).nativeElement.value = event.key
    if (index < this.inputs.length - 1) {
      this.inputs.get(index + 1).nativeElement.focus();
    }

    // @ts-ignore
    this.pinCodeComplete.emit(this.values);
  }


  onFocusIn(idx: number, $event: FocusEvent) {
    if (idx === 0) {
      return;
    }
    if (!this.values[idx - 1]) {
      $event.preventDefault();
      this.inputs.get(this.getFirstNotSet()).nativeElement.focus();
    }
  }

  getFirstNotSet() {
    return this.values.findIndex(x => !x)
  }

  onPaste($event: ClipboardEvent) {
    let clipboardData = $event.clipboardData;
    if (clipboardData) {
      let pastedText = clipboardData.getData('text');
      if (pastedText.length !== this.numOfDigits) {
        return;
      }
      if (!/^\d+$/.test(pastedText)) {
        return;
      }

      for (let i = 0; i < pastedText.length; ++i) {
        this.values[i] = pastedText.charAt(i);
        this.inputs.get(i).nativeElement.value = pastedText.charAt(i)
      }
      this.inputs.get(this.numOfDigits - 1).nativeElement.focus();
    }
  }
}
