import {Component, OnInit} from "@angular/core";
import {HomeService} from "../../home/home.service";

@Component({
  templateUrl: 'error404.component.html',
  styleUrls: ['../../../assets/css/home.css','../../../assets/css/errors.css', '../../../assets/vendor/fontawesome-free-6.4.0-web/css/all.css'],
  providers:[HomeService]
})

export class Error404Component implements OnInit{
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
