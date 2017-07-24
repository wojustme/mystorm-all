package com.wojustme.mystorm.comp;

/**
 * outputcollector传输的数据封装类
 * 一个tuple就是一条数据
 * @author wojustme
 * @date 2017/7/17
 * @package com.wojustme.mystorm.comp
 */
public class Tuple {

  // 数据key值
  private String key;
  // 真实数据
  private String data;

  // 该数据含有key，用于分组
  public Tuple(String key, String data) {
    this.key = key;
    this.data = data;
  }
  // 无分区key
  public Tuple(String data) {
    this(null, data);
  }

  public String getKey() {
    return key;
  }

  public String getData() {
    return data;
  }
}
