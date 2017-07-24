package com.wojustme.mystorm.topology.zk;


import java.util.List;

/**
 * 用于于zk连接
 * 保存数据序列化
 * @author wojustme
 * @date 2017/7/18
 * @package com.wojustme.mystorm.topology.zk
 */
public class TopologyZkBean {
  // 拓扑名
  private String topologyName;
  // 组件列表
  private List<CompZkBean> compList;

  public String getTopologyName() {
    return topologyName;
  }

  public void setTopologyName(String topologyName) {
    this.topologyName = topologyName;
  }

  public List<CompZkBean> getCompList() {
    return compList;
  }

  public void setCompList(List<CompZkBean> compList) {
    this.compList = compList;
  }
}
