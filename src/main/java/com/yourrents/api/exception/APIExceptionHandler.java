package com.yourrents.api.exception;

/*-
 * #%L
 * YourRents API
 * %%
 * Copyright (C) 2023 - 2024 Your Rents Team
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.yourrents.services.common.util.exception.ApiError;
import com.yourrents.services.common.util.exception.DataConflictException;
import com.yourrents.services.common.util.exception.DataNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class APIExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(APIExceptionHandler.class);

  @ExceptionHandler(DataNotFoundException.class)
  public ResponseEntity<Object> dataNotFound(DataNotFoundException e, NativeWebRequest request) {
    logger.trace(e.getMessage(), e);
    return super.handleExceptionInternal(e,
        buildErrorResponse(e.getMessage(), e, request, HttpStatus.NOT_FOUND.value()),
        new HttpHeaders(), HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler(DataConflictException.class)
  public ResponseEntity<Object> dataConflict(DataConflictException e, NativeWebRequest request) {
    logger.trace(e.getMessage(), e);
    return super.handleExceptionInternal(e,
        buildErrorResponse(e.getMessage(), e, request, HttpStatus.CONFLICT.value()),
        new HttpHeaders(), HttpStatus.CONFLICT, request);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e,
      NativeWebRequest request) {
    logger.trace(e.getMessage(), e);
    return super.handleExceptionInternal(e,
        buildErrorResponse(e.getMessage(), e, request, HttpStatus.BAD_REQUEST.value()),
        new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  @Override
  public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    logger.trace(ex.getMessage(), ex);
    Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.toMap(
            FieldError::getField,
            FieldError::getDefaultMessage
        ));
    return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Message> genericError(Exception e) {
    logger.error("Unexpected error", e);
    Message message = new Message("Unexpected error: \n" + e.toString());
    return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ApiError buildErrorResponse(String message, Exception e, NativeWebRequest request,
      int status) {
    return new ApiError(message, e.getMessage(), status,
        ((HttpServletRequest) (request.getNativeRequest())).getRequestURI());
  }

}
