import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {

  constructor() { }

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
