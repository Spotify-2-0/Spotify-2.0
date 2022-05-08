import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeartBtnComponent } from './heart-btn.component';

describe('HeartBtnComponent', () => {
  let component: HeartBtnComponent;
  let fixture: ComponentFixture<HeartBtnComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HeartBtnComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HeartBtnComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
