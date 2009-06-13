/*
 * Copyright 2008 Google Inc.
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
package com.google.feedserver.tools;

import com.google.feedserver.client.TypelessFeedServerClient;
import com.google.feedserver.util.CommonsCliHelper;
import com.google.feedserver.util.FeedServerClientException;
import com.google.feedserver.util.FileUtil;
import com.google.gdata.client.GoogleService;
import com.google.gdata.util.AuthenticationException;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Generic Google Feed Server Client Tool. See files in resources/clientTool for
 * examples of how to invoke it with various command line arguments and the
 * format of the input files.
 *
 */
public class FeedServerClientTool {

  public static final String OPERATION_GET_FEED = "getFeed";
  public static final String OPERATION_GET_ENTRY = "getEntry";
  public static final String OPERATION_INSERT = "insert";
  public static final String OPERATION_UPDATE = "update";
  public static final String OPERATION_DELETE = "delete";
  public static final String OPERATION_INSERT_GADGETSPEC = "insertGadgetSpec";
  public static final String OPERATION_UPDATE_GADGETSPEC = "updateGadgetSpec";
  public static final String ALL_OPERATIONS =
      OPERATION_GET_FEED + ", " + OPERATION_GET_ENTRY + ", " + OPERATION_INSERT + ", "
          + OPERATION_UPDATE + OPERATION_DELETE + OPERATION_INSERT_GADGETSPEC + " or "
          + OPERATION_UPDATE_GADGETSPEC;

  public static String url_FLAG = null;
  public static String url_HELP = "URL to feed or entry";

  public static String op_FLAG = null;
  public static String op_HELP = "Operation to perform on feed or entry (" + ALL_OPERATIONS + ")";

  public static String entryFilePath_FLAG = null;
  public static String entryFilePath_HELP = "Path to Atom XML file to insert or update";

  public static String username_FLAG = null;
  public static String username_HELP =
      "Optional user name used for login. " + "Can be entered on console";

  public static String password_FLAG = null;
  public static String password_HELP =
      "Optional password used for login. " + "Can be entered on console";

  public static String authnURL_FLAG = null;
  public static String authnURL_HELP =
      "The URL of the server that will handle authentication and return a token to be used with every request. The format is host:portNo";
  public static String authnURLProtocol_FLAG = "http";
  public static String authnURLProtocol_HELP =
      "Optional. The default protocol assumed is 'http'." + "Explicitly specify if its 'https'";

  public static String serviceName_FLAG = null;
  public static String serviceName_HELP =
      "The name of the service with which the user account is associated with";

  public static String gadgetName_FLAG = null;
  public static String gadgetName_HELP = "The name of the gadget";

  public static String gadgetSpecFile_FLAG = null;
  public static String gadgetSpecFile_HELP = "The path of the gadget spec xml file";

  public static String gadgetSpecEntityFile_FLAG = null;
  public static String gadgetSpecEntityFile_HELP =
      "The gadget spec entity file. This will define the feed entry schema used for storing gadget specs";

  public static final int TAB_STOP = 2;

  protected TypelessFeedServerClient feedServerClient;
  protected ThreadLocal<Integer> indentation;
  protected FileUtil fileUtil = new FileUtil();

  public static void main(String[] args) throws Exception {
    FeedServerClientTool tool = new FeedServerClientTool();
    tool.run(args);
  }

  public FeedServerClientTool() {
    indentation = new ThreadLocal<Integer>();
    indentation.set(0);
  }

  public FeedServerClientTool(TypelessFeedServerClient feedServerClient) {
    // initialize indentation for print result
    indentation = new ThreadLocal<Integer>();
    indentation.set(0);
    this.feedServerClient = feedServerClient;
  }

  public void run(String[] args) throws FeedServerClientException, MalformedURLException,
      IOException, AuthenticationException {
    // register command line flags
    CommonsCliHelper cliHelper = createClientHelper(args, getClass());

    checkServiceAndAuthNParams(cliHelper);

    // Process the request
    processRequest(cliHelper);
  }

  /**
   * Creates a client helper for the given command line arguments and the given
   * class
   * 
   * @param args The command line arguments
   * @param clazz The class that will process the command line arguments
   * @return The client helper
   */
  @SuppressWarnings("unchecked")
  protected CommonsCliHelper createClientHelper(String[] args, Class clazz) {
    CommonsCliHelper cliHelper = new CommonsCliHelper();
    cliHelper.register(clazz);
    cliHelper.parse(args);

    return cliHelper;
  }

  /**
   * Processes the request by forwarding the request to appropriate methods
   * matching the specified operation
   * 
   * @param cliHelper The client helper for retrieving the required paramater
   *        values
   * @throws FeedServerClientException
   * @throws MalformedURLException
   * @throws IOException
   * @throws AuthenticationException
   */
  protected void processRequest(CommonsCliHelper cliHelper) throws FeedServerClientException,
      MalformedURLException, IOException, AuthenticationException {
    // Check and process the specified request
    if (OPERATION_GET_FEED.equals(op_FLAG)) {
      getUserCredentials();
      printFeed(getFeed(url_FLAG));
    } else if (OPERATION_GET_ENTRY.equals(op_FLAG)) {
      getUserCredentials();
      printEntry(getEntry(url_FLAG));
    } else if (OPERATION_INSERT.equals(op_FLAG)) {
      getUserCredentials();
      printEntry(insert(url_FLAG, readFile(new File(entryFilePath_FLAG))));
    } else if (OPERATION_UPDATE.equals(op_FLAG)) {
      getUserCredentials();
      printEntry(update(url_FLAG, readFile(new File(entryFilePath_FLAG))));
    } else if (OPERATION_DELETE.equals(op_FLAG)) {
      getUserCredentials();
      delete(url_FLAG);
    } else if (OPERATION_INSERT_GADGETSPEC.equals(op_FLAG)) {
      getUserCredentials();
      printEntry(insert(url_FLAG, constructGadgetSpecEntryXml(gadgetSpecEntityFile_FLAG,
          gadgetName_FLAG, gadgetSpecFile_FLAG)));
    } else if (OPERATION_UPDATE_GADGETSPEC.equals(op_FLAG)) {
      getUserCredentials();
      printEntry(update(url_FLAG, constructGadgetSpecEntryXml(gadgetSpecEntityFile_FLAG,
          gadgetName_FLAG, gadgetSpecFile_FLAG)));
    } else {
      if (op_FLAG != null) {
        System.err.println("Unknown operation.  Must use " + ALL_OPERATIONS + ".");
      }
      cliHelper.usage();
    }
  }

  /**
   * Checks that the user has specified the service and authn parameters
   * 
   * @param cliHelper The client helper for retrieving the required paramater
   *        values
   */
  protected void checkServiceAndAuthNParams(CommonsCliHelper cliHelper) {
    // Check for the URL to be connected for authenticating and getting the
    // authZ token
    if (authnURL_FLAG == null) {
      System.err.println("Must specify the URL of the server that will handle authentication");
      cliHelper.usage();
      return;
    }

    // Check for the URL to be connected for authenticating and getting the
    // authZ token
    if (serviceName_FLAG == null) {
      System.err.println("Must specify the service name that will be used to authenticate users");
      cliHelper.usage();
      return;
    }

    // Initialize the feedserver client with the given authN URL and protocol
    this.feedServerClient =
        new TypelessFeedServerClient(new GoogleService(serviceName_FLAG, FeedServerClientTool.class
            .getName(), authnURLProtocol_FLAG, authnURL_FLAG));
  }

  /**
   * Gets user name and password from command line, or if missing, from console
   * and sets it on FeedServer client
   *
   * @throws AuthenticationException
   */
  protected void getUserCredentials() throws AuthenticationException {
    String username = username_FLAG;

    while (username == null || username.trim().isEmpty()) {
      getConsole().printf("The username cannot be null or blank");
      username = getConsole().readLine("\nUsername: ");
    }

    String password = password_FLAG;

    while (password == null || password.trim().isEmpty()) {
      getConsole().printf("The password cannot be null or blank");
      password = new String(getConsole().readPassword("\nPassword: "));
    }

    feedServerClient.setUserCredentials(username, password);
  }

  protected Console getConsole() {
    Console console = System.console();
    if (console == null) {
      throw new NullPointerException("no console");
    } else {
      return console;
    }
  }

  /**
   * Gets a feed at the given URL
   *
   * @param url URL of the feed
   * @return A feed of entries as a List of Maps
   * @throws MalformedURLException
   * @throws FeedServerClientException
   */
  public List<Map<String, Object>> getFeed(String url) throws MalformedURLException,
      FeedServerClientException {
    return feedServerClient.getEntries(new URL(url));
  }

  /**
   * Gets an entry at the given URL
   *
   * @param url URL of the entry
   * @return The entity as a Map
   * @throws MalformedURLException
   * @throws FeedServerClientException
   */
  public Map<String, Object> getEntry(String url) throws MalformedURLException,
      FeedServerClientException {
    return feedServerClient.getEntry(new URL(url));
  }

  /**
   * Inserts a new entry into a feed given a file containing its Atom XML
   * representation
   *
   * @param url URL to the feed
   * @param entryFile File containing the Atom XML representation of the new
   *        entry
   * @return The inserted entity as a Map
   * @throws IOException
   * @throws FeedServerClientException
   * @throws MalformedURLException
   */
  public Map<String, Object> insert(String url, File entryFile) throws IOException,
      FeedServerClientException, MalformedURLException {
    return insert(url, readFile(entryFile));
  }

  /**
   * Inserts a new entry into a feed given its Atom XML representation
   *
   * @param url URL to the feed
   * @param entryXml Atom XML representation of the new entry
   * @return The inserted entity as a Map
   * @throws IOException
   * @throws FeedServerClientException
   * @throws MalformedURLException
   */
  public Map<String, Object> insert(String url, String entryXml) throws IOException,
      FeedServerClientException, MalformedURLException {
    return insert(url, feedServerClient.getMapFromXml(entryXml));
  }

  /**
   * Inserts a new entry into a feed given the entry as a Map
   *
   * @param url URL to the feed
   * @param entity Entity to insert into feed
   * @return The inserted entity as a Map
   * @throws IOException
   * @throws FeedServerClientException
   * @throws MalformedURLException
   */
  public Map<String, Object> insert(String url, Map<String, Object> entity) throws IOException,
      FeedServerClientException, MalformedURLException {
    return feedServerClient.insertEntry(new URL(url), entity);
  }

  /**
   * Updates an entry given the file to its Atom XML representation
   *
   * @param url URL to the entry
   * @param entryFile File containing the Atom XML representation of the new
   *        entry
   * @return Updated entity as a Map
   * @throws IOException
   * @throws FeedServerClientException
   * @throws MalformedURLException
   */
  public Map<String, Object> update(String url, File entryFile) throws IOException,
      FeedServerClientException, MalformedURLException {
    return update(url, readFile(entryFile));
  }

  /**
   * Updates an entry given its Atom XML representation
   *
   * @param url URL to the entry
   * @param entryXml Atom XML representation of the entry
   * @return Updated entity as a Map
   * @throws IOException
   * @throws FeedServerClientException
   * @throws MalformedURLException
   */
  public Map<String, Object> update(String url, String entryXml) throws IOException,
      FeedServerClientException, MalformedURLException {
    return update(url, feedServerClient.getMapFromXml(entryXml));
  }

  /**
   * Updates an entry given its entity
   *
   * @param url URL to entry
   * @param entity Entity to update to for the entry
   * @return Updated entity as a Map
   * @throws IOException
   * @throws FeedServerClientException
   * @throws MalformedURLException
   */
  public Map<String, Object> update(String url, Map<String, Object> entity) throws IOException,
      FeedServerClientException, MalformedURLException {
    return feedServerClient.updateEntry(new URL(url), entity);
  }

  /**
   * Deletes an entry at the give URL
   *
   * @param url URL to the entry to delete
   * @throws FeedServerClientException
   * @throws MalformedURLException
   */
  public void delete(String url) throws FeedServerClientException, MalformedURLException {
    feedServerClient.deleteEntry(new URL(url));
  }

  /**
   * Prints a feed
   *
   * @param feed Feed to print as a List of Maps
   */
  public void printFeed(List<Map<String, Object>> feed) {
    printFeed(feed, System.out);
  }

  /**
   * Prints a feed on a stream
   *
   * @param feed Feed to print
   * @param out Stream to print on
   */
  public void printFeed(List<Map<String, Object>> feed, PrintStream out) {
    if (feed == null) {
      println(out, "Resource not found");
    } else {
      println(out, "<entities>");
      indentMore();
      for (Map<String, Object> entry : feed) {
        printEntry(entry, out);
      }
      indentLess();
      println(out, "</entities>");
    }
  }

  protected void printList(List<Map<String, Object>> feed) {
    printList(feed, System.out);
  }

  protected void printList(List<Map<String, Object>> feed, PrintStream out) {
    for (Map<String, Object> entry : feed) {
      printMap(entry, out);
    }
  }

  /**
   * Prints an entry
   *
   * @param entry Entry to print
   */
  public void printEntry(Map<String, Object> entry) {
    printEntry(entry, System.out);
  }

  /**
   * Prints an entry on a stream
   *
   * @param entry Entry to print
   * @param out Stream to print on
   */
  public void printEntry(Map<String, Object> entry, PrintStream out) {
    if (entry == null) {
      println(out, "Resource not found");
    } else {
      println(out, "<entity>");
      indentMore();
      printMap(entry);
      indentLess();
      println(out, "</entity>");
    }
  }

  /**
   * Prints error message on error output.
   * @param message Message to print
   */
  public void printError(String message) {
    System.err.println("Error: " + message);
  }

  protected void printMap(Map<String, Object> entry) {
    printMap(entry, System.out);
  }

  protected void printMap(Map<String, Object> entity, PrintStream out) {
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
        print(out, "<" + name + (i == 0 ? " repeatable=\"true\"" : "") + ">");
        if (values[i] instanceof Map) {
          out.println();
          indentMore();
          printMap((Map<String, Object>) values[i], out);
          indentLess();
        } else {
          out.print(values[i] == null ? "" : StringEscapeUtils.escapeXml(values[i].toString()));
        }
        println(out, "</" + name + ">");
      }
    } else if (value != null && value instanceof Map) {
      println(out, "<" + name + ">");
      indentMore();
      printMap((Map<String, Object>) value, out);
      indentLess();
      println(out, "</" + name + ">");
    } else {
      print(out, "<" + name + ">");
      out.print(value == null ? "" : StringEscapeUtils.escapeXml(value.toString()));
      out.println("</" + name + ">");
    }
  }

  protected void indentMore() {
    indentation.set(indentation.get() + TAB_STOP);
  }

  protected void indentLess() {
    indentation.set(indentation.get() - TAB_STOP);
  }

  protected void printIndentation(PrintStream out) {
    for (int i = 0; i < indentation.get(); i++) {
      out.print(' ');
    }
  }

  protected void print(PrintStream out, String s) {
    printIndentation(out);
    out.print(s);
  }

  protected void println(PrintStream out, String s) {
    printIndentation(out);
    out.println(s);
  }

  /**
   * Helper function that reads contents of specified file into a String.
   *
   * @param file File to read.
   * @return string with file contents.
   * @throws IOException if any file operations fail.
   */
  protected String readFile(File file) throws IOException {
    return resolveEmbeddedFiles(fileUtil.readFileContents(file), file.getParentFile());
  }

  /**
   * The pattern to get the name tag in the feed schema
   */
  protected final static Pattern gadgetSpecNameTag = Pattern.compile("<name>.*?</name>");

  /**
   * The pattern to get the speccontent tag in the feed schema
   */
  protected final static Pattern gadgetSpecSpecContentTag =
      Pattern.compile("<specContent>.*?</specContent>");

  /**
   * The file path indicator
   */
  protected final static String FILE_PATH_INDICATOR = "@";

  /**
   * Constructs the gadget spec entry that will be used for creating a new entry
   * or updating an existing entry
   * 
   * @param gadgetSpecEntityFile The gadget spec entity file which serves as the
   *        feed schema for the the gadget entry
   * @param gadgetName The gadget name
   * @param gadgetSpecFilePath The gadget spec file path
   * @return The gadget spec entry xml
   * @throws IOException If any exceptions are encountered
   */
  protected String constructGadgetSpecEntryXml(String gadgetSpecEntityFile, String gadgetName,
      String gadgetSpecFilePath) throws IOException {

    while (gadgetName == null || gadgetName.trim().isEmpty()) {
      getConsole().printf("The gadget name cannot be null or blank");
      gadgetName = getConsole().readLine("\nGadget name: ");
    }

    while (gadgetSpecFilePath == null || gadgetSpecFilePath.trim().isEmpty()) {
      getConsole().printf("The gadget spec file cannot be null or blank");
      gadgetName = getConsole().readLine("\nGadget spec file: ");
    }

    // Get the gadget spec feed schema
    File file = new File(gadgetSpecEntityFile);
    String gadgetSpecXml = fileUtil.readFileContents(file);
    StringBuilder fileContents = new StringBuilder();

    int lastStart = 0;

    // Add the given gadget name in the feed schema
    for (Matcher matcher = gadgetSpecNameTag.matcher(gadgetSpecXml); matcher.find();) {
      fileContents.append(gadgetSpecXml.substring(0, matcher.start() + 6));
      fileContents.append(gadgetName);
      lastStart = matcher.end() - 7;
    }

    // Add the given gadget file path in the feed schema
    for (Matcher matcher = gadgetSpecSpecContentTag.matcher(gadgetSpecXml); matcher.find();) {
      fileContents.append(gadgetSpecXml.substring(lastStart, lastStart + 7));
      lastStart += 7;
      fileContents.append(gadgetSpecXml.substring(lastStart, matcher.start() + 13));
      fileContents.append(FILE_PATH_INDICATOR).append(gadgetSpecFilePath);
      lastStart = matcher.end() - 14;
    }

    fileContents.append(gadgetSpecXml.substring((lastStart)));

    // Get the final gadget spec entry xml
    return resolveEmbeddedFiles(fileContents.toString(), file.getParentFile());
  }

  /**
   * Reads a file and returns its content as a string
   *
   * @param directory Directory relative to which the file is
   * @param filePath Path to file relative to directory
   * @return Content of file
   * @throws IOException
   */
  protected String readFile(File directory, String filePath) throws IOException {
    File file = new File(directory, filePath);
    return readFile(file);
  }

  /**
   * Pattern for embedded file path
   */
  protected final static Pattern embeddedFilePathPattern = Pattern.compile(">@.*?<");

  /**
   * Replaces all occurrences of ">@embeddedFilePath<" with ">the XML escaped
   * content of
   * embeddedFilePath<".  For example, if file "abc.xml"'s content is "
   * <abc>value</abc>", then "<xyz>@abc.xml</xyz>" becomes
   * "<xyz>&lt;abc&gt;value&lt;/abc&gt;".
   *
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
