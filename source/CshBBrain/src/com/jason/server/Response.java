/**
 * <li>文件名：ResponseMessage.java
 * <li>说明：
 * <li>创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
 * <li>创建日期：2011-11-11
 * <li>修改人： 
 * <li>修改日期：
 */
package com.jason.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jason.util.MyStringUtil;
import com.jason.util.CoderUtils;

/**
 * <li>类型名称：
 * <li>说明：处理后的响应信息
 * <li>创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
 * <li>创建日期：2011-11-11
 * <li>修改人： 
 * <li>修改日期：
 */
public class Response {
	private static Log log = LogFactory.getLog(Response.class);// 日志记录器
	private String header;// 响应头信息
	private String body;// 响应信息体
	private String filePath;// 文件路径
	private ConcurrentLinkedQueue<ByteBuffer> currentBufferQueue;// 当前正在传输的缓存区队列
	private boolean isBuffered = false;// 是否将内容放入缓存，默认为没有
	private boolean isWriteFinished = false;// 是否完成传输处理
	private long transelateSize = 0;// 已经传输的文件的长度
	private FileInputStream fileInput = null;// 文件流
	private FileChannel fileChannel = null;// 文件传输通道
	private static int callMax = 100000;// 最大呼叫10万次
	private volatile AtomicInteger callCount = new AtomicInteger(0);
	private ByteBuffer currentByteBuffer = null;// 当前的bytebuffer
	
	// 只有body
	public static Response msgOnlyBody(String body){
		Response responseMessage = new Response();
		responseMessage.setBody(body);
		return responseMessage;
	}
	
	// 只有file
	public static Response msgOnlyFile(String filePath){
		Response responseMessage = new Response();
		responseMessage.setFilePath(filePath);
		return responseMessage;
	}
	
	// header 和 file
	public static Response msgHeaderFile(String header, String filePath){
		Response responseMessage = new Response();
		responseMessage.setHeader(header);
		responseMessage.setFilePath(filePath);
		return responseMessage;
	}
	
	// body 和 file
	public static Response msgBodyFile(String body, String filePath){
		Response responseMessage = new Response();
		responseMessage.setBody(body);
		responseMessage.setFilePath(filePath);
		return responseMessage;
	}
	
	// header
	public static Response msgOnlyHeader(String header){
		Response responseMessage = new Response();
		responseMessage.setHeader(header);
		return responseMessage;
	}
	
	// body 和 file
	public static Response msgHeaderBody(String header, String body){
		Response responseMessage = new Response();
		responseMessage.setBody(body);
		responseMessage.setHeader(header);
		return responseMessage;
	}
	
	// header,body 和 file
	public static Response msgBodyFile(String header, String body, String filePath){
		Response responseMessage = new Response();
		responseMessage.setHeader(header);
		responseMessage.setBody(body);
		responseMessage.setFilePath(filePath);
		return responseMessage;
	}
	
	public Response(){
		
	}
	
	/**
	 * 
	 * <li>方法名：write
	 * <li>@param sk
	 * <li>@return
	 * <li>返回类型：boolean
	 * <li>说明：返回true表示传输没有发生异常，返回false表示发生异常
	 * <li>创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
	 * <li>创建日期：2012-3-10
	 * <li>修改人： 
	 * <li>修改日期：
	 */
	public boolean write(Client sk){		
		SocketChannel socketChannel =sk.getChannel();
		if(!this.isBuffered){// 将内容放入到缓冲区中去
			this.bufferedContent();
		}
		
		// 处理响应头，响应体的传输
		ByteBuffer buffer = this.currentBufferQueue.poll();
		if(buffer != null){
			log.info("the postion of the data in write: " + buffer.position());
			buffer.flip();// 反转缓存区
				
			while(buffer != null){
				int transCount = 0;
				try{
					transCount = socketChannel.write(buffer);
					log.info("translate size:" + transCount);
				}catch(IOException e){
					e.printStackTrace();
					this.closeChannel();
					sk.close();
					return false;
				}// 非阻塞的sockect连接一次可能写不完数据
				
				this.callCount.incrementAndGet();//回写计数器
				if(buffer.position() + transCount >= buffer.limit()){// 传完
					buffer.clear();
					BufferPool.getInstance().releaseBuffer(buffer);// 将用完的缓冲区放回缓冲区池
					this.currentBufferQueue.poll();// 从队列中删除缓冲区
					
					// 获取下一个缓冲区进行传输
					buffer = this.currentBufferQueue.poll();
					if(buffer != null){
						buffer.flip();// 反转缓存区
					}
					
				}else{// 没有传完,下次再传输
					buffer.position(buffer.position() + transCount);
					return true;
				}
				
				log.info("向客户端传输数据的长度 : " + transCount);
			}			
		}
		
		// 如果有文件则传输文件
		if(!MyStringUtil.isBlank(this.filePath)){
		    try{
		    	if(this.fileChannel == null){
		    		this.createFileChannel();// 处理文件不存在的问题
		    	}
		    	
		    	// 直接通过文件管道将文件内容输出到客户端
				if(transelateSize < fileChannel.size()){
					Long transferLength = fileChannel.transferTo(transelateSize, (fileChannel.size() - transelateSize), socketChannel);					
					transelateSize += transferLength;
				}else{// 文件传输完毕
					this.closeChannel();
					this.isWriteFinished = true;
				}
				
				this.callCount.incrementAndGet();//回写计数器
		    }catch(IOException e){
		      e.printStackTrace(); 
		      this.closeChannel();// 关闭文件
		      sk.close();
		      return false;
		    }
		}else{
			this.isWriteFinished = true;
		}
		
		return true;// 正常处理
	}
	
	/**
	 * 
	 * <li>方法名：createFileChannel
	 * <li>@return
	 * <li>返回类型：boolean
	 * <li>说明：创建通道
	 * <li>创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
	 * <li>创建日期：2012-2-3
	 * <li>修改人： 
	 * <li>修改日期：
	 */
	private boolean createFileChannel(){
		File requestedFile = new File(this.filePath);
		if(requestedFile != null && requestedFile.exists()){
		    try {				    	
		    	// 直接通过文件管道将文件内容输出到客户端
				this.fileInput = new FileInputStream(requestedFile);   
				this.fileChannel = fileInput.getChannel(); 
		    } catch (IOException e) {   
		      e.printStackTrace(); 
		      this.closeChannel();
		    }
		    return true;
		}
		
		return false;
	}
	
	/**
	 * 
	 * <li>方法名：closeChannel
	 * <li>
	 * <li>返回类型：void
	 * <li>说明：删除文件通道和文件流
	 * <li>创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
	 * <li>创建日期：2012-2-3
	 * <li>修改人： 
	 * <li>修改日期：
	 */
	private void closeChannel(){
		try {
    		if(this.fileInput != null){
    			this.fileInput.close();
    			this.fileInput = null;
    		}
    		
    		if(this.fileChannel != null){
    			this.fileChannel.close();
    			this.fileChannel = null;
    		}
    	}catch (IOException ex) {
    		ex.printStackTrace();   
    	}
	}
	
	/**
	 * 
	 * <li>方法名：bufferedContent
	 * <li>
	 * <li>返回类型：void
	 * <li>说明：将响应头和响应体内容放入到缓存中
	 * <li>创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
	 * <li>创建日期：2012-2-3
	 * <li>修改人： 
	 * <li>修改日期：
	 */
	public void bufferedContent(){
		if(!this.isBuffered){// 如果已经做的缓存就不做处理了
			this.currentBufferQueue = new ConcurrentLinkedQueue<ByteBuffer>();// 当前正在处理的缓冲区队列
			
			ByteBuffer headerBuffer = processBuffered(this.header);		
			ByteBuffer bodyBuffer = processBuffered(headerBuffer, this.body);
			
			if(bodyBuffer != null){
				this.currentBufferQueue.add(bodyBuffer);
			}
			this.isBuffered = true;
		}
	}
	
	/**
	 * 
	 * <li>方法名：getBuffer
	 * <li>@return
	 * <li>返回类型：ByteBuffer
	 * <li>说明：
	 * <li>创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
	 * <li>创建日期：2012-9-18
	 * <li>修改人： 
	 * <li>修改日期：
	 */
	public ByteBuffer getBuffer(){
		if(this.currentBufferQueue == null){
			this.currentBufferQueue = new ConcurrentLinkedQueue<ByteBuffer>();// 当前正在处理的缓冲区队列			
		}
		
		if(this.currentByteBuffer == null || this.currentByteBuffer.remaining() <= 0){
			this.currentByteBuffer = BufferPool.getInstance().getBuffer();
			this.currentBufferQueue.add(this.currentByteBuffer);
		}
		
		return this.currentByteBuffer;
	}
	
	/**
	 * 
	 * <li>方法名：appendBytes
	 * <li>@param datas
	 * <li>返回类型：void
	 * <li>说明：将数据放入到缓冲区中
	 * <li>创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
	 * <li>创建日期：2012-9-19
	 * <li>修改人： 
	 * <li>修改日期：
	 */
	public void appendBytes(byte...datas){
		this.isBuffered = true;
		ByteBuffer buffer = this.getBuffer();
		if(buffer.remaining() >= datas.length){// 缓冲区的空间大于数据的长度
			buffer.put(datas);
			log.info("the postion of the data: " + buffer.position());
		}else{// 缓冲区的空间小于数据的长度
			int offset = 0;// 初始相对位移为0
			int length = buffer.remaining();// 初始传递的数据长度为缓冲区的剩余长度
					
			do{
				buffer.put(datas, offset, length);
				offset += length;
				
				if(offset < datas.length){
					buffer = this.getBuffer();
					length = (buffer.remaining() > (datas.length - offset) ? (datas.length - offset) : buffer.remaining());
				}else{
					break;
				}
			}while(true);
		}
	}
	
	/**
	 * 
	 * <li>方法名：processBuffered
	 * <li>@param msg
	 * <li>@return
	 * <li>返回类型：ByteBuffer
	 * <li>说明：将内容放入到缓存，默认设置缓冲区为空
	 * <li>创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
	 * <li>创建日期：2012-2-3
	 * <li>修改人： 
	 * <li>修改日期：
	 */
	private ByteBuffer processBuffered(String msg){
		return this.processBuffered(null, msg);
	}
	
	/**
	 * 
	 * <li>方法名：processBuffered
	 * <li>@param bb
	 * <li>@param msg
	 * <li>@return
	 * <li>返回类型：ByteBuffer
	 * <li>说明：带缓冲区的将内容放入到缓冲区
	 * <li>创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
	 * <li>创建日期：2012-2-3
	 * <li>修改人： 
	 * <li>修改日期：
	 */
	private ByteBuffer processBuffered(ByteBuffer bb, String msg){
		// 处理没有内容的情况
		if(msg == null){
			return bb;
		}
		
		byte[] header = CoderUtils.toByte(msg);
		//byte[] header = Utf8Coder.toNormalByte(msg);
		int cotentLength = header.length;
		int count = 0;
		
		if(bb == null){// 如果缓冲区为空，则创建一个缓冲区
			bb = BufferPool.getInstance().getBuffer();
		}
		
		do{			
			int mark = cotentLength - count;// 设置往缓冲区写入字节数组的上限下标
			boolean newBuffer = false; // 下次循环是否重新获取缓冲区标识符
			
			if(mark > bb.remaining()){// 如果字节数组剩余的内容大于缓存区的大小取缓冲区的剩余大小为字节数组
				mark = bb.remaining();
				newBuffer = true;
			}
			
			bb.put(header, count, mark);
			count += mark;
			
			if(newBuffer){
				this.currentBufferQueue.add(bb);// 将放满内容的缓冲区放入到队列中
				bb = BufferPool.getInstance().getBuffer();
			}else{
				return bb;
			}
			
		}while(cotentLength > count);
		
		return null;
	}
	
	/**
	 * 
	 * <li>方法名：isException
	 * <li>@return
	 * <li>返回类型：boolean
	 * <li>说明：判断传输是否出现了异常
	 * <li>创建人：CshBBrain;技术博客：http://cshbbrain.iteye.com/
	 * <li>创建日期：2012-2-24
	 * <li>修改人： 
	 * <li>修改日期：
	 */
	public boolean isException(){
		boolean isExc = callCount.incrementAndGet() > callMax;
		if(isExc){
			this.closeChannel();
		}
		return isExc;
	}
		
	public Response(String header){
		this.header = header;
	}
	
	public Response(String header,String body){
		this.header = header;
		this.body = body;
	}
	
	public Response(String header,String body,String filePath){
		this.header = header;
		this.body = body;
		this.filePath = filePath;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public ConcurrentLinkedQueue<ByteBuffer> getCurrentBufferQueue() {
		return currentBufferQueue;
	}

	public void setCurrentBufferQueue(
			ConcurrentLinkedQueue<ByteBuffer> currentBufferQueue) {
		this.currentBufferQueue = currentBufferQueue;
	}

	public boolean isBuffered() {
		return isBuffered;
	}

	public void setBuffered(boolean isBuffered) {
		this.isBuffered = isBuffered;
	}

	public boolean isWriteFinished() {
		return isWriteFinished;
	}

	public void setWriteFinished(boolean isWriteFinished) {
		this.isWriteFinished = isWriteFinished;
	}

	public long getTranselateSize() {
		return transelateSize;
	}

	public void setTranselateSize(long transelateSize) {
		this.transelateSize = transelateSize;
	}

	public AtomicInteger getCallCount() {
		return callCount;
	}

	public void setCallCount(AtomicInteger callCount) {
		this.callCount = callCount;
	}

	public ByteBuffer getCurrentByteBuffer() {
		return currentByteBuffer;
	}

	public void setCurrentByteBuffer(ByteBuffer currentByteBuffer) {
		this.currentByteBuffer = currentByteBuffer;
	}	
}
