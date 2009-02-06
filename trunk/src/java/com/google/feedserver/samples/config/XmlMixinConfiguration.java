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

package com.google.feedserver.samples.config;

import com.google.feedserver.config.MixinConfiguration;
import com.google.feedserver.util.FeedServerUtil;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Configuration class for mixin's.
 * 
 * Example Configuration:
 * 
 * <pre>
 * &lt;mixin&gt;
 *   &lt;adapterName&gt; someAdapterToBeWrapped &lt;/adapterName&gt;
 *   &lt;wrapperConfig&gt; ...wrapper configuration... &lt;/config&gt;
 * &lt;/mixin&gt;
 * </pre>
 * 
 * or
 * 
 * <pre>
 * &lt;mixin&gt;
 *   &lt;wrapperClassName&gt; com.domain.SomeAdapterWrapper &lt;/className&gt;
 *   &lt;wrapperConfig&gt; ...wrapper configuration... &lt;/config&gt;
 * &lt;/mixin&gt;
 * </pre>
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public class XmlMixinConfiguration implements MixinConfiguration {

  private static final String ERROR_NOT_SUPPORTED = "Not supported";
  public static final String MIXIN = "mixin";

  private String targetAdapterName;
  private String wrapperName;
  private String wrapperConfig;

  public XmlMixinConfiguration(String configXml) {
    parseMixinConfiguration(configXml);
  }

  public XmlMixinConfiguration() {
  }

  private void parseMixinConfiguration(String configData) {
    Document doc = FeedServerUtil.parseDocument(configData);
    Node adapterWrapperConfigNode = doc.getFirstChild();
    if (adapterWrapperConfigNode == null || !adapterWrapperConfigNode.getNodeName().equals(MIXIN)) {
      throw new UnsupportedOperationException(ERROR_NOT_SUPPORTED);
    }
    NodeList adapterWrapperConfigNoldChildList = adapterWrapperConfigNode.getChildNodes();
    for (int i = 0; i < adapterWrapperConfigNoldChildList.getLength(); i++) {
      Node childNode = adapterWrapperConfigNoldChildList.item(i);
      if (childNode.getNodeName().equals(ADAPTER_NAME)) {
        setTargetAdapterName(childNode.getTextContent());
      } else if (childNode.getNodeName().equals(WRAPPER_CONFIG)) {
        setWrapperConfig(childNode.getTextContent());
      } else if (childNode.getNodeName().equals(WRAPPER_CLASS_NAME)) {
        setWrapperName(childNode.getTextContent());
      }
    }
  }

  public String getTargetAdapterName() {
    return targetAdapterName;
  }

  public String getWrapperName() {
    return wrapperName;
  }

  public String getWrapperConfig() {
    return wrapperConfig;
  }

  public void setTargetAdapterName(String targetAdapterName) {
    this.targetAdapterName = targetAdapterName;
  }

  public void setWrapperName(String wrapperName) {
    this.wrapperName = wrapperName;
  }

  public void setWrapperConfig(String wrapperConfig) {
    this.wrapperConfig = wrapperConfig;
  }
}
