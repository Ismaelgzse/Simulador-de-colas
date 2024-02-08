package es.tfg.simuladorteoriacolas.security.auth;

import es.tfg.simuladorteoriacolas.security.jwt.JwtCookieManager;
import es.tfg.simuladorteoriacolas.security.jwt.JwtService;
import es.tfg.simuladorteoriacolas.security.jwt.SecurityCipher;
import es.tfg.simuladorteoriacolas.token.Token;
import es.tfg.simuladorteoriacolas.user.UserRepository;
import es.tfg.simuladorteoriacolas.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtCookieManager cookieUtil;


    public String logout(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession(false);
        SecurityContextHolder.clearContext();
        session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                cookie.setMaxAge(0);
                cookie.setValue("");
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
        }

        return "logout successfully";
    }

    public ResponseEntity<AuthenticationResponse> login(LoginRequest request,String encryptedAccessToken, String
            encryptedRefreshToken) {
        var authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getNickname(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = SecurityCipher.decrypt(encryptedAccessToken);
        String refreshToken = SecurityCipher.decrypt(encryptedRefreshToken);

        String username = request.getNickname();
        UserDetails user = userDetailsService.loadUserByUsername(username);

        boolean accessTokenValid = jwtService.validateToken(accessToken);
        boolean refreshTokenValid = jwtService.validateToken(refreshToken);

        HttpHeaders responseHeaders = new HttpHeaders();
        Token newAccessToken;
        Token newRefreshToken;
        if (!accessTokenValid && !refreshTokenValid) {
            newAccessToken = jwtService.generateToken(user);
            accessToken = newAccessToken.getTokenValue();
            newRefreshToken = jwtService.generateRefreshToken(user);
            addAccessTokenCookie(responseHeaders, newAccessToken);
            addRefreshTokenCookie(responseHeaders, newRefreshToken);
        }
        else if (!accessTokenValid) {
            newAccessToken = jwtService.generateToken(user);
            accessToken = newAccessToken.getTokenValue();
            addAccessTokenCookie(responseHeaders, newAccessToken);
        }
        else if (refreshTokenValid) {
            newAccessToken = jwtService.generateToken(user);
            accessToken = newAccessToken.getTokenValue();
            newRefreshToken = jwtService.generateRefreshToken(user);
            addAccessTokenCookie(responseHeaders, newAccessToken);
            addRefreshTokenCookie(responseHeaders, newRefreshToken);
        }

        addAuthTokenHeader(responseHeaders, accessToken);
        AuthenticationResponse loginResponse = new AuthenticationResponse(AuthenticationResponse.Status.SUCCESS,
                "Auth successful. Tokens are created in cookie.");
        return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);

    }

    private void addAccessTokenCookie(HttpHeaders httpHeaders, Token token) {
        httpHeaders.add(HttpHeaders.SET_COOKIE,
                cookieUtil.createAccessTokenCookie(token.getTokenValue(), token.getDuration()).toString());
    }

    private void addRefreshTokenCookie(HttpHeaders httpHeaders, Token token) {
        httpHeaders.add(HttpHeaders.SET_COOKIE,
                cookieUtil.createRefreshTokenCookie(token.getTokenValue(), token.getDuration()).toString());
    }

    private void addAuthTokenHeader(HttpHeaders httpHeaders, String tokenValue) {
        httpHeaders.add(HttpHeaders.AUTHORIZATION, tokenValue);
    }

    public ResponseEntity<AuthenticationResponse> refreshToken(String encryptedRefreshToken
    ) throws IOException {
        //final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken= SecurityCipher.decrypt(encryptedRefreshToken);
        boolean refreshTokenValid = jwtService.validateToken(refreshToken);

        if (!refreshTokenValid) {
            AuthenticationResponse loginResponse = new AuthenticationResponse(AuthenticationResponse.Status.FAILURE,
                    "Invalid refresh token !");
            return ResponseEntity.ok().body(loginResponse);
        }
        var userNickname = jwtService.extractNickname(refreshToken);
        UserDetails user = userDetailsService.loadUserByUsername(userNickname);

        Token newAccessToken = jwtService.generateToken(user);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil
                .createAccessTokenCookie(newAccessToken.getTokenValue(), newAccessToken.getDuration()).toString());

        addAuthTokenHeader(responseHeaders, newAccessToken.getTokenValue());
        AuthenticationResponse loginResponse = new AuthenticationResponse(AuthenticationResponse.Status.SUCCESS,
                "Auth successful. Tokens are created in cookie.");
        return ResponseEntity.ok().headers(responseHeaders).body(loginResponse);

    }
}
