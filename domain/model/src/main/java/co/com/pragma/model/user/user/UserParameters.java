package co.com.pragma.model.user.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserParameters {
  private String nombres;
  private String apellidos;
  private LocalDate fechaNacimiento;
  private String direccion;
  private String telefono;
  private String correoElectronico;
  private BigDecimal salarioBase;
}
