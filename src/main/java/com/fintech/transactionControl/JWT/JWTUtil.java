package com.fintech.transactionControl.JWT;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JWTUtil {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long expiry;

    private Key getKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createToken(String email, String role){
     return Jwts.builder().setSubject(email).claim("role",role).setIssuedAt(new Date()).setExpiration(new Date(System.currentTimeMillis()+expiry)).signWith(getKey(), SignatureAlgorithm.HS256).compact();
    }


    public Boolean validateToken(String token){
        try{
            Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token);
            return true;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
