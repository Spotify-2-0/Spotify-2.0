import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StepperComponent } from './stepper.component';

describe('StepperComponent', () => {
  let component: StepperComponent;
  let fixture: ComponentFixture<StepperComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StepperComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StepperComponent);
    component = fixture.componentInstance;

    const testStep = {
      name: "test step",
      component: jasmine.createSpyObj("component", {canProceed: () => true})
    }
    component.steps.push(testStep);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
