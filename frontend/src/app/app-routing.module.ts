import { NgModule } from '@angular/core';
import {ExtraOptions, RouterModule, Routes} from '@angular/router';
import {SignUpComponent} from "./signUp/signUp.component";
import {LogInComponent} from "./logIn/logIn.component";
import {HomeComponent} from "./home/home.component";
import {PasswordRecoveryComponent} from "./passwordRecovery/passwordRecovery.component";

const routes: Routes = [
  {path: 'sign-up', component: SignUpComponent, data: {title: 'Registrarse'}},
  {path: 'login', component: LogInComponent, data: {title: 'Iniciar sesion'}},
  {path: 'home', component: HomeComponent, data: {title: 'Inicio'}},
  {path: 'forgottenPassword', component: PasswordRecoveryComponent, data: {title: 'Recuperación de contraseña'}}
];

const routerOptions: ExtraOptions = {
  useHash: false,
  scrollPositionRestoration: 'enabled',
  anchorScrolling: 'enabled'
};

@NgModule({
  imports: [RouterModule.forRoot(routes,routerOptions)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
