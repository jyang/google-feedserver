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

import com.google.feedserver.util.FeedServerClientException;
import com.google.feedserver.util.FileUtil;
import com.google.gdata.client.GoogleService;

import java.io.File;
import java.net.URL;

/**
 * Command for uploading a gadget spec into user's PrivateGadgetSpec feed.
 *
 * Usage: fsct uploadUserGadget <gadgetSpecFilePath> <flags ...>
 */
public class UploadUserGadget extends GadgetCommand {

  public UploadUserGadget(GoogleService service, FileUtil fileUtil) {
    super(service, fileUtil);
  }

  @Override
  public void execute(String[] args) throws Exception {
    String gadgetSpecFilePath = checkNotFlag(args[1]);

    if (gadgetSpecFilePath == null) {
      throw new IllegalArgumentException("Missing first argument for gadget spec file path");
    }

    File gadgetSpecFile = new File(gadgetSpecFilePath);
    String gadgetName = gadgetSpecFile.getName();
    URL feedUrl = new URL(getUserFeedUrl(PRIVATE_GADGET_SPEC));
    
    GadgetSpecEntity entity = new GadgetSpecEntity(gadgetName);
    try {
      specClient.deleteEntity(feedUrl, entity);
    } catch (FeedServerClientException e) {
      // entity doesn't exist
    }
    
    try {
      entity.setSpecContent(fileUtil.readFileContents(gadgetSpecFilePath));
      specClient.insertEntity(feedUrl, entity);
      System.out.println("User gadget '" + gadgetName + "' uploaded successfully");
      System.out.println("URL: " + getUserFeedUrl(PRIVATE_GADGET_SPEC) + "/" + entity.getName());
    } catch (FeedServerClientException e) {
      System.err.println("Error: Failed to upload user gadget '" + gadgetName + "': " +
          e.getMessage());
    }
  }

  @Override
  public void usage(boolean inShell) {
    System.out.println(getFeedClientCommand(inShell) + getCommandName() + " <gadgetSpecFilePath>");
    System.out.println("    Uploads a gadget to user's private gadget store");
  }
}
