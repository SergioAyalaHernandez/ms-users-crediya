package co.com.pragma.api.openapi;

import co.com.pragma.model.user.user.UserParameters;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRestDocs {

  @RouterOperations({
          @RouterOperation(
                  path = "/api/v1/usuarios",
                  method = RequestMethod.POST,
                  operation = @Operation(
                          operationId = "createUser",
                          summary = "Crear un nuevo usuario",
                          description = "Crea un nuevo usuario en el sistema",
                          requestBody = @RequestBody(
                                  required = true,
                                  content = @Content(schema = @Schema(
                                          implementation = UserParameters.class,
                                          example = "{\n" +
                                                  "  \"nombres\": \"Juan Camilo\",\n" +
                                                  "  \"apellidos\": \"Pérez Perez\",\n" +
                                                  "  \"fechaNacimiento\": \"1990-01-01\",\n" +
                                                  "  \"direccion\": \"Calle Falsa 123\",\n" +
                                                  "  \"telefono\": \"123456789\",\n" +
                                                  "  \"correoElectronico\": \"juan1.perez@example.com\",\n" +
                                                  "  \"salarioBase\": 50000.00\n" +
                                                  "}"
                                  ))
                          ),
                          responses = {
                                  @ApiResponse(
                                          responseCode = "201",
                                          description = "Usuario creado exitosamente",
                                          content = @Content(schema = @Schema(implementation = UserParameters.class))
                                  ),
                                  @ApiResponse(
                                          responseCode = "400",
                                          description = "Datos de usuario inválidos"
                                  )
                          }
                  )
          )
  })
  @Bean
  public RouterFunction<ServerResponse> documentedRouterFunction(co.com.pragma.api.Handler handler) {
    return route(POST("/api/v1/usuarios"), handler::createUser);
  }
}
