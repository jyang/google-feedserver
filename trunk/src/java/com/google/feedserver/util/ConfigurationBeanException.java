/* Copyright 2008 Google Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.google.feedserver.util;

/**
 * Represents an error in configuration of the Secure Link Client.
 * 
 * @author rayc@google.com (Ray Colline)
 */
public class ConfigurationBeanException extends Exception {

  /**
   * Creates the exception with the specified error message.
   * 
   * @param message the error message.
   */
  public ConfigurationBeanException(String message) {
    super(message);
  }
  
  /**
   * Creates the exception with the specified underlying cause.
   * 
   * @param cause the underlying cause of this exception.
   */
  public ConfigurationBeanException(Throwable cause) {
    super(cause);
  }
  
  /**
   * Creates the exception with the specified error message and underlying
   * cause.
   * 
   * @param message the error message.
   * @param cause the underlying cause of this exception.
   */
  public ConfigurationBeanException(String message, Throwable cause) {
    super(message, cause);
  }
}
