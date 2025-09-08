package co.com.pragma.api.auth;

import co.com.pragma.model.user.user.LoginRequest;
import co.com.pragma.usecase.auth.LoginServiceUseCase;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import co.com.pragma.api.utils.Constants;

import java.util.Map;

@Component
@AllArgsConstructor
public class AuthHandler {

  private final LoginServiceUseCase loginServiceUseCase;

  public Mono<ServerResponse> login(ServerRequest request) {
    return request.bodyToMono(LoginRequest.class)
            .flatMap(dto -> loginServiceUseCase.login(dto.getCorreoElectronico(), dto.getPassword())
                    .flatMap(token -> ServerResponse.ok().bodyValue(Map.of(Constants.TOKEN_KEY, token)))
                    .onErrorResume(e -> ServerResponse.status(Constants.FORBIDDEN_STATUS).bodyValue(Map.of(Constants.ERROR_KEY, e.getMessage()))));
  }

}
