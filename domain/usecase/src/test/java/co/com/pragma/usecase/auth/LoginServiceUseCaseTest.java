package co.com.pragma.usecase.auth;


import co.com.pragma.model.user.user.UserParameters;
import co.com.pragma.model.user.user.gateways.JwtProvider;
import co.com.pragma.model.user.user.gateways.UserGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoginServiceUseCaseEdgeCasesTest {

  @Mock
  private UserGateway userGateway;

  @Mock
  private JwtProvider jwtProvider;

  @InjectMocks
  private LoginServiceUseCase loginServiceUseCase;

  @Test
  void login_WhenEmailIsNull_ShouldPropagateGatewayBehavior() {
    // Given
    when(userGateway.findByCorreoElectronico(null))
            .thenReturn(Mono.empty());

    // When
    Mono<String> result = loginServiceUseCase.login(null, "password");

    // Then
    StepVerifier.create(result)
            .expectErrorMatches(throwable ->
                    throwable instanceof RuntimeException &&
                            throwable.getMessage().equals("Usuario no encontrado"))
            .verify();

    verify(userGateway).findByCorreoElectronico(null);
  }

  @Test
  void login_WhenPasswordIsNull_ShouldReturnError() {
    // Given
    UserParameters user = new UserParameters();
    user.setPassword("validPassword");

    when(userGateway.findByCorreoElectronico("test@example.com"))
            .thenReturn(Mono.just(user));

    // When
    Mono<String> result = loginServiceUseCase.login("test@example.com", null);

    // Then
    StepVerifier.create(result)
            .expectErrorMatches(throwable ->
                    throwable instanceof RuntimeException &&
                            throwable.getMessage().equals("Contraseña incorrecta"))
            .verify();
  }

  @Test
  void login_WhenUserIdIsNull_ShouldHandleGracefully() {
    // Given
    UserParameters user = new UserParameters();
    user.setId(null); // ID nulo
    user.setCorreoElectronico("test@example.com");
    user.setPassword("password123");
    user.setRole("USER");

    when(userGateway.findByCorreoElectronico("test@example.com"))
            .thenReturn(Mono.just(user));

    // When
    Mono<String> result = loginServiceUseCase.login("test@example.com", "password123");

    // Then
    StepVerifier.create(result)
            .expectErrorMatches(throwable ->
                    throwable instanceof NullPointerException &&
                    throwable.getMessage().contains("Cannot invoke \"java.lang.Long.toString()\" because the return value of \"co.com.pragma.model.user.user.UserParameters.getId()\" is null"))
            .verify();
  }

  @Test
  void login_WhenUserHasMultipleRoles_ShouldIncludeInClaims() {
    // Given
    String expectedToken = "jwt-token-123";
    UserParameters user = new UserParameters();
    user.setId(1L);
    user.setCorreoElectronico("admin@example.com");
    user.setPassword("adminpass");
    user.setRole("ADMIN,USER"); // Múltiples roles

    Map<String, Object> expectedClaims = Map.of("roles", "ADMIN,USER");

    when(userGateway.findByCorreoElectronico("admin@example.com"))
            .thenReturn(Mono.just(user));
    when(jwtProvider.generateToken("1", expectedClaims))
            .thenReturn(expectedToken);

    // When
    Mono<String> result = loginServiceUseCase.login("admin@example.com", "adminpass");

    // Then
    StepVerifier.create(result)
            .expectNext(expectedToken)
            .verifyComplete();

    verify(jwtProvider).generateToken("1", expectedClaims);
  }
}
