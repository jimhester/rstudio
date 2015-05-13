/*
 * ApplicationContext.java
 *
 * Copyright (C) 2009-12 by RStudio, Inc.
 *
 * Unless you have received this program directly from RStudio pursuant
 * to the terms of a commercial license agreement with RStudio, then
 * this program is licensed to you under the terms of version 3 of the
 * GNU Affero General Public License. This program is distributed WITHOUT
 * ANY EXPRESS OR IMPLIED WARRANTY, INCLUDING THOSE OF NON-INFRINGEMENT,
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE. Please refer to the
 * AGPL (http://www.gnu.org/licenses/agpl-3.0.txt) for more details.
 *
 */

package org.rstudio.studio.client.application;

import org.rstudio.core.client.URIUtils;
import org.rstudio.core.client.regex.Pattern;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;

public class ApplicationContext
{
   public static String getContextId()
   {
      return Window.Location.getParameter(CTX);
   }
   
   public static String getProject()
   {
      return Window.Location.getParameter(PROJ);
   }
   
   public static String getApplicationUrl(String pathName)
   {
      String url = GWT.getHostPageBaseURL() + pathName;
      String contextId = getContextId();
      if (contextId != null)
      {
         return URIUtils.addQueryParam(url, CTX, contextId);
      }
      else
      {
         return url;
      }
   }
   
   public static String getUrlWithContext(String url)
   {
      String contextId = getContextId();
      if (contextId != null)
         return getUrlWithContext(url, contextId);
      else
         return url;
   }
   
   public static String getUrlWithContext(String url, String contextId)
   {
      // if we already have a context id then just replace it
      if (url.contains(CTX + "="))
      {
         contextId = URL.encodeQueryString(contextId);
         url = replaceCtx(url, CTX + "=" + contextId);
      }
      // otherwise add it
      else 
      {
         url = URIUtils.addQueryParam(url, CTX, contextId);
      }
      
      return url;
   }
   
   public static String getUrlWithoutContext(String url)
   {
      url = replaceCtx(url, "");
      if (url.endsWith("?"))
         return url.substring(0, url.length() - 1);
      else if (url.contains("?#"))
         return url.replace("?#", "#");
      return url;
   }
   
   private static String replaceCtx(String url, String newValue)
   {
      return Pattern.create(CTX + "=[\\w]+").replaceAll(url, newValue); 
   }
   
   
   private static final String PROJ = "proj";
   private static final String CTX = "ctx";
}
