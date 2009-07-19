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

package com.google.feedserver.configstore;

import org.apache.abdera.protocol.server.provider.managed.CollectionAdapterConfiguration;
import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;

import java.util.Collection;
import java.util.Map;

/**
 * A store that keeps the {@link FeedConfiguration} and
 * {@link CollectionAdapterConfiguration} for the HostedServer.
 * 
 * @author abhinavk@gmail.com (Abhinav Khandelwal)
 * 
 */
public interface FeedConfigStore {

  /**
   * Gets the {@link FeedConfiguration} for a specific feed in a namespace
   * @param namespace Namespace
   * @param feedId Feed id
   * @param userId User id of per user feed requested; null if not per user feed
   */
  public FeedConfiguration getFeedConfiguration(String namespace, String feedId, String userId)
      throws FeedConfigStoreException;

  /**
   * Gets a collection of all the feed IDs for all of the Feeds in a namespace
   * 
   * @param namespace The namespace to enumerate the feed IDs for
   * @return The feed IDs of all the feeds in the namespace
   * @throws FeedConfigStoreException
   */
  public Collection<String> getFeedIds(String namespace) throws FeedConfigStoreException;

  /**
   * Gets a collection of all of the {@link FeedConfiguration}s in the namespace
   * 
   * @param namespace The namespace to enumerate the feed configurations for
   * @return A map from feedID to {@link FeedConfiguration}
   * @throws FeedConfigStoreException
   */
  public Map<String, FeedConfiguration> getFeedConfigurations(String namespace)
      throws FeedConfigStoreException;

  /**
   * Determines if the given feed is contained within the namespace
 * @param userId TODO
   */
  public boolean hasFeed(String namespace, String feedId, String userId) throws FeedConfigStoreException;

  /**
   * Add a feed with the given {@link FeedConfiguration} to the namespace
   * 
   * @param namespace The namespace to add the feed to
   * @param config The feed configuration for the feed
   * @throws FeedConfigStoreException
   */
  public void addFeed(String namespace, FeedConfiguration config) throws FeedConfigStoreException;

  /**
   * Updates an existing feed in the namespace with a new
   * {@link FeedConfiguration}
   * 
   * @param namespace The namespace to update the feed in
   * @param userId User id of per user feed requested; null if not per user feed
   * @param config The feed configuration to update the feed with
   * @throws FeedConfigStoreException
   */
  public void updateFeed(String namespace, String userId, FeedConfiguration config)
      throws FeedConfigStoreException;

  /**
   * Deletes an existing feed in the namespace
   * 
   * @param namespace The namespace to delete the feed from
   * @param feedId The feed to be deleted
   * @param userId User id of per user feed requested; null if not per user feed 
   * @throws FeedConfigStoreException
   */
  public void deleteFeed(String namespace, String feedId, String userId) throws FeedConfigStoreException;

  /**
   * Gets the adapter configuration for a specific feed in a namespace
   * 
   * @param namespace The namespace the feed is located in
   * @param adapterName Adapter name of the adapter configuration.
   * @return The adaptor configuration for the feed
   * @throws FeedConfigStoreException
   */
  public CollectionAdapterConfiguration getAdapterConfiguration(String namespace, String adapterName)
      throws FeedConfigStoreException;

  /**
   * Determines if the given adapter is contained within the namespace
   * 
   * @param namespace The namespace the feed is located in
   * @param adapterName The adapter to get the adapter configuration for
   */
  public boolean hasAdapterConfiguration(String namespace, String adapterName)
      throws FeedConfigStoreException;

  /**
   * Gets a collection of adapter IDs for the
   * {@link CollectionAdapterConfiguration}s that are allowed in the namespace
   * 
   * @param namespace The namespace to enumerate adapters that are allowed
   * @return A collection of the adapter IDs for allowed adapters
   * @throws FeedConfigStoreException
   */
  public Collection<String> getAllowedAdapters(String namespace) throws FeedConfigStoreException;

  /**
   * Gets a map of all {@link CollectionAdapterConfiguration}s in the namespace
   * 
   * @param namespace The namespace to enumerate adapters for
   * @return A map from adapter ID to {@link CollectionAdapterConfiguration}
   * @throws FeedConfigStoreException
   */
  public Map<String, CollectionAdapterConfiguration> getAdapterConfigurations(String namespace)
      throws FeedConfigStoreException;

  /**
   * Add a adapter configuration with the given
   * {@link CollectionAdapterConfiguration} to the namespace
   * 
   * @param namespace The namespace to add the feed to
   * @param config The feed configuration for the feed
   * @throws FeedConfigStoreException
   */
  public void addAdapterConfiguration(String namespace, CollectionAdapterConfiguration config)
      throws FeedConfigStoreException;

  /**
   * Updates an existing adapter in the namespace with a new
   * {@link CollectionAdapterConfiguration}
   * 
   * @param namespace The namespace to update the feed in
   * @param config The adapter configuration to update the feed with
   * @throws FeedConfigStoreException
   */
  public void updateAdapterConfiguration(String namespace, CollectionAdapterConfiguration config)
      throws FeedConfigStoreException;

  /**
   * Deletes an existing adapter in the namespace
   * 
   * @param namespace The namespace to delete the feed from
   * @param adapterId The feed to be deleted
   * @throws FeedConfigStoreException
   */
  public void deleteAdapterConfiguration(String namespace, String adapterId)
      throws FeedConfigStoreException;

  /**
   * Allows the adapter ID in the namespace
   * 
   * @param namespace The namespace to allow the adaptor in
   * @param adapterId The adapter to allow in the namepace
   * @throws FeedConfigStoreException
   */
  public void allowAdapter(String namespace, String adapterId) throws FeedConfigStoreException;

  /**
   * Disallows the adapter ID in the namespace
   * 
   * @param namespace The namespace to allow the adaptor in
   * @param adapterId The adapter to allow in the namepace
   * @throws FeedConfigStoreException
   */
  public void disallowAdapter(String namespace, String adapterId) throws FeedConfigStoreException;
}
