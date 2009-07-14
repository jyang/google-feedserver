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

import com.google.feedserver.client.TypelessFeedServerClient;
import com.google.feedserver.tools.commands.DeleteGadget;
import com.google.feedserver.tools.commands.DeleteUserGadget;
import com.google.feedserver.tools.commands.ListGadgets;
import com.google.feedserver.tools.commands.ListUserGadgets;
import com.google.feedserver.tools.commands.PublishUserGadget;
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

import java.io.Console;
import java.util.LinkedHashMap;
import java.util.Map;

public class FeedClient {

  // flags
  public static String host_FLAG = "http://feedserver-enterprise.googleusercontent.com";
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

  // instance variables
  protected Map<String, FeedClientCommand> commands;
  protected TypelessFeedServerClient typelessClient;
  protected FileUtil fileUtil;
  protected CommonsCliHelper commandLine;

  public static void main(String[] args) {
    FeedClient shell = new FeedClient(args);
    shell.execute(args);
  }

  public FeedClient(String[] args) {
    GoogleService service = new GoogleService(
        serviceName_FLAG, FeedClient.class.getName(), authnURLProtocol_FLAG, authnURL_FLAG);
    typelessClient = new TypelessFeedServerClient(service);
    fileUtil = new FileUtil();
    commandLine = new CommonsCliHelper();

    init(args);
  }

  protected void init(String[] args) {
    commands  = new LinkedHashMap<String, FeedClientCommand>();
    addCommands();

    commandLine.register(FeedClient.class);
    commandLine.parse(args);
  }

  protected void login() {
    while (userEmail_FLAG == null || userEmail_FLAG.trim().isEmpty()) {
      userEmail_FLAG = getConsole().readLine("User email: ");
    }
    while (password_FLAG == null || password_FLAG.trim().isEmpty()) {
      password_FLAG = new String(getConsole().readPassword("Password: "));
    }
  }

  protected void addCommands() {
    GoogleService service = typelessClient.getService();
    addCommand(new DeleteGadget(service, fileUtil));
    addCommand(new DeleteUserGadget(service, fileUtil));
    addCommand(new ListGadgets(service, fileUtil));
    addCommand(new ListUserGadgets(service, fileUtil));
    addCommand(new PublishUserGadget(service, fileUtil));
    addCommand(new Shell(service, fileUtil, this));
    addCommand(new ShowGadget(service, fileUtil));
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
    } else {
      // login
      login();
      try {
        typelessClient.setUserCredentials(userEmail_FLAG, password_FLAG);
      } catch (AuthenticationException e) {
        printError(e.getMessage());
        return;
      }

      // execute command
      FeedClientCommand command = commands.get(args[0].toLowerCase());
      if (command == null) {
        printError("command '" + args[0] + "' not found");
      } else {
        try {
          command.execute(args);
        } catch (Exception e) {
          printError(e.getMessage());
          e.printStackTrace();
        }
      }
    }
  }

  public static Console getConsole() {
    Console console = System.console();
    if (console == null) {
      throw new NullPointerException("no console");
    } else {
      return console;
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

  protected void printError(String message) {
    System.err.println("Error: " + message);
  }
}
