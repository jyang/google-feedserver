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

package com.google.feedserver.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The utility class for file based feed config stores
 * 
 * @author rakeshs101981@gmail.com (Rakesh Shete)
 */
public class FileSystemConfigStoreUtil extends ConfigStoreUtil {

  private static final Logger logger = Logger.getLogger(FileSystemConfigStoreUtil.class.getName());
  /**
   * The character that indicates that the string value following it is a
   * filename
   */
  public static String FILE_INDICATOR = "@";

  private FileSystemConfigStoreUtil() {
  }

  /**
   * Checks if the given string is a file path. It checks if the string starts
   * with the file indicator {@link FileSystemConfigStoreUtil#FILE_INDICATOR}
   * indiating it is a file path
   * 
   * @param stringToCheck The string to check
   * @return True if if the string starts with the file indicator
   *         {@link FileSystemConfigStoreUtil#FILE_INDICATOR} indiating it is a
   *         file path and false otherwise
   */
  public static boolean checkIfStringIsFilePath(String stringToCheck) {
    return stringToCheck.startsWith(FileSystemConfigStoreUtil.FILE_INDICATOR);
  }

  /**
   * Removes the {@link FileSystemConfigStoreUtil#FILE_INDICATOR} from the
   * string, splits the comma separated list of filenames and returns them as an
   * array of filename
   * 
   * @return The array of individual filenames
   */
  public static String[] getListOfFileNames(String stringOfFileNames) {
    stringOfFileNames = stringOfFileNames.replace(FileSystemConfigStoreUtil.FILE_INDICATOR, "");
    String[] fileNames = stringOfFileNames.split(",");
    return fileNames;
  }

  /**
   * Constructs and returns the input stream for the given filepath using the
   * classloader for the current thread or the system classloader
   * 
   * @param filePath The filepath
   * @return The input stream
   * @throws IOException If any IO exceptions are encountered
   */
  public static InputStream getInputStream(String filePath) throws IOException {
    try {
      InputStream reader =
          Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
      if (reader == null) {
        reader = new FileInputStream(filePath);
      }
      return reader;
    } catch (IOException e) {
      logger
          .log(Level.SEVERE, "Problems encountered while reading file contents " + e.getMessage());
      throw e;
    }
  }

  /**
   * Reads the contents of the given file and returns the entire content as a
   * string
   * 
   * @param filePath The path of the file
   * @return The content as string
   * @throws IOException If exceptions are encountered while reading the file
   *         contents
   */
  public static String getFileContents(String filePath) throws IOException {
    StringBuffer fileData = new StringBuffer(1000);
    InputStream reader = null;
    try {
      reader = getInputStream(filePath);
      byte[] buf = new byte[1024];
      int numRead = 0;
      while ((numRead = reader.read(buf)) != -1) {
        String readData = new String(buf);
        fileData.append(readData);
        buf = new byte[1024];
      }
    } catch (IOException e) {
      logger
          .log(Level.SEVERE, "Problems encountered while reading file contents " + e.getMessage());
      throw e;
    } finally {
      if (reader != null)
        try {
          reader.close();
        } catch (IOException e) {
          logger.log(Level.WARNING, "Problems encountered while closing the file handle "
              + e.getMessage());
        }
    }

    return fileData.toString().trim();
  }

  /**
   * Constructs and returns the input stream for the given filepath using the
   * classloader for the current thread or the system classloader
   * 
   * @param filePath The filepath
   * @return The input stream
   * @throws IOException If any IO exceptions are encountered
   */
  public static InputStream getInputStreamForFile(File filePath) throws IOException {
    return getInputStream(filePath.getPath());
  }

}
