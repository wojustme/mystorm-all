package com.wojustme.mystorm.topology;

import com.wojustme.mystorm.api.ISpout;
import com.wojustme.mystorm.comp.OutPutCollector;

/**
 * @author wojustme
 * @date 2017/7/18
 * @package com.wojustme.mystorm.topology
 */
public class DbSpout implements ISpout {

  private String name;

  public DbSpout(String name) {
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
    return "DbSpout{" +
        "name='" + name + '\'' +
        '}';
  }
}
