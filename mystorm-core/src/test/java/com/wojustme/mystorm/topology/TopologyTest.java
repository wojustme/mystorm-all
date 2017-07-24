package com.wojustme.mystorm.topology;

import com.wojustme.mystorm.comp.IBoltComp;
import com.wojustme.mystorm.comp.ISpoutComp;
import com.wojustme.mystorm.comp.ITask;
import com.wojustme.mystorm.topology.zk.TopologyManager;
import com.wojustme.mystorm.util.PropsUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Properties;

/**
 * @author wojustme
 * @date 2017/7/18
 * @package com.wojustme.mystorm.topology
 */
public class TopologyTest {

  ISpoutComp dbSpout;
  ISpoutComp mqSpout;
  IBoltComp splitBolt;
  IBoltComp countBolt;
  IBoltComp printBolt;

  @Before
  public void initComp() {
    dbSpout = new DbSpout("dbSpout");
    mqSpout = new MqSpout("mqSpout");
    splitBolt = new SplitBolt("splitBolt");
    countBolt = new CountBolt("countBolt");
    printBolt = new PrintBolt("printBolt");
  }

  private TopologyBean getTopology() {
    TopologyBuilder builder = new TopologyBuilder("wordcount");

    builder.setSpout("dbSpout", dbSpout);
    builder.setSpout("mqSpout", mqSpout);
    builder.setBolt("splitBolt", splitBolt);
    builder.setBolt("countBolt", countBolt, 2);
    builder.setBolt("printBolt", printBolt, 2);

    builder.setDataFlow("dbSpout", Strategy.RANDOM, "splitBolt");
    builder.setDataFlow("mqSpout", Strategy.RANDOM, "splitBolt");
    builder.setDataFlow("splitBolt", Strategy.GROUP, "countBolt");
    builder.setDataFlow("countBolt", Strategy.RANDOM, "printBolt");


    return builder.build();
  }

  @Test
  public void buildTopology() {
    TopologyBean topologyBean = getTopology();

    List<CompStat<ITask>> allComps = topologyBean.getCompList();
    for (CompStat<ITask> compStat : allComps) {
      System.out.println("该组件名 -> " + compStat.getCompName());
      System.out.println("该组件类型 -> " + compStat.getCompType());
      System.out.println("该组件运行类 -> " + compStat.getCompCls());
      System.out.println("该组件执行策略 -> " + compStat.getStrategy());
      if (compStat.getNextCompList() == null) {
        System.out.println("无下游节点");
      } else {
        System.out.println("下游节点有");
        for (CompStat<ITask> stat : compStat.getNextCompList()) {
          System.out.println(stat.getCompName());
        }
      }
      System.out.println("===================== 分隔符 =====================");
    }

  }


  @Test
  public void submitTopology() {
    TopologyBean topologyBean = getTopology();
    Properties properties = PropsUtil.loadProps("mystorm.properties", true);
    TopologyManager topologyManager = new TopologyManager(topologyBean, properties);
    topologyManager.submit("fds");
  }
}
