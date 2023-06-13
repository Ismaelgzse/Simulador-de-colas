import {Component, OnInit} from "@angular/core";
import {HomeService} from "../../home/home.service";

@Component({
  templateUrl: 'error403.component.html',
  styleUrls: ['../../../assets/css/home.css','../../../assets/css/errors.css', '../../../assets/vendor/fontawesome-free-6.4.0-web/css/all.css'],
  providers:[HomeService]
})

export class Error403Component implements OnInit{
  logged:boolean

  constructor(private homeService:HomeService) {
  }

  ngOnInit(): void {
    this.homeService.getFolders().subscribe(
      (success=>{
        this.logged=true;
      }),
      (error => this.logged=false)
    )
  }


}


