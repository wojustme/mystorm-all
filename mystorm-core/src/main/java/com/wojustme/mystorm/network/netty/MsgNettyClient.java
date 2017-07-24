package com.wojustme.mystorm.network.netty;


import com.wojustme.mystorm.network.netty.converter.ByteToMsgBeanDecoder;
import com.wojustme.mystorm.network.IMsgClient;
import com.wojustme.mystorm.network.handler.ClientHandler;
import com.wojustme.mystorm.network.msg.MsgBean;
import com.wojustme.mystorm.network.msg.MsgType;
import com.wojustme.mystorm.network.netty.converter.MsgBeanToByteEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 使用Netty实现通信客户端
 * @author wojustme
 * @date 2017/7/15
 * @package com.wojustme.mystorm.network.netty
 */
public class MsgNettyClient implements IMsgClient {
  // 收发处理回调
  private ClientHandler clientHandler;

  public MsgNettyClient() {
  }

  public MsgNettyClient(ClientHandler clientHandler) {
    this.clientHandler = clientHandler;
  }

  @Override
  public void connect(String host, int port) {
    EventLoopGroup group = new NioEventLoopGroup();

    try {
      Bootstrap b = new Bootstrap();
      b.group(group).channel(NioSocketChannel.class)
          .option(ChannelOption.TCP_NODELAY, true)
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              ch.pipeline().addLast(new ByteToMsgBeanDecoder());
              ch.pipeline().addLast(new MsgBeanToByteEncoder());

              ch.pipeline().addLast(new ClientHandlerTmpl(clientHandler));
            }
          });
      ChannelFuture f = b.connect(host, port).sync();
      f.channel().closeFuture().sync();
    } catch (InterruptedException e) {

    } finally {
      group.shutdownGracefully();
    }
  }

  @Override
  public void setHandler(ClientHandler handler) {
    this.clientHandler = handler;
  }

  @Override
  public ClientHandler getHandler() {
    return clientHandler;
  }
}
