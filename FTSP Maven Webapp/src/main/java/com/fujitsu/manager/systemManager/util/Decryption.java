package com.fujitsu.manager.systemManager.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.snmp4j.security.DecryptParams;
import org.snmp4j.security.PrivDES;

/**
 * 利用SNMP4J提供的DES算法对配置文件进行解密
 * 
 * @author ZhongLe
 * 
 */
public class Decryption {
	
	private static Logger log = Logger.getLogger(Decryption.class);
	/**
	 * 解密测试，控制台输入加密后文件路径，控制台打印解密结果集
	 * 
	 * @param ars
	 */
	public static void main(String[] ars) {
		System.out.println("解密开始");
		Map<String, String> map = null;
		try {
			Decryption cryp = Decryption.getInstance();
			map = cryp.decryption("./license.txt");
			if (map == null){
				System.err.println("解密出错！");
				log.error("解密出错！");
			}
			else{
				System.out.println(map);
				log.info(map);
			}
		} catch (IOException e) {
			System.err.println("读写操作出错！");
			e.printStackTrace();
			log.error(e,e.fillInStackTrace());
		}
		System.out.println("解密结束");
	}

	// fields
	private static Decryption decryption = null;
	private PrivDES des = null;

	private Decryption() {
		des = new PrivDES();
	}

	/**
	 * 单例模式获取对象
	 * 
	 * @return 返回该类实例
	 */
	public static Decryption getInstance() {
		if (decryption == null)
			decryption = new Decryption();
		return decryption;
	}

	/**
	 * 私有方法，获取解密所需计算机信息并以字符串返回
	 * 
	 * @return 返回MAC地址、硬盘序列号、CPU序列号、操作系统信息
	 * @throws IOException
	 */
	private String getInfos() throws IOException {
		StringBuffer sb = new StringBuffer();
		String temp = null;
//		// 获取MAC地址
//		if ((temp = GetInfo.getMACAddress()) != null)
//			sb.append(temp);
//		else {
//			System.err.println("收集网卡信息出错！");
//			return null;
//		}
//		// 获取CPU序列号
//		if ((temp = GetInfo.getCPUSerial()) != null)
//			sb.append(temp);
//		else {
//			System.err.println("收集CPU信息出错！");
//			return null;
//		}
		// 获取操作系统信息
		if ((temp = GetInfo.getOsInfo()) != null)
			sb.append(temp);
		else {
			System.err.println("收集OS信息出错！");
			return null;
		}
		// 获取硬盘序列号
		if ((temp = GetInfo.getHardDiskNum()) != null)
			sb.append(temp);
		else {
			System.err.println("收集硬盘信息出错！");
			return null;
		}
		return sb.toString();
	}

	/**
	 * 解密配置文件，使用hashmap返回配置文件中保存的属性值对
	 * 
	 * @param cryptedFileName
	 *            待解密的配置文件名
	 * @return 使用Hashmap返回解密成功后的配置数据
	 * @throws IOException
	 */
	public HashMap<String, String> decryption(String cryptedFileName)
			throws IOException {

		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		HashMap<String, String> props = new HashMap<String, String>();

		// 将获得的计算机信息作为key
		String k = null;
		byte[] encryptionKey = null;
		if ((k = getInfos()) != null){
//			k = k.substring(10, 46);
			encryptionKey = k.getBytes();
		}
		else{
			log.info("key is null");
			return null;
		}

		// 初始化待解密的密文文件
		File inputFile = new File(cryptedFileName);
		byte[] buffer = new byte[256];
		FileInputStream input = new FileInputStream(inputFile);

		// 一个个buffer的读入并解密
		DecryptParams decryptParams = new DecryptParams(new byte[8], 0, 8);// 解密的时候需要这个参数
		int total = -1; // 存储读入的byte数目

		// 先读入解密所需的参数
		while ((total = input.read(decryptParams.array)) != -1) {
			if (total != 8)
				return null;
			// 再读入密文
			if ((total = input.read(buffer)) != -1) {
				byte[] result = des.decrypt(buffer, 0, buffer.length,
						encryptionKey, 0, 0, decryptParams);
				//若解密失败返回null
				if(result == null)
					return null;
				// 输出转化为字符串
				sb.append(new String(result, 0, total));
			}
		}
		input.close();

		// 将解密的配置信息整理并放入Hashmap
		br = new BufferedReader(new StringReader(sb.toString()));
		String line = null;
		String[] sa = null;
		int i = 0;
		while ((line = br.readLine()) != null) {
			// 判断是否为注释行
			if ((i = line.indexOf("//")) != -1)
				//若是，则去掉注释
				line = line.substring(0, i);
			if (!(line.trim().equals(""))) {
				sa = line.split("=");
				if (sa.length < 2)
					return null;
				props.put(sa[0].trim(), sa[1].trim());
			}
		}
		br.close();

		if (props.isEmpty())
			return null;
		return props;
	}
}
