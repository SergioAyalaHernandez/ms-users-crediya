package co.com.pragma.api.utils;

public class Constants {
  //authHandlers
  public static final int FORBIDDEN_STATUS = 403;
  public static final String TOKEN_KEY = "token";
  public static final String ERROR_KEY = "error";

  public static final String API_BASE_PATH = "/api/v1";
  public static final String LOGIN_PATH = API_BASE_PATH + "/login";
  public static final String USERS_PATH = API_BASE_PATH + "/usuarios";
  public static final String USERS_PATH_WILDCARD = USERS_PATH + "/**";
  public static final String REQUESTS_PATH_WILDCARD = API_BASE_PATH + "/solicitudes/**";
  public static final String REQUESTS_PATH_users = API_BASE_PATH + "/usuarios/{documentNumber}";
  // Roles
  public static final String ROLE_ADMIN = "ADMIN";
  public static final String ROLE_ADVISOR = "ASESOR";
  public static final String ROLE_USER = "USER";
  public static final String ROLE_CLIENT = "CLIENTE";
  public static final String ROLE_PREFIX = "ROLE_";

  // Auth
  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String BEARER_PREFIX = "Bearer ";
  public static final String WEBJARS_PATH = "/webjars/**";
  public static final String SWAGGER_UI_PATH = "/swagger-ui/**";
  public static final String SWAGGER_RESOURCES_PATH = "/swagger-resources/**";
  public static final String[] API_DOCS_PATHS = {"/v3/api-docs/**", "/v2/api-docs/**"};
  public static final String TOKEN_ERROR = "Token inválido";

  public static final String LOG_CREATE_USER = "Iniciando creación de usuario";
  public static final String LOG_BUSINESS_ERROR = "Error de negocio: ";
  public static final String LOG_UNEXPECTED_ERROR = "Error inesperado: ";
  public static final String LOG_FIND_USER = "Iniciando búsqueda de usuario por número de documento";
  public static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
  public static final String UNEXPECTED_ERROR_MESSAGE = "Ocurrió un error inesperado";
  public static final String DOCUMENT_NUMBER_PARAM = "documentNumber";
}
