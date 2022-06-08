import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule, ReactiveFormsModule } from '@angular/forms'

import { StepUserProfileComponent } from './step-user-profile.component';
import { HttpClientModule } from "@angular/common/http";
import { UserService } from "../../../services/user.service";

describe('StepUserProfileComponent', () => {
  let component: StepUserProfileComponent;
  let userService: jasmine.SpyObj<UserService>;
  let fixture: ComponentFixture<StepUserProfileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        FormsModule,
        ReactiveFormsModule,
        HttpClientModule
      ],
      declarations: [ StepUserProfileComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    userService = TestBed.inject(UserService) as jasmine.SpyObj<UserService>;
    userService['_userSubject'].next({
      firstName: '',
      lastName: '',
      email: '',
      emailConfirmed: true,
      displayName: '',
      id: 0
    })

    fixture = TestBed.createComponent(StepUserProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
