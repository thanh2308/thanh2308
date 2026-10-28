package com.example.eshop.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
  public static final String COOKIE_NAME = "ACCESS_TOKEN";
  private final SecretKey key;
  private final long expirationMs;

  public JwtService(
      @Value("${app.jwt.secret:}") String secret,
      @Value("${app.jwt.expiration-ms:86400000}") long expirationMs) {
    // Local development fallback only: a fresh key invalidates tokens after restart.
    this.key =
        secret.isBlank()
            ? Jwts.SIG.HS256.key().build()
            : Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    this.expirationMs = expirationMs;
    if (secret.isBlank())
      org.slf4j.LoggerFactory.getLogger(JwtService.class)
          .warn(
              "APP_JWT_SECRET chưa đặt: dùng khóa tạm cho lần chạy này. Đặt khóa riêng khi triển"
                  + " khai.");
  }

  public String generateToken(UserDetails u) {
    Date now = new Date();
    return Jwts.builder()
        .subject(u.getUsername())
        .claim("roles", u.getAuthorities().stream().map(Object::toString).toList())
        .issuedAt(now)
        .expiration(new Date(now.getTime() + expirationMs))
        .signWith(key)
        .compact();
  }

  public String extractUsername(String token) {
    return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
  }

  public long getExpirationSeconds() {
    return expirationMs / 1000;
  }
}
