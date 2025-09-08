package co.com.pragma.r2dbc.user.repository;

import co.com.pragma.model.user.user.UserParameters;
import co.com.pragma.r2dbc.user.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveCrudRepository<User, Long> {
  Mono<Boolean> existsByCorreoElectronico(String email);
  Mono<User> findByNumeroDocumento(String documentNumber);
  Mono<User> findByCorreoElectronico(String correoElectronico);
}
