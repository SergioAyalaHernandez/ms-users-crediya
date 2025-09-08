package co.com.pragma.r2dbc.jwt;

import static org.junit.jupiter.api.Assertions.*;

import co.com.pragma.model.user.user.gateways.JwtProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class JwtProviderImplTest {

  private JwtProvider jwtProvider;
  private String userId;
  private Map<String, Object> claims;

  @BeforeEach
  void setUp() {
    // Arrange - Configuración general
    jwtProvider = new JwtProviderImpl();
    userId = "usuario123";
    claims = new HashMap<>();
  }

  @Test
  @DisplayName("Debe generar un token JWT válido")
  void generateTokenTest() {
    // Arrange
    claims.put("roles", "ADMIN,USER");

    // Act
    String token = jwtProvider.generateToken(userId, claims);

    // Assert
    assertNotNull(token);
    assertTrue(token.length() > 0);
    assertTrue(jwtProvider.validateToken(token));
  }

  @Test
  @DisplayName("Debe validar correctamente un token válido")
  void validateValidTokenTest() {
    // Arrange
    String token = jwtProvider.generateToken(userId, claims);

    // Act
    boolean isValid = jwtProvider.validateToken(token);

    // Assert
    assertTrue(isValid);
  }

  @Test
  @DisplayName("Debe invalidar un token mal formado")
  void validateInvalidTokenTest() {
    // Arrange
    String invalidToken = "token.invalido.jwt";

    // Act
    boolean isValid = jwtProvider.validateToken(invalidToken);

    // Assert
    assertFalse(isValid);
  }

  @Test
  @DisplayName("Debe extraer el ID de usuario del token")
  void getUserIdFromTokenTest() {
    // Arrange
    String token = jwtProvider.generateToken(userId, claims);

    // Act
    String extractedUserId = jwtProvider.getUserIdFromToken(token);

    // Assert
    assertEquals(userId, extractedUserId);
  }

  @Test
  @DisplayName("Debe extraer roles del token cuando existen")
  void getRolesFromTokenWithRolesTest() {
    // Arrange
    claims.put("roles", "ADMIN,USER");
    String token = jwtProvider.generateToken(userId, claims);

    // Act
    List<String> roles = jwtProvider.getRoleFromToken(token);

    // Assert
    assertNotNull(roles);
    assertEquals(2, roles.size());
    assertTrue(roles.contains("ADMIN"));
    assertTrue(roles.contains("USER"));
  }

  @Test
  @DisplayName("Debe manejar el caso de token sin roles")
  void getRolesFromTokenWithoutRolesTest() {
    // Arrange
    String token = jwtProvider.generateToken(userId, claims);

    // Act
    List<String> roles = jwtProvider.getRoleFromToken(token);

    // Assert
    assertNotNull(roles);
    assertTrue(roles.isEmpty());
  }

  @Test
  @DisplayName("Debe manejar el caso de roles vacíos")
  void getRolesFromTokenWithEmptyRolesTest() {
    // Arrange
    claims.put("roles", "");
    String token = jwtProvider.generateToken(userId, claims);

    // Act
    List<String> roles = jwtProvider.getRoleFromToken(token);

    // Assert
    assertNotNull(roles);
    assertTrue(roles.isEmpty());
  }
}
