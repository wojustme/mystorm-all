package com.wojustme.mystorm.comp;

/**
 * topology的spout节点接口
 * @author wojustme
 * @date 2017/7/17
 * @package com.wojustme.mystorm.comp
 */
public interface ISpoutComp extends ITask {
  // 用于制造下一条数据
  void nextTuple();

  @Override
  default TaskType getTaskType() {
    return TaskType.SPOUT;
  }
}
