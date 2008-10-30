/* Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.feedserver.tools;

import com.google.feedserver.client.TypelessFeedServerClient;
import com.google.feedserver.util.FeedServerClientException;
import com.google.feedserver.util.CommonsCliHelper;
import com.google.gdata.client.GoogleService;
import com.google.gdata.util.AuthenticationException;
import com.google.inject.Inject;

import java.io.BufferedInputStream;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Generic Google Feed Server Client Tool
 *
 */
public class FeedServerClientTool {

  public static final String OPERATION_GET_FEED = "getFeed";
  public static final String OPERATION_GET_ENTRY = "getEntry";
  public static final String OPERATION_INSERT = "insert";
  public static final String OPERATION_UPDATE = "update";
  public static final String OPERATION_DELETE = "delete";
  public static final String ALL_OPERATIONS = OPERATION_GET_FEED + ", " + OPERATION_GET_ENTRY +
      ", " + OPERATION_INSERT + ", " + OPERATION_UPDATE + " or " + OPERATION_DELETE;

  public static String url_FLAG = null;
  public static String url_HELP = "URL to feed or entry";

  public static String op_FLAG = null;
  public static String op_HELP = "Operation to perform on feed or entry (" + ALL_OPERATIONS + ")";

  public static String entryFilePath_FLAG = null;
  public static String entryFilePath_HELP = "Path to Atom XML file to insert or update";

  public static String username_FLAG = null;
  public static String username_HELP = "Optional user name used for login. " +
      "Can be entered on console";

  public static String password_FLAG = null;
  public static String password_HELP = "Optional password used for login. " +
      "Can be entered on console";

  protected TypelessFeedServerClient feedServerClient;

  public static void main(String[] args) throws Exception {

    FeedServerClientTool tool = new FeedServerClientTool();
    tool.run(args);
  }

  public FeedServerClientTool() {

    this(new TypelessFeedServerClient(
	    new GoogleService("jotspot", FeedServerClientTool.class.getName())));
  }

  @Inject
  public FeedServerClientTool(TypelessFeedServerClient feedServerClient) {

    this.feedServerClient = feedServerClient;
  }

  public void run(String[] args) throws FeedServerClientException, MalformedURLException,
  	  IOException, AuthenticationException {

    // register command line flags
    CommonsCliHelper cliHelper = new CommonsCliHelper();
    cliHelper.register(FeedServerClientTool.class);
    cliHelper.parse(args);

    if (OPERATION_GET_FEED.equals(op_FLAG)) {
      getUserCredentials();
      printList(getFeed(url_FLAG));
    } else if (OPERATION_GET_ENTRY.equals(op_FLAG)) {
      getUserCredentials();
      printMap(getEntry(url_FLAG));
    } else if (OPERATION_INSERT.equals(op_FLAG)) {
      getUserCredentials();
      printMap(insert(url_FLAG, readFile(new File(entryFilePath_FLAG))));
    } else if (OPERATION_UPDATE.equals(op_FLAG)) {
      getUserCredentials();
      printMap(update(url_FLAG, readFile(new File(entryFilePath_FLAG))));
    } else if (OPERATION_DELETE.equals(op_FLAG)) {
      getUserCredentials();
      delete(url_FLAG);
    } else {
      if (op_FLAG != null) {
        System.err.println("Unknown operation.  Must use " + ALL_OPERATIONS + ".");
      }
      cliHelper.usage();
    }
  }

  protected void getUserCredentials() throws AuthenticationException {

    String username = username_FLAG == null ? getConsole().readLine("Username: ") : username_FLAG;
    String password = password_FLAG == null ?
        new String(getConsole().readPassword("Password: ")): password_FLAG;
    feedServerClient.setUserCredentials(username, new String(password));
  }

  protected Console getConsole() {
    Console console = System.console();
    if (console == null) {
      throw new NullPointerException("no console");
    } else {
      return console;
    }
  }

  public List<Map<String, Object>> getFeed(String url) throws MalformedURLException,
      FeedServerClientException {

    return feedServerClient.getEntries(new URL(url));
  }

  public Map<String, Object> getEntry(String url) throws MalformedURLException,
      FeedServerClientException {

    return feedServerClient.getEntry(new URL(url));
  }

  public Map<String, Object> insert(String url, File entryFile) throws IOException,
      FeedServerClientException, MalformedURLException {

    return insert(url, readFile(entryFile));
  }
  
  public Map<String, Object> insert(String url, String entryXml) throws IOException,
      FeedServerClientException, MalformedURLException {

    return insert(url, feedServerClient.getMapFromXml(entryXml));
  }

  public Map<String, Object> insert(String url, Map<String, Object> entity) throws IOException,
      FeedServerClientException, MalformedURLException {

    return feedServerClient.insertEntry(new URL(url), entity);
  }

  public Map<String, Object> update(String url, File entryFile) throws IOException,
      FeedServerClientException, MalformedURLException {

    return update(url, readFile(entryFile));
  }

  public Map<String, Object> update(String url, String entryXml) throws IOException,
      FeedServerClientException, MalformedURLException {

    return update(url, feedServerClient.getMapFromXml(entryXml));
  }

  public Map<String, Object> update(String url, Map<String, Object> entity) throws IOException,
      FeedServerClientException, MalformedURLException {

    return feedServerClient.updateEntry(new URL(url), entity);
  }

  public void delete(String url) throws FeedServerClientException, MalformedURLException {

    feedServerClient.deleteEntry(new URL(url));
  }

  public void printList(List<Map<String, Object>> feed) {

    printList(feed, System.out);
  }

  public void printList(List<Map<String, Object>> feed, PrintStream out) {

    out.println("<entities>");
    for (Map<String, Object> entry: feed) {
      out.println("<entity>");
      printMap(entry, out);
      out.println("</entity>");
    }
    out.println("</entities>");
  }

  public void printMap(Map<String, Object> entry) {

    printMap(entry, System.out);
  }

  public void printMap(Map<String, Object> entity, PrintStream out) {

    for (Map.Entry<String, Object> e : entity.entrySet()) {
      printProperty(e.getKey(), e.getValue(), out);
    }
  }

  protected void printProperty(Map<String, Object> entity, String name, PrintStream out) {

    printProperty(name, entity.get(name), out);
  }

  @SuppressWarnings("unchecked")
  protected void printProperty(String name, Object value, PrintStream out) {

    if (value != null && value instanceof Object[]) {
      Object[] values = (Object[]) value;
      for (int i = 0; i < values.length; i++) {
        out.print("<" + name + (i == 0 ? " repeatable=\"true\"" : "") + ">");
        if (values[i] instanceof Map) {
          printMap((Map<String, Object>) values[i], out);
        } else {
          out.print(values[i] == null ? "" : StringEscapeUtils.escapeXml(values[i].toString()));
        }
        out.println("</" + name + ">");
      }
    } else if (value != null && value instanceof Map) {
      out.print("<" + name + ">");
      printMap((Map<String, Object>) value, out);
      out.println("</" + name + ">");
    } else {
      out.print("<" + name + ">");
      out.print(value == null ? "" : StringEscapeUtils.escapeXml(value.toString()));
      out.println("</" + name + ">");
    }
  }
  
  /**
   * Helper function that reads contents of specified file into a String.
   * 
   * @param filename to read.
   * @return string with file contents.
   * @throws IOException if any file operations fail.
   */
  protected String readFile(File file) throws IOException {

    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
    byte[] fileContents = new byte[(int) file.length()];
    bis.read(fileContents);
    return resolveEmbeddedFiles(new String(fileContents), file.getParentFile());
  }

  /**
   * Reads a file and returns its content as a string
   * @param directory Directory relative to which the file is
   * @param filePath Path to file relative to directory
   * @return Content of file
   * @throws IOException
   */
  protected String readFile(File directory, String filePath) throws IOException {

    File file = new File(directory, filePath);
    return readFile(file);
  }

  protected final static Pattern embeddedFilePathPattern = Pattern.compile(">@.*?<");

  /**
   * Replaces all occurrences of ">@embeddedFilePath<" with ">the XML escaped content of
   * embeddedFilePath<".  For example, if file "abc.xml"'s content is "<abc>value</abc>",
   * then "<xyz>@abc.xml</xyz>" becomes "<xyz>&lt;abc&gt;value&lt;/abc&gt;".
   * @param content Input content
   * @param directory Directory where embedded files are to be found 
   * @return Content with all content of embedded files
   * @throws IOException 
   */
  protected String resolveEmbeddedFiles(String content, File directory) throws IOException {

    StringBuilder builder = new StringBuilder();
    int lastStart = 0;
    for (Matcher matcher = embeddedFilePathPattern.matcher(content); matcher.find();) {
      // add before embedded file
      builder.append(content.substring(lastStart, matcher.start() + 1));

      // add embedded file
      String group = matcher.group();
      String embeddedFilePath = group.substring(2, group.length() - 1);
      String embeddedFileContent = readFile(directory, embeddedFilePath);
	  builder.append(StringEscapeUtils.escapeXml(embeddedFileContent));

      lastStart = matcher.end() - 1;
    }
    builder.append(content.substring(lastStart));

    return builder.toString();
  }
}
