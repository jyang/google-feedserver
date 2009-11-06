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

public abstract class ListFilteredGadgets extends GadgetDirFilterCommand {

  public ListFilteredGadgets(GoogleService service, FileUtil fileUtil, String gadgetFilterName,
      String gadgetFilterExternalName) {
    super(service, fileUtil, gadgetFilterName, gadgetFilterExternalName);
  }

  @Override
  public void execute(String[] args) throws Exception {
    URL feedUrl = new URL(getDomainFeedUrl(gadgetFilterName));
    List<FilteredGadgetEntity> entities = filteredGadgetClient.getEntities(feedUrl);
    for (FilteredGadgetEntity entity: entities) {
      System.out.println(getDomainEntryUrl(gadgetFilterName, entity.getGadgetId()) + ": " +
          decorateNoUrl(entity.getGadgetUrl()));
    }
  }

  protected String decorateNoUrl(String gadgetUrl) {
    return gadgetUrl == null ? "[gadget spec URL not found]" : gadgetUrl;
  }

  @Override
  public void usage(boolean inShell) {
    System.out.println(getFeedClientCommand(inShell) + getCommandName());
    System.out.println("    Lists " + gadgetFilterExternalName +
        " listed gadgets for domain's public gadget directory");
  }
}
