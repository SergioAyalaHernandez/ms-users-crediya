package co.com.pragma.usecase.user;

import co.com.pragma.model.user.user.UserParameters;
import co.com.pragma.model.user.user.gateways.UserGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class UserUseCaseTest {

  @Mock
  private UserGateway userGateway;

  @InjectMocks
  private UserUseCase userUseCase;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createUser_ShouldReturnMonoObject() {
    // Arrange
    UserParameters userParameters = new UserParameters();
    when(userGateway.createUser(userParameters)).thenReturn(Mono.just(new Object()));

    // Act
    Mono<Object> result = userUseCase.createUser(userParameters);

    // Assert
    assertNotNull(result);
    verify(userGateway, times(1)).createUser(userParameters);
  }
}