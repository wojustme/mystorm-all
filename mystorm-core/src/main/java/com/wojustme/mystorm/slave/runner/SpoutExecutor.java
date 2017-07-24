package com.wojustme.mystorm.slave.runner;

import com.wojustme.mystorm.comp.ISpoutComp;
import com.wojustme.mystorm.comp.OutPutCollector;

/**
 * spout运行线程
 * 运行spout类型task任务
 * @author wojustme
 * @date 2017/7/17
 * @package com.wojustme.mystorm.slave.runner
 */
public class SpoutExecutor implements Runnable {

  // 线程名
  private String executorName;
  // 当前spout运行线程，一个spout只运行一个spout实例任务
  private ISpoutComp spoutInstance;
  // 该线程维护的网络引用
  private OutPutCollector collector;

  public SpoutExecutor(String executorName, ISpoutComp spoutInstance, OutPutCollector collector) {
    this.executorName = executorName;
    this.spoutInstance = spoutInstance;
    this.collector = collector;
    spoutInstance.init(collector);
  }

  @Override
  public void run() {
    spoutInstance.nextTuple();
  }
}
