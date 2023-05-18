package es.tfg.simuladorteoriacolas.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.tfg.simuladorteoriacolas.security.jwt.JwtService;
import es.tfg.simuladorteoriacolas.user.Role;
import es.tfg.simuladorteoriacolas.user.User;
import es.tfg.simuladorteoriacolas.user.UserRepository;
import es.tfg.simuladorteoriacolas.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request){
        User user= new User();
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userService.save(user);

        var jtwToken= jwtService.generateToken(user);

        return new AuthenticationResponse(jtwToken);

    }

    public AuthenticationResponse login(LoginRequest request){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getNickname(),
                        request.getPassword()
                )
        );
        var user = userService.findByNickname(request.getNickname()).orElseThrow();
        
        var jtwToken= jwtService.generateToken(user);

        return new AuthenticationResponse(jtwToken);
    }
}
