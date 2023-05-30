package es.tfg.simuladorteoriacolas.security.auth;

import es.tfg.simuladorteoriacolas.security.services.UserDetailsServiceImpl;
import es.tfg.simuladorteoriacolas.user.Role;
import es.tfg.simuladorteoriacolas.user.User;
import es.tfg.simuladorteoriacolas.user.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/newUser")
    public ResponseEntity<User> register (@RequestBody RegisterRequest request){
        if (userService.findByNickname(request.getNickname()).isPresent()){
            return ResponseEntity.badRequest().build();
        }
        var user= new User();
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setSecurityQuestion(request.getSecurityQuestion());
        user.setSecurityAnswer(request.getSecurityAnswer());
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        var savedUser=userService.save(user);

        return ResponseEntity.created(fromCurrentRequest().path("/{id}").buildAndExpand(user.getNickname()).toUri()).body(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Parameter(description = "Access token.") @CookieValue(name = "accessToken", required = false) String accessToken,
            @Parameter(description = "Refresh token.") @CookieValue(name = "refreshToken", required = false) String refreshToken,
            @Parameter(description = "Login request.") @RequestBody LoginRequest loginRequest) {

        return authenticationService.login(loginRequest, accessToken, refreshToken);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            @Parameter(description = "Refresh token.") @CookieValue(name = "refreshToken", required = false) String refreshToken) throws IOException {
        return authenticationService.refreshToken(refreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<AuthenticationResponse> logOut(@Parameter(description = "HTTP Servlet Request.") HttpServletRequest request,
                                               @Parameter(description = "HTTP Servlet Response.") HttpServletResponse response) {
        return ResponseEntity.ok(new AuthenticationResponse(AuthenticationResponse.Status.SUCCESS, authenticationService.logout(request, response)));
    }

}
