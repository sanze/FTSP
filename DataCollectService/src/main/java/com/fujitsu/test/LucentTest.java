package com.fujitsu.test;

import java.util.Map;

import com.fujitsu.IService.IDataCollectService;
import com.fujitsu.IService.IEMSSession;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.DataCollectMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.manager.dataCollectManager.service.EMSCollectService;
import com.fujitsu.util.BeanUtil;

public class LucentTest{

	private static IDataCollectService dataCollectService;
	private static DataCollectMapper dataCollectMapper;

	public static void main(String[] args) throws CommonException{
		
		dataCollectMapper = (DataCollectMapper) BeanUtil.getBean("dataCollectMapper");
		dataCollectService = (IDataCollectService) BeanUtil.getBean("dataCollectService");
		
		Map paramter = dataCollectMapper.selectTableById("T_BASE_EMS_CONNECTION", "BASE_EMS_CONNECTION_ID", 5);
		
		try{
			
			dataCollectService.startCorbaConnect(paramter);
			
			for (int i = 0; i < 100; i++) {
				
				IEMSSession emsSession = EMSCollectService.sessionMap.get("192.5.1.16");
				
				Object xxx = emsSession.getEmsSession();

				ZTE_U31.emsSession.EmsSession_I session = (ZTE_U31.emsSession.EmsSession_I) xxx;
					
				session.ping();
				
				Thread.sleep(3*1000);
				
				System.out.println("测试连接第" + i + "次");
			}
//			//syncEmsInfo
//			dataCollectService.syncEmsInfo(paramter, 1);
//			//syncNeEquipmentOrHolder
//			dataCollectService.syncNeEquipmentOrHolder(paramter, 1102, 1);
//			//syncNePtp
//			dataCollectService.syncNePtp(paramter, 1102, 1);
//			//syncNeCtp
//			dataCollectService.syncNeCtp(paramter, 1102, 1);
//			//syncNeProtectionGroup
//			dataCollectService.syncNeProtectionGroup(paramter, 1102, 1);

		}catch (Exception e){
			e.printStackTrace();
			ExceptionHandler.handleException(e);
		}finally{
			dataCollectService.disCorbaConnect(paramter);
		}
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
