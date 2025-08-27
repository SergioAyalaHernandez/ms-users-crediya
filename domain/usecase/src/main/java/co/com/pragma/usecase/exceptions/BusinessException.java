package co.com.pragma.usecase.exceptions;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
  private final ExceptionType type;
  private final ErrorResponse errorResponse;

  public BusinessException(ExceptionType type, ErrorResponse errorResponse) {
    super(errorResponse.getMessage());
    this.type = type;
    this.errorResponse = errorResponse;
  }
}