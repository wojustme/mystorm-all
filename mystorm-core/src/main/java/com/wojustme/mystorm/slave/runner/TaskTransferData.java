package com.wojustme.mystorm.slave.runner;


/**
 * 主线程与工作线程通信数据封装
 * @author wojustme
 * @date 2017/7/23
 * @package com.wojustme.mystorm.slave.runner
 */
public class TaskTransferData {
  // 数据阻塞数据最大量
  public static final int length = 1000;

  // 任务名
  private String taskName;
  // 输入的数据
  private String dataVal;

  public TaskTransferData(String taskName, String dataVal) {
    this.taskName = taskName;
    this.dataVal = dataVal;
  }

  public String getTaskName() {
    return taskName;
  }

  public void setTaskName(String taskName) {
    this.taskName = taskName;
  }

  public String getDataVal() {
    return dataVal;
  }

  public void setDataVal(String dataVal) {
    this.dataVal = dataVal;
  }
}
