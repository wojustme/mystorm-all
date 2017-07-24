package com.wojustme.mystorm.demo;

import com.wojustme.mystorm.api.IBolt;
import com.wojustme.mystorm.comp.OutPutCollector;
import com.wojustme.mystorm.comp.Tuple;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wojustme
 * @date 2017/7/19
 * @package com.wojustme.mystorm.demo
 */
public class CountBolt implements IBolt {
  private OutPutCollector collector;
  private Map<String, Integer> wordCountMap = new HashMap<>();
  @Override
  public void execute(String data) {
    Integer num = wordCountMap.get(data);
    if (num == null || num == 0) {
      wordCountMap.put(data, 1);
    } else {
      wordCountMap.put(data, num + 1);
    }

    for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
      collector.emit(new Tuple("", entry.getKey() + ":" + entry.getValue()));
    }
  }

  @Override
  public void init(OutPutCollector collector) {
    this.collector = collector;
  }
}
