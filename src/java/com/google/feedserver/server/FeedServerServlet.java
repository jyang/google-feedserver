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

package com.google.feedserver.server;

import com.google.feedserver.config.GlobalServerConfiguration;
import com.google.feedserver.manager.FeedServerProvider;
import com.google.feedserver.samples.config.AllowAllAclValidator;

import org.apache.abdera.Abdera;
import org.apache.abdera.protocol.server.Provider;
import org.apache.abdera.protocol.server.ServiceManager;
import org.apache.abdera.protocol.server.impl.DefaultProvider;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.apache.abdera.util.ServiceUtil;

import java.util.Map;

/**
 * A wrapper over {@link AbderaServlet}. This exposes the {@link Provider} used
 * in {@link AbderaServlet}.
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public class FeedServerServlet extends AbderaServlet {

  GlobalServerConfiguration globalServerConfiguration;

  public FeedServerServlet(GlobalServerConfiguration configuration) {
    this.globalServerConfiguration = configuration;
    // Make sure that there is a AclValidator for this instance.
    if (null == this.globalServerConfiguration.getAclValidator()) {
      this.globalServerConfiguration.setAclValidator(new AllowAllAclValidator());
    }
  }

  @Override
  protected FeedServerProvider createProvider() {
    Abdera abdera = ServiceManager.getAbdera();
    String providerName = globalServerConfiguration.getProviderClassName();
    if (null == providerName) {
      providerName = DefaultProvider.class.getName();
    }

    FeedServerProvider provider =
        (FeedServerProvider) ServiceUtil.newInstance(FeedServerProvider.class.getName(),
            providerName, abdera);
    Map<String, String> properties = getProperties(getServletConfig());
    provider.init(abdera, properties, globalServerConfiguration);
    return provider;
  }

  public Provider getProvider() {
    return this.provider;
  }

  public GlobalServerConfiguration getGlobalServerConfiguration() {
    return globalServerConfiguration;
  }
}
