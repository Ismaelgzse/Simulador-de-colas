import {Component, OnInit} from "@angular/core";
import {HomeService} from "./home.service";

import { ModalDismissReasons, NgbDatepickerModule, NgbModal } from '@ng-bootstrap/ng-bootstrap';


@Component({
  selector:'app-home',
  templateUrl:'./home.component.html',
  styleUrls: ['../../assets/css/home.css', '../../assets/vendor/fontawesome-free-6.4.0-web/css/all.css'],
  providers:[HomeService]
})

export class HomeComponent implements OnInit{


  constructor(private modalService: NgbModal) {
  }

  ngOnInit(): void {
  }

  open(content: any) {
    this.modalService.open(content, {ariaLabelledBy: 'modal-basic-title'})
  }

  save():void{
    this.modalService.dismissAll();
  }

}
