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
package org.openqa.selendroid.server.handler;

import org.openqa.selendroid.server.RequestHandler;
import org.openqa.selendroid.server.Response;
import org.openqa.selendroid.server.exceptions.SelendroidException;
import org.openqa.selendroid.server.model.AndroidElement;
import org.openqa.selendroid.util.SelendroidLogger;
import org.webbitserver.HttpRequest;

import com.google.gson.JsonElement;

public class SendKeys extends RequestHandler {

  public SendKeys(HttpRequest request) {
    super(request);
  }

  @Override
  public Response handle() {
    SelendroidLogger.log("send keys command");
    Long id = getElementId();
    JsonElement value = getPayload().get("value");
    if (value.isJsonNull()) {
      return new Response(getSessionId(), new SelendroidException(
          "No key to send to an element was found."));
    }
    String text = value.getAsString();

    AndroidElement element = getElementFromCache(id);
    Long start = System.currentTimeMillis();
    element.enterText(text);
    System.out.println("Send keys done in " + (System.currentTimeMillis() - start) / 1000
        + " seconds");
    return new Response(getSessionId(), "");
  }

}
