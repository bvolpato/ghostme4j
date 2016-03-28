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
package org.brunocvcunha.ghostme4j.provider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.brunocvcunha.ghostme4j.GhostMe;
import org.brunocvcunha.ghostme4j.model.Proxy;
import org.brunocvcunha.inutils4j.MyHTTPUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * free-proxy-list.net provider
 * 
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class FreeProxyListProvider implements IProxyProvider {

  private static final Logger LOGGER = Logger.getLogger(FreeProxyListProvider.class);

  @Override
  public String getName() {
    return "free-proxy-list.net";
  }

  @Override
  public List<Proxy> getProxies(int quantity, boolean test) throws IOException {
    GhostMe.applyUserAgent();
    
    List<Proxy> proxies = new ArrayList<>();

    String url = "http://free-proxy-list.net/";


    LOGGER.info("Fetching URL: " + url);

    String content = MyHTTPUtils.getContent(url);

    Document doc = Jsoup.parse(content);
    Elements table = doc.select("table#proxylisttable");

    Elements proxyLine = table.select("tbody > tr");

    for (Element proxyElement : proxyLine) {
      LOGGER.info("Parsing line: " + proxyElement.text());

      Elements proxyTd = proxyElement.children();

      Proxy proxy = new Proxy();
      proxy.setIp(proxyTd.get(0).text());
      proxy.setPort(Integer.valueOf(proxyTd.get(1).text()));
      proxy.setCountry(proxyTd.get(2).text());

      if (test) {
        proxy.updateStatus();
      }

      proxies.add(proxy);

      if (quantity > 0 && proxies.size() >= quantity) {
        break;
      }
    }

    return proxies;
  }

}
