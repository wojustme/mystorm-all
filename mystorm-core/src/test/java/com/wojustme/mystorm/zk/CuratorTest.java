package com.wojustme.mystorm.zk;


import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author wojustme
 * @date 2017/7/3
 * @package com.wojustme.mystorm.zk
 */
public class CuratorTest {

  private final String zk_addr = "localhost:2181";
  private CuratorFramework client;

  @Before
  public void init() {
    client = CuratorFrameworkFactory.newClient(
        zk_addr,
        new RetryNTimes(1, 5000)
    );
  }

  @After
  public void destory() {
    if (client != null) {
      client.close();
    }
    client = null;
  }

  @Test
  // 创建临时节点
  public void CreateNode() throws Exception {
    client.start();
    client.create().withMode(CreateMode.EPHEMERAL).forPath("/mystorm/topologies/topology-2", "http://localhost:8888/download/123.pptx".getBytes());
//    Thread.sleep(Integer.MAX_VALUE);
  }


  @Test
  // 创建临时节点
  public void CheckNode() throws Exception {
    client.start();
    assert client.checkExists().forPath("/hello") == null;
  }

  @Test
  public void watchNode() throws Exception {
    client.start();

    PathChildrenCache nodeCache = new PathChildrenCache(client, "/hello", true);
    nodeCache.start(true);
    nodeCache.getListenable().addListener(new PathChildrenCacheListener() {
      @Override
      public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
        switch (event.getType()) {
          case CHILD_ADDED:
            System.out.println("add...");
            break;
          default:
            System.out.println("error...");
            break;
        }
      }
    });

    Thread.sleep(Integer.MAX_VALUE);

  }

  @Test
  public void delPath() {
    String path = "/mystorm";

    client.start();
    try {
      // 清空所有
      client.delete().deletingChildrenIfNeeded().forPath(path);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
