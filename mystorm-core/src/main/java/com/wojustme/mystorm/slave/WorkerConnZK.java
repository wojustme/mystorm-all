package com.wojustme.mystorm.slave;

import com.wojustme.mystorm.observer.EventBean;
import com.wojustme.mystorm.schedule.AssiginTaskZkBean;
import com.wojustme.mystorm.slave.event.WorkerEventType;
import com.wojustme.mystorm.slave.stat.WorkerStat;
import com.wojustme.mystorm.util.JsonUtil;
import com.wojustme.mystorm.util.ZkHandlerUtl;
import jdk.nashorn.internal.runtime.WithObject;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * 启动worker节点
 * => 连接Zk集群
 * => 创建临时节点/mystorm/nodes/workers/worker-1节点(work-****, 随机码)，写入内容有开放的IP+Port
 * => 创建永久节点/mystorm/assignments/worker-1，并监听该子节点信息变化(update操作)
 * => 监听节点/mystorm/topologies变化，是否添加或者删除拓扑(下载拓扑jar包)
 * @author wojustme
 * @date 2017/7/12
 * @package com.wojustme.mystorm.slave
 */
public class WorkerConnZK {

  private static final Logger LOGGER = LoggerFactory.getLogger(WorkerConnZK.class);

  // 连接的ZK的地址和端口信息
  private final String ZK_ADDR;
  // 失效session时间(毫秒)
  private final int SESSION_TIME_MS;
  // 重试次数
  private final int RETRY;
  // 重试间隔时间
  private final int BETWEEN_RETRY_TIME_MS;
  // 处理ZK根路径
  private final String ZK_ROOT_PATH;
  // 连接zk的客户端
  private CuratorFramework client;

  private final String workerId;

  // worker事件订阅器
  private final WorkerSubject workerSubject;

  private Properties config;

  // 当前worker节点名字
  private final String workerName;
  // 当前worker节点信息
  private WorkerStat workerStat;

  public WorkerConnZK(String workerName, WorkerSubject workerSubject, Properties config, WorkerStat workerStat) {
    this.config = config;
    // 初始化参数
    ZK_ADDR = config.getProperty("zk.addr");
    SESSION_TIME_MS = Integer.valueOf(config.getProperty("zk.session.time"));
    RETRY = Integer.valueOf(config.getProperty("zk.retry.count"));
    BETWEEN_RETRY_TIME_MS = Integer.valueOf(config.getProperty("zk.retry.interval.time"));
    ZK_ROOT_PATH = config.getProperty("zk.root.path");
    this.workerSubject = workerSubject;
    this.workerId = config.getProperty("worker.id");
    this.workerName = workerName;
    this.workerStat = workerStat;
    // 初始化zk的客户端
    initClient();
    // 创建临时节点/mystorm/nodes/workers/worker-1节点(work-****, 随机码)，写入内容有开放的IP+Port
    createWorkerNode();
    createWorkerAssignNode();
    watchAssignments();
  }


  // 初始化zk的客户端
  private void initClient() {
    client = CuratorFrameworkFactory.builder()
        .connectString(ZK_ADDR)
        .connectionTimeoutMs(SESSION_TIME_MS)
        .retryPolicy(new RetryNTimes(RETRY, BETWEEN_RETRY_TIME_MS))
        .namespace(ZK_ROOT_PATH)
        .build();
    client.start();
  }

  // 销毁zk连接
  private void destoryClient() {
    if (client != null) {
      client.close();
      client = null;
    }
  }

  // 创建worker临时节点
  private void createWorkerNode() {
    String nodePath = "/nodes/workers/" + workerName;
    String workStatMsg = initWorkerStat();
    try {
      // 如果该节点存在，删除
      if (client.checkExists().forPath(nodePath) != null) {
        client.delete().forPath(nodePath);
      }
      client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(nodePath, workStatMsg.getBytes("utf-8"));
    } catch (Exception e) {
    }
  }

  // 创建安排任务节点
  private void createWorkerAssignNode() {
    String nodePath = "/assignments/" + workerName;
    try {
      // 如果该节点存在，删除
      if (client.checkExists().forPath(nodePath) == null) {
        // todo
        ZkHandlerUtl.createData(client, nodePath, CreateMode.PERSISTENT);
      }
    } catch (Exception e) {
    }
  }


  // 监听toplogy变更情况
  private void watchAssignments() {
    String watchNodePath = "/assignments/" + workerName;
    PathChildrenCache nodeCache = null;
    try {
      if (client.checkExists().forPath(watchNodePath) != null) {
        nodeCache = new PathChildrenCache(client, watchNodePath, true);
        nodeCache.start(true);

        nodeCache.getListenable().addListener((client, event) -> {
          switch (event.getType()) {
            case CHILD_ADDED:
              LOGGER.info("新任务提交");
              // 新任务提交
              ChildData newTaskData = event.getData();
              AssiginTaskZkBean newTask = JsonUtil.toBeanObj(new String(newTaskData.getData(), "utf-8"), AssiginTaskZkBean.class);
              workerSubject.nodifyObservers(new EventBean(WorkerEventType.NEW_TASK_ASSIGNED, newTask));
              break;
            case CHILD_REMOVED:
              break;
            case CHILD_UPDATED:
              break;
            default:
          }
        });
      }
    } catch (Exception e){
    }
  }

  // 初始化工作节点状态信息
  private String initWorkerStat() {
    return JsonUtil.toJsonStr(workerStat);
  }

  public CuratorFramework getClient() {
    return client;
  }

  public void updateStat() {
    String nodePath = "/nodes/workers/" + workerName;
    try {
      ZkHandlerUtl.writeData(client, nodePath, JsonUtil.toJsonStr(workerStat));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
