package es.tfg.simuladorteoriacolas.security.jwt;

import es.tfg.simuladorteoriacolas.token.Token;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Autowired
    private UserDetailsService userDetailsService;

    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationFilter.class);


    @Value("${jwtSecret}")
    private String jwtSecret;

    private static long JWT_EXPIRATION_IN_MS = 86400000;
    private static Long REFRESH_TOKEN_EXPIRATION_MSEC = 10800000l;

    public String extractNickname(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }


    public Token generateToken(UserDetails user) {

        Claims claims = Jwts.claims().setSubject(user.getUsername());

        claims.put("auth", user.getAuthorities().stream().map(s -> new SimpleGrantedAuthority("ROLE_" + s))
                .filter(Objects::nonNull).collect(Collectors.toList()));

        Date now = new Date();
        Long duration = now.getTime() + JWT_EXPIRATION_IN_MS;
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION_IN_MS);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.HOUR_OF_DAY, 8);

        String token = Jwts
                .builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiryDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

        return new Token(Token.TokenType.ACCESS, token, duration,
                LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault()));
    }

    public Token generateRefreshToken(UserDetails user) {

        Claims claims = Jwts.claims().setSubject(user.getUsername());

        claims.put("auth", user.getAuthorities().stream().map(s -> new SimpleGrantedAuthority("ROLE_"+s))
                .filter(Objects::nonNull).collect(Collectors.toList()));
        Date now = new Date();
        Long duration = now.getTime() + REFRESH_TOKEN_EXPIRATION_MSEC;
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_MSEC);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.HOUR_OF_DAY, 8);
        String token = Jwts
                .builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiryDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

        return new Token(Token.TokenType.REFRESH, token, duration,
                LocalDateTime.ofInstant(expiryDate.toInstant(), ZoneId.systemDefault()));

    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException exception) {
            LOG.debug("Invalid JWT token");
        } catch (MalformedJwtException ex) {
            LOG.debug("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            LOG.debug("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            LOG.debug("Unsupported JWT exception");
        } catch (IllegalArgumentException ex) {
            LOG.debug("JWT claims empty");
        }
        return false;
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
