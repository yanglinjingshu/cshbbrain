# Introduction #

版本发布新闻


# Details #
开源WebSocket服务器CshBBrain V2.0.0版本发布。在V2.0.0版本中添加服务器集群功能，以满足大并发量高容量的分布式系统开发。如果你需要开发带有集群功能的WebSocket服务器，CshBBrain V2.0.0也许是非常适合你的选择。在CshBBrain V2.0.0中你可以将某个服务器设置为纯粹的集群管理服务器，或纯粹的业务节点服务器和集群管理业务节点服务器3中类型。

管理服务器启动日志：

13:16:34,008  INFO MasterServer:464 - 数据读取回写监听线程创建成功：请求数据传输监听线程0
13:16:34,008  INFO MasterServer:803 - 请求处理调度线程创建完毕
13:16:34,024  INFO MasterServer:464 - 数据读取回写监听线程创建成功：请求数据传输监听线程1
13:16:34,024  INFO MasterServer:803 - 请求处理调度线程创建完毕
13:16:34,024  INFO MasterServer:929 - 连接监听线程创建成功
13:16:34,024  INFO MasterServer:960 - 集群连接监听线程创建成功
13:16:34,039  INFO MasterServer:992 - 集群服务器准备就绪，等待集群请求到来
13:16:34,039  INFO MasterServer:1055 - 服务器准备就绪，等待请求到来
13:16:57,226  INFO ClustersDecoder:99 - the msg received:
CshBBrain
Host:192.168.1.111
Key:789a71bbd02e47b8a45c7810
Protocol:Protocol


13:16:57,226  INFO ClustersDecoder:224 - 789a71bbd02e47b8a45c7810258EAFA5-E914-47DA-95CA-C5AB0DC85B11
13:16:57,257  INFO ClustersDecoder:340 - the response: CshBBrain
Host:192.168.1.111
Accept:fdW9PLg7Nj/qsdmwx+FXLL/k/9w=
Protocol:protocol


13:16:57,335  INFO Response:158 - 向客户端传输数据的长度 : 87

业务节点服务器启动日志：

13:16:57,054  INFO MasterServer:464 - 数据读取回写监听线程创建成功：请求数据传输监听线程0
13:16:57,070  INFO MasterServer:803 - 请求处理调度线程创建完毕
13:16:57,070  INFO MasterServer:464 - 数据读取回写监听线程创建成功：请求数据传输监听线程1
13:16:57,070  INFO MasterServer:803 - 请求处理调度线程创建完毕
13:16:57,070  INFO MasterServer:929 - 连接监听线程创建成功
13:16:57,070  INFO MasterServer:960 - 集群连接监听线程创建成功
13:16:57,085  INFO MasterServer:258 - 集群通信客户端消息处理线程创建完毕
13:16:57,085  INFO MasterServer:313 - 成功连接到集群服务器  192.168.1.220 的端口:9191
13:16:57,101  INFO MasterServer:1055 - 服务器准备就绪，等待请求到来
13:16:57,116  INFO MasterServer:992 - 集群服务器准备就绪，等待集群请求到来
13:16:57,148  INFO Client:668 - CshBBrain
Host:192.168.1.111
Key:789a71bbd02e47b8a45c7810
Protocol:Protocol


13:16:57,148  INFO ClustersCoder:162 - the response: CshBBrain
Host:192.168.1.111
Key:789a71bbd02e47b8a45c7810
Protocol:Protocol


13:16:57,226  INFO Response:158 - 向客户端传输数据的长度 : 80
13:16:57,335  INFO ClustersDecoder:99 - the msg received:
CshBBrain
Host:192.168.1.111
Accept:fdW9PLg7Nj/qsdmwx+FXLL/k/9w=
Protocol:protocol

http://cshbbrain.iteye.com/blog/1703637