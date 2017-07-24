package com.wojustme.mystorm.slave.stat;


import java.util.ArrayList;
import java.util.List;

/**
 * 正在运行线程状态
 * 1、线程名; 2、线程类型(spout、bolt); 3、线程申请任务总量; 4、运行任务数量; 5、运行任务清单
 * @author wojustme
 * @date 2017/7/19
 * @package com.wojustme.mystorm.slave.stat
 */
public class ActiveBoltExecutorStat {

  // bolt线程名
  private String executorName;
  // bolt线程申请任务总量
  private int allTaskNum;
  // 运行bolt任务数量
  private int activeTaskNum;
  // 运行bolt任务清单
  private List<String> taskList;


  public ActiveBoltExecutorStat(String executorName, int allTaskNum, int activeTaskNum) {
    this.executorName = executorName;
    this.allTaskNum = allTaskNum;
    this.activeTaskNum = activeTaskNum;
    this.taskList = new ArrayList<>();
  }

  public String getExecutorName() {
    return executorName;
  }

  public void setExecutorName(String executorName) {
    this.executorName = executorName;
  }

  public int getAllTaskNum() {
    return allTaskNum;
  }

  public void setAllTaskNum(int allTaskNum) {
    this.allTaskNum = allTaskNum;
  }

  public int getActiveTaskNum() {
    return activeTaskNum;
  }

  public void setActiveTaskNum(int activeTaskNum) {
    this.activeTaskNum = activeTaskNum;
  }

  public List<String> getTaskList() {
    return taskList;
  }

  public void setTaskList(List<String> taskList) {
    this.taskList = taskList;
  }

  public void addTask(String taskName) {
    if (taskList == null) {
      taskList = new ArrayList<>();
    }
    taskList.add(taskName);
  }

  @Override
  public String toString() {
    return "ActiveBoltExecutorStat{" +
        "executorName='" + executorName + '\'' +
        ", allTaskNum=" + allTaskNum +
        ", activeTaskNum=" + activeTaskNum +
        ", taskList=" + taskList +
        '}';
  }
}
