package com.example.errors;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
public class ApiError {
  private Instant timestamp = Instant.now();

  private int status;

  private String error;

  private String message;

  private String path;

  private Map<String, String> validationErrors;

  public ApiError(int status, String error, String message, String path) {
    this.status = status;
    this.error = error;
    this.message = message;
    this.path = path;
  }
}
