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

package com.google.feedserver.tools.hosted;

import com.google.feedserver.tools.FeedServerClientTool;
import com.google.feedserver.util.CommonsCliHelper;
import com.google.feedserver.util.FeedServerClientException;
import com.google.gdata.util.AuthenticationException;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The feed server client tool for opeartions specific to hosted version of the
 * feedserver
 * 
 * @author rakeshs101981@gmail.com (Rakesh Shete)
 */
public class HostedFeedServerClientTools extends FeedServerClientTool {

  public static final String OPERATION_PUBLISH = "publish";

  public HostedFeedServerClientTools() {
    super();
  }

  /**
   * @param args The arguments
   * @throws IOException
   * @throws FeedServerClientException
   * @throws AuthenticationException
   * @throws MalformedURLException
   */
  public static void main(String[] args) throws MalformedURLException, AuthenticationException,
      FeedServerClientException, IOException {
    HostedFeedServerClientTools hostedFeedServerClientTools = new HostedFeedServerClientTools();
    hostedFeedServerClientTools.run(args);
  }

  public static String publishFeedEntryURL_FLAG = null;
  public static String publishFeedEntryURL_HELP = "Path to Atom XML file to insert or update";

  @Override
  protected void processRequest(CommonsCliHelper cliHelper) throws FeedServerClientException,
      MalformedURLException, IOException, AuthenticationException {
    if (OPERATION_PUBLISH.equals(op_FLAG)) {
      getUserCredentials();
      printEntry(publishToDirectory(url_FLAG, createPublishFeedSpecXml(entryFilePath_FLAG,
          publishFeedEntryURL_FLAG)));
    } else {
      super.processRequest(cliHelper);
    }
  }

  /**
   * Publishes a given feed entry to the given directory. The directory itself
   * is identified as a feed
   * 
   * @param feedEntryUrl URL to the feed entry
   * @param publishFeedSpecXML Atom XML representation of the new entry
   * @return The inserted entity as a Map
   * @throws IOException
   * @throws FeedServerClientException
   * @throws MalformedURLException
   */
  public Map<String, Object> publishToDirectory(String feedEntryUrl, String publishFeedSpecXML)
      throws IOException, FeedServerClientException, MalformedURLException {

    // Get a map representation for the given Atom XML
    return insert(feedEntryUrl, feedServerClient.getMapFromXml(publishFeedSpecXML));
  }

  /**
   * The pattern to get the url tag in the feed schema
   */
  protected final static Pattern publishFeedEntrySpecUrlTag = Pattern.compile("<url>.*?</url>");

  /**
   * Creates an xml representing the entry spec for publishing it
   * 
   * @param publishFeedEntrySpecFile The spec entity file for constructing a
   *        feed entry spec to publish to a directory
   * @param feedEntryUrl The url of the feed entry Url that will be published
   * @return The xml representing the entry spec for publishing it
   * @throws IOException
   */
  protected String createPublishFeedSpecXml(String publishFeedEntrySpecFile, String feedEntryUrl)
      throws IOException {
    // Get the gadget spec feed schema
    File file = new File(publishFeedEntrySpecFile);
    String publishFeedEntrySpecXml = readFileContents(file);
    StringBuilder fileContents = new StringBuilder();

    int lastStart = 0;

    // Add the given feed entry url
    for (Matcher matcher = publishFeedEntrySpecUrlTag.matcher(publishFeedEntrySpecXml); matcher
        .find();) {
      fileContents.append(publishFeedEntrySpecXml.substring(0, matcher.start() + 5));
      fileContents.append(feedEntryUrl);
      lastStart = matcher.end() - 6;
    }

    fileContents.append(publishFeedEntrySpecXml.substring((lastStart)));

    // Get the final spec entry xml. Resolve any file path placeholders to
    // actual contents of the file
    return resolveEmbeddedFiles(fileContents.toString(), file.getParentFile());
  }

}
