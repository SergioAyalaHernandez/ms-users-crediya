package co.com.pragma.r2dbc.exceptions;

public class DataIntegrityViolationException extends RuntimeException {
  public DataIntegrityViolationException(String message) {
    super(message);
  }

  public DataIntegrityViolationException(String message, Throwable cause) {
    super(message, cause);
  }
}