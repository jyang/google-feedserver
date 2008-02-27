/**
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.feedserver.config;

import java.io.IOException;
import java.io.Reader;


/**
 * Configuration for feed-server adapters.
 *
 * @author abhinavk@gmail.com (Abhinav Khandelwal)
 *
 */
public class AdapterConfiguration extends Configuration {
  private final String fileLocation;
  public AdapterConfiguration(String fileLocation) {
    this.fileLocation = fileLocation;
  }

  public Reader getAdapterConfigAsReader()
      throws IOException {
    ServerConfiguration config = ServerConfiguration.getInstance();
    String filePath = config.getAdapterConfigLocation() + fileLocation;
    return loadConfigFileAsReader(filePath);
  }
}
