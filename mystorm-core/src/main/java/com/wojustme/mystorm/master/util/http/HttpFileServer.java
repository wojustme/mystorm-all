package com.wojustme.mystorm.master.util.http;

import com.wojustme.mystorm.master.MasterSubject;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.io.File;
import java.util.Properties;

/**
 * 用于jar的上传和下载服务
 * @author wojustme
 * @date 2017/7/14
 * @package com.wojustme.mystorm.master.util.http
 */
public class HttpFileServer implements Runnable {

  private final MasterSubject masterSubject;
  private final Properties config;

  public HttpFileServer(MasterSubject masterSubject, Properties config) {
    this.masterSubject = masterSubject;
    this.config = config;

    // 检查文件夹正确性
    File file = new File(config.getProperty("file.directory"));
    if (file.exists()) {
      if (file.isFile()) {
        new RuntimeException("目录存在，但却是文件类型，失败");
      }
    } else {
      file.mkdir();
    }

  }

  public void run() {

    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workGroup = new NioEventLoopGroup();

    try {
      ServerBootstrap serverBootstrap = new ServerBootstrap();
      serverBootstrap.group(bossGroup, workGroup)
          .channel(NioServerSocketChannel.class)
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              ChannelPipeline pipeline = ch.pipeline();
              pipeline.addLast(new HttpRequestDecoder());
              pipeline.addLast(new HttpResponseEncoder());
              pipeline.addLast(new ChunkedWriteHandler());
              pipeline.addLast(new FileHandler(masterSubject, config));
            }
          });

      ChannelFuture sync = serverBootstrap.bind(Integer.valueOf(config.getProperty("file.http.port"))).sync();
      sync.channel().closeFuture().sync();

    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
