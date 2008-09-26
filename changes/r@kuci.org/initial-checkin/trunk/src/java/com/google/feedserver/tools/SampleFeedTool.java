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

import com.google.feedserver.client.FeedServerClient;
import com.google.feedserver.client.FeedServerEntry;
import com.google.feedserver.util.FeedServerClientException;
import com.google.feedserver.util.CommonsCliHelper;
import com.google.gdata.client.GoogleService;

import org.apache.commons.beanutils.BeanMap;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * Sample tool that uses the {@link FeedServerClient} to retrieve and write "vehicle" feed
 * entries.  This tool highlights how to create a bean representing a feed entry, instantiate
 * the client and execute CRUD operations against the feedstore.
 * 
 * @author r@kuci.org (Ray Colline)
 *
 */
public class SampleFeedTool {
  
  // Logging instance
  private static final Logger LOG = Logger.getLogger(SampleFeedTool.class);

  // Flags
  public static String feedUrl_FLAG = null;
  public static String feedUrl_HELP = "Url to fetch";
  
  public static String task_FLAG = "get";
  public static String task_HELP = "Operation to perform";
  
  public static String entry_FLAG = null;
  public static String entry_HELP = "xml file to insert or update";
  
  public static void main(String[] args) throws FeedServerClientException, MalformedURLException,
      IOException {
    
    // Bootstrap logging system
    PropertyConfigurator.configure(getBootstrapLoggingProperties());
    
    CommonsCliHelper cliHelper = new CommonsCliHelper();
    cliHelper.register(SampleFeedTool.class);
    cliHelper.parse(args);
    
    FeedServerClient<VehicleBean> feedClient = new FeedServerClient<VehicleBean>(
        new GoogleService("test", "test"), VehicleBean.class);
    
    if (task_FLAG.equals("get")) {
      URL feedUrl = new URL(feedUrl_FLAG);
      for (VehicleBean vehicleBean : feedClient.getEntities(feedUrl)) {
        BeanMap beanMap = new BeanMap(vehicleBean);
        for (Object key : beanMap.keySet()) {
          if (beanMap.get(key) instanceof String[]) {
            for (String value : (String[]) beanMap.get(key)) {
              LOG.info("key " + key + " multi value " + value); 
            } 
          } else {
            LOG.info("key " + key + " value  " + beanMap.get(key));
          }
        }
      }
    } else if (task_FLAG.equals("insert")) {
      String entryXml = readFileIntoString(entry_FLAG);
      FeedServerEntry vehicleEntry = new FeedServerEntry(entryXml);
      feedClient.insertEntry(new URL(feedUrl_FLAG), vehicleEntry);
    } else if (task_FLAG.equals("update")) {
      String entryXml = readFileIntoString(entry_FLAG);
      FeedServerEntry vehicleEntry = new FeedServerEntry(entryXml);
      feedClient.updateEntry(new URL(feedUrl_FLAG), vehicleEntry);
    } else if (task_FLAG.equals("delete")) {
      if (entry_FLAG == null) {
        feedClient.deleteEntry(new URL(feedUrl_FLAG));
      } else {
        String entryXml = readFileIntoString(entry_FLAG);
        FeedServerEntry vehicleEntry = new FeedServerEntry(entryXml);
        feedClient.deleteEntry(new URL(feedUrl_FLAG), vehicleEntry);
      }
    } else {
      LOG.fatal("Incorrect target specified.  Must use 'get', 'insert', 'delete', 'update'");
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
  
  /**
   * Bean representing the vehicle feed.  For every element in the "entity" element, create
   * a private field var, then create getters and setters.
   * 
   * XML for a "vehicle":
   * <pre>
   * <entity>
   *   <name>vehicle3</name>
   *   <owner>Joe</owner>
   *   <price>23002</price>
   *   <propertyName repeatable="true">make</propertyName>
   *   <propertyName>model</propertyName>
   *   <propertyName>year</propertyName>
   *   <propertyValue repeatable="true">Honda</propertyValue>
   *   <propertyValue>Civic Hybrid</propertyValue>
   *   <propertyValue>2007</propertyValue>
   * </entity>
   * </pre>
   * 
   * @author rayc@google.com (Ray Colline)
   */
  public static class VehicleBean {
    
    private String name;
    private String owner;
    private String price;
    private String[] propertyName;
    private String[] propertyValue;
    
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
    public String getOwner() {
      return owner;
    }
    public void setOwner(String owner) {
      this.owner = owner;
    }
    public String getPrice() {
      return price;
    }
    public void setPrice(String price) {
      this.price = price;
    }
    public String[] getPropertyName() {
      return propertyName;
    }
    public void setPropertyName(String[] propertyName) {
      this.propertyName = propertyName;
    }
    public String[] getPropertyValue() {
      return propertyValue;
    }
    public void setPropertyValue(String[] propertyValue) {
      this.propertyValue = propertyValue;
    }
  }
}
