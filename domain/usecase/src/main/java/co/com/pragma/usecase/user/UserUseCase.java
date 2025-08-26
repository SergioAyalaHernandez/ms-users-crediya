package co.com.pragma.usecase.user;

import co.com.pragma.model.user.user.UserParameters;
import co.com.pragma.model.user.user.gateways.UserGateway;
import co.com.pragma.usecase.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Mono;

@Log
@RequiredArgsConstructor
public class UserUseCase {
  private final UserGateway userGateway;

  public Mono<Object> createUser(UserParameters userParameters) {
    log.info("Creating user...");
    validateUserParameters(userParameters);
    return Mono.just(userParameters.getCorreoElectronico())
            .flatMap(userGateway::existsByCorreoElectronico)
            .flatMap(exists -> {
              if (exists) {
                return Mono.error(new IllegalArgumentException(Constants.ERROR_USUARIO_EXISTENTE));
              }
              return userGateway.createUser(userParameters);
            });
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

    if (anyNullOrEmpty
            || userParameters.getFechaNacimiento() == null
            || userParameters.getSalarioBase() == null) {
      throw new IllegalArgumentException(Constants.ERROR_ELEMENTOS_NECESARIOS);
    }
  }

  private void validateSalaryRange(UserParameters userParameters) {
    if (userParameters.getSalarioBase().compareTo(Constants.SALARIO_MINIMO) < 0
            || userParameters.getSalarioBase().compareTo(Constants.SALARIO_MAXIMO) > 0) {
      {
        throw new IllegalArgumentException(Constants.ERROR_SALARIO_INVALIDO);
      }
    }
  }

}
