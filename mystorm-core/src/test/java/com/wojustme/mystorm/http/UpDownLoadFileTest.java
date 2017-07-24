package com.wojustme.mystorm.http;


import com.wojustme.mystorm.util.http.UpDownLoadUtil;
import org.junit.Test;

import java.util.UUID;


/**
 * @author wojustme
 * @date 2017/7/12
 * @package com.wojustme.mystorm
 */
public class UpDownLoadFileTest {


  @Test
  public void upload() {
    boolean b = UpDownLoadUtil.uploadFile("http://127.0.0.1:9527/upload", "/Users/wojustme/test/mystorm-demo.jar", "com", "hello");
    System.out.println(b);
  }

  @Test
  public void download() {

    boolean b = UpDownLoadUtil.downloadFile("http://127.0.0.1:9527/download", "123.pptx", "/Users/wojustme/test/123");
    System.out.println(b);
  }

  @Test
  public void testUUID() {

    UUID uuid = UUID.randomUUID();
    System.out.println(uuid.toString());

  }

}
