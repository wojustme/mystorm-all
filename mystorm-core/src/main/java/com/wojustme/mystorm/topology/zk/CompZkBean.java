package com.wojustme.mystorm.topology.zk;

import com.wojustme.mystorm.comp.ITask;
import com.wojustme.mystorm.comp.TaskType;
import com.wojustme.mystorm.topology.Strategy;

import java.util.List;

/**
 * 组件存储zk的数据格式
 * @author wojustme
 * @date 2017/7/18
 * @package com.wojustme.mystorm.topology.zk
 */
public class CompZkBean {

  // 组件名
  private String compName;
  // 执行类
  private String compClsStr;
  // 该组件类型
  private TaskType compType;
  // 下游组件名称
  private List<String> nextCompNameList;
  // 数据流向策略
  private Strategy strategy;

  public CompZkBean(String compName, Class<? extends ITask> compCls, TaskType compType, List<String> nextCompNameList, Strategy strategy) {
    this.compName = compName;
    this.compClsStr = compCls.getName();
    this.compType = compType;
    this.nextCompNameList = nextCompNameList;
    this.strategy = strategy;
  }

  public String getCompName() {
    return compName;
  }

  public String getCompClsStr() {
    return compClsStr;
  }

  public TaskType getCompType() {
    return compType;
  }

  public List<String> getNextCompNameList() {
    return nextCompNameList;
  }


  public Strategy getStrategy() {
    return strategy;
  }
}
