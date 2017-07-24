package com.wojustme.mystorm.comp;

/**
 * topology的任务接口
 * 包含spout和bolt两种
 * @author wojustme
 * @date 2017/7/17
 * @package com.wojustme.mystorm.comp
 */
public interface ITask {

  // 每一个任务初始化调用方法
  void init(OutPutCollector collector);

  // 获得当前任务类型
  TaskType getTaskType();
}
