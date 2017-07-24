package com.wojustme.mystorm.network.netty;

import com.wojustme.mystorm.network.handler.ReadHandlerCallBack;
import com.wojustme.mystorm.network.msg.MsgBean;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author wojustme
 * @date 2017/7/17
 * @package com.wojustme.mystorm.network.netty
 */
public class ServerHandlerTmpl extends ChannelInboundHandlerAdapter {

  // 收发处理回调
  private ReadHandlerCallBack hc;

  public ServerHandlerTmpl(ReadHandlerCallBack hc) {
    this.hc = hc;
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    hc.readHanlder(ctx, (MsgBean) msg);
  }
}
