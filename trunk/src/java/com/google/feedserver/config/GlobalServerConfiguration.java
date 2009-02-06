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

import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;

/**
 * Interface to make a hosted server that uses abdera adapters.
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public interface GlobalServerConfiguration {

  /**
   * Gets the location for the AdapterConfig for the specified namespace
   */
  public String getAdapterConfigLocation(String namespace);

  /**
   * Gets the location of the FeedConfig for the specified namespace
   */
  public String getFeedConfigLocation(String namespace);

  /**
   * Gets the Feed Configuration suffix
   */
  public String getFeedConfigSuffix();

  /**
   * Gets the namespace
   */
  public String getFeedNamespace(String namespace);

  /**
   * Gets the namespace prefix
   */
  public String getFeedNamespacePrefix(String namespace);

  /**
   * Gets the Server port
   */
  public int getPort();

  /**
   * Gets the URI for the feedserver with the specified namespace
   */
  public String getServerUri(String namespace);

  /**
   * Gets URI prefix for Feeds
   */
  public String getFeedPathPrefix();

  /**
   * Gets the {@link FeedConfiguration} for the specified feed in the namespace
   * 
   * @param namespace The namespace the feed is in
   * @param feedId The feed to get the {@link FeedConfiguration} for
   */
  public FeedConfiguration getFeedConfiguration(String namespace, String feedId)
      throws FeedConfigStoreException;

  /**
   * Get the fully qualified class name for adapter wrapper manager.
   * 
   * @return fully qualified class name for adapter wrapper manager.
   */
  public String getWrapperManagerClassName();

  /**
   * Gets the {@link FeedConfigStore} for the Server Configuration
   */
  public FeedConfigStore getFeedConfigStore();

  /**
   * Get the fully qualified class name of the provider that will be used for
   * this server.
   * 
   * @return fully qualified class name of the Provider.
   */
  public String getProviderClassName();

  @SuppressWarnings("unchecked")
  public AdapterBackendPool getAdapterBackendPool(String poolId);

  @SuppressWarnings("unchecked")
  public void setAdapterBackendPool(String poolId, AdapterBackendPool pool);

  public AclValidator getAclValidator();

  public void setAclValidator(AclValidator aclValidator);

}
