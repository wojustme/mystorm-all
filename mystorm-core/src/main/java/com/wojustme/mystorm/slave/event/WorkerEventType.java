package com.wojustme.mystorm.slave.event;

import com.wojustme.mystorm.observer.IEventType;

/**
 * @author wojustme
 * @date 2017/7/12
 * @package com.wojustme.mystorm.slave.event
 */
public enum WorkerEventType implements IEventType {
  // 成功下载一个topology的jar包
  DOWNLOAD_JAR_FILE_OK,
  NEW_TASK_ASSIGNED,
  RECV_NEW_DATA
}
