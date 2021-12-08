package ro.deloittedigital.samekh.usermanagement.utility;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ro.deloittedigital.samekh.usermanagement.repository.UserRepository;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JWTUtility {
    private final UserRepository userRepository;

    @Value(value = "${jwt.secret}")
    private String secret;

    @Value(value = "${jwt.issuer}")
    private String issuer;

    public String generateJWT(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuer(issuer)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public boolean isValid(String jwt) {
        try {
            Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(jwt);
            return true;
        } catch (ExpiredJwtException exception) {
            log.info("[JWTUtility] expired JWT: {} - {}", jwt, exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            log.info("[JWTUtility] unsupported JWT: {} - {}", jwt, exception.getMessage());
        } catch (MalformedJwtException exception) {
            log.info("[JWTUtility] malformed JWT: {} - {}", jwt, exception.getMessage());
        } catch (SignatureException exception) {
            log.info("[JWTUtility] invalid JWT signature: {} - {}", jwt, exception.getMessage());
        } catch (IllegalArgumentException exception) {
            log.info("[JWTUtility] JWT payload is empty: {} - {}", jwt, exception.getMessage());
        }
        return false;
    }

    private Date getExpirationDate(String jwt) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(jwt)
                .getBody()
                .getExpiration();
    }

    public boolean isExpired(String jwt) {
        return getExpirationDate(jwt).before(new Date());
    }

    public String getEmail(String jwt) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }

    public boolean existsUserByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }
}
