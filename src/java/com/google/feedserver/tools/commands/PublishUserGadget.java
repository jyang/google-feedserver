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

import com.google.feedserver.client.FeedServerClient;
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

  public static final String ERROR_GADGET_ALREADY_EXISTS = "Gadget already exists";
  
  public static class GadgetDirEntity {
    protected String id;
    protected String url;
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
  }

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
    GadgetSpecEntity domainGadgetEntity;
    try {
      domainGadgetEntity = specClient.getEntity(domainGadgetEntryUrl);
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
    FeedServerClient<GadgetDirEntity> dirClient = new FeedServerClient<GadgetDirEntity>(
        service, GadgetDirEntity.class);
    URL domainDirFeedUrl = new URL(getDomainFeedUrl(PRIVATE_GADGET));
    try {
      domainDirEntity = dirClient.insertEntity(domainDirFeedUrl, domainDirEntity);
    } catch(FeedServerClientException e) {
      String message = e.getMessage();
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
    String answer = FeedClient.getConsole().readLine("Continue? (y/n) ");
    return "y".equals(answer.toLowerCase());
  }

  @Override
  public void usage() {
    System.out.println("fsct " + getCommandName() + " <gadgetName>");
  }
}
