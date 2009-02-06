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

/**
 * Property info defines the property of a entry in feed.
 * 
 * @author abhinavk@gmail.com (Abhinav Khandelwal)
 * 
 */
public interface PropertyInfo {
  public static final String LABEL = "label";
  public static final String NAME = "name";
  public static final String REPEATABLE = "repeatable";
  public static final String TYPE = "typeName";
  public static final String PROPERITES = "properties";

  /**
   * Get the name of the property
   * 
   * @return name of the property
   */
  public String getName();

  /**
   * Get a descriptive label for this property.
   * 
   * @return descriptive label for this property.
   */
  public String getLabel();

  /**
   * Get the name of type of this property. The result of this function should
   * be same as getType().getName().
   * 
   * @return name of type of this property.
   */
  public String getTypeName();

  /**
   * Is this property repeatable
   * 
   * @return <code>true</code> if the property is repeatable, <code>false</code>
   *         o/w.
   */
  public boolean isRepeatable();
}
