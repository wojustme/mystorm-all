package com.wojustme.mystorm.topology;

import com.wojustme.mystorm.comp.IBoltComp;
import com.wojustme.mystorm.comp.ISpoutComp;
import com.wojustme.mystorm.comp.ITask;
import com.wojustme.mystorm.comp.TaskType;

import java.util.*;

/**
 * 应用topology的构建
 * @author wojustme
 * @date 2017/7/18
 * @package com.wojustme.mystorm.topology
 */
public class TopologyBuilder {

  // topology名
  private String topologyName;

  // 该组件数据执行哪种策略
  private Map<String, Strategy> strategyMap;
  // 该组件下一个组件，即数据流动
  private Map<String, Set<String>> nextTaskMap;


  // spout组件集合，map的value值代表该组件实例化个数
  private Map<String, ISpoutComp> spoutCompTypeMap;
  // 通过名称索引spout组件
  private Map<String, Integer> spoutInstanceCountMap;

  // bolt组件集合，value含义同上
  private Map<String, IBoltComp> boltCompTypeMap;
  // 通过名称索引bolt组件
  private Map<String, Integer> boltInstanceCountMap;


  public TopologyBuilder() {
    this(null);
  }

  public TopologyBuilder(String topologyName) {
    this.topologyName = topologyName;

    this.strategyMap = new HashMap<>();
    this.nextTaskMap = new HashMap<>();
    this.spoutCompTypeMap = new HashMap<>();
    this.spoutInstanceCountMap = new HashMap<>();
    this.boltCompTypeMap = new HashMap<>();
    this.boltInstanceCountMap = new HashMap<>();
  }

  public String getTopologyName() {
    return topologyName;
  }

  public void setTopologyName(String topologyName) {
    this.topologyName = topologyName;
  }

  // 设置spout组件
  public void setSpout(String spoutName, ISpoutComp spout, int count) {
    if (spoutCompTypeMap.containsKey(spoutName) || boltCompTypeMap.containsKey(spoutName)) {
      throw new RuntimeException("重复定义" + spoutName);
    }
    if (spoutInstanceCountMap.containsKey(spout)) {
      throw new RuntimeException("重复定义" + spout);
    }
    spoutCompTypeMap.put(spoutName, spout);
    spoutInstanceCountMap.put(spoutName, count);
  }
  public void setSpout(String spoutName, ISpoutComp spout) {
    setSpout(spoutName, spout, 1);
  }

  // 设置bolt组件
  public void setBolt(String boltName, IBoltComp bolt, int count) {
    if (boltCompTypeMap.containsKey(boltName) || spoutCompTypeMap.containsKey(boltName)) {
      throw new RuntimeException("重复定义" + boltName);
    }
    if (boltInstanceCountMap.containsKey(bolt)) {
      throw new RuntimeException("重复定义" + bolt);
    }
    boltCompTypeMap.put(boltName, bolt);
    boltInstanceCountMap.put(boltName, count);
  }
  public void setBolt(String spoutName, IBoltComp bolt) {
    setBolt(spoutName, bolt, 1);
  }


  // 设置数据的流向
  public void setDataFlow(String currentTask, Strategy strategy, String nextTask) {
    if (strategyMap.get(currentTask) == null) {
      // 未创建
      Set<String> taskSet = new HashSet<>();
      taskSet.add(nextTask);
      strategyMap.put(currentTask, strategy);
      nextTaskMap.put(currentTask, taskSet);
    } else if (strategyMap.get(currentTask) == strategy) {
      Set<String> taskSet = nextTaskMap.get(currentTask);
      if (taskSet == null) {
        taskSet = new HashSet<>();
      }
      if (taskSet.contains(nextTask)) {
        // 设置过该任务
        throw new RuntimeException("设置过该任务");
      }
      taskSet.add(nextTask);
    } else {
      // 错误
    }
  }

  public TopologyBean build() {

    TopologyStat topologyStat = new TopologyStat();

    for (Map.Entry<String, ISpoutComp> entry : spoutCompTypeMap.entrySet()) {
      // 设置的spout名称
      String spoutName = entry.getKey();
      // 该spout拥有实例个数
      int num = spoutInstanceCountMap.get(spoutName);
      // 该spout类构造模型
      Class<? extends ISpoutComp> spoutCls = entry.getValue().getClass();
      topologyStat.addComp(spoutName, num, spoutCls, TaskType.SPOUT);
    }
    for (Map.Entry<String, IBoltComp> entry : boltCompTypeMap.entrySet()) {
      // 设置的spout名称
      String boltName = entry.getKey();
      // 该spout拥有实例个数
      int num = boltInstanceCountMap.get(boltName);
      // 该spout类构造模型
      Class<? extends IBoltComp> boltCls = entry.getValue().getClass();
      topologyStat.addComp(boltName, num, boltCls, TaskType.BOLT);
    }

    topologyStat.initTable(topologyName);
    topologyStat.fillTable(strategyMap, nextTaskMap);

    List<CompStat<ITask>> allComps = topologyStat.getAllComps();

    return new TopologyBean(topologyName, allComps);
  }

  @Override
  public String toString() {
    return "TopologyBuilder{" +
        "topologyName='" + topologyName + '\'' +
        ", strategyMap=" + strategyMap +
        ", nextTaskMap=" + nextTaskMap +
        ", spoutCompTypeMap=" + spoutCompTypeMap +
        ", spoutInstanceCountMap=" + spoutInstanceCountMap +
        ", boltCompTypeMap=" + boltCompTypeMap +
        ", boltInstanceCountMap=" + boltInstanceCountMap +
        '}';
  }
}

