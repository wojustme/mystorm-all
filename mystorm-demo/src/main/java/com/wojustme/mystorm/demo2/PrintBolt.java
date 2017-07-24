package com.wojustme.mystorm.demo2;

import com.wojustme.mystorm.api.IBolt;
import com.wojustme.mystorm.comp.OutPutCollector;

/**
 * @author wojustme
 * @date 2017/7/23
 * @package com.wojustme.mystorm.demo2
 */
public class PrintBolt implements IBolt {

  @Override
  public void init(OutPutCollector collector) {

  }

  @Override
  public void execute(String data) {
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
    System.out.println("PrintBolt组件收到数据:" + data);
    System.out.println("+++++++++++++++++++++++++++++++++++++++++++");
  }
}
