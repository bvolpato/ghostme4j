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

import com.google.gson.GsonBuilder;

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
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.brunocvcunha.ghostme4j.GhostMe;
import org.brunocvcunha.ghostme4j.helper.GhostMeHelper;
import org.brunocvcunha.ghostme4j.model.Proxy;
import org.brunocvcunha.inutils4j.MyDateUtils;
import org.brunocvcunha.inutils4j.MyHTTPUtils;
import org.brunocvcunha.inutils4j.MyStringUtils;

/**
 * freevpn.ninja provider
 * 
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class FreeVPNNinjaProvider implements IProxyProvider {

  private static final Logger LOGGER = Logger.getLogger(FreeVPNNinjaProvider.class);

  private long lastFetchTime = 0;

  private List<Proxy> lastFetchList;

  public static void main(String[] args) throws IOException {
    FreeVPNNinjaProvider provider = new FreeVPNNinjaProvider();
    List<Proxy> proxies = provider.getProxies(-1, true, false);
    
    List<Proxy> anonymous = new ArrayList<>();
    for (Proxy proxy : proxies) {
      if (proxy.isAnonymous()) {
        anonymous.add(proxy);
      }
    }

    System.out.println(anonymous.size());
    
    System.out.println(new GsonBuilder().serializeNulls().setPrettyPrinting().create().toJson(anonymous));
  }
  
  @Override
  public String getName() {
    return "freevpn.ninja";
  }

  @Override
  public List<Proxy> getProxies(final int quantity, final boolean test, final boolean useCache)
      throws IOException {

    GhostMeHelper.disableSslVerification();
    
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

    List<String> proxiesLines = MyStringUtils.asListLines(content);
    Collections.shuffle(proxiesLines);

    int threadCount = proxiesLines.size() > 20 ? 20 : proxiesLines.size();
    if (threadCount <= 0) {
      threadCount = 1;
    }
    
    final ExecutorService testService = Executors.newFixedThreadPool(threadCount);

    final CountDownLatch countDown = new CountDownLatch(proxiesLines.size());

    for (final String proxyLine : proxiesLines) {

      Thread validateTd = new Thread() {
        @Override
        public void run() {
          try {
            LOGGER.debug("Validate line: " + proxyLine);

            String hostIp = proxyLine.split("\\s")[0];
            
            Proxy proxy = new Proxy();
            proxy.setIp(hostIp.split(":")[0]);
            proxy.setPort(Integer.valueOf(hostIp.split(":")[1]));
            proxy.setCountry(proxyLine.split("\\s")[2]);
            proxy.setSourceLine(proxyLine);
            
            if (test) {
              proxy.updateStatus();
              
              try {
                proxy.updateAnonymity();
              } catch (IOException e) {
                //it's ok.
              }
            }

            LOGGER.debug("Validated: " + proxy);

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
    return "https://freevpn.ninja/free-proxy/txt";
  }

}
