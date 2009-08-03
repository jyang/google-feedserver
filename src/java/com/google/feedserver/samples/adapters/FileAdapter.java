/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. The ASF licenses this file to You under the
 * Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License. For additional information regarding copyright in this work,
 * please see the NOTICE file in the top level directory of this distribution.
 */
package com.google.feedserver.samples.adapters;

import com.google.feedserver.adapters.AbstractManagedCollectionAdapter;
import com.google.feedserver.adapters.FeedServerAdapterException;
import com.google.feedserver.config.FeedServerConfiguration;
import com.google.feedserver.config.NamespacedAdapterConfiguration;
import com.google.feedserver.metadata.FeedInfo;
import com.google.feedserver.util.ContentUtil;
import com.google.feedserver.util.FileUtil;
import com.google.feedserver.util.XmlUtil;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;
import org.xml.sax.SAXException;

import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

/**
 * An adapter that uses the file system to store resources.  A feed is stored as a directory.
 * An entry is stored as a file under that directory.  The name of the entry file is {entryId}.
 */
public class FileAdapter extends AbstractManagedCollectionAdapter {

  protected XmlUtil xmlUtil = new XmlUtil();
  protected FileUtil fileUtil = new FileUtil();

  public static class FileAdapterConfig {
    // root directory of all feeds
    protected String root;

    public FileAdapterConfig() {}

    public FileAdapterConfig(String root) {
      setRoot(root);
    }

    public String getRoot() {
      return root;
    }

    public void setRoot(String root) {
      this.root = root;
    }
  }

  protected FileAdapterConfig fileAdapterConfig = new FileAdapterConfig();

  public FileAdapter(Abdera abdera, FeedConfiguration config) throws IllegalArgumentException,
      IntrospectionException, IllegalAccessException, InvocationTargetException, SAXException,
      IOException, ParserConfigurationException {
    this(new XmlUtil(), new FileUtil(), abdera, config);
  }

  public FileAdapter(XmlUtil xmlUtil, FileUtil fileUtil, Abdera abdera, FeedConfiguration config)
      throws IllegalArgumentException, IntrospectionException, IllegalAccessException,
      InvocationTargetException, SAXException, IOException, ParserConfigurationException {
    this(xmlUtil, fileUtil, abdera, config, getFileAdapterConfig(xmlUtil, config));
  }

  public FileAdapter(XmlUtil xmlUtil, FileUtil fileUtil, Abdera abdera,
      FeedConfiguration config, FileAdapterConfig fileAdapterConfig)
      throws IllegalArgumentException {
    super(abdera, config);

    this.xmlUtil = xmlUtil;
    this.fileUtil = fileUtil;
    this.fileAdapterConfig = fileAdapterConfig;
  }

  @Override
  public Entry createEntry(RequestContext request, Entry entry) throws FeedServerAdapterException {
    Map<String, Object> properties = getPropertyMapForEntry(entry);
    String entityFileContent = xmlUtil.convertPropertiesToXml(properties);

    Object entryId = properties.get(ContentUtil.ID);
    if (entryId == null) {
      entryId = properties.get(ContentUtil.NAME);
    }
    if (entryId == null) {
      entryId = System.currentTimeMillis();
    }
    String entityFilePath = getEntityFilePath(entryId);

    if (fileUtil.exists(entityFilePath)) {
      throw new FeedServerAdapterException(
          FeedServerAdapterException.Reason.ENTRY_ALREADY_EXISTS,
          "entry " + entryId + " already exists");
    }

    try {
      fileUtil.writeFileContents(entityFilePath, entityFileContent);
      return entry;
    } catch (IOException e) {
      throw new FeedServerAdapterException(
          FeedServerAdapterException.Reason.IO_ERROR, e.getMessage());
    }
  }

  @Override
  public void deleteEntry(RequestContext request, Object entryId) throws FeedServerAdapterException {
    if (!fileUtil.delete(getEntityFilePath(entryId))) {
      throw new FeedServerAdapterException(
          FeedServerAdapterException.Reason.ERROR_EXECUTING_ADAPTER_REQUEST,
          "entry not successfully deleted");
    }
  }

  @Override
  public FeedInfo getFeedInfo(RequestContext request) {
    return getFeedInfoFromConfig(request);
  }

  @Override
  public Entry retrieveEntry(RequestContext request, Object entryId)
      throws FeedServerAdapterException {
    try {
      String entityFilePath = getEntityFilePath(entryId);
      String entityFileContent = fileUtil.readFileContents(entityFilePath);
      Map<String, Object> entityProperties = xmlUtil.convertXmlToProperties(entityFileContent);
      entityProperties.put(ContentUtil.ID, entryId);
      return createEntryFromProperties(
          null, entityProperties);
    } catch (IOException e) {
      throw new FeedServerAdapterException(
          FeedServerAdapterException.Reason.IO_ERROR, e.getMessage());
    } catch (SAXException e) {
      throw new FeedServerAdapterException(
          FeedServerAdapterException.Reason.ENTITY_DATA_INVALID, e.getMessage());
    } catch (ParserConfigurationException e) {
      throw new FeedServerAdapterException(
          FeedServerAdapterException.Reason.FEED_CONFIGURATION_NOT_CORRECT, e.getMessage());
    }
  }

  protected String getEntityFilePath(Object entryId) {
    return fileAdapterConfig.getRoot() + File.separator + config.getFeedId() +
        File.separator + entryId;
  }

  @Override
  public Feed retrieveFeed(RequestContext request) throws FeedServerAdapterException {
    try {
      String feedId = config.getFeedId();
      String feedDirPath = fileAdapterConfig.getRoot() + File.separator + feedId;
      File feedDir = new File(feedDirPath);
      Feed feed = createFeed();
      for (File entityFile: feedDir.listFiles()) {
        if (entityFile.isFile() && !entityFile.isHidden()) {
          String entityFileContent = fileUtil.readFileContents(entityFile);
          Map<String, Object> entityProperties = xmlUtil.convertXmlToProperties(entityFileContent);
          entityProperties.put(ContentUtil.ID, entityFile.getName());
          createEntryFromProperties(feed, entityProperties);
        }
      }
      return feed;
    } catch (IOException e) {
      throw new FeedServerAdapterException(
          FeedServerAdapterException.Reason.IO_ERROR, e.getMessage());
    } catch (SAXException e) {
      throw new FeedServerAdapterException(
          FeedServerAdapterException.Reason.ENTITY_DATA_INVALID, e.getMessage());
    } catch (ParserConfigurationException e) {
      throw new FeedServerAdapterException(
          FeedServerAdapterException.Reason.FEED_CONFIGURATION_NOT_CORRECT, e.getMessage());
    }
  }

  @Override
  public Entry updateEntry(RequestContext request, Object entryId, Entry entry)
      throws FeedServerAdapterException {
    String entityFilePath = getEntityFilePath(entryId);
    if (!fileUtil.exists(entityFilePath)) {
      throw new FeedServerAdapterException(
          FeedServerAdapterException.Reason.ENTRY_DOES_NOT_EXIST,
          "entry " + entryId + " does not exist");
    }

    Map<String, Object> properties = getPropertyMapForEntry(entry);
    String entityFileContent = xmlUtil.convertPropertiesToXml(properties);
    try {
      fileUtil.writeFileContents(entityFilePath, entityFileContent);
      return entry;
    } catch (IOException e) {
      throw new FeedServerAdapterException(
          FeedServerAdapterException.Reason.IO_ERROR, e.getMessage());
    }
  }

  protected static FileAdapterConfig getFileAdapterConfig(XmlUtil xmlUtil, FeedConfiguration config)
      throws IntrospectionException, IllegalAccessException, InvocationTargetException,
      SAXException, IOException, ParserConfigurationException {
    NamespacedAdapterConfiguration adapterConfig =
        (NamespacedAdapterConfiguration) config.getAdapterConfiguration();
    String fileAdapterConfigValue =
        (String) adapterConfig.getProperty(FeedServerConfiguration.CONFIG_VALUE_KEY);
    FileAdapterConfig fileAdapterConfig = new FileAdapterConfig();
    xmlUtil.convertXmlToBean(fileAdapterConfigValue, fileAdapterConfig);
    return fileAdapterConfig;
  }
}
