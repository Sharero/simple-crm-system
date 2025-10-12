package com.example.errors;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiError> handleMissingParam(
      MissingServletRequestParameterException ex, WebRequest req) {
    String path = ((ServletWebRequest) req).getRequest().getRequestURI();
    String msg = "Required request parameter '" + ex.getParameterName() + "' is missing";
    ApiError err = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", msg, path);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiError> handleTypeMismatch(
      MethodArgumentTypeMismatchException ex, WebRequest req) {
    String path = ((ServletWebRequest) req).getRequest().getRequestURI();
    String name = ex.getName();
    String requiredType =
        ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
    String msg =
        "Parameter '"
            + name
            + "' could not be converted to type "
            + requiredType
            + ": "
            + ex.getMessage();
    ApiError err = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", msg, path);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
  }

  @ExceptionHandler(TypeMismatchException.class)
  public ResponseEntity<ApiError> handleTypeMismatch2(TypeMismatchException ex, WebRequest req) {
    String path = ((ServletWebRequest) req).getRequest().getRequestURI();
    String msg = "Type mismatch: " + ex.getMessage();
    ApiError err = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", msg, path);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(
      MethodArgumentNotValidException ex, WebRequest req) {
    String path = ((ServletWebRequest) req).getRequest().getRequestURI();
    ApiError err =
        new ApiError(
            HttpStatus.BAD_REQUEST.value(), "Validation Failed", "Validation errors", path);
    Map<String, String> validationErrors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(fe -> validationErrors.put(fe.getField(), fe.getDefaultMessage()));
    err.setValidationErrors(validationErrors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiError> handleUnreadable(
      HttpMessageNotReadableException ex, WebRequest req) {
    String path = ((ServletWebRequest) req).getRequest().getRequestURI();
    String msg =
        "Malformed JSON or invalid value: "
            + (ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : ex.getMessage());
    ApiError err = new ApiError(HttpStatus.BAD_REQUEST.value(), "Malformed Request", msg, path);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ApiError> handleNotFound(EntityNotFoundException ex, WebRequest req) {
    String path = ((ServletWebRequest) req).getRequest().getRequestURI();
    ApiError err = new ApiError(HttpStatus.NOT_FOUND.value(), "Not Found", ex.getMessage(), path);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex, WebRequest req) {
    String path = ((ServletWebRequest) req).getRequest().getRequestURI();
    ApiError err =
        new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request", ex.getMessage(), path);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
  }

  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<ApiError> handleDb(DataAccessException ex, WebRequest req) {
    String path = ((ServletWebRequest) req).getRequest().getRequestURI();
    ApiError err =
        new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(), "Database Error", ex.getMessage(), path);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleAll(Exception ex, WebRequest req) {
    String path = ((ServletWebRequest) req).getRequest().getRequestURI();
    ApiError err =
        new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            ex.getMessage(),
            path);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
  }
}
