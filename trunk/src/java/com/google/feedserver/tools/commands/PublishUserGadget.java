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
import com.google.feedserver.util.FeedServerClientException;
import com.google.feedserver.util.FileUtil;
import com.google.gdata.client.GoogleService;

import java.net.URL;

/**
 * Command for publishing a user gadget to a domain's private gadget directory.
 *
 * Usage: fsct publishUserGadget <gadgetName> <flags ...>
 */
public class PublishUserGadget extends GadgetCommand {

  public PublishUserGadget(GoogleService service, FileUtil fileUtil) {
    super(service, fileUtil);
  }

  @Override
  public void execute(String[] args) throws Exception {
    String gadgetName = checkNotFlag(args[1]);

    if (gadgetName == null) {
      throw new IllegalArgumentException("Missing first argument for gadget name");
    }

    // read user gadget
    URL userGadgetEntryUrl = new URL(getUserEntryUrl(PRIVATE_GADGET_SPEC, gadgetName));
    GadgetSpecEntity userGadgetEntity;
    try {
      userGadgetEntity = specClient.getEntity(userGadgetEntryUrl);
    } catch(FeedServerClientException e) {
      throw new FeedServerClientException(
          "Failed to get user gadget '" + userGadgetEntryUrl + "'", e.getCause());
    }

    // copy to domain gadget
    URL domainGadgetEntryUrl = new URL(getDomainEntryUrl(PRIVATE_GADGET_SPEC, gadgetName));
    URL domainGadgetFeedUrl = new URL(getDomainFeedUrl(PRIVATE_GADGET_SPEC));
    try {
      specClient.getEntity(domainGadgetEntryUrl);
      // domain gadget exists; update
      try {
        if (promptContinue("About to overwrite gadget '" + domainGadgetEntryUrl + "'")) {
          specClient.updateEntity(domainGadgetFeedUrl, userGadgetEntity);
        } else
          return;
      } catch(FeedServerClientException e) {
        throw new Exception("Failed to update gadget '" + domainGadgetEntryUrl + "'");
      }
    } catch(FeedServerClientException e) {
      // domain gadget doesn't exist; insert
      specClient.insertEntity(domainGadgetFeedUrl, userGadgetEntity);
    }

    // publish domain gadget
    GadgetDirEntity domainDirEntity = new GadgetDirEntity();
    domainDirEntity.setUrl(domainGadgetEntryUrl.toString());
    URL domainDirFeedUrl = new URL(getDomainFeedUrl(PRIVATE_GADGET));
    try {
      domainDirEntity = dirClient.insertEntity(domainDirFeedUrl, domainDirEntity);
    } catch(FeedServerClientException e) {
      if (e.getMessage().indexOf(ERROR_GADGET_ALREADY_EXISTS) < 0) {
        throw e;
      }
    }

    if (domainDirEntity.getId() == null) {
      System.out.println("Gadget '" + userGadgetEntryUrl + "' already published and visible in " +
          "your domain's private gadget directory");
    } else {
      System.out.println("Gadget '" + userGadgetEntryUrl + "' published successfully to '" +
          getDomainEntryUrl(PRIVATE_GADGET, domainDirEntity.getId()) + "' and now visible " +
          "in your domain's private gadget directory");
    }
  }

  protected boolean promptContinue(String message) {
    System.out.println(message);
    String answer = FeedClient.getConsole().readLine("Continue? (y/N) ");
    return "y".equals(answer.toLowerCase());
  }

  @Override
  public void usage(boolean inShell) {
    System.out.println(getFeedClientCommand(inShell) + getCommandName() + " <gadgetName>");
    System.out.println("    Publishes a user gadget to domain's private gadget directory");
  }
}
