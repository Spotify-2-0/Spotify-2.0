import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormInputTextComponent } from './form-input-text.component';
import { FormControl, FormGroup } from "@angular/forms";

describe('FormInputTextComponent', () => {
  let component: FormInputTextComponent;
  let fixture: ComponentFixture<FormInputTextComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FormInputTextComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FormInputTextComponent);
    component = fixture.componentInstance;
    component.formGroup = new FormGroup({
      input: new FormControl(true),
    });
    component.formName = 'input'
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
