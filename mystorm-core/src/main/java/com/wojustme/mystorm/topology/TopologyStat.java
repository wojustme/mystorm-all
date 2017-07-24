package com.wojustme.mystorm.topology;

import com.wojustme.mystorm.comp.TaskType;
import com.wojustme.mystorm.comp.ITask;

import java.util.*;

/**
 * 该topology真实状态类
 * 使用真实的实例来确定topology状态
 * @author wojustme
 * @date 2017/7/18
 * @package com.wojustme.mystorm.topology
 */
public class TopologyStat {

  private Map<String, Integer> compCountMap;
  private Map<String, Class<? extends ITask>> compClsMap;
  private Map<String, List<CompStat<ITask>>> compStatMap;
  private Map<String, TaskType> compTypeMap;


  public TopologyStat() {
    compCountMap = new HashMap<>();
    compClsMap = new HashMap<>();
    compStatMap = new HashMap<>();
    compTypeMap = new HashMap<>();
  }

  public void addComp(String compName, int compCount, Class<? extends ITask> compCls, TaskType taskType) {
    compCountMap.put(compName, compCount);
    compClsMap.put(compName, compCls);
    compTypeMap.put(compName, taskType);
  }

  // 初始化任务表格
  // 形如
  //            compName         |        compCls      |     stragey     |   nextCompClsList
  //  该组件名(组件名-实例名)           该组件实例化的类模板     该组件运行的策略         下游组件
  // 初始化组件名和类模板，初始任务策略为随机策略
  public void initTable(String topologyName) {
    for (Map.Entry<String, Class<? extends ITask>> compCls : compClsMap.entrySet()) {
      String name = compCls.getKey();
      Class<? extends ITask> taskCls = compCls.getValue();
      int instanceNum = compCountMap.get(name);

      if (!compStatMap.containsKey(name)) {
        compStatMap.put(name, new ArrayList<>());
      }
      List<CompStat<ITask>> compStats = compStatMap.get(name);
      if (compStats == null) {
        compStats = new ArrayList<>();
      }


      for (int i = 0; i < instanceNum; i++) {
//        String compName = topologyName + "-" + name + "-instance" + i;
        String compName = name + "-instance" + i;
        compStats.add(new CompStat(compName, taskCls, compTypeMap.get(name)));
      }
    }
  }

  // 对上面表格进行填充，主要是运行策略和下游组件
  public void fillTable(Map<String, Strategy> strategyMap, Map<String, Set<String>> nextTaskMap) {
    // 设置策略
    for (Map.Entry<String, Strategy> entry : strategyMap.entrySet()) {
      String name = entry.getKey();
      Strategy strategy = entry.getValue();
      List<CompStat<ITask>> compStatsList = compStatMap.get(name);
      for (CompStat compStat : compStatsList) {
        compStat.setStrategy(strategy);
      }
    }

    // 设置下游组件
    for (Map.Entry<String, Set<String>> nextTask : nextTaskMap.entrySet()) {
      // 当前组件名
      String currentTaskName = nextTask.getKey();
      // 下游组件名，集合
      Set<String> compStrSet = nextTask.getValue();

      List<CompStat> list = new ArrayList<>();

      for (String nextCompName : compStrSet) {
        // 该组件名有多个实例
        List<CompStat<ITask>> compStatsList = compStatMap.get(nextCompName);
        if (compStatsList.size() > 0) {
          list.addAll(compStatsList);
        }
      }

      for (CompStat compStat : compStatMap.get(currentTaskName)) {
        compStat.setNextCompClsList(list);
      }

    }
  }

  // 获得所有组件
  public List<CompStat<ITask>> getAllComps() {
    List<CompStat<ITask>> compStatList = new ArrayList<>();

    for (Map.Entry<String, List<CompStat<ITask>>> entry : compStatMap.entrySet()) {
      compStatList.addAll(entry.getValue());
    }
    return compStatList;
  }


}
