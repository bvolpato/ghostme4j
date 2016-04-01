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
package org.brunocvcunha.ghostme4j.blacklist;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.brunocvcunha.ghostme4j.GhostMe;
import org.brunocvcunha.ghostme4j.helper.GhostMeHelper;
import org.junit.Test;

/**
 * Sblam Black list
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class SFSBlackListTest {

  /**
   * Test for fetch in the list
   * @throws IOException 
   */
  @Test
  public void testFetch() throws IOException {
    IBlackListProvider provider = new SFSBlackListProvider();
    
    List<String> ips = provider.getIPs();
    System.out.println(ips);
    assertFalse(ips.isEmpty());
  }

  
}
