package co.com.pragma.api;

import co.com.pragma.usecase.user.UserUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

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

        // Configure mock to return a successful response
        Mockito.when(userUseCase.createUser(Mockito.any()))
               .thenReturn(reactor.core.publisher.Mono.just("Usuario creado correctamente"));

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
                        }

@Configuration
class TestConfig {
    @Bean
    public UserUseCase userUseCase() {
        return Mockito.mock(UserUseCase.class);
    }
}