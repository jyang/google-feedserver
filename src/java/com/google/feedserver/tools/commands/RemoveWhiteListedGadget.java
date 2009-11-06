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


/**
 * Command that removes a white listed gadget for domain's public gadget directory.
 *
 * Usage: fsct removeWhiteListedGadget gadgetId <flags ...> 
 */
public class RemoveWhiteListedGadget extends RemoveFilteredGadget {

  public RemoveWhiteListedGadget(GoogleService service, FileUtil fileUtil) {
    super(service, fileUtil, wHITE_LISTED_GADGET, "white");
  }
}
