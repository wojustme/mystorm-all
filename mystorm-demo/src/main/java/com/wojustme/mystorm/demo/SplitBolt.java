package com.wojustme.mystorm.demo;

import com.wojustme.mystorm.api.IBolt;
import com.wojustme.mystorm.comp.OutPutCollector;
import com.wojustme.mystorm.comp.Tuple;

/**
 * @author wojustme
 * @date 2017/7/19
 * @package com.wojustme.mystorm.demo
 */
public class SplitBolt implements IBolt {
  private OutPutCollector collector;

  @Override
  public void init(OutPutCollector collector) {
    this.collector = collector;
  }

  @Override
  public void execute(String data) {
    String[] split = data.split(" ");
    for (String s : split) {
      collector.emit(new Tuple(s, s));
    }
  }
}
