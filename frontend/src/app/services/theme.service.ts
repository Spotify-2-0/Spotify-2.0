import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {

  constructor() {
    window.addEventListener('storage', (event) => {
      if (event.storageArea === localStorage) {
        if (event.key == 'user_theme' && event.newValue) {
          document.documentElement.setAttribute('data-theme', event.newValue);
        }
      }
    }, false);
  }

  public toggleTheme(): void {
    const theme = localStorage.getItem('user_theme')
    let newTheme = theme === 'light' ? 'dark' : 'light';
    localStorage.setItem('user_theme', newTheme);
    document.documentElement.setAttribute('data-theme', newTheme);
  }

  public setTheme(theme: string): void {
    localStorage.setItem('user_theme', theme);
  }

  public getTheme(): string {
    return localStorage.getItem('user_theme')!;
  }
}
