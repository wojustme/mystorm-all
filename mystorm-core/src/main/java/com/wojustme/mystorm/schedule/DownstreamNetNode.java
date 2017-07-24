package com.wojustme.mystorm.schedule;

/**
 * @author wojustme
 * @date 2017/7/22
 * @package com.wojustme.mystorm.schedule
 */
public class DownstreamNetNode {

  private String host;
  private int port;
  private String taskName;

  public DownstreamNetNode(String host, int port, String taskName) {
    this.host = host;
    this.port = port;
    this.taskName = taskName;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getTaskName() {
    return taskName;
  }

  @Override
  public String toString() {
    return "DownstreamNetNode{" +
        "host='" + host + '\'' +
        ", port=" + port +
        ", taskName='" + taskName + '\'' +
        '}';
  }
}
