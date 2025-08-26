package co.com.pragma.api;

import co.com.pragma.model.user.user.UserParameters;
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
        return serverRequest.bodyToMono(UserParameters.class).
                flatMap(userUseCase::createUser)
                .flatMap(user -> ServerResponse.ok().bodyValue(user))
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue("Error creating user: " + error.getMessage()));

    }
}
