import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {PasswordFormDTO} from "./passwordRecovery.component";
import {User} from "../user/user.model";

@Injectable()
export class PasswordRecoveryService{
  constructor(private httpClient: HttpClient) {
  }

  checkUser(user: PasswordFormDTO):Observable<boolean>{
    return this.httpClient.post('/api/forgottenPassword',user) as Observable<boolean>;
  }

  changePassword(user:PasswordFormDTO):Observable<User>{
    return this.httpClient.put('/api/forgottenPassword',user) as Observable<User>;
  }


}
