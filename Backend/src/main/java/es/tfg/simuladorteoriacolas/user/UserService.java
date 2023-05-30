package es.tfg.simuladorteoriacolas.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public User save(User user){
        return userRepository.save(user);
    }

    public User save(String nickname,String email, String securityQuestion, String securityAnswer, String password){
        var user= new User();
        user.setNickname(nickname);
        user.setEmail(email);
        user.setSecurityQuestion(securityQuestion);
        user.setSecurityAnswer(securityAnswer);
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    public Boolean matchingNickQuestionAnswer(String nickname, String securityQuestion, String securityAnswer){
        Optional<User> user=userRepository.findByNickname(nickname);
        if (user.isPresent()){
            if (user.get().getSecurityAnswer()==securityAnswer && user.get().getSecurityQuestion()==securityQuestion){
                return true;
            }
        }
        return false;
    }

    public Optional<User> findByNickname(String nickname){
        return userRepository.findByNickname(nickname);
    }
}
