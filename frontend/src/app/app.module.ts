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
import { SigninPageComponent } from './sites/signin-page/signin-page.component';
import { LoggedPageComponent } from './sites/logged-page/logged-page.component';
import { SidebarComponent } from './sites/logged-page/sidebar/sidebar.component';
import { PlayerComponent } from './sites/logged-page/player/player.component';

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
    LoggedPageComponent,
    SidebarComponent,
    PlayerComponent,
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
