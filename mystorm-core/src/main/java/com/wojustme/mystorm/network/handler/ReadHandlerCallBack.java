package com.wojustme.mystorm.network.handler;

import com.wojustme.mystorm.network.msg.MsgBean;
import io.netty.channel.ChannelHandlerContext;

/**
 * 网络读取处理
 * @author wojustme
 * @date 2017/7/17
 * @package com.wojustme.mystorm.network.handler
 */
public interface ReadHandlerCallBack {
  void readHanlder(ChannelHandlerContext ctx, MsgBean recvMsg);
}
