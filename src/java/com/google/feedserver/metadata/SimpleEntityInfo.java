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
 * A basic implementation of {@link EntityInfo}. It takes a map as an input
 * which has two entries corresponding to array of {@link PropertyInfo} and
 * {@link TypeInfo} for this entity.
 * 
 * @author abhinavk@gmail.com (Abhinav Khandelwal)
 * 
 */
public class SimpleEntityInfo implements EntityInfo {
  private Map<String, PropertyInfo> properties;
  private Map<String, TypeInfo> types;

  public SimpleEntityInfo(Map<String, Object> entityInfo) {
    properties = new HashMap<String, PropertyInfo>();
    types = new HashMap<String, TypeInfo>();
    init(entityInfo);
  }

  protected void init(Map<String, Object> entityInfo) {
    TypeUtil typeUtil = new TypeUtil();
    Object props = entityInfo.get(PropertyInfo.PROPERITES);
    if (null != props) {
      typeUtil.createPropertyInfo(properties, props);
    }
    Object type = entityInfo.get(TypeInfo.TYPES);
    if (null != type) {
      typeUtil.createTypeInfo(types, type);
    }
  }

  @Override
  public Collection<PropertyInfo> getProperityInfo() {
    return properties.values();
  }

  @Override
  public Collection<TypeInfo> getTypeInfo() {
    return types.values();
  }

  @Override
  public PropertyInfo getProperty(String name) {
    return properties.get(name);
  }

  @Override
  public TypeInfo getType(String name) {
    return types.get(name);
  }

  @Override
  public boolean hasProperty(String name) {
    return properties.containsKey(name);
  }

  @Override
  public boolean hasType(String name) {
    return types.containsKey(name);
  }

}
