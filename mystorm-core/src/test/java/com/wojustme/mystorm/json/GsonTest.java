package com.wojustme.mystorm.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;

/**
 * @author wojustme
 * @date 2017/7/20
 * @package com.wojustme.mystorm.json
 */
public class GsonTest {




  public String toStr() {
    Exam exam = new Exam(87, 98);
    Student student = new Student("xurenhe", 12, Sex.MAN, exam);

    Gson gson = new GsonBuilder().create();
    String out = gson.toJson(student);
//    System.out.println(out);
    return out;
  }

  @Test
  public void toJavaObj() {
    String outSaveTmp = toStr();
    Gson gson = new GsonBuilder().create();
    Student out = gson.fromJson(outSaveTmp, Student.class);
    System.out.println(out);
    System.out.println(out.getExam().getEnglishOut());
  }
}
