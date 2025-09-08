package co.com.pragma.r2dbc.user.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("USERS")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column("id")
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column("nombres")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Column("apellidos")
    private String apellidos;

    @Column("fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column("direccion")
    private String direccion;

    @Column("telefono")
    private String telefono;

    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico no es válido")
    @Column("correo_electronico")
    private String correoElectronico;

    @NotNull(message = "El salario base es obligatorio")
    @Min(value = 0, message = "El salario base debe ser mayor o igual a 0")
    @Max(value = 15000000, message = "El salario base no puede superar los 15.000.000")
    @Column("salario_base")
    private Double salarioBase;

    @NotNull(message = "El número de documento es obligatorio")
    @Column("numero_documento")
    private BigDecimal numeroDocumento;

    private String role;

    private String password;

}
