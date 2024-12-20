package com.imures.kaadbackend.configuration;

import com.imures.kaadbackend.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${application.security.jwt.secret-key}")
    private String SECRET_KEY; // for testing purpose only, change after!

    @Value("${application.security.jwt.refresh-secret-key}")
    private String REFRESH_SECRET_KEY;

    @Value("${application.security.jwt.expiration-time}")
    private long EXPIRATION_TIME;

    @Value("${application.security.jwt.refresh-expiration-time}")
    private long REFRESH_EXPIRATION_TIME;

    public String generateToken(User userDetails){
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("firstName",userDetails.getFirstName());
        claims.put("lastName",userDetails.getLastName());
        claims.put("id",userDetails.getId());
        return generateToken(claims, userDetails);
    }

    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ){
        return generateToken(userDetails, extraClaims, getSingInKey(), EXPIRATION_TIME);
    }

    public String generateRefreshToken(
            UserDetails userDetails
    ){
        return generateToken(userDetails, new HashMap<>() ,getRefreshKey(), REFRESH_EXPIRATION_TIME);
    }

    private String generateToken(UserDetails userDetails, Map<String, Object> extraClaims, Key singKey, long EXPIRATION_TIME){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(singKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails){
        Claims claims = extractAllClaims(token, getRefreshKey());
        String extractedUserMail = claims.getSubject();
        Date expiration = claims.getExpiration();
        return (extractedUserMail.equals(userDetails.getUsername()) && !expiration.before(new Date()));
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRefreshedUsername(String refreshToken) {
        return extractAllClaims(refreshToken, getRefreshKey()).getSubject();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token, getSingInKey());
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token, Key signingKey){
        return Jwts
                .parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSingInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Key getRefreshKey(){
        byte[] keyBytes = Decoders.BASE64.decode(REFRESH_SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}