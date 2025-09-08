package co.com.pragma.r2dbc.user.adapter;

import co.com.pragma.model.user.user.UserParameters;
import co.com.pragma.r2dbc.exceptions.DataIntegrityViolationException;
import co.com.pragma.r2dbc.exceptions.RepositoryException;
import co.com.pragma.r2dbc.user.entity.User;
import co.com.pragma.r2dbc.user.mapper.UserMapper;
import co.com.pragma.r2dbc.user.repository.UserRepository;
import co.com.pragma.r2dbc.utils.Constants;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private Validator validator;

    @InjectMocks
    private UserRepositoryAdapter adapter;

    private UserParameters userParameters;
    private User userEntity;

    @BeforeEach
    void setUp() {
        userParameters = new UserParameters();
        userParameters.setCorreoElectronico("test@example.com");

        userEntity = new User();
    }

    @Test
    void createUser_Success() {
        // Arrange
        Set<ConstraintViolation<UserParameters>> emptyViolations = new HashSet<>();
        when(validator.validate(userParameters)).thenReturn(emptyViolations);
        when(userMapper.toEntity(userParameters)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(Mono.just(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userParameters);

        // Act & Assert
        StepVerifier.create(adapter.createUser(userParameters))
                .expectNext(userParameters)
                .verifyComplete();

        verify(validator).validate(userParameters);
        verify(userMapper).toEntity(userParameters);
        verify(userRepository).save(userEntity);
        verify(userMapper).toDto(userEntity);
    }

    @Test
    void createUser_ValidationFailed() {
        // Arrange
        Set<ConstraintViolation<UserParameters>> violations = new HashSet<>();
        ConstraintViolation<UserParameters> violation = mock(ConstraintViolation.class);
        violations.add(violation);
        when(validator.validate(userParameters)).thenReturn(violations);

        // Act & Assert
        StepVerifier.create(adapter.createUser(userParameters))
                .expectErrorSatisfies(throwable -> {
                    assertTrue(throwable instanceof RepositoryException);
                    assertEquals("Error inesperado al crear el usuario", throwable.getMessage());

                    Throwable cause = throwable.getCause();
                    assertTrue(cause instanceof RepositoryException);
                    assertEquals("Validation failed", cause.getMessage());

                    Throwable rootCause = cause.getCause();
                    assertTrue(rootCause instanceof ConstraintViolationException);
                })
                .verify();

        verify(validator).validate(userParameters);
        verify(userMapper, never()).toEntity(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_DataIntegrityViolation() {
        // Arrange
        Set<ConstraintViolation<UserParameters>> emptyViolations = new HashSet<>();
        when(validator.validate(userParameters)).thenReturn(emptyViolations);
        when(userMapper.toEntity(userParameters)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(Mono.error(new DataIntegrityViolationException("Error")));

        // Act & Assert
        StepVerifier.create(adapter.createUser(userParameters))
                .expectErrorSatisfies(throwable -> {
                    assertTrue(throwable instanceof RepositoryException);
                    assertEquals(Constants.MISSING_REQUIRED_FIELDS, throwable.getMessage());
                })
                .verify();

        verify(validator).validate(userParameters);
        verify(userMapper).toEntity(userParameters);
        verify(userRepository).save(userEntity);
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void createUser_UnexpectedException() {
        // Arrange
        Set<ConstraintViolation<UserParameters>> emptyViolations = new HashSet<>();
        when(validator.validate(userParameters)).thenReturn(emptyViolations);
        when(userMapper.toEntity(userParameters)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(Mono.error(new RuntimeException("Unexpected error")));

        // Act & Assert
        StepVerifier.create(adapter.createUser(userParameters))
                .expectErrorSatisfies(throwable -> {
                    assertTrue(throwable instanceof RepositoryException);
                    assertEquals(Constants.UNEXPECTED_ERROR_MESSAGE, throwable.getMessage());
                })
                .verify();

        verify(validator).validate(userParameters);
        verify(userMapper).toEntity(userParameters);
        verify(userRepository).save(userEntity);
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void existsByCorreoElectronico_EmailExists() {
        // Arrange
        String email = "existing@example.com";
        when(userRepository.existsByCorreoElectronico(email)).thenReturn(Mono.just(true));

        // Act & Assert
        StepVerifier.create(adapter.existsByCorreoElectronico(email))
                .expectErrorSatisfies(throwable -> {
                    assertTrue(throwable instanceof RepositoryException);
                    assertEquals(Constants.EMAIL_ALREADY_IN_USE, throwable.getMessage());
                })
                .verify();

        verify(userRepository).existsByCorreoElectronico(email);
    }

    @Test
    void existsByCorreoElectronico_EmailDoesNotExist() {
        // Arrange
        String email = "new@example.com";
        when(userRepository.existsByCorreoElectronico(email)).thenReturn(Mono.just(false));

        // Act & Assert
        StepVerifier.create(adapter.existsByCorreoElectronico(email))
                .expectNext(false)
                .verifyComplete();

        verify(userRepository).existsByCorreoElectronico(email);
    }

    @Test
    void findByDocumentNumber_WhenEmpty_ShouldExecuteDeferredLogging() {
        // Given
        String documentNumber = "12345678";
        when(userRepository.findByNumeroDocumento(documentNumber))
                .thenReturn(Mono.empty());

        // When
        StepVerifier.create(userRepository.findByNumeroDocumento(documentNumber))
                .expectComplete()
                .verify();

        // Then
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void findByDocumentNumber_UserExists() {
        // Arrange
        String documentNumber = "123456789";
        User userEntity = new User();
        userEntity.setNumeroDocumento(new BigDecimal(123456789));

        when(userRepository.findByNumeroDocumento(documentNumber)).thenReturn(Mono.just(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userParameters);

        // Act & Assert
        StepVerifier.create(adapter.findByDocumentNumber(documentNumber))
                .expectNext(userParameters)
                .verifyComplete();

        verify(userRepository).findByNumeroDocumento(documentNumber);
        verify(userMapper).toDto(userEntity);
    }

    @Test
    void findByDocumentNumber_UserNotFound() {
        // Arrange
        String documentNumber = "nonexistent123";
        when(userRepository.findByNumeroDocumento(documentNumber)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(adapter.findByDocumentNumber(documentNumber))
                .verifyComplete();

        verify(userRepository).findByNumeroDocumento(documentNumber);
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void findByDocumentNumber_ErrorOccurs() {
        // Arrange
        String documentNumber = "123456789";
        RuntimeException exception = new RuntimeException("Error al buscar usuario");
        when(userRepository.findByNumeroDocumento(documentNumber)).thenReturn(Mono.error(exception));

        // Act & Assert
        StepVerifier.create(adapter.findByDocumentNumber(documentNumber))
                .expectErrorSatisfies(throwable -> {
                    assertTrue(throwable instanceof RepositoryException);
                    assertEquals(Constants.UNEXPECTED_ERROR_MESSAGE, throwable.getMessage());
                    assertSame(exception, throwable.getCause());
                })
                .verify();

        verify(userRepository).findByNumeroDocumento(documentNumber);
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void findByCorreoElectronico_UserExists() {
        // Arrange
        String email = "test@example.com";
        User userEntity = new User();
        userEntity.setCorreoElectronico(email);

        when(userRepository.findByCorreoElectronico(email)).thenReturn(Mono.just(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userParameters);

        // Act & Assert
        StepVerifier.create(adapter.findByCorreoElectronico(email))
                .expectNext(userParameters)
                .verifyComplete();

        verify(userRepository).findByCorreoElectronico(email);
        verify(userMapper).toDto(userEntity);
    }

    @Test
    void findByCorreoElectronico_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        when(userRepository.findByCorreoElectronico(email)).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(adapter.findByCorreoElectronico(email))
                .verifyComplete();

        verify(userRepository).findByCorreoElectronico(email);
        verify(userMapper, never()).toDto(any());
    }

    @Test
    void findByCorreoElectronico_ErrorOccurs() {
        // Arrange
        String email = "test@example.com";
        RuntimeException exception = new RuntimeException("Error al buscar usuario por correo");
        when(userRepository.findByCorreoElectronico(email)).thenReturn(Mono.error(exception));

        // Act & Assert
        StepVerifier.create(adapter.findByCorreoElectronico(email))
                .expectErrorSatisfies(throwable -> {
                    assertTrue(throwable instanceof RepositoryException);
                    assertEquals(Constants.UNEXPECTED_ERROR_MESSAGE, throwable.getMessage());
                    assertSame(exception, throwable.getCause());
                })
                .verify();

        verify(userRepository).findByCorreoElectronico(email);
        verify(userMapper, never()).toDto(any());
    }
}