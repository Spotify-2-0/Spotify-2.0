import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { MainComponent } from './sites/main/main.component';
import { IconComponent } from './components/icon/icon.component';
import { HttpClientModule } from "@angular/common/http";
import { AppComponent } from "./app.component";
import { WorldMapComponent } from './components/world-map/world-map.component';
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { SignupPageComponent } from './sites/signup-page/signup-page.component';
import { CheckboxComponent } from './components/checkbox/checkbox.component';
import { FormInputTextComponent } from "./components/form-input-text/form-input-text.component";
import { ReactiveFormsModule } from "@angular/forms";
import { LoadingBarRouterModule } from "@ngx-loading-bar/router";
import { LoadingBarHttpClientModule } from "@ngx-loading-bar/http-client";
import { AvatarCropperComponent } from './components/avatar-cropper/avatar-cropper.component';
import { SigninPageComponent } from './sites/signin-page/signin-page.component';
import { StepperComponent } from './components/stepper/stepper.component';
import { StepEmailValidationComponent } from './components/setup/step-email-validation/step-email-validation.component';
import { StepUserProfileComponent } from './components/setup/step-user-profile/step-user-profile.component';
import { StepUserInterestsComponent } from './components/setup/step-user-interests/step-user-interests.component';
import { SetupPageComponent } from './sites/setup-page/setup-page.component';
import { StepFinalComponent } from './components/setup/step-final/step-final.component';

@NgModule({
  declarations: [
    AppComponent,
    MainComponent,
    IconComponent,
    WorldMapComponent,
    SignupPageComponent,
    SigninPageComponent,
    FormInputTextComponent,
    CheckboxComponent,
    AvatarCropperComponent,
    StepperComponent,
    StepEmailValidationComponent,
    StepUserProfileComponent,
    StepUserInterestsComponent,
    SetupPageComponent,
    StepFinalComponent,
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
  bootstrap: [AppComponent]
})
export class AppModule { }
