import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StepEmailValidationComponent } from './step-email-validation.component';
import { HttpClientModule } from "@angular/common/http";

describe('StepEmailValidationComponent', () => {
  let component: StepEmailValidationComponent;
  let fixture: ComponentFixture<StepEmailValidationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [StepEmailValidationComponent],
      imports: [HttpClientModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StepEmailValidationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
