package es.tfg.simuladorteoriacolas.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;


    public User save(User user){
        return userRepository.save(user);
    }

    public Optional<User> findByNickname(String nickname){
        return userRepository.findByNickname(nickname);
    }
}
