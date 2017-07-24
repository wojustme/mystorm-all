package com.wojustme.mystorm.slave.stat;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 用于存储工作状态
 * @author wojustme
 * @date 2017/7/19
 * @package com.wojustme.mystorm.slave.stat
 */
public class WorkerStat {

  // worker名
  private final String workerName;

  // 总共申请了bolt线程数量
  private final int allBoltExecutorNum;
  // 已经使用的bolt线程数量
  private int activeBoltExecutorNum;
  // 运行的bolt线程列表
  List<ActiveBoltExecutorStat> activeBoltExecutorStatList;

  // 总共申请了spout线程数量
  private final int allSpoutExecutorNum;
  // 已经使用的spout线程数量
  private int activeSpoutExecutorNum;
  // 运行的spout线程列表
  List<String> activeSpoutExecutorNameList;

  // 配置信息
  private final Properties config;
  // 开放IP和端口
  private final String workerHost;
  private final int workerPort;

  private final int boltTaskNumEachExecutor;


  public WorkerStat(Properties config) {
    String tmp = "localhost";
    this.config = config;
    this.workerName = "worker-" + config.getProperty("worker.id");
    this.allSpoutExecutorNum = Integer.valueOf(config.getProperty("worker.spout.executor.num"));
    this.allBoltExecutorNum = Integer.valueOf(config.getProperty("worker.bolt.executor.num"));
    this.boltTaskNumEachExecutor = Integer.valueOf(config.getProperty("worker.bolt.executor.task.num"));
    this.workerPort = Integer.valueOf(config.getProperty("worker.port"));
    try {
      tmp = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    this.workerHost = tmp;
    activeBoltExecutorStatList = new ArrayList<>();
    activeSpoutExecutorNameList = new ArrayList<>();
  }

  public int getAllBoltExecutorNum() {
    return allBoltExecutorNum;
  }

  public int getActiveBoltExecutorNum() {
    return activeBoltExecutorNum;
  }

  public void setActiveBoltExecutorNum(int activeBoltExecutorNum) {
    this.activeBoltExecutorNum = activeBoltExecutorNum;
  }

  public List<ActiveBoltExecutorStat> getActiveBoltExecutorStatList() {
    return activeBoltExecutorStatList;
  }

  public void setActiveBoltExecutorStatList(List<ActiveBoltExecutorStat> activeBoltExecutorStatList) {
    this.activeBoltExecutorStatList = activeBoltExecutorStatList;
  }

  public int getAllSpoutExecutorNum() {
    return allSpoutExecutorNum;
  }

  public int getActiveSpoutExecutorNum() {
    return activeSpoutExecutorNum;
  }

  public void setActiveSpoutExecutorNum(int activeSpoutExecutorNum) {
    this.activeSpoutExecutorNum = activeSpoutExecutorNum;
  }

  public List<String> getActiveSpoutExecutorNameList() {
    return activeSpoutExecutorNameList;
  }

  public void setActiveSpoutExecutorNameList(List<String> activeSpoutExecutorNameList) {
    this.activeSpoutExecutorNameList = activeSpoutExecutorNameList;
  }

  public String getWorkerHost() {
    return workerHost;
  }

  public int getWorkerPort() {
    return workerPort;
  }

  public int getBoltTaskNumEachExecutor() {
    return boltTaskNumEachExecutor;
  }

  public String getWorkerName() {
    return workerName;
  }

  public void addSpoutExecutor(String spoutTaskName) {
    this.activeSpoutExecutorNameList.add(spoutTaskName);
  }
}
