package com.wojustme.mystorm.json;

/**
 * @author wojustme
 * @date 2017/7/20
 * @package com.wojustme.mystorm.json
 */
public class Student {
  private String name;
  private int age;
  private Sex sex;
  private Exam exam;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public Sex getSex() {
    return sex;
  }

  public void setSex(Sex sex) {
    this.sex = sex;
  }

  public Exam getExam() {
    return exam;
  }

  public void setExam(Exam exam) {
    this.exam = exam;
  }

  public Student() {
  }

  public Student(String name, int age, Sex sex, Exam exam) {
    this.name = name;
    this.age = age;
    this.sex = sex;
    this.exam = exam;
  }


  @Override
  public String toString() {
    return "Student{" +
        "name='" + name + '\'' +
        ", age=" + age +
        ", sex=" + sex +
        ", exam=" + exam +
        '}';
  }
}
