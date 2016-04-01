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

import java.io.IOException;
import java.util.List;

import org.brunocvcunha.ghostme4j.helper.GhostMeHelper;
import org.brunocvcunha.inutils4j.MyHTTPUtils;
import org.brunocvcunha.inutils4j.MyStringUtils;

/**
 * Stop Forum Spam Blacklist
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class SFSBlackListProvider implements IBlackListProvider {

  @Override
  public String getName() {
    return "sfs";
  }

  @Override
  public List<String> getIPs() throws IOException {
    GhostMeHelper.disableSslVerification();
    String ips = MyHTTPUtils.getContent("https://cdn.content-network.net/nbl/sfs-30.txt");
    
    //replace comment lines
    ips = ips.replaceAll("#[^\n]*\r?\n", "");
    
    ips = ips.replace("deny ", "").trim();
    ips = ips.replace(";", "").trim();
    
    return MyStringUtils.asListLines(ips);
  }

}
