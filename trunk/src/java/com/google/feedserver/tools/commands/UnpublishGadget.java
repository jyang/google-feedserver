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
import java.net.URLEncoder;
import java.util.List;

/**
 * Command for unpublishing a gadget.  Gadget is removed from domain's private gadget directory
 * and domain's private gadget store.
 *
 * Usage: fsct unpublishGadget <gadgetName> <flags ...>
 * Usage: fsct unpublishGadget <gadgetSpecUrl> <flags ...>
 */
public class UnpublishGadget extends GadgetCommand {

  public UnpublishGadget(GoogleService service, FileUtil fileUtil) {
    super(service, fileUtil);
  }

  @Override
  public void execute(String[] args) throws Exception {
    String gadgetName = checkNotFlag(args[1]);

    if (gadgetName == null) {
      throw new IllegalArgumentException("Missing first argument for gadget name");
    }

    String domainGadgetEntryUrl = gadgetName.startsWith("http") ? gadgetName :
        getCanonicalGadgetSpecUrl(gadgetName);

    URL dirQueryUrl = new URL(getDomainFeedUrl(PRIVATE_GADGET) +
        "?url=" + URLEncoder.encode(domainGadgetEntryUrl, "UTF-8"));
    List<GadgetDirEntity> dirEntities = dirClient.getEntities(dirQueryUrl);

    if (dirEntities.size() == 0) {
      System.err.println("Gadget '" + domainGadgetEntryUrl + "' not found in directory");
    } else {
      if (dirEntities.size() > 1) {
        System.err.println("Expected to find 1 gadget in directory but found " + dirEntities.size());
        System.err.println("Unpublishing only the first");
      }

      // delete from domain's private gadget directory
      GadgetDirEntity dirEntity = dirEntities.get(0);
      URL dirEntryUrl = new URL(getDomainEntryUrl(PRIVATE_GADGET, dirEntity.getId()));
      dirClient.deleteEntry(dirEntryUrl);
      System.out.println(
          "Unpublished gadget '" + dirEntity.getUrl() + "' from '" + dirEntryUrl + "'");
    }
  }

  @Override
  public void usage(boolean inShell) {
    System.out.println(getFeedClientCommand(inShell) + getCommandName() +
        " <gadgetName|gadgetSpecUrl>");
    System.out.println("    Removes a gadget from domain's private gadget directory");
  }
}
