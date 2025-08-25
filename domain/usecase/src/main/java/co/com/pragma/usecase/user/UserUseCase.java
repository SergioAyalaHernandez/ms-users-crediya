package co.com.pragma.usecase.user;

import co.com.pragma.model.user.user.UserParameters;
import co.com.pragma.model.user.user.gateways.UserGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

@Log
@RequiredArgsConstructor
public class UserUseCase {
  private final UserGateway userGateway;

  public Mono<UserParameters> createUser(UserParameters userParameters) {
    log.info("Creating user...");
    return userGateway.createUser(userParameters);
  }
}
