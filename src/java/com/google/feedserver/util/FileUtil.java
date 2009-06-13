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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

public class FileUtil {

  /**
   * Reads contents of specified file into a String.
   *
   * @param filePath Path to file to read.
   * @return string with file contents.
   * @throws IOException if any file operations fail.
   */
  public String readFileContents(String filePath) throws IOException {
    return readFileContents(new File(filePath));
  }


  /**
   * Reads contents of specified file into a String.
   *
   * @param file File to read.
   * @return string with file contents.
   * @throws IOException if any file operations fail.
   */
  public String readFileContents(File file) throws IOException {
    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
    byte[] fileContents = new byte[(int) file.length()];
    bis.read(fileContents);
    return new String(fileContents);
  }

  /**
   * Writes contents to a file to overwrite it
   * @param filePath Path to file to overwrite
   * @param contents Contents of file to write
   * @throws IOException
   */
  public void writeFileContents(String filePath, String contents) throws IOException {
    writeFileContents(new File(filePath), contents);
  }

  /**
   * Writes contents to a file to overwrite it
   * @param file File to overwrite
   * @param contents Contents of file to write
   * @throws IOException
   */
  public void writeFileContents(File file, String contents) throws IOException {
    FileWriter writer = new FileWriter(file);
    writer.write(contents);
  }

  /**
   * Tests whether a file or directory exists
   * @param path Path to file or directory
   * @return <code>true</code> if file or directory exists; <code>false</code> otherwise
   */
  public boolean exists(String path) {
    return new File(path).exists();
  }

  /**
   * Deletes a file or directory
   * @param path Path to file or directory
   * @return <code>true</code> if successful; <code>false</code> otherwise
   */
  public boolean delete(String path) {
    return new File(path).delete();
  }
}
