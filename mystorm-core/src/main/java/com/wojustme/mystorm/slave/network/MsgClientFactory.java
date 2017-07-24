package com.wojustme.mystorm.slave.network;

import com.wojustme.mystorm.comp.SendDataClientHandler;
import com.wojustme.mystorm.comp.ServerHost;
import com.wojustme.mystorm.network.IMsgClient;
import com.wojustme.mystorm.network.netty.MsgNettyClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wojustme
 * @date 2017/7/23
 * @package com.wojustme.mystorm.slave.network
 */
public class MsgClientFactory {

  // 主机名和端口 -> msgClient映射
  public Map<ServerHost, IMsgClient> msgClientMap;
  // 主机名和端口 -> 客户端client引用

  public MsgClientFactory() {
    this.msgClientMap = new HashMap<>();
  }

  // 获得数据发送的客户端
  public IMsgClient getMsgClient(ServerHost serverHost) {
    IMsgClient msgClient = msgClientMap.get(serverHost);
    if (msgClient == null) {
      msgClient = new MsgNettyClient();
    }
    return msgClient;
  }

  public void sendMsg(ServerHost serverHost, String msg) {
    SendDataClientHandler sendDataClientHandler = new SendDataClientHandler(msg);
    IMsgClient msgClient = getMsgClient(serverHost);
    if (msgClient.getHandler() == null) {
      msgClient.setHandler(sendDataClientHandler);
    }
    msgClient.connect(serverHost.getHost(), serverHost.getPort());
  }
}
