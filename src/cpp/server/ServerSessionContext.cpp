/*
 * ServerSessionContext.cpp
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

// TODO: Save Viewer as Web page open after has no ctx
// TODO: Help href and src replacements need to add ctx


#include <server/ServerSessionContext.hpp>

#include <core/Error.hpp>

#include <core/http/URL.hpp>
#include <core/http/Util.hpp>
#include <core/http/Response.hpp>

using namespace rstudio::core;

namespace rstudio {
namespace server {

bool sessionContextForRequest(
      boost::shared_ptr<core::http::AsyncConnection> ptrConnection,
      const std::string& username,
      SessionContext* pSessionContext)
{
   // look for the context designator either in the URL parameter
   // or in the HTTP referrer
   std::string id = ptrConnection->request().queryParamValue("ctx");
   if (id.empty())
   {
      http::URL refererURL(ptrConnection->request().headerValue("Referer"));
      std::string baseURL, queryParams;
      refererURL.split(&baseURL, &queryParams);
      http::Fields queryFields;
      http::util::parseQueryString(queryParams, &queryFields);
      id = http::util::fieldValue(queryFields, "ctx");
   }

   // if we don't have a ctx then this is an error
   if (!id.empty())
   {
      *pSessionContext = SessionContext(username, id);
      return true;
   }
   else
   {
      // build message
      std::string msg = "No context id provided for " +
                        ptrConnection->request().uri();

      // log as an error
      LOG_ERROR_MESSAGE(msg);

      // write error response
      ptrConnection->response().setError(http::status::BadGateway, msg);
      ptrConnection->writeResponse();
      return false;
   }
}

void handleContextInitRequest(const json::JsonRpcRequest& request,
                              json::JsonRpcResponse* pResponse)
{
   pResponse->setField("ep", "false");
   pResponse->setResult("42");
}

} // namespace server
} // namespace rstudio

