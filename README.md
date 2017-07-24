# mystorm-all
一个简单的storm的demo程序

## 使用

1. 环境: 必须有JDK8(使用了lambda表达式), ZooKeeper(topology事件监听)
2. 配置脚本bin/conf, 启动多个worker节点, 只需要修改myworker.properties文件, mystorm.properties文件可以不必变动，但是这里面含有线程启动个数，酌情启动。
3. 启动，在bin文件夹中。主节点: ./start-master.sh, 工作节点: ./start-worker.sh, 提交脚本例子: ./submit-topology.sh, 清理zk数据: ./stop.sh
4. mystorm-core是框架核心库，而mystorm-demo是我简单写的两个demo程序

## 总结

### 1. 不足

1. UI未做
2. 任务删除，即kill topology未实现
3. 网络通信，使用netty，但未实现连接对象池(包含client定时失效)
4. 提交中必须指定拓扑名(未优化)
5. 数据冗余
6. Java堆使用率过高
7. 数据分组，使用字符串的hashCode方法，效率不高，且key只能为String类型
8. 数据传输可靠性
9. 水压检测
10. ……

### 2. 收获

1. 了解了storm工作原理(参考了storm设计理念，但也不同)
2. 熟悉了netty这个NIO框架，准备后期好好去看看netty底层原理
3. 较为熟练的掌握了Java的反射机制
4. 了解了JDK中的阻塞队列，计划之后好好去看看JDK并发包各个组件


## 展望

1. 不在基于该项目编写，但等我较为系统地学习了netty和JDK并发包后，会对流式处理有个新的认识，并重新编写一个新的demo框架
2. 数据偏移是流式处理最大的问题瓶颈，我会继续去查找论文和资料，在新的框架中进行加入
3. 单机的并发性能有时也能胜任一些问题，计划学习scala的actor、golang的channel等等并发模型
4. 未来的流式处理框架会是Flink，会去对比和参考Flink

## 计划

计划赶不上变化😃😃😃
