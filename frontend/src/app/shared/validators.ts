import {
  AbstractControl, AsyncValidatorFn,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
} from '@angular/forms';
import { Observable, of } from "rxjs";

export const requiredValidator = (): ValidatorFn => {
  return (control: AbstractControl): ValidationErrors | null => {
    if (control.value.length === 0) {
      return {
        required: 'This field is required',
      };
    }
    return null;
  };
};

export const firstNameValidator = (): ValidatorFn => {
  return (control: AbstractControl): ValidationErrors | null => {
    if (control.value.length === 0) {
      return {
        required: 'First name cannot be empty',
      };
    }
    if (control.value.length > 16) {
      return {
        required: 'First name cannot be longer than 16 characters',
      };
    }
    return null;
  };
};

export const lastNameValidator = (): ValidatorFn => {
  return (control: AbstractControl): ValidationErrors | null => {
    if (control.value.length === 0) {
      return {
        required: 'Last name cannot be empty',
      };
    }
    if (control.value.length > 16) {
      return {
        required: 'Last name cannot be longer than 16 characters',
      };
    }
    return null;
  };
};

export const displayNameValidator = (): ValidatorFn => {
  return (control: AbstractControl): ValidationErrors | null => {
    if (control.value.length === 0) {
      return {
        required: 'Display name cannot be empty',
      };
    }
    if (control.value.length > 16) {
      return {
        required: 'Display name cannot be longer than 16 characters',
      };
    }
    return null;
  };
};


export const emailValidator = (): ValidatorFn => {
  return (control: AbstractControl): ValidationErrors | null => {
    const validEmailRegex = new RegExp(
      /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/
    );
    if (!validEmailRegex.test(control.value)) {
      return {
        password: 'Email must be in format user.name@domain.com',
      };
    }


    return null;
  };
};

export const passwordValidator = (): ValidatorFn => {
  return (control: AbstractControl): ValidationErrors | null => {
    /*
      Minimum eight characters, at least one uppercase letter,
      one lowercase letter, one number and one special character
    */
    //const validPasswordRegex = new RegExp(
    //  '^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])(?=.{8,})'
    //);
    const validPasswordRegex = new RegExp(
      '^(?=.*[a-z])(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#&()–{}:;\',?/*~$^+=<>]).{8,}$'
    );
    if (!validPasswordRegex.test(control.value)) {
      return {
        password:
          'Password must be between 8 to 255 characters long, contain uppercase letter, lowercase letter, number, and special character',
      };
    }
    return null;
  };
};

export const ConfirmedValidator = (
  controlName: string,
  matchingControlName: string
) => {
  return (formGroup: FormGroup) => {
    const control = formGroup.controls[controlName];
    const matchingControl = formGroup.controls[matchingControlName];
    if (matchingControl.errors && !matchingControl.errors.confirmedValidator) {
      return;
    }
    if (control.value !== matchingControl.value) {
      matchingControl.setErrors({
        confirmedValidator: 'Passwords are not equal',
      });
    } else {
      matchingControl.setErrors(null);
    }
  };
};

export const tosValidator = (): ValidatorFn => {
  return (control: AbstractControl): ValidationErrors | null => {
    if (!control.value) {
      return {
        tos: false,
      };
    }
    return null;
  };
};
