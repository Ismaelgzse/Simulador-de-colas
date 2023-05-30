import {Component, ElementRef, Injectable, OnInit, ViewChild} from "@angular/core";
import {LogInService} from "./logIn.service";
import {Router} from "@angular/router";

export class LoginForm {
  nickname:String;
  password:String
}

@Component({
  selector:'app-login',
  templateUrl:'./logIn.component.html',
  styleUrls: ['../../assets/css/loginAndRegistration.css', '../../assets/vendor/fontawesome-free-6.4.0-web/css/all.css'],
  providers: [LogInService]
})

export class LogInComponent implements OnInit{

  wasValidated:boolean;
  @ViewChild('form') logInFormElement: ElementRef;
  logInForm:LoginForm;
  loading:boolean
  visibilityPassword:boolean


  constructor(private router:Router,private logInService:LogInService) {
  }

  ngOnInit(): void {
    this.loading=false
    this.wasValidated=false;
    this.logInForm={
      nickname:'',
      password:''
    }
    this.visibilityPassword=true
  }

  logIn(event:Event):void{
    event.preventDefault()
    if (this.logInFormElement.nativeElement.checkValidity()){
      this.loading=true;
      this.logInService.logIn(this.logInForm.nickname,this.logInForm.password).subscribe(
        (success =>{
          this.router.navigate(['home'])
        }),
        (error => {
          this.loading=false;
          //hacer control de errores
        })
      )
    }else {
      this.logInForm.nickname="jj";
    }
  }

  changeVisibility():void{
    this.visibilityPassword=!this.visibilityPassword
  }

}
