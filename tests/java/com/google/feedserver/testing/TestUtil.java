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

package com.google.feedserver.testing;

import com.google.feedserver.adapters.AbstractManagedCollectionAdapter;
import com.google.feedserver.config.FeedServerConfiguration;
import com.google.feedserver.config.MixinConfiguration;
import com.google.feedserver.config.NamespacedAdapterConfiguration;
import com.google.feedserver.config.NamespacedFeedConfiguration;
import com.google.feedserver.config.PerNamespaceServerConfiguration;
import com.google.feedserver.configstore.FeedConfigStore;
import com.google.feedserver.samples.config.MapMixinConfiguration;
import com.google.feedserver.samples.config.XmlMixinConfiguration;
import com.google.feedserver.samples.configstore.SampleFileSystemFeedConfigStore;
import com.google.feedserver.samples.manager.XmlWrapperManager;
import com.google.feedserver.wrappers.ManagedCollectionAdapterWrapper;

import org.apache.abdera.Abdera;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for feed server tests.
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public class TestUtil {
  public TestUtil() {
  }

  public static final String XML_PROLOG = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";

  public static final String TEST_TITLE = "test title";
  public static final String TEST_WRAPPER1_CONFIG = "asdfsadf";
  public static final String TEST_WRAPPER2_CONFIG = "12345345";
  public static final String TEST_WRAPPER3_CONFIG = "zxcvzxcv";
  public static final String WRAPPER_CLASS_NAME =
      ManagedCollectionAdapterWrapper.class.getSimpleName();
  public static final String WRAPPER_CLASS = ManagedCollectionAdapterWrapper.class.getName();
  public static final String DOMAIN_NAME = "testdomain.com";

  public static final String SAMPLE_ADAPTER_NAME = SampleBasicAdapter.class.getSimpleName();
  public static final String SAMPLE_ADAPTER_CLASS = SampleBasicAdapter.class.getName();

  public static final String SAMPLE_ADAPTER_WITH_IMPLICIT_MIXINS = "SampleBasicAdapterWithMixins";

  public static final String SAMPLE_ADAPTER_MIXIN_1 = "mixin1";
  public static final String SAMPLE_ADAPTER_MIXIN_2 = "mixin2";
  public static final String SAMPLE_ADAPTER_MIXIN_3 = "mixin3";

  public static final String ADAPTER_CONFIGURATION = "<a>Adapter Config</a>";
  public static final String XML = "XML";
  public static final String FEED_CONFIGURATION = "<a>Feed Config</a>";
  public static final String TEST_AUTHOR = "test author";

  public static final String TEST_ADAPTER_WITH_WRAPPER = "testAdapterWithWrapper";
  public static final String TEST_ADAPTER_WITH_NO_WRAPPER = "testAdapterWithNoWrapper";
  public static final String TEST_WRAPPER_ADAPTER_WITH_WRAPPER = "testWrapperAdapterWithWrapper";
  public static final String TEST_WRAPPER_ADAPTER_WITH_NO_WRAPPER =
      "testWrapperAdapterWithNoWrapper";

  public static final String TEST_FEED_WITH_ADAPTER_WITH_WRAPPERS =
      "testFeedWithAdapterWithWrappers";
  public static final String TEST_FEED_WITH_ADAPTER_WITH_NO_WRAPPERS =
      "testFeedWithAdaptersWithNoWrappers";
  public static final String TEST_FEED_WITH_WRAPPER_ADAPTER_WITH_WRAPPERS =
      "testFeedWithWrapperAdapterWithWrappers";
  public static final String TEST_FEED_WITH_WRAPPER_ADAPTER_WITH_NO_WRAPPERS =
      "testFeedWithWrapperAdaptersWithNoWrappers";
  /**
   * Property in Adapters feed which indicate the class location of the adapter.
   */
  public static final String ADAPTERS_CLASS_NAME = "className";

  /**
   * Property in Adapters feed which indicate if the given adapter is an
   * wrapper.
   */
  public static final String IS_WRAPPER = "isWrapper";

  public static final Map<String, Object> TEST_WRAPPER1 = new HashMap<String, Object>();


  public static final Map<String, Object> TEST_WRAPPER2 = new HashMap<String, Object>();


  public static final Map<String, Object> TEST_WRAPPER3 = new HashMap<String, Object>();

  public static final String WRAPPER_ADAPTER1_CONFIG =
      XML_PROLOG + "<" + XmlMixinConfiguration.MIXIN + ">\n" + "  <"
          + MixinConfiguration.ADAPTER_NAME + ">" + TEST_ADAPTER_WITH_NO_WRAPPER + "</"
          + MixinConfiguration.ADAPTER_NAME + ">\n" + "  <" + MixinConfiguration.WRAPPER_CONFIG
          + ">" + TEST_WRAPPER3_CONFIG + "</" + MixinConfiguration.WRAPPER_CONFIG + ">\n" + "</"
          + XmlMixinConfiguration.MIXIN + ">";

  public static final String WRAPPER_ADAPTER2_CONFIG =
      XML_PROLOG + "<" + XmlMixinConfiguration.MIXIN + ">\n" + "  <"
          + MixinConfiguration.ADAPTER_NAME + ">" + TEST_ADAPTER_WITH_WRAPPER + "</"
          + MixinConfiguration.ADAPTER_NAME + ">\n" + "  <" + MixinConfiguration.WRAPPER_CONFIG
          + ">" + TEST_WRAPPER3_CONFIG + "</" + MixinConfiguration.WRAPPER_CONFIG + ">\n" + "</"
          + XmlMixinConfiguration.MIXIN + ">";

  @SuppressWarnings("unchecked")
  public static final Map[] TEST_WRAPPERS = new Map[3];

  public static final String[] TEST_IMPLICIT_MIXINS =
      new String[] {SAMPLE_ADAPTER_MIXIN_1, SAMPLE_ADAPTER_MIXIN_2, SAMPLE_ADAPTER_MIXIN_3};

  public static Map<String, Object> adapterWithNoImplicitMixins = new HashMap<String, Object>();

  public static Map<String, Object> mixin1 = new HashMap<String, Object>();

  public static Map<String, Object> mixin2 = new HashMap<String, Object>();

  public static Map<String, Object> mixin3 = new HashMap<String, Object>();


  public static Map<String, Object> wrapperEntry = new HashMap<String, Object>();

  public static Map<String, Object> adapterWith3ImplicitMixins = new HashMap<String, Object>();

  public static Map<String, Object> adapterConfigWithImplicitMixins = new HashMap<String, Object>();

  public static Map<String, Object> adapterConfigWithNoWrappersMap = new HashMap<String, Object>();


  public static Map<String, Object> adapterConfigWithWrappersEntry = new HashMap<String, Object>();

  public static Map<String, Object> wrapperConfigWithAdapterWithNoWrappersMap =
      new HashMap<String, Object>();

  public static Map<String, Object> wrapperConfigWithAdapterWithWrappersMap =
      new HashMap<String, Object>();

  public static Map<String, Object> baseFeedConfigMap = new HashMap<String, Object>();

  public static Map<String, Object> feedConfigWithAdapterWithWrapperMap = null;

  public static Map<String, Object> feedConfigWithAdapterWithNoWrappersMap = null;

  public static Map<String, Object> feedConfigWithWrapperAdapterWithWrapperMap = null;

  public static Map<String, Object> feedConfigWithWrapperAdapterWithNoWrappersMap = null;


  static {
    TEST_WRAPPER1.put(MixinConfiguration.WRAPPER_CLASS_NAME, WRAPPER_CLASS_NAME);
    TEST_WRAPPER1.put(MixinConfiguration.WRAPPER_CONFIG, TEST_WRAPPER1_CONFIG);

    TEST_WRAPPER2.put(MixinConfiguration.WRAPPER_CLASS_NAME, WRAPPER_CLASS_NAME);
    TEST_WRAPPER2.put(MixinConfiguration.WRAPPER_CONFIG, TEST_WRAPPER2_CONFIG);

    TEST_WRAPPER3.put(MixinConfiguration.WRAPPER_CLASS_NAME, TestUtil.WRAPPER_CLASS_NAME);
    TEST_WRAPPER3.put(MixinConfiguration.WRAPPER_CONFIG, TestUtil.TEST_WRAPPER3_CONFIG);

    TEST_WRAPPERS[0] = TEST_WRAPPER1;
    TEST_WRAPPERS[1] = TEST_WRAPPER2;
    TEST_WRAPPERS[2] = TEST_WRAPPER3;

    adapterWithNoImplicitMixins.put(FeedServerConfiguration.ID_KEY, SAMPLE_ADAPTER_NAME);
    adapterWithNoImplicitMixins.put(ADAPTERS_CLASS_NAME, SAMPLE_ADAPTER_CLASS);
    adapterWithNoImplicitMixins.put(IS_WRAPPER, Boolean.FALSE);

    mixin1.put(FeedServerConfiguration.ID_KEY, SAMPLE_ADAPTER_MIXIN_1);
    mixin1.put(ADAPTERS_CLASS_NAME, WRAPPER_CLASS);
    mixin1.put(IS_WRAPPER, Boolean.TRUE);
    mixin1.put(FeedServerConfiguration.CONFIG_VALUE_KEY, TEST_WRAPPER1_CONFIG);

    mixin2.put(FeedServerConfiguration.ID_KEY, SAMPLE_ADAPTER_MIXIN_2);
    mixin2.put(ADAPTERS_CLASS_NAME, WRAPPER_CLASS);
    mixin2.put(IS_WRAPPER, Boolean.TRUE);
    mixin2.put(FeedServerConfiguration.CONFIG_VALUE_KEY, TEST_WRAPPER2_CONFIG);

    mixin3.put(FeedServerConfiguration.ID_KEY, SAMPLE_ADAPTER_MIXIN_3);
    mixin3.put(ADAPTERS_CLASS_NAME, WRAPPER_CLASS);
    mixin3.put(IS_WRAPPER, Boolean.TRUE);
    mixin3.put(FeedServerConfiguration.CONFIG_VALUE_KEY, TEST_WRAPPER3_CONFIG);

    wrapperEntry.put(FeedServerConfiguration.ID_KEY, WRAPPER_CLASS_NAME);
    wrapperEntry.put(ADAPTERS_CLASS_NAME, WRAPPER_CLASS);
    wrapperEntry.put(IS_WRAPPER, Boolean.TRUE);

    adapterConfigWithImplicitMixins.put(FeedServerConfiguration.ID_KEY,
        SAMPLE_ADAPTER_WITH_IMPLICIT_MIXINS);
    adapterConfigWithImplicitMixins.put(FeedServerConfiguration.ADAPTER_TYPE_KEY,
        SAMPLE_ADAPTER_WITH_IMPLICIT_MIXINS);
    adapterConfigWithImplicitMixins.put(FeedServerConfiguration.CONFIG_VALUE_KEY,
        ADAPTER_CONFIGURATION);
    adapterConfigWithImplicitMixins.put(FeedServerConfiguration.CONFIG_VALUE_TYPE_KEY, XML);

    adapterWith3ImplicitMixins.put(FeedServerConfiguration.ID_KEY,
        SAMPLE_ADAPTER_WITH_IMPLICIT_MIXINS);
    adapterWith3ImplicitMixins.put(ADAPTERS_CLASS_NAME, SAMPLE_ADAPTER_CLASS);
    adapterWith3ImplicitMixins.put(IS_WRAPPER, Boolean.FALSE);
    adapterWith3ImplicitMixins.put(FeedServerConfiguration.IMPLICIT_MIXINS, TEST_IMPLICIT_MIXINS);

    adapterConfigWithNoWrappersMap
        .put(FeedServerConfiguration.ID_KEY, TEST_ADAPTER_WITH_NO_WRAPPER);
    adapterConfigWithNoWrappersMap.put(FeedServerConfiguration.ADAPTER_TYPE_KEY,
        SAMPLE_ADAPTER_NAME);
    adapterConfigWithNoWrappersMap.put(FeedServerConfiguration.CONFIG_VALUE_KEY,
        ADAPTER_CONFIGURATION);
    adapterConfigWithNoWrappersMap.put(FeedServerConfiguration.CONFIG_VALUE_TYPE_KEY, XML);

    adapterConfigWithWrappersEntry.put(FeedServerConfiguration.ID_KEY, TEST_ADAPTER_WITH_WRAPPER);
    adapterConfigWithWrappersEntry.put(FeedServerConfiguration.ADAPTER_TYPE_KEY,
        SAMPLE_ADAPTER_NAME);
    adapterConfigWithWrappersEntry.put(FeedServerConfiguration.CONFIG_VALUE_KEY,
        ADAPTER_CONFIGURATION);
    adapterConfigWithWrappersEntry.put(FeedServerConfiguration.CONFIG_VALUE_TYPE_KEY, XML);
    adapterConfigWithWrappersEntry.put(FeedServerConfiguration.MIXINS, TEST_WRAPPERS);

    wrapperConfigWithAdapterWithNoWrappersMap.put(FeedServerConfiguration.ID_KEY,
        TEST_WRAPPER_ADAPTER_WITH_NO_WRAPPER);
    wrapperConfigWithAdapterWithNoWrappersMap.put(FeedServerConfiguration.ADAPTER_TYPE_KEY,
        WRAPPER_CLASS_NAME);
    wrapperConfigWithAdapterWithNoWrappersMap.put(FeedServerConfiguration.CONFIG_VALUE_KEY,
        WRAPPER_ADAPTER1_CONFIG);
    wrapperConfigWithAdapterWithNoWrappersMap.put(FeedServerConfiguration.CONFIG_VALUE_TYPE_KEY,
        XML);

    wrapperConfigWithAdapterWithWrappersMap.put(FeedServerConfiguration.ID_KEY,
        TEST_WRAPPER_ADAPTER_WITH_WRAPPER);
    wrapperConfigWithAdapterWithWrappersMap.put(FeedServerConfiguration.ADAPTER_TYPE_KEY,
        WRAPPER_CLASS_NAME);
    wrapperConfigWithAdapterWithWrappersMap.put(FeedServerConfiguration.CONFIG_VALUE_KEY,
        WRAPPER_ADAPTER2_CONFIG);
    wrapperConfigWithAdapterWithWrappersMap.put(FeedServerConfiguration.CONFIG_VALUE_TYPE_KEY, XML);

    baseFeedConfigMap.put(NamespacedFeedConfiguration.PROP_TITLE_NAME, TEST_TITLE);
    baseFeedConfigMap.put(NamespacedFeedConfiguration.PROP_AUTHOR_NAME, TEST_AUTHOR);
    baseFeedConfigMap.put(FeedServerConfiguration.CONFIG_VALUE_KEY, FEED_CONFIGURATION);
    baseFeedConfigMap.put(FeedServerConfiguration.CONFIG_VALUE_TYPE_KEY, XML);

    feedConfigWithAdapterWithWrapperMap = new HashMap<String, Object>(baseFeedConfigMap);
    feedConfigWithAdapterWithNoWrappersMap = new HashMap<String, Object>(baseFeedConfigMap);

    feedConfigWithWrapperAdapterWithWrapperMap = new HashMap<String, Object>(baseFeedConfigMap);

    feedConfigWithWrapperAdapterWithNoWrappersMap = new HashMap<String, Object>(baseFeedConfigMap);

    feedConfigWithAdapterWithWrapperMap.put(FeedServerConfiguration.ID_KEY,
        TEST_FEED_WITH_ADAPTER_WITH_WRAPPERS);
    feedConfigWithAdapterWithWrapperMap.put(FeedServerConfiguration.FEED_ADAPTER_NAME_KEY,
        TEST_ADAPTER_WITH_WRAPPER);

    feedConfigWithAdapterWithNoWrappersMap.put(FeedServerConfiguration.ID_KEY,
        TEST_FEED_WITH_ADAPTER_WITH_NO_WRAPPERS);
    feedConfigWithAdapterWithNoWrappersMap.put(FeedServerConfiguration.FEED_ADAPTER_NAME_KEY,
        TEST_ADAPTER_WITH_NO_WRAPPER);

    feedConfigWithWrapperAdapterWithWrapperMap.put(FeedServerConfiguration.ID_KEY,
        TEST_FEED_WITH_WRAPPER_ADAPTER_WITH_WRAPPERS);
    feedConfigWithWrapperAdapterWithWrapperMap.put(FeedServerConfiguration.FEED_ADAPTER_NAME_KEY,
        TEST_WRAPPER_ADAPTER_WITH_WRAPPER);

    feedConfigWithWrapperAdapterWithNoWrappersMap.put(FeedServerConfiguration.ID_KEY,
        TEST_FEED_WITH_WRAPPER_ADAPTER_WITH_NO_WRAPPERS);
    feedConfigWithWrapperAdapterWithNoWrappersMap.put(
        FeedServerConfiguration.FEED_ADAPTER_NAME_KEY, TEST_WRAPPER_ADAPTER_WITH_NO_WRAPPER);

  }

  private Abdera abdera;
  private FeedServerConfiguration globalServerConfig;
  private PerNamespaceServerConfiguration serverConfig;
  private NamespacedAdapterConfiguration adapterConfigWithNoWrappers;
  private NamespacedAdapterConfiguration adapterConfigWithWrappers;
  private NamespacedAdapterConfiguration wrapperAdapterConfigWithNoWrappers;
  private NamespacedAdapterConfiguration wrapperAdapterConfigWithWrappers;
  private NamespacedFeedConfiguration feedWithAdaptersWithNoWrappers;
  private NamespacedFeedConfiguration feedWithAdaptersWithWrappers;
  private NamespacedFeedConfiguration feedWithWrapperAdaptersWithNoWrappers;
  private NamespacedFeedConfiguration feedWithWrapperAdaptersWithWrappers;
  private SampleBasicAdapter basicAdapterWithFeedConfigWithNoWrappers;
  private SampleBasicAdapter basicAdapterWithFeedConfigWithWrappers;
  private AbstractManagedCollectionAdapter basicWrapperAdapterWithFeedConfigWithNoWrappers;
  private AbstractManagedCollectionAdapter basicWrapperAdapterWithFeedConfigWithWrappers;
  private SampleFileSystemFeedConfigStore fileSystemFeedConfigStore;

  public void setup() throws Exception {
    setup(true);
  }

  public void setup(boolean addDataToFeedStore) throws Exception {
    abdera = new Abdera();
    fileSystemFeedConfigStore = new SampleFileSystemFeedConfigStore();
    globalServerConfig = FeedServerConfiguration.createIntance(fileSystemFeedConfigStore);
    globalServerConfig.setWrapperManagerClassName(XmlWrapperManager.class.getName());
    serverConfig = new PerNamespaceServerConfiguration(globalServerConfig, DOMAIN_NAME);
    adapterConfigWithNoWrappers =
        new NamespacedAdapterConfiguration(adapterConfigWithNoWrappersMap, serverConfig);

    MixinConfiguration mc1 = new MapMixinConfiguration(TEST_WRAPPER1);
    mc1.setWrapperName(WRAPPER_CLASS);
    MixinConfiguration mc2 = new MapMixinConfiguration(TEST_WRAPPER2);
    mc2.setWrapperName(WRAPPER_CLASS);
    MixinConfiguration mc3 = new MapMixinConfiguration(TEST_WRAPPER3);
    mc3.setWrapperName(WRAPPER_CLASS);
    MixinConfiguration[] mixinConfigs = new MixinConfiguration[] {mc1, mc2, mc3};

    Map<String, Object> tmp = new HashMap<String, Object>(adapterConfigWithWrappersEntry);
    tmp.put(FeedServerConfiguration.MIXINS, mixinConfigs);
    adapterConfigWithWrappers = new NamespacedAdapterConfiguration(tmp, serverConfig);

    wrapperAdapterConfigWithNoWrappers =
        new NamespacedAdapterConfiguration(wrapperConfigWithAdapterWithNoWrappersMap, serverConfig);
    wrapperAdapterConfigWithWrappers =
        new NamespacedAdapterConfiguration(wrapperConfigWithAdapterWithWrappersMap, serverConfig);
    feedWithAdaptersWithNoWrappers =
        new NamespacedFeedConfiguration(feedConfigWithAdapterWithNoWrappersMap,
            adapterConfigWithNoWrappers, serverConfig);
    feedWithAdaptersWithWrappers =
        new NamespacedFeedConfiguration(feedConfigWithAdapterWithWrapperMap,
            adapterConfigWithWrappers, serverConfig);
    feedWithWrapperAdaptersWithNoWrappers =
        new NamespacedFeedConfiguration(feedConfigWithWrapperAdapterWithNoWrappersMap,
            wrapperAdapterConfigWithNoWrappers, serverConfig);
    feedWithWrapperAdaptersWithWrappers =
        new NamespacedFeedConfiguration(feedConfigWithWrapperAdapterWithWrapperMap,
            wrapperAdapterConfigWithWrappers, serverConfig);
    basicAdapterWithFeedConfigWithNoWrappers =
        new SampleBasicAdapter(getAbdera(), feedWithAdaptersWithNoWrappers);
    basicAdapterWithFeedConfigWithWrappers =
        new SampleBasicAdapter(getAbdera(), feedWithAdaptersWithWrappers);
    basicWrapperAdapterWithFeedConfigWithNoWrappers =
        new ManagedCollectionAdapterWrapper(getAbdera(), feedWithWrapperAdaptersWithNoWrappers);
    basicWrapperAdapterWithFeedConfigWithWrappers =
        new ManagedCollectionAdapterWrapper(getAbdera(), feedWithWrapperAdaptersWithWrappers);

  }



  public void tearDown() {
  }

  public Abdera getAbdera() {
    return abdera;
  }

  public NamespacedAdapterConfiguration getAdapterWithNoWrappers() {
    return adapterConfigWithNoWrappers;
  }

  public NamespacedAdapterConfiguration getAdapterWithWrappers() {
    return adapterConfigWithWrappers;
  }

  public FeedConfigStore getFeedConfigStore() {
    return fileSystemFeedConfigStore;
  }



  public NamespacedFeedConfiguration getFeedWithAdaptersWithNoWrappers() {
    return feedWithAdaptersWithNoWrappers;
  }

  public NamespacedFeedConfiguration getFeedWithAdaptersWithWrappers() {
    return feedWithAdaptersWithWrappers;
  }

  public FeedServerConfiguration getGlobalServerConfig() {
    return globalServerConfig;
  }

  public PerNamespaceServerConfiguration getServerConfig() {
    return serverConfig;
  }

  public SampleBasicAdapter getBasicAdapterWithFeedConfigWithNoWrappers() {
    return basicAdapterWithFeedConfigWithNoWrappers;
  }

  public SampleBasicAdapter getBasicAdapterWithFeedConfigWithWrappers() {
    return basicAdapterWithFeedConfigWithWrappers;
  }

  public AbstractManagedCollectionAdapter getBasicWrapperAdapterWithFeedConfigWithNoWrappers() {
    return basicWrapperAdapterWithFeedConfigWithNoWrappers;
  }

  public AbstractManagedCollectionAdapter getBasicWrapperAdapterWithFeedConfigWithWrappers() {
    return basicWrapperAdapterWithFeedConfigWithWrappers;
  }
}
