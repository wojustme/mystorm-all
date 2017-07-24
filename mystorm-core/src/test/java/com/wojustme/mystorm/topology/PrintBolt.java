package com.wojustme.mystorm.topology;

import com.wojustme.mystorm.api.IBolt;
import com.wojustme.mystorm.comp.OutPutCollector;
import com.wojustme.mystorm.comp.Tuple;

/**
 * @author wojustme
 * @date 2017/7/18
 * @package com.wojustme.mystorm.topology
 */
public class PrintBolt implements IBolt {

  private String name;

  public PrintBolt(String name) {
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
    return "PrintBolt{" +
        "name='" + name + '\'' +
        '}';
  }
}
