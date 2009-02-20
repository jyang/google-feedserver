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

package com.google.feedserver.filters;

import net.oauth.OAuthConsumer;
import net.oauth.OAuthServiceProvider;
import net.oauth.signature.RSA_SHA1;

import java.util.Collection;

/**
 * It stores public keys for OAuth Verification.
 * 
 * @author abhinavk@gmail.com (Abhinav Khandelwal)
 */
public interface KeyManager {

  public enum KeyType {
    RSA_SHA1_PrivateKey(RSA_SHA1.PRIVATE_KEY), RSA_SHA1_PublicKey(RSA_SHA1.PUBLIC_KEY),
    RSA_SHA1_X509Certificate(RSA_SHA1.X509_CERTIFICATE), HMAC_SHA1("HMAC_SHA"), PLAINTEXT(
        "Plaintext");

    private String keyName;

    private KeyType(String name) {
      this.keyName = name;
    }

    public String getKeyName() {
      return keyName;
    }
  }

  /**
   * Checks if there is a key of type {@link KeyType} for the given consumer
   * 
   * @param consumerId The consumer-id
   * @param keyType One of the key types as defined in {@link KeyType}
   * @return True if there exists a key and false otherwise
   */
  boolean hasKeyForConsumer(String consumerId, KeyType keyType);

  /**
   * Returns the key to be used for signing requests for the given consumer-id.
   * <br/> The key type should be supported for the given consumer-id
   * 
   * @param consumerId The id identifying the consumer
   * @param keyType One of the values mentioned in the {@link KeyType}.
   * @return The key to be used by the consumer
   */
  Object getKeyForConsumer(String consumerId, KeyType keyType);

  /**
   * Returns a {@link OAuthConsumer} instance for the given consumer with the
   * default properties set
   * 
   * @param provider The OAuth service provider used by the consumer
   * @param consumerId The identifier for the consumer
   * @param oAuthSignatureMethod The signature method that will be used
   * @return The {@link OAuthConsumer} instance for the given consumer
   */
  OAuthConsumer getOAuthConsumer(OAuthServiceProvider provider, String consumerId,
      String oAuthSignatureMethod);

  /**
   * Return a collection of key types supported for the given OAuth signature
   * method
   * 
   * @param oAuthSignatureMethod The OAuth signature method: : RSA-SHA1,
   *        HMAC_SHA1 or Plaintext
   * @return The collection of supported key types
   */
  Collection<KeyType> getPossibleKeyTypesForSignatureType(String oAuthSignatureMethod);
}
