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
package com.google.feedserver.util;

import com.google.feedserver.client.FeedServerEntry;
import com.google.gdata.data.OtherContent;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.xml.sax.SAXException;

import java.beans.IntrospectionException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

/**
 * Uses reflection to scan registered beans for any annotation containing
 * {@link Flag} or {@link ConfigFile}. It uses the "help" entry in the Flag
 * decorator for flag help. In addition if you specify @ConfigFile with @Flag,
 * this flag will be used to read an XML file from the filesystem containing a
 * payload-in-content entry for this bean. It will read the file first then
 * reads the flags. Flags supersede file content. With this class you can create
 * configuration files using payload-in-content files and have them represented
 * as Beans for consumption in code.
 * <p>
 * <br/> Boolean, Integer and String fields are supported.
 * 
 * <br/> example: to setup --filename as a flag. in class Foo with flag help.
 * </p>
 * <p>
 * <blockquote>
 * 
 * <pre>
 * 
 * public class FooBean {
 *    {@literal @}Flag(help = &quot;config file for foobean&quot;)
 *    {@literal @}ConfigFile 
 *    private String configFileName = &quot;/etc/configfile&quot;;
 *    {@literal @}Flag(help = &quot;filename to access&quot;) 
 *    private String filename = &quot;/tmp/defaultfile&quot;;
 *    public getFilename() { 
 *       return filename; 
 *    }
 *    
 *    public setFilename(String filename) { 
 *      this.filename = filename; 
 *    }
 * }
 * 
 * public class Foo {
 *      public static void main(String[] args) { 
 *          FooBean fooBean = new FooBean(); 
 *          BeanCliHelper cliHelper = new BeanCliHelper();
 *          cliHelper.register(fooBean); cliHelper.parse(args);
 *          System.stdout.println(&quot;Filename is &quot; + fooBean.getFilename()); 
 *      } 
 * }
 * 
 * </pre>
 * 
 * </blockquote>
 * </p>
 * 
 * Note: classes with identical flag field names will get overwritten with the
 * last one processed. TODO(rayc) support collision detection and use fully
 * qualified class name for flag option.
 * 
 * @author r@kuci.org (Ray Colline)
 * 
 */
public class BeanCliHelper {

  // We allow tests to set the file contents to avoid going to disk
  private static String testFileContents;

  private List<Object> beans;
  private CommandLine flags;
  private Options options;

  public BeanCliHelper() {
    beans = new ArrayList<Object>();
  }

  public void register(Object bean) {
    beans.add(bean);
  }

  /**
   * With provided command line string, populates all registered classes with
   * decorated fields with their config file then command-line values.
   * 
   * @param args command-line.
   */
  public void parse(String[] args) throws ConfigurationBeanException {
    options = createOptions();
    GnuParser parser = new GnuParser();
    try {
      flags = parser.parse(options, args);
    } catch (ParseException e) {
      usage();
      throw new ConfigurationBeanException("Command line parse error.", e);
    }

    /*
     * Print help text and exit.
     */
    if (flags.hasOption("help")) {
      usage();
      System.exit(0);
    }
    populateBeansFromConfigFile();
    populateBeansFromCommandLine();
  }

  /**
   * Prints usage information.
   */
  public void usage() {
    new HelpFormatter().printHelp("Usage", options);
  }

  /**
   * Must be called after {@link GnuParser#parse(Options, String[])} and if bean
   * has {@link ConfigFile} decorator load the file that field points to and
   * populate bean. If no decorator is present we just no-op. If there is not a
   * default location for the config file in the bean, or they did not specify
   * the flag on the commandline, we no-op and assume they want to configure
   * entirely from defaults or commandline.
   */
  private void populateBeansFromConfigFile() throws ConfigurationBeanException {

    populateBeansFromCommandLine(); // we do an initial pass to pick up
    // ConfigFile flag

    // We go through all registered beans.
    for (Object bean : beans) {
      // Go through each field to find annotated fields. We only support one
      // ConfigFile per
      // bean. The first one encountered here will be used.
      for (Field field : bean.getClass().getDeclaredFields()) {
        ConfigFile configFileAnnotation = field.getAnnotation(ConfigFile.class);
        String configFileName = "";
        if (configFileAnnotation == null) {
          // no config file in this bean, we no-op.
          continue;
        }

        try {
          // retrieve the field value
          configFileName =
              (String) bean.getClass().getMethod(
                  "get" + field.getName().substring(0, 1).toUpperCase()
                      + field.getName().substring(1), (Class[]) null).invoke(bean, (Object[]) null);
          if (configFileName == null) {
            return; // There is no config file default or flag entry, we no-op.
          }

          // load the file into a string.
          String configFileContents = readFileIntoString(configFileName);

          // parse the contents into the bean.
          FeedServerEntry configEntry = new FeedServerEntry(configFileContents);
          ContentUtil contentUtil = new ContentUtil();
          contentUtil.fillBean((OtherContent) configEntry.getContent(), bean);

          // BeanUtil throws many exceptions, re-wrap them and throw our
          // exception.
        } catch (RuntimeException e) {
          throw new ConfigurationBeanException(e);
        } catch (IllegalAccessException e) {
          throw new ConfigurationBeanException(e);
        } catch (InvocationTargetException e) {
          throw new ConfigurationBeanException(e);
        } catch (NoSuchMethodException e) {
          throw new ConfigurationBeanException(e);
        } catch (IOException e) {
          throw new ConfigurationBeanException("Error reading config file " + configFileName, e);
        } catch (IntrospectionException e) {
          throw new ConfigurationBeanException(e);
        } catch (SAXException e) {
          throw new ConfigurationBeanException(e);
        } catch (ParserConfigurationException e) {
          throw new ConfigurationBeanException(e);
        }
      }
    }
  }

  /**
   * Loop through each registered class and create and parse command line
   * options for fields decorated with {@link Flag}.
   */
  private void populateBeansFromCommandLine() {

    // Go through all registered beans.
    for (Object bean : beans) {

      // Search for all fields in the bean with Flag decorator.
      for (Field field : bean.getClass().getDeclaredFields()) {
        Flag flag = field.getAnnotation(Flag.class);
        if (flag == null) {
          // not decorated, continue.
          continue;
        }
        String argName = field.getName();
        // Boolean Flags
        if (field.getType().getName().equals(Boolean.class.getName())
            || field.getType().getName().equals(Boolean.TYPE.getName())) {
          if (flags.hasOption(argName)) {
            setField(field, bean, new Boolean(true));
          } else if (flags.hasOption("no" + argName)) {
            setField(field, bean, new Boolean(false));
          }
          // Integer Flags
        } else if (field.getType().getName().equals(Integer.class.getName())
            || field.getType().getName().equals(Integer.TYPE.getName())) {
          String argValue = flags.getOptionValue(argName, null);
          if (argValue != null) {
            try {
              setField(field, bean, Integer.valueOf(argValue));
            } catch (NumberFormatException e) {
              throw new RuntimeException(e);
            }
          }
          // String Flag
        } else if (field.getType().getName().equals(String.class.getName())) {
          String argValue = flags.getOptionValue(argName, null);
          if (argValue != null) {
            setField(field, bean, argValue);
          }
          // Repeated String Flag
        } else if (field.getType().getName().equals(String[].class.getName())) {
          String[] argValues = flags.getOptionValues(argName);
          if (argValues != null) {
            setField(field, bean, argValues);
          }
        }
      }
    }
  }

  /**
   * Sets value in the supplied field's setter to the given value.
   * 
   * @param field the flag field.
   * @param value the value, usually a Boolean or a String.
   * @throws RuntimeException if the field is mis-configured.
   */
  private void setField(Field field, Object bean, Object value) {
    try {
      bean.getClass().getMethod(
          "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1),
          field.getType()).invoke(bean, value);
      // Lots of errors can happen when using introspection. Most are
      // programming errors
      // so we throw RuntimeExceptions.
    } catch (IllegalArgumentException e) {
      throw new RuntimeException("Field error:" + field.getName(), e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Field error:" + field.getName(), e);
    } catch (NullPointerException e) {
      throw new RuntimeException("Field error:" + field.getName(), e);
    } catch (SecurityException e) {
      throw new RuntimeException("Field error:" + field.getName(), e);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Field error:" + field.getName(), e);
    } catch (InvocationTargetException e) {
      throw new RuntimeException("Field error:" + field.getName(), e);
    }
  }

  /**
   * For each class registered, we extract options based on the {@link Flag}
   * decorator set on each field in the bean. We use the help argument on the
   * decorator to set command line option usage text.
   * 
   * Note: classes with identical flag field names will get overwritten with the
   * last one processed. TODO(rayc) support collision detection and use fully
   * qualified class name for flag option.
   * 
   * see {@link Flag} for more info.
   * 
   * @return Options all command-line options registered for parsing.
   */
  private Options createOptions() {

    Options options = new Options();
    options.addOption(new Option("help", false, "Print out usage."));

    // Go through all registered beans.
    for (Object bean : beans) {

      // Go through all fields.
      for (Field field : bean.getClass().getDeclaredFields()) {
        Flag flag = field.getAnnotation(Flag.class);
        if (flag == null) {
          continue; // no decorator we move on.
        }

        // Check type we only support boolean, String and Integer.
        if ((field.getType() != Integer.class) && (field.getType() != Integer.TYPE)
            && (field.getType() != String.class) && (field.getType() != Boolean.class)
            && (field.getType() != Boolean.TYPE)) {
          throw new RuntimeException("Field: " + field.getName() + " flag type not supported");
        }

        // Create options.
        String argName = field.getName();
        if (field.getType().getName().equals(Boolean.class.getName())
            || field.getType().getName().equals(Boolean.TYPE.getName())) {
          options.addOption(new Option(argName, false, flag.help()));
          options.addOption(new Option("no" + argName, false, flag.help()));
        } else {
          options.addOption(new Option(argName, true, flag.help()));
        }
      }
    }
    return options;
  }

  /**
   * Helper that loads a file into a string.
   * 
   * @param fileName properties file with rules configuration.
   * @returns a String representing the file.
   * @throws IOException if any errors are encountered reading the properties
   *         file
   */
  static String readFileIntoString(final String fileName) throws IOException {
    if (testFileContents != null) {
      return testFileContents;
    }
    File file = new File(fileName);
    byte[] fileContents = new byte[(int) file.length()];
    new BufferedInputStream(new FileInputStream(fileName)).read(fileContents);
    return new String(fileContents);
  }

  /**
   * We use this to set the config file for unit tests.
   * 
   * @param contents a test payload-in-content file represented as a string.
   */
  static void setTestFileContents(String contents) {
    testFileContents = contents;
  }
}
