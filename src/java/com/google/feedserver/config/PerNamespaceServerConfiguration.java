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

import com.google.feedserver.configstore.FeedConfigStoreException;

import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;
import org.apache.abdera.protocol.server.provider.managed.ServerConfiguration;

/**
 * This is server configuration per namespace.
 * 
 * @author abhinavk@gmail.com (Abhinav Khandelwal)
 * 
 */
public class PerNamespaceServerConfiguration extends ServerConfiguration {
  private final GlobalServerConfiguration globalServerConfiguration;
  private final String namespace;
  private String identity;

  /**
   * Creates a new server configuration per namespace
   * 
   * @param globalServerConfig
   * @param namespace
   */
  public PerNamespaceServerConfiguration(GlobalServerConfiguration globalServerConfig,
      String namespace) {
    this.globalServerConfiguration = globalServerConfig;
    this.namespace = namespace;
  }

  /**
   * Gets the adapter configuration location for this namespace
   */
  @Override
  public String getAdapterConfigLocation() {
    return globalServerConfiguration.getAdapterConfigLocation(namespace);
  }

  /**
   * Gets the feed configuration location for this namespace
   */
  @Override
  public String getFeedConfigLocation() {
    return globalServerConfiguration.getFeedConfigLocation(namespace);
  }

  /**
   * Gets the feed configuration suffix for this namespace
   */
  @Override
  public String getFeedConfigSuffix() {
    return globalServerConfiguration.getFeedConfigSuffix();
  }

  /**
   * Gets the feed namespace for this namespace
   */
  @Override
  public String getFeedNamespace() {
    return globalServerConfiguration.getFeedNamespace(namespace);
  }

  /**
   * Gets the feed namespace prefix for this namespace
   */
  @Override
  public String getFeedNamespacePrefix() {
    return globalServerConfiguration.getFeedNamespacePrefix(namespace);
  }

  /**
   * Gets the server port
   */
  @Override
  public int getPort() {
    return globalServerConfiguration.getPort();
  }

  /**
   * Gets the server URI for this namespace
   */
  @Override
  public String getServerUri() {
    return globalServerConfiguration.getServerUri(namespace);
  }

  /**
   * Gets the Feed configuration for the specified feed
   * 
   * @param feedId The feed to get the {@link FeedConfiguration for}
   */
  @Override
  public FeedConfiguration loadFeedConfiguration(String feedId)
      throws FeedConfigStoreException {
    return loadFeedConfiguration(feedId, null);
  }

  /**
   * Gets the Feed configuration for the specified feed
   * 
   * @param feedId The feed to get the {@link FeedConfiguration for}
   * @param userId User email of per user feed requested; null if not per user feed
   */
  public FeedConfiguration loadFeedConfiguration(String feedId, String userId)
      throws FeedConfigStoreException {
    return globalServerConfiguration.getFeedConfiguration(namespace, feedId, userId);
  }

  /**
   * Get the fully qualified class name for adapter wrapper manager.
   * 
   * @return fully qualified class name for adapter wrapper manager.
   */
  public String getWrapperManagerClassName() {
    return globalServerConfiguration.getWrapperManagerClassName();
  }

  /**
   * Gets the namespace this configuration is specific to.
   */
  public String getNameSpace() {
    return namespace;
  }

  /**
   * Gets the user name to use for the configuration
   */
  public String getIdentity() {
    return identity;
  }

  /**
   * Sets the username to use for the configuration
   */
  public void setIdentity(String identity) {
    this.identity = identity;
  }

  /**
   * Get Global server configuration.
   */
  public GlobalServerConfiguration getGolbalServerConfiguration() {
    return globalServerConfiguration;
  }
}
