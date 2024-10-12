package com.one.conversormonedas.exceptions;

public class ErrorConversionMonedaException extends RuntimeException {
  private final String message;

  public ErrorConversionMonedaException(String message) {
    this.message = message;
  }

  @Override
  public String getMessage() {
    return this.message;
  }
}
