package com.wojustme.mystorm.master.util.http;

import com.wojustme.mystorm.master.MasterSubject;
import com.wojustme.mystorm.master.event.MasterEventType;
import com.wojustme.mystorm.observer.EventBean;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.UUID;

/**
 * netty封装的http文件上传和下载处理器
 * @author wojustme
 * @date 2017/7/14
 * @package com.wojustme.mystorm.master.util.http
 */
public class FileHandler extends SimpleChannelInboundHandler<HttpObject> {


  // 日志
  private static final Logger LOG = LoggerFactory.getLogger(FileHandler.class);

  // 常量
  private final String basePath;
  private final String downloadPrefix;
  private final String uploadPrefix;

  // 接入的HttpRequest
  private HttpRequest currentRequest;
  private static final HttpDataFactory FACTORY = new DefaultHttpDataFactory(DefaultHttpDataFactory.MAXSIZE);
  // 对HTTP中post请求进行编码封装
  private HttpPostRequestDecoder postDecoder;

  // 上传文件信息
  private UpLoadJarFileMsg upLoadJarFileMsg;

  static {
    DiskFileUpload.deleteOnExitTemporaryFile = true;
    DiskFileUpload.baseDirectory = null;
    DiskAttribute.deleteOnExitTemporaryFile = true;
    DiskAttribute.baseDirectory = null;
  }

  private MasterSubject masterSubject;
  private Properties config;

  public FileHandler(MasterSubject masterSubject, Properties config) {
    this.masterSubject = masterSubject;
    this.config = config;

    basePath = config.getProperty("file.directory");
    downloadPrefix = config.getProperty("file.download.url");
    uploadPrefix = config.getProperty("file.upload.url");

    upLoadJarFileMsg = new UpLoadJarFileMsg();

//     uuid = UUID.randomUUID().toString();
  }

  @Override
  public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    // 注销
    if (postDecoder != null) {
      postDecoder.cleanFiles();
    }
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, HttpObject httpObject) throws Exception {

    if (httpObject instanceof HttpRequest) {
      HttpRequest httpRequest = (HttpRequest) httpObject;
      handlerRequest(ctx, httpRequest);
    } else if (httpObject instanceof HttpContent) {
      HttpContent httpContent = (HttpContent) httpObject;
      handlerContent(ctx, httpContent);
    } else {
      LOG.error("未知方法");
      sendErrorResponse(ctx.channel(), HttpResponseStatus.SERVICE_UNAVAILABLE, "不支持该编码格式");
    }

  }

  // 发送上传成功响应数据
  private void sendUploadResponse(Channel channel) {
    String okMsg = "200 ok";
    ByteBuf byteBuf = Unpooled.copiedBuffer(okMsg, CharsetUtil.UTF_8);

    // 构建回应的消息
    FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
    // 允许跨域
    response.headers().set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");

    if (HttpUtil.isKeepAlive(currentRequest)) {
      response.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
    }

    sendResponseMsg(channel, response);
  }

  // 重置，清空
  private void reset() {
    currentRequest = null;
    if (postDecoder != null) {
      postDecoder.destroy();
      postDecoder = null;
    }
  }

  // 用于处理HttpContent请求
  private void handlerContent(ChannelHandlerContext ctx, HttpContent httpContent) {
    if (currentRequest.method() == HttpMethod.POST) {
      handlerUpLoad(ctx, httpContent);
    } else {
      if (httpContent.content().readableBytes() == 0) {
        reset();
        LOG.info("非post方法要求处理httpcontent, 跳过...");
        return;
      }
    }

  }

  // 处理上传文件
  private void handlerUpLoad(ChannelHandlerContext ctx, HttpContent httpContent) {
    String url = currentRequest.uri();
    try {
      URI uri = new URI(url);
      if (uri.getPath().equals(uploadPrefix)) {
        if (postDecoder != null) {
          try {
            postDecoder.offer(httpContent);
          } catch (HttpPostRequestDecoder.ErrorDataDecoderException e) {
            e.printStackTrace();
            sendErrorResponse(ctx.channel(), HttpResponseStatus.SERVICE_UNAVAILABLE, "无法编码");
          }
        }
      }
      // 从chunk中读取chunk
      readHttpDataChunkByChunk();
      // 最后一片数据
      if (httpContent instanceof LastHttpContent) {
        sendUploadResponse(ctx.channel());
        reset();
        masterSubject.nodifyObservers(new EventBean(MasterEventType.UPLOAD_FLE_OK, upLoadJarFileMsg));
      }
    } catch (URISyntaxException e) {
      e.printStackTrace();
      sendErrorResponse(ctx.channel(), HttpResponseStatus.NOT_FOUND, "无法解析请求URL" + url);
    }

  }

  // 从http请求数据中读取chunk数据
  private void readHttpDataChunkByChunk() {
    while (postDecoder.hasNext()) {
      InterfaceHttpData data = postDecoder.next();
      try {
        writeHttpData(data);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        data.release();
      }
    }
  }

  // 开始保存文件数据
  private void writeHttpData(InterfaceHttpData data) throws IOException {

    if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
      // 上传数据
      Attribute attribute = (Attribute) data;
      if (attribute.getName().equals("mainClass")) {
        upLoadJarFileMsg.setMainCls(attribute.getValue());
      }
      if (attribute.getName().equals("topologyName")) {
//        upLoadJarFileMsg.setTopologyName(attribute.getValue() + "--" + uuid);
        upLoadJarFileMsg.setTopologyName(attribute.getValue());
      }
    } else if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
      // 上传文件
      FileUpload fileUpload = (FileUpload) data;
      if (fileUpload.isCompleted()) {
        String rootDirectory = basePath;
//        String fileName = fileUpload.getFilename().split(".jar")[0];
//        fileName += "--" + uuid + ".jar";
        String fileName = fileUpload.getFilename();
        File dest = new File(rootDirectory, fileName);
        // 将上传的缓存文件重命名为dest
        fileUpload.renameTo(dest);
        // 设置上传jar包对象
        upLoadJarFileMsg.setFileName(fileName);
        upLoadJarFileMsg.setFilePath(dest.getAbsolutePath());
        // 清除缓存数据
        postDecoder.removeHttpDataFromClean(fileUpload);
      }
    }
  }

  // 用于处理HttpRequest请求
  private void handlerRequest(ChannelHandlerContext ctx, HttpRequest httpRequest) {
    // 注册到当前的request
    this.currentRequest = httpRequest;
    HttpMethod method = httpRequest.method();
    if (method == HttpMethod.GET) {
      handlerGet(ctx, httpRequest);
    } else if (method == HttpMethod.POST) {
      handlerPost(ctx, httpRequest);
    } else {
      LOG.error("未知方法");
      sendErrorResponse(ctx.channel(), HttpResponseStatus.METHOD_NOT_ALLOWED, "不支持" + method.name());
    }
  }

  // post方法请求
  private void handlerPost(ChannelHandlerContext ctx, HttpRequest httpRequest) {
      postDecoder = new HttpPostRequestDecoder(FACTORY, httpRequest);
  }

  // get方法请求
  private void handlerGet(ChannelHandlerContext ctx, HttpRequest httpRequest) {
    // 获得请求的URL
    final String uri = httpRequest.uri();
    String filePath = checkDownLoadUrl(uri);

    if (filePath == null) {
      LOG.error("文件路径非法");
      sendErrorResponse(ctx.channel(), HttpResponseStatus.NOT_EXTENDED, filePath + "不存在");
      return;
    }

    // 创建文件
    File file = new File(filePath);
    if (!file.exists() || file.isDirectory()) {
      LOG.error("文件不存在");
      sendErrorResponse(ctx.channel(), HttpResponseStatus.NOT_EXTENDED, filePath + "不存在");
      return;
    }

    // 开始下载文件
    handlerDownLoad(ctx, httpRequest, file);

  }

  // 处理文件下载
  private void handlerDownLoad(ChannelHandlerContext ctx, HttpRequest httpRequest, File file) {
    RandomAccessFile randomAccessFile;
    try {
      randomAccessFile = new RandomAccessFile(file, "r");
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      LOG.error("文件不存在");
      sendErrorResponse(ctx.channel(), HttpResponseStatus.NOT_EXTENDED, file.getPath() + "不存在");
      return;
    }

    try {
      long fileLength = randomAccessFile.length();

      // 开始创建response回应
      HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
      // 设置response回应头部长度
      HttpUtil.setContentLength(response, fileLength);
      // 设置response回应头部类型
      setDownLoadHeader(response, file);

      // 判断当前请求是否还存活
      if (HttpUtil.isKeepAlive(httpRequest)) {
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.KEEP_ALIVE);
      }
      // 发送数据
      ctx.write(response);

      // 发送文件的future
      ChannelFuture sendFileFuture = null;
      sendFileFuture = ctx.write(new ChunkedFile(randomAccessFile, 0, fileLength, 8192), ctx.newProgressivePromise());
      // 增加发送监听事件
      sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
        @Override
        public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {
        }

        @Override
        public void operationComplete(ChannelProgressiveFuture future) throws Exception {
        }
      });

      // 检测是否最后一块数据
      ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);

      if (!HttpUtil.isKeepAlive(httpRequest)) {
        // 发送完，关闭通道
        lastContentFuture.addListener(ChannelFutureListener.CLOSE);
      }

    } catch (IOException e) {
      e.printStackTrace();
      LOG.error("下载文件失败");
      sendErrorResponse(ctx.channel(), HttpResponseStatus.BAD_REQUEST, "下载文件失败");
      return;
    }
  }

  // 校验uri
  private String checkDownLoadUrl(String uri) {

    try {
      uri = new URI(uri).getPath();
    } catch (URISyntaxException e) {
      e.printStackTrace();
      return uri;
    }

    String[] split = uri.split(downloadPrefix);
    if (split.length <= 1) {
      return null;
    }
    if (!split[0].equals("")) {
      return null;
    }
    String requestFilePath = split[1];
    if (!requestFilePath.startsWith("/")) {
      return null;
    }
    return basePath + requestFilePath;
  }

  // 设置下载文件的头部信息
  private void setDownLoadHeader(HttpResponse response, File file) {
    MimetypesFileTypeMap mimetypesFileTypeMap = new MimetypesFileTypeMap();
    response.headers().set(HttpHeaderNames.CONTENT_TYPE, mimetypesFileTypeMap.getContentType(file.getPath()));
  }


  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    if (ctx.channel().isActive()) {
      LOG.error("error...");
    }
  }

  // 发送响应
  private void sendResponseMsg(Channel ch, HttpResponse response) {
    ChannelFuture channelFuture = ch.writeAndFlush(response);
    if (!HttpUtil.isKeepAlive(currentRequest)) {
      channelFuture.addListener(ChannelFutureListener.CLOSE);
    }
  }
  // 构建错误响应
  private void sendErrorResponse(Channel ch, HttpResponseStatus status, String errorMsg) {
    ByteBuf byteBuf = Unpooled.copiedBuffer(errorMsg, CharsetUtil.UTF_8);
    // 构建回应的消息
    FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, byteBuf);
    sendResponseMsg(ch, response);
  }
}
