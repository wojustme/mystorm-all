package com.wojustme.mystorm.network.netty;

import com.wojustme.mystorm.network.handler.ClientHandler;
import com.wojustme.mystorm.network.msg.MsgBean;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author wojustme
 * @date 2017/7/17
 * @package com.wojustme.mystorm.network.netty
 */
public class ClientHandlerTmpl extends ChannelInboundHandlerAdapter {

  // 客户初始化、读操作回调
  private ClientHandler clientHandler;
  ClientHandlerTmpl(ClientHandler clientHandler) {
    this.clientHandler = clientHandler;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    clientHandler.connectHanlder(ctx);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    clientHandler.readHanlder(ctx, (MsgBean) msg);
  }

}
