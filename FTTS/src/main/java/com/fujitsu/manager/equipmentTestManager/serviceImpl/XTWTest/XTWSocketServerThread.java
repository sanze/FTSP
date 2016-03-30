package com.fujitsu.manager.equipmentTestManager.serviceImpl.XTWTest;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.fujitsu.IService.IDeviceTest;
import com.fujitsu.util.SpringContextUtil;

/**
 * @Description：昕天卫接收设备推送信息服务
 * @author cao senrong
 * @date 2015-1-8
 * @version V1.0
 */
public class XTWSocketServerThread  implements Runnable{
	
	public static void main(String[] args) throws IOException {
		new XTWSocketServerThread().run();
	}
	
	public static final int SERVER_PORT = 5000;

	private ServerSocket serverSocket = null;

	private ExecutorService executorService = null;

	private final int POOL_SIZE = 2;

	public XTWSocketServerThread() throws IOException {
		// 检查系统CPU个数
		int cpuCount = Runtime.getRuntime().availableProcessors();
		// 创建线程池，用于支持多线程处理
		executorService = Executors.newFixedThreadPool(cpuCount * POOL_SIZE);
		// 创建ServerSocket实例
		System.out.println("[SocketServer MSG]: Socket server main thread starting---------------\n");
		serverSocket = new ServerSocket(SERVER_PORT);
	}
	
    public void run() {
    	// 开始监听，等待请求
        System.out.println("[SocketServer MSG]: Socket server main thread started---------------\n");
        try {
        	while (true) {
    			try {
    				Socket socket = serverSocket.accept();
    				executorService.execute(new analyzeResponse(socket));
    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     
}

class analyzeResponse implements Runnable {
	private Socket socket = null;

	public analyzeResponse(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			String clientIp = socket.getInetAddress().getHostAddress();
			System.out.println("[SocketServer MSG]: Get Request from" + clientIp + ":" + socket.getPort());
			// 获得输入流
			InputStream socketInStream = socket.getInputStream();
			
			IDeviceTest deviceTest = (IDeviceTest)SpringContextUtil.getBean("xtwService");

			deviceTest.socketServerMsgHandle(socketInStream);
			System.out.println("[SocketServer MSG]: Request (" + clientIp + ":" + socket.getPort()+") handled");
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (socket != null)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
}

