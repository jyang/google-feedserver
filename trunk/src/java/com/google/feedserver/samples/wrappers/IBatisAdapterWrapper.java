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

package com.google.feedserver.samples.wrappers;

import com.google.feedserver.adapters.AbstractManagedCollectionAdapter;
import com.google.feedserver.adapters.FeedServerAdapterException;
import com.google.feedserver.config.NamespacedAdapterConfiguration;
import com.google.feedserver.samples.configstore.SampleFileSystemFeedConfigStore;
import com.google.feedserver.util.FeedServerUtil;
import com.google.feedserver.util.FileSystemConfigStoreUtil;
import com.google.feedserver.wrappers.ManagedCollectionAdapterWrapper;

import com.ibatis.sqlmap.engine.builder.xml.SqlMapClasspathEntityResolver;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.transform.TransformerException;

/**
 * The wrapper that will be used to merge the feed database resource files and
 * adding it to the sqlMapConfig as sqlMap resources.
 * <p>
 * This allows cleaner separation of feed specific database table definitions
 * and queries from the adapter configuration which can be generic to any feeds.
 * At runtime this wrapper is applied before the target adapter methods are
 * invoked sothat the target adapter is passed the sqlMap for the specific feed
 * in question
 * </p>
 * 
 * @author rakeshs101981@gmail.com (Rakesh Shete)
 */
public class IBatisAdapterWrapper extends ManagedCollectionAdapterWrapper {

  public static final String ATTRIBUTE_RESOURCE = "resource";
  public static final String NODE_SQL_MAP_NAME = "sqlMap";
  public static final String SQLMAP_DOCTYPE_STRING =
      "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n\n" + "<!DOCTYPE sqlMapConfig \n\n"
          + "PUBLIC \"-//ibatis.apache.org//DTD SQL Map Config 2.0//EN\" \n"
          + "\"http://ibatis.apache.org/dtd/sql-map-config-2.dtd\">";

  public static final Logger logger = Logger.getLogger(IBatisAdapterWrapper.class.getName());

  /**
   * A wrapper constructor to be used when a wrapper is declared as a part of
   * AdapterConfiguration of another adapter in AdapterConfig feed.
   * 
   * The config of target adapter defines all the wrappers to be used for this
   * adapter and also their configurations.
   * 
   * @param targetAdapter the target adapter for this wrapper.
   * @param wrapperConfig the configuration data for this wrapper.
   */
  public IBatisAdapterWrapper(AbstractManagedCollectionAdapter targetAdapter, String wrapperConfig) {
    super(targetAdapter, wrapperConfig);
  }

  /**
   * A wrapper constructor to be used when this wrapper is defined and used as
   * an adapter in AdapterConfig feed.
   * 
   * The config in AdapterConfig defines the target adapter for this wrapper and
   * the configuration data for this wrapper.
   * 
   * @param abdera The abdera configuration
   * @param config The feed configuration
   */
  public IBatisAdapterWrapper(Abdera abdera, FeedConfiguration config) {
    super(abdera, config);
  }

  @Override
  public Feed retrieveFeed(RequestContext request) throws FeedServerAdapterException {
    // Set the feed db config details as SQL resource
    setFeedDBResourcesToAdapterConfig();
    return super.retrieveFeed(request);
  }

  @Override
  public Entry createEntry(RequestContext request, Entry entry) throws FeedServerAdapterException {
    // Set the feed db config details as SQL resource
    setFeedDBResourcesToAdapterConfig();
    return super.createEntry(request, entry);
  }

  @Override
  public Entry updateEntry(RequestContext request, Object entryId, Entry entry)
      throws FeedServerAdapterException {
    // Set the feed db config details as SQL resource
    setFeedDBResourcesToAdapterConfig();
    return super.updateEntry(request, entryId, entry);
  }

  @Override
  public ResponseContext deleteEntry(RequestContext request) {
    // Set the feed db config details as SQL resource
    setFeedDBResourcesToAdapterConfig();
    return super.deleteEntry(request);
  }

  @Override
  public void deleteEntry(RequestContext request, Object entryId) throws FeedServerAdapterException {
    // Set the feed db config details as SQL resource
    setFeedDBResourcesToAdapterConfig();
    super.deleteEntry(request, entryId);
  }

  @Override
  public Entry retrieveEntry(RequestContext request, Object entryId)
      throws FeedServerAdapterException {
    // Set the feed db config details as SQL resource
    setFeedDBResourcesToAdapterConfig();
    return super.retrieveEntry(request, entryId);
  }


  /**
   * Sets the feed db config details for the given feed as sqlMap resource
   */
  private void setFeedDBResourcesToAdapterConfig() {
    NamespacedAdapterConfiguration adapterConfig = getConfiguration().getAdapterConfiguration();

    // Read the adapter config value. This will be
    // <sqlMapConfig>...</sqlMapConfig>
    String sqlMapConfig = adapterConfig.getConfigData();

    // Get the feed config value to be set as sqlMap resource
    String sqlMapFeedUrl = getFeedConfigData();

    Document adapterConfigDocument =
        FeedServerUtil.parseDocument(sqlMapConfig, new SqlMapClasspathEntityResolver());
    if (null == adapterConfigDocument) {
      String message = "adapter config document is null";
      RuntimeException e = new RuntimeException(message);
      logger.log(Level.SEVERE, message, e);
      throw e;
    }
    Element adapterParentNode = adapterConfigDocument.getDocumentElement();
    if (!adapterParentNode.getNodeName().equals("sqlMapConfig")) {
      String message = "adapter config document element not <sqlMapConfig>";
      RuntimeException e = new RuntimeException(message);
      logger.log(Level.SEVERE, message, e);
      throw e;
    }
    Element node = adapterConfigDocument.createElement(NODE_SQL_MAP_NAME);
    node.setAttribute(ATTRIBUTE_RESOURCE, sqlMapFeedUrl);
    adapterParentNode.appendChild(node);
    try {
      adapterConfig.setConfigData(SQLMAP_DOCTYPE_STRING
          + FeedServerUtil.getDocumentAsXml(adapterConfigDocument));
    } catch (TransformerException e) {
      logger.log(Level.SEVERE, e.getMessage(), e);
      throw new RuntimeException("invalid configuration: " + e.getMessage());
    }
  }

  /**
   * Returns the feed config data to be set as sqlMap resource.
   * <p>
   * If it is the name of the file, then, it will set the correct base path
   * which be used to locate it at runtime by IBatis
   * </p>
   * <p>
   * In other cases the config value will be retruned as is
   * </p>
   * 
   * 
   * @return The feed config value to be set as sqlMap resource.
   */
  private String getFeedConfigData() {
    String dbResourceValues = getConfiguration().getConfigData();

    // Check if it is a filepath
    if (FileSystemConfigStoreUtil.checkIfStringIsFilePath(dbResourceValues)) {
      // Append the base path s.t it can be located by the runtime classloader
      String basePath = SampleFileSystemFeedConfigStore.BASE_CONFIGURATION_PATH;
      String fileName =
          dbResourceValues.substring(dbResourceValues
              .indexOf(FileSystemConfigStoreUtil.FILE_INDICATOR) + 1);

      InputStream reader =
          Thread.currentThread().getContextClassLoader().getResourceAsStream(
              basePath + "/" + fileName);
      if (reader == null) {
        basePath = basePath.substring(basePath.indexOf("/") + 1);
      }

      dbResourceValues =
          new StringBuilder(basePath).append("/").append(
              dbResourceValues.substring(dbResourceValues
                  .indexOf(FileSystemConfigStoreUtil.FILE_INDICATOR) + 1)).toString();
    }

    return dbResourceValues.trim();

  }



}
