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

import com.google.feedserver.tools.FeedClient;
import com.google.feedserver.tools.FeedClientCommand;
import com.google.feedserver.util.FileUtil;
import com.google.gdata.client.GoogleService;

import java.util.ArrayList;
import java.util.List;

/**
 * Command for starting FSCT shell to execute multiple commands with one login.
 *
 * Usage: fsct shell <flags ...>
 */
public class Shell extends FeedClientCommand {
  public static final String PROMPT = "fsct> ";

  public static final String COMMAND_HELP = "help";
  public static final String COMMAND_QUIT = "quit";

  protected FeedClient feedClient;

  public Shell(GoogleService service, FileUtil fileUtil, FeedClient feedClient) {
    super(service, fileUtil);
    this.feedClient = feedClient;
  }

  @Override
  public void execute(String[] shellArgs) throws Exception {
    for (;;) {
      String[] args = parseCommandLine(FeedClient.getConsole().readLine(PROMPT));

      if (args.length == 0) {
        // empty command line; do nothing
      } else if (isCommand(args, getCommandName())) {
        // already in shell: ignore
      } else if (isCommand(args, COMMAND_QUIT)) {
        break;
      } else if (isCommand(args, COMMAND_HELP)) {
        feedClient.printCommandUsage(true);

        System.out.println();
        System.out.println("  " + COMMAND_QUIT);
        System.out.println("    Quits shell");
      } else {
        args = append(args, shellArgs);

        FeedClient shell = new FeedClient(args);
        shell.execute(args);
      }
    }
  }

  protected static String[] parseCommandLine(String commandLine) {
    String[] args = commandLine.split(" ");
    List<String> outList = new ArrayList<String>();
    for (int i = 0; i < args.length; i++) {
      args[i] = args[i].trim();
      if (!args[i].isEmpty()) {
        outList.add(args[i]);
      }
    }
    String[] outArgs = new String[outList.size()];
    return outList.toArray(outArgs);
  }

  protected boolean isCommand(String[] args, String commandName) {
    return commandName.toLowerCase().equals(args[0].toLowerCase());
  }

  protected String[] append(String[] commandArgs, String[] shellArgs) {
    String[] args = new String[commandArgs.length + shellArgs.length];
    int i = 0;
    for (; i < commandArgs.length; i++) {
      args[i] = commandArgs[i];
    }
    for (int j = 0; j < shellArgs.length; j++) {
      args[i + j] = shellArgs[j];
    }
    return args;
  }

  @Override
  public void usage(boolean inShell) {
    System.out.println(getFeedClientCommand(inShell) + getCommandName());
    System.out.println("    Starts FSCT shell to execute multiple commands with one login");
  }
}
