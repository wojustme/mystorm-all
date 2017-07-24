package com.wojustme.mystorm.network;

/**
 * 网络数据封装
 * @author wojustme
 * @date 2017/7/23
 * @package com.wojustme.mystorm.network
 */
public class NetworkData {

  // 任务名
  private String taskName;
  // 真实数据
  private String data;

  public NetworkData(String taskName, String data) {
    this.taskName = taskName;
    this.data = data;
  }

  public String getTaskName() {
    return taskName;
  }

  public void setTaskName(String taskName) {
    this.taskName = taskName;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }
}
