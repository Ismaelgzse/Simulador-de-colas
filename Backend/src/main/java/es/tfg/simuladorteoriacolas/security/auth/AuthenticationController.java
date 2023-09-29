package es.tfg.simuladorteoriacolas.security.auth;

import es.tfg.simuladorteoriacolas.items.ItemDTO;
import es.tfg.simuladorteoriacolas.user.UserEntity;
import es.tfg.simuladorteoriacolas.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    /**
     * Register a new user
     *
     * @param request Register request.
     * @return {@code True} A new user is registered.
     */
    @Operation(summary = "Register a new user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User entity.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserEntity.class))}),
            @ApiResponse(responseCode = "500", description = "Error occurred while creating a user",
                    content = @Content)
    })
    @PostMapping("/newUser")
    public ResponseEntity<UserEntity> register (@Parameter(description = "Register request") @RequestBody RegisterRequest request){
        if (userService.findByNickname(request.getNickname()).isPresent()){
            return ResponseEntity.badRequest().build();
        }
        var savedUser=userService.save(request.getNickname(),request.getEmail(),request.getSecurityQuestion(),request.getSecurityAnswer(),request.getPassword());
        return ResponseEntity.created(fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getNickname()).toUri()).body(savedUser);
    }

    /**
     * User login to the application.
     *
     * @param accessToken Access token.
     * @param refreshToken Refresh token.
     * @param loginRequest Login request.
     * @return {@code True} Auth response.
     */
    @Operation(summary = "User login to the application.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication response.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Error occurred while logging in a user",
                    content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Parameter(description = "Access token.") @CookieValue(name = "accessToken", required = false) String accessToken,
            @Parameter(description = "Refresh token.") @CookieValue(name = "refreshToken", required = false) String refreshToken,
            @Parameter(description = "Login request.") @RequestBody LoginRequest loginRequest) {

        return authenticationService.login(loginRequest, accessToken, refreshToken);
    }

    /**
     * Refresh a token
     *
     * @param refreshToken Refresh token
     * @return {@code True} Auth response.
     * @throws IOException
     */
    @Operation(summary = "Refresh a token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication response.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Error occurred while refreshing a token",
                    content = @Content)
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(
            @Parameter(description = "Refresh token.") @CookieValue(name = "refreshToken", required = false) String refreshToken) throws IOException {
        return authenticationService.refreshToken(refreshToken);
    }

    /**
     * Logout from the application
     *
     * @param request Http servlet information.
     * @param response Http servlet response
     * @return {@code True} Auth response.
     */
    @Operation(summary = "Logout from the application.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication response.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = AuthenticationResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Error occurred while logging out a user",
                    content = @Content)
    })
    @PostMapping("/logout")
    public ResponseEntity<AuthenticationResponse> logOut(@Parameter(description = "HTTP Servlet Request.") HttpServletRequest request,
                                               @Parameter(description = "HTTP Servlet Response.") HttpServletResponse response) {
        return ResponseEntity.ok(new AuthenticationResponse(AuthenticationResponse.Status.SUCCESS, authenticationService.logout(request, response)));
    }

}
