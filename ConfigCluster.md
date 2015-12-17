# Introduction #
宝贝鱼(CshBBrain)集群配置使用说明

# Details #

最近有不少朋友在询问宝贝鱼(CshBBrain)集群的问题，说集群老不成功，本篇文章主要就是解答这个问题的。

首先介绍下宝贝鱼(CshBBrain)中集群相关的几个概念：
1.集群开关参数clustersSwitch：参数取值1表示打开集群开关，服务器将作为集群中的一个节点（可能只担当管理集群中其他服务器的角色，也可能只担当集群中的一个业务节点服务器角色，也可能2者兼任），总之要启用集群，必须将启动的服务器实例的这个参数配置为1.

2.管理服务器（master server）：参数clustersRole配置为2，在集群中只担当管理集群中的其他服务器的角色，定时收集监控集群中所管理服务器节点的配置信息、运行情况和负载；并担当集群中各服务器节点的负载分配和均衡。初步的想法是，客户端首先连接管理服务器，管理服务器根据集群中各服务器的负载状况和软硬件配置情况，给当前客户端分配一个节点服务器，客户端和分配的节点服务器建立连接进行后续的业务交互处理。

3.业务节点服务器（biz server）：参数clustersRole配置为1，在集群中只担当业务处理接入节点服务器，启动后首先连接到集群中指定的管理服务器（master server），汇报节点的软硬件配置（最重要的是要告诉管理服务器他的ip地址和客户端接入的端口），然后等待客户端请求的到来，并定时汇报自身的负载状况给管理服务器。

4.在有些时候可能不需要一台单独的管理服务器，那么你可以将其中1台服务器配置即为管理服务器（master server）又是业务节点服务器（biz server），你只需要将参数clustersRole配置为3.

5.端口参数详解：
5.1参数port：不管是管理服务器，还是业务服务器，该端口都是用来监听客户端建立连接的端口。
5.2参数clustersPort：管理服务器和业务服务器之间建立连接，相互通信的端口；管理服务器配置这个端口用于监听业务服务器建立连接的请求，业务服务器配置这个端口主要用于监听来至其他业务服务器的连接请求以及交互需要。
5.3参数masterServer：管理服务器的ip地址和端口号码，具有业务服务器角色的服务器需要配置他所归属的管理服务器，管理服务器不需要配置这个参数。

6.手把手教你配置集群：
6.1配置启动纯管理服务器节点：
6.1.1将配置文件config\_1.properties的内容拷贝到config.properties中，注意将clustersRole设置为2，具体内容如下：

#the port of the server
port=9090

#the priority of the thread in pool
maxPriority=5

#the tmp folder
tmpRoot=e:/tmp/

#the factor of the work process thread per CPU kernel
requestWorker=5

#the factor of the read writer monitor thread per CPU kernel
monitorWorker=1

#the buffer size of the read buffer,unit is KB
readBuffer=5

#the buffer size of the write buffer,unit is KB
writeBuffer=64

#the send buffer size of the system sockect
sockectSendBufferSize=64

#the receive buffer of the system socket
sockectReceiveBufferSize=5

#wheather the connection keep alive,1:keep alive,0:not keep alive
keepConnect=1

#the count of blank read
zoreFetchCount = 1000000

#the min size and the max size of the buffer pool
minBufferPoolSize=1000
maxBufferPoolSize=1000

#whether open the broadcast thread,0:close,1:open
broadSwitch=0

#the timeout for every client,0:validate all the time,not 0:the timeout minites
timeOut=0

#the cluseter switch,0:close,1:open
clustersSwitch=1

#the cluseter port for the server
clustersPort=9191

#the responsibility of the server,1:biz server,2:master server,3:biz and master server
clustersRole=2

#the master server address,include the ip and port

#masterServer=192.168.1.220:9292

6.1.2打开ClustersServer文件，运行，启动成功后后台输出如下信息：
22:48:31,781  INFO MasterServer:554 - 数据读取回写监听线程创建成功：请求数据传输监听线程0
22:48:31,781  INFO MasterServer:953 - 请求处理调度线程创建完毕
22:48:31,781  INFO MasterServer:554 - 数据读取回写监听线程创建成功：请求数据传输监听线程1
22:48:31,796  INFO MasterServer:953 - 请求处理调度线程创建完毕
22:48:31,796  INFO MasterServer:1095 - 连接监听线程创建成功
22:48:31,796  INFO MasterServer:1126 - 集群连接监听线程创建成功
22:48:31,812  INFO MasterServer:1158 - 集群服务器准备就绪，等待集群请求到来
22:48:31,812  INFO MasterServer:1221 - 服务器准备就绪，等待请求到来

6.2配置启动纯业务节点服务器：
6.2.1将配置文件config\_2.properties的内容拷贝到config.properties中，注意将clustersRole设置为1，具体内容如下：

#the port of the server
port=7070

#the priority of the thread in pool
maxPriority=5

#the tmp folder
tmpRoot=e:/tmp/

#the factor of the work process thread per CPU kernel
requestWorker=5

#the factor of the read writer monitor thread per CPU kernel
monitorWorker=1

#the buffer size of the read buffer,unit is KB
readBuffer=5

#the buffer size of the write buffer,unit is KB
writeBuffer=64

#the send buffer size of the system sockect
sockectSendBufferSize=64

#the receive buffer of the system socket
sockectReceiveBufferSize=5

#wheather the connection keep alive,1:keep alive,0:not keep alive
keepConnect=1

#the count of blank read
zoreFetchCount = 1000000

#the min size and the max size of the buffer pool
minBufferPoolSize=1000
maxBufferPoolSize=1000

#whether open the broadcast thread,0:close,1:open
broadSwitch=0

#the timeout for every client,0:validate all the time,not 0:the timeout minites
timeOut=0

#the cluseter switch,0:close,1:open
clustersSwitch=1

#the cluseter port for the server
clustersPort=7171

#the responsibility of the server,1:biz server,2:master server,3:biz and master server
clustersRole=1

#the master server address,include the ip and port
masterServer=192.168.1.220:9191 （注意：因为管理服务器的clustersPort配置为9191，所以业务节点服务器这里的端口要配置为9191）

6.1.2打开Server文件，运行，启动成功后后台输出如下信息：

22:48:57,828  INFO MasterServer:554 - 数据读取回写监听线程创建成功：请求数据传输监听线程0
22:48:57,828  INFO MasterServer:953 - 请求处理调度线程创建完毕
22:48:57,843  INFO MasterServer:554 - 数据读取回写监听线程创建成功：请求数据传输监听线程1
22:48:57,843  INFO MasterServer:953 - 请求处理调度线程创建完毕
22:48:57,843  INFO MasterServer:1095 - 连接监听线程创建成功
22:48:57,843  INFO MasterServer:1126 - 集群连接监听线程创建成功
22:48:57,843  INFO MasterServer:269 - 集群通信客户端消息处理线程创建完毕
22:48:57,859  INFO MasterServer:324 - 成功连接到集群服务器  192.168.1.220 的端口:9191
22:48:57,875  INFO MasterServer:1221 - 服务器准备就绪，等待请求到来
22:48:57,890  INFO MasterServer:1158 - 集群服务器准备就绪，等待集群请求到来

下面是握手信息：
22:48:57,921  INFO Client:844 - 向客户端null发送数据：CshBBrain
Host:192.168.1.220
Key:b9db05a1b940499da96b0dbb
Protocol:Protocol

22:48:57,921  INFO ClustersCoder:167 - the response: CshBBrain
Host:192.168.1.220
Key:b9db05a1b940499da96b0dbb
Protocol:Protocol

等几分钟，业务节点服务器后台会输入如下信息，是因为目前我只做了简单处理，在业务节点服务器把本身的负载等情况定时汇报给管理服务器时，管理服务器只简单的将节点服务器发来的信息返回，并在节点服务器端输出这些信息：

22:49:57,859  INFO MasterServer:377 - 节点服务器：192.168.1.220:1674
服务器CPU内核数量：2
服务器读写监听线程数量：2
服务器工作线程数量：10
活跃连接客户端数量：0
活跃集群连接客户端数量：0
活跃本地连接客户端数量：0

22:49:57,859  INFO Client:844 - 向客户端null发送数据：action=1&coreCount=2&readerWriterCount=2&workerCount=10&clientCount=0&clustersCount=0&port=7070&localCount=0
22:50:27,859  INFO MasterServer:377 - 节点服务器：192.168.1.220:1674
服务器CPU内核数量：2
服务器读写监听线程数量：2
服务器工作线程数量：10
活跃连接客户端数量：0
活跃集群连接客户端数量：0
活跃本地连接客户端数量：0

22:50:27,859  INFO Client:844 - 向客户端null发送数据：action=1&coreCount=2&readerWriterCount=2&workerCount=10&clientCount=0&clustersCount=0&port=7070&localCount=0

按照上面所说的配置你的集群，然后在此基础上扩展开发你自己的集群管理协调功能即可。