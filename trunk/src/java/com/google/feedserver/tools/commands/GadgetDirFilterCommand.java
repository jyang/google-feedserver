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
import com.google.feedserver.util.FileUtil;
import com.google.gdata.client.GoogleService;

public abstract class GadgetDirFilterCommand extends GadgetCommand {

  public static final String GADGETS = "gadgets";
  public static final String SETTINGS = "settings";
  public static final String wHITE_LISTED_GADGET = "WhiteListedGadget";
  public static final String BLACK_LISTED_GADGET = "BlackListedGadget";

  public enum FilterType {
    NO_RESTRICTION, WHITE_LIST, BLACK_LIST
  }

  /**
   * Gadget configuration bean. 
   */
  public static class GadgetConfigEntity {
    protected String gadgetFilter;
  
    public GadgetConfigEntity() {}
    
    public GadgetConfigEntity(String filterTypeName) {
      this.gadgetFilter = filterTypeName;
    }

    public String getName() {
      return SETTINGS;
    }

    public String getGadgetFilter() {
      return gadgetFilter;
    }
  
    public void setGadgetFilter(String gadgetFilter) {
      this.gadgetFilter = gadgetFilter;
    }
  }

  /**
   * Filter gadget bean.
   */
  public static class FilteredGadgetEntity {
    protected String gadgetId;
    protected String gadgetUrl;

    public FilteredGadgetEntity() {}

    public FilteredGadgetEntity(String gadgetIdOrUrl) {
      if (isUrl(gadgetIdOrUrl)) {
        setGadgetUrl(gadgetIdOrUrl);
      } else {
        setGadgetId(gadgetIdOrUrl);
      }
    }

    public String getGadgetId() {
      return gadgetId;
    }

    public void setGadgetId(String gadgetId) {
      this.gadgetId = gadgetId;
    }

    public String getGadgetUrl() {
      return gadgetUrl;
    }

    public void setGadgetUrl(String gadgetUrl) {
      this.gadgetUrl = gadgetUrl;
    }
  }

  protected FeedServerClient<GadgetConfigEntity> gadgetConfigClient;
  protected FeedServerClient<FilteredGadgetEntity> filteredGadgetClient;
  protected String gadgetFilterName;
  protected String gadgetFilterExternalName;

  public GadgetDirFilterCommand(GoogleService service, FileUtil fileUtil) {
    super(service, fileUtil);

    gadgetConfigClient = new FeedServerClient<GadgetConfigEntity>(service,
        GadgetConfigEntity.class);
    filteredGadgetClient = new FeedServerClient<FilteredGadgetEntity>(service,
        FilteredGadgetEntity.class);
  }

  public GadgetDirFilterCommand(GoogleService service, FileUtil fileUtil, String gadgetFilterName,
      String gadgetFilterExternalName) {
    this(service, fileUtil);

    this.gadgetFilterName = gadgetFilterName;
    this.gadgetFilterExternalName = gadgetFilterExternalName;
  }

  public static boolean isUrl(String s) {
    return s != null && s.startsWith("http");
  }
}
