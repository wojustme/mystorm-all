package com.wojustme.mystorm.master.event;

import com.wojustme.mystorm.observer.IEventType;

/**
 * @author wojustme
 * @date 2017/7/12
 * @package com.wojustme.mystorm.master.event
 */
public enum MasterEventType implements IEventType {

  UPLOAD_FLE_OK,
  INIT_TOPOLOGY_OK,
  // worker节点是否下载成功
  WORKERS_DOWNLOAD_OK,
  WORKERS_DOWNLOAD_FAIL,
  // 安排任务成功
  ASSGINTASK_OK

}
