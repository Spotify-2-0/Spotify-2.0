import { Injectable } from '@angular/core';
import { AbstractControl, AsyncValidatorFn, ValidationErrors } from "@angular/forms";
import { Observable, of, timer } from "rxjs";
import { UserService } from "./user.service";
import {
  map,
  mergeMap
} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class ValidationService {

  private readonly validEmailRegex = new RegExp(/^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/);

  constructor(private readonly userService: UserService) { }

  public emailValidatorAsync = (): AsyncValidatorFn => {
    return (control: AbstractControl): Observable<ValidationErrors | null> => {
      return timer(500).pipe(
        mergeMap(() => {
          if (!this.validEmailRegex.test(control.value)) {
            return of({
              email: 'Email must be in format user.name@domain.com',
            });
          }

          return this.userService.existsByEmail(control.value).pipe(
            map(result => {
              if (result) {
                return { email: 'Email is already taken' }
              }
              return null;
            })
          );
        }),

      );
    }

  };


}
