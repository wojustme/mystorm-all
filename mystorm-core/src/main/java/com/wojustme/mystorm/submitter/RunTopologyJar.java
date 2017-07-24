package com.wojustme.mystorm.submitter;

import com.wojustme.mystorm.master.util.http.UpLoadJarFileMsg;
import com.wojustme.mystorm.util.LoadExtraJar;

import java.lang.reflect.Method;
import java.net.URLClassLoader;

/**
 * @author wojustme
 * @date 2017/7/19
 * @package com.wojustme.mystorm.submitter
 */
public class RunTopologyJar {

  private String jarFilePath;
  private String jarMainCls;
  private String topologyName;
  private String topologyJarName;

  public RunTopologyJar(UpLoadJarFileMsg upLoadJarFileMsg) {

    this.jarFilePath = upLoadJarFileMsg.getFilePath();
    this.jarMainCls = upLoadJarFileMsg.getMainCls();
    this.topologyJarName = upLoadJarFileMsg.getFileName();
    this.topologyName = upLoadJarFileMsg.getTopologyName();

  }

  public boolean runMain() {
    boolean flag = false;
    try {

      Class clazz = LoadExtraJar.getTargetCls(jarFilePath, jarMainCls);

      Method mainMethod = clazz.getMethod("main", String[].class);

      // 暂时只有一个输入参数为topology名
      String[] realMainArgs = new String[] {topologyName, topologyJarName};

      mainMethod.invoke(null, (Object) realMainArgs);

      flag = true;
    } catch (Exception e) {
      e.printStackTrace();
      flag = false;
    }
    return flag;

  }


}
