package co.com.pragma.usecase.auth;

import co.com.pragma.model.user.user.gateways.JwtProvider;
import co.com.pragma.model.user.user.gateways.UserGateway;
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
            .switchIfEmpty(Mono.error(new RuntimeException("Usuario no encontrado")))
            .flatMap(user -> {
              if (!user.getPassword().equals(password)) {
                return Mono.error(new RuntimeException("Contraseña incorrecta"));
              }

              Map<String, Object> claims = new HashMap<>();
              claims.put("roles", user.getRole());

              String userId = user.getId().toString();
              return Mono.just(jwtProvider.generateToken(userId, claims));
            });
  }
}
