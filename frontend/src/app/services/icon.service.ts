import { Injectable } from '@angular/core';
import { HttpClient } from "@angular/common/http";
import { catchError, map } from "rxjs/operators";
import { of } from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class IconService {

  private _registeredIcons: {[key: string]: string | undefined} = {};

  constructor(private readonly http: HttpClient) { }

  requestIcon = (iconName: string) => {
    const icon = this._registeredIcons[iconName];
    if (icon) {
      return of(icon)
    }
    const path = `assets/svgs/${iconName}.svg`
    return this.http.get(path, { responseType: 'text' })
      .pipe(
        catchError(() => of(undefined)),
        map(data => {
          this._registeredIcons[iconName] = data;
          return data;
      }));
  }
}
