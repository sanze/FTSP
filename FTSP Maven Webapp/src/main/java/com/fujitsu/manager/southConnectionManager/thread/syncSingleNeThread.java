package com.fujitsu.manager.southConnectionManager.thread;

import java.util.Map;
import java.util.concurrent.Callable;

import com.fujitsu.IService.IDataCollectServiceProxy;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.ConnectionManagerMapper;
import com.fujitsu.model.NeSyncResultModel;
import com.fujitsu.util.SpringContextUtil;

public class syncSingleNeThread implements Callable<NeSyncResultModel> {
	
	public IDataCollectServiceProxy dataCollectService;
	
	private ConnectionManagerMapper connectionManagerMapper;
	
	private int emsConnectionId;
	private int neId;
	private int commandLevel;
	private Map param;
	private boolean updateProcessInfo;
	
	/**
	 * @param emsConnectionId  网管Id
	 * @param neId 网元id
	 * @param param
	 * 					进度条：sessionId，syncName（可选）
	 * 					同步参数：DISPLAY_NAME（网元名），SUPORT_RATES（支持层速率），BASIC_SYNC_STATUS（同步状态），t_base_ne表中对应字段
	 * @param commandLevel
	 * @param updateProcessInfo  是否需要进度条
	 */
	public syncSingleNeThread(int emsConnectionId, int neId, Map param,
			int commandLevel, boolean updateProcessInfo) {
		this.emsConnectionId = emsConnectionId;
		this.neId = neId;
		this.commandLevel = commandLevel;
		this.param = param;
		this.updateProcessInfo = updateProcessInfo;
		// 获取bean
		this.connectionManagerMapper = (ConnectionManagerMapper) SpringContextUtil
				.getBean("connectionManagerMapper");
	}
	
	public NeSyncResultModel call() {

		String sessionId = param.get("sessionId")==null?null:param.get("sessionId").toString();
		String syncName = param.get("syncName")==null?null:param.get("syncName").toString();
		
		String neName = param.get("DISPLAY_NAME").toString();
		String layRatesString = param.get("SUPORT_RATES")==null?null:param.get("SUPORT_RATES").toString();
		Integer neSyncStatus = param.get("BASIC_SYNC_STATUS")==null?null:Integer.valueOf(param.get("BASIC_SYNC_STATUS").toString());
		String text = "";
		//错误信息
		String errorMessage = "";
		
		System.out.println(neName+"开始同步！@@@@@@@@@@@@@@@@@@@@");
		
		//返回结果
		NeSyncResultModel result = new NeSyncResultModel(neId,neName);
		
		//如果为null，获取实时网元同步状态
		if(neSyncStatus == null){
			Map ne = connectionManagerMapper.getNeInfoByNeId(neId);
			neSyncStatus = ne.get("BASIC_SYNC_STATUS")==null?null:Integer.valueOf(ne.get("BASIC_SYNC_STATUS").toString());
		}
		//网元正在同步,直接返回
		if(neSyncStatus!=null&&neSyncStatus.intValue() == CommonDefine.NE_SYNC_DOING){
			return result;
		}
		
		//更新状态为正在同步
		connectionManagerMapper.updateNeBasicSyncInfo(neId,
				CommonDefine.NE_SYNC_DOING, errorMessage);

		/*@@@@@@@获取采集服务@@@@@@@@@@@*/
		try {
			dataCollectService = SpringContextUtil
					.getDataCollectServiceProxy(emsConnectionId);
		} catch (Exception e) {
			if(CommonException.class.isInstance(e)){
				errorMessage = ((CommonException)e).getErrorMessage();
			}else{
				errorMessage = "未知错误！";
			}
			//更新网元同步时间，同步状态
			connectionManagerMapper.updateNeBasicSyncInfo(neId,
					CommonDefine.NE_SYNC_FAILED, errorMessage);
			//填充返回值
			result.setBasicSyncResult(false);
			result.setBasicSyncMessage(errorMessage);
//			result.setMstpSyncResult(false);
//			result.setMstpSyncMessage(e.getErrorMessage());
//			result.setCrsSyncResult(false);
//			result.setCrsSyncMessage(e.getErrorMessage());
			return result;
		}
		/*@@@@@@@ 基础数据同步 @@@@@@@@@@@*/
		try {
			// 更新进度条内容
			if(updateProcessInfo){
				text = "网元:" + neName+ "     正在同步设备信息";
				CommonDefine.setProcessParameter(sessionId,syncName,null,null,text);
			}
			//同步基础数据
			dataCollectService.syncNeEquipmentOrHolder(neId, commandLevel);
			
			// 更新进度条内容
			if(updateProcessInfo){
				text = "网元:" + neName+ "     正在同步端口信息";
				CommonDefine.setProcessParameter(sessionId,syncName,null,null,text);
			}
			dataCollectService.syncNePtp(neId, commandLevel);
			
			// 更新进度条内容
			if(updateProcessInfo){
				text = "网元:" + neName+ "     正在同步通道信息";
				CommonDefine.setProcessParameter(sessionId,syncName,null,null,text);
			}
			dataCollectService.syncNeCtp(neId, commandLevel);
			
			//处理附加信息
			dataCollectService.syncExtendInfo_NE(neId, commandLevel);
			
		} catch (Exception e) {
			if(CommonException.class.isInstance(e)){
				errorMessage = ((CommonException)e).getErrorMessage();
			}else{
				errorMessage = "未知错误！";
			}
			//更新网元同步时间，同步状态--基础数据部分同步失败直接返回
			connectionManagerMapper.updateNeBasicSyncInfo(neId,
					CommonDefine.NE_SYNC_FAILED, errorMessage);
			//填充返回值
			result.setBasicSyncResult(false);
			result.setBasicSyncMessage(errorMessage);
//			result.setMstpSyncResult(false);
//			result.setMstpSyncMessage(e.getErrorMessage());
//			result.setCrsSyncResult(false);
//			result.setCrsSyncMessage(e.getErrorMessage());
			return result;
		}
		
		/*@@@@@@@ syncNeEthService syncNeBindingPath @@@@@@@@@@@*/
		try {
			// 更新进度条内容
			if(updateProcessInfo){
				text = "网元:" + neName+ "     正在同步以太网信息";
				CommonDefine.setProcessParameter(sessionId,syncName,null,null,text);
			}
			dataCollectService.syncNeEthService(neId, commandLevel);
			
			// 更新进度条内容
			if(updateProcessInfo){
				text = "网元:" + neName+ "     正在同步BindingPath信息";
				CommonDefine.setProcessParameter(sessionId,syncName,null,null,text);
			}
			dataCollectService.syncNeBindingPath(neId, commandLevel);
			
		}  catch (Exception e) {
			if(CommonException.class.isInstance(e)){
				errorMessage = errorMessage+((CommonException)e).getErrorMessage()+"||";
			}else{
				errorMessage = "未知错误！";
			}
			//填充返回值
//			result.setMstpSyncResult(false);
//			result.setBasicSyncMessage(e.getErrorMessage());
		}
		
		/*@@@@@@@ syncNeCRS 需要在同步保护信息之前@@@@@@@@@@@*/
		try {
			// 更新进度条内容
			if(updateProcessInfo){
				text = "网元:" + neName+ "     正在同步交叉连接信息";
				CommonDefine.setProcessParameter(sessionId,syncName,null,null,text);
			}
			//组织层速率
			if(layRatesString == null){
				Map ne = connectionManagerMapper.getNeInfoByNeId(neId);
				layRatesString = ne.get("SUPORT_RATES").toString();
			}
			short[] layerRateList = null;
			if (layRatesString.isEmpty()){
				layerRateList =null;
			} else {
				layerRateList = constructLayRates(layRatesString);
			}
			dataCollectService.syncNeCRS(neId, layerRateList, commandLevel);
		} catch (Exception e) {
			if(CommonException.class.isInstance(e)){
				errorMessage = errorMessage+((CommonException)e).getErrorMessage()+"||";
			}else{
				errorMessage = "未知错误！";
			}
//			result.setCrsSyncResult(false);
//			result.setCrsSyncMessage(e.getErrorMessage());
		}
		
		/*@@@@@@@ syncNeEProtectionGroup @@@@@@@@@@@*/
		try {
			// 更新进度条内容
			if(updateProcessInfo){
				text = "网元:" + neName+ "     正在同步设备保护信息";
				CommonDefine.setProcessParameter(sessionId,syncName,null,null,text);
			}
			//同步其他数据
			dataCollectService.syncNeEProtectionGroup(neId, commandLevel);
		} catch (Exception e) {
//			if(CommonException.class.isInstance(e)){
//				errorMessage = errorMessage+((CommonException)e).getErrorMessage()+"||";
//			}else{
//				errorMessage = "未知错误！";
//			}
		}
		/*@@@@@@@ syncNeProtectionGroup @@@@@@@@@@@*/
		try {
			// 更新进度条内容
			if(updateProcessInfo){
				text = "网元:" + neName+ "     正在同步SDH保护信息";
				CommonDefine.setProcessParameter(sessionId,syncName,null,null,text);
			}
			dataCollectService.syncNeProtectionGroup(neId, commandLevel);
		}  catch (Exception e) {
//			if(CommonException.class.isInstance(e)){
//				errorMessage = errorMessage+((CommonException)e).getErrorMessage()+"||";
//			}else{
//				errorMessage = "未知错误！";
//			}
		}
		/*@@@@@@@ syncNeWDMProtectionGroup @@@@@@@@@@@*/	
		try {
			// 更新进度条内容
			if(updateProcessInfo){
				text = "网元:" + neName+ "     正在同步WDM保护信息";
				CommonDefine.setProcessParameter(sessionId,syncName,null,null,text);
			}
			dataCollectService.syncNeWDMProtectionGroup(neId, commandLevel);
		}  catch (Exception e) {
//			if(CommonException.class.isInstance(e)){
//				errorMessage = errorMessage+((CommonException)e).getErrorMessage()+"||";
//			}else{
//				errorMessage = "未知错误！";
//			}
		}
		/*@@@@@@@ syncNeClock @@@@@@@@@@@*/
		try {
			// 更新进度条内容
			if(updateProcessInfo){
				text = "网元:" + neName+ "     正在同步时钟信息";
				CommonDefine.setProcessParameter(sessionId,syncName,null,null,text);
			}
			dataCollectService.syncNeClock(neId, commandLevel);
		}  catch (Exception e) {
//			if(CommonException.class.isInstance(e)){
//				errorMessage = errorMessage+((CommonException)e).getErrorMessage()+"||";
//			}else{
//				errorMessage = "未知错误！";
//			}
		}
		/*@@@@@@@ syncNeInternalLink @@@@@@@@@@@*/
		try {
			// 更新进度条内容
			if(updateProcessInfo){
				text = "网元:" + neName+ "     正在同步内部链路信息";
				CommonDefine.setProcessParameter(sessionId,syncName,null,null,text);
			}
			dataCollectService.syncNeInternalLink(neId, commandLevel);
		}  catch (Exception e) {
//			if(CommonException.class.isInstance(e)){
//				errorMessage = errorMessage+((CommonException)e).getErrorMessage()+"||";
//			}else{
//				errorMessage = "未知错误！";
//			}
		}

		/*@@@@@@@ syncNeVBs @@@@@@@@@@@*/
		try {
			// 更新进度条内容
			if(updateProcessInfo){
				text = "网元:" + neName+ "     正在同步VB信息";
				CommonDefine.setProcessParameter(sessionId,syncName,null,null,text);
			}
			dataCollectService.syncNeVBs(neId, commandLevel);
			
		}  catch (Exception e) {
			if(CommonException.class.isInstance(e)){
				errorMessage = errorMessage+((CommonException)e).getErrorMessage()+"||";
			}else{
				errorMessage = "未知错误！";
			}
			//填充返回值
//			result.setMstpSyncResult(false);
//			result.setBasicSyncMessage(e.getErrorMessage());
		}
		
		
		//更新网元同步时间，同步状态
		if(!errorMessage.isEmpty()){
			errorMessage = errorMessage.substring(0, errorMessage.length()-2);
		}
		connectionManagerMapper.updateNeBasicSyncInfo(neId,
				CommonDefine.NE_SYNC_HAD, errorMessage);
		
		System.out.println(neName+"同步完成！@@@@@@@@@@@@@@@@@@@@");
		return result;
	}
	
	private short[] constructLayRates(String layRatesString) {
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
