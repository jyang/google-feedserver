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

package com.google.feedserver.metadata;

import com.google.feedserver.util.XmlUtil;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

/**
 * @author abhinavk@gmail.com (Abhinav Khandelwal)
 * 
 */
public class SimpleFeedInfo implements FeedInfo {

  private static final String DEFAULT_ENTITY_PATH = "/entry/content/entity";

  private final Map<String, Object> entityInfoMap;
  private final EntityInfo entityInfo;

  /**
   * A simple implementation of {@link FeedInfo}.
   * 
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   */
  public SimpleFeedInfo(String feedInfoXml) throws SAXException, IOException,
      ParserConfigurationException {
    this.entityInfoMap = new XmlUtil().convertXmlToProperties(feedInfoXml);
    this.entityInfo = new SimpleEntityInfo(entityInfoMap);
  }

  @SuppressWarnings("unchecked")
  public SimpleFeedInfo(Map<String, Object> feedInfoMap) {
    this.entityInfoMap = (Map<String, Object>) feedInfoMap.get(ENTITY_INFO);
    this.entityInfo = new SimpleEntityInfo(entityInfoMap);
  }

  @Override
  public EntityInfo getEntityInfo() {
    if (null == entityInfo) {
      return null;
    }
    return entityInfo;
  }

  @Override
  public String getPath() {
    return DEFAULT_ENTITY_PATH;
  }
}
