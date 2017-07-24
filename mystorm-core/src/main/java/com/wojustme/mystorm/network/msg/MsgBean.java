package com.wojustme.mystorm.network.msg;

/**
 * @author wojustme
 * @date 2017/7/17
 * @package com.wojustme.mystorm.network.msg
 */
public class MsgBean {

  private MsgType msgType;
  private String data;

  public MsgBean(MsgType msgType, String data) {
    this.msgType = msgType;
    this.data = data;
  }

  public MsgType getMsgType() {
    return msgType;
  }

  public String getData() {
    return data;
  }


  @Override
  public String toString() {
    return "MsgBean{" +
        "msgType=" + msgType +
        ", data='" + data + '\'' +
        '}';
  }
}
