import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SpotiTableComponent } from './spoti-table.component';

describe('SpotiTableComponent', () => {
  let component: SpotiTableComponent;
  let fixture: ComponentFixture<SpotiTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SpotiTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SpotiTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
