package com.wojustme.mystorm.slave.runner;


import com.wojustme.mystorm.comp.*;
import com.wojustme.mystorm.schedule.AssiginTaskZkBean;
import com.wojustme.mystorm.schedule.DownstreamNetNode;
import com.wojustme.mystorm.slave.Worker;
import com.wojustme.mystorm.slave.WorkerConnZK;
import com.wojustme.mystorm.slave.stat.ActiveBoltExecutorStat;
import com.wojustme.mystorm.slave.stat.WorkerStat;
import com.wojustme.mystorm.topology.Strategy;
import com.wojustme.mystorm.util.LoadExtraJar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 线程管理器
 * 对所有的任务和工作线程、工作任务进行管理
 * @author wojustme
 * @date 2017/7/21
 * @package com.wojustme.mystorm.slave
 */
public class ExecutorManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorManager.class);


  // 当前配置
  private final Properties conf;
  // 当前worker节点装填信息
  private final WorkerStat workerStat;
  // 存储所有的任务，每一个任务都具有互异性
  private Set<String> taskNameSet;
  // 存储spout任务 -> spout执行线程
  private Map<String, SpoutExecutor> runSpoutExecutorMap;
  // 存储bolt任务 -> bolt执行线程
  private Map<String, BoltExecutor> runBoltExecutorMap;
  // 存储bolt运行线程实例引用, bolt线程名 -> bolt执行线程
  private Map<String, BoltExecutor> boltExecutorContainer;
  // 控制该线程开关
  private Map<String, Boolean> executorRunningMap;
  // bolt线程个数
  private int boltExecutorNum = 0;

  // 与spout线程或者bolt线程通信队列，线程名 -> 队列引用
  // 只用于接收任务数据，传递数据作用
  // 发送数据直接在线程中完成
  private Map<String, BlockingQueue<TaskTransferData>> executorQueue;

  private final WorkerConnZK workerConnZK;

  public ExecutorManager(Properties conf, WorkerStat workerStat, WorkerConnZK workerConnZK) {
    this.conf = conf;
    this.workerStat = workerStat;
    this.taskNameSet = new HashSet<>();
    this.runSpoutExecutorMap = new HashMap<>();
    this.runBoltExecutorMap = new HashMap<>();
    this.boltExecutorContainer = new HashMap<>();
    this.executorQueue = new HashMap<>();
    this.executorRunningMap = new HashMap<>();
    this.workerConnZK = workerConnZK;
  }

  // 增加新任务
  public void addTask(AssiginTaskZkBean newTask) throws Exception{
    // 新任务名
    String taskName = newTask.getTaskName();
    String jarFileName = newTask.getJarFileName();
    // 当前jar包的路径
    String jarFilePath = conf.getProperty("worker.jarFile.path") + "/" + jarFileName;
    // 执行的主类
    String compClsStr = newTask.getCompClsStr();
    // 数据流动执行策略
    Strategy strategy = newTask.getStrategy();
    // 下游节点信息
    List<DownstreamNetNode> downstreamNetNodeList = newTask.getDownstreamNetNodeList();

    if (newTask.getTaskType() == TaskType.SPOUT) {
      // 添加spout任务
      // 实例化该spout组件对象
      ISpoutComp spoutCompObj = (ISpoutComp) LoadExtraJar.getTargetCls(jarFilePath, compClsStr).newInstance();
      addSpoutTask(taskName, spoutCompObj, strategy, downstreamNetNodeList);
    } else if (newTask.getTaskType() == TaskType.BOLT) {
      // 添加bolt任务
      // 实例化该bolt组件对象
      IBoltComp boltCompObj = (IBoltComp) LoadExtraJar.getTargetCls(jarFilePath, compClsStr).newInstance();
      addBoltTask(taskName, boltCompObj, strategy, downstreamNetNodeList);
    } else {
      LOGGER.error("错误任务类型");
    }

    // 更新worker节点状态信息
    workerConnZK.updateStat();
  }

  // 增加spout运行组件
  private void addSpoutTask(String taskName, ISpoutComp spoutCompObj, Strategy strategy, List<DownstreamNetNode> downstreamNetNodeList) {
    // 添加一个任务名到任务集合中去
    taskNameSet.add(taskName);
    // todo 暂时将网络引用设置为null，后续添加
    OutPutCollector outPutCollector = new NettyOutPutCollector(taskName, strategy, downstreamNetNodeList);
    SpoutExecutor spoutExecutor = new SpoutExecutor(taskName, spoutCompObj, outPutCollector);
    runSpoutExecutorMap.put(taskName, spoutExecutor);
    workerStat.addSpoutExecutor(taskName);
    workerStat.setActiveSpoutExecutorNum(workerStat.getActiveSpoutExecutorNum() + 1);
    new Thread(spoutExecutor).start();
  }
  // 增加bolt运行组件
  private void addBoltTask(String taskName, IBoltComp boltCompObj, Strategy strategy, List<DownstreamNetNode> downstreamNetNodeList) {
    taskNameSet.add(taskName);
    List<ActiveBoltExecutorStat> activeBoltExecutorList = workerStat.getActiveBoltExecutorStatList();
    // 是否可以加入已经存在的bolt线程
    boolean b = false;
    for (ActiveBoltExecutorStat activeBoltExecutorStat : activeBoltExecutorList) {
      // 可以运行task个数
      int allTaskNum = activeBoltExecutorStat.getAllTaskNum();
      // 已经运行的task个数
      int activeTaskNum = activeBoltExecutorStat.getActiveTaskNum();
      if (allTaskNum - activeTaskNum > 0) {
        activeBoltExecutorStat.setActiveTaskNum(activeTaskNum + 1);
        activeBoltExecutorStat.addTask(taskName);
        String executorName = activeBoltExecutorStat.getExecutorName();
        // 获得当前线程
        BoltExecutor boltExecutor = boltExecutorContainer.get(executorName);
        // 想bolt线程添加任务
        OutPutCollector outPutCollector = new NettyOutPutCollector(taskName, strategy, downstreamNetNodeList);
        // todo 网络引用
        boltExecutor.addTask(taskName, boltCompObj, outPutCollector);
        // 添加映射任务与线程映射关系
        runBoltExecutorMap.put(taskName, boltExecutor);
        b = true;
        break;
      }
    }
    if (!b) {
      // 如果当前线程不够，则创建新线程
      createBoltExecutor();
      addBoltTask(taskName, boltCompObj, strategy, downstreamNetNodeList);
    }
  }


  // 创建新的bolt线程
  private void createBoltExecutor() {
    boltExecutorNum += 1;
    List<ActiveBoltExecutorStat> activeBoltExecutorStatList = workerStat.getActiveBoltExecutorStatList();
    if (activeBoltExecutorStatList == null) {
      activeBoltExecutorStatList = new ArrayList<>();
    }
    String executorName = "boltEcecutor-" + boltExecutorNum;
    // 添加到可运行的线程集合中去
    activeBoltExecutorStatList.add(new ActiveBoltExecutorStat(executorName, workerStat.getAllBoltExecutorNum(), 0));
    // 设置通信队列executorQueue
    BlockingQueue<TaskTransferData> taskTransferData = new ArrayBlockingQueue<>(TaskTransferData.length);
    executorQueue.put(executorName, taskTransferData);
    executorRunningMap.put(executorName, true);
    BoltExecutor newBoltExecutor = new BoltExecutor(executorName, taskTransferData, executorRunningMap.get(executorName));
    boltExecutorContainer.put(executorName, newBoltExecutor);
    // 新线程启动
    new Thread(newBoltExecutor).start();


  }

  // 接收到消息
  public void recvData(String taskName, String data) {

    // 获得运行的线程
    BoltExecutor boltExecutor = runBoltExecutorMap.get(taskName);
//    boltExecutor.getExecutorName();
    BlockingQueue<TaskTransferData> blockingQueue = executorQueue.get(boltExecutor.getExecutorName());
    TaskTransferData taskTransferData = new TaskTransferData(taskName, data);
    try {
      blockingQueue.put(taskTransferData);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
//    blockingQueue.add(taskTransferData);
  }
}
