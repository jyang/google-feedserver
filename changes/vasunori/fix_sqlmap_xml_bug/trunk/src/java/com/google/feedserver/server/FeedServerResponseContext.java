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

import org.apache.abdera.model.Base;
import org.apache.abdera.protocol.server.impl.BaseResponseContext;

import java.io.IOException;
import java.io.OutputStream;

public class FeedServerResponseContext<T extends Base>
    extends BaseResponseContext<T> {
  protected T base;

  public FeedServerResponseContext(T t) {
    super(t);
    base = t;
  }

  @Override
  public void writeTo(OutputStream out) throws IOException {
    if (hasEntity()) {
      if (writer == null) {
        base.writeTo(out);  // TODO: JSON here
      } else {
        writeTo(out, writer);  // TODO: JSON here
      }
    }
  }
}
