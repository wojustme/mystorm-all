package com.wojustme.mystorm.demo2;

import com.wojustme.mystorm.topology.Strategy;
import com.wojustme.mystorm.topology.SubmitConfTemplate;
import com.wojustme.mystorm.topology.TopologyBean;
import com.wojustme.mystorm.topology.TopologyBuilder;
import com.wojustme.mystorm.topology.zk.TopologyManager;

import java.util.Properties;

/**
 * @author wojustme
 * @date 2017/7/23
 * @package com.wojustme.mystorm.demo2
 */
public class TopologyDemoMain {

  public static void main(String[] args) {
    String topologyName = args[0];
    String topologyJarName = args[1];
    TopologyBuilder builder = new TopologyBuilder(topologyName);
    builder.setSpout("wcSpout2", new WcSpout());
    builder.setBolt("printBolt", new PrintBolt());

    builder.setDataFlow("wcSpout2", Strategy.RANDOM, "printBolt");

    TopologyBean topologyBean = builder.build();
    Properties properties = new SubmitConfTemplate().getProperties();
    TopologyManager topologyManager = new TopologyManager(topologyBean, properties);
    topologyManager.submit(topologyJarName);
  }
}
