/*
 * Copyright 2009 Google Inc.
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
package com.google.feedserver.wrappers;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;
import org.xml.sax.SAXException;

import com.google.feedserver.adapters.AbstractManagedCollectionAdapter;
import com.google.feedserver.adapters.FeedServerAdapterException;
import com.google.feedserver.resource.AuthorizedEntity;
import com.google.feedserver.server.FlagConfig;

/**
 * Wrapper that controls access to resources
 */
public abstract class AccessControlWrapper extends ManagedCollectionAdapterWrapper {

  public AccessControlWrapper(Abdera abdera, FeedConfiguration config) {
    super(abdera, config);
  }

  /**
   * Creates an instance of AccessControlWrapper.
   * @param target Target adapter being wrapped
   * @param wrapperConfig XML for Config bean
   * @throws ParserConfigurationException 
   * @throws IOException 
   * @throws SAXException 
   * @throws InvocationTargetException 
   * @throws IllegalAccessException 
   * @throws IntrospectionException 
   * @throws IllegalArgumentException 
   */
  public AccessControlWrapper(AbstractManagedCollectionAdapter target, String wrapperConfig)
      throws IllegalArgumentException, IntrospectionException, IllegalAccessException,
      InvocationTargetException, SAXException, IOException, ParserConfigurationException {
    super(target, wrapperConfig);
  }

  protected void checkAccess(String operation, RequestContext request, Object entryId)
      throws FeedServerAdapterException {
    if (new Boolean(FlagConfig.enableAccessControl_FLAG).equals(Boolean.TRUE)) {
      doCheckAccess(operation, request, entryId);
    }
  }

  protected abstract void doCheckAccess(String operation, RequestContext request, Object entryId)
  throws FeedServerAdapterException;

  @Override
  public Entry createEntry(RequestContext request, Entry entry)
      throws FeedServerAdapterException {
    checkAccess(AuthorizedEntity.OPERATION_CREATE, request, null);
    return super.createEntry(request, entry);
  }

  @Override
  public void deleteEntry(RequestContext request, Object entryId)
      throws FeedServerAdapterException {
    checkAccess(AuthorizedEntity.OPERATION_DELETE, request, entryId);
    super.deleteEntry(request, entryId);
  }

  @Override
  public Entry retrieveEntry(RequestContext request, Object entryId)
      throws FeedServerAdapterException {
    checkAccess(AuthorizedEntity.OPERATION_RETRIEVE, request, entryId);
    return super.retrieveEntry(request, entryId);
  }

  @Override
  public Feed retrieveFeed(RequestContext request)
      throws FeedServerAdapterException {
    checkAccess(AuthorizedEntity.OPERATION_RETRIEVE, request, null);
    return super.retrieveFeed(request);
  }

  @Override
  public Entry updateEntry(RequestContext request, Object entryId, Entry entry)
      throws FeedServerAdapterException {
    checkAccess(AuthorizedEntity.OPERATION_UPDATE, request, entryId);
    return super.updateEntry(request, entryId, entry);
  }
}
