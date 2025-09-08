package co.com.pragma.r2dbc.jwt;

import co.com.pragma.model.user.user.gateways.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Log
public class JwtProviderImpl implements JwtProvider {

  private final Key secretKey = Keys.hmacShaKeyFor("ClaveSuperSecretaDeJWTQueDebeTenerAlMenos256Bits!".getBytes());
  private final long expirationMillis = 3600000; // 1 hora

  @Override
  public String generateToken(String userId, Map<String, Object> claims) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expirationMillis);

    log.info("Generando token para usuario: " + userId);
    log.info("Claims a incluir en el token: " + claims);

    return Jwts.builder()
            .setSubject(userId)
            .addClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
  }

  @Override
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
              .setSigningKey(secretKey)
              .build()
              .parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  @Override
  public String getUserIdFromToken(String token) {
    Claims claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
    return claims.getSubject();
  }

  @Override
  public List<String> getRoleFromToken(String token) {
    try {
      Claims claims = Jwts.parserBuilder()
              .setSigningKey(secretKey)
              .build()
              .parseClaimsJws(token)
              .getBody();

      String rolesStr = claims.get("roles", String.class);
      log.info("Roles extraídos del token: " + (rolesStr != null ? rolesStr : "null"));

      if (rolesStr == null || rolesStr.isEmpty()) {
        log.info("No se encontraron roles en el token");
        return List.of();
      }

      List<String> roles = Arrays.stream(rolesStr.split(","))
              .map(String::trim)
              .collect(Collectors.toList());

      log.info("Roles procesados: " + roles);
      return roles;
    } catch (Exception e) {
      log.info("Error al obtener roles del token: " + e.getMessage());
      return List.of();
    }
  }

}
