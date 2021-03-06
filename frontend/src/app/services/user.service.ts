import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import {
  ConfirmEmailResponse,
  PasswordChangeRequest,
  PasswordResetPinToKeyResponse,
  SignInRequest,
  SignResponse,
  SignUpRequest,
  UpdateRequest,
  User,
  UserExistsByResponse,
  ActivityResponse,
  IdAndNameDTO
} from "../models/models";
import { BehaviorSubject, EMPTY, Observable } from "rxjs";
import { environment } from "../../environments/environment";
import { catchError, map, tap } from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private _userSubject = new BehaviorSubject<User | undefined>(undefined);
  public user = this._userSubject.asObservable();

  constructor(private readonly http: HttpClient) { }

  public currentUser(): User | undefined {
    return this._userSubject.value;
  }

  public signUp = (request: SignUpRequest): Observable<Object> => {
    return this.http.post<SignResponse>(`${environment.serverURL}/auth/signup`, request)
      .pipe(
        catchError(err => {
          console.error(err)
          return EMPTY;
        }),
        map(response => {
          this._userSubject.next(response.user)
          localStorage.setItem('access_token', response.token)
          return response.user;
        })
      );
  }

  public signIn = (request: SignInRequest): Observable<Object> => {
    return this.http.post<SignResponse>(`${environment.serverURL}/auth/signin`, request)
      .pipe(
        catchError(err => {
          console.error(err)
          return EMPTY;
        }),
        map(response => {
          this._userSubject.next(response.user)
          localStorage.setItem('access_token', response.token)
          return response.user;
        })
      );
  }

  public me(): Observable<User> {
    return this.http.get<User>(`${environment.serverURL}/user/me`)
      .pipe(
        catchError(err => {
          console.error(err)
          return EMPTY;
        }),
        tap(x => {
          this._userSubject.next(x)
        })
      );
  }

  public logout = () => {
    localStorage.removeItem('access_token')
  }


  public existsByEmail = (email: string): Observable<boolean> => {
    return this.http.post<UserExistsByResponse>(`${environment.serverURL}/user/existsByEmail`, { email: email })
      .pipe(map(response => response.exists))
  }

  public sendConfirmationEmail = (): Observable<void> => {
    return this.http.post<void>(`${environment.serverURL}/user/sendEmailConfirmationCode`, {});
  }

  public confirmEmail = (code: string): Observable<boolean> => {
    return this.http.post<ConfirmEmailResponse>(`${environment.serverURL}/user/confirmEmail`, { code: code })
      .pipe(
        tap(response => this._userSubject.next({...this.currentUser()!, emailConfirmed: response.success})),
        map(response => response.success))
  }

  public updateUserDetails = (toUpdate: UpdateRequest): Observable<void> => {
    return this.http.patch<void>(`${environment.serverURL}/user/updatePreferences`, toUpdate);
  }

  public getUserProfileUrl = (userId: number) => {
    return `${environment.serverURL}/user/profile/${userId}/avatar`
  }

  public uploadAvatar = (avatar: Blob): Observable<void> => {
    const form = new FormData();
    form.append('image', avatar);
    return this.http.post<void>(`${environment.serverURL}/user/uploadAvatar`, form);
  }

  public setDefaultAvatarForCurrentUser = (): Observable<void> => {
    return this.http.put<void>(`${environment.serverURL}/user/setDefaultAvatarForCurrentUser`, null);
  }

  public sendEmailPasswordReset = (email: string): Observable<void> => {
    return this.http.post<void>(`${environment.serverURL}/user/sendEmailPasswordReset`, {
      email: email
    });
  }

  public getPasswordChangeKeyFromPinCode = (email: string, pin: string): Observable<string> => {
    return this.http.post<PasswordResetPinToKeyResponse>(`${environment.serverURL}/user/passwordResetKeyFromPinCode`, {
      email: email,
      pin: pin
    }).pipe(map(response => response.key))
  }

  public changePassword = (changePasswordRequest: PasswordChangeRequest): Observable<void> => {
    return this.http.patch<void>(`${environment.serverURL}/user/changePassword`, changePasswordRequest);
  }

  public passwordResetKeyFromPinCode = (resetKey: string, password: string): Observable<void> => {
    return this.http.post<void>(`${environment.serverURL}/user/resetPassword`, {
      key: resetKey,
      password: password,
    })
  }

  public userActivity = (page: number, size: number): Observable<{ content: ActivityResponse[], totalPages: number }> => {
    return this.http.get<{ content: ActivityResponse[], totalPages: number  }>(`${environment.serverURL}/user/activity`, {
      params: {
        page: page,
        pageSize: size
      }
    }).pipe(map(response => {
      response.content.forEach(activity => {
        activity.occurrenceDate = new Date(activity.occurrenceDate);
      })
      return { content: response.content, totalPages: response.totalPages };
    }));
  }

  public getUsersByNameStartingWith = (str: string): Observable<IdAndNameDTO[]> => {
    return this.http.get<IdAndNameDTO[]>(`${environment.serverURL}/user/startingWith`, { params: {
      name: str
    }})
  }
}
