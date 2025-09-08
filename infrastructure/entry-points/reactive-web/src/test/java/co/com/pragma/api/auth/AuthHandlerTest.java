package co.com.pragma.api.auth;

import co.com.pragma.model.user.user.LoginRequest;
import co.com.pragma.usecase.auth.LoginServiceUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthHandlerTest {

  @Mock
  private LoginServiceUseCase loginServiceUseCase;

  @Mock
  private ServerRequest serverRequest;

  @InjectMocks
  private AuthHandler authHandler;

  private LoginRequest loginRequest;

  @BeforeEach
  void setUp() {
    loginRequest = new LoginRequest();
    loginRequest.setCorreoElectronico("test@example.com");
    loginRequest.setPassword("password123");
  }

  @Test
  void login_WhenValidCredentials_ShouldReturnOkWithToken() {
    // Given
    String expectedToken = "jwt-token-123";
    when(serverRequest.bodyToMono(LoginRequest.class))
            .thenReturn(Mono.just(loginRequest));
    when(loginServiceUseCase.login(anyString(), anyString()))
            .thenReturn(Mono.just(expectedToken));

    // When
    Mono<ServerResponse> response = authHandler.login(serverRequest);

    // Then
    StepVerifier.create(response)
            .expectNextMatches(serverResponse -> {
              // Verificar que el status sea 200 OK
              return serverResponse.statusCode().value() == 200;
            })
            .verifyComplete();

    verify(loginServiceUseCase).login("test@example.com", "password123");
  }

  @Test
  void login_WhenInvalidCredentials_ShouldReturn403WithError() {
    // Given
    when(serverRequest.bodyToMono(LoginRequest.class))
            .thenReturn(Mono.just(loginRequest));
    when(loginServiceUseCase.login(anyString(), anyString()))
            .thenReturn(Mono.error(new RuntimeException("Usuario no encontrado")));

    // When
    Mono<ServerResponse> response = authHandler.login(serverRequest);

    // Then
    StepVerifier.create(response)
            .expectNextMatches(serverResponse -> {
              // Verificar que el status sea 403 Forbidden
              return serverResponse.statusCode().value() == 403;
            })
            .verifyComplete();

    verify(loginServiceUseCase).login("test@example.com", "password123");
  }

  @Test
  void login_WhenInvalidRequestBody_ShouldHandleError() {
    // Given
    when(serverRequest.bodyToMono(LoginRequest.class))
            .thenReturn(Mono.error(new RuntimeException("Invalid request body")));

    // When
    Mono<ServerResponse> response = authHandler.login(serverRequest);

    // Then
    StepVerifier.create(response)
            .expectError(RuntimeException.class)
            .verify();
  }
}

