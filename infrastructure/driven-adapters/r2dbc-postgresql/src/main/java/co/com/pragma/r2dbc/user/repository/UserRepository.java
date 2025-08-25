package co.com.pragma.r2dbc.user.repository;

import co.com.pragma.r2dbc.user.entity.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface UserRepository extends ReactiveCrudRepository<User, String> {
}
