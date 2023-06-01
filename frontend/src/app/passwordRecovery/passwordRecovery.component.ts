import {Component, ElementRef, OnInit, ViewChild} from "@angular/core";
import {LogInService} from "../logIn/logIn.service";
import {PasswordRecoveryService} from "./passwordRecovery.service";
import {Router} from "@angular/router";

export class PasswordFormDTO{
  nickname:String;
  securityQuestion:String;
  securityAnswer:String;
  password:String;
}

@Component({
  selector:'app-passwordRecovery',
  templateUrl:'./passwordRecovery.component.html',
  styleUrls: ['../../assets/css/loginAndRegistration.css', '../../assets/vendor/fontawesome-free-6.4.0-web/css/all.css'],
  providers: [PasswordRecoveryService]
})

export class PasswordRecoveryComponent implements OnInit{
  formNum:number;
  passwordRecoveryForm:PasswordFormDTO;
  preguntaseguridadOpciones: String[];
  visibilidadRespuesta:boolean;
  visibilidadPassword1:boolean;
  visibilidadPassword2:boolean;
  passwordConfirmation:String;
  wasValidated:boolean;
  @ViewChild('form') passwordRecoveryFormElement: ElementRef;
  loading:boolean;


  constructor(private passwordRecoveryService:PasswordRecoveryService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.formNum=1;
    this.passwordRecoveryForm={
      nickname:'',
      securityAnswer:'',
      securityQuestion:'',
      password:''
    }
    this.passwordConfirmation=''
    this.preguntaseguridadOpciones = [
      "Nombre de tu mascota",
      "Nombre de tu comida favorita",
      "Año nacimiento de tu padre",
      "Año nacimiento de tu madre",
      "Serie favorita",
      "Pelicula favorita"
    ];

    this.visibilidadRespuesta=true;
    this.wasValidated=false;
    this.visibilidadPassword1=true;
    this.visibilidadPassword2=true;
    this.loading=false;

  }

  changeVisibilityAnswer():void{
    this.visibilidadRespuesta=!this.visibilidadRespuesta
  }

  changeVisibilityPassword1():void{
    this.visibilidadPassword1=!this.visibilidadPassword1
  }
  changeVisibilityPassword2():void{
    this.visibilidadPassword2=!this.visibilidadPassword2
  }

  changeForm(even:Event):void{
    even.preventDefault();
    if (this.passwordRecoveryFormElement.nativeElement.checkValidity()){
      this.loading=true;
      this.passwordRecoveryService.checkUser(this.passwordRecoveryForm).subscribe(
        (success =>{
          this.formNum=2;
          this.loading=false;
        }),
        (error => {
          this.wasValidated = false;
        })
      )
    }
  }

  confirmPassword(even:Event):void{
    even.preventDefault();
    if (this.passwordRecoveryForm.password===this.passwordConfirmation){
      if (this.passwordRecoveryFormElement.nativeElement.checkValidity()){
        this.loading=true;
        this.passwordRecoveryService.changePassword(this.passwordRecoveryForm).subscribe(
          (success =>{
            this.router.navigate(["login"])
          }),
          (error => {
            this.wasValidated = false;
          })
        )
      }
    }

  }


}
