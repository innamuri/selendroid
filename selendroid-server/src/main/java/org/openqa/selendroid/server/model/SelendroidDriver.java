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
package org.openqa.selendroid.server.model;

import java.util.List;

import org.openqa.selendroid.android.WindowType;
import org.openqa.selendroid.server.Session;

import com.google.gson.JsonObject;

public interface SelendroidDriver {

  public AndroidElement findElement(By by);

  public List<AndroidElement> findElements(By by);

  public String getCurrentUrl();

  public Session getSession();

  public JsonObject getSessionCapabilities(String sessionId);

  public Object getWindowSource();

  public String initializeSession(JsonObject desiredCapabilities);

  public void stopSession();

  public void switchWindow(WindowType type);

  public byte[] takeScreenshot();

  public Keyboard getKeyboard();

  public String getTitle();

  public void get(String url);
}
