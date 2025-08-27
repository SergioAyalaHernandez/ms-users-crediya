package co.com.pragma.r2dbc.user.mapper;

import co.com.pragma.model.user.user.UserParameters;
import co.com.pragma.r2dbc.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

  private UserMapper userMapper;

  @BeforeEach
  void setUp() {
    userMapper = Mappers.getMapper(UserMapper.class);
  }

  @Test
  void toEntity_shouldMapCorrectly() {
    // Arrange
    UserParameters userParameters = new UserParameters();
    userParameters.setNombres("John Doe");
    userParameters.setApellidos("Smith Johnson");
    userParameters.setCorreoElectronico("john@example.com");
    userParameters.setTelefono("123456789");
    userParameters.setDireccion("Calle Principal 123");
    userParameters.setFechaNacimiento(java.time.LocalDate.of(1990, 5, 15));
    userParameters.setSalarioBase(BigDecimal.valueOf(2500.0));
    // Act
    User user = userMapper.toEntity(userParameters);

    // Assert
    assertNotNull(user);
    assertNull(user.getId());
    assertEquals(userParameters.getNombres(), user.getNombres());
    assertEquals(userParameters.getApellidos(), user.getApellidos());
    assertEquals(userParameters.getCorreoElectronico(), user.getCorreoElectronico());
    assertEquals(userParameters.getTelefono(), user.getTelefono());
    assertEquals(userParameters.getDireccion(), user.getDireccion());
    assertEquals(userParameters.getFechaNacimiento(), user.getFechaNacimiento());
    assertEquals(userParameters.getSalarioBase().doubleValue(), user.getSalarioBase());
  }

  @Test
  void toDto_shouldMapCorrectly() {
    // Arrange
    User user = new User();
    user.setId(1L);
    user.setNombres("Jane Doe");
    user.setCorreoElectronico("jane@example.com");
    user.setTelefono("12345");
    user.setDireccion("123 Main St");
    user.setFechaNacimiento(java.time.LocalDate.of(1990, 1, 1));
    user.setSalarioBase(1000.0);

    // Act
    UserParameters userParameters = userMapper.toDto(user);

    // Assert
    assertNotNull(userParameters);
    assertEquals(user.getNombres(), userParameters.getNombres());
    assertEquals(user.getCorreoElectronico(), userParameters.getCorreoElectronico());
    assertEquals(user.getTelefono(), userParameters.getTelefono());
    assertEquals(user.getDireccion(), userParameters.getDireccion());
    assertEquals(user.getFechaNacimiento(), userParameters.getFechaNacimiento());
    assertEquals(user.getSalarioBase(), userParameters.getSalarioBase().doubleValue());
  }

  @Test
  void toEntity_withNullParameter_shouldReturnNull() {
    // Act & Assert
    assertNull(userMapper.toEntity(null));
  }

  @Test
  void toDto_withNullParameter_shouldReturnNull() {
    // Act & Assert
    assertNull(userMapper.toDto(null));
  }
}