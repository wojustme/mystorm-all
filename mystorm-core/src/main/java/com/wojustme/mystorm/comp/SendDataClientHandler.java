package com.wojustme.mystorm.comp;

import com.wojustme.mystorm.network.handler.ClientHandler;
import com.wojustme.mystorm.network.msg.MsgBean;
import com.wojustme.mystorm.network.msg.MsgType;
import io.netty.channel.ChannelHandlerContext;

/**
 * 各个worker节点间数据收发
 * @author wojustme
 * @date 2017/7/23
 * @package com.wojustme.mystorm.comp
 */
public class SendDataClientHandler implements ClientHandler {

  private String msgStr;

  public SendDataClientHandler(String msgStr) {
    this.msgStr = msgStr;
  }

  @Override
  public void connectHanlder(ChannelHandlerContext ctx) {

    MsgBean recvMsg = new MsgBean(MsgType.SEND_DATA, msgStr);
    ctx.writeAndFlush(recvMsg);
  }

  @Override
  public void readHanlder(ChannelHandlerContext ctx, MsgBean recvMsg) {
    if (recvMsg.getMsgType() == MsgType.RECV_OK) {
      ctx.close();
    }
  }
}
