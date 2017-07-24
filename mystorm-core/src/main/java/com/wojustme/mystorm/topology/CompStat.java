package com.wojustme.mystorm.topology;

import com.wojustme.mystorm.comp.ITask;
import com.wojustme.mystorm.comp.TaskType;

import java.util.List;

/**
 * 每一个组件状态属性
 * @author wojustme
 * @date 2017/7/18
 * @package com.wojustme.mystorm.topology
 */
public class CompStat<T extends ITask> {

  // 该组件名称
  private String compName;
  // 组件类模板
  private Class<T> compCls;
  // 组件执行的策略
  private Strategy strategy;
  // 下游组件
  private List<CompStat> nextCompList;
  //
  private TaskType compType;

  public CompStat(String compName, Class<T> compCls, Strategy strategy, TaskType compType) {
    this.compName = compName;
    this.compCls = compCls;
    this.strategy = strategy;
    this.compType = compType;
  }

  public CompStat(String compName, Class<T> compCls, TaskType compType) {
    this(compName, compCls, Strategy.RANDOM, compType);
  }

  public void setStrategy(Strategy strategy) {
    this.strategy = strategy;
  }

  public void setNextCompClsList(List<CompStat> nextCompList) {
    this.nextCompList = nextCompList;
  }

  public String getCompName() {
    return compName;
  }

  public List<CompStat> getNextCompList() {
    return nextCompList;
  }

  public Strategy getStrategy() {
    return strategy;
  }

  public Class<T> getCompCls() {
    return compCls;
  }

  public TaskType getCompType() {
    return compType;
  }

  @Override
  public String toString() {
    return "CompStat{" +
        "compName='" + compName + '\'' +
        ", compCls=" + compCls +
        ", nextCompList=" + nextCompList +
        ", strategy=" + strategy +
        '}';
  }
}
