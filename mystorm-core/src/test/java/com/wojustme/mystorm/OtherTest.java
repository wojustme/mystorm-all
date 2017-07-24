package com.wojustme.mystorm;

import org.junit.Test;

import java.net.InetAddress;

/**
 * @author wojustme
 * @date 2017/7/20
 * @package com.wojustme.mystorm
 */
public class OtherTest {

  @Test
  public void getIP() {
//    InetAddress ia=null;
    try {
//      ia=ia.getLocalHost();
//
//      String localname=ia.getHostName();
//      String localip=ia.getHostAddress();
//      System.out.println("本机名称是："+ localname);
      System.out.println("本机的ip是 ："+ InetAddress.getLocalHost().getHostAddress());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
