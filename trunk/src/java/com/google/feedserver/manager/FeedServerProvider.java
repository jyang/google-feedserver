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
import com.google.feedserver.config.FeedServerConfiguration;
import com.google.feedserver.config.GlobalServerConfiguration;
import com.google.feedserver.config.PerNamespaceServerConfiguration;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.impl.RegexTargetResolver;
import org.apache.abdera.protocol.server.provider.managed.ManagedProvider;

import java.util.Map;

/**
 * {@link FeedServerProvider} extends {@link ManagedProvider} so that we can use
 * Abdera for different namespaces. Unlike workspace, the namespaces are
 * resolved at run time.
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public class FeedServerProvider extends ManagedProvider {
  public GlobalServerConfiguration globalServerConfiguration;

  public FeedServerProvider() {
    this(FeedServerConfiguration.getIntance());
    // The target resolver provides the URL path mappings
    super.setTargetResolver(new RegexTargetResolver().setPattern("/([^/#?]+)/(\\?[^#]*)?",
        TargetType.TYPE_SERVICE, AbstractManagedCollectionAdapter.PARAM_NAMESPACE).setPattern(
        "/([^/#?]+)/([^/#?]+);categories", TargetType.TYPE_CATEGORIES,
        AbstractManagedCollectionAdapter.PARAM_NAMESPACE,
        AbstractManagedCollectionAdapter.CATEGORY_PARAMETER).setPattern(
        "/([^/#?]+)/([^/#?;]+)(\\?[^#]*)?", TargetType.TYPE_COLLECTION,
        AbstractManagedCollectionAdapter.PARAM_NAMESPACE,
        AbstractManagedCollectionAdapter.PARAM_FEED).setPattern(
        "/([^/#?]+)/([^/#?]+)/([^/#?]+)(\\?[^#]*)?", TargetType.TYPE_ENTRY,
        AbstractManagedCollectionAdapter.PARAM_NAMESPACE,
        AbstractManagedCollectionAdapter.PARAM_FEED, AbstractManagedCollectionAdapter.PARAM_ENTRY));


  }

  public FeedServerProvider(GlobalServerConfiguration configuration) {
    this.globalServerConfiguration = configuration;
  }

  public void init(Abdera abdera, Map<String, String> properties,
      GlobalServerConfiguration configuration) {
    super.init(abdera, properties);
    this.globalServerConfiguration = configuration;



  }

  public GlobalServerConfiguration getGlobalServerConfiguration() {
    return globalServerConfiguration;
  }


  /**
   * Gets a {@link AbstractManagedCollectionAdapter} for the feed specified in
   * the request.
   */
  public AbstractManagedCollectionAdapter getCollectionAdapter(RequestContext request) {
    try {
      return getCollectionAdapterManager(request).getAdapter(
          request.getTarget().getParameter(AbstractManagedCollectionAdapter.PARAM_FEED));
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Gets a new {@link FeedServerAdapterManager}
   */
  @Override
  public FeedServerAdapterManager getCollectionAdapterManager(RequestContext request) {
    return new FeedServerAdapterManager(abdera, getServerConfiguration(request));
  }

  /**
   * Gets a new server configuration based on the namespace in the request
   * 
   * @param request {@link RequestContext} to determine the namespace from
   */
  @Override
  protected PerNamespaceServerConfiguration getServerConfiguration(RequestContext request) {
    return new PerNamespaceServerConfiguration(globalServerConfiguration, request.getTarget()
        .getParameter(AbstractManagedCollectionAdapter.PARAM_NAMESPACE));
  }
}
