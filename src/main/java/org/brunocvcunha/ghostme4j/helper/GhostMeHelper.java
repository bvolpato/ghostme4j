/**
 * Copyright (C) 2016 Bruno Candido Volpato da Cunha (brunocvcunha@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.brunocvcunha.ghostme4j.helper;

import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.Proxy;
import java.util.LinkedHashMap;

import org.brunocvcunha.ghostme4j.model.ProxyBinResponse;
import org.brunocvcunha.inutils4j.MyHTTPUtils;

/**
 * Ghost Me Helper
 * 
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class GhostMeHelper {

  /**
   * @return my ip info
   * @throws IOException I/O Error
   */
  public static ProxyBinResponse getMyInformation() throws IOException {
    return getMyInformation(null);
  }
  
  /**
   * @return my ip info
   * @throws IOException I/O Error
   */
  public static ProxyBinResponse getMyInformation(Proxy proxyToUse) throws IOException {
    String ipInfo = MyHTTPUtils.getContent("http://httpbin.org/get?show_env=1", new LinkedHashMap<String, String>(), proxyToUse);

    return new GsonBuilder().create().fromJson(ipInfo, ProxyBinResponse.class);
  }
}
