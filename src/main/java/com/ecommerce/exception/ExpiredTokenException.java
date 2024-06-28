package com.ecommerce.exception;

public class ExpiredTokenException extends TokenException {

  public ExpiredTokenException(String message) {
    super(message);
  }

}
