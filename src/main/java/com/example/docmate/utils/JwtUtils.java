package com.example.docmate.utils;

import com.example.docmate.enums.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;


@Component
public class JwtUtils {
    //this is utils class.
    @Value("${jwt.secret}")
    private String secret;

//    Token expires in 24 hours

    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private Long expiration;

    @Value("${jwt.expiration}") // 15 minutes for access token
    private Long accessTokenExpiration;

//@Value("${jwt.expiration:30000}") // 30 seconds for access token
//private Long accessTokenExpiration;

    @Value("${jwt.refresh-expiration}") // 7 days for refresh token
    private Long refreshTokenExpiration;

//    onverts your secret string into a cryptographic key
//Used to sign and verify tokens

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Generate JWT token
    public String generateToken(String email, Role role, String userId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Generate SHORT-LIVED Access Token (15 min)
    public String generateAccessToken(String email, Role role, String userId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .claim("userId", userId)
                .claim("type", "ACCESS")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Generate LONG-LIVED Refresh Token (7 days)
    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("type", "REFRESH")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extract email/username
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    // Extract role
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }



    public String extractTokenType(String token) {
        return getClaims(token).get("type", String.class);
    }

    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    //    onverts your secret string into a cryptographic key
//    Used to sign and verify tokens
    // Validate JWT
    public boolean validateToken(String token, String username) {
        return username.equals(extractUsername(token))
                && getClaims(token).getExpiration().after(new Date());
    }

    public boolean validateAccessToken(String token, String username) {
        return username.equals(extractUsername(token))
                && !isTokenExpired(token)
                && "ACCESS".equals(extractTokenType(token));
    }

    public boolean validateRefreshToken(String token) {
        return !isTokenExpired(token)
                && "REFRESH".equals(extractTokenType(token));
    }

    // Internal helper
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
