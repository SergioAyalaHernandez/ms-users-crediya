package co.com.pragma.api;

import co.com.pragma.api.auth.AuthHandler;
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
import org.springframework.web.reactive.function.server.CoRouterFunctionDsl;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
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
            ),
            @RouterOperation(
                    path = "/api/v1/usuarios/{documentNumber}",
                    method = RequestMethod.GET,
                    operation = @Operation(
                            operationId = "getUserByDocumentNumber",
                            summary = "Obtener usuario por número de documento",
                            description = "Obtiene la información de un usuario a partir de su número de documento",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Usuario encontrado exitosamente",
                                            content = @Content(schema = @Schema(implementation = UserParameters.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Usuario no encontrado"
                                    ),
                                    @ApiResponse(
                                            responseCode = "401",
                                            description = "No autorizado, token inválido o expirado"
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/login",
                    method = RequestMethod.POST,
                    operation = @Operation(
                            operationId = "login",
                            summary = "Iniciar sesión",
                            description = "Autentica al usuario y devuelve un token JWT",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(
                                            example = "{\n" +
                                                    "  \"correoElectronico\": \"juan3.perez@example.com\",\n" +
                                                    "  \"password\": \"12345678\"\n" +
                                                    "}"
                                    ))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Autenticación exitosa",
                                            content = @Content(schema = @Schema(example = "{ \"token\": \"eyJhbGciOiJIUzI1NiJ9...\" }"))
                                    ),
                                    @ApiResponse(
                                            responseCode = "401",
                                            description = "Credenciales inválidas"
                                    )
                            }
                    )
            )
    })
    @Bean
    public RouterFunction<ServerResponse> routerFunction(Handler handler, AuthHandler authHandler) {
        return route(POST("/api/v1/usuarios"), handler::createUser)
                .andRoute(GET("/api/v1/usuarios/{documentNumber}"), handler::getUserByDocumentNumber)
                .andRoute(POST("/api/v1/login"), authHandler::login);
    }

}
