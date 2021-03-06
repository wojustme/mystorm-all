package com.wojustme.mystorm.master;

import com.wojustme.mystorm.observer.EventBean;
import com.wojustme.mystorm.observer.Subject;

/**
 * master节点相关的主题
 * 单例
 * @author wojustme
 * @date 2017/7/12
 * @package com.wojustme.mystorm.master
 */
public class MasterSubject extends Subject {

  // 私有化构造函数
  private MasterSubject() {}

  // 单例变量
  private static volatile MasterSubject instance;

  // 获得单例变量
  public static MasterSubject getIstance() {
    // 对象实例化时与否判断（不使用同步代码块，instance不等于null时，直接返回对象，提高运行效率）
    if (instance == null) {
      // 同步代码块（对象未初始化时，使用同步代码块，保证多线程访问时对象在第一次创建后，不再重复被创建）
      synchronized (MasterSubject.class) {
        // 未初始化，则初始instance变量
        if (instance == null) {
          instance = new MasterSubject();
        }
      }
    }
    return instance;
  }


  // 可能多线程通知，则需要加锁
  @Override
  public synchronized void nodifyObservers(EventBean newEvent) {
    //状态发生改变，通知各个观察者
    super.nodifyObservers(newEvent);
  }

}
