package com.wojustme.mystorm.slave.util;

import com.wojustme.mystorm.util.ZkHandlerUtl;
import com.wojustme.mystorm.util.http.UpDownLoadUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;

import java.io.File;
import java.util.Properties;

/**
 * 下载topology的jar包
 * @author wojustme
 * @date 2017/7/21
 * @package com.wojustme.mystorm.slave.util
 */
public class DownLoadJarHelper {

  public static boolean downLoadJar(String urlWithFile, Properties config) {

    String[] split = urlWithFile.split("/");
    String jarFileName = split[split.length - 1];
    String url = urlWithFile.split("/" + jarFileName)[0];

    // 存储目录
    File saveFileDir = new File(config.getProperty("worker.jarFile.path"));
    if (!saveFileDir.exists()) {
      saveFileDir.mkdir();
    }

    // 下载文件

    boolean b = UpDownLoadUtil.downloadFile(url, jarFileName, saveFileDir.getAbsolutePath());

    return b;

  }

}
