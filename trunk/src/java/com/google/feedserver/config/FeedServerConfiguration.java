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

package com.google.feedserver.config;

import com.google.feedserver.configstore.FeedConfigStore;
import com.google.feedserver.configstore.FeedConfigStoreException;
import com.google.feedserver.util.SimpleCommandLineParser;

import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration file for FeedServer
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public class FeedServerConfiguration implements GlobalServerConfiguration {
  /**
   * Adapter configuration protocol buffer and FeedStore table name
   */
  public static final String ADAPTER_CONFIGURATION = "AdapterConfig";

  /**
   * Feed / Adapter Configuration protocol buffer Unique ID Field
   */
  public static final String ID_KEY = "name";

  /**
   * Adapter Configuration property for Adapter Type
   */
  public static final String ADAPTER_TYPE_KEY = "type";

  /**
   * Adapter Configuration property for Config Value Type
   */
  public static final String CONFIG_VALUE_TYPE_KEY = "configValueType";

  /**
   * Adapter Configuration property for Config Type
   */
  public static final String CONFIG_VALUE_KEY = "configValue";

  /**
   * Adapter configuration property for wrappers.
   */
  public static final String MIXINS = "mixins";

  /**
   * Adapter configuration property for implicit mixins.
   */
  public static final String IMPLICIT_MIXINS = "implicitMixins";

  /**
   * Adapter configuration property for wrappers.
   */
  public static final String REQUIRED_WRAPPERS = "requiredWrappers";

  /**
   * Feed configuration protocol buffer name
   */
  public static final String FEED_CONFIGURATION = "FeedConfig";

  /**
   * Feed Configuration property for Adaptor name
   */
  public static final String FEED_ADAPTER_NAME_KEY = "adapterName";

  /**
   * Feed configuration property for type meta-data configuration.
   */
  public static final String FEED_TYPE_CONFIG_KEY = "feedInfo";


  /**
   * The command line argument name for server port
   */
  private static final String OPTION_PORT = "port";

  /**
   * The port no on which the service is available
   */
  private int port = 8080;

  /**
   * The command line argument name for server uri
   */
  private static final String OPTION_FEED_SERVER_URI = "uri";

  /**
   * The server uri
   */
  private String uri = "http://localhost:8080";

  /**
   * The wrapper manager class name
   */
  private String wrapperManagerClassName = null;

  private static FeedServerConfiguration instance = null;

  /**
   * Property in Adapters feed which indicate if the given adapter is an
   * wrapper.
   */
  public static final String IS_WRAPPER = "isWrapper";

  protected FeedConfigStore feedConfigStore;

  @SuppressWarnings("unchecked")
  private final Map<String, AdapterBackendPool> adapterPool;

  private AclValidator aclValidator;

  @SuppressWarnings("unchecked")
  public FeedServerConfiguration(FeedConfigStore feedConfigStore) {
    this.feedConfigStore = feedConfigStore;
    this.adapterPool = new HashMap<String, AdapterBackendPool>();
  }

  /**
   * Creates the singleton instance of the {@link FeedServerConfiguration}
   */
  public static FeedServerConfiguration createIntance(FeedConfigStore feedConfigStore) {
    instance = new FeedServerConfiguration(feedConfigStore);
    return instance;
  }

  /**
   * Gets the singleton instance of the {@link FeedServerConfiguration}
   */
  public static FeedServerConfiguration getIntance() {
    return instance;
  }

  @Override
  public FeedConfigStore getFeedConfigStore() {
    return feedConfigStore;
  }

  @Override
  public FeedConfiguration getFeedConfiguration(String namespace, String feedId)
      throws FeedConfigStoreException {
    return feedConfigStore.getFeedConfiguration(namespace, feedId);
  }

  @Override
  public String getServerUri(String namespace) {
    return uri.concat("/").concat(namespace);
  }

  @Override
  public String getFeedPathPrefix() {
    return "";
  }

  @Override
  public String getFeedNamespace(String namespace) {
    return null;
  }

  @Override
  public String getFeedNamespacePrefix(String namespace) {
    return null;
  }

  @Override
  public String getWrapperManagerClassName() {
    return wrapperManagerClassName;
  }

  /**
   * Gets the FeedServer port - this operation is not supported
   */
  @Override
  public int getPort() {
    return port;
  }

  /**
   * Gets the FeedServer port - this operation is not supported
   */
  @Override
  public String getAdapterConfigLocation(String namespace) {
    throw new UnsupportedOperationException();
  }

  /**
   * Gets the location of the FeedConfig - this operation is not supported
   */
  @Override
  public String getFeedConfigLocation(String namespace) {
    throw new UnsupportedOperationException();
  }

  /**
   * Gets the FeedConfig suffix - this operation is not supported
   */
  @Override
  public String getFeedConfigSuffix() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getProviderClassName() {
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public AdapterBackendPool getAdapterBackendPool(String poolId) {
    return adapterPool.get(poolId);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void setAdapterBackendPool(String poolId, AdapterBackendPool pool) {
    adapterPool.put(poolId, pool);
  }

  @Override
  public AclValidator getAclValidator() {
    return aclValidator;
  }

  @Override
  public void setAclValidator(AclValidator aclValidator) {
    this.aclValidator = aclValidator;
  }


  /**
   * Initialize the configuration with command line arguments
   * 
   * @param parser The parser to parse arguments
   */
  public void initialize(SimpleCommandLineParser parser) {
    if (null != parser) {
      if (parser.containsKey(OPTION_PORT)) {
        port = Integer.parseInt(parser.getValue(OPTION_PORT));
      }
      if (parser.containsKey(OPTION_FEED_SERVER_URI)) {
        uri = parser.getValue(OPTION_FEED_SERVER_URI);
      }

    }
  }

  /**
   * Sets the server URI
   * 
   * @param uri The URI
   */
  public void setSeverURI(String uri) {
    this.uri = uri;
  }

  /**
   * Sets the wrapper manager class the name
   * 
   * @param wrapperManagerClassName The wrapper manager class name
   */
  public void setWrapperManagerClassName(String wrapperManagerClassName) {
    this.wrapperManagerClassName = wrapperManagerClassName;
  }



}
