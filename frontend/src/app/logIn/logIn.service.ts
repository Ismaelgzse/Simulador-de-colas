import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable()
export class LogInService {
  constructor(private httpClient: HttpClient) {
  }

  logIn(nickname: String, password: String): Observable<boolean> {
    return this.httpClient.post('/api/login', {nickname, password}, {withCredentials: true}) as Observable<boolean>;
  }

}
