import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StepUserInterestsComponent } from './step-user-interests.component';

describe('StepUserInterestsComponent', () => {
  let component: StepUserInterestsComponent;
  let fixture: ComponentFixture<StepUserInterestsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [StepUserInterestsComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StepUserInterestsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
