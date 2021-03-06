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

import java.util.HashMap;
import java.util.Map;

/**
 * Sample map based implementation for {@link MixinConfiguration}
 * 
 * @author abhinavk@gmail.com (Abhinav Khandelwal)
 */
public class MapMixinConfiguration implements MixinConfiguration {

  private final Map<String, Object> map;

  public MapMixinConfiguration() {
    this(new HashMap<String, Object>(3));
  }

  public MapMixinConfiguration(Map<String, Object> map) {
    this.map = map;
  }

  @Override
  public String getTargetAdapterName() {
    return returnStringValue(ADAPTER_NAME);
  }

  @Override
  public String getWrapperConfig() {
    return returnStringValue(WRAPPER_CONFIG);
  }

  @Override
  public String getWrapperName() {
    return returnStringValue(WRAPPER_CLASS_NAME);
  }

  @Override
  public void setTargetAdapterName(String targetAdapterName) {
    map.put(ADAPTER_NAME, targetAdapterName);
  }

  @Override
  public void setWrapperConfig(String wrapperConfig) {
    map.put(WRAPPER_CONFIG, wrapperConfig);
  }

  @Override
  public void setWrapperName(String wrapperName) {
    map.put(WRAPPER_CLASS_NAME, wrapperName);
  }

  private String returnStringValue(String id) {
    if (null != map.get(id)) {
      return map.get(id).toString();
    } else {
      return null;
    }
  }
}
