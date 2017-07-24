package com.wojustme.mystorm.demo;

import com.wojustme.mystorm.api.ISpout;
import com.wojustme.mystorm.comp.OutPutCollector;
import com.wojustme.mystorm.comp.Tuple;

import java.util.Random;

/**
 * @author wojustme
 * @date 2017/7/19
 * @package com.wojustme.mystorm.demo
 */
public class WcSpout implements ISpout {

  private OutPutCollector outPutCollector;
  @Override
  public void init(OutPutCollector collector) {
    this.outPutCollector = collector;
  }

  @Override
  public void nextTuple() {
    String[] str = {
        "hello hello china I am",
        "ok I am fine welcome",
        "hello",
        "hello"
    };
    for (String s : str) {
      try {
        Thread.sleep(5000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      outPutCollector.emit(new Tuple("", s));
    }
  }
}
