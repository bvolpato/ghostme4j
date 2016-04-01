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
package org.brunocvcunha.ghostme4j.model;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import org.brunocvcunha.ghostme4j.GhostMe;
import org.brunocvcunha.ghostme4j.helper.GhostMeHelper;

/**
 * Proxy Representation
 * 
 * @author Bruno Candido Volpato da Cunha
 *
 */
public class Proxy {

  private String ip;
  private int port;
  private ProxyTypeEnum type;
  private String country;
  private boolean online;
  private long latency;
  private boolean anonymous;
  private String sourceLine;

  /**
   * @return the ip
   */
  public String getIp() {
    return ip;
  }

  /**
   * @param ip the ip to set
   */
  public void setIp(String ip) {
    this.ip = ip.trim();
  }

  /**
   * @return the port
   */
  public int getPort() {
    return port;
  }

  /**
   * @param port the port to set
   */
  public void setPort(int port) {
    this.port = port;
  }

  /**
   * @return the type
   */
  public ProxyTypeEnum getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(ProxyTypeEnum type) {
    this.type = type;
  }

  /**
   * @return the country
   */
  public String getCountry() {
    return country;
  }

  /**
   * @param country the country to set
   */
  public void setCountry(String country) {
    this.country = country;
  }

  /**
   * @return the online
   */
  public boolean isOnline() {
    return online;
  }

  /**
   * @param online the online to set
   */
  public void setOnline(boolean online) {
    this.online = online;
  }

  /**
   * @return the latency
   */
  public long getLatency() {
    return latency;
  }

  /**
   * @param latency the latency to set
   */
  public void setLatency(long latency) {
    this.latency = latency;
  }

  /**
   * @return the anonymous
   */
  public boolean isAnonymous() {
    return anonymous;
  }

  /**
   * @return if blacklisted
   */
  public boolean isBlackListed() {
    return GhostMe.isBlacklisted(ip);
  }

  /**
   * @param anonymous the anonymous to set
   */
  public void setAnonymous(boolean anonymous) {
    this.anonymous = anonymous;
  }

  /**
   * @return the sourceLine
   */
  public String getSourceLine() {
    return sourceLine;
  }

  /**
   * @param sourceLine the sourceLine to set
   */
  public void setSourceLine(String sourceLine) {
    this.sourceLine = sourceLine;
  }

  /**
   * Update the proxy status
   */
  public void updateStatus() {

    String hostaddr = null;
    try {
      hostaddr = InetAddress.getByName(ip).getHostAddress();
    } catch (UnknownHostException e) {
      online = false;
      latency = Long.MAX_VALUE;
      return;
    }

    int total = 0;
    long totalPing = 0;

    // test ping 4 times
    int times = 4;

    while (total < times) {
      total++;
      long start = System.currentTimeMillis();

      SocketAddress sockaddr = new InetSocketAddress(hostaddr, port);
      try (Socket socket = new Socket()) {
        socket.connect(sockaddr, 1000);
      } catch (Exception e) {
        online = false;
        return;
      }

      totalPing += (System.currentTimeMillis() - start);
    }

    online = true;
    latency = totalPing / total;
  }


  /**
   * Update the anonymous status of the proxy
   * @throws IOException I/O error
   */
  public void updateAnonymity() throws IOException {
    ProxyBinResponse response = GhostMeHelper.getMyInformation(this.getJavaNetProxy());

    if (!response.getOrigin().equalsIgnoreCase(this.getIp())) {
      anonymous = false;
      return;
    }

    if (!response.getHeaders().get("X-Forwarded-For").equalsIgnoreCase(this.getIp())) {
      anonymous = false;
      return;
    }

    if (!response.getHeaders().get("X-Real-Ip").equalsIgnoreCase(this.getIp())) {
      anonymous = false;
      return;
    }

    anonymous = true;

  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((country == null) ? 0 : country.hashCode());
    result = prime * result + ((ip == null) ? 0 : ip.hashCode());
    result = prime * result + (int) (latency ^ (latency >>> 32));
    result = prime * result + (online ? 1231 : 1237);
    result = prime * result + port;
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Proxy other = (Proxy) obj;
    if (country == null) {
      if (other.country != null)
        return false;
    } else if (!country.equals(other.country))
      return false;
    if (ip == null) {
      if (other.ip != null)
        return false;
    } else if (!ip.equals(other.ip))
      return false;
    if (latency != other.latency)
      return false;
    if (online != other.online)
      return false;
    if (port != other.port)
      return false;
    if (type != other.type)
      return false;
    return true;
  }

  /**
   * @return java.net.Proxy for the current instance
   */
  public java.net.Proxy getJavaNetProxy() {
    java.net.Proxy.Type typeToUse;
    if (this.type == ProxyTypeEnum.SOCKS4 || this.type == ProxyTypeEnum.SOCKS5) {
      typeToUse = java.net.Proxy.Type.SOCKS;
    } else {
      typeToUse = java.net.Proxy.Type.HTTP;
    }
    
    return new java.net.Proxy(typeToUse, new InetSocketAddress(ip, port));
  }

  /* (non-Javadoc)
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return String
        .format(
            "Proxy [ip=%s, port=%s, type=%s, country=%s, online=%s, latency=%s, anonymous=%s, sourceLine=%s]",
            ip, port, type, country, online, latency, anonymous, sourceLine);
  }


}
