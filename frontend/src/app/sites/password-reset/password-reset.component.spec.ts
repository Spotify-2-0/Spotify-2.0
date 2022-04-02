import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PasswordResetComponent } from './password-reset.component';
import { HttpClientModule } from "@angular/common/http";
import { ReactiveFormsModule } from "@angular/forms";
import { RouterTestingModule } from "@angular/router/testing";

describe('PasswordResetComponent', () => {
  let component: PasswordResetComponent;
  let fixture: ComponentFixture<PasswordResetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PasswordResetComponent ],
      imports: [ ReactiveFormsModule, HttpClientModule, RouterTestingModule ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PasswordResetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
