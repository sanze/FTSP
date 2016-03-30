package com.fujitsu.manager.dataCollectManager.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fujitsu.IService.IDataCollectService;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.model.LinkAlterModel;
import com.fujitsu.model.LinkAlterResultModel;

public abstract class DataCollectService implements IDataCollectService{
	
	private static List<String> EXCUTE_POOL = new ArrayList<String>();
	
	protected static final int GET_EMS = 0;

	protected static final int GET_ALL_MANAGED_ELEMENT = 1;

	protected static final int GET_ALL_EQUIPMENT = 2;

	protected static final int GET_ALL_PTPS = 3;

	protected static final int GET_CONTAINED_POTENTIAL_TPS = 4;

	protected static final int GET_ALL_TOP_LEVEL_TOPOLOGICAL_LINKS = 5;

	protected static final int GET_ALL_MSTP_END_POINTS = 6;

	protected static final int GET_ALL_E_PROTECTION_GROUPS = 7;

	protected static final int GET_ALL_PROTECTION_GROUPS = 8;

	protected static final int GET_ALL_WDM_PROTECTION_GROUPS = 9;

	protected static final int GET_CLOCK_SOURCE_STATUS = 10;

	protected static final int GET_ALL_TOPOLOGICAL_LINKS = 11;

	protected static final int GET_ALL_INTERNAL_TOPOLOGICAL_LINKS = 12;

	protected static final int GET_ALL_EMS_AND_ME_ACTIVE_ALARMS = 13;

	protected static final int GET_ALL_ACTIVE_ALARMS = 14;

	protected static final int GET_ALL_CURRENT_PM = 15;

	protected static final int GET_ALL_CURRENT_PM_PTPLIST = 16;

	protected static final int GET_ALL_HISTORY_PM = 17;

	protected static final int GET_ALL_ETH_SERVICE = 18;

	protected static final int GET_CRS = 19;

	protected static final int GET_ALL_BINDING_PATH = 20;
	
	protected static final int GET_ALL_VBS = 21;
	
	protected static final int GET_ALL_SNC = 22;
	
	protected static final int GET_ALL_ROUTE = 23;
	
	protected static final int GET_ALL_SNC_FROM_ROUTE = 24;
	
	protected static final int GET_ALL_FLOW_DOMAINS = 25;
	
	protected static final int GET_ALL_FDFRS = 26;
	
	protected static final int GET_ALL_LINK_OF_FDFRS = 27;
	
	protected static final String PARAM_PTP_NAME_LIST = "ptpNameList";
	protected static final String PARAM_LAYER_RATE = "layerRateList";
	protected static final String PARAM_PM_LOCATION = "pmLocationList";
	protected static final String PARAM_GRANULARITY = "granularityList";
	protected static final String PARAM_DISPLAY_NAME = "displayName";
	protected static final String PARAM_START_TIME = "startTime";
	protected static final String PARAM_END_TIME = "endTime";
	protected static final String PARAM_FTP_IP = DataCollectDefine.FTP_IP;
	protected static final String PARAM_FTP_PORT = DataCollectDefine.FTP_PORT;
	protected static final String PARAM_FTP_USERNAME = DataCollectDefine.FTP_USER_NAME;
	protected static final String PARAM_FTP_PASSWORD = DataCollectDefine.FTP_PASSWORD;
	protected static final String PARAM_PTN_SYSTEM_NAME = "PTN_SYS_NAME";
	protected static final String PARAM_PTN_SYSTEM_ID = "PTN_SYS_ID";
	protected static final String PARAM_PTN_DATE = "PTN_DATE";
	
	protected static final String PARAM_NEED_SORT = "needSort";
	
	protected static final String UNUSED_POWER_VALUE = "-60.00";
	protected static final int INVALID_POWER_VALUE_THRESHOLD = 100;
	protected static final int PM_ORI_TABLE_IS_EXISTENT = 1;
	protected static final int HOURS_BEFORE_COLLECT_TIME = 2;
	protected static final int HOURS_AFTER_COLLECT_TIME = 3;
	protected static final String OPTICAL_POWER_NUM_FORMAT = "#0.00";
	protected static final String WAVELENGTH_NUM_FORMAT = "###0.00";
	protected static final String SNR_NUM_FORMAT = "#0.0";
	protected static final String COUNT_VALUE_NUM_FORMAT = "#0";
	protected static final String FEC_NUM_FORMAT = "0.00E0";
	protected static final int CHANNEL_WAVE_LENGTH_MIN = 1300;
	protected static final int CHANNEL_WAVE_LENGTH_MAX = 1600;
	protected static final int SNR_MIN = 0;
	protected static final int SNR_MAX = 100;
	protected static final String INVALID_PM_VALUE = "超出标准值";
	protected static final int TEMPERATURE_MIN = -200;
	protected static final int TEMPERATURE_MAX = 200;
	
	protected static final List<Integer> checkList =  new ArrayList<Integer>() {
		{add(GET_ALL_EMS_AND_ME_ACTIVE_ALARMS);}
		{add(GET_ALL_ACTIVE_ALARMS);}
		{add(GET_ALL_CURRENT_PM);}
		{add(GET_ALL_CURRENT_PM_PTPLIST);}
		{add(GET_ALL_HISTORY_PM);}
	};
	
	// 通道性能参数名列表
	protected static final List<String> pathPmList = new ArrayList<String>() {
		{ add("VC4_BBE"); }
		{ add("VC4_ES"); }
		{ add("VC4_SES"); }
		{ add("VC4_CSES"); }
		{ add("VC4_UAS"); }
		{ add("VC3_BBE"); }
		{ add("VC3_ES"); }
		{ add("VC3_SES"); }
		{ add("VC3_CSES"); }
		{ add("VC3_UAS"); }
		{ add("VC12_BBE"); }
		{ add("VC12_ES"); }
		{ add("VC12_SES"); }
		{ add("VC12_CSES"); }
		{ add("VC12_UAS"); }
		{ add("E4_BBE"); }
		{ add("E4_ES"); }
		{ add("E4_SES"); }
		{ add("E4_UAS"); }
		{ add("E3_BBE"); }
		{ add("E3_ES"); }
		{ add("E3_SES"); }
		{ add("E3_UAS"); }
		{ add("E1_BBE"); }
		{ add("E1_ES"); }
		{ add("E1_SES"); }
		{ add("E1_UAS"); }
		{ add("OTU_BBE"); }
		{ add("OTU_ES"); }
		{ add("OTU_SES"); }
		{ add("OTU_UAS"); }
		{ add("OTU1_BBE"); }
		{ add("OTU1_ES"); }
		{ add("OTU1_SES"); }
		{ add("OTU1_UAS"); }
		{ add("OTU2_BBE"); }
		{ add("OTU2_ES"); }
		{ add("OTU2_SES"); }
		{ add("OTU2_UAS"); }
		{ add("OTU3_BBE"); }
		{ add("OTU3_ES"); }
		{ add("OTU3_SES"); }
		{ add("OTU3_UAS"); }
		{ add("OTU5G_BBE"); }
		{ add("OTU5G_ES"); }
		{ add("OTU5G_SES"); }
		{ add("OTU5G_UAS"); }
		{ add("ODU_BBE"); }
		{ add("ODU_ES"); }
		{ add("ODU_SES"); }
		{ add("ODU_UAS"); }
		{ add("ODU1_BBE"); }
		{ add("ODU1_ES"); }
		{ add("ODU1_SES"); }
		{ add("ODU1_UAS"); }
		{ add("ODU2_BBE"); }
		{ add("ODU2_ES"); }
		{ add("ODU2_SES"); }
		{ add("ODU2_UAS"); }
		{ add("ODU3_BBE"); }
		{ add("ODU3_ES"); }
		{ add("ODU3_SES"); }
		{ add("ODU3_UAS"); }
		{ add("ODU5G_BBE"); }
		{ add("ODU5G_ES"); }
		{ add("ODU5G_SES"); }
		{ add("ODU5G_UAS"); }
		{ add("DSR_CV"); }
		{ add("DSR_ES"); }
		{ add("DSR_SES"); }
		{ add("DSR_CSES"); }
		{ add("DSR_UAS"); }
		{ add("DSR_OFS"); }
	};
	
	// 上下限值在基准表中的性能参数名列表
	protected static final List<String> limitValInCompareList = new ArrayList<String>() {
		{ add("ENV_TMP_MAX"); }
		{ add("ENV_TMP_MIN"); }
		{ add("ENV_TMP_CUR"); }
		{ add("OPT_LTEMP_MAX"); }
		{ add("OPT_LTEMP_MIN"); }
		{ add("OPT_LTEMP_CUR"); }
	};

	@Override
	public void syncNeCRS(Map paramter, int neId, short[] layerRateList,
			int commandLevel) throws CommonException {
		//生成key
		String key = generatorKey(paramter, neId, GET_CRS);
		try{
			//检查是否可以执行
			while(!isOperateCanExcute(key)){
				try {
					Thread.sleep(3*1000);
				} catch (InterruptedException e) {
					throw new CommonException(e,
							MessageCodeDefine.CORBA_RUNTIME_EXCEPTION); 
				}
			}
			//真实方法
			syncNeCRSImpl(paramter,neId,layerRateList,commandLevel);
		}catch(Exception e){
			ExceptionHandler.handleException(e);
			if(CommonException.class.isInstance(e)){
				//直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
				throw new CommonException(new NullPointerException(),((CommonException)e).getErrorCode(),((CommonException)e).getErrorMessage());
			}else{
				throw new CommonException(new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999);
			}
		}finally{
			EXCUTE_POOL.remove(key);
		}
	}

	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IDataCollectService#syncNeList(java.util.Map, int)
	 */
	@Override
	public void syncNeList(Map paramter, int commandLevel)
			throws CommonException {
		//生成key
		String key = generatorKey(paramter, null, GET_ALL_MANAGED_ELEMENT);
		try{
			//检查是否可以执行
			while(!isOperateCanExcute(key)){
				try {
					Thread.sleep(3*1000);
				} catch (InterruptedException e) {
					throw new CommonException(e,
							MessageCodeDefine.CORBA_RUNTIME_EXCEPTION); 
				}
			}
			//真实方法
			syncNeListImpl(paramter,commandLevel);
		}catch(Exception e){
			ExceptionHandler.handleException(e);
			if(CommonException.class.isInstance(e)){
				//直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
				throw new CommonException(new NullPointerException(),((CommonException)e).getErrorCode(),((CommonException)e).getErrorMessage());
			}else{
				throw new CommonException(new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999);
			}
		}finally{
			EXCUTE_POOL.remove(key);
		}
	}

	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IDataCollectService#syncNeEquipmentOrHolder(java.util.Map, int, int)
	 */
	@Override
	public void syncNeEquipmentOrHolder(Map paramter, int neId, int commandLevel)
			throws CommonException {
		//生成key
		String key = generatorKey(paramter, neId, GET_ALL_EQUIPMENT);
		try{
			//检查是否可以执行
			while(!isOperateCanExcute(key)){
				try {
					Thread.sleep(3*1000);
				} catch (InterruptedException e) {
					throw new CommonException(e,
							MessageCodeDefine.CORBA_RUNTIME_EXCEPTION); 
				}
			}
			//真实方法
			syncNeEquipmentOrHolderImpl(paramter,neId,commandLevel);
		}catch(Exception e){
			ExceptionHandler.handleException(e);
			if(CommonException.class.isInstance(e)){
				//直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
				throw new CommonException(new NullPointerException(),((CommonException)e).getErrorCode(),((CommonException)e).getErrorMessage());
			}else{
				throw new CommonException(new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999);
			}
		}finally{
			EXCUTE_POOL.remove(key);
		}
	}



	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IDataCollectService#syncNePtp(java.util.Map, int, int)
	 */
	@Override
	public void syncNePtp(Map paramter, int neId, int commandLevel)
			throws CommonException {
		//生成key
		String key = generatorKey(paramter, neId, GET_ALL_PTPS);
		try{
			//检查是否可以执行
			while(!isOperateCanExcute(key)){
				try {
					Thread.sleep(3*1000);
				} catch (InterruptedException e) {
					throw new CommonException(e,
							MessageCodeDefine.CORBA_RUNTIME_EXCEPTION); 
				}
			}
			//真实方法
			syncNePtpImpl(paramter,neId,commandLevel);
		}catch(Exception e){
			ExceptionHandler.handleException(e);
			if(CommonException.class.isInstance(e)){
				//直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
				throw new CommonException(new NullPointerException(),((CommonException)e).getErrorCode(),((CommonException)e).getErrorMessage());
			}else{
				throw new CommonException(new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999);
			}
		}finally{
			EXCUTE_POOL.remove(key);
		}
		
	}

	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IDataCollectService#syncNeInternalLink(java.util.Map, int, int)
	 */
	@Override
	public void syncNeInternalLink(Map paramter, int neId, int commandLevel)
			throws CommonException {
		//生成key
		String key = generatorKey(paramter, neId, GET_ALL_INTERNAL_TOPOLOGICAL_LINKS);
		try{
			//检查是否可以执行
			while(!isOperateCanExcute(key)){
				try {
					Thread.sleep(3*1000);
				} catch (InterruptedException e) {
					throw new CommonException(e,
							MessageCodeDefine.CORBA_RUNTIME_EXCEPTION); 
				}
			}
			//真实方法
			syncNeInternalLinkImpl(paramter,neId,commandLevel);
		}catch(Exception e){
			ExceptionHandler.handleException(e);
			if(CommonException.class.isInstance(e)){
				//直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
				throw new CommonException(new NullPointerException(),((CommonException)e).getErrorCode(),((CommonException)e).getErrorMessage());
			}else{
				throw new CommonException(new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999);
			}
		}finally{
			EXCUTE_POOL.remove(key);
		}
	}



	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IDataCollectService#syncNeEProtectionGroup(java.util.Map, int, int)
	 */
	@Override
	public void syncNeEProtectionGroup(Map paramter, int neId, int commandLevel)
			throws CommonException {
		//生成key
		String key = generatorKey(paramter, neId, GET_ALL_E_PROTECTION_GROUPS);
		try{
			//检查是否可以执行
			while(!isOperateCanExcute(key)){
				try {
					Thread.sleep(3*1000);
				} catch (InterruptedException e) {
					throw new CommonException(e,
							MessageCodeDefine.CORBA_RUNTIME_EXCEPTION); 
				}
			}
			//真实方法
			syncNeEProtectionGroupImpl(paramter,neId,commandLevel);
		}catch(Exception e){
			ExceptionHandler.handleException(e);
			if(CommonException.class.isInstance(e)){
				//直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
				throw new CommonException(new NullPointerException(),((CommonException)e).getErrorCode(),((CommonException)e).getErrorMessage());
			}else{
				throw new CommonException(new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999);
			}
		}finally{
			EXCUTE_POOL.remove(key);
		}
	}



	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IDataCollectService#syncNeProtectionGroup(java.util.Map, int, int)
	 */
	@Override
	public void syncNeProtectionGroup(Map paramter, int neId, int commandLevel)
			throws CommonException {
		//生成key
		String key = generatorKey(paramter, neId, GET_ALL_PROTECTION_GROUPS);
		try{
			//检查是否可以执行
			while(!isOperateCanExcute(key)){
				try {
					Thread.sleep(3*1000);
				} catch (InterruptedException e) {
					throw new CommonException(e,
							MessageCodeDefine.CORBA_RUNTIME_EXCEPTION); 
				}
			}
			//真实方法
			syncNeProtectionGroupImpl(paramter,neId,commandLevel);
		}catch(Exception e){
			ExceptionHandler.handleException(e);
			if(CommonException.class.isInstance(e)){
				//直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
				throw new CommonException(new NullPointerException(),((CommonException)e).getErrorCode(),((CommonException)e).getErrorMessage());
			}else{
				throw new CommonException(new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999);
			}
		}finally{
			EXCUTE_POOL.remove(key);
		}
	}



	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IDataCollectService#syncNeWDMProtectionGroup(java.util.Map, int, int)
	 */
	@Override
	public void syncNeWDMProtectionGroup(Map paramter, int neId,
			int commandLevel) throws CommonException {
		//生成key
		String key = generatorKey(paramter, neId, GET_ALL_WDM_PROTECTION_GROUPS);
		try{
			//检查是否可以执行
			while(!isOperateCanExcute(key)){
				try {
					Thread.sleep(3*1000);
				} catch (InterruptedException e) {
					throw new CommonException(e,
							MessageCodeDefine.CORBA_RUNTIME_EXCEPTION); 
				}
			}
			//真实方法
			syncNeWDMProtectionGroupImpl(paramter,neId,commandLevel);
		}catch(Exception e){
			ExceptionHandler.handleException(e);
			if(CommonException.class.isInstance(e)){
				//直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
				throw new CommonException(new NullPointerException(),((CommonException)e).getErrorCode(),((CommonException)e).getErrorMessage());
			}else{
				throw new CommonException(new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999);
			}
		}finally{
			EXCUTE_POOL.remove(key);
		}
	}



	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IDataCollectService#syncNeCtp(java.util.Map, int, int)
	 */
	@Override
	public void syncNeCtp(Map paramter, int neId, int commandLevel)
			throws CommonException {
		//生成key
		String key = generatorKey(paramter, neId, GET_CONTAINED_POTENTIAL_TPS);
		try{
			//检查是否可以执行
			while(!isOperateCanExcute(key)){
				try {
					Thread.sleep(3*1000);
				} catch (InterruptedException e) {
					throw new CommonException(e,
							MessageCodeDefine.CORBA_RUNTIME_EXCEPTION); 
				}
			}
			//真实方法
			syncNeCtpImpl(paramter,neId,commandLevel);
		}catch(Exception e){
			ExceptionHandler.handleException(e);
			if(CommonException.class.isInstance(e)){
				//直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
				throw new CommonException(new NullPointerException(),((CommonException)e).getErrorCode(),((CommonException)e).getErrorMessage());
			}else{
				throw new CommonException(new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999);
			}
		}finally{
			EXCUTE_POOL.remove(key);
		}
	}



	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IDataCollectService#syncLink(java.util.Map, int)
	 */
	@Override
	public void syncLink(Map paramter, List<LinkAlterModel> syncList,
			int commandLevel) throws CommonException {
		// //生成key
		// String key = generatorKey(paramter, null,
		// GET_ALL_TOP_LEVEL_TOPOLOGICAL_LINKS);
		// try{
		// //检查是否可以执行
		// while(!isOperateCanExcute(key)){
		// try {
		// Thread.sleep(3*1000);
		// } catch (InterruptedException e) {
		// throw new CommonException(e,
		// MessageCodeDefine.CORBA_RUNTIME_EXCEPTION);
		// }
		// }
		// //真实方法
		// syncLinkImpl(paramter,syncList,commandLevel);
		// }catch(Exception e){
		// ExceptionHandler.handleException(e);
		// if(CommonException.class.isInstance(e)){
		// //直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
		// throw new CommonException(new
		// NullPointerException(),((CommonException)e).getErrorCode(),((CommonException)e).getErrorMessage());
		// }else{
		// throw new CommonException(new
		// NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999);
		// }
		// }finally{
		// EXCUTE_POOL.remove(key);
		// }
		try {
			// 真实方法
			syncLinkImpl(paramter, syncList, commandLevel);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
			if (CommonException.class.isInstance(e)) {
				// 直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
				throw new CommonException(new NullPointerException(),
						((CommonException) e).getErrorCode(),
						((CommonException) e).getErrorMessage());
			} else {
				throw new CommonException(new NullPointerException(),
						MessageCodeDefine.MESSAGE_CODE_999999);
			}
		} finally {
		}
	}
	
	@Override
	public LinkAlterResultModel getLinkAlterList(Map paramter, int commandLevel) throws CommonException {
		//生成key
		String key = generatorKey(paramter, null, GET_ALL_TOP_LEVEL_TOPOLOGICAL_LINKS);
		try{
			//检查是否可以执行
			while(!isOperateCanExcute(key)){
				try {
					Thread.sleep(3*1000);
				} catch (InterruptedException e) {
					throw new CommonException(e,
							MessageCodeDefine.CORBA_RUNTIME_EXCEPTION); 
				}
			}
			//真实方法
			LinkAlterResultModel result = getLinkAlterListImpl(paramter,commandLevel);
			
			return result;
		}catch(Exception e){
			ExceptionHandler.handleException(e);
			if(CommonException.class.isInstance(e)){
				//直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
				throw new CommonException(new NullPointerException(),((CommonException)e).getErrorCode(),((CommonException)e).getErrorMessage());
			}else{
				throw new CommonException(new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999);
			}
		}finally{
			EXCUTE_POOL.remove(key);
		}
	}

	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IDataCollectService#syncNeEthService(java.util.Map, int, int)
	 */
	@Override
	public void syncNeEthService(Map paramter, int neId, int commandLevel)
			throws CommonException {
		//生成key
		String key = generatorKey(paramter, neId, GET_ALL_ETH_SERVICE);
		try{
			//检查是否可以执行
			while(!isOperateCanExcute(key)){
				try {
					Thread.sleep(3*1000);
				} catch (InterruptedException e) {
					throw new CommonException(e,
							MessageCodeDefine.CORBA_RUNTIME_EXCEPTION); 
				}
			}
			//真实方法
			syncNeEthServiceImpl(paramter,neId,commandLevel);
		}catch(Exception e){
			ExceptionHandler.handleException(e);
			if(CommonException.class.isInstance(e)){
				//直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
				throw new CommonException(new NullPointerException(),((CommonException)e).getErrorCode(),((CommonException)e).getErrorMessage());
			}else{
				throw new CommonException(new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999);
			}
		}finally{
			EXCUTE_POOL.remove(key);
		}
	}



	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IDataCollectService#syncNeBindingPath(java.util.Map, int, int)
	 */
	@Override
	public void syncNeBindingPath(Map paramter, int neId, int commandLevel)
			throws CommonException {
		//生成key
		String key = generatorKey(paramter, neId, GET_ALL_BINDING_PATH);
		try{
			//检查是否可以执行
			while(!isOperateCanExcute(key)){
				try {
					Thread.sleep(3*1000);
				} catch (InterruptedException e) {
					throw new CommonException(e,
							MessageCodeDefine.CORBA_RUNTIME_EXCEPTION); 
				}
			}
			//真实方法
			syncNeBindingPathImpl(paramter,neId,commandLevel);
		}catch(Exception e){
			ExceptionHandler.handleException(e);
			if(CommonException.class.isInstance(e)){
				//直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
				throw new CommonException(new NullPointerException(),((CommonException)e).getErrorCode(),((CommonException)e).getErrorMessage());
			}else{
				throw new CommonException(new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999);
			}
		}finally{
			EXCUTE_POOL.remove(key);
		}
	}


	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IDataCollectService#syncNeClock(java.util.Map, int, int)
	 */
	@Override
	public void syncNeClock(Map paramter, int neId, int commandLevel)
			throws CommonException {
		//生成key
		String key = generatorKey(paramter, neId, GET_CLOCK_SOURCE_STATUS);
		try{
			//检查是否可以执行
			while(!isOperateCanExcute(key)){
				try {
					System.out.println("sleep...................");
					Thread.sleep(3*1000);
				} catch (InterruptedException e) {
					throw new CommonException(e,
							MessageCodeDefine.CORBA_RUNTIME_EXCEPTION); 
				}
			}
			//真实方法
			syncNeClockImpl(paramter,neId,commandLevel);
		}catch(Exception e){
			ExceptionHandler.handleException(e);
			if(CommonException.class.isInstance(e)){
				//直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
				throw new CommonException(new NullPointerException(),((CommonException)e).getErrorCode(),((CommonException)e).getErrorMessage());
			}else{
				throw new CommonException(new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999);
			}
		}finally{
			EXCUTE_POOL.remove(key);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IDataCollectService#syncNeVBs(java.util.Map, int, int)
	 */
	@Override
	public void syncNeVBs(Map paramter, int neId, int commandLevel)
			throws CommonException {
		//生成key
		String key = generatorKey(paramter, neId, GET_ALL_VBS);
		try{
			//检查是否可以执行
			while(!isOperateCanExcute(key)){
				try {
					System.out.println("sleep...................");
					Thread.sleep(3*1000);
				} catch (InterruptedException e) {
					throw new CommonException(e,
							MessageCodeDefine.CORBA_RUNTIME_EXCEPTION); 
				}
			}
			//真实方法
			syncNeVBsImpl(paramter,neId,commandLevel);
		}catch(Exception e){
			ExceptionHandler.handleException(e);
			if(CommonException.class.isInstance(e)){
				//直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
				throw new CommonException(new NullPointerException(),((CommonException)e).getErrorCode(),((CommonException)e).getErrorMessage());
			}else{
				throw new CommonException(new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999);
			}
		}finally{
			EXCUTE_POOL.remove(key);
		}
	}
	
	@Override
	public void syncSNC(Map paramter, int commandLevel)
			throws CommonException {
		//生成key
		String key = generatorKey(paramter, null, GET_ALL_SNC);
		try{
			//检查是否可以执行
			while(!isOperateCanExcute(key)){
				try {
					Thread.sleep(3*1000);
				} catch (InterruptedException e) {
					throw new CommonException(e,
							MessageCodeDefine.CORBA_RUNTIME_EXCEPTION); 
				}
			}
			//真实方法
			syncSNCImpl(paramter,commandLevel);
		}catch(Exception e){
			ExceptionHandler.handleException(e);
			if(CommonException.class.isInstance(e)){
				e.printStackTrace();
				//直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
				throw new CommonException(new NullPointerException(),((CommonException)e).getErrorCode(),((CommonException)e).getErrorMessage());
			}else{
				throw new CommonException(new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999);
			}
		}finally{
			EXCUTE_POOL.remove(key);
		}
	}
	
	
	@Override
	public void syncCRSFromRoute(Map paramter, int commandLevel)
			throws CommonException {
		//生成key
		String key = generatorKey(paramter, null, GET_ALL_SNC_FROM_ROUTE);
		try{
			//检查是否可以执行
			while(!isOperateCanExcute(key)){
				try {
					Thread.sleep(3*1000);
				} catch (InterruptedException e) {
					throw new CommonException(e,
							MessageCodeDefine.CORBA_RUNTIME_EXCEPTION); 
				}
			}
			//真实方法
			syncCRSFromRouteImpl(paramter,commandLevel);
		}catch(Exception e){
			ExceptionHandler.handleException(e);
			if(CommonException.class.isInstance(e)){
				//直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
				throw new CommonException(new NullPointerException(),((CommonException)e).getErrorCode(),((CommonException)e).getErrorMessage());
			}else{
				throw new CommonException(new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999);
			}
		}finally{
			EXCUTE_POOL.remove(key);
		}
	}
	
	@Override
	public void syncRoute(Map paramter, int commandLevel)
			throws CommonException {
		//生成key
		String key = generatorKey(paramter, null, GET_ALL_ROUTE);
		try{
			//检查是否可以执行
			while(!isOperateCanExcute(key)){
				try {
					Thread.sleep(3*1000);
				} catch (InterruptedException e) {
					throw new CommonException(e,
							MessageCodeDefine.CORBA_RUNTIME_EXCEPTION); 
				}
			}
			//真实方法
			syncRouteImpl(paramter,commandLevel);
		}catch(Exception e){
			ExceptionHandler.handleException(e);
			if(CommonException.class.isInstance(e)){
				//直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
				throw new CommonException(new NullPointerException(),((CommonException)e).getErrorCode(),((CommonException)e).getErrorMessage());
			}else{
				throw new CommonException(new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999);
			}
		}finally{
			EXCUTE_POOL.remove(key);
		}
	}
	
//	/* (non-Javadoc)
//	 * @see com.fujitsu.IService.IDataCollectService#syncNeList(java.util.Map, int)
//	 */
//	@Override
//	public void syncAllFlowDomains(Map paramter, int commandLevel)
//			throws CommonException {
//		//生成key
//		String key = generatorKey(paramter, null, GET_ALL_MANAGED_ELEMENT);
//		try{
//			//检查是否可以执行
//			while(!isOperateCanExcute(key)){
//				try {
//					Thread.sleep(3*1000);
//				} catch (InterruptedException e) {
//					throw new CommonException(e,
//							MessageCodeDefine.CORBA_RUNTIME_EXCEPTION); 
//				}
//			}
//			//真实方法
//			syncAllFlowDomainsImpl(paramter,commandLevel);
//		}catch(Exception e){
//			ExceptionHandler.handleException(e);
//			if(CommonException.class.isInstance(e)){
//				//直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
//				throw new CommonException(new NullPointerException(),((CommonException)e).getErrorCode(),((CommonException)e).getErrorMessage());
//			}else{
//				throw new CommonException(new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999);
//			}
//		}finally{
//			EXCUTE_POOL.remove(key);
//		}
//	}
	
	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IDataCollectService#syncNeList(java.util.Map, int)
	 */
	@Override
	public void syncAllFdfrs(Map paramter, int commandLevel)
			throws CommonException {
		//生成key
		String key = generatorKey(paramter, null, GET_ALL_MANAGED_ELEMENT);
		try{
			//检查是否可以执行
			while(!isOperateCanExcute(key)){
				try {
					Thread.sleep(3*1000);
				} catch (InterruptedException e) {
					throw new CommonException(e,
							MessageCodeDefine.CORBA_RUNTIME_EXCEPTION); 
				}
			}
			//真实方法
			syncAllFdfrsImpl(paramter,commandLevel);
		}catch(Exception e){
			e.printStackTrace();
			ExceptionHandler.handleException(e);
			if(CommonException.class.isInstance(e)){
				//直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
				throw new CommonException(new NullPointerException(),((CommonException)e).getErrorCode(),((CommonException)e).getErrorMessage());
			}else{
				throw new CommonException(new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999);
			}
		}finally{
			EXCUTE_POOL.remove(key);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.fujitsu.IService.IDataCollectService#syncNeList(java.util.Map, int)
	 */
	@Override
	public void syncLinkOfFdfrs(Map paramter, int commandLevel)
			throws CommonException {
		//生成key
		String key = generatorKey(paramter, null, GET_ALL_MANAGED_ELEMENT);
		try{
			//检查是否可以执行
			while(!isOperateCanExcute(key)){
				try {
					Thread.sleep(3*1000);
				} catch (InterruptedException e) {
					throw new CommonException(e,
							MessageCodeDefine.CORBA_RUNTIME_EXCEPTION); 
				}
			}
			//真实方法
			syncLinkOfFdfrsImpl(paramter,commandLevel);
		}catch(Exception e){
			ExceptionHandler.handleException(e);
			if(CommonException.class.isInstance(e)){
				//直接抛出e会导致客户端catch不到CommonException，此处统一替换为NullPointerException
				throw new CommonException(new NullPointerException(),((CommonException)e).getErrorCode(),((CommonException)e).getErrorMessage());
			}else{
				throw new CommonException(new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999);
			}
		}finally{
			EXCUTE_POOL.remove(key);
		}
	}
	
	//判断操作是否可以进行--用于限制同一网管同一操作的并发进行，会产生冗余数据
	private synchronized boolean isOperateCanExcute(String key){
		boolean result = true;

		if(EXCUTE_POOL.contains(key)){
			result =  false;
		}else{
			EXCUTE_POOL.add(key);
		}
		return result;
	}
	
	//生成唯一标示符
	private String generatorKey(Map paramter, Integer neId, int collectType) {
		neId = neId == null ? 0 : neId.intValue();
		String key = String.valueOf(paramter.hashCode()) + "_"
				+ String.valueOf(neId) + "_" + String.valueOf(collectType);
		return key;
	}
	
	
	//-----------------------  抽象方法  ------------------------------
	//抽象方法
	public abstract void syncNeListImpl(Map paramter,
			int commandLevel) throws CommonException;
	
	public abstract void syncNeEquipmentOrHolderImpl(Map paramter, int neId,
			int commandLevel) throws CommonException;
	
	public abstract void syncNeCRSImpl(Map paramter,int neId, short[] layerRateList, 
			int commandLevel) throws CommonException;
	
	public abstract void syncNePtpImpl(Map paramter, int neId,
			int commandLevel) throws CommonException;
	
	public abstract void syncLinkImpl(Map paramter, List<LinkAlterModel> syncList,
			int commandLevel) throws CommonException;
	
	public abstract LinkAlterResultModel getLinkAlterListImpl(Map paramter, 
			int commandLevel) throws CommonException;
	
	public abstract void syncNeBindingPathImpl(Map paramter, int neId,
			int commandLevel) throws CommonException;
	
	public abstract void syncNeClockImpl(Map paramter, int neId,
			int commandLevel) throws CommonException;
	
	public abstract void syncNeEProtectionGroupImpl(Map paramter, int neId,
			int commandLevel) throws CommonException;
	
	public abstract void syncNeEthServiceImpl(Map paramter, int neId,
			int commandLevel) throws CommonException;
	
	public abstract void syncNeInternalLinkImpl(Map paramter, int neId,
			int commandLevel) throws CommonException;
	
	public abstract void syncNeProtectionGroupImpl(Map paramter, int neId,
			int commandLevel) throws CommonException;
	
	public abstract void syncNeWDMProtectionGroupImpl(Map paramter, int neId,
			int commandLevel) throws CommonException;
	
	public abstract void syncNeCtpImpl(Map paramter, int neId,
			int commandLevel) throws CommonException;
	
	public abstract void syncNeVBsImpl(Map paramter, int neId,
			int commandLevel) throws CommonException;
	
	public abstract void syncSNCImpl(Map paramter,
			int commandLevel) throws CommonException;
	
	public abstract void syncRouteImpl(Map paramter,
			int commandLevel) throws CommonException;
	
	public abstract void syncCRSFromRouteImpl(Map paramter,
			int commandLevel) throws CommonException;
	
//	public abstract void syncAllFlowDomainsImpl(Map paramter,
//			int commandLevel) throws CommonException;
	
	public abstract void syncAllFdfrsImpl(Map paramter,
			int commandLevel) throws CommonException;
	
	public abstract void syncLinkOfFdfrsImpl(Map paramter,
			int commandLevel) throws CommonException;
}
