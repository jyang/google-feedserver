/* Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.feedserver.tools;

import com.google.feedserver.client.TypelessFeedServerClient;
import com.google.feedserver.tools.SampleFeedTool.ConfBean;
import com.google.feedserver.util.BeanCliHelper;
import com.google.feedserver.util.ConfigurationBeanException;
import com.google.feedserver.util.FeedServerClientException;
import com.google.gdata.client.GoogleService;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * Sample tool that uses the {@link TypelessFeedServerClient} to retrieve and write "vehicle" feed
 * entries.  This tool highlights how to create a bean representing a feed entry, instantiate
 * the client and execute CRUD operations against the feedstore.
 * 
 * @author r@kuci.org (Ray Colline)
 *
 */
public class TypelessSampleFeedTool {
  
  // Logging instance
  private static final Logger log = Logger.getLogger(TypelessSampleFeedTool.class);

  public static void main(String[] args) throws FeedServerClientException, MalformedURLException,
      IOException, ConfigurationBeanException {
    
    // Bootstrap logging system
    PropertyConfigurator.configure(getBootstrapLoggingProperties());
    
    // Parse config options from command line into bean.
    ConfBean confBean = new ConfBean();
    BeanCliHelper cliHelper = new BeanCliHelper();
    cliHelper.register(confBean);
    cliHelper.parse(args);
    
    TypelessFeedServerClient feedClient = new TypelessFeedServerClient(
        new GoogleService("test", "test"));
    
    if (confBean.getTask().equals("get")) {
      URL feedUrl = new URL(confBean.getFeedUrl());
      for (Map<String, Object> vehicleMap : feedClient.getEntries(feedUrl)) {
        for (String key : vehicleMap.keySet()) { 
          if (vehicleMap.get(key) instanceof String) {
	        log.info("key " + key + " value " + vehicleMap.get(key)); 
          } else if (vehicleMap.get(key) instanceof Object[]) {
            for (Object value : (Object[]) vehicleMap.get(key)) {
              log.info("repeated key" + key + " value " + vehicleMap.get(key)); 
            }
          } else {
            log.warn("Invalid object in typeless map " + key);
          }
        }
      }
    } else if (confBean.getTask().equals("insert")) {
      String entryXml = readFileIntoString(confBean.getEntry());
      feedClient.insertEntry(new URL(confBean.getFeedUrl()), feedClient.getTypelessMapFromXml(entryXml));
    } else if (confBean.getTask().equals("update")) {
      String entryXml = readFileIntoString(confBean.getEntry());
      feedClient.updateEntry(new URL(confBean.getFeedUrl()), feedClient.getTypelessMapFromXml(entryXml));
    } else if (confBean.getTask().equals("delete")) {
      if (confBean.getEntry() == null) {
        feedClient.deleteEntry(new URL(confBean.getFeedUrl()));
      } else {
        String entryXml = readFileIntoString(confBean.getEntry());
        feedClient.deleteEntry(new URL(confBean.getFeedUrl()), feedClient.getTypelessMapFromXml(entryXml));
      }
    } else {
      log.fatal("Incorrect target specified.  Must use 'get', 'insert', 'delete', 'update'");
      cliHelper.usage();
    }
  }
  
  /**
   * Helper function that reads contents of specified file into a String.
   * 
   * @param filename to read.
   * @return string with file contents.
   * @throws IOException if any file operations fail.
   */
  private static String readFileIntoString(String filename) throws IOException {
      File file = new File(filename);
      BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filename));
      byte[] fileContents = new byte[(int) file.length()];
      bis.read(fileContents);
      return new String(fileContents);
  }
  
  /**
   * Returns a base set of logging properties so we can log fatal errors before config parsing is 
   * done.
   * 
   * @return Properties a basic console logging setup.
   */
  public static Properties getBootstrapLoggingProperties() {
    Properties props = new Properties();
    props.setProperty("log4j.rootLogger","info, A");
    props.setProperty("log4j.appender.A", "org.apache.log4j.ConsoleAppender");
    props.setProperty("log4j.appender.A.layout", "org.apache.log4j.PatternLayout");
    props.setProperty("log4j.appender.A.layout.ConversionPattern", "%-4r [%t] %-5p %c %x - %m%n");
    return props;
  }
}
