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

package com.google.feedserver.manager;

import com.google.feedserver.adapters.AbstractManagedCollectionAdapter;
import com.google.feedserver.config.GlobalServerConfiguration;
import com.google.feedserver.configstore.FeedConfigStore;
import com.google.feedserver.configstore.FeedConfigStoreException;

import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;
import org.apache.abdera.protocol.server.provider.managed.ManagedProvider;
import org.apache.abdera.protocol.server.provider.managed.ManagedWorkspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A {@link ManagedWorkspace} that uses {@link FeedConfigStore} to store the
 * feed configuration and also supports dynamic namespaces.
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public class FeedServerWorkspace extends ManagedWorkspace {

  protected final GlobalServerConfiguration globalServerConfiguration;

  public FeedServerWorkspace(ManagedProvider provider,
      GlobalServerConfiguration globalServerConfiguration) {
    super(provider);
    this.globalServerConfiguration = globalServerConfiguration;
  }

  /**
   * Get {@link CollectionInfo} for all of the Collections in the workspace for
   * the namespace container in the {@link RequestContext}
   */
  @Override
  public Collection<CollectionInfo> getCollections(RequestContext request) {
    List<CollectionInfo> collections = new ArrayList<CollectionInfo>();
    try {
      for (FeedConfiguration config : globalServerConfiguration.getFeedConfigStore()
          .getFeedConfigurations(
              request.getTarget().getParameter(AbstractManagedCollectionAdapter.PARAM_NAMESPACE))
          .values())
        collections.add(config);
    } catch (FeedConfigStoreException e) {
      throw new RuntimeException(e);
    }
    return collections;
  }
}
