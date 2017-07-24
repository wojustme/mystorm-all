package com.wojustme.mystorm.network;

/**
 * 用于节点间数据通信的服务端
 * @author wojustme
 * @date 2017/7/15
 * @package com.wojustme.mystorm.network
 */
public interface IMsgServer extends Runnable {

  // 在某个端口启动
  void startServer(int port);
}
