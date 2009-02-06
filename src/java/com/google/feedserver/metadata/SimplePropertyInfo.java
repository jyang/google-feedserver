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
 * A simple implementation of {@link PropertyInfo}.
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public class SimplePropertyInfo implements PropertyInfo {
  private String label;
  private String name;
  private String typeName;
  private boolean repeatable;

  /**
   *
   */
  public SimplePropertyInfo(Map<String, Object> properties) {
    label = (String) properties.get(PropertyInfo.LABEL);
    name = (String) properties.get(PropertyInfo.NAME);
    typeName = (String) properties.get(PropertyInfo.TYPE);
    repeatable = Boolean.getBoolean((String) properties.get(PropertyInfo.REPEATABLE));
  }

  @Override
  public String getLabel() {
    return label;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getTypeName() {
    return typeName;
  }

  @Override
  public boolean isRepeatable() {
    return repeatable;
  }

  /**
   * @param label the label to set
   */
  public void setLabel(String label) {
    this.label = label;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @param repeatable the repeatable to set
   */
  public void setRepeatable(boolean repeatable) {
    this.repeatable = repeatable;
  }

  /**
   * @param typeName the typeName to set
   */
  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }
}
