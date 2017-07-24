package com.wojustme.mystorm.schedule;

import com.wojustme.mystorm.comp.TaskType;
import com.wojustme.mystorm.master.MasterConnZK;
import com.wojustme.mystorm.slave.stat.ActiveBoltExecutorStat;
import com.wojustme.mystorm.slave.stat.WorkerStat;
import com.wojustme.mystorm.topology.zk.CompZkBean;
import com.wojustme.mystorm.util.JsonUtil;
import com.wojustme.mystorm.util.ZkHandlerUtl;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import java.util.*;

/**
 * 安排任务
 * @author wojustme
 * @date 2017/7/19
 * @package com.wojustme.mystorm.schedule
 */
public final class AssignTaskUtil {

  public static void assginTask(MasterConnZK masterConnZK, String topologyName) throws Exception{
    assginTask(masterConnZK.getZkClient(), topologyName);
  }

  public static void assginTask(CuratorFramework zkClient, String topologyName) throws Exception {

    Map<String, List<CompZkBean>> allTasks = getTaskNameList(zkClient, topologyName);
    Map<String, Map<String, Integer>> workerSoltsMap = calcWorkSolts(getWorkerStat(zkClient));
    if (!checkCanRunTopology(allTasks, workerSoltsMap)) {
      throw new Exception("不可运行。。。申请不到资源");
    }
    arrangeTask(zkClient, allTasks, workerSoltsMap, topologyName);
  }

  // 安排, 暂时采用轮询方式安排任务
  // todo
  private static void arrangeTask(CuratorFramework zkClient, Map<String, List<CompZkBean>> allTasks, Map<String, Map<String, Integer>> workerSoltsMap, String topologyName) {

    // 计算
    List<CompZkBean> spoutTasks = allTasks.get("spout");
    List<CompZkBean> boltTasks = allTasks.get("bolt");

    // 映射: 任务  ->  工作节点名
    Map<CompZkBean, String> rsArrangement =arrangeToWorkerNode(spoutTasks, boltTasks, workerSoltsMap);
    Set<Map.Entry<CompZkBean, String>> entries = rsArrangement.entrySet();
    Map<String, String> taskName2Worker = new HashMap<>(entries.size());
    // 映射: 任务名 -> 工作节点名
    for (Map.Entry<CompZkBean, String> entry : entries) {
      taskName2Worker.put(entry.getKey().getCompName(), entry.getValue());
    }

    // 首先获得被分配的worker节点的IP和port，映射关系
    Map<String, WorkNetMsg> workNetMsg = getWorkNetMsg(zkClient, rsArrangement);

    // 计算得出真实分配信息
    // 任务名 -> AssiginTaskZkBean
    Map<String, AssiginTaskZkBean> assignmentMsg = getAssignmentMsg(zkClient, topologyName, spoutTasks, boltTasks, taskName2Worker, workNetMsg);

    // 填写/mystorm/topologies/wordcount/wordcount-countBolt-instance1中子节点runNode数据
    // 填写任务安排信息
    // 路径为/mystorm/assignments/worker节点名/任务名
    for (Map.Entry<String, AssiginTaskZkBean> entry : assignmentMsg.entrySet()) {
      String basPath = "/assignments/" + taskName2Worker.get(entry.getKey());
      AssiginTaskZkBean assiginTask = entry.getValue();
      String assiginTaskPath = basPath + "/" +assiginTask.getTaskName();
      try {
        ZkHandlerUtl.createData(zkClient, assiginTaskPath, JsonUtil.toJsonStr(assiginTask), CreateMode.PERSISTENT);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }



  }

  // 获得真实分配信息
  private static Map<String, AssiginTaskZkBean> getAssignmentMsg(CuratorFramework zkClient, String topologyName, List<CompZkBean> spoutTasks, List<CompZkBean> boltTasks, Map<String, String> taskName2Worker, Map<String, WorkNetMsg> workNetMsg) {
    String jarFileName = "";
    try {
      jarFileName = new String(zkClient.getData().forPath("/topologies/" + topologyName));
    } catch (Exception e) {
      e.printStackTrace();
    }



    Map<String, AssiginTaskZkBean> tmp = new HashMap<>();
    for (CompZkBean spoutTask : spoutTasks) {
      String taskName = spoutTask.getCompName();
      String realTaskName = topologyName + "-" + taskName;
      AssiginTaskZkBean assiginTaskZkBean = new AssiginTaskZkBean(realTaskName, jarFileName, spoutTask.getCompClsStr(), spoutTask.getCompType(), spoutTask.getStrategy());

      // 开始设置下游节点信息
      for (String nextTaskName : spoutTask.getNextCompNameList()) {
        WorkNetMsg wnm = workNetMsg.get(taskName2Worker.get(nextTaskName));
        assiginTaskZkBean.addNetNode(new DownstreamNetNode(wnm.getHost(), wnm.getPort(), topologyName + "-" + nextTaskName));
      }

      tmp.put(taskName, assiginTaskZkBean);
    }

    for (CompZkBean boltTask : boltTasks) {
      String taskName = boltTask.getCompName();
      String realTaskName = topologyName + "-" + taskName;
      AssiginTaskZkBean assiginTaskZkBean = new AssiginTaskZkBean(realTaskName, jarFileName, boltTask.getCompClsStr(), boltTask.getCompType(), boltTask.getStrategy());

      // 开始设置下游节点信息
      for (String nextTaskName : boltTask.getNextCompNameList()) {
        WorkNetMsg wnm = workNetMsg.get(taskName2Worker.get(nextTaskName));
        assiginTaskZkBean.addNetNode(new DownstreamNetNode(wnm.getHost(), wnm.getPort(), topologyName + "-" + nextTaskName));
      }
      tmp.put(taskName, assiginTaskZkBean);
    }

    return tmp;

  }

  // 任务分配节点
  // 返回，任务  ->  工作节点名  映射
  private static Map<CompZkBean, String> arrangeToWorkerNode(List<CompZkBean> spoutTasks, List<CompZkBean> boltTasks, Map<String, Map<String, Integer>> workerSoltsMap) {

    int spoutTasksNum = spoutTasks.size();
    int boltTasksNum = boltTasks.size();

    Map<CompZkBean, String> rsArrangement = new HashMap<>(spoutTasksNum + boltTasksNum);

    // 分配spout
    for (CompZkBean spoutComp : spoutTasks) {
      for (Map.Entry<String, Map<String, Integer>> entry : workerSoltsMap.entrySet()) {
        String workerName = entry.getKey();
        int spoutNum = entry.getValue().get("spout");
        if (spoutNum > 0) {
          rsArrangement.put(spoutComp, workerName);
          entry.getValue().put("spout", spoutNum - 1);
          break;
        }
      }
    }

    // 分配bolt
    for (CompZkBean boltComp : boltTasks) {
      for (Map.Entry<String, Map<String, Integer>> entry : workerSoltsMap.entrySet()) {
        String workerName = entry.getKey();
        int boltNum = entry.getValue().get("bolt");
        if (boltNum > 0) {
          rsArrangement.put(boltComp, workerName);
          entry.getValue().put("bolt", boltNum - 1);
          break;
        }
      }
    }
    return rsArrangement;
  }

  // 获得被分配的worker节点的IP和port
  private static Map<String, WorkNetMsg> getWorkNetMsg(CuratorFramework zkClient, Map<CompZkBean, String> rsArrangement) {
    Collection<String> values = rsArrangement.values();
    Set<String> workNodePaths = new HashSet<>();
    for (String val : values) {
      workNodePaths.add(val);
    }
    Map<String, WorkNetMsg> workNetMsgMap = new HashMap<>();
    for (String workNodePath : workNodePaths) {
      try {
        WorkerStat workerStat = JsonUtil.toBeanObj(new String(zkClient.getData().forPath("/nodes/workers/" + workNodePath), "utf-8"), WorkerStat.class);
        workNetMsgMap.put(workNodePath, new WorkNetMsg(workerStat.getWorkerHost(), workerStat.getWorkerPort()));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return workNetMsgMap;
  }

  // 验证可运行性
  private static boolean checkCanRunTopology(Map<String, List<CompZkBean>> allTasks, Map<String, Map<String, Integer>> workerSoltsMap) {

    int totalSpoutSolt = 0;
    int totalBoltSolt = 0;

    for (Map.Entry<String, Map<String, Integer>> entry : workerSoltsMap.entrySet()) {
      Map<String, Integer> value = entry.getValue();
      totalSpoutSolt += value.get("spout");
      totalBoltSolt += value.get("bolt");
    }

    if (allTasks.get("spout").size() > totalSpoutSolt || allTasks.get("bolt").size() > totalBoltSolt) {
      return false;
    }
    return true;
  }

  // 获得当前topology所有任务清单
  private static Map<String, List<CompZkBean>> getTaskNameList(CuratorFramework zkClient, String topologyName) {
    // 获得当前zk中节点topologyName
    String topologyPath = "/topologies/" + topologyName;

    // spout类型任务集合
    List<CompZkBean> spoutTaskList = new ArrayList<>();
    // bolt类型任务集合
    List<CompZkBean> boltTaskList = new ArrayList<>();
    Map<String, List<CompZkBean>> allTasks = new HashMap<>(2);
    allTasks.put("spout", spoutTaskList);
    allTasks.put("bolt", boltTaskList);

//    List<CompZkBean> taskNameList = new ArrayList<>();

    try {
      List<String> childPaths = zkClient.getChildren().forPath(topologyPath);
      for (String childPath : childPaths) {
        if (childPath.startsWith("comp-")) {
          String path = topologyPath + "/" + childPath;
          String dataStr = new String(zkClient.getData().forPath(path), "utf-8");
          CompZkBean compZkBean = JsonUtil.toBeanObj(dataStr, CompZkBean.class);
//          taskNameList.add(compZkBean);
          if (compZkBean.getCompType() == TaskType.SPOUT) {
            spoutTaskList.add(compZkBean);
          }
          if (compZkBean.getCompType() == TaskType.BOLT) {
            boltTaskList.add(compZkBean);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return allTasks;
  }

  // 获得当前worker工作情况
  private static List<WorkerStat> getWorkerStat(CuratorFramework zkClient) {
    String workerClusterPath = "/nodes/workers";
    List<WorkerStat> workerStatList = new ArrayList<>();
    try {
      List<String> workers = zkClient.getChildren().forPath(workerClusterPath);
      for (String worker : workers) {
        byte[] bytes = zkClient.getData().forPath(workerClusterPath + "/" + worker);
        String data = new String(bytes, "utf-8");
        WorkerStat workerStat = JsonUtil.toBeanObj(data, WorkerStat.class);
        workerStatList.add(workerStat);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return workerStatList;
  }


  // 计算可分配的工作槽
  // workName -> { [spout || bolt] -> num }
  private static Map<String, Map<String,Integer>> calcWorkSolts(List<WorkerStat> workerStatList) {

    // 每一个工作接单含有多少个工作槽
    Map<String, Map<String,Integer>> canAllocateSlots = new HashMap<>();

    for (WorkerStat workerStat : workerStatList) {
      Map<String, Integer> workerSlots = new HashMap<>(2);
      int spoutNum = 0;
      int boltNum = 0;


      spoutNum += workerStat.getAllSpoutExecutorNum() - workerStat.getActiveSpoutExecutorNum();

      boltNum += (workerStat.getAllBoltExecutorNum() - workerStat.getActiveBoltExecutorNum()) * workerStat.getBoltTaskNumEachExecutor();
      for (ActiveBoltExecutorStat executorStat : workerStat.getActiveBoltExecutorStatList()) {
        boltNum += executorStat.getAllTaskNum() - executorStat.getActiveTaskNum();
      }

      workerSlots.put("spout", spoutNum);
      workerSlots.put("bolt", boltNum);

      canAllocateSlots.put(workerStat.getWorkerName(), workerSlots);
    }

    return canAllocateSlots;
  }

  /**
   * @author wojustme
   * @date 2017/7/22
   * @package com.wojustme.mystorm.schedule
   */
  static class WorkNetMsg {
    private String host;
    private int port;

    public WorkNetMsg(String host, int port) {
      this.host = host;
      this.port = port;
    }


    public String getHost() {
      return host;
    }

    public int getPort() {
      return port;
    }

    @Override
    public String toString() {
      return "WorkNetMsg{" +
          "host='" + host + '\'' +
          ", port=" + port +
          '}';
    }
  }
}

