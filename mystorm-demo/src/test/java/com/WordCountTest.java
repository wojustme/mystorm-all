package com;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author wojustme
 * @date 2017/7/24
 * @package com
 */
public class WordCountTest {

  String[] str = {
      "Do you understand the feeling of missing someone It is just like that you will spend a long hard time to turn the ice-cold water you have drunk into tears",
      "Life isn't about waiting for the storm to pass it's about learning to dance in the rain"
  };

  @Test
  public void countWord() {
    Map<String, Integer> rs = new HashMap<>();
    for (String s : str) {
      String[] split = s.split(" ");
      for (String word : split) {
        if (rs.get(word) == null || rs.get(word) == 0) {
          rs.put(word, 1);
        } else {
          rs.put(word, rs.get(word) + 1);
        }
      }
    }
    for (Map.Entry<String, Integer> entry : rs.entrySet()) {
      System.out.println(entry.getKey() + "->" + entry.getValue());
    }

  }

  @Test
  public void printNum() {
    Integer i = new Integer(1);
    int a = i;
    System.out.println(i == 1);
  }
}
