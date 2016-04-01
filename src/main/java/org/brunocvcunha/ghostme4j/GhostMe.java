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
package org.brunocvcunha.ghostme4j;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.log4j.Logger;
import org.brunocvcunha.ghostme4j.blacklist.IBlackListProvider;
import org.brunocvcunha.ghostme4j.blacklist.SblamBlackListProvider;
import org.brunocvcunha.ghostme4j.model.Proxy;
import org.brunocvcunha.ghostme4j.provider.FreeProxyListProvider;
import org.brunocvcunha.ghostme4j.provider.GoogleProxyProvider;
import org.brunocvcunha.ghostme4j.provider.IProxyProvider;
import org.brunocvcunha.ghostme4j.provider.SSLProxiesProvider;
import org.brunocvcunha.inutils4j.MyNumberUtils;
import org.brunocvcunha.inutils4j.MyStringUtils;

/**
 * Ghost Me! ;-)
 * 
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class GhostMe {

  protected static final Logger LOGGER = Logger.getLogger(GhostMe.class);

  private static final IProxyProvider[] PROVIDERS = new IProxyProvider[] {
      new FreeProxyListProvider(), new SSLProxiesProvider(), new GoogleProxyProvider()};
  private static final IBlackListProvider[] BLACKLIST_PROVIDERS =
      new IBlackListProvider[] {new SblamBlackListProvider()};


  private static Set<String> blacklistCache;

  /**
   * Get a Proxy
   * 
   * @param test whether to test the proxy
   * @return used proxy
   * @throws IOException I/O Error
   */
  public static Proxy getProxy(final boolean test) throws IOException {
    
    int providerIndex = MyNumberUtils.randomIntBetween(0, PROVIDERS.length - 1);
    IProxyProvider randomProvider = PROVIDERS[providerIndex];
    
    return getProxy(randomProvider, test);
  }
  
  /**
   * Get a Proxy, using the given provider
   * 
   * @param provider provider to use
   * @param test whether to test the proxy
   * @return used proxy
   * @throws IOException I/O Error
   */
  public static Proxy getProxy(final IProxyProvider provider, final boolean test) throws IOException {
    LOGGER.info("Getting proxy...");

    List<Proxy> proxies = provider.getProxies(-1, false, true);
    LOGGER.info("Total Proxies: " + proxies.size());

    final CountDownLatch countDown = new CountDownLatch(proxies.size());
    final AtomicReference<Proxy> useProxy = new AtomicReference<>();
    final ExecutorService testService = Executors.newFixedThreadPool((proxies.size() > 10 ? 10 : proxies.size()));

    for (final Proxy proxy : proxies) {

      Thread validate = new Thread() {
        @Override
        public void run() {
          try {

            if (test) {
              
              if (proxy.isBlackListed()) {
                return;
              }
              
              proxy.updateStatus();
              if (!proxy.isOnline()) {
                return;
              }

              proxy.updateAnonymity();
              if (!proxy.isAnonymous()) {
                return;
              }
            }

            if (useProxy.get() == null) {
              LOGGER.info("Using Proxy: " + proxy.toString());
              useProxy.set(proxy);

              // count to 0
              while (countDown.getCount() > 0) {
                countDown.countDown();
              }

            }

          } catch (Exception e) {
            LOGGER.info("Proxy validation returned error: " + e.getMessage(), e);
          } finally {
            countDown.countDown();
          }
        }
      };
      testService.submit(validate);

    }

    try {
      countDown.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    testService.shutdownNow();

    if (useProxy.get() == null) {
      //if null, use another provider
      return getProxy(test);
    }
    
    return useProxy.get();

  }

  /**
   * Ghost the HTTP in the system properties
   * 
   * @param test whether to test the proxy
   * @return used proxy
   * @throws IOException I/O Exception
   */
  public static Proxy ghostMySystemProperties(boolean test) throws IOException {
    LOGGER.info("Ghosting System Properties...");

    Proxy use = getProxy(test);

    if (use != null) {
      applyProxy(use);
    }

    return use;
  }


  /**
   * Apply Proxy
   * 
   * @param use Proxy to use
   */
  public static void applyProxy(Proxy use) {
    System.setProperty("java.net.useSystemProxies", "false");
    System.setProperty("http.proxyHost", use.getIp());
    System.setProperty("http.proxyPort", String.valueOf(use.getPort()));
    System.setProperty("https.proxyHost", use.getIp());
    System.setProperty("https.proxyPort", String.valueOf(use.getPort()));
  }

  /**
   * Apply Random User Agent
   */
  public static void applyUserAgent() {
    applyUserAgent(getRandomUserAgent());
  }

  /**
   * Apply User Agent
   * 
   * @param agent Agent to apply
   */
  public static void applyUserAgent(String agent) {
    LOGGER.info("Applying agent: " + agent);
    System.setProperty("http.agent", agent);
    System.setProperty("https.agent", agent);
  }

  /**
   * @return a random user-agent
   */
  public static String getRandomUserAgent() {
    try {
      List<String> userAgents =
          MyStringUtils.getContentLines(GhostMe.class.getResourceAsStream("/userAgent.txt"));
      int agentIndex = MyNumberUtils.randomIntBetween(0, userAgents.size() - 1);
      return userAgents.get(agentIndex);
    } catch (IOException e) {
      LOGGER.error("Error to get user agents", e);
    }

    return "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.87 Safari/537.36";
  }


  /**
   * Check if the IP is in a blacklist 
   * @param ip IP to check
   * @return whether the ip is blacklisted or not
   */
  public synchronized static boolean isBlacklisted(String ip) {
    if (blacklistCache == null || blacklistCache.isEmpty()) {
      blacklistCache = new HashSet<>();

      for (IBlackListProvider provider : BLACKLIST_PROVIDERS) {
        try {
          blacklistCache.addAll(provider.getIPs());
        } catch (IOException e) {
          LOGGER.warn("Error fetching blacklist records", e);
        }
      }

    }
    
    return blacklistCache.contains(ip);
  }
}
