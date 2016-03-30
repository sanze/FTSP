package com.fujitsu.common;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CommonMethod {
	
	// 转换Map 内容 字符串“null" -> null
	public  static Map<String, Object>  ChangeMapNull(Map<String, Object> map)
	{
		Map<String, Object> resultMap = new HashMap<String, Object>();
		try
		{
			if(map != null)
			{
				Set set = map.entrySet();
				for(Iterator iter = set.iterator(); iter.hasNext();)
				{
					Map.Entry entry = (Map.Entry)iter.next();

					String key = (String)entry.getKey();

					if(entry.getValue() == null || entry.getValue().equals("null")){
						resultMap.put(key, null);
					}else{
						resultMap.put(key, entry.getValue().toString());
					}
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return resultMap;
	}

	public static String[] constructMHStringToArray(String str) {

		if (str.length() > 0) {
			String[] composite;
			composite = str.split(":");

			for (int i = 0; i < composite.length; i++) {
				if (composite[i].equals("")) {
					composite[i] = "NONE";
				}
			}
			return composite;
		}

		return null;
	}

	public static String getEqptSerialNo(String str) {
		String composite=""; 
		if (str.length() > 0) {
			if(str.contains("210012")){
				composite = str.substring(0, 6);
			}else{
				composite = str.substring(0, 5);
			}
		} 
		return composite;
	}

	//获取设备类型（通过映射）
	public static String getEquipType(String serialNo) {
		String result = "";
		String[] composite;
		if(CommonDefine.EQUIP_SERIAL_NO_MAP.containsKey(serialNo)){
			String value = CommonDefine.EQUIP_SERIAL_NO_MAP.get(serialNo).toString();		
			composite = constructMHStringToArray(value);
			result = composite[0];
		}

		return result;
	} 
	//获取设备所在槽道（通过映射）
	public static String getEquipSlot(String serialNo) {
		String result = "";
		String[] composite;
		if(CommonDefine.EQUIP_SERIAL_NO_MAP.containsKey(serialNo)){
			String value = CommonDefine.EQUIP_SERIAL_NO_MAP.get(serialNo).toString();		
			composite = constructMHStringToArray(value);
			result = composite[1];
		}

		return result;
	}
	
	//获取设备端口数（通过映射）
	public static String getEquipPortCount(String serialNo) {
		String result = "";
		String[] composite;
		if(CommonDefine.EQUIP_SERIAL_NO_MAP.containsKey(serialNo)){
			String value = CommonDefine.EQUIP_SERIAL_NO_MAP.get(serialNo).toString();		
			composite = constructMHStringToArray(value);
			result = composite[2];
		}

		return result;
	}
	
	//获取设备厂家（通过映射）
	public static String getEquipFactory(String serialNo) {
		String result ="";
		String[] composite;
		if(CommonDefine.EQUIP_SERIAL_NO_MAP.containsKey(serialNo)){
			String value = CommonDefine.EQUIP_SERIAL_NO_MAP.get(serialNo).toString();		
			composite = constructMHStringToArray(value);
			result = composite[3];
		}

		return result;
	}
	
	//获取设备具体参数（通过映射）
	public static String getEquipWaveLen(String serialNo) {
		String result ="";
		String[] composite;
		if(CommonDefine.EQUIP_SERIAL_NO_MAP.containsKey(serialNo)){
			String value = CommonDefine.EQUIP_SERIAL_NO_MAP.get(serialNo).toString();		
			composite = constructMHStringToArray(value);
			result = composite[4];
		}

		return result;
	}
	/**
	 * @function:ip地址转为十进制格式
	 * @data:2014-6-23
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
					System.out.println(ipCode);
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return ipCode;
	}
}
