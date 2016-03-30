package com.fujitsu.manager.systemManager.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.snmp4j.security.DecryptParams;
import org.snmp4j.security.PrivDES;

/**
 * 利用SNMP4J提供的DES算法对配置文件进行加密
 * 
 * @author ZhongLe
 * 
 */
public class Cryption {
	/**
	 * 加密，控制台输入待加密文件和加密后文件路径以及系统信息文件路径，控制台打印解密结果集
	 * 
	 * @param ars
	 */
	public static void main(String[] ars) {
//		System.out.println("加密配置文件");
//		String input, output, key;
//		Scanner in = new Scanner(System.in);
//		System.out.println("请输入待加密文件文件名：");
//		input = in.next();
//		System.out.println("请输入加密后文件文件名：");
//		output = in.next();
//		System.out.println("请输入系统信息文件文件名：");
//		key = in.next();
		try {
			Cryption cryp = Cryption.getInstance();
			if (cryp.encryption("./config.txt", "./license.txt", cryp.getKey("./osinfo.txt")))
				System.out.println("加密结束");
			else
				System.err.println("加密出错！");
		} catch (IOException e) {
			System.err.println("读写操作出错！");
			e.printStackTrace();
		}

		System.out.println("加密结束");
	}

	// fields
	private static Cryption cryption = null;
	private PrivDES des = null;

	private Cryption() {
		des = new PrivDES();
	}

	/**
	 * 单例模式获取对象
	 * 
	 * @return 返回该类实例
	 */
	public static Cryption getInstance() {
		if (cryption == null)
			cryption = new Cryption();
		return cryption;
	}

	/**
	 * 加密配置文件，需要源文件路径、输出文件路径、String类型的密钥
	 * 
	 * @param unencryptedFileName
	 *            待加密的配置文件
	 * @param cryptedFileName
	 *            输出的秘文路径
	 * @param key
	 *            加密密钥
	 * @return 加密成功返回true 否则返回false
	 * @throws IOException
	 */
	private boolean encryption(String unencryptedFileName,
			String cryptedFileName, String key) throws IOException {

		// 将key转化为byte数组
		byte[] encryptionKey = null;
		if ((key) != null){
//			key = key.substring(10, 46);
			encryptionKey = key.getBytes();
		}
		else
			return false;

		// 初始化输出文件
		File outputFile = new File(cryptedFileName);
		if (!outputFile.exists())
			outputFile.createNewFile();
		FileOutputStream output = new FileOutputStream(outputFile);

		// 初始化待加密的源文件
		byte[] buffer = new byte[256];//buffer大小
		File inputFile = new File(unencryptedFileName);
		FileInputStream input = new FileInputStream(inputFile);

		// 一个个buffer的加密
		int total = -1; // 存储读入的byte数目
		DecryptParams decryptParams = new DecryptParams();// 解密的时候需要这个参数

		byte[] result = null;
		while ((total = input.read(buffer)) != -1) {
			result = des.encrypt(buffer, 0, total, encryptionKey, 92, 0,
					decryptParams);
			//若加密失败返回false
			if(result == null)
				return false;
			// 先写入解密需要的参数再写入秘文
			output.write(decryptParams.array);
			output.write(result);
		}

		output.close();
		input.close();
		return true;
	}

	/**
	 * 私有方法，根据配置文件生成密钥
	 * 
	 * @param keyFileName
	 *            存储系统信息的文件名
	 * @return 返回由文件中信息生成的密钥，若失败返回null
	 * @throws IOException
	 */
	private String getKey(String keyFileName) throws IOException {
		String harddisk = null, cpu = null, os = null, mac = null;
		// 从系统信息文件读入
		BufferedReader br = new BufferedReader(new FileReader(keyFileName));
		String line = null;
		while ((line = br.readLine()) != null) {
//			if (line.indexOf("操作系统网卡地址:") != -1) {
//				mac = line.substring(line.indexOf("操作系统网卡地址:")
//						+ "操作系统网卡地址:".length(), line.length());
//				continue;
//			}
//			if (line.indexOf("处理器序列号:") != -1) {
//				cpu = line.substring(line.indexOf("处理器序列号:")
//						+ "处理器序列号:".length(), line.length());
//			}
			if (line.indexOf("操作系统序列号:") != -1) {
				os = line.substring(line.indexOf("操作系统序列号:")
						+ "操作系统序列号:".length(), line.length());
				continue;
			}
			else if (line.indexOf("硬盘序列号:") != -1) {
				harddisk = line.substring(line.indexOf("硬盘序列号:")
						+ "硬盘序列号:".length(), line.length());
				continue;
			}
		}
		br.close();
		// 返回密钥
		if (harddisk != null && os != null)
			return os + harddisk;
		return null;
	}
}
