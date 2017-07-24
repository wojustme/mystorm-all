package com.wojustme.mystorm.network.netty.converter;

import com.wojustme.mystorm.network.msg.MsgBean;
import com.wojustme.mystorm.network.msg.MsgType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 自定义的编码器
 * 从bean数据格式转成byte数据格式
 * @author wojustme
 * @date 2017/7/17
 * @package com.wojustme.mystorm.network.netty.converter
 */
public class MsgBeanToByteEncoder extends MessageToByteEncoder<MsgBean> {

  @Override
  protected void encode(ChannelHandlerContext ctx, MsgBean msg, ByteBuf out) throws Exception {
    MsgType msgType = msg.getMsgType();
    String data = msg.getData();
    int i = 0;
    switch (msgType) {
      case DEFAULT:
        i = 0;
        break;
      case HELLO:
        i = 1;
        break;
      case SEND_DATA:
        i = 2;
        break;
      case RECV_OK:
        i = 3;
        break;
      case DOWNLOAD_JAR:
        i = 4;
        break;
      case DOWNLOAD_JAR_OK:
        i = 5;
        break;
      default:
        i = 0;
    }
    out.writeInt(i);
    out.writeBytes(data.getBytes("UTF-8"));
  }
}
