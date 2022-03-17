import { APP_INITIALIZER, NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MainComponent } from "./sites/main/main.component";
import { SigninPageComponent } from './sites/signin-page/signin-page.component';
import { SignupPageComponent } from "./sites/signup-page/signup-page.component";



const initializer = (): (() => Promise<void>) => {
  let theme = localStorage.getItem('user_theme')
  if (!theme) {
    if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
      theme = 'dark';
    } else {
      theme = 'light';
    }
    localStorage.setItem('user_theme', theme);
  }

  document.documentElement.setAttribute('data-theme', theme);
  return () => Promise.resolve();
};

const routes: Routes = [
  {
    path: '',
    component: MainComponent,
  },
  {
    path: 'signup',
    component: SignupPageComponent,
  },
  {
    path: 'signin',
    component: SigninPageComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializer,
      multi: true,
    },
  ]
})
export class AppRoutingModule { }
