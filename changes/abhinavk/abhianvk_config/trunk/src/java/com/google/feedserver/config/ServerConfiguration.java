/**
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.feedserver.config;

import com.google.sample.util.SimpleCommandLineParser;

/**
 * Configuration for feed server.
 *
 * @author abhinavk@gmail.com (Abhinav Khandelwal)
 *
 */
public class ServerConfiguration extends Configuration {
  private static final String OPTION_PORT = "port";
  private int port = 8080;

  private static final String OPTION_FEED_SERVER_URI = "uri";
  private String uri = "http://localhost:8080/";

  private static final String OPTION_FEED_NS = "namespace";
  private String namespace = "http://feeds.yourdomain.com/ns/1.0";

  private static final String OPTION_ENTRY_PREFIX = "nsPrefix";
  private String nsPrefix = "g";

  private static final String OPTION_FEED_CONFIG_LOCATION = "feedConfigLocation";
  private String feedConfigLocation = "conf/feedserver/adapter/";

  private static final String OPTION_FEED_ADAPTER_CONFIG_LOCATION
      = "feedAdapterConfigLocation";
  private String feedAdapterConfigLocation = "conf/feedserver/";

  protected static final String PROPERTIES_FILE_SUFFIX = ".properties";

  private ServerConfiguration() { }

  private static final ServerConfiguration serverConfiguration
      = new ServerConfiguration();
  
  public void initialize(SimpleCommandLineParser parser) {
    if (null != parser) {
      if (parser.containsKey(OPTION_PORT)) {
        port = Integer.parseInt(parser.getValue(OPTION_PORT));
      }
      if (parser.containsKey(OPTION_FEED_SERVER_URI)) {
        uri = parser.getValue(OPTION_FEED_SERVER_URI);
      }
      if (parser.containsKey(OPTION_FEED_NS)) {
        namespace = parser.getValue(OPTION_FEED_NS);
      }
      if (parser.containsKey(OPTION_ENTRY_PREFIX)) {
        nsPrefix = parser.getValue(OPTION_ENTRY_PREFIX);
      }
      if (parser.containsKey(OPTION_FEED_CONFIG_LOCATION)) {
        nsPrefix = parser.getValue(OPTION_FEED_CONFIG_LOCATION);
      }
      if (parser.containsKey(OPTION_FEED_ADAPTER_CONFIG_LOCATION)) {
        nsPrefix = parser.getValue(OPTION_FEED_ADAPTER_CONFIG_LOCATION);
      }
    }
  }

  public static ServerConfiguration getInstance() {
    return serverConfiguration;
  }

  public int getPort() {
    return port;
  }

  public String getServerUri() {
    return uri;
  }
  
  public String getFeedNamespace() {
    return namespace;
  }

  public String getFeedNamespacePrefix() {
    return nsPrefix;
  }
  
  public String getFeedConfigLocation() {
    return feedConfigLocation;
  }

  public String getAdapterConfigLocation() {
    return feedAdapterConfigLocation;
  }
}
