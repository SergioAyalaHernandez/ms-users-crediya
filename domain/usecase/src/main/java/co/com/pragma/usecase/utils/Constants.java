package co.com.pragma.usecase.utils;

public class Constants {
  public static final java.math.BigDecimal SALARIO_MINIMO = java.math.BigDecimal.ZERO;
  public static final java.math.BigDecimal SALARIO_MAXIMO = new java.math.BigDecimal("15000000");
  public static final String ERROR_ELEMENTOS_NECESARIOS = "Todos los elementos son necesarios";
  public static final String ERROR_SALARIO_INVALIDO = "Salario base debe ser positivo y menor o igual a 15,000,000";
  public static final String ERROR_USUARIO_EXISTENTE = "El usuario con el correo electrónico ya existe";
}
