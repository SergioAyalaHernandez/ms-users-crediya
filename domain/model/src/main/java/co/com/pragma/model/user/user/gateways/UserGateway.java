package co.com.pragma.model.user.user.gateways;

import co.com.pragma.model.user.user.UserParameters;
import reactor.core.publisher.Mono;

public interface UserGateway {
  Mono<UserParameters> createUser(UserParameters userParameters);
}
