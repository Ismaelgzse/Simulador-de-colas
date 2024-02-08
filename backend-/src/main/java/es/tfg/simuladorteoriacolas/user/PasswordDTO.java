package es.tfg.simuladorteoriacolas.user;

public class PasswordDTO {
    private String nickname;

    private String securityQuestion;

    private String securityAnswer;

    private String password;

    public PasswordDTO(String nickname, String securityQuestion, String securityAnswer, String password) {
        this.nickname = nickname;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.password=password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
