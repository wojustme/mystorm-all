package com.wojustme.mystorm.demo;

import com.wojustme.mystorm.topology.Strategy;
import com.wojustme.mystorm.topology.SubmitConfTemplate;
import com.wojustme.mystorm.topology.TopologyBean;
import com.wojustme.mystorm.topology.TopologyBuilder;
import com.wojustme.mystorm.topology.zk.TopologyManager;

import java.util.Properties;

/**
 * @author wojustme
 * @date 2017/7/19
 * @package com.wojustme.mystorm.demo
 */
public class TopologyDemoMain {

  public static void main(String[] args) {

    String topologyName = args[0];
    String topologyJarName = args[1];

    TopologyBuilder builder = new TopologyBuilder(topologyName);

    builder.setSpout("wcSpout", new WcSpout());
    builder.setBolt("splitBolt", new SplitBolt(), 2);
    builder.setBolt("countBolt", new CountBolt(), 4);
    builder.setBolt("printOutBolt", new PrintOutBolt());

    builder.setDataFlow("wcSpout", Strategy.RANDOM, "splitBolt");
    builder.setDataFlow("splitBolt", Strategy.GROUP, "countBolt");
    builder.setDataFlow("countBolt", Strategy.RANDOM, "printOutBolt");

    /**
     * spout -> 1
     * bolt -> 2 + 4 + 1 = 7
     */

    TopologyBean topologyBean = builder.build();
    Properties properties = new SubmitConfTemplate().getProperties();
    TopologyManager topologyManager = new TopologyManager(topologyBean, properties);
    topologyManager.submit(topologyJarName);
  }

}

