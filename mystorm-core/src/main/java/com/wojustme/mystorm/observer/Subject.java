package com.wojustme.mystorm.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * 订阅的主题
 * @author wojustme
 * @date 2017/7/12
 * @package com.wojustme.mystorm.observer
 */
public abstract class Subject {
  private List<Observer> list = new ArrayList<>();

  // 添加观察者
  public void addObserver(Observer observer){
    list.add(observer);
  }
  // 删除观察者
  public void removeObserver(Observer observer){
    list.remove(observer);
  }

  // 通知观察者新事件
  public void nodifyObservers(EventBean newEvent) {
    for(Observer observer : list){
      observer.update(newEvent);
    }
  }
}
