package es.tfg.simuladorteoriacolas.security.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.tfg.simuladorteoriacolas.security.jwt.JwtService;
import es.tfg.simuladorteoriacolas.token.Token;
import es.tfg.simuladorteoriacolas.token.TokenRepository;
import es.tfg.simuladorteoriacolas.token.TokenType;
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

    @Autowired
    private TokenRepository tokenRepository;

    public AuthenticationResponse register(RegisterRequest request){
        User user= new User();
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        var userSaved=userService.save(user);

        var jtwToken= jwtService.generateToken(user);
        saveUserToken(userSaved, jtwToken);

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
        revokeUserTokens(user);
        saveUserToken(user,jtwToken);


        return new AuthenticationResponse(jtwToken);
    }

    private void revokeUserTokens(User user){
        var validTokens= tokenRepository.findAllValidTokens(user.getId());
        if (!validTokens.isEmpty()){
            validTokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });
            tokenRepository.saveAll(validTokens);
        }
    }

    private void saveUserToken(User user, String jtwToken) {
        var token= Token.builder().user(user)
                .token(jtwToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }
}
