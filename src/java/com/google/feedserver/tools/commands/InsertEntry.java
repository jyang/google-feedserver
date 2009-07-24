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
import com.google.feedserver.util.XmlUtil;
import com.google.gdata.client.GoogleService;

import java.net.URL;
import java.util.Map;

/**
 * Command for inserting an entity into a feed.
 *
 * Usage: fsct insertEntry <feedUrl> <entityFilePath> <flags ...>
 *   feedUrl: relative to host
 */
public class InsertEntry extends TypelessCommand {

  public InsertEntry(GoogleService service, FileUtil fileUtil) {
    super(service, fileUtil);
  }

  @Override
  public void execute(String[] args) throws Exception {
    String relativeFeedUrl = checkNotFlag(args[1]);
    if (relativeFeedUrl == null) {
      throw new IllegalArgumentException("Missing first argument for relative feed URL");
    }

    String entitFilePath = checkNotFlag(args[2]);
    if (entitFilePath == null) {
      throw new IllegalArgumentException("Missing second argument for entity file path");
    }

    String entityXml = new FileUtil().readFileContents(entitFilePath);
    Map<String, Object> entity = new XmlUtil().convertXmlToProperties(entityXml);
    String feedUrl = FeedClient.host_FLAG + relativeFeedUrl;
    Map<String, Object> insertedEntity =
        typelessClient.insertEntry(new URL(feedUrl), entity);
    System.out.println(insertedEntity);
  }

  @Override
  public void usage(boolean inShell) {
    System.out.println(getFeedClientCommand(inShell) + getCommandName() +
        " <relativeFeedUrl> <entityFilePath> <flags ...>");
    System.out.println("    Inserts an enttry into a feed");
  }
}
