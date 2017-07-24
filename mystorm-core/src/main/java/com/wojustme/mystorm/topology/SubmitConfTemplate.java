package com.wojustme.mystorm.topology;

import java.util.Properties;

/**
 * @author wojustme
 * @date 2017/7/19
 * @package com.wojustme.mystorm.topology
 */
public class SubmitConfTemplate {

  private Properties properties;

  public SubmitConfTemplate() {
    Properties properties = new Properties();
    properties.setProperty("zk.addr", "localhost:2181");
    properties.setProperty("zk.session.time", "5000");
    properties.setProperty("zk.retry.count", "3");
    properties.setProperty("zk.retry.interval.time", "2000");
    properties.setProperty("zk.root.path", "mystorm");

    this.properties = properties;
  }

  public Properties getProperties() {
    return properties;
  }
}
