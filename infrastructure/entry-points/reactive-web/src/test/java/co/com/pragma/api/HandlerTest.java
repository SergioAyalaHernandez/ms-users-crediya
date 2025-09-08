package co.com.pragma.api;

import co.com.pragma.model.user.user.UserParameters;
import co.com.pragma.usecase.exceptions.BusinessException;
import co.com.pragma.usecase.exceptions.ErrorResponse;
import co.com.pragma.usecase.user.UserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.EntityResponse;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HandlerTest {

  private UserUseCase userUseCase;
  private Handler handler;

  @BeforeEach
  void setUp() {
    userUseCase = mock(UserUseCase.class);
    handler = new Handler(userUseCase);
  }

  @Test
  void createUser_shouldReturnOk_whenUserCreated() {
    // Arrange
    UserParameters user = UserParameters.builder()
            .nombres("Juan")
            .apellidos("Pérez")
            .fechaNacimiento(LocalDate.of(1990, 1, 1))
            .salarioBase(BigDecimal.valueOf(50000))
            .build();

    when(userUseCase.createUser(any(UserParameters.class))).thenReturn(Mono.just(user));

    MockServerRequest request = MockServerRequest.builder()
            .body(Mono.just(user));

    // Act
    Mono<ServerResponse> responseMono = handler.createUser(request);

    // Assert
    StepVerifier.create(responseMono)
            .expectNextMatches(res ->
                    res.statusCode().is2xxSuccessful())
            .verifyComplete();
  }

  @Test
  void createUser_shouldReturnBusinessError_whenUseCaseThrowsBusinessException() {
    // Arrange
    ErrorResponse errorResponse = new ErrorResponse("", "USER_EXISTS", 403);
    BusinessException businessException = new BusinessException(null, errorResponse);

    UserParameters user = UserParameters.builder().nombres("Juan").build();

    when(userUseCase.createUser(any(UserParameters.class))).thenReturn(Mono.error(businessException));

    MockServerRequest request = MockServerRequest.builder()
            .body(Mono.just(user));

    // Act
    Mono<ServerResponse> responseMono = handler.createUser(request);

    // Assert
    StepVerifier.create(responseMono)
            .expectNextMatches(res -> {
              return res.statusCode().value() == 403; // Cambiar 409 por 403 para coincidir con el ErrorResponse
            })
            .verifyComplete();
  }

  @Test
  void createUser_shouldReturn500_whenUnexpectedError() {
    // Arrange
    UserParameters user = UserParameters.builder().nombres("Juan").build();

    when(userUseCase.createUser(any(UserParameters.class))).thenReturn(Mono.error(new RuntimeException("DB down")));

    MockServerRequest request = MockServerRequest.builder()
            .body(Mono.just(user));

    // Act
    Mono<ServerResponse> responseMono = handler.createUser(request);

    // Assert
    StepVerifier.create(responseMono)
            .expectNextMatches(res -> {
              return res.statusCode().value() == 500;
            })
            .verifyComplete();
  }

  @Test
  void getUserByDocumentNumber_shouldReturnUser_whenFound() {
    // Arrange
    UserParameters user = UserParameters.builder().nombres("Maria").build();
    when(userUseCase.findByDocumentNumber("123")).thenReturn(Mono.just(user));

    MockServerRequest request = MockServerRequest.builder()
            .pathVariable("documentNumber", "123")
            .build();

    // Act
    Mono<ServerResponse> responseMono = handler.getUserByDocumentNumber(request);

    // Assert
    StepVerifier.create(responseMono)
            .expectNextMatches(res -> res instanceof EntityResponse<?>)
            .verifyComplete();

    StepVerifier.create(responseMono.flatMap(res ->
                    Mono.just(((EntityResponse<?>) res).entity())))
            .expectNextMatches(entity ->
                    entity instanceof UserParameters &&
                            "Maria".equals(((UserParameters) entity).getNombres()))
            .verifyComplete();
  }

  @Test
  void getUserByDocumentNumber_shouldReturnBusinessError_whenUseCaseThrowsBusinessException() {
    // Arrange
    ErrorResponse errorResponse = new ErrorResponse("", "USER_EXISTS", 403);
    BusinessException businessException = new BusinessException(null, errorResponse);

    when(userUseCase.findByDocumentNumber("123")).thenReturn(Mono.error(businessException));

    MockServerRequest request = MockServerRequest.builder()
            .pathVariable("documentNumber", "123")
            .build();

    // Act
    Mono<ServerResponse> responseMono = handler.getUserByDocumentNumber(request);

    // Assert
    StepVerifier.create(responseMono)
            .assertNext(res -> {
              assertEquals(403, res.statusCode().value());
            })
            .verifyComplete();

  }

  @Test
  void getUserByDocumentNumber_shouldReturn500_whenUnexpectedError() {
    // Arrange
    when(userUseCase.findByDocumentNumber("123")).thenReturn(Mono.error(new RuntimeException("DB timeout")));

    MockServerRequest request = MockServerRequest.builder()
            .pathVariable("documentNumber", "123")
            .build();

    // Act
    Mono<ServerResponse> responseMono = handler.getUserByDocumentNumber(request);

    // Assert
    StepVerifier.create(responseMono)
            .expectNextMatches(res -> {
              return res.statusCode().value() == 500;
            })
            .verifyComplete();
  }
}
