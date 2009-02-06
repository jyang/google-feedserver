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
 * Defines a custom type.
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public interface TypeInfo {
  public static final String NAME = "name";
  public static final String TYPES = "types";

  /**
   * Get the name for this types.
   * 
   * @return public name of the type
   */
  public String getName();

  /**
   * Get all the properties for a custom object.
   * 
   * @return A collection of child properties of this object.
   */
  public Collection<PropertyInfo> getProperties();

  /**
   * Get top level property for this feed.
   * 
   * @param name name of the property.
   * @return {@link PropertyInfo} for the specified property.
   */
  public PropertyInfo getProperty(String name);

  /**
   * Does the feed info contains the specified property.
   * 
   * @param name name of the property.
   * @return <code>true</code> if the specified property is present,
   *         <code>false</code> otherwise.
   */
  public boolean hasProperty(String name);
}
