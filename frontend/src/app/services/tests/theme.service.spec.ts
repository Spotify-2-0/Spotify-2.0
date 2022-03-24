import { TestBed } from '@angular/core/testing';

import { ThemeService } from '../theme.service';

describe('ThemeService', () => {
  let service: ThemeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ThemeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should change theme', () => {
    service.setTheme('dark');
    expect(service.getTheme()).toBe('dark');
  });

  it('should toggle theme', () => {
    service.setTheme('light');
    service.toggleTheme();
    expect(service.getTheme()).toBe('dark');
  });

  it('should return valid theme', () => {
    service.setTheme('light');
    expect(service.getTheme()).toBe('light');
  });
});
