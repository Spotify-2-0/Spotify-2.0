import { APP_INITIALIZER, NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoggedPageComponent } from './sites/logged-page/logged-page.component';
import { HomeComponent } from "./sites/main/home.component";
import { SigninPageComponent } from './sites/signin-page/signin-page.component';
import { SignupPageComponent } from './sites/signup-page/signup-page.component';
import { SetupPageComponent } from './sites/setup-page/setup-page.component';

const initializer = (): (() => Promise<void>) => {
  let theme = localStorage.getItem('user_theme');
  if (!theme) {
    if (
      window.matchMedia &&
      window.matchMedia('(prefers-color-scheme: dark)').matches
    ) {
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
    component: HomeComponent,
  },
  {
    path: 'signup',
    component: SignupPageComponent,
  },
  {
    path: 'setup',
    component: SetupPageComponent,
  },
  {
    path: 'signin',
    component: SigninPageComponent
  },
  {
    path: 'logged',
    component: LoggedPageComponent
  }
]

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: initializer,
      multi: true,
    },
  ],
})
export class AppRoutingModule {}
