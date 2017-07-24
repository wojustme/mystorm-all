package com.wojustme.mystorm.slave;

import com.wojustme.mystorm.network.NetworkData;
import com.wojustme.mystorm.observer.IEventType;
import com.wojustme.mystorm.schedule.AssiginTaskZkBean;
import com.wojustme.mystorm.slave.data.NetworkHandler;
import com.wojustme.mystorm.observer.EventBean;
import com.wojustme.mystorm.observer.Observer;
import com.wojustme.mystorm.slave.event.WorkerEventType;
import com.wojustme.mystorm.network.IMsgServer;
import com.wojustme.mystorm.slave.network.MsgNettyServer;
import com.wojustme.mystorm.slave.runner.ExecutorManager;
import com.wojustme.mystorm.slave.stat.WorkerStat;
import com.wojustme.mystorm.util.PropsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * 工作节点启动
 * @author wojustme
 * @date 2017/7/12entry
 * @package com.wojustme.mystorm.slave
 */
public class Worker implements Observer {

  private static final Logger LOGGER = LoggerFactory.getLogger(Worker.class);

  // 该工作节点名
  private String workerName;
  // 当前工作节点状态
  private WorkerStat workerStat;
  // 关于mystorm所有的配置
  private final Properties config;
  // 用于观察的主题
  private final WorkerSubject workerSubject;
  // worker的zk连接器
  private WorkerConnZK workerConnZK;
  // 该worker节点的线程管理器
  private ExecutorManager executorManager;

  public Worker(String[] args) {
    config = setConfig(args);
    this.workerName = "worker-" + config.getProperty("worker.id");
    // 重命名下载文件目录
    config.setProperty("worker.jarFile.path", config.getProperty("worker.jarFile.path") + "_" + workerName);
    this.workerStat = new WorkerStat(config);
    workerSubject = WorkerSubject.getIstance();
    workerSubject.addObserver(this);

  }

  // 设置配置属性
  private Properties setConfig(String[] args) {
    // todo 暂时设计从资源文件中加载到属性
    Properties mystormProps = PropsUtil.loadProps("mystorm.properties", true);
    // 加载workerId
    Properties workerProps = PropsUtil.loadProps("myworker.properties", true);

    if (args.length > 1) {
      throw new RuntimeException("配置设置错误");
    }
    if (args.length == 1) {
      String confDir = args[0];
      mystormProps = PropsUtil.loadProps(confDir + "/mystorm.properties");
      workerProps = PropsUtil.loadProps(confDir + "/myworker.properties");
    }

    mystormProps.setProperty("worker.id", workerProps.getProperty("my.id"));
    mystormProps.setProperty("worker.port", workerProps.getProperty("my.port"));

    return mystormProps;
  }


  // 获得所有的配置信息
  public Properties getAllConfig() {
    return config;
  }

  // 获取指定的配置值
  public String getConfigVal(String key) {
    return config.getProperty(key);
  }

  // 获得当前主题
  public WorkerSubject getWorkerSubject() {
    return workerSubject;
  }

  @Override
  public void update(EventBean event) {
    IEventType eventType = event.getEventType();
    if (eventType instanceof WorkerEventType) {

      switch ((WorkerEventType) eventType) {
        case DOWNLOAD_JAR_FILE_OK:
          LOGGER.info("下载文件成功");
          break;
        case NEW_TASK_ASSIGNED:
          // 新的分配的任务
          AssiginTaskZkBean newTask = (AssiginTaskZkBean) event.getEventMsg();
          try {
            executorManager.addTask(newTask);
          } catch (Exception e) {
            e.printStackTrace();
          }
          break;
        case RECV_NEW_DATA:
          NetworkData networkData = (NetworkData) event.getEventMsg();
          executorManager.recvData(networkData.getTaskName(), networkData.getData());
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
    this.workerConnZK = new WorkerConnZK(workerName, workerSubject, config, workerStat);
    // 使用netty监听端口是否有数据流入流出
    IMsgServer iMsgServer = new MsgNettyServer(config, new NetworkHandler(config, workerSubject));
    this.executorManager = new ExecutorManager(config, workerStat, workerConnZK);
    new Thread(iMsgServer).start();
  }

  public static void main(String[] args) {
    Worker worker = new Worker(args);
    worker.start();
  }

}
