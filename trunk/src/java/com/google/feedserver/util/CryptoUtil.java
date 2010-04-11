package com.google.feedserver.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import biz.source_code.base64Coder.Base64Coder;

public class CryptoUtil {

  public static final String ENCODING = "UTF8";
  public static final String MAC_NAME = "HmacSHA1";

  /**
   * Gets the signature of a message using a secret key and HMAC SHA1.
   * @param message Message
   * @param secretKeyString Secret key
   * @return Signature of message
   * @throws UnsupportedEncodingException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   */
  public String getSignature(String message, String secretKeyString)
      throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
    SecretKey secretKey = new SecretKeySpec(secretKeyString.getBytes(ENCODING), MAC_NAME);
    Mac mac = Mac.getInstance(MAC_NAME);
    mac.init(secretKey);
    byte[] bytes = message.getBytes(ENCODING);
    return new String(Base64Coder.encode(mac.doFinal(bytes)));
  }

  /**
   * Verifies a message and its signature using a secret key.
   * @param message Message
   * @param signature Signature
   * @param secretKey Secret key
   * @return true if message's signature computed using the secret key is indeed the 
   * @throws NoSuchAlgorithmException 
   * @throws UnsupportedEncodingException 
   * @throws InvalidKeyException 
   */
  public boolean verifySigature(String message, String signature, String secretKey)
      throws InvalidKeyException, UnsupportedEncodingException, NoSuchAlgorithmException {
    return getSignature(message, secretKey) == signature;
  }
}
