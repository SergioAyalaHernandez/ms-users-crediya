package co.com.pragma.usecase.user;

import co.com.pragma.model.user.user.UserParameters;
import co.com.pragma.model.user.user.gateways.UserGateway;
import co.com.pragma.usecase.exceptions.BusinessException;
import co.com.pragma.usecase.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserUseCaseTest {
  @Mock
  private UserGateway userGateway;

  @InjectMocks
  private UserUseCase userUseCase;

  private UserParameters validUserParameters;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    validUserParameters = new UserParameters();
    validUserParameters.setNombres("Juan");
    validUserParameters.setApellidos("Pérez");
    validUserParameters.setDireccion("Calle 123");
    validUserParameters.setTelefono("1234567890");
    validUserParameters.setCorreoElectronico("juan@ejemplo.com");
    validUserParameters.setFechaNacimiento(LocalDate.of(1990, 1, 1));
    validUserParameters.setSalarioBase(new BigDecimal("2000000"));
  }

  private UserParameters buildUserWithSalary(BigDecimal salario) {
    UserParameters user = new UserParameters();
    user.setNombres("Juan");
    user.setApellidos("Pérez");
    user.setDireccion("Calle 123");
    user.setTelefono("1234567890");
    user.setCorreoElectronico("juan@ejemplo.com");
    user.setFechaNacimiento(LocalDate.of(1990, 1, 1));
    user.setSalarioBase(salario);
    return user;
  }

  @Test
  @DisplayName("Debería crear un usuario exitosamente")
  void shouldCreateUserSuccessfully() {
    // Arrange
    when(userGateway.existsByCorreoElectronico("juan@ejemplo.com")).thenReturn(Mono.just(false));
    when(userGateway.createUser(validUserParameters)).thenReturn(Mono.just(validUserParameters));

    // Act & Assert
    StepVerifier.create(userUseCase.createUser(validUserParameters))
            .expectNext(validUserParameters)
            .verifyComplete();

    verify(userGateway).existsByCorreoElectronico("juan@ejemplo.com");
    verify(userGateway).createUser(validUserParameters);
  }

  @Test
  @DisplayName("Debería lanzar excepción cuando el correo ya existe")
  void shouldThrowExceptionWhenEmailAlreadyExists() {
    // Arrange
    when(userGateway.existsByCorreoElectronico("juan@ejemplo.com")).thenReturn(Mono.just(true));

    // Act & Assert
    StepVerifier.create(userUseCase.createUser(validUserParameters))
            .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                    ((BusinessException) throwable).getErrorResponse().getCode().equals("USER_EXISTS"))
            .verify();

    verify(userGateway).existsByCorreoElectronico("juan@ejemplo.com");
    verify(userGateway, never()).createUser(any());
  }

  @Test
  @DisplayName("Debería lanzar excepción cuando faltan campos requeridos")
  void shouldThrowExceptionWhenRequiredFieldsAreMissing() {
    // Arrange
    UserParameters invalidUser = new UserParameters();
    invalidUser.setNombres("Juan");
    invalidUser.setApellidos(null); // Campo requerido faltante

    // Act & Assert
    StepVerifier.create(userUseCase.createUser(invalidUser))
            .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                    ((BusinessException) throwable).getErrorResponse().getCode().equals("MISSING_FIELDS"))
            .verify();

    verify(userGateway, never()).existsByCorreoElectronico(any());
    verify(userGateway, never()).createUser(any());
  }


  @Test
  @DisplayName("Debería lanzar excepción cuando el salario está por encima del máximo")
  void shouldThrowExceptionWhenSalaryAboveMaximum() {
    // Arrange
    UserParameters userWithHighSalary = new UserParameters();
    userWithHighSalary.setNombres("Juan");
    userWithHighSalary.setApellidos("Pérez");
    userWithHighSalary.setDireccion("Calle 123");
    userWithHighSalary.setTelefono("1234567890");
    userWithHighSalary.setCorreoElectronico("juan@ejemplo.com");
    userWithHighSalary.setFechaNacimiento(LocalDate.of(1990, 1, 1));
    userWithHighSalary.setSalarioBase(new BigDecimal("100000000")); // Por encima del máximo

    // Act & Assert
    StepVerifier.create(userUseCase.createUser(userWithHighSalary))
            .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                    ((BusinessException) throwable).getErrorResponse().getCode().equals("INVALID_SALARY"))
            .verify();

    verify(userGateway, never()).existsByCorreoElectronico(any());
    verify(userGateway, never()).createUser(any());
  }

  @Test
  @DisplayName("Debería manejar errores del gateway y propagarlos")
  void shouldHandleGatewayErrors() {
    // Arrange
    RuntimeException gatewayException = new RuntimeException("Error en gateway");
    when(userGateway.existsByCorreoElectronico("juan@ejemplo.com")).thenReturn(Mono.error(gatewayException));

    // Act & Assert
    StepVerifier.create(userUseCase.createUser(validUserParameters))
            .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                    throwable.getMessage().equals("Error en gateway"))
            .verify();

    verify(userGateway).existsByCorreoElectronico("juan@ejemplo.com");
    verify(userGateway, never()).createUser(any());
  }
  @Test
  @DisplayName("Debería validar correctamente los límites del rango salarial")
  void shouldValidateSalaryRangeBoundaries() {
    // Caso límite inferior exacto
    UserParameters userWithMinSalary = new UserParameters();
    userWithMinSalary.setNombres(validUserParameters.getNombres());
    userWithMinSalary.setApellidos(validUserParameters.getApellidos());
    userWithMinSalary.setDireccion(validUserParameters.getDireccion());
    userWithMinSalary.setTelefono(validUserParameters.getTelefono());
    userWithMinSalary.setCorreoElectronico(validUserParameters.getCorreoElectronico());
    userWithMinSalary.setFechaNacimiento(validUserParameters.getFechaNacimiento());
    userWithMinSalary.setSalarioBase(new BigDecimal("1000000")); // Asumiendo que este es el mínimo

    // Simulamos que el correo no existe para el caso de salario mínimo
    when(userGateway.existsByCorreoElectronico(userWithMinSalary.getCorreoElectronico())).thenReturn(Mono.just(false));
    when(userGateway.createUser(userWithMinSalary)).thenReturn(Mono.just(userWithMinSalary));

    StepVerifier.create(userUseCase.createUser(userWithMinSalary))
            .expectNext(userWithMinSalary)
            .verifyComplete();

    verify(userGateway).existsByCorreoElectronico(userWithMinSalary.getCorreoElectronico());
    verify(userGateway).createUser(userWithMinSalary);
    reset(userGateway); // Reseteamos los mocks para la siguiente prueba

    // Caso límite superior exacto
    UserParameters userWithMaxSalary = new UserParameters();
    userWithMaxSalary.setNombres(validUserParameters.getNombres());
    userWithMaxSalary.setApellidos(validUserParameters.getApellidos());
    userWithMaxSalary.setDireccion(validUserParameters.getDireccion());
    userWithMaxSalary.setTelefono(validUserParameters.getTelefono());
    userWithMaxSalary.setCorreoElectronico(validUserParameters.getCorreoElectronico());
    userWithMaxSalary.setFechaNacimiento(validUserParameters.getFechaNacimiento());
    userWithMaxSalary.setSalarioBase(new BigDecimal("10000000")); // Asumiendo que este es el máximo

    // Simulamos que el correo no existe para el caso de salario máximo
    when(userGateway.existsByCorreoElectronico(userWithMaxSalary.getCorreoElectronico())).thenReturn(Mono.just(false));
    when(userGateway.createUser(userWithMaxSalary)).thenReturn(Mono.just(userWithMaxSalary));

    StepVerifier.create(userUseCase.createUser(userWithMaxSalary))
            .expectNext(userWithMaxSalary)
            .verifyComplete();

    verify(userGateway).existsByCorreoElectronico(userWithMaxSalary.getCorreoElectronico());
    verify(userGateway).createUser(userWithMaxSalary);
  }

  @Test
  @DisplayName("Debería lanzar excepción cuando el salario está por debajo del mínimo")
  void shouldThrowExceptionWhenSalaryBelowMinimum() {
    // Arrange
    UserParameters userWithLowSalary = new UserParameters();
    userWithLowSalary.setNombres("Juan");
    userWithLowSalary.setApellidos("Pérez");
    userWithLowSalary.setDireccion("Calle 123");
    userWithLowSalary.setTelefono("1234567890");
    userWithLowSalary.setCorreoElectronico("juan@ejemplo.com");
    userWithLowSalary.setFechaNacimiento(LocalDate.of(1990, 1, 1));
    userWithLowSalary.setSalarioBase(new BigDecimal("-90"));

    when(userGateway.existsByCorreoElectronico(anyString())).thenReturn(Mono.just(false));
    when(userGateway.createUser(any(UserParameters.class))).thenReturn(Mono.just(userWithLowSalary));

    // Act & Assert
    StepVerifier.create(userUseCase.createUser(userWithLowSalary))
            .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                    ((BusinessException) throwable).getErrorResponse().getCode().equals("INVALID_SALARY"))
            .verify();

    verify(userGateway, never()).existsByCorreoElectronico(any());
    verify(userGateway, never()).createUser(any());
  }

  @Test
  @DisplayName("Debería validar correctamente un salario dentro del rango")
  void shouldValidateSalaryWithinRange() {
    // Arrange
    UserParameters userWithValidSalary = new UserParameters();
    userWithValidSalary.setNombres("Juan");
    userWithValidSalary.setApellidos("Pérez");
    userWithValidSalary.setDireccion("Calle 123");
    userWithValidSalary.setTelefono("1234567890");
    userWithValidSalary.setCorreoElectronico("juan@ejemplo.com");
    userWithValidSalary.setFechaNacimiento(LocalDate.of(1990, 1, 1));
    userWithValidSalary.setSalarioBase(new BigDecimal("5000000")); // Valor dentro del rango

    when(userGateway.existsByCorreoElectronico(userWithValidSalary.getCorreoElectronico()))
            .thenReturn(Mono.just(false));
    when(userGateway.createUser(userWithValidSalary))
            .thenReturn(Mono.just(userWithValidSalary));

    // Act & Assert
    StepVerifier.create(userUseCase.createUser(userWithValidSalary))
            .expectNext(userWithValidSalary)
            .verifyComplete();

    verify(userGateway).existsByCorreoElectronico(userWithValidSalary.getCorreoElectronico());
    verify(userGateway).createUser(userWithValidSalary);
  }


  @Test
  @DisplayName("Debería pasar cuando el salario es exactamente igual al mínimo")
  void shouldPassWhenSalaryEqualsMinimum() {
    UserParameters user = buildUserWithSalary(Constants.SALARIO_MINIMO);
    when(userGateway.existsByCorreoElectronico(anyString())).thenReturn(Mono.just(false));
    when(userGateway.createUser(any(UserParameters.class))).thenReturn(Mono.just(user));

    StepVerifier.create(userUseCase.createUser(user))
            .expectNextMatches(u -> u.getSalarioBase().equals(Constants.SALARIO_MINIMO))
            .verifyComplete();
  }

  @Test
  @DisplayName("Debería pasar cuando el salario es exactamente igual al máximo")
  void shouldPassWhenSalaryEqualsMaximum() {
    UserParameters user = buildUserWithSalary(Constants.SALARIO_MAXIMO);
    when(userGateway.existsByCorreoElectronico(anyString())).thenReturn(Mono.just(false));
    when(userGateway.createUser(any(UserParameters.class))).thenReturn(Mono.just(user));

    StepVerifier.create(userUseCase.createUser(user))
            .expectNextMatches(u -> u.getSalarioBase().equals(Constants.SALARIO_MAXIMO))
            .verifyComplete();
  }

  @Test
  @DisplayName("Debería mapear correctamente un error con mensaje de correo en uso")
  void shouldMapErrorWhenEmailAlreadyUsedMessage() {
    UserParameters user = buildUserWithSalary(Constants.SALARIO_MINIMO);

    when(userGateway.existsByCorreoElectronico(anyString()))
            .thenReturn(Mono.error(new RuntimeException("correo electrónico ya está en uso")));

    StepVerifier.create(userUseCase.createUser(user))
            .expectErrorMatches(ex -> ex instanceof BusinessException
                    && ((BusinessException) ex).getErrorResponse().getCode().equals("USER_EXISTS"))
            .verify();
  }

  @Test
  @DisplayName("Debería encontrar usuario por número de documento exitosamente")
  void shouldFindUserByDocumentNumberSuccessfully() {
    // Arrange
    String documentNumber = "12345678";
    UserParameters expectedUser = validUserParameters;
    when(userGateway.findByDocumentNumber(documentNumber)).thenReturn(Mono.just(expectedUser));

    // Act & Assert
    StepVerifier.create(userUseCase.findByDocumentNumber(documentNumber))
            .expectNext(expectedUser)
            .verifyComplete();

    verify(userGateway).findByDocumentNumber(documentNumber);
  }

  @Test
  @DisplayName("Debería lanzar excepción cuando el usuario no existe")
  void shouldThrowExceptionWhenUserNotFound() {
    // Arrange
    String documentNumber = "99999999";
    when(userGateway.findByDocumentNumber(documentNumber)).thenReturn(Mono.empty());

    // Act & Assert
    StepVerifier.create(userUseCase.findByDocumentNumber(documentNumber))
            .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                    ((BusinessException) throwable).getErrorResponse().getCode().equals("USER_NOT_FOUND"))
            .verify();

    verify(userGateway).findByDocumentNumber(documentNumber);
  }

  @Test
  @DisplayName("Debería propagar errores del gateway al buscar por documento")
  void shouldPropagateGatewayErrorsWhenFindingByDocument() {
    // Arrange
    String documentNumber = "12345678";
    RuntimeException gatewayException = new RuntimeException("Error en gateway");
    when(userGateway.findByDocumentNumber(documentNumber)).thenReturn(Mono.error(gatewayException));

    // Act & Assert
    StepVerifier.create(userUseCase.findByDocumentNumber(documentNumber))
            .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                    throwable.getMessage().equals("Error en gateway"))
            .verify();

    verify(userGateway).findByDocumentNumber(documentNumber);
  }
}