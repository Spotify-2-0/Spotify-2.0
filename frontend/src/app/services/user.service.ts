import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import {
  ConfirmEmailResponse, SignInRequest, SignResponse,
  SignUpRequest,
  User,
  UserExistsByResponse
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

  public updateFirstName(newFirstName: string) {
    //TODO: connect to backend
    this.http.patch(`${environment.serverURL}/user/<template>`, { firstName: newFirstName }).subscribe();
  }

  public updateLastName(newLastName: string) {
    //TODO: connect to backend
    this.http.patch(`${environment.serverURL}/user/<template>`, { lastName: newLastName }).subscribe();
  }

  public updateDispalyName(newDisplayName: string) {
    //TODO: connect to backend
    this.http.patch(`${environment.serverURL}/user/<template>`, { displayName: newDisplayName }).subscribe();
  }

  public updateEmail(newEmail: string) {
    //TODO: connect to backend
    this.http.patch(`${environment.serverURL}/user/<template>`, { email: newEmail }).subscribe();
  }

}
