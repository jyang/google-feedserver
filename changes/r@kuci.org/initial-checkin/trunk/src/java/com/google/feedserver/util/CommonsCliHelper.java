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
package com.google.feedserver.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CommonsCliHelper {
  
  @SuppressWarnings("unchecked")
  private List<Class> classes;
  private CommandLine flags;
  private Options options;
  
  @SuppressWarnings("unchecked")
  public CommonsCliHelper() {
    classes = new ArrayList<Class>();
  }
  
  @SuppressWarnings("unchecked")
  public void register(Class flagClass) {
    classes.add(flagClass); 
  }
  
  public void parse(String[] args) {
    options = createOptions();
    GnuParser parser = new GnuParser();
    try {
      flags = parser.parse(options, args);
    } catch (ParseException e) {
      usage();
      throw new RuntimeException(e);
    }
    
    /*
     * Print help text and exit.
     */
    if (flags.hasOption("help")) {
      usage();
      System.exit(0);
    }
    populateClasses();
  }
  
  public void usage() {
    new HelpFormatter().printHelp("Usage", options);
  }
  
  /**
   * Loop through each registered class and parse the command line for their flags.  If
   * option isnt specified we leave the default.
   */
  @SuppressWarnings("unchecked")
  private void populateClasses() {
    for (Class flagClass : classes) {
      for (Field field : flagClass.getFields()) {
        if (field.getName().endsWith("_FLAG")) {
          String argName = field.getName().substring(
              0, field.getName().length() - "_FLAG".length());
          String helpText = getHelpText(flagClass, argName);
          if (field.getType().getName().equals(Boolean.class.getName())) {
            boolean newValue;
            if (flags.hasOption(argName)) {
              setField(field, new Boolean(true));
            } else if (flags.hasOption("no" + argName)) {
              setField(field, new Boolean(false));
            }
          } else {
            String argValue = flags.getOptionValue(argName, null);
            if (argValue != null) {
              setField(field, argValue);
            }
          }
        }
      }
    }
  }

  /**
   * Sets value in the supplied field to the given value.
   * 
   * @param field the flag field.
   * @param value the value, usually a Boolean or a String.
   * @throws RuntimeException if the field is mis-configured.
   */
  private void setField(Field field, Object value) {
    try {
      field.set(null, value);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Field " + field.getName() + " must be a String", e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Field " + field.getName() + " must be public", e);
    } catch (NullPointerException e) {
      throw new RuntimeException("Field " + field.getName() + " must be static");
    }
  }
  
  /**
   * For each class registered, we extract options based on the flags set within the class.
   * 
   *  - Any class field ending in {prefix}_FLAG is turned into "--{prefix}" on the command line. 
   *   eg.  "public String adminEmail_FLAG" becomes "--adminEmail"
   *  - Any field defined with {prefix}_HELP will be used as the help text.
   * 
   * @return Options all commandline options registered for parsing.
   */
  @SuppressWarnings("unchecked")
  private Options createOptions() {
    
    Options options = new Options();
    options.addOption(new Option("help", false, "Print out usage."));
    for (Class flagClass : classes) {
      for (Field field : flagClass.getFields()) {
        if (field.getName().endsWith("_FLAG")) {
          String argName = field.getName().substring(
              0, field.getName().length() - "_FLAG".length());
          String helpText = getHelpText(flagClass, argName);
          if (field.getType().getName().equals(Boolean.class.getName())) {
            options.addOption(new Option(argName, false, helpText));
            options.addOption(new Option("no" + argName, true, helpText));
          } else {
            options.addOption(new Option(argName, true, helpText));
          }
        }
      }
    }
    return options;
  }
    
    
  /**
   * Returns help text for the given field and class. 
   * 
   * @returns String the help text.
   */
  @SuppressWarnings("unchecked")
  private String getHelpText(Class flagClass, String argumentName) {
    String helpText = "None Available";
    try {
      helpText = (String) flagClass.getField(argumentName + "_HELP").get(null);
    } catch (SecurityException e) {
      throw new RuntimeException(e);
    } catch (NoSuchFieldException e) {
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Help text must be of type String.", e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Help text must be public!", e);
    }
    return helpText;
  }
}
