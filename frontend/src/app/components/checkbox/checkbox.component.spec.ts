import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CheckboxComponent } from './checkbox.component';
import { FormControl, FormGroup } from "@angular/forms";

describe('CheckboxComponent', () => {
  let component: CheckboxComponent;
  let fixture: ComponentFixture<CheckboxComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CheckboxComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckboxComponent);
    component = fixture.componentInstance;
    component.form = new FormGroup({
      checkbox: new FormControl(true),
    });
    component.formName = 'checkbox'
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
