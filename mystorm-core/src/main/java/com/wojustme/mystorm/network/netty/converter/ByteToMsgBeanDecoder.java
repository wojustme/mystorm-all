package com.wojustme.mystorm.network.netty.converter;

import com.wojustme.mystorm.network.msg.MsgBean;
import com.wojustme.mystorm.network.msg.MsgType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 自定义的解码器
 * 从btye数据格式转成bean数据格式
 * @author wojustme
 * @date 2017/7/17
 * @package com.wojustme.mystorm.network.netty.converter
 */
public class ByteToMsgBeanDecoder extends ByteToMessageDecoder {


  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    int length = in.readableBytes();
    MsgType type = MsgType.getMsgType(in.readInt());
    byte[] bytes = new byte[length - 4];
    in.readBytes(bytes);
    out.add(new MsgBean(type, new String(bytes)));
  }
}
