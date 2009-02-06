/*
 * Copyright 2008 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.feedserver.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

/**
 * The encryption utility that supports symmetric encryption. It uses the
 * DES-EDE algorithm for encryption and decryption.
 * <p>
 * The encrypted values are converted to their hex representation and returned
 * back as string. <br/> The decryption first converts the input encrypted hex
 * string to byte and then decrypts it to the original string
 * </p>
 * 
 * @author rakeshs101981@gmail.com (Rakesh Shete)
 */
public class EncryptionUtil {

  private static String algorithm = "DESede";
  private static Key key = null;
  private static EncryptionUtil encryptionUtil = null;
  static {
    encryptionUtil = new EncryptionUtil();
  }

  private static Logger logger = Logger.getLogger(EncryptionUtil.class.getName());

  /**
   * Default constructor
   */
  private EncryptionUtil() {
    try {
      key = KeyGenerator.getInstance(algorithm).generateKey();
    } catch (NoSuchAlgorithmException e) {
      logger.log(Level.SEVERE,
          "Problems encountered while generating a key to be used for encryption/decryption", e);
    }
  }

  public static EncryptionUtil getInstance() {
    return encryptionUtil;
  }



  /**
   * Encrypts the given string using a symmetric key algorithm
   * 
   * @param input The input
   * @return The encrypted string with hex representation
   * @throws InvalidKeyException
   * @throws BadPaddingException
   * @throws IllegalBlockSizeException
   * @throws NoSuchPaddingException
   * @throws NoSuchAlgorithmException
   */
  public String encrypt(String input) throws InvalidKeyException, BadPaddingException,
      IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException {
    Cipher cipher = Cipher.getInstance(algorithm);
    cipher.init(Cipher.ENCRYPT_MODE, key);
    byte[] inputBytes = input.getBytes();
    byte[] encryptedBytes = cipher.doFinal(inputBytes);
    String encryptedValue = new String(Hex.encodeHex(encryptedBytes));
    return encryptedValue;
  }

  /**
   * Decrypts the given encrypted string and returns the original value
   * 
   * @param encryptedValue The encrypted string
   * @return The decrypted value
   * @throws InvalidKeyException
   * @throws BadPaddingException
   * @throws IllegalBlockSizeException
   * @throws DecoderException
   * @throws NoSuchPaddingException
   * @throws NoSuchAlgorithmException
   */
  public String decrypt(String encryptedValue) throws InvalidKeyException, BadPaddingException,
      IllegalBlockSizeException, DecoderException, NoSuchAlgorithmException, NoSuchPaddingException {
    byte[] encryptionBytes = Hex.decodeHex(encryptedValue.toCharArray());
    Cipher cipher = Cipher.getInstance(algorithm);
    cipher.init(Cipher.DECRYPT_MODE, key);
    byte[] recoveredBytes = cipher.doFinal(encryptionBytes);
    String recovered = new String(recoveredBytes);
    return recovered;
  }

}
