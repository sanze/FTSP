package com.fujitsu.manager.equipmentTestManager.serviceImpl.XTWTest.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * @function：
 * @author cao senrong
 * @date 2014-4-11
 * @version V1.0
 */
public class RTU {
	
	private Socket socket;
	private InputStream is;
	private PrintStream out;
	
	
	/**
	 * @function:
	 * @data:2014-4-11
	 * @author cao senrong
	 * @param rtuIp
	 * @param rtuPort
	 * @return
	 *
	 */
	public boolean connect(String rtuIp, int rtuPort){
		boolean result = false;
		SocketAddress socketAddress = new InetSocketAddress(rtuIp, rtuPort);
		socket = new Socket();
		for(int i=0; i<3; i++){
			try {
				Thread.sleep(4000);
				System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$     connect");
				socket.connect(socketAddress, 5000);
				socket.setSoTimeout(5000);
				result = socket.isConnected();
				if(result){
					is = socket.getInputStream();
					out = new PrintStream(socket.getOutputStream());
					break;
				}
			} catch (Exception e) {
				continue;
			}
		}
		
		return result;
	}
	
	/**
	 * @function:
	 * @data:2014-4-11
	 * @author cao senrong
	 *
	 */
	public void disConnect(){
		if(out != null){
			out.close();
		}
		if(is != null){
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(socket != null){
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * @function:
	 * @data:2014-4-11
	 * @author cao senrong
	 * @param command
	 * @return
	 *
	 */
	public String sendCommand(String command){
		StringBuffer stringBuffer = new StringBuffer();
		for(int n=0;n<3;n++){
			try {
				Thread.sleep(500);
				out.print(command);
				out.flush();
				System.out.println("sending command ：" + command);
				byte[] serByte = new byte[2048];
				if(command.contains("918C")||command.contains("818C")){
					Thread.sleep(15000);
				}else{
					Thread.sleep(4000);
				}
				
				int flag = is.read(serByte);
				for(int i = 0; i < serByte.length; i++){
				  if((serByte[i]&0xff) < 128){
					  stringBuffer.append((char)(serByte[i]&0xff));
				  }else{
					  byte[] str = new byte[100];
					  System.arraycopy(serByte, i, str, 0, 100);
					  String result = new String(str,"gbk");
					  stringBuffer.append(result);
					  break;
				  }
				}
				break;
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		System.out.println("get result ："+stringBuffer.toString());
		return stringBuffer.toString();
	}
	
	
//	public static void main(String[] args) {
//		String preCommand = "__________________________________________________________________________________________________________________________________________________________________________________________________________________";
//		RTU rtu = new RTU();
//		rtu.connect("192.168.101.151", 5000);
//		rtu.sendCommand(preCommand+"960C          RTU0000001 1 510");
//	}
}



