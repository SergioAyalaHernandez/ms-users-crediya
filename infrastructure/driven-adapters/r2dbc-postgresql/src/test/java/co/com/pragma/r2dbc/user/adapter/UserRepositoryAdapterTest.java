package co.com.pragma.r2dbc.user.adapter;

import co.com.pragma.model.user.user.UserParameters;
import co.com.pragma.r2dbc.exceptions.RepositoryException;
import co.com.pragma.r2dbc.user.entity.User;
import co.com.pragma.r2dbc.user.mapper.UserMapper;
import co.com.pragma.r2dbc.user.repository.UserRepository;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserRepositoryAdapterTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private Validator validator;

  @InjectMocks
  private UserRepositoryAdapter userRepositoryAdapter;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createUser_ShouldSaveUser_WhenValidParameters() {
    UserParameters userParameters = new UserParameters();
    userParameters.setCorreoElectronico("test@example.com");

    when(validator.validate(userParameters)).thenReturn(Collections.emptySet());
    when(userRepository.existsByCorreoElectronico("test@example.com")).thenReturn(Mono.just(false));
    when(userMapper.toEntity(userParameters)).thenReturn(new User());
    when(userRepository.save(any(User.class))).thenReturn(Mono.just(new User()));
    when(userMapper.toDto(any(User.class))).thenReturn(userParameters);

    StepVerifier.create(userRepositoryAdapter.createUser(userParameters))
            .expectNext(userParameters)
            .verifyComplete();

    verify(userRepository).existsByCorreoElectronico("test@example.com");
    verify(userRepository).save(any(User.class));
  }


  @Test
  void createUser_ShouldThrowException_WhenEmailAlreadyExists() {
    UserParameters userParameters = new UserParameters();
    userParameters.setCorreoElectronico("test@example.com");

    when(validator.validate(userParameters)).thenReturn(Collections.emptySet());
    when(userRepository.existsByCorreoElectronico("test@example.com")).thenReturn(Mono.just(true));

    StepVerifier.create(userRepositoryAdapter.createUser(userParameters))
            .expectErrorSatisfies(throwable -> {
              assert throwable instanceof RepositoryException;
              assert throwable.getMessage().equals("El correo electrónico ya está en uso");
            })
            .verify();

    verify(userRepository).existsByCorreoElectronico("test@example.com");
    verify(userRepository, never()).save(any(User.class));
  }
}