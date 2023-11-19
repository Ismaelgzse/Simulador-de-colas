package es.tfg.simuladorteoriacolas.user;

import es.tfg.simuladorteoriacolas.simulation.Simulation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserAPIController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Checks if the password recovery form matches the user data stored in the database.
     *
     * @param passwordDTO The password recovery form.
     * @return {@code True} Boolean with the value of the check.
     */
    @Operation(summary = "Checks if the password recovery form matches the user data stored in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The result of the check of the user.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "500", description = "Error occurred while checking the data.",
                    content = @Content)
    })
    @PostMapping("/forgottenPassword")
    public Boolean passwordRecoveryStep1(@Parameter(description = "The password recovery form") @RequestBody PasswordDTO passwordDTO){
        return userService.matchingNickQuestionAnswer(passwordDTO.getNickname(),passwordDTO.getSecurityQuestion(),passwordDTO.getSecurityAnswer());

    }

    /**
     * Resets the password and saves the updated user.
     *
     * @param passwordDTO The password recovery form.
     * @return {@code True} The user updated.
     */
    @Operation(summary = "Resets the password and saves the updated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The user entity.",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserEntity.class))}),
            @ApiResponse(responseCode = "500", description = "Error occurred while saving the user.",
                    content = @Content)
    })
    @PutMapping("/forgottenPassword")
    public ResponseEntity<UserEntity> passwordRecoveryStep2(@Parameter(description = "The password recovery form") @RequestBody PasswordDTO passwordDTO){
        Boolean match=userService.matchingNickQuestionAnswer(passwordDTO.getNickname(),passwordDTO.getSecurityQuestion(),passwordDTO.getSecurityAnswer());
        if (match){
            UserEntity user= userService.findByNickname(passwordDTO.getNickname()).get();
            user.setPassword(passwordEncoder.encode(passwordDTO.getPassword()));
            userService.save(user);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Checks if the user already exists in the database.
     *
     * @param nickname The nickname of the user.
     * @return {@code True} Boolean with the value of the check.
     */
    @Operation(summary = "Checks if the user already exists in the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The result of the check of the user.",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Boolean.class)))}),
            @ApiResponse(responseCode = "500", description = "Error occurred while checking the user.",
                    content = @Content)
    })
    @GetMapping("/existingUser")
    public Boolean checkIfUserExist(@Parameter(description = "The nickname of the user") @RequestParam String nickname){
        return userService.existUser(nickname);
    }

    /**
     * Checks if the user is authenticated.
     *
     * @param request Http servlet information.
     * @return {@code True} Boolean with the value of the check.
     */
    @Operation(summary = "Checks if the user is authenticated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The result of the check.",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Boolean.class)))}),
            @ApiResponse(responseCode = "403", description = "Not authenticated.",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Error occurred while checking the user.",
                    content = @Content)
    })
    @GetMapping("/isAuthenticated")
    public  ResponseEntity<Boolean> isAuthenticated(@Parameter(description = "Http servlet information") HttpServletRequest request){
        if (request.getUserPrincipal()!=null){
            return ResponseEntity.ok(userService.existUser(request.getUserPrincipal().getName()));
        }
        return ResponseEntity.ok(false);
    }
}
