package com.wojustme.mystorm.master.net;

import com.wojustme.mystorm.network.IMsgClient;
import com.wojustme.mystorm.network.handler.ClientHandler;
import com.wojustme.mystorm.network.msg.MsgBean;
import com.wojustme.mystorm.network.msg.MsgType;
import com.wojustme.mystorm.network.netty.MsgNettyClient;
import com.wojustme.mystorm.slave.stat.WorkerStat;
import com.wojustme.mystorm.util.JsonUtil;
import com.wojustme.mystorm.util.ZkHandlerUtl;
import io.netty.channel.ChannelHandlerContext;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * 向worker节点发送指令
 * @author wojustme
 * @date 2017/7/21
 * @package com.wojustme.mystorm.master.net
 */
public class CommunicateToWorker {

  public static boolean sendMsg(CuratorFramework zkClient, String topologyName, Properties conf) throws Exception {

    boolean flag = true;

    String url = buildDownLoadUrl(zkClient, topologyName, conf);

    DownJarNettyClient downJarNettyClient = new DownJarNettyClient(url);
    // 创建netty客户端
    IMsgClient msgClient = new MsgNettyClient(downJarNettyClient);

    for (NetMsg workerNode : getWorkerNetMsg(zkClient)) {
      if (downJarNettyClient.isFlag()) {
        msgClient.connect(workerNode.getHost(), workerNode.getPort());
      } else {
        return false;
      }
    }
    return flag;
  }

  // 获得下载url
  private static String buildDownLoadUrl(CuratorFramework zkClient, String topologyName, Properties conf) throws Exception {

    // 获得jar文件名
    String jarFileName = ZkHandlerUtl.getData(zkClient, "/topologies/" + topologyName);

    // 下载的url
    String host = InetAddress.getLocalHost().getHostAddress();
    String port = conf.getProperty("file.http.port");
    String subUrlPath = conf.getProperty("file.download.url");
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("http://");
    stringBuilder.append(host);
    stringBuilder.append(":");
    stringBuilder.append(port);
    stringBuilder.append(subUrlPath);
    stringBuilder.append("/");
    stringBuilder.append(jarFileName);

    return stringBuilder.toString();
  }

  // 获取所有worker的ip
  private static Set<NetMsg> getWorkerNetMsg(CuratorFramework zkClient) throws Exception {
    Set<NetMsg> workerSet = new HashSet<>();
    String workersNodePath = "/nodes/workers";
    List<String> childNodeNameList = ZkHandlerUtl.getChildNodeName(zkClient, workersNodePath);
    for (String childName : childNodeNameList) {
      WorkerStat workerStat = JsonUtil.toBeanObj(ZkHandlerUtl.getData(zkClient, workersNodePath + "/" + childName), WorkerStat.class);
      workerSet.add(new NetMsg(workerStat.getWorkerHost(), workerStat.getWorkerPort()));
    }
    return workerSet;
  }
}

class NetMsg {
  private String host;
  private int port;

  public NetMsg(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }
}

class DownJarNettyClient implements ClientHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(DownJarNettyClient.class);

  private boolean flag = true;

  private final String url;

  public DownJarNettyClient(String url) {
    this.url = url;
  }

  public boolean isFlag() {
    return flag;
  }

  @Override
  public void connectHanlder(ChannelHandlerContext ctx) {
    MsgBean recvMsg = new MsgBean(MsgType.DOWNLOAD_JAR, url);
    ctx.writeAndFlush(recvMsg);
  }

  @Override
  public void readHanlder(ChannelHandlerContext ctx, MsgBean recvMsg) {
    LOGGER.info("客户端收到" + recvMsg.getData());
    if (recvMsg.getMsgType() == MsgType.DOWNLOAD_JAR_OK) {
      flag = true;
    } else {
      flag = false;
    }
    ctx.close();
  }
}
