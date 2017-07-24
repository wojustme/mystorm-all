package com.wojustme.mystorm.util;

import java.util.Random;

/**
 * @author wojustme
 * @date 2017/7/23
 * @package com.wojustme.mystorm.util
 */
public final class KeyHashUtil {

  // 利用Java自带的string中hashcode方法
  public static int computeHashKey(String keyStr, int hashNum) {
    return  Math.abs(keyStr.hashCode()) % hashNum;
  }

  // 随机
  public static int randomKey(int num) {
    Random random = new Random();
    return random.nextInt(num);
  }

}
