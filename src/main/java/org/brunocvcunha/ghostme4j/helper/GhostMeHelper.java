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

import org.brunocvcunha.inutils4j.MyStringUtils;

/**
 * Ghost Me Helper
 * 
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class GhostMeHelper {

  /**
   * @return my ip
   */
  public static String getMyIp() {
    String ip = MyStringUtils.getContent("http://ipinfo.io/ip");
    return ip;
  }
}
