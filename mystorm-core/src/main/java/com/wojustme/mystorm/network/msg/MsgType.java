package com.wojustme.mystorm.network.msg;

/**
 * 接收到数据类型
 * 每次修改需要同时修改netty的编解码器
 * @author wojustme
 * @date 2017/7/17
 * @package com.wojustme.mystorm.network.msg
 */
public enum MsgType {
  // 默认
  DEFAULT(0),
  HELLO(1),
  // 发送数据
  SEND_DATA(2),
  // 接收成功
  RECV_OK(3),
  // master节点通知下载jar包
  DOWNLOAD_JAR(4),
  // 返回master节点，下载成功
  DOWNLOAD_JAR_OK(5);

  MsgType(int i) {
  }
  public static MsgType getMsgType(int i) {
    switch (i) {
      case 0:
        return DEFAULT;
      case 1:
        return HELLO;
      case 2:
        return SEND_DATA;
      case 3:
        return RECV_OK;
      case 4:
        return DOWNLOAD_JAR;
      case 5:
        return DOWNLOAD_JAR_OK;
      default:
        return DEFAULT;
    }
  }
}
