package es.tfg.simuladorteoriacolas.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public UserEntity save(UserEntity user) {
        return userRepository.save(user);
    }

    public UserEntity save(String nickname, String email, String securityQuestion, String securityAnswer, String password) {
        var user = new UserEntity();
        user.setNickname(nickname);
        user.setEmail(email);
        user.setSecurityQuestion(securityQuestion);
        user.setSecurityAnswer(securityAnswer);
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public Boolean matchingNickQuestionAnswer(String nickname, String securityQuestion, String securityAnswer) {
        Optional<UserEntity> user = userRepository.findByNickname(nickname);
        if (user.isPresent()) {
            if (user.get().getSecurityAnswer().equals(securityAnswer) && user.get().getSecurityQuestion().equals(securityQuestion)) {
                return true;
            }
        }
        return false;
    }

    public Boolean existUser(String nickname) {
        Optional<UserEntity> user = userRepository.findByNickname(nickname);
        if (user.isPresent()){
            return true;
        }
        return false;
    }

    public Optional<UserEntity> findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }
}
