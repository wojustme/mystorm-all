package com.wojustme.mystorm.util.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.CharsetUtils;

import java.io.*;

/**
 * @author wojustme
 * @date 2017/7/12
 * @package com.wojustme.mystorm.util.http
 */
public final class UpDownLoadUtil {

  // 上传文件
  public static boolean uploadFile(String url, String filePath, String mainCls, String topologyName) {

    boolean flag = false;
    CloseableHttpClient httpClient = null;
    CloseableHttpResponse response = null;

    try {
      httpClient = HttpClients.createDefault();
      // 上传文件的url
      HttpPost httpPost = new HttpPost(url);
      // 设置文件属性
      FileBody bin = new FileBody(new File(filePath));
      // 设置文件编码格式
      HttpEntity reqEntity = MultipartEntityBuilder.create()
          .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
          .addPart("multipartFile", bin)
          .addPart("mainClass", new StringBody(mainCls))
          .addPart("topologyName", new StringBody(topologyName))
          .setCharset(CharsetUtils.get("UTF-8"))
          .build();
      // 将数据注入post方法中
      httpPost.setEntity(reqEntity);
      // 执行post方法
      response = httpClient.execute(httpPost);

      // 如果返回码为200，则成功
      if (response.getStatusLine().getStatusCode() == 200) {
        flag =  true;
      }
    } catch (Exception e) {
      e.printStackTrace();
      flag = false;
    } finally {
      try {
        if(response != null){
          response.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        if(httpClient != null){
          httpClient.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return flag;
  }

  // 下载文件
  public static boolean downloadFile(String url, String fileName, String directoryPath) {

    boolean flag = false;

    String filePath = directoryPath + "/" + fileName;
    String realUrl = url + "/" + fileName;

    OutputStream out = null;
    InputStream in = null;

    CloseableHttpClient httpClient = HttpClients.createDefault();
    try {
      HttpGet httpGet = new HttpGet(realUrl);

      HttpResponse response = httpClient.execute(httpGet);

      HttpEntity entity = response.getEntity();

      in = entity.getContent();
      long length = entity.getContentLength();
      if (length <= 0) {
        System.out.println("下载文件不存在！");
        return false;
      }

      File file = new File(filePath);
      if (!file.exists()) {
        file.createNewFile();
      }
      out = new FileOutputStream(file);

      byte[] buffer = new byte[4096];
      int readLength = 0;
      while ((readLength=in.read(buffer)) > 0) {
        byte[] bytes = new byte[readLength];
        System.arraycopy(buffer, 0, bytes, 0, readLength);
        out.write(bytes);
      }

      out.flush();
      flag = true;

    } catch (IOException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if(in != null){
          in.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        if(out != null){
          out.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return flag;
  }

}
