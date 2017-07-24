package com.wojustme.mystorm.master;

import com.wojustme.mystorm.master.net.CommunicateToWorker;
import com.wojustme.mystorm.master.util.http.HttpFileServer;
import com.wojustme.mystorm.master.util.http.UpLoadJarFileMsg;
import com.wojustme.mystorm.observer.IEventType;
import com.wojustme.mystorm.schedule.AssignTaskUtil;
import com.wojustme.mystorm.submitter.RunTopologyJar;
import com.wojustme.mystorm.util.PropsUtil;
import com.wojustme.mystorm.master.event.MasterEventType;
import com.wojustme.mystorm.observer.EventBean;
import com.wojustme.mystorm.observer.Observer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

/**
 * @author wojustme
 * @date 2017/7/12
 * @package com.wojustme.mystorm.master
 */
public class Master implements Observer {

  private static final Logger LOGGER = LoggerFactory.getLogger(Master.class);

  // 关于mystorm所有的配置
  private final Properties config;
  // 用于观察的主题
  private final MasterSubject masterSubject;
  //
  private MasterConnZK masterConnZK;


  public Master(String[] args) {
    config = setConfig(args);
    masterSubject = MasterSubject.getIstance();
    masterSubject.addObserver(this);
  }

  // 获得当前主题
  public MasterSubject getMasterSubject() {
    return masterSubject;
  }

  // 设置配置属性
  private Properties setConfig(String[] args) {
    // todo 暂时设计从资源文件中加载到属性
    if (args.length == 0) {
      return PropsUtil.loadProps("mystorm.properties", true);
    }
    if (args.length > 1) {
      throw new RuntimeException("配置设置错误");
    }
    String confDir = args[0];
    return PropsUtil.loadProps(confDir + "/mystorm.properties");
  }

  // 获得所有的配置信息
  public Properties getAllConfig() {
    return config;
  }

  // 获取指定的配置值
  public String getConfigVal(String key) {
    return config.getProperty(key);
  }


  // 所有的处理事件
  @Override
  public void update(EventBean event) {
    // 当前的事件类型
    IEventType eventType = event.getEventType();
    if (eventType instanceof MasterEventType) {

      switch ((MasterEventType) eventType) {
        case UPLOAD_FLE_OK:
          LOGGER.info("jar包上传成功");
          UpLoadJarFileMsg eventMsg = (UpLoadJarFileMsg) event.getEventMsg();
          // 运行任务解析
          boolean isRunOk = new RunTopologyJar(eventMsg).runMain();
          break;
        case INIT_TOPOLOGY_OK:
          String topologyName = (String) event.getEventMsg();
          LOGGER.info("解析成功, topologyName -> " + topologyName);
          try {
            boolean isDownLoadOk = CommunicateToWorker.sendMsg(masterConnZK.getZkClient(), topologyName, config);
            if (isDownLoadOk) {
              masterSubject.nodifyObservers(new EventBean(MasterEventType.WORKERS_DOWNLOAD_OK, topologyName));
            } else {
              masterSubject.nodifyObservers(new EventBean(MasterEventType.WORKERS_DOWNLOAD_FAIL, null));
            }

          } catch (Exception e) {
            e.printStackTrace();
          }
          break;
        case WORKERS_DOWNLOAD_OK:
          // 安排任务
          try {
            AssignTaskUtil.assginTask(masterConnZK, (String) event.getEventMsg());
            masterSubject.nodifyObservers(new EventBean(MasterEventType.ASSGINTASK_OK, null));
          } catch (Exception e) {
            e.printStackTrace();
            // todo
            // 安排任务失败
          }
          break;
        case ASSGINTASK_OK:
          // 任务安排成功
          break;
        default:
          break;

      }
    } else {
      LOGGER.error("未知行为");
    }
  }

  public void start() {

    // 与zk连接相关的相关操作
    this.masterConnZK = new MasterConnZK(masterSubject, config);

    // 启动文件上传、下载服务地址的相关线程
    new Thread(new HttpFileServer(masterSubject, config)).start();
  }

  public static void main(String[] args) {
    Master master = new Master(args);
    master.start();
  }
}
