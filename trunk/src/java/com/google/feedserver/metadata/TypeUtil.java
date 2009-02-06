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

import java.util.Map;

/**
 * Utility class for type metadata feed.
 * 
 * @author abhinavk@gmail.com (Abhinav Khandelwal)
 * 
 */
public class TypeUtil {

  /**
   * Converts an array of property maps into PropertyInfo objects.
   */
  @SuppressWarnings("unchecked")
  protected void createPropertyInfo(Map<String, PropertyInfo> properties, Object props) {
    if (!props.getClass().isArray()) {
      return;
    }
    Object[] propertyArray = (Object[]) props;
    for (Object property : propertyArray) {
      if (property instanceof Map) {
        Map<String, Object> propertyMap = (Map<String, Object>) property;
        PropertyInfo propertyInfo = new SimplePropertyInfo(propertyMap);
        properties.put(propertyInfo.getName(), propertyInfo);
      }
    }
  }

  /**
   * Converts an array of type maps into TypeInfo objects.
   */
  @SuppressWarnings("unchecked")
  public void createTypeInfo(Map<String, TypeInfo> types, Object type) {
    if (!type.getClass().isArray()) {
      return;
    }
    Object[] typeArray = (Object[]) type;
    for (Object typeObject : typeArray) {
      if (typeObject instanceof Map) {
        Map<String, Object> typeMap = (Map<String, Object>) typeObject;
        TypeInfo typeInfo = new SimpleTypeInfo(typeMap);
        types.put(typeInfo.getName(), typeInfo);
      }
    }
  }
}
