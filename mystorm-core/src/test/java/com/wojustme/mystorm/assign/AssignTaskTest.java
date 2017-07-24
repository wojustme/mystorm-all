package com.wojustme.mystorm.assign;

import com.wojustme.mystorm.schedule.AssignTaskUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.junit.Before;
import org.junit.Test;

/**
 * 测试
 * @author wojustme
 * @date 2017/7/20
 * @package com.wojustme.mystorm.assign
 */
public class AssignTaskTest {

  CuratorFramework zkClient;

  @Before
  public void initZkClient() {
    zkClient = CuratorFrameworkFactory.builder()
        .connectString("localhost:2181")
        .connectionTimeoutMs(5000)
        .retryPolicy(new RetryNTimes(3, 2000))
        .namespace("mystorm")
        .build();
    zkClient.start();
  }

  @Test
  public void assign() throws Exception {
    AssignTaskUtil.assginTask(zkClient, "wordcount");
  }

}
