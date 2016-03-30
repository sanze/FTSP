package com.fujitsu.util;

import globaldefs.NameAndStringValue_T;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.model.ItemSelectInfo;
import com.fujitsu.model.PmColumn;

/**
 * @author xuxj
 * @version 2013/01/30,
 */

public class CSVUtil {

	private static final String SPECIAL_CHAR_A = "[^\",\\n 　]";
	private static final String SPECIAL_CHAR_B = "[^\",\\n]";

	// 层速率 数字对应map
	public static HashMap<String, String> LAYRATE_MAPING;
	
	private static int HW_HEAD_EMS_NAME = 1;
	private static int HW_HEAD_ME_NAME = 3;
	private static int HW_HEAD_PTP_NAME = 5;
	private static int HW_HEAD_CTP_NAME = 7;
	private static int HW_HEAD_LAYRE_RATE = 9;
	private static int HW_HEAD_GRANULARITY = 10;
	private static int HW_PM_RETRIEVAL_TIME = 11;
	private static int HW_PM_PARAMETER = 13;
	private static int HW_PM_LOCATION = 14;
	private static int HW_PM_VALUE = 15;
	private static int HW_PM_UNIT = 16;
	private static int HW_PM_STATUS = 17;

	// pm性能数据头信息
	// private static String UserLabel;
	// private static String EMSName;
	// private static String EMSNativeName;
	// private static String MEName;
	// private static String MENativeName;
	// private static String PTPName;
	// private static String PTPNativeName;
	// private static String CTPName;
	// private static String CTPNativeName;
	// private static String LayerRate;
	// private static String Granularity;

	public static void main(String[] args) throws CommonException {
		
		CSVUtil sdf = new CSVUtil();

		String tempPath = "D:/FTP文件目录/192.168.20.1_AX-XJXLD404.csv";

		File file = new File(tempPath);

		List<LUCENT.performance.PMData_T> list = sdf.readCsvFileForLUCENT(file, "GBK");

		System.out.println("解析出的性能条目：" + list.size());
		for(LUCENT.performance.PMData_T data:list){
			for(NameAndStringValue_T name:data.tpName){
				System.out.print(name.name+" ");
				System.out.print(name.value+" ");
				System.out.println(" ");
			}
			System.out.println("层速率："+data.layerRate);
			System.out.println("周期："+data.granularity);
			System.out.println("性能时间："+data.retrievalTime);
			for(LUCENT.performance.PMMeasurement_T pm:data.pmMeasurementList){
				System.out.print(pm.pmParameterName+" ");
				System.out.print(pm.pmLocation+" ");
				System.out.print(pm.value+" ");
				System.out.print(pm.unit+" ");
				System.out.print(pm.intervalStatus+" ");
				System.out.println(" ");
			}
			System.out.println(" ");
		}
	}
	
	
	public synchronized static List<HW.performance.PMData_T> readCsvFileForHW(File file, String encode)
			throws CommonException {

		CSVUtil read = new CSVUtil();
		InputStream ins = null;
		BufferedReader bufferedReader = null;
		// 创建性能数据列表对象
		List<HW.performance.PMData_T> list = new ArrayList<HW.performance.PMData_T>();
		// 性能数据对象
		HW.performance.PMData_T pmdata = null;
		// 性能对象
		HW.performance.PMMeasurement_T pmm = null;
		// 性能参数
		List<HW.performance.PMMeasurement_T> listpm = null;
		
		//应对一个pmData中含多个retrievalTime数据情况
//		20140508000000.0Z
//		20140507000000.0Z
		Map<String, List<HW.performance.PMMeasurement_T>> multiDatePm = new HashMap<String, List<HW.performance.PMMeasurement_T>>();

		try {
			ins = new FileInputStream(file);

			// ins = ftpUtils.ftpClient.retrieveFileStream(fileName);
			bufferedReader = new BufferedReader(new InputStreamReader(ins, encode));
			// 行号
			int lineNumber = 0;
			// test
			String strLine = "";
			// 循环读取一行数据
			while ((strLine = bufferedReader.readLine()) != null) {
				
				// 去除标题行
				lineNumber++;
				if (lineNumber == 1) {
					continue;
				}
				//是否头信息
				boolean isHeadLine = false;

				// 判断是否一个性能数据的起始行
				if (strLine.contains("24h")||strLine.contains("15min")) {
					//将上一个组装完成的性能加入list
					if(pmdata!=null){
						for(String retrievalTime:multiDatePm.keySet()){
							HW.performance.PMData_T pmDateTemp = copyPmData(pmdata);
							pmDateTemp.retrievalTime = retrievalTime;
							pmDateTemp.pmMeasurementList = multiDatePm.get(retrievalTime)
									.toArray(new HW.performance.PMMeasurement_T[multiDatePm.get(retrievalTime)
											.size()]);
							list.add(pmDateTemp);
						}
						multiDatePm.clear();
					}
					//------------------------------------
					//初始化性能
					isHeadLine = true;
					pmdata = new HW.performance.PMData_T();
				} else {
					
				}
				//分解数据
				String[] lineData =  strLine.split(",");
				//填充头信息
				if(isHeadLine&&lineData.length>0){
					//层速率
					String layerRate = read.getLayRate(lineData[HW_HEAD_LAYRE_RATE],
							"HWDefaultLayRate.xml");

					if (layerRate == null || layerRate.isEmpty()) {
						pmdata.layerRate = (short) -1;
					} else {
						pmdata.layerRate = Short.valueOf(layerRate);
					}
					//周期
					pmdata.granularity = lineData[HW_HEAD_GRANULARITY];

					NameAndStringValue_T tt1 = new NameAndStringValue_T(
							DataCollectDefine.COMMON.EMS,
							lineData[HW_HEAD_EMS_NAME]);
					NameAndStringValue_T tt2 = new NameAndStringValue_T(
							DataCollectDefine.COMMON.MANAGED_ELEMENT,
							lineData[HW_HEAD_ME_NAME]);
					//设置ptp名或equipHolder名
					NameAndStringValue_T tt3;
					if(lineData[HW_HEAD_PTP_NAME].contains(DataCollectDefine.COMMON.PORT)){
						tt3 = new NameAndStringValue_T(
								DataCollectDefine.COMMON.PTP,
								lineData[HW_HEAD_PTP_NAME]);
					}else{
						tt3 = new NameAndStringValue_T(
								DataCollectDefine.COMMON.EQUIPMENT_HOLDER,
								lineData[HW_HEAD_PTP_NAME]);
					}
					//设置ctp名或equip名
					NameAndStringValue_T tt4;
					if(DataCollectDefine.COMMON.PTP.equals(tt3.name)){
						tt4 = new NameAndStringValue_T(
								DataCollectDefine.COMMON.CTP,
								lineData[HW_HEAD_CTP_NAME]);
					}else{
						tt4 = new NameAndStringValue_T(
								DataCollectDefine.COMMON.EQUIPMENT,
								lineData[HW_HEAD_CTP_NAME]);
					}
					if(tt4.value.isEmpty()){
						pmdata.tpName = new NameAndStringValue_T[]{tt1,tt2,tt3};
					}else{
						pmdata.tpName = new NameAndStringValue_T[]{tt1,tt2,tt3,tt4};
					}
				}
				if(!isHeadLine&&lineData.length>0){
					// 非性能数据起始行，填入头信息
					pmm = new HW.performance.PMMeasurement_T();
					pmm.pmParameterName = lineData[HW_PM_PARAMETER];
					pmm.pmLocation = lineData[HW_PM_LOCATION];
					pmm.value = Float.parseFloat(lineData[HW_PM_VALUE]);
					pmm.unit = lineData[HW_PM_UNIT];
					pmm.intervalStatus = lineData[HW_PM_STATUS];
					//设置性能采集时间
					pmdata.retrievalTime = lineData[HW_PM_RETRIEVAL_TIME];
					//添加性能行
					if(multiDatePm.containsKey(pmdata.retrievalTime)){
						multiDatePm.get(pmdata.retrievalTime).add(pmm);
					}else{
						listpm = new ArrayList<HW.performance.PMMeasurement_T>();
						listpm.add(pmm);
						multiDatePm.put(pmdata.retrievalTime, listpm);
					}
					
				}
			}
			//对应最后一条数据
			if(pmdata!=null){
				for(String retrievalTime:multiDatePm.keySet()){
					HW.performance.PMData_T pmDateTemp = copyPmData(pmdata);
					pmDateTemp.retrievalTime = retrievalTime;
					pmDateTemp.pmMeasurementList = multiDatePm.get(retrievalTime)
							.toArray(new HW.performance.PMMeasurement_T[multiDatePm.get(retrievalTime)
									.size()]);
					list.add(pmDateTemp);
				}
				multiDatePm.clear();
			}
			if (file.isFile() && file.exists()) {
				file.delete();
			}
		} catch (FileNotFoundException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_FILE_NOT_FOUND_EXCEPTION);
		} catch (IOException e) {
			throw new CommonException(e, MessageCodeDefine.CORBA_IO_EXCEPTION);
		} finally {
			try {
				if (ins != null) {
					ins.close();
				}
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_IO_EXCEPTION);
			}
		}
		try {
			System.out.println("HW解析出的性能条目："
					+ list.size()
					+ " 文件名："
					+ new String(file.getName().getBytes(
							DataCollectDefine.ENCODE_ISO),
							DataCollectDefine.ENCODE_GBK));
		} catch (UnsupportedEncodingException e) {
		}
		return list;
	}

	
	public synchronized static List<FENGHUO.performance.PMData_T> readCsvFileForFIM(File file, String encode)
			throws CommonException {

		CSVUtil read = new CSVUtil();
		InputStream ins = null;
		BufferedReader bufferedReader = null;
		// 创建性能数据列表对象
		List<FENGHUO.performance.PMData_T> list = new ArrayList<FENGHUO.performance.PMData_T>();
		// 性能数据对象
		FENGHUO.performance.PMData_T pmdata = null;
		// 性能对象
		FENGHUO.performance.PMMeasurement_T pmm = null;
		// 性能参数
		List<FENGHUO.performance.PMMeasurement_T> listpm = null;
		
		//应对一个pmData中含多个retrievalTime数据情况
//		20140508000000.0Z
//		20140507000000.0Z
		Map<String,List<FENGHUO.performance.PMMeasurement_T>> multiDatePm = new HashMap<String,List<FENGHUO.performance.PMMeasurement_T>>();

		try {
			ins = new FileInputStream(file);

			// ins = ftpUtils.ftpClient.retrieveFileStream(fileName);
			bufferedReader = new BufferedReader(new InputStreamReader(ins, encode));
			// 行号
			int lineNumber = 0;
			// test
			String strLine = "";
			// 循环读取一行数据
			while ((strLine = bufferedReader.readLine()) != null) {
				
				// 去除标题行
				lineNumber++;
				if (lineNumber == 1) {
					continue;
				}
				//是否头信息
				boolean isHeadLine = false;

				// 判断是否一个性能数据的起始行
				if (strLine.contains("24h")||strLine.contains("15min")) {
					//将上一个组装完成的性能加入list
					if(pmdata!=null){
						for(String retrievalTime:multiDatePm.keySet()){
							FENGHUO.performance.PMData_T pmDateTemp = copyPmData(pmdata);
							pmDateTemp.retrievalTime = retrievalTime;
							pmDateTemp.pmMeasurementList = multiDatePm.get(retrievalTime)
									.toArray(new FENGHUO.performance.PMMeasurement_T[multiDatePm.get(retrievalTime)
											.size()]);
							list.add(pmDateTemp);
						}
						multiDatePm.clear();
					}
					//------------------------------------
					//初始化性能
					isHeadLine = true;
					pmdata = new FENGHUO.performance.PMData_T();
				} else {
					
				}
				//分解数据
				String[] lineData =  strLine.split(",");
				//填充头信息
				if(isHeadLine&&lineData.length>0){
					
					String layerRate = read.getLayRate(lineData[HW_HEAD_LAYRE_RATE],
							"HWDefaultLayRate.xml");

					if (layerRate == null || layerRate.isEmpty()) {
						pmdata.layerRate = (short) -1;
					} else {
						pmdata.layerRate = Short.valueOf(layerRate);
					}
					pmdata.granularity = lineData[HW_HEAD_GRANULARITY];

					NameAndStringValue_T tt1 = new NameAndStringValue_T(
							DataCollectDefine.COMMON.EMS,
							lineData[HW_HEAD_EMS_NAME]);
					NameAndStringValue_T tt2 = new NameAndStringValue_T(
							DataCollectDefine.COMMON.MANAGED_ELEMENT,
							lineData[HW_HEAD_ME_NAME]);
					//设置ptp名或equipHolder名
					NameAndStringValue_T tt3;
					if(lineData[HW_HEAD_PTP_NAME].contains(DataCollectDefine.COMMON.PORT)){
						tt3 = new NameAndStringValue_T(
								DataCollectDefine.COMMON.PTP,
								lineData[HW_HEAD_PTP_NAME]);
					}else{
						tt3 = new NameAndStringValue_T(
								DataCollectDefine.COMMON.EQUIPMENT_HOLDER,
								lineData[HW_HEAD_PTP_NAME]);
					}
					//设置ctp名或equip名
					NameAndStringValue_T tt4;
					if(DataCollectDefine.COMMON.PTP.equals(tt3.name)){
						tt4 = new NameAndStringValue_T(
								DataCollectDefine.COMMON.CTP,
								lineData[HW_HEAD_CTP_NAME]);
					}else{
						tt4 = new NameAndStringValue_T(
								DataCollectDefine.COMMON.EQUIPMENT,
								lineData[HW_HEAD_CTP_NAME]);
					}
					if(tt4.value.isEmpty()){
						pmdata.tpName = new NameAndStringValue_T[]{tt1,tt2,tt3};
					}else{
						pmdata.tpName = new NameAndStringValue_T[]{tt1,tt2,tt3,tt4};
					}
				}
				if(!isHeadLine&&lineData.length>0){
					// 非性能数据起始行，填入头信息
					pmm = new FENGHUO.performance.PMMeasurement_T();
					pmm.pmParameterName = lineData[HW_PM_PARAMETER];
					pmm.pmLocation = lineData[HW_PM_LOCATION];
					pmm.value = Float.parseFloat(lineData[HW_PM_VALUE]);
					pmm.unit = lineData[HW_PM_UNIT];
					pmm.intervalStatus = lineData[HW_PM_STATUS];
					//设置性能采集时间
					pmdata.retrievalTime = lineData[HW_PM_RETRIEVAL_TIME];
					//添加性能行
					if(multiDatePm.containsKey(pmdata.retrievalTime)){
						multiDatePm.get(pmdata.retrievalTime).add(pmm);
					}else{
						listpm = new ArrayList<FENGHUO.performance.PMMeasurement_T>();
						listpm.add(pmm);
						multiDatePm.put(pmdata.retrievalTime, listpm);
					}
					
				}
			}
			//对应最后一条数据
			if(pmdata!=null){
				for(String retrievalTime:multiDatePm.keySet()){
					FENGHUO.performance.PMData_T pmDateTemp = copyPmData(pmdata);
					pmDateTemp.retrievalTime = retrievalTime;
					pmDateTemp.pmMeasurementList = multiDatePm.get(retrievalTime)
							.toArray(new FENGHUO.performance.PMMeasurement_T[multiDatePm.get(retrievalTime)
									.size()]);
					list.add(pmDateTemp);
				}
				multiDatePm.clear();
			}
			if (file.isFile() && file.exists()) {
				file.delete();
			}
		} catch (FileNotFoundException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_FILE_NOT_FOUND_EXCEPTION);
		} catch (IOException e) {
			throw new CommonException(e, MessageCodeDefine.CORBA_IO_EXCEPTION);
		} finally {
			try {
				if (ins != null) {
					ins.close();
				}
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_IO_EXCEPTION);
			}
		}
		try {
			System.out.println("FIM解析出的性能条目："
					+ list.size()
					+ " 文件名："
					+ new String(file.getName().getBytes(
							DataCollectDefine.ENCODE_ISO),
							DataCollectDefine.ENCODE_GBK));
		} catch (UnsupportedEncodingException e) {
		}
		return list;
	}
	
	public synchronized static List<ZTE_U31.performance.PMData_T> readCsvFileForZTE(File file, String encode)
			throws CommonException {

		CSVUtil read = new CSVUtil();
		InputStream ins = null;
		BufferedReader bufferedReader = null;
		// 创建性能数据列表对象
		List<ZTE_U31.performance.PMData_T> list = new ArrayList<ZTE_U31.performance.PMData_T>();
		// 性能数据对象
		ZTE_U31.performance.PMData_T pmdata = null;
		// 当性能源改变时,复制pmdata
		ZTE_U31.performance.PMData_T pmdatatmp = null;
		// 性能参数
		List<ZTE_U31.performance.PMMeasurement_T> pmmlist = null;

		try {
			ins = new FileInputStream(file);

			// ins = ftpUtils.ftpClient.retrieveFileStream(fileName);
			bufferedReader = new BufferedReader(new InputStreamReader(ins, encode));
			String regExp = read.getRegExp();

			// 行号
			String strLine = bufferedReader.readLine();
			PmColumn pmColumn=PmColumn.getPmColumn(strLine);// 解析标题行
			if(pmColumn.inValid())
				return list;
			String str = "";
			// 循环读取一行数据
			strLine = bufferedReader.readLine();
			while (strLine != null) {
				List<String> columns=new ArrayList<String>();
				
				Pattern pattern = Pattern.compile(regExp);
				Matcher matcher = pattern.matcher(strLine);

				// 分解一行数据
				while (matcher.find()) {
					// 分解行信息为单元信息
					str = matcher.group();
					str = str.trim();
					if (str.endsWith(",")) {
						str = str.substring(0, str.length() - 1);
						str = str.trim();
					}
					if (str.startsWith("\"") && str.endsWith("\"")) {
						str = str.substring(1, str.length() - 1);
						if (read.isExisted("\"\"", str)) {
							str = str.replaceAll("\"\"", "\"");
						}
					}
					if (str.startsWith("'") && str.endsWith("'")) {
						str = str.substring(1, str.length() - 1);
					}
					columns.add(str);
				}
				strLine = bufferedReader.readLine();//读下一行
				// 性能对象
				ZTE_U31.performance.PMMeasurement_T pmm = null;
				// 性能名称对象
				List<NameAndStringValue_T> namelist = null;

				// 头行数据或(tpName、layerRate、granularity、retrievalTime)其中之一与上一条数据不同
				if (pmdata==null) {
					pmdata = new ZTE_U31.performance.PMData_T();
					pmmlist = new ArrayList<ZTE_U31.performance.PMMeasurement_T>();
				}

				boolean newData = false;
				if(!pmColumn.basicInValid(columns)||!pmColumn.measurementInValid(columns)){
					if(!pmColumn.basicInValid(columns)){
						namelist=new ArrayList<NameAndStringValue_T>();
						if(pmColumn.EMS_Name!=null){
							namelist.add(new NameAndStringValue_T(
									DataCollectDefine.COMMON.EMS,columns.get(pmColumn.EMS_Name)));
						}else{
							namelist.add(new NameAndStringValue_T(
									DataCollectDefine.COMMON.EMS,""));
						}
						if(pmColumn.ME_Name!=null){
							namelist.add(new NameAndStringValue_T(
									DataCollectDefine.COMMON.MANAGED_ELEMENT,columns.get(pmColumn.ME_Name)));
						}
						short layerRate=0;
						if(pmColumn.PTP_Name!=null){
							String ptpName=columns.get(pmColumn.PTP_Name);
							if(ptpName!=null&&!ptpName.isEmpty()){
								namelist.addAll(PmColumn.constructPtpName(ptpName));
								//E300 无LayerRate列，从ptp中取
								String rate=PmColumn.getProperty(ptpName,
										DataCollectDefine.ZTE.ZTE_PTP_LAYERRATE);
								if(rate!=null&&rate.matches("[0-9]+"))
									layerRate=Short.valueOf(rate);
							}
						}
						if(pmColumn.CTP_Name!=null){
							String ctpName=columns.get(pmColumn.CTP_Name);
							if(ctpName!=null&&!ctpName.isEmpty())
								namelist.addAll(PmColumn.constructCtpName(ctpName));
						}
						NameAndStringValue_T[] tpName=namelist.toArray(new NameAndStringValue_T[namelist.size()]);
						
						String granularity=null;
						String retrievalTime=null;
						if(pmColumn.LayerRate!=null){
							String rate=columns.get(pmColumn.LayerRate);
							if(rate!=null&&rate.matches("[0-9]+"))
								layerRate=Short.valueOf(rate);
						}
						if(pmColumn.Granularity!=null){
							granularity=columns.get(pmColumn.Granularity);
							if(!PmColumn.isEmpty(granularity)){
								if(DataCollectDefine.COMMON.GRANULARITY_24HOUR_STRING.contains(granularity)){
									granularity=DataCollectDefine.COMMON.GRANULARITY_24HOUR_STRING;
								}else{
									granularity=DataCollectDefine.COMMON.GRANULARITY_15MIN_STRING;
								}
							}
						}
						if(pmColumn.RetrievalTime!=null){
							retrievalTime=columns.get(pmColumn.RetrievalTime);
						}
						newData=(!PmColumn.equals(tpName,pmdata.tpName)||
							layerRate!=pmdata.layerRate||
							!PmColumn.equals(granularity,pmdata.granularity)||
							!PmColumn.equals(retrievalTime,pmdata.retrievalTime))&&!pmmlist.isEmpty();
						if(newData){//性能源改变
							pmdata.pmMeasurementList=pmmlist.toArray(
									new ZTE_U31.performance.PMMeasurement_T[pmmlist.size()]);
							pmdatatmp=pmdata;
							pmdata=new ZTE_U31.performance.PMData_T();
							pmmlist = new ArrayList<ZTE_U31.performance.PMMeasurement_T>();
						}
						pmdata.tpName=tpName;
						pmdata.layerRate=layerRate;
						pmdata.granularity=granularity;
						pmdata.retrievalTime=retrievalTime;
					}
					if(!pmColumn.measurementInValid(columns)){
						pmm = new ZTE_U31.performance.PMMeasurement_T();
						pmm.pmParameter=columns.get(pmColumn.Parameter);
						pmm.value=Float.NaN;
						String value=columns.get(pmColumn.Value);
						if(value.matches("^[+-]?\\d+(\\.\\d+)?([Ee][+-]?\\d+(\\.\\d+)?)?")){
							pmm.value=Float.valueOf(value);
						}
						if(pmColumn.Unit!=null){
							pmm.unit=columns.get(pmColumn.Unit);
						}
						if(pmColumn.Location!=null){
							pmm.pmLocation=columns.get(pmColumn.Location);
						}
						if(pmColumn.Status!=null){
							pmm.intervalStatus=columns.get(pmColumn.Status);
						}
						pmmlist.add(pmm);
					}
				}
				if(newData){//性能源改变
					if(pmdatatmp!=null&&pmdatatmp.pmMeasurementList!=null)
						list.add(pmdatatmp);
					pmdatatmp=null;
				}
				if(strLine==null){//最后一行了
					pmdata.pmMeasurementList=pmmlist.toArray(
							new ZTE_U31.performance.PMMeasurement_T[pmmlist.size()]);
					pmdatatmp=pmdata;
					newData=true;
					if(pmdatatmp!=null&&pmdatatmp.pmMeasurementList!=null)
						list.add(pmdatatmp);
					pmdatatmp=null;
				}
			}
			if (file.isFile() && file.exists()) {
				file.delete();
			}
		} catch (FileNotFoundException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_FILE_NOT_FOUND_EXCEPTION);
		} catch (IOException e) {
			throw new CommonException(e, MessageCodeDefine.CORBA_IO_EXCEPTION);
		} finally {
			try {
				if (ins != null) {
					ins.close();
				}
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_IO_EXCEPTION);
			}
		}
		try {
			System.out.println("ZTE解析出的性能条目："
					+ list.size()
					+ " 文件名："
					+ new String(file.getName().getBytes(
							DataCollectDefine.ENCODE_ISO),
							DataCollectDefine.ENCODE_GBK));
		} catch (UnsupportedEncodingException e) {
		}
		return list;
	}

	/**
	 * @param file
	 * @return
	 * @throws CommonException
	 */
	public synchronized static List<LUCENT.performance.PMData_T> readCsvFileForLUCENT(
			File file, String encode) throws CommonException {
		
		//朗讯文档结构与华为一致，可以共用表头,但是每个字段会多“”，需要做去引号处理
		CSVUtil read = new CSVUtil();
		InputStream ins = null;
		BufferedReader bufferedReader = null;
		// 创建性能数据列表对象
		List<LUCENT.performance.PMData_T> list = new ArrayList<LUCENT.performance.PMData_T>();
		// 性能数据对象
		LUCENT.performance.PMData_T pmdata = null;
		// 性能对象
		LUCENT.performance.PMMeasurement_T pmm = null;
		// 性能参数
		List<LUCENT.performance.PMMeasurement_T> listpm = null;
		
		//应对一个pmData中含多个retrievalTime数据情况
//		20140508000000.0Z
//		20140507000000.0Z
		Map<String, List<LUCENT.performance.PMMeasurement_T>> multiDatePm = new HashMap<String, List<LUCENT.performance.PMMeasurement_T>>();

		try {
			ins = new FileInputStream(file);

			// ins = ftpUtils.ftpClient.retrieveFileStream(fileName);
			bufferedReader = new BufferedReader(new InputStreamReader(ins, encode));
			// 行号
			int lineNumber = 0;
			// test
			String strLine = "";
			// 循环读取一行数据
			while ((strLine = bufferedReader.readLine()) != null) {
				
				// 去除标题行
				lineNumber++;
				if (lineNumber == 1) {
					continue;
				}
				//是否头信息
				boolean isHeadLine = false;

				// 判断是否一个性能数据的起始行
				if (strLine.contains("24h")||strLine.contains("15min")) {
					//将上一个组装完成的性能加入list
					if(pmdata!=null){
						for(String retrievalTime:multiDatePm.keySet()){
							LUCENT.performance.PMData_T pmDateTemp = copyPmData(pmdata);
							pmDateTemp.retrievalTime = retrievalTime;
							pmDateTemp.pmMeasurementList = multiDatePm.get(retrievalTime)
									.toArray(new LUCENT.performance.PMMeasurement_T[multiDatePm.get(retrievalTime)
											.size()]);
							list.add(pmDateTemp);
						}
						multiDatePm.clear();
					}
					//------------------------------------
					//初始化性能
					isHeadLine = true;
					pmdata = new LUCENT.performance.PMData_T();
				} else {
					
				}
				//分解数据
				String[] lineData =  strLine.split(",");
				//格式化数据
				lineData = formatLineData(lineData);
				//填充头信息
				if(isHeadLine&&lineData.length>0){
					//层速率
					String layerRate = read.getLayRate(lineData[HW_HEAD_LAYRE_RATE],
							"HWDefaultLayRate.xml");

					if (layerRate == null || layerRate.isEmpty()) {
						pmdata.layerRate = (short) -1;
					} else {
						pmdata.layerRate = Short.valueOf(layerRate);
					}
					//周期
					pmdata.granularity = lineData[HW_HEAD_GRANULARITY];

					NameAndStringValue_T tt1 = new NameAndStringValue_T(
							DataCollectDefine.COMMON.EMS,
							lineData[HW_HEAD_EMS_NAME]);
					NameAndStringValue_T tt2 = new NameAndStringValue_T(
							DataCollectDefine.COMMON.MANAGED_ELEMENT,
							lineData[HW_HEAD_ME_NAME]);
					//设置ptp名或equipHolder名
					NameAndStringValue_T tt3;
					//朗讯均为ptp性能
					tt3 = new NameAndStringValue_T(
							DataCollectDefine.COMMON.PTP,
							lineData[HW_HEAD_PTP_NAME]);
//					if(lineData[HW_HEAD_PTP_NAME].contains(DataCollectDefine.COMMON.PORT)){
//						tt3 = new NameAndStringValue_T(
//								DataCollectDefine.COMMON.PTP,
//								lineData[HW_HEAD_PTP_NAME]);
//					}else{
//						tt3 = new NameAndStringValue_T(
//								DataCollectDefine.COMMON.EQUIPMENT_HOLDER,
//								lineData[HW_HEAD_PTP_NAME]);
//					}
					//设置ctp名或equip名
					NameAndStringValue_T tt4;
					if(DataCollectDefine.COMMON.PTP.equals(tt3.name)){
						tt4 = new NameAndStringValue_T(
								DataCollectDefine.COMMON.CTP,
								lineData[HW_HEAD_CTP_NAME]);
					}else{
						tt4 = new NameAndStringValue_T(
								DataCollectDefine.COMMON.EQUIPMENT,
								lineData[HW_HEAD_CTP_NAME]);
					}
					if(tt4.value.isEmpty()){
						pmdata.tpName = new NameAndStringValue_T[]{tt1,tt2,tt3};
					}else{
						pmdata.tpName = new NameAndStringValue_T[]{tt1,tt2,tt3,tt4};
					}
				}
				if(!isHeadLine&&lineData.length>0){
					// 非性能数据起始行，填入头信息
					pmm = new LUCENT.performance.PMMeasurement_T();
					pmm.pmParameterName = lineData[HW_PM_PARAMETER];
					pmm.pmLocation = lineData[HW_PM_LOCATION];
					pmm.value = Float.parseFloat(lineData[HW_PM_VALUE]);
					pmm.unit = lineData[HW_PM_UNIT];
					pmm.intervalStatus = lineData[HW_PM_STATUS];
					//设置性能采集时间
					pmdata.retrievalTime = lineData[HW_PM_RETRIEVAL_TIME];
					//添加性能行
					if(multiDatePm.containsKey(pmdata.retrievalTime)){
						multiDatePm.get(pmdata.retrievalTime).add(pmm);
					}else{
						listpm = new ArrayList<LUCENT.performance.PMMeasurement_T>();
						listpm.add(pmm);
						multiDatePm.put(pmdata.retrievalTime, listpm);
					}
					
				}
			}
			//对应最后一条数据
			if(pmdata!=null){
				for(String retrievalTime:multiDatePm.keySet()){
					LUCENT.performance.PMData_T pmDateTemp = copyPmData(pmdata);
					pmDateTemp.retrievalTime = retrievalTime;
					pmDateTemp.pmMeasurementList = multiDatePm.get(retrievalTime)
							.toArray(new LUCENT.performance.PMMeasurement_T[multiDatePm.get(retrievalTime)
									.size()]);
					list.add(pmDateTemp);
				}
				multiDatePm.clear();
			}
			if (file.isFile() && file.exists()) {
				file.delete();
			}
		} catch (FileNotFoundException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_FILE_NOT_FOUND_EXCEPTION);
		} catch (IOException e) {
			throw new CommonException(e, MessageCodeDefine.CORBA_IO_EXCEPTION);
		} finally {
			try {
				if (ins != null) {
					ins.close();
				}
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_IO_EXCEPTION);
			}
		}
		try {
			System.out.println("LUCENT解析出的性能条目："
					+ list.size()
					+ " 文件名："
					+ new String(file.getName().getBytes(
							DataCollectDefine.ENCODE_ISO),
							DataCollectDefine.ENCODE_GBK));
		} catch (UnsupportedEncodingException e) {
		}
		return list;
	}
	
	public synchronized static List<ALU.performance.PMData_T> readCsvFileForALU(File file, String encode)
			throws CommonException {

		CSVUtil read = new CSVUtil();
		InputStream ins = null;
		BufferedReader bufferedReader = null;
		// 创建性能数据列表对象
		List<ALU.performance.PMData_T> list = new ArrayList<ALU.performance.PMData_T>();
		// 性能数据对象
		ALU.performance.PMData_T pmdata = null;
		// 当性能源改变时,复制pmdata
		ALU.performance.PMData_T pmdatatmp = null;
		// 性能参数
		List<ALU.performance.PMMeasurement_T> pmmlist = null;
		// 获取网元名称（corbaIp + "_" + targetDisplayName + ".zip"）
		String fileName = file.getName();
		String neName = fileName.substring(fileName.indexOf('_')+1, fileName.length());
		neName = neName.replace(".zip", "");
		
		try {
			ins = new FileInputStream(file);

			// ins = ftpUtils.ftpClient.retrieveFileStream(fileName);
			bufferedReader = new BufferedReader(new InputStreamReader(ins, encode));
			String regExp = read.getRegExp();

			// 行号
			String strLine = bufferedReader.readLine();
			PmColumn pmColumn=PmColumn.getPmColumn(strLine);// 解析标题行
			if(pmColumn.inValid())
				return list;
			String str = "";
			// 循环读取一行数据
			strLine = bufferedReader.readLine();
			while (strLine != null) {
				List<String> columns=new ArrayList<String>();
				
				Pattern pattern = Pattern.compile(regExp);
				Matcher matcher = pattern.matcher(strLine);

				// 分解一行数据
				while (matcher.find()) {
					// 分解行信息为单元信息
					str = matcher.group();
					str = str.trim();
					if (str.endsWith(",")) {
						str = str.substring(0, str.length() - 1);
						str = str.trim();
					}
					if (str.startsWith("\"") && str.endsWith("\"")) {
						str = str.substring(1, str.length() - 1);
						if (read.isExisted("\"\"", str)) {
							str = str.replaceAll("\"\"", "\"");
						}
					}
					if (str.startsWith("'") && str.endsWith("'")) {
						str = str.substring(1, str.length() - 1);
					}
					columns.add(str);
				}
				// 排除非本网元的性能信息
				if (!columns.get(pmColumn.ME_Name).equals(neName)) {
					strLine = bufferedReader.readLine();//读下一行
					continue;
				}
				
				strLine = bufferedReader.readLine();//读下一行
				// 性能对象
				ALU.performance.PMMeasurement_T pmm = null;
				// 性能名称对象
				List<NameAndStringValue_T> namelist = null;

				// 头行数据或(tpName、layerRate、granularity、retrievalTime)其中之一与上一条数据不同
				if (pmdata==null) {
					pmdata = new ALU.performance.PMData_T();
					pmmlist = new ArrayList<ALU.performance.PMMeasurement_T>();
				}

				boolean newData = false;
				if(!pmColumn.basicInValid(columns)||!pmColumn.measurementInValid(columns)){
					if(!pmColumn.basicInValid(columns)){
						namelist=new ArrayList<NameAndStringValue_T>();
						if(pmColumn.EMS_Name!=null){
							namelist.add(new NameAndStringValue_T(
									DataCollectDefine.COMMON.EMS,columns.get(pmColumn.EMS_Name)));
						}else{
							namelist.add(new NameAndStringValue_T(
									DataCollectDefine.COMMON.EMS,""));
						}
						if(pmColumn.ME_Name!=null){
							namelist.add(new NameAndStringValue_T(
									DataCollectDefine.COMMON.MANAGED_ELEMENT,columns.get(pmColumn.ME_Name)));
						}
						short layerRate=0; // 初始值
						if(pmColumn.PTP_Name!=null){
							String ptpName=columns.get(pmColumn.PTP_Name);
							if(ptpName!=null&&!ptpName.isEmpty()){
								List<NameAndStringValue_T> temp = PmColumn.constructPtpName(ptpName);
								try {
									String[] name = temp.get(0).value.split("/");
									if (name.length == 3) {
										name[1] = name[1].replaceFirst("sr", "s");
										name[1] = name[1].replaceFirst("sl", "b");
										String port = name[2].split("-")[0];
										String layer = name[2].split("-")[1];
										name[2] = port.replace("port#", "p");
										StringBuilder sb = new StringBuilder();
										sb.append(name[0]).append("/").append(name[1]).append(name[2]);
										temp.get(0).value = sb.toString();
										// 贝尔历史性能文件中无层速率，暂时解析TP尾部的字符串来简单区分
										if (layer.contains("RsTTP")) {
											layerRate = 20;
										} else if (layer.contains("MsTTP")) {
											layerRate = 25;
										}
									}	
								} catch (Exception e) {
									System.out.println("贝尔历史性能文件的PtpName转换错误："+temp.get(0).value);
								}
								
								namelist.addAll(temp);
								
							}
						}
						/*if(pmColumn.CTP_Name!=null){
							String ctpName=columns.get(pmColumn.CTP_Name);
							//FIXME 
							if(ctpName!=null&&!ctpName.isEmpty())
								namelist.addAll(PmColumn.constructCtpName(ctpName));
						}*/
						NameAndStringValue_T[] tpName=namelist.toArray(new NameAndStringValue_T[namelist.size()]);
						
						String granularity=null;
						String retrievalTime=null;

						if(pmColumn.Granularity!=null){
							granularity=columns.get(pmColumn.Granularity);
							if(!PmColumn.isEmpty(granularity)){
								if(DataCollectDefine.ALU.GRANULARITY_24HOUR_STRING.contains(granularity)){
									granularity=DataCollectDefine.COMMON.GRANULARITY_24HOUR_STRING;
								}else{
									granularity=DataCollectDefine.COMMON.GRANULARITY_15MIN_STRING;
								}
							}
						}
						if(pmColumn.RetrievalTime!=null){
							retrievalTime=columns.get(pmColumn.RetrievalTime);
						}
						newData=(!PmColumn.equals(tpName,pmdata.tpName)||
							layerRate!=pmdata.layerRate||
							!PmColumn.equals(granularity,pmdata.granularity)||
							!PmColumn.equals(retrievalTime,pmdata.retrievalTime))&&!pmmlist.isEmpty();
						if(newData){//性能源改变
							pmdata.pmMeasurementList=pmmlist.toArray(
									new ALU.performance.PMMeasurement_T[pmmlist.size()]);
							pmdatatmp=pmdata;
							pmdata=new ALU.performance.PMData_T();
							pmmlist = new ArrayList<ALU.performance.PMMeasurement_T>();
						}
						pmdata.tpName=tpName;
						pmdata.layerRate=layerRate;
						pmdata.granularity=granularity;
						pmdata.retrievalTime=retrievalTime;
					}
					if(!pmColumn.measurementInValid(columns)){
						if(pmColumn.ParameterValue!=null&&!pmColumn.ParameterValue.isEmpty()){
							for(String parameter:pmColumn.ParameterValue.keySet()){
								pmm = new ALU.performance.PMMeasurement_T();
								pmm.pmParameterName=parameter;
								//首字母转换为小写,当前性能中为小写
								StringBuilder lower=new StringBuilder(pmm.pmParameterName);
								lower.setCharAt(0, Character.toLowerCase(pmm.pmParameterName.charAt(0)));
								pmm.pmParameterName=lower.toString();
								pmm.value=Float.NaN;
								String value=columns.get(pmColumn.ParameterValue.get(parameter));
								if(value.matches("^[+-]?\\d+(\\.\\d+)?([Ee][+-]?\\d+(\\.\\d+)?)?")){
									pmm.value=Float.valueOf(value);
								}
								if(pmColumn.Unit!=null){
									pmm.unit=columns.get(pmColumn.Unit);
								} else {
									pmm.unit = "";
								}
								if(pmColumn.Location!=null){
									pmm.pmLocation=columns.get(pmColumn.Location);
								}
								if(pmColumn.Status!=null){
									pmm.intervalStatus=columns.get(pmColumn.Status);
								}
								pmmlist.add(pmm);
							}
						}else{
							pmm = new ALU.performance.PMMeasurement_T();
							pmm.pmParameterName=columns.get(pmColumn.Parameter);
							//首字母转换为小写,当前性能中为小写
							StringBuilder lower=new StringBuilder(pmm.pmParameterName);
							lower.setCharAt(0, Character.toLowerCase(pmm.pmParameterName.charAt(0)));
							pmm.pmParameterName=lower.toString();
							pmm.value=Float.NaN;
							String value=columns.get(pmColumn.Value);
							if(value.matches("^[+-]?\\d+(\\.\\d+)?([Ee][+-]?\\d+(\\.\\d+)?)?")){
								pmm.value=Float.valueOf(value);
							}
							if(pmColumn.Unit!=null){
								pmm.unit=columns.get(pmColumn.Unit);
							} else {
								pmm.unit = "";
							}
							if(pmColumn.Location!=null){
								pmm.pmLocation=columns.get(pmColumn.Location);
							}
							if(pmColumn.Status!=null){
								pmm.intervalStatus=columns.get(pmColumn.Status);
							}
							pmmlist.add(pmm);
						}
					}
				}
				if(newData){//性能源改变
					if(pmdatatmp!=null&&pmdatatmp.pmMeasurementList!=null)
						list.add(pmdatatmp);
					pmdatatmp=null;
				}
				if(strLine==null){//最后一行了
					pmdata.pmMeasurementList=pmmlist.toArray(
							new ALU.performance.PMMeasurement_T[pmmlist.size()]);
					pmdatatmp=pmdata;
					newData=true;
					if(pmdatatmp!=null&&pmdatatmp.pmMeasurementList!=null)
						list.add(pmdatatmp);
					pmdatatmp=null;
				}
			}
			if (file.isFile() && file.exists()) {
				file.delete();
			}
		} catch (FileNotFoundException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_FILE_NOT_FOUND_EXCEPTION);
		} catch (IOException e) {
			throw new CommonException(e, MessageCodeDefine.CORBA_IO_EXCEPTION);
		} finally {
			try {
				if (ins != null) {
					ins.close();
				}
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_IO_EXCEPTION);
			}
		}
		try {
			System.out.println("ALU解析出的性能条目："
					+ list.size()
					+ " 文件名："
					+ new String(file.getName().getBytes(
							DataCollectDefine.ENCODE_ISO),
							DataCollectDefine.ENCODE_GBK));
		} catch (UnsupportedEncodingException e) {
		}
		return list;
	}
	
	//朗讯格式化数据，去除每个字段的引号
	private static String[] formatLineData(String[] source){
		List<String> dataList = new ArrayList<String>();
		
		for(String xxx:source){
			if(xxx.startsWith("\"")){
				xxx = xxx.substring(1);
			}
			if(xxx.endsWith("\"")){
				xxx = xxx.substring(0,xxx.length()-1);
			}
			dataList.add(xxx);
		}
		source = dataList.toArray(source);
		return source;
	}

	/**
	 * @param argChar
	 * @param argStr
	 * @return
	 */
	private boolean isExisted(String argChar, String argStr) {

		boolean blnReturnValue = false;
		if ((argStr.indexOf(argChar) >= 0)
				&& (argStr.indexOf(argChar) <= argStr.length())) {
			blnReturnValue = true;
		}
		return blnReturnValue;
	}

	/**
	 * 正则表达式。
	 * 
	 * @return 匹配csv文件里最小单位的正则表达式。
	 */
	private String getRegExp() {

		String strRegExp = "";

		strRegExp = "\"((" + SPECIAL_CHAR_A + "*[,\\n 　])*(" + SPECIAL_CHAR_A
				+ "*\"{2})*)*" + SPECIAL_CHAR_A + "*\"[ 　]*,[ 　]*" + "|"
				+ SPECIAL_CHAR_B + "*[ 　]*,[ 　]*" + "|\"((" + SPECIAL_CHAR_A
				+ "*[,\\n 　])*(" + SPECIAL_CHAR_A + "*\"{2})*)*"
				+ SPECIAL_CHAR_A + "*\"[ 　]*" + "|" + SPECIAL_CHAR_B + "*[ 　]*";

		return strRegExp;
	}

	// 取得层速率
	private String getLayRate(String key, String fileName) throws CommonException {
		String value = "-1";

		if (LAYRATE_MAPING == null || LAYRATE_MAPING.size() == 0) {
			LAYRATE_MAPING = new HashMap<String, String>();

			List<ItemSelectInfo> list;

			list = XmlUtil.parserXmlForLayRate(fileName);

			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					LAYRATE_MAPING.put(list.get(i).getKey(), list.get(i)
							.getValue1());
				}
			}
		}
		value = LAYRATE_MAPING.get(key);
		return value;
	}
	
	//复制性能数据
	private static HW.performance.PMData_T copyPmData(HW.performance.PMData_T source){
		HW.performance.PMData_T target = new HW.performance.PMData_T();
		target.layerRate = source.layerRate;
		target.granularity = source.granularity;
		target.retrievalTime = source.retrievalTime;
		target.tpName = source.tpName;
		target.pmMeasurementList = source.pmMeasurementList;
		return target;
	}
	
	//复制性能数据
	private static LUCENT.performance.PMData_T copyPmData(LUCENT.performance.PMData_T source){
		LUCENT.performance.PMData_T target = new LUCENT.performance.PMData_T();
		target.layerRate = source.layerRate;
		target.granularity = source.granularity;
		target.retrievalTime = source.retrievalTime;
		target.tpName = source.tpName;
		target.pmMeasurementList = source.pmMeasurementList;
		return target;
	}
	
	//复制性能数据
	private static FENGHUO.performance.PMData_T copyPmData(FENGHUO.performance.PMData_T source){
		FENGHUO.performance.PMData_T target = new FENGHUO.performance.PMData_T();
		target.layerRate = source.layerRate;
		target.granularity = source.granularity;
		target.retrievalTime = source.retrievalTime;
		target.tpName = source.tpName;
		target.pmMeasurementList = source.pmMeasurementList;
		return target;
	}

	// //检测是否支持层速率的数据
	// public boolean isSupportLayerRate(String layerRate){
	//
	// ResourceBundle bundle =
	// ResourceBundle.getBundle(Define.SYSTEM_CONFIG_FILE);
	// String hwSupportLayerRate = bundle.getString("hwSupportLayerRate");
	// String[] temp = hwSupportLayerRate.split(",");
	// for(String s:temp){
	// if(s.equals(layerRate)){
	// return true;
	// }
	//
	// }
	// return false;
	// }
}
