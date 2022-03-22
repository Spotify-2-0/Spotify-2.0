import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StepUserProfileComponent } from './step-user-profile.component';

describe('StepUserProfileComponent', () => {
  let component: StepUserProfileComponent;
  let fixture: ComponentFixture<StepUserProfileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ StepUserProfileComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(StepUserProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
