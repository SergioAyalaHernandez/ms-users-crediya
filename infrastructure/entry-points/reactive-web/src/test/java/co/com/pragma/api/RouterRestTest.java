package co.com.pragma.api;

import co.com.pragma.model.user.user.UserParameters;
import co.com.pragma.usecase.exceptions.BusinessException;
import co.com.pragma.usecase.exceptions.ErrorResponse;
import co.com.pragma.usecase.exceptions.ExceptionType;
import co.com.pragma.usecase.user.UserUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
@Import(TestConfig.class)
class RouterRestTest {

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private UserUseCase userUseCase;

  @Test
  void testCreateUser() {
    // Given
    String userJson = "{\n" +
            "  \"nombres\": \"Juan Camilo\",\n" +
            "  \"apellidos\": \"Pérez Perez\",\n" +
            "  \"fechaNacimiento\": \"1990-01-01\",\n" +
            "  \"direccion\": \"Calle Falsa 123\",\n" +
            "  \"telefono\": \"123456789\",\n" +
            "  \"correoElectronico\": \"juan1.perez@example.com\",\n" +
            "  \"salarioBase\": 50000.00\n" +
            "}";

    Mockito.when(userUseCase.createUser(Mockito.any()))
            .thenReturn(Mono.just(Mockito.mock(UserParameters.class)));

    // When/Then
    webTestClient.post()
            .uri("/api/v1/usuarios")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .value(userResponse -> {
              Assertions.assertThat(userResponse).isNotNull();
            });
  }

  @Test
  void testCreateUser_ConDatosValidos() {
    String userJson = "{\n" +
            "  \"nombres\": \"Juan Camilo\",\n" +
            "  \"apellidos\": \"Pérez Perez\",\n" +
            "  \"fechaNacimiento\": \"1990-01-01\",\n" +
            "  \"direccion\": \"Calle Falsa 123\",\n" +
            "  \"telefono\": \"123456789\",\n" +
            "  \"correoElectronico\": \"juan1.perez@example.com\",\n" +
            "  \"salarioBase\": 50000.00\n" +
            "}";

    Mockito.when(userUseCase.createUser(Mockito.any()))
            .thenReturn(Mono.just(Mockito.mock(UserParameters.class)));

    webTestClient.post()
            .uri("/api/v1/usuarios")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .value(userResponse -> {
              Assertions.assertThat(userResponse).isNotNull();
              Assertions.assertThat(userResponse).contains("nombres", "apellidos", "fechaNacimiento");
            });

    Mockito.verify(userUseCase).createUser(Mockito.any());
  }

  @Test
  void testCreateUser_ConError() {
    String userJson = "{\n" +
            "  \"nombres\": \"Juan Camilo\",\n" +
            "  \"apellidos\": \"Pérez Perez\",\n" +
            "  \"fechaNacimiento\": \"1990-01-01\",\n" +
            "  \"direccion\": \"Calle Falsa 123\",\n" +
            "  \"telefono\": \"123456789\",\n" +
            "  \"correoElectronico\": \"juan1.perez@example.com\",\n" +
            "  \"salarioBase\": 50000.00\n" +
            "}";

    Mockito.when(userUseCase.createUser(Mockito.any()))
            .thenReturn(reactor.core.publisher.Mono.error(new RuntimeException("Error al crear usuario")));

    webTestClient.post()
            .uri("/api/v1/usuarios")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange()
            .expectStatus().is5xxServerError();

    Mockito.verify(userUseCase, Mockito.atLeastOnce()).createUser(Mockito.any());
  }

  @Test
  void testCreateUser_ConFormatoIncorrecto() {
    String userJson = "{ datos incorrectos }";

    Mockito.when(userUseCase.createUser(Mockito.any()))
            .thenReturn(reactor.core.publisher.Mono.error(new RuntimeException("Error de formato")));

    webTestClient.post()
            .uri("/api/v1/usuarios")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange()
            .expectStatus().is5xxServerError();

  }

  @Test
  void testRutaNoExistente() {
    webTestClient.post()
            .uri("/api/v1/ruta-que-no-existe")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue("{}")
            .exchange()
            .expectStatus().isNotFound();
  }

  @Test
  void testCreateUser_ConBusinessException() {
    String userJson = "{\n" +
            "  \"nombres\": \"Juan Camilo\",\n" +
            "  \"apellidos\": \"Pérez Perez\",\n" +
            "  \"fechaNacimiento\": \"1990-01-01\",\n" +
            "  \"direccion\": \"Calle Falsa 123\",\n" +
            "  \"telefono\": \"123456789\",\n" +
            "  \"correoElectronico\": \"juan1.perez@example.com\",\n" +
            "  \"salarioBase\": 50000.00\n" +
            "}";

    ErrorResponse errorResponse = new ErrorResponse("BUSINESS_ERROR", "Usuario ya existe", HttpStatus.BAD_REQUEST.value());

    Mockito.when(userUseCase.createUser(Mockito.any()))
            .thenReturn(Mono.error(new BusinessException(ExceptionType.ALREADY_EXISTS, errorResponse)));

    webTestClient.post()
            .uri("/api/v1/usuarios")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(userJson)
            .exchange()
            .expectStatus().isBadRequest()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.code").isEqualTo("BUSINESS_ERROR")
            .jsonPath("$.message").isEqualTo("Usuario ya existe")
            .jsonPath("$.status").isEqualTo(400);

    Mockito.verify(userUseCase, Mockito.atLeastOnce()).createUser(Mockito.any());
  }
}

@Configuration
class TestConfig {
  @Bean
  public UserUseCase userUseCase() {
    return Mockito.mock(UserUseCase.class);
  }
}