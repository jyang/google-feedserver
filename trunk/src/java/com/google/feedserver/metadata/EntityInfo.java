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
 * EntityInfo defines the property and their types for this feed.
 * 
 * @author abhinavk@google.com (Abhinav Khandelwal)
 * 
 */
public interface EntityInfo {

  /**
   * Get all the properties of this entity.
   * 
   * @return Colection of top level properties for this entity.
   */
  public Collection<PropertyInfo> getProperityInfo();

  /**
   * Get all the custom types for this property.
   * 
   * @return custom types for this property.
   */
  public Collection<TypeInfo> getTypeInfo();

  /**
   * Get top level property for this feed.
   * 
   * @param name name of the property.
   * @return {@link PropertyInfo} for the specified property.
   */
  public PropertyInfo getProperty(String name);

  /**
   * Get custom type for this feed.
   * 
   * @param name name of the type.
   * @return {@link TypeInfo} for the specified type.
   */
  public TypeInfo getType(String name);

  /**
   * Does the feed info contains the specified property.
   * 
   * @param name name of the property.
   * @return <code>true</code> if the specified property is present,
   *         <code>false</code> otherwise.
   */
  public boolean hasProperty(String name);

  /**
   * Does the feed info contains the specified custom.
   * 
   * @param name name of the type.
   * @return <code>true</code> if the specified type is present,
   *         <code>false</code> otherwise.
   */
  public boolean hasType(String name);
}
