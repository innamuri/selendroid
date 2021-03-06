/*
 * Copyright 2012 selendroid committers.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.openqa.selendroid.server.inspector;

import org.openqa.selendroid.ServerInstrumentation;
import org.openqa.selendroid.server.model.KnownElements;
import org.openqa.selendroid.server.model.NativeSearchScope;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

public class SelendroidInspectorServlet implements HttpHandler {
  private NativeSearchScope scope = new NativeSearchScope(ServerInstrumentation.getInstance(),
      new KnownElements());

  @Override
  public void handleHttpRequest(HttpRequest httpRequest, HttpResponse httpResponse,
      HttpControl httpControl) throws Exception {
    if (!"GET".equals(httpRequest.method())) {
      httpResponse.status(500);
      httpResponse.end();
      return;
    }

    // httpResponse.header("Content-Type", "text/plain");
    httpResponse.content(buildHtml());
    httpResponse.end();
  }

  private String buildHtml() {
    StringBuilder html = new StringBuilder();
    html.append("<html><head><title>Selendroid inspector</title></head>");
    html.append("<body><h1>Selendroid Inspector</h1><hr/>");

    html.append("</body></html>");
    return html.toString();
  }

}
