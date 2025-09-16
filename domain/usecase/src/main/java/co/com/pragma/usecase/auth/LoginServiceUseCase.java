package co.com.pragma.usecase.auth;

import co.com.pragma.model.user.user.gateways.JwtProvider;
import co.com.pragma.model.user.user.gateways.UserGateway;
import co.com.pragma.usecase.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Log
@RequiredArgsConstructor
public class LoginServiceUseCase {

  private final UserGateway userGateway;
  private final JwtProvider jwtProvider;

  public Mono<String> login(String correoElectronico, String password) {
    return userGateway.findByCorreoElectronico(correoElectronico)
            .switchIfEmpty(Mono.error(new RuntimeException(Constants.USUARIO_NO_ENCONTRADO)))
            .flatMap(user -> {
              if (!user.getPassword().equals(password)) {
                return Mono.error(new RuntimeException(Constants.CONTRASENA_INCORRECTA));
              }

              Map<String, Object> claims = new HashMap<>();
              claims.put(Constants.CLAIM_ROLES, user.getRole());

              String userId = user.getId().toString();
              return Mono.just(jwtProvider.generateToken(user, claims));
            });
  }
}
