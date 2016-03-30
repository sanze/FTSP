package main;

import globaldefs.NameAndStringValue_T;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.Logger;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.IService.IEMSSession;
import com.fujitsu.service.EMSCollectService;
import com.fujitsu.serviceImpl.ALUCorba.ALUEMSSession;
import com.fujitsu.serviceImpl.FIMCorba.FIMEMSSession;
import com.fujitsu.serviceImpl.HWCorba.HWEMSSession;
import com.fujitsu.serviceImpl.LUCENTCorba.LUCENTEMSSession;
import com.fujitsu.serviceImpl.VEMS.ALUVEMSSession;
import com.fujitsu.serviceImpl.VEMS.FIMVEMSSession;
import com.fujitsu.serviceImpl.VEMS.HWVEMSSession;
import com.fujitsu.serviceImpl.VEMS.LUCENTVEMSSession;
import com.fujitsu.serviceImpl.VEMS.VEMSSession;
import com.fujitsu.serviceImpl.VEMS.ZTEVEMSSession;
import com.fujitsu.serviceImpl.ZTEU31Corba.ZTEU31EMSSession;
import com.fujitsu.util.CommonUtil;
import com.fujitsu.util.ExcelWriterUtil;
import com.fujitsu.util.NameAndStringValueUtil;

public class GetAndPrintData_Stub extends GetAndPrintData {

	public static Logger log = Logger.getLogger(GetAndPrintData_Stub.class);

	public static HashMap<String, Map> EmsMap = new HashMap<String, Map>();
	
	protected IEMSSession emsSession;
	private static com.fujitsu.util.ExcelWriterUtil excelWriter;
	private static com.fujitsu.util.ObjectPrinter txtWriter;
	public static HashMap<String, Class> CmdObjectHelper;
	
	public static Object[] neArray = null;
	public static Object[] subnetArray = null;
	public static Set<String> neList = new HashSet<String>();
	public static HashMap<String, Object> neMap = new HashMap<String, Object>();
//	public static List<NameAndStringValue_T[]> subnetNamesList = new ArrayList<NameAndStringValue_T[]>();
//	public static List<NameAndStringValue_T[]> ptpNamesList = new ArrayList<NameAndStringValue_T[]>();
//	public static List<NameAndStringValue_T[]> subnetNodeList = new ArrayList<NameAndStringValue_T[]>();
//	public static List<NameAndStringValue_T[]> meNodeList = new ArrayList<NameAndStringValue_T[]>();
	public static List<NameAndStringValue_T[]> subnetWorkList = new ArrayList<NameAndStringValue_T[]>();

	public static String[] Locations = new String[]{};
	public GetAndPrintData_Stub() {
		try {
			initData();
			if(p.containsKey("multiThreadCollect")
					&&!p.getProperty("multiThreadCollect").isEmpty()
					&&Boolean.parseBoolean(p.getProperty("multiThreadCollect"))){
				//多线程采集
				testMultiThreadCollect();
			}else{
				//正常采集
			getAndPrint();
			
			
			
			}
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}
	}

	//测试多线程采集性能数据
	public void testMultiThreadCollect(){
		String selectedNeIdString = (String) p.get(SELECTED_NE_ID);
		
		//采集多个网元历史性能--新增功能20140618
		String[] neIds = selectedNeIdString.split(",");
		List<NameAndStringValue_T[]> neList = new ArrayList<NameAndStringValue_T[]>();
		for(String neId:neIds){
			neList.add(getMeName(neMap.get(neId)));
		}
		//----------------线性采集性能数据------------------
		String GRANULARITY = DataCollectDefine.COMMON.GRANULARITY_24HOUR_STRING;
		log.info("线性采集"+neList.size()+"个网元，开始时间："+new Date());
		for(NameAndStringValue_T[] ne:neList){
			try {
				// 历史性能配置参数
				ftpIp = p.getProperty(FTPIP);
				if(ftpIp==null||ftpIp.isEmpty()||
					ftpIp.equals(CommonUtil.getLocalHostName())){
					ftpIp=CommonUtil.getLocalHost(corbaip).getHostAddress();
				}
				String portStr=p.getProperty(FTPPORT);
				if(portStr==null||portStr.isEmpty()){
					ftpPort=21;
				}else{
					ftpPort = Integer.valueOf(portStr);
				}
				userName = p.getProperty(FTPUSERNAME);
				password = p.getProperty(FTPPASSWORD);
				startTime = p.getProperty(STARTTIME);
				endTime = p.getProperty(ENDTIME);
				
				
				int factory=nmsType/10;
				String fileName = corbaip+"_"+ne[1].value;
				switch(factory){
				case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
					fileName = fileName+".txt";
					break;
				case DataCollectDefine.FACTORY_HW_FLAG:
					fileName = fileName+".csv";
					break;
				case DataCollectDefine.FACTORY_ZTE_FLAG:
					fileName = fileName+".dat";
					break;
				case DataCollectDefine.FACTORY_LUCENT_FLAG:
					fileName = fileName+".csv";
					break;
				default:
					fileName = fileName+".csv";
				}
				
				String ftpPath=EMSCollectService.constructFtpDestination(
						ftpIp, ftpPort, fileName, factory);
				
				emsSession.getHistoryPMData(ne,
						ftpPath, userName, password, startTime,
						endTime, new short[]{},new String[]{}, 
						new String[] {GRANULARITY});
			} catch (CommonException e) {
				e.printStackTrace();
			}
		}
		log.info("线性采集"+neList.size()+"个网元，结束时间："+new Date());
		//----------------多线程采集性能数据------------------
		for(NameAndStringValue_T[] ne:neList){
			collectPm collect=new collectPm(ne);
	        Thread thread= new Thread(collect);
	        thread.start();
		}
		
	}
	
	class collectPm implements Runnable {
		private NameAndStringValue_T[] ne;
		String GRANULARITY = DataCollectDefine.COMMON.GRANULARITY_24HOUR_STRING;
		
		public collectPm(NameAndStringValue_T[] ne){
			this.ne = ne;
		}
		public void run() {
			try {
				log.info("多线程采集网元："+ne[1].value+"，开始时间："+new Date());
				
				// 历史性能配置参数
				ftpIp = p.getProperty(FTPIP);
				if(ftpIp==null||ftpIp.isEmpty()||
					ftpIp.equals(CommonUtil.getLocalHostName())){
					ftpIp=CommonUtil.getLocalHost(corbaip).getHostAddress();
				}
				String portStr=p.getProperty(FTPPORT);
				if(portStr==null||portStr.isEmpty()){
					ftpPort=21;
				}else{
					ftpPort = Integer.valueOf(portStr);
				}
				userName = p.getProperty(FTPUSERNAME);
				password = p.getProperty(FTPPASSWORD);
				startTime = p.getProperty(STARTTIME);
				endTime = p.getProperty(ENDTIME);
				
				int factory=nmsType/10;
				String fileName = corbaip+"_"+ne[1].value;
				switch(factory){
				case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
					fileName = fileName+".txt";
					break;
				case DataCollectDefine.FACTORY_HW_FLAG:
					fileName = fileName+".csv";
					break;
				case DataCollectDefine.FACTORY_ZTE_FLAG:
					fileName = fileName+".dat";
					break;
				case DataCollectDefine.FACTORY_LUCENT_FLAG:
					fileName = fileName+".csv";
					break;
				default:
					fileName = fileName+".csv";
				}
				
				String ftpPath=EMSCollectService.constructFtpDestination(
						ftpIp, ftpPort, fileName, factory);

				emsSession.getHistoryPMData(ne,
						ftpPath, userName, password, startTime,
						endTime, new short[]{},new String[]{}, 
						new String[] {GRANULARITY});
				
				log.info("多线程采集网元："+ne[1].value+"，结束时间："+new Date());
				
			} catch (CommonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	/**
	 * @param args
	 */
	public void getAndPrint() throws Exception {

		if(exportExcel)
			excelWriter = new ExcelWriterUtil(basePath+"DATA_EMS",encode);
		if(exportTxt)
			txtWriter = new com.fujitsu.util.ObjectPrinter(encode);
		/*#########EMSElementMgr######*/
		String CMD=VEMSSession.getNotification;
		try{if (checkProperty(CMD)) {
			emsSession.startUpNotification();
		}}catch(CommonException e){ExceptionHandler.handleException(e);}
		catch(Exception e){
			ExceptionHandler.handleException(e);
		}
		CMD=VEMSSession.getSupportedManagers;
		try{if (checkProperty(CMD)) {
			Object datas = emsSession.getSupportedManagers();
			//对象数据序列化保存
			writeObject(datas, basePath,
					CMD, CmdObjectHelper);
			// 打印数据
			if(exportExcel)
				excelWriter.writeExcel(datas,CMD, SheetNumber++);
			
		}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
		CMD=VEMSSession.getEms;
		try{if (checkProperty(CMD)) {
			Object datas = emsSession.getEMS();
			//对象数据序列化保存
			writeObject(datas, basePath,
					CMD, CmdObjectHelper);
			// 打印数据
			if(exportExcel)
				excelWriter.writeExcel(datas,CMD, SheetNumber++);
			
		}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
		CMD=VEMSSession.getAllEMSAndMEActiveAlarms;
		try{if (checkProperty(CMD)) {
			Object datas = emsSession.getAllEMSAndMEActiveAlarms();
			//对象数据序列化保存
			writeObject(datas, basePath,
					CMD,CmdObjectHelper);
			// 打印数据
			if(exportExcel)
				excelWriter.writeExcel(datas,CMD, SheetNumber++);
			if(exportTxt){
				txtWriter.writeLogFile(basePath,CMD, txtWriter.printObject(datas),false);
			}
			
		}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
		CMD=VEMSSession.getAllEMSSystemActiveAlarms;
		try{if (checkProperty(CMD)) {
			Object datas = emsSession.getAllEMSSystemActiveAlarms();
			//对象数据序列化保存
			writeObject(datas, basePath,
					CMD,CmdObjectHelper);
			// 打印数据
			if(exportExcel)
				excelWriter.writeExcel(datas,CMD, SheetNumber++);
			
		}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
		CMD=VEMSSession.getAllTopLevelSubnetworks;
		try{if (checkProperty(CMD)) {
			if(subnetArray==null){
				subnetArray=(Object[])emsSession.getAllTopLevelSubnetworks();
			}
			Object[] datas = subnetArray;
			// 对象数据序列化保存
			writeObject(datas, basePath,
					CMD,CmdObjectHelper);
			// 打印数据
			if(exportExcel)
				excelWriter.writeExcel(datas,CMD, SheetNumber++);
			
		}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
		CMD=VEMSSession.getAllTopLevelSubnetworkNames;
		try{if (checkProperty(CMD)) {
			Object datas = emsSession.getAllTopLevelSubnetworkNames();
			// 对象数据序列化保存
			writeObject(datas, basePath,
					CMD,CmdObjectHelper);
			// 打印数据
			if(exportExcel)
				excelWriter.writeExcel(datas,CMD, SheetNumber++);
			
		}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
		CMD=VEMSSession.getAllTopLevelTopologicalLinks;
		try{if (checkProperty(CMD)) {
			Object datas = emsSession.getAllTopLevelTopologicalLinks();
			// 对象数据序列化保存
			writeObject(datas, basePath,
					CMD,CmdObjectHelper);
			// 打印数据
			if(exportExcel)
				excelWriter.writeExcel(datas,CMD, SheetNumber++);
		}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
		CMD=VEMSSession.getAllManagedElements;
		try{if (checkProperty(CMD)) {
			if(neArray==null){
				neArray=(Object[])emsSession.getAllManagedElements();
			}
			Object[] datas = neArray;
			// 对象数据序列化保存
			writeObject(datas, basePath,
					CMD,CmdObjectHelper);
			// 打印数据
			if(exportExcel)
				excelWriter.writeExcel(datas,CMD, SheetNumber++);
			
		}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
		CMD=VEMSSession.getAllManagedElementNames;
		try{if (checkProperty(CMD)) {
			Object datas = emsSession.getAllManagedElementNames();
			// 对象数据序列化保存
			writeObject(datas, basePath,
					CMD,CmdObjectHelper);
			// 打印数据
			if(exportExcel)
				excelWriter.writeExcel(datas,CMD, SheetNumber++);
			
		}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
		CMD=VEMSSession.getAllTopologicalLinks;
		try{if (checkProperty(CMD)) {
			for (NameAndStringValue_T[] name : subnetWorkList) {
				String path=VEMSSession.getRelationPathByName(name);
				Object datas = emsSession.getAllTopologicalLinks(name);
				// 对象数据序列化保存
				writeObject(datas, basePath+path,
						CMD,CmdObjectHelper);
				// 打印数据
				if(exportExcel)
					excelWriter.writeExcel(datas,CMD, SheetNumber++);
			}
		}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
		
		CMD=VEMSSession.getAllSubnetworkConnections;
		try{if (checkProperty(CMD)) {
			for (NameAndStringValue_T[] name : subnetWorkList) {
				CMD=VEMSSession.getAllSubnetworkConnections;
				String path=VEMSSession.getRelationPathByName(name).replace(":", "");
				Object[] datas = emsSession.getAllSubnetworkConnections(name);
				// 对象数据序列化保存
				writeObject(datas, basePath+path,
						CMD,CmdObjectHelper);
				// 打印数据
				if(exportExcel)
					excelWriter.writeExcel(datas,CMD, SheetNumber++);
				
				int tmpSheetNumber = 0;
				ExcelWriterUtil tmpexcelWriter = null;

				for(Object item:datas){
					NameAndStringValue_T[] sncName = getSncName(item);
					String tmpPath=VEMSSession.getRelationPathByName(sncName).replace(":", "");
					CMD=VEMSSession.getRoute;
					
					if(exportExcel)
						tmpexcelWriter = new ExcelWriterUtil(
							basePath+tmpPath+CMD,encode);
					
					if (checkProperty(CMD)){
						try{
						Object itemdatas = emsSession.getRoute(sncName);
						// 对象数据序列化保存
						writeObject(itemdatas, basePath+tmpPath, CMD,CmdObjectHelper);
						// 打印数据
						if(exportExcel){
							if(((Object[])itemdatas).length > 0){
							tmpexcelWriter.writeExcel(itemdatas,getOnlyValueByName(sncName).replace(":", ""), tmpSheetNumber);
							}
						}
						if(exportTxt){
							if(((Object[])itemdatas).length > 0){
								txtWriter.writeLogFile(basePath+tmpPath,getOnlyValueByName(sncName).replace(":", ""), txtWriter.printObject(itemdatas),false);
							}
						}
						}catch (Exception e) {
							ExceptionHandler.handleException(e);
							haveARest();
						}
					}
				}
				break;
			}
		}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
		
		
		CMD=VEMSSession.getHoldingTime;
		try{if (checkProperty(CMD)) {
			Object datas = emsSession.getHoldingTime();
			// 对象数据序列化保存
			writeObject(datas, basePath, CMD,CmdObjectHelper);
			// 打印数据
			if(exportExcel)
				excelWriter.writeExcel(datas,CMD, SheetNumber++);
		}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
//		CMD=VEMSSession.getAllSubnetworkConnections;
//		try{if (true||checkProperty(CMD)) {
//			Object datas = emsSession.getAllSubnetworkConnections();
//			// 对象数据序列化保存
//			writeObject(datas, basePath, CMD,CmdObjectHelper);
//			// 打印数据
//			if(exportExcel)
//				excelWriter.writeExcel(datas,CMD, SheetNumber++);
//			if(exportTxt){
//				txtWriter.writeLogFile(basePath,CMD, txtWriter.printObject(datas),false);
//			}
//		}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
		
//		CMD=VEMSSession.getSNC;
//		try{if (true||checkProperty(CMD)) {
//			Object datas = emsSession.getSNC();
//			// 对象数据序列化保存
//			writeObject(datas, basePath, CMD,CmdObjectHelper);
//			// 打印数据
//			if(exportExcel)
//				excelWriter.writeExcel(datas,CMD, SheetNumber++);
//		}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
		
//		CMD=VEMSSession.getRoute;
//		try{if (true||checkProperty(CMD)) {
//			Object datas = emsSession.getRoute();
//			// 对象数据序列化保存
//			writeObject(datas, basePath, CMD,CmdObjectHelper);
//			// 打印数据
//			if(exportExcel)
//				excelWriter.writeExcel(datas,CMD, SheetNumber++);
//			if(exportTxt){
//				txtWriter.writeLogFile(basePath,CMD, txtWriter.printObject(datas),false);
//			}
//		}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
		
		
//		CMD=VEMSSession.getAllEthernetSubnetworkConnections;
//		try{if (true||checkProperty(CMD)) {
//			Object datas = emsSession.getAllEthernetSubnetworkConnections();
//			// 对象数据序列化保存
//			writeObject(datas, basePath, CMD,CmdObjectHelper);
//			// 打印数据
//			if(exportExcel)
//				excelWriter.writeExcel(datas,CMD, SheetNumber++);
//		}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
		//***************************  贝尔新增加命令 ****************************
		
		if (needToCollectAll) {
			//NameAndStringValue_T[] namingAttributes = null;
			short[] connectionRateList = null;
			if (!neMap.isEmpty()) {
				Iterator iter;
				iter = neMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					Object me = entry.getValue();
					
					//为空采集
					if(isConnectionRatelistNull){
						connectionRateList = new short[]{};
					}else{// 支持层速率
						connectionRateList = getMeSupportedRates(me);
					}
					// 网元显示名称
					neName = Stringformat(getMeNativeEmsName(me));

					try {
						collectSingleNeData(me, connectionRateList, 
								needToCollectAll);
					} catch (Exception e) {
						ExceptionHandler.handleException(e);
					}
					// 从列表中移除该ne
					neList.remove(entry.getKey());
					saveNeList(neList);
				}
			}
		} else {
			String selectedNeIdString = (String) p.get(SELECTED_NE_ID);
			NameAndStringValue_T[] namingAttributes = null;
			short[] connectionRateList = null;
			if (!selectedNeIdString.equals("")) {
				if (!neMap.isEmpty()) {
					Iterator iter=neMap.entrySet().iterator();
					
					//采集单个网元数据
					if(selectedNeIdString.split(",").length == 1){
					while (iter.hasNext()) {
						Map.Entry entry = (Map.Entry) iter.next();
						Object me = entry.getValue();
						// 取得网元名
						namingAttributes=getMeName(me);
						if (namingAttributes[namingAttributes.length - 1].value
									.equals(selectedNeIdString)) {
							// 支持层速率
							connectionRateList = getMeSupportedRates(me);
							// 网元显示名称
							neName = Stringformat(getMeNativeEmsName(me));
							try {
								collectSingleNeData(me, connectionRateList,
										needToCollectAll);
							} catch (Exception e) {
								ExceptionHandler.handleException(e);
							}
						}
						// 从列表中移除该ne
						neList.remove(entry.getKey());
						saveNeList(neList);
					}
					}else{
						neName = "";
						//采集多个网元历史性能--新增功能20140618
						String[] neIds = selectedNeIdString.split(",");
						List<NameAndStringValue_T[]> neList = new ArrayList<NameAndStringValue_T[]>();
						for(String neId:neIds){
							neList.add(getMeName(neMap.get(neId)));
							neName = neName+Stringformat(getMeNativeEmsName(neMap.get(neId)))+"_";
						}
						neName = neName.substring(0, neName.length()-1);
						CMD=VEMSSession.getHistoryPMData_NEs;
						try{if (true) {
							String GRANULARITY = DataCollectDefine.COMMON.GRANULARITY_24HOUR_STRING;
							// 历史性能配置参数
							ftpIp = p.getProperty(FTPIP);
							if(ftpIp==null||ftpIp.isEmpty()||
								ftpIp.equals(CommonUtil.getLocalHostName())){
								ftpIp=CommonUtil.getLocalHost(corbaip).getHostAddress();
							}
							String portStr=p.getProperty(FTPPORT);
							if(portStr==null||portStr.isEmpty()){
								ftpPort=21;
							}else{
								ftpPort = Integer.valueOf(portStr);
							}
							userName = p.getProperty(FTPUSERNAME);
							password = p.getProperty(FTPPASSWORD);
							startTime = p.getProperty(STARTTIME);
							endTime = p.getProperty(ENDTIME);
							//对包含斜杠的网元名进行转换
							String neDisplayName = neName;
							if(neDisplayName.contains("\\\\")){
								neDisplayName = neDisplayName.replaceAll("\\\\","/");
							}
							if(neDisplayName.contains("/")){
								neDisplayName = neDisplayName.replaceAll("/","");
							}

							//如果neDisplayName包含中文会报错，所以需要先进行转码操作
							neDisplayName = new String(neDisplayName.getBytes("GBK"),"iso-8859-1");
							int factory=nmsType/10;
							String fileName = corbaip+"_"+neDisplayName;
							switch(factory){
							case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
								fileName = fileName+".txt";
								break;
							case DataCollectDefine.FACTORY_HW_FLAG:
								fileName = fileName+".csv";
								break;
							case DataCollectDefine.FACTORY_ZTE_FLAG:
								fileName = fileName+".dat";
								break;
							case DataCollectDefine.FACTORY_LUCENT_FLAG:
								fileName = fileName+".csv";
								break;
							default:
								fileName = fileName+".csv";
							}
							String ftpPath=EMSCollectService.constructFtpDestination(
									ftpIp, ftpPort, fileName, factory);
							// 打印数据
							emsSession.getHistoryPMData_NEs(neList,
								ftpPath, userName, password, startTime,
								endTime, new short[]{},new String[]{}, 
								new String[] {GRANULARITY});
						}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
							
//							int factory=nmsType/10;
//							if(factory==DataCollectDefine.FACTORY_ZTE_FLAG){
//								// 打印数据
//								Object[] datas=emsSession.getHistoryPMData_NEs(neList, ftpIpAndFileName, GRANULARITY, CMD, selectedNeIdString, GRANULARITY, _layerRateList, _pMLocationList, GRANULARITY)(namingAttributes,
//										startTime,endTime, new short[]{},
//										new String[]{}, 
//										new String[] {GRANULARITY});
//								// 对象数据序列化保存
//								writeObject(datas, basePath+path, CMD.split("_")[0]+"_"+GRANULARITY,
//										CmdObjectHelper);
//								// 打印数据
//								//String fileDir=needToCollectAll?CURRENT_PM_NE:"";
//								if(exportExcel)
//									excelWriter.writeExcel(datas,CMD+"_"+GRANULARITY, SheetNumber++);
//							}
//						}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
					}
				}
			}
		}
		log.info("Collection Complete.");
		System.out.println("Collection Complete.");
		CMD=VEMSSession.getNotification;
		if (checkProperty(CMD)) {//获取通知
			System.out.println("receiving notifications, press any key to stop and exit.");
			System.in.read();
		} else {
			Thread.sleep(5 * 1000);
		}
		emsSession.endSession();
	}

	// 取单个网元数据
	private void collectSingleNeData(Object me, 
			short[] connectionRateList, boolean needToCollectAll) {
		NameAndStringValue_T[] namingAttributes=getMeName(me);

		if (namingAttributes != null) {
			SheetNumber = 0;
			String path=VEMSSession.getRelationPathByName(namingAttributes);
			String fileDir=getFileDirOfNe(me);
			String baseName=getValueByName(namingAttributes)+"_"+neName.replaceAll("/", "-");
			if(exportExcel)
				excelWriter = new ExcelWriterUtil(basePath+fileDir+baseName,encode);
			String CMD=VEMSSession.getManagedElement;
			try{if (checkProperty(CMD)) {
				Object datas = emsSession.getManagedElement(namingAttributes);
				// 对象数据序列化保存
				writeObject(datas, basePath+path, CMD,CmdObjectHelper);
				// 打印数据
				if(exportExcel)
					excelWriter.writeExcel(datas,CMD, SheetNumber++);
			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
			CMD=VEMSSession.getAllActiveAlarms;
			try{if (checkProperty(CMD)) {
				Object datas = emsSession.getAllActiveAlarms(namingAttributes);
				// 此对象不可序列化 //对象数据序列化保存
				writeObject(datas, basePath+path, CMD,CmdObjectHelper);
				// 打印数据
				if(exportExcel)
					excelWriter.writeExcel(datas,CMD, SheetNumber++);
			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
			CMD=VEMSSession.getAllInternalTopologicalLinks;
			try{if (checkProperty(CMD)) {
				Object datas = emsSession.getAllInternalTopologicalLinks(namingAttributes);
				// 对象数据序列化保存
				writeObject(datas, basePath+path, CMD,CmdObjectHelper);
				// 打印数据
				excelWriter.writeExcel(datas,CMD, SheetNumber++);
				
			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
			CMD=VEMSSession.getAllEquipment;
			try{if (checkProperty(CMD)||checkProperty(VEMSSession.getAllCurrentPMData_Equip)) {
				Object[] datas = (Object[])emsSession.getAllEquipment(namingAttributes);
				if (checkProperty(CMD)){
					try{
					// 对象数据序列化保存
					writeObject(datas, basePath+path, CMD.split("_")[0],CmdObjectHelper);
					// 打印数据
					if(exportExcel)
						excelWriter.writeExcel(datas,CMD, SheetNumber++);
					}catch (Exception e) {
						ExceptionHandler.handleException(e);
						haveARest();
					}
				}
				CMD=VEMSSession.getAllCurrentPMData_Equip;
				if (checkProperty(CMD)){
					int equipSheetNumber = 0;
					ExcelWriterUtil equipexcelWriter = null;
					if(exportExcel)
						equipexcelWriter = new ExcelWriterUtil(
							basePath+fileDir+baseName+"_"+CMD,encode);
					for(Object item:datas){
						String GRANULARITY = DataCollectDefine.COMMON.GRANULARITY_24HOUR_STRING;
						NameAndStringValue_T[] itemName=null;
						Object itemdatas;
						itemName = getEquipName(item);

						String tmpPath=VEMSSession.getRelationPathByName(itemName);
						try{
						itemdatas = emsSession.getAllCurrentPMData(
								itemName,new short[]{},Locations,
								new String[] {GRANULARITY});
						// 对象数据序列化保存
						writeObject(itemdatas, basePath+
							tmpPath, CMD.split("_")[0]+"_"+GRANULARITY,CmdObjectHelper);
						if(exportExcel)
							equipexcelWriter.writeExcel(itemdatas,getNoValueByName(itemName)+"_"+GRANULARITY, equipSheetNumber++);
						}catch (Exception e) {
							ExceptionHandler.handleException(e);
							haveARest();
						}
						try{
						GRANULARITY=DataCollectDefine.COMMON.GRANULARITY_15MIN_STRING;
						itemdatas = emsSession.getAllCurrentPMData(
								itemName,new short[]{},Locations,
								new String[] {GRANULARITY});
						// 对象数据序列化保存
						writeObject(itemdatas, basePath+
							tmpPath, CMD.split("_")[0]+"_"+GRANULARITY,CmdObjectHelper);
						if(exportExcel)
							equipexcelWriter.writeExcel(itemdatas,getNoValueByName(itemName)+"_"+GRANULARITY, equipSheetNumber++);
						}catch (Exception e) {
							ExceptionHandler.handleException(e);
							haveARest();
						}
					}
				}
			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
			try{if (checkProperty(VEMSSession.getAllPTPs)||
					checkProperty(VEMSSession.getContainedPotentialTPs)||
					checkProperty(VEMSSession.getBindingPath)||
					checkProperty(VEMSSession.getAllCurrentPMData_Ptp)) {
				Object[] datas = (Object[])emsSession.getAllPTPs(namingAttributes);
				CMD=VEMSSession.getAllPTPs;
				if (checkProperty(CMD)){
					// 对象数据序列化保存
					writeObject(datas, basePath+path, CMD,CmdObjectHelper);
					// 打印数据
					if(exportExcel)
						excelWriter.writeExcel(datas,CMD, SheetNumber++);
				}
				if (checkProperty(VEMSSession.getContainedPotentialTPs)||
						checkProperty(VEMSSession.getBindingPath)||
						checkProperty(VEMSSession.getAllCurrentPMData_Ptp)) {
					String ptpInfo = (String) p.get("selectedPtp");
					
					int tmpSheetNumber = 0;
					CMD=VEMSSession.getContainedPotentialTPs;
					ExcelWriterUtil tmpexcelWriter = null;
					if(exportExcel)
						tmpexcelWriter = new ExcelWriterUtil(
							basePath+fileDir+baseName+"_"+CMD,encode);					
					int tmpBindingSheetNumber = 0;
					CMD=VEMSSession.getBindingPath;
					ExcelWriterUtil tmpBindingexcelWriter = null;
					if(exportExcel)
						tmpBindingexcelWriter = new ExcelWriterUtil(
							basePath+fileDir+baseName+"_"+CMD,encode);
					
					int ptpSheetNumber = 0;
					CMD=VEMSSession.getAllCurrentPMData_Ptp;
					ExcelWriterUtil ptpexcelWriter = null;
					if(exportExcel)
						ptpexcelWriter = new ExcelWriterUtil(
							basePath+fileDir+baseName+"_"+CMD,encode);
					
					for(Object item:datas){
						NameAndStringValue_T[] ptpName = getPtpName(item);
						if(null==ptpInfo||ptpInfo.isEmpty()||ptpInfo.equals(ptpName[2].value)){
							String tmpPath=VEMSSession.getRelationPathByName(ptpName);
							CMD=VEMSSession.getContainedPotentialTPs;
							if (checkProperty(CMD)){
								try{
								Object itemdatas = emsSession.getContainedPotentialTPs(ptpName);
								// 对象数据序列化保存
								writeObject(itemdatas, basePath+tmpPath, CMD,CmdObjectHelper);
								// 打印数据
								if(exportExcel){
									if(((Object[])itemdatas).length > 0){
									tmpexcelWriter.writeExcel(itemdatas,getNoValueByName(ptpName), tmpSheetNumber++);
									}
								}
								if(exportTxt){
									if(((Object[])itemdatas).length > 0){
										txtWriter.writeLogFile(basePath,getNoValueByName(ptpName)+"_"+CMD, txtWriter.printObject(itemdatas),false);
									}
								}
								}catch (Exception e) {
									ExceptionHandler.handleException(e);
									haveARest();
								}
							}
							
							CMD=VEMSSession.getBindingPath;
							if (checkProperty(CMD)){
								try{
								Object itemdatas = emsSession.getBindingPath(ptpName);
								// 对象数据序列化保存
								writeObject(itemdatas, basePath+tmpPath, CMD,CmdObjectHelper);
								// 打印数据
								if(exportExcel){
									tmpBindingexcelWriter.writeExcel(itemdatas,getNoValueByName(ptpName), tmpBindingSheetNumber++);
								}
								}catch (Exception e) {
									ExceptionHandler.handleException(e);
									haveARest();
								}
							}
							CMD=VEMSSession.getAllCurrentPMData_Ptp;
							if (checkProperty(CMD)) {
								String GRANULARITY = DataCollectDefine.COMMON.GRANULARITY_24HOUR_STRING;
								Object itemdatas;
								try{
								itemdatas = emsSession.getAllCurrentPMData(ptpName,
										new short[]{},Locations,
										new String[] {GRANULARITY});
								// 对象数据序列化保存
								writeObject(itemdatas, basePath+
									tmpPath, CMD.split("_")[0]+"_"+GRANULARITY,CmdObjectHelper);
								if(exportExcel){
									if(((Object[])itemdatas).length > 0){
									ptpexcelWriter.writeExcel(itemdatas,getNoValueByName(ptpName)+"_"+GRANULARITY, ptpSheetNumber++);
									}
								}
								}catch (Exception e) {
									ExceptionHandler.handleException(e);
									haveARest();
								}
								try{
								GRANULARITY=DataCollectDefine.COMMON.GRANULARITY_15MIN_STRING;
								itemdatas = emsSession.getAllCurrentPMData(ptpName,
										new short[]{},Locations,
										new String[] {GRANULARITY});
								// 对象数据序列化保存
								writeObject(itemdatas, basePath+
									tmpPath, CMD.split("_")[0]+"_"+GRANULARITY,CmdObjectHelper);
								if(exportExcel){
									if(((Object[])itemdatas).length > 0){
									ptpexcelWriter.writeExcel(itemdatas,getNoValueByName(ptpName)+"_"+GRANULARITY, ptpSheetNumber++);
									}
								}
								}catch (Exception e) {
									ExceptionHandler.handleException(e);
									haveARest();
								}
							}
						}
					}
				}
			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
			CMD=VEMSSession.getAllPTPNames;
			try{if (checkProperty(CMD)) {
				Object datas = emsSession.getAllPTPNames(namingAttributes);
				// 对象数据序列化保存
				writeObject(datas, basePath+path, CMD, CmdObjectHelper);
				// 打印数据
				if(exportExcel)
					excelWriter.writeExcel(datas,CMD, SheetNumber++);
			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
			CMD=VEMSSession.getAllCrossConnections;
			try{if (checkProperty(CMD)) {
				Object datas = emsSession.getAllCrossConnections(namingAttributes,connectionRateList);
				// 对象数据序列化保存
				writeObject(datas, basePath+path, CMD,CmdObjectHelper);
				// 打印数据
				if(exportExcel)
					excelWriter.writeExcel(datas,CMD, SheetNumber++);
			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
			CMD=VEMSSession.getAllCurrentPMData_Ne;
			try{if (checkProperty(CMD)) {
				String GRANULARITY = DataCollectDefine.COMMON.GRANULARITY_24HOUR_STRING;
				Object datas;
				try{
				datas = emsSession.getAllCurrentPMData(namingAttributes,
						new short[]{},Locations,
						new String[] {GRANULARITY});
				// 对象数据序列化保存
				writeObject(datas, basePath+path, CMD.split("_")[0]+"_"+GRANULARITY,
						CmdObjectHelper);
				// 打印数据
				if(exportExcel)
					excelWriter.writeExcel(datas,CMD+"_"+GRANULARITY, SheetNumber++);
				}catch (Exception e) {
					ExceptionHandler.handleException(e);
					haveARest();
				}
				try{
				GRANULARITY = DataCollectDefine.COMMON.GRANULARITY_15MIN_STRING;
				datas = emsSession.getAllCurrentPMData(namingAttributes,
						new short[]{},Locations,
						new String[] {GRANULARITY});
				// 对象数据序列化保存
				writeObject(datas, basePath+path, CMD.split("_")[0]+"_"+GRANULARITY,
						CmdObjectHelper);
				// 打印数据
				//String fileDir=needToCollectAll?CURRENT_PM_NE:"";
				if(exportExcel)
					excelWriter.writeExcel(datas,CMD+"_"+GRANULARITY, SheetNumber++);
				}catch (Exception e) {
					ExceptionHandler.handleException(e);
					haveARest();
				}
				try{
				GRANULARITY = DataCollectDefine.COMMON.GRANULARITY_NA_STRING;
				datas = emsSession.getAllCurrentPMData(namingAttributes,
						new short[]{},Locations,
						new String[] {GRANULARITY});
				// 对象数据序列化保存
				writeObject(datas, basePath+path, CMD.split("_")[0]+"_"+GRANULARITY,
						CmdObjectHelper);
				// 打印数据
				//String fileDir=needToCollectAll?CURRENT_PM_NE:"";
				if(exportExcel)
					excelWriter.writeExcel(datas,CMD+"_"+GRANULARITY, SheetNumber++);
				}catch (Exception e) {
					ExceptionHandler.handleException(e);
					haveARest();
				}
			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
			CMD=VEMSSession.getHistoryPMData+"_Cmd";
			try{if (checkProperty(CMD)) {
				String GRANULARITY = DataCollectDefine.COMMON.GRANULARITY_24HOUR_STRING;
				// 历史性能配置参数
				startTime = p.getProperty(STARTTIME);
				endTime = p.getProperty(ENDTIME);
				
				int factory=nmsType/10;
				if(factory==DataCollectDefine.FACTORY_ZTE_FLAG){
					// 打印数据
					Object[] datas=emsSession.getHistoryPMData(namingAttributes,
							startTime,endTime, new short[]{},
							new String[]{}, 
							new String[] {GRANULARITY});
					// 对象数据序列化保存
					writeObject(datas, basePath+path, CMD.split("_")[0]+"_"+GRANULARITY,
							CmdObjectHelper);
					// 打印数据
					//String fileDir=needToCollectAll?CURRENT_PM_NE:"";
					if(exportExcel)
						excelWriter.writeExcel(datas,CMD+"_"+GRANULARITY, SheetNumber++);
				}
			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
			CMD=VEMSSession.getHistoryPMData;
			try{if (checkProperty(CMD)) {
				String GRANULARITY = DataCollectDefine.COMMON.GRANULARITY_24HOUR_STRING;
				// 历史性能配置参数
				ftpIp = p.getProperty(FTPIP);
				if(ftpIp==null||ftpIp.isEmpty()||
					ftpIp.equals(CommonUtil.getLocalHostName())){
					ftpIp=CommonUtil.getLocalHost(corbaip).getHostAddress();
				}
				String portStr=p.getProperty(FTPPORT);
				if(portStr==null||portStr.isEmpty()){
					ftpPort=21;
				}else{
					ftpPort = Integer.valueOf(portStr);
				}
				userName = p.getProperty(FTPUSERNAME);
				password = p.getProperty(FTPPASSWORD);
				startTime = p.getProperty(STARTTIME);
				endTime = p.getProperty(ENDTIME);
				//对包含斜杠的网元名进行转换
				String neDisplayName = neName;
				if(neDisplayName.contains("\\\\")){
					neDisplayName = neDisplayName.replaceAll("\\\\","/");
				}
				if(neDisplayName.contains("/")){
					neDisplayName = neDisplayName.replaceAll("/","");
				}

				//如果neDisplayName包含中文会报错，所以需要先进行转码操作
				neDisplayName = new String(neDisplayName.getBytes("GBK"),"iso-8859-1");
				int factory=nmsType/10;
				String fileName = corbaip+"_"+neDisplayName;
				switch(factory){
				case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
					fileName = fileName+".txt";
					break;
				case DataCollectDefine.FACTORY_HW_FLAG:
				case DataCollectDefine.FACTORY_LUCENT_FLAG:
				default:
					fileName = fileName+".csv";
					break;
				case DataCollectDefine.FACTORY_ZTE_FLAG:
					fileName = fileName+".dat";
					break;
				case DataCollectDefine.FACTORY_ALU_FLAG:
					fileName = fileName+".zip";
					break;
				}
				String ftpPath=EMSCollectService.constructFtpDestination(
						ftpIp, ftpPort, fileName, factory);
				// 打印数据
				emsSession.getHistoryPMData(namingAttributes,
					ftpPath, userName, password, startTime,
					endTime, new short[]{},new String[]{}, 
					new String[] {GRANULARITY});
			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
			CMD=VEMSSession.getMEPMcapabilities;
			try{if (checkProperty(CMD)) {
				Object datas = emsSession.getMEPMcapabilities(namingAttributes);
				// 对象数据序列化保存
				writeObject(datas, basePath+path, CMD,CmdObjectHelper);
				// 打印数据
				if(exportExcel)
					excelWriter.writeExcel(datas,CMD, SheetNumber++);
			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
			
			try{if (checkProperty(VEMSSession.getAllWDMProtectionGroups)) {
				Object[] datas = (Object[])emsSession.getAllWDMProtectionGroups(namingAttributes);
				CMD=VEMSSession.getAllWDMProtectionGroups;
				if (checkProperty(CMD)){
					// 对象数据序列化保存
					writeObject(datas, basePath+path, CMD,CmdObjectHelper);
					// 打印数据
					if(exportExcel)
						excelWriter.writeExcel(datas,CMD, SheetNumber++);
				}
			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
			
			try{if (checkProperty(VEMSSession.getMEconfigData)) {
				Object datas = (Object)emsSession.getMEconfigData(namingAttributes);
				CMD=VEMSSession.getMEconfigData;
				if (checkProperty(CMD)){
					// 对象数据序列化保存
					writeObject(datas, basePath+path, CMD,CmdObjectHelper);
					// 打印数据
					if(exportExcel)
						excelWriter.writeExcel(datas,CMD, SheetNumber++);
				}
			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
			try{if (checkProperty(VEMSSession.getAllProtectionGroups)
					||checkProperty(VEMSSession.retrieveSwitchData)) {
				Object[] datas = (Object[])emsSession.getAllProtectionGroups(namingAttributes);
				CMD=VEMSSession.getAllProtectionGroups;
				if (checkProperty(CMD)){
					// 对象数据序列化保存
					writeObject(datas, basePath+path, CMD,CmdObjectHelper);
					// 打印数据
					if(exportExcel)
						excelWriter.writeExcel(datas,CMD, SheetNumber++);
				}
				CMD=VEMSSession.retrieveSwitchData;
				if (checkProperty(CMD)) {
					int tmpSheetNumber = 0;
					ExcelWriterUtil tmpexcelWriter=null;
					if(exportExcel){
						tmpexcelWriter = new ExcelWriterUtil(
							basePath+fileDir+baseName+"_"+CMD, encode);
					}
					for(Object item:datas){
						try{
						NameAndStringValue_T[] pgpName = getPgpName(item);
						String tmpPath=VEMSSession.getRelationPathByName(pgpName);
						Object itemdatas = emsSession.retrieveSwitchData(pgpName);
						// 对象数据序列化保存
						writeObject(itemdatas, basePath+tmpPath, CMD,CmdObjectHelper);
						// 打印数据
						if(exportExcel)
							tmpexcelWriter.writeExcel(itemdatas,getNoValueByName(pgpName), tmpSheetNumber++);
						}catch (Exception e) {
							ExceptionHandler.handleException(e);
							haveARest();
						}
					}
				}
			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
			try{if (checkProperty(VEMSSession.getAllEProtectionGroups)
					||checkProperty(VEMSSession.retrieveESwitchData)) {
				Object[] datas = (Object[])emsSession.getAllEProtectionGroups(namingAttributes);
				CMD=VEMSSession.getAllEProtectionGroups;
				if (checkProperty(CMD)){
					// 对象数据序列化保存
					writeObject(datas, basePath+path, CMD,CmdObjectHelper);
					// 打印数据
					if(exportExcel)
						excelWriter.writeExcel(datas,CMD, SheetNumber++);
				}
				CMD=VEMSSession.retrieveESwitchData;
				if (checkProperty(CMD)) {
					int tmpSheetNumber = 0;
					ExcelWriterUtil tmpexcelWriter = new ExcelWriterUtil(
							basePath+fileDir+baseName+"_"+CMD, encode);
					for(Object item:datas){
						NameAndStringValue_T[] epgpName = getEpgpName(item);
						String tmpPath=VEMSSession.getRelationPathByName(epgpName);
						Object itemdatas = emsSession.retrieveESwitchData(epgpName);
						// 对象数据序列化保存
						writeObject(itemdatas, basePath+tmpPath, CMD);
						// 打印数据
						tmpexcelWriter.writeExcel(itemdatas,getNoValueByName(epgpName), tmpSheetNumber++);
					}
				}
			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
			CMD=VEMSSession.getObjectClockSourceStatus;
			try{if (checkProperty(CMD)) {
				Object datas = emsSession.getObjectClockSourceStatus(namingAttributes);
				// 对象数据序列化保存
				writeObject(datas, basePath+path, CMD,CmdObjectHelper);
				// 打印数据
				if(exportExcel)
					excelWriter.writeExcel(datas,CMD, SheetNumber++);
				
			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
			CMD=VEMSSession.getAllMstpEndPointNames;
			try{if (checkProperty(CMD)) {
				Object datas = emsSession.getAllMstpEndPointNames(namingAttributes);
				// 对象数据序列化保存
				writeObject(datas, basePath+path, CMD,CmdObjectHelper);
				// 打印数据
				if(exportExcel)
					excelWriter.writeExcel(datas,CMD, SheetNumber++);
				
			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
			CMD=VEMSSession.getAllMstpEndPoints;
			try{if (checkProperty(CMD)) {
				Object datas = emsSession.getAllMstpEndPoints(namingAttributes);
				// 对象数据序列化保存
				writeObject(datas, basePath+path, CMD,CmdObjectHelper);
				// 打印数据
				if(exportExcel)
					excelWriter.writeExcel(datas,CMD, SheetNumber++);
				
			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
			CMD=VEMSSession.getAllEthService;
			try{if (checkProperty(CMD)) {
				Object datas = emsSession.getAllEthService(namingAttributes);
				// 对象数据序列化保存
				writeObject(datas, basePath+path, CMD,CmdObjectHelper);
				// 打印数据
				if(exportExcel)
					excelWriter.writeExcel(datas,CMD, SheetNumber++);
				
			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
			CMD=VEMSSession.getAllVBNames;
			try{if (checkProperty(CMD)) {
				Object datas = emsSession.getAllVBNames(namingAttributes);
				// 对象数据序列化保存
				writeObject(datas, basePath+path, CMD,CmdObjectHelper);
				// 打印数据
				if(exportExcel)
					excelWriter.writeExcel(datas,CMD, SheetNumber++);
				
			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
			CMD=VEMSSession.getAllVBs;
			try{if (checkProperty(CMD)) {
				Object datas = emsSession.getAllVBs(namingAttributes);
				// 对象数据序列化保存
				writeObject(datas, basePath+path, CMD,CmdObjectHelper);
				// 打印数据
				if(exportExcel)
					excelWriter.writeExcel(datas,CMD, SheetNumber++);
				
			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
//			CMD=VEMSSession.getBindingPath;
//			try{if (checkProperty(CMD)) {
//				Object datas = emsSession.getBindingPath(namingAttributes);
//				// 对象数据序列化保存
//				writeObject(datas, basePath+path, CMD,CmdObjectHelper);
//				// 打印数据
//				if(exportExcel)
//					excelWriter.writeExcel(datas,CMD, SheetNumber++);
//				
//			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
			CMD=VEMSSession.getAllVLANs;
			try{if (checkProperty(CMD)) {
				Object datas = emsSession.getAllVLANs(namingAttributes);
				// 对象数据序列化保存
				writeObject(datas, basePath+path, CMD,CmdObjectHelper);
				// 打印数据
				if(exportExcel)
					excelWriter.writeExcel(datas,CMD, SheetNumber++);
				
			}}catch(Exception e){ExceptionHandler.handleException(e);haveARest();}
		} else
			log.error("NE id is not in map");
	}

	private void initData() throws Exception {
		InputStream in;
		String configLocation="conf/"+GetAndPrintData.CFG_FILEPATH;
		if(new File("../"+configLocation).exists())
			configLocation="../"+configLocation;
		in = new BufferedInputStream(new FileInputStream(configLocation));
		p = new Properties();
		p.load(in);
		in.close();

		String neprop = null;
		try {
			in = new BufferedInputStream(new FileInputStream(VEMSSession.NE_DIR));
			Scanner read = new Scanner(in);
			neprop = read.nextLine();
		} catch (Exception e) {
		}

		if (neprop != null) {
			System.out.println("上次采集可能被意外中止,是否继续上次采集?(Y/N)");
			Scanner read = new Scanner(System.in);
			String s = read.nextLine();
			if (!"Y".equals(s.trim().toUpperCase())) {
				// 不继续采集
				neprop = null;
			}
		}

		// get Instance
		nmsType = Integer.valueOf(p.getProperty(NMS_TYPE));
		needToCollectAll = Boolean.parseBoolean(p
				.getProperty(COLLECT_ALL_NE));
		intervalTime = Long.parseLong(p.getProperty(INTERVALTIME));
		exportExcel = ((String) p.get(EXPORTEXCEL)).equals(ENABLE);
		exportObject = ((String) p.get(EXPORTOBJECT)).equals(ENABLE);
		exportTxt = ((String) p.get(EXPORTTXT)).equals(ENABLE);
		// get Instance
		encode = p.getProperty(ENCODE);
		
		corbaip = p.getProperty(IP);
		port = p.getProperty(PORT);
		corbaname = p.getProperty(NAME);
		corbapassword = p.getProperty(PASSWORD);
		nmsname = p.getProperty(NMS_NAME);
		isConnectionRatelistNull = Boolean.parseBoolean(p.getProperty(IS_CONNECTION_RATE_LIST_NULL));
		
		if(p.containsKey("sourceEncode")&&!p.getProperty("sourceEncode").isEmpty()){
			NameAndStringValueUtil.sourceEncode = p.getProperty("sourceEncode");
		}
		
		Map ems=new HashMap();
		ems.put("IP", corbaip);
		ems.put("TYPE", nmsType);
		ems.put("EMS_NAME", nmsname);
		ems.put("USER_NAME", corbaname);
		ems.put("PASSWORD", corbapassword);
		ems.put("PORT", port);
		ems.put("CONNECTION_MODE", DataCollectDefine.CONNECT_MODE_AUTO);
		ems.put("ENCODE", encode);
		
		EmsMap.put(corbaip, ems);
		
		switch(nmsType){
		case DataCollectDefine.NMS_TYPE_OTNM2000_FLAG:
			basePath=FILEPATH_FIM + "/" + corbaip+"/";
			CmdObjectHelper = FIMVEMSSession.CmdObjectHelper;
			if(VEMSSession.EMS_NAME_VEMS.equals(nmsname)){
				emsSession = FIMVEMSSession.newInstance(
						corbaname,corbapassword,corbaip,port,nmsname,encode);
			}else{
				emsSession = FIMEMSSession.newInstance(
					corbaname,corbapassword,corbaip,port,nmsname,encode);
			}
			break;
		case DataCollectDefine.NMS_TYPE_T2000_FLAG:
		case DataCollectDefine.NMS_TYPE_U2000_FLAG:
			basePath=FILEPATH_HW + "/" + corbaip+"/";
			CmdObjectHelper = HWVEMSSession.CmdObjectHelper;
			if(VEMSSession.EMS_NAME_VEMS.equals(nmsname)){
				emsSession = HWVEMSSession.newInstance(
						corbaname,corbapassword,corbaip,port,nmsname,encode);
			}else{
				emsSession = HWEMSSession.newInstance(
					corbaname,corbapassword,corbaip,port,nmsname,encode);
			}
			Locations = new String[]{
					DataCollectDefine.HW.HW_PM_LOCATION_NA,
					DataCollectDefine.HW.HW_PM_LOCATION_NEAR_END_RX,
					DataCollectDefine.HW.HW_PM_LOCATION_NEAR_END_TX
					};
			break;
		case DataCollectDefine.NMS_TYPE_E300_FLAG:
		case DataCollectDefine.NMS_TYPE_U31_FLAG:
			basePath=FILEPATH_ZTE + "/" + corbaip+"/";
			CmdObjectHelper = ZTEVEMSSession.CmdObjectHelper;
			if(VEMSSession.EMS_NAME_VEMS.equals(nmsname)){
				emsSession = ZTEVEMSSession.newInstance(
						corbaname,corbapassword,corbaip,port,nmsname,encode);
			}else{
				emsSession = ZTEU31EMSSession.newInstance(
					corbaname,corbapassword,corbaip,port,nmsname,encode);
			}
			break;
		case DataCollectDefine.NMS_TYPE_LUCENT_OMS_FLAG:
			basePath=FILEPATH_LUCENT + "/" + corbaip+"/";
			CmdObjectHelper = LUCENTVEMSSession.CmdObjectHelper;
			if(VEMSSession.EMS_NAME_VEMS.equals(nmsname)){
				emsSession = LUCENTVEMSSession.newInstance(
					corbaname,corbapassword,corbaip,port,nmsname,encode);
			}else{
				emsSession = LUCENTEMSSession.newInstance(
					corbaname,corbapassword,corbaip,port,nmsname,encode);
			}
			break;
		case DataCollectDefine.NMS_TYPE_ALU_FLAG:
			basePath=FILEPATH_ALU + "/" + corbaip+"/";
			CmdObjectHelper = ALUVEMSSession.CmdObjectHelper;
			if(VEMSSession.EMS_NAME_VEMS.equals(nmsname)){
				emsSession = ALUVEMSSession.newInstance(
						corbaname,corbapassword,corbaip,port,nmsname,encode);
			}else{
				emsSession = ALUEMSSession.newInstance(
					corbaname,corbapassword,corbaip,port,nmsname,encode);
			}
			break;
		}

		if(!DataCollectDefine.EMS_NAME_VEMS.equals(nmsname)&&
				!CommonUtil.isReachable(corbaip)){
			throw new UnknownHostException();
		}

		log.info("start initNeMap");
		Object[] meList = (Object[])emsSession.getAllManagedElements();

		Object[] subnetWorks = (Object[])emsSession.getAllTopLevelSubnetworks();
		if (checkProperty(VEMSSession.getAllManagedElements)||needToCollectAll){
			neArray=meList;
		}
		if (checkProperty(VEMSSession.getAllTopLevelSubnetworks)){
			subnetArray=subnetWorks;
		}
		if (neprop != null) {
			// 传说中的断点续采- -b
			String[] nes = neprop.split(",");
			for (String s : nes) {
				neList.add(s.trim());
			}
			for (int i = 0; i < meList.length; i++) {
				NameAndStringValue_T [] name = getMeName(meList[i]);
				if(name==null) continue;
				String neSerialNo = name[1].value;
				if (neList.contains(neSerialNo)) {
					neMap.put(neSerialNo, meList[i]);
				}
			}
		} else {
			for (int i = 0; i < meList.length; i++) {
				NameAndStringValue_T [] name = getMeName(meList[i]);
				if(name==null) continue;
				String neSerialNo = name[1].value;
				neMap.put(neSerialNo, meList[i]);
			}
		}
		// 将没采过的网元neSerialNo统统记录到那个小破文件里,然后采完一个删一个
		neList.clear();
		neList.addAll(neMap.keySet());//总的需采集/续采网元
		saveNeList(neList);
		for (int i = 0; i < subnetWorks.length; i++) {
			NameAndStringValue_T [] subnetName = getSubnetName(subnetWorks[i]);
			if(subnetName==null) continue;
			subnetWorkList.add(subnetName);
		}
		log.info("initNeMap success");
	}
	
	public static ExcelWriterUtil notifyExcelWriter = null;
	public static int notifyNumber = 0;
	public static int notifySheetNumber = 0;
	//public static ArrayList<String> Notifications = new ArrayList<String>();
	public static void ExportNotification(Object datas){
		try{
			String CMD = VEMSSession.getNotifyName(datas)+"_"+(notifyNumber++);
			//if(Notifications.contains(CMD))
			//	return;
			writeObject(datas, basePath+VEMSSession.DIR_NOTIFY+File.separator, 
					CMD,CmdObjectHelper);
			// 打印数据
			if(exportExcel){
				if(notifyExcelWriter==null||(notifySheetNumber%255)==0){
					notifyExcelWriter = new ExcelWriterUtil(basePath+VEMSSession.DIR_NOTIFY+"_"+(notifySheetNumber/255),encode);
					notifySheetNumber = 0;
				}
				notifyExcelWriter.writeExcel(datas,CMD, notifySheetNumber++);
			}
			//Notifications.add(CMD);
		}catch(Exception e){
			ExceptionHandler.handleException(e);
		}
	}
	
	public static String getProductName(Object ne){
		return (String)VEMSSession.getField(ne,"productName");
		/*if(FENGHUO.managedElement.ManagedElement_T.class.isInstance(ne)){
			FENGHUO.managedElement.ManagedElement_T me=(FENGHUO.managedElement.ManagedElement_T)ne;
			return me.productName;
		} else if(HW.managedElement.ManagedElement_T.class.isInstance(ne)){
			HW.managedElement.ManagedElement_T me=(HW.managedElement.ManagedElement_T)ne;
			return me.productName;
		} else if(ZTE_U31.managedElement.ManagedElement_T.class.isInstance(ne)){
			ZTE_U31.managedElement.ManagedElement_T me=(ZTE_U31.managedElement.ManagedElement_T)ne;
			return me.productName;
		} else if(LUCENT.managedElement.ManagedElement_T.class.isInstance(ne)){
			LUCENT.managedElement.ManagedElement_T me=(LUCENT.managedElement.ManagedElement_T)ne;
			return me.productName;
		} else if(ALU.managedElement.ManagedElement_T.class.isInstance(ne)){
			ALU.managedElement.ManagedElement_T me=(ALU.managedElement.ManagedElement_T)ne;
			return me.productName;
		}
		return null;*/
	}
	public static NameAndStringValue_T[] getMeName(Object ne){
		return (NameAndStringValue_T[])VEMSSession.getField(ne,"name");
		
		/*if(FENGHUO.managedElement.ManagedElement_T.class.isInstance(ne)){
			FENGHUO.managedElement.ManagedElement_T me=(FENGHUO.managedElement.ManagedElement_T)ne;
			return me.name;
		} else if(HW.managedElement.ManagedElement_T.class.isInstance(ne)){
			HW.managedElement.ManagedElement_T me=(HW.managedElement.ManagedElement_T)ne;
			return me.name;
		} else if(ZTE_U31.managedElement.ManagedElement_T.class.isInstance(ne)){
			ZTE_U31.managedElement.ManagedElement_T me=(ZTE_U31.managedElement.ManagedElement_T)ne;
			return me.name;
		} else if(LUCENT.managedElement.ManagedElement_T.class.isInstance(ne)){
			LUCENT.managedElement.ManagedElement_T me=(LUCENT.managedElement.ManagedElement_T)ne;
			return me.name;
		} else if(ALU.managedElement.ManagedElement_T.class.isInstance(ne)){
			ALU.managedElement.ManagedElement_T me=(ALU.managedElement.ManagedElement_T)ne;
			return me.name;
		}
		return null;*/
	}
	public static NameAndStringValue_T[] getSubnetName(Object subnet){
		return (NameAndStringValue_T[])VEMSSession.getField(subnet,"name");
		
		/*if(FENGHUO.multiLayerSubnetwork.MultiLayerSubnetwork_T.class.isInstance(subnet)){
			FENGHUO.multiLayerSubnetwork.MultiLayerSubnetwork_T me=(FENGHUO.multiLayerSubnetwork.MultiLayerSubnetwork_T)subnet;
			return me.name;
		} else if(HW.multiLayerSubnetwork.MultiLayerSubnetwork_T.class.isInstance(subnet)){
			HW.multiLayerSubnetwork.MultiLayerSubnetwork_T me=(HW.multiLayerSubnetwork.MultiLayerSubnetwork_T)subnet;
			return me.name;
		} else if(ZTE_U31.multiLayerSubnetwork.MultiLayerSubnetwork_T.class.isInstance(subnet)){
			ZTE_U31.multiLayerSubnetwork.MultiLayerSubnetwork_T me=(ZTE_U31.multiLayerSubnetwork.MultiLayerSubnetwork_T)subnet;
			return me.name;
		} else if(LUCENT.multiLayerSubnetwork.MultiLayerSubnetwork_T.class.isInstance(subnet)){
			LUCENT.multiLayerSubnetwork.MultiLayerSubnetwork_T me=(LUCENT.multiLayerSubnetwork.MultiLayerSubnetwork_T)subnet;
			return me.name;
		}else if(ALU.multiLayerSubnetwork.MultiLayerSubnetwork_T.class.isInstance(subnet)){
			ALU.multiLayerSubnetwork.MultiLayerSubnetwork_T me=(ALU.multiLayerSubnetwork.MultiLayerSubnetwork_T)subnet;
			return me.name;
		}
		return null;*/
	}
	
	public static NameAndStringValue_T[] getSncName(Object snc){
		return (NameAndStringValue_T[])VEMSSession.getField(snc,"name");
		
	}
	public static short[] getMeSupportedRates(Object ne){
		Object object=VEMSSession.getField(ne,"supportedRates");
		if(object==null)
			object=VEMSSession.getField(ne,"connectionRates");
		if(object==null)
			object=new short[]{};
		return (short[])object;
		
		/*if(FENGHUO.managedElement.ManagedElement_T.class.isInstance(ne)){
			FENGHUO.managedElement.ManagedElement_T me=(FENGHUO.managedElement.ManagedElement_T)ne;
			return me.supportedRates;
		} else if(HW.managedElement.ManagedElement_T.class.isInstance(ne)){
			HW.managedElement.ManagedElement_T me=(HW.managedElement.ManagedElement_T)ne;
			return me.supportedRates;
		} else if(ZTE_U31.managedElement.ManagedElement_T.class.isInstance(ne)){
			ZTE_U31.managedElement.ManagedElement_T me=(ZTE_U31.managedElement.ManagedElement_T)ne;
			return me.connectionRates;
		} else if(LUCENT.managedElement.ManagedElement_T.class.isInstance(ne)){
			LUCENT.managedElement.ManagedElement_T me=(LUCENT.managedElement.ManagedElement_T)ne;
			return me.supportedRates;
		}else if(ALU.managedElement.ManagedElement_T.class.isInstance(ne)){
			ALU.managedElement.ManagedElement_T me=(ALU.managedElement.ManagedElement_T)ne;
			return me.supportedRates;
		}
		return new short[]{};*/
	}
	public static String getMeNativeEmsName(Object ne){
		return (String)VEMSSession.getField(ne,"nativeEMSName");
		
		/*if(FENGHUO.managedElement.ManagedElement_T.class.isInstance(ne)){
			FENGHUO.managedElement.ManagedElement_T me=(FENGHUO.managedElement.ManagedElement_T)ne;
			return me.nativeEMSName;
		} else if(HW.managedElement.ManagedElement_T.class.isInstance(ne)){
			HW.managedElement.ManagedElement_T me=(HW.managedElement.ManagedElement_T)ne;
			return me.nativeEMSName;
		} else if(ZTE_U31.managedElement.ManagedElement_T.class.isInstance(ne)){
			ZTE_U31.managedElement.ManagedElement_T me=(ZTE_U31.managedElement.ManagedElement_T)ne;
			return me.nativeEMSName;
		} else if(LUCENT.managedElement.ManagedElement_T.class.isInstance(ne)){
			LUCENT.managedElement.ManagedElement_T me=(LUCENT.managedElement.ManagedElement_T)ne;
			return me.nativeEMSName;
		}else if(ALU.managedElement.ManagedElement_T.class.isInstance(ne)){
			ALU.managedElement.ManagedElement_T me=(ALU.managedElement.ManagedElement_T)ne;
			return me.nativeEMSName;
		}
		return null;*/
	}
	public static NameAndStringValue_T[] getEquipName(Object equip){
		int discriminator=(Integer)VEMSSession.getField(equip,"__discriminator.__value");
		if(HW.equipment.EquipmentTypeQualifier_T._EQT==discriminator){
			return (NameAndStringValue_T[])VEMSSession.getField(equip,"___equip.name");
		}else{
			return (NameAndStringValue_T[])VEMSSession.getField(equip,"___holder.name");
		}
		
		/*if(FENGHUO.equipment.EquipmentOrHolder_T.class.isInstance(equip)){
			FENGHUO.equipment.EquipmentOrHolder_T item=(FENGHUO.equipment.EquipmentOrHolder_T)equip;
			if(FENGHUO.equipment.EquipmentTypeQualifier_T._EQT==item.discriminator().value()){
				return item.equip().name;
			}else{
				return item.holder().name;
			}
		} else if(HW.equipment.EquipmentOrHolder_T.class.isInstance(equip)){
			HW.equipment.EquipmentOrHolder_T item=(HW.equipment.EquipmentOrHolder_T)equip;
			if(HW.equipment.EquipmentTypeQualifier_T._EQT==item.discriminator().value()){
				return item.equip().name;
			}else{
				return item.holder().name;
			}
		} else if(ZTE_U31.equipment.EquipmentOrHolder_T.class.isInstance(equip)){
			ZTE_U31.equipment.EquipmentOrHolder_T item=(ZTE_U31.equipment.EquipmentOrHolder_T)equip;
			if(ZTE_U31.equipment.EquipmentTypeQualifier_T._EQT==item.discriminator().value()){
				return item.equip().name;
			}else{
				return item.holder().name;
			}
		} else if(LUCENT.equipment.EquipmentOrHolder_T.class.isInstance(equip)){
			LUCENT.equipment.EquipmentOrHolder_T item=(LUCENT.equipment.EquipmentOrHolder_T)equip;
			if(LUCENT.equipment.EquipmentTypeQualifier_T._EQT==item.discriminator().value()){
				return item.equip().name;
			}else{
				return item.holder().name;
			}
		}else if(ALU.equipment.EquipmentOrHolder_T.class.isInstance(equip)){
			ALU.equipment.EquipmentOrHolder_T item=(ALU.equipment.EquipmentOrHolder_T)equip;
			if(ALU.equipment.EquipmentTypeQualifier_T._EQT==item.discriminator().value()){
				return item.equip().name;
			}else{
				return item.holder().name;
			}
		}
		return null;*/
	}
	public static NameAndStringValue_T[] getPtpName(Object ptp){
		return (NameAndStringValue_T[])VEMSSession.getField(ptp,"name");
		
		/*if(FENGHUO.terminationPoint.TerminationPoint_T.class.isInstance(ptp)){
			FENGHUO.terminationPoint.TerminationPoint_T item=(FENGHUO.terminationPoint.TerminationPoint_T)ptp;
			return item.name;
		} else if(HW.terminationPoint.TerminationPoint_T.class.isInstance(ptp)){
			HW.terminationPoint.TerminationPoint_T item=(HW.terminationPoint.TerminationPoint_T)ptp;
			return item.name;
		} else if(ZTE_U31.terminationPoint.TerminationPoint_T.class.isInstance(ptp)){
			ZTE_U31.terminationPoint.TerminationPoint_T item=(ZTE_U31.terminationPoint.TerminationPoint_T)ptp;
			return item.name;
		} else if(LUCENT.terminationPoint.TerminationPoint_T.class.isInstance(ptp)){
			LUCENT.terminationPoint.TerminationPoint_T item=(LUCENT.terminationPoint.TerminationPoint_T)ptp;
			return item.name;
		} else if(ALU.terminationPoint.TerminationPoint_T.class.isInstance(ptp)){
			ALU.terminationPoint.TerminationPoint_T item=(ALU.terminationPoint.TerminationPoint_T)ptp;
			return item.name;
		}
		return null;*/
	}
	public static NameAndStringValue_T[] getPgpName(Object pgp){
		return (NameAndStringValue_T[])VEMSSession.getField(pgp,"name");
		
		/*if(FENGHUO.protection.ProtectionGroup_T.class.isInstance(pgp)){
			FENGHUO.protection.ProtectionGroup_T item=(FENGHUO.protection.ProtectionGroup_T)pgp;
			return item.name;
		} else if(HW.protection.ProtectionGroup_T.class.isInstance(pgp)){
			HW.protection.ProtectionGroup_T item=(HW.protection.ProtectionGroup_T)pgp;
			return item.name;
		} else if(ZTE_U31.protection.ProtectionGroup_T.class.isInstance(pgp)){
			ZTE_U31.protection.ProtectionGroup_T item=(ZTE_U31.protection.ProtectionGroup_T)pgp;
			return item.name;
		} else if(LUCENT.protection.ProtectionGroup_T.class.isInstance(pgp)){
			LUCENT.protection.ProtectionGroup_T item=(LUCENT.protection.ProtectionGroup_T)pgp;
			return item.name;
		} else if(ALU.protection.ProtectionGroup_T.class.isInstance(pgp)){
			ALU.protection.ProtectionGroup_T item=(ALU.protection.ProtectionGroup_T)pgp;
			return item.name;
		}
		return null;*/
	}
	public static NameAndStringValue_T[] getEpgpName(Object epgp){
		return (NameAndStringValue_T[])VEMSSession.getField(epgp,"name");
				
		/*if(FENGHUO.protection.EProtectionGroup_T.class.isInstance(epgp)){
			FENGHUO.protection.EProtectionGroup_T item=(FENGHUO.protection.EProtectionGroup_T)epgp;
			return item.name;
		} else if(HW.protection.EProtectionGroup_T.class.isInstance(epgp)){
			HW.protection.EProtectionGroup_T item=(HW.protection.EProtectionGroup_T)epgp;
			return item.name;
		} else if(ZTE_U31.protection.EProtectionGroup_T.class.isInstance(epgp)){
			ZTE_U31.protection.EProtectionGroup_T item=(ZTE_U31.protection.EProtectionGroup_T)epgp;
			return item.name;
		} else if(LUCENT.protection.EProtectionGroup_T.class.isInstance(epgp)){
			LUCENT.protection.EProtectionGroup_T item=(LUCENT.protection.EProtectionGroup_T)epgp;
			return item.name;
		}
		else if(ALU.protection.EProtectionGroup_T.class.isInstance(epgp)){
			ALU.protection.EProtectionGroup_T item=(ALU.protection.EProtectionGroup_T)epgp;
			return item.name;
		}
		return null;*/
	}
	public static String getFileDirOfNe(Object ne){

		if(DirByProductName){
			String productName = getProductName(ne);
			StringBuilder tempString = new StringBuilder();
			tempString.append(productName);
			tempString.append(File.separator);
			//tempString.append(getPathByName(ne.name));
			return tempString.toString();
		}else{
			NameAndStringValue_T[] meName = getMeName(ne);
			return VEMSSession.getRelationPathByName(meName);
		}
		
	}
	
	public static String getOnlyValueByName(NameAndStringValue_T[] name){
		StringBuilder tempString = new StringBuilder();
		tempString.append(name[name.length-1].value.replaceAll("/", ";"));
		return tempString.toString();
	}

	public static String getValueByName(NameAndStringValue_T[] name){
		StringBuilder tempString = new StringBuilder();
		tempString.append(name[name.length-1].name);
		tempString.append("_");
		tempString.append(name[name.length-1].value.replaceAll("/", ";"));
		return tempString.toString();
	}
	public static String getNoValueByName(NameAndStringValue_T[] name){
		StringBuilder tempString = new StringBuilder();
		tempString.append(name[name.length-1].name);
		tempString.append(name[name.length-1].value.replaceAll("/[a-z]*=", "-").replaceAll("/", ";"));
		return tempString.toString();
	}
}
