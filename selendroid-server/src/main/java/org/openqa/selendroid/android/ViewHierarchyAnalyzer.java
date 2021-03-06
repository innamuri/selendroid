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
package org.openqa.selendroid.android;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selendroid.ServerInstrumentation;
import org.openqa.selendroid.server.exceptions.SelendroidException;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

public class ViewHierarchyAnalyzer {
  private static final ViewHierarchyAnalyzer INSTANCE = new ViewHierarchyAnalyzer();

  public static ViewHierarchyAnalyzer getDefaultInstance() {
    return INSTANCE;
  }

  public Set<View> _getTopLevelViews() {
    // Set<View> top=new HashSet<View>();
    // top.add(ServerInstrumentation.getInstance().getRootView());
    // return top;
    try {
      Class<?> wmClass = Class.forName("android.view.WindowManagerImpl");
      Object wm = wmClass.getDeclaredMethod("getDefault").invoke(null);
      Field views = wmClass.getDeclaredField("mViews");
      views.setAccessible(true);
      synchronized (wm) {
        return new HashSet<View>(Arrays.asList(((View[]) views.get(wm)).clone()));
      }
    } catch (Exception exception) {
      throw new SelendroidException("Selendroid only supports Android 2.2", exception);
    }
  }

  public Set<View> getTopLevelViews() {
    Class<?> windowManager = null;
    try {
      String windowManagerClassName;
      if (android.os.Build.VERSION.SDK_INT >= 17) {
        windowManagerClassName = "android.view.WindowManagerGlobal";
      } else {
        windowManagerClassName = "android.view.WindowManagerImpl";
      }
      windowManager = Class.forName(windowManagerClassName);

    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    } catch (SecurityException e) {
      e.printStackTrace();
    }
    Field views;
    Field instanceField;
    try {
      views = windowManager.getDeclaredField("mViews");
      instanceField = windowManager.getDeclaredField(getWindowManagerString());
      views.setAccessible(true);
      instanceField.setAccessible(true);
      Object instance = instanceField.get(null);
      synchronized (windowManager) {
        return new HashSet<View>(Arrays.asList(((View[]) views.get(instance))));
      }
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  private String getWindowManagerString() {
    if (android.os.Build.VERSION.SDK_INT >= 17) {
      return "sDefaultWindowManager";
    } else if (android.os.Build.VERSION.SDK_INT >= 13) {
      return "sWindowManager";
    } else {
      return "mWindowManager";
    }
  }

  public View getRecentDecorView() {
    return getRecentDecorView(getTopLevelViews());
  }

  private View getRecentDecorView(Set<View> views) {
    Collection<View> decorViews = Collections2.filter(views, new DecorViewPredicate());
    View container = null;
    long drawingTime = 0;
    for (View view : decorViews) {
      if (view.isShown() && view.hasWindowFocus() && view.getDrawingTime() > drawingTime) {
        container = view;
        drawingTime = view.getDrawingTime();
      }
    }
    return container;
  }

  private static class DecorViewPredicate implements Predicate<View> {
    @Override
    public boolean apply(View view) {
      return "DecorView".equals(view.getClass().getSimpleName());
    }
  }

  public Collection<View> getViews() {
    View rootView = getRecentDecorView();
    final List<View> views = new ArrayList<View>();
    addAllChilren((ViewGroup) rootView, views);

    return views;
  }

  private void addAllChilren(ViewGroup viewGroup, List<View> list) {
    if (viewGroup.getChildCount() == 0) {
      return;
    }
    for (int i = 0; i < viewGroup.getChildCount(); i++) {
      View childView = viewGroup.getChildAt(i);
      if (childView.isShown()) {
        list.add(childView);
      }
      if (childView instanceof ViewGroup) {
        addAllChilren((ViewGroup) childView, list);
      }
    }
  }

  public static String getNativeId(View view) {
    String id = "";
    try {
      id =
          ServerInstrumentation.getInstance().getCurrentActivity().getResources()
              .getResourceName(view.getId());
      // remove the package name
      id = id.split(":")[1];
    } catch (Resources.NotFoundException e) {
      // can happen
    }
    return id;
  }

  public WebView findWebView() {
    final List<WebView> webViews = new ArrayList<WebView>();
    Collection<View> views = getViews();
    for (View view : views) {
      if (view instanceof WebView) {
        final WebView webview = (WebView) view;
        webViews.add(webview);
      }
    }
    if (webViews.isEmpty()) {
      return null;
    }
    return webViews.get(0);
  }
}
