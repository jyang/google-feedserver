/**
 * Copyright 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.feedserver.server;

import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.Target;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.impl.RegexTargetResolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

public class FeedServerTargetResolver extends RegexTargetResolver {
  public FeedServerTargetResolver() {
    setPattern("/" + "([^/#?]+)", TargetType.TYPE_COLLECTION);
    setPattern("/" + "([^/#?]+)/([^/#?]+)", TargetType.TYPE_ENTRY);
  }

  @Override
  protected Target getTarget(
      TargetType type,
      RequestContext request,
      Matcher matcher) {
    return new FeedServerTarget(type, request, matcher);
  }

  private static final class FeedServerTarget extends RegexTarget {

    protected FeedServerTarget(
        TargetType type,
        RequestContext context,
        Matcher matcher) {
      super(type, context, matcher);
    }

    @Override
    public String getParameter(String name) {
      TargetType type = getType();

      if (type.equals(TargetType.TYPE_COLLECTION) ||
          type.equals(TargetType.TYPE_ENTRY)) {
        if (name.equalsIgnoreCase(FeedServerProvider.PARAM_FEED)) {
          return matcher.group(1);
        }
      }

      if (type.equals(TargetType.TYPE_ENTRY)) {
        if (name.equalsIgnoreCase(FeedServerProvider.PARAM_ENTRY)) {
          return matcher.group(2);
        }
      }

      return super.getParameter(name);
    }

    @Override
    public String[] getParameterNames() {
      List<String> paramNames = new ArrayList<String>();
      paramNames.addAll(Arrays.asList(super.getParameterNames()));
      TargetType type = getType();
      if (type.equals(TargetType.TYPE_COLLECTION) ||
          type.equals(TargetType.TYPE_ENTRY)) {
        paramNames.add(FeedServerProvider.PARAM_FEED);
      }

      if (type.equals(TargetType.TYPE_ENTRY)) {
        paramNames.add(FeedServerProvider.PARAM_ENTRY);
      }

      return paramNames.toArray(new String[0]);
    }
  }
}