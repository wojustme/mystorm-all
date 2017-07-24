package com.wojustme.mystorm.json;

/**
 * @author wojustme
 * @date 2017/7/20
 * @package com.wojustme.mystorm.json
 */
public class Exam {

  private int mathOut;
  private int EnglishOut;

  public int getMathOut() {
    return mathOut;
  }

  public void setMathOut(int mathOut) {
    this.mathOut = mathOut;
  }

  public int getEnglishOut() {
    return EnglishOut;
  }

  public void setEnglishOut(int englishOut) {
    EnglishOut = englishOut;
  }

  public Exam() {
  }

  public Exam(int mathOut, int englishOut) {
    this.mathOut = mathOut;
    EnglishOut = englishOut;
  }


  @Override
  public String toString() {
    return "Exam{" +
        "mathOut=" + mathOut +
        ", EnglishOut=" + EnglishOut +
        '}';
  }
}
