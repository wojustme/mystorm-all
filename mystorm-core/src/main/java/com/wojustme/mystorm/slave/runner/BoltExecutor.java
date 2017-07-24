package com.wojustme.mystorm.slave.runner;

import com.wojustme.mystorm.comp.IBoltComp;
import com.wojustme.mystorm.comp.OutPutCollector;
import com.wojustme.mystorm.comp.Tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * bolt运行线程
 * 运行bolt类型task任务
 * @author wojustme
 * @date 2017/7/17
 * @package com.wojustme.mystorm.slave.runner
 */
public class BoltExecutor implements Runnable {

  // 当前线程名
  private String executorName;
  // 接收任务队列
  private BlockingQueue<TaskTransferData> taskQueue;
  // 网络发送器
  private Map<String, OutPutCollector> collectorMap;
  // 该线程含有task最大数量
  private int maxNum;
  // bolt任务容器
  private Map<String, IBoltComp> boltCompMap;
  // 当前运行的标记
  private volatile boolean isRunning = false;



  public BoltExecutor(String executorName, BlockingQueue<TaskTransferData> taskQueue, boolean isRunning) {
    this.executorName = executorName;
    this.taskQueue = taskQueue;
    this.collectorMap = new HashMap<>();
    this.boltCompMap = new HashMap<>();
    this.isRunning = true;
  }

  // 添加任务
  public void addTask(String taskName, IBoltComp boltInstance, OutPutCollector outPutCollector) {
    // 对boltInstance进行初始化
    // 将该outPutCollector注入bolt实例
    boltInstance.init(outPutCollector);
    boltCompMap.put(taskName, boltInstance);
    collectorMap.put(taskName, outPutCollector);
  }

  @Override
  public void run() {
    while (isRunning) {
      try {
        // 先从taskQueue获取数据
        TaskTransferData takeData = taskQueue.take();
        // 任务名
        String taskName = takeData.getTaskName();
        // 获得当前传输的数据值
        String tupleVal = takeData.getDataVal();
        // 当前bolt组件实例
        IBoltComp boltComp = boltCompMap.get(taskName);
        // 执行该bolt组件
        boltComp.execute(tupleVal);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  public String getExecutorName() {
    return executorName;
  }

  public boolean isRunning() {
    return isRunning;
  }
}
