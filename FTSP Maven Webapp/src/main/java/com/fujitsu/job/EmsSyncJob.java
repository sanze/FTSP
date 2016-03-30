package com.fujitsu.job;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.fujitsu.IService.IDataCollectServiceProxy;
import com.fujitsu.IService.ISouthConnectionService;
import com.fujitsu.abstractService.AbstractService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.ConnectionManagerMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.manager.southConnectionManager.thread.syncSingleNeThread;
import com.fujitsu.model.EmsConnectionModel;
import com.fujitsu.model.FutureModel;
import com.fujitsu.model.NeSyncResultModel;
import com.fujitsu.util.SpringContextUtil;

public class EmsSyncJob implements Job {


	private ConnectionManagerMapper connectionManagerMapper;

//	private IQuartzManagerService quartzManagerService;

	//用接口申明不要用实现类！！！
	private ISouthConnectionService southConnectionService;


	public EmsSyncJob() {
		connectionManagerMapper = (ConnectionManagerMapper) SpringContextUtil
				.getBean("connectionManagerMapper");
		southConnectionService = (ISouthConnectionService) SpringContextUtil
				.getBean("southConnectionServiceImpl");
//		quartzManagerService = (IQuartzManagerService) SpringContextUtil
//				.getBean("quartzManagerService");
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		//优先级为4
		Integer collectLevel = CommonDefine.COLLECT_LEVEL_4;
		// 获取网管Id
		int emsConnectionId = Integer.parseInt(context.getJobDetail()
				.getJobDataMap().get("BASE_EMS_CONNECTION_ID").toString());
		//获取网管连接对象
		Map emsConnection = connectionManagerMapper.getConnectionByEmsConnectionId(emsConnectionId);
		//获取任务对象
		Map syncTask = connectionManagerMapper.getEmsSyncTask(emsConnectionId);
		//任务Id
		int taskId = Integer.valueOf(syncTask.get("SYS_TASK_ID").toString());
		//网元列表对象
		List<Map> neList = new ArrayList<Map>();
		
        //同步线程
		Callable<NeSyncResultModel>  syncThread = null;
		//结果集
		List<FutureModel> futureList = new ArrayList<FutureModel>();
		//运行结果
		NeSyncResultModel result;
		
		//是否中止标志
		boolean isStop = false;
		
		try {
			// 获取该网管下所有网元列表
			neList = connectionManagerMapper
					.getAllNeListByEmsConnnectionId(emsConnectionId);
			
			//初始化运行详细信息
			initRunDetailInfo(neList,taskId,emsConnection);
			//任务运行状态
			int taskStatusValue;
			//网元Id
			Integer neId;
			//
			FutureModel futureModel;
			
			// 提交网元同步任务
			for(Map ne:neList){
				neId = Integer.valueOf(ne.get("BASE_NE_ID").toString());
				//同步网元
				Map param = new HashMap();
				param.put("DISPLAY_NAME", ne.get("DISPLAY_NAME"));
				param.put("SUPORT_RATES", ne.get("SUPORT_RATES"));
				param.put("BASIC_SYNC_STATUS", ne.get("BASIC_SYNC_STATUS"));

				// 开启线程
				syncThread = new syncSingleNeThread(
						emsConnectionId, neId,
						param,collectLevel,false);

				//执行同步
				Future<NeSyncResultModel> future = AbstractService.getPool(emsConnectionId).submit(syncThread);

				futureModel = new FutureModel(neId,ne.get("DISPLAY_NAME").toString(),future);
				//保存
				futureList.add(futureModel);
			}
			//同步计数
			Integer syncCount = 0;
			
//			// 1.先将网元的状态更新为等待中
//			southConnectionService.updateEmsSyncTaskStatus(neList,
//					taskId, "","等待执行", null,  CommonDefine.FALSE,CommonDefine.SUCCESS);
			//监测同步进度
			while(syncCount!=neList.size()){
				//获取任务状态
				taskStatusValue = Integer.parseInt(connectionManagerMapper
						.getTaskStatusValue(taskId));
				//任务状态为暂停
				if (taskStatusValue == CommonDefine.EMS_SYNC_PAUSE) {
//					//暂停所有同步进程
//					for(FutureModel future : futureList){
//						future.getFuture().wait();
//					}
					// 2.检查暂停时间是否到期
					checkPause(emsConnectionId, taskId);
//					//唤醒同步进程
//					for(FutureModel future : futureList){
//						future.getFuture().notify();
//					}
				}
				//任务状态为终止
				else if (taskStatusValue == CommonDefine.EMS_SYNC_INGSTOP) {
					//取消所有同步进程
					for(FutureModel future : futureList){
						if(future.getFuture().isDone()){
							//获取运行结果
							result = future.getFuture().get();
							//更新任务详细信息
							updateTaskStatus(taskId,result);
						}else{
							//取消任务
							future.getFuture().cancel(false);
							//设置返回结果
							result = new NeSyncResultModel(future.getNeId(),"");
							result.setBasicSyncResult(false);
							result.setBasicSyncMessage("同步中止");
//							result.setMstpSyncResult(false);
//							result.setMstpSyncMessage("同步中止");
//							result.setCrsSyncResult(false);
//							result.setCrsSyncMessage("同步中止");
							//更新任务详细信息
							updateTaskStatus(taskId,result);
						}
					}
					isStop = true;
					break;
				}else{
					//判断同步是否完成
					for(FutureModel future : futureList){
						if(future.getFuture().isDone()){
							//获取运行结果
							result = future.getFuture().get();
							//更新任务详细信息
							updateTaskStatus(taskId,result);
							//移除对象
							syncCount++;
							futureList.remove(future);
							break;
						}
					}
				}
			}
			//@@@@@@@ 拓扑链路同步 @@@@@@@@@@@@@
			// 1.先将网管拓扑链路同步的状态更新为等待中
			if(isStop){
				connectionManagerMapper.updateTaskDetailInfo(taskId,
						emsConnectionId, CommonDefine.TASK_TARGET_TYPE.EMS,
						null, null, "同步中止");
			}else{
				// 网管级别拓扑链路同步 在任务正常执行时
				try {
					// 网管级别拓扑链路同步操作
					EmsConnectionModel emsConnectionModel=new EmsConnectionModel();
					emsConnectionModel.setEmsConnectionId(emsConnectionId);
					southConnectionService.topoLinkSync(emsConnectionModel);
					connectionManagerMapper.updateTaskDetailInfo(taskId,
							emsConnectionId, CommonDefine.TASK_TARGET_TYPE.EMS, null,
							CommonDefine.SUCCESS, "完成");
				} catch (CommonException e) {
					connectionManagerMapper.updateTaskDetailInfo(taskId,
							emsConnectionId, CommonDefine.TASK_TARGET_TYPE.EMS, null,
							null, "采集失败（" + e.getErrorMessage() + ")");
				}
				//同步snc route信息
				try {
					IDataCollectServiceProxy dataCollectService = SpringContextUtil
							.getDataCollectServiceProxy(emsConnectionId);
					//同步snc
					dataCollectService.syncSNC(collectLevel);
					//同步route
					dataCollectService.syncRoute(collectLevel);
					//syncAllFdfrs
					dataCollectService.syncAllFdfrs(collectLevel);
					//syncLinkOfFdfrs
					dataCollectService.syncLinkOfFdfrs(collectLevel);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		} finally {
			//计算下次执行时间
			String next = null;
			try {
				next = southConnectionService.calculateDate(syncTask.get("PERIOD").toString(), syncTask.get(
						"PERIOD_TYPE").toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			//是否中止
			if(isStop){
				//设置为同步终止
				connectionManagerMapper.updateTaskStatus(taskId,
						CommonDefine.EMS_SYNC_INGSTOP,next);
			}else{
				//同步成功计数
				Integer succeedCollected = connectionManagerMapper
						.getTaskRunResultCount(taskId, CommonDefine.SUCCESS);
				//同步失败计数
				Integer failedCollected = connectionManagerMapper
						.getTaskRunResultCount(taskId, CommonDefine.FAILED);
				
				//更新任务状态
				if (failedCollected == 0
						&& succeedCollected == neList.size()+1) {
					connectionManagerMapper.updateTaskStatus(taskId,
							CommonDefine.EMS_SYNC_SUCCESS,next);
				} else if (succeedCollected > 0 && failedCollected > 0) {
					connectionManagerMapper.updateTaskStatus(taskId,
							CommonDefine.EMS_SYNC_PARTSUCESS,next);
				}else{
					connectionManagerMapper.updateTaskStatus(taskId,
							CommonDefine.EMS_SYNC_FAILED,next);
				}
			}
		}
	}

	private void updateTaskStatus(int taskId, NeSyncResultModel result) {
		// 更新基础信息
		connectionManagerMapper.updateTaskDetailInfo(taskId, result.getNeId(),
				CommonDefine.TASK_TARGET_TYPE.NE, null,
				result.isBasicSyncResult() ? CommonDefine.SUCCESS
						: CommonDefine.FAILED, result.getBasicSyncMessage());
//		// 更新MSTP信息
//		connectionManagerMapper.updateTaskDetailInfo(taskId, result.getNeId(),
//				CommonDefine.TASK_TARGET_TYPE.NE, CommonDefine.NE_MSTP_SYNC,
//				result.isMstpSyncResult() ? CommonDefine.SUCCESS
//						: CommonDefine.FAILED, result.getMstpSyncMessage());
//		// 更新CRS信息
//		connectionManagerMapper.updateTaskDetailInfo(taskId, result.getNeId(),
//				CommonDefine.TASK_TARGET_TYPE.NE, CommonDefine.NE_CROSS_SYNC,
//				result.isCrsSyncResult() ? CommonDefine.SUCCESS
//						: CommonDefine.FAILED, result.getCrsSyncMessage());
	}
			
			
//			for ( ; neList.size() > 0; ) {
//				Map ne = neList.get(0);
//				//获取任务状态
//				taskStatusValue = Integer.parseInt(connectionManagerMapper
//						.getTaskStatusValue(taskId));
//
//				neId = Integer.valueOf(ne.get("neId").toString());
//				//任务状态为暂停
//				if (taskStatusValue == CommonDefine.EMS_SYNC_PAUSE) {
//					// 1.先将网元的状态更新为等待中
//					southConnectionService.updateEmsSyncTaskStatus(neList,
//							taskId, "","等待执行", null,  CommonDefine.FALSE,CommonDefine.SUCCESS);
//					// 2.检查暂停时间是否到期
//					checkPause(emsConnectionId, taskId);
//					//同步网元
//					Map param = new HashMap();
//					param.put("neName", ne.get("DISPLAY_NAME"));
//					param.put("layRatesString", ne.get("SUPORT_RATES"));
//
//					// 开启线程
//					syncThread = new syncSingleNeThread(
//							emsConnectionId, neId,
//							param,collectLevel,false);
//					//执行同步
//					Future future = pool.submit(syncThread);
//					//保存
//					futureList.add(future);
//					//移除网元
//					neList.remove(ne);
//				} else	if (taskStatusValue == CommonDefine.EMS_SYNC_INGSTOP) {
//					// 1.将该任务下的剩余未同步的网元状态更新为 同步中止
//					southConnectionService.updateEmsSyncTaskStatus(neList,
//							taskId,"", "同步中止", null,  CommonDefine.FALSE,CommonDefine.SUCCESS);
//					// 2.退出
//					return;
//				} else {
//					//同步网元
//					Map param = new HashMap();
//					param.put("neName", ne.get("DISPLAY_NAME"));
//					param.put("layRatesString", ne.get("SUPORT_RATES"));
//
//					// 开启线程
//					syncThread = new syncSingleNeThread(
//							emsConnectionId, neId,
//							param,collectLevel,false);
//					//执行同步
//					Future future = pool.submit(syncThread);
//					//保存
//					futureList.add(future);
//					//移除网元
//					neList.remove(ne);
//				}
//			}
//
//			// 网管级别拓扑链路同步执行操作
//			taskStatusValue = Integer.parseInt(connectionManagerMapper
//					.getTaskStatusValue(taskId));
//
//			//等待暂停结束
//			if (taskStatusValue == CommonDefine.EMS_SYNC_PAUSE) {
//				// 网管级别拓扑链路同步 在任务暂停时
//				// 1.先将网管拓扑链路同步的状态更新为等待中
//				connectionManagerMapper.updateTaskDetailInfo(taskId,
//						emsConnectionId, CommonDefine.TASK_TARGET_TYPE.EMS,
//						null, null, "等待执行");
//				// 2.检查暂停时间是否到期
//				checkPause(emsConnectionId, taskId);
//				// 网管级别拓扑链路同步 在任务正常执行时
//				try {
//					// 网管级别拓扑链路同步操作
//					southConnectionService.topoLinkSyncChangeList(
//							emsConnectionId, collectLevel, taskId);
//				} catch (CommonException e) {
//	
//				}
//			} else	if (taskStatusValue == CommonDefine.EMS_SYNC_INGSTOP) {
//				// 1.将该任务下的剩余未同步的网管拓扑链路同步状态更新为 同步中止
//				connectionManagerMapper.updateTaskDetailInfo(taskId,
//						emsConnectionId, CommonDefine.TASK_TARGET_TYPE.EMS,
//						null, null, "同步中止");
//				// 2.退出
//				return;
//			} else {
//				// 网管级别拓扑链路同步 在任务正常执行时
//				try {
//					// 网管级别拓扑链路同步操作
//					southConnectionService.topoLinkSyncChangeList(
//							emsConnectionId, collectLevel, taskId);
//				} catch (CommonException e) {
//	
//				}
//			}
//		} catch (Exception e) {
//			if(CommonException.class.isInstance(e)){
//				CommonException ce = (CommonException) e;
//				southConnectionService.updateEmsSyncTaskStatus(neList, taskId,"",
//						"采集失败（" + ce.getErrorMessage() + ")", null,  CommonDefine.FALSE,CommonDefine.FALSE);
//				connectionManagerMapper.updateTaskDetailInfo(taskId,
//						emsConnectionId, CommonDefine.TASK_TARGET_TYPE.EMS, null,
//						null, "采集失败（" + ce.getErrorMessage() + ")");
//			}
//			ExceptionHandler.handleException(e);
//			
//		} finally {
//			// 比较采集失败的任务详细记录数是否为鸭蛋,鸭蛋则为成功,否则部分成功(→_←)
//			try {
//				Integer succeedCollected = 0;
//				succeedCollected = connectionManagerMapper
//						.getTaskRunResultCount(taskId, CommonDefine.SUCCESS);
//				Integer failedCollected = 0;
//				failedCollected = connectionManagerMapper
//						.getTaskRunResultCount(taskId, CommonDefine.FAILED);
//				String next = null;
//				try {
//					syncTask = connectionManagerMapper.getEmsSyncTask(emsConnectionId);
//					next = southConnectionService.calculateDate(syncTask.get("PERIOD").toString(), syncTask.get(
//							"PERIOD_TYPE").toString());
//				} catch (ParseException e) {
//					throw new CommonException(e, MessageCodeDefine.CIR_EXCPT_PARSE);
//				}
//				
//				if (failedCollected == 0
//						&& succeedCollected == 3*neList.size()+1) {
//					connectionManagerMapper.updateTaskStatus(taskId,
//							CommonDefine.EMS_SYNC_SUCCESS,next);
//				} else if (succeedCollected > 0 && failedCollected > 0) {
//					connectionManagerMapper.updateTaskStatus(taskId,
//							CommonDefine.EMS_SYNC_PARTSUCESS,next);
//				}else{
//					connectionManagerMapper.updateTaskStatus(taskId,
//							CommonDefine.EMS_SYNC_FAILED,next);
//				}
//			} catch (Exception e) {
//				ExceptionHandler.handleException(e);
//			}
//		}
//
//	}	
	
	//初始化运行详细信息，返回任务详细条目数
	private void initRunDetailInfo(List<Map> neList, int taskId,Map emsConnection){
		//网管Id
		int emsConnectionId = Integer.valueOf(emsConnection.get("BASE_EMS_CONNECTION_ID").toString());
		//
		HashMap  mapParam = new HashMap();
		// 删除该网管任务下的所有网元，重新插入（为防止网元新增、删除等特殊情况）
		connectionManagerMapper.deleteAllNeListByTaskId(taskId);
		// 将网管下的所有网元全部插入任务执行详细表中；
		for (Map ne : neList) {
			// 每个网元插入三条记录，因为每个网元都有三种同步操作 1.网元基础数据同步 2.交叉连接同步 3.以太网同步
			mapParam.put("SYS_TASK_RUN_DETAIL_ID", null);
			mapParam.put("TARGET_ID",
					Integer.parseInt(ne.get("BASE_NE_ID").toString()));
			mapParam.put("TARGET_NAME", ne.get("DISPLAY_NAME"));
			mapParam.put("SYS_TASK_ID", taskId);
			mapParam.put("TARGET_TYPE",
					CommonDefine.TASK_TARGET_TYPE.NE);
			// TYPE 1.网元基础数据同步 2.交叉连接同步 3.以太网同步
			mapParam.put("TYPE", null);
			connectionManagerMapper.addTaskRunDetailInfo(mapParam);
		}
		// 向任务执行详细表中插入网管级的拓扑链路同步任务
		mapParam.put("SYS_TASK_RUN_DETAIL_ID", null);
		mapParam.put("TARGET_ID", emsConnectionId);
		mapParam.put("TARGET_NAME", emsConnection.get("DISPLAY_NAME"));
		mapParam.put("SYS_TASK_ID", taskId);
		mapParam.put("TARGET_TYPE", CommonDefine.TASK_TARGET_TYPE.EMS);
		mapParam.put("TYPE", null);
		connectionManagerMapper.addTaskRunDetailInfo(mapParam);
		
		// 先将任务执行详细表中所有任务的状态更新为等待中
		southConnectionService.updateEmsSyncTaskStatus(neList,
				taskId, "","等待执行", null,  CommonDefine.FALSE,CommonDefine.SUCCESS);
		
		// 再将任务表更新执行状态为“执行中”操作
		connectionManagerMapper.updateTaskStatus(taskId,
				CommonDefine.EMS_SYNC_ING,"");
	}
	
	// 检查任务是否被暂停
	private void checkPause(int emsConnectionId, int taskId) {
		Date forbiddenTime;
		SimpleDateFormat format = new SimpleDateFormat(
				CommonDefine.COMMON_FORMAT);

		Timestamp now = new Timestamp(System.currentTimeMillis());
		// 检测是否需要暂停?是否超时
		String forbiddenTimeString = connectionManagerMapper
				.getTaskForbiddenTime(taskId);
		if (forbiddenTimeString != null) {
			// 采集暂停
			try {
				forbiddenTime = format.parse(forbiddenTimeString);
				while (true) {
					if (forbiddenTimeString == null
							|| now.getTime() >= forbiddenTime.getTime()) {
						// 当前时间已经超过需要暂停的时间,暂停时间置为null,跳出死循环
//						connectionManagerMapper
//								.deleteTaskForbiddenTime(taskId);
						try{
							southConnectionService.proceedTaskSetting(emsConnectionId, taskId);
							break;
						} catch (CommonException e) {
							e.printStackTrace();
						}
					} else {
						try {
							// 线程休眠60秒
							Thread.sleep(60 * 1 * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						forbiddenTimeString = connectionManagerMapper
								.getTaskForbiddenTime(taskId);
						if (forbiddenTimeString != null) {
							forbiddenTime = format.parse(forbiddenTimeString);
							now = new Timestamp(System.currentTimeMillis());
						}
					}
				}	
			} catch (ParseException e) {
				ExceptionHandler.handleException(e);
			}

		}
	}
}