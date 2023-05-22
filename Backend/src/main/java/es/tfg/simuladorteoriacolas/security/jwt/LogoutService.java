package es.tfg.simuladorteoriacolas.security.jwt;

import es.tfg.simuladorteoriacolas.token.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        //Extract Jwt from the header of the request
        final String jwtHeader = request.getHeader("Authorization");
        //Check there is a jwt token
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer ")){
            return;
        }
        final String jwt= jwtHeader.substring(7);
        var token= tokenRepository.findByToken(jwt).orElseThrow();
        if (token != null){
            token.setExpired(true);
            token.setRevoked(true);
            tokenRepository.save(token);
            SecurityContextHolder.clearContext();
        }
    }
}
