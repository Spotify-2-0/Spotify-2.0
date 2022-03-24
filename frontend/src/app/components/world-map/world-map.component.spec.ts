import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WorldMapComponent } from './world-map.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('WorldMapComponent', () => {
  let component: WorldMapComponent;
  let fixture: ComponentFixture<WorldMapComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WorldMapComponent],
      imports: [BrowserAnimationsModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(WorldMapComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
