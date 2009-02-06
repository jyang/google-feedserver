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

package com.google.feedserver.config;

/**
 * An interface to get configurations for mixins.
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public interface MixinConfiguration {
  public static final String WRAPPER_CONFIG = "wrapperConfig";
  public static final String ADAPTER_NAME = "adapterName";
  public static final String WRAPPER_CLASS_NAME = "wrapperName";

  public String getTargetAdapterName();

  public String getWrapperName();

  public String getWrapperConfig();

  public void setTargetAdapterName(String targetAdapterName);

  public void setWrapperName(String wrapperName);

  public void setWrapperConfig(String wrapperConfig);
}
