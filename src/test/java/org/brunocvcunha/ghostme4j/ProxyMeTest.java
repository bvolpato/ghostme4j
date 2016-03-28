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

import static org.junit.Assert.*;

import org.brunocvcunha.ghostme4j.helper.GhostMeHelper;
import org.brunocvcunha.ghostme4j.model.Proxy;
import org.brunocvcunha.ghostme4j.model.ProxyBinResponse;
import org.junit.Test;

/**
 * Proxy Me Test
 * 
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class ProxyMeTest {

  /**
   * Test Proxy Application
   * 
   * @throws Exception
   */
  @Test
  public void testProxySystem() throws Exception {

    Proxy used = GhostMe.ghostMySystemProperties(true);

    ProxyBinResponse response = GhostMeHelper.getMyInformation();
    assertEquals(used.getIp().trim(), response.getOrigin().trim());
    assertEquals(used.getIp().trim(), response.getHeaders().get("X-Forwarded-For"));
    assertEquals(used.getIp().trim(), response.getHeaders().get("X-Real-Ip"));
  }
  
  /**
   * Test Proxy in Connection
   * 
   * @throws Exception
   */
  @Test
  public void testProxyConnection() throws Exception {

    Proxy used = GhostMe.getProxy(true);
    
    ProxyBinResponse response = GhostMeHelper.getMyInformation(used.getJavaNetProxy());
    assertEquals(used.getIp().trim(), response.getOrigin().trim());
    assertEquals(used.getIp().trim(), response.getHeaders().get("X-Forwarded-For"));
    assertEquals(used.getIp().trim(), response.getHeaders().get("X-Real-Ip"));
  }
  
  
  /**
   * Test function to get random user agents
   */
  @Test
  public void testUserAgent() {
    String agent = GhostMe.getRandomUserAgent();
    System.out.println("Agent is: " + agent);
    
    assertNotNull(agent);
    assertTrue(agent.contains("Mozilla/5.0"));
  }

}
