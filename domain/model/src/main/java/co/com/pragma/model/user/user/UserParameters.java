package co.com.pragma.model.user.user;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserParameters {
  private Long id;
  private String nombres;
  private String apellidos;
  private LocalDate fechaNacimiento;
  private String direccion;
  private String telefono;
  private String correoElectronico;
  private BigDecimal salarioBase;
  private BigDecimal numeroDocumento;
  private String role;
  private String password;
}
