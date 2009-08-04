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

package com.google.feedserver.tools.commands;

import com.google.feedserver.tools.FeedClient;
import com.google.feedserver.util.FileUtil;
import com.google.gdata.client.GoogleService;

import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Command for getting a domain feed.
 *
 * Usage: fsct getFeed <feedUrlPath> <flags ...>
 *   feedUrlPath: part of feed URL after host
 */
public class GetFeed extends TypelessCommand {

  public GetFeed(GoogleService service, FileUtil fileUtil) {
    super(service, fileUtil);
  }

  @Override
  public void execute(String[] args) throws Exception {
    String feedUrlPath = checkNotFlag(args[1]);

    if (feedUrlPath == null) {
      throw new IllegalArgumentException("Missing first argument for relative feed URL");
    }

    String feedUrl = FeedClient.host_FLAG + feedUrlPath;
    List<Map<String, Object>> entities = typelessClient.getEntries(new URL(feedUrl));
    System.out.println(entities);
  }

  @Override
  public void usage(boolean inShell) {
    System.out.println(getFeedClientCommand(inShell) + getCommandName() +
        " <feedUrlPath> <flags ...>");
    System.out.println("    Gets a domain feed. <feedUrlPath> is the part of feed " +
        "URL after host. For example, if feed URL is " +
        "'http://feedserver-enterprise.googleusercontent.com/a/example.com/g/PrivateGadgetSpec', " +
        "<feedUrlPath> is '/a/example.com/g/PrivateGadgetSpec'");
  }
}
