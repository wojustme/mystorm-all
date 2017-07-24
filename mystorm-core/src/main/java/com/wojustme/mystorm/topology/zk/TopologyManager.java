package com.wojustme.mystorm.topology.zk;

import com.wojustme.mystorm.comp.ITask;
import com.wojustme.mystorm.comp.TaskType;
import com.wojustme.mystorm.topology.CompStat;
import com.wojustme.mystorm.topology.Strategy;
import com.wojustme.mystorm.topology.TopologyBean;
import com.wojustme.mystorm.util.JsonUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * topology管理着
 * 管理着拓扑的提交与杀死
 * @author wojustme
 * @date 2017/7/18
 * @package com.wojustme.mystorm.topology.zk
 */
public class TopologyManager {

  // 当前zk客户端是否关闭
  private boolean isClosed = true;

  // zk连接客户端
  private CuratorFramework client;

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

  // 配置信息
  private Properties conf;

  // 提交的数据
  private TopologyZkBean topologyZkBean;

  public TopologyManager(TopologyBean topologyBean, Properties conf) {
    // todo 暂时设计从资源文件中加载到属性
    this.conf = conf;

    this.topologyZkBean = generateTopologyZkBean(topologyBean);

    ZK_ADDR = conf.getProperty("zk.addr");
    SESSION_TIME_MS = Integer.valueOf(conf.getProperty("zk.session.time"));
    RETRY = Integer.valueOf(conf.getProperty("zk.retry.count"));
    BETWEEN_RETRY_TIME_MS = Integer.valueOf(conf.getProperty("zk.retry.interval.time"));
    ZK_ROOT_PATH = conf.getProperty("zk.root.path");

    initZkClient();
  }

  private TopologyZkBean generateTopologyZkBean(TopologyBean topologyBean) {
    TopologyZkBean topologyZkBean = new TopologyZkBean();
    topologyZkBean.setTopologyName(topologyBean.getTopologyName());
    List<CompStat<ITask>> compList = topologyBean.getCompList();

    List<CompZkBean> compZkBeanList = new ArrayList<>();

    for (CompStat<ITask> compStat : compList) {
      String compName = compStat.getCompName();
      TaskType compType = compStat.getCompType();
      Class<ITask> compCls = compStat.getCompCls();
      List<CompStat> nextCompList = compStat.getNextCompList();
      Strategy strategy = compStat.getStrategy();
      List<String> nextCompNameList = new ArrayList<>();
      if (nextCompList != null) {
        for (CompStat stat : nextCompList) {
          nextCompNameList.add(stat.getCompName());
        }
      }

      compZkBeanList.add(new CompZkBean(compName, compCls, compType, nextCompNameList, strategy));
    }
    topologyZkBean.setCompList(compZkBeanList);
    return topologyZkBean;

  }

  // 初始化zk客户端
  private void initZkClient() {
    client = CuratorFrameworkFactory.builder()
        .connectString(ZK_ADDR)
        .connectionTimeoutMs(SESSION_TIME_MS)
        .retryPolicy(new RetryNTimes(RETRY, BETWEEN_RETRY_TIME_MS))
        .namespace(ZK_ROOT_PATH)
        .build();
  }

  // 提交topology
  public void submit(String topologyJarName) {

    openZkClient();
    // 该拓扑的名称
    String topologyName = topologyZkBean.getTopologyName();
    String topologyPath = "/topologies/" + topologyName;

    List<CompZkBean> compList = topologyZkBean.getCompList();

    // 向ZK写入topology节点
    createNode(topologyPath, topologyJarName);

    String statPath = topologyPath + "/stat";
    createNode(statPath, "creating");

    // 向ZK写入组件信息
    for (CompZkBean compZkBean : compList) {
      // 创建组件节点, 组件都以comp-开头
      String compPath = topologyPath + "/comp-" + compZkBean.getCompName();
      String beanData = JsonUtil.toJsonStr(compZkBean);
      createNode(compPath, beanData);

      // 写入运行节点信息
      String runNodePath = compPath + "/runNode";
      String runNodeBean = JsonUtil.toJsonStr(null);
      createNode(runNodePath, runNodeBean);
    }


    writeNode(statPath, "created");
    // 关闭连接
    closeZkClient();
  }
  // 杀死topology
  public void kill() {

  }


  // 创建该节点
  private void createNode(String path, String beanData) {
    try {
      client.create().withMode(CreateMode.PERSISTENT)
          .forPath(path, beanData.getBytes("utf-8"));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  // 创建该节点
  private void createNode(String path) {
    try {
      client.create().withMode(CreateMode.PERSISTENT)
          .forPath(path);

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  // 修改数据
  private void writeNode(String path, String beanData) {
    try {
      client.setData().forPath(path, beanData.getBytes("utf-8"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  private void closeZkClient() {
    if (!isClosed) {
      client.close();
      isClosed = true;
    }
  }

  private void openZkClient() {
    if (isClosed) {
      client.start();
      isClosed = false;
    }
  }
}
