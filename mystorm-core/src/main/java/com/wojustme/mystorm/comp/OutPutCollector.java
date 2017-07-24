package com.wojustme.mystorm.comp;

/**
 * 数据发送器
 * @author wojustme
 * @date 2017/7/17
 * @package com.wojustme.mystorm.comp
 */
public interface OutPutCollector {

  // 用于发射数据
  void emit(Tuple tuple);
}
