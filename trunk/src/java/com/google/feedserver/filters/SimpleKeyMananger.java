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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A simple key manager for signed-fetch.
 * 
 * @author abhinavk@gmail.com (Abhinav Khandelwal)
 * 
 */
public class SimpleKeyMananger implements KeyManager {

  private static final String DEFAULT_KEY =
      "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCx4Fdng0OGbbidfewlGJkmG/L1"
          + "4NlfXYaPgdYAyaEByebaIGBikCKDCFUe06z5khQh3NAe8d413TJ1zUmDx74L4yXO"
          + "jfw69oYPerC/MnQs2fsvzRzRdWu8QAt0P3Os77RdJmlMr08muXZbn2VmUkVSTelX"
          + "6MVHw1h4H9+2jsBW0QIDAQAB";

  private static final String DEFAULT_CERT =
      "-----BEGIN CERTIFICATE-----\n"
          + "MIICNjCCAZ+gAwIBAgIJAIGyPPESGPf/MA0GCSqGSIb3DQEBBQUAMB0xGzAZBgNV\n"
          + "BAMTEmVudGVycHJpc2UtdGVzdGluZzAeFw0wODA5MTIxNzA3MzVaFw0wOTA5MTIx\n"
          + "NzA3MzVaMB0xGzAZBgNVBAMTEmVudGVycHJpc2UtdGVzdGluZzCBnzANBgkqhkiG\n"
          + "9w0BAQEFAAOBjQAwgYkCgYEA2Ea3QGkQ0jP6TK7x5+6dG2Hzlgs6x0cP744CL95S\n"
          + "7dRWcPRYtIHHmfXLWyoLLEZIML5/zSNBsZE85t95uxp3AI9m9+eCoFCVb+EoNklw\n"
          + "tuAvbvYDgv0O/5LgeOKsZnrz5FxCqqNvjIn0Ikt9aU2WR5JQYkltnxdz3JubNQ9g\n"
          + "uw0CAwEAAaN+MHwwHQYDVR0OBBYEFD5/PU+bcJgilHfWIZ4YAOOiiO+9ME0GA1Ud\n"
          + "IwRGMESAFD5/PU+bcJgilHfWIZ4YAOOiiO+9oSGkHzAdMRswGQYDVQQDExJlbnRl\n"
          + "cnByaXNlLXRlc3RpbmeCCQCBsjzxEhj3/zAMBgNVHRMEBTADAQH/MA0GCSqGSIb3\n"
          + "DQEBBQUAA4GBAJYsJh+qchZ63UXkQigGxqEYlNJLxR7LV0uM7mzDQcg6SVoG7mlF\n"
          + "KTzlvjH/wDj9osYu060jXvoWNQvVNAOVynB7icFWgSeg7pez+9eb6V6NncMsEtlc\n"
          + "gbQE39Dn/WIKt0icLuL/IHYxh/09xdlT12WWT8HV3u5wUlZaQM0sOtBq\n"
          + "-----END CERTIFICATE-----\n";

  public SimpleKeyMananger() {
  }

  private static final List<KeyType> RSA_SHA1_KEY_TYPES = new ArrayList<KeyType>();

  private static final List<KeyType> HMAC_SHA1_KEY_TYPES = new ArrayList<KeyType>();

  private static final List<KeyType> PLAINTEXT_KEY_TYPES = new ArrayList<KeyType>();

  private static final List<KeyType> KEY_TYPES_NONE = new ArrayList<KeyType>();

  static {
    RSA_SHA1_KEY_TYPES.add(KeyType.RSA_SHA1_PublicKey);
    RSA_SHA1_KEY_TYPES.add(KeyType.RSA_SHA1_X509Certificate);

    HMAC_SHA1_KEY_TYPES.add(KeyType.HMAC_SHA1);

    PLAINTEXT_KEY_TYPES.add(KeyType.PLAINTEXT);
  }

  @Override
  public OAuthConsumer getOAuthConsumer(OAuthServiceProvider provider, String consumerId,
      String oAuthSignatureMethod) {
    OAuthConsumer consumer = new OAuthConsumer(null, consumerId, null, provider);
    Collection<KeyType> possilbeKeyTypes =
        getPossibleKeyTypesForSignatureType(oAuthSignatureMethod);
    for (KeyType keyType : possilbeKeyTypes) {
      if (hasKeyForConsumer(consumerId, keyType)) {
        consumer.setProperty(keyType.getKeyName(), getKeyForConsumer(consumerId, keyType));
      }
    }
    return consumer;
  }

  @Override
  public Object getKeyForConsumer(String consumerId, KeyType keyType) {
    if (KeyType.RSA_SHA1_X509Certificate == keyType) {
      return DEFAULT_CERT;
    }
    if (KeyType.RSA_SHA1_PublicKey == keyType) {
      return DEFAULT_KEY;
    }
    return null;
  }

  @Override
  public boolean hasKeyForConsumer(String consumerId, KeyType keyType) {
    if (KeyType.RSA_SHA1_PublicKey == keyType) {
      return false;
    }
    if (KeyType.RSA_SHA1_X509Certificate == keyType) {
      return true;
    }
    return false;
  }

  @Override
  public Collection<KeyType> getPossibleKeyTypesForSignatureType(String oAuthSignatureMethod) {
    if ("RSA-SHA1".equals(oAuthSignatureMethod)) {
      return RSA_SHA1_KEY_TYPES;
    } else if ("HMAC_SHA1".equals(oAuthSignatureMethod)) {
      return HMAC_SHA1_KEY_TYPES;
    } else if ("Plaintext".equals(oAuthSignatureMethod)) {
      return PLAINTEXT_KEY_TYPES;
    }
    return KEY_TYPES_NONE;
  }
}
