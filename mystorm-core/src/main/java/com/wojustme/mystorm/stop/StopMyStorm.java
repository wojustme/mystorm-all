package com.wojustme.mystorm.stop;

import com.wojustme.mystorm.util.PropsUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;

import java.util.Properties;

/**
 * @author wojustme
 * @date 2017/7/24
 * @package com.wojustme.mystorm.stop
 */
public class StopMyStorm {

  public static void main(String[] args) {

    Properties conf = PropsUtil.loadProps("mystorm.properties", true);;

    if (args.length > 1) {
      throw new RuntimeException("配置设置错误");
    }
    if (args.length == 1) {
      String confDir = args[0];
      conf = PropsUtil.loadProps(confDir + "/mystorm.properties");
    }


    CuratorFramework client = CuratorFrameworkFactory.newClient(
        conf.getProperty("zk.addr"),
        new RetryNTimes(1, 5000)
    );
    String path = "/mystorm";
    client.start();
    try {
      // 清空所有
      client.delete().deletingChildrenIfNeeded().forPath(path);
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("del /mystorm ok");
  }
}
