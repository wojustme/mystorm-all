package com.wojustme.mystorm.topology;

import com.wojustme.mystorm.comp.OutPutCollector;
import com.wojustme.mystorm.api.ISpout;

/**
 * @author wojustme
 * @date 2017/7/18
 * @package com.wojustme.mystorm.topology
 */
public class MqSpout implements ISpout {

  private String name;

  public MqSpout(String name) {
    this.name = name;
  }

  @Override
  public void nextTuple() {

  }

  @Override
  public void init(OutPutCollector collector) {

  }

  @Override
  public String toString() {
    return "MqSpout{" +
        "name='" + name + '\'' +
        '}';
  }
}
