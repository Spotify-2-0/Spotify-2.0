import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class JwtService {

  parseJwt(token: string) {
    const _decodeToken = (token: string) => {
      try {
        return JSON.parse(atob(token));
      } catch {
        return;
      }
    };

    return token
      .split('.')
      .map(token => _decodeToken(token))
      .reduce((acc, curr) => {
          if (!!curr) acc = { ...acc, ...curr };
          return acc;
        }, Object.create(null));
  }

  public isTokenExpired = (token: string) => {
    const jwt = this.parseJwt(token);
    return jwt['exp'] * 1000 < Date.now()
  }
}
