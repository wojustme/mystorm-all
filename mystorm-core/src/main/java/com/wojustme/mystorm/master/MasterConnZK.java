package com.wojustme.mystorm.master;

import com.wojustme.mystorm.master.event.MasterEventType;
import com.wojustme.mystorm.observer.EventBean;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * master节点连接ZK操作类
 * => 连接ZK集群
 * => 创建永久节点/mystorm/nodes/master，写入当前节点信息内容，开放的IP+Port，
 * => 创建永久节点/mystorm/nodes/workers，为工作节点worker提供目录(如果存在，则跳过)
 * => 创建永久节点/mystorm/nodes/topologies，为提交的拓扑结构提供目录(如果存在，则跳过)
 * => 创建永久/mystorm/nodes/assignments，为安排任务提供(如果存在，则跳过)
 * @author wojustme
 * @date 2017/7/12
 * @package com.wojustme.mystorm.master
 */
public class MasterConnZK {

  private static final Logger LOGGER = LoggerFactory.getLogger(MasterConnZK.class);

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
  private CuratorFramework zkClient;
  private boolean isClientClose = true;
  // 监听主题注入
  private final MasterSubject masterSubject;

  public MasterConnZK(MasterSubject masterSubject, Properties config) {
    // 初始化参数
    ZK_ADDR = config.getProperty("zk.addr");
    SESSION_TIME_MS = Integer.valueOf(config.getProperty("zk.session.time"));
    RETRY = Integer.valueOf(config.getProperty("zk.retry.count"));
    BETWEEN_RETRY_TIME_MS = Integer.valueOf(config.getProperty("zk.retry.interval.time"));
    ZK_ROOT_PATH = config.getProperty("zk.root.path");

    this.masterSubject = masterSubject;

    // 初始化zk的客户端
    initClient();
    // 创建临时节点/mystorm/nodes/master
    createMasterNode();
    // 创建永久节点/mystorm/nodes/workers
    createWatchWorkerNodes();
    // 创建永久节点/mystorm/topologies
    createWatchTopologyNodes();
    // 创建永久/mystorm/assignments
    createWatchAssignmentNodes();
    // 监听节点/mystorm/topologies变化，是否添加或者删除拓扑(下载拓扑jar包)
    watchTopologies();

  }

  // 初始化zk的客户端
  private void initClient() {
    zkClient = CuratorFrameworkFactory.builder()
        .connectString(ZK_ADDR)
        .connectionTimeoutMs(SESSION_TIME_MS)
        .retryPolicy(new RetryNTimes(RETRY, BETWEEN_RETRY_TIME_MS))
        .namespace(ZK_ROOT_PATH)
        .build();
    zkClient.start();
    isClientClose = false;
  }

  // 销毁zk连接
  private void destoryClient() {

    if (zkClient != null) {
      if (!isClientClose) {
        zkClient.close();
      }
      zkClient = null;
    }
  }


  // 创建节点/mystorm/nodes/master
  private void createMasterNode() {
    String nodePath = "/nodes/master";
    try {
      // 如果该节点存在，删除
      if (zkClient.checkExists().forPath(nodePath) != null) {
        zkClient.delete().forPath(nodePath);
      }
      zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(nodePath);
    } catch (Exception e) {
    }
  }

  // 创建节点/mystorm/nodes/workers
  private void createWatchWorkerNodes() {
    String workerPath = "/nodes/workers";
    try {
      // 如果该节点存在，跳过
      if (zkClient.checkExists().forPath(workerPath) != null) {
        return;
      }
      zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(workerPath);
    } catch (Exception e) {
    }
  }

  // 创建节点/mystorm/topologies
  private void createWatchTopologyNodes() {
    String topologyPath = "/topologies";
    try {
      // 如果该节点存在，跳过
      if (zkClient.checkExists().forPath(topologyPath) != null) {
        return;
      }
      zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(topologyPath);
    } catch (Exception e) {
    }
  }

  // 创建节点/mystorm/assignments
  private void createWatchAssignmentNodes() {
    String topologyPath = "/assignments";
    try {
      // 如果该节点存在，跳过
      if (zkClient.checkExists().forPath(topologyPath) != null) {
        return;
      }
      zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(topologyPath);
    } catch (Exception e) {
    }
  }

  // 监听toplogy变更情况
  private void watchTopologies(){
    String watchNodePath = "/topologies";
    PathChildrenCache nodeCache = null;
    try {
      if (zkClient.checkExists().forPath(watchNodePath) != null) {
        nodeCache = new PathChildrenCache(zkClient, watchNodePath, true);
        nodeCache.start(true);

        nodeCache.getListenable().addListener((CuratorFramework client, PathChildrenCacheEvent event) -> {
          switch (event.getType()) {
            case CHILD_ADDED:
              ChildData data = event.getData();
              String newTopologyPath = data.getPath();
              String statPath = newTopologyPath + "/stat";
              NodeCache statNodeCache = new NodeCache(client, statPath, false);
              statNodeCache.start();
              statNodeCache.getListenable().addListener(() -> {
                String statData = new String(statNodeCache.getCurrentData().getData(), "utf-8");
                // 拓扑创建成功
                if (statData.equals("created")) {
                  String[] tmpArr = newTopologyPath.split("/");
                  String topologyName = tmpArr[tmpArr.length - 1];
                  masterSubject.nodifyObservers(new EventBean(MasterEventType.INIT_TOPOLOGY_OK, topologyName));
                }
                // todo 拓扑创建失败，需要删除。。。。
              });
              break;
            case CHILD_REMOVED:
              LOGGER.info("remove...");
              break;
            case CHILD_UPDATED:
              LOGGER.info("update");
              break;
            default:
              LOGGER.error("error...");
          }
        });
      }
    } catch (Exception e){
    }
  }

  public CuratorFramework getZkClient() {
    return zkClient;
  }

  public boolean isClientClose() {
    return isClientClose;
  }
}
