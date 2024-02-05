package com.nashirul.zakat.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
@Component
public class JwtUtils {
    @Value("${jwt.secret}")
    private String secret = "";
    private final String secretKey = generateSecretKey(secret);

    private static final long EXPIRATION_TIME = 864_000_000; // 10 days

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public String extractSubject(String token) {
        return Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private String generateSecretKey(String secret) {
        // Convert the custom word to a byte array
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);

        // You may apply additional cryptographic techniques here if needed
        byte[] hashedBytes = hashWithSHA512(secretBytes);

        // Use the byte array as a secret key
        SecretKey key = new SecretKeySpec(hashedBytes, "HmacSHA512");

        // Encode the key to a Base64 string for easy representation
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    private byte[] hashWithSHA512(byte[] input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            return digest.digest(input);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing with SHA-512", e);
        }
    }
}
