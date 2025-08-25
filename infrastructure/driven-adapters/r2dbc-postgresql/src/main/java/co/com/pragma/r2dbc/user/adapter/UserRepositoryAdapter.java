package co.com.pragma.r2dbc.user.adapter;

import co.com.pragma.model.user.user.UserParameters;
import co.com.pragma.model.user.user.gateways.UserGateway;
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
  public Mono<UserParameters> createUser(UserParameters userParameters) {
    Set<ConstraintViolation<UserParameters>> violations = validator.validate(userParameters);
    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }
    User userEntity = userMapper.toEntity(userParameters);
    return userRepository.save(userEntity)
            .map(userMapper::toDto);
  }
}
