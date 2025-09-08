package co.com.pragma.model.user.user;

import lombok.Data;

@Data
public class LoginRequest {
  private String correoElectronico;
  private String password;
}