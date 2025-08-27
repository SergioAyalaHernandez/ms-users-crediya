package co.com.pragma.api;

import co.com.pragma.model.user.user.UserParameters;
import co.com.pragma.usecase.exceptions.BusinessException;
import co.com.pragma.usecase.exceptions.ErrorResponse;
import co.com.pragma.usecase.user.UserUseCase;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Log
public class Handler {

    private final UserUseCase userUseCase;
    public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
        log.info("Iniciando creación de usuario");
      return serverRequest.bodyToMono(UserParameters.class)
              .flatMap(userUseCase::createUser)
              .flatMap(user -> ServerResponse.ok().bodyValue(user))
              .onErrorResume(error -> {
                if (error instanceof BusinessException businessException) {
                  log.warning("Error de negocio: " + businessException.getMessage());
                  return ServerResponse.status(businessException.getErrorResponse().getStatus())
                          .bodyValue(businessException.getErrorResponse());
                }

                log.severe("Error inesperado: " + error + " - Tipo: " + error.getClass().getName());
                return ServerResponse.status(500)
                        .bodyValue(new ErrorResponse("INTERNAL_SERVER_ERROR",
                                "Ocurrió un error inesperado", 500));
              });
    }
}
