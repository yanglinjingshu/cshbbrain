#实时消息推送类应用实例：股指推送
# Introduction #

实时消息推送类应用demo:股指推送


# Details #

1.从github 或google code 下载CshBBrain 的1.0.2版本源代码包。下载地址：
GitHub下载地址：https://github.com/CshBBrain/CshBBrain/downloads

googlecode下载地址：http://code.google.com/p/cshbbrain/downloads/list

2.将源代码包解压导入到eclipse中，编译通过。

3.找到src/com/jason/server/ws/ 目录下的 StockServer.java 文件，点击运行。

4.找到test/stock目录下的index.html文件，使用chrome浏览器打开此文件。

5.看到服务器后台有如下输出：

17:16:51,000  INFO MasterServer:192 - [{"raw":"1","name":"上证指数","points":"2104.932","change":"2.06","changeScale":"0.10%"},{"raw":"2","name":"深证成指","points":"8650.191","change":"-14.90","changeScale":"-0.17%"},{"raw":"2","name":"纳斯达克","points":"3044.12","change":"-5.30","changeScale":"-0.17%"},{"raw":"2","name":"日经指数","points":"8534.12","change":"-12.66","changeScale":"-0.15%"},{"raw":"1","name":"道琼斯","points":"13328.85","change":"2.46","changeScale":"0.02%"},{"raw":"1","name":"新加坡海峡时报指数","points":"3041.75","change":"9.09","changeScale":"0.30%"},{"raw":"2","name":"台湾台北指数","points":"7437.04","change":"-14.68","changeScale":"-0.20%"},{"raw":"1","name":"恒生指数","points":"21136.43","change":"137.38","changeScale":"0.65%"}]

[{"raw":"1","name":"上证指数","points":"2104.932","change":"2.06","changeScale":"0.10%"},{"raw":"2","name":"深证成指","points":"8650.191","change":"-14.90","changeScale":"-0.17%"},{"raw":"2","name":"纳斯达克","points":"3044.12","change":"-5.30","changeScale":"-0.17%"},{"raw":"2","name":"日经指数","points":"8534.12","change":"-12.66","changeScale":"-0.15%"},{"raw":"1","name":"道琼斯","points":"13328.85","change":"2.46","changeScale":"0.02%"},{"raw":"1","name":"新加坡海峡时报指数","points":"3041.75","change":"9.09","changeScale":"0.30%"},{"raw":"2","name":"台湾台北指数","points":"7437.04","change":"-14.68","changeScale":"-0.20%"},{"raw":"1","name":"恒生指数","points":"21136.43","change":"137.38","changeScale":"0.65%"}]

浏览器中呈现如下图所示就表示启动成功。
![http://dl.iteye.com/upload/attachment/0075/0141/9340b6f0-65b0-3719-9783-4df09f090e51.png](http://dl.iteye.com/upload/attachment/0075/0141/9340b6f0-65b0-3719-9783-4df09f090e51.png)

服务器隔3秒采集一次最新的股指，并把最新股指推送到客户端。客户端的股指隔3秒滚动更新一次。