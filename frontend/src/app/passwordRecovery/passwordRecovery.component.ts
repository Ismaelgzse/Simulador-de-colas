import {Component, ElementRef, OnInit, ViewChild} from "@angular/core";
import {LogInService} from "../logIn/logIn.service";
import {PasswordRecoveryService} from "./passwordRecovery.service";
import {Router} from "@angular/router";
import {flip} from "@popperjs/core";

export class PasswordFormDTO {
  nickname: String;
  securityQuestion: String;
  securityAnswer: String;
  password: String;
}

@Component({
  selector: 'app-passwordRecovery',
  templateUrl: './passwordRecovery.component.html',
  styleUrls: ['../../assets/css/loginAndRegistration.css', '../../assets/vendor/fontawesome-free-6.4.0-web/css/all.css'],
  providers: [PasswordRecoveryService]
})

export class PasswordRecoveryComponent implements OnInit {
  formNum: number;
  error: number;
  passwordRecoveryForm: PasswordFormDTO;
  preguntaseguridadOpciones: String[];
  visibilidadRespuesta: boolean;
  visibilidadPassword1: boolean;
  visibilidadPassword2: boolean;
  passwordConfirmation: String;
  wasValidated: boolean;
  @ViewChild('form') passwordRecoveryFormElement: ElementRef;
  loading: boolean;


  constructor(private passwordRecoveryService: PasswordRecoveryService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.error = 0;
    this.formNum = 1;
    this.passwordRecoveryForm = {
      nickname: '',
      securityAnswer: '',
      securityQuestion: '',
      password: ''
    }
    this.passwordConfirmation = ''
    this.preguntaseguridadOpciones = [
      "Nombre de tu mascota",
      "Nombre de tu comida favorita",
      "Año nacimiento de tu padre",
      "Año nacimiento de tu madre",
      "Serie favorita",
      "Pelicula favorita"
    ];

    this.visibilidadRespuesta = true;
    this.wasValidated = false;
    this.visibilidadPassword1 = true;
    this.visibilidadPassword2 = true;
    this.loading = false;

  }

  changeVisibilityAnswer(): void {
    this.visibilidadRespuesta = !this.visibilidadRespuesta
  }

  changeVisibilityPassword1(): void {
    this.visibilidadPassword1 = !this.visibilidadPassword1
  }

  changeVisibilityPassword2(): void {
    this.visibilidadPassword2 = !this.visibilidadPassword2
  }

  changeForm(even: Event): void {
    even.preventDefault();
    //Check the validity of the format of the form fields
    if (this.passwordRecoveryFormElement.nativeElement.checkValidity()) {
      this.loading = true;
      //If the user, the security question and the security answer are correct we change to the second form
      this.passwordRecoveryService.checkUser(this.passwordRecoveryForm).subscribe({
        next: (success: boolean) => {
          if (success) {
            this.formNum = 2;
            this.loading = false;
            this.error = 0;
            this.wasValidated = false;
          } else {
            this.error = 1;
            this.loading = false;
          }
        },
        error: (err) => {
          this.router.navigate(['/error500'])
        }
      })
    } else {
      this.wasValidated = true;
      this.loading = false;
    }
  }

  //The second form
  confirmPassword(event: Event): void {
    event.preventDefault();
    this.wasValidated = false;
    //If the format of the new password is correct, we change the old password of the user
    if (this.passwordRecoveryForm.password === this.passwordConfirmation) {
      if (this.passwordRecoveryFormElement.nativeElement.checkValidity()) {
        this.loading = true;
        this.passwordRecoveryService.changePassword(this.passwordRecoveryForm).subscribe({
          next: (success) => {
            this.router.navigate(["login"])
          },
          error: (err) => {
            this.loading = false;
            this.error = 0;
            this.router.navigate(['/error500'])
          }
        })
      } else {
        this.wasValidated = true;
      }
    } else {
      this.error = 2;
    }

  }


}
