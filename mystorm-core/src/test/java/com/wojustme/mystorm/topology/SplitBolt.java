package com.wojustme.mystorm.topology;

import com.wojustme.mystorm.api.IBolt;
import com.wojustme.mystorm.comp.OutPutCollector;
import com.wojustme.mystorm.comp.Tuple;

/**
 * @author wojustme
 * @date 2017/7/18
 * @package com.wojustme.mystorm.topology
 */
public class SplitBolt implements IBolt {

  private String name;

  public SplitBolt(String name) {
    this.name = name;
  }

  @Override
  public void execute(String data) {

  }

  @Override
  public void init(OutPutCollector collector) {

  }


  @Override
  public String toString() {
    return "SplitBolt{" +
        "name='" + name + '\'' +
        '}';
  }
}
