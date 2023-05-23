package es.tfg.simuladorteoriacolas.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.http.Cookie;


import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        //Try to extract the token from the cookie
        String tokenFromCookie= getJwtToken(request,true);

        if (StringUtils.hasText(tokenFromCookie) && jwtService.validateToken(tokenFromCookie)) {
            final String nickname = jwtService.extractNickname(tokenFromCookie);

            //Check that there is a nickname in the jwt token and the tokend is correct, and the user is not logged
            if (nickname != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                //Check the user exist in the database
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(nickname);
                UsernamePasswordAuthenticationToken authenticationToken= new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                //Creates an authentication token with the user details, the request and the jwt token
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtToken(HttpServletRequest request, boolean fromCookie) {

        if (fromCookie) {
            return getJwtFromCookie(request);
        } else {
            return getJwtFromRequest(request);
        }
    }

    private String getJwtFromRequest(HttpServletRequest request) {

        //Extract Jwt from the header of the request
        String bearerToken = request.getHeader("Authorization");
        //Check there is a jwt token
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {

            String accessToken = bearerToken.substring(7);
            if (accessToken == null) {
                return null;
            }

            return SecurityCipher.decrypt(accessToken);
        }
        return null;
    }

    private String getJwtFromCookie(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            return "";
        }

        for (Cookie cookie : cookies) {
            if (JwtCookieManager.ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName())) {
                String accessToken = cookie.getValue();
                if (accessToken == null) {
                    return null;
                }

                return SecurityCipher.decrypt(accessToken);
            }
        }
        return null;
    }
}
