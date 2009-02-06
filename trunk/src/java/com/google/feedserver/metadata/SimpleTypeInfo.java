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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public class SimpleTypeInfo implements TypeInfo {

  private String name;
  private Map<String, PropertyInfo> properties;

  public SimpleTypeInfo(Map<String, Object> typeInfo) {
    properties = new HashMap<String, PropertyInfo>();
    init(typeInfo);
  }

  protected void init(Map<String, Object> typeInfo) {
    name = (String) typeInfo.get(TypeInfo.NAME);
    Object props = typeInfo.get(PropertyInfo.PROPERITES);
    if (null != props) {
      new TypeUtil().createPropertyInfo(properties, props);
    }
  }

  @Override
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public Collection<PropertyInfo> getProperties() {
    return properties.values();
  }

  public void addProperty(PropertyInfo property) {
    properties.put(property.getName(), property);
  }

  @Override
  public PropertyInfo getProperty(String key) {
    return properties.get(key);
  }

  @Override
  public boolean hasProperty(String key) {
    return properties.containsKey(key);
  }
}
