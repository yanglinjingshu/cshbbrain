Hello World for CshBBrain
# Introduction #

A broadcast message demo and A Hello, XX demo.


# Details #

CshBBrain开发示例
1.服务器定时推送广播消息
服务器每隔10秒钟将服务器的当前时间所对应的毫秒数发送给客户度，客户端接收服务器发送的消息并在网页上显示出来。
1.1 我们创建一个线程来每隔10秒发送一个消息：

protected void startBroadMessage(){// 定时发送广播消息的线程
> while(true){
> > try{
> > > Response rs = new Response();// 创建一个响应消息
> > > String msg = "current datetime of server current datetime of server current datetime of server current datetime of server current datetime of server current datetime of server: " + System.currentTimeMillis();
> > > //System.out.println(msg);
> > > rs.setBody(msg);// 给响应消息设置内容
> > > MasterServer.addBroadMessage(rs);// 将要发送的消息广播出去


> broadMessageThread.sleep(10000);// 线程睡眠10秒钟
> }catch(InterruptedException e){
> > e.printStackTrace();

> }
> }
}

1.2 启动服务器时，启动广播线程：
BroadThread.getInstance();// 创建广播线程

1.3 完整代码：
1.3.1 消息广播线程：
package com.jason.server.ws.biz;

import com.jason.server.Response;
import com.jason.server.MasterServer;

/
  * <li>类型名称：<br>
<ul><li><li>说明：<br>
</li><li><li>创建人：CshBBrain;技术博客：<a href='http://cshbbrain.iteye.com/'>http://cshbbrain.iteye.com/</a>
</li><li><li>创建日期：2012-8-24<br>
</li><li><li>修改人：<br>
</li><li><li>修改日期：<br>
</li><li>
public class BroadThread {<br>
</li></ul><blockquote>private static BroadThread broadThread= new BroadThread();<br>
private Thread broadMessageThread;// 发送广播消息的线程</blockquote>

<blockquote>public static BroadThread getInstance(){<br>
<blockquote>return broadThread;<br>
</blockquote>}</blockquote>

<blockquote>private BroadThread(){<br>
<blockquote>//	消息广播线程<br>
Runnable writeDistributeRunner = new Runnable(){<br>
<blockquote>public void run(){<br>
<blockquote>try{<br>
<blockquote>startBroadMessage();<br>
</blockquote>}catch(Exception e){<br>
<blockquote>e.printStackTrace();<br>
</blockquote>}<br>
</blockquote>}<br>
</blockquote>};</blockquote></blockquote>

<blockquote>this.broadMessageThread = new Thread(writeDistributeRunner);<br>
this.broadMessageThread.setName("广播消息生成线程");<br>
this.broadMessageThread.start();<br>
</blockquote><blockquote>}</blockquote>

<blockquote>protected void startBroadMessage(){<br>
<blockquote>while(true){<br>
<blockquote>try{<br>
<blockquote>Response rs = new Response();<br>
String msg = "current datetime of server current datetime of server current datetime of server current datetime of server current datetime of server current datetime of server: " + System.currentTimeMillis();<br>
//System.out.println(msg);<br>
rs.setBody(msg);<br>
MasterServer.addBroadMessage(rs);</blockquote></blockquote></blockquote></blockquote>

<blockquote>broadMessageThread.sleep(10000);<br>
</blockquote><blockquote>}catch(InterruptedException e){<br>
<blockquote>e.printStackTrace();<br>
</blockquote>}<br>
</blockquote><blockquote>}<br>
</blockquote><blockquote>}<br>
}</blockquote>

1.3.2 启动消息广播线程<br>
package com.jason.server.ws;<br>
<br>
import java.io.IOException;<br>
<br>
import com.jason.server.MasterServer;<br>
import com.jason.server.ws.biz.BroadThread;<br>
<br>
/<br>
<ul><li><li>类型名称：<br>
</li><li><li>说明：websocket服务器入口类。<br>
</li><li><li>创建人：CshBBrain;技术博客：<a href='http://cshbbrain.iteye.com/'>http://cshbbrain.iteye.com/</a>
</li><li><li>创建日期：2011-11-18<br>
</li><li><li>修改人：<br>
</li><li><li>修改日期：<br>
</li><li>
public class Server{</li></ul>

<blockquote>/<br>
<ul><li><li>方法名：main<br>
</li><li><li>@param args<br>
</li><li><li>返回类型：void<br>
</li><li><li>说明：WebSocket服务器，设置websocketdecoder,websocketProcess,websocketcoder给服务器<br>
</li><li><li>创建人：CshBBrain;技术博客：<a href='http://cshbbrain.iteye.com/'>http://cshbbrain.iteye.com/</a>
</li><li><li>创建日期：2011-11-18<br>
</li><li><li>修改人：<br>
</li><li><li>修改日期：<br>
</li><li>
</li></ul>public static void main(String<a href='.md'>.md</a> args){<br>
<blockquote>try{<br>
<blockquote>new MasterServer(new WebSocketCoder(), new WebSocketDecoder(), new Processer());<br>
BroadThread.getInstance();<br>
</blockquote>}catch(IOException e){<br>
<blockquote>e.printStackTrace();<br>
</blockquote>}<br>
</blockquote>}<br>
}</blockquote>

2.客户端与服务器相互发送消息<br>
用户在网页上输入一个名字，提交到服务器，服务器回返回"Hello" + 提交的名字。<br>
2.1 重写Service类中的 public Response service(Client sockector, HashMap<String, String> requestData)方法：<br>
public Response service(Client sockector, HashMap<String, String> requestData){<br>
<blockquote>if(requestData == null){<br>
<blockquote>return null;<br>
</blockquote>}</blockquote>

<blockquote>Response responseMessage = null;</blockquote>

<blockquote>try{<br>
<blockquote>if(MyStringUtil.isBlank(requestData.get(Constants.HANDSHAKE))){<br>
<blockquote>responseMessage = Response.msgOnlyBody(requestData.get(Constants.FILED_MSG));<br>
</blockquote>}else{<br>
<blockquote>responseMessage = Response.msgOnlyBody("Hello," + requestData.get(Constants.FILED_MSG));// 将收到的人名前添加hello问候语<br>
</blockquote>}<br>
</blockquote>}catch(Exception e){<br>
<blockquote>e.printStackTrace();<br>
responseMessage = Response.msgOnlyBody("500处理失败了");<br>
</blockquote>}</blockquote>

<blockquote>return responseMessage;<br>
}</blockquote>

2.2 Service类完整代码：<br>
/<br>
<ul><li><li>文件名：Service.java<br>
</li><li><li>说明：<br>
</li><li><li>创建人：CshBBrain;技术博客：<a href='http://cshbbrain.iteye.com/'>http://cshbbrain.iteye.com/</a>
</li><li><li>创建日期：2011-11-27<br>
</li><li><li>修改人：<br>
</li><li><li>修改日期：<br>
</li><li>
package com.jason.server.ws.biz;</li></ul>

import java.util.HashMap;<br>
<br>
import com.jason.server.Client;<br>
import com.jason.server.Response;<br>
import com.jason.util.MyStringUtil;<br>
<br>
/<br>
<ul><li><li>类型名称：<br>
</li><li><li>说明：业务处理类<br>
</li><li><li>创建人：CshBBrain;技术博客：<a href='http://cshbbrain.iteye.com/'>http://cshbbrain.iteye.com/</a>
</li><li><li>创建日期：2011-11-27<br>
</li><li><li>修改人：<br>
</li><li><li>修改日期：<br>
</li><li>
public class Service{<br>
</li></ul><blockquote>private static Service service = new Service();// 服务单实例;// 服务单实例</blockquote>

<blockquote>public static Service getInstance(){<br>
<blockquote>return service;<br>
</blockquote>}</blockquote>

<blockquote>private Service(){}</blockquote>

<blockquote>/<br>
<ul><li>
</li><li><li>方法名：service<br>
</li><li><li>@param requestData<br>
</li><li><li>@return<br>
</li><li><li>返回类型：ResponseMessage<br>
</li><li><li>说明：业务处理入口方法，对各种接口的请求进行处理<br>
</li><li><li>创建人：CshBBrain;技术博客：<a href='http://cshbbrain.iteye.com/'>http://cshbbrain.iteye.com/</a>
</li><li><li>创建日期：2011-12-5<br>
</li><li><li>修改人：<br>
</li><li><li>修改日期：<br>
</li><li>
</li></ul>public Response service(Client sockector, HashMap<String, String> requestData){<br>
<blockquote>if(requestData == null){<br>
<blockquote>return null;<br>
</blockquote>}</blockquote></blockquote>

<blockquote>Response responseMessage = null;</blockquote>

<blockquote>try{<br>
<blockquote>if(MyStringUtil.isBlank(requestData.get(Constants.HANDSHAKE))){<br>
<blockquote>responseMessage = Response.msgOnlyBody(requestData.get(Constants.FILED_MSG));<br>
</blockquote>}else{<br>
<blockquote>responseMessage = Response.msgOnlyBody("Hello," + requestData.get(Constants.FILED_MSG));<br>
</blockquote>}<br>
</blockquote>}catch(Exception e){<br>
<blockquote>e.printStackTrace();<br>
responseMessage = Response.msgOnlyBody("500处理失败了");<br>
</blockquote>}</blockquote>

<blockquote>return responseMessage;<br>
</blockquote><blockquote>}</blockquote>


}<br>
<br>
2.3 客户端页面代码：<br>
<br>
<!DOCTYPE HTML><br>
<br>
<br>
<html><br>
<br>
<br>
<br>
<br>
<head><br>
<br>
<br>
<br>
<br>
<meta charset="utf-8"><br>
<br>
<br>
<br>
<br>
<meta name="apple-mobile-web-app-capable" content="yes" /><br>
<br>
<br>
<br>
<br>
<meta name="apple-mobile-web-app-status-bar-style" content="black" /><br>
<br>
<br>
<br>
<br>
<meta name="format-detection" content="telephone=no"><br>
<br>
<br>
<br>
<br>
<meta name="viewport" content="minimum-scale=1.0,maximum-scale=1,width=device-width,user-scalable=no" /><br>
<br>
<br>
<br>
<br>
<title><br>
<br>
首页<br>
<br>
</title><br>
<br>
<br>
<!--link rel="stylesheet" type="text/css" media="all" href="css/style.css" /--><br>
<br>
<br>
<link rel="stylesheet" type="text/css" media="all and (orientation:portrait)" href="css/style.css" /><br>
<br>
<br>
<br>
<br>
<link rel="stylesheet" type="text/css" media="all and (orientation:landscape)" href="css/style_land.css"/><br>
<br>
<br>
<br>
<br>
<script src="js/scrollpic.js" type="text/javascript"><br>
<br>
<br>
<br>
</script><br>
<br>
<br>
<br>
<br>
<br>
Unknown end tag for </head><br>
<br>
<br>
<br>
<br>
<br>
<body><br>
<br>
<br>
<br>
<br>
<section class="nav"><br>
<br>
<br>
<blockquote><div>
<blockquote><div></blockquote></blockquote>

<blockquote></div>
</blockquote><blockquote><div>
<blockquote>

<input type="text" id="txt" />

<br>
<br>
<br>
<button value="send" onclick="send();"><br>
<br>
send<br>
<br>
</button><br>
<br>
<br>
</blockquote></div>
<blockquote></div>
<br>
<br>
</section><br>
<br>
</blockquote></blockquote>

<br>
<br>
<script language="javascript" type="text/javascript"><br>
<br>
<br>
window.WebSocket = window.WebSocket || window.MozWebSocket;<br>
var socket = new WebSocket("ws://192.168.1.222:9090/echo"); // 注意这里的ip地址改为你自己的地址，创建sockect客户端<br>
<br>
socket.onopen = function(){<br>
<blockquote>//socket.send('i am client');</blockquote>

<blockquote>socket.onclose = function(e){// 关闭连接处理事件<br>
<blockquote>alert('close the socket');<br>
</blockquote>};</blockquote>

<blockquote>socket.onerror = function(msg){// 出错处理事件<br>
<blockquote>alert('error:' + msg );<br>
</blockquote>};<br>
};</blockquote>


socket.onmessage = function(e) {// 收到消息处理事件，将收到的内容以红色显示在页面上<br>
<blockquote>document.getElementById('stock').innerHTML = '<font color='red'>' + event.data + '</font>';<br>
};</blockquote>

function send(){// 发送输入的人名<br>
<blockquote>socket.send(document.getElementById('txt').value);<br>
}</blockquote>

<br>
<br>
</script><br>
<br>
<br>
<br>
<br>
<br>
</body><br>
<br>
<br>
<br>
<br>
<br>
Unknown end tag for </html><br>
<br>
