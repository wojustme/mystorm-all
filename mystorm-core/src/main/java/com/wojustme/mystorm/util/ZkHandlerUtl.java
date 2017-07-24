package com.wojustme.mystorm.util;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import java.util.List;

/**
 * @author wojustme
 * @date 2017/7/21
 * @package com.wojustme.mystorm.util
 */
public final class ZkHandlerUtl {


  // 向节点写数据
  public static void writeData(CuratorFramework zkClient, String path, String data) throws Exception{
    zkClient.setData().forPath(path, data.getBytes("utf-8"));
  }

  public static void createData(CuratorFramework zkClient, String path, String data, CreateMode createMode) throws Exception {
    zkClient.create().withMode(createMode).forPath(path, data.getBytes("utf-8"));
  }

  public static void createData(CuratorFramework zkClient, String path, CreateMode createMode) throws Exception {
    zkClient.create().creatingParentsIfNeeded().withMode(createMode).forPath(path);
  }

  public static String getData(CuratorFramework zkClient, String path) throws Exception {
    return new String(zkClient.getData().forPath(path), "utf-8");
  }


  public static List<String> getChildNodeName(CuratorFramework zkClient, String path) throws Exception {
    return zkClient.getChildren().forPath(path);
  }
}
