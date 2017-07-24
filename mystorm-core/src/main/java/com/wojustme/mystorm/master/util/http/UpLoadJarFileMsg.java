package com.wojustme.mystorm.master.util.http;

import java.util.Arrays;

/**
 * 对上传jar文件的封装
 * @author wojustme
 * @date 2017/7/19
 * @package com.wojustme.mystorm.master.util.http
 */
public class UpLoadJarFileMsg {

  private String fileName;
  private String filePath;
  private String mainCls;
  private String topologyName;
  private String[] argsArr;

  public UpLoadJarFileMsg() {
  }

  public String getFilePath() {
    return filePath;
  }

  public void setFilePath(String filePath) {
    this.filePath = filePath;
  }

  public String getMainCls() {
    return mainCls;
  }

  public void setMainCls(String mainCls) {
    this.mainCls = mainCls;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String[] getArgsArr() {
    return argsArr;
  }

  public void setArgsArr(String[] argsArr) {
    this.argsArr = argsArr;
  }

  public String getTopologyName() {
    return topologyName;
  }

  public void setTopologyName(String topologyName) {
    this.topologyName = topologyName;
  }

  @Override
  public String toString() {
    return "UpLoadJarFileMsg{" +
        "fileName='" + fileName + '\'' +
        ", filePath='" + filePath + '\'' +
        ", mainCls='" + mainCls + '\'' +
        ", topologyName='" + topologyName + '\'' +
        ", argsArr=" + Arrays.toString(argsArr) +
        '}';
  }
}
