package com.wojustme.mystorm.hash;

import com.wojustme.mystorm.util.KeyHashUtil;
import org.junit.Test;

/**
 * @author wojustme
 * @date 2017/7/23
 * @package com.wojustme.mystorm.hash
 */
public class KeyHashTest {


  @Test
  public void calcHash() {
    String s1 = "hh";
    String s2 = "hello";
    String s3 = "hello";
    String s4 = "hello123";

    int hashNum = 4;
    System.out.println(KeyHashUtil.computeHashKey(s1, hashNum));
    System.out.println(KeyHashUtil.computeHashKey(s2, hashNum));
    System.out.println(KeyHashUtil.computeHashKey(s3, hashNum));
    System.out.println(KeyHashUtil.computeHashKey(s4, hashNum));
  }

  @Test
  public void calcRandom() {
    while (true) {
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println(KeyHashUtil.randomKey(1));
    }
  }

}
