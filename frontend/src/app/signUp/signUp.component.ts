import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {Router} from "@angular/router";
import {SignUpService} from "./signUp.service";


export interface RegistrationForm {
  nickname: String;
  email: String;
  password: String;
  securityQuestion: String;
  securityAnswer: String;
}


@Component({
  selector: 'app-sign-up',
  templateUrl: './signUp.component.html',
  styleUrls: ['../../assets/css/loginAndRegistration.css', '../../assets/vendor/fontawesome-free-6.4.0-web/css/all.css'],
  providers: [SignUpService]
})

export class SignUpComponent implements OnInit {

  preguntaseguridadOpciones: String[];
  visibilidadPassword: boolean;
  visibilidadRespuesta: boolean;
  @ViewChild('form') signUpFormElement: ElementRef;
  wasValidated: boolean
  loading: boolean
  existUser: boolean

  registrationForm: RegistrationForm;

  constructor(
    private signUpService: SignUpService,
    private router: Router) {

  }

  ngOnInit(): void {

    this.registrationForm = {
      email: '',
      nickname: '',
      password: '',
      securityAnswer: '',
      securityQuestion: ''
    };

    this.preguntaseguridadOpciones = [
      "Nombre de tu mascota",
      "Nombre de tu comida favorita",
      "Año nacimiento de tu padre",
      "Año nacimiento de tu madre",
      "Serie favorita",
      "Pelicula favorita"
    ];

    this.visibilidadPassword = true;

    this.visibilidadRespuesta = true;

    this.loading = false;

    this.wasValidated = false;

    this.existUser = false
  }

  signUp(event: Event): void {
    event.preventDefault();
    //Check the validity of the format of the form fields
    if (this.signUpFormElement.nativeElement.checkValidity()) {
      //Check if the user exists, if it does, we show an error
      this.signUpService.checkIfExistUser(this.registrationForm.nickname).subscribe({
        next: (success: boolean) => {
          if (success) {
            this.existUser = true;
          } else {
            //If doesn't, we registrate the user
            this.existUser = false
            this.loading = true;
            this.signUpService.signUp(this.registrationForm).subscribe({
              next: (success) => {
                this.router.navigate(["login"])
              },
              error: (err) => {
                this.loading = false;
              }
            })
          }
        }
      })
    } else {
      this.wasValidated = true;
    }

  }

  changeVisibilityPassword(): void {
    this.visibilidadPassword = !this.visibilidadPassword
  }

  changeVisibilityAnswer(): void {
    this.visibilidadRespuesta = !this.visibilidadRespuesta
  }

}
