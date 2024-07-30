package com.ecommerce.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HeaderUtil {

  private static final String ACCESS_TOKEN_KEY = "Access";

  public static String getAccessToken(HttpServletRequest request) {
    return request.getHeader(ACCESS_TOKEN_KEY);
  }

  public static void setAccessToken(HttpServletResponse response, String token) {
    setHeader(response, ACCESS_TOKEN_KEY, token);
  }

  private void setHeader(HttpServletResponse response, String name, String value) {
    response.setHeader(name, value);
  }

}
