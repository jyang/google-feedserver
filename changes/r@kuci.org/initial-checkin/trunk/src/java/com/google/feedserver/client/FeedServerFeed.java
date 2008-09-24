/* Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.feedserver.client;

import com.google.gdata.data.BaseFeed;
import com.google.gdata.data.ExtensionProfile;

/**
 * Feed class representing a "payload-in-content" feed.
 * 
 * @author rayc@google.com (Ray Colline)
 */
public class FeedServerFeed extends BaseFeed<FeedServerFeed, FeedServerEntry> {

  /**
   * Creates the empty feed.
   */
  public FeedServerFeed() {
    super(FeedServerEntry.class);
  }
  
  @Override
  public void declareExtensions(ExtensionProfile extProfile) {
    // Declare arbitrary XML support for the feed instances, so any
    // extensions not explicitly declared in the profile will be captured.
    extProfile.declareArbitraryXmlExtension(BaseFeed.class);
    super.declareExtensions(extProfile);
  }
}
