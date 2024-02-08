package es.tfg.simuladorteoriacolas.security.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class AuthenticationResponse {

    private Status status;
    private String message;
    private String error;

    public enum Status {
        SUCCESS, FAILURE
    }

    public AuthenticationResponse() {
    }

    public AuthenticationResponse(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public AuthenticationResponse(Status status, String message, String error) {
        this.status = status;
        this.message = message;
        this.error = error;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "LoginResponse [status=" + status + ", message=" + message + ", error=" + error + "]";
    }

}
