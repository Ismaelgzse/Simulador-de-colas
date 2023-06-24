import { BrowserModule } from '@angular/platform-browser';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import {SignUpComponent} from "./signUp/signUp.component";
import {CommonModule} from "@angular/common";
import {HttpClientModule} from "@angular/common/http";
import {NgModule} from "@angular/core";
import {LogInComponent} from "./logIn/logIn.component";
import {HomeComponent} from "./home/home.component";
import {PasswordRecoveryComponent} from "./passwordRecovery/passwordRecovery.component";
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import {Error403Component} from "./errors/error403/error403.component";
import {Error404Component} from "./errors/error404/error404.component";
import {Error500Component} from "./errors/error500/error500.component";
import {HeaderComponent} from "./header/header.component";
import {SimulationComponent} from "./simulation/simulation.component";


@NgModule({
  declarations: [
    AppComponent,
    SignUpComponent,
    LogInComponent,
    HomeComponent,
    PasswordRecoveryComponent,
    Error403Component,
    Error404Component,
    Error500Component,
    HeaderComponent,
    SimulationComponent
  ],
  imports: [
    BrowserModule,
    CommonModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    NgbModule,
    ReactiveFormsModule
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
