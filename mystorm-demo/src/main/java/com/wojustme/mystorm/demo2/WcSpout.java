package com.wojustme.mystorm.demo2;

import com.wojustme.mystorm.api.ISpout;
import com.wojustme.mystorm.comp.OutPutCollector;
import com.wojustme.mystorm.comp.Tuple;

import java.util.Random;

/**
 * @author wojustme
 * @date 2017/7/23
 * @package com.wojustme.mystorm.demo2
 */
public class WcSpout implements ISpout {
  private OutPutCollector outPutCollector;
  @Override
  public void init(OutPutCollector collector) {
    this.outPutCollector = collector;
  }

  @Override
  public void nextTuple() {
    while (true) {
      try {
        Thread.sleep(10000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      int i = new Random().nextInt() % 6;
      outPutCollector.emit(new Tuple("hello", "hello" + i));
    }
  }
}
