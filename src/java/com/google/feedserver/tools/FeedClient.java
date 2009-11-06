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

package com.google.feedserver.tools;

import com.google.feedserver.tools.commands.AddBlackListedGadget;
import com.google.feedserver.tools.commands.AddWhiteListedGadget;
import com.google.feedserver.tools.commands.DeleteGadget;
import com.google.feedserver.tools.commands.DeleteUserGadget;
import com.google.feedserver.tools.commands.DirGadgets;
import com.google.feedserver.tools.commands.DirPublicGadgets;
import com.google.feedserver.tools.commands.GetEntry;
import com.google.feedserver.tools.commands.GetFeed;
import com.google.feedserver.tools.commands.ListBlackListedGadgets;
import com.google.feedserver.tools.commands.RemoveBlackListedGadget;
import com.google.feedserver.tools.commands.ShowPublicGadgetDirFilter;
import com.google.feedserver.tools.commands.InsertEntry;
import com.google.feedserver.tools.commands.ListGadgets;
import com.google.feedserver.tools.commands.ListUserGadgets;
import com.google.feedserver.tools.commands.ListWhiteListedGadgets;
import com.google.feedserver.tools.commands.PublishGadget;
import com.google.feedserver.tools.commands.PublishUserGadget;
import com.google.feedserver.tools.commands.RemoveWhiteListedGadget;
import com.google.feedserver.tools.commands.SetPublicGadgetDirFilter;
import com.google.feedserver.tools.commands.Shell;
import com.google.feedserver.tools.commands.ShowGadget;
import com.google.feedserver.tools.commands.ShowUserGadget;
import com.google.feedserver.tools.commands.UnpublishGadget;
import com.google.feedserver.tools.commands.UploadGadget;
import com.google.feedserver.tools.commands.UploadUserGadget;
import com.google.feedserver.util.CommonsCliHelper;
import com.google.feedserver.util.FileUtil;
import com.google.gdata.client.GoogleService;
import com.google.gdata.util.AuthenticationException;

import jline.ConsoleReader;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class FeedClient {

  // flags
  public static String HOST_DEFAULT = "http://feedserver-enterprise.googleusercontent.com";
  public static String host_FLAG = HOST_DEFAULT;
  public static final String host_HELP = "Optional FeedServer host name.  Defaults to '" +
      host_FLAG + "'";

  public static String userEmail_FLAG = null;
  public static final String userEmail_HELP =
      "Optional user email address used for login. " + "Can be entered on console";

  public static String password_FLAG = null;
  public static final String password_HELP =
      "Optional password used for login. " + "Can be entered on console";

  public static String authnURLProtocol_FLAG = "https";
  public static final String authnURLProtocol_HELP = "Optional authentication protocl.  " +
      "Defaults to '" + authnURLProtocol_FLAG + "'";

  public static String authnURL_FLAG = "www.google.com";
  public static final String authnURL_HELP = "Optional authentication service URL.  Defaults to '"
      + authnURL_FLAG + "'";

  public static String serviceName_FLAG = "esp";
  public static final String serviceName_HELP = "Optional service name.  Defaults to '" +
      serviceName_FLAG + "'";

  protected static ConsoleReader consoleReader;
  static {
    try {
      consoleReader = new ConsoleReader();
    } catch (IOException e) {
      printError(e.getMessage());
    }
  }

  // instance variables
  protected Map<String, FeedClientCommand> commands;
  protected FileUtil fileUtil;
  protected CommonsCliHelper commandLine;
  protected GoogleService service;

  public static void main(String[] args) {
    FeedClient shell = new FeedClient(args);
    shell.execute(args);
  }

  /**
   * Reads a line from console
   * @param prompt Prompt to print
   * @return Line entered on console
   * @throws IOException
   */
  public static String readLine(String prompt) throws IOException {
    return consoleReader.readLine(prompt);
  }

  /**
   * Reads a password from console
   * @param prompt Prompt to print
   * @return Password entered on console
   * @throws IOException
   */
  public static String readPassword(String prompt) throws IOException {
    return consoleReader.readLine(prompt, new Character('*'));
  }

  /**
   * Prompts user to continue or stop
   * @param message Message to show
   * @return true if user wants to continue; false otherwise
   * @throws IOException
   */
  public static boolean promptContinue(String message) throws IOException {
    System.out.println(message);
    String answer = readLine("Continue? (y/N) ");
    return "y".equals(answer.toLowerCase());
  }

  public FeedClient(String[] args) {
    this();

    initCommandLine(args);
    initCommands();
  }

  protected FeedClient() {
    fileUtil = new FileUtil();
    commandLine = new CommonsCliHelper();
  }

  protected void initCommandLine(String[] args) {
    // trim all values
    for (int i = 0; i < args.length; i++) {
      args[i] = args[i].trim();
    }

    commandLine.register(FeedClient.class);
    commandLine.parse(args);
  }

  protected void login() throws AuthenticationException, IOException {
    while (userEmail_FLAG == null || userEmail_FLAG.trim().isEmpty()) {
      userEmail_FLAG = readLine("User email: ");
    }
    while (password_FLAG == null || password_FLAG.trim().isEmpty()) {
      password_FLAG = new String(readPassword("Password: "));
    }
    service.setUserCredentials(userEmail_FLAG, password_FLAG);
  }

  protected void initCommands() {
    service = new GoogleService(
        serviceName_FLAG, FeedClient.class.getName(), authnURLProtocol_FLAG, authnURL_FLAG);

    commands = new LinkedHashMap<String, FeedClientCommand>();
    addCommand(new AddBlackListedGadget(service, fileUtil));
    addCommand(new AddWhiteListedGadget(service, fileUtil));
    addCommand(new DeleteGadget(service, fileUtil));
    addCommand(new DeleteUserGadget(service, fileUtil));
    addCommand(new DirGadgets(service, fileUtil));
    addCommand(new DirPublicGadgets(service, fileUtil));
    addCommand(new GetEntry(service, fileUtil));
    addCommand(new GetFeed(service, fileUtil));
    addCommand(new InsertEntry(service, fileUtil));
    addCommand(new ListGadgets(service, fileUtil));
    addCommand(new ListUserGadgets(service, fileUtil));
    addCommand(new ListBlackListedGadgets(service, fileUtil));
    addCommand(new ListWhiteListedGadgets(service, fileUtil));
    addCommand(new PublishGadget(service, fileUtil));
    addCommand(new PublishUserGadget(service, fileUtil));
    addCommand(new RemoveBlackListedGadget(service, fileUtil));
    addCommand(new RemoveWhiteListedGadget(service, fileUtil));
    addCommand(new SetPublicGadgetDirFilter(service, fileUtil));
    addCommand(new Shell(service, fileUtil, this));
    addCommand(new ShowGadget(service, fileUtil));
    addCommand(new ShowPublicGadgetDirFilter(service, fileUtil));
    addCommand(new ShowUserGadget(service, fileUtil));
    addCommand(new UnpublishGadget(service, fileUtil));
    addCommand(new UploadGadget(service, fileUtil));
    addCommand(new UploadUserGadget(service, fileUtil));
  }

  protected void addCommand(FeedClientCommand command) {
    commands.put(command.getCommandName().toLowerCase(), command);
    commandLine.register(command.getClass());
  }

  public void execute(String[] args) {
    if (args.length == 0) {
      printUsage(args);
      return;
    }

    FeedClientCommand command = commands.get(args[0].toLowerCase());
    if (command == null) {
      printError("command '" + args[0] + "' not found");
    } else {
      try {
        login();
        command.execute(args);
      } catch (Exception e) {
        printError(e.getMessage());
      }
    }
  }

  public void printUsage(String[] args) {
    System.out.println("Usage: fsct <command> <arg> <arg> ... <-flag> <-flag> ...");
    System.out.println("e.g.: fsct uploadUserGadget /tmp/hello-user.xml -userEmail " +
      "john.doe@example.com -host " + host_FLAG);
    System.out.println();

    System.out.print("Flag ");
    commandLine.usage();

    System.out.println();
    printCommandUsage(false);
  }

  public void printCommandUsage(boolean inShell) {
    System.out.println("Commands (case insensitive):");
    for (Map.Entry<String, FeedClientCommand> entry: commands.entrySet()) {
      FeedClientCommand command = entry.getValue();
      System.out.println();
      System.out.print("  ");
      command.usage(inShell);
    }
  }

  protected static void printError(String message) {
    System.err.println("Error: " + message);
  }
}
