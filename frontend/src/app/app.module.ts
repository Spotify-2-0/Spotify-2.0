import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { HomeComponent } from './sites/main/home.component';
import { IconComponent } from './components/icon/icon.component';
import { HttpClientModule } from '@angular/common/http';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { SignupPageComponent } from './sites/signup-page/signup-page.component';
import { CheckboxComponent } from './components/checkbox/checkbox.component';
import { FormInputTextComponent } from './components/form-input-text/form-input-text.component';
import { ReactiveFormsModule } from '@angular/forms';
import { LoadingBarRouterModule } from '@ngx-loading-bar/router';
import { LoadingBarHttpClientModule } from '@ngx-loading-bar/http-client';
import { AvatarCropperComponent } from './components/avatar-cropper/avatar-cropper.component';
import { SigninPageComponent } from './sites/signin-page/signin-page.component';
import { LoggedPageComponent } from './sites/logged-page/logged-page.component';
import { SidebarComponent } from './sites/logged-page/sidebar/sidebar.component';
import { PlayerComponent } from './sites/logged-page/player/player.component';
import { StepperComponent } from './components/stepper/stepper.component';
import { StepEmailValidationComponent } from './components/setup/step-email-validation/step-email-validation.component';
import { StepUserProfileComponent } from './components/setup/step-user-profile/step-user-profile.component';
import { StepUserInterestsComponent } from './components/setup/step-user-interests/step-user-interests.component';
import { SetupPageComponent } from './sites/setup-page/setup-page.component';
import { StepFinalComponent } from './components/setup/step-final/step-final.component';
import { NavigationComponent } from './components/navigation/navigation.component';
import { FooterComponent } from './components/footer/footer.component';
import { NotFoundComponent } from './sites/not-found/not-found.component';
import { PinCodeComponent } from './components/pin-code/pin-code.component';
import { PasswordResetComponent } from './sites/password-reset/password-reset.component';
import { AccountSettingsComponent } from './sites/logged-page/settings/account-settings/account-settings.component';
import { SettingsComponent } from './sites/logged-page/settings/settings.component';
import { ChangePasswordComponent } from './sites/logged-page/settings/change-password/change-password.component';
import { ModalComponent } from './components/modal/modal.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    IconComponent,
    SignupPageComponent,
    SigninPageComponent,
    FormInputTextComponent,
    CheckboxComponent,
    LoggedPageComponent,
    SidebarComponent,
    PlayerComponent,
    AvatarCropperComponent,
    StepperComponent,
    StepEmailValidationComponent,
    StepUserProfileComponent,
    StepUserInterestsComponent,
    SetupPageComponent,
    StepFinalComponent,
    NavigationComponent,
    FooterComponent,
    NotFoundComponent,
    PinCodeComponent,
    PasswordResetComponent,
    SettingsComponent,
    AccountSettingsComponent,
    ChangePasswordComponent,
    ModalComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    LoadingBarRouterModule,
    LoadingBarHttpClientModule,
  ],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
