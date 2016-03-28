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

import java.util.List;

import org.apache.log4j.Logger;
import org.brunocvcunha.ghostme4j.model.Proxy;
import org.brunocvcunha.ghostme4j.provider.FreeProxyListProvider;
import org.brunocvcunha.ghostme4j.provider.IProxyProvider;
import org.brunocvcunha.inutils4j.MyNumberUtils;

/**
 * Ghost Me! ;-)
 * 
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class GhostMe {

  private static final Logger LOGGER = Logger.getLogger(GhostMe.class);

  private static final IProxyProvider[] PROVIDERS =
      new IProxyProvider[] {new FreeProxyListProvider()};


  /**
   * Get a Proxy
   * @param test whether to test the proxy
   * @return used proxy
   * @throws Exception
   */
  public static Proxy getProxy(boolean test) throws Exception {
    LOGGER.info("Getting proxy...");

    int providerIndex = MyNumberUtils.randomIntBetween(0, PROVIDERS.length - 1);
    IProxyProvider randomProvider = PROVIDERS[providerIndex];

    List<Proxy> proxies = randomProvider.getProxies(-1, false);
    for (Proxy proxy : proxies) {

      if (test) {
        proxy.updateStatus();
        if (!proxy.isOnline()) {
          continue;
        }
        
        proxy.updateAnonymity();
        if (!proxy.isAnonymous()) {
          continue;
        }
      }

      LOGGER.info("Using Proxy: " + proxy.toString());
      return proxy;

    }

    return null;

  }

  /**
   * Ghost the HTTP in the system properties
   * @param test whether to test the proxy
   * @return used proxy
   * @throws Exception
   */
  public static Proxy ghostMySystemProperties(boolean test) throws Exception {
    LOGGER.info("Ghosting System Properties...");

    Proxy use = getProxy(test);

    if (use != null) {
      applyProxy(use);
    }

    return use;
  }

  
  /**
   * Apply Proxy
   * @param use Proxy to use
   */
  public static void applyProxy(Proxy use) {
    System.setProperty("http.proxyHost", use.getIp());
    System.setProperty("http.proxyPort", String.valueOf(use.getPort()));
    System.setProperty("https.proxyHost", use.getIp());
    System.setProperty("https.proxyPort", String.valueOf(use.getPort()));
  }
} 
