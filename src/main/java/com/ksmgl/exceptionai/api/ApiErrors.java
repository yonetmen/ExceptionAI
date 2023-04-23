package com.ksmgl.exceptionai.api;

public final class ApiErrors {

  private ApiErrors() {
  }

  public static final String AUTH_ERROR =
      "\nIncorrect API key provided. " +
          "You can find your API key at https://platform.openai.com/account/api-keys.\n\n" +
          "To update your API Key and/or other settings:\n\n" +
          "Go to \"File > Settings > ExceptionAI Settings\" \n\n" +
          "If you are not sure how to configure these settings please follow the link below. \n\n" +
          "https://platform.openai.com/docs/api-reference/completions/create";

  public static final String INSUFFICIENT_QUOTA =
      "\nYou exceeded your current quota, please check your plan and billing details.\n\n" +
          "You can check your usage at https://platform.openai.com/account/usage";

  public static final String INVALID_REQUEST_ERROR =
      "\nThere was an error in your settings. Please make sure that you provided valid " +
          "Model, Temperature and/or MaxTokens values.\n" +
          "See documentation: https://platform.openai.com/docs/api-reference/chat \n" +
          "See github for simplified guidance: https://github.com/yonetmen/ExceptionAI";

  public static final String INTERNAL_SERVER_ERROR =
      "\nOpenAI API server had an error while processing your request.\n" +
          "Retry your request after a brief wait to see if the issue persists.\n" +
          "Check OpenAI status page: https://status.openai.com/";

}
