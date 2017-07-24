package com.wojustme.mystorm.topology.zk;

/**
 * 当前组件运行在那个节点上
 * @author wojustme
 * @date 2017/7/20
 * @package com.wojustme.mystorm.topology.zk
 */
public class RunNodeBean {

  private String workerName;
  private String executorName;

  public String getWorkerName() {
    return workerName;
  }

  public void setWorkerName(String workerName) {
    this.workerName = workerName;
  }

  public String getExecutorName() {
    return executorName;
  }

  public void setExecutorName(String executorName) {
    this.executorName = executorName;
  }



}
