package com.fujitsu.manager.systemManager.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;


/**
 * 获取计算机Mac、CPU序列号、硬盘序列号、OS序列号
 * 
 * @author ZhongLe
 */
public class GetInfo {
	private static Logger log = Logger.getLogger(GetInfo.class);

	/**
	 * main函数生成一个存储本机配置信息的文件
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("正在收集计算机信息...");
		File file = new File("./osinfo.txt");
		FileWriter fw;
		try {
			fw = new java.io.FileWriter(file);
			String temp[] = new String[14];
			temp[0] = System.getProperty("java.version");
			temp[1] = System.getProperty("java.vendor");
			temp[2] = System.getProperty("java.vm.specification.version");
			temp[3] = System.getProperty("java.class.version");
			temp[4] = System.getProperty("java.class.path");
			temp[5] = getMACAddress();
			temp[6] = System.getProperty("os.name");
			temp[7] = getHardDiskNum();
//			temp[8] = getCPUSerial();
			temp[8] = System.getProperty("os.arch");
			temp[9] = System.getProperty("os.version");
			temp[10] = getOsInfo();
			temp[11] = System.getProperty("user.name");
			temp[12] = System.getProperty("user.home");
			temp[13] = System.getProperty("user.dir");
			for (String s : temp) {
				if (s == null) {
					temp = null;
					break;
				}
			}
			if (temp != null) {
				fw.write("Java 运行时环境版本:" + temp[0] + "\r\n");
				fw.write("Java 运行时环境供应商:" + temp[1] + "\r\n");
				fw.write("Java 虚拟机规范版本:" + temp[2] + "\r\n");
				fw.write("Java 类格式版本号:" + temp[3] + "\r\n");
				fw.write("Java 类路径:" + temp[4] + "\r\n");
				fw.write("操作系统网卡地址:" + temp[5] + "\r\n");
				fw.write("操作系统的名称:" + temp[6] + "\r\n");
				fw.write("硬盘序列号:" + temp[7] + "\r\n");
//				fw.write("处理器序列号:" + temp[8] + "\r\n");
				fw.write("操作系统的架构:" + temp[8] + "\r\n");
				fw.write("操作系统的版本:" + temp[9] + "\r\n");
				fw.write("操作系统序列号:" + temp[10] + "\r\n");
				fw.write("用户的账户名称:" + temp[11] + "\r\n");
				fw.write("用户的主目录:" + temp[12] + "\r\n");
				fw.write("用户的当前工作目录:" + temp[13] + "\r\n");
			}
			else{
				System.err.println("采集信息出错！");
			}
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e,e.fillInStackTrace());
		}
		System.out.println("信息收集完毕！");
	}

	/**
	 * 静态方法，返回网卡物理地址
	 * 
	 * @return 返回获得的物理地址
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public static String getMACAddress() throws SocketException,
			UnknownHostException {
		// 获得网络接口对象（即网卡），并得到mac地址，mac地址存在于一个byte数组中。
		byte[] mac = NetworkInterface.getByInetAddress(
				InetAddress.getLocalHost()).getHardwareAddress();
		if (mac == null)
			return null;
		// 把mac地址拼装成String
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mac.length; i++) {
			// mac[i] & 0xFF 把byte转化为正整数
			String s = Integer.toHexString(mac[i] & 0xFF);
			sb.append(s.length() == 1 ? 0 + s : s);
		}
		// 把字符串所有小写字母改为大写成为正规的mac地址并返回
		return sb.toString().toUpperCase();
	}

	/**
	 * 静态方法，返回硬盘C盘序列号
	 * 
	 * @return 返回获得的C盘序列号，如失败则返回null
	 * @throws IOException
	 *             获取命令行输出失败则抛出异常
	 */
	public static String getHardDiskNum() throws IOException {
		// 命令行执行命令并获取返回的输出流
		BufferedReader buffreader = new BufferedReader(new InputStreamReader(
				Runtime.getRuntime().exec("cmd /c dir c:").getInputStream()));
		// 读入命令行输出,并取得硬盘序列号
		String line = null;
		while ((line = buffreader.readLine()) != null) {
			if (line.indexOf("卷的序列号是 ") != -1) {
				return line.substring(
						line.indexOf("卷的序列号是 ") + "卷的序列号是 ".length(),
						line.length());
			}
		}
		buffreader.close();
		return null;
	}

	/**
	 * 静态方法， 利用wmic获取cpu序列号
	 * 
	 * @return 返回cpu序列号，若获取失败则返回null
	 * @throws IOException
	 */
	public static String getCPUSerial() throws IOException {
		// 命令行执行命令并获取返回的输出流 wmic在xp环境中需要手工关闭输出流否则会柱塞进程
		Process p = Runtime.getRuntime().exec("wmic CPU get processorid");
		p.getOutputStream().close();
		BufferedReader buffreader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		// 读入命令行输出,并取得硬盘序列号
		String line = null;
		while ((line = buffreader.readLine()) != null) {
			if (line.indexOf("ProcessorId") != -1) {
				while ((line = buffreader.readLine()) != null) {
					if (!(line = line.trim()).equals("")) {
						return line.trim();
					}
				}
			}
		}
		buffreader.close();
		return null;
	}

	/**
	 * 静态方法， 利用wmic获取操作系统序列号
	 * 
	 * @return 返回操作系统安装序列号
	 * @throws IOException
	 */
	public static String getOsInfo() throws IOException {
		// 命令行执行命令并获取返回的输出流 wmic在xp环境中需要手工关闭输出流否则会柱塞进程
		Process p = Runtime.getRuntime().exec("wmic OS get SerialNumber");
		p.getOutputStream().close();
		BufferedReader buffreader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		// 读入命令行输出,并取得硬盘序列号
		String line = null;
		while ((line = buffreader.readLine()) != null) {
			if (line.indexOf("SerialNumber") != -1) {
				while ((line = buffreader.readLine()) != null) {
					if (!line.trim().equals("")) {
						return line.trim();
					}
				}
			}
		}
		buffreader.close();
		return null;
	}

}
