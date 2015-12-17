介绍CshBBrain的整体架构

# Introduction #

介绍CshBBrain的整体架构


# Details #

在介绍CshBBrain服务器架构前，我们先分析下业界流行NIO框架的架构，目前业界流行的NIO框架有Mina,Netty,Grizzly等。他们都采用了Reactor模式，下面上张Reactor模式的示意图：
![http://dl.iteye.com/upload/attachment/0074/6411/c332f366-7e95-3af7-b999-4979be3f76e2.jpg](http://dl.iteye.com/upload/attachment/0074/6411/c332f366-7e95-3af7-b999-4979be3f76e2.jpg)

1.核心组件包括：
1.1.Synchronous Event Demultiplexexer:Event loop + 事件分离
1.2.Dispatcher:事件派发，可以采用多线程实现
1.3.Rqeust Handler:事件处理，业务代码

2.Reactor线程配置：
2.1 Boss Thread + Worker Thread:
2.1.1.Boss 处理OP\_ACCEPT、OP\_CONNECT，处理连接的接入
2.1.2.Worker处理OP\_READ、OP\_WRITER，处理IO读写

2.2 Reactor线程数量配置：
Netty: 1 + 2 **CPU内核数量
Mina:   1 + CPU 内核数量 + 1
Grizzly: 1 + 1
CshBBrain：1 + n** CPU内核数量 （运行时可根据需要自己灵活配置，n:为网络数据读写监听线程CPU内核因子，例如设置为1则表示系统将为每个内核创建1个数据读写监听线程，你可以根据自己服务器的性能和需要配置2，4，8个读写监听线程每内核；设置将更加灵活，对应到系统参数monitorWorker）

3.Reactor模式分工模型：

3.1.OP\_READ和OP\_ACCEPT都运行在reactor线程
3.2.OP\_ACCEPT运行在reactor，OP\_READ运行在单独的线程。
3.3.OP\_READ和OP\_ACCEPT都运行在单独的线程
3.4.OP\_READ运行在reactor线程，而OP\_ACCEPT运行在单独的线程

4.Reactor模式分工模型的选择

4.1.类echo应用，unmashall和业务处理的开销非常低，选择第一种模型。创建线程和切换线程的开销
4.2.第二、第三、第四种模型，从测试来看，OP\_ACCEPT的处理开销很低；从已经完成三路握手的队列移出
4.3.最佳选择：第二种模型，unmashall一般是cpu-bound.业务逻辑代码通常比较耗时，不要在reactor线程处理
CshBBrain 采用的是第二种分工模型。

上张CshBBrain服务器的架构图：
![http://dl.iteye.com/upload/attachment/0074/6409/494df042-6bf2-3af4-8eec-08f8c1a44c67.jpg](http://dl.iteye.com/upload/attachment/0074/6409/494df042-6bf2-3af4-8eec-08f8c1a44c67.jpg)

> 在开发CshBBrain之前，有研究过Mina和Netty，从Mina和Netty的设计上借鉴了不少思想。
1.connectMonitor：负责accept和建立连接，并将连接的Read和Write事件分派给具体的readWriteMonitor线程，分配策略采用简单的平均分配法。
2.readWriteMonitor：负责监听每个连接的Read事件和Write事件，并注册连接的Read和Write监听事件。当程序触发Write事件，直接调用连接的Write代码。当触发连接的Read事件时，程序创建一个Read task 并将Read task放入Read task Queue队列中。
3.processDistribute：负责给Read task Queue队列中的任务分配Worker处理线程进行处理。
4.Worker:负责从连接读取数据，解码数据，调用业务处理程序，编码数据，并创建write task 任务放入到Write Task Queue,readWriteMonitor线程会获取队列中的write task 进行write事件注册。