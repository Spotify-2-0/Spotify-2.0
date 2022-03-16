import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { MainComponent } from './sites/main/main.component';
import { IconComponent } from './components/icon/icon.component';
import { HttpClientModule } from "@angular/common/http";
import { AppComponent } from "./app.component";
import { WorldMapComponent } from './components/world-map/world-map.component';
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";

@NgModule({
  declarations: [
    AppComponent,
    MainComponent,
    IconComponent,
    WorldMapComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
