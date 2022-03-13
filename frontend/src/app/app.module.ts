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

@NgModule({
  declarations: [
    AppComponent,
    MainComponent,
    IconComponent,
    WorldMapComponent,
    SignupPageComponent,
    FormInputTextComponent,
    CheckboxComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule,
    ReactiveFormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
