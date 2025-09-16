package co.com.pragma.usecase.user;

import co.com.pragma.model.user.user.UserParameters;
import co.com.pragma.model.user.user.gateways.UserGateway;
import co.com.pragma.usecase.exceptions.BusinessException;
import co.com.pragma.usecase.exceptions.ErrorResponse;
import co.com.pragma.usecase.exceptions.ExceptionType;
import co.com.pragma.usecase.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

@Log
@RequiredArgsConstructor
public class UserUseCase {
  private final UserGateway userGateway;

  public Mono<UserParameters> createUser(UserParameters userParameters) {
    log.info(Constants.LOG_CREATING_USER);

    return validateAndPrepareUser(userParameters)
            .flatMap(this::checkEmailAvailability)
            .flatMap(this::persistUser)
            .onErrorMap(this::mapCreationError)
            .doOnError(error -> log.severe(Constants.LOG_ERROR_CREATING_USER + error.getMessage()));
  }


  private Mono<UserParameters> validateAndPrepareUser(UserParameters userParameters) {
    try {
      validateUserParameters(userParameters);
      log.info(Constants.LOG_USER_PARAMETERS_VALIDATED);
      return Mono.just(userParameters);
    } catch (BusinessException e) {
      return Mono.error(e);
    }
  }

  private Mono<UserParameters> checkEmailAvailability(UserParameters userParameters) {
    return userGateway.existsByCorreoElectronico(userParameters.getCorreoElectronico())
            .flatMap(exists -> {
              if (exists) {
                log.severe(Constants.LOG_EMAIL_EXISTS + userParameters.getCorreoElectronico());
                return Mono.error(new BusinessException(
                        ExceptionType.ALREADY_EXISTS,
                        new ErrorResponse(Constants.USER_EXISTS_CODE, Constants.ERROR_USUARIO_EXISTENTE, 409)
                ));
              }
              log.info(Constants.LOG_EMAIL_AVAILABLE);
              return Mono.just(userParameters);
            });
  }

  private Mono<UserParameters> persistUser(UserParameters userParameters) {
    return userGateway.createUser(userParameters);
  }

  private Throwable mapCreationError(Throwable throwable) {
    if (throwable instanceof BusinessException) return throwable;

    if (throwable.getMessage() != null &&
            throwable.getMessage().contains(Constants.LOG_EMAIL_EXISTS)) {
      return new BusinessException(
              ExceptionType.ALREADY_EXISTS,
              new ErrorResponse(Constants.USER_EXISTS_CODE, Constants.ERROR_USUARIO_EXISTENTE, 409)
      );
    }
    return throwable;
  }

  private void validateUserParameters(UserParameters userParameters) {
    validateRequiredFields(userParameters);
    validateSalaryRange(userParameters);
  }

  private void validateRequiredFields(UserParameters userParameters) {
    boolean anyNullOrEmpty = java.util.stream.Stream.of(
            userParameters.getNombres(),
            userParameters.getApellidos(),
            userParameters.getDireccion(),
            userParameters.getTelefono(),
            userParameters.getCorreoElectronico()
    ).anyMatch(value -> value == null || value.isEmpty());

    if (anyNullOrEmpty || userParameters.getFechaNacimiento() == null || userParameters.getSalarioBase() == null) {
      throw new BusinessException(
              ExceptionType.BAD_REQUEST,
              new ErrorResponse(Constants.MISSING_FIELDS_CODE, Constants.ERROR_ELEMENTOS_NECESARIOS, 400)
      );
    }
  }

  private void validateSalaryRange(UserParameters userParameters) {
    if (userParameters.getSalarioBase().compareTo(Constants.SALARIO_MINIMO) < 0 ||
            userParameters.getSalarioBase().compareTo(Constants.SALARIO_MAXIMO) > 0) {
      throw new BusinessException(
              ExceptionType.BAD_REQUEST,
              new ErrorResponse(Constants.INVALID_SALARY_CODE, Constants.ERROR_SALARIO_INVALIDO, 400)
      );
    }
  }

  public Mono<UserParameters> findByDocumentNumber(String documentNumber) {
    log.info(Constants.LOG_FINDING_USER_BY_DOCUMENT + documentNumber);
    return userGateway.findByDocumentNumber(documentNumber)
            .switchIfEmpty(Mono.error(new BusinessException(
                    ExceptionType.NOT_FOUND,
                    new ErrorResponse(Constants.USER_NOT_FOUND_CODE, Constants.USER_NOT_FOUND_MESSAGE, 404)
            )))
            .doOnSuccess(user -> log.info(Constants.LOG_USER_FOUND_BY_DOCUMENT + documentNumber))
            .doOnError(error -> log.severe(Constants.LOG_ERROR_FINDING_USER_BY_DOCUMENT + error.getMessage()));
  }

}