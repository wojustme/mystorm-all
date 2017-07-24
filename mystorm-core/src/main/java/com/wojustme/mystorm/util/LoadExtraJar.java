package com.wojustme.mystorm.util;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author wojustme
 * @date 2017/7/19
 * @package com.wojustme.mystorm.util
 */
public final class LoadExtraJar {

  public static URLClassLoader loadJars(String jarFilePath) throws Exception {
    File file = new File(jarFilePath);
    URL url= file.toURI().toURL();
    return new URLClassLoader(new URL[] {url});
  }

  public static Class getTargetCls(String jarFilePath, String targetClsName) throws Exception {
    return loadJars(jarFilePath).loadClass(targetClsName);
  }

}
