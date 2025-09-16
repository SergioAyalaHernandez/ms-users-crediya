package co.com.pragma.api;

import co.com.pragma.api.utils.Constants;
import co.com.pragma.model.user.user.UserParameters;
import co.com.pragma.usecase.exceptions.BusinessException;
import co.com.pragma.usecase.exceptions.ErrorResponse;
import co.com.pragma.usecase.user.UserUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Log
public class Handler {

  private final UserUseCase userUseCase;

  public Mono<ServerResponse> createUser(ServerRequest serverRequest) {
    log.info(Constants.LOG_CREATE_USER);
    return serverRequest.bodyToMono(UserParameters.class)
            .flatMap(userUseCase::createUser)
            .flatMap(user -> ServerResponse.ok().bodyValue(user))
            .onErrorResume(error -> {
              if (error instanceof BusinessException businessException) {
                log.warning(Constants.LOG_BUSINESS_ERROR + businessException.getMessage());
                return ServerResponse.status(businessException.getErrorResponse().getStatus())
                        .bodyValue(businessException.getErrorResponse());
              }

              log.severe(Constants.LOG_UNEXPECTED_ERROR + error);
              return ServerResponse.status(500)
                      .bodyValue(new ErrorResponse(Constants.INTERNAL_SERVER_ERROR, Constants.UNEXPECTED_ERROR_MESSAGE, 500));
            });
  }

  public Mono<ServerResponse> getUserByDocumentNumber(ServerRequest serverRequest) {
    log.info(Constants.LOG_FIND_USER);
    String documentNumber = serverRequest.queryParam(Constants.DOCUMENT_NUMBER_PARAM)
            .orElse(serverRequest.pathVariable(Constants.DOCUMENT_NUMBER_PARAM));

    return userUseCase.findByDocumentNumber(documentNumber)
            .flatMap(user -> ServerResponse.ok().bodyValue(user))
            .onErrorResume(error -> {
              if (error instanceof BusinessException businessException) {
                log.warning(Constants.LOG_BUSINESS_ERROR + businessException.getMessage());
                return ServerResponse.status(businessException.getErrorResponse().getStatus())
                        .bodyValue(businessException.getErrorResponse());
              }

              log.severe(Constants.LOG_UNEXPECTED_ERROR + error);
              return ServerResponse.status(500)
                      .bodyValue(new ErrorResponse(Constants.INTERNAL_SERVER_ERROR, Constants.UNEXPECTED_ERROR_MESSAGE, 500));
            });
  }
}
