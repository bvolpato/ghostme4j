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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.brunocvcunha.ghostme4j.GhostMe;
import org.brunocvcunha.ghostme4j.model.Proxy;
import org.brunocvcunha.inutils4j.MyDateUtils;
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

  private long lastFetchTime = 0;

  private List<Proxy> lastFetchList;


  @Override
  public String getName() {
    return "www.free-proxy-list.net";
  }

  @Override
  public List<Proxy> getProxies(final int quantity, final boolean test, final boolean useCache)
      throws IOException {

    // 5 min
    if (useCache && lastFetchList != null
        && lastFetchTime > (System.currentTimeMillis() - (MyDateUtils.MINUTE_MILLIS * 5))) {
      LOGGER.info("Fetch from cache.");
      return lastFetchList;
    }

    final List<Proxy> proxies = new CopyOnWriteArrayList<>();

    String url = getUrl();


    LOGGER.info("Fetching Proxy URL: " + url);

    Map<String, String> headers = new LinkedHashMap<>();
    headers.put("User-Agent", GhostMe.getRandomUserAgent());

    // "http://httpbin.org/get?show_env=1"
    String content = MyHTTPUtils.getContent(url, headers);

    LOGGER.debug("Content: " + content);
    Document doc = Jsoup.parse(content);
    Elements table = doc.select("table#proxylisttable");

    Elements proxyLine = table.select("tbody > tr");

    // shuffle
    List<Elements> elementList = new ArrayList<>();
    for (Element proxyElement : proxyLine) {
      Elements proxyTd = proxyElement.children();
      elementList.add(proxyTd);
    }
    Collections.shuffle(elementList);

    final ExecutorService testService = Executors.newFixedThreadPool((elementList.size() > 10 ? 10 : elementList.size()));

    final CountDownLatch countDown = new CountDownLatch(elementList.size());

    for (final Elements proxyTd : elementList) {

      Thread validateTd = new Thread() {
        @Override
        public void run() {
          try {
            LOGGER.debug("Validate line: " + proxyTd.text());
            
            if (proxyTd.get(4).text().equalsIgnoreCase("transparent")) {
              // transparent is not enough to ghost :)
              return;
            }
            
            Proxy proxy = new Proxy();
            proxy.setIp(proxyTd.get(0).text());
            proxy.setPort(Integer.valueOf(proxyTd.get(1).text()));
            proxy.setCountry(proxyTd.get(2).text());
            proxy.setSourceLine(proxyTd.text());
            
            if (test) {
              proxy.updateStatus();
            }

            proxies.add(proxy);

            if (quantity > 0 && proxies.size() >= quantity) {
              //countdown to 0, don't need to wait anymore
              while (countDown.getCount() > 0) {
                countDown.countDown();
              }
              
            }
          } finally {
            countDown.countDown();
          }
        }
      };
      testService.submit(validateTd);
      
    }

    try {
      countDown.await();
      testService.shutdownNow();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    lastFetchList = proxies;
    lastFetchTime = System.currentTimeMillis();

    return proxies;
  }

  /**
   * @return the url to fetch
   */
  protected String getUrl() {
    return "http://www.free-proxy-list.net/";
  }

}
