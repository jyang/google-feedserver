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

package com.google.feedserver.samples.configstore;

import com.google.feedserver.config.FeedServerConfiguration;
import com.google.feedserver.config.MixinConfiguration;
import com.google.feedserver.config.NamespacedAdapterConfiguration;
import com.google.feedserver.config.NamespacedFeedConfiguration;
import com.google.feedserver.config.PerNamespaceServerConfiguration;
import com.google.feedserver.configstore.FeedConfigStore;
import com.google.feedserver.configstore.FeedConfigStoreException;
import com.google.feedserver.configstore.FeedConfigStoreException.Reason;
import com.google.feedserver.samples.config.MapMixinConfiguration;
import com.google.feedserver.samples.config.XmlMixinConfiguration;
import com.google.feedserver.util.ConfigStoreUtil;
import com.google.feedserver.util.FileSystemConfigStoreUtil;

import org.apache.abdera.protocol.server.provider.managed.CollectionAdapterConfiguration;
import org.apache.abdera.protocol.server.provider.managed.FeedConfiguration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a sample implementation of the {@link FeedConfigStore}. The feed and
 * adapter configurations are stored as files on the file system.
 * <p>
 * Following folder structure is used: <br/>
 * <ul>
 * <li><Root deployment directory>
 * <ul>
 * <li>conf
 * <ul>
 * <li>feedserver<ul><li>FeedConfig<ul><li>'%feedId%'.properties -- The file with feed
 * configuration values </li></ul></li></ul> <ul><li>AdapterConfig<ul><li>'%adapterName%'.properties -- The
 * file with adapter configuration values</li></ul></li></ul></li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 * 
 * @author rakeshs@google.com (Rakesh Shete)
 * 
 */
public class SampleFileSystemFeedConfigStore implements FeedConfigStore {

  private static final Logger logger =
      Logger.getLogger(SampleFileSystemFeedConfigStore.class.getName());

  /**
   * The placeholder for the namespace directory name which will be replaced by
   * the actual namepsace provided with the request
   */
  private static final String namespacePlaceHolder = "namespace";

  /**
   * The base path of the directory under which the feed and adapter
   * configuration will be stored
   */
  public static String BASE_CONFIGURATION_PATH = "conf/feedserver";

  /**
   * The base path to the feed configuration directory with the namespace
   * placeholder which should be replaced with the actual namespace at runtime
   */
  private static String FEED_CONFIGURATION_PATH =
      BASE_CONFIGURATION_PATH + "/" + namespacePlaceHolder + "/"
          + FeedServerConfiguration.FEED_CONFIGURATION;

  /**
   * The base path to the adapter configuration directory with the namespace
   * placeholder which should be replaced with the actual namespace at runtime
   */
  private static String ADAPTER_CONFIGURATION_PATH =
      BASE_CONFIGURATION_PATH + "/" + namespacePlaceHolder + "/"
          + FeedServerConfiguration.ADAPTER_CONFIGURATION;

  private static String ADAPTER_PATH =
      BASE_CONFIGURATION_PATH + "/" + namespacePlaceHolder + "/" + "Adapter";

  /**
   * The file extension to be used for constructing the file name
   */
  private static String FILE_EXTENSION = ".properties";

  /**
   * The file extension to be used for constructing the file name
   */
  private static String MIXIN_FILE_EXTENSION = ".xml";

  /**
   * The property name identifying the adapter class name
   */
  public static final String ADAPTER_CLASS_NAME = "className";



  /**
   * Default constructor
   */
  public SampleFileSystemFeedConfigStore() {
  }



  /**
   * Stores the feed configuration to the file system as properties file under
   * the given namespace
   * 
   * @param namespace The namespace under which the feed configuration has to be
   *        stored
   * @param config The feed configuration
   * @throws FeedConfigStoreException If any exception is encountered while
   *         storing the feed configuration
   * 
   * @see com.google.feedserver.configstore.FeedConfigStore#addFeed(String,
   *      FeedConfiguration)
   */
  @Override
  public void addFeed(String namespace, FeedConfiguration config) throws FeedConfigStoreException {
    // Get the feed configuration properties to save to file
    Properties prop = extractFeedConfigProperties(config);

    String adapterName = prop.getProperty(FeedServerConfiguration.FEED_ADAPTER_NAME_KEY);

    if (!hasAdapterConfiguration(namespace, adapterName)) {
      throw new FeedConfigStoreException(Reason.ADAPTER_CONFIG_DOES_NOT_EXIST,
          "There is no adapter configuration with the given name : " + adapterName
              + " Add the adapter configuration and then try adding feed configuration");
    }

    // Check and create the namespace and feed configuration directories if
    // they don't exist
    createNamespaceAndFeedConfigDir(namespace);

    // Get the handle to the file at the given location
    File feedFile = getFeedConfigFilePath(namespace, config.getFeedId());

    // Check that the file does not exist
    if (feedFile.exists()) {
      throw new FeedConfigStoreException(Reason.INTERNAL_ERROR, "The feed already exists");
    }

    // Write the feed values to the file
    writeToFile(prop, feedFile, true);
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.feedserver.configstore.FeedConfigStore
   * #getAdapterConfiguration(java.lang.String, java.lang.String)
   */
  @Override
  public CollectionAdapterConfiguration getAdapterConfiguration(String namespace, String adapterName)
      throws FeedConfigStoreException {
    logger.log(Level.FINE, "Retrieving the adapter configuration with given namespace : "
        + namespace + " and adapterName : " + adapterName);

    return getAdapterConfig(namespace, adapterName);

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.feedserver.configstore.FeedConfigStore
   * #getAdapterConfigurations(java.lang.String)
   */
  @Override
  public Map<String, CollectionAdapterConfiguration> getAdapterConfigurations(String namespace)
      throws FeedConfigStoreException {
    // Check that the namespace and feed configuration directories exist
    File adapterConfigDir = checkNamespaceAndAdapterConfigDir(namespace);

    Map<String, CollectionAdapterConfiguration> adapterConfigurations =
        new HashMap<String, CollectionAdapterConfiguration>();
    // Traverse all the files, store the filenames (without the extension)
    // as
    // adpaternames and get the adapter configurations for the same
    for (String adapterConfigFileName : adapterConfigDir.list()) {
      String adapterName =
          adapterConfigFileName.substring(0, adapterConfigFileName.lastIndexOf("."));
      NamespacedAdapterConfiguration adapterConfiguration = null;

      try {
        adapterConfiguration = getAdapterConfig(namespace, adapterName);
      } catch (FeedConfigStoreException e) {
        logger.log(Level.SEVERE, "Unable to retrieve adapter configuration with adapterName : "
            + adapterName + "  with the given namespace : " + namespace);
        continue;
      }
      adapterConfigurations.put(adapterName, adapterConfiguration);
    }
    return adapterConfigurations;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.feedserver.configstore.FeedConfigStore
   * #getAllowedAdapters(java.lang.String)
   */
  @Override
  public Collection<String> getAllowedAdapters(String namespace) throws FeedConfigStoreException {
    // Check that the namespace and feed configuration directories exist
    File adapterConfigDir = checkNamespaceAndAdapterConfigDir(namespace);
    List<String> adapterNames = new ArrayList<String>();

    for (String adapterConfigFileName : adapterConfigDir.list()) {
      adapterNames.add(adapterConfigFileName.substring(0, adapterConfigFileName.lastIndexOf(".")));
    }

    Collections.sort(adapterNames);
    return adapterNames;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.feedserver.configstore.FeedConfigStore
   * #getFeedConfiguration(java.lang.String, java.lang.String)
   */
  @Override
  public FeedConfiguration getFeedConfiguration(String namespace, String feedId)
      throws FeedConfigStoreException {

    logger.log(Level.FINE, "Retrieving the feed with given namespace : " + namespace
        + " and feedId : " + feedId);

    // Construct the feed file path
    File feedFile = getFeedConfigFilePath(namespace, feedId);

    logger.log(Level.FINEST, "The path of the file to fetch the feed configuration from : "
        + feedFile.getAbsolutePath());

    try {
      // Get the feed configuration
      return getFeedConfiguration(namespace, feedId, feedFile);
    } catch (FileNotFoundException e) {
      throw new FeedConfigStoreException(Reason.FEED_DOES_NOT_EXIST,
          "Please check that the feed namespace : " + namespace + " and feedId : " + feedId
              + " are valid");
    } catch (IOException e) {
      throw new FeedConfigStoreException(Reason.INTERNAL_ERROR,
          "Problems encountered while fetching the feed configuration for the given namespace : "
              + namespace + " and feedId : " + feedId);
    }
  }



  /*
   * (non-Javadoc)
   * 
   * @see com.google.feedserver.configstore.FeedConfigStore
   * #getFeedConfigurations(java.lang.String)
   */
  @Override
  public Map<String, FeedConfiguration> getFeedConfigurations(String namespace)
      throws FeedConfigStoreException {
    // Check that the namespace and feed configuration directories exist
    File feedConfigDir = checkNamespaceAndFeedConfigDir(namespace);

    Map<String, FeedConfiguration> feedConfigurations = new HashMap<String, FeedConfiguration>();
    // Traverse all the files, store the filenames (without the extension)
    // as feedIds and get the feed configurations for the same
    for (String feedConfigFileName : feedConfigDir.list()) {
      if (feedConfigFileName.endsWith(FILE_EXTENSION)) {
        String feedId = feedConfigFileName.substring(0, feedConfigFileName.lastIndexOf("."));
        File feedConfigFile = getFeedConfigFilePath(namespace, feedId);
        FeedConfiguration feedConfiguration;
        try {
          feedConfiguration = getFeedConfiguration(namespace, feedId, feedConfigFile);
        } catch (FileNotFoundException e) {
          logger.log(Level.WARNING, "Unable to retrieve feed configuration for feedId : " + feedId
              + "  with the given namespace : " + namespace);
          continue;
        } catch (IOException e) {
          logger.log(Level.WARNING, "Unable to retrieve feed configuration for feedId : " + feedId
              + "  with the given namespace : " + namespace);
          continue;
        }

        feedConfigurations.put(feedId, feedConfiguration);
      }
    }
    return feedConfigurations;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.feedserver.configstore.FeedConfigStore
   * #getFeedIds(java.lang.String)
   */
  @Override
  public Collection<String> getFeedIds(String namespace) throws FeedConfigStoreException {
    // Check that the namespace and feed configuration directories exist
    File feedConfigDir = checkNamespaceAndFeedConfigDir(namespace);

    List<String> feedIds = new ArrayList<String>();
    // Traverse all the files and store the filenames (without the
    // extension) as
    // feedIds
    for (String feedConfigFileName : feedConfigDir.list()) {
      if (feedConfigFileName.endsWith(FILE_EXTENSION)) {
        feedIds.add(feedConfigFileName.substring(0, feedConfigFileName.lastIndexOf(".")));
      }
    }

    // Sort the feedIds
    Collections.sort(feedIds);

    return feedIds;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.feedserver.configstore.FeedConfigStore
   * #deleteFeed(java.lang.String, java.lang.String)
   */
  @Override
  public void deleteFeed(String namespace, String feedId) throws FeedConfigStoreException {
    // Construct and get the feed file handle
    File feedFile = getFeedConfigFilePath(namespace, feedId);

    // Check that the feed file exists
    if (!feedFile.exists()) {
      throw new FeedConfigStoreException(Reason.FEED_DOES_NOT_EXIST,
          "No feed configuration exists with the given feedId : " + feedId
              + "  for the given namespace : " + namespace);
    }

    // Delete the feed configuration
    if (!feedFile.delete()) {
      throw new FeedConfigStoreException(Reason.INTERNAL_ERROR,
          "Unable to delete the feed configuration for feedId : " + feedId
              + "  for the given namespace : " + namespace);
    }

  }


  /*
   * (non-Javadoc)
   * 
   * @see com.google.feedserver.configstore.FeedConfigStore
   * #hasFeed(java.lang.String, java.lang.String)
   */
  @Override
  public boolean hasFeed(String namespace, String feedId) throws FeedConfigStoreException {
    // Get the handle to the feed config file
    File feedFile = getFeedConfigFilePath(namespace, feedId);
    return feedFile.exists();
  }



  /*
   * (non-Javadoc)
   * 
   * @see com.google.feedserver.configstore.FeedConfigStore
   * #updateFeed(java.lang.String,
   * org.apache.abdera.protocol.server.provider.managed.FeedConfiguration)
   */
  @Override
  public void updateFeed(String namespace, FeedConfiguration config)
      throws FeedConfigStoreException {
    // Get the feed configuration properties to save to file
    Properties prop = extractFeedConfigProperties(config);

    // Get a file handle to feed file location
    File feedFile = getFeedConfigFilePath(namespace, config.getFeedId());

    // Check that the file does not exist
    if (!feedFile.exists()) {
      logger.log(Level.WARNING, "The feed configuration file does not exist");
      throw new FeedConfigStoreException(Reason.INTERNAL_ERROR,
          "The feed configuration file does not exist");
    }

    // Write the values to the file
    writeToFile(prop, feedFile, false);

  }


/**
   * Adds a adapter configuration with the given namespace. <br/>
   * It expects an instance of {@link NamespacedAdapterConfiguration} as
   * adapter configuration
   * 
   * @throws FeedConfigStoreException
   *             If
   *             <ul>
   *             <li>The adapter configuration is not an instance of
   *             {@link NamespacedAdapterConfiguration}</li>
   *             <li>An adapter configuration with the given namepsace already
   *             exists</li>
   *             <li>Any I/O exceptions are encountered while saving the
   *             configuration details to the file system</li>
   *             </ul>
   * 
   * @see com.google.feedserver.configstore.FeedConfigStore
   *      #addAdapterConfiguration(java.lang.String,
   *      org.apache.abdera.protocol.server
   *      .provider.managed.CollectionAdapterConfiguration)
   */
  @Override
  public void addAdapterConfiguration(String namespace, CollectionAdapterConfiguration config)
      throws FeedConfigStoreException {
    // Check for an instance that supports namespace
    if (!(config instanceof NamespacedAdapterConfiguration)) {
      logger.log(Level.SEVERE, "Expected an instance of : "
          + NamespacedAdapterConfiguration.class.getClass() + " but was : " + config.getClass());
      throw new FeedConfigStoreException(Reason.INVALID_ADAPTER_CONFIGURATION,
          "Please provide an instance of : " + NamespacedAdapterConfiguration.class);
    }

    // Create the namespace and adapter config directories if they are not
    // present
    createNamespaceAndAdapterConfigDir(namespace);

    // Type cast to the instance with namespace adapter configuration
    NamespacedAdapterConfiguration namespaceAdapterConfig = (NamespacedAdapterConfiguration) config;

    // Get the adapter config file
    File adapaterFile = getAdapterConfigFile(namespace, namespaceAdapterConfig.getAdapterName());

    // Check if the adapter configuration already exists
    if (adapaterFile.exists()) {
      logger.log(Level.WARNING, "Adapter configuration already exists!!");
      throw new FeedConfigStoreException(Reason.INVALID_ADAPTER_CONFIGURATION,
          "Adapter configuration already exists!!");
    }

    // Get the adapter config properties
    Properties prop = extractAdapterConfigProperties(namespaceAdapterConfig);

    // Write the contents to the file system
    writeToFile(prop, adapaterFile, true);

  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.feedserver.configstore.FeedConfigStore
   * #updateAdapterConfiguration(java.lang.String,
   * org.apache.abdera.protocol.server
   * .provider.managed.CollectionAdapterConfiguration)
   */
  @Override
  public void updateAdapterConfiguration(String namespace, CollectionAdapterConfiguration config)
      throws FeedConfigStoreException {
    // Get the feed configuration properties to save to file
    Properties prop = extractAdapterConfigProperties((NamespacedAdapterConfiguration) config);

    // Get a file handle to feed file location
    File adapterConfigFile =
        getFeedConfigFilePath(namespace, ((NamespacedAdapterConfiguration) config).getAdapterName());

    // Check that the file does not exist
    if (!adapterConfigFile.exists()) {
      logger.log(Level.WARNING, "The adapter configuration file does not exist");
      throw new FeedConfigStoreException(Reason.INTERNAL_ERROR,
          "The adapter configuration file does not exist");
    }

    // Write the values to the file
    writeToFile(prop, adapterConfigFile, false);

  }


  /*
   * (non-Javadoc)
   * 
   * @see com.google.feedserver.configstore.FeedConfigStore
   * #hasAdapterConfiguration(java.lang.String, java.lang.String)
   */
  @Override
  public boolean hasAdapterConfiguration(String namespace, String adapterName)
      throws FeedConfigStoreException {
    // Get the handle to the adapter config file
    File adapterFile = getAdapterConfigFile(namespace, adapterName);
    return adapterFile.exists();
  }


  /*
   * (non-Javadoc)
   * 
   * @see com.google.feedserver.configstore.FeedConfigStore
   * #allowAdapter(java.lang.String, java.lang.String)
   */
  @Override
  public void allowAdapter(String namespace, String adapterId) throws FeedConfigStoreException {
    throw new FeedConfigStoreException(Reason.INTERNAL_ERROR,
        "This functionality is not implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.google.feedserver.configstore.FeedConfigStore
   * #deleteAdapterConfiguration(java.lang.String, java.lang.String)
   */
  @Override
  public void deleteAdapterConfiguration(String namespace, String adapterId)
      throws FeedConfigStoreException {
    // Get the handle to the adapter file
    File adapterFile = getAdapterConfigFile(namespace, adapterId);

    // Check that the adapter file exists
    if (!adapterFile.exists()) {
      throw new FeedConfigStoreException(Reason.ADAPTER_CONFIG_DOES_NOT_EXIST,
          "No adapter configuration exists with the given adaptername : " + adapterId
              + "  for the given namespace : " + namespace);
    }

    // Delete the adapter configuration
    if (!adapterFile.delete()) {
      throw new FeedConfigStoreException(Reason.INTERNAL_ERROR,
          "Unable to delete the adapter configuration for adaptername : " + adapterId
              + "  for the given namespace : " + namespace);
    }

  }



  /*
   * (non-Javadoc)
   * 
   * @see com.google.feedserver.configstore.FeedConfigStore
   * #disallowAdapter(java.lang.String, java.lang.String)
   */
  @Override
  public void disallowAdapter(String namespace, String adapterId) throws FeedConfigStoreException {
    throw new FeedConfigStoreException(Reason.INTERNAL_ERROR,
        "This functionality is not implemented");
  }


  /**
   * Returns the feed configuration for the given feed under the given namespace
   * 
   * @param namespace
   * @param feedId
   * @return
   * @see com.google.feedserver.configstore.FeedConfigStore#getFeedConfiguration(String,
   *      String)
   */
  private File getFeedConfigFilePath(String namespace, String feedId) {
    // Construct the feed file path
    String pathToFeedFile =
        new StringBuilder(FEED_CONFIGURATION_PATH.replace(namespacePlaceHolder, namespace)).append(
            "/").append(feedId).append(FILE_EXTENSION).toString();
    // Get a file handle
    File feedFile = new File(pathToFeedFile);

    return feedFile;
  }

  /**
   * Returns the handle to the adapter config file
   * 
   * @param namespace The namespace
   * @param adapterName The adapter name used as filename
   * @return The handle to the adapter config file
   */
  private File getAdapterConfigFile(String namespace, String adapterName) {
    // Construct the feed file path
    String pathToAdapterConfigFile =
        new StringBuilder(ADAPTER_CONFIGURATION_PATH.replace(namespacePlaceHolder, namespace))
            .append("/").append(adapterName).append(FILE_EXTENSION).toString();
    // Get a file handle
    File adapterFile = new File(pathToAdapterConfigFile);

    return adapterFile;
  }

  /**
   * Reads the feed config properties from the given feed configuration and
   * returns them as name-value property pairs
   * 
   * @param config The feed configuration
   * @return Feed config properties as name-value property pairs
   */
  private Properties extractFeedConfigProperties(FeedConfiguration config) {
    Map<String, Object> feedProperties = ((NamespacedFeedConfiguration) config).getProprties();

    Properties prop = new Properties();

    for (String feedAttributeKey : feedProperties.keySet()) {
      prop.setProperty(feedAttributeKey, (String) feedProperties.get(feedAttributeKey));
    }

    return prop;
  }



  /**
   * Extracts properties of the adapter configuration and returns them as
   * property name-value pairs
   * 
   * @param namespaceAdapterConfig The adapater configuration
   * @return Name-value pairs of properties
   */
  private Properties extractAdapterConfigProperties(
      NamespacedAdapterConfiguration namespaceAdapterConfig) {
    Properties prop = new Properties();

    Map<String, Object> adapterConfigProperties = namespaceAdapterConfig.getProperties();

    for (String adapterConfigProperty : adapterConfigProperties.keySet()) {
      prop.setProperty(adapterConfigProperty, (String) adapterConfigProperties
          .get(adapterConfigProperty));
    }

    return prop;
  }

  /**
   * Writes the property values as contents to the given file
   * 
   * @param prop The name-value property values
   * @param file The file to which the contents are to be written
   * @param createNewFile True if file has to be created and false if it has to
   *        be updated
   * @throws FeedConfigStoreException If any exception is encountered while
   *         writing the contents to the file
   */
  private void writeToFile(Properties prop, File file, boolean createNewFile)
      throws FeedConfigStoreException {
    try {
      // Check if a new file needs to be created
      if (createNewFile) {
        if (!file.createNewFile()) {
          throw new FeedConfigStoreException(Reason.INTERNAL_ERROR, "Unable to create file : "
              + file.getAbsolutePath() + " to write the configuration details");
        }
      }

      // Create an output stream and write to the file
      FileOutputStream outputStream = new FileOutputStream(file);
      prop.store(outputStream, "");
    } catch (FileNotFoundException e) {
      throw new FeedConfigStoreException(Reason.INTERNAL_ERROR,
          "Problems encountered while locating the file : " + file.getAbsolutePath()
              + " to write the configuration details", e);
    } catch (IOException e) {
      throw new FeedConfigStoreException(Reason.INTERNAL_ERROR,
          "Problems encountered while writing the configuration details to the file : "
              + file.getAbsolutePath(), e);
    }
  }

  /**
   * Creates directories for namepsace and feed configuration if they do not
   * exist
   * 
   * @param namespace The namespace to be used
   * @throws FeedConfigStoreException If problems are encountered while creating
   *         the configuration directories
   */
  private void createNamespaceAndFeedConfigDir(String namespace) throws FeedConfigStoreException {
    // Create the namespace directory
    createNamespaceDirectory(namespace);

    File feedConfigDir =
        new File(
            new StringBuilder(FEED_CONFIGURATION_PATH.replace(namespacePlaceHolder, namespace))
                .toString());
    if (!feedConfigDir.exists()) {
      if (!feedConfigDir.mkdir()) {
        throw new FeedConfigStoreException(Reason.INTERNAL_ERROR,
            "Unable to create the feed configuration directory at the path : "
                + feedConfigDir.getAbsolutePath());
      }
    }
  }

  /**
   * Creates the namepsace directory
   * 
   * @param namespace The namespace directory
   * @throws FeedConfigStoreException The problems are encountered while
   *         creating the directory
   */
  private void createNamespaceDirectory(String namespace) throws FeedConfigStoreException {
    String pathTonamespaceDirectory =
        new StringBuilder(BASE_CONFIGURATION_PATH).append("/").append(namespace).toString();
    File namespaceDirectory = new File(pathTonamespaceDirectory);

    // Check if the namespace directory exists and create it if it does not
    // exist
    if (!namespaceDirectory.isDirectory()) {
      if (!namespaceDirectory.mkdir()) {
        throw new FeedConfigStoreException(Reason.INTERNAL_ERROR,
            "Unable to create namespace directory at the given path : "
                + namespaceDirectory.getAbsolutePath());
      }
    }
  }

  /**
   * Creates directories for namepsace and adapter configuration if they do not
   * exist
   * 
   * @param namespace The namespace to be used
   * @throws FeedConfigStoreException If problems are encountered while creating
   *         the configuration directories
   */
  private void createNamespaceAndAdapterConfigDir(String namespace) throws FeedConfigStoreException {
    // Create the namespace directory
    createNamespaceDirectory(namespace);

    // Check if the adapter configuration directory exists and
    // create it if it does not exist
    File adapterConfigDir =
        new File(new StringBuilder(ADAPTER_CONFIGURATION_PATH.replace(namespacePlaceHolder,
            namespace)).toString());
    if (!adapterConfigDir.exists()) {
      if (!adapterConfigDir.mkdir()) {
        throw new FeedConfigStoreException(Reason.INTERNAL_ERROR,
            "Unable to create the adapter configuration directory at the path : "
                + adapterConfigDir.getAbsolutePath());
      }
    }
  }



/**
	 * Returns the feed configuration with given feedId under the given
	 * namespace.
	 * <p>
	 * The adapter configuratipon is also fetched and set as part of feed
	 * configuration
	 * </p>
	 * 
	 * @param feedId
	 *            The feed-id
	 * @param feedConfigFilePath
	 *            The handle to the feed config file
	 * @return The feed configuration
	 * @throws FileNotFoundException
	 *             If feed config file is not found
	 * @throws IOException
	 *             If problems encountered while reading the config values
	 * @throws FeedConfigStoreException
	 *             Any feed store specific problems
	 */
  private FeedConfiguration getFeedConfiguration(String namespace, String feedId,
      File feedConfigFilePath) throws FileNotFoundException, IOException, FeedConfigStoreException {
    // Load the properties file
    Properties prop = new Properties();
    InputStream is = FileSystemConfigStoreUtil.getInputStream(feedConfigFilePath.getPath());
    prop.load(is);

    String subUri = prop.getProperty(FeedConfiguration.PROP_SUB_URI_NAME);
    String adapterName = prop.getProperty(FeedServerConfiguration.FEED_ADAPTER_NAME_KEY);

    Map<String, Object> propertiesMap = new HashMap<String, Object>();
    propertiesMap.put(FeedServerConfiguration.ID_KEY, feedId);
    propertiesMap.put(FeedServerConfiguration.FEED_ADAPTER_NAME_KEY, adapterName);
    propertiesMap.put(FeedConfiguration.PROP_AUTHOR_NAME, prop
        .getProperty(FeedConfiguration.PROP_AUTHOR_NAME));
    propertiesMap.put(FeedConfiguration.PROP_TITLE_NAME, prop
        .getProperty(FeedConfiguration.PROP_TITLE_NAME));
    propertiesMap.put(FeedServerConfiguration.CONFIG_VALUE_KEY, prop
        .get(FeedServerConfiguration.CONFIG_VALUE_KEY));
    propertiesMap.put(FeedConfiguration.PROP_SUB_URI_NAME, subUri);

    // Get the adapter configuration
    NamespacedAdapterConfiguration adapterConfig = getAdapterConfig(namespace, adapterName);

    NamespacedFeedConfiguration feedConfig =
        new NamespacedFeedConfiguration(propertiesMap, adapterConfig,
            getNamesapceServerConfiguration(namespace));

    return feedConfig;
  }

/**
	 * Returns the adapter configuration with given adapter name under the given
	 * namespace.
	 * <p>
	 * If the config value is a file indicated by '@' at the start of the config
	 * value, then, the contents of the file are read and set as the config
	 * value. <br/>
	 * It adds the reference to feed specific database query file name in the
	 * config value. <br/>
	 * </p>
	 * 
	 * @param namespace
	 *            The namespace of the adapter config
	 * @param adapterName
	 *            The adapter name
	 * @return The adapter configuration
	 * @throws FeedConfigStoreException
	 *             Any feed store specific problems
	 */
  private NamespacedAdapterConfiguration getAdapterConfig(String namespace, String adapterName)
      throws FeedConfigStoreException {
    try {
      // Set the adapter config properties
      Map<String, Object> adapterConfigProperties =
          getAdapterConfigProperties(namespace, adapterName);

      // Load the target adapter properties
      Map<String, Object> adapterProperties =
          getAdapter(namespace, (String) adapterConfigProperties
              .get(FeedServerConfiguration.ADAPTER_TYPE_KEY));

      // Set the adapter classname
      adapterConfigProperties.put(FeedServerConfiguration.ADAPTER_TYPE_KEY, adapterProperties
          .get(ADAPTER_CLASS_NAME));

      adapterProperties.remove(ADAPTER_CLASS_NAME);
      adapterProperties.remove(FeedServerConfiguration.ID_KEY);

      // Config values from adapter config will override the config values from
      // adapter
      if (containsKeyWithNonNullValue(adapterConfigProperties,
          FeedServerConfiguration.CONFIG_VALUE_KEY)) {
        adapterProperties.remove(FeedServerConfiguration.CONFIG_VALUE_KEY);
        adapterProperties.remove(FeedServerConfiguration.CONFIG_VALUE_TYPE_KEY);
      }
      adapterConfigProperties.putAll(adapterProperties);

      NamespacedAdapterConfiguration adapterConfiguration =
          new NamespacedAdapterConfiguration(adapterConfigProperties,
              getNamesapceServerConfiguration(namespace));
      return adapterConfiguration;
    } catch (FileNotFoundException e) {
      throw new FeedConfigStoreException(Reason.ADAPTER_CONFIG_DOES_NOT_EXIST,
          "No adapter configuration exists with the given name : " + adapterName);
    } catch (IOException e) {
      throw new FeedConfigStoreException(Reason.INTERNAL_ERROR,
          "Problems encountered while retrieving the adapter configuration with the given name : "
              + adapterName);
    }
  }

  /**
   * Retrieves and returns the adapter config properties stored under 
   * 'AdapterConfig' by the given adapterName
   * 
   * @param namespace The namespace under which the adapter config is stored
   * @param adapterName The adapter config name
   * @return The adapter config properties
   * @throws IOException If problems are encountered while reading and loading
   *         the adapter config properties
   * @throws FeedConfigStoreException
   */
  private Map<String, Object> getAdapterConfigProperties(String namespace, String adapterName)
      throws IOException, FeedConfigStoreException {
    // Load the adapter config
    File adapterConfigFile = getAdapterConfigFile(namespace, adapterName);

    Properties prop = new Properties();
    InputStream is = FileSystemConfigStoreUtil.getInputStreamForFile(adapterConfigFile);
    prop.load(is);

    // Set the adapter config properties
    Map<String, Object> adapterConfigProperties = new HashMap<String, Object>();
    adapterConfigProperties.put(FeedServerConfiguration.ID_KEY, adapterName);
    adapterConfigProperties.put(FeedServerConfiguration.ADAPTER_TYPE_KEY, prop
        .get(FeedServerConfiguration.ADAPTER_TYPE_KEY));
    setConfigValues(adapterConfigProperties, prop);
    // Setup the mixins
    if (prop.containsKey(FeedServerConfiguration.MIXINS)) {
      adapterConfigProperties.put(FeedServerConfiguration.MIXINS, getMixinConfigs(namespace, prop
          .getProperty(FeedServerConfiguration.MIXINS)));
    }
    return adapterConfigProperties;

  }

  /**
   * Checks if the specified property is in the map with a non-null value
   * 
   * @param adapterConfigProperties The containing map
   * @param key The key to be checked
   * @return True if the key is in the map with a non-null value and false
   *         otherwise
   */
  private boolean containsKeyWithNonNullValue(Map<String, Object> adapterConfigProperties,
      String key) {
    return adapterConfigProperties.containsKey(key) && null != adapterConfigProperties.get(key);
  }

  /**
   * Returns the adapter details by reading the adapter from the 'Adapter'
   * directory
   * 
   * @param namespace The namespace under which this adapter will reside
   * @param adapterName The name of the adapter. This should be the name of the
   *        file under 'Adapter' directory containing the details
   * @return The map containing all adapter properties
   * @throws FeedConfigStoreException
   */
  private Map<String, Object> getAdapter(String namespace, String adapterName)
      throws FeedConfigStoreException {
    Map<String, Object> adapterProperties = new HashMap<String, Object>();
    String filePath =
        new StringBuffer(ADAPTER_PATH.replace(namespacePlaceHolder, namespace)).append("/").append(
            adapterName).append(FILE_EXTENSION).toString();

    try {
      Properties prop = new Properties();
      prop.load(FileSystemConfigStoreUtil.getInputStream(filePath));

      // Set the adapter class name
      adapterProperties.put(ADAPTER_CLASS_NAME, prop.get(ADAPTER_CLASS_NAME));
      // Set the config values
      setConfigValues(adapterProperties, prop);

      if (ConfigStoreUtil.hasImplicitMixins(prop) && !ConfigStoreUtil.isWrapper(prop)) {
        MixinConfiguration[] implicitMixinConfigurations =
            getImplicitMixinConfigurationsForAdapter(namespace, prop);
        adapterProperties.put(FeedServerConfiguration.IMPLICIT_MIXINS, implicitMixinConfigurations);
      }

      return adapterProperties;
    } catch (IOException e) {
      logger.log(Level.WARNING, "Problems encountered while retrieving the adapter with name : "
          + adapterName, e);
      throw new FeedConfigStoreException(Reason.INTERNAL_ERROR,
          "Problems encountered while retrieving the adapter with name : " + adapterName);
    }
  }

  /**
   * Take the adapter properties and generate all the implicit mixin
   * configurations that you need for this adapter.
   * 
   * @param namespace The namespace the adapter belongs to.
   * @param properties adapter Properties
   * @throws FeedConfigStoreException
   * @throws IOException
   */
  private MixinConfiguration[] getImplicitMixinConfigurationsForAdapter(String namespace,
      Properties properties) throws FeedConfigStoreException, IOException {
    List<MixinConfiguration> implicitMixinConfigurations = new ArrayList<MixinConfiguration>();

    String implicitMixins = (String) properties.get(FeedServerConfiguration.IMPLICIT_MIXINS);
    String[] mixinFileNames = FileSystemConfigStoreUtil.getListOfFileNames(implicitMixins);

    for (String mixinFileName : mixinFileNames) {
      String mixinFilePath =
          new StringBuilder(ADAPTER_PATH).append("/").append(mixinFileName).append(FILE_EXTENSION)
              .toString().replace(namespacePlaceHolder, namespace);
      Properties mixinProperties = new Properties();
      mixinProperties.load(FileSystemConfigStoreUtil.getInputStream(mixinFilePath));

      MapMixinConfiguration mixinConfig = new MapMixinConfiguration();
      mixinConfig.setWrapperName(mixinProperties.getProperty(ADAPTER_CLASS_NAME));
      if (ConfigStoreUtil.containsKeyWithNonNullValue(mixinProperties,
          FeedServerConfiguration.CONFIG_VALUE_KEY)) {
        mixinConfig.setWrapperConfig(mixinProperties.get(FeedServerConfiguration.CONFIG_VALUE_KEY)
            .toString().trim());
      } else {
        mixinConfig.setWrapperConfig("");
      }
      implicitMixinConfigurations.add(mixinConfig);
    }

    MixinConfiguration[] implicitMixinConfigs =
        new MixinConfiguration[implicitMixinConfigurations.size()];
    return implicitMixinConfigurations.toArray(implicitMixinConfigs);

  }


  /**
   * Checks if the config properties exist and adds them to the given map. If
   * the config value is a file name (starting with
   * {@link FileSystemConfigStoreUtil.FILE_INDICATOR}), the contants of the file
   * will be read and set as {@link FeedServerConfiguration.CONFIG_VALUE_KEY}
   * 
   * @param configProperties The map of config properties
   * @param prop The properties to check
   * @throws IOException Thrown if problems are encountered while reading the
   *         file contents
   */
  private void setConfigValues(Map<String, Object> configProperties, Properties prop)
      throws IOException {
    if (prop.containsKey(FeedServerConfiguration.CONFIG_VALUE_TYPE_KEY)) {
      String configType = prop.getProperty(FeedServerConfiguration.CONFIG_VALUE_TYPE_KEY);
      String configValue = prop.getProperty(FeedServerConfiguration.CONFIG_VALUE_KEY);
      // If it is the path to a file, then read its content and set it as
      // configValue
      if (FileSystemConfigStoreUtil.checkIfStringIsFilePath(configValue)) {
        String[] configValues = FileSystemConfigStoreUtil.getListOfFileNames(configValue);
        StringBuilder configValueAsString = new StringBuilder();
        for (String configFileName : configValues) {
          configValueAsString.append(FileSystemConfigStoreUtil
              .getFileContents(BASE_CONFIGURATION_PATH + "/" + configFileName));
        }
        configProperties.put(FeedServerConfiguration.CONFIG_VALUE_KEY, configValueAsString
            .toString().trim());
      } else {
        configProperties.put(FeedServerConfiguration.CONFIG_VALUE_KEY, prop.getProperty(
            FeedServerConfiguration.CONFIG_VALUE_KEY).trim());
      }
      configProperties.put(FeedServerConfiguration.CONFIG_VALUE_TYPE_KEY, configType);
    }
  }

  /**
   * Reads the mixin configuration from the specified files, creates
   * {@link XmlMixinConfiguration} from the config
   * 
   * @param namespace The namespace for which this mixin config is defined
   * @param mixins The list of mixin config
   * @return An array of mixin configurations
   * @throws FeedConfigStoreException If problems are encountered while
   *         retrieiving the mixin configuration
   */
  private MixinConfiguration[] getMixinConfigs(String namespace, String mixins)
      throws FeedConfigStoreException {

    List<MixinConfiguration> mixinConfigs = new ArrayList<MixinConfiguration>();

    if (FileSystemConfigStoreUtil.checkIfStringIsFilePath(mixins)) {
      String[] mixinConfigNames = FileSystemConfigStoreUtil.getListOfFileNames(mixins);
      for (String mixinFileName : mixinConfigNames) {
        mixinFileName =
            new StringBuilder(ADAPTER_CONFIGURATION_PATH.replace(namespacePlaceHolder, namespace))
                .append("/" + mixinFileName).append(MIXIN_FILE_EXTENSION).toString();

        String mixinContent;
        try {
          mixinContent = FileSystemConfigStoreUtil.getFileContents(mixinFileName);
          XmlMixinConfiguration mixin = new XmlMixinConfiguration(mixinContent.trim());
          mixinConfigs.addAll(processMixinConfig(namespace, mixin));
        } catch (IOException e) {
          logger.log(Level.WARNING, "Problems encountered while loading the mixin configuration"
              + mixinFileName, e);
          throw new FeedConfigStoreException(Reason.INTERNAL_ERROR,
              "Problems encountered while loading the mixin configuration : " + mixinFileName);
        }
      }
    } else {
      XmlMixinConfiguration mixin = new XmlMixinConfiguration(mixins.trim());
      mixinConfigs.addAll(processMixinConfig(namespace, mixin));
    }

    MixinConfiguration[] mixinConfig = new MixinConfiguration[mixinConfigs.size()];
    return mixinConfigs.toArray(mixinConfig);
  }


/**
   * The method processes the mixin configuration to check if the given mixin
   * has defined any wrapper config
   * 
   * <pre>
   * If there is no wrapper config, it has to be a mixin with adapter config
   * stored separately and hence retrieves it to set the wrapper name to the
   * adapter config type with the config value as the wrapper config
   * </pre>
   * <pre>
   * If the wrapper config is set it implies that the wrapper adapter needs to
   * be retrieved and set as wrapper class
   * </pre>
   * 
   * 
   * @param namespace The namespace for the given mixin
   * @param mixin The mixin to be processed
   * @return mixinConfigs List of mixin configs
   * @throws FeedConfigStoreException If errors are encountered while retrieving
   *         the adapterConfig OR adapter details pointed by the mixin
   */
  private List<MixinConfiguration> processMixinConfig(String namespace, MixinConfiguration mixin)
      throws FeedConfigStoreException {
    List<MixinConfiguration> mixinConfigs = new ArrayList<MixinConfiguration>();
    MapMixinConfiguration mixinConfig = new MapMixinConfiguration();
    if (null == mixin.getWrapperConfig()) {
      // This has to be a mixin with config stored seperately.
      NamespacedAdapterConfiguration adapterConfiguration =
          (NamespacedAdapterConfiguration) getAdapterConfiguration(namespace, mixin
              .getWrapperName());
      mixinConfig.setWrapperConfig(adapterConfiguration.getConfigData());
      for (MixinConfiguration adapterMixinConfig : adapterConfiguration.getMixins()) {
        mixinConfigs.add(adapterMixinConfig);
      }
      mixinConfig.setWrapperName(adapterConfiguration.getAdapterType());
    } else {
      // Just load the adapter config properties and use the adapter class name
      // as mixin wrapper name
      Map<String, Object> adapterWrapper = getAdapter(namespace, mixin.getWrapperName());
      mixinConfig.setWrapperName((String) adapterWrapper.get(ADAPTER_CLASS_NAME));
      mixinConfig.setWrapperConfig(mixin.getWrapperConfig());
    }
    mixinConfigs.add(mixinConfig);
    return mixinConfigs;
  }


  /**
   * Return an instance of the namespace server configuration
   * 
   * @param namespace The namespace
   * @return The namespace configuration
   */
  private PerNamespaceServerConfiguration getNamesapceServerConfiguration(String namespace) {
    return new PerNamespaceServerConfiguration(FeedServerConfiguration.getIntance(), namespace);
  }


  /**
   * Checks if the namespace and feed config directories exist and creates them
   * if they do not exist
   * 
   * @param namespace The namepsace
   * @return The file handle to feed config directory
   * @throws FeedConfigStoreException Any feed store exception
   */
  private File checkNamespaceAndFeedConfigDir(String namespace) throws FeedConfigStoreException {

    // Check and ensure tht namespace directory exists
    File namespaceDirectory = checkNamespaceDirectory(namespace);

    String feedConfigPath =
        new StringBuilder(FEED_CONFIGURATION_PATH.replace(namespacePlaceHolder, namespace))
            .toString();

    File feedConfigDir = new File(feedConfigPath);

    // Check that the feed configuration directory exists
    if (!feedConfigDir.isDirectory()) {
      logger.log(Level.SEVERE,
          "There exists no feed configuration directory for the given namespace : " + namespace);
      throw new FeedConfigStoreException(Reason.INTERNAL_ERROR,
          "There is no feed configuration directory for the given namespace : " + namespace);
    }

    return feedConfigDir;
  }

  /**
   * Checks if the namepsace directory exists
   * 
   * @param namespace The namespace
   * @return Th File handle to the namepsace directory
   * @throws FeedConfigStoreException If problems are encountered while checking
   *         and and getting a handle to the namespace directory
   */
  private File checkNamespaceDirectory(String namespace) throws FeedConfigStoreException {
    String pathTonamespaceDirectory =
        new StringBuilder(BASE_CONFIGURATION_PATH).append("/").append(namespace).toString();
    File namespaceDirectory = new File(pathTonamespaceDirectory);

    // Check that namespace directory exists
    if (!namespaceDirectory.isDirectory()) {
      logger.log(Level.SEVERE, "There exists no directory for the given namespace : " + namespace);
      throw new FeedConfigStoreException(Reason.DOMAIN_NOT_FOUND, "Invalid namespace : "
          + namespace);
    }

    return namespaceDirectory;
  }

  /**
   * Checks if the namespace and adapter config directories exist and creates
   * them if they do not exist
   * 
   * @param namespace The namepsace
   * @return The file handle to adapter config directory
   * @throws FeedConfigStoreException Any feed store exception
   */
  private File checkNamespaceAndAdapterConfigDir(String namespace) throws FeedConfigStoreException {
    // Check and ensure tht namespace directory exists
    File namespaceDirectory = checkNamespaceDirectory(namespace);

    String adapterConfigPath =
        new StringBuilder(ADAPTER_CONFIGURATION_PATH.replace(namespacePlaceHolder, namespace))
            .toString();

    File adapterConfigDir = new File(adapterConfigPath);

    // Check that the feed configuration directory exists
    if (!adapterConfigDir.isDirectory()) {
      logger.log(Level.SEVERE,
          "There exists no adapter configuration directory for the given namespace : " + namespace);
      throw new FeedConfigStoreException(Reason.INTERNAL_ERROR,
          "There is no adapter configuration directory for the given namespace : " + namespace);
    }

    return adapterConfigDir;
  }



}
