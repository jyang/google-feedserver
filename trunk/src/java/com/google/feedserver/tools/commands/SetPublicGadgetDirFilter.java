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
 * Command that sets domain's public gadget directory filter.
 *
 * Usage: fsct setPublicGadgetDirFilter NO_RESTRICTION|WHITE_LIST|BLACK_LIST <flags ...> 
 */
public class SetPublicGadgetDirFilter extends GadgetDirFilterCommand {

  public SetPublicGadgetDirFilter(GoogleService service, FileUtil fileUtil) {
    super(service, fileUtil);
  }

  @Override
  public void execute(String[] args) throws Exception {
    String filterTypeName = checkFilterType(checkNotFlag(args[1]));

    URL gadgetConfigFeedUrl = new URL(getDomainFeedUrl(GADGETS));
    GadgetConfigEntity entity = new GadgetConfigEntity(filterTypeName);
    gadgetConfigClient.updateEntity(gadgetConfigFeedUrl, entity);
  }

  protected String checkFilterType(String filterTypeName) {
    if (FilterType.NO_RESTRICTION.name().equalsIgnoreCase(filterTypeName) ||
        FilterType.WHITE_LIST.name().equalsIgnoreCase(filterTypeName) ||
        FilterType.BLACK_LIST.name().equalsIgnoreCase(filterTypeName)) {
      return filterTypeName;
    } else {
      throw new IllegalArgumentException("Invalid filter type. Valid values: " +
          FilterType.NO_RESTRICTION.name() + ", " + FilterType.WHITE_LIST.name() + ", " +
          FilterType.BLACK_LIST.name());
    }
  }

  @Override
  public void usage(boolean inShell) {
    System.out.println(getFeedClientCommand(inShell) + getCommandName() + " " +
        FilterType.NO_RESTRICTION.name() + "|" + FilterType.WHITE_LIST.name() + "|" +
        FilterType.BLACK_LIST.name());
    System.out.println("    Sets domain's public gadget directory filter");
  }
}
