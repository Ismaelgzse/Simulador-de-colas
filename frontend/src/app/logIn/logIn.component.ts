import {AfterViewInit, Component, ElementRef, Injectable, OnInit, ViewChild} from "@angular/core";
import {LogInService} from "./logIn.service";
import {Router} from "@angular/router";
import {HomeService} from "../home/home.service";


export class LoginForm {
  nickname: String;
  password: String
}

@Component({
  selector: 'app-login',
  templateUrl: './logIn.component.html',
  styleUrls: ['../../assets/css/loginAndRegistration.css', '../../assets/vendor/fontawesome-free-6.4.0-web/css/all.css'],
  providers: [LogInService, HomeService]
})

export class LogInComponent implements OnInit {

  wasValidated: boolean;
  @ViewChild('form') logInFormElement: ElementRef;
  logInForm: LoginForm;
  loading: boolean;
  visibilityPassword: boolean;
  error: boolean;
  logOut: boolean


  constructor(private router: Router, private logInService: LogInService, private homeService: HomeService) {
    //If the url is Logout, we show a success message
    if (this.router.url === '/logout') {
      this.logInService.logOut().subscribe({
          next: (success) => {
            this.logOut = true;
          }
        }
      )
      //If is autheticated, the user is redirected to the home screen
    } else {
      this.homeService.isAuthenticated().subscribe({
          next: (isAuthenticated) => {
            if (isAuthenticated) {
              this.router.navigate(['home'])
            }
          }
        }
      )
    }
  }

  ngOnInit(): void {
    this.logOut = false;
    this.loading = false
    this.wasValidated = false;
    this.logInForm = {
      nickname: '',
      password: ''
    }
    this.visibilityPassword = true;
    this.error = false;

  }

  logIn(event: Event): void {
    event.preventDefault()
    //Check the validity of the format of the form fields
    if (this.logInFormElement.nativeElement.checkValidity()) {
      this.loading = true;
      this.logInService.logIn(this.logInForm.nickname, this.logInForm.password).subscribe({
        //If is correct the user is redirected to the home screen
          next: (success) => {
            this.router.navigate(['home']);
          },
        //If not, we show the user an error alert
          error: (error) => {
            this.loading = false;
            this.error = true;
          }
        }
      )
    } else {
      this.wasValidated = true;
    }
  }

  changeVisibility(): void {
    this.visibilityPassword = !this.visibilityPassword;
  }


}
