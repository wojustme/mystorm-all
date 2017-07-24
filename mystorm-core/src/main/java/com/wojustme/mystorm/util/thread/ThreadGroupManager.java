package com.wojustme.mystorm.util.thread;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wojustme
 * @date 2017/7/17
 * @package com.wojustme.mystorm.util.thread
 */
public class ThreadGroupManager {

  // 线程组名称
  private String groupName;

  // 被管理的线程组
  private ThreadGroup threadGroup;

  // 监管的线程列表
  private List <Thread> threadList;

  // 初始化
  public ThreadGroupManager(String groupName) {
    this.groupName = groupName;
    threadGroup = new ThreadGroup(groupName);
    threadList = new ArrayList<>();
  }

  // 添加线程
  public void addThread(Runnable runnable) {
    Thread thread = new Thread(threadGroup, runnable);
    threadList.add(thread);
  }

}
