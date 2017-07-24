package com.wojustme.mystorm.slave.network;


import com.wojustme.mystorm.network.IMsgServer;
import com.wojustme.mystorm.network.handler.ServerHandler;
import com.wojustme.mystorm.network.netty.ServerHandlerTmpl;
import com.wojustme.mystorm.network.netty.converter.ByteToMsgBeanDecoder;
import com.wojustme.mystorm.network.netty.converter.MsgBeanToByteEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Properties;

/**
 * 使用Netty实现通信客户端
 * @author wojustme
 * @date 2017/7/15
 * @package com.wojustme.mystorm.slave
 */
public class MsgNettyServer implements IMsgServer {

  // 收发处理回调
  private ServerHandler serverHandler;
  // mystorm的配置信息
  private final Properties conf;

  public MsgNettyServer(Properties conf, ServerHandler serverHandler) {
    this.conf = conf;
    this.serverHandler = serverHandler;
  }

  @Override
  public void run() {
    int port = Integer.valueOf(conf.getProperty("worker.port"));
    startServer(port);
  }

  @Override
  public void startServer(int port) {
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .option(ChannelOption.SO_BACKLOG, 1024)
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              // 自定义的编解码器
              ch.pipeline().addLast(new ByteToMsgBeanDecoder());
              ch.pipeline().addLast(new MsgBeanToByteEncoder());
              ch.pipeline().addLast(new ServerHandlerTmpl(serverHandler));
            }
          });

      ChannelFuture f = b.bind(port).sync();
      f.channel().closeFuture().sync();

    } catch (InterruptedException e) {

    } finally {
      bossGroup.shutdownGracefully();
      workerGroup.shutdownGracefully();
    }
  }

}
