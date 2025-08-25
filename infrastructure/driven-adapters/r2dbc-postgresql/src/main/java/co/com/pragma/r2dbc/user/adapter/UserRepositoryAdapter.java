package co.com.pragma.r2dbc.user.adapter;

import co.com.pragma.model.user.user.UserParameters;
import co.com.pragma.model.user.user.gateways.UserGateway;
import co.com.pragma.r2dbc.exceptions.DataIntegrityViolationException;
import co.com.pragma.r2dbc.exceptions.RepositoryException;
import co.com.pragma.r2dbc.user.entity.User;
import co.com.pragma.r2dbc.user.mapper.UserMapper;
import co.com.pragma.r2dbc.user.repository.UserRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Log
public class UserRepositoryAdapter implements UserGateway {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final Validator validator;

  @Transactional
  @Override
  public Mono<Object> createUser(UserParameters userParameters) {
    log.info("Iniciando creación de usuario con parámetros: " + userParameters);
    validateUserParameters(userParameters);
    return checkDuplicateEmail(userParameters.getCorreoElectronico())
            .switchIfEmpty(Mono.defer(() -> {
              log.info("No se encontró correo duplicado, procediendo a guardar el usuario.");
              return saveUser(userParameters);
            }));
  }

  private void validateUserParameters(UserParameters userParameters) {
    log.info("Validando parámetros del usuario: " + userParameters);
    Set<ConstraintViolation<UserParameters>> violations = validator.validate(userParameters);
    if (!violations.isEmpty()) {
      log.severe("Falló la validación de parámetros: " + violations);
      throw new RepositoryException("Validation failed", new ConstraintViolationException(violations));
    }
    log.info("Validación de parámetros exitosa.");
  }

  private Mono<Object> checkDuplicateEmail(String email) {
    log.info("Verificando si el correo electrónico ya existe: " + email);
    return userRepository.existsByCorreoElectronico(email)
            .doOnNext(exists -> log.info("Resultado de existsByCorreoElectronico para " + email + ": " + exists))
            .flatMap(existingUser -> {
              if (existingUser) {
                return Mono.error(new RepositoryException("El correo electrónico ya está en uso"));
              }
              return Mono.empty();
            });
  }

  private Mono<UserParameters> saveUser(UserParameters userParameters) {
    log.info("Guardando usuario con parámetros: " + userParameters);
    User userEntity = userMapper.toEntity(userParameters);
    return userRepository.save(userEntity)
            .map(userMapper::toDto)
            .doOnSuccess(savedUser -> log.info("Usuario guardado exitosamente: " + savedUser))
            .onErrorMap(DataIntegrityViolationException.class, e -> {
              log.severe("Violación de integridad de datos al guardar el usuario: " + e.getMessage());
              return new RepositoryException("Faltan campos obligatorios", e);
            });
  }

}
