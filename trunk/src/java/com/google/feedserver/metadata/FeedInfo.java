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
 * It defines the properties of metadata type feed for a feed.
 * 
 * @author abhinavk@gmail.com (Abhinav Khandelwal)
 * 
 */
public interface FeedInfo {
  public static final String ENTITY_INFO = "entityInfo";

  /**
   * Get the entity description for this feed.
   * 
   * @return {@link EntityInfo} for this feed.
   */
  public EntityInfo getEntityInfo();

  /**
   * Get the location of this entity info in the feed. eg. "/content/entity" if
   * the entity info is loaded into content element of the feed and the xml has
   * "entity" as the top level element.
   * 
   * @return location of entity info in the feed.
   */
  public String getPath();
}
