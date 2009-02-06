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

/**
 * Defines a custom basic type. A basic type has no properties. When written as
 * XML it will have no sub elements.
 */
public abstract class BasicTypeInfo implements TypeInfo {

  private String name;

  /**
   * Constructs a new basic type info
   */
  public BasicTypeInfo(String name) {
    this.name = name;
  }

  /**
   * Get the name for this type.
   * 
   * @return public name of the type
   */
  public String getName() {
    return name;
  }

  /**
   * Get all the properties for a custom object.
   * 
   * @return A collection of child properties of this object or null if this
   *         type has no properties.
   */
  public Collection<PropertyInfo> getProperties() {
    return null;
  }

  /**
   * Get top level property for this feed.
   * 
   * @param name name of the property.
   * @return {@link PropertyInfo} for the specified property or null if the
   *         property does not exist.
   */
  public PropertyInfo getProperty(String name) {
    return null;
  }

  /**
   * Returns true if the feed info contains the specified property.
   * 
   * @param name name of the property.
   * @return <code>true</code> if the specified property is present,
   *         <code>false</code> otherwise.
   */
  public boolean hasProperty(String name) {
    return false;
  }
}
