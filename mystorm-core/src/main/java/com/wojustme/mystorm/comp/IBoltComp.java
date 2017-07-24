package com.wojustme.mystorm.comp;

/**
 * topology的spout节点接口
 * @author wojustme
 * @date 2017/7/17
 * @package com.wojustme.mystorm.comp
 */
public interface IBoltComp extends ITask {

  // 对于输入的数据进行操作
  void execute(String data);

  @Override
  default TaskType getTaskType() {
    return TaskType.BOLT;
  }

}
