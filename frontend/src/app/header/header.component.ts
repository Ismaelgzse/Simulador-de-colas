import {Component} from "@angular/core";
import {NavigationEnd, Router} from "@angular/router";
import {HomeService} from "../home/home.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['../../assets/css/home.css', '../../assets/css/loginAndRegistration.css', '../../assets/vendor/fontawesome-free-6.4.0-web/css/all.css'],
  providers: [HomeService]
})

export class HeaderComponent {


  header: number;

  constructor(public router: Router, private homeService: HomeService) {
    if (router.url === '\/logout') {
      this.header = 1
    } else {
      router.events.subscribe(
        (event) => {
          if (event instanceof NavigationEnd) {
            this.changeHeader();
          }
        }
      )
    }
  }

  changeHeader() {
    //If the user is logged in, we decide the information of the header
    this.homeService.isAuthenticated().subscribe({
        next: (isAuthenticated: boolean) => {
          this.header = isAuthenticated ? 0 : 1;
        },
        error: (err => this.header = 1)
      }
    )
  }
}
