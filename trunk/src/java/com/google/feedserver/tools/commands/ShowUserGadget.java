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

/**
 * Command for showing the spec of a user gadget.
 *
 * Usage: fsct showUserGadget <gadgetName> <flags ...>
 */
public class ShowUserGadget extends GadgetCommand {

  public ShowUserGadget(GoogleService service, FileUtil fileUtil) {
    super(service, fileUtil);
  }

  @Override
  public void execute(String[] args) throws Exception {
    String gadgetName = checkNotFlag(args[1]);

    if (gadgetName == null) {
      throw new IllegalArgumentException("Missing first argument for gadget name");
    }

    URL userGadgetEntryUrl = new URL(getUserEntryUrl(PRIVATE_GADGET_SPEC, gadgetName));
    GadgetSpecEntity entity = specClient.getEntity(userGadgetEntryUrl);
    System.out.println(entity.getSpecContent());
  }

  @Override
  public void usage(boolean inShell) {
    System.out.println(getFeedClientCommand(inShell) + getCommandName() + " <gadgetName>");
    System.out.println("    Shows the spec of a user gadget");
  }
}
