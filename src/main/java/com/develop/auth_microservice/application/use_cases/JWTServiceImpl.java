package com.develop.auth_microservice.application.use_cases;

import com.develop.auth_microservice.domain.interfaces.JWTService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JWTServiceImpl implements JWTService {

    private String secretKey = "";

    public JWTServiceImpl() {
        try {
            KeyGenerator key = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = key.generateKey();
            secretKey = Encoders.BASE64URL.encode(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 30 * 60 * 1000)) // 30 minutos
                .and()
                .signWith(getKey())
                .compact();

    }

    @Override
    public Key getKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
