package com.wojustme.mystorm.comp;

/**
 * 封装ip和port
 * @author wojustme
 * @date 2017/7/23
 * @package com.wojustme.mystorm.comp
 */
public class ServerHost {
  private String host;
  private int port;

  public ServerHost(String host, int port) {
    this.host = host;
    this.port = port;
  }

  @Override
  public int hashCode() {
    return host.hashCode() + port;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ServerHost) {
      ServerHost otherObj = (ServerHost) obj;
      if (otherObj.hashCode() == this.hashCode()) {
        if (otherObj.host.equals(this.host) && otherObj.port == this.port) {
          return true;
        }
      }
    }
    return false;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  @Override
  public String toString() {
    return "ServerHost{" +
        "host='" + host + '\'' +
        ", port=" + port +
        '}';
  }
}
