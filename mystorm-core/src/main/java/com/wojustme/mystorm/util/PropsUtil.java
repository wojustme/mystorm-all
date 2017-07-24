package com.wojustme.mystorm.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 属性工具类
 * @author wojustme
 * @date 2017/7/12
 * @package com.wojustme.mystorm.util
 */
public final class PropsUtil {


  // 是否通过资源文件加载
  public static Properties loadProps(String fileName, boolean isCurrentClassLoader) {
    Properties props = null;
    InputStream is = null;
    if (!isCurrentClassLoader) {
      return loadProps(fileName);
    }
    try {
      is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
      props =loadProps(is);
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return props;
  }

  // 通过流加载，私有
  private static Properties loadProps(InputStream is) {
    Properties props = null;
    try {
      if (is == null) {
        throw new FileNotFoundException("file is not found");
      }
      props = new Properties();
      props.load(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return props;
  }

  // 指定文件
  public static Properties loadProps(String fileName) {

    Properties props = null;
    InputStream is = null;
    try {
      is = new FileInputStream(fileName);
      props = loadProps(is);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return props;
  }
}
