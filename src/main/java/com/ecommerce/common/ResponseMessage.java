package com.ecommerce.common;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ResponseMessage {

  private HttpStatus status;
  private String message;
  private Object data;
}
