package com.wojustme.mystorm.slave.data;

import com.wojustme.mystorm.network.NetworkData;
import com.wojustme.mystorm.network.handler.ServerHandler;
import com.wojustme.mystorm.network.msg.MsgBean;
import com.wojustme.mystorm.network.msg.MsgType;
import com.wojustme.mystorm.observer.EventBean;
import com.wojustme.mystorm.slave.WorkerSubject;
import com.wojustme.mystorm.slave.event.WorkerEventType;
import com.wojustme.mystorm.slave.util.DownLoadJarHelper;
import com.wojustme.mystorm.util.JsonUtil;
import io.netty.channel.ChannelHandlerContext;

import java.util.Properties;

/**
 * @author wojustme
 * @date 2017/7/17
 * @package com.wojustme.mystorm.slave.data
 */
public class NetworkHandler implements ServerHandler {

  private Properties config;
  // 用于观察的主题
  private final WorkerSubject workerSubject;

  public NetworkHandler(Properties config, WorkerSubject workerSubject) {
    this.config = config;
    this.workerSubject = workerSubject;
  }

  @Override
  public void readHanlder(ChannelHandlerContext ctx, MsgBean recvMsg) {

    switch (recvMsg.getMsgType()) {
      case DOWNLOAD_JAR:
        DownLoadJarHelper.downLoadJar(recvMsg.getData(), config);
        ctx.writeAndFlush(new MsgBean(MsgType.DOWNLOAD_JAR_OK, "下载成功"));
        workerSubject.nodifyObservers(new EventBean(WorkerEventType.DOWNLOAD_JAR_FILE_OK, null));
        break;
      case SEND_DATA:
        // 接收到数据
        NetworkData networkData = JsonUtil.toBeanObj(recvMsg.getData(), NetworkData.class);
        ctx.writeAndFlush(new MsgBean(MsgType.RECV_OK, "处理成功"));
        workerSubject.nodifyObservers(new EventBean(WorkerEventType.RECV_NEW_DATA, networkData));
        break;
    }

  }
}
