package com.wojustme.mystorm.topology;

/**
 * @author wojustme
 * @date 2017/7/18
 * @package com.wojustme.mystorm.topology
 */
public interface ISerializeTopolgy {
  byte[] encode();
  byte[] decode(byte[] bytes);

}
