package cz.upce.ticketmanager.auth;

import cz.upce.ticketmanager.config.AppProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final AppProperties props;

    public JwtService(AppProperties props) { this.props = props; }

    private Key key(){
        return Keys.hmacShaKeyFor(props.getJwt().getSecret().getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    public String generateToken(String subject){
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(props.getJwt().getExpirationMinutes() * 60L);
        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractSubject(String token){
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean isValid(String token){
        try {
            extractSubject(token);
            return true;
        } catch (JwtException | IllegalArgumentException e){
            return false;
        }
    }

    public long getExpiresInSeconds(){
        return props.getJwt().getExpirationMinutes() * 60L;
    }
}
