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

  // pub.1210278512.2713152949996518384.cer
  private static final String DEFAULT_CERT =
	  "-----BEGIN CERTIFICATE-----\n" +
	  "MIIDBDCCAm2gAwIBAgIJAK8dGINfkSTHMA0GCSqGSIb3DQEBBQUAMGAxCzAJBgNV\n" +
	  "BAYTAlVTMQswCQYDVQQIEwJDQTEWMBQGA1UEBxMNTW91bnRhaW4gVmlldzETMBEG\n" +
	  "A1UEChMKR29vZ2xlIEluYzEXMBUGA1UEAxMOd3d3Lmdvb2dsZS5jb20wHhcNMDgx\n" +
	  "MDA4MDEwODMyWhcNMDkxMDA4MDEwODMyWjBgMQswCQYDVQQGEwJVUzELMAkGA1UE\n" +
	  "CBMCQ0ExFjAUBgNVBAcTDU1vdW50YWluIFZpZXcxEzARBgNVBAoTCkdvb2dsZSBJ\n" +
	  "bmMxFzAVBgNVBAMTDnd3dy5nb29nbGUuY29tMIGfMA0GCSqGSIb3DQEBAQUAA4GN\n" +
	  "ADCBiQKBgQDQUV7ukIfIixbokHONGMW9+ed0E9X4m99I8upPQp3iAtqIvWs7XCbA\n" +
	  "bGqzQH1qX9Y00hrQ5RRQj8OI3tRiQs/KfzGWOdvLpIk5oXpdT58tg4FlYh5fbhIo\n" +
	  "VoVn4GvtSjKmJFsoM8NRtEJHL1aWd++dXzkQjEsNcBXwQvfDb0YnbQIDAQABo4HF\n" +
	  "MIHCMB0GA1UdDgQWBBSm/h1pNY91bNfW08ac9riYzs3cxzCBkgYDVR0jBIGKMIGH\n" +
	  "gBSm/h1pNY91bNfW08ac9riYzs3cx6FkpGIwYDELMAkGA1UEBhMCVVMxCzAJBgNV\n" +
	  "BAgTAkNBMRYwFAYDVQQHEw1Nb3VudGFpbiBWaWV3MRMwEQYDVQQKEwpHb29nbGUg\n" +
	  "SW5jMRcwFQYDVQQDEw53d3cuZ29vZ2xlLmNvbYIJAK8dGINfkSTHMAwGA1UdEwQF\n" +
	  "MAMBAf8wDQYJKoZIhvcNAQEFBQADgYEAYpHTr3vQNsHHHUm4MkYcDB20a5KvcFoX\n" +
	  "gCcYtmdyd8rh/FKeZm2me7eQCXgBfJqQ4dvVLJ4LgIQiU3R5ZDe0WbW7rJ3M9ADQ\n" +
	  "FyQoRJP8OIMYW3BoMi0Z4E730KSLRh6kfLq4rK6vw7lkH9oynaHHWZSJLDAp17cP\n" +
	  "j+6znWkN9/g=\n" +
	  "-----END CERTIFICATE-----\n";

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
