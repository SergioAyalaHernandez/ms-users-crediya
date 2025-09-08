package co.com.pragma.r2dbc.user.adapter;

import co.com.pragma.model.user.user.UserParameters;
import co.com.pragma.model.user.user.gateways.UserGateway;
import co.com.pragma.r2dbc.exceptions.DataIntegrityViolationException;
import co.com.pragma.r2dbc.exceptions.RepositoryException;
import co.com.pragma.r2dbc.user.mapper.UserMapper;
import co.com.pragma.r2dbc.user.repository.UserRepository;
import co.com.pragma.r2dbc.utils.Constants;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Set;

@Repository
@RequiredArgsConstructor
@Log
public class UserRepositoryAdapter implements UserGateway {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final Validator validator;

  @Override
  public Mono<UserParameters> createUser(UserParameters userParameters) {
    log.info(Constants.CREATING_USER_INIT + userParameters);
    return Mono.just(userParameters)
            .doOnNext(this::validateUserParameters)
            .map(userMapper::toEntity)
            .doOnNext(user -> user.setRole(Constants.ROLE_USER))
            .flatMap(userRepository::save)
            .map(userMapper::toDto)
            .doOnSuccess(savedUser -> log.info(Constants.USER_CREATED_SUCCESSFULLY + savedUser))
            .onErrorMap(e -> {
              if (e instanceof DataIntegrityViolationException) {
                log.severe(Constants.DATA_INTEGRITY_VIOLATION + e.getMessage());
                return new RepositoryException(Constants.MISSING_REQUIRED_FIELDS, e);
              }
              log.severe(Constants.UNEXPECTED_ERROR_CREATING_USER + e.getMessage());
              return new RepositoryException(Constants.UNEXPECTED_ERROR_MESSAGE, e);
            });
  }

  @Override
  public Mono<Boolean> existsByCorreoElectronico(String email) {
    log.info(Constants.CHECKING_EMAIL_EXISTENCE + email);
    return userRepository.existsByCorreoElectronico(email)
            .doOnNext(exists -> log.info(Constants.EMAIL_EXISTENCE_RESULT + email + ": " + exists))
            .flatMap(existingUser -> {
              if (existingUser) {
                return Mono.error(new RepositoryException(Constants.EMAIL_ALREADY_IN_USE));
              }
              return Mono.just(false);
            });
  }

  @Override
  public Mono<UserParameters> findByDocumentNumber(String documentNumber) {
    log.info(Constants.SEARCHING_USER_BY_DOCUMENT + documentNumber);
    return userRepository.findByNumeroDocumento(documentNumber)
            .map(userMapper::toDto)
            .doOnSuccess(user -> log.info(Constants.USER_FOUND_BY_DOCUMENT + (user != null ? user : "No encontrado")))
            .switchIfEmpty(Mono.defer(() -> {
              log.info(Constants.USER_NOT_FOUND_BY_DOCUMENT + documentNumber);
              return Mono.empty();
            }))
            .onErrorMap(e -> {
              log.severe(Constants.ERROR_FINDING_USER_BY_DOCUMENT + e.getMessage());
              return new RepositoryException(Constants.UNEXPECTED_ERROR_MESSAGE, e);
            });
  }

  private void validateUserParameters(UserParameters userParameters) {
    log.info(Constants.VALIDATING_USER_PARAMETERS + userParameters);
    Set<ConstraintViolation<UserParameters>> violations = validator.validate(userParameters);
    if (!violations.isEmpty()) {
      log.severe(Constants.VALIDATION_FAILED + violations);
      throw new RepositoryException(Constants.VALIDATION_EXCEPTION_MESSAGE, new ConstraintViolationException(violations));
    }
    log.info(Constants.VALIDATION_SUCCESSFUL);
  }

  @Override
  public Mono<UserParameters> findByCorreoElectronico(String email) {
    log.info(Constants.SEARCHING_USER_BY_EMAIL + email);
    return userRepository.findByCorreoElectronico(email)
            .map(userMapper::toDto)
            .doOnSuccess(user -> log.info(Constants.USER_NOT_FOUND + (user != null ? user : "No encontrado")))
            .switchIfEmpty(Mono.defer(() -> {
              log.info(Constants.USER_NOT_FOUND + email);
              return Mono.empty();
            }))
            .onErrorMap(e -> {
              log.severe(Constants.USER_NOT_FOUND_BY_EMAIL + e.getMessage());
              return new RepositoryException(Constants.UNEXPECTED_ERROR_MESSAGE, e);
            });
  }
}
