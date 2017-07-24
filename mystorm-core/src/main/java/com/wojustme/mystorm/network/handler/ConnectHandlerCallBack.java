package com.wojustme.mystorm.network.handler;

import io.netty.channel.ChannelHandlerContext;

/**
 * 网络连接成功处理
 * @author wojustme
 * @date 2017/7/17
 * @package com.wojustme.mystorm.network.handler
 */
public interface ConnectHandlerCallBack {
  void connectHanlder(ChannelHandlerContext ctx);
}
