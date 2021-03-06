import { APP_INITIALIZER, NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoggedPageComponent } from './sites/logged-page/logged-page.component';
import { HomeComponent } from './sites/main/home.component';
import { SigninPageComponent } from './sites/signin-page/signin-page.component';
import { SignupPageComponent } from './sites/signup-page/signup-page.component';
import { SetupPageComponent } from './sites/setup-page/setup-page.component';
import { NotFoundComponent } from './sites/not-found/not-found.component';
import { UserService } from './services/user.service';
import { AuthGuard } from './guards/auth.guard';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { TokenInterceptor } from './interceptors/token.interceptor';
import { EmailConfirmedGuard } from './guards/email-confirmed.guard';
import { JwtService } from './services/jwt.service';
import { NegateAuthGuard } from './guards/negate-auth.guard';
import { NegateEmailConfirmedGuard } from './guards/negate-email-confirmed.guard';
import { PasswordResetComponent } from './sites/password-reset/password-reset.component';
import { AccountSettingsComponent } from './sites/logged-page/settings/account-settings/account-settings.component';
import { SettingsComponent } from './sites/logged-page/settings/settings.component';
import { ChangePasswordComponent } from './sites/logged-page/settings/change-password/change-password.component';
import { ActivityLogComponent } from './sites/logged-page/settings/activity-log/activity-log.component';
import { CollectionsComponent } from './sites/logged-page/collections/collections.component';
import { SingleCollectionComponent } from './sites/logged-page/collections/single-collection/single-collection.component';

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

const userInitializer = (
  userService: UserService,
  jwtService: JwtService
): (() => Promise<void>) => {
  if (!localStorage.getItem('access_token')) {
    return () => Promise.resolve();
  }

  if (jwtService.isTokenExpired(localStorage.getItem('access_token')!)) {
    userService.logout();
    return () => Promise.resolve();
  }

  return () =>
    userService
      .me()
      .toPromise()
      .then((result) => console.log(result))
      .catch((error) => console.log(error));
};

const routes: Routes = [
  {
    path: '',
    canActivate: [NegateAuthGuard],
    data: { authRouteTo: '/app' },
    component: HomeComponent,
  },
  {
    path: 'signup',
    canActivate: [NegateAuthGuard],
    data: { authRouteTo: '/app' },
    component: SignupPageComponent,
  },
  {
    path: 'signin',
    canActivate: [NegateAuthGuard],
    data: { authRouteTo: '/app' },
    component: SigninPageComponent,
  },
  {
    path: 'setup',
    canActivate: [AuthGuard, NegateEmailConfirmedGuard],
    data: {
      authRouteTo: '/',
      emailRouteTo: '/app',
    },
    component: SetupPageComponent,
  },
  {
    path: 'password-reset',
    canActivate: [NegateAuthGuard],
    data: {
      authRouteTo: '/app',
    },
    component: PasswordResetComponent,
  },
  {
    path: 'app',
    canActivate: [AuthGuard, EmailConfirmedGuard],
    data: {
      authRouteTo: '/app',
      emailRouteTo: '/setup',
    },
    children: [
      {
        path: 'collections',
        canActivate: [AuthGuard, EmailConfirmedGuard],
        data: {
          authRouteTo: '/app',
          emailRouteTo: '/setup',
        },
        component: CollectionsComponent,
      },
      {
        path: 'collection/:id',
        canActivate: [AuthGuard, EmailConfirmedGuard],
        data: {
          authRouteTo: '/app',
          emailRouteTo: '/setup',
        },
        component: SingleCollectionComponent,
      },
      {
        path: 'settings',
        canActivate: [AuthGuard, EmailConfirmedGuard],
        data: {
          authRouteTo: '/app',
          emailRouteTo: '/setup',
        },
        component: SettingsComponent,
        children: [
          {
            path: 'account-settings',
            canActivate: [AuthGuard, EmailConfirmedGuard],
            data: {
              authRouteTo: '/app',
              emailRouteTo: '/setup',
            },
            component: AccountSettingsComponent,
          },
          {
            path: 'change-password',
            canActivate: [AuthGuard, EmailConfirmedGuard],
            data: {
              authRouteTo: '/app',
              emailRouteTo: '/setup',
            },
            component: ChangePasswordComponent,
          },
          //TODO: Notification settings
          {
            path: 'activity-log',
            canActivate: [AuthGuard, EmailConfirmedGuard],
            data: {
              authRouteTo: '/app',
              emailRouteTo: '/setup',
            },
            component: ActivityLogComponent,
          }
        ],
      },
    ],
    component: LoggedPageComponent,
  },

  {
    path: '**',
    pathMatch: 'full',
    component: NotFoundComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [
    AuthGuard,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true,
    },
    {
      provide: APP_INITIALIZER,
      useFactory: initializer,
      multi: true,
    },
    {
      provide: APP_INITIALIZER,
      useFactory: userInitializer,
      deps: [UserService, JwtService],
      multi: true,
    },
  ],
})
export class AppRoutingModule {}
