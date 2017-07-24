package com.wojustme.mystorm.jarRunner;

import com.wojustme.mystorm.api.ISpout;
import com.wojustme.mystorm.comp.ISpoutComp;
import com.wojustme.mystorm.util.LoadExtraJar;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author wojustme
 * @date 2017/7/19
 * @package com.wojustme.mystorm.jarRunner
 */

public class RunJarTest {


  @Test
  public void runJar() throws Exception{

    // 动态加载jar，解析类
    URLClassLoader urlClassLoader = LoadExtraJar.loadJars("/Users/wojustme/codes/mycode/mystorm-all/files/mystorm-demo.jar");
    Class clazz = urlClassLoader.loadClass("com.wojustme.mystorm.demo.TopologyDemoMain");

    Method mainMethod = clazz.getMethod("main", String[].class);

    String[] args = new String[10];
    mainMethod.invoke(null, (Object) args);

  }

  @Test
  public void getCls() throws Exception {
    Class targetCls = LoadExtraJar.getTargetCls("/Users/wojustme/test/mystorm-demo.jar", "com.wojustme.mystorm.demo2.WcSpout");
    ISpoutComp o = (ISpoutComp) targetCls.newInstance();
    System.out.println(o);
  }

}
