package com.finstuff.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    private final String secret;

    JwtService() throws NoSuchAlgorithmException {
        KeyGenerator generator = KeyGenerator.getInstance("HmacSHA256");
        SecretKey secretKey = generator.generateKey();
        secret = Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    public SecretKey getKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username){
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims().add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+1000*60))
                .and()
                .signWith(getKey())
                .compact();
    }

    public Claims extractAllClaims (String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername (String token){
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    public Date extractIssuedAt(String token){
        Claims claims = extractAllClaims(token);
        return claims.getIssuedAt();
    }

    public Date extractExpiration(String token){
        Claims claims = extractAllClaims(token);
        return claims.getExpiration();
    }

    public boolean isExpired(String token){
        return extractIssuedAt(token).after(extractExpiration(token));
    }

    public boolean validateToken (String token, UserDetails userDetails){
        return userDetails.getUsername().equals(extractUsername(token))
                && !isExpired(token);
    }
}
