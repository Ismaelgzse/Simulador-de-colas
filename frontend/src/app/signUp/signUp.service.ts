import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {RegistrationForm} from "./signUp.component";
import {Observable} from "rxjs";
import {User} from "../user/user.model";

@Injectable()
export class SignUpService {
   constructor(private httpClient:HttpClient) {
   }

   signUp(user:RegistrationForm) : Observable<User>{
     return this.httpClient.post('/api/newUser',user) as Observable<User>
   }
}
