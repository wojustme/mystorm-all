package com.wojustme.mystorm.schedule;

import com.wojustme.mystorm.comp.TaskType;
import com.wojustme.mystorm.topology.Strategy;

import java.util.ArrayList;
import java.util.List;

/**
 * 安排任务的bean
 * 用于写入zk中
 * @author wojustme
 * @date 2017/7/21
 * @package com.wojustme.mystorm.schedule
 */
public class AssiginTaskZkBean {

  // 任务名
  private String taskName;
  // 该执行jar包名
  private String jarFileName;
  // 执行类
  private String compClsStr;
  // 类型
  private TaskType taskType;
  // 数据流动策略
  private Strategy strategy;
  // 下游网络节点信息
  private List<DownstreamNetNode> downstreamNetNodeList;


  public AssiginTaskZkBean(String taskName, String jarFileName, String compClsStr, TaskType taskType, Strategy strategy) {
    this.taskName = taskName;
    this.jarFileName = jarFileName;
    this.compClsStr = compClsStr;
    this.taskType = taskType;
    this.strategy = strategy;
    this.downstreamNetNodeList = new ArrayList<>();
  }

  public void addNetNode(DownstreamNetNode node) {
    downstreamNetNodeList.add(node);
  }

  public List<DownstreamNetNode> getDownstreamNetNodeList() {
    return downstreamNetNodeList;
  }

  public String getJarFileName() {
    return jarFileName;
  }

  public String getTaskName() {
    return taskName;
  }

  public String getCompClsStr() {
    return compClsStr;
  }

  public TaskType getTaskType() {
    return taskType;
  }

  public Strategy getStrategy() {
    return strategy;
  }

  @Override
  public String toString() {
    return "AssiginTaskZkBean{" +
        "taskName='" + taskName + '\'' +
        ", jarFileName='" + jarFileName + '\'' +
        ", compClsStr='" + compClsStr + '\'' +
        ", taskType=" + taskType +
        ", downstreamNetNodeList=" + downstreamNetNodeList +
        '}';
  }
}

