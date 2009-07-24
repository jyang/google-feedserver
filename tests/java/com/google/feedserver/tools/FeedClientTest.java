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
package com.google.feedserver.tools;

import junit.framework.TestCase;

public class FeedClientTest extends TestCase {

  public void testCommandLineArgumentLeadingAndTrailingSpaceHandling() {
    class TestFeedClient extends FeedClient {
      public TestFeedClient(String[] args) {
        super();
        initCommandLine(args);
      }
    }

    String[] args = new String[] {
        "-host", "111", "-userEmail", " 222", "-password", "333 ", "-serviceName", " 444 "};
    FeedClient client = new TestFeedClient(args);

    assertEquals("111", FeedClient.host_FLAG);
    assertEquals("222", FeedClient.userEmail_FLAG);
    assertEquals("333", FeedClient.password_FLAG);
    assertEquals("444", FeedClient.serviceName_FLAG);
  }
}
