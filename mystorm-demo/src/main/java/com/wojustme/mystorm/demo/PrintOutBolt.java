package com.wojustme.mystorm.demo;

import com.wojustme.mystorm.api.IBolt;
import com.wojustme.mystorm.comp.OutPutCollector;
import com.wojustme.mystorm.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wojustme
 * @date 2017/7/19
 * @package com.wojustme.mystorm.demo
 */
public class PrintOutBolt implements IBolt {

  private Map<String, Integer> outMap = new HashMap<>();

  private OutPutCollector collector;
  @Override
  public void execute(String data) {

    String[] split = data.split(":");
    String key = split[0];
    int val = Integer.valueOf(split[1]);
    outMap.put(key, val);

    System.out.println("++++++++++统计结果+++++++++");
    System.out.println(outMap);
    System.out.println("++++++++++++++++++++++++++");
  }

  @Override
  public void init(OutPutCollector collector) {
    this.collector = collector;
  }
}
