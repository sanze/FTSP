package com.fujitsu.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fujitsu.IService.IDataCollectService;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.DataCollectMapper;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmMeasurementModel;
import com.fujitsu.util.BeanUtil;

public class Test{

	private static IDataCollectService dataCollectService;
	private static DataCollectMapper dataCollectMapper;

	public static void main(String[] args) throws CommonException{
		
		dataCollectMapper = (DataCollectMapper) BeanUtil.getBean("dataCollectMapper");
		dataCollectService = (IDataCollectService) BeanUtil.getBean("dataCollectService");
//		
//		for(int i = 0;i<10;i++){
//			Callable<Object> thread = new Callable<Object>() {
//				public Object call() throws CommonException {
//					
//					System.out.println("开始同步！"+new Date());
//
//					Map paramter = dataCollectMapper.selectTableById("T_BASE_EMS_CONNECTION", "BASE_EMS_CONNECTION_ID", 3);
//					
//					LinkAlterResultModel data = dataCollectService.getLinkAlterList(paramter, 1);
//					
////					dataCollectService.syncLink(paramter, data.getChangeList(), 1);
//					
//					System.out.println("同步完成！"+new Date());
//
//					return null;
//					
//				}
//			};
//			// 添加采集进程
//			FutureTask<Object> future = new FutureTask<Object>(thread);
//			// 执行采集进程
//			ExecutorService executorService = Executors.newFixedThreadPool(10);
//			
//			executorService.submit(future);
//		}
		
//		for(int i=0;i<10;i++){
//			Map paramter = dataCollectMapper.selectTableById("T_BASE_EMS_CONNECTION", "BASE_EMS_CONNECTION_ID", 3);
//			
//			System.out.println(paramter.hashCode());
//		}
		
		Map paramter = dataCollectMapper.selectTableById("T_BASE_EMS_CONNECTION", "BASE_EMS_CONNECTION_ID", 3);
		
		List<Integer> ptpIdList = new ArrayList<Integer>();
		ptpIdList.add(60103);
		List<PmDataModel> pmDataList = dataCollectService.getCurrentPmData_PtpList(paramter, ptpIdList, new short[] {}, new int[] {
				1,3}, new int[] { 1 }, false, true, false,
				1);
		
		for(PmDataModel model:pmDataList){
			
			System.out.println("网元ID："+model.getNeId());
			System.out.println("端口ID："+model.getPtpId());
			
			for(PmMeasurementModel xxx:model.getPmMeasurementList()){
				System.out.println("原始性能名："+xxx.getPmParameterName());
				System.out.println("标准性能名："+xxx.getPmStdIndex());
			}
		}
		
		
//		ALU.emsSession.EmsSession_I emsSession = null;
//		// ior解析
//		// 创建一个ORB实例
//		ORB orb = ORB.init(args, null);
//
//		// 创建NmsSession_IPOA，新建HWNmsSessionImpl类
//		ALUNmsSessionImpl nmsSession = new ALUNmsSessionImpl("xxxx");
//		// 创建emsSessionHolder
//		ALU.emsSession.EmsSession_IHolder emsSessionHolder = new ALU.emsSession.EmsSession_IHolder();
//		// ior文件内容
//		org.omg.CORBA.Object obj = orb
//				.string_to_object("IOR:000000000000003F49444C3A6D746E6D2E746D666F72756D2E6F72672F656D7353657373696F6E466163746F72792F456D7353657373696F6E466163746F72795F493A312E300000000000010000000000000064000102000000000B31302E31302E32342E31000014AE00000000000E53657373696F6E466163746F72790000000000020000000000000008000000004A414300000000010000001C00000000000100010000000105010001000101090000000105010001");
//
//		try {
//			ALU.emsSessionFactory.EmsSessionFactory_I emsSessionFactory = ALU.emsSessionFactory.EmsSessionFactory_IHelper
//					.narrow(obj);
//			// 填用户名，密码
//			emsSessionFactory.getEmsSession("userName", "password",
//					nmsSession._this(), emsSessionHolder);
//			// 获取emsSession
//			emsSession = emsSessionHolder.value;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		/* 使用emsSession获取数据 */
//		ALU.emsSession.EmsSession_IPackage.managerNames_THolder holder = new ALU.emsSession.EmsSession_IPackage.managerNames_THolder();
//
//		try {
//			emsSession.getSupportedManagers(holder);
//		} catch (ProcessingFailureException e) {
//			e.printStackTrace();
//		}
//		for (String xxx : holder.value) {
//			System.out.println(xxx);
//		}
       
        
       
//		IHWEMSSession emsSession = null;
//		IZTEEMSSession emsSessionU31 = null;
//		ILUCENTEMSSession emsSessionLucent = null;
//			startUp();
//		dataCollectMapper = (DataCollectMapper) BeanUtil.getBean("dataCollectMapper");
		
//		Map<String, Object> shieldMap = new HashMap<String, Object>();
//		
//		shieldMap.put("status", DataCollectDefine.ALARM_SHIELD_STATUS_ENABLE);
//		
//		List<Map<String, Object>> shieldListByNULL = dataCollectMapper.judgeIsNeedShieldByNULL(shieldMap);
//		
//		StringBuffer strBuild = new StringBuffer();
//		List<String> shieldByNULL = new ArrayList<String>();
//		for (Map<String,Object> item : shieldListByNULL) {
//			strBuild.setLength(0);
//			strBuild.append(item.get("FACTORY")).append(",");
//			strBuild.append(item.get("NATIVE_PROBABLE_CAUSE")).append(",");
//			strBuild.append(item.get("ALARM_TYPE")).append(",");
//			strBuild.append(item.get("ALARM_LEVEL")).append(",");
//			strBuild.append(item.get("ALARM_AFFECTING"));
//			shieldByNULL.add(strBuild.toString());
//		}
		
//		Map paramter = dataCollectMapper.selectTableById("T_BASE_EMS_CONNECTION", "BASE_EMS_CONNECTION_ID", 3);
//		try{
//			System.out.println("启动采集服务开始。。。");
			//修改log4j配置文件地址
//			PropertyConfigurator.configure(ClassLoader.getSystemResource("resourceConfig/logConfig/log4j.properties"));
//			new Stdout2Log4j();
//			List<Map> neNameList = new ArrayList<Map>();
//			List<Map> neList = dataCollectMapper.selectTableListById("T_BASE_NE", "BASE_EMS_CONNECTION_ID", 4);
//			for(Map ne:neList){
//				int neId = Integer.valueOf(ne.get("BASE_NE_ID").toString());
//				List<Map> shelfList = dataCollectMapper.selectTableListById("T_BASE_SHELF", "BASE_NE_ID", neId);
//				if(shelfList.size() == 0){
//					neNameList.add(ne);
////					System.out.println(ne.get("NAME")+"--"+ne.get("DISPLAY_NAME"));
//				}
//			}
			
//			List<Map> neList = dataCollectMapper.selectTableListById("T_BASE_NE", "BASE_EMS_CONNECTION_ID", 4);
//			List<String> fileNameList = new ArrayList<String>();
//			
//			File file = new File("D:\\WorkSpace10.1\\VEMS\\HW\\134.244.5.120");
//			File xx[] = file.listFiles();
//			
//			for(File subFile:xx){
//				if(subFile.isDirectory()&&subFile.getName().contains("ManagedElement_")){
//					fileNameList.clear();
//					boolean needToPrint = true;
//					for(String name:subFile.list()){
//						if(name.contains("PTP_;rack=1")){
//							needToPrint = false;
//							break;
//						}
//					}
//					if(needToPrint){
//						System.out.print(subFile.getName().split("_")[1]+", ");
//					}
////					if(!fileNameList.contains("PTP_;rack=1")){
////						System.out.print(subFile.getName().split("_")[1]+", ");
////					}
////					if(subFile.list().length<15){
////						fileNameList.add(subFile.getName().split("_")[1]);
////						System.out.print(subFile.getName().split("_")[1]+", ");
////					}
//				}else{
//					continue;
//				}
//			}
			
//			for(Map unSyncNe:neNameList){
//				if(!fileNameList.contains(unSyncNe.get("NAME").toString())){
//					System.out.println(unSyncNe.get("NAME")+"--"+unSyncNe.get("DISPLAY_NAME"));
//				}
//			}
//			
//			for(Map ne:neList){
//				String neName = "ManagedElement_"+ne.get("NAME");
//				if(!fileNameList.contains(neName)){
//					System.out.println(ne.get("NAME")+"--"+ne.get("DISPLAY_NAME"));
//				}
//			}
//			//华为OTN
//			String corbaName = "njfst1";
//			String corbaPassword = "njfst1";
//			String corbaIp = "133.37.155.131";
//			String corbaPort = "12001";
//			String emsName = "Huawei/U2000";
//			String encode = "GBK";
//			
//			emsSession = new HWVEMSSession(corbaName, corbaPassword, corbaIp,
//					corbaPort, emsName, encode);
//			
//			emsSession = new HWEMSSession(corbaName, corbaPassword, corbaIp,
//					corbaPort, emsName,encode);
//			
//			emsSession.connect();
			
//			//中兴C网
//			String corbaName = "corba";
//			String corbaPassword = "111111";
//			String corbaIp = "192.1.1.16";
//			String corbaPort = "6001";
//			String emsName = "ZTE/E300";
//			String encode = "GBK";
//			
//			emsSessionU31 = new ZTEU31EMSSession(corbaName, corbaPassword, corbaIp,
//					corbaPort, emsName,encode);
//			
//			emsSessionU31.connect();
			
//			//朗讯
//			String corbaName = "corba";
//			String corbaPassword = "oms+1234";
//			String corbaIp = "192.168.22.1";
//			String corbaPort = "55075";
//			String emsName = "Lucent/lqoms";
//			String encode = "GBK";
//			
//			emsSessionLucent = new LUCENTEMSSession(corbaName, corbaPassword, corbaIp,
//					corbaPort, emsName,encode);
//			
//			emsSessionLucent.connect();
//			
//			System.out.println("启动采集服务成功。。。");
//			
//			Thread.sleep(1000*60*2);

//			dataCollectService = (IDataCollectService) BeanUtil.getBean("dataCollectService");
//			
//			dataCollectService.getLinkAlterList(paramter, 1);
			
//			dataCollectService = (IDataCollectService) BeanUtil.getBean("hwDataCollectService");
			
			//已测试
//			dataCollectService.syncEmsInfo(paramter,0);
			
//			int xx = dataCollectService.startCorbaConnect(paramter);
			
			//已测试
//			dataCollectService.syncNeList(paramter,0);
			//
//			LinkAlterResultModel model = dataCollectService.getLinkAlterList(0);
//			System.out.println("isChanged:"+model.isChanged());
//			System.out.println("isNeedSyncNe:"+model.isNeedSyncNe());
//			System.out.println("changeSize:"+model.getChangeList().size());
			//已测试
//			dataCollectService.syncLink(paramter,0);
			//已测试
//			dataCollectService.syncNeEquipmentOrHolder(paramter,4, 0);

			//mstp ptp_type归类不全 层速率47，49无对应信息
//			dataCollectService.syncNePtp(paramter,4, 0);
			
			//已测试
//			dataCollectService.syncNeCtp(paramter,4, 0);
			
			//内部link已测试
//			dataCollectService.syncNeInternalLink(1, 0);
			
			//部分信息需确认 
//			dataCollectService.syncNeClock(56, 0);
			
			//[ERROR]：(1092091919) 发送asn请求失败
//			dataCollectService.syncNeEProtectionGroup(56, 0);
			
			//未测试 无数据
//			dataCollectService.syncNeProtectionGroup(4, 0);
			
			//未测试 无数据
//			dataCollectService.syncNeWDMProtectionGroup(56, 0);
			
//			Map ne = dataCollectMapper.selectTableById("T_BASE_NE", "BASE_NE_ID", 4);
//			short[] layerRate = constructLayRates(ne.get("SUPORT_RATES").toString());
//			//已测试
//			dataCollectService.syncNeCRS(paramter,4,layerRate,0);

			//未测试 无数据
//			dataCollectService.syncNeEthService(1, 0);
			
			//未测试 无数据
//			dataCollectService.syncNeBindingPath(1, 0);
			

			//简单测试，附加信息未添加
//			List<AlarmDataModel> alarms = dataCollectService.getAllEMSAndMEActiveAlarms(null, null, 0);
//			for(AlarmDataModel model:alarms){
//				System.out.println(model.getNameString());
//			}
			//简单测试，附加信息未添加
//			List<AlarmDataModel> alarms = dataCollectService.getAllActiveAlarms(847, null, 0);
//			for(AlarmDataModel model:alarms){
//				System.out.println(model.getNameString());
//			}
			
			//简单测试，附加信息未添加
//			List<PmDataModel> pmdatas = dataCollectService.getCurrentPmData_Ne(847, new short[]{}, new int[]{}, new int[]{}, false, false, false, 0);
//			
//			System.out.println(pmdatas.size());
			
//			List<Integer> ptpIdList = new ArrayList<Integer>();
//			
//			ptpIdList.add(4797);
//			ptpIdList.add(4815);
//			ptpIdList.add(4816);
//			
//			List<PmDataModel> pmdatas = dataCollectService.getCurrentPmData_PtpList(ptpIdList, new short[]{}, new int[]{}, new int[]{}, false, false, false, 0);
//			
//			System.out.println(pmdatas.size());
			
			//简单测试，附加信息未添加
//			List<PmDataModel> pmdatas = dataCollectService.getHistoryPmData_Ne(1, "20131224010101", new short[]{}, new int[]{}, new int[]{}, true, true, true, 0);
//			
//			System.out.println(pmdatas.size());
			
//			dataCollectService.syncAllEMSAndMEActiveAlarms(new int[]{}, new int[]{}, 1);
			
//			paramter = dataCollectMapper.selectTableById("T_BASE_EMS_CONNECTION", "BASE_EMS_CONNECTION_ID", 8);
			
			
//			List<PmDataModel> data = dataCollectService.getCurrentPmData_Ne(paramter, 2, new short[]{}, new int[]{1,2,3,4,5}, new int[]{1}, true, true, true, 1);
//			System.out.println("吆西，网元数据采集成功，采集数量为："+data.size());
//			List<Integer> ptpIdList = new ArrayList<Integer>();
//			ptpIdList.add(73874);
//			ptpIdList.add(73875);
//			data = dataCollectService.getCurrentPmData_PtpList(paramter, ptpIdList, new short[]{}, new int[]{1,2,3,4,5}, new int[]{1}, true, true, true, 1);
//			System.out.println("吆西，采集成功，采集数量为："+data.size());
//		}catch (Exception e){
//			ExceptionHandler.handleException(e);
//		}finally{
//			emsSession.endSession();
//			dataCollectService.disCorbaConnect(paramter);
//		}
    }
	
	public static short[] constructLayRates(String layRatesString) {
		String[] composite = layRatesString.split(":");
		short[] layRate;
		if(composite.length>0){
			layRate = new short[composite.length];
			for (int i = 0; i < composite.length; i++) {
				layRate[i] = Short.valueOf(composite[i]);
			}
		}else{
			layRate = new short[]{};
		}
		return layRate;
	}
}
