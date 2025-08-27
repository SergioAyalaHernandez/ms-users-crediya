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
    return Mono.just(userParameters)
            .doOnNext(params -> {
                validateUserParameters(params);
              log.info(Constants.LOG_USER_PARAMETERS_VALIDATED);
            })
            .flatMap(params -> userGateway.existsByCorreoElectronico(params.getCorreoElectronico())
                    .flatMap(exists -> {
                        if (exists) {
                        log.severe(Constants.LOG_EMAIL_EXISTS + params.getCorreoElectronico());
                            return Mono.error(new BusinessException(
                            ExceptionType.ALREADY_EXISTS,
                            new ErrorResponse("USER_EXISTS", Constants.ERROR_USUARIO_EXISTENTE, 409)
                            ));
                        }
                        log.info(Constants.LOG_EMAIL_AVAILABLE);
                    return userGateway.createUser(params);
                    })
            )
                            .onErrorMap(throwable -> {
                        log.severe(Constants.LOG_ERROR_USER_CREATION + throwable.getMessage());
                        if (throwable instanceof BusinessException) {
                            return throwable;
                        }
                        if (throwable.getMessage() != null &&
                            throwable.getMessage().contains("correo electrónico ya está en uso")) {
                                    return new BusinessException(
                                        ExceptionType.ALREADY_EXISTS,
                                        new ErrorResponse("USER_EXISTS", Constants.ERROR_USUARIO_EXISTENTE, 409)
                                    );
                                }
                                return throwable;
                    })
            .doOnError(error -> log.severe(Constants.LOG_ERROR_CREATING_USER + error.getMessage()));
  }

  private void validateUserParameters(UserParameters userParameters) {
    log.info(Constants.LOG_VALIDATING_USER_PARAMETERS);
    validateRequiredFields(userParameters);
    validateSalaryRange(userParameters);
    log.info(Constants.LOG_ALL_PARAMETERS_VALIDATED);
  }

  private void validateRequiredFields(UserParameters userParameters) {
    log.info(Constants.LOG_VALIDATING_REQUIRED_FIELDS);
    boolean anyNullOrEmpty = java.util.stream.Stream.of(
            userParameters.getNombres(),
            userParameters.getApellidos(),
            userParameters.getDireccion(),
            userParameters.getTelefono(),
            userParameters.getCorreoElectronico()
    ).anyMatch(value -> value == null || value.isEmpty());

    if (anyNullOrEmpty
            || userParameters.getFechaNacimiento() == null
            || userParameters.getSalarioBase() == null) {
      log.severe(Constants.LOG_REQUIRED_FIELDS_FAILED);
      throw new BusinessException(
              ExceptionType.BAD_REQUEST,
              new ErrorResponse("MISSING_FIELDS", Constants.ERROR_ELEMENTOS_NECESARIOS, 400)
      );
    }
    log.info(Constants.LOG_REQUIRED_FIELDS_PRESENT);
  }

  private void validateSalaryRange(UserParameters userParameters) {
    log.info(Constants.LOG_VALIDATING_SALARY + userParameters.getSalarioBase());
    if (userParameters.getSalarioBase().compareTo(Constants.SALARIO_MINIMO) < 0
            || userParameters.getSalarioBase().compareTo(Constants.SALARIO_MAXIMO) > 0) {
      log.severe(Constants.LOG_SALARY_VALIDATION_FAILED);
      throw new BusinessException(
              ExceptionType.BAD_REQUEST,
              new ErrorResponse("INVALID_SALARY", Constants.ERROR_SALARIO_INVALIDO, 400)
      );
    }
    log.info(Constants.LOG_SALARY_VALIDATION_SUCCESS);
  }


}