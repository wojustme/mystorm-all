package com.wojustme.mystorm.network;

import com.wojustme.mystorm.network.handler.ClientHandler;

/**
 * 用于节点间数据通信的客户端
 * @author wojustme
 * @date 2017/7/15
 * @package com.wojustme.mystorm.network
 */
public interface IMsgClient {
  // 连接
  void connect(String host, int port);
  ClientHandler getHandler();
  void setHandler(ClientHandler handler);
}
