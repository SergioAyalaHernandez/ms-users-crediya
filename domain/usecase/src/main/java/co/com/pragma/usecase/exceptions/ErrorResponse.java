package co.com.pragma.usecase.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
  private String code;
  private String message;
  private int status;
}