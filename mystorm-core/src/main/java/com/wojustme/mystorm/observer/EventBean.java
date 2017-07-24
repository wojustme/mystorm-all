package com.wojustme.mystorm.observer;

/**
 * 封装事件数据类型
 * @author wojustme
 * @date 2017/7/12
 * @package com.wojustme.mystorm.observer
 */
public class EventBean {

  private IEventType eventType;
  private Object eventMsg;

  public EventBean() {
  }

  public EventBean(IEventType eventType, Object eventMsg) {
    this.eventType = eventType;
    this.eventMsg = eventMsg;
  }

  public IEventType getEventType() {
    return eventType;
  }

  public void setEventType(IEventType eventType) {
    this.eventType = eventType;
  }

  public Object getEventMsg() {
    return eventMsg;
  }

  public void setEventMsg(Object eventMsg) {
    this.eventMsg = eventMsg;
  }

  @Override
  public String toString() {
    return "EventBean{" +
        "eventType=" + eventType +
        ", eventMsg=" + eventMsg +
        '}';
  }
}
