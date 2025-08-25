package co.com.pragma.r2dbc.user.mapper;

import co.com.pragma.model.user.user.UserParameters;
import co.com.pragma.r2dbc.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
  @Mapping(target = "id", ignore = true)
  User toEntity(UserParameters userParameters);

  UserParameters toDto(User user);
}
