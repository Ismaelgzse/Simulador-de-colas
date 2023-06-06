package es.tfg.simuladorteoriacolas.user;

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

    @PostMapping("/forgottenPassword")
    public Boolean passwordRecoveryStep1(@RequestBody PasswordDTO passwordDTO){
        return userService.matchingNickQuestionAnswer(passwordDTO.getNickname(),passwordDTO.getSecurityQuestion(),passwordDTO.getSecurityAnswer());

    }

    @PutMapping("/forgottenPassword")
    public ResponseEntity<UserEntity> passwordRecoveryStep2(@RequestBody PasswordDTO passwordDTO){
        Boolean match=userService.matchingNickQuestionAnswer(passwordDTO.getNickname(),passwordDTO.getSecurityQuestion(),passwordDTO.getSecurityAnswer());
        if (match){
            UserEntity user= userService.findByNickname(passwordDTO.getNickname()).get();
            user.setPassword(passwordEncoder.encode(passwordDTO.getPassword()));
            userService.save(user);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/existingUser")
    public Boolean checkIfUserExist(@RequestParam String nickname){
        return userService.existUser(nickname);
    }
}
