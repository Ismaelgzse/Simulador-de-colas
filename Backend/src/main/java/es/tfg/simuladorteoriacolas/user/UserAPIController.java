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

    @GetMapping("/forgottenPassword")
    public ResponseEntity<Boolean> passwordRecoveryStep1(@RequestBody PasswordDTO passwordDTO){
        Boolean match=userService.matchingNickQuestionAnswer(passwordDTO.getNickname(),passwordDTO.getSecurityQuestion(),passwordDTO.getSecurityAnswer());
        if (match){
            return ResponseEntity.ok(match);
        }
        return ResponseEntity.badRequest().build();
    }

    @PutMapping("/forgottenPassword")
    public ResponseEntity<User> passwordRecoveryStep2(@RequestBody PasswordDTO passwordDTO){
        Boolean match=userService.matchingNickQuestionAnswer(passwordDTO.getNickname(),passwordDTO.getSecurityQuestion(),passwordDTO.getSecurityAnswer());
        if (match){
            User user= userService.findByNickname(passwordDTO.getNickname()).get();
            user.setPassword(passwordEncoder.encode(passwordDTO.getPassword()));
            userService.save(user);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.badRequest().build();
    }
}
