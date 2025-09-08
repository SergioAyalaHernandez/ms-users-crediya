package co.com.pragma.r2dbc.utils;

public class Constants {
  public static final String VALIDATING_USER_PARAMETERS = "Validando parámetros del usuario: ";
  public static final String VALIDATION_FAILED = "Falló la validación de parámetros: ";
  public static final String VALIDATION_SUCCESSFUL = "Validación de parámetros exitosa.";
  public static final String VALIDATION_EXCEPTION_MESSAGE = "Validation failed";
  public static final String EMAIL_ALREADY_IN_USE = "El correo electrónico ya está en uso";
  public static final String CHECKING_EMAIL_EXISTENCE = "Verificando si el correo electrónico ya existe: ";
  public static final String EMAIL_EXISTENCE_RESULT = "Resultado de existsByCorreoElectronico para ";
  public static final String CREATING_USER_INIT = "Iniciando creación de usuario con parámetros: ";
  public static final String USER_CREATED_SUCCESSFULLY = "Usuario creado exitosamente: ";
  public static final String DATA_INTEGRITY_VIOLATION = "Violación de integridad de datos al crear el usuario: ";
  public static final String MISSING_REQUIRED_FIELDS = "Faltan campos obligatorios";
  public static final String UNEXPECTED_ERROR_CREATING_USER = "Error inesperado al crear el usuario: ";
  public static final String UNEXPECTED_ERROR_MESSAGE = "Error inesperado al crear el usuario";

  // Constantes para búsqueda de usuario por documento
  public static final String SEARCHING_USER_BY_DOCUMENT = "Buscando usuario por número de documento: ";
  public static final String USER_FOUND_BY_DOCUMENT = "Usuario encontrado por número de documento: ";
  public static final String USER_NOT_FOUND_BY_DOCUMENT = "No se encontró usuario con número de documento: ";
  public static final String ERROR_FINDING_USER_BY_DOCUMENT = "Error al buscar usuario por número de documento: ";
  public static final String SEARCHING_USER_BY_EMAIL =" Buscando usuario por correo electrónico: ";
  public static final String USER_FOUND_BY_EMAIL = "Usuario encontrado por correo electrónico: ";
  public static final String USER_NOT_FOUND_BY_EMAIL = "No se encontró usuario con correo electrónico";
  public static final String USER_NOT_FOUND = "El usuario con el documento especificado ";

  // Constantes roles
  public static final String ROLE_USER = "USER";
  public static final String ROLE_ADMIN = "ADMIN";
  public static final String ROLE_ASESOR = "ASESOR";
}
