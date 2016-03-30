package com.fujitsu.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

public class CommonUtil extends BaseCommonUtil{

	/**
	 * @function:ip地址转为十进制格式
	 * @data:2015-1-6
	 * @author cao senrong
	 * @param ip
	 * @return
	 * String 
	 *
	 */
	public static String ipToDecString(String ip){
		String ipCode = "";
		try {
			if(ip!=null && !"".equals(ip)){
				String[] ipCodeArr = ip.split("\\.");
				if(ipCodeArr.length == 4){
					int ipCode1 = Integer.valueOf(ipCodeArr[0]);
					int ipCode2 = Integer.valueOf(ipCodeArr[1]);
					int ipCode3 = Integer.valueOf(ipCodeArr[2]);
					int ipCode4 = Integer.valueOf(ipCodeArr[3]);
					String ipCodeStr1 = Integer.toHexString(ipCode1);
					if(ipCodeStr1.length()==1){
						ipCodeStr1 = "0" + ipCodeStr1;
					}
					String ipCodeStr2 = Integer.toHexString(ipCode2);
					if(ipCodeStr2.length()==1){
						ipCodeStr2 = "0" + ipCodeStr2;
					}
					String ipCodeStr3 = Integer.toHexString(ipCode3);
					if(ipCodeStr3.length()==1){
						ipCodeStr3 = "0" + ipCodeStr3;
					}
					String ipCodeStr4 = Integer.toHexString(ipCode4);
					if(ipCodeStr4.length()==1){
						ipCodeStr4 = "0" + ipCodeStr4;
					}
					String ipCodeHex = ipCodeStr1 + ipCodeStr2 + ipCodeStr3 + ipCodeStr4;
					BigInteger ipCodeDec = new BigInteger(ipCodeHex,16);
					ipCode = ipCodeDec.toString(10);
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return ipCode;
	}

	/**
	 * @function:ip地址转十六进制
	 * @data:2015-1-6
	 * @author cao senrong
	 * @param ip
	 * @return
	 * String 
	 *
	 */
	public static String ipToHexString(String ip){
		String ipCode = "";
		try {
			if(ip!=null && !"".equals(ip)){
				String[] ipCodeArr = ip.split("\\.");
				if(ipCodeArr.length == 4){
					int ipCode1 = Integer.valueOf(ipCodeArr[0]);
					int ipCode2 = Integer.valueOf(ipCodeArr[1]);
					int ipCode3 = Integer.valueOf(ipCodeArr[2]);
					int ipCode4 = Integer.valueOf(ipCodeArr[3]);
					String ipCodeStr1 = Integer.toHexString(ipCode1);
					if(ipCodeStr1.length()==1){
						ipCodeStr1 = "0" + ipCodeStr1;
					}
					String ipCodeStr2 = Integer.toHexString(ipCode2);
					if(ipCodeStr2.length()==1){
						ipCodeStr2 = "0" + ipCodeStr2;
					}
					String ipCodeStr3 = Integer.toHexString(ipCode3);
					if(ipCodeStr3.length()==1){
						ipCodeStr3 = "0" + ipCodeStr3;
					}
					String ipCodeStr4 = Integer.toHexString(ipCode4);
					if(ipCodeStr4.length()==1){
						ipCodeStr4 = "0" + ipCodeStr4;
					}
					ipCode = ipCodeStr1 + ipCodeStr2 + ipCodeStr3 + ipCodeStr4;
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return ipCode;
	}

	/**
	 * @function:十六进制字符串转IP地址
	 * @data:2015-1-6
	 * @author cao senrong
	 * @param hexString
	 * @return
	 * String 
	 *
	 */
	public static String hexStringToIp(String hexString){
		String ip = "";
		try {
			if(hexString!=null && !"".equals(hexString)){
				String ipCodeHex1 = hexString.substring(0, 2);
				String ipCodeHex2 = hexString.substring(2, 4);
				String ipCodeHex3 = hexString.substring(4, 6);
				String ipCodeHex4 = hexString.substring(6, 8);
				BigInteger temp = new BigInteger(ipCodeHex1,16);
				String ipCodeDec1 = temp.toString(10);
				temp = new BigInteger(ipCodeHex2,16);
				String ipCodeDec2 = temp.toString(10);
				temp = new BigInteger(ipCodeHex3,16);
				String ipCodeDec3 = temp.toString(10);
				temp = new BigInteger(ipCodeHex4,16);
				String ipCodeDec4 = temp.toString(10);
				ip = ipCodeDec1 +"."+ipCodeDec2+"."+ipCodeDec3+"."+ipCodeDec4;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ip;
	}

	/**
	 * @function:十进制字符串转IP地址
	 * @data:2015-1-6
	 * @author cao senrong
	 * @param decString
	 * @return
	 * String 
	 *
	 */
	public static String decStringToIp(String decString){
		String ip = "";
		try {
			if(decString!=null && !"".equals(decString)){
				BigInteger ipDec = new BigInteger(decString);
				String ipHex = ipDec.toString(16);
				String ipCodeHex1 = ipHex.substring(0, 2);
				String ipCodeHex2 = ipHex.substring(2, 4);
				String ipCodeHex3 = ipHex.substring(4, 6);
				String ipCodeHex4 = ipHex.substring(6, 8);
				BigInteger temp = new BigInteger(ipCodeHex1,16);
				String ipCodeDec1 = temp.toString(10);
				temp = new BigInteger(ipCodeHex2,16);
				String ipCodeDec2 = temp.toString(10);
				temp = new BigInteger(ipCodeHex3,16);
				String ipCodeDec3 = temp.toString(10);
				temp = new BigInteger(ipCodeHex4,16);
				String ipCodeDec4 = temp.toString(10);
				ip = ipCodeDec1 +"."+ipCodeDec2+"."+ipCodeDec3+"."+ipCodeDec4;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ip;
	}
	
	/**
	 * @function:检查对象属性不为空
	 * @data:2015-1-6
	 * @author cao senrong
	 * @param Object
	 * @return
	 * boolean 
	 *
	 */
	@SuppressWarnings("rawtypes")
	public static boolean checkObjectNotEmpty(Object obj, String[] exceptList){
		boolean unNull = true;
		boolean unCheck = false;
		Class cls = obj.getClass();
    	try {
			java.lang.reflect.Field[] flds = cls.getDeclaredFields();
			if ( flds != null )
			{
			    for ( int i = 0; i < flds.length; i++ )
			    {	
			    	flds[i].setAccessible(true);
//			    	System.out.println(flds[i].getName() + " - " + flds[i].get(obj));
			    	if(exceptList != null){
			    	for(int j=0;j<exceptList.length;j++){
			    		if(flds[i].getName().equals(exceptList[j])){
			    			unCheck = true;
			    			break;
			    		}
			    	}
			    	}
			    	
			    	if(unCheck){
			    		flds[i].setAccessible(false);
			    		break;
			    	}else if(flds[i].get(obj) == null || "".equals(flds[i].get(obj))){
			    		unNull = false;
			    		flds[i].setAccessible(false);
			    		break;
			    	}
			    	flds[i].setAccessible(false);
			    }
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
    	return unNull;
	}
	
	
	/**
	 * @function:
	 * @data:2015-1-12
	 * @author cao senrong
	 * @param filePath
	 * @param fileName
	 * @param Content
	 * @return
	 * boolean 
	 *
	 */
	public static boolean SaveOTDRFile(String filePath,String fileName, String Content)
	{
		String tempFileName = fileName + ".temp";

		File file = null;
		FileWriter writer = null;
		try {
			File path=new File(filePath);
			if(!path.exists()) 
				if(!path.mkdirs())
					new Exception("this file can not be created!.."+path);
			
			file = new File(filePath + "\\" + tempFileName);

			//打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			writer = new FileWriter(file, false);

			writer.write(Content);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (file.exists()) {
			file.renameTo(new File(filePath + "\\" + fileName));
		}
		return true;
	}
	
	public static void saveTestResultToBinaryFile (String filePath, String fileName, byte[] content) {
		File file = new File(filePath + "\\" + fileName);
		try {
			FileOutputStream out = new FileOutputStream(file, false);
			out.write(content);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
