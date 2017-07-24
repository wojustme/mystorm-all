package com.wojustme.mystorm.topology;

import com.wojustme.mystorm.comp.ITask;

import java.util.List;

/**
 * 最终topology的Bean
 * @author wojustme
 * @date 2017/7/18
 * @package com.wojustme.mystorm.topology
 */
public class TopologyBean {

  private String topologyName;
  private List<CompStat<ITask>> compList;

  public TopologyBean(String topologyName, List<CompStat<ITask>> compList) {
    this.topologyName = topologyName;
    this.compList = compList;
  }

  public String getTopologyName() {
    return topologyName;
  }

  public List<CompStat<ITask>> getCompList() {
    return compList;
  }
}
