/*
 * Copyright 2009 Google Inc.
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

import com.google.feedserver.util.FileUtil;
import com.google.gdata.client.GoogleService;

public abstract class FeedClientCommand {

  protected String domainName;
  protected GoogleService service;
  protected FileUtil fileUtil;

  public FeedClientCommand(GoogleService service, FileUtil fileUtil) {
    this.service = service;
    this.fileUtil = fileUtil;
  }

  abstract public void execute(String[] args) throws Exception;
  abstract public void usage();

  protected String getUserEmail() {
    return FeedClient.userEmail_FLAG;
  }

  protected String getDomainName() {
    String userEmail = getUserEmail();
    return userEmail.substring(userEmail.indexOf('@') + 1);
  }

  protected String getDomainFeedUrl(String feedName) {
    return FeedClient.host_FLAG + "/a/" + getDomainName() + "/g/" + feedName;
  }

  protected String getDomainEntryUrl(String feedName, String entryId) {
    return getDomainFeedUrl(feedName) + "/" + entryId;
  }

  protected String getUserFeedUrl(String feedName) {
    return FeedClient.host_FLAG + "/a/" + getDomainName() + "/user/" + getUserEmail() + "/g/" +
        feedName;
  }

  protected String getUserEntryUrl(String feedName, String entryId) {
    return getUserFeedUrl(feedName) + "/" + entryId;
  }

  protected String checkNotFlag(String arg) {
    if (arg != null && arg.startsWith("-")) {
      throw new IllegalArgumentException("Argument (not flag) expected in place of '" + arg +
          "'.  Please move all flags to after arugments");
    } else {
      return arg;
    }
  }

  /**
   * Returns class name with initial letter in lower case as command name.
   * @return Command name
   */
  protected String getCommandName() {
    String className = getClass().getSimpleName(); 
    return Character.toLowerCase(className.charAt(0)) + className.substring(1);
  }
}
