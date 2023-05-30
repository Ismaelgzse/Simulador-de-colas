export class User {
  private _nickname: string;
  private _email:string;
  private _password:string;
  private _securityQuestion: string;
  private _securityAnswer: string;


  get nickname() : string {
    return this._nickname;
  }

  set nickname(nickname: string) {
    this._nickname = nickname
  }

  get email() : string {
    return this._email;
  }

  set email(email: string) {
    this._email = email
  }

  get password() : string {
    return this._password;
  }

  set password(password: string) {
    this._password = password
  }

  get securityQuestion() : string {
    return this._securityQuestion;
  }

  set securityQuestion(securityQuestion: string) {
    this._securityQuestion = securityQuestion
  }

  get securityAnswer() : string {
    return this._securityAnswer;
  }

  set securityAnswer(securityAnswer: string) {
    this._securityAnswer = securityAnswer
  }


}
