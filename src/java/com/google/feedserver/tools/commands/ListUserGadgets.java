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

import com.google.feedserver.util.FileUtil;
import com.google.gdata.client.GoogleService;

import java.net.URL;
import java.util.List;

/**
 * Command for listing all gadgets that belong to a user.
 *
 * Usage: fsct listUserGadgets <flags ...>
 */
public class ListUserGadgets extends GadgetCommand {

  public ListUserGadgets(GoogleService service, FileUtil fileUtil) {
    super(service, fileUtil);
  }

  @Override
  public void execute(String[] args) throws Exception {
    URL userGadgetFeedUrl = new URL(getUserFeedUrl(PRIVATE_GADGET_SPEC));
    List<GadgetSpecEntity> entities = specClient.getEntities(userGadgetFeedUrl);
    for (GadgetSpecEntity entity: entities) {
      System.out.println(getUserEntryUrl(PRIVATE_GADGET_SPEC, entity.getName()));
    }
  }

  @Override
  public void usage(boolean inShell) {
    System.out.println(getFeedClientCommand(inShell) + getCommandName());
    System.out.println("    Lists gadgets in user's private gadget store");
  }
}
