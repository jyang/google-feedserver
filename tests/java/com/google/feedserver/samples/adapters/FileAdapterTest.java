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

import com.google.feedserver.samples.adapters.FileAdapter.FileAdapterConfig;
import com.google.feedserver.util.FileUtil;
import com.google.feedserver.util.XmlUtil;

import junit.framework.TestCase;

import org.easymock.classextension.EasyMock;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Entry;
import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;
import org.apache.abdera.protocol.server.provider.managed.ServerConfiguration;

import java.io.File;

/**
 * FileAdpater tests
 *
 * TODO: Add error cases
 */
public class FileAdapterTest extends TestCase {

  public static final String ROOT_DIR = "root-dir";
  public static final String FEED_ID = "feed-id";
  public static final String ENTRY_ID = "entry-id";
  public static final String ENTITY = "<entity><name>" + ENTRY_ID + "</name></entity>";

  protected FileAdapter adapter;
  protected FileUtil fileUtilMock;

  protected ServerConfiguration serverConfig = new ServerConfiguration() {
    @Override
    public String getAdapterConfigLocation() {
      return null;
    }

    @Override
    public String getFeedConfigLocation() {
      return null;
    }

    @Override
    public String getFeedConfigSuffix() {
      return null;
    }

    @Override
    public String getFeedNamespace() {
      return null;
    }

    @Override
    public String getFeedNamespacePrefix() {
      return null;
    }

    @Override
    public int getPort() {
      return 0;
    }

    @Override
    public String getServerUri() {
      return FEED_ID;
    }

    @Override
    public FeedConfiguration loadFeedConfiguration(String feedId) throws Exception {
      return null;
    }
  };

  public String getFilePath(String fileName) {
    return ROOT_DIR + File.separator + fileName;
  }

  protected Entry buildEntry() {
    Entry entry = adapter.getAbdera().newEntry();
    entry.setContent(ENTITY);
    return entry;
  }

  @Override
  public void setUp() throws Exception {
    fileUtilMock = EasyMock.createMock(FileUtil.class);
    FeedConfiguration feedConfig = new FeedConfiguration(FEED_ID, null, null, null, serverConfig);
    FileAdapterConfig fileAdapterConfig = new FileAdapterConfig(ROOT_DIR);
    adapter = new FileAdapter(
        new XmlUtil(), fileUtilMock, new Abdera(), feedConfig, fileAdapterConfig);
  }

  // test create
  public void testCreateEntry() throws Exception {
    String entityFilePath = adapter.getEntityFilePath(ENTRY_ID);
    EasyMock.expect(
        fileUtilMock.exists(entityFilePath))
        .andReturn(false);
    fileUtilMock.writeFileContents(entityFilePath, ENTITY);

    EasyMock.replay(fileUtilMock);

    adapter.createEntry(null, buildEntry());

    EasyMock.verify(fileUtilMock);
  }

  // test get
  public void testRetrieveEntry() throws Exception {
    EasyMock.expect(
        fileUtilMock.readFileContents(adapter.getEntityFilePath(ENTRY_ID)))
        .andReturn(ENTITY);

    EasyMock.replay(fileUtilMock);

    Entry entry = adapter.retrieveEntry(null, ENTRY_ID);

    EasyMock.verify(fileUtilMock);
  }

  // test update
  public void testUpdateEntry() throws Exception {
    String entityFilePath = adapter.getEntityFilePath(ENTRY_ID);
    EasyMock.expect(
        fileUtilMock.exists(entityFilePath))
        .andReturn(true);
    fileUtilMock.writeFileContents(entityFilePath, ENTITY);

    EasyMock.replay(fileUtilMock);

    Entry entry2 = adapter.updateEntry(null, ENTRY_ID, buildEntry());

    EasyMock.verify(fileUtilMock);
  }

  // test delete
  public void testDeleteEntry() throws Exception {
    EasyMock.expect(
        fileUtilMock.delete(adapter.getEntityFilePath(ENTRY_ID)))
        .andReturn(true);

    EasyMock.replay(fileUtilMock);

    adapter.deleteEntry(null, ENTRY_ID);

    EasyMock.verify(fileUtilMock);
  }
}
