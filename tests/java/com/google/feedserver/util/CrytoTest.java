package com.google.feedserver.util;

import junit.framework.TestCase;

public class CrytoTest extends TestCase {

  public static final String SECRET_KEY = "test-secret-key";
  public static final String MESSAGE = "test-message";

  private CryptoUtil util = new CryptoUtil();

  public void testVerifySignature() throws Exception {
    String computedSignature = util.getSignature(MESSAGE, SECRET_KEY);
    assertEquals(util.getSignature(MESSAGE, SECRET_KEY), computedSignature);
  }
}
