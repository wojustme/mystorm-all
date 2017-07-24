package com.wojustme.mystorm.submitter;

import com.wojustme.mystorm.util.http.UpDownLoadUtil;


/**
 * 任务提交帮助文件
 * @author wojustme
 * @date 2017/7/19
 * @package com.wojustme.mystorm.submitter
 */
public class SubmiterHelper {

  // jar包所在路径
  private String jarFilePath;
  // 运行主类
  private String mainClass;
  // master节点
  private String master;
  // topology名
  private String topologyName;

  public String getJarFilePath() {
    return jarFilePath;
  }

  public void setJarFilePath(String jarFilePath) {
    this.jarFilePath = jarFilePath;
  }

  public String getMainClass() {
    return mainClass;
  }

  public void setMainClass(String mainClass) {
    this.mainClass = mainClass;
  }

  public String getMaster() {
    return master;
  }

  public void setMaster(String master) {
    this.master = master;
  }

  public String getTopologyName() {
    return topologyName;
  }

  public void setTopologyName(String topologyName) {
    this.topologyName = topologyName;
  }

  private boolean check() {
    return checkStr(jarFilePath) && checkStr(mainClass) && checkStr(topologyName) && checkStr(master);
  }
  private boolean checkStr(String s) {
    if (s != null && !s.trim().equals("")) {
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return "SubmiterHelper{" +
        "jarFilePath='" + jarFilePath + '\'' +
        ", mainClass='" + mainClass + '\'' +
        ", master='" + master + '\'' +
        ", topologyName='" + topologyName + '\'' +
        '}';
  }

  public static void main(String[] args) {
    SubmiterHelper submiterHelper = new SubmiterHelper();
    String nextNeedFiled = null;
    boolean hasSettedJar = false;
    for (String s : args) {
      if (nextNeedFiled != null && !nextNeedFiled.equals("")) {
        switch (nextNeedFiled) {
          case "mainClass":
            submiterHelper.setMainClass(s);
            break;
          case "topologyName":
            submiterHelper.setTopologyName(s);
            break;
          case "master":
            submiterHelper.setMaster(s);
            break;
          default:
        }
        nextNeedFiled = null;
      }
      if (s.trim().equals("--mainClass")) {
        nextNeedFiled = "mainClass";
      }
      if (s.trim().equals("--topologyName")) {
        nextNeedFiled = "topologyName";
      }
      if (s.trim().equals("--master")) {
        nextNeedFiled = "master";
      }
      if (s.endsWith(".jar")) {
        if (hasSettedJar) {
          throw new RuntimeException("设置了多个jar包");
        }
        submiterHelper.setJarFilePath(s);
        hasSettedJar = true;
      }
    }
    if (!submiterHelper.check()) {
      throw new RuntimeException("设置错误");
    }
    UpDownLoadUtil.uploadFile("http://" + submiterHelper.getMaster() + "/upload", submiterHelper.getJarFilePath(), submiterHelper.getMainClass(), submiterHelper.getTopologyName());

  }



}
