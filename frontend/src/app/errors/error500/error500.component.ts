import {Component, OnInit} from "@angular/core";
import {LogInService} from "../../logIn/logIn.service";
import {HomeComponent} from "../../home/home.component";
import {HomeService} from "../../home/home.service";

@Component({
  templateUrl: 'error500.component.html',
  styleUrls: ['../../../assets/css/home.css','../../../assets/css/errors.css', '../../../assets/vendor/fontawesome-free-6.4.0-web/css/all.css'],
  providers:[HomeService]
})

export class Error500Component implements OnInit{
  logged:boolean

  constructor(private homeService:HomeService) {
  }

  ngOnInit(): void {
    this.homeService.isAutenticated().subscribe(
      {
        next: (success)=>{
          this.logged=success;
        },
        error: (err)=>{
          this.logged=false;
        }
      }
    )
  }

}
