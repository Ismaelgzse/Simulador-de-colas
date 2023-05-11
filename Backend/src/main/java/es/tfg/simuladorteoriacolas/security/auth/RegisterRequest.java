package es.tfg.simuladorteoriacolas.security.auth;


import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {

    private String email;

    @NotBlank
    private String nickname;

    @NotBlank
    private String password;

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
