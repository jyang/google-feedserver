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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that the decorated field be interpreted as a command-line-flag.
 * The name of the decorated field becomes the option name. eg. 
 * 
 *   @Flag(help = "filename to access")
 *   private String filename = "/tmp/defaultfile";
 * 
 * becomes --filename a String option with help "filename to access".
 * 
 * @author rayc@google.com (Ray Colline)
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Flag {
  String help() default "None";
}
