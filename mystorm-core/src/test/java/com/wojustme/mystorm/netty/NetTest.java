package com.wojustme.mystorm.netty;

import com.wojustme.mystorm.network.IMsgClient;
import com.wojustme.mystorm.network.handler.ClientHandler;
import com.wojustme.mystorm.network.msg.MsgBean;
import com.wojustme.mystorm.network.msg.MsgType;
import com.wojustme.mystorm.network.netty.MsgNettyClient;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Test;

/**
 * @author wojustme
 * @date 2017/7/21
 * @package com.wojustme.mystorm.netty
 */
public class NetTest {

  @Test
  public void sendMsg() {
    String url ="http://localhost:9527/download/mystorm-demo.jar";
    // 创建netty客户端
    IMsgClient msgClient = new MsgNettyClient(new ClientHandler() {
      @Override
      public void connectHanlder(ChannelHandlerContext ctx) {
        MsgBean recvMsg = new MsgBean(MsgType.DOWNLOAD_JAR, url);
        System.out.println(recvMsg);
        ctx.writeAndFlush(recvMsg);
      }

      @Override
      public void readHanlder(ChannelHandlerContext ctx, MsgBean recvMsg) {
        System.out.println("++++++++++++++++++++++++++++++");
        System.out.println("客户端收到" + recvMsg.getData());
        System.out.println("++++++++++++++++++++++++++++++");
        ctx.close();
      }
    });

    msgClient.connect("localhost", 9528);
  }
}
