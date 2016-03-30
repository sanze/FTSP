package com.fujitsu.util;

import globaldefs.NameAndStringValue_T;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import com.fujitsu.common.DataCollectDefine;

public class NameAndStringValueUtil {

	public static String sourceEncode = DataCollectDefine.ENCODE_ISO;

	public NameAndStringValue_T[] constructNeName(String internalEmsName,
			String neSerialNo) {
		NameAndStringValue_T tempName1 = new NameAndStringValue_T();
		NameAndStringValue_T tempName2 = new NameAndStringValue_T();

		tempName1.name = DataCollectDefine.COMMON.EMS;
		tempName1.value = internalEmsName;
		tempName2.name = DataCollectDefine.COMMON.MANAGED_ELEMENT;
		tempName2.value = neSerialNo;
		NameAndStringValue_T[] neName = { tempName1, tempName2 };

		return neName;
	}

	public NameAndStringValue_T[] constructName(String item, String internalEmsName,
			String neSerialNo) {
		if (item.length() > 0) {
			NameAndStringValue_T[] name;
			NameAndStringValue_T tempName;
			String[] composite;
			composite = item.split(":");
			name = new NameAndStringValue_T[composite.length / 2 + 2];
			name[0] = new NameAndStringValue_T();
			name[0].name = DataCollectDefine.COMMON.EMS;
			name[0].value = internalEmsName;
			name[1] = new NameAndStringValue_T();
			name[1].name = DataCollectDefine.COMMON.MANAGED_ELEMENT;
			name[1].value = neSerialNo;
			for (int i = 0, j = 2; i < composite.length; i++, j++) {
				tempName = new NameAndStringValue_T();
				tempName.name = composite[i];
				tempName.value = composite[i + 1];
				name[j] = tempName;
				i++;
			}
			return name;
		}
		return null;
	}

	public String decompositionName(NameAndStringValue_T[] item) {
		if (item.length > 2) {
			StringBuilder tempString = new StringBuilder();
			for (int i = 2; i < item.length; i++) {
				tempString.append(item[i].name);
				tempString.append(":");
				tempString.append(item[i].value);
				if (i != item.length - 1) {
					tempString.append(":");
				}
			}
			return tempString.toString();
		} else {
			return getNeSerialNo(item);
		}
	}
	
	public String decompositionCtpName(NameAndStringValue_T[] item) {
		if (item.length > 3) {
			StringBuilder tempString = new StringBuilder();
			for (int i = 3; i < item.length; i++) {
				tempString.append(item[i].name);
				tempString.append(":");
				tempString.append(item[i].value);
				if (i != item.length - 1) {
					tempString.append(":");
				}
			}
			return tempString.toString();
		} else {
			return "";
		}
	}

	public String getNeSerialNo(NameAndStringValue_T[] meName) {
		String neSerialNo = null;
		if (meName.length > 1) {
			neSerialNo = meName[1].value;
		} else {
		}
		return neSerialNo;
	}

	public NameAndStringValue_T[] getMeNameFromPtpName(
			NameAndStringValue_T[] ptpName) {

		NameAndStringValue_T tempName1 = ptpName[0];
		NameAndStringValue_T tempName2 = ptpName[1];
		NameAndStringValue_T[] meName = { tempName1, tempName2 };
		return meName;
	}
	
	public NameAndStringValue_T[] getPtpNameFromCtpName(
			NameAndStringValue_T[] ctpName) {
		NameAndStringValue_T tempName1 = ctpName[0];
		NameAndStringValue_T tempName2 = ctpName[1];
		NameAndStringValue_T tempName3 = ctpName[2];
		NameAndStringValue_T[] ptpName = { tempName1, tempName2, tempName3 };
		return ptpName;
	}

	public static HashMap<String, String> getNameMapFromNameValue(
			String nameValue) {
		HashMap<String, String> nameMap = new HashMap<String, String>();
		String[] tempString = nameValue.split("/");
		for (String temp : tempString) {
			String[] string = temp.split("=");
			if (string.length == 2) {
				if (nameMap.containsKey(string[0])) {

				} else {
					nameMap.put(string[0], string[0] + "=" + string[1]);
				}
			}
		}
		return nameMap;
	}

	public String getEquipmentNoFromTargetName(
			NameAndStringValue_T[] targetName, String targetType) {
		HashMap<String, String> nameMap = getNameMapFromNameValue(targetName[2].value);

		String targetNo = "";
		if (nameMap.get(targetType) != null
				&& nameMap.get(targetType).split("=").length == 2) {
			targetNo = nameMap.get(targetType).split("=")[1];
		}
		return targetNo;
	}

	public String getEquipmentNoFromTargetName(String targetName,
			String targetType) {
		HashMap<String, String> nameMap = getNameMapFromNameValue(targetName);

		String targetNo = "";
		if (nameMap.get(targetType) != null
				&& nameMap.get(targetType).split("=").length == 2) {
			targetNo = nameMap.get(targetType).split("=")[1];
		}
		return targetNo;
	}

	public static String Stringformat(String value, String encode) {
		String tempStr = "";
		try {
			if (encode!=null&&!encode.isEmpty()) {
				tempStr = new String(
						value.getBytes(sourceEncode), encode);
			} else {//自动识别编码
				String sysEncoding = System.getProperty("file.encoding");
				if(sysEncoding==null||sysEncoding.isEmpty()){
					sysEncoding = DataCollectDefine.ENCODE_GBK;
				}
				tempStr = new String(
						value.getBytes(sourceEncode),
						sysEncoding);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tempStr;
	}

	public String decompositionLayRates(short[] layRates) {
		StringBuilder tempString = new StringBuilder();
		for (int i = 0; i < layRates.length; i++) {
			tempString.append(String.valueOf(layRates[i]));
			if (i != layRates.length - 1) {
				tempString.append(":");
			}
		}
		return tempString.toString();
	}
	
	public short[] constructLayRates(String layRatesString) {
		short[] layRate = new short[]{};
		if(!layRatesString.trim().isEmpty()){
		String[] composite = layRatesString.split(":");
		if(composite.length>0){
			layRate = new short[composite.length];
		for (int i = 0; i < composite.length; i++) {
			layRate[i] = Short.valueOf(composite[i]);
		}
			}
		}
		return layRate;
	}

	public static void main(String args[]) {
		System.out
				.println(getNameMapFromNameValue("/direction=src/layerrate=1/ptptype=Backplane_Bus_Out/rack=0/shelf=6/slot=9/port=2"));
		System.out
				.println(getNameMapFromNameValue("PTP:/rack=1/shelf=1/slot=1/domain=sdh/port=1"));
		HashMap<String, String> nameMap = getNameMapFromNameValue("PTP:/rack=1/shelf=1/slot=415/domain=wdm/port=16");
		String rackNo = nameMap.get(DataCollectDefine.COMMON.RACK).split("=")[1];
		String shelfNo = nameMap.get(DataCollectDefine.COMMON.SHELF).split("=")[1];
		String slotNo = nameMap.get(DataCollectDefine.COMMON.SLOT).split("=")[1];
		String portNo = nameMap.get(DataCollectDefine.COMMON.PORT).split("=")[1];
		System.out.println(rackNo);
		System.out.println(shelfNo);
		System.out.println(slotNo);
		System.out.println(portNo);
		// String sdf = getCtpParameterFromCtpName("sts24c_vc4_8c=234");
		// System.out.println(sdf);
	}

}
