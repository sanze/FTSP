package com.fujitsu.job;

import java.util.ArrayList;
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
import com.fujitsu.dao.mysql.CircuitManagerMapper;
import com.fujitsu.manager.southConnectionManager.thread.syncSingleNeThread;
import com.fujitsu.model.EmsConnectionModel;
import com.fujitsu.model.FutureModel;
import com.fujitsu.model.NeSyncResultModel;
import com.fujitsu.util.SpringContextUtil;

public class NotificationJob implements Job {

	private CircuitManagerMapper circuitManagerMapper;
	private ISouthConnectionService southConnectionService;
	public IDataCollectServiceProxy dataCollectService;

	public NotificationJob() {
		circuitManagerMapper = (CircuitManagerMapper) SpringContextUtil
				.getBean("circuitManagerMapper");
		southConnectionService = (ISouthConnectionService) SpringContextUtil
				.getBean("southConnectionServiceImpl");
	}


	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		//优先级为4
		Integer collectLevel = CommonDefine.COLLECT_LEVEL_4;
				
		Map map = new HashMap();
		Map update = null;
		Map select = null;
	
		System.out.println("进入quartz，增量消息循环。。。。" );
		//先处理网元级别的增量更新
		select = new HashMap();
		select.put("NAME", "t_base_ne");
		select.put("VALUE", "*");
		select.put("ID_NAME", "BASIC_SYNC_STATUS");
		select.put("ID_VALUE", CommonDefine.NE_SYNC_NEED);
		select.put("ID_NAME_2", "IS_DEL");
		select.put("ID_VALUE_2", CommonDefine.FALSE);
		
		List<Map> listNe = circuitManagerMapper.getByParameter(select);
		 //同步线程
		Callable<NeSyncResultModel>  syncThread = null;
		//结果集
		List<FutureModel> futureList = new ArrayList<FutureModel>();
		//运行结果
		NeSyncResultModel result;
		
		//是否中止标志
		boolean isStop = false;
		
		//初始化运行详细信息
		//initRunDetailInfo(neList,taskId,emsConnection);
		//任务运行状态
		int taskStatusValue;
		//网元Id
		Integer neId;
		//
		FutureModel futureModel;
		
		Integer emsConnectionId = null;
		// 提交网元同步任务
		for(Map ne:listNe){
			neId = Integer.valueOf(ne.get("BASE_NE_ID").toString());
			//同步网元
			Map param = new HashMap();
			param.put("DISPLAY_NAME", ne.get("DISPLAY_NAME"));
			param.put("SUPORT_RATES", ne.get("SUPORT_RATES"));
			param.put("BASIC_SYNC_STATUS", ne.get("BASIC_SYNC_STATUS"));

			emsConnectionId = Integer.parseInt(ne.get("BASE_EMS_CONNECTION_ID").toString());
			
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
		
	
		
		//先处理网元级别的增量更新
		select = new HashMap();
		select.put("NAME", "t_base_ems_connection");
		select.put("VALUE", "*");
		select.put("ID_NAME", "LINK_SYNC_STATUS");
		select.put("ID_VALUE", CommonDefine.NE_SYNC_NEED);
		select.put("ID_NAME_2", "IS_DEL");
		select.put("ID_VALUE_2", CommonDefine.FALSE);
		
		List<Map> listEms = circuitManagerMapper.getByParameter(select);
		for(Map ems :listEms ){
			
//			try {
//				//更新emslink状态
//				update = new HashMap();
//				update.put("NAME", "t_base_ems_connection");
//				update.put("ID_NAME", "BASE_EMS_CONNECTION_ID");
//				update.put("ID_VALUE", ems.get("BASE_EMS_CONNECTION_ID"));
//				update.put("ID_NAME_2", "LINK_SYNC_STATUS");
//				update.put("ID_VALUE_2", CommonDefine.NE_SYNC_DOING);
//
//				circuitManagerMapper.updateByParameter(update);
//				dataCollectService = SpringContextUtil
//						.getDataCollectServiceProxy(Integer.parseInt(ems.get("BASE_EMS_CONNECTION_ID").toString()));	
//					List<LinkAlterModel> changeList=new ArrayList<LinkAlterModel>();
//					List<LinkAlterModel> tmpList=dataCollectService.getLinkAlterList(collectLevel).getChangeList();
//					//去除有冲突的
//					for(LinkAlterModel tmp:tmpList){
//						if(tmp.getChangeType()==CommonDefine.CHANGE_TYPE_DELETE||
//							tmp.getConflictList()==null||
//							tmp.getConflictList().isEmpty()){
//							changeList.add(tmp);
//						}
//					}
//				
//				dataCollectService.syncLink(changeList, collectLevel);
//				// 判断在同步期间是否还有增量信息过来
//				select = new HashMap();
//				select.put("NAME", "t_base_ems_connection");
//				select.put("VALUE", "*");
//				select.put("ID_NAME", "BASE_EMS_CONNECTION_ID");
//				select.put("ID_VALUE", ems.get("BASE_EMS_CONNECTION_ID"));					
//				List<Map> listIsNeed = circuitManagerMapper.getByParameter(select);
//				if(listIsNeed!=null&&listIsNeed.size()>0){
//					if(Integer.parseInt(listIsNeed.get(0).get("LINK_SYNC_STATUS").toString())==CommonDefine.NE_SYNC_NEED){
//						// 相等则不做处理，表示下次循环需要继续同步
//					}else{
//						// 同步完成
//						update = new HashMap();
//						update.put("NAME", "t_base_ems_connection");
//						update.put("ID_NAME", "BASE_EMS_CONNECTION_ID");
//						update.put("ID_VALUE", ems.get("BASE_EMS_CONNECTION_ID"));
//						update.put("ID_NAME_2", "LINK_SYNC_STATUS");
//						update.put("ID_VALUE_2", CommonDefine.NE_SYNC_HAD);
//						circuitManagerMapper.updateByParameter(update);
//					}
//				}
//				
//				} catch (CommonException e) {
//					update = new HashMap();
//					update.put("NAME", "t_base_ems_connection");
//					update.put("ID_NAME", "BASE_EMS_CONNECTION_ID");
//					update.put("ID_VALUE", ems.get("BASE_EMS_CONNECTION_ID"));
//					update.put("ID_NAME_2", "LINK_SYNC_STATUS");
//					update.put("ID_VALUE_2", CommonDefine.NE_SYNC_FAILED);
//					circuitManagerMapper.updateByParameter(update);
//			}
			// 网管级别拓扑链路同步操作
			EmsConnectionModel emsConnectionModel=new EmsConnectionModel();
			emsConnectionModel.setEmsConnectionId(Integer.parseInt(ems.get("BASE_EMS_CONNECTION_ID").toString()));
			try {
				southConnectionService.topoLinkSync(emsConnectionModel);
			} catch (CommonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		
	}

}
