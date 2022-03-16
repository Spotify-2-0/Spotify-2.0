import { TestBed, waitForAsync } from '@angular/core/testing';

import { IconService } from '../icon.service';
import { HttpClientModule } from "@angular/common/http";

describe('IconService', () => {
  let service: IconService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ HttpClientModule ],
    });
    service = TestBed.inject(IconService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should find icon', waitForAsync(() => {
    service.requestIcon('theme')
      .subscribe(icon => expect(icon).not.toBeUndefined())
  }));

  it('should not find icon', waitForAsync(() => {
    service.requestIcon('icon_that_doesnt_exist')
      .subscribe(icon => expect(icon).toBeUndefined())
  }));
});
