import { NgModule } from '@angular/core';
import {ExtraOptions, RouterModule, Routes} from '@angular/router';
import {SignUpComponent} from "./signUp/signUp.component";
import {LogInComponent} from "./logIn/logIn.component";
import {HomeComponent} from "./home/home.component";
import {PasswordRecoveryComponent} from "./passwordRecovery/passwordRecovery.component";
import {Error403Component} from "./errors/error403/error403.component";
import {Error404Component} from "./errors/error404/error404.component";
import {Error500Component} from "./errors/error500/error500.component";
import {SimulationComponent} from "./simulation/simulation.component";

const routes: Routes = [
  {path: '',redirectTo:'login',pathMatch:'full'},
  {path: 'sign-up', component: SignUpComponent, data: {title: 'Registrarse'}},
  {path: 'login', component: LogInComponent, data: {title: 'Iniciar sesion'}},
  {path: 'home', component: HomeComponent, data: {title: 'Inicio'}},
  {path: 'forgottenPassword', component: PasswordRecoveryComponent, data: {title: 'Recuperación de contraseña'}},
  {path: 'error403', component: Error403Component, data: {title: 'Error 403'}},
  {path: 'logout' , component:LogInComponent, data: {title: 'Cierre de sesión'}},
  {path: 'error500', component: Error500Component, data: {title: 'Error 500'}},
  {path: 'simulation/:id', component:SimulationComponent,data:{title: 'Simulación'}},
  {path: '**', component: Error404Component, data: {title: 'Error 404'}}

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
