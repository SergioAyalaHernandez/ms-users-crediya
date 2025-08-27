package co.com.pragma.usecase.utils;

public class Constants {
  public static final java.math.BigDecimal SALARIO_MINIMO = new java.math.BigDecimal("1");
  public static final java.math.BigDecimal SALARIO_MAXIMO = new java.math.BigDecimal("15000000");
  public static final String ERROR_ELEMENTOS_NECESARIOS = "Todos los elementos son necesarios";
  public static final String ERROR_SALARIO_INVALIDO = "Salario base debe ser positivo y menor o igual a 15,000,000";
  public static final String ERROR_USUARIO_EXISTENTE = "El usuario con el correo electrónico ya existe";
  // Log messages
  public static final String LOG_CREATING_USER = "Creating user...";
  public static final String LOG_USER_PARAMETERS_VALIDATED = "User parameters validated successfully";
  public static final String LOG_EMAIL_EXISTS = "Email already exists: ";
  public static final String LOG_EMAIL_AVAILABLE = "Email available, creating user in repository";
  public static final String LOG_ERROR_USER_CREATION = "Error in user creation flow: ";
  public static final String LOG_ERROR_CREATING_USER = "Error creating user: ";
  public static final String LOG_VALIDATING_USER_PARAMETERS = "Validating user parameters";
  public static final String LOG_ALL_PARAMETERS_VALIDATED = "All user parameters validated successfully";
  public static final String LOG_VALIDATING_REQUIRED_FIELDS = "Validating required fields";
  public static final String LOG_REQUIRED_FIELDS_FAILED = "Required fields validation failed - missing fields";
  public static final String LOG_REQUIRED_FIELDS_PRESENT = "All required fields are present";
  public static final String LOG_VALIDATING_SALARY = "Validating salary range: ";
  public static final String LOG_SALARY_VALIDATION_FAILED = "Salary validation failed - salary outside valid range";
  public static final String LOG_SALARY_VALIDATION_SUCCESS = "Salary validation successful";

}
