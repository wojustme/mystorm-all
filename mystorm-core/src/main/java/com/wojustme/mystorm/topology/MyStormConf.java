package com.wojustme.mystorm.topology;

/**
 * @author wojustme
 * @date 2017/7/19
 * @package com.wojustme.mystorm.topology
 */
public class MyStormConf {


  private String zkConf = "localhost:2181";


  public String getZkConf() {
    return zkConf;
  }

  public void setZkConf(String zkConf) {
    this.zkConf = zkConf;
  }
}
