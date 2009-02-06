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

package com.google.feedserver.authentication;

import com.google.feedserver.authentication.TokenManagerException.Reason;
import com.google.feedserver.util.EncryptionUtil;
import com.google.inject.Singleton;

import org.apache.commons.codec.DecoderException;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;

/**
 * The sample implementation for {@link TokenManager}.
 * <p>
 * The implementation generates tokens without doing any authentication and
 * validates the token with timestamp values stored in the map for a given
 * user-name & service-name pair
 * </p>
 * 
 * @author rakeshs101981@gmail.com (Rakesh Shete)
 * 
 */
@Singleton
public class SampleTokenManager implements TokenManager {

  Logger logger = Logger.getLogger(SampleTokenManager.class.getName());

  /**
   * The map that will store the mapping for a given user-email with the service
   * and the timestamp when the token was generated.
   * <p>
   * For validation, the user-email & service will be checked with the timestamp
   * in the input token.
   * </p>
   * <p>
   * The key-value details:
   * <ul>
   * <li>Key: The key will be 'user-email:service-name' string</li>
   * <li>Value: The value will be the timestamp when the token was generated</li>
   * </ul>
   * </p>
   */
  private Map<String, String> authtokens;

  public SampleTokenManager() {
    authtokens = new Hashtable<String, String>();
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.feedserver.authentication.TokenManager#generateToken(javax
   * .servlet .http.HttpServletRequest)
   */
  @Override
  public String generateAuthzToken(HttpServletRequest request) throws TokenManagerException {
    // Encrypt the user-email with the service name along with a timestamp
    String timeStamp = new StringBuffer(String.valueOf(System.currentTimeMillis())).toString();
    String email = request.getParameter("Email");
    String service = request.getParameter("service");

    String stringToEncrypt =
        new StringBuffer(email).append(":").append(service).append(":").append(timeStamp)
            .toString();
    String authToken = null;
    try {
      authToken = EncryptionUtil.getInstance().encrypt(stringToEncrypt);
      authtokens.put(email + ":" + service, timeStamp);
    } catch (InvalidKeyException e) {
      logger.log(Level.SEVERE, "Problem encountered while generating the token", e);
      throw new TokenManagerException("Problem encountered while generating the token",
          Reason.UNEXPECTED_ERROR, e);
    } catch (BadPaddingException e) {
      logger.log(Level.SEVERE, "Problem encountered while generating the token", e);
      throw new TokenManagerException("Problem encountered while generating the token",
          Reason.UNEXPECTED_ERROR, e);
    } catch (IllegalBlockSizeException e) {
      logger.log(Level.SEVERE, "Problem encountered while generating the token", e);
      throw new TokenManagerException("Problem encountered while generating the token",
          Reason.UNEXPECTED_ERROR, e);
    } catch (NoSuchAlgorithmException e) {
      logger.log(Level.SEVERE, "Problem encountered while generating the token", e);
      throw new TokenManagerException("Problem encountered while generating the token",
          Reason.UNEXPECTED_ERROR, e);
    } catch (NoSuchPaddingException e) {
      logger.log(Level.SEVERE, "Problem encountered while generating the token", e);
      throw new TokenManagerException("Problem encountered while generating the token",
          Reason.UNEXPECTED_ERROR, e);
    }

    return authToken;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * com.google.feedserver.authentication.TokenManager#validateRequestToken(
   * javax.servlet.http.HttpServletRequest, java.lang.String)
   */
  @Override
  public boolean validateAuthzToken(HttpServletRequest request) throws TokenManagerException {
    String authZHeader = request.getHeader("Authorization");
    if (authZHeader != null) {
      String authTokenString = authZHeader.substring(authZHeader.indexOf("auth"));
      String authToken = authTokenString.substring(authTokenString.indexOf('=') + 1);
      try {
        String decryptedValue = EncryptionUtil.getInstance().decrypt(authToken);
        String authTokenKey = decryptedValue.substring(0, decryptedValue.lastIndexOf(":"));
        String timestamp = decryptedValue.substring(decryptedValue.lastIndexOf(":") + 1);
        if (authtokens.containsKey(authTokenKey) && authtokens.get(authTokenKey).equals(timestamp)) {
          return true;
        }
      } catch (InvalidKeyException e) {
        logger.log(Level.SEVERE, "Problem encountered while validating the authorization token", e);
        throw new TokenManagerException(
            "Problem encountered while validating the authorization token",
            Reason.UNEXPECTED_ERROR, e);
      } catch (BadPaddingException e) {
        logger.log(Level.SEVERE, "Problem encountered while validating the authorization token", e);
        throw new TokenManagerException(
            "Problem encountered while validating the authorization token",
            Reason.UNEXPECTED_ERROR, e);
      } catch (IllegalBlockSizeException e) {
        logger.log(Level.SEVERE, "Problem encountered while validating the authorization token", e);
        throw new TokenManagerException(
            "Problem encountered while validating the authorization token",
            Reason.UNEXPECTED_ERROR, e);
      } catch (DecoderException e) {
        logger.log(Level.SEVERE, "Problem encountered while validating the authorization token", e);
        throw new TokenManagerException(
            "Problem encountered while validating the authorization token",
            Reason.UNEXPECTED_ERROR, e);
      } catch (NoSuchAlgorithmException e) {
        logger.log(Level.SEVERE, "Problem encountered while validating the authorization token", e);
        throw new TokenManagerException(
            "Problem encountered while validating the authorization token",
            Reason.UNEXPECTED_ERROR, e);
      } catch (NoSuchPaddingException e) {
        logger.log(Level.SEVERE, "Problem encountered while validating the authorization token", e);
        throw new TokenManagerException(
            "Problem encountered while validating the authorization token",
            Reason.UNEXPECTED_ERROR, e);
      }
    }

    return false;
  }

}
