package com.ksmgl.exceptionai.api;

public final class APIResponse {

  private final int code;
  private final String message;

  public APIResponse(int code, String message) {
    this.code = code;
    this.message = message;
  }

  public int getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }
}
