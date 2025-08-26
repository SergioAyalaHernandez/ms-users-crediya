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
import org.springframework.stereotype.Component;
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
              return Mono.empty();
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
}
