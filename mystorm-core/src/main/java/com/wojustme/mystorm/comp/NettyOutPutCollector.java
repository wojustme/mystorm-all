package com.wojustme.mystorm.comp;

import com.wojustme.mystorm.network.NetworkData;
import com.wojustme.mystorm.schedule.DownstreamNetNode;
import com.wojustme.mystorm.slave.network.MsgClientFactory;
import com.wojustme.mystorm.topology.Strategy;
import com.wojustme.mystorm.util.JsonUtil;
import com.wojustme.mystorm.util.KeyHashUtil;

import java.util.*;

/**
 * @author wojustme
 * @date 2017/7/20
 * @package com.wojustme.mystorm.comp
 */
public class NettyOutPutCollector implements OutPutCollector {

  // 当前发送器隶属于哪个任务
  private String taskName;
  // 当前数据流动策略
  private Strategy strategy;
  // 下游数据节点个数
  private final int nextNodeNum;
  // 下游数据节点
  private List<ServerHost> nextWorkerNodes;
  // 下游任务实例名 -> 下游网络信息
  private List<String> taskList;
  // 管理下游网络实例
  private MsgClientFactory msgClientFactory;

  public NettyOutPutCollector(String taskName, Strategy strategy, List<DownstreamNetNode> downstreamNetNodeList) {
    this.taskName = taskName;
    this.nextWorkerNodes = new ArrayList<>();
    this.taskList = new ArrayList<>();
    this.strategy = strategy;
    int i = 0;
    for (DownstreamNetNode downstreamNetNode : downstreamNetNodeList) {
      String host = downstreamNetNode.getHost();
      int port = downstreamNetNode.getPort();
      String nextTaskName = downstreamNetNode.getTaskName();
      ServerHost nextServerHost = new ServerHost(host, port);
      nextWorkerNodes.add(nextServerHost);
      taskList.add(nextTaskName);
      i++;
    }
    nextNodeNum = i;
    this.msgClientFactory = new MsgClientFactory();
  }

  @Override
  public void emit(Tuple tuple) {
    if (nextWorkerNodes.size() <= 0) {
      return;
    }
    // todo 连接发送数据
    Set<Integer> nextNodeIndexSet = new HashSet<>();
    switch (strategy) {
      case GROUP:
        // 用于分区的key
        String key = tuple.getKey();
        nextNodeIndexSet.add(KeyHashUtil.computeHashKey(key, nextNodeNum));
        break;
      case RANDOM:
        nextNodeIndexSet.add(KeyHashUtil.randomKey(nextNodeNum));
        break;
      case BROADCAST:
        for (int i = 0; i < nextNodeNum; i++) {
          nextNodeIndexSet.add(i);
        }
        break;
    }

    // 真实数据
    String data = tuple.getData();
    // 循环发送，random和group模式只发送一次，而broadcast模式发送多次
    for (int index : nextNodeIndexSet) {
      ServerHost serverHost = nextWorkerNodes.get(index);
      // 网络中传输的数据
      NetworkData networkData = new NetworkData(taskList.get(index), data);
      msgClientFactory.sendMsg(serverHost, JsonUtil.toJsonStr(networkData));
    }

  }

}
