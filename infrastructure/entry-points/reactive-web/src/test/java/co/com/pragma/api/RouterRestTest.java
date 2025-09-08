package co.com.pragma.api;

import co.com.pragma.api.auth.AuthHandler;
import co.com.pragma.api.security.SecurityConfig;
import co.com.pragma.model.user.user.UserParameters;
import co.com.pragma.model.user.user.gateways.JwtProvider;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.mockito.Mockito.when;

@WebFluxTest
@Import({RouterRest.class, SecurityConfig.class})
@ContextConfiguration(classes = {RouterRestTest.TestConfig.class, TestApplication.class})
class RouterRestTest {

  @Autowired
  private Handler handler;

  @Autowired
  private AuthHandler authHandler;

  @Autowired
  private JwtProvider jwtProvider;

  @Autowired
  private WebTestClient webTestClient;

  @Configuration
  @TestConfiguration
  static class TestConfig {
    @Bean
    public Handler handler() {
      return Mockito.mock(Handler.class);
    }

    @Bean
    public AuthHandler authHandler() {
      return Mockito.mock(AuthHandler.class);
    }

    @Bean
    public JwtProvider jwtProvider() {
      return Mockito.mock(JwtProvider.class);
    }
  }

  @Nested
  class UserEndpointTests {
    @Test
    void shouldCreateUserSuccessfully() {
      // Given
      UserParameters userParameters = UserParameters.builder()
              .nombres("Juan Camilo")
              .apellidos("Pérez Perez")
              .fechaNacimiento(LocalDate.of(1990, 1, 1))
              .direccion("Calle Falsa 123")
              .telefono("123456789")
              .correoElectronico("juan1.perez@example.com")
              .salarioBase(BigDecimal.valueOf(50000.00))
              .salarioBase(BigDecimal.valueOf(50000.00))
              .build();

      when(jwtProvider.validateToken(Mockito.anyString())).thenReturn(Boolean.TRUE);
      when(jwtProvider.getRoleFromToken(Mockito.anyString())).thenReturn(Collections.singletonList("ADMIN"));

      when(handler.createUser(Mockito.any()))
              .thenAnswer(invocation ->
                      ServerResponse.ok()
                              .contentType(MediaType.APPLICATION_JSON)
                              .bodyValue(userParameters)
              );

      webTestClient.post()
              .uri("/api/v1/usuarios")
              .contentType(MediaType.APPLICATION_JSON)
              .header(HttpHeaders.AUTHORIZATION, "Bearer fake-token-for-test")
              .bodyValue(userParameters)
              .exchange()
              .expectStatus().isOk()
              .expectBody(UserParameters.class)
              .isEqualTo(userParameters);

    }

    @Test
    void shouldGetUserByDocumentNumber() {
      // Given
      String documentNumber = "12345678";
      UserParameters userParameters = UserParameters.builder()
              .nombres("Juan Camilo")
              .apellidos("Pérez Perez")
              .fechaNacimiento(LocalDate.of(1990, 1, 1))
              .build();

      when(handler.getUserByDocumentNumber(Mockito.any()))
              .thenAnswer(invocation ->
                      ServerResponse.ok()
                              .contentType(MediaType.APPLICATION_JSON)
                              .bodyValue(userParameters)
              );

      when(jwtProvider.validateToken(Mockito.anyString())).thenReturn(Boolean.TRUE);
      when(jwtProvider.getRoleFromToken(Mockito.anyString())).thenReturn(Collections.singletonList("ADMIN"));

      // When & Then
      webTestClient.get()
              .uri("/api/v1/usuarios/{documentNumber}", documentNumber)
              .header(HttpHeaders.AUTHORIZATION, "Bearer fake-token-for-test")
              .exchange()
              .expectStatus().isOk()
              .expectBody(UserParameters.class)
              .isEqualTo(userParameters);
    }


    @Test
    void shouldFailWithUnauthorizedWhenNoToken() {
      // Given
      String documentNumber = "12345678";

      // When & Then
      webTestClient.get()
              .uri("/api/v1/usuarios/{documentNumber}", documentNumber)
              .exchange()
              .expectStatus().is5xxServerError();
    }
  }

  @Nested
  class AuthEndpointTests {
    @Test
    void shouldLoginSuccessfully() {
      // Given
      String token = "fake-jwt-token";
      when(authHandler.login(Mockito.any()))
              .thenAnswer(invocation ->
                      ServerResponse.ok()
                              .contentType(MediaType.TEXT_PLAIN)
                              .bodyValue(token)
              );

      // When & Then
      webTestClient.post()
              .uri("/api/v1/login")
              .contentType(MediaType.APPLICATION_JSON)
              .bodyValue("{\"username\":\"admin\",\"password\":\"password\"}")
              .exchange()
              .expectStatus().isOk()
              .expectBody(String.class)
              .isEqualTo(token);
    }
  }

}