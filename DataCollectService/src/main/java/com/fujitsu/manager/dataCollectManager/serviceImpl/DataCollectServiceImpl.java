package com.fujitsu.manager.dataCollectManager.serviceImpl;

import globaldefs.NameAndStringValue_T;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;

import org.apache.commons.lang.math.RandomUtils;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;

import com.fujitsu.IService.IEMSCollect;
import com.fujitsu.IService.IFaultManagerService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.common.DataCollectDefine.COMMON;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.DataCollectMapper;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.AlarmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ClockSourceStatusModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.CrossConnectModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EProtectionGroupModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EmsDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EquipmentHolderModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EquipmentModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EquipmentOrHolderModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EthServiceModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.FdfrModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.MSTPBindingPathModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ManagedElementModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmMeasurementModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ProtectionGroupModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ProtectionSwtichDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.StateDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.SubnetworkConnectionModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TCADataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TerminationPointModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TopologicalLinkModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.VirtualBridgeModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.WDMProtectionGroupModel;
import com.fujitsu.manager.dataCollectManager.service.DataCollectService;
import com.fujitsu.manager.dataCollectManager.service.EMSCollectService;
import com.fujitsu.manager.dataCollectManager.serviceImpl.ALUCorba.ALUDataToModel;
import com.fujitsu.model.CommandModel;
import com.fujitsu.model.CommandPriorityModel;
import com.fujitsu.model.CurrentPmCollectModel;
import com.fujitsu.model.LinkAlterModel;
import com.fujitsu.model.LinkAlterResultModel;
import com.fujitsu.model.NeAlterModel;
import com.fujitsu.util.BeanUtil;
import com.fujitsu.util.CommonUtil;
import com.fujitsu.util.FileWriterUtil;
import com.fujitsu.util.FtpUtils;
import com.fujitsu.util.NameAndStringValueUtil;

/**
 * @author xuxiaojun
 * 
 */
//@Transactional(rollbackFor = Exception.class)
public class DataCollectServiceImpl extends DataCollectService {

	@Resource
	private IFaultManagerService faultManagerService;
	
	private static DataCollectMapper dataCollectMapper = (DataCollectMapper) BeanUtil
			.getBean("dataCollectMapper");

	private static NameAndStringValueUtil nameUtil = new NameAndStringValueUtil();
//考虑并发情况，不能使用静态变量
//	public static HashMap<String, Object> syncObjectPool = new HashMap<String, Object>();

	public static Map<Integer, CommandPriorityModel> commandPriorityMap = new HashMap<Integer, CommandPriorityModel>();

	private static ExecutorService executorService;

	// PM参数归一化表格暂存对象
	private static HashMap<String, Object> pmStdParameterTbl = new HashMap<String, Object>();

//	// 连接Id
//	private int emsConnectionId;
//	// 网管ip
//	private String ip;
//	// 网管厂家
//	private int factory;
//	// 网管类型
//	private int type;
//	// 内部ems名称
//	private String internalEmsName;
//	//暂停时间
//	private Date forbiddenTimeLimit;
//	//采集状态
//	private int collectStatus;
//	//超时时间
//	private Integer timeOut;
//
//	private String encode = DataCollectDefine.ENCODE_GBK;

	@Override
	@IMethodLog(desc = "DataCollectService：启动corba连接")
	public int startCorbaConnect(Map paramter) throws CommonException {
		IEMSCollect service = getInstance(paramter);
		int result = service.startCorbaConnect();
		//如果连接正常，同步网管信息
		if(result == DataCollectDefine.CONNECT_STATUS_NORMAL_FLAG){
			syncEmsInfo(paramter,1);
		}
		
		return result;
	}

	@Override
	@IMethodLog(desc = "DataCollectService：断开corba连接")
	public boolean disCorbaConnect(Map paramter) throws CommonException {
		IEMSCollect service = getInstance(paramter);
		return service.disCorbaConnect();
	}

	@Override
	@IMethodLog(desc = "DataCollectService：启动telnet连接")
	public int startTelnetConnect(Map paramter) throws CommonException {
		return 0;
	}

	@Override
	@IMethodLog(desc = "DataCollectService：断开telnet连接")
	public boolean disTelnetConnect(Map paramter) throws CommonException {
		return false;
	}

	@Override
	@IMethodLog(desc = "DataCollectService：登录telnet网元")
	public int logonTelnetNe(Map paramter,int neId) {
		return 0;
	}

	@Override
	@IMethodLog(desc = "DataCollectService：退出登录telnet网元")
	public int logoutTelnetNe(Map paramter,int neId) {
		return 0;
	}

	@Override
	@IMethodLog(desc = "DataCollectService：同步网管信息")
	public void syncEmsInfo(Map paramter,int commandLevel) throws CommonException {

		EmsDataModel emsDataInEms = (EmsDataModel) getDataFromEms(this.GET_EMS,
				null, null, paramter,commandLevel);

		Map emsConnection = new HashMap<String, String>();

		emsConnection.put("INTERNAL_EMS_NAME",
				emsDataInEms.getInternalEmsName());
		emsConnection.put("EMS_VERSION", emsDataInEms.getEmsVersion());
		emsConnection.put("IDL_VERSION", emsDataInEms.getInterfaceVersion());
		emsConnection.put("IP", getIp(paramter));

		dataCollectMapper.updateEmsConnectionByIP(emsConnection);

	}
	
	@Override
	@IMethodLog(desc = "DataCollectService：同步SNC信息")
	public void syncSNCImpl(Map paramter,int commandLevel) throws CommonException {

		List<SubnetworkConnectionModel> sncDataListInEms = (List<SubnetworkConnectionModel>) getDataFromEms(this.GET_ALL_SNC,
				null, null, paramter,commandLevel);

		// 获取数据库中SNC列表
		List<Map> sncDataListInDB = dataCollectMapper
				.selectDataListByEmsConnectionId("T_BASE_PTN_SNC", getEmsConnectionId(paramter),
						null);

		List<Map> insertSNCList = new ArrayList<Map>();

		Map snc = null;

		// 循环EMS获取的snc列表
		for (SubnetworkConnectionModel sncInEms : sncDataListInEms) {
			// 是否存在DB中标志位
			boolean isExistInDB = false;
			// 循环DB中snc列表
			for (Map sncInDB : sncDataListInDB) {
				// 更新snc
				if (sncInDB.get("NAME").toString().equals(sncInEms.getSncSerialNo())) {
					// 组织网元表数据
					snc = sncModelToTable(sncInEms, getEmsConnectionId(paramter),true);
					int sncId = (Integer) sncInDB.get("BASE_PTN_SNC_ID");
					// 加入BASE_PTN_SNC_ID
					snc.put("BASE_PTN_SNC_ID",sncId);
					// 更新网元数据
					dataCollectMapper.updateSncById(snc);
					// 设置网元存在DB标志位
					isExistInDB = true;
					// 在网元列表中移除
					sncDataListInDB.remove(sncInDB);
					break;
				}
			}
			// 新增snc
			if (!isExistInDB) {
				snc = sncModelToTable(sncInEms, getEmsConnectionId(paramter),false);
				if(snc!=null){
					insertSNCList.add(snc);
				}
			}
		}
		// 在DB中存在的ptp 但实际网管上已经没有的snc设为标记删除 IS_DEL = 1
		for (Map sncInDB : sncDataListInDB) {
			sncInDB.put("IS_DEL", DataCollectDefine.TRUE);
			dataCollectMapper.updateSncById(sncInDB);
		}
		// 插入snc数据
		if (insertSNCList.size() > 0) {
			dataCollectMapper.insertSncBatch(insertSNCList);
		}

	}
	
	
	@Override
	@IMethodLog(desc = "DataCollectService：同步ROUTE信息")
	public void syncRouteImpl(Map paramter,int commandLevel) throws CommonException {

		paramter.put(PARAM_NEED_SORT, true);
		
		List<CrossConnectModel> routeDataListInEms = (List<CrossConnectModel>) getDataFromEms(this.GET_ALL_ROUTE,
				null, null, paramter,commandLevel);

		// 获取数据库中SNC列表
		List<Map> routeDataListInDB = dataCollectMapper
				.selectDataListByEmsConnectionId("T_BASE_PTN_ROUTE", getEmsConnectionId(paramter),
						null);

		List<Map> insertRouteList = new ArrayList<Map>();

		Map route = null;

		// 循环EMS获取的snc列表
		for (CrossConnectModel routeInEms : routeDataListInEms) {
			// 是否存在DB中标志位
			boolean isExistInDB = false;
			// 循环DB中snc列表
			for (Map routeInDB : routeDataListInDB) {
				String aEndCtpInDB = routeInDB.get("A_END_CTP") == null?"":routeInDB.get("A_END_CTP").toString();
				String aEndCtpInEms =  routeInEms.getaEndNameList().length>0?nameUtil.decompositionName(routeInEms.getaEndNameList()[0]):"";
				
				String zEndCtpInDB = routeInDB.get("Z_END_CTP") == null?"":routeInDB.get("Z_END_CTP").toString();
				String zEndCtpInEms =  routeInEms.getzEndNameList().length>0?nameUtil.decompositionName(routeInEms.getzEndNameList()[0]):"";
				
				// 更新snc
				if (routeInDB.get("NAME").toString().equals(routeInEms.getBelongedTrail())&&
						aEndCtpInDB.equals(aEndCtpInEms) &&
						zEndCtpInDB.equals(zEndCtpInEms)) {
					// 组织网元表数据
					route = routeModelToTable(routeInEms, getEmsConnectionId(paramter),true);
					
					if(route ==null){
						continue;
					}
					
					int routeId = (Integer) routeInDB.get("BASE_PTN_ROUTE_ID");
					// 加入BASE_PTN_SNC_ID
					route.put("BASE_PTN_ROUTE_ID",routeId);
					// 更新网元数据
					dataCollectMapper.updateRouteById(route);
					// 设置网元存在DB标志位
					isExistInDB = true;
					// 在网元列表中移除
					routeDataListInDB.remove(routeInDB);
					break;
				}
			}
			// 新增snc
			if (!isExistInDB) {
				route = routeModelToTable(routeInEms, getEmsConnectionId(paramter),false);
				if(route!=null){
					insertRouteList.add(route);
				}
			}
		}
		// 在DB中存在的ptp 但实际网管上已经没有的snc设为标记删除 IS_DEL = 1
		for (Map routeInDB : routeDataListInDB) {
			routeInDB.put("IS_DEL", DataCollectDefine.TRUE);
			dataCollectMapper.updateRouteById(routeInDB);
		}
		// 插入snc数据
		if (insertRouteList.size() > 0) {
			dataCollectMapper.insertRouteBatch(insertRouteList);
		}

	}
	
	
	@Override
	@IMethodLog(desc = "DataCollectService：同步Fdfr信息")
	public void syncAllFdfrsImpl(Map paramter,int commandLevel) throws CommonException {
		
		List<FdfrModel> fdfrDataListInEms = (List<FdfrModel>) getDataFromEms(this.GET_ALL_FDFRS,
				null, null, paramter,commandLevel);

		// 获取数据库中SNC列表
		List<Map> fdfrDataListInDB = dataCollectMapper
				.selectDataListByEmsConnectionId("T_BASE_PTN_FDFR", getEmsConnectionId(paramter),
						null);

		List<Map> insertFdfrList = new ArrayList<Map>();

		Map fdfr = null;
		
		//删除端口列表信息
		dataCollectMapper.deletePtnFdfrListByEmsConnectionId(getEmsConnectionId(paramter));

		// 循环EMS获取的fdfr列表
		for (FdfrModel fdfrInEms : fdfrDataListInEms) {
			// 是否存在DB中标志位
			boolean isExistInDB = false;
			// 循环DB中snc列表
			for (Map fdfrInDB : fdfrDataListInDB) {
				
				// 更新snc
				if (fdfrInDB.get("NAME").toString().equals(fdfrInEms.getNameString())&&
						fdfrInDB.get("FD_NAME").toString().equals(fdfrInEms.getFdName())) {
					
					int fdfrId = (Integer) fdfrInDB.get("BASE_PTN_FD_ID");
					// 组织网元表数据
					fdfr = fdfrModelToTable(fdfrInEms, getEmsConnectionId(paramter),fdfrId,true);
					
					List<Map> fdfraEndPtpList = (List<Map>) fdfr.get("aEnd");
					List<Map> fdfrzEndPtpList = (List<Map>) fdfr.get("zEnd");
					
					fdfr.remove("aEnd");
					fdfr.remove("zEnd");
					// 更新网元数据
					dataCollectMapper.updateFdfrById(fdfr);
					//插入list数据
					if(fdfraEndPtpList.size()>0){
						dataCollectMapper.insertFdfrListBatch(fdfraEndPtpList);
					}
					if(fdfrzEndPtpList.size()>0){
						dataCollectMapper.insertFdfrListBatch(fdfrzEndPtpList);
					}
					// 设置网元存在DB标志位
					isExistInDB = true;
					// 在网元列表中移除
					fdfrDataListInDB.remove(fdfrInDB);
					break;
				}
			}
			// 新增fdfr
			if (!isExistInDB) {
				fdfr = fdfrModelToTable(fdfrInEms, getEmsConnectionId(paramter),null,false);

				if(fdfr!=null){
					insertFdfrList.add(fdfr);
				}
			}
		}
		// 在DB中存在的ptp 但实际网管上已经没有的snc设为标记删除 IS_DEL = 1
		for (Map fdfrInDB : fdfrDataListInDB) {
			fdfrInDB.put("IS_DEL", DataCollectDefine.TRUE);
			dataCollectMapper.updateFdfrById(fdfrInDB);
		}
		// 插入fdfr数据
		if (insertFdfrList.size() > 0) {
			
			for(Map data:insertFdfrList){
				
				List<Map> fdfraEndPtpList = (List<Map>) data.get("aEnd");
				List<Map> fdfrzEndPtpList = (List<Map>) data.get("zEnd");

				data.remove("aEnd");
				data.remove("zEnd");
				// 更新网元数据
				dataCollectMapper.insertFdfr(data);
				//插入list数据
				if(fdfraEndPtpList.size()>0){
					for(Map fdfrAEndPtp:fdfraEndPtpList){
						fdfrAEndPtp.put("BASE_PTN_FD_ID", data.get("BASE_PTN_FD_ID"));
					}
					dataCollectMapper.insertFdfrListBatch(fdfraEndPtpList);
				}
				if(fdfrzEndPtpList.size()>0){
					for(Map fdfrZEndPtp:fdfraEndPtpList){
						fdfrZEndPtp.put("BASE_PTN_FD_ID", data.get("BASE_PTN_FD_ID"));
					}
					dataCollectMapper.insertFdfrListBatch(fdfrzEndPtpList);
				}
				
			}
		}

	}
	
	@Override
	public void syncLinkOfFdfrsImpl(Map paramter, int commandLevel)
			throws CommonException {
		
		List<TopologicalLinkModel> linkOfFdfrsDataListInEms = (List<TopologicalLinkModel>) getDataFromEms(this.GET_ALL_LINK_OF_FDFRS,
				null, null, paramter,commandLevel);

		// 获取数据库中SNC列表
		List<Map> linkOfFdfrsDataListInDB = dataCollectMapper
				.selectDataListByEmsConnectionId("T_BASE_PTN_FDFR_LINK", getEmsConnectionId(paramter),
						null);

		List<Map> insertLinkOfFdfrsList = new ArrayList<Map>();

		Map linkOfFdfrs = null;

		// 循环EMS获取的snc列表
		for (TopologicalLinkModel linkOfFdfrsInEms : linkOfFdfrsDataListInEms) {
			// 是否存在DB中标志位
			boolean isExistInDB = false;
			// 循环DB中snc列表
			for (Map linkOfFdfrsInDB : linkOfFdfrsDataListInDB) {
				// 更新snc
				if (linkOfFdfrsInDB.get("FD_NAME").toString().equals(linkOfFdfrsInEms.getFdName())&&
						linkOfFdfrsInDB.get("FDFR_NAME").toString().equals(linkOfFdfrsInEms.getFdfrName()) &&
						linkOfFdfrsInDB.get("NAME").toString().equals(linkOfFdfrsInEms.getNameString())) {

					// 组织网元表数据
					linkOfFdfrs = linkOfFdfrModelToTable(linkOfFdfrsInEms, getEmsConnectionId(paramter),true);
					
					if(linkOfFdfrs ==null){
						continue;
					}
					int linkOfFdfrsId = (Integer) linkOfFdfrsInDB.get("BASE_PTN_FDFR_LINK_ID");
					// 加入BASE_PTN_FDFR_LINK_ID
					linkOfFdfrs.put("BASE_PTN_FDFR_LINK_ID",linkOfFdfrsId);
					// 更新网元数据
					dataCollectMapper.updateLinkOfFdfrsById(linkOfFdfrs);
					// 设置网元存在DB标志位
					isExistInDB = true;
					// 在网元列表中移除
					linkOfFdfrsDataListInDB.remove(linkOfFdfrsInDB);
					break;
				}
			}
			// 新增snc
			if (!isExistInDB) {
				linkOfFdfrs = linkOfFdfrModelToTable(linkOfFdfrsInEms, getEmsConnectionId(paramter),false);
				if(linkOfFdfrs!=null){
					insertLinkOfFdfrsList.add(linkOfFdfrs);
				}
			}
		}
		// 在DB中存在的ptp 但实际网管上已经没有的snc设为标记删除 IS_DEL = 1
		for (Map linkOfFdfrsInDB : linkOfFdfrsDataListInDB) {
			linkOfFdfrsInDB.put("IS_DEL", DataCollectDefine.TRUE);
			dataCollectMapper.updateLinkOfFdfrsById(linkOfFdfrsInDB);
		}
		// 插入snc数据
		if (insertLinkOfFdfrsList.size() > 0) {
			dataCollectMapper.insertLinkOfFdfrsBatch(insertLinkOfFdfrsList);
		}
		
	}
	
	@Override
	@IMethodLog(desc = "DataCollectService：获取网元列表")
	public List<NeAlterModel> getNeAlertList(Map paramter,int commandLevel)
			throws CommonException {

		NeAlterModel alterModel = null;

		// 更改列表详情
		List<NeAlterModel> changeList = new ArrayList<NeAlterModel>();

		List<ManagedElementModel> neListInEms = (List<ManagedElementModel>) getDataFromEms(
				this.GET_ALL_MANAGED_ELEMENT, null, null, paramter, commandLevel);

		// 获取数据库中网元列表
		List<Map> neListInDB = dataCollectMapper
				.selectDataListByEmsConnectionId("T_BASE_NE", getEmsConnectionId(paramter),
						DataCollectDefine.FALSE);
		
		Map ne = null;

		// 循环EMS网元获取的网元列表
		for (ManagedElementModel neInEms : neListInEms) {
			// 是否存在DB中标志位
			boolean isExistInDB = false;
			// 循环DB中网元列表
			for (Map neInDB : neListInDB) {
				// 网元比对--比对未转码原始字符是否相同
				if (neInDB.get("NATIVE_EMS_NAME_ORI").toString()
						.equals(neInEms.getNativeEMSNameOri())||
						neInDB.get("NAME").toString().equals(neInEms.getNeSerialNo())) {
					//无变化的网元更新网元信息，与网管一致
					// 组织网元表数据
					ne = managedElementModelToTable(neInEms, getEmsConnectionId(paramter),
							getType(paramter), getFactory(paramter), true);
					// 加入网元Id
					ne.put("BASE_NE_ID", neInDB.get("BASE_NE_ID"));
					// 更新网元数据
					dataCollectMapper.updateNeById(ne);
					// 设置网元存在DB标志位
					isExistInDB = true;
					// 在网元列表中移除
					neListInDB.remove(neInDB);
					break;
				}
			}
			// 新增网元
			if (!isExistInDB) {
				alterModel = new NeAlterModel();
				alterModel.setChangeType(1);
				alterModel.setNeName(neInEms.getNativeEMSName());
				changeList.add(alterModel);
			}
		}
		// 删除网元列表
		for (Map neInDB : neListInDB) {
			alterModel = new NeAlterModel();
			alterModel.setChangeType(2);
			alterModel.setNeName(neInDB.get("DISPLAY_NAME").toString());
			changeList.add(alterModel);
		}

		return changeList;
	}

	@Override
	@IMethodLog(desc = "DataCollectService：同步网元列表信息--仅仅网元列表信息")
	public void syncNeListImpl(Map paramter,int commandLevel) throws CommonException {
		
		List<ManagedElementModel> neListInEms = (List<ManagedElementModel>) getDataFromEms(
				this.GET_ALL_MANAGED_ELEMENT, null, null, paramter,commandLevel);

		// 获取数据库中网元列表
		List<Map> neListInDB = dataCollectMapper
				.selectDataListByEmsConnectionId("T_BASE_NE", getEmsConnectionId(paramter),
						null);

		List<Map> insertNeList = new ArrayList<Map>();

		Map ne = null;

		// 循环EMS网元获取的网元列表
		for (ManagedElementModel neInEms : neListInEms) {
			// 是否存在DB中标志位
			boolean isExistInDB = false;
			// 循环DB中网元列表
			for (Map neInDB : neListInDB) {
				// 更新网元，先核对网元名，再核对ID，都不一致情况才认为是新增
				if (neInDB.get("NATIVE_EMS_NAME_ORI").toString()
						.equals(neInEms.getNativeEMSNameOri())||
						neInDB.get("NAME").toString().equals(neInEms.getNeSerialNo())) {
					// 组织网元表数据
					ne = managedElementModelToTable(neInEms, getEmsConnectionId(paramter),
							getType(paramter), getFactory(paramter), true);
					// 加入网元Id
					ne.put("BASE_NE_ID", neInDB.get("BASE_NE_ID"));
					// 更新网元数据
					dataCollectMapper.updateNeById(ne);
					// 设置网元存在DB标志位
					isExistInDB = true;
					// 在网元列表中移除
					neListInDB.remove(neInDB);
					break;
				}
			}
			// 新增网元
			if (!isExistInDB) {
				ne = managedElementModelToTable(neInEms, getEmsConnectionId(paramter), getType(paramter),
						getFactory(paramter), false);
				insertNeList.add(ne);
			}
		}

		// 在DB中存在的网元 但实际网管上已经没有的网元设为标记删除 IS_DEL = 2
		for (Map neInDB : neListInDB) {
			neInDB.put("IS_DEL", DataCollectDefine.DELETE_FLAG);
			//通讯状态标记为离线
			neInDB.put("COMMUNICATION_STATE", DataCollectDefine.COMMON.NE_COMM_OUT_OF_SERVICE);
			dataCollectMapper.updateNeById(neInDB);
		}
		// 插入网元数据
		if (insertNeList.size() > 0) {
			dataCollectMapper.insertNeBatch(insertNeList);
		}
	}

	@Override
	@IMethodLog(desc = "DataCollectService：同步单个网元")
	public void syncSingleNeData(Map paramter,int neId, boolean isSyncPtp,
			boolean isSyncMstp, boolean isSyncInternalLink,
			boolean isSyncEprotection, boolean isSyncProtection,
			boolean isSyncWdmProtection, boolean isSyncClock,
			boolean isSyncCtp, int commandLevel) throws CommonException {

	}

	
	

	@Override
	@IMethodLog(desc = "DataCollectService：同步网元设备板卡信息")
	public void syncNeEquipmentOrHolderImpl(Map paramter,int neId, int commandLevel)
			throws CommonException {

		Map ne = dataCollectMapper.selectTableById("T_BASE_NE", "BASE_NE_ID",
				neId);
		// 获取设备信息
		List<EquipmentOrHolderModel> equipmentOrHolderListInEms = (List<EquipmentOrHolderModel>) getDataFromEms(
				this.GET_ALL_EQUIPMENT, ne, null, paramter,commandLevel);

		//BUG #3207 
/*		对策：
		1. 为避免现场网元为全空这种尴尬问题，可以做个判断。
		2. EQPT和PTP 2条采集命令，理论上不可能返回全空。 如果全空，就不更新网元数据。*/
		if(equipmentOrHolderListInEms.size() == 0){
			throw new CommonException(new NullPointerException(),
					MessageCodeDefine.CORBA_EXCUTION_EXCEPTION,"设备数据为空！");
		}

		// 获取数据库中rack信息
		List<Map> rackListInDB = dataCollectMapper.selectDataListByNeId(
				"T_BASE_RACK", neId, null);

		// 获取数据库中shelf信息
		List<Map> shelfListInDB = dataCollectMapper.selectDataListByNeId(
				"T_BASE_SHELF", neId, null);

		// 获取数据库中slot信息
		List<Map> slotListInDB = dataCollectMapper.selectDataListByNeId(
				"T_BASE_SLOT", neId, null);
		
		// 获取数据库中sub slot信息
		List<Map> subSlotListInDB = dataCollectMapper.selectDataListByNeId(
				"T_BASE_SUB_SLOT", neId, null);

		// 获取数据库中unit信息
		List<Map> unitListInDB = dataCollectMapper.selectDataListByNeId(
				"T_BASE_UNIT", neId, null);
		
		// 获取数据库中 sub unit信息
		List<Map> subUnitListInDB = dataCollectMapper.selectDataListByNeId(
				"T_BASE_SUB_UNIT", neId, null);

		List<Map> insertRackList = new ArrayList<Map>();

		List<Map> insertShelfList = new ArrayList<Map>();

		List<Map> insertSlotList = new ArrayList<Map>();
		
		List<Map> insertSubSlotList = new ArrayList<Map>();

		List<Map> insertUnitList = new ArrayList<Map>();
		
		List<Map> insertSubUnitList = new ArrayList<Map>();
		
		List<String> unitTypeList = new ArrayList<String>();

		Map rack = null;

		Map shelf = null;

		Map slot = null;
		
		Map subSlot = null;

		Map unit = null;
		
		Map subUnit = null;

		for (EquipmentOrHolderModel model : equipmentOrHolderListInEms) {
			// rack信息
			if (model.getHolder() != null
					&& DataCollectDefine.COMMON.RACK.equals(model.getHolder()
							.getHolderType())) {
				// 是否存在DB中标志位
				boolean isExistInDB = false;
				// 循环DB中rack列表
				for (Map rackInDB : rackListInDB) {
					// 更新rack
					if (rackInDB.get("RACK_NO").toString()
							.equals(model.getHolder().getRackNo())) {
						// 组织rack表数据
						rack = rackModelToTable(model.getHolder(),
								getEmsConnectionId(paramter), neId, getType(paramter), true);
						// 加入rack Id
						rack.put("BASE_RACK_ID", rackInDB.get("BASE_RACK_ID"));
						// 更新rack数据
						dataCollectMapper.updateRackById(rack);
						// 设置rack存在DB标志位
						isExistInDB = true;
						// 在rack列表中移除
						rackListInDB.remove(rackInDB);
						break;
					}
				}
				// 新增rack
				if (!isExistInDB) {
					rack = rackModelToTable(model.getHolder(), getEmsConnectionId(paramter),
							neId, getType(paramter), false);
					insertRackList.add(rack);
				}
			}

			// shelf信息
			else if (model.getHolder() != null
					&& DataCollectDefine.COMMON.SHELF.equals(model.getHolder()
							.getHolderType())) {
				// 是否存在DB中标志位
				boolean isExistInDB = false;
				// 循环DB中shelf列表
				for (Map shelfInDB : shelfListInDB) {
					// 更新shelf
					if (shelfInDB.get("RACK_NO").toString()
							.equals(model.getHolder().getRackNo())
							&& shelfInDB.get("SHELF_NO").toString()
									.equals(model.getHolder().getShelfNo())) {
						// 组织shelf表数据
						shelf = shelfModelToTable(model.getHolder(),
								getEmsConnectionId(paramter), neId, getType(paramter), true);
						// 加入shelf Id
						shelf.put("BASE_SHELF_ID",
								shelfInDB.get("BASE_SHELF_ID"));
						// 更新shelf数据
						dataCollectMapper.updateShelfById(shelf);
						// 设置shelf存在DB标志位
						isExistInDB = true;
						// 在shelf列表中移除
						shelfListInDB.remove(shelfInDB);
						break;
					}
				}
				// 新增shelf
				if (!isExistInDB) {
					shelf = shelfModelToTable(model.getHolder(),
							getEmsConnectionId(paramter), neId, getType(paramter), false);
					insertShelfList.add(shelf);
				}
			}

			// slot信息
			else if (model.getHolder() != null
					&& DataCollectDefine.COMMON.SLOT.equals(model.getHolder()
							.getHolderType())) {
				
				//添加可支持板卡列表
				unitTypeList.addAll(model.getHolder().getAcceptableEquipmentTypeList());
				
				// 是否存在DB中标志位
				boolean isExistInDB = false;
				// 循环DB中slot列表
				for (Map slotInDB : slotListInDB) {
					// 更新slot
					if (slotInDB.get("RACK_NO").toString()
							.equals(model.getHolder().getRackNo())
							&& slotInDB.get("SHELF_NO").toString()
									.equals(model.getHolder().getShelfNo())
							&& slotInDB.get("SLOT_NO").toString()
									.equals(model.getHolder().getSlotNo())) {
						// 组织slot表数据
						slot = slotModelToTable(model.getHolder(),
								getEmsConnectionId(paramter), neId, getType(paramter), true);
						// 加入slot Id
						slot.put("BASE_SLOT_ID", slotInDB.get("BASE_SLOT_ID"));
						// 更新slot数据
						dataCollectMapper.updateSlotById(slot);
						// 设置slot存在DB标志位
						isExistInDB = true;
						// 在slot列表中移除
						slotListInDB.remove(slotInDB);
						break;
					}
				}
				// 新增slot
				if (!isExistInDB) {
					slot = slotModelToTable(model.getHolder(), getEmsConnectionId(paramter),
							neId, getType(paramter), false);
					insertSlotList.add(slot);
				}
				
				//
			}
			// sub slot信息
			else if (model.getHolder() != null
					&& DataCollectDefine.COMMON.SUB_SLOT.equals(model.getHolder()
							.getHolderType())) {
				// 是否存在DB中标志位
				boolean isExistInDB = false;
				// 循环DB中sub slot列表
				for (Map subSlotInDB : subSlotListInDB) {
					// 更新sub slot
					if (subSlotInDB.get("RACK_NO").toString()
							.equals(model.getHolder().getRackNo())
							&& subSlotInDB.get("SHELF_NO").toString()
									.equals(model.getHolder().getShelfNo())
							&& subSlotInDB.get("SLOT_NO").toString()
									.equals(model.getHolder().getSlotNo())
							&& subSlotInDB.get("SUB_SLOT_NO").toString()
							.equals(model.getHolder().getSubSlotNo())) {
						// 组织sub slot表数据
						subSlot = subSlotModelToTable(model.getHolder(),
								getEmsConnectionId(paramter), neId, getType(paramter), true);
						// 加入sub slot Id
						subSlot.put("BASE_SUB_SLOT_ID", subSlotInDB.get("BASE_SUB_SLOT_ID"));
						// 更新sub slot数据
						dataCollectMapper.updateSubSlotById(subSlot);
						// 设置sub slot存在DB标志位
						isExistInDB = true;
						// 在slot列表中移除
						subSlotListInDB.remove(subSlotInDB);
						break;
					}
				}
				// 新增sub slot
				if (!isExistInDB) {
					subSlot = subSlotModelToTable(model.getHolder(), getEmsConnectionId(paramter),
							neId, getType(paramter), false);
					insertSubSlotList.add(subSlot);
				}
			}

			// unit信息
			else if (model.getEquip() != null && !model.getEquip().isSubUnit()) {
				// 是否存在DB中标志位
				boolean isExistInDB = false;
				// 循环DB中unit列表
				for (Map unitInDB : unitListInDB) {
					// 更新unit
					if (unitInDB.get("RACK_NO").toString()
							.equals(model.getEquip().getRackNo())
							&& unitInDB.get("SHELF_NO").toString()
									.equals(model.getEquip().getShelfNo())
							&& unitInDB.get("SLOT_NO").toString()
									.equals(model.getEquip().getSlotNo())) {
						// 组织unit表数据
						unit = equipModelToTable(model.getEquip(),
								getEmsConnectionId(paramter), neId, getType(paramter), true);
						// 加入unit Id
						unit.put("BASE_UNIT_ID", unitInDB.get("BASE_UNIT_ID"));
						// 更新unit数据
						dataCollectMapper.updateUnitById(unit);
						// 设置unit存在DB标志位
						isExistInDB = true;
						// 在unit列表中移除
						unitListInDB.remove(unitInDB);
						break;
					}
				}
				// 新增unit
				if (!isExistInDB) {
					unit = equipModelToTable(model.getEquip(), getEmsConnectionId(paramter),
							neId, getType(paramter), false);
					insertUnitList.add(unit);
				}
			}
			
			// sub unit信息
			else if (model.getEquip() != null && model.getEquip().isSubUnit()) {
				// 是否存在DB中标志位
				boolean isExistInDB = false;
				// 循环DB中unit列表
				for (Map subUnitInDB : subUnitListInDB) {
					// 更新sub unit
					if (subUnitInDB.get("RACK_NO").toString()
							.equals(model.getEquip().getRackNo())
							&& subUnitInDB.get("SHELF_NO").toString()
									.equals(model.getEquip().getShelfNo())
							&& subUnitInDB.get("SLOT_NO").toString()
									.equals(model.getEquip().getSlotNo())
							&& subUnitInDB.get("SUB_SLOT_NO").toString()
									.equals(model.getEquip().getSubSlotNo())) {
						// 组织sub unit表数据
						subUnit = subEquipModelToTable(model.getEquip(),
								getEmsConnectionId(paramter), ne, getType(paramter), true);
						// 加入sub unit Id
						subUnit.put("BASE_SUB_UNIT_ID", subUnitInDB.get("BASE_SUB_UNIT_ID"));
						// 更新sub unit数据
						dataCollectMapper.updateSubUnitById(subUnit);
						// 设置sub unit存在DB标志位
						isExistInDB = true;
						// 在sub unit列表中移除
						subUnitListInDB.remove(subUnitInDB);
						break;
					}
				}
				// 新增sub unit
				if (!isExistInDB) {
					subUnit = subEquipModelToTable(model.getEquip(), getEmsConnectionId(paramter),
							ne, getType(paramter), false);
					insertSubUnitList.add(subUnit);
				}
			}
		}
		// 在DB中存在的rack 但实际网管上已经没有的rack设为标记删除 IS_DEL = 1
		for (Map rackInDB : rackListInDB) {
			rackInDB.put("IS_DEL", DataCollectDefine.TRUE);
			dataCollectMapper.updateRackById(rackInDB);
		}
		if (insertRackList.size() > 0) {
			dataCollectMapper.insertRackBatch(insertRackList);
		}
		// 在DB中存在的shelf 但实际网管上已经没有的shelf设为标记删除 IS_DEL = 1
		for (Map shlefInDB : shelfListInDB) {
			shlefInDB.put("IS_DEL", DataCollectDefine.TRUE);
			dataCollectMapper.updateShelfById(shlefInDB);
		}
		if (insertShelfList.size() > 0) {
			dataCollectMapper.insertShelfBatch(insertShelfList);
		}
		// 在DB中存在的slot 但实际网管上已经没有的slot设为标记删除 IS_DEL = 1
		for (Map slotInDB : slotListInDB) {
			slotInDB.put("IS_DEL", DataCollectDefine.TRUE);
			dataCollectMapper.updateSlotById(slotInDB);
		}
		if (insertSlotList.size() > 0) {
			dataCollectMapper.insertSlotBatch(insertSlotList);
		}
		
		// 在DB中存在的sub slot 但实际网管上已经没有的sub slot设为标记删除 IS_DEL = 1
		for (Map subSlotInDB : subSlotListInDB) {
			subSlotInDB.put("IS_DEL", DataCollectDefine.TRUE);
			dataCollectMapper.updateSubSlotById(subSlotInDB);
		}
		if (insertSubSlotList.size() > 0) {
			dataCollectMapper.insertSubSlotBatch(insertSubSlotList);
		}
		// 在DB中存在的unit 但实际网管上已经没有的unit设为标记删除 IS_DEL = 1
		for (Map unitInDB : unitListInDB) {
			unitInDB.put("IS_DEL", DataCollectDefine.TRUE);
			dataCollectMapper.updateUnitById(unitInDB);
		}
		if (insertUnitList.size() > 0) {
			dataCollectMapper.insertUnitBatch(insertUnitList);
		}
		// 在DB中存在的sub unit 但实际网管上已经没有的sub unit设为标记删除 IS_DEL = 1
		for (Map subUnitInDB : subUnitListInDB) {
			subUnitInDB.put("IS_DEL", DataCollectDefine.TRUE);
			dataCollectMapper.updateSubUnitById(subUnitInDB);
		}
		if (insertSubUnitList.size() > 0) {
			dataCollectMapper.insertSubUnitBatch(insertSubUnitList);
		}
		// 添加外键关联shelf slot unit表
		addForeignKeyForEquip(neId, true, false, null);

		//插入T_NWA_UNIT_TYPE_DEFINE表
		Map data = new HashMap();
		//去除空板卡类型
		List<String> unitTypeListFinal = new ArrayList<String>();
		for(String unitType:unitTypeList){
			if(!unitType.isEmpty()){
				unitTypeListFinal.add(unitType);
			}
		}
		data.put("factory", getFactory(paramter));
		data.put("unitTypeList", unitTypeListFinal);
		data.put("createTime", new Date());
		if(unitTypeListFinal.size()>0){
			dataCollectMapper.insertUnitTypeDefineBatch(data);
		}
		
	}

	@Override
	@IMethodLog(desc = "DataCollectService：同步网元ptp信息")
	public void syncNePtpImpl(Map paramter,int neId, int commandLevel) throws CommonException {

		Map ne = dataCollectMapper.selectTableById("T_BASE_NE", "BASE_NE_ID",
				neId);

		Map ptp = null;

		Map unit = null;
		// 获取ptp信息
		List<TerminationPointModel> ptpListInEms = (List<TerminationPointModel>) getDataFromEms(
				this.GET_ALL_PTPS, ne, null, paramter,commandLevel);

		//BUG #3207 
/*		对策：
		1. 为避免现场网元为全空这种尴尬问题，可以做个判断。
		2. EQPT和PTP 2条采集命令，理论上不可能返回全空。 如果全空，就不更新网元数据。*/
		if(ptpListInEms.size() == 0){
			throw new CommonException(new NullPointerException(),
					MessageCodeDefine.CORBA_EXCUTION_EXCEPTION,"端口数据为空！");
		}

		// 获取mstp ptp信息
		List<TerminationPointModel> mstpPtpListInEms = (List<TerminationPointModel>) getDataFromEms(
				this.GET_ALL_MSTP_END_POINTS, ne, null,
				paramter,commandLevel);

		// 合并ptp信息
		ptpListInEms.addAll(mstpPtpListInEms);

		List<Map> insertPtpList = new ArrayList<Map>();

		// // 获取数据库中ptp信息--IS_DEL标记为0,domain=1,2的ptp 业务类型 1.SDH 2.WDM 3.ETH
		// 4.ATM
		// String domain = String.valueOf(DataCollectDefine.DOMAIN_SDH) + ","
		// + String.valueOf(DataCollectDefine.DOMAIN_WDM);

		List<Map> ptpListInDB = dataCollectMapper.selectPtpListByNeId(neId,
				null, null);
		int defaultDomain=DataCollectDefine.COMMON.DOMAIN_UNKNOW_FLAG;
		if(ne.get("TYPE")!=null){
			int neDomain=(Integer)ne.get("TYPE");
			switch(neDomain){
			case DataCollectDefine.COMMON.NE_TYPE_SDH_FLAG:
			case DataCollectDefine.COMMON.NE_TYPE_PTN_FLAG:
				defaultDomain=DataCollectDefine.COMMON.DOMAIN_SDH_FLAG;
				break;
			case DataCollectDefine.COMMON.NE_TYPE_WDM_FLAG:
			case DataCollectDefine.COMMON.NE_TYPE_OTN_FLAG:
			case DataCollectDefine.COMMON.NE_TYPE_FTTX_FLAG:
			case DataCollectDefine.COMMON.NE_TYPE_MICROWAVE_FLAG:
				defaultDomain=DataCollectDefine.COMMON.DOMAIN_WDM_FLAG;
				break;
			//case DataCollectDefine.COMMON.NE_TYPE_VIRTUAL_NE_FLAG:
			//case DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG:
			}
		}
		// 循环EMS网元获取的ptp列表
		for (TerminationPointModel ptpInEms : ptpListInEms) {
			// 是否存在DB中标志位
			boolean isExistInDB = false;
			// 循环DB中ptp列表
			for (Map ptpInDB : ptpListInDB) {
				// 更新ptp
				if (ptpInDB.get("NAME").toString()
						.equals(ptpInEms.getNameString())) {
					// 组织ptp表数据
					ptp = terminationPointModelToTable(ptpInEms,
							getEmsConnectionId(paramter), neId, getType(paramter), defaultDomain, true);
					// 加入ptpId
					ptp.put("BASE_PTP_ID", ptpInDB.get("BASE_PTP_ID"));
					// 更新ptp数据
					dataCollectMapper.updatePtpById(ptp);
					// 设置存在DB标志位
					isExistInDB = true;
					// 在网元列表中移除
					ptpListInDB.remove(ptpInDB);
					break;
				}
			}
			// 新增网元
			if (!isExistInDB) {
				ptp = terminationPointModelToTable(ptpInEms, getEmsConnectionId(paramter),
						neId, getType(paramter), defaultDomain, false);
				insertPtpList.add(ptp);
			}
		}
		// 在DB中存在的ptp 但实际网管上已经没有的ptp设为标记删除 IS_DEL = 1
		for (Map ptpInDB : ptpListInDB) {
			ptpInDB.put("IS_DEL", DataCollectDefine.TRUE);
			dataCollectMapper.updatePtpById(ptpInDB);
		}
		if (insertPtpList.size() > 0) {
			dataCollectMapper.insertPtpBatch(insertPtpList);
		}
		// 添加外键关联ptp表
		addForeignKeyForEquip(neId, false, true, null);

	}

	// @Override
	// @IMethodLog(desc = "DataCollectService：同步网元MSTP ptp信息")
	// public void syncNeMstpEndPoints(int neId, int commandLevel)
	// throws CommonException {
	//
	// Map ne = dataCollectMapper.selectTableById("T_BASE_NE", "BASE_NE_ID",
	// neId);
	//
	// Map ptp = null;
	// // 获取ptp信息
	// List<TerminationPointModel> ptpListInEms = (List<TerminationPointModel>)
	// getDataFromEms(
	// this.GET_ALL_MSTP_END_POINTS, ne.get("NAME").toString(), null,
	// null);
	//
	// List<Map> insertPtpList = new ArrayList<Map>();
	//
	// // // 获取数据库中ptp信息--IS_DEL标记为0,domain=eth/atm的ptp
	// // String domain = String.valueOf(DataCollectDefine.DOMAIN_ETH) + ","
	// // + String.valueOf(DataCollectDefine.DOMAIN_ATM);
	//
	// List<Map> ptpListInDB = dataCollectMapper.selectPtpListByNeId(neId,
	// null);
	//
	// // 循环EMS网元获取的ptp列表
	// for (TerminationPointModel ptpInEms : ptpListInEms) {
	// // 是否存在DB中标志位
	// boolean isExistInDB = false;
	// // 循环DB中ptp列表
	// for (Map ptpInDB : ptpListInDB) {
	// // 更新ptp
	// if (ptpInDB.get("NAME").toString()
	// .equals(ptpInEms.getNameString())) {
	// // 组织ptp表数据
	// ptp = terminationPointModelToTable(ptpInEms,
	// emsConnectionId, neId, factory, true);
	// // 加入ptpId
	// ptp.put("BASE_PTP_ID", ptpInDB.get("BASE_PTP_ID"));
	// // 更新ptp数据
	// dataCollectMapper.updatePtpById(ptp);
	// // 设置存在DB标志位
	// isExistInDB = true;
	// // 在网元列表中移除
	// ptpListInDB.remove(ptpInDB);
	// break;
	// }
	// }
	// // 新增网元
	// if (!isExistInDB) {
	// ptp = terminationPointModelToTable(ptpInEms, emsConnectionId,
	// neId, factory, false);
	// insertPtpList.add(ptp);
	// }
	// }
	// // 在DB中存在的ptp 但实际网管上已经没有的ptp设为标记删除 IS_DEL = 1
	// for (Map ptpInDB : ptpListInDB) {
	// ptpInDB.put("IS_DEL", DataCollectDefine.TRUE);
	// dataCollectMapper.updatePtpById(ptpInDB);
	// }
	// if (insertPtpList.size() > 0) {
	// dataCollectMapper.insertPtpBatch(insertPtpList);
	// }
	// // 添加外键关联PTP表
	// addForeignKeyForEquip(neId, false, true, new Integer[] {
	// DataCollectDefine.COMMON.DOMAIN_ETH_FLAG,
	// DataCollectDefine.COMMON.DOMAIN_ATM_FLAG });
	//
	// }

	@Override
	@IMethodLog(desc = "DataCollectService：同步网元equip protection信息")
	public void syncNeEProtectionGroupImpl(Map paramter,int neId, int commandLevel)
			throws CommonException {

		// 获取网元
		Map ne = dataCollectMapper.selectTableById("T_BASE_NE", "BASE_NE_ID",
				neId);

		Map eProtectionGroup = null;

		List<EProtectionGroupModel> eProtectionGroupListInEms = (List<EProtectionGroupModel>) getDataFromEms(
				this.GET_ALL_E_PROTECTION_GROUPS, ne,
				null, paramter,commandLevel);

		// 获取数据库中eProtectionGroup信息
		List<Map> eProtectionGroupListInDB = dataCollectMapper
				.selectDataListByNeId("T_BASE_E_PRO_GROUP", neId, null);

		// 循环EMS网元获取的eProtectionGroup列表
		for (EProtectionGroupModel eProtectionGroupInEms : eProtectionGroupListInEms) {
			// 是否存在DB中标志位
			boolean isExistInDB = false;
			// 循环DB中eProtectionGroup列表
			for (Map eProtectionGroupInDB : eProtectionGroupListInDB) {
				// 更新eProtectionGroup
				if (eProtectionGroupInDB.get("NAME").toString()
						.equals(eProtectionGroupInEms.getNameString())) {
					// 组织eProtectionGroup表数据
					eProtectionGroup = eProtectionGroupModelToTable(
							eProtectionGroupInEms, getEmsConnectionId(paramter), neId, getType(paramter),
							true);

					int eProtectionGroupId = (Integer) eProtectionGroupInDB
							.get("BASE_E_PRO_GROUP_ID");
					// 加入BASE_E_PRO_GROUP_ID
					eProtectionGroup.put("BASE_E_PRO_GROUP_ID",
							eProtectionGroupId);
					// 更新eProtectionGroup数据
					dataCollectMapper
							.updateEProtectionGroupById(eProtectionGroup);

					// 删除板卡列表
					dataCollectMapper
							.deleteProtectUnitByEProtectionGroupId(eProtectionGroupId);
					// 插入板卡列表
					insertProtectUnitList(neId, eProtectionGroupId, true,
							eProtectionGroupInEms.getProtectedList());
					insertProtectUnitList(neId, eProtectionGroupId, false,
							eProtectionGroupInEms.getProtectingList());

					// 设置存在DB标志位
					isExistInDB = true;
					// 在eProtectionGroup列表中移除
					eProtectionGroupListInDB.remove(eProtectionGroupInDB);
					break;
				}
			}
			// 新增eProtectionGroup
			if (!isExistInDB) {
				eProtectionGroup = eProtectionGroupModelToTable(
						eProtectionGroupInEms, getEmsConnectionId(paramter), neId, getType(paramter),
						false);
				dataCollectMapper.insertEProtectionGroup(eProtectionGroup);
				int eProtectionGroupId = Integer.valueOf(eProtectionGroup.get(
						"BASE_E_PRO_GROUP_ID").toString());
				// 插入板卡列表
				insertProtectUnitList(neId, eProtectionGroupId, true,
						eProtectionGroupInEms.getProtectedList());
				insertProtectUnitList(neId, eProtectionGroupId, false,
						eProtectionGroupInEms.getProtectingList());

			}
		}
		// 在DB中存在的eProtectionGroup 但实际网管上已经没有的eProtectionGroup设为标记删除 IS_DEL = 1
		for (Map eProtectionGroupInDB : eProtectionGroupListInDB) {
			eProtectionGroupInDB.put("IS_DEL", DataCollectDefine.TRUE);
			dataCollectMapper.updateEProtectionGroupById(eProtectionGroupInDB);
		}
	}

	@Override
	@IMethodLog(desc = "DataCollectService：同步网元环保护信息")
	public void syncNeProtectionGroupImpl(Map paramter,int neId, int commandLevel)
			throws CommonException {

		// 获取网元
		Map ne = dataCollectMapper.selectTableById("T_BASE_NE", "BASE_NE_ID",
				neId);
		
		// 获取网元类型
		int neType = ne.get("TYPE") != null ? Integer.valueOf(ne
				.get("TYPE").toString()) : DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG;
		//未知网元类型
		if(DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG == neType){
			throw new CommonException(new NullPointerException(),
					MessageCodeDefine.CORBA_UNKNOW_NE_TYPE_EXCEPTION);
		}
		//只有SDH才有环保护
		if(neType != DataCollectDefine.COMMON.NE_TYPE_SDH_FLAG){
			return;
		}

		Map protectionGroup = null;

		List<ProtectionGroupModel> protectionGroupListInEms = (List<ProtectionGroupModel>) getDataFromEms(
				this.GET_ALL_PROTECTION_GROUPS, ne,
				null, paramter,commandLevel);

		// 获取数据库中ProtectionGroup信息
		List<Map> protectionGroupListInDB = dataCollectMapper
				.selectDataListByNeId("T_BASE_PRO_GROUP", neId, null);
		// 循环EMS网元获取的ProtectionGroup列表
		for (ProtectionGroupModel protectionGroupInEms : protectionGroupListInEms) {
			// 是否存在DB中标志位
			boolean isExistInDB = false;
			// 循环DB中ProtectionGroup列表
			for (Map protectionGroupInDB : protectionGroupListInDB) {
				// 更新ProtectionGroup
				if (protectionGroupInDB.get("NAME").toString()
						.equals(protectionGroupInEms.getNameString())) {
					// 组织ProtectionGroup表数据
					protectionGroup = protectionGroupModelToTable(
							protectionGroupInEms, getEmsConnectionId(paramter), neId, getType(paramter),
							true);

					int protectionGroupId = (Integer) protectionGroupInDB
							.get("BASE_PRO_GROUP_ID");
					// 加入BASE_PRO_GROUP_ID
					protectionGroup.put("BASE_PRO_GROUP_ID", protectionGroupId);
					// 更新protectionGroup数据
					dataCollectMapper
							.updateProtectionGroupById(protectionGroup);

					// 删除ptp列表
					dataCollectMapper
							.deleteProtectPtpByProtectionGroupId(protectionGroupId);
					
					// 插入ptp列表
					insertProtectPtpList(protectionGroupInEms.getProtectionGroupType(),getFactory(paramter).intValue(),neId, protectionGroupId,
							protectionGroupInEms.getPgpTPList());

					// 设置存在DB标志位
					isExistInDB = true;
					// 在protectionGroup列表中移除
					protectionGroupListInDB.remove(protectionGroupInDB);
					break;
				}
			}
			// 新增protectionGroup
			if (!isExistInDB) {
				protectionGroup = protectionGroupModelToTable(
						protectionGroupInEms, getEmsConnectionId(paramter), neId, getType(paramter),
						false);
				dataCollectMapper.insertProtectionGroup(protectionGroup);
				int protectionGroupId = Integer.valueOf(protectionGroup.get(
						"BASE_PRO_GROUP_ID").toString());
				// 插入ptp列表
				insertProtectPtpList(protectionGroupInEms.getProtectionGroupType(),getFactory(paramter).intValue(),neId, protectionGroupId,
						protectionGroupInEms.getPgpTPList());

			}
		}
		// 在DB中存在的protectionGroup 但实际网管上已经没有的protectionGroup设为标记删除 IS_DEL = 1
		for (Map protectionGroupInDB : protectionGroupListInDB) {
			protectionGroupInDB.put("IS_DEL", DataCollectDefine.TRUE);
			dataCollectMapper.updateProtectionGroupById(protectionGroupInDB);
		}

	}

	@Override
	@IMethodLog(desc = "DataCollectService：同步网元WDM环保护信息")
	public void syncNeWDMProtectionGroupImpl(Map paramter,int neId, int commandLevel)
			throws CommonException {
		// 获取网元
		Map ne = dataCollectMapper.selectTableById("T_BASE_NE", "BASE_NE_ID",
				neId);
		
		// 获取网元类型
		int neType = ne.get("TYPE") != null ? Integer.valueOf(ne
				.get("TYPE").toString()) : DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG;
		//未知网元类型
		if(DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG == neType){
			throw new CommonException(new NullPointerException(),
					MessageCodeDefine.CORBA_UNKNOW_NE_TYPE_EXCEPTION);
		}
		//只有WDM/OTN才有wdmProtection
		if(neType != DataCollectDefine.COMMON.NE_TYPE_WDM_FLAG
				&& neType != DataCollectDefine.COMMON.NE_TYPE_OTN_FLAG){
			return;
		}

		Map wdmProtectionGroup = null;

		List<WDMProtectionGroupModel> wdmProtectionGroupListInEms = (List<WDMProtectionGroupModel>) getDataFromEms(
				this.GET_ALL_WDM_PROTECTION_GROUPS, ne,
				null, paramter,commandLevel);

		// 获取数据库中wdmProtectionGroup信息
		List<Map> wdmProtectionGroupListInDB = dataCollectMapper
				.selectDataListByNeId("T_BASE_WDM_PRO_GROUP", neId, null);

		// 循环EMS网元获取的WDMProtectionGroupModel列表
		for (WDMProtectionGroupModel wdmProtectionGroupInEms : wdmProtectionGroupListInEms) {
			// 是否存在DB中标志位
			boolean isExistInDB = false;
			// 循环DB中wdmProtectionGroup列表
			for (Map wdmProtectionGroupInDB : wdmProtectionGroupListInDB) {
				// 更新wdmProtectionGroup
				if (wdmProtectionGroupInDB.get("NAME").toString()
						.equals(wdmProtectionGroupInEms.getNameString())) {
					// 组织wdmProtectionGroup表数据
					wdmProtectionGroup = wdmProtectionGroupModelToTable(
							wdmProtectionGroupInEms, getEmsConnectionId(paramter), neId,
							getType(paramter), true);

					int wdmProtectionGroupId = Integer
							.valueOf(wdmProtectionGroupInDB.get(
									"BASE_WDM_PRO_GROUP_ID").toString());
					// 加入BASE_WDM_PRO_GROUP_ID
					wdmProtectionGroup.put("BASE_WDM_PRO_GROUP_ID",
							wdmProtectionGroupId);
					// 更新wdmProtectionGroup数据
					dataCollectMapper
							.updateWdmProtectionGroupById(wdmProtectionGroup);

					// 删除ptp列表
					dataCollectMapper
							.deleteWdmProtectPtpByProtectionGroupId(wdmProtectionGroupId);
					// 插入ptp列表
					insertWdmProtectPtpList(neId, wdmProtectionGroupId,
							wdmProtectionGroupInEms.getPgpTPList());

					// 设置存在DB标志位
					isExistInDB = true;
					// 在wdmProtectionGroup列表中移除
					wdmProtectionGroupListInDB.remove(wdmProtectionGroupInDB);
					break;
				}
			}
			// 新增wdmProtectionGroup
			if (!isExistInDB) {
				wdmProtectionGroup = wdmProtectionGroupModelToTable(
						wdmProtectionGroupInEms, getEmsConnectionId(paramter), neId, getType(paramter),
						false);
				dataCollectMapper.insertWdmProtectionGroup(wdmProtectionGroup);
				int wdmProtectionGroupId = Integer.valueOf(wdmProtectionGroup
						.get("BASE_WDM_PRO_GROUP_ID").toString());
				// 插入ptp列表
				insertWdmProtectPtpList(neId, wdmProtectionGroupId,
						wdmProtectionGroupInEms.getPgpTPList());

			}
		}
		// 在DB中存在的wdmProtectionGroup 但实际网管上已经没有的wdmProtectionGroup设为标记删除 IS_DEL
		// = 1
		for (Map wdmProtectionGroupInDB : wdmProtectionGroupListInDB) {
			wdmProtectionGroupInDB.put("IS_DEL", DataCollectDefine.TRUE);
			dataCollectMapper
					.updateWdmProtectionGroupById(wdmProtectionGroupInDB);
		}

	}

	@Override
	@IMethodLog(desc = "DataCollectService：同步网元的ctp信息")
	public void syncNeCtpImpl(Map paramter,int neId, int commandLevel) throws CommonException {
		// 获取网元
		Map ne = dataCollectMapper.selectTableById("T_BASE_NE", "BASE_NE_ID",
				neId);
		
		// 获取网元类型
		int neType = ne.get("TYPE") != null ? Integer.valueOf(ne
				.get("TYPE").toString()) : DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG;
		//未知网元类型
		if(DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG == neType){
			throw new CommonException(new NullPointerException(),
					MessageCodeDefine.CORBA_UNKNOW_NE_TYPE_EXCEPTION);
		}
		// 同步SDHctp
		else if (DataCollectDefine.COMMON.NE_TYPE_SDH_FLAG == neType) {
//		
			syncNeSdhCtp(paramter,ne, commandLevel);
		} else if(DataCollectDefine.COMMON.NE_TYPE_WDM_FLAG == neType
				||DataCollectDefine.COMMON.NE_TYPE_OTN_FLAG == neType){
			//
			syncNeOtnCtp(paramter,ne, commandLevel);
		} else if(DataCollectDefine.COMMON.NE_TYPE_PTN_FLAG == neType){
			//
			syncNePtnCtp(paramter,ne, commandLevel);
		}
	}

	@IMethodLog(desc = "DataCollectService：同步网元的sdh ctp信息")
	private void syncNeSdhCtp(Map paramter,Map ne, int commandLevel)
			throws CommonException {

		//获取网元Id
		int neId = Integer.valueOf(ne.get("BASE_NE_ID").toString());
		
		Integer[] domain = null;
		List<Map> ptpListInDB = new ArrayList<Map>();
		
		switch (getFactory(paramter).intValue()) {
		case DataCollectDefine.FACTORY_HW_FLAG:
		case DataCollectDefine.FACTORY_LUCENT_FLAG:
		case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
		case DataCollectDefine.FACTORY_ALU_FLAG:
		case DataCollectDefine.FACTORY_FUJITSU_FLAG:
			// 获取DB中的ptp列表--domain = sdh,eth,atm
			// eth不能加入网管报错[ERROR]：(1092092090) EMS内部错误)
			// Integer[] domain = new
			// Integer[]{DataCollectDefine.COMMON.DOMAIN_SDH_FLAG,
			// DataCollectDefine.COMMON.DOMAIN_ETH_FLAG,
			// DataCollectDefine.COMMON.DOMAIN_ATM_FLAG};
			domain = new Integer[] {
					DataCollectDefine.COMMON.DOMAIN_SDH_FLAG,
					DataCollectDefine.COMMON.DOMAIN_ATM_FLAG };
			ptpListInDB = dataCollectMapper.selectPtpListByNeId(neId,
					domain, DataCollectDefine.FALSE);
			break;
		case DataCollectDefine.FACTORY_ZTE_FLAG:
			// domain添加eth项
			domain = new Integer[] { 
					DataCollectDefine.COMMON.DOMAIN_SDH_FLAG,
					DataCollectDefine.COMMON.DOMAIN_ATM_FLAG,
					DataCollectDefine.COMMON.DOMAIN_ETH_FLAG };
			ptpListInDB = dataCollectMapper.selectPtpListByNeId(neId, domain,
					DataCollectDefine.FALSE);
			break;
		default:
			domain = new Integer[] {
					DataCollectDefine.COMMON.DOMAIN_SDH_FLAG,
					DataCollectDefine.COMMON.DOMAIN_ATM_FLAG };
			ptpListInDB = dataCollectMapper.selectPtpListByNeId(neId,
					domain, DataCollectDefine.FALSE);
			break;
		}
		
		// 定义文件路径
		String filePath = FileWriterUtil.BASE_FILE_PATH
				+ DataCollectDefine.COMMON.CTP + "/" + getIp(paramter) + "_"
				+ ne.get("NAME") + ".txt";

		// 如果存在同名文件，删除
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
		
		// 循环ptp列表
		for (Map ptpInDB : ptpListInDB) {
			// 是否同步过ctp标志
			boolean isSyncCtp = false;

			if (ptpInDB.get("IS_SYNC_CTP") != null) {
				isSyncCtp = DataCollectDefine.TRUE == Integer.valueOf(ptpInDB
						.get("IS_SYNC_CTP").toString()) ? true : false;
			}
			// 未同步过ctp情况
			if (!isSyncCtp) {
				// 查看是否有同步过的相同类型ptp
				/*
				 * 相同PTP条件： 1. domain=SDH 2. EMS类型、网元型号、板卡名称、PTP_TYPE一致。 添加代码补全
				 * .
				 */
				//作废，不能直接拷贝，贝尔出现问题
/*				String productName = ne.get("PRODUCT_NAME") != null ? ne.get(
						"PRODUCT_NAME").toString() : null;
				String ptpType = ptpInDB.get("PTP_TYPE") != null ? ptpInDB.get(
						"PTP_TYPE").toString() : null;
				Map unit = dataCollectMapper
						.selectTableById("T_BASE_UNIT ", "BASE_UNIT_ID",
								Integer.valueOf(ptpInDB.get("BASE_UNIT_ID")
										.toString()));

				String unitName = unit.get("UNIT_NAME") == null ? null : unit
						.get("UNIT_NAME").toString();

				Map syncPtp = dataCollectMapper.selectPtpForSyncCtp(domain,
						ptpType, getType(paramter), productName, unitName);

				// 有同步过的相同类型的ptp 直接复制ctp数据
				
				if (syncPtp != null) {
					List<Map> sdhCtps = dataCollectMapper.selectTableListById(
							"T_BASE_SDH_CTP ", "BASE_PTP_ID", Integer
									.valueOf(syncPtp.get("BASE_PTP_ID")
											.toString()));

					if (sdhCtps.size() > 0) {
						for (Map ctp : sdhCtps) {
							ctp.put("BASE_SDH_CTP_ID", null);
							ctp.put("BASE_EMS_CONNECTION_ID", ptpInDB.get("BASE_EMS_CONNECTION_ID"));
							ctp.put("BASE_NE_ID", ptpInDB.get("BASE_NE_ID"));
							ctp.put("BASE_PTP_ID", ptpInDB.get("BASE_PTP_ID"));
//							ctp.put("TOP_CTP", null);
//							ctp.put("CONNECTION_TYPE", null);
//							ctp.put("LAYER_RATE", null);
//							ctp.put("IS_SEPARATE", null);
						}
						//会导致Got error -1 from storage engine数据库报错，改为写txt方式，最后插入
//						dataCollectMapper.insertSdhCtpBatch(sdhCtps);
						// 写入txt文件
						FileWriterUtil.writeToTxtSdhCtp(filePath, sdhCtps);
					}
				} else {
					//生成ctp文件
					generateCtpFile(ptpInDB, paramter, ne, commandLevel, filePath);
				}*/
				//生成ctp文件
				generateCtpFile(ptpInDB, paramter, ne, commandLevel, filePath);
				
			} else {
				continue;
			}
		}
		// ctp数据导入数据库
		if (file.exists()) {
			dataCollectMapper.insertSdhCtpBatchTxt(filePath,DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
		}
		for (Map ptpInDB : ptpListInDB) {
			// 更新同步ctp标志
			ptpInDB.put("IS_SYNC_CTP", DataCollectDefine.TRUE);
			dataCollectMapper.updatePtpById(ptpInDB);
		}
	}
	
	//生成ctp文件
	private void generateCtpFile(Map ptpInDB, Map paramter, Map ne,
			int commandLevel, String filePath) throws CommonException {
		//网元id
		int neId = Integer.valueOf(ne.get("BASE_NE_ID").toString());
		// 采集ctp
		// 组装ptp名
		NameAndStringValue_T[] ptpName = nameUtil.constructName(
				ptpInDB.get("NAME").toString(), getIntenalEmsName(paramter), ne
						.get("NAME").toString());
		// 获取ctp信息
		List<TerminationPointModel> ctpListInEms = (List<TerminationPointModel>) getDataFromEms(
				this.GET_CONTAINED_POTENTIAL_TPS, ne, ptpName, paramter,
				commandLevel);

		//是否需要写文件
		boolean needToWriteTxt = true;
		
		for (TerminationPointModel model : ctpListInEms) {
			//设置网管id，网元id
			model.setEmsConnectionId(getEmsConnectionId(paramter));
			model.setNeId(neId);
			model.setPtpId(Integer.valueOf(ptpInDB.get("BASE_PTP_ID")
					.toString()));
			
			switch (getFactory(paramter).intValue()) {
			case DataCollectDefine.FACTORY_HW_FLAG:
			case DataCollectDefine.FACTORY_LUCENT_FLAG:
			case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
			case DataCollectDefine.FACTORY_ALU_FLAG:
			case DataCollectDefine.FACTORY_FUJITSU_FLAG:
				break;
			case DataCollectDefine.FACTORY_ZTE_FLAG:
				//PTP层速率为96？
				if (ptpInDB.get("LAYER_RATE") != null
					&& (ptpInDB.get("LAYER_RATE").toString().contains("96"))) {
					//不需要写文件
					needToWriteTxt = false;
					//step1 在t_base_ptp_virtual表中查找PTP
					//step2 如果不存在t_base_ptp_virtual表增加PTP数据
					//step3 在t_base_sdh_ctp表中新增CTP，关联到t_base_ptp_virtual表中的PTP
					String ptpNameString = nameUtil.decompositionName(nameUtil
							.getPtpNameFromCtpName(model.getName()));
					String rackNo = nameUtil.getEquipmentNoFromTargetName(
							ptpNameString, DataCollectDefine.COMMON.RACK);
					String shelfNo = nameUtil.getEquipmentNoFromTargetName(
							ptpNameString, DataCollectDefine.COMMON.SHELF);
					String slotNo = nameUtil.getEquipmentNoFromTargetName(
							ptpNameString, DataCollectDefine.COMMON.SLOT);
					String portNo = nameUtil.getEquipmentNoFromTargetName(
							ptpNameString, DataCollectDefine.COMMON.PORT);
					String ptpType = nameUtil.getEquipmentNoFromTargetName(
							ptpNameString, DataCollectDefine.ZTE.ZTE_PTP_TYPE);
					Map ptpVirtual = dataCollectMapper.selectPtpVirtualForZTE(neId,
							ptpType, rackNo, shelfNo, slotNo, portNo);
					// 不存在此条数据，插入数据
					if (ptpVirtual == null) {
						ptpVirtual = constructPtpVirtualData(getEmsConnectionId(paramter), neId,
								nameUtil.getPtpNameFromCtpName(model.getName()));
						dataCollectMapper.insertPtpVirtual(ptpVirtual);
					}
					int ptpVirtualId = Integer.valueOf(ptpVirtual.get(
							"BASE_PTP_ID").toString());
					String ctpNameString = nameUtil.decompositionCtpName(model.getName());
					// 查找ctp 关联ptp类型 1.真实ptp ,关联t_base_ptp表 2.虚拟ptp,关联t_base_ptp_virtual表
					Map ctp = dataCollectMapper.selectSdhCtpRelPtpIdAndRelPtpType(
							ptpVirtualId, ctpNameString,2);

					// 不存在此条数据，插入数据
					if (ctp == null) {
						ctp = constructSdhCtpData(getEmsConnectionId(paramter), getType(paramter), neId, ptpVirtualId,
								model.getName(),model.getRate());
						dataCollectMapper.insertSdhCtp(ctp);
					}
				}else{
					//正常流程
				}
				break;
			// 此处不填写，放在写txt文件中添加
			// model.setDisplayName(model.getNativeEMSName());
			}
		}
		// 写入txt文件
		if(needToWriteTxt){
			FileWriterUtil.writeToTxtSdhCtp(filePath, ctpListInEms,
					getType(paramter));
		}
	}

	@IMethodLog(desc = "DataCollectService：同步网元的otn ctp信息")
	private void syncNeOtnCtp(Map paramter,Map ne, int commandLevel)
			throws CommonException {
		
		//获取网元Id
		int neId = Integer.valueOf(ne.get("BASE_NE_ID").toString());

		// 获取DB中的ptp列表--domain = wdm
		Integer[] domain = new Integer[] { DataCollectDefine.COMMON.DOMAIN_WDM_FLAG };

		List<Map> ptpListInDB = dataCollectMapper.selectPtpListByNeId(neId,
				domain, DataCollectDefine.FALSE);

		// 定义文件路径
		String filePathForOtnCtp = FileWriterUtil.BASE_FILE_PATH
				+ DataCollectDefine.COMMON.CTP + "/" + getIp(paramter) + "_"
				+ ne.get("DISPLAY_NAME") + ".txt";

		// 定义文件路径
		String filePathForOtnCtpParam = FileWriterUtil.BASE_FILE_PATH
				+ DataCollectDefine.COMMON.CTP_PARAM + "/" + getIp(paramter) + "_"
				+ ne.get("DISPLAY_NAME") + ".txt";
		// 如果存在同名文件，删除
		File fileForCtp = new File(filePathForOtnCtp);
		if (fileForCtp.exists()) {
			fileForCtp.delete();
		}
		File fileForCtpParam = new File(filePathForOtnCtpParam);
		if (fileForCtpParam.exists()) {
			fileForCtpParam.delete();
		}
		
		// 循环ptp列表
		for (Map ptpInDB : ptpListInDB) {
			// 是否同步过ctp标志
			boolean isSyncCtp = false;

			if (ptpInDB.get("IS_SYNC_CTP") != null) {
				isSyncCtp = DataCollectDefine.TRUE == Integer.valueOf(ptpInDB
						.get("IS_SYNC_CTP").toString()) ? true : false;
			}
			// 未同步过ctp情况
			if (!isSyncCtp) {
				// 采集ctp
				// 组装ptp名
				NameAndStringValue_T[] ptpName = nameUtil.constructName(ptpInDB
						.get("NAME").toString(), getIntenalEmsName(paramter), ne
						.get("NAME").toString());
				// 获取ctp信息
				List<TerminationPointModel> ctpListInEms = (List<TerminationPointModel>) getDataFromEms(
						this.GET_CONTAINED_POTENTIAL_TPS, ne, ptpName, paramter,commandLevel);

				for (TerminationPointModel model : ctpListInEms) {

					model.setEmsConnectionId(getEmsConnectionId(paramter));
					model.setNeId(neId);
					model.setPtpId(Integer.valueOf(ptpInDB.get("BASE_PTP_ID")
							.toString()));
					// 截取NAME, ：/och=1/otu2=1
					model.setDisplayName(model.getCtpValue());
					
					// 写入txt文件--otn ctp参数
					FileWriterUtil
							.writeToTxtOtnCtpParam(filePathForOtnCtpParam, model
									.getTransmissionParams(),model.getPtpId(),model.getCtpValue(),getType(paramter));
					
				}
				// 写入txt文件
				FileWriterUtil
						.writeToTxtOtnCtp(filePathForOtnCtp, ctpListInEms);
			} else {
				continue;
			}
		}
		// ctp数据导入数据库
		if (fileForCtp.exists()) {
			dataCollectMapper.insertOtnCtpBatchTxt(filePathForOtnCtp, DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
		}
		// ctp数据导入数据库
		if (fileForCtpParam.exists()) {
			dataCollectMapper.insertOtnCtpParamBatchTxt(filePathForOtnCtpParam, DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
		}
		for (Map ptpInDB : ptpListInDB) {
			// 更新同步ctp标志
			ptpInDB.put("IS_SYNC_CTP", DataCollectDefine.TRUE);
			dataCollectMapper.updatePtpById(ptpInDB);
		}
	}
	
	@IMethodLog(desc = "DataCollectService：同步网元的ptn ctp信息")
	private void syncNePtnCtp(Map paramter,Map ne, int commandLevel)
			throws CommonException {
		
		//获取网元Id
		int neId = Integer.valueOf(ne.get("BASE_NE_ID").toString());

		List<Map> ptpListInDB = dataCollectMapper.selectPtpListByNeId(neId,
				null, DataCollectDefine.FALSE);

		// 定义文件路径
		String filePathForPtnCtp = FileWriterUtil.BASE_FILE_PATH
				+ DataCollectDefine.COMMON.CTP + "/" + getIp(paramter) + "_"
				+ ne.get("DISPLAY_NAME") + ".txt";

		// 如果存在同名文件，删除
		File fileForCtp = new File(filePathForPtnCtp);
		if (fileForCtp.exists()) {
			fileForCtp.delete();
		}
		
		// 循环ptp列表
		for (Map ptpInDB : ptpListInDB) {
			// 是否同步过ctp标志
			boolean isSyncCtp = false;

			if (ptpInDB.get("IS_SYNC_CTP") != null) {
				isSyncCtp = DataCollectDefine.TRUE == Integer.valueOf(ptpInDB
						.get("IS_SYNC_CTP").toString()) ? true : false;
			}
			// 未同步过ctp情况
			if (!isSyncCtp) {
				// 采集ctp
				// 组装ptp名
				NameAndStringValue_T[] ptpName = nameUtil.constructName(ptpInDB
						.get("NAME").toString(), getIntenalEmsName(paramter), ne
						.get("NAME").toString());
				// 获取ctp信息
				List<TerminationPointModel> ctpListInEms = (List<TerminationPointModel>) getDataFromEms(
						this.GET_CONTAINED_POTENTIAL_TPS, ne, ptpName, paramter,commandLevel);

				for (TerminationPointModel model : ctpListInEms) {

					model.setEmsConnectionId(getEmsConnectionId(paramter));
					model.setNeId(neId);
					model.setPtpId(Integer.valueOf(ptpInDB.get("BASE_PTP_ID")
							.toString()));
					
					String tunnleId = "";
					String pwId = "";
					
					if("1001".equals(model.getLayerRateString())){
						tunnleId = model.getName()[3].value;
						model.setPTNTP_TUNNELId(tunnleId);
					}else if("1002".equals(model.getLayerRateString())){
						pwId = model.getName()[3].value;
						model.setPTNTP_PWId(pwId);
					}
					
					String displayName = "";
					if(!tunnleId.isEmpty()){
						displayName = "Tunnel=" + tunnleId;
					}
					if(!pwId.isEmpty()){
						if(displayName.isEmpty()){
							displayName = "PW=" + pwId;
						}else{
							displayName = displayName+"/PW=" + pwId;
						}
					}
					
					if(displayName.isEmpty()){
						displayName = model.getName()[3].value;
					}
					
					// 截取NAME, ："Tunnel=" + TunnelId + "/PW=" + PWId 形式显示
					model.setDisplayName(displayName);
					
				}
				// 写入txt文件
				FileWriterUtil
						.writeToTxtPtnCtp(filePathForPtnCtp, ctpListInEms);
			} else {
				continue;
			}
		}
		// ctp数据导入数据库
		if (fileForCtp.exists()) {
			dataCollectMapper.insertPtnCtpBatchTxt(filePathForPtnCtp, DataCollectDefine.MYSQL_FILE_TERMINAL_SYMBOL);
		}
		for (Map ptpInDB : ptpListInDB) {
			// 更新同步ctp标志
			ptpInDB.put("IS_SYNC_CTP", DataCollectDefine.TRUE);
			dataCollectMapper.updatePtpById(ptpInDB);
		}
	}

	@Override
	@IMethodLog(desc = "DataCollectService：网元附加数据处理")
	public void syncExtendInfo_NE(Map paramter,int neId, int commandLevel)
			throws CommonException {

		// 获取下载至本地的缓存文件
		String tempPath = System.getProperty("java.io.tmpdir")+"information/信息对照表.xlsx";
		
		File tempFile = new File(tempPath);
		
		String sourceFilePath = this.getClass().getClassLoader().getResource("kettle").getPath()+"/信息对照表/信息对照表.xlsx";
		
		File sourceFile = new File(sourceFilePath);
		
		if(tempFile.isFile()){
			if(!CommonUtil.getFileMD5(tempFile).equals(CommonUtil.getFileMD5(sourceFile))){
				CommonUtil.copyFile(sourceFilePath,tempPath);
			}
		}else{
			CommonUtil.copyFile(sourceFilePath,tempPath);
		}
		
		//调用kettle处理数据
		String path = "kettle/syncExtendInfo_NE.ktr";
		
//			String path = "D:/FTSP3.0项目/04.MK/trunk/maven项目/DataCollectService/src/main/resources/kettle/PTN_REPORT_TRANS.ktr";
		
		//获取数据库连接参数
		Map param = BeanUtil.getDataBaseParam();
		
		//放入neId参数
		param.put("NE_ID", neId);
		
		//运行转换
		runTransfer(param,path);
		
	}
	
	
	@Override
	@IMethodLog(desc = "DataCollectService：同步网元交叉连接信息")
	public void syncNeCRSImpl(Map paramter,int neId, short[] layerRateList, int commandLevel)
			throws CommonException {

		// 获取网元
		Map ne = dataCollectMapper.selectTableById("T_BASE_NE", "BASE_NE_ID",
				neId);
		
		// 获取网元类型
		int neType = ne.get("TYPE") != null ? Integer.valueOf(ne
				.get("TYPE").toString()) : DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG;
		//未知网元类型
		if(DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG == neType){
			throw new CommonException(new NullPointerException(),
					MessageCodeDefine.CORBA_UNKNOW_NE_TYPE_EXCEPTION);
		}

		// 采所有支持交叉连接
		if (layerRateList == null) {
			String layerRate = ne.get("SUPORT_RATES").toString();

			layerRateList = nameUtil.constructLayRates(layerRate);
		}

		// 同步SDH 交叉连接
		if (DataCollectDefine.COMMON.NE_TYPE_SDH_FLAG == neType) {
			//sdh交叉
			syncNeSdhCRS(paramter,neId, layerRateList, commandLevel);
		} else if(DataCollectDefine.COMMON.NE_TYPE_WDM_FLAG == neType
				|| DataCollectDefine.COMMON.NE_TYPE_OTN_FLAG == neType){
			//wdm/otn交叉 FIXME 烽火OTN交叉 待解析
			if(getFactory(paramter)!=DataCollectDefine.FACTORY_FIBERHOME_FLAG){
				syncNeOtnCRS(paramter,neId,getType(paramter), layerRateList, commandLevel);
			}
		}else if(DataCollectDefine.COMMON.NE_TYPE_PTN_FLAG == neType){
			//中兴ptn交叉连接
			if(getFactory(paramter) ==DataCollectDefine.FACTORY_ZTE_FLAG){
				syncNePtnCRS(paramter,neId,getType(paramter), layerRateList, commandLevel);
			}
		}
	}

	@IMethodLog(desc = "DataCollectService：同步网元sdh交叉连接信息")
	private void syncNeSdhCRS(Map paramter,int neId, short[] layerRateList, int commandLevel)
			throws CommonException {

		//是否未知交叉连接类型
		boolean unknowCrsTypeException = false;
		// 获取网元
		Map ne = dataCollectMapper.selectTableById("T_BASE_NE", "BASE_NE_ID",
				neId);

		// 包装连接速率参数
		paramter.put(PARAM_LAYER_RATE, layerRateList);

		// 从网管获取交叉连接数据
		List<CrossConnectModel> crsListInEms = (List<CrossConnectModel>) getDataFromEms(
				this.GET_CRS, ne, null, paramter,commandLevel);

		// 获取数据库中crs信息,IS_VIRTUAL=0 是否虚拟交叉 0：不是 1：是
		List<Map> crsListInDB = dataCollectMapper.selectSdhCrsListByNeId(neId,
				DataCollectDefine.FALSE, null);

		List<Map> insertCrsList = new ArrayList<Map>();

		Map crs = null;
		// 循环crs列表
		for (CrossConnectModel crsInEms : crsListInEms) {

			switch (crsInEms.getCcType()){
			//0:SIMPLE型SNC 1.ADD_DROP_A 型 SNC 2.ADD_DROP_Z 型 SNC
			case 0: 
			case 1: 
			case 2: 
			// a端ctp列表
			for (NameAndStringValue_T[] aEndName : crsInEms.getaEndNameList()) {
				// a端ctp
				Map aEndCtp = null;
				// a端ptp名
				String aEndPtpName = null;
				// a端ctp名
				String aEndCtpName = null;

				// a端 ptp名称
				aEndPtpName = nameUtil.decompositionName(nameUtil
						.getPtpNameFromCtpName(aEndName));
				// a端 ctp名称
				aEndCtpName = nameUtil.decompositionCtpName(aEndName);

				// a端Ctp
				aEndCtp = dataCollectMapper.selectSdhCtp(getEmsConnectionId(paramter), neId,
						aEndPtpName, aEndCtpName);

				// z端ctp列表
				for (NameAndStringValue_T[] zEndName : crsInEms
						.getzEndNameList()) {

					// z端ctp
					Map zEndCtp = null;
					// z端ptp名
					String zEndPtpName = null;
					// z端ctp名
					String zEndCtpName = null;

					// z端 ptp名称
					zEndPtpName = nameUtil.decompositionName(nameUtil
							.getPtpNameFromCtpName(zEndName));
					// z端 ctp名称
					zEndCtpName = nameUtil.decompositionCtpName(zEndName);
					// a端Ctp
					zEndCtp = dataCollectMapper.selectSdhCtp(getEmsConnectionId(paramter),
							neId, zEndPtpName, zEndCtpName);

					// 是否需要循环DB列表
					boolean needCircleCtpInDB = true;

					// a端 ctp为空 手动插入ctp
					if (aEndCtp == null) {
						aEndCtp = constructSdhCtpData(getEmsConnectionId(paramter),getType(paramter), neId, null, 
								aEndName,crsInEms.getLayerRate());
						//ctp构建失败--继续下一个
						if(aEndCtp == null){
							continue;
						}
						dataCollectMapper.insertSdhCtp(aEndCtp);
						needCircleCtpInDB = false;
					}

					// z端 ctp为空 手动插入ctp
					if (zEndCtp == null) {
						zEndCtp = constructSdhCtpData(getEmsConnectionId(paramter), getType(paramter), neId, null, 
								zEndName,crsInEms.getLayerRate());
						//ctp构建失败--继续下一个
						if(zEndCtp == null){
							continue;
						}
						dataCollectMapper.insertSdhCtp(zEndCtp);
						needCircleCtpInDB = false;
					}

					//双向交叉,先拆分成两条,再与数据库逐一比对
					int size=1;
					if(crsInEms.getDirection()==globaldefs.ConnectionDirection_T._CD_BI){
						size=2;//双向拆分成两条
					}
					
					String rate = aEndCtp.get("CONNECT_RATE").toString();
					if(rate.isEmpty()){
						rate = zEndCtp.get("CONNECT_RATE").toString();
					}
					for(int direct=0;direct<size;direct++){
						if(direct==1){
							Map tmpMap=aEndCtp;
							aEndCtp=zEndCtp;
							zEndCtp=tmpMap;
						}
						// 是否存在DB中标志位
						boolean isExistInDB = false;
	
						// 是否需要循环列表
						if (needCircleCtpInDB) {
							// 循环DB中crs列表
							for (Map crsInDB : crsListInDB) {
								// 更新crs -- a端 z端相同
								if (Integer.valueOf(
										aEndCtp.get("BASE_SDH_CTP_ID").toString())
										.intValue() == Integer.valueOf(
										crsInDB.get("A_END_CTP").toString())
										.intValue()
										&& Integer.valueOf(
												zEndCtp.get("BASE_SDH_CTP_ID")
														.toString()).intValue() == Integer
												.valueOf(
														crsInDB.get("Z_END_CTP")
																.toString())
												.intValue()) {
									// 组织crs表数据
									crs = crossConnectModelToSdhTable(
											crsInEms,
											getEmsConnectionId(paramter),
											neId,
											Integer.valueOf(aEndCtp.get(
													"BASE_PTP_ID").toString()),
											Integer.valueOf(aEndCtp.get(
													"BASE_SDH_CTP_ID").toString()),
											Integer.valueOf(zEndCtp.get(
													"BASE_PTP_ID").toString()),
											Integer.valueOf(zEndCtp.get(
													"BASE_SDH_CTP_ID").toString()),
													rate,
											true);
									// 加入crsId
									crs.put("BASE_SDH_CRS_ID",
											crsInDB.get("BASE_SDH_CRS_ID"));
	
									// 更新crs数据
									dataCollectMapper.updateSdhCrsById(crs);
									// 设置crs存在DB标志位
									isExistInDB = true;
									// 在crs列表中移除
									crsListInDB.remove(crsInDB);
									break;
								}
							}
						}
	
						// 新增crs
						if (!isExistInDB) {
							// 组织crs表数据
							crs = crossConnectModelToSdhTable(crsInEms,
									getEmsConnectionId(paramter), neId, Integer.valueOf(aEndCtp
											.get("BASE_PTP_ID").toString()),
									Integer.valueOf(aEndCtp.get("BASE_SDH_CTP_ID")
											.toString()), Integer.valueOf(zEndCtp
											.get("BASE_PTP_ID").toString()),
									Integer.valueOf(zEndCtp.get("BASE_SDH_CTP_ID")
											.toString()), 
											rate,false);
							insertCrsList.add(crs);
							/*//如果是双向交叉，a,z端调换后保存
							if(crsInEms.getDirection() == 1){
								// 组织crs表数据
								crs = crossConnectModelToSdhTable(crsInEms,
										getEmsConnectionId(paramter), neId, Integer.valueOf(zEndCtp
												.get("BASE_PTP_ID").toString()),
										Integer.valueOf(zEndCtp.get("BASE_SDH_CTP_ID")
												.toString()), Integer.valueOf(aEndCtp
												.get("BASE_PTP_ID").toString()),
										Integer.valueOf(aEndCtp.get("BASE_SDH_CTP_ID")
												.toString()), 
										aEndCtp.get("CONNECT_RATE").toString(),false);
								insertCrsList.add(crs);
							}*/
						}
					}
				}
			}
				break;
			case 7:  //_ST_EXPLICIT
				int i = 0;
				// a端ctp列表
				for (NameAndStringValue_T[] aEndName : crsInEms.getaEndNameList()) {
					// a端ctp
					Map aEndCtp = null;
					// a端ptp名
					String aEndPtpName = null;
					// a端ctp名
					String aEndCtpName = null;

					// a端 ptp名称
					aEndPtpName = nameUtil.decompositionName(nameUtil
							.getPtpNameFromCtpName(aEndName));
					// a端 ctp名称
					aEndCtpName = nameUtil.decompositionCtpName(aEndName);

					// a端Ctp
					aEndCtp = dataCollectMapper.selectSdhCtp(getEmsConnectionId(paramter), neId,
							aEndPtpName, aEndCtpName);

					// z端ctp列表
					NameAndStringValue_T[] zEndName = crsInEms
							.getzEndNameList()[i];

						// z端ctp
						Map zEndCtp = null;
						// z端ptp名
						String zEndPtpName = null;
						// z端ctp名
						String zEndCtpName = null;

						// z端 ptp名称
						zEndPtpName = nameUtil.decompositionName(nameUtil
								.getPtpNameFromCtpName(zEndName));
						// z端 ctp名称
						zEndCtpName = nameUtil.decompositionCtpName(zEndName);
						// a端Ctp
						zEndCtp = dataCollectMapper.selectSdhCtp(getEmsConnectionId(paramter),
								neId, zEndPtpName, zEndCtpName);

						// 是否需要循环DB列表
						boolean needCircleCtpInDB = true;

						// a端 ctp为空 手动插入ctp
						if (aEndCtp == null) {
							aEndCtp = constructSdhCtpData(getEmsConnectionId(paramter), getType(paramter), neId, null, 
									aEndName,crsInEms.getLayerRate());
							//ctp构建失败--继续下一个
							if(aEndCtp == null){
								continue;
							}
							dataCollectMapper.insertSdhCtp(aEndCtp);
							needCircleCtpInDB = false;
						}

						// z端 ctp为空 手动插入ctp
						if (zEndCtp == null) {
							zEndCtp = constructSdhCtpData(getEmsConnectionId(paramter), getType(paramter), neId, null, 
									zEndName,crsInEms.getLayerRate());
							//ctp构建失败--继续下一个
							if(zEndCtp == null){
								continue;
							}
							dataCollectMapper.insertSdhCtp(zEndCtp);
							needCircleCtpInDB = false;
						}

						//双向交叉,先拆分成两条,再与数据库逐一比对
						int size=1;
						if(crsInEms.getDirection()==globaldefs.ConnectionDirection_T._CD_BI){
							size=2;//双向拆分成两条
						}
						for(int direct=0;direct<size;direct++){
							if(direct==1){
								Map tmpMap=aEndCtp;
								aEndCtp=zEndCtp;
								zEndCtp=tmpMap;
							}
							// 是否存在DB中标志位
							boolean isExistInDB = false;
		
							// 是否需要循环列表
							if (needCircleCtpInDB) {
								// 循环DB中crs列表
								for (Map crsInDB : crsListInDB) {
									// 更新crs -- a端 z端相同
									if (Integer.valueOf(
											aEndCtp.get("BASE_SDH_CTP_ID").toString())
											.intValue() == Integer.valueOf(
											crsInDB.get("A_END_CTP").toString())
											.intValue()
											&& Integer.valueOf(
													zEndCtp.get("BASE_SDH_CTP_ID")
															.toString()).intValue() == Integer
													.valueOf(
															crsInDB.get("Z_END_CTP")
																	.toString())
													.intValue()) {
										// 组织crs表数据
										crs = crossConnectModelToSdhTable(
												crsInEms,
												getEmsConnectionId(paramter),
												neId,
												Integer.valueOf(aEndCtp.get(
														"BASE_PTP_ID").toString()),
												Integer.valueOf(aEndCtp.get(
														"BASE_SDH_CTP_ID").toString()),
												Integer.valueOf(zEndCtp.get(
														"BASE_PTP_ID").toString()),
												Integer.valueOf(zEndCtp.get(
														"BASE_SDH_CTP_ID").toString()),
												aEndCtp.get("CONNECT_RATE").toString(),
												true);
										// 加入crsId
										crs.put("BASE_SDH_CRS_ID",
												crsInDB.get("BASE_SDH_CRS_ID"));
		
										// 更新crs数据
										dataCollectMapper.updateSdhCrsById(crs);
										// 设置crs存在DB标志位
										isExistInDB = true;
										// 在crs列表中移除
										crsListInDB.remove(crsInDB);
										break;
									}
								}
							}
		
							// 新增crs
							if (!isExistInDB) {
								// 组织crs表数据
								crs = crossConnectModelToSdhTable(crsInEms,
										getEmsConnectionId(paramter), neId, Integer.valueOf(aEndCtp
												.get("BASE_PTP_ID").toString()),
										Integer.valueOf(aEndCtp.get("BASE_SDH_CTP_ID")
												.toString()), Integer.valueOf(zEndCtp
												.get("BASE_PTP_ID").toString()),
										Integer.valueOf(zEndCtp.get("BASE_SDH_CTP_ID")
												.toString()), 
										aEndCtp.get("CONNECT_RATE").toString(),false);
								insertCrsList.add(crs);
								/*//如果是双向交叉，a,z端调换后保存
								if(crsInEms.getDirection() == 1){
									// 组织crs表数据
									crs = crossConnectModelToSdhTable(crsInEms,
											getEmsConnectionId(paramter), neId, Integer.valueOf(zEndCtp
													.get("BASE_PTP_ID").toString()),
											Integer.valueOf(zEndCtp.get("BASE_SDH_CTP_ID")
													.toString()), Integer.valueOf(aEndCtp
													.get("BASE_PTP_ID").toString()),
											Integer.valueOf(aEndCtp.get("BASE_SDH_CTP_ID")
													.toString()), 
											aEndCtp.get("CONNECT_RATE").toString(),false);
									insertCrsList.add(crs);
								}*/
							}
						}
						i++;
				}
				break;
			default:
				//不分析不入库
				unknowCrsTypeException = true;
			}
		}
		// 在DB中存在的crs 但实际网管上已经没有的crs设为标记删除 IS_DEL = 1
		for (Map crsInDB : crsListInDB) {
			crsInDB.put("IS_DEL", DataCollectDefine.TRUE);
			dataCollectMapper.updateSdhCrsById(crsInDB);
		}
		// 插入crs数据
		if (insertCrsList.size() > 0) {

			// 数据量可能很大，需要分批插入
			List<Map> temp = null;

			int start = 0;
			int limit = 1000;

			while (insertCrsList.size() > start) {

				if (start + limit > insertCrsList.size()) {
					temp = insertCrsList.subList(start, insertCrsList.size());
				} else {
					temp = insertCrsList.subList(start, start + limit);
				}
				start = start + limit;

				dataCollectMapper.insertSdhCrsBatch(temp);
			}
		}
		//抛出未知交叉连接类型异常
		if (unknowCrsTypeException) {
			throw new CommonException(new NullPointerException(),
					MessageCodeDefine.CORBA_UNKNOW_NE_TYPE_EXCEPTION);
		}
	}

	@IMethodLog(desc = "DataCollectService：同步网元otn交叉连接信息")
	private void syncNeOtnCRS(Map paramter,int neId, int type,short[] layerRateList, int commandLevel)
			throws CommonException {

		//是否未知交叉连接类型
		boolean unknowCrsTypeException = false;
		// 获取网元
		Map ne = dataCollectMapper.selectTableById("T_BASE_NE", "BASE_NE_ID",
				neId);

		// 包装连接速率参数
		paramter.put(PARAM_LAYER_RATE, layerRateList);

		// 从网管获取交叉连接数据
		List<CrossConnectModel> crsListInEms = (List<CrossConnectModel>) getDataFromEms(
				this.GET_CRS, ne, null, paramter,commandLevel);
		
		// 获取数据库中crs信息,IS_VIRTUAL=0 是否虚拟交叉 0：不是 1：是
		List<Map> crsListInDB = dataCollectMapper.selectOtnCrsListByNeId(neId,
				DataCollectDefine.FALSE, DataCollectDefine.FALSE, null);

		List<Map> insertCrsList = new ArrayList<Map>();

		Map crs = null;

		// 循环crs列表
		for (CrossConnectModel crsInEms : crsListInEms) {

			switch (crsInEms.getCcType()){
			//0:SIMPLE型SNC 1.ADD_DROP_A 型 SNC 2.ADD_DROP_Z 型 SNC
			case 0: 
			case 1: 
			case 2: 
			// a端ctp列表
			for (NameAndStringValue_T[] aEndName : crsInEms.getaEndNameList()) {

				// a端ctp
				Map aEndCtp = null;
				// a端ptp名
				String aEndPtpName = null;
				// a端ctp名
				String aEndCtpName = "";

				// 是否需要循环DB列表
				boolean needCircleCtpInDB = true;

				// a端 ptp名称
				aEndPtpName = nameUtil.decompositionName(nameUtil
						.getPtpNameFromCtpName(aEndName));

				// 取得a端ptp--FTP情况
				if (aEndName.length == 3) {

					aEndCtp = constructOtnCtpData(getEmsConnectionId(paramter), neId,
							aEndName, null);
					
					//ctp构建失败--继续下一个
					if(aEndCtp == null){
						continue;
					}

					if (aEndCtp.get("BASE_OTN_CTP_ID") == null) {
						dataCollectMapper.insertOtnCtp(aEndCtp);
					}
				} else {
					// a端 ctp名称
					aEndCtpName = nameUtil.decompositionCtpName(aEndName);
					// a端Ctp
					aEndCtp = dataCollectMapper.selectOtnCtp(getEmsConnectionId(paramter),
							neId, aEndPtpName, aEndCtpName);
					
					//only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
					if(aEndCtp == null && aEndPtpName.contains("ptptype")){
						aEndCtp = selectCtpForE300(getEmsConnectionId(paramter),
								neId, aEndPtpName, aEndCtpName);
					}

					// a端 ctp为空 手动插入ctp
					if (aEndCtp == null) {
						aEndCtp = constructOtnCtpData(getEmsConnectionId(paramter), neId,
								aEndName, null);
						//ctp构建失败--继续下一个
						if(aEndCtp == null){
							continue;
						}
						dataCollectMapper.insertOtnCtp(aEndCtp);
						needCircleCtpInDB = false;
					}
				}
				// z端ctp列表
				for (NameAndStringValue_T[] zEndName : crsInEms
						.getzEndNameList()) {

					// z端ctp
					Map zEndCtp = null;
					// z端ptp名
					String zEndPtpName = null;
					// z端ctp名
					String zEndCtpName = "";
					// z端 ptp名称
					zEndPtpName = nameUtil.decompositionName(nameUtil
							.getPtpNameFromCtpName(zEndName));

					// 取得z端ptp--FTP情况
					if (zEndName.length == 3) {
						zEndCtp = constructOtnCtpData(getEmsConnectionId(paramter), neId,
								zEndName, null);
						//ctp构建失败--继续下一个
						if(zEndCtp == null){
							continue;
						}
						if (zEndCtp.get("BASE_OTN_CTP_ID") == null) {
							dataCollectMapper.insertOtnCtp(zEndCtp);
						}
					} else {
						// z端 ctp名称
						zEndCtpName = nameUtil.decompositionCtpName(zEndName);
						// a端Ctp
						zEndCtp = dataCollectMapper
								.selectOtnCtp(getEmsConnectionId(paramter), neId,
										zEndPtpName, zEndCtpName);
						
						//only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
						if(zEndCtp == null && zEndPtpName.contains("ptptype")){
							zEndCtp = selectCtpForE300(getEmsConnectionId(paramter),
									neId, zEndPtpName, zEndCtpName);
						}
						
						// z端 ctp为空 手动插入ctp
						if (zEndCtp == null) {
							zEndCtp = constructOtnCtpData(getEmsConnectionId(paramter),
									neId, zEndName, null);
							//ctp构建失败--继续下一个
							if(zEndCtp == null){
								continue;
							}
							dataCollectMapper.insertOtnCtp(zEndCtp);
							needCircleCtpInDB = false;
						}
					}

					//双向link,先拆分成两条,再与数据库逐一比对
					int size=1;
					if(crsInEms.getDirection()==globaldefs.ConnectionDirection_T._CD_BI){
						size=2;//双向拆分成两条
					}
					for(int direct=0;direct<size;direct++){
						if(direct==1){
							Map tmpMap=aEndCtp;
							aEndCtp=zEndCtp;
							zEndCtp=tmpMap;
						}
						// 是否存在DB中标志位
						boolean isExistInDB = false;
	
						// 是否需要循环DB列表
						if (needCircleCtpInDB) {
							// 循环DB中crs列表
							for (Map crsInDB : crsListInDB) {
								// 更新crs -- a端 z端相同
								if (Integer.valueOf(
										aEndCtp.get("BASE_OTN_CTP_ID").toString())
										.intValue() == Integer.valueOf(
										crsInDB.get("A_END_CTP").toString())
										.intValue()
										&& Integer.valueOf(
												zEndCtp.get("BASE_OTN_CTP_ID")
														.toString()).intValue() == Integer
												.valueOf(
														crsInDB.get("Z_END_CTP")
																.toString())
												.intValue()) {
									// 组织crs表数据
									crs = crossConnectModelToOtnTable(
											crsInEms,
											getEmsConnectionId(paramter),
											neId,
											Integer.valueOf(aEndCtp.get(
													"BASE_PTP_ID").toString()),
											Integer.valueOf(aEndCtp.get(
													"BASE_OTN_CTP_ID").toString()),
											Integer.valueOf(zEndCtp.get(
													"BASE_PTP_ID").toString()),
											Integer.valueOf(zEndCtp.get(
													"BASE_OTN_CTP_ID").toString()),
											aEndCtpName, zEndCtpName, type, true);
									// 加入crsId
									crs.put("BASE_OTN_CRS_ID",
											crsInDB.get("BASE_OTN_CRS_ID"));
									// 更新crs数据
									dataCollectMapper.updateOtnCrsById(crs);
									// 设置crs存在DB标志位
									isExistInDB = true;
									// 在crs列表中移除
									crsListInDB.remove(crsInDB);
									break;
								}
							}
						}
	
						// 新增crs
						if (!isExistInDB) {
							// 组织crs表数据
							crs = crossConnectModelToOtnTable(crsInEms,
									getEmsConnectionId(paramter), neId, Integer.valueOf(aEndCtp
											.get("BASE_PTP_ID").toString()),
									Integer.valueOf(aEndCtp.get("BASE_OTN_CTP_ID")
											.toString()), Integer.valueOf(zEndCtp
											.get("BASE_PTP_ID").toString()),
									Integer.valueOf(zEndCtp.get("BASE_OTN_CTP_ID")
											.toString()), aEndCtpName, zEndCtpName,
											type, false);
							crs.put("IS_FROM_ROUTE", DataCollectDefine.FALSE);
							insertCrsList.add(crs);
							/*//如果是双向交叉，a,z端调换后保存
							if(crsInEms.getDirection() == 1){
								// 组织crs表数据
								crs = crossConnectModelToOtnTable(crsInEms,
										getEmsConnectionId(paramter), neId, Integer.valueOf(zEndCtp
												.get("BASE_PTP_ID").toString()),
										Integer.valueOf(zEndCtp.get("BASE_OTN_CTP_ID")
												.toString()), Integer.valueOf(aEndCtp
												.get("BASE_PTP_ID").toString()),
										Integer.valueOf(aEndCtp.get("BASE_OTN_CTP_ID")
												.toString()), zEndCtpName, aEndCtpName,
												type, false);
								insertCrsList.add(crs);
							}*/
						}
					}
				}
			}
				break;
			case 7:  //_ST_EXPLICIT
				int i = 0;
				// a端ctp列表
				for (NameAndStringValue_T[] aEndName : crsInEms.getaEndNameList()) {
					// a端ctp
					Map aEndCtp = null;
					// a端ptp名
					String aEndPtpName = null;
					// a端ctp名
					String aEndCtpName = "";

					// 是否需要循环DB列表
					boolean needCircleCtpInDB = true;

					// a端 ptp名称
					aEndPtpName = nameUtil.decompositionName(nameUtil
							.getPtpNameFromCtpName(aEndName));

					// 取得a端ptp--FTP情况
					if (aEndName.length == 3) {

						aEndCtp = constructOtnCtpData(getEmsConnectionId(paramter), neId,
								aEndName, null);
						
						//ctp构建失败--继续下一个
						if(aEndCtp == null){
							continue;
						}

						if (aEndCtp.get("BASE_OTN_CTP_ID") == null) {
							dataCollectMapper.insertOtnCtp(aEndCtp);
						}
					} else {
						// a端 ctp名称
						aEndCtpName = nameUtil.decompositionCtpName(aEndName);
						// a端Ctp
						aEndCtp = dataCollectMapper.selectOtnCtp(getEmsConnectionId(paramter),
								neId, aEndPtpName, aEndCtpName);
						
						//only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
						if(aEndCtp == null && aEndPtpName.contains("ptptype")){
							aEndCtp = selectCtpForE300(getEmsConnectionId(paramter),
									neId, aEndPtpName, aEndCtpName);
						}

						// a端 ctp为空 手动插入ctp
						if (aEndCtp == null) {
							aEndCtp = constructOtnCtpData(getEmsConnectionId(paramter), neId,
									aEndName, null);
							//ctp构建失败--继续下一个
							if(aEndCtp == null){
								continue;
							}
							dataCollectMapper.insertOtnCtp(aEndCtp);
							needCircleCtpInDB = false;
						}
					}
					// z端ctp列表
					NameAndStringValue_T[] zEndName = crsInEms
							.getzEndNameList()[i];
					// z端ctp
					Map zEndCtp = null;
					// z端ptp名
					String zEndPtpName = null;
					// z端ctp名
					String zEndCtpName = "";
					// z端 ptp名称
					zEndPtpName = nameUtil.decompositionName(nameUtil
							.getPtpNameFromCtpName(zEndName));

					// 取得z端ptp--FTP情况
					if (zEndName.length == 3) {
						zEndCtp = constructOtnCtpData(getEmsConnectionId(paramter), neId,
								zEndName, null);
						//ctp构建失败--继续下一个
						if(zEndCtp == null){
							continue;
						}
						if (zEndCtp.get("BASE_OTN_CTP_ID") == null) {
							dataCollectMapper.insertOtnCtp(zEndCtp);
						}
					} else {
						// z端 ctp名称
						zEndCtpName = nameUtil.decompositionCtpName(zEndName);
						// a端Ctp
						zEndCtp = dataCollectMapper
								.selectOtnCtp(getEmsConnectionId(paramter), neId,
										zEndPtpName, zEndCtpName);
						
						//only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
						if(zEndCtp == null && zEndPtpName.contains("ptptype")){
							zEndCtp = selectCtpForE300(getEmsConnectionId(paramter),
									neId, zEndPtpName, zEndCtpName);
						}
						
						// z端 ctp为空 手动插入ctp
						if (zEndCtp == null) {
							zEndCtp = constructOtnCtpData(getEmsConnectionId(paramter),
									neId, zEndName, null);
							//ctp构建失败--继续下一个
							if(zEndCtp == null){
								continue;
							}
							dataCollectMapper.insertOtnCtp(zEndCtp);
							needCircleCtpInDB = false;
						}
					}

					//双向link,先拆分成两条,再与数据库逐一比对
					int size=1;
					if(crsInEms.getDirection()==globaldefs.ConnectionDirection_T._CD_BI){
						size=2;//双向拆分成两条
					}
					for(int direct=0;direct<size;direct++){
						if(direct==1){
							Map tmpMap=aEndCtp;
							aEndCtp=zEndCtp;
							zEndCtp=tmpMap;
						}
						// 是否存在DB中标志位
						boolean isExistInDB = false;
	
						// 是否需要循环DB列表
						if (needCircleCtpInDB) {
							// 循环DB中crs列表
							for (Map crsInDB : crsListInDB) {
								// 更新crs -- a端 z端相同
								if (Integer.valueOf(
										aEndCtp.get("BASE_OTN_CTP_ID").toString())
										.intValue() == Integer.valueOf(
										crsInDB.get("A_END_CTP").toString())
										.intValue()
										&& Integer.valueOf(
												zEndCtp.get("BASE_OTN_CTP_ID")
														.toString()).intValue() == Integer
												.valueOf(
														crsInDB.get("Z_END_CTP")
																.toString())
												.intValue()) {
									// 组织crs表数据
									crs = crossConnectModelToOtnTable(
											crsInEms,
											getEmsConnectionId(paramter),
											neId,
											Integer.valueOf(aEndCtp.get(
													"BASE_PTP_ID").toString()),
											Integer.valueOf(aEndCtp.get(
													"BASE_OTN_CTP_ID").toString()),
											Integer.valueOf(zEndCtp.get(
													"BASE_PTP_ID").toString()),
											Integer.valueOf(zEndCtp.get(
													"BASE_OTN_CTP_ID").toString()),
											aEndCtpName, zEndCtpName, type, true);
									// 加入crsId
									crs.put("BASE_OTN_CRS_ID",
											crsInDB.get("BASE_OTN_CRS_ID"));
									// 更新crs数据
									dataCollectMapper.updateOtnCrsById(crs);
									// 设置crs存在DB标志位
									isExistInDB = true;
									// 在crs列表中移除
									crsListInDB.remove(crsInDB);
									break;
								}
							}
						}
	
						// 新增crs
						if (!isExistInDB) {
							// 组织crs表数据
							crs = crossConnectModelToOtnTable(crsInEms,
									getEmsConnectionId(paramter), neId, Integer.valueOf(aEndCtp
											.get("BASE_PTP_ID").toString()),
									Integer.valueOf(aEndCtp.get("BASE_OTN_CTP_ID")
											.toString()), Integer.valueOf(zEndCtp
											.get("BASE_PTP_ID").toString()),
									Integer.valueOf(zEndCtp.get("BASE_OTN_CTP_ID")
											.toString()), aEndCtpName, zEndCtpName,
											type, false);
							crs.put("IS_FROM_ROUTE", DataCollectDefine.FALSE);
							insertCrsList.add(crs);
							/*//如果是双向交叉，a,z端调换后保存
							if(crsInEms.getDirection() == 1){
								// 组织crs表数据
								crs = crossConnectModelToOtnTable(crsInEms,
										getEmsConnectionId(paramter), neId, Integer.valueOf(zEndCtp
												.get("BASE_PTP_ID").toString()),
										Integer.valueOf(zEndCtp.get("BASE_OTN_CTP_ID")
												.toString()), Integer.valueOf(aEndCtp
												.get("BASE_PTP_ID").toString()),
										Integer.valueOf(aEndCtp.get("BASE_OTN_CTP_ID")
												.toString()), zEndCtpName, aEndCtpName,
												type, false);
								insertCrsList.add(crs);
							}*/
						}
					}
					i++;
				}
				break;
			default:
				//不分析不入库
				unknowCrsTypeException = true;
			}
		}
		// 在DB中存在的crs 但实际网管上已经没有的crs设为标记删除 IS_DEL = 1
		for (Map crsInDB : crsListInDB) {
			crsInDB.put("IS_DEL", DataCollectDefine.TRUE);
			dataCollectMapper.updateOtnCrsById(crsInDB);
		}
		// 插入crs数据
		if (insertCrsList.size() > 0) {
			
			// 数据量可能很大，需要分批插入
			List<Map> temp = null;

			int start = 0;
			int limit = 1000;

			while (insertCrsList.size() > start) {

				if (start + limit > insertCrsList.size()) {
					temp = insertCrsList.subList(start, insertCrsList.size());
				} else {
					temp = insertCrsList.subList(start, start + limit);
				}
				start = start + limit;

				dataCollectMapper.insertOtnCrsBatch(temp);
			}
		}
		//抛出未知交叉连接类型异常
		if (unknowCrsTypeException) {
			throw new CommonException(new NullPointerException(),
					MessageCodeDefine.CORBA_UNKNOW_NE_TYPE_EXCEPTION);
		}
	}
	
	
	@Override
	@IMethodLog(desc = "DataCollectService：同步SNC信息从route渠道")
	public void syncCRSFromRouteImpl(Map paramter,int commandLevel) throws CommonException {
		//中兴U31网管执行
		if(DataCollectDefine.NMS_TYPE_U31_FLAG == getType(paramter)){
			syncOtnCRSFromRoute(paramter,getType(paramter),commandLevel);
		}
	}
	
	@IMethodLog(desc = "DataCollectService：同步网元otn交叉连接信息")
	private void syncOtnCRSFromRoute(Map paramter,int type, int commandLevel)
			throws CommonException {

		//是否未知交叉连接类型
		boolean unknowCrsTypeException = false;
		// 从网管获取交叉连接数据
		paramter.put(PARAM_NEED_SORT, false);
		
		List<CrossConnectModel> crsListInEms = (List<CrossConnectModel>) getDataFromEms(this.GET_ALL_ROUTE,
				null, null, paramter,commandLevel);
		
		// 获取数据库中crs信息,IS_FROM_ROUTE=0 是否route渠道同步交叉 0：不是 1：是
		List<Map> crsListInDB = dataCollectMapper.selectOtnCrsListFromRoute(
				DataCollectDefine.TRUE, null);

		List<Map> insertCrsList = new ArrayList<Map>();

		Map crs = null;

		// 循环crs列表
		for (CrossConnectModel crsInEms : crsListInEms) {

			switch (crsInEms.getCcType()){
			//0:SIMPLE型SNC 1.ADD_DROP_A 型 SNC 2.ADD_DROP_Z 型 SNC
			case 0: 
			case 1: 
			case 2: 
			// a端ctp列表
			for (NameAndStringValue_T[] aEndName : crsInEms.getaEndNameList()) {
				
				Map ne = dataCollectMapper.selectNeByNeName(getEmsConnectionId(paramter), nameUtil.getNeSerialNo(aEndName));
				
				Integer neId = null;
				//中兴 otn网元才继续
				if(ne == null || (DataCollectDefine.COMMON.NE_TYPE_OTN_FLAG != Integer.valueOf(ne
						.get("TYPE").toString()) &&
						DataCollectDefine.COMMON.NE_TYPE_WDM_FLAG != Integer.valueOf(ne
								.get("TYPE").toString()))){
					continue;
				}else{
					neId = (Integer)ne.get("BASE_NE_ID");
				}
				
				// a端ctp
				Map aEndCtp = null;
				// a端ptp名
				String aEndPtpName = null;
				// a端ctp名
				String aEndCtpName = "";

				// 是否需要循环DB列表
				boolean needCircleCtpInDB = true;

				// a端 ptp名称
				aEndPtpName = nameUtil.decompositionName(nameUtil
						.getPtpNameFromCtpName(aEndName));

				// 取得a端ptp--FTP情况
				if (aEndName.length == 3) {

					aEndCtp = constructOtnCtpData(getEmsConnectionId(paramter), neId,
							aEndName, null);
					
					//ctp构建失败--继续下一个
					if(aEndCtp == null){
						continue;
					}

					if (aEndCtp.get("BASE_OTN_CTP_ID") == null) {
						dataCollectMapper.insertOtnCtp(aEndCtp);
					}
				} else {
					// a端 ctp名称
					aEndCtpName = nameUtil.decompositionCtpName(aEndName);
					// a端Ctp
					aEndCtp = dataCollectMapper.selectOtnCtp(getEmsConnectionId(paramter),
							neId, aEndPtpName, aEndCtpName);
					
					//only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
					if(aEndCtp == null && aEndPtpName.contains("ptptype")){
						aEndCtp = selectCtpForE300(getEmsConnectionId(paramter),
								neId, aEndPtpName, aEndCtpName);
					}

					// a端 ctp为空 手动插入ctp
					if (aEndCtp == null) {
						aEndCtp = constructOtnCtpData(getEmsConnectionId(paramter), neId,
								aEndName, null);
						//ctp构建失败--继续下一个
						if(aEndCtp == null){
							continue;
						}
						dataCollectMapper.insertOtnCtp(aEndCtp);
						needCircleCtpInDB = false;
					}
				}
				// z端ctp列表
				for (NameAndStringValue_T[] zEndName : crsInEms
						.getzEndNameList()) {

					// z端ctp
					Map zEndCtp = null;
					// z端ptp名
					String zEndPtpName = null;
					// z端ctp名
					String zEndCtpName = "";
					// z端 ptp名称
					zEndPtpName = nameUtil.decompositionName(nameUtil
							.getPtpNameFromCtpName(zEndName));

					// 取得z端ptp--FTP情况
					if (zEndName.length == 3) {
						zEndCtp = constructOtnCtpData(getEmsConnectionId(paramter), neId,
								zEndName, null);
						//ctp构建失败--继续下一个
						if(zEndCtp == null){
							continue;
						}
						if (zEndCtp.get("BASE_OTN_CTP_ID") == null) {
							dataCollectMapper.insertOtnCtp(zEndCtp);
						}
					} else {
						// z端 ctp名称
						zEndCtpName = nameUtil.decompositionCtpName(zEndName);
						// a端Ctp
						zEndCtp = dataCollectMapper
								.selectOtnCtp(getEmsConnectionId(paramter), neId,
										zEndPtpName, zEndCtpName);
						
						//only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
						if(zEndCtp == null && zEndPtpName.contains("ptptype")){
							zEndCtp = selectCtpForE300(getEmsConnectionId(paramter),
									neId, zEndPtpName, zEndCtpName);
						}
						
						// z端 ctp为空 手动插入ctp
						if (zEndCtp == null) {
							zEndCtp = constructOtnCtpData(getEmsConnectionId(paramter),
									neId, zEndName, null);
							//ctp构建失败--继续下一个
							if(zEndCtp == null){
								continue;
							}
							dataCollectMapper.insertOtnCtp(zEndCtp);
							needCircleCtpInDB = false;
						}
					}

					//双向link,先拆分成两条,再与数据库逐一比对
					int size=1;
					if(crsInEms.getDirection()==globaldefs.ConnectionDirection_T._CD_BI){
						size=2;//双向拆分成两条
					}
					for(int direct=0;direct<size;direct++){
						if(direct==1){
							Map tmpMap=aEndCtp;
							aEndCtp=zEndCtp;
							zEndCtp=tmpMap;
						}
						// 是否存在DB中标志位
						boolean isExistInDB = false;
	
						// 是否需要循环DB列表
						if (needCircleCtpInDB) {
							// 循环DB中crs列表
							for (Map crsInDB : crsListInDB) {
								// 更新crs -- a端 z端相同
								if (Integer.valueOf(
										aEndCtp.get("BASE_OTN_CTP_ID").toString())
										.intValue() == Integer.valueOf(
										crsInDB.get("A_END_CTP").toString())
										.intValue()
										&& Integer.valueOf(
												zEndCtp.get("BASE_OTN_CTP_ID")
														.toString()).intValue() == Integer
												.valueOf(
														crsInDB.get("Z_END_CTP")
																.toString())
												.intValue()) {
									// 组织crs表数据
									crs = crossConnectModelToOtnTable(
											crsInEms,
											getEmsConnectionId(paramter),
											neId,
											Integer.valueOf(aEndCtp.get(
													"BASE_PTP_ID").toString()),
											Integer.valueOf(aEndCtp.get(
													"BASE_OTN_CTP_ID").toString()),
											Integer.valueOf(zEndCtp.get(
													"BASE_PTP_ID").toString()),
											Integer.valueOf(zEndCtp.get(
													"BASE_OTN_CTP_ID").toString()),
											aEndCtpName, zEndCtpName, type, true);
									// 加入crsId
									crs.put("BASE_OTN_CRS_ID",
											crsInDB.get("BASE_OTN_CRS_ID"));
									// 更新crs数据
									dataCollectMapper.updateOtnCrsById(crs);
									// 设置crs存在DB标志位
									isExistInDB = true;
									// 在crs列表中移除
									crsListInDB.remove(crsInDB);
									break;
								}
							}
						}
	
						// 新增crs
						if (!isExistInDB) {
							// 组织crs表数据
							crs = crossConnectModelToOtnTable(crsInEms,
									getEmsConnectionId(paramter), neId, Integer.valueOf(aEndCtp
											.get("BASE_PTP_ID").toString()),
									Integer.valueOf(aEndCtp.get("BASE_OTN_CTP_ID")
											.toString()), Integer.valueOf(zEndCtp
											.get("BASE_PTP_ID").toString()),
									Integer.valueOf(zEndCtp.get("BASE_OTN_CTP_ID")
											.toString()), aEndCtpName, zEndCtpName,
											type, false);
							crs.put("IS_FROM_ROUTE", DataCollectDefine.TRUE);
							insertCrsList.add(crs);
							/*//如果是双向交叉，a,z端调换后保存
							if(crsInEms.getDirection() == 1){
								// 组织crs表数据
								crs = crossConnectModelToOtnTable(crsInEms,
										getEmsConnectionId(paramter), neId, Integer.valueOf(zEndCtp
												.get("BASE_PTP_ID").toString()),
										Integer.valueOf(zEndCtp.get("BASE_OTN_CTP_ID")
												.toString()), Integer.valueOf(aEndCtp
												.get("BASE_PTP_ID").toString()),
										Integer.valueOf(aEndCtp.get("BASE_OTN_CTP_ID")
												.toString()), zEndCtpName, aEndCtpName,
												type, false);
								insertCrsList.add(crs);
							}*/
						}
					}
				}
			}
				break;
			case 7:  //_ST_EXPLICIT
				int i = 0;
				// a端ctp列表
				for (NameAndStringValue_T[] aEndName : crsInEms.getaEndNameList()) {
					Map ne = dataCollectMapper.selectNeByNeName(getEmsConnectionId(paramter), nameUtil.getNeSerialNo(aEndName));
					
					Integer neId = null;
					//中兴 otn网元才继续
					if(ne == null || (DataCollectDefine.COMMON.NE_TYPE_OTN_FLAG != Integer.valueOf(ne
							.get("TYPE").toString()) &&
							DataCollectDefine.COMMON.NE_TYPE_WDM_FLAG != Integer.valueOf(ne
									.get("TYPE").toString()))){
						continue;
					}else{
						neId = (Integer)ne.get("BASE_NE_ID");
					}
					// a端ctp
					Map aEndCtp = null;
					// a端ptp名
					String aEndPtpName = null;
					// a端ctp名
					String aEndCtpName = "";

					// 是否需要循环DB列表
					boolean needCircleCtpInDB = true;

					// a端 ptp名称
					aEndPtpName = nameUtil.decompositionName(nameUtil
							.getPtpNameFromCtpName(aEndName));

					// 取得a端ptp--FTP情况
					if (aEndName.length == 3) {

						aEndCtp = constructOtnCtpData(getEmsConnectionId(paramter), neId,
								aEndName, null);
						
						//ctp构建失败--继续下一个
						if(aEndCtp == null){
							continue;
						}

						if (aEndCtp.get("BASE_OTN_CTP_ID") == null) {
							dataCollectMapper.insertOtnCtp(aEndCtp);
						}
					} else {
						// a端 ctp名称
						aEndCtpName = nameUtil.decompositionCtpName(aEndName);
						// a端Ctp
						aEndCtp = dataCollectMapper.selectOtnCtp(getEmsConnectionId(paramter),
								neId, aEndPtpName, aEndCtpName);
						
						//only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
						if(aEndCtp == null && aEndPtpName.contains("ptptype")){
							aEndCtp = selectCtpForE300(getEmsConnectionId(paramter),
									neId, aEndPtpName, aEndCtpName);
						}

						// a端 ctp为空 手动插入ctp
						if (aEndCtp == null) {
							aEndCtp = constructOtnCtpData(getEmsConnectionId(paramter), neId,
									aEndName, null);
							//ctp构建失败--继续下一个
							if(aEndCtp == null){
								continue;
							}
							dataCollectMapper.insertOtnCtp(aEndCtp);
							needCircleCtpInDB = false;
						}
					}
					// z端ctp列表
					NameAndStringValue_T[] zEndName = crsInEms
							.getzEndNameList()[i];
					// z端ctp
					Map zEndCtp = null;
					// z端ptp名
					String zEndPtpName = null;
					// z端ctp名
					String zEndCtpName = "";
					// z端 ptp名称
					zEndPtpName = nameUtil.decompositionName(nameUtil
							.getPtpNameFromCtpName(zEndName));

					// 取得z端ptp--FTP情况
					if (zEndName.length == 3) {
						zEndCtp = constructOtnCtpData(getEmsConnectionId(paramter), neId,
								zEndName, null);
						//ctp构建失败--继续下一个
						if(zEndCtp == null){
							continue;
						}
						if (zEndCtp.get("BASE_OTN_CTP_ID") == null) {
							dataCollectMapper.insertOtnCtp(zEndCtp);
						}
					} else {
						// z端 ctp名称
						zEndCtpName = nameUtil.decompositionCtpName(zEndName);
						// a端Ctp
						zEndCtp = dataCollectMapper
								.selectOtnCtp(getEmsConnectionId(paramter), neId,
										zEndPtpName, zEndCtpName);
						
						//only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
						if(zEndCtp == null && zEndPtpName.contains("ptptype")){
							zEndCtp = selectCtpForE300(getEmsConnectionId(paramter),
									neId, zEndPtpName, zEndCtpName);
						}
						
						// z端 ctp为空 手动插入ctp
						if (zEndCtp == null) {
							zEndCtp = constructOtnCtpData(getEmsConnectionId(paramter),
									neId, zEndName, null);
							//ctp构建失败--继续下一个
							if(zEndCtp == null){
								continue;
							}
							dataCollectMapper.insertOtnCtp(zEndCtp);
							needCircleCtpInDB = false;
						}
					}

					//双向link,先拆分成两条,再与数据库逐一比对
					int size=1;
					if(crsInEms.getDirection()==globaldefs.ConnectionDirection_T._CD_BI){
						size=2;//双向拆分成两条
					}
					for(int direct=0;direct<size;direct++){
						if(direct==1){
							Map tmpMap=aEndCtp;
							aEndCtp=zEndCtp;
							zEndCtp=tmpMap;
						}
						// 是否存在DB中标志位
						boolean isExistInDB = false;
	
						// 是否需要循环DB列表
						if (needCircleCtpInDB) {
							// 循环DB中crs列表
							for (Map crsInDB : crsListInDB) {
								// 更新crs -- a端 z端相同
								if (Integer.valueOf(
										aEndCtp.get("BASE_OTN_CTP_ID").toString())
										.intValue() == Integer.valueOf(
										crsInDB.get("A_END_CTP").toString())
										.intValue()
										&& Integer.valueOf(
												zEndCtp.get("BASE_OTN_CTP_ID")
														.toString()).intValue() == Integer
												.valueOf(
														crsInDB.get("Z_END_CTP")
																.toString())
												.intValue()) {
									// 组织crs表数据
									crs = crossConnectModelToOtnTable(
											crsInEms,
											getEmsConnectionId(paramter),
											neId,
											Integer.valueOf(aEndCtp.get(
													"BASE_PTP_ID").toString()),
											Integer.valueOf(aEndCtp.get(
													"BASE_OTN_CTP_ID").toString()),
											Integer.valueOf(zEndCtp.get(
													"BASE_PTP_ID").toString()),
											Integer.valueOf(zEndCtp.get(
													"BASE_OTN_CTP_ID").toString()),
											aEndCtpName, zEndCtpName, type, true);
									// 加入crsId
									crs.put("BASE_OTN_CRS_ID",
											crsInDB.get("BASE_OTN_CRS_ID"));
									// 更新crs数据
									dataCollectMapper.updateOtnCrsById(crs);
									// 设置crs存在DB标志位
									isExistInDB = true;
									// 在crs列表中移除
									crsListInDB.remove(crsInDB);
									break;
								}
							}
						}
	
						// 新增crs
						if (!isExistInDB) {
							// 组织crs表数据
							crs = crossConnectModelToOtnTable(crsInEms,
									getEmsConnectionId(paramter), neId, Integer.valueOf(aEndCtp
											.get("BASE_PTP_ID").toString()),
									Integer.valueOf(aEndCtp.get("BASE_OTN_CTP_ID")
											.toString()), Integer.valueOf(zEndCtp
											.get("BASE_PTP_ID").toString()),
									Integer.valueOf(zEndCtp.get("BASE_OTN_CTP_ID")
											.toString()), aEndCtpName, zEndCtpName,
											type, false);
							crs.put("IS_FROM_ROUTE", DataCollectDefine.TRUE);
							
							insertCrsList.add(crs);
							/*//如果是双向交叉，a,z端调换后保存
							if(crsInEms.getDirection() == 1){
								// 组织crs表数据
								crs = crossConnectModelToOtnTable(crsInEms,
										getEmsConnectionId(paramter), neId, Integer.valueOf(zEndCtp
												.get("BASE_PTP_ID").toString()),
										Integer.valueOf(zEndCtp.get("BASE_OTN_CTP_ID")
												.toString()), Integer.valueOf(aEndCtp
												.get("BASE_PTP_ID").toString()),
										Integer.valueOf(aEndCtp.get("BASE_OTN_CTP_ID")
												.toString()), zEndCtpName, aEndCtpName,
												type, false);
								insertCrsList.add(crs);
							}*/
						}
					}
					i++;
				}
				break;
			default:
				//不分析不入库
				unknowCrsTypeException = true;
			}
		}
		// 在DB中存在的crs 但实际网管上已经没有的crs设为标记删除 IS_DEL = 1
		for (Map crsInDB : crsListInDB) {
			crsInDB.put("IS_DEL", DataCollectDefine.TRUE);
			dataCollectMapper.updateOtnCrsById(crsInDB);
		}
		// 插入crs数据
		if (insertCrsList.size() > 0) {
			
			// 数据量可能很大，需要分批插入
			List<Map> temp = null;

			int start = 0;
			int limit = 1000;

			while (insertCrsList.size() > start) {

				if (start + limit > insertCrsList.size()) {
					temp = insertCrsList.subList(start, insertCrsList.size());
				} else {
					temp = insertCrsList.subList(start, start + limit);
				}
				start = start + limit;

				dataCollectMapper.insertOtnCrsBatch(temp);
			}
		}
		//抛出未知交叉连接类型异常
		if (unknowCrsTypeException) {
			throw new CommonException(new NullPointerException(),
					MessageCodeDefine.CORBA_UNKNOW_NE_TYPE_EXCEPTION);
		}
	}
	
	
	@IMethodLog(desc = "DataCollectService：同步网元ptn交叉连接信息")
	private void syncNePtnCRS(Map paramter,int neId, int type,short[] layerRateList, int commandLevel)
			throws CommonException {

		//是否未知交叉连接类型
		boolean unknowCrsTypeException = false;
		// 获取网元
		Map ne = dataCollectMapper.selectTableById("T_BASE_NE", "BASE_NE_ID",
				neId);

		// 包装连接速率参数
		paramter.put(PARAM_LAYER_RATE, layerRateList);

		// 从网管获取交叉连接数据
		List<CrossConnectModel> crsListInEms = (List<CrossConnectModel>) getDataFromEms(
				this.GET_CRS, ne, null, paramter,commandLevel);
		//ctp列表
		List<Map> ptnCtpList = new ArrayList<Map>();
		//ctp名称列表
		Map<String,NameAndStringValue_T[]> ctpNameList = new HashMap<String,NameAndStringValue_T[]>();
		
		Map<String,Map> ptpList = new HashMap<String,Map>();
		
		//获取a z端ctp，组装成ptn数据
		for(CrossConnectModel cc:crsListInEms){
			for(NameAndStringValue_T[] endName:cc.getaEndNameList()){
				String ctpNameKey = nameUtil.decompositionName(endName);
				if(!ctpNameList.containsKey(ctpNameKey)){
					
					String ptpName = nameUtil.decompositionName(nameUtil.getPtpNameFromCtpName(endName));
					
					Map ptp = null;
					
					if(ptpList.containsKey(ptpName)){
						ptp = ptpList.get(ptpName);
					}else{
						ptp = dataCollectMapper.selectPtpByNeIdAndPtpName(neId, ptpName);
						ptpList.put(ptpName, ptp);
					}
					
					if(ptp!=null){
						Integer ptpId = Integer.valueOf(ptp.get("BASE_PTP_ID").toString());
						Map ptnCtp = constructPtnCtpData(getEmsConnectionId(paramter),neId,ptpId,endName,cc);
						dataCollectMapper.insertOrUpdatePtnCtp(ptnCtp);
					}
					ctpNameList.put(ctpNameKey, endName);
				}
			}
			for(NameAndStringValue_T[] endName:cc.getzEndNameList()){
				String ctpNameKey = nameUtil.decompositionName(endName);
				if(!ctpNameList.containsKey(ctpNameKey)){
					
					String ptpName = nameUtil.decompositionName(nameUtil.getPtpNameFromCtpName(endName));
					
					Map ptp = null;
					
					if(ptpList.containsKey(ptpName)){
						ptp = ptpList.get(ptpName);
					}else{
						ptp = dataCollectMapper.selectPtpByNeIdAndPtpName(neId, ptpName);
						ptpList.put(ptpName, ptp);
					}
					
					if(ptp!=null){
						Integer ptpId = Integer.valueOf(ptp.get("BASE_PTP_ID").toString());
						Map ptnCtp = constructPtnCtpData(getEmsConnectionId(paramter),neId,ptpId,endName,cc);
						dataCollectMapper.insertOrUpdatePtnCtp(ptnCtp);
					}
					ctpNameList.put(ctpNameKey, endName);
				}
			}
		}
	}
	
	@IMethodLog(desc = "DataCollectService：生成PTN CTP对象map")
	private Map constructPtnCtpData(int emsConnectionId, int neId, Integer ptpId, 
			NameAndStringValue_T[] ctpName,CrossConnectModel cc) {
		
		Map ptnCtp = new HashMap();

		String tunnleId = nameUtil.getEquipmentNoFromTargetName(
				ctpName[3].value, DataCollectDefine.COMMON.TMPCTP);
		if (tunnleId.isEmpty()) {
			tunnleId = nameUtil.getEquipmentNoFromTargetName(ctpName[3].value,
					DataCollectDefine.COMMON.TMP);
		}

		String pwId = nameUtil.getEquipmentNoFromTargetName(ctpName[3].value,
				DataCollectDefine.COMMON.PW);

		//设置tunnleId
		if(tunnleId == null || tunnleId.isEmpty()){
			if("1001".equals(cc.getLayerRate())){
				tunnleId = ctpName[3].value;
			}
		}
		//设置pwId
		if(pwId == null || pwId.isEmpty()){
			if("1002".equals(cc.getLayerRate())){
				pwId = ctpName[3].value;
			}
		}

		ptnCtp.put("BASE_PTN_CTP_ID", null);
		ptnCtp.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
		ptnCtp.put("BASE_NE_ID", neId);
		ptnCtp.put("BASE_PTP_ID", ptpId);
		
		String displayName = "";
		if(!tunnleId.isEmpty()){
			displayName = "Tunnel=" + tunnleId;
		}
		if(!pwId.isEmpty()){
			if(displayName.isEmpty()){
				displayName = "PW=" + pwId;
			}else{
				displayName = displayName+"/PW=" + pwId;
			}
		}
		
		if(displayName.isEmpty()){
			displayName = ctpName[3].value;
		}
		
		ptnCtp.put("NAME", nameUtil.decompositionCtpName(ctpName));
		
		ptnCtp.put("DISPLAY_NAME", displayName);
		ptnCtp.put("OWNER", null);
		
		ptnCtp.put("TUNNEL_ID", tunnleId);
		ptnCtp.put("PW_ID", pwId);
		
		ptnCtp.put("USER_LABEL", cc.getUserLabel());
		ptnCtp.put("NATIVE_EMS_NAME", cc.getNativeEMSName());
		ptnCtp.put("LAYER_RATE", cc.getLayerRate());
		ptnCtp.put("LSP_TYPE", cc.getLSPType());
		ptnCtp.put("PW_TYPE", cc.getPWType());
		ptnCtp.put("SRC_IN_LABEL", cc.getSrcInLabel());
		ptnCtp.put("SRC_OUT_LABEL", cc.getSrcOutLabel());
		ptnCtp.put("DEST_IN_LABEL", cc.getDestInLabel());
		ptnCtp.put("DEST_OUT_LABEL", cc.getDestOutLabel());
		ptnCtp.put("SRC_IP", cc.getSrcIP());
		ptnCtp.put("DEST_IP", cc.getDestIP());
		ptnCtp.put("BELONGED_TRAIL", cc.getBelongedTrail());
		
		ptnCtp.put("IS_DEL", DataCollectDefine.FALSE);
		ptnCtp.put("CREATE_TIME", new Date());
		return ptnCtp;
	}

	@Override
	@IMethodLog(desc = "DataCollectService：预同步网元Link信息")
	public LinkAlterResultModel getLinkAlterListImpl(Map paramter,int commandLevel)
			throws CommonException {

		// 预同步结果返回
		LinkAlterResultModel linkAlterResultModel = new LinkAlterResultModel();

		linkAlterResultModel.setNeedSyncEms(false);
		linkAlterResultModel.setChanged(false);
		linkAlterResultModel.setNeedSyncNe(false);
		// 更改列表详情
		List<LinkAlterModel> changeList = new ArrayList<LinkAlterModel>();
		// 更改对象
		LinkAlterModel linkAlterModel = null;
		// 需要同步网元列表
		List<Integer> syncNeList = new ArrayList<Integer>();
		// 网管采集link列表
		List<TopologicalLinkModel> linkListInEms = (List<TopologicalLinkModel>) getDataFromEms(
				this.GET_ALL_TOPOLOGICAL_LINKS, null, null, paramter,commandLevel);

		// 获取数据库中link信息,linkType=1 1.外部link 2.内部link 3.手工link
		Integer[] linkType = null;
		//判断网管类型 除t2000,U2000外 其他外部link中可能包含内部link，需要一同对比，t2000,U2000的外部link中包含的内部link数据做丢弃处理
		switch (getType(paramter)) {
		case DataCollectDefine.NMS_TYPE_T2000_FLAG:
		case DataCollectDefine.NMS_TYPE_U2000_FLAG:
			linkType = new Integer[] { DataCollectDefine.LINK_TYPE_EXTERNAL_FLAG };
			break;
		case DataCollectDefine.NMS_TYPE_E300_FLAG:
		case DataCollectDefine.NMS_TYPE_U31_FLAG:
		case DataCollectDefine.NMS_TYPE_LUCENT_OMS_FLAG:
		case DataCollectDefine.NMS_TYPE_OTNM2000_FLAG:
		case DataCollectDefine.NMS_TYPE_ALU_FLAG:
			linkType = new Integer[] { DataCollectDefine.LINK_TYPE_EXTERNAL_FLAG,DataCollectDefine.LINK_TYPE_INTERNAL_FLAG};
			break;
		default:
			linkType = new Integer[] { DataCollectDefine.LINK_TYPE_EXTERNAL_FLAG };
		}
		// 数据库中link列表
		List<Map> linkListInDB = dataCollectMapper
				.selectLinkListByEmsConnectionId(getEmsConnectionId(paramter), null,
						linkType, DataCollectDefine.FALSE);
		// 网元对象
		Map ne = null;

		Map aEndPtp = null;
		Map zEndPtp = null;

		// 循环EMS获取的link列表
		for (TopologicalLinkModel linkInEms : linkListInEms) {
			//贝尔link发现有link一端为ctp的情况，无法正常入库，数据舍弃
			if(linkInEms.getaEndTP().length > 3 || linkInEms.getzEndTP().length>3){
				continue;
			}
			
			//双向link,先拆分成两条,再与数据库逐一比对
			int size=1;
			if(linkInEms.getDirection()==globaldefs.ConnectionDirection_T._CD_BI){
				size=2;//双向拆分成两条
			}
			for(int direct=0;direct<size;direct++){
				// 是否存在DB中标志位
				boolean isExistInDB = false;
				int isMain = DataCollectDefine.TRUE;
				if(direct==0){
					// a端端口
					aEndPtp = dataCollectMapper.selectPtpByNeSerialNoAndPtpName(
							getEmsConnectionId(paramter), linkInEms.getaEndNESerialNo(),
							linkInEms.getaEndPtpName());
					// z端端口
					zEndPtp = dataCollectMapper.selectPtpByNeSerialNoAndPtpName(
							getEmsConnectionId(paramter), linkInEms.getzEndNESerialNo(),
							linkInEms.getzEndPtpName());
				}else{//反向link
					isMain = DataCollectDefine.FALSE;
					Map tmpMap=aEndPtp;
					aEndPtp=zEndPtp;
					zEndPtp=tmpMap;
					linkInEms.switchDirection();
				}
	
				// 加入需要同步网元列表
				if (aEndPtp == null || zEndPtp == null) {
	
					if (aEndPtp == null) {
	
						ne = dataCollectMapper.selectNeByNeName(
								getEmsConnectionId(paramter),
								linkInEms.getaEndNESerialNo());
	
						if (ne == null) {
							// 需要同步网管
							linkAlterResultModel.setNeedSyncEms(true);
//							return linkAlterResultModel;
						} else {
							//获取网元Id
							int neId = Integer.valueOf(ne.get("BASE_NE_ID")
									.toString());
							//获取网元类型
							int neType = Integer.valueOf(ne.get("TYPE")
									.toString());
							//非未知网元
							if (neType!= DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG&&
									!syncNeList.contains(neId)) {
								syncNeList.add(neId);
							}
						}
					}
	
					if (zEndPtp == null) {
	
						ne = dataCollectMapper.selectNeByNeName(
								getEmsConnectionId(paramter),
								linkInEms.getzEndNESerialNo());
	
						if (ne == null) {
							// 需要同步网管
							linkAlterResultModel.setNeedSyncEms(true);
//							return linkAlterResultModel;
						} else {
							int neId = Integer.valueOf(ne.get("BASE_NE_ID")
									.toString());
							//获取网元类型
							int neType = Integer.valueOf(ne.get("TYPE")
									.toString());
							//非未知网元
							if (neType!= DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG&&
									!syncNeList.contains(neId)) {
								syncNeList.add(neId);
							}
						}
					}
					continue;
				}
				// a端z端不为null
				else if (aEndPtp != null && zEndPtp != null) {
					List<Map<String,Object>> conflictList = new ArrayList<Map<String,Object>>();
//					boolean isLocked=false;
					Map updateLink=null;
					// 循环DB中link列表
					for (Map linkInDB : linkListInDB) {
	
						// 更新link -- a端 z端相同
						if (Integer.valueOf(aEndPtp.get("BASE_PTP_ID").toString())
								.intValue() == Integer.valueOf(
								linkInDB.get("A_END_PTP").toString()).intValue()
								&& Integer.valueOf(
										zEndPtp.get("BASE_PTP_ID").toString())
										.intValue() == Integer.valueOf(
										linkInDB.get("Z_END_PTP").toString())
										.intValue()) {
	
							// 设置link存在DB标志位
							isExistInDB = true;
							// 组织link表数据
							Map link = topologicalLinkModelToTable(linkInEms,
									getEmsConnectionId(paramter), Integer.valueOf(aEndPtp.get(
											"BASE_NE_ID").toString()),
									Integer.valueOf(aEndPtp.get("BASE_PTP_ID")
											.toString()), Integer.valueOf(zEndPtp
											.get("BASE_NE_ID").toString()),
									Integer.valueOf(zEndPtp.get("BASE_PTP_ID")
											.toString()),
									getType(paramter), DataCollectDefine.LINK_TYPE_EXTERNAL_FLAG,false);
							//设置是否主电路
							link.put("IS_MAIN", isMain);
							// 加入linkId
							link.put("BASE_LINK_ID", linkInDB.get("BASE_LINK_ID"));
							// 更新link数据
							dataCollectMapper.updateLinkById(link);
							
							// 添加为更新, 继续判断与之后link的冲突, 遍历冲突判断结束后移除
							updateLink=linkInDB;
							// 在link列表中移除
							//linkListInDB.remove(linkInDB);
							//break;
						}
						// 冲突link -- z端相同 20140821与汤健沟通：A可重复，即同一端可作为多条link的发送端
						else if (Integer.valueOf(aEndPtp.get("BASE_PTP_ID").toString())
								.intValue() == Integer.valueOf(
								linkInDB.get("A_END_PTP").toString()).intValue()
								|| Integer.valueOf(
										zEndPtp.get("BASE_PTP_ID").toString())
										.intValue() == Integer.valueOf(
										linkInDB.get("Z_END_PTP").toString())
										.intValue()) {
							//只能与手工link冲突
							if(linkInDB.get("IS_MANUAL")!=null&&
								(Integer)linkInDB.get("IS_MANUAL")==DataCollectDefine.TRUE){
								conflictList.add(linkInDB);
							}/*
							if(linkInDB.get("IS_LOCKED")!=null&&
								(Integer)linkInDB.get("IS_LOCKED")==DataCollectDefine.TRUE){
								// 设置link被冲突锁定标志
								isLocked = true;
							}*/
						}
					}
					//移除已更新且判断完冲突的link
					linkListInDB.remove(updateLink);
	
					// 新增link
					if (!isExistInDB||(conflictList!=null&&!conflictList.isEmpty())) {
						linkAlterModel=new LinkAlterModel();
						if(!isExistInDB){
						switch (getType(paramter)) {
						case DataCollectDefine.NMS_TYPE_T2000_FLAG:
						case DataCollectDefine.NMS_TYPE_U2000_FLAG:
							//a,z网元相同，丢弃数据
							if(Integer.valueOf(aEndPtp.get(
									"BASE_NE_ID").toString()).intValue() == Integer.valueOf(zEndPtp.get(
											"BASE_NE_ID").toString()).intValue()){
								continue;
							}
							break;
						case DataCollectDefine.NMS_TYPE_E300_FLAG:
						case DataCollectDefine.NMS_TYPE_U31_FLAG:
						case DataCollectDefine.NMS_TYPE_LUCENT_OMS_FLAG:
						case DataCollectDefine.NMS_TYPE_OTNM2000_FLAG:
						case DataCollectDefine.NMS_TYPE_ALU_FLAG:
							break;
						default:
						}
							
						// 组织link表数据
						Map link = topologicalLinkModelToTable(linkInEms,
								getEmsConnectionId(paramter), Integer.valueOf(aEndPtp.get(
										"BASE_NE_ID").toString()),
								Integer.valueOf(aEndPtp.get("BASE_PTP_ID")
										.toString()), Integer.valueOf(zEndPtp
										.get("BASE_NE_ID").toString()),
								Integer.valueOf(zEndPtp.get("BASE_PTP_ID")
										.toString()),
								getType(paramter), DataCollectDefine.LINK_TYPE_EXTERNAL_FLAG,false);
						//设置是否主电路
						link.put("IS_MAIN", isMain);
						linkAlterModel.setLink(link);
						// 新增link
						linkAlterModel.setChangeType(DataCollectDefine.CHANGE_TYPE_ADD);
						}else{
							linkAlterModel.setLink(updateLink);
							// 新增link
							linkAlterModel.setChangeType(DataCollectDefine.CHANGE_TYPE_UPDATE);
						}
						
						linkAlterModel.setConflictList(conflictList);
						
						linkAlterModel.setLinkName(linkInEms.getNativeEMSName());
	
						linkAlterModel.setaEndPtp(Integer.valueOf(aEndPtp.get(
								"BASE_PTP_ID").toString()));
	
						linkAlterModel.setzEndPtp(Integer.valueOf(zEndPtp.get(
								"BASE_PTP_ID").toString()));
	
						changeList.add(linkAlterModel);
					}
				}
			}
		}

		// 无需同步网元情况下 增加link变更信息
//		if (syncNeList.size() == 0) {
			for (Map linkInDB : linkListInDB) {
				//手工link不删除
				if(linkInDB.get("IS_MANUAL")!=null&&
					(Integer)linkInDB.get("IS_MANUAL")==DataCollectDefine.TRUE){
					continue;
				}
				/*if(linkInDB.get("IS_LOCKED")!=null&&
					(Integer)linkInDB.get("IS_LOCKED")==DataCollectDefine.TRUE){
					// 锁定link 不删除
					continue;
				}*/
				linkAlterModel = new LinkAlterModel();
				linkAlterModel.setLink(linkInDB);
				// 删除link
				linkAlterModel.setChangeType(DataCollectDefine.CHANGE_TYPE_DELETE);

				linkAlterModel
						.setLinkName(linkInDB.get("DISPLAY_NAME") != null ? linkInDB
								.get("DISPLAY_NAME").toString() : "");

				linkAlterModel.setaEndPtp(Integer.valueOf(linkInDB.get(
						"A_END_PTP").toString()));

				linkAlterModel.setzEndPtp(Integer.valueOf(linkInDB.get(
						"Z_END_PTP").toString()));

				changeList.add(linkAlterModel);
			}
//		}

		// 设置是否需要同步网元，link是否有更改标识
//		if (syncNeList.size() > 0 || changeList.size() > 0) {

			if (syncNeList.size() > 0) {
				linkAlterResultModel.setNeedSyncNe(true);
				linkAlterResultModel.setSyncNeList(syncNeList);
			}
			if (changeList.size() > 0) {
				linkAlterResultModel.setChanged(true);
				linkAlterResultModel.setChangeList(changeList);
				syncLinkImpl(paramter,changeList,commandLevel);
		}
//		}
		
		return linkAlterResultModel;
	}

	@Override
	@IMethodLog(desc = "DataCollectService：同步网元Link信息")
	public void syncLinkImpl(Map paramter,List<LinkAlterModel> syncList, int commandLevel) throws CommonException {
		if(syncList==null)
			return;
		List<Map> insertLinkList = new ArrayList<Map>();
		Map<String, Object> linkStatic=new HashMap<String, Object>();
		linkStatic.put("BASE_LINK_ID",null);
		linkStatic.put("NAME",null);
		linkStatic.put("USER_LABEL",null);
		linkStatic.put("NATIVE_EMS_NAME",null);
        linkStatic.put("DISPLAY_NAME",null);
        linkStatic.put("OWNER",null);
        linkStatic.put("DIRECTION",null);
        linkStatic.put("A_EMS_CONNECTION_ID",null);
        linkStatic.put("A_NE_ID",null);
        linkStatic.put("A_END_PTP",null);
        linkStatic.put("Z_EMS_CONNECTION_ID",null);
        linkStatic.put("Z_NE_ID",null);
        linkStatic.put("Z_END_PTP",null);
        linkStatic.put("LINK_TYPE",null);
        linkStatic.put("CHANGE_STATE",null);
        linkStatic.put("IS_MAIN",null);
        linkStatic.put("IS_MANUAL",null);
        linkStatic.put("IS_DEL",null);
        linkStatic.put("CREATE_TIME",null);
        linkStatic.put("UPDATE_TIME",null);

		// 循环EMS获取的link列表
		for (LinkAlterModel linkSelect : syncList) {
			if(linkSelect.getChangeType()==DataCollectDefine.CHANGE_TYPE_ADD){
				//先删除冲突源
				/*for(Map conflict:linkSelect.getConflictList()){
					Map link=new HashMap<String, Object>();
					link.put("BASE_LINK_ID", conflict.get("BASE_LINK_ID"));
					link.put("CHANGE_STATE", DataCollectDefine.LATEST_DEL);
					link.put("IS_DEL", DataCollectDefine.TRUE);
					dataCollectMapper.updateLinkById(link);
				}*/
				Map link=new HashMap<String, Object>(linkStatic);
				link.putAll(linkSelect.getLink());
				if(link.get("BASE_LINK_ID")!=null){
					dataCollectMapper.updateLinkById(link);
				}else{
					insertLinkList.add(link);
				}
			}else if(linkSelect.getChangeType()==DataCollectDefine.CHANGE_TYPE_DELETE){
				Map link=new HashMap<String, Object>();
				link.put("BASE_LINK_ID", linkSelect.getLink().get("BASE_LINK_ID"));
				link.put("CHANGE_STATE", DataCollectDefine.LATEST_DEL);
				link.put("IS_DEL", DataCollectDefine.TRUE);
				dataCollectMapper.updateLinkById(link);
			}else if(linkSelect.getChangeType()==DataCollectDefine.CHANGE_TYPE_DELETE_CONFLICT){
				//删除冲突源
				for(Map conflict:linkSelect.getConflictList()){
					Map link=new HashMap<String, Object>();
					link.put("BASE_LINK_ID", conflict.get("BASE_LINK_ID"));
					link.put("CHANGE_STATE", DataCollectDefine.LATEST_DEL);
					link.put("IS_DEL", DataCollectDefine.TRUE);
					dataCollectMapper.updateLinkById(link);
				}
			}
		}
		// 插入link数据
		if (insertLinkList.size() > 0) {
			dataCollectMapper.insertLinkBatch(insertLinkList);
		}
		return;
	}

	@Override
	@IMethodLog(desc = "DataCollectService：同步网元内部link")
	public void syncNeInternalLinkImpl(Map paramter,int neId, int commandLevel)
			throws CommonException {

		Map ne = dataCollectMapper.selectTableById("T_BASE_NE", "BASE_NE_ID",
				neId);
		
		// 获取网元类型
		int neType = ne.get("TYPE") != null ? Integer.valueOf(ne
				.get("TYPE").toString()) : DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG;
		//未知网元类型
		if(DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG == neType){
			throw new CommonException(new NullPointerException(),
					MessageCodeDefine.CORBA_UNKNOW_NE_TYPE_EXCEPTION);
		}
		//只有WDM/OTN才有internalLink
		if(neType != DataCollectDefine.COMMON.NE_TYPE_WDM_FLAG
				&& neType != DataCollectDefine.COMMON.NE_TYPE_OTN_FLAG){
//			//U31 不取内部link,在外部link中已包含
//			if(DataCollectDefine.NMS_TYPE_U31_FLAG == getType(paramter).intValue()){
//				return;
//			}
			return;
		}

		List<TopologicalLinkModel> linkListInEms = (List<TopologicalLinkModel>) getDataFromEms(
				this.GET_ALL_INTERNAL_TOPOLOGICAL_LINKS, ne, null, paramter,commandLevel);

		// 获取数据库中link信息,linkType=2 1.外部link 2.内部link 3.手工link
		Integer[] linkType = new Integer[] { DataCollectDefine.LINK_TYPE_INTERNAL_FLAG };
		List<Map> linkListInDB = dataCollectMapper
				.selectLinkListByEmsConnectionId(getEmsConnectionId(paramter), neId,
						linkType, null);

		List<Map> insertLinkList = new ArrayList<Map>();

		Map link = null;

		Map aEndPtp = null;
		Map zEndPtp = null;

		// 循环EMS获取的link列表
		for (TopologicalLinkModel linkInEms : linkListInEms) {
			//贝尔link发现有link一端为ctp的情况，无法正常入库，数据舍弃
			if(linkInEms.getaEndTP().length > 3 || linkInEms.getzEndTP().length>3){
				continue;
			}
			//双向link,先拆分成两条,再与数据库逐一比对
			int size=1;
			if(linkInEms.getDirection()==globaldefs.ConnectionDirection_T._CD_BI){
				size=2;//双向拆分成两条
			}
			for(int direct=0;direct<size;direct++){
				// 是否存在DB中标志位
				boolean isExistInDB = false;
				int isMain = DataCollectDefine.TRUE;
				if(direct==0){//单向或双向时的正向
					// a端端口
					aEndPtp = dataCollectMapper.selectPtpByNeSerialNoAndPtpName(
							getEmsConnectionId(paramter), linkInEms.getaEndNESerialNo(),
							linkInEms.getaEndPtpName());
					// z端端口
					zEndPtp = dataCollectMapper.selectPtpByNeSerialNoAndPtpName(
							getEmsConnectionId(paramter), linkInEms.getzEndNESerialNo(),
							linkInEms.getzEndPtpName());
				}else{//双向时的反向
					isMain = DataCollectDefine.FALSE;
					Map tmpMap=aEndPtp;
					aEndPtp=zEndPtp;
					zEndPtp=tmpMap;
					linkInEms.switchDirection();
				}
				
	
				if (aEndPtp != null && zEndPtp != null) {
	
					// 循环DB中link列表
					for (Map linkInDB : linkListInDB) {
	
						// 更新link -- a端 z端相同
						if (Integer.valueOf(aEndPtp.get("BASE_PTP_ID").toString())
								.intValue() == Integer.valueOf(
								linkInDB.get("A_END_PTP").toString()).intValue()
								&& Integer.valueOf(
										zEndPtp.get("BASE_PTP_ID").toString())
										.intValue() == Integer.valueOf(
										linkInDB.get("Z_END_PTP").toString())
										.intValue()) {
							// 组织link表数据
							link = topologicalLinkModelToTable(linkInEms,
									getEmsConnectionId(paramter), neId, Integer.valueOf(aEndPtp
											.get("BASE_PTP_ID").toString()), neId,
									Integer.valueOf(zEndPtp.get("BASE_PTP_ID")
											.toString()),
									getType(paramter), DataCollectDefine.LINK_TYPE_INTERNAL_FLAG, true);
							// 加入linkId
							link.put("BASE_LINK_ID", linkInDB.get("BASE_LINK_ID"));
							// 更新link数据
							dataCollectMapper.updateLinkById(link);
							// 设置link存在DB标志位
							isExistInDB = true;
							// 在link列表中移除
							linkListInDB.remove(linkInDB);
							break;
						}
					}
	
					// 新增网元
					if (!isExistInDB) {
						// 组织link表数据
						link = topologicalLinkModelToTable(linkInEms,
								getEmsConnectionId(paramter), neId, Integer.valueOf(aEndPtp.get(
										"BASE_PTP_ID").toString()), neId,
								Integer.valueOf(zEndPtp.get("BASE_PTP_ID")
										.toString()),
								getType(paramter), DataCollectDefine.LINK_TYPE_INTERNAL_FLAG,
								false);
						//设置是否主电路
						link.put("IS_MAIN", isMain);
						insertLinkList.add(link);
						
						/*// 如果方向为1标识为双向，a,z端对调保存
						if (linkInEms.getDirection()== 1) {
							link = topologicalLinkModelToTable(linkInEms,
									getEmsConnectionId(paramter), neId, Integer.valueOf(zEndPtp.get(
											"BASE_PTP_ID").toString()), neId,
									Integer.valueOf(aEndPtp.get("BASE_PTP_ID")
											.toString()),
									DataCollectDefine.LINK_TYPE_INTERNAL_FLAG, getType(paramter),
									false);
							//设置是否主电路
							link.put("IS_MAIN", DataCollectDefine.FALSE);
							insertLinkList.add(link);
						}*/
						
						//设置a,z端口为连接点
						//20141106 改为触发器处理
//						aEndPtp.put("PORT_TYPE", DataCollectDefine.COMMON.PORT_TYPE_INTERNAL_LINK_POINT);
//						dataCollectMapper.updatePtpById(aEndPtp);
//						zEndPtp.put("PORT_TYPE", DataCollectDefine.COMMON.PORT_TYPE_INTERNAL_LINK_POINT);
//						dataCollectMapper.updatePtpById(zEndPtp);
					}
				}
			}
		}

		// 在DB中存在的link 但实际网管上已经没有的link设为标记删除 IS_DEL = 1
		for (Map linkInDB : linkListInDB) {
			linkInDB.put("IS_DEL", DataCollectDefine.TRUE);
			dataCollectMapper.updateLinkById(linkInDB);
		}
		// 插入link数据
		if (insertLinkList.size() > 0) {
			dataCollectMapper.insertLinkBatch(insertLinkList);
		}

	}

	// @Override
	// public void syncNeVBs(int neId, int commandLevel) throws CommonException
	// {
	// // TODO Auto-generated method stub
	//
	// }

	@Override
	@IMethodLog(desc = "DataCollectService：以太网同步")
	public void syncNeEthServiceImpl(Map paramter,int neId, int commandLevel)
			throws CommonException {

		// 获取ne
		Map ne = dataCollectMapper.selectTableById("T_BASE_NE", "BASE_NE_ID",
				neId);
		
		// 获取网元类型
		int neType = ne.get("TYPE") != null ? Integer.valueOf(ne
				.get("TYPE").toString()) : DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG;
		//未知网元类型
		if(DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG == neType){
			throw new CommonException(new NullPointerException(),
					MessageCodeDefine.CORBA_UNKNOW_NE_TYPE_EXCEPTION);
		}
		//只有SDH才有ethservice
		if(neType != DataCollectDefine.COMMON.NE_TYPE_SDH_FLAG){
			return;
		}

		// 获取以太网数据对象
		List<EthServiceModel> ethServiceListInEms = (List<EthServiceModel>) getDataFromEms(
				this.GET_ALL_ETH_SERVICE, ne, null, paramter,commandLevel);

		Map ethService = null;

		Map aEndPtp = null;
		Map zEndPtp = null;

		List<Map> insertEthServiceList = new ArrayList<Map>();

		// 获取数据库中ethService信息
		List<Map> ethServiceListInDB = dataCollectMapper.selectDataListByNeId(
				"T_BASE_ETH_SVC", neId, null);

		// 循环EMS网元获取的ethService列表
		for (EthServiceModel ethServiceInEms : ethServiceListInEms) {

			// 是否存在DB中标志位
			boolean isExistInDB = false;
			// a端端口
			aEndPtp = dataCollectMapper.selectPtpByNeSerialNoAndPtpName(
					getEmsConnectionId(paramter), ethServiceInEms.getaEndNESerialNo(),
					ethServiceInEms.getaEndPointName());
			// z端端口
			zEndPtp = dataCollectMapper.selectPtpByNeSerialNoAndPtpName(
					getEmsConnectionId(paramter), ethServiceInEms.getzEndNESerialNo(),
					ethServiceInEms.getzEndPointName());

			if (aEndPtp != null && zEndPtp != null) {
				// 循环DB中ethService列表
				for (Map ethServiceInDB : ethServiceListInDB) {
					// 更新ethService
					if (ethServiceInDB.get("NAME").toString()
							.equals(ethServiceInEms.getNameString())) {

						// 组织ethService表数据
						ethService = ethServiceModelToTable(ethServiceInEms,
								getEmsConnectionId(paramter), neId, Integer.valueOf(aEndPtp
										.get("BASE_PTP_ID").toString()),
								Integer.valueOf(zEndPtp.get("BASE_PTP_ID")
										.toString()), aEndPtp.get("PTP_TYPE")
										.toString(), zEndPtp.get("PTP_TYPE")
										.toString(), getType(paramter), true);

						// 加入ethServiceId
						ethService.put("BASE_ETH_SVC_ID",
								ethServiceInDB.get("BASE_ETH_SVC_ID"));
						// 更新ethService数据
						dataCollectMapper.updateEthServiceById(ethService);
						// 设置存在DB标志位
						isExistInDB = true;
						// 在网元列表中移除
						ethServiceListInDB.remove(ethServiceInDB);
						break;
					}
				}
				// 新增ethService
				if (!isExistInDB) {
					ethService = ethServiceModelToTable(ethServiceInEms,
							getEmsConnectionId(paramter), neId, Integer.valueOf(aEndPtp.get(
									"BASE_PTP_ID").toString()),
							Integer.valueOf(zEndPtp.get("BASE_PTP_ID")
									.toString()), aEndPtp.get("PTP_TYPE")
									.toString(), zEndPtp.get("PTP_TYPE")
									.toString(), getType(paramter), false);
					insertEthServiceList.add(ethService);
				}
			}
		}
		// 在DB中存在的ethService 但实际网管上已经没有的ethService设为标记删除 IS_DEL = 1
		for (Map ethServiceInDB : ethServiceListInDB) {
			ethServiceInDB.put("IS_DEL", DataCollectDefine.TRUE);
			dataCollectMapper.updateEthServiceById(ethServiceInDB);
		}
		if (insertEthServiceList.size() > 0) {
			dataCollectMapper.insertEthServiceBatch(insertEthServiceList);
		}
	}

	@Override
	@IMethodLog(desc = "DataCollectService：同步BindingPath数据 网元为单位")
	public void syncNeBindingPathImpl(Map paramter,int neId, int commandLevel)
			throws CommonException {

		// 获取ne
		Map ne = dataCollectMapper.selectTableById("T_BASE_NE", "BASE_NE_ID",
				neId);
		
		// 获取网元类型
		int neType = ne.get("TYPE") != null ? Integer.valueOf(ne
				.get("TYPE").toString()) : DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG;
		//未知网元类型
		if(DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG == neType){
			throw new CommonException(new NullPointerException(),
					MessageCodeDefine.CORBA_UNKNOW_NE_TYPE_EXCEPTION);
		}
		//只有SDH才有bindingPath
		if(neType != DataCollectDefine.COMMON.NE_TYPE_SDH_FLAG 
				&&neType != DataCollectDefine.COMMON.NE_TYPE_PTN_FLAG
				){
			return;
		}
		//获取网元序列号
		String neSerialNo = ne.get("NAME").toString();
		
		Integer[] domain = null;
		List<Map> ptpList = new ArrayList<Map>();
		switch (getFactory(paramter).intValue()) {
		//朗讯不支持bingdingPath数据
		case DataCollectDefine.FACTORY_LUCENT_FLAG:
			break;
		case DataCollectDefine.FACTORY_HW_FLAG:
		case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
		case DataCollectDefine.FACTORY_ALU_FLAG:
		case DataCollectDefine.FACTORY_FUJITSU_FLAG:
			// domain 为eth
			domain = new Integer[] { DataCollectDefine.COMMON.DOMAIN_ETH_FLAG };
			ptpList = dataCollectMapper.selectPtpListByNeId(neId, domain,
					DataCollectDefine.FALSE);
			break;
		case DataCollectDefine.FACTORY_ZTE_FLAG:
			// domain不做限制
//			domain = new Integer[] { DataCollectDefine.COMMON.DOMAIN_ETH_FLAG };
//			ptpList = dataCollectMapper.selectPtpListByNeId(neId, domain,
//					DataCollectDefine.FALSE);
			// 获取真实 ptp信息
			List<TerminationPointModel> ptpListInEms = (List<TerminationPointModel>) getDataFromEms(
					this.GET_ALL_PTPS, ne, null, paramter,commandLevel);
			
			for(TerminationPointModel ptpModel:ptpListInEms){
				Map ptp = new HashMap();
				
				ptp.put("NAME", ptpModel.getNameString());
				ptp.put("LAYER_RATE", ptpModel.getLayerRateString());
				
				ptpList.add(ptp);
			}
			
			break;
		default:
			// domain 为eth
			domain = new Integer[] { DataCollectDefine.COMMON.DOMAIN_ETH_FLAG };
			ptpList = dataCollectMapper.selectPtpListByNeId(neId, domain,
					DataCollectDefine.FALSE);
			break;
		}
		//循环ptp，获取bindingpath数据
		for (Map ptp : ptpList) {
			Integer ptpId = null;
			if(ptp.get("BASE_PTP_ID")!=null){
				ptpId = Integer.valueOf(ptp.get("BASE_PTP_ID").toString());
			}
			String ptpNameString = ptp.get("NAME").toString();
			
			switch (getFactory(paramter).intValue()) {
			case DataCollectDefine.FACTORY_HW_FLAG:
			case DataCollectDefine.FACTORY_LUCENT_FLAG:
			case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
			case DataCollectDefine.FACTORY_ALU_FLAG:
			case DataCollectDefine.FACTORY_FUJITSU_FLAG:
				// 只同步ptp_type为mp的端口
				String ptpType = ptp.get("PTP_TYPE") != null ? ptp.get("PTP_TYPE")
						.toString() : "";
				if (DataCollectDefine.COMMON.PTP_TYPE_OTHER_MP.equals(ptpType)) {
					// 同步单个ptp的bindingPath数据
					syncPtpBindingPath(paramter, neId, ptpId, ptpNameString,
							getIntenalEmsName(paramter), neSerialNo,
							commandLevel);
				}
				break;
			//中兴特殊单独处理
			case DataCollectDefine.FACTORY_ZTE_FLAG:
				// 只要同步层速率含“96”和“1”的PTP
				if (ptp.get("LAYER_RATE") != null
						&& (ptp.get("LAYER_RATE").toString().contains("96") || 
								ptp.get("LAYER_RATE").toString().contains("1") ||
								ptp.get("LAYER_RATE").toString().contains("98") || 
								ptp.get("LAYER_RATE").toString().contains("99"))) {
					// 同步单个ptp的bindingPath数据
					syncPtpBindingPath(paramter, neId, ptpId,
							ptpNameString, getIntenalEmsName(paramter),
					neSerialNo, commandLevel);
				}
				break;
			default:
				// 同步单个ptp的bindingPath数据
				syncPtpBindingPath(paramter, neId, ptpId, ptpNameString,
						getIntenalEmsName(paramter), neSerialNo,
						commandLevel);
				break;
			}
		}
	}

	@Override
	@IMethodLog(desc = "DataCollectService：同步BindingPath数据 ptp为单位")
	public void syncPtpBindingPath(Map paramter,int neId, Integer ptpId, String ptpNameString,
			String internalEmsName, String neSerialNo, int commandLevel)
					throws CommonException {

		List<Map> insertBindingPathList = new ArrayList<Map>();

		Map bindingPath = null;

		// 组装ptp名
		NameAndStringValue_T[] ptpName = nameUtil.constructName(ptpNameString,
				internalEmsName, neSerialNo);

		// 获取bindingPath数据
		List<MSTPBindingPathModel> mstpBindingPathListInEms = (List<MSTPBindingPathModel>) getDataFromEms(
				this.GET_ALL_BINDING_PATH, null, ptpName, paramter,
				commandLevel);

		System.out.println(new Date()
				+ " DataCollectService：同步BindingPath数据 ptp为单位，数据条目："
				+ mstpBindingPathListInEms.size());

		// 删除原有pathList数据
		if(ptpId!=null){
			dataCollectMapper.deleteBindingPathByPtpId(ptpId);
		}else{
			dataCollectMapper.deleteBindingPathByNeIdPtpName(neId, ptpNameString);
		}

		// 循环数据列表
		for (MSTPBindingPathModel mstpBindingPathInEms : mstpBindingPathListInEms) {

			// allPahtList
			for (NameAndStringValue_T[] ctpName : mstpBindingPathInEms
					.getAllPathList()) {
				// 构建bindingPath数据
				bindingPath = constructBindingPathData(getFactory(paramter)
						.intValue(), getType(paramter).intValue(), getEmsConnectionId(paramter), neId, ptpId,ptpNameString,
						mstpBindingPathInEms, ctpName, false);

				if (bindingPath != null) {
					insertBindingPathList.add(bindingPath);
				}
			}
			// usedPahtList
			for (NameAndStringValue_T[] ctpName : mstpBindingPathInEms
					.getUsedPathList()) {
				// 构建bindingPath数据
				bindingPath = constructBindingPathData(getFactory(paramter)
						.intValue(), getType(paramter).intValue(), getEmsConnectionId(paramter), neId, ptpId,ptpNameString,
						mstpBindingPathInEms, ctpName, true);

				if (bindingPath != null) {
					insertBindingPathList.add(bindingPath);
				}
			}
			// 插入bindingPath数据
			if(insertBindingPathList.size()>0){
				dataCollectMapper.insertBindingPathBatch(insertBindingPathList);
			}
		}
	}
	
	
	
	/**
	 * 构建bindingPath数据
	 * @param factory
	 * @param emsConnectionId
	 * @param neId
	 * @param ptpId
	 * @param model
	 * @param vcgTpName
	 * @param ctpName
	 * @return
	 */
	private Map constructBindingPathData(int factory, int emsType, int emsConnectionId,
			int neId, Integer ptpId, String ptpNameStringFromSource, MSTPBindingPathModel model, NameAndStringValue_T[] ctpName, boolean isUsed) {

		Map bindingPath = null;
		Map ctp = null;

		String ptpNameStringFromData;
		String ctpNameString;
		// 中兴特殊处理
		switch (factory) {
		case DataCollectDefine.FACTORY_ZTE_FLAG:
			// 获取vcg ptpId
			// ptp名称字符串
			ptpNameStringFromData = nameUtil.decompositionName(model.getVcgTpName());
			Map vcgPtp = dataCollectMapper.selectPtpByNeIdAndPtpName(neId,
					ptpNameStringFromData);
			// only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
			if (vcgPtp == null && ptpNameStringFromData.contains("ptptype")) {
				vcgPtp = selectPtpForE300(neId, ptpNameStringFromData);
			}
			Integer vcgPtpId = null;
			//ptp未找到
			if (vcgPtp != null) {
				vcgPtpId = Integer.valueOf(vcgPtp.get("BASE_PTP_ID")
						.toString());
			}
			bindingPath = constructBindingPathForZTE(model, emsType, emsConnectionId,
					neId, ptpId, ptpNameStringFromSource,vcgPtpId, ptpNameStringFromData, ctpName, isUsed);
			break;
		// 一般处理
		default:
			ptpNameStringFromData = nameUtil.decompositionName(nameUtil
					.getPtpNameFromCtpName(ctpName));

			ctpNameString = nameUtil.decompositionCtpName(ctpName);

			// 查找ctp
			ctp = dataCollectMapper.selectSdhCtp(emsConnectionId, neId,
					ptpNameStringFromData, ctpNameString);
			// ctp未找到继续
			if (ctp == null) {
				return null;
			}
			ctp.put("IS_ETH", DataCollectDefine.TRUE);
			dataCollectMapper.updateSdhCtpById(ctp);

			bindingPath = bindingPathModelToTable(model, neId, ptpId, ptpNameStringFromSource, ptpId, null,null,null,
					Long.valueOf( ctp.get("BASE_SDH_CTP_ID").toString()), null, isUsed, false);
			break;
		}
		return bindingPath;
	}
			
	/**
	 * 构建中兴bindingPath数据
	 * @param model
	 * @param emsConnectionId
	 * @param neId
	 * @param ptpId
	 * @param vcgPtpId
	 * @param ctpName
	 * @param isUsed
	 * @return
	 */
	private Map constructBindingPathForZTE(
			MSTPBindingPathModel model, int emsType, int emsConnectionId,
			int neId, Integer ptpId, String ptpNameStringFromSource,Integer vcgPtpId,String vcgPtpName,
			NameAndStringValue_T[] ctpName, boolean isUsed) {
		// bindingPath数据模型
		Map bindingPath = null;

		// 是ctp处理逻辑
		if (ctpName.length>3&&!ctpName[3].name.isEmpty()) {
			// step1 根据bindingTpNameList中数据在T_BASE_PTP_VIRTUAL表增加PTP数据
			// step2 在t_base_sdh_ctp表中新增CTP，关联到T_BASE_PTP_VIRTUAL表中的PTP
			// step3
			// 在t_base_binding_path表中新增数据，在CTP字段添加数据，在BINDING_PTP_ID字段数据为空。
			// 从Ctp的对象名中获取Ptp对象名
			String ptpNameString = nameUtil.decompositionName(nameUtil
					.getPtpNameFromCtpName(ctpName));
			String rackNo = nameUtil.getEquipmentNoFromTargetName(
					ptpNameString, DataCollectDefine.COMMON.RACK);
			String shelfNo = nameUtil.getEquipmentNoFromTargetName(
					ptpNameString, DataCollectDefine.COMMON.SHELF);
			String slotNo = nameUtil.getEquipmentNoFromTargetName(
					ptpNameString, DataCollectDefine.COMMON.SLOT);
			String portNo = nameUtil.getEquipmentNoFromTargetName(
					ptpNameString, DataCollectDefine.COMMON.PORT);
			String direction = nameUtil.getEquipmentNoFromTargetName(
					ptpNameString, DataCollectDefine.ZTE.ZTE_DIRECTION);
			String ptpType = nameUtil.getEquipmentNoFromTargetName(
					ptpNameString, DataCollectDefine.ZTE.ZTE_PTP_TYPE);
			
			Map ctp = null;
			
			if(DataCollectDefine.NMS_TYPE_U31_FLAG == emsType){
				Map ptpReal = dataCollectMapper.selectPtpByNeIdAndPtpName(neId,
						ptpNameString);
				// only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
				if (ptpReal == null && ptpNameString.contains("ptptype")) {
					ptpReal = selectPtpForE300(neId, ptpNameString);
				}
				// 不存在此条数据，返回
				if (ptpReal == null) {
					return null;
				}
				int ptpRealId = Integer.valueOf(ptpReal.get(
						"BASE_PTP_ID").toString());

				String ctpNameString = nameUtil.decompositionCtpName(ctpName);
				// 查找ctp 关联ptp类型 1.真实ptp ,关联t_base_ptp表 2.虚拟ptp,关联t_base_ptp_virtual表
				ctp = dataCollectMapper.selectSdhCtpRelPtpIdAndRelPtpType(
						ptpRealId, ctpNameString,1);

				// 不存在此条数据，插入数据
				if (ctp == null) {
					ctp = constructSdhCtpData(emsConnectionId, null, neId, ptpRealId,
							ctpName,null);
					ctp.put("IS_ETH", DataCollectDefine.TRUE);
					dataCollectMapper.insertSdhCtp(ctp);
				}else{
					//不需要更新
//					ctp.put("IS_ETH", DataCollectDefine.TRUE);
//					dataCollectMapper.updateSdhCtpById(ctp);
				}
			}else if(DataCollectDefine.NMS_TYPE_E300_FLAG == emsType){
				Map ptpVirtual = dataCollectMapper.selectPtpVirtualForZTE(neId,
						ptpType, rackNo, shelfNo, slotNo, portNo);
				// 不存在此条数据，插入数据
				if (ptpVirtual == null) {
					ptpVirtual = constructPtpVirtualData(emsConnectionId, neId,
							nameUtil.getPtpNameFromCtpName(ctpName));
					dataCollectMapper.insertPtpVirtual(ptpVirtual);
				}
				int ptpVirtualId = Integer.valueOf(ptpVirtual.get(
						"BASE_PTP_ID").toString());

				String ctpNameString = nameUtil.decompositionCtpName(ctpName);
				// 查找ctp 关联ptp类型 1.真实ptp ,关联t_base_ptp表 2.虚拟ptp,关联t_base_ptp_virtual表
				ctp = dataCollectMapper.selectSdhCtpRelPtpIdAndRelPtpType(
						ptpVirtualId, ctpNameString,2);

				// 不存在此条数据，插入数据
				if (ctp == null) {
					ctp = constructSdhCtpData(emsConnectionId, null, neId, ptpVirtualId,
							ctpName,null);
					ctp.put("IS_ETH", DataCollectDefine.TRUE);
					dataCollectMapper.insertSdhCtp(ctp);
				}else{
					//不需要更新
//					ctp.put("IS_ETH", DataCollectDefine.TRUE);
//					dataCollectMapper.updateSdhCtpById(ctp);
				}
			}
			bindingPath = bindingPathModelToTable(model, neId, ptpId,ptpNameStringFromSource,
					vcgPtpId, vcgPtpName,null, null,Long.valueOf( ctp.get("BASE_SDH_CTP_ID").toString()), null,
					isUsed, false);
		} else {
			// 非ctp处理逻辑
			String ptpNameString = nameUtil.decompositionName(nameUtil
					.getPtpNameFromCtpName(ctpName));
			
			Integer bindingPtpId = null;
			if(DataCollectDefine.NMS_TYPE_U31_FLAG == emsType){
				Map bindingPtp = dataCollectMapper.selectPtpByNeIdAndPtpName(neId,
						ptpNameString);
				// only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
				if (bindingPtp == null && ptpNameString.contains("ptptype")) {
					bindingPtp = selectPtpForE300(neId, ptpNameString);
				}
				//未找到ptp
				if(bindingPtp != null){
					bindingPtpId = (Integer) bindingPtp.get("BASE_PTP_ID");
				}
			}else if(DataCollectDefine.NMS_TYPE_E300_FLAG == emsType){
				// 非ctp处理逻辑
				// step1 VCG的输入PTP是否为：ptptype=NNIFTP
				if (model.getVcgTpName()[2].value.contains("NNIFTP")) {
					// 根据bindingTpNameList中数据(强制将PTP类型改为FTP后在t_base_ptp表中查询）
					//在t_base_binding_path表中新增数据,在BINDDING_PTP_ID字段新增bingdingTpNameList中的PTP数据，CTP数据为空。
					ctpName[2].name = DataCollectDefine.COMMON.FTP;
				} else {
					// 根据bindingTpNameList中数据在t_base_binding_path表中新增数据,在BASE_BINDDING_PTP_ID字段新增bingdingTpNameList中的PTP数据，CTP数据为空。
				}
				ptpNameString = nameUtil.decompositionName(nameUtil
						.getPtpNameFromCtpName(ctpName));
				Map bindingPtp = dataCollectMapper.selectPtpByNeIdAndPtpName(neId,
						ptpNameString);
				// only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
				if (bindingPtp == null && ptpNameString.contains("ptptype")) {
					bindingPtp = selectPtpForE300(neId, ptpNameString);
				}
				//未找到ptp
				if(bindingPtp != null){
					bindingPtpId = (Integer) bindingPtp.get("BASE_PTP_ID");
				}
			}
			bindingPath = bindingPathModelToTable(model, neId, ptpId,ptpNameStringFromSource,
					vcgPtpId, vcgPtpName,bindingPtpId, ptpNameString, null,
					null, isUsed, false);
		}
		return bindingPath;
	}

	@Override
	@IMethodLog(desc = "DataCollectService：同步网元时钟信息")
	public void syncNeClockImpl(Map paramter,int neId, int commandLevel) throws CommonException {

		Map ne = dataCollectMapper.selectTableById("T_BASE_NE", "BASE_NE_ID",
				neId);
		
		// 获取网元类型
		int neType = ne.get("TYPE") != null ? Integer.valueOf(ne
				.get("TYPE").toString()) : DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG;
		//未知网元类型
		if(DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG == neType){
			throw new CommonException(new NullPointerException(),
					MessageCodeDefine.CORBA_UNKNOW_NE_TYPE_EXCEPTION);
		}
		//只有SDH/OTN才有时钟信息
		if(neType != DataCollectDefine.COMMON.NE_TYPE_SDH_FLAG
				&& neType != DataCollectDefine.COMMON.NE_TYPE_OTN_FLAG){
			return;
		}

		Map clock = null;
		// 获取时钟源信息
		List<ClockSourceStatusModel> clockSourceListInEms = (List<ClockSourceStatusModel>) getDataFromEms(
				this.GET_CLOCK_SOURCE_STATUS, ne, null,
				paramter,commandLevel);

		List<Map> insertClockSourceList = new ArrayList<Map>();

		// 获取数据库中clockSource信息
		List<Map> clockSourceListInDB = dataCollectMapper.selectDataListByNeId(
				"T_BASE_CLOCK", neId, null);

		// 循环EMS网元获取的clockSource列表
		for (ClockSourceStatusModel clockSourceInEms : clockSourceListInEms) {
			// 是否存在DB中标志位
			boolean isExistInDB = false;
			// 循环DB中clockSource列表
			for (Map clockSourceInDB : clockSourceListInDB) {
				// 更新clockSource
				if (clockSourceInDB.get("NAME").toString()
						.equals(clockSourceInEms.getNameString())) {
					// 组织clock表数据
					clock = clockSourceStatusModelToTable(clockSourceInEms,
							getEmsConnectionId(paramter), neId, getType(paramter), true);
					// 加入clockId
					clock.put("BASE_CLOCK_ID",
							clockSourceInDB.get("BASE_CLOCK_ID"));
					// 更新clock数据
					dataCollectMapper.updateClockSourceById(clock);
					// 设置存在DB标志位
					isExistInDB = true;
					// 在网元列表中移除
					clockSourceListInDB.remove(clockSourceInDB);
					break;
				}
			}
			// 新增网元
			if (!isExistInDB) {
				clock = clockSourceStatusModelToTable(clockSourceInEms,
						getEmsConnectionId(paramter), neId, getType(paramter), false);
				insertClockSourceList.add(clock);
			}
		}
		// 在DB中存在的ptp 但实际网管上已经没有的ptp设为标记删除 IS_DEL = 1
		for (Map clockSourceInDB : clockSourceListInDB) {
			clockSourceInDB.put("IS_DEL", DataCollectDefine.TRUE);
			dataCollectMapper.updateClockSourceById(clockSourceInDB);
		}
		if (insertClockSourceList.size() > 0) {
			dataCollectMapper.insertClockSourceBatch(insertClockSourceList);
		}
	}

	@Override
	@IMethodLog(desc = "DataCollectService：获取网元当前性能数据")
	public List<PmDataModel> getCurrentPmData_Ne(Map paramter,int neId,
			short[] layerRateList, int[] pmLocationList,
			int[] pmGranularityList, boolean collectNumbic,
			boolean collectPhysical, boolean collectCtp, int commandLevel)
			throws CommonException {

		// 获取网元对象
		Map ne = dataCollectMapper.selectTableById("T_BASE_NE", "BASE_NE_ID",
				neId);

		// 转换性能采集参数
		String[] locationList = pmLocationTransform(pmLocationList, getType(paramter));
		String[] granularityList = granularityTransform(pmGranularityList, getType(paramter));

		paramter.put(PARAM_LAYER_RATE, layerRateList);
		paramter.put(PARAM_PM_LOCATION, locationList);
		paramter.put(PARAM_GRANULARITY, granularityList);

		int emsId=getEmsConnectionId(paramter);
		// 获取当前性能
		List<PmDataModel> pmDatas = (List<PmDataModel>) getDataFromEms(
				this.GET_ALL_CURRENT_PM, ne, null,
				paramter,commandLevel);
		
//		//测试用
//		return pmDatas;

		// 转换pm性能
		List<PmDataModel> datas = pmDataTransformation(emsId,neId, pmDatas,
				collectNumbic, collectPhysical, collectCtp);

		return datas;
	}

	@Override
	@IMethodLog(desc = "DataCollectService：获取ptp列表当前性能数据")
	public List<PmDataModel> getCurrentPmData_PtpList(Map paramter,List<Integer> ptpIdList,
			short[] layerRateList, int[] pmLocationList,
			int[] pmGranularityList, boolean collectNumbic,
			boolean collectPhysical, boolean collectCtp, int commandLevel)
			throws CommonException {
		
		//ptpId去重处理，防止采集数据重复
		List<Integer> ptpIdListNew = new ArrayList<Integer>();
		for (Integer ptpId : ptpIdList) {
			if(!ptpIdListNew.contains(ptpId)){
				ptpIdListNew.add(ptpId);
			}
		}

		// 保存ptp关联信息
		List<CurrentPmCollectModel> pmModels = new ArrayList<CurrentPmCollectModel>();
		// ptp关联信息模型
		CurrentPmCollectModel pmModel = null;
		// 返回数据类型定义
		List<PmDataModel> datas = new ArrayList<PmDataModel>();

		// 获取ptp名字集合
		List<String> ptpNameList = new ArrayList<String>();
		for (Integer ptpId : ptpIdListNew) {
			Map ptpName = dataCollectMapper
					.selectPtpNameAndNeNameByPtpId(ptpId);
			if (ptpName != null) {
				// 保存ptp关联信息
				pmModel = new CurrentPmCollectModel();
				pmModel.setNeId(Integer.valueOf(ptpName.get("BASE_NE_ID")
						.toString()));
				pmModel.setPtpId(ptpId);
				pmModel.setNePtpName(ptpName.get("NE_PTP_NAME").toString());

				pmModels.add(pmModel);
				// 保存ptpName
				ptpNameList.add(ptpName.get("NE_PTP_NAME").toString());
			}
		}

		// 转换性能采集参数
		String[] locationList = pmLocationTransform(pmLocationList, getType(paramter));
		String[] granularityList = granularityTransform(pmGranularityList, getType(paramter));

		paramter.put(PARAM_PTP_NAME_LIST, ptpNameList);
		paramter.put(PARAM_LAYER_RATE, layerRateList);
		//垃圾中兴一指定location就采不到数据了
		if(DataCollectDefine.NMS_TYPE_E300_FLAG == getType(paramter)
				|| DataCollectDefine.NMS_TYPE_U31_FLAG == getType(paramter)){
			paramter.put(PARAM_PM_LOCATION, new String[] {});
		}else{
		paramter.put(PARAM_PM_LOCATION, locationList);
		}
		paramter.put(PARAM_GRANULARITY, granularityList);

		int emsId=getEmsConnectionId(paramter);
		// 获取当前性能
		Map<String, List<PmDataModel>> pmDatas = (Map<String, List<PmDataModel>>) getDataFromEms(
				this.GET_ALL_CURRENT_PM_PTPLIST, null, null, paramter,commandLevel);

		for (CurrentPmCollectModel model : pmModels) {
			if (pmDatas.containsKey(model.getNePtpName())) {
				// 转换pm性能
				List<PmDataModel> tempDatas = pmDataTransformation(emsId,
						model.getNeId(), pmDatas.get(model.getNePtpName()),
						collectNumbic, collectPhysical, collectCtp);
				datas.addAll(tempDatas);
//				// 测试用
//				 datas.addAll(pmDatas.get(model.getNePtpName()));
			}
		}
		// 定义文件路径
		String filePath = FileWriterUtil.BASE_FILE_PATH
				 + "getCurrentPmData_PtpList.txt";
		try {
			FileWriterUtil.writeToTxtCurrentPmData_PtpList(filePath, datas);
		} catch (CommonException e) {
			e.printStackTrace();
		}
		return datas;
	}

	@Override
	@IMethodLog(desc = "DataCollectService：获取网元历史性能数据")
	public List<PmDataModel> getHistoryPmData_Ne(Map paramter,int neId, String time,
			short[] layerRateList, int[] pmLocationList,
			int[] pmGranularityList, boolean collectNumbic,
			boolean collectPhysical, boolean collectCtp, int commandLevel)
			throws CommonException {
		
		// 获取网元对象
		Map ne = dataCollectMapper.selectTableById("T_BASE_NE", "BASE_NE_ID",
				neId);

		String displayName = (String) ne.get("DISPLAY_NAME");
		// 开始时间
		String startTime = null;
		// 结束时间
		String endTime = null;
		// 格式化时间
		SimpleDateFormat parser = CommonUtil
				.getDateFormatter((DataCollectDefine.RETRIEVAL_TIME_FORMAT));

		Date date = null;
		try {
			date = parser.parse(time);
		} catch (ParseException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_PARSE_EXCEPTION);
		}

		//FIXME 采集历史性能数据可能存在时区问题
		// 设置时间格式，各厂家不同
		switch (getType(paramter)) {
		case DataCollectDefine.NMS_TYPE_T2000_FLAG:
		case DataCollectDefine.NMS_TYPE_U2000_FLAG:
		case DataCollectDefine.NMS_TYPE_LUCENT_OMS_FLAG:
			// 时间转换 startTime<=date<=endTime
			startTime = parser.format(CommonUtil.getSpecifiedDay(date, -2, 1))
					+ ".0Z";
			endTime = parser.format(CommonUtil.getSpecifiedDay(date, -1, 0)) 
					+ ".0Z";
			break;
		case DataCollectDefine.NMS_TYPE_U31_FLAG:
			// 时间转换 startTime+24h<=date<endTime+24h
			// U31采集区间需为前72小时~前24小时,筛选区间-48小时~-24小时
			startTime = parser.format(CommonUtil.getSpecifiedDay(date, -3, 0))
					+ ".0Z";
			endTime = parser.format(CommonUtil.getSpecifiedDay(date, -1, 0)) 
					+ ".0Z";
			//15分钟性能
			if(paramter.get(PARAM_PTN_SYSTEM_NAME)!=null){
				startTime = parser.format(CommonUtil.getSpecifiedDay(date, -1, 0));
				endTime = parser.format(CommonUtil.getSpecifiedDay(date, 0, 0)) ;
				startTime = startTime.substring(0, 8)+"000000"+".0Z";
				endTime = endTime.substring(0, 8)+"000000"+".0Z";
			}
			
			break;
		//中兴历史性能采集时间段修改为提前1天
		case DataCollectDefine.NMS_TYPE_E300_FLAG:
			// 时间转换
			startTime = parser.format(CommonUtil.getSpecifiedDay(date, -1, 1))
					+ ".0Z";
			endTime = parser.format(CommonUtil.getSpecifiedDay(date, 0, 0)) 
					+ ".0Z";
			break;
		case DataCollectDefine.NMS_TYPE_OTNM2000_FLAG:
			// 时间转换 startTime<=date<endTime "yyyyMMddhhmmss.s[Z|{+|-}HHMm]"
			// #StartTime="20060101101010.0";
			//烽火时间规律还没研究透,按汤健指示取值区间(T1-72h)~T1,筛选区间(T1-48h)~(T1-24h)
			startTime = parser.format(CommonUtil.getSpecifiedDay(date, -3, 0))
					+ ".0";
			endTime = parser.format(date) + ".0";
			break;
		//FIXME 贝尔历史性能时间参数格式待调查
		case DataCollectDefine.NMS_TYPE_ALU_FLAG:
		default:
			// 时间转换 startTime<=date<endTime
			startTime = parser.format(CommonUtil.getSpecifiedDay(date, -3, 0))
					+ ".0Z";
			endTime = parser.format(CommonUtil.getSpecifiedDay(date, -1, 0)) 
					+ ".0Z";
			break;
		}

		// 转换性能采集参数
		String[] locationList = pmLocationTransform(pmLocationList, getType(paramter));
		String[] granularityList = granularityTransform(pmGranularityList, getType(paramter));
		
		//优先配置文件中获取，如果配置文件中没有，自动适配
		String key = "FTP_"+getIp(paramter);
		String ftpIp = CommonUtil.getFtpIpMappingConfigProperty(key);
		if(ftpIp == null){
			InetAddress host=CommonUtil.getLocalHost(getIp(paramter));
			if(host!=null){
				//获取ip地址后写入配置文件
				ftpIp = host.getHostAddress();
				CommonUtil.writeFtpIpMappingConfigProperty(key, ftpIp);
			}
		}
		
		int ftpPort=Integer.parseInt(CommonUtil
			.getSystemConfigProperty(DataCollectDefine.FTP_PORT));
		String userName=CommonUtil.getSystemConfigProperty(DataCollectDefine.FTP_USER_NAME);
		String password=CommonUtil.getSystemConfigProperty(DataCollectDefine.FTP_PASSWORD);

		//历史性能文件名组装
		displayName = constructHistoryPmTargetName(getFactory(paramter), getIp(paramter), displayName);
		
		paramter.put(PARAM_LAYER_RATE, layerRateList);
		paramter.put(PARAM_PM_LOCATION, locationList);
		paramter.put(PARAM_GRANULARITY, granularityList);

		paramter.put(PARAM_DISPLAY_NAME, displayName);
		paramter.put(PARAM_START_TIME, startTime);
		paramter.put(PARAM_END_TIME, endTime);
		paramter.put(PARAM_FTP_IP, ftpIp);
		paramter.put(PARAM_FTP_PORT, ftpPort);
		paramter.put(PARAM_FTP_USERNAME, userName);
		paramter.put(PARAM_FTP_PASSWORD, password);

		int emsId=getEmsConnectionId(paramter);
		// 获取历史性能
		List<PmDataModel> pmDatas = (List<PmDataModel>) getDataFromEms(
				this.GET_ALL_HISTORY_PM, ne, null,
				paramter,commandLevel);
		
//		//测试用
//		return pmDatas;
		List<PmDataModel> datas = pmDatas;
		// 转换pm性能
		if(pmDatas.size()>0){
			datas = pmDataTransformation(emsId,neId, pmDatas,
					collectNumbic, collectPhysical, collectCtp);
		}
		
		//转移文件至待处理
		if(paramter.get(PARAM_PTN_SYSTEM_NAME)!=null){
			String ptnSysId = paramter.get(PARAM_PTN_SYSTEM_ID).toString();
			String ptnSysName = paramter.get(PARAM_PTN_SYSTEM_NAME).toString();
			//转移地址
			String destFile = constructPtnFilePath(ptnSysId,ptnSysName,startTime,0);
			
			transfer15MinHistoryPmFile(ftpIp, ftpPort,
					userName, password, displayName,destFile);
		}
		return datas;
	}
	
	/**
	 * 获取ptn报表数据
	 * @param paramter
	 * @param neList
	 * @param time
	 * @param commandLevel
	 */
	@Override
	@IMethodLog(desc = "DataCollectService：获取ptn报表数据")
	public void getPtnReportData(Map paramter, String ptnSysId, String ptnSysName, List<Integer> neList, 
			String time, int commandLevel) throws CommonException{
		paramter.put(PARAM_PTN_SYSTEM_ID, ptnSysId);
		paramter.put(PARAM_PTN_SYSTEM_NAME, ptnSysName);
		//采集历史性能文件
		for(Integer neId:neList){
			getHistoryPmData_Ne(paramter,neId, time,
					new short[]{}, new int[]{},
					new int[]{COMMON.GRANULARITY_15MIN_FLAG}, false,false,false, commandLevel);
		}
		//分析数据
		analysisPtnData(paramter,time);
	}
		
	//处理预存在"PTN_REPORT/"+ptnSysId+"_"+ptnSysName+"_待处理"+"/"+startTime.substring(0, 8);文件中的文件
	private  void analysisPtnData(Map paramter ,String time) throws CommonException{
		//优先配置文件中获取，如果配置文件中没有，自动适配
		String key = "FTP_"+getIp(paramter);
		String ftpIp = CommonUtil.getFtpIpMappingConfigProperty(key);
		if(ftpIp == null){
			InetAddress host=CommonUtil.getLocalHost(getIp(paramter));
			if(host!=null){
				//获取ip地址后写入配置文件
				ftpIp = host.getHostAddress();
				CommonUtil.writeFtpIpMappingConfigProperty(key, ftpIp);
			}
		}
		
		int ftpPort=Integer.parseInt(CommonUtil
			.getSystemConfigProperty(DataCollectDefine.FTP_PORT));
		String userName=CommonUtil.getSystemConfigProperty(DataCollectDefine.FTP_USER_NAME);
		String password=CommonUtil.getSystemConfigProperty(DataCollectDefine.FTP_PASSWORD);
			
			//下载至本地java临时目录
			//获取ftp工具类
			FtpUtils ftpUtils = new FtpUtils(ftpIp,ftpPort,userName,password);
			// 获取下载至本地的缓存文件
			String tempPath = System.getProperty("java.io.tmpdir");
			
		//ptn参数
		String ptnSysId = paramter.get(PARAM_PTN_SYSTEM_ID).toString();
		String ptnSysName = paramter.get(PARAM_PTN_SYSTEM_NAME).toString();
		//开始时间
		// 格式化时间
		SimpleDateFormat parser = CommonUtil
				.getDateFormatter((DataCollectDefine.RETRIEVAL_TIME_FORMAT));
		Date date = null;
		
		try {
			date = parser.parse(time);
		} catch (ParseException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_PARSE_EXCEPTION);
		}
		
		// 开始时间
		String startTime = null;
		
		startTime = parser.format(CommonUtil.getSpecifiedDay(date, -1, 0));
		startTime = startTime.substring(0, 8)+"000000"+".0Z";
		//源文件地址
		String sourceFilePath = constructPtnFilePath(ptnSysId,ptnSysName,startTime,0);
		
		List<String> fileNameList = ftpUtils.getFileList(sourceFilePath);
		
		//下载至临时文件夹
		for(String fileName : fileNameList){
			String fileNameIso = fileName;
			try {
				fileNameIso =  new String(fileNameIso.getBytes("GBK"), "iso-8859-1");
			} catch (UnsupportedEncodingException e) {
				throw new CommonException(new NullPointerException(),
						MessageCodeDefine.CORBA_UNSUPPORTED_ENCODING_EXCEPTION);
			} 
			
			ftpUtils.downloadFile(sourceFilePath+"/" + fileNameIso, tempPath,
					fileName);
		}
		
		if(true){
				//调用kettle分析文件
				Map param = BeanUtil.getDataBaseParam();
				param.put(PARAM_PTN_SYSTEM_ID, ptnSysId);
				param.put(PARAM_PTN_SYSTEM_NAME, ptnSysName);
				param.put(PARAM_PTN_DATE, startTime.substring(0, 8));
				
				param.put(PARAM_FTP_IP, ftpIp);
				param.put(PARAM_FTP_PORT, ftpPort);
				param.put(PARAM_FTP_USERNAME, userName);
				param.put(PARAM_FTP_PASSWORD, password);
				
				//jod路径
//				String path = this.getClass().getClassLoader().getResource("kettle/PTN_REPORT.kjb").getPath();
//				String path = System.getProperty("user.dir") + "/../lib/kettle/PTN_REPORT.kjb";
				
				String path = "kettle/PTN_REPORT_TRANS.ktr";
				
//			String path = "D:/FTSP3.0项目/04.MK/trunk/maven项目/DataCollectService/src/main/resources/kettle/PTN_REPORT_TRANS.ktr";
			
				//运行转换
				runTransfer(param,path);
				
				//转移文件至已处理
			String destFile = constructPtnFilePath(ptnSysId,ptnSysName,startTime,1);
			//转移文件
			for(String fileName : fileNameList){
				
				String fileNameIso = fileName;
				try {
					fileNameIso =  new String(fileNameIso.getBytes("GBK"), "iso-8859-1");
					ftpUtils.createDirectory(destFile);
					ftpUtils.ftpClient.rename(sourceFilePath+"/"+fileNameIso, destFile+"/"+fileNameIso);
				} catch (UnsupportedEncodingException e) {
					throw new CommonException(new NullPointerException(),
							MessageCodeDefine.CORBA_UNSUPPORTED_ENCODING_EXCEPTION);
				} catch (IOException e) {
					throw new CommonException(new NullPointerException(),
							MessageCodeDefine.CORBA_FILE_NOT_FOUND_EXCEPTION);
				} 
				//删除临时文件
				File file = new File(tempPath+fileName);
				if(file.exists()){
				file.delete();
				}
			}
		}
	}
	
	//0 待处理 1 已处理
	private String constructPtnFilePath(String ptnSysId,String ptnSysName,String startTime,int flag) throws CommonException{
		
		String flagString = flag == 0?"待处理":"已处理";
		String path = "PTN_REPORT/"+ptnSysId+"_"+ptnSysName+"_"+flagString+"/"+startTime.substring(0, 8);
		try {
			path =  new String(path.getBytes("GBK"), "iso-8859-1");
		} catch (UnsupportedEncodingException e) {
			throw new CommonException(new NullPointerException(),
					MessageCodeDefine.CORBA_UNSUPPORTED_ENCODING_EXCEPTION);
		}
		return path;
	}
	
	private void runJob(Map params, String jobPath) {
		try {
			KettleEnvironment.init();
			// jobname 是Job脚本的路径及名称
//			JobMeta jobMeta = new JobMeta(jobPath, null);
			
			JobMeta jobMeta = new JobMeta(this.getClass().getClassLoader().getResourceAsStream(jobPath), null,null);

			Job job = new Job(null, jobMeta);
			// 向Job 脚本传递参数，脚本中获取参数值：${参数名}
			job.setVariable("host", params.get(DataCollectDefine.DB_HOST).toString());
			job.setVariable("sid", params.get(DataCollectDefine.DB_SID).toString());
			job.setVariable("port", params.get(DataCollectDefine.DB_PORT).toString());
			job.setVariable("username", params.get(DataCollectDefine.DB_USERNAME).toString());
			job.setVariable("password", params.get(DataCollectDefine.DB_PASSWORD).toString());
			
			job.setVariable(PARAM_FTP_IP, params.get(PARAM_FTP_IP).toString());
			job.setVariable(PARAM_FTP_PORT, params.get(PARAM_FTP_PORT).toString());
			job.setVariable(PARAM_FTP_USERNAME, params.get(PARAM_FTP_USERNAME).toString());
			job.setVariable(PARAM_FTP_PASSWORD, params.get(PARAM_FTP_PASSWORD).toString());
			
			job.setVariable(PARAM_PTN_SYSTEM_ID, params.get(PARAM_PTN_SYSTEM_ID).toString());
			job.setVariable(PARAM_PTN_SYSTEM_NAME, params.get(PARAM_PTN_SYSTEM_NAME).toString());
			job.setVariable(PARAM_PTN_DATE, params.get(PARAM_PTN_DATE).toString());
			
			job.start();
			job.waitUntilFinished();
			if (job.getErrors() > 0) {
				throw new Exception(
						"There are errors during job exception!(执行job发生异常)");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void runTransfer(Map params, String jobPath) {
		try {
			// // 初始化
			// 转换元对象
			KettleEnvironment.init();// 初始化
			EnvUtil.environmentInit();
			
			VariableSpace space = new Variables();
			
			space.setVariable("host",params.get(DataCollectDefine.DB_HOST).toString());
			space.setVariable("sid",params.get(DataCollectDefine.DB_SID).toString());
			space.setVariable("port",params.get(DataCollectDefine.DB_PORT).toString());
			space.setVariable("username",params.get(DataCollectDefine.DB_USERNAME).toString());
			space.setVariable("password",params.get(DataCollectDefine.DB_PASSWORD).toString());
			
			if(params.get(PARAM_PTN_SYSTEM_ID)!=null){
			space.setVariable(PARAM_PTN_SYSTEM_ID,params.get(PARAM_PTN_SYSTEM_ID).toString());
			space.setVariable(PARAM_PTN_SYSTEM_NAME,params.get(PARAM_PTN_SYSTEM_NAME).toString());
			space.setVariable(PARAM_PTN_DATE,params.get(PARAM_PTN_DATE).toString());
			}
			
			if(params.get("NE_ID")!=null){
				space.setVariable("NE_ID",params.get("NE_ID").toString());
			}
			
			TransMeta transMeta = new TransMeta(this.getClass().getClassLoader().getResourceAsStream(jobPath), null,true,space,null);
			// 转换
			Trans trans = new Trans(transMeta);
			
//			trans.setVariable("host", params.get(DataCollectDefine.DB_HOST).toString());
//			trans.setVariable("sid", params.get(DataCollectDefine.DB_SID).toString());
//			trans.setVariable("port", params.get(DataCollectDefine.DB_PORT).toString());
//			trans.setVariable("username", params.get(DataCollectDefine.DB_USERNAME).toString());
//			trans.setVariable("password", params.get(DataCollectDefine.DB_PASSWORD).toString());
//			
//			trans.setVariable(PARAM_PTN_SYSTEM_ID, params.get(PARAM_PTN_SYSTEM_ID).toString());
//			trans.setVariable(PARAM_PTN_SYSTEM_NAME, params.get(PARAM_PTN_SYSTEM_NAME).toString());
//			trans.setVariable(PARAM_PTN_DATE, params.get(PARAM_PTN_DATE).toString());
			// 执行转换
			trans.execute(new String[]{});
			// 等待转换执行结束
			trans.waitUntilFinished();
			// 抛出异常
			if (trans.getErrors() > 0) {
				throw new Exception(
						"There are errors during transformation exception!(执行trans发生异常)");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//转移采集的15分钟历史性能文件
	private void transfer15MinHistoryPmFile(String ftpIp, int ftpPort,
			String userName, String password, String displayName,
			String destFile) throws CommonException {
		//获取ftp工具类
		FtpUtils ftpUtils = new FtpUtils(ftpIp,ftpPort,userName,password);
		// 获取下载至本地的缓存文件
		File file = EMSCollectService.getHistoryPMTempFile(ftpUtils, displayName);
		//转移文件
		boolean uploadResult = ftpUtils.uploadFile(file.getPath(),destFile,file.getName());
		//删除缓存文件
		file.delete();
		//删除原始文件
		if(uploadResult){
			ftpUtils.deleteFile(file.getName());
		}
	}
	
	//历史性能文件名组装
	private String constructHistoryPmTargetName(int factory, String corbaIp,
			String displayName) throws CommonException {
		// 对包含斜杠的网元名进行转换
		if (displayName.contains("\\\\")) {
			displayName = displayName.replaceAll("\\\\", "/");
		}
		if (displayName.contains("/")) {
			displayName = displayName.replaceAll("/", "");
		}
		// 如果neDisplayName包含中文会报错，所以需要先进行转码操作
		try {
			displayName = new String(
					displayName.getBytes(DataCollectDefine.ENCODE_GBK),
					DataCollectDefine.ENCODE_ISO);
		} catch (UnsupportedEncodingException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_UNSUPPORTED_ENCODING_EXCEPTION);
		}
		switch (factory) {
		case DataCollectDefine.FACTORY_HW_FLAG:
			displayName = corbaIp + "_" + displayName + ".csv";
			break;
		case DataCollectDefine.FACTORY_ZTE_FLAG:
			displayName = corbaIp + "_" + displayName + ".dat";
			break;
		case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
			displayName = corbaIp + "_" + displayName + ".txt";
			break;
		case DataCollectDefine.FACTORY_LUCENT_FLAG:
			displayName = corbaIp + "_" + displayName + ".csv";
			break;
		case DataCollectDefine.FACTORY_ALU_FLAG:
			displayName = corbaIp + "_" + displayName + ".zip";
			break;
		}
		return displayName;
	}
	
	@Override
	@IMethodLog(desc = "DataCollectService：获取网管告警列表")
	public void syncAllEMSAndMEActiveAlarms(Map paramter,int[] objectType,
			int[] perceivedSeverity, int commandLevel) throws CommonException {
		List<AlarmDataModel> alarms = new ArrayList<AlarmDataModel>();
		// 获取网管告警列表
		List<AlarmDataModel> alarmList = (List<AlarmDataModel>) getDataFromEms(
				this.GET_ALL_EMS_AND_ME_ACTIVE_ALARMS, null, null, paramter,
				commandLevel);
		//插入厂家及emsId信息
		 for (AlarmDataModel model : alarmList) {
			 model.setEmsId(getEmsConnectionId(paramter));
			 model.setFactory(getFactory(paramter));
		 }
		// 告警模块入库
		faultManagerService.alarmDataToMongodb(alarmList, getEmsConnectionId(paramter),
				null, DataCollectDefine.ALARM_TO_DB_TYPE_SYNCH);
	}

	public static Map<String, Object> getTargetAdditionInfo1(AlarmDataModel almModel) {
		Map<String, Object> result = null;
		Date t1 = new Date();
		switch (almModel.getObjectType()) {
		case DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_EMS:
			// 获取网管级别告警的附加信息
			result = getTargetEmsAdditionInfo(almModel);
			break;
		case DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_MANAGED_ELEMENT:
			// 获取网元级别告警的附加信息
			result = getTargetNeAdditionInfo(almModel);
			break;
		case DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_TOPOLOGICAL_LINK:
			// 获取拓扑链路级别告警的附加信息
			result = getTargetLinkAdditionInfo(almModel);
			break;
		case DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_SUBNETWORK_CONNECTION:
			// 获取子网连接级别告警的附加信息
			result = getTargetSubNetworkAdditionInfo(almModel);
			break;
		case DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT:
			// 获取PTP级别告警的附加信息
			result = getTargetPtpAdditionInfo(almModel);
			break;
		case DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT:
			// 获取CTP级别告警的附加信息
			result = getTargetCtpAdditionInfo(almModel);
			break;
		case DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_EQUIPMENT_HOLDER:
			// 获取设备安装位置级别告警的附加信息
			result = getTargetEqptHolderAdditionInfo(almModel);
			break;
		case DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_EQUIPMENT:
			// 获取板卡级别告警的附加信息
			result = getTargetEqptAdditionInfo(almModel);
			break;
		case DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_PROTECTION_GROUP:
			// 获取保护组级别告警的附加信息
			result = getTargetProtectAdditionInfo(almModel);
			break;
		case DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_AID:
			// 获取AID级别告警的附加信息
			result = getTargetAidAdditionInfo(almModel);
			break;
		default:
			// 如果发现未能识别的Object Type则缺省获取网管级别告警的附加信息
			result = getTargetEmsAdditionInfo(almModel);
			break;
		}	
//		CommonUtil.timeDif("告警附加查询1：", t1, new Date());
		return result;
	}
	
	/**
	 * 获取网管级别告警的附加信息
	 * @param emsId
	 * @return
	 */
	private static Map<String, Object> getTargetEmsAdditionInfo(AlarmDataModel almModel) {
		Map<String, Object> result = null;
		int emsId = almModel.getEmsId();
		// 获取网管分组及网管信息
		result = dataCollectMapper.getEmsObjInfoForAlm(emsId);
		
		// 补充包机人信息
		if (result != null) {
			result.putAll(getInspectInfo(null, emsId, null, null, null));	
		} else {
			printAlmParseErrInfo(DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_EMS,
					emsId, null);
		}
		return result;
	}
	/**
	 * 获取网元级别告警的附加信息
	 * @param emsId
	 * @param objectName
	 * @return
	 */
	private static Map<String, Object> getTargetNeAdditionInfo(AlarmDataModel almModel) {
		Map<String, Object> result = new HashMap<String,Object>();
		int emsId = almModel.getEmsId();
		NameAndStringValue_T[] objectName = almModel.getObjectName();
		
		// 取得网元序列号    例：589826
		 String neSerialNo = nameUtil.getNeSerialNo(objectName);

		// 获取网管分组、网管、子网及网元相关信息
		result = dataCollectMapper.getNeObjInfoForAlm(emsId, neSerialNo);
		
		// 如果网元信息获取失败，则获取网管级附加信息
		if (result == null) {
			result = getTargetEmsAdditionInfo(almModel);
			printAlmParseErrInfo(DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_MANAGED_ELEMENT,
					emsId, objectName);
			return result;
		}
		// 补充资源信息
		if (result != null && result.get("RESOURCE_ROOM_ID") != null) {
			int roomId = Integer.parseInt(result.get("RESOURCE_ROOM_ID").toString());
			result.putAll(getResourceInfoByRoomId(roomId));
		}

		// 补充包机人信息
		if (result != null) {
		result.putAll(getInspectInfo(result.get("BASE_EMS_GROUP_ID"), emsId, result.get("SUBNET_ID"),
				result.get("PARENT_SUBNET"), result.get("NE_ID")));
		}
		return result;
	}
	
	/**
	 * 获取链路级别告警的附加信息
	 * @param emsId
	 * @param objectName
	 * @return
	 */
	private static Map<String, Object> getTargetLinkAdditionInfo(AlarmDataModel almModel) {
		Map<String, Object> result = null;
		NameAndStringValue_T[] objectName = almModel.getObjectName();
		int factory = almModel.getFactory();
		StringBuilder sb = new StringBuilder();
		 	
		// 按不同的厂家分别处理
		/* 阿尔卡特_贝尔
		 * name:EMS|TopologicalLink
		 * value:ALU/hldxoms|CONNECT_128 
		 */
		// 获取网管级别的告警附加信息
		result = getTargetEmsAdditionInfo(almModel);
		
		String flag = "";
		if (result != null) {
			// 贝尔
			if (DataCollectDefine.FACTORY_ALU_FLAG == factory) {
				flag = getValueOfKey(almModel.getOriginalInfo(), "nativeEMSName");
			}
			// 其他
			if (flag.isEmpty()) {
				int size = objectName.length;
				sb.setLength(0);
				for (int i=0; i<size; i++) {
					sb.append(objectName[i].value).append(':');
				}
				sb.deleteCharAt(sb.length()-1);
				flag = sb.toString();
			}
			
			// 将拓扑链路标识写到综告标识字段
			result.put("COMPLEX_ALM_DESCRIPTION", flag);
		}
		
		return result;
	}
	
	/**
	 * 获取子网连接级别告警的附加信息
	 * @param emsId
	 * @param objectName
	 * @return
	 */
	private static Map<String, Object> getTargetSubNetworkAdditionInfo(AlarmDataModel almModel) {
		Map<String, Object> result = null;
		NameAndStringValue_T[] objectName = almModel.getObjectName();
		int factory = almModel.getFactory();
		StringBuilder sb = new StringBuilder();
			
		/* 阿尔卡特_贝尔：
		 * name：EMS|MultiLayerSubnetwork|SubnetworkConnection
		 * value：ALU/hldxoms|PKT|PW_101
		 * 中兴：
		 * name：EMS|MultiLayerSubnetwork|SubnetworkConnection
		 * value：ZTE/1|1|883899530000000001
		 */
		// 获取网管级别的告警附加信息
		result = getTargetEmsAdditionInfo(almModel);
		
		String flag = "";
		if (result != null) {
			// 中兴
			if (DataCollectDefine.FACTORY_ZTE_FLAG == factory) {
				flag = getValueOfKey(almModel.getOriginalInfo(), "Trail");
			// 贝尔
			} else if (DataCollectDefine.FACTORY_ALU_FLAG == factory) {
				flag = getValueOfKey(almModel.getOriginalInfo(), "nativeEMSName");
			}
			// 其他
			if (flag.isEmpty()) {
				int size = objectName.length;
				sb.setLength(0);
				for (int i=0; i<size; i++) {
					sb.append(objectName[i].value).append(':');
				}
				sb.deleteCharAt(sb.length()-1);
				flag = sb.toString();
			}
			// 将子网连接标识写到综告标识字段
			result.put("COMPLEX_ALM_DESCRIPTION", flag);
		}
		
		return result;
	}
	/**
	 * 获取PTP级别告警的附加信息
	 * @param emsId
	 * @param objectName
	 * @return
	 */
	private static Map<String, Object> getTargetPtpAdditionInfo(AlarmDataModel almModel) {
		Map<String, Object> result = new HashMap<String,Object>();
		int emsId = almModel.getEmsId();
		NameAndStringValue_T[] objectName = almModel.getObjectName();
		
		// 取得网元序列号    例：589826
		String neSerialNo = nameUtil.getNeSerialNo(objectName);
		
		// 取得端口
		String ptpName = nameUtil.decompositionName(objectName);
		if (ptpName.contains("CTP:")) {
			// 从Ctp的对象名中获取Ptp对象名
			ptpName = nameUtil.decompositionName(nameUtil.getPtpNameFromCtpName(objectName));			
		}

		// 获取ptp为主的相关信息
		result = dataCollectMapper.getPtpObjInfoForAlm(emsId, neSerialNo, ptpName);

		// E300的PTP信息获取特殊处理（告警信息中的PTP名有可能与基础数据采集中的PTP名不一致）
		//only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
		if ((result == null||result.isEmpty()) && ptpName.contains("ptptype")) {
			String rackNo = nameUtil.getEquipmentNoFromTargetName(ptpName, DataCollectDefine.COMMON.RACK);
			String shelfNo = nameUtil.getEquipmentNoFromTargetName(ptpName, DataCollectDefine.COMMON.SHELF);
			String slotNo = nameUtil.getEquipmentNoFromTargetName(ptpName, DataCollectDefine.COMMON.SLOT);
			String portNo = nameUtil.getEquipmentNoFromTargetName(ptpName, DataCollectDefine.COMMON.PORT);
			String ptpType = nameUtil.getEquipmentNoFromTargetName(ptpName,	DataCollectDefine.ZTE.ZTE_PTP_TYPE);
			String directionString = nameUtil.getEquipmentNoFromTargetName(ptpName,	DataCollectDefine.ZTE.ZTE_DIRECTION);
			String physicalPort = nameUtil.getEquipmentNoFromTargetName(ptpName, DataCollectDefine.ZTE.ZTE_PHYSICAL_PORT);
			String channelNo = nameUtil.getEquipmentNoFromTargetName(ptpName, DataCollectDefine.ZTE.ZTE_CHANEL_NO);
			/* BUG #1396
			现状：
			中兴E300 PTP入库时，PTPTYPE=OPM的端口，解析错误。
			PTP:/direction=sink/layerrate=1/ptptype=OPM/rack=0/shelf=3/slot=10/PhysicalPort=1/ChannelNo=23
			以上例子，无法正确识别入库。
			修改为：
			对PTPTYPE=OPM的端口,独立逻辑处理：
			1.PORT_NO=截取PhysicalPort-CHChannelNo 实例：1-CH1 1-CH2 1-CH3 1-CH80
			2.DISPLAY_NAME 不变 实例：1-CH1 1-CH2 1-CH3 1-CH80
			3.PTP_FTP 设为FTP （因为不能在树上显示）
			4.IS_SYNC_CTP 设为已经同步 (因为太多，而且底下不会有CTP)*/
			if(!physicalPort.isEmpty()&&!channelNo.isEmpty()){
				portNo = physicalPort+"-CH"+channelNo;
			}
			//默认双向
			int direction = 1;
			if(directionString.equals("src")){
				direction = 2;
			}else if(directionString.equals("sink")){
				direction = 3;
			}else if(directionString.equals("NA")){
				direction = 0;
			}
			result = dataCollectMapper.getPtpObjInfoForAlmE300(emsId, neSerialNo, direction, ptpType, rackNo,
					shelfNo, slotNo, portNo);			
		}
		// 如果获取PTP附加信息失败，则获取网元级附加信息
		if (result == null) {
			printAlmParseErrInfo(DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT,
					emsId, objectName);			
			return getTargetNeAdditionInfo(almModel);
		}else if(result.get("PTP_ID")!=null){
			Integer ptpId = Integer.valueOf(result.get("PTP_ID").toString());
			List<Map<String,Object>> affectTps=dataCollectMapper.getAffectTpsByPtpId(ptpId, 
					new Integer[] { DataCollectDefine.LINK_TYPE_EXTERNAL_FLAG}, 
					DataCollectDefine.FALSE);
			if(affectTps!=null&&!affectTps.isEmpty())
				result.put("affectTPs", affectTps);
		}
		// 补充资源信息
		if (result != null && result.get("RESOURCE_ROOM_ID") != null) {
			int roomId = Integer.parseInt(result.get("RESOURCE_ROOM_ID").toString());
			result.putAll(getResourceInfoByRoomId(roomId));
		}
		
		// 补充包机人信息
		if (result != null) {
		result.putAll(getInspectInfo(result.get("BASE_EMS_GROUP_ID"), emsId, result.get("SUBNET_ID"),
				result.get("PARENT_SUBNET"), result.get("NE_ID")));
		}
		return result;
	}
	/**
	 * 获取CTP级别告警的附加信息
	 * @param emsId
	 * @param objectName
	 * @return
	 */
	private static Map<String, Object> getTargetCtpAdditionInfo(AlarmDataModel almModel) {
		Map<String, Object> result = new HashMap<String,Object>();
		int emsId = almModel.getEmsId();
		NameAndStringValue_T[] objectName = almModel.getObjectName();
		
		// 取得ctp
		String ctpName = nameUtil.decompositionCtpName(objectName);
		//中兴存在NonWorkCTP情况，需要特殊处理 转换成正常ctp
		if("NonWorkCTP".equals(objectName[3].name)){
			ctpName = "CTP:"+objectName[3].value;
		}
		/* 例：ctpName = "CTP:/sts3c_au4-j=2"; */
		
		// 获取Ptp为主的相关信息
		result = getTargetPtpAdditionInfo(almModel);
		
		// 获取相关Ctp信息
		if (result != null && result.get("NE_TYPE") != null && result.get("PTP_ID") != null) {
			// 组织查询语句
			Map select = new HashMap();
			
			if (result.get("NE_TYPE").toString().equals(DataCollectDefine.COMMON.NE_TYPE_SDH_FLAG+"")) {
				select.put("NAME", "t_base_sdh_ctp");
			} else {
				select.put("NAME", "t_base_otn_ctp");
				}
			select.put("ID_NAME", "BASE_PTP_ID");
			select.put("ID_VALUE", result.get("PTP_ID"));
			select.put("ID_NAME_2", "NAME");
			select.put("ID_VALUE_2", ctpName);
	
			List<Map> list_ctp = dataCollectMapper.getByParameter(select);
	
			if (list_ctp != null && list_ctp.size() > 0) {
				if (result.get("NE_TYPE").toString().equals(DataCollectDefine.COMMON.NE_TYPE_SDH_FLAG+"")) {
					result.put("CTP_ID", list_ctp.get(0).get("BASE_SDH_CTP_ID"));
					result.put("CTP_TYPE", list_ctp.get(0).get("CONNECT_RATE"));
				} else {
					result.put("CTP_ID", list_ctp.get(0).get("BASE_OTN_CTP_ID"));
					result.put("CTP_TYPE", "光通道");
				}
				result.put("CTP_NAME", list_ctp.get(0).get("DISPLAY_NAME"));
				result.put("CTP_NATIVE_EMS_NAME", list_ctp.get(0).get("NATIVE_EMS_NAME"));
				result.put("CTP_USER_LABEL", list_ctp.get(0).get("USER_LABEL"));
			} else {
				printAlmParseErrInfo(DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT,
						emsId, objectName);
			}
		}
		return result;
	}
	/**
	 * 获取设备安装位置级别告警的附加信息
	 * @param emsId
	 * @param objectName
	 * @return
	 */
	private static Map<String, Object> getTargetEqptHolderAdditionInfo(AlarmDataModel almModel) {
		Map<String, Object> result = new HashMap<String,Object>();
		int emsId = almModel.getEmsId();
		NameAndStringValue_T[] objectName = almModel.getObjectName();
		
		// 取得网元序列号    例：589826
		String neSerialNo = nameUtil.getNeSerialNo(objectName);
		
		// 获取目标名称
		String targetName = nameUtil.decompositionName(objectName);
	
		// 根据Rack、Shelf、Slot分别进行处理
		if (targetName.contains(DataCollectDefine.COMMON.SLOT)) {
			// 获取槽道为主的相关信息
			result = dataCollectMapper.getSlotObjInfoForAlm(emsId, neSerialNo, targetName);
		} else if (targetName.contains(DataCollectDefine.COMMON.SHELF)) {
			// 获取子架为主的相关信息
			result = dataCollectMapper.getShelfObjInfoForAlm(emsId, neSerialNo, targetName);
		} else if (targetName.contains(DataCollectDefine.COMMON.RACK)) {
			// 获取机架为主的相关信息
			result = dataCollectMapper.getRackObjInfoForAlm(emsId, neSerialNo, targetName);
		}
		// 如果获取设备安装位置级附加信息失败，则获取网元级附加信息
		if (result==null) {
			printAlmParseErrInfo(DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_EQUIPMENT_HOLDER,
					emsId, objectName);
			return getTargetNeAdditionInfo(almModel);
		}
		
		// 补充资源信息
		if (result != null && result.get("RESOURCE_ROOM_ID") != null) {
			int roomId = Integer.parseInt(result.get("RESOURCE_ROOM_ID").toString());
			result.putAll(getResourceInfoByRoomId(roomId));
		}
		
		// 补充包机人信息
		if (result != null) {
		result.putAll(getInspectInfo(result.get("BASE_EMS_GROUP_ID"), emsId, result.get("SUBNET_ID"),
				result.get("PARENT_SUBNET"), result.get("NE_ID")));
		}
		return result;
	}
	/**
	 * 获取板卡级别告警的附加信息
	 * @param emsId
	 * @param objectName
	 * @return
	 */
	private static Map<String, Object> getTargetEqptAdditionInfo(AlarmDataModel almModel) {
		Map<String, Object> result = new HashMap<String,Object>();
		int emsId = almModel.getEmsId();
		NameAndStringValue_T[] objectName = almModel.getObjectName();
		
		// 取得网元序列号    例：589826
		String neSerialNo = nameUtil.getNeSerialNo(objectName);
		
		// 取得板卡对象名
		String unitName = nameUtil.decompositionName(objectName);
		
		// 除去告警ObjectName中有时带有的sub-slot信息（贝尔网管的板卡级告警的设备定位信息包含子槽位信息）
		unitName = unitName.replaceFirst("/sub-slot="+"\\d*", "");
		
		// 获取板卡为主的相关信息
		result = dataCollectMapper.getUnitObjInfoForAlm(emsId, neSerialNo, unitName);
		
		// 如果获取板卡级附加信息失败，则获取网元级附加信息
		if (result == null) {
			printAlmParseErrInfo(DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_EQUIPMENT,
					emsId, objectName);
			return getTargetNeAdditionInfo(almModel);
		}
		// 补充资源信息
		if (result != null && result.get("RESOURCE_ROOM_ID") != null) {
			int roomId = Integer.parseInt(result.get("RESOURCE_ROOM_ID").toString());
			result.putAll(getResourceInfoByRoomId(roomId));
		}
		
		// 补充包机人信息
		if (result!=null) {
		result.putAll(getInspectInfo(result.get("BASE_EMS_GROUP_ID"), emsId, result.get("SUBNET_ID"),
				result.get("PARENT_SUBNET"), result.get("NE_ID")));
		}
		
		return result;
	}
	/**
	 * 获取保护组级别告警的附加信息
	 * @param emsId
	 * @param objectName
	 * @return
	 */
	private static Map<String, Object> getTargetProtectAdditionInfo(AlarmDataModel almModel) {
		Map<String, Object> result = new HashMap<String,Object>();
		int emsId = almModel.getEmsId();
		NameAndStringValue_T[] objectName = almModel.getObjectName();
		
		// 取得网元序列号    例：589826
		 String neSerialNo = nameUtil.getNeSerialNo(objectName);

		// 获取保护组对象名
		String protectName = nameUtil.decompositionName(objectName);
		
		// 获取保护组为主的相关信息
		result = dataCollectMapper.getProtectObjInfoForAlm(emsId, neSerialNo, protectName);

		// 如果获取保护组附加信息失败，则获取网元级附加信息
		if (result == null) {
			printAlmParseErrInfo(DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_PROTECTION_GROUP,
					emsId, objectName);
			return getTargetNeAdditionInfo(almModel);
		}
		
		// 补充资源信息
		if (result != null && result.get("RESOURCE_ROOM_ID") != null) {
			int roomId = Integer.parseInt(result.get("RESOURCE_ROOM_ID").toString());
				result.putAll(getResourceInfoByRoomId(roomId));
			}
		
		// 补充包机人信息
		if (result !=null) {
		result.putAll(getInspectInfo(result.get("BASE_EMS_GROUP_ID"), emsId, result.get("SUBNET_ID"),
				result.get("PARENT_SUBNET"), result.get("NE_ID")));
		}
		return result;
	}
	/**
	 * 获取AID级别告警的附加信息
	 * @param emsId
	 * @param objectName
	 * @return
	 */
	private static Map<String, Object> getTargetAidAdditionInfo(AlarmDataModel almModel) {
		Map<String, Object> result = null;
		int emsId = almModel.getEmsId();
		NameAndStringValue_T[] objectName = almModel.getObjectName();
		int factory = almModel.getFactory();
		StringBuilder sb = new StringBuilder();
		
		// objectName数组的最后一项键值的名称
		String NAME=objectName[objectName.length-1].name;
		
		// 由于各厂家的AID类型告警内容不一，因此按厂家分类进行处理
		// 中兴
		if (DataCollectDefine.FACTORY_ZTE_FLAG == factory) {
			// 如果objectName数组的最后一项键值的名称为"AID"
			if ("AID".equals(NAME)) {
				// 取得AID项的值
				String aidValue = objectName[objectName.length-1].value;
				// 匹配数字格式 1/1/1/201，中兴网管中出现
				if (aidValue.matches("\\d+/\\d+/\\d+/\\d+")) {
					// 取得网元序列号    例：589826
					String neSerialNo = nameUtil.getNeSerialNo(objectName);				
					String[] value = aidValue.split("/");
					// 获取AID为主的相关信息
					result = dataCollectMapper.getAidObjInfoForAlm(emsId, neSerialNo, value[0], value[1], value[2]);
					result.put("PORT_NO", value[3]);
					
				// 匹配"/aid="开头的值信息，中兴网管中出现
				} else if (aidValue.contains("/aid=")) {
					/* 中兴U31上WDM设备上出现的AID类型告警的格式与板卡级告警结构类似
					 * 所以暂按板卡级告警的方式来获取告警附加信息 */
					NameAndStringValue_T[] newObjName = new NameAndStringValue_T[objectName.length-1];
					for (int i=0; i<newObjName.length; i++) {
						newObjName[i] = new NameAndStringValue_T();
						newObjName[i].name = objectName[i].name;
						newObjName[i].value = objectName[i].value;
					}
					// 按板卡级告警获取附加信息
					return getTargetEqptAdditionInfo(almModel);
								
				// 其他任意字符开头的值信息，贝尔网管中出现
				} else {
					// 获取网管级附加信息
					return getTargetEmsAdditionInfo(almModel);
				}
				
			// 中兴U31上PTN设备上AID类型告警中ObjectName出现了PTP/CTP格式
			} else if ("PTP".equals(NAME) || "FTP".equals(NAME)) {
				// 按PTP级告警获取告警附加信息
				return getTargetPtpAdditionInfo(almModel);
			} else if ("CTP".equals(NAME)) {
				// 按CTP级告警获取告警附加信息
				return getTargetCtpAdditionInfo(almModel);
			
			/* 时钟类型告警
			 * name：EMS|ManagedElement|CLKS
			 * value：ZTE/1|284(P)|/rack=0/shelf=1/slot=1/src=11_10
			 */
			} else if ("CLKS".equals(NAME)) {
				// 取得网元序列号    例：589826
				String neSerialNo = nameUtil.getNeSerialNo(objectName);		
				// 取得CLKS项的值
				String clksValue = objectName[objectName.length-1].value;
				String str = parseInstallLocInfoForZTE(clksValue);
				String[] loc = str.split(",");
				// 获取AID为主的相关信息
				result = dataCollectMapper.getAidObjInfoForAlm(emsId, neSerialNo, loc[0], loc[1], loc[2]);
				if (result != null) {
					result.put("PORT_NO", loc[3]);
				} else {
					// 如果信息获取失败，则获取网元级附加信息
					System.out.println("告警附加信息获取错误（AID(CLKS)-安装位置信息），EmsId:"+emsId+"，ClksValue:" + clksValue);
					return getTargetNeAdditionInfo(almModel);
				}
				
			/* TM-MEP告警
			 * name1：EMS|ManagedElement|FTP|TM-MEG|TM-MEP
			 * value1：ZTE/1|56(P)|/rack=0/shelf=1/slot=255/port=125_22|12|1
			 * name2：EMS|ManagedElement|PTP|CTP|TM-MEG|TM-MEP
			 * value2：ZTE/1|1(P)|/rack=0/shelf=1/slot=10/port=1_1|/tmp=4085|8|4
			 */
			} else if ("TM-MEP".equals(NAME)) {
				// 获取PTP级别的告警附加信息
				result = getTargetPtpAdditionInfo(almModel);
				// 补充CTP及其以后的相关信息
				if (result != null && objectName[3].name.equals("CTP")) {
					StringBuilder ctpName = new StringBuilder();
					for (int i=3; i<objectName.length; i++) {
						ctpName.append(objectName[i].value).append(':');
					}
					ctpName.deleteCharAt(ctpName.length()-1);
					result.put("CTP_NAME", ctpName.toString());
				}
				return result;
			}
		}
		
		// 阿尔卡特_贝尔
		if (DataCollectDefine.FACTORY_ALU_FLAG == factory) {
			// 如果objectName数组的最后一项键值的名称为"AID"
			if ("AID".equals(NAME)) {
				// 取得AID项的值
				String aidValue = objectName[objectName.length-1].value;
				// 解析AID项的值
				String formatedValue = parseAidInfoForAlu(aidValue);

				if (!formatedValue.isEmpty()) {
					String[] tempStr = formatedValue.split(",");
					// 按emsId、网元原始名称、机架号、子架号、槽位号获取告警附加信息
					result = dataCollectMapper.getAidObjInfoForAlu(emsId, tempStr[0],
							tempStr[1], tempStr[2], tempStr[3]);
					
					// 有端口号
					if (result != null && tempStr.length == 5) {
						result.put("PORT_NAME", tempStr[4]);			
					} else {
						// 如果信息获取失败，则获取网元级附加信息
						result = dataCollectMapper.getNeObjInfoByNativeNameForAlm(emsId, tempStr[0]);
						sb.append("告警附加信息获取错误（AID-安装位置信息），EmsId:").append(emsId);
						sb.append("，AidValue:").append(aidValue);
						System.out.println(sb.toString());
						if (result == null) {
							// 如果网元信息获取失败，则获取网管级附加信息
							printAlmParseErrInfo(DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_MANAGED_ELEMENT,
									emsId, objectName);
							return getTargetEmsAdditionInfo(almModel);
						}
					}
					
				// 其他非格式化的值信息
				} else {
					// 获取网管级附加信息
					result = getTargetEmsAdditionInfo(almModel);
					// 将AID字段的值写入到综告标识
					if (result != null) {
						int size = objectName.length;
						sb.setLength(0);
						for (int i=0; i<size; i++) {
							sb.append(objectName[i].value).append(':');
						}
						sb.deleteCharAt(sb.length()-1);
						result.put("COMPLEX_ALM_DESCRIPTION", sb.toString());
					}
					return result;
				}
				
			// 如果objectName数组的最后一项键值的名称为"PMP"
			// name: EMS|ManagedElement|PTP|PMP 或  EMS|ManagedElement|PTP|CTP|PMP
			} else if ("PMP".equals(NAME)) {
				// 获取CTP级附加信息
				if (objectName[objectName.length-2].name.equals("CTP")) {
					return getTargetCtpAdditionInfo(almModel);
				
				// 获取PTP级附加信息
				} else if (objectName[objectName.length-2].name.equals("PTP")) {
					return getTargetPtpAdditionInfo(almModel);
				}
			
			// 其他非确定任意字符开头的值信息
			} else {
				// 获取网管级附加信息
				result = getTargetEmsAdditionInfo(almModel);
				// 将objectName的值写入综告标识
				if (result != null) {
					int size = objectName.length;
					sb.setLength(0);
					for (int i=0; i<size; i++) {
						sb.append(objectName[i].value).append(':');
					}
					sb.deleteCharAt(sb.length()-1);
					result.put("COMPLEX_ALM_DESCRIPTION", sb.toString());
				}
				return result;
			}
			
		// 其他厂家	
		} else {
			// 获取网管级附加信息
			result = getTargetEmsAdditionInfo(almModel);
			// 将objectName的值写入综告标识
			if (result != null) {
				int size = objectName.length;
				sb.setLength(0);
				for (int i=0; i<size; i++) {
					sb.append(objectName[i].value).append(':');
				}
				sb.deleteCharAt(sb.length()-1);
				result.put("COMPLEX_ALM_DESCRIPTION", sb.toString());
			}
			return result;
		}
		
		// 补充资源信息
		if (result != null && result.get("RESOURCE_ROOM_ID") != null) {
			int roomId = Integer.parseInt(result.get("RESOURCE_ROOM_ID").toString());
			result.putAll(getResourceInfoByRoomId(roomId));
		}
		
		// 补充包机人信息
		if (result != null) {
		result.putAll(getInspectInfo(result.get("BASE_EMS_GROUP_ID"), emsId, result.get("SUBNET_ID"),
				result.get("PARENT_SUBNET"), result.get("NE_ID")));
		}
		return result;
	}
	/**
	 * 获取资源相关附加信息
	 * @param roomId
	 * @return
	 */
	private static Map<String, Object> getResourceInfoByRoomId(int roomId) {
		Map<String, Object> result = new HashMap();

		// 铁总北京项目资源划分标志
		boolean resFlagForTZBJ = false;
		String resStr = CommonUtil.getSystemConfigProperty("ResourceForTZBJ");	
		if (resStr != null && "true".equals(resStr)) {
			resFlagForTZBJ = true;
		}
		Map select = new HashMap();
		// 铁总流程
		if (resFlagForTZBJ) {
			// 查询局站信息
			select.put("NAME", "t_resource_station");
			select.put("ID_NAME", "RESOURCE_STATION_ID");
			select.put("ID_VALUE", roomId);
			List<Map> list_station = dataCollectMapper.getByParameter(select);
			if (list_station != null && list_station.size() > 0) {
				// 局站相关附加信息
				result.put("STATION_ID", list_station.get(0).get("RESOURCE_STATION_ID"));
				result.put("DISPLAY_STATION", list_station.get(0).get("STATION_NAME"));

				// 查询区域信息
				select.clear();
				select.put("NAME", "t_resource_area");
				select.put("ID_NAME", "RESOURCE_AREA_ID");
				select.put("ID_VALUE", list_station.get(0).get("RESOURCE_AREA_ID"));
				List<Map> list_area = dataCollectMapper.getByParameter(select);

				if (list_area != null && list_area.size() > 0) {
					// 局站相关附加信息
					result.put("AREA_ID", list_area.get(0).get("RESOURCE_AREA_ID"));
					result.put("DISPLAY_AREA", list_area.get(0).get("AREA_NAME"));
				}
			}
		// 默认流程
		} else {
			// 查询机房及局站信息
			Map roomMap = dataCollectMapper.getRoomAndStationInfoByRoomId(roomId);
			if (roomMap != null) {
				result.put("RESOURCE_ROOM_ID", roomId);
				result.put("RESOURCE_ROOM", roomMap.get("ROOM_NAME"));
				result.put("STATION_ID", roomMap.get("STATION_ID"));
				result.put("DISPLAY_STATION", roomMap.get("STATION_NAME"));
				if (roomMap.get("RESOURCE_AREA_ID") != null) {
					// 查询区域信息
					select.clear();
					select.put("NAME", "t_resource_area");
					select.put("ID_NAME", "RESOURCE_AREA_ID");
					select.put("ID_VALUE", roomMap.get("RESOURCE_AREA_ID"));
					List<Map> list_area = dataCollectMapper.getByParameter(select);	
					if (list_area != null && list_area.size() > 0) {
						result.put("AREA_ID", list_area.get(0).get("RESOURCE_AREA_ID"));
						result.put("DISPLAY_AREA", list_area.get(0).get("AREA_NAME"));
					}
				}
			}
		}
		return result;
	}
	/**
	 * 获取包机人附加信息
	 * @param emsGrpId
	 * @param emsId
	 * @param subnetIdList
	 * @param neId
	 * @return
	 */
	private static Map<String, Object> getInspectInfo(Object emsGrpId, Integer emsId,
			Object subnetId, Object parentSubnet, Object neId) {
		Map<String, Object> result = new HashMap<String,Object>();
		Map<String, Object> param = new HashMap<String, Object>();
		if (emsGrpId != null) {
			param.put("emsGrpId", emsGrpId);
			}
		if (emsId != null) {
			param.put("emsId", emsId);
		}
		if (subnetId != null) {
			List<Object> list_subId = new ArrayList<Object>();
			// 组织子网ID列表
			list_subId.add(subnetId);
			// 判断是否有二级子网存在
			if (parentSubnet != null) {
				// 将二级子网的id存入list
				list_subId.add(parentSubnet);
				// 获取所有相关子网的id,一共遍历10次，如果子网嵌套超过10层，则10层以上不作处理
				for (int j = 1; j < list_subId.size()
						&& list_subId.size() <= 10; j++) {
					Map<String, Object> select = new HashMap<String, Object>();
					select.put("NAME", "t_base_subnet");
					select.put("ID_NAME", "BASE_SUBNET_ID");
					select.put("ID_VALUE", list_subId.get(j));
					// 查处二级子网
					List<Map> list_parent = dataCollectMapper.getByParameter(select);
					if (list_parent != null && list_parent.size() > 0) {
						// 判断二级子网是否存在三级节点
						if (list_parent.get(0).get("PARENT_SUBNET") != null) {
							list_subId.add(list_parent.get(0).get(
									"PARENT_SUBNET"));
	}
	}
				}
			}			
			param.put("subnetIdList", list_subId);
	}
		if (neId != null) {
			param.put("neId", neId);
		}
		// 获取包机人信息
		List<Map<String, Object>> mans = dataCollectMapper.getInspectInfoForAlm(param);
		
		// 整合包机人id和名称		
		String engineerId = "";
		String engineer = "";
		for (Map<String, Object> item : mans) {
			engineerId += item.get("INSPECT_ENGINEER_ID").toString() + ",";
			engineer += item.get("NAME").toString() + ",";
		}
		// 给包机人信息赋值,并将最后一个逗号去掉
		if (engineerId.length() > 0) {
			result.put("INSPECT_ENGINEER_ID", engineerId.substring(0,
					engineerId.length() - 1));
			result.put("INSPECT_ENGINEER", engineer.substring(0,
					engineer.length() - 1));
	}
		return result;
	}
	
	/**
	 * 解析贝尔AID告警信息中的设备安装位置信息
	 * @param nameString 'XiAn-6002#r01sr1sl10/ETHLocPort#12#1'
	 * @return 以逗号分隔的设备安装位置信息，格式：机架号,子架号,槽位号,端口号
	 */
	private static String parseInstallLocInfo(String nameString){
		String fromatStr = "";
		List<String> tmpList=NameAndStringValueUtil.match(nameString,DataCollectDefine.ALU.PTP_REGEX);
		if(tmpList!=null&&!tmpList.isEmpty()){
			fromatStr=tmpList.get(0);
			fromatStr=fromatStr.replaceFirst(DataCollectDefine.ALU.PTP_PORT+"0*", ",");
			fromatStr=fromatStr.replaceFirst(DataCollectDefine.ALU.PTP_RACK+"0*", "");
			fromatStr=fromatStr.replaceFirst(DataCollectDefine.ALU.PTP_SHELF+"0*", ",");
			fromatStr=fromatStr.replaceFirst(DataCollectDefine.ALU.PTP_SLOT+"0*", ",");
		}
		return fromatStr;
	}
	
	/**
	 * 解析贝尔AID告警信息中的设备安装位置信息
	 * @param aidStr 目前匹配三种格式：<br>
	 *        1）ChangXin/r1sr1sl14/ETHLocPort#2#1<br>
	 *        2）GanYanChi_6012#r01sr1sl02/ETHLocPort#11#1<br>
	 *        3）HuiAnPuLiangKu-A026#r01sr1sl02<br>
	 * @return 以逗号分隔的设备安装位置信息，不在要求解析格式范围内时返回空字符串<br>
	 *         格式1：网元名,机架号,子架号,槽位号,端口号<br>
	 *         格式2：网元名,机架号,子架号,槽位号
	 */
	private static String parseAidInfoForAlu(String aidStr) {
		String result = "";	
		String name1 = "[%-.0-z]+/";
		String name2 = "[%-z]+#";
		String rack = "r";
		String shelf = "((sr)|s)";
		String slot = "((sl)|b)";
		String port = "/[A-z]+#";
		
		// aidStr:ChangXin/r1sr1sl14/ETHLocPort#2#1
		String pattern1 = name1 + rack + "[0-9]+" + shelf + "[0-9]+" + slot + "[0-9]+" + port + "[0-9]+";
		
		// aidStr:GanYanChi_6012#r01sr1sl02/ETHLocPort#11#1
		String pattern2 = name2 + rack + "[0-9]+" + shelf + "[0-9]+" + slot + "[0-9]+" + port +"[0-9]+";
		
		// aidStr:HuiAnPuLiangKu-A026#r01sr1sl02
		String pattern3 = name2 + rack + "[0-9]+" + shelf + "[0-9]+" + slot + "[0-9]+";

		List<String> tmpList = NameAndStringValueUtil.match(aidStr, pattern1);
		if (tmpList != null && !tmpList.isEmpty()) {	
			result = tmpList.get(0);
			result = result.replaceFirst(port+"0*", ",");
			result = result.replaceFirst("/" + rack + "0*", ",");
			result = result.replaceFirst(shelf + "0*", ",");
			result = result.replaceFirst(slot + "0*", ",");
		} else {
			tmpList = NameAndStringValueUtil.match(aidStr, pattern2);
			if (tmpList != null && !tmpList.isEmpty()) {	
				result = tmpList.get(0);
				result = result.replaceFirst(port+"0*", ",");
				result = result.replaceFirst("#" + rack + "0*", ",");
				result = result.replaceFirst(shelf + "0*", ",");
				result = result.replaceFirst(slot + "0*", ",");
			} else {
				tmpList = NameAndStringValueUtil.match(aidStr, pattern3);
				if (tmpList != null && !tmpList.isEmpty()) {	
					result = tmpList.get(0);
					result = result.replaceFirst("#" + rack + "0*", ",");
					result = result.replaceFirst(shelf + "0*", ",");
					result = result.replaceFirst(slot + "0*", ",");
				}
			}
		}
		return result;
	}
	
	/**
	 * 解析中兴AID告警信息中的设备安装位置信息
	 * @param nameString '/rack=0/shelf=1/slot=255/port=125_33'
	 * @return 以逗号分隔的设备安装位置信息，格式：机架号,子架号,槽位号,端口号
	 */
	private static String parseInstallLocInfoForZTE(String nameString){
		String result = "";
		String regExp = "/rack=[0-9]+/shelf=[0-9]+/slot=[0-9]+/(port=|src=)[0-9]*_?[0-9]*";
		List<String> tmpList = NameAndStringValueUtil.match(nameString, regExp);
		if (tmpList != null && !tmpList.isEmpty()) {
			result=tmpList.get(0);
			result=result.replaceFirst("/rack=", "");
			result=result.replaceFirst("/shelf=", ",");
			result=result.replaceFirst("/slot=", ",");
			result=result.replaceFirst("(/port=|src=)", ",");			
		}
		return result;
	}
	
	private static void printAlmParseErrInfo(int type, int emsId, NameAndStringValue_T[] objectName) {
		String head = "";
		StringBuilder sb = new StringBuilder();
		
		switch (type) {
		case DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_EMS:
			head = "告警附加信息获取错误（网管级别）";
			break;
		case DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_MANAGED_ELEMENT:
			head = "告警附加信息获取错误（网元级别）";
			break;
		case DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_TOPOLOGICAL_LINK:
			head = "告警附加信息获取错误（拓扑链路级别）";
			break;
		case DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_SUBNETWORK_CONNECTION:
			head = "告警附加信息获取错误（子网连接级别）";
			break;
		case DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT:
			head = "告警附加信息获取错误（端口级别）";
			break;
		case DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT:
			head = "告警附加信息获取错误（通道级别）";
			break;
		case DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_EQUIPMENT_HOLDER:
			head = "告警附加信息获取错误（设备安装位置级别）";
			break;
		case DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_EQUIPMENT:
			head = "告警附加信息获取错误（板卡级别）";
			break;
		case DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_PROTECTION_GROUP:
			head = "告警附加信息获取错误（保护组级别）";
			break;
		case DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_AID:
			head = "告警附加信息获取错误（AID级别）";
			break;
		default:
			head = "告警附加信息获取错误（未知级别）";
			break;
		}
		
		sb.append(head).append("，EmsId:").append(emsId);
		System.out.println(sb.toString());
		if (objectName != null) {
			for (NameAndStringValue_T t : objectName) {
				sb.setLength(0);
				sb.append("    Name:").append(t.name);
				System.out.println(sb.toString());
				sb.setLength(0);
				sb.append("    Value:").append(t.value);
				System.out.println(sb.toString());
			}
		}
	}
	
	/**
	 * 根据告警信息返回相关的附加信息
	 * 
	 * @param model
	 * @return
	 */
	public static Map<String, Object> getTargetAdditionInfo(int emsConnectionId,int objectType, NameAndStringValue_T[] objectName) {
		Map neMap=null;
		Map rackMap=null;
		Map shelfMap=null;
		Map slotMap=null;
		Map unitMap=null;
		Map ptpMap=null;
		Map ctpMap=null;
		Date t1 = new Date();
		NameAndStringValueUtil nameUtil = new NameAndStringValueUtil();
		// 判断告警级别
		Map select = null;
		Map target = new HashMap();

		// 定义包机人的变量
		String inspect_engineer = "";
		String inspect_engineer_id = "";
		Map inspect_map = new HashMap();

		// 分四级查找 1. 网元 2.子网，3网管，4网管分组

		// 先取得网管以及分组信息
		select = new HashMap();
		select.put("NAME", "t_base_ems_connection");
		select.put("ID_NAME", "BASE_EMS_CONNECTION_ID");
		select.put("ID_VALUE", emsConnectionId);
		List<Map> list_ems = dataCollectMapper.getByParameter(select);
		if (list_ems != null && list_ems.size() > 0) {
			target.put("EMS_NAME", list_ems.get(0).get(
				"DISPLAY_NAME"));
			
			// 查询包机人信息
			// 3.网管
			// 组织查询语句
			select = new HashMap();
			select.put("ID_NAME", "TARGET_TYPE");
			select.put("ID_VALUE",
					DataCollectDefine.COMMON.TARGET_TYPE_EMS_FLAG);
			select.put("ID_NAME_2", "TARGET_ID");
			select.put("ID_VALUE_2", list_ems.get(0).get(
					"BASE_EMS_CONNECTION_ID"));

			List<Map> inspect_ems = dataCollectMapper.getInspect(select);
			if (inspect_ems != null && inspect_ems.size() > 0) {
				for (Map map : inspect_ems) {
					inspect_map.put(map.get("INSPECT_ENGINEER_ID"), map
							.get("NAME"));
				}
			}		

			select = new HashMap();
			select.put("NAME", "t_base_ems_group");
			select.put("ID_NAME", "BASE_EMS_GROUP_ID");
			select.put("ID_VALUE", list_ems.get(0).get("BASE_EMS_GROUP_ID"));
			List<Map> list_group = dataCollectMapper.getByParameter(select);
			if (list_group != null && list_group.size() > 0) {
				// 网管分组的附加信息
				target.put("BASE_EMS_GROUP_ID", list_group.get(0).get(
						"BASE_EMS_GROUP_ID"));
				target
						.put("EMS_GROUP_NAME", list_group.get(0).get(
								"GROUP_NAME"));

				// 4.网管分组 查询包机人信息
				// 组织查询语句
				select = new HashMap();
				select.put("ID_NAME", "TARGET_TYPE");
				select.put("ID_VALUE",
						DataCollectDefine.COMMON.TARGET_TYPE_EMSGROUP_FLAG);
				select.put("ID_NAME_2", "TARGET_ID");
				select.put("ID_VALUE_2", list_group.get(0).get(
						"BASE_EMS_GROUP_ID"));

				List<Map> inspect_group = dataCollectMapper.getInspect(select);
				if (inspect_group != null && inspect_group.size() > 0) {
					for (Map map : inspect_ems) {
						inspect_map.put(map.get("INSPECT_ENGINEER_ID"), map
								.get("NAME"));
					}
				}
			}
		}
		// 验证网元信息
		// 取得网元序列号 589826
		 String neSerialNo = nameUtil.getNeSerialNo(objectName);
		//String neSerialNo = "111(P)";
		// 组织查询语句
		select = new HashMap();
		select.put("NAME", "t_base_ne");
		select.put("ID_NAME", "BASE_EMS_CONNECTION_ID");
		select.put("ID_VALUE", emsConnectionId);
		select.put("ID_NAME_2", "NAME");
		select.put("ID_VALUE_2", neSerialNo);
		List<Map> list_ne = dataCollectMapper.getByParameter(select);
		if (list_ne != null && list_ne.size() > 0) {
			neMap=list_ne.get(0);

			// 查询子网信息
			select = new HashMap();
			select.put("NAME", "t_base_subnet");
			select.put("ID_NAME", "BASE_SUBNET_ID");
			select.put("ID_VALUE", list_ne.get(0).get("BASE_SUBNET_ID"));
			List<Map> list_subnet = dataCollectMapper.getByParameter(select);
			if (list_subnet != null && list_subnet.size() > 0) {
				// 子网相关附加信息
				target.put("BASE_SUBNET_ID", list_subnet.get(0)
						.get("BASE_SUBNET_ID"));
				target.put("BASE_SUBNET_NAME", list_subnet.get(0)
						.get("DISPLAY_NAME"));

				List list_subId = new ArrayList();
				list_subId.add(list_subnet.get(0).get("BASE_SUBNET_ID"));
				// 2.子网
				// 判断是否有二级子网存在
				if (list_subnet.get(0).get("PARENT_SUBNET") != null) {
					// 将二级子网的id存入list
					list_subId.add(list_subnet.get(0).get("PARENT_SUBNET"));
					// 获取所有相关子网的id,一共遍历10次，如果子网嵌套超过10层，则10层以上不作处理
					for (int j = 1; j < list_subId.size()
							&& list_subId.size() <= 10; j++) {
						select = new HashMap();
						select.put("NAME", "t_base_subnet");
						select.put("ID_NAME", "BASE_SUBNET_ID");
						select.put("ID_VALUE", list_subId.get(j));
						// 查处二级子网
						List<Map> list_parent = dataCollectMapper
								.getByParameter(select);
						if (list_parent != null && list_parent.size() > 0) {
							// 判断二级子网是否存在三级节点
							if (list_parent.get(0).get("PARENT_SUBNET") != null) {
								list_subId.add(list_parent.get(0).get(
										"PARENT_SUBNET"));
							}
						}
					}
				}
				// 查询出子网相关的包机人
				// 组织查询语句
				select = new HashMap();
				select.put("ID_NAME", "TARGET_TYPE");
				select.put("ID_VALUE",
						DataCollectDefine.COMMON.TARGET_TYPE_SUBNET_FLAG);
				select.put("ID_NAME_2", "TARGET_ID");
				select.put("ID_VALUE_2", list_subId);

				List<Map> inspect_sunet = dataCollectMapper
						.getInspectSubnet(select);
				if (inspect_sunet != null && inspect_sunet.size() > 0) {
					for (Map map : inspect_sunet) {
						inspect_map.put(map.get("INSPECT_ENGINEER_ID"), map
								.get("NAME"));
					}
				}
			}

			// 铁总北京项目资源划分标志
			boolean resFlagForTZBJ = false;
			String resStr = CommonUtil.getSystemConfigProperty("ResourceForTZBJ");	
			if (resStr != null && "true".equals(resStr)) {
				resFlagForTZBJ = true;
			}
			
			// 铁总流程
			if (resFlagForTZBJ) {
				// 查询局站信息
				select = new HashMap();
				select.put("NAME", "t_resource_station");
				select.put("ID_NAME", "RESOURCE_STATION_ID");
				select.put("ID_VALUE", list_ne.get(0).get("RESOURCE_ROOM_ID"));
				List<Map> list_station = dataCollectMapper.getByParameter(select);
				if (list_station != null && list_station.size() > 0) {
					// 局站相关附加信息
					target.put("STATION_ID", list_station.get(0).get(
							"RESOURCE_STATION_ID"));
					target.put("DISPLAY_STATION", list_station.get(0).get(
							"STATION_NAME"));

					// 查询区域信息
					select = new HashMap();
					select.put("NAME", "t_resource_area");
					select.put("ID_NAME", "RESOURCE_AREA_ID");
					select.put("ID_VALUE", list_station.get(0).get(
							"RESOURCE_AREA_ID"));
					List<Map> list_area = dataCollectMapper
							.getByParameter(select);

					if (list_area != null && list_area.size() > 0) {
						// 局站相关附加信息
						target.put("AREA_ID", list_area.get(0).get(
								"RESOURCE_AREA_ID"));
						target.put("DISPLAY_AREA", list_area.get(0).get(
								"AREA_NAME"));
					}
				}
			// 默认流程
			} else {
				// 查询机房信息
				select = new HashMap();
				select.put("NAME", "t_resource_room");
				select.put("ID_NAME", "RESOURCE_ROOM_ID");
				select.put("ID_VALUE", list_ne.get(0).get("RESOURCE_ROOM_ID"));
				List<Map> list_room = dataCollectMapper.getByParameter(select);
				if (list_room != null && list_room.size() > 0) {
					// 机房相关附加信息
					target.put("RESOURCE_ROOM_ID", list_room.get(0).get(
							"RESOURCE_ROOM_ID"));
					target.put("RESOURCE_ROOM", list_room.get(0).get("ROOM_NAME"));
	
					// 查询局站信息
					select = new HashMap();
					select.put("NAME", "t_resource_station");
					select.put("ID_NAME", "RESOURCE_STATION_ID");
					select.put("ID_VALUE", list_room.get(0).get(
							"RESOURCE_STATION_ID"));
					List<Map> list_station = dataCollectMapper
							.getByParameter(select);
					if (list_station != null && list_station.size() > 0) {
						// 局站相关附加信息
						target.put("STATION_ID", list_station.get(0).get(
								"RESOURCE_STATION_ID"));
						target.put("DISPLAY_STATION", list_station.get(0).get(
								"STATION_NAME"));
	
						// 查询区域信息
						select = new HashMap();
						select.put("NAME", "t_resource_area");
						select.put("ID_NAME", "RESOURCE_AREA_ID");
						select.put("ID_VALUE", list_station.get(0).get(
								"RESOURCE_AREA_ID"));
						List<Map> list_area = dataCollectMapper
								.getByParameter(select);
	
						if (list_area != null && list_area.size() > 0) {
							// 局站相关附加信息
							target.put("AREA_ID", list_area.get(0).get(
									"RESOURCE_AREA_ID"));
							target.put("DISPLAY_AREA", list_area.get(0).get(
									"AREA_NAME"));
						}
					}
				}					
			}

			// 查询包机人信息

			// 1.网元
			// 组织查询语句
			select = new HashMap();
			select.put("ID_NAME", "TARGET_TYPE");
			select
					.put("ID_VALUE",
							DataCollectDefine.COMMON.TARGET_TYPE_NE_FLAG);
			select.put("ID_NAME_2", "TARGET_ID");
			select.put("ID_VALUE_2", list_ne.get(0).get("BASE_NE_ID"));

			List<Map> inspect_ne = dataCollectMapper.getInspect(select);
			if (inspect_ne != null && inspect_ne.size() > 0) {
				for (Map map : inspect_ne) {
					inspect_map.put(map.get("INSPECT_ENGINEER_ID"), map
							.get("NAME"));
				}
			}

		}
		if (neMap != null) {
			// ctp
			if (objectType == DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT) {
				// 取得端口
				String ptpNameString = nameUtil.decompositionName(nameUtil.getPtpNameFromCtpName(objectName));
				// String ptpNameString =
				// "PTP:/rack=1/shelf=1/slot=3/domain=sdh/port=1";
				// 取得ctp
				String ctpValue = nameUtil.decompositionCtpName(objectName);
				//中兴存在NonWorkCTP情况，需要特殊处理 转换成正常ctp
				if("NonWorkCTP".equals(objectName[3].name)){
					ctpValue = "CTP:"+objectName[3].value;
				}
				// String ctpValue = "CTP:/sts3c_au4-j=2";

				// 组织查询语句
				select = new HashMap();
				select.put("NAME", "t_base_ptp");
				select.put("ID_NAME", "BASE_NE_ID");
				select.put("ID_VALUE", list_ne.get(0).get("BASE_NE_ID"));
				select.put("ID_NAME_2", "NAME");
				select.put("ID_VALUE_2", ptpNameString);

				// 取得ptp对象
				List<Map> list_ptp = dataCollectMapper.getByParameter(select);
				
				//only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
				if((list_ptp == null||list_ptp.isEmpty())&& ptpNameString.contains("ptptype")){
					Integer neId=Integer.valueOf(String.valueOf(list_ne.get(0).get("BASE_NE_ID")));
					Map ptp = selectPtpForE300(neId,ptpNameString);
					if(ptp!=null&&!ptp.isEmpty()){
						list_ptp=new ArrayList<Map>();
						list_ptp.add(ptp);
					}
				}
				
				if (list_ptp != null && list_ptp.size() > 0) {
					ptpMap=list_ptp.get(0);
					
					// 组织查询语句
					select = new HashMap();
					
					if(list_ne.get(0).get("TYPE").toString().equals(DataCollectDefine.COMMON.NE_TYPE_SDH_FLAG+"")){
						select.put("NAME", "t_base_sdh_ctp");
					}else{
						select.put("NAME", "t_base_otn_ctp");
					}
					select.put("ID_NAME", "BASE_PTP_ID");
					select.put("ID_VALUE", list_ptp.get(0).get("BASE_PTP_ID"));
					select.put("ID_NAME_2", "NAME");
					select.put("ID_VALUE_2", ctpValue);

					List<Map> list_ctp = dataCollectMapper
							.getByParameter(select);

					if (list_ctp != null && list_ctp.size() > 0) {
						ctpMap=list_ctp.get(0);
					}
				}
			}
			// ptp
			else if (objectType == DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT) {
				// 取得端口
				String ptpNameString = nameUtil.decompositionName(objectName);

				// 组织查询语句
				select = new HashMap();
				select.put("NAME", "t_base_ptp");
				select.put("ID_NAME", "BASE_NE_ID");
				select.put("ID_VALUE", list_ne.get(0).get("BASE_NE_ID"));
				select.put("ID_NAME_2", "NAME");
				select.put("ID_VALUE_2", ptpNameString);

				// 取得ptp对象
				List<Map> list_ptp = dataCollectMapper.getByParameter(select);
				//only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
				if((list_ptp == null||list_ptp.isEmpty())&& ptpNameString.contains("ptptype")){
					Integer neId=Integer.valueOf(String.valueOf(list_ne.get(0).get("BASE_NE_ID")));
					Map ptp = selectPtpForE300(neId,ptpNameString);
					if(ptp!=null&&!ptp.isEmpty()){
						list_ptp=new ArrayList<Map>();
						list_ptp.add(ptp);
					}
				}
				if (list_ptp != null && list_ptp.size() > 0) {
					ptpMap=list_ptp.get(0);
				}
			}
			// 设置equip对象相关属性
			else if (objectType == DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_EQUIPMENT) {

				String unitName = nameUtil.decompositionName(objectName);
	
				// 组织查询语句
				select = new HashMap();
				select.put("NAME", "t_base_unit");
				select.put("ID_NAME", "BASE_NE_ID");
				select.put("ID_VALUE", list_ne.get(0).get("BASE_NE_ID"));
				select.put("ID_NAME_2", "NAME");
				select.put("ID_VALUE_2", unitName);
	
				// 查询板卡信息
				List<Map> list_unit = dataCollectMapper.getByParameter(select);
				if (list_unit != null && list_unit.size() > 0) {
					unitMap=list_unit.get(0);
				}
			}
			// 解析成rack，shelf，slot
			else if (objectType == DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_EQUIPMENT_HOLDER) {
				String targetName = nameUtil.decompositionName(objectName);
				if (targetName.contains(DataCollectDefine.COMMON.SLOT)) {
					// 组织查询语句
					select = new HashMap();
					select.put("NAME", "t_base_slot");
					select.put("ID_NAME", "BASE_NE_ID");
					select.put("ID_VALUE", list_ne.get(0).get("BASE_NE_ID"));
					select.put("ID_NAME_2", "NAME");
					select.put("ID_VALUE_2", targetName);

					List<Map> list_slot = dataCollectMapper
							.getByParameter(select);
					if (list_slot != null && list_slot.size() > 0) {
						slotMap=list_slot.get(0);
					}
				} else if (targetName.contains(DataCollectDefine.COMMON.SHELF)) {
					// 组织查询语句
					select = new HashMap();
					select.put("NAME", "t_base_shelf");
					select.put("ID_NAME", "BASE_NE_ID");
					select.put("ID_VALUE", list_ne.get(0).get("BASE_NE_ID"));
					select.put("ID_NAME_2", "NAME");
					select.put("ID_VALUE_2", targetName);

					List<Map> list_shelf = dataCollectMapper
							.getByParameter(select);
					if (list_shelf != null && list_shelf.size() > 0) {
						shelfMap=list_shelf.get(0);
					}
				} else if (targetName.contains(DataCollectDefine.COMMON.RACK)) {
					// 组织查询语句
					select = new HashMap();
					select.put("NAME", "t_base_shelf");
					select.put("ID_NAME", "BASE_NE_ID");
					select.put("ID_VALUE", list_ne.get(0).get("BASE_NE_ID"));
					select.put("ID_NAME_2", "NAME");
					select.put("ID_VALUE_2", targetName);

					List<Map> list_rack = dataCollectMapper
							.getByParameter(select);
					if (list_rack != null && list_rack.size() > 0) {
						rackMap=list_rack.get(0);
					}
				}
			}
			// link告警
			else if (objectType == DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_TOPOLOGICAL_LINK) {
				String targetName = nameUtil.decompositionName(objectName);
				// 组织查询语句
				select = new HashMap();
				select.put("NAME", "t_base_link");
				select.put("ID_NAME", "NAME");
				select.put("ID_VALUE", targetName);
				select.put("ID_NAME_2", "BASE_EMS_CONNECTION_ID");
				select.put("ID_VALUE_2", emsConnectionId);
	
				List<Map> list_link = dataCollectMapper
						.getByParameter(select);
				if (list_link != null && list_link.size() > 0) {
					target.put("BASE_LINK_ID", list_link.get(0).get("BASE_LINK_ID"));
				}
			}
			// 保护组告警
			else if (objectType == DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_PROTECTION_GROUP) {
				String targetName = nameUtil.decompositionName(objectName);
				// 组织查询语句
				select = new HashMap();
				select.put("NAME", "T_BASE_PRO_GROUP");
				select.put("ID_NAME", "NAME");
				select.put("ID_VALUE", targetName);
				select.put("ID_NAME_2", "BASE_EMS_CONNECTION_ID");
				select.put("ID_VALUE_2", emsConnectionId);
	
				List<Map> list_pro_group = dataCollectMapper
						.getByParameter(select);
				if (list_pro_group != null && list_pro_group.size() > 0) {
					target.put("BASE_PRO_GROUP_ID", list_pro_group.get(0).get("BASE_PRO_GROUP_ID"));
				}
				
			}
			// AID告警
			else if (objectType == DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_AID) {
				String aidValue = objectName[objectName.length-1].value;
				// 匹配数字格式 1/1/1/201
				if (aidValue.matches("\\d+/\\d+/\\d+/\\d+")) {
					String[] value = aidValue.split("/");
					// 获取单元信息
					unitMap = dataCollectMapper.getUnitByManyParam(emsConnectionId,
							target.get("NE_ID") != null ? Integer.valueOf(target.get("NE_ID").toString()) : null,
							Integer.valueOf(value[0]), Integer.valueOf(value[1]), Integer.valueOf(value[2]));
					target.put("PORT_NO", value[3]);
				}
			}
			// 网元告警
			else if (objectType == DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_MANAGED_ELEMENT) {
				// 网元信息，在方法前段作为公用方法已经添加
			}
			// 剩下的作为网管告警
			else {
				// 网管信息，在方法前段作为公用方法已经添加
			}
			if(unitMap==null&&ptpMap!=null){
				// 查询板卡附加信息
				select = new HashMap();
				select.put("NAME", "t_base_unit");
				select.put("ID_NAME", "BASE_UNIT_ID");
				select.put("ID_VALUE", ptpMap.get("BASE_UNIT_ID"));

				List<Map> list_unit = dataCollectMapper
						.getByParameter(select);
				if (list_unit != null && list_unit.size() > 0) {
					unitMap=list_unit.get(0);
				}
			}
			if(slotMap==null&&unitMap!=null){
				// 组织查询语句 slot 信息
				select = new HashMap();
				select.put("NAME", "t_base_slot");
				select.put("ID_NAME", "BASE_SLOT_ID");
				select.put("ID_VALUE", unitMap.get("BASE_SLOT_ID"));

				List<Map> list_slot = dataCollectMapper.getByParameter(select);
				if (list_slot != null && list_slot.size() > 0) {
					slotMap=list_slot.get(0);
				}
			}
			if(shelfMap==null&&slotMap!=null){
				// 组织查询语句 shelf 信息
				select = new HashMap();
				select.put("NAME", "t_base_shelf");
				select.put("ID_NAME", "BASE_SHELF_ID");
				select.put("ID_VALUE", slotMap.get("BASE_SHELF_ID"));

				List<Map> list_shelf = dataCollectMapper
						.getByParameter(select);
				if (list_shelf != null && list_shelf.size() > 0) {
					shelfMap=list_shelf.get(0);
				}
			}
			if(rackMap==null&&shelfMap!=null){
				// 组织查询语句
				select = new HashMap();
				select.put("NAME", "t_base_rack");
				select.put("ID_NAME", "BASE_RACK_ID");
				select.put("ID_VALUE", shelfMap.get("BASE_RACK_ID"));

				List<Map> list_rack = dataCollectMapper
						.getByParameter(select);
				if (list_rack != null && list_rack.size() > 0) {
					rackMap=list_rack.get(0);
				}
			}
			if(ctpMap!=null){
				// ctp相关的附加信息
				/** 原名规范名 */
				target.put("CTP_NATIVE_EMS_NAME", ctpMap.get("NATIVE_EMS_NAME"));
				target.put("CTP_USER_LABEL", ctpMap.get("USER_LABEL"));
				/** 原名规范名 */
				target.put("CTP_NAME", ctpMap.get(
						"DISPLAY_NAME"));
				if(list_ne.get(0).get("TYPE").toString().equals(DataCollectDefine.COMMON.NE_TYPE_SDH_FLAG+"")){
					target.put("CTP_ID", ctpMap.get(
							"BASE_SDH_CTP_ID"));
					target.put("BASE_SDH_CTP_ID", ctpMap.get(
							"BASE_SDH_CTP_ID"));
					target.put("CTP_TYPE", "SDH");
				}else{
					target.put("CTP_ID", ctpMap.get(
							"BASE_OTN_CTP_ID"));
					target.put("BASE_SDH_CTP_ID", ctpMap.get(
							"BASE_OTN_CTP_ID"));
					target.put("CTP_TYPE", "OTN");
				}
			}
			if(ptpMap!=null){
				// 端口相关的附加信息
				/** 原名规范名 */
				target.put("PTP_NATIVE_EMS_NAME", ptpMap.get("NATIVE_EMS_NAME"));
				target.put("PTP_USER_LABEL", ptpMap.get("USER_LABEL"));
				/** 原名规范名 */
				target.put("RACK_ID", ptpMap.get("BASE_RACK_ID"));
				target.put("SHELF_ID", ptpMap.get("BASE_SHELF_ID"));
				target.put("SLOT_ID", ptpMap.get("BASE_SLOT_ID"));
				target.put("BASE_SUB_SLOT_ID", ptpMap.get("BASE_SUB_SLOT_ID"));
				target.put("UNIT_ID", ptpMap.get("BASE_UNIT_ID"));
				target.put("BASE_SUB_UNIT_ID", ptpMap.get("BASE_SUB_UNIT_ID"));
				target.put("PTP_ID", ptpMap.get("BASE_PTP_ID"));
				target.put("RACK_NO", ptpMap.get("RACK_NO"));
				target.put("SHELF_NO", ptpMap.get("SHELF_NO"));
				target.put("SLOT_NO", ptpMap.get("SLOT_NO"));
				target.put("DOMAIN", ptpMap.get("DOMAIN"));
				target.put("PORT_NO", ptpMap.get("PORT_NO"));
				target.put("PTP_TYPE", ptpMap.get("PTP_TYPE"));
				target.put("INTERFACE_RATE", ptpMap.get("RATE"));
				target.put("PORT_NAME", ptpMap.get("DISPLAY_NAME"));
			}
			if(unitMap!=null){
				// 板卡相关的附加信息
				/** 原名规范名 */
				target.put("UNIT_NATIVE_EMS_NAME", unitMap.get("NATIVE_EMS_NAME"));
				target.put("UNIT_USER_LABEL", unitMap.get("USER_LABEL"));
				/** 原名规范名 */
				target.put("RACK_ID", ptpMap.get("BASE_RACK_ID"));
				target.put("SHELF_ID", ptpMap.get("BASE_SHELF_ID"));
				target.put("SLOT_ID", ptpMap.get("BASE_SLOT_ID"));
				target.put("UNIT_ID", unitMap.get("BASE_UNIT_ID"));
				target.put("UNIT_NAME", unitMap.get("DISPLAY_NAME"));
			}
			if(slotMap!=null){
				// 槽道的附加信息
				/** 原名规范名 */
				target.put("SLOT_NATIVE_EMS_NAME", slotMap.get("NATIVE_EMS_NAME"));
				target.put("SLOT_USER_LABEL", slotMap.get("USER_LABEL"));
				/** 原名规范名 */
				target.put("RACK_ID", ptpMap.get("BASE_RACK_ID"));
				target.put("SHELF_ID", ptpMap.get("BASE_SHELF_ID"));
				target.put("SLOT_ID", ptpMap.get("BASE_SLOT_ID"));
				target.put("RACK_NO", slotMap.get("RACK_NO"));
				target.put("SHELF_NO", slotMap.get("SHELF_NO"));
				target.put("SLOT_NO", slotMap.get("SLOT_NO"));
				target.put("SLOT_DISPLAY_NAME", slotMap.get("DISPLAY_NAME"));
			}
			if(shelfMap!=null){
				// 子架的附加信息
				/** 原名规范名 */
				target.put("SHELF_NATIVE_EMS_NAME", shelfMap.get("NATIVE_EMS_NAME"));
				target.put("SHELF_USER_LABEL", shelfMap.get("USER_LABEL"));
				/** 原名规范名 */
				target.put("RACK_ID", ptpMap.get("BASE_RACK_ID"));
				target.put("SHELF_ID", ptpMap.get("BASE_SHELF_ID"));
				target.put("RACK_NO", shelfMap.get("RACK_NO"));
				target.put("SHELF_NO", shelfMap.get("SHELF_NO"));
				target.put("SHELF_DISPLAY_NAME", shelfMap.get("DISPLAY_NAME"));
			}
			if(rackMap!=null){
				// 机架的附加信息
				/** 原名规范名 */
				target.put("RACK_NATIVE_EMS_NAME", rackMap.get("NATIVE_EMS_NAME"));
				target.put("RACK_USER_LABEL", rackMap.get("USER_LABEL"));
				/** 原名规范名 */
				target.put("RACK_ID", ptpMap.get("BASE_RACK_ID"));
				target.put("RACK_NO", rackMap.get("RACK_NO"));
				target.put("RACK_DISPLAY_NAME", rackMap.get("DISPLAY_NAME"));
			}
			// 网元相关的附加信息
			/** 原名规范名 */
			target.put("NE_NATIVE_EMS_NAME", neMap.get("NATIVE_EMS_NAME"));
			target.put("NE_USER_LABEL", neMap.get("USER_LABEL"));
			/** 原名规范名 */
			target.put("NE_ID", neMap.get("BASE_NE_ID"));
			target.put("NE_NAME", neMap.get("DISPLAY_NAME"));
			target.put("PRODUCT_NAME", neMap.get("PRODUCT_NAME"));
			target.put("NE_TYPE", neMap.get("TYPE"));
		}
		
		// 整合包机人id和名称,遍历map
		for (Object key : inspect_map.keySet()) {
			inspect_engineer_id += key.toString() + ";";
			inspect_engineer += inspect_map.get(key) + ";";
		}
		// 给包机人信息赋值,并将最后一个逗号去掉
		if (inspect_engineer_id.length() > 0) {
			target.put("INSPECT_ENGINEER_ID", inspect_engineer_id.substring(0,
					inspect_engineer_id.length() - 1));
			target.put("INSPECT_ENGINEER", inspect_engineer.substring(0,
					inspect_engineer.length() - 1));
		}
//		CommonUtil.timeDif("告警附加信息查询：", t1, new Date());
		return target;
	}
	

	@Override
	@IMethodLog(desc = "DataCollectService：获取网元告警列表")
	public void syncAllActiveAlarms(Map paramter,int neId,
			int[] perceivedSeverity, int commandLevel) throws CommonException {

		Map ne = dataCollectMapper.selectTableById("T_BASE_NE", "BASE_NE_ID",
				neId);

		List<AlarmDataModel> alarms = new ArrayList<AlarmDataModel>();
		// 获取网管告警列表
		List<AlarmDataModel> alarmList = (List<AlarmDataModel>) getDataFromEms(
				this.GET_ALL_ACTIVE_ALARMS, ne, null,
				paramter,commandLevel);

		//插入厂家及emsId信息
		 for (AlarmDataModel model : alarmList) {
			 model.setEmsId(getEmsConnectionId(paramter));
			 model.setFactory(getFactory(paramter));
		 }
		// 告警模块入库
		faultManagerService.alarmDataToMongodb(alarmList, getEmsConnectionId(paramter),
				neId, DataCollectDefine.ALARM_TO_DB_TYPE_SYNCH);
	}

	
	@Override
	@IMethodLog(desc = "DataCollectService：同步网元vb信息")
	public void syncNeVBsImpl(Map paramter, int neId, int commandLevel)
			throws CommonException {

		// 只同步中兴
		if(DataCollectDefine.NMS_TYPE_E300_FLAG != getType(paramter) && 
				DataCollectDefine.NMS_TYPE_U31_FLAG != getType(paramter)){
			return;
		}
		
		// 获取网元
		Map ne = dataCollectMapper.selectTableById("T_BASE_NE", "BASE_NE_ID",
				neId);

		Map vb = null;

		List<VirtualBridgeModel> vbListInEms = (List<VirtualBridgeModel>) getDataFromEms(
				this.GET_ALL_VBS, ne, null, paramter, commandLevel);

		// 获取数据库中vb信息
		List<Map> vbListInDB = dataCollectMapper.selectDataListByNeId(
				"T_BASE_VB", neId, null);

		// 循环EMS网元获取的vb列表
		for (VirtualBridgeModel vbInEms : vbListInEms) {
			// 是否存在DB中标志位
			boolean isExistInDB = false;
			// 循环DB中vb列表
			for (Map vbInDB : vbListInDB) {
				// 更新vb
				if (vbInDB.get("NAME").toString()
						.equals(vbInEms.getNameString())) {
					// 组织vb表数据
					vb = vbModelToTable(vbInEms, getEmsConnectionId(paramter),
							neId, true);

					int vbId = (Integer) vbInDB.get("BASE_VB_ID");
					// 加入BASE_VB_ID
					vb.put("BASE_VB_ID", vbId);
					// 更新vb数据
					dataCollectMapper.updateVBById(vb);
					
					// 删除ptp列表
					dataCollectMapper
							.deletePtpByVbId(vbId);
					
					// 插入ptp列表
					insertPtpListForVB(vbId,neId,vbInEms.getLogicalTPList());

					// 设置存在DB标志位
					isExistInDB = true;
					// 在vb列表中移除
					vbListInDB.remove(vbInDB);
					break;
				}
			}
			// 新增vb
			if (!isExistInDB) {
				vb = vbModelToTable(vbInEms, getEmsConnectionId(paramter),
						neId, false);
				dataCollectMapper.insertVB(vb);
				int vbId = Integer.valueOf(vb.get("BASE_VB_ID").toString());

				// 插入ptp列表
				insertPtpListForVB(vbId,neId,vbInEms.getLogicalTPList());
			}
		}
		// 在DB中存在的vb 但实际网管上已经没有的vb设为标记删除 IS_DEL = 1
		for (Map vbInDB : vbListInDB) {
			vbInDB.put("IS_DEL", DataCollectDefine.TRUE);
			dataCollectMapper.updateVBById(vbInDB);
		}
	}
	

	@Override
	public void insertTCAData(TCADataModel model){
		//查询是否含有此数据
		Map tcaData = dataCollectMapper.selectTableByColumn("T_ALARM_TCA", "FILTER_FOR_CLEAR", model.getFilterForClear());
		
		if(tcaData ==null){
			//插入新数据
			tcaData = tcaDataModelToTable(model,false);
			if(tcaData != null){
				dataCollectMapper.insertTcaData(tcaData);
			}
		}else{
			Integer id = Integer.valueOf(tcaData.get("ID").toString());
			//更新
			tcaData = tcaDataModelToTable(model,true);
			if(tcaData != null){
				tcaData.put("ID", id);
				dataCollectMapper.updateTcaDataByKey(tcaData);
			}
		}
	}
	
	@Override
	public void insertProtectionSwitchData(ProtectionSwtichDataModel model){

		//数据转换
		Map protectionSwitchData = protectionSwitchDataModelToTable(model);
		
		if(protectionSwitchData !=null && protectionSwitchData.get("PROTECTED_TP")!=null){
			dataCollectMapper.insertProtectionSwitchData(protectionSwitchData);
		}
	}
	
	//数据转换
	private Map tcaDataModelToTable(TCADataModel model, boolean isUpdate) {

		Map map = new HashMap();

		if (isUpdate) {
			//更新越限值
			map.put("PM_VALUE", model.getValue());
			//等级
			map.put("PERCEIVED_SEVERITY", model.getPerceivedSeverity());
			//清除时间
			if(model.getPerceivedSeverity() == DataCollectDefine.ALARM_PS_CLEARED){
				if(model.getClearTime() == null || model.getClearTime().isEmpty()){
					map.put("CLEAR_TIME", new Date());
				}else{
					map.put("CLEAR_TIME", model.getClearTime());
				}
			}
		} else {
			// 新增信息
			//获取附加信息
			map = getTargetAdditionInfo(model.getEmsConnectionId(),model.getObjectType(),model.getObjectName());
			
			map.put("BASE_EMS_CONNECTION_ID", model.getEmsConnectionId());
			
			map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
			//设置目标类型
			if(model.getTargetType() == DataCollectDefine.COMMON.TARGET_TYPE_CTP_FLAG){
				if(map.get("BASE_SDH_CTP_ID")!=null){
					map.put("TARGET_TYPE", DataCollectDefine.COMMON.TARGET_TYPE_SDH_CTP_FLAG);
				}else{
					map.put("TARGET_TYPE", DataCollectDefine.COMMON.TARGET_TYPE_OTN_CTP_FLAG);
				}
			}else{
				map.put("TARGET_TYPE", model.getTargetType());
			}
			map.put("LAYER_RATE", model.getLayerRate());
			map.put("PERCEIVED_SEVERITY", model.getPerceivedSeverity());
			map.put("PM_INDEX", model.getPmParameterName());
			map.put("PM_VALUE", model.getValue());
			map.put("LOCATION", model.getLocationFlag());
			map.put("UNIT", model.getUnit());
			map.put("GRANULARITY", model.getGranularityFlag());
			map.put("THRESHOLD_TYPE", model.getThresholdType());
			map.put("CLEAR_STATUS", model.getClearStatus());
			map.put("IS_CLEARABLE", model.isClearable()?DataCollectDefine.TRUE:DataCollectDefine.FALSE);
			if(model.getEmsTime()!=null){
			map.put("EMS_TIME", model.getEmsTime().isEmpty()?null:model.getEmsTime());
			}else{
				map.put("EMS_TIME", null);
			}
			if(model.getNeTime()!=null){
			map.put("ARISES_TIME", model.getNeTime().isEmpty()?null:model.getNeTime());
			}else{
				map.put("ARISES_TIME", null);
			}
			map.put("SAVE_TIME", new Date());
			if(model.getClearTime()!=null){
			map.put("CLEAR_TIME", model.getClearTime().isEmpty()?null:model.getClearTime());
			}else{
				map.put("CLEAR_TIME", null);
			}
			
			//无用字段
			map.put("BASE_SUB_SLOT_ID", null);
			map.put("BASE_SUB_UNIT_ID", null);
			//初始化性能标准表
			initPmStdParameterTbl();
			// 以厂家，EMS类型，PTP类型，层速率，原始PM参数名为关键字查询PM参数归一化表格
			// 区分中兴设备每信道的光功率波长用
			String ptpType = "";
			if (model.getObjectNameFullString().contains("OPM")) {
				ptpType = "OPM";
			}
			String key = generatePmStdKey(String.valueOf(model.getFactory()), 
					String.valueOf(model.getEmsType()), 
					ptpType,
					String.valueOf(model.getLayerRate()),
					model.getPmParameterName());
			
			Map pmStdParameterTblItem = (Map) pmStdParameterTbl.get(key);
			//未整理的性能 丢弃
			if(pmStdParameterTblItem == null){
				//记录文档
				// 定义文件路径
				String filePath = FileWriterUtil.BASE_FILE_PATH
						 + "TCARecord.txt";
				try {
					FileWriterUtil.writeToTxtTCA(filePath, model);
				} catch (CommonException e) {
					e.printStackTrace();
				}
				return null;
			}else{
			map.put("PM_STD_INDEX", pmStdParameterTblItem.get("PM_STD_INDEX"));
			map.put("TYPE", pmStdParameterTblItem.get("TYPE"));
			map.put("PM_DESCRIPTION", pmStdParameterTblItem.get("PM_DESCRIPTION"));
			}
			//唯一性标识
			map.put("FILTER_FOR_CLEAR", model.getFilterForClear());
		}
		return map;
	}

	
	//保护倒换数据转换
	private Map protectionSwitchDataModelToTable(ProtectionSwtichDataModel model) {

		Map map = new HashMap();

		Map ne = dataCollectMapper.selectNeByNeName(model.getEmsConnectionId(), model.getNeSerialNo());
		
		if(ne == null){
			return null;
		}
		
		int neId = Integer.valueOf(ne.get("BASE_NE_ID").toString());
		// 获取网元类型
		int neType = ne.get("TYPE") != null ? Integer.valueOf(ne
				.get("TYPE").toString()) : DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG;
		//获取目标信息
		Map target = null;
		
		//PROTECTED_TP
		target = getTargetInfo(model.getEmsConnectionId(),neId,neType,model.getNeSerialNo(),model.getProtectedTP(),model.getTargetType());
		if(target!=null){
			map.put("PROTECTED_TP", target.get("TARGET_ID"));
			map.put("TARGET_TYPE", target.get("TARGET_TYPE"));
			map.put("PROTECTED_TP_DESC", target.get("DISPLAY_NAME"));
		}
		//SWITCH_AWAY_FROM_TP
		target = getTargetInfo(model.getEmsConnectionId(),neId,neType,model.getNeSerialNo(),model.getSwitchAwayFromTP(),model.getTargetType());
		if(target!=null){
			map.put("SWITCH_AWAY_FROM_TP", target.get("TARGET_ID"));
			map.put("TARGET_TYPE", target.get("TARGET_TYPE"));
			map.put("SWITCH_AWAY_FROM_TP_DESC", target.get("DISPLAY_NAME"));
		}
		//SWITCH_TO_TP
		target = getTargetInfo(model.getEmsConnectionId(),neId,neType,model.getNeSerialNo(),model.getSwitchToTP(),model.getTargetType());
		if(target!=null){
			map.put("SWITCH_TO_TP", target.get("TARGET_ID"));
			map.put("TARGET_TYPE", target.get("TARGET_TYPE"));
			map.put("SWITCH_TO_TP_DESC", target.get("DISPLAY_NAME"));
		}
	
		map.put("BASE_EMS_CONNECTION_ID", model.getEmsConnectionId());
		map.put("BASE_NE_ID", neId);
		map.put("NOTIFICATION_ID", model.getNotificationId());
		map.put("EMS_TIME", model.getEmsTime().isEmpty()?null:model.getEmsTime());
		map.put("NE_TIME", model.getNeTime().isEmpty()?null:model.getNeTime());
		map.put("PROTECT_TYPE", model.getProtectType());
		map.put("PROTECT_TYPE_ORI", model.getProtectTypeOri());
		map.put("PROTECT_CATEGORY", model.getProtectCategory());
		map.put("SWITCH_RESON", model.getSwtichReason());
		map.put("LAYER_RATE", model.getLayerRate());
		map.put("GROUP_NAME", model.getGroupName());
		map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
		map.put("CREATE_TIME", new Date());
		return map;
	}
	
	//获取目标信息
	private Map getTargetInfo(int emsConnectionId,int neId,int neType,String neSerialNo,NameAndStringValue_T[] targetName,int targetType){
		
		Map target = null;
		
		if(targetName.length == 0){
			return null;
		}
		
		switch(targetType){
		case DataCollectDefine.COMMON.TARGET_TYPE_CTP_FLAG:
			// ctp
			Map ctp = null;
			// ptp名
			String ptpName = null;
			// ctp名
			String ctpName = null;
			// ptp名称
			ptpName = nameUtil.decompositionName(nameUtil
					.getPtpNameFromCtpName(targetName));
			// ctp名称
			ctpName = nameUtil.decompositionCtpName(targetName);
			//
			if(DataCollectDefine.COMMON.NE_TYPE_SDH_FLAG == neType){
				// ctp
				ctp = dataCollectMapper.selectSdhCtp(emsConnectionId, neId,
						ptpName, ctpName);

				if(ctp!=null){
					target = new HashMap();
					target.put("TARGET_ID",ctp.get("BASE_SDH_CTP_ID"));
					target.put("TARGET_TYPE",DataCollectDefine.COMMON.TARGET_TYPE_SDH_CTP_FLAG);
					target.put("DISPLAY_NAME",ctp.get("DISPLAY_NAME"));
				}
			}else{
				// ctp
				ctp = dataCollectMapper.selectOtnCtp(emsConnectionId, neId,
						ptpName, ctpName);
				
				//only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
				if(ctp == null && ptpName.contains("ptptype")){
					ctp = selectCtpForE300(emsConnectionId,
							neId, ptpName, ctpName);
				}
				
				if(ctp!=null){
					target = new HashMap();
					target.put("TARGET_ID",ctp.get("BASE_OTN_CTP_ID"));
					target.put("TARGET_TYPE",DataCollectDefine.COMMON.TARGET_TYPE_OTN_CTP_FLAG);
					target.put("DISPLAY_NAME",ctp.get("DISPLAY_NAME"));
				}
			}
			break;
		case DataCollectDefine.COMMON.TARGET_TYPE_PTP_FLAG:
			// ptp名称
			String ptpNameString = nameUtil.decompositionName(targetName);
			
			Map ptp = dataCollectMapper.selectPtpByNeSerialNoAndPtpName(emsConnectionId, neSerialNo, ptpNameString);
			
			//only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
			if(ptp == null && ptpNameString.contains("ptptype")){
				ptp = selectPtpForE300(neId,ptpNameString);
			}
			//华为U2000 wdm保护倒换事件存在 事件中是ptp，实际基础数据中是ftp情况，需要再查找一遍
			if(ptp == null){
				if(targetName[2].name.equals(DataCollectDefine.COMMON.PTP)){
					targetName[2].name = DataCollectDefine.COMMON.FTP;
				}else if(targetName[2].name.equals(DataCollectDefine.COMMON.FTP)){
					targetName[2].name = DataCollectDefine.COMMON.PTP;
				}
				//更换名称
				ptpNameString = nameUtil.decompositionName(targetName);
				ptp = dataCollectMapper.selectPtpByNeSerialNoAndPtpName(emsConnectionId, neSerialNo, ptpNameString);
			}
			if(ptp!=null){
				target = new HashMap();
				target.put("TARGET_ID",ptp.get("BASE_PTP_ID"));
				target.put("TARGET_TYPE",DataCollectDefine.COMMON.TARGET_TYPE_PTP_FLAG);
				target.put("DISPLAY_NAME",ptp.get("PORT_DESC"));
			}
			break;
		case DataCollectDefine.COMMON.TARGET_TYPE_EQUIPMENT_FLAG:
			
			String unitName = nameUtil.decompositionName(targetName);
			Map unit = dataCollectMapper.selectUnitByNeIdAndName(neId, unitName);
			if(unit!=null){
				target = new HashMap();
				target.put("TARGET_ID",unit.get("BASE_UNIT_ID"));
				target.put("TARGET_TYPE",DataCollectDefine.COMMON.TARGET_TYPE_EQUIPMENT_FLAG);
				target.put("DISPLAY_NAME",unit.get("UNIT_DESC"));
			}
			break;
		}
		return target;
		
	}

	@Override
	@IMethodLog(desc = "DataCollectService：更新状态")
	public void updateState(StateDataModel model) throws CommonException {
		if(DataCollectDefine.COMMON.ALARM_OBJECT_TYPE_MANAGED_ELEMENT==
				model.getObjectType()){
			String neSerialNo = nameUtil.getNeSerialNo(model.getObjectName());
			// 组织查询语句
			Map select = new HashMap();
			select.put("NAME", "t_base_ne");
			select.put("ID_NAME", "BASE_EMS_CONNECTION_ID");
			select.put("ID_VALUE", model.getEmsId());
			select.put("ID_NAME_2", "NAME");
			select.put("ID_VALUE_2", neSerialNo);
			List<Map> list_ne = dataCollectMapper.getByParameter(select);
			if (list_ne != null && list_ne.size() > 0) {
				for(Map neInDB:list_ne){
					StateDataModel.State[] states = model.getState();
					for(StateDataModel.State state:states){
						if("communicationState".equals(state.getName())){
							neInDB.put("COMMUNICATION_STATE", state.getValue());
							dataCollectMapper.updateNeById(neInDB);
						}
					}
				}
			}
		}
	}
	
	/**
	 * 获取corba实现类
	 * 
	 * @param type
	 * @return
	 * @throws CommonException
	 */
	private IEMSCollect getInstance(Map paramter) throws CommonException {
		IEMSCollect service = null;
		
		switch (getType(paramter)) {
		case DataCollectDefine.NMS_TYPE_T2000_FLAG:
		case DataCollectDefine.NMS_TYPE_U2000_FLAG:
			service = (IEMSCollect) BeanUtil.getBean("hwDataCollectService");
			break;
		case DataCollectDefine.NMS_TYPE_E300_FLAG:
		case DataCollectDefine.NMS_TYPE_U31_FLAG:
			service = (IEMSCollect) BeanUtil
					.getBean("zteU31DataCollectService");
			break;
		case DataCollectDefine.NMS_TYPE_LUCENT_OMS_FLAG:
			service = (IEMSCollect) BeanUtil
					.getBean("lucentDataCollectService");
			break;
		case DataCollectDefine.NMS_TYPE_OTNM2000_FLAG:
			service = (IEMSCollect) BeanUtil.getBean("fimDataCollectService");
			break;
		case DataCollectDefine.NMS_TYPE_ALU_FLAG:
			service = (IEMSCollect) BeanUtil.getBean("aluDataCollectService");
			break;
		case DataCollectDefine.NMS_TYPE_FUJITSU_FLAG:
			service = (IEMSCollect) BeanUtil
					.getBean("fujitsuDataCollectService");
			break;
		default:
			// service = (IEMSCollect) BeanUtil.getBean("serviceManager");
		}
		service.initParameter(
				(String) paramter.get("USER_NAME"),
				(String) paramter.get("PASSWORD"),
				(String) paramter.get("IP"),
				(String) paramter.get("PORT"),
				(String) paramter.get("EMS_NAME"), 
				(String) paramter.get("INTERNAL_EMS_NAME"), 
				(String) paramter.get("ENCODE"),
				(Integer) paramter.get("ITERATOR_NUM"));
		return service;
	}

	/**
	 * @param neId
	 * @param addForEquip
	 * @param addForPtp
	 * @param ptpDomain
	 */
	private void addForeignKeyForEquip(int neId, boolean addForEquip,
			boolean addForPtp, Integer[] ptpDomain) {
		Map equip;
		Map equipNo = new HashMap();
		String rackNo;
		String shelfNo;
		String slotNo;
		String subSlotNo;

		if (addForEquip) {
			// ------------------ 在shelf表中添加外键信息
			// --------------------------------
			// 获取数据库中shelf信息
			List<Map> shelfListInDB = dataCollectMapper.selectDataListByNeId(
					"T_BASE_SHELF", neId, DataCollectDefine.FALSE);

			// 循环DB中shelf列表
			for (Map shelfInDB : shelfListInDB) {
				equipNo.clear();
				rackNo = shelfInDB.get("RACK_NO").toString();
				equipNo.put("rackNo", rackNo);
				equip = dataCollectMapper.selectEquipByEquipNo("T_BASE_RACK",
						neId, equipNo, DataCollectDefine.FALSE);
				if (equip == null) {
					ExceptionHandler.handleException(new CommonException(
							new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999,
							"【error】shelf上层设备无法找到，网元Id："+neId+"，shelf："+shelfInDB.get("NAME")));
					// 标记删除
					shelfInDB.put("IS_DEL", DataCollectDefine.TRUE);
					// 更新shelf数据
					dataCollectMapper.updateShelfById(shelfInDB);
					continue;
				}
				// 加入rackId
				shelfInDB.put("BASE_RACK_ID", equip.get("BASE_RACK_ID"));
				// 更新shelf数据
				dataCollectMapper.updateShelfById(shelfInDB);
			}

			// ------------------ 在slot表中添加外键信息 --------------------------------
			// 获取数据库中slot信息
			List<Map> slotListInDB = dataCollectMapper.selectDataListByNeId(
					"T_BASE_SLOT", neId, DataCollectDefine.FALSE);

			// 循环DB中slot列表
			for (Map slotInDB : slotListInDB) {
				equipNo.clear();
				rackNo = slotInDB.get("RACK_NO").toString();
				shelfNo = slotInDB.get("SHELF_NO").toString();
				equipNo.put("rackNo", rackNo);
				equipNo.put("shelfNo", shelfNo);
				equip = dataCollectMapper.selectEquipByEquipNo("T_BASE_SHELF",
						neId, equipNo, DataCollectDefine.FALSE);
				if (equip == null) {
					ExceptionHandler.handleException(new CommonException(
							new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999,
							"【error】slot上层设备无法找到，网元Id："+neId+"，slot："+slotInDB.get("NAME")));
					// 标记删除
					slotInDB.put("IS_DEL", DataCollectDefine.TRUE);
					// 更新slot数据
					dataCollectMapper.updateSlotById(slotInDB);
					continue;
				}
				// 加入rackId
				slotInDB.put("BASE_RACK_ID", equip.get("BASE_RACK_ID"));
				// 加入shelfId
				slotInDB.put("BASE_SHELF_ID", equip.get("BASE_SHELF_ID"));
				// 更新slot数据
				dataCollectMapper.updateSlotById(slotInDB);
			}
			
			
			// ------------------ 在sub slot表中添加外键信息 --------------------------------
			// 获取数据库中slot信息
			List<Map> subSlotListInDB = dataCollectMapper.selectDataListByNeId(
					"T_BASE_SUB_SLOT", neId, DataCollectDefine.FALSE);

			// 循环DB中slot列表
			for (Map subSlotInDB : subSlotListInDB) {
				equipNo.clear();
				rackNo = subSlotInDB.get("RACK_NO").toString();
				shelfNo = subSlotInDB.get("SHELF_NO").toString();
				slotNo = subSlotInDB.get("SLOT_NO").toString();
				equipNo.put("rackNo", rackNo);
				equipNo.put("shelfNo", shelfNo);
				equipNo.put("slotNo", slotNo);
				
				equip = dataCollectMapper.selectEquipByEquipNo("T_BASE_SLOT",
						neId, equipNo, DataCollectDefine.FALSE);
				if (equip == null) {
					ExceptionHandler.handleException(new CommonException(
							new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999,
							"【error】subSlot上层设备无法找到，网元Id："+neId+"，subSlot："+subSlotInDB.get("NAME")));
					// 标记删除
					subSlotInDB.put("IS_DEL", DataCollectDefine.TRUE);
					// 更新slot数据
					dataCollectMapper.updateSubSlotById(subSlotInDB);
					continue;
				}
				// 加入slotId
				subSlotInDB.put("BASE_SLOT_ID", equip.get("BASE_SLOT_ID"));
				// 更新slot数据
				dataCollectMapper.updateSubSlotById(subSlotInDB);
			}

			// ------------------ 在unit表中添加外键信息 --------------------------------
			// 获取数据库中unit信息--IS_DEL标记为0的网元
			List<Map> unitListInDB = dataCollectMapper.selectDataListByNeId(
					"T_BASE_UNIT", neId, DataCollectDefine.FALSE);
			
			String unitDesc;
			// 循环DB中unit列表
			for (Map unitInDB : unitListInDB) {
				equipNo.clear();
				rackNo = unitInDB.get("RACK_NO").toString();
				shelfNo = unitInDB.get("SHELF_NO").toString();
				slotNo = unitInDB.get("SLOT_NO").toString();

				equipNo.put("rackNo", rackNo);
				equipNo.put("shelfNo", shelfNo);
				equipNo.put("slotNo", slotNo);

				equip = dataCollectMapper.selectEquipByEquipNo("T_BASE_SLOT",
						neId, equipNo, DataCollectDefine.FALSE);
				if (equip == null) {
					ExceptionHandler.handleException(new CommonException(
							new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999,
							"【error】unit上层设备无法找到，网元Id："+neId+"，unit："+unitInDB.get("NAME")));
					// 标记删除
					unitInDB.put("IS_DEL", DataCollectDefine.TRUE);
					// 更新slot数据
					dataCollectMapper.updateUnitById(unitInDB);
					continue;
				}
				// 加入rackId
				unitInDB.put("BASE_RACK_ID", equip.get("BASE_RACK_ID"));
				// 加入shelfId
				unitInDB.put("BASE_SHELF_ID", equip.get("BASE_SHELF_ID"));
				// 加入slotId
				unitInDB.put("BASE_SLOT_ID", equip.get("BASE_SLOT_ID"));
				
				//unit显示名称--暂不添加
				//RACK.DISPLAY_NAME-SHELF.DISPLAY_NAME_SLOT.DISPLAYNAME
				unitDesc = dataCollectMapper.selectPortDescByIds(
						Integer.valueOf(equip.get("BASE_RACK_ID").toString()), 
						Integer.valueOf(equip.get("BASE_SHELF_ID").toString()), 
						Integer.valueOf(equip.get("BASE_SLOT_ID").toString()),
						Integer.valueOf(unitInDB.get("BASE_UNIT_ID").toString()));
				
				unitInDB.put("UNIT_DESC", unitDesc);
				// 更新unit数据
				dataCollectMapper.updateUnitById(unitInDB);
			}
			
			// ------------------ 在sub unit表中添加外键信息 --------------------------------
			// 获取数据库中sub unit信息--IS_DEL标记为0的网元
			List<Map> subUnitListInDB = dataCollectMapper.selectDataListByNeId(
					"T_BASE_SUB_UNIT", neId, DataCollectDefine.FALSE);

			// 循环DB中sub unit列表
			for (Map subUnitInDB : subUnitListInDB) {
				equipNo.clear();
				rackNo = subUnitInDB.get("RACK_NO").toString();
				shelfNo = subUnitInDB.get("SHELF_NO").toString();
				slotNo = subUnitInDB.get("SLOT_NO").toString();
				subSlotNo = subUnitInDB.get("SUB_SLOT_NO").toString();

				equipNo.put("rackNo", rackNo);
				equipNo.put("shelfNo", shelfNo);
				equipNo.put("slotNo", slotNo);
				equipNo.put("subSlotNo", subSlotNo);

				equip = dataCollectMapper.selectEquipByEquipNo("T_BASE_SUB_SLOT",
						neId, equipNo, DataCollectDefine.FALSE);
				//如果没有找到上层设备--直接跳过，在log中记录此详细信息，方便查找
				if (equip == null) {
					ExceptionHandler.handleException(new CommonException(
							new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999,
							"【error】subUnit上层设备无法找到，网元Id："+neId+"，subUnit："+subUnitInDB.get("NAME")));
					// 标记删除
					subUnitInDB.put("IS_DEL", DataCollectDefine.TRUE);
					// 更新slot数据
					dataCollectMapper.updateSubUnitById(subUnitInDB);
					continue;
				}
				// 加入sub slot Id
				subUnitInDB.put("BASE_SUB_SLOT_ID", equip.get("BASE_SUB_SLOT_ID"));
				// 更新unit数据
				dataCollectMapper.updateSubUnitById(subUnitInDB);
			}
		}

		if (addForPtp) {
			//多线程并发状态会有冲突，不能使用静态变量
			HashMap<String, Object> syncObjectPool = new HashMap<String, Object>();

			String key;

			String portDesc;
			// ------------------ 在ptp表中添加外键信息 --------------------------------
			// 获取数据库中ptp信息--IS_DEL标记为0,domain=sdh/wdm的ptp
			List<Map> ptpListInDB = dataCollectMapper.selectPtpListByNeId(neId,
					ptpDomain, DataCollectDefine.FALSE);

			// 循环DB中unit列表
			for (Map ptpInDB : ptpListInDB) {

				equipNo.clear();
				rackNo = ptpInDB.get("RACK_NO").toString();
				shelfNo = ptpInDB.get("SHELF_NO").toString();
				slotNo = ptpInDB.get("SLOT_NO").toString();
				subSlotNo = ptpInDB.get("SUB_SLOT_NO") != null ? ptpInDB.get(
						"SUB_SLOT_NO").toString() : null;

				equipNo.put("rackNo", rackNo);
				equipNo.put("shelfNo", shelfNo);
				equipNo.put("slotNo", slotNo);

				key = neId+";"+rackNo + ";" + shelfNo + ";" + slotNo;

				if (syncObjectPool.containsKey(key)) {
					equip = (Map) syncObjectPool.get(key);
				} else {
					equip = dataCollectMapper.selectEquipByEquipNo(
							"T_BASE_UNIT", neId, equipNo,
							DataCollectDefine.FALSE);
					syncObjectPool.put(key, equip);
				}
				//如果没有找到上层设备--直接跳过，在log中记录此详细信息，方便查找
				if (equip == null) {
					ExceptionHandler.handleException(new CommonException(
							new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999,
							"【error】ptp上层设备无法找到，网元Id："+neId+"，ptp："+ptpInDB.get("NAME")));
					// 标记删除
					ptpInDB.put("IS_DEL", DataCollectDefine.TRUE);
					// 更新slot数据
					dataCollectMapper.updatePtpById(ptpInDB);
					continue;
				}
				// 加入rackId
				ptpInDB.put("BASE_RACK_ID", equip.get("BASE_RACK_ID"));
				// 加入shelfId
				ptpInDB.put("BASE_SHELF_ID", equip.get("BASE_SHELF_ID"));
				// 加入slotId
				ptpInDB.put("BASE_SLOT_ID", equip.get("BASE_SLOT_ID"));
				// 加入unitId
				ptpInDB.put("BASE_UNIT_ID", equip.get("BASE_UNIT_ID"));
				
				//添加sfp信息--济南联通需求
				String sfpAllInfo = equip.get("SFP_INFO")!=null?equip.get("SFP_INFO").toString():"";
				if(sfpAllInfo!=null&&!sfpAllInfo.isEmpty()){
					for(String sfpInfo:sfpAllInfo.split(";")){
						String[] info = sfpInfo.split("#");
						if(info.length == 2){
							String portNo = info[0];
							if(portNo.equals(ptpInDB.get("PORT_NO"))){
								//数据样例1200Mb/s-1310nm-LC-10km(0.009mm)
								String value = info[1];
								//去除（）内容
								if(value.contains("(")){
									value=value.substring(0,value.lastIndexOf("("));
								}
								ptpInDB.put("OPT_MODEL", value);
								break;
							}
						}
					}
				}
				
//				Map shelf = dataCollectMapper.selectTableById("T_BASE_SHELF",
//						"BASE_SHELF_ID",
//						Integer.valueOf(equip.get("BASE_SHELF_ID").toString()));
				
//				portDesc = rackNo + "-" + shelf.get("DISPLAY_NAME") + "-" + slotNo + "("
//				+ equip.get("UNIT_NAME") + ")" + "-"
//				+ ptpInDB.get("PORT_NO") + "("
//				+ ptpInDB.get("DISPLAY_NAME")
//				+ ")";

				//端口显示名称
				//RACK.DISPLAY_NAME-SHELF.DISPLAY_NAME_SLOT.DISPLAYNAME(UNIT.DISPLAYNAME)_PTP.DISPLAY_NAME
				portDesc = dataCollectMapper.selectPortDescByIds(
						Integer.valueOf(equip.get("BASE_RACK_ID").toString()), 
						Integer.valueOf(equip.get("BASE_SHELF_ID").toString()), 
						Integer.valueOf(equip.get("BASE_SLOT_ID").toString()),
						Integer.valueOf(equip.get("BASE_UNIT_ID").toString()))
						+ "-"
						+ ptpInDB.get("DISPLAY_NAME");

				ptpInDB.put("PORT_DESC", portDesc);

				if(subSlotNo!=null&&!subSlotNo.isEmpty()){
					equipNo.put("subSlotNo", subSlotNo);
					
					key = neId+";"+rackNo + ";" + shelfNo + ";" + slotNo + ";" + subSlotNo;
	
					if (syncObjectPool.containsKey(key)) {
						equip = (Map) syncObjectPool.get(key);
					} else {
						equip = dataCollectMapper.selectEquipByEquipNo(
								"T_BASE_SUB_UNIT", neId, equipNo,
								DataCollectDefine.FALSE);
						syncObjectPool.put(key, equip);
					}
					//如果没有找到上层设备--直接跳过，在log中记录此详细信息，方便查找
					if (equip == null) {
						ExceptionHandler.handleException(new CommonException(
								new NullPointerException(),MessageCodeDefine.MESSAGE_CODE_999999,
								"【error】ptp上层设备无法找到，网元Id："+neId+"，ptp："+ptpInDB.get("NAME")));
						// 标记删除
						ptpInDB.put("IS_DEL", DataCollectDefine.TRUE);
						// 更新slot数据
						dataCollectMapper.updatePtpById(ptpInDB);
						continue;
					}
					// 加入subSlotId
					ptpInDB.put("BASE_SUB_SLOT_ID", equip.get("BASE_SUB_SLOT_ID"));
					// 加入subUnitId
					ptpInDB.put("BASE_SUB_UNIT_ID", equip.get("BASE_SUB_UNIT_ID"));
				}
				// 更新ptp数据
				dataCollectMapper.updatePtpById(ptpInDB);
			}
		}
	}

	/**
	 * @param collectType
	 * @return
	 * @throws CommonException
	 */
	private Object getDataFromEms(int collectType, Map ne,
			NameAndStringValue_T[] ptpName, Map paramter,int collectLevel)
			throws CommonException {
		//检查ems是否可以采集
		IsEmsCanCollect(paramter,collectLevel);
		//检查ne是否在线
		if(ne!=null){
			IsNeCanCollect(ne,collectType);
		}
		//包装命令
		CommandModel commandModel = new CommandModel(String.valueOf(RandomUtils.nextDouble()),collectType,collectLevel,new Date());
		
		Object data = null;
		
		int emsConnectionId = Integer.valueOf(paramter.get("BASE_EMS_CONNECTION_ID").toString());

		//检查是否最优先级命令
		while(!IsHighestCommand(paramter,commandModel)){
			try {
				System.out.println("sleep...................");
				Thread.sleep(3*1000);
			} catch (InterruptedException e) {
				throw new CommonException(e,
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION); 
			}
		}
		
		//命令间隔时间--
		int intervalTime = Integer.valueOf(paramter.get("INTERVAL_TIME").toString());
		try {
			Thread.sleep(intervalTime*1000);
		} catch (InterruptedException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION); 
		}
		
		try{
			//获取数据
			data = getData(collectType,ne,ptpName,paramter);
		}
		catch(CommonException e){
			throw e;
		}finally{
			//移除命令
			CommandPriorityModel model = commandPriorityMap.get(emsConnectionId);
			model.removeCmd(commandModel);
		}
		
		return data;
	}

	/**
	 * @param collectType
	 * @return
	 * @throws CommonException
	 */
	private Object getData(final int collectType, final Map ne,
			final NameAndStringValue_T[] ptpName, final Map paramter)
			throws CommonException {
		
		//获取网元名
		final String neName = ne!=null?ne.get("NAME").toString():null;
		
		Callable<Object> thread = new Callable<Object>() {
			public Object call() throws CommonException {
				
				IEMSCollect service = getInstance(paramter);
				
				Object data = null;

				short[] layerRateList = null;
				String[] pmLocationList = null;
				String[] granularityList = null;
				List<String> ptpNameList = null;

				switch (collectType) {

				case GET_EMS:
					data = service.getEMS();
					break;
				case GET_ALL_MANAGED_ELEMENT:
					data = service.getAllManagedElements();
					break;
				case GET_ALL_EQUIPMENT:
					data = service.getAllEquipment(neName);
					break;
				case GET_ALL_PTPS:
					data = service.getAllPTPs(neName);
					break;
				case GET_CONTAINED_POTENTIAL_TPS:
					data = service.getContainedPotentialTPs(ptpName);
					break;
				// case GET_ALL_TOP_LEVEL_TOPOLOGICAL_LINKS:
				// data = service.getAllTopLevelTopologicalLinks();
				// break;
				case GET_ALL_MSTP_END_POINTS:
					data = service.getAllMstpEndPoints(neName);
					break;
				case GET_ALL_E_PROTECTION_GROUPS:
					data = service.getAllEProtectionGroups(neName);
					break;
				case GET_ALL_PROTECTION_GROUPS:
					data = service.getAllProtectionGroups(neName);
					break;
				case GET_ALL_WDM_PROTECTION_GROUPS:
					data = service.getAllWDMProtectionGroups(neName);
					break;
				case GET_CLOCK_SOURCE_STATUS:
					data = service.getObjectClockSourceStatus(neName);
					break;
				case GET_ALL_TOPOLOGICAL_LINKS:
					data = service.getAllTopologicalLinks();
					break;
				case GET_ALL_INTERNAL_TOPOLOGICAL_LINKS:
					data = service.getAllInternalTopologicalLinks(neName);
					break;
				case GET_ALL_EMS_AND_ME_ACTIVE_ALARMS:
					data = service.getAllEMSAndMEActiveAlarms();
					break;
				case GET_ALL_ACTIVE_ALARMS:
					data = service.getAllActiveAlarms(neName);
					break;
				case GET_ALL_CURRENT_PM:

					layerRateList = (short[]) paramter.get(PARAM_LAYER_RATE);
					pmLocationList = (String[]) paramter.get(PARAM_PM_LOCATION);
					granularityList = (String[]) paramter
							.get(PARAM_GRANULARITY);

					data = service.getCurrentPmData_Ne(neName, layerRateList,
							pmLocationList, granularityList);
					break;

				case GET_ALL_CURRENT_PM_PTPLIST:

					ptpNameList = (List<String>) paramter
							.get(PARAM_PTP_NAME_LIST);
					layerRateList = (short[]) paramter.get(PARAM_LAYER_RATE);
					pmLocationList = (String[]) paramter.get(PARAM_PM_LOCATION);
					granularityList = (String[]) paramter
							.get(PARAM_GRANULARITY);

					data = service.getCurrentPmData_PtpList(ptpNameList,
							layerRateList, pmLocationList, granularityList);

					break;

				case GET_ALL_HISTORY_PM:

					layerRateList = (short[]) paramter.get(PARAM_LAYER_RATE);
					pmLocationList = (String[]) paramter.get(PARAM_PM_LOCATION);
					granularityList = (String[]) paramter
							.get(PARAM_GRANULARITY);

					String targetDisplayName = (String) paramter
							.get(PARAM_DISPLAY_NAME);

					// 采集开始--结束时间段
					String startTime = (String) paramter.get(PARAM_START_TIME);
					String endTime = (String) paramter.get(PARAM_END_TIME);
					// ftp地址
					String ip = (String) paramter.get(PARAM_FTP_IP);
					
					int port = (Integer) paramter.get(PARAM_FTP_PORT);
						
					String userName = (String) paramter.get(PARAM_FTP_USERNAME);
					String password = (String) paramter.get(PARAM_FTP_PASSWORD);

					boolean needAnalysisPm = paramter.get(PARAM_PTN_SYSTEM_NAME) ==null ?true:false;
					
					data = service.getHistoryPmData_Ne(targetDisplayName,
							neName, startTime, endTime, layerRateList,
							pmLocationList, granularityList, ip, port, userName,
							password,getType(paramter),needAnalysisPm);
					break;

				case GET_ALL_ETH_SERVICE:
					data = service.getAllEthService(neName);
					break;

				case GET_CRS:
					layerRateList = (short[]) paramter.get(PARAM_LAYER_RATE);
					data = service.getCRS(neName, layerRateList);
					break;

				case GET_ALL_BINDING_PATH:
					data = service.getBindingPath(ptpName);
					break;

				case GET_ALL_VBS:
					data = service.getAllVBs(neName);
					break;
					
				case GET_ALL_SNC:
					data = service.getAllSubnetworkConnections();
					break;
					
				case GET_ALL_ROUTE:
					boolean needSort = (Boolean) paramter.get(PARAM_NEED_SORT);
					data = service.getRoute(needSort);
					break;
					
				case GET_ALL_FDFRS:
					data = service.getAllFDFrs();
					break;
					
				case GET_ALL_LINK_OF_FDFRS:
					data = service.getAllLinkOfFDFrs();
					break;

				default:
				}
				return data;
			}
		};

		if (executorService == null) {
			executorService = Executors.newCachedThreadPool();
		}

		// 添加采集进程
		FutureTask<Object> future = new FutureTask<Object>(thread);
		// 执行采集进程
		executorService.submit(future);

		Object data = null;
		int timeOut = Integer.valueOf(paramter.get("TIME_OUT").toString());
		
		//pm collect timeout default 2 times t_base_ems_connection.TIME_OUT
		if(GET_ALL_CURRENT_PM == collectType || GET_ALL_HISTORY_PM ==collectType){
			int times = 2;
			if(CommonUtil.getSystemConfigProperty("pmCollectTimeOutTimes")!=null){
				times = Integer.valueOf(CommonUtil.getSystemConfigProperty("pmCollectTimeOutTimes"));
			}
			timeOut = times*timeOut;
		}
		//alarm collect timeout default 4 times t_base_ems_connection.TIME_OUT
		if(GET_ALL_EMS_AND_ME_ACTIVE_ALARMS == collectType || GET_ALL_ACTIVE_ALARMS == collectType){
			int times = 4;
			if(CommonUtil.getSystemConfigProperty("alarmCollectTimeOutTimes")!=null){
				times = Integer.valueOf(CommonUtil.getSystemConfigProperty("alarmCollectTimeOutTimes"));
			}
			timeOut = times*timeOut;
		}
		try {
			data = future.get(timeOut, TimeUnit.SECONDS);
		}catch (TimeoutException e) {
			throw new CommonException(e,
					MessageCodeDefine.CORBA_TIMEOUT_EXCEPTION);
		}catch(Exception e){
			//记录log信息，此处记录比较详细
			ExceptionHandler.handleException(e);
			if (CommonException.class.isInstance(e.getCause())) {
				throw new CommonException(e,
						((CommonException) e.getCause()).getErrorCode(),
						((CommonException) e.getCause()).getErrorMessage());
			} else {
				e.printStackTrace();
				throw new CommonException(e,
						MessageCodeDefine.CORBA_EXCUTION_EXCEPTION);
			}
		}
		return data;
	}

	/**
	 * @param model
	 * @param emsConnectionId
	 * @param type
	 * @param isUpdate
	 * @return
	 */
	@IMethodLog(desc = "DataCollectService：网元信息表对象构建")
	private Map managedElementModelToTable(ManagedElementModel model,
			int emsConnectionId, int type, int factory, boolean isUpdate) {

		Map map = new HashMap();

		// 获取网元类型
		Map productMapping = dataCollectMapper.selectProductMapping(
				model.getProductName(), factory);

		//扩充产品名称对应表数据
		if(productMapping == null){
			productMapping = new HashMap();
			productMapping.put("BASE_PRODUCT_MAPPING_ID", null);
			productMapping.put("PRODUCT_NAME", model.getProductName());
			productMapping.put("TYPE", DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG);
			productMapping.put("FACTORY", factory);
			dataCollectMapper.insertProductMapping(productMapping);
		}

		// 基础信息
		map.put("NAME", nameUtil.decompositionName(model.getName()));
		map.put("NATIVE_EMS_NAME_ORI", model.getNativeEMSNameOri());
		map.put("NATIVE_EMS_NAME",model.getNativeEMSName());
		map.put("LOCATION", model.getLocation());
		map.put("OWNER", model.getOwner());
		map.put("VERSION", model.getVersion());
		map.put("PRODUCT_NAME", model.getProductName());
		map.put("COMMUNICATION_STATE", model.getCommunicationState());
		map.put("SUPORT_RATES",
				nameUtil.decompositionLayRates(model.getSupportedRates()));
		// 操作状态 中兴
		map.put("OPERATIONAL_STATUS", model.getOperationalStatus());
		// 告警状态 中兴
		map.put("ALARM_STATUS", model.getAlarmStatus());
		// 描述信息 中兴
		map.put("DESCRIPTION_INFO", model.getDescriptionInfo());
		// 网元功能分类 中兴
		map.put("ME_TYPE", model.getMeType());
		// ROUTE_ID
		map.put("PTN_ROUTE_ID", model.getRouteId());
		// 网络地址 中兴U31
		map.put("NET_ADDRESS", model.getNetAddress());
		// 网元类型 1 SDH 2 WDM 3 OTN 4 PTN 99 未知 根据productName查询对应表取得
		map.put("TYPE", productMapping != null ? productMapping.get("TYPE")
				: DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG);
		// 厂家 1.HW 2.ZTE 3.朗讯 4.烽火 5.ALU 9.富士通
		map.put("FACTORY", factory);
		map.put("IS_DEL", DataCollectDefine.FALSE);

		if (isUpdate) {
			// 更新信息
			// 显示模式为自动时需要更新
			if (map.get("DISPLAY_MODE") != null
					&& Integer.valueOf(map.get("DISPLAY_MODE").toString())
							.intValue() == DataCollectDefine.DISPLAY_MODE_AUTO) {
				map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			}
			// map.put("UPDATE_TIME", new Date());
		} else {
			// USER_LABEL用作规范名称--初始化使用原始名称
			map.put("USER_LABEL", map.get("NATIVE_EMS_NAME"));
			map.put("DISPLAY_NAME",map.get("NATIVE_EMS_NAME"));
			// 新增信息
			map.put("BASE_NE_ID", null);
			map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
			map.put("BASE_SUBNET_ID", null);
			// 网元是否已同步--网管侧
			map.put("EMS_IN_SYNC_STATE", DataCollectDefine.FALSE);
			// 机房Id
			map.put("RESOURCE_ROOM_ID", null);
			// telnet连接登录用户名
			map.put("USER_NAME", null);
			// telnet连接登录密码
			map.put("PASSWORD", null);
			// 连接方式 0.自动连接 1.手动连接
			map.put("CONNECTION_MODE", null);
			// 同步状态 1.已同步 2.未同步 3.同步失败
			map.put("BASIC_SYNC_STATUS", DataCollectDefine.SYNC_NOT_FLAG);
			// 网元基础数据同步时间
			map.put("BASIC_SYNC_TIME", null);
			// 网元基础数据同步结果
			map.put("BASIC_SYNC_RESULT", null);
			// 同步状态 1.已同步 2.未同步 3.同步失败
			map.put("MSTP_SYNC_STATUS", DataCollectDefine.SYNC_NOT_FLAG);
			// MSTP类同步时间
			map.put("MSTP_SYNC_TIME", null);
			// MSTP类同步结果
			map.put("MSTP_SYNC_RESULT", null);
			// 同步状态 1.已同步 2.未同步 3.同步失败
			map.put("CRS_SYNC_STATUS", DataCollectDefine.SYNC_NOT_FLAG);
			// 交叉连接类同步时间
			map.put("CRS_SYNC_TIME", null);
			// 交叉连接类同步结果
			map.put("CRS_SYNC_RESULT", null);
			// 网元采集等级 1.重点采集 2.循环采集 3.不采集
			map.put("NE_LEVEL", DataCollectDefine.COLLECT_NE_LV_1_FLAG);
			// 采集结果
			map.put("COLLECT_RESULT", null);
			// 采集间隔时间
			map.put("COLLECT_INTERVAL", null);
			// 最近采集时间
			map.put("LAST_COLLECT_TIME", null);
			// 是否虚拟网元 0：不是 1：是
			map.put("IS_VIRTUAL_NE", DataCollectDefine.FALSE);
			// 是否采集计数值 0：不是 1：是
			map.put("COLLECT_NUMBIC", DataCollectDefine.TRUE);
			// 是否采集物理量 0：不是 1：是
			map.put("COLLECT_PHYSICAL", DataCollectDefine.TRUE);
			// 是否采集通道信息 0：不是 1：是 WDM通道 hw:每信道中心波长（current max min） zte:channelNo
			// SDH通道 CTP性能
			map.put("COLLECT_CTP", DataCollectDefine.FALSE);
			map.put("POSITION_X", null);
			map.put("POSITION_Y", null);
			map.put("CREATE_TIME", new Date());
			// map.put("UPDATE_TIME", null);
		}

		return map;
	}
	
	/**
	 * @param model
	 * @param emsConnectionId
	 * @param type
	 * @param isUpdate
	 * @return
	 */
	@IMethodLog(desc = "DataCollectService：snc信息表对象构建")
	private Map sncModelToTable(SubnetworkConnectionModel model,
			int emsConnectionId, boolean isUpdate) {

		Map map = new HashMap();

		// 基础信息
		map.put("USER_LABEL", model.getUserLabel());
		map.put("NATIVE_EMS_NAME",model.getNativeEMSName());
		map.put("DISPLAY_NAME", model.getNativeEMSName());
		map.put("OWNER", model.getOwner());
		map.put("STATIC_PRO_LV", model.getStaticProtectionLevel());
		map.put("SNC_TYPE", model.getSncType());
		map.put("DIRECTION", model.getDirection());
		map.put("SNC_STATE",model.getSncState());
		map.put("RATE", model.getRate());
		map.put("LSP_TYPE", model.getLSPType());
		map.put("SERVICE_STATE", model.getServiceState());
		map.put("EMS_CREATE_TIME", model.getCreateTime());
		map.put("IS_DEL", DataCollectDefine.FALSE);
		map.put("SUBNETWORK_NAME", model.getSubnetworkName());
		map.put("BELONG_SNC", model.getBelong_snc());
		//a端ptp
		String aEndNeName = nameUtil.getNeSerialNo(model.getaEndTP());
		String aEndPtpName = "";
		String aEndName = "";
		
		//ctp 情况
		if(model.getaEndTP().length == 4){
			aEndPtpName = nameUtil.decompositionName(nameUtil.getPtpNameFromCtpName(model.getaEndTP()));
			aEndName = nameUtil.decompositionCtpName(model.getaEndTP());
			
			Map aEndCtp = dataCollectMapper.selectPtnCtpByNeSerialNoAndCtpName(emsConnectionId, aEndNeName, aEndPtpName,aEndName);
			if(aEndCtp == null){
				return null;
			}
			map.put("A_END_PTP", aEndCtp.get("BASE_PTP_ID"));
			map.put("A_END_CTP", aEndName);
		}else if(model.getaEndTP().length == 3){
			
			aEndName = nameUtil.decompositionName(model.getaEndTP());
			
			Map aEndPtp = dataCollectMapper.selectPtpByNeSerialNoAndPtpName(emsConnectionId, aEndNeName, aEndName);
			if(aEndPtp == null){
				return null;
			}
			map.put("A_END_PTP", aEndPtp.get("BASE_PTP_ID"));
			map.put("A_END_CTP", "");
		}
		
		//z端ptp
		String zEndNeName = nameUtil.getNeSerialNo(model.getzEndTP());
		String zEndPtpName = "";
		String zEndName = "";
		//ctp 情况
		if(model.getzEndTP().length == 4){
			zEndPtpName = nameUtil.decompositionName(nameUtil.getPtpNameFromCtpName(model.getzEndTP()));
			zEndName = nameUtil.decompositionCtpName(model.getzEndTP());
			
			Map zEndCtp = dataCollectMapper.selectPtnCtpByNeSerialNoAndCtpName(emsConnectionId, zEndNeName, zEndPtpName,zEndName);
			if(zEndCtp == null){
				return null;
			}
			map.put("Z_END_PTP", zEndCtp.get("BASE_PTP_ID"));
			map.put("Z_END_CTP", zEndName);
		}else if(model.getzEndTP().length == 3){
			zEndName = nameUtil.decompositionName(model.getzEndTP());
			Map zEndPtp = dataCollectMapper.selectPtpByNeSerialNoAndPtpName(emsConnectionId, zEndNeName, zEndName);
			if(zEndPtp == null){
				return null;
			}
			map.put("Z_END_PTP", zEndPtp.get("BASE_PTP_ID"));
			map.put("Z_END_CTP", "");
		}
		if (isUpdate) {
			// map.put("UPDATE_TIME", new Date());
		} else {
			// 新增信息
			map.put("BASE_PTN_SNC_ID", null);
			map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
			map.put("NAME", model.getSncSerialNo());
			map.put("CREATE_TIME", new Date());
			// map.put("UPDATE_TIME", null);
		}

		return map;
	}
	
	/**
	 * @param model
	 * @param emsConnectionId
	 * @param type
	 * @param isUpdate
	 * @return
	 */
	@IMethodLog(desc = "DataCollectService：route信息表对象构建")
	private Map routeModelToTable(CrossConnectModel model,
			int emsConnectionId, boolean isUpdate) {

		Map map = new HashMap();
		// 基础信息
		map.put("SEQUENCE", model.getSequence());
		map.put("IS_DEL", DataCollectDefine.FALSE);

		if(model.getaEndNameList().length>0){
			NameAndStringValue_T[] aEndCtpName = model.getaEndNameList()[0];
			
			//a端ptp
			String aEndNeName = nameUtil.getNeSerialNo(aEndCtpName);
			String aEndPtpName = nameUtil.decompositionName(nameUtil.getPtpNameFromCtpName(aEndCtpName));
			String aEndName = nameUtil.decompositionCtpName(aEndCtpName);
			//ctp 情况
			Map aEndCtp = dataCollectMapper.selectPtnCtpByNeSerialNoAndCtpName(emsConnectionId, aEndNeName, aEndPtpName,aEndName);
			if(aEndCtp == null){
				//插入数据库表ptn ctp数据
				aEndCtp = insertPtnCtpManual(emsConnectionId, aEndCtpName,model);
				//依然是null
				if(aEndCtp == null){
					return null;
				}else{
					map.put("A_END_PTP", aEndCtp.get("BASE_PTP_ID"));
					map.put("A_END_CTP", aEndName);
				}
			}else{
				map.put("A_END_PTP", aEndCtp.get("BASE_PTP_ID"));
				map.put("A_END_CTP", aEndName);
			}
		}else{
			map.put("A_END_PTP", null);
			map.put("A_END_CTP", null);
		}
		
		if(model.getzEndNameList().length>0){
			NameAndStringValue_T[] zEndCtpName = model.getzEndNameList()[0];
			//z端ptp
			String zEndNeName = nameUtil.getNeSerialNo(zEndCtpName);
			String zEndPtpName = nameUtil.decompositionName(nameUtil.getPtpNameFromCtpName(zEndCtpName));
			String zEndName = nameUtil.decompositionCtpName(zEndCtpName);
			//ctp 情况
			Map zEndCtp = dataCollectMapper.selectPtnCtpByNeSerialNoAndCtpName(emsConnectionId, zEndNeName, zEndPtpName,zEndName);
			if(zEndCtp == null){
				//插入数据库表ptn ctp数据
				zEndCtp = insertPtnCtpManual(emsConnectionId, zEndCtpName,model);
				//依然是null
				if(zEndCtp == null){
					return null;
				}else{
					map.put("Z_END_PTP", zEndCtp.get("BASE_PTP_ID"));
					map.put("Z_END_CTP", zEndName);
				}
			}else{
				map.put("Z_END_PTP", zEndCtp.get("BASE_PTP_ID"));
				map.put("Z_END_CTP", zEndName);
			}
		}else{
			map.put("Z_END_PTP", null);
			map.put("Z_END_CTP", null);
		}
		
		if (isUpdate) {
			// map.put("UPDATE_TIME", new Date());
		} else {
			
			// 新增信息
			map.put("BASE_PTN_ROUTE_ID", null);
			map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
			map.put("NAME", model.getBelongedTrail());
			map.put("CREATE_TIME", new Date());
			// map.put("UPDATE_TIME", null);
		}
		return map;
	}
	
	
	/**
	 * @param model
	 * @param emsConnectionId
	 * @param type
	 * @param isUpdate
	 * @return
	 */
	@IMethodLog(desc = "DataCollectService：fdfr信息表对象构建")
	private Map fdfrModelToTable(FdfrModel model,
			int emsConnectionId, Integer fdfrId,boolean isUpdate) {

		Map map = new HashMap();

		// 基础信息
		map.put("USER_LABEL", model.getUserLabel());
		map.put("NATIVE_EMS_NAME",model.getNativeEMSName());
		map.put("DISPLAY_NAME", model.getNativeEMSName());
		map.put("OWNER", model.getOwner());
		map.put("LAYER_RATE", model.getLayer());
		map.put("DIRECTION", model.getDirection());
		map.put("SERVICE_STATE", model.getServiceState());
		map.put("NETWORK_ACCESS_DOMAIN",model.getNetworkAccessDomain());
		map.put("FDFR_STATE", model.getFdfrState());
		map.put("FDFR_TYPE", model.getFdfrType());
		map.put("SERVICE_TYPE", model.getServiceType());
		map.put("TRAFFIC_TYPE", model.getTrafficType());
		map.put("ADMINISTRATIVE_STATE", model.getAdministrativeState());
		map.put("OAM_ENABLED", model.getOamEnabled());
		map.put("COMMENTS", model.getComments());
		map.put("CUSTOMER", model.getCustomer());
		map.put("IGMP_SNOOPING_STATE", model.getIgmpSnoopingState());
		map.put("FLEXIBLE", model.getFlexible());
		map.put("IS_DEL", DataCollectDefine.FALSE);
		
		map.put("aEnd", tpDataModelToTable(model.getaEnd(),emsConnectionId,fdfrId,"A"));
		map.put("zEnd", tpDataModelToTable(model.getaEnd(),emsConnectionId,fdfrId,"Z"));
		
		if (isUpdate) {
			// map.put("UPDATE_TIME", new Date());
		} else {
			// 新增信息
			map.put("BASE_PTN_FD_ID", fdfrId);
			map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
			map.put("FD_NAME", model.getFdName());
			map.put("NAME", model.getNameString());
			map.put("CREATE_TIME", new Date());
			// map.put("UPDATE_TIME", null);
		}

		return map;
	}
	
	
	/**
	 * @param neName
	 * @param ip
	 * @param model
	 */
	@IMethodLog(desc = "DataCollectService：linkOfFdfr信息表对象构建")
	private Map linkOfFdfrModelToTable(TopologicalLinkModel model,
			int emsConnectionId,boolean isUpdate) {

		Map map = new HashMap();
		
		//a端ptp
		String aEndNeName = nameUtil.getNeSerialNo(model.getaEndTP());
		String aEndPtpName = nameUtil.decompositionName(nameUtil.getPtpNameFromCtpName(model.getaEndTP()));
		String aEndName = nameUtil.decompositionCtpName(model.getaEndTP());
		//ctp 情况
		Map aEndCtp = dataCollectMapper.selectPtnCtpByNeSerialNoAndCtpName(emsConnectionId, aEndNeName, aEndPtpName,aEndName);
		if(aEndCtp == null){
			return null;
		}
		
		//z端ptp
		String zEndNeName = nameUtil.getNeSerialNo(model.getzEndTP());
		String zEndPtpName = nameUtil.decompositionName(nameUtil.getPtpNameFromCtpName(model.getzEndTP()));
		String zEndName = nameUtil.decompositionCtpName(model.getzEndTP());
		//ctp 情况
		Map zEndCtp = dataCollectMapper.selectPtnCtpByNeSerialNoAndCtpName(emsConnectionId, zEndNeName, zEndPtpName,zEndName);
		if(zEndCtp == null){
			return null;
		}
		// 基础信息
		 map.put("USER_LABEL", model.getUserLabel());
		map.put("NATIVE_EMS_NAME",model.getNativeEMSName());
		map.put("DISPLAY_NAME",model.getUserLabel());
		map.put("OWNER", model.getOwner());
		map.put("DIRECTION", model.getDirection());
		map.put("LAYER_RATE", model.getRate());
		
		map.put("UNDERLYING_TYPE", model.getUnderlyingType());
		map.put("PROTOCOL_TYPE", model.getProtocolType());
		map.put("SERVER_OBJ", model.getServerObj());
		
		map.put("A_END_NE", aEndCtp.get("BASE_NE_ID"));
		map.put("A_END_PTP", aEndCtp.get("BASE_PTP_ID"));
		map.put("A_END_CTP", aEndName);
		
		map.put("Z_END_NE", zEndCtp.get("BASE_NE_ID"));
		map.put("Z_END_PTP", zEndCtp.get("BASE_PTP_ID"));
		map.put("Z_END_CTP", zEndName);

		map.put("IS_DEL", DataCollectDefine.FALSE);

		if (isUpdate) {
			// 更新信息
		} else {
			// 新增信息
			map.put("BASE_PTN_FDFR_LINK_ID", null);
			map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
			map.put("FD_NAME", model.getFdName());
			map.put("FDFR_NAME", model.getFdfrName());
			map.put("NAME", model.getNameString());
			map.put("CREATE_TIME", new Date());
			// map.put("UPDATE_TIME", null);
		}
		return map;
	}
	
	/**
	 * @param model
	 * @param emsConnectionId
	 * @param type
	 * @param isUpdate
	 * @return
	 */
	@IMethodLog(desc = "DataCollectService：fdfr_list信息表对象构建")
	private List<Map> tpDataModelToTable(List<TerminationPointModel> model,
			int emsConnectionId, Integer fdfrId, String A_OR_Z) {

		List<Map> dataList = new ArrayList<Map>();
		
		for(TerminationPointModel tpData:model){
			Map map = new HashMap();
			// 基础信息
			map.put("BASE_PTN_FDFR_LIST_ID", null);
			map.put("BASE_PTN_FD_ID",fdfrId);
			map.put("A_OR_Z", A_OR_Z);
			map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
			// 端口
			Map ptp = dataCollectMapper.selectPtpByNeSerialNoAndPtpName(
					emsConnectionId, nameUtil.getNeSerialNo(tpData.getName()),
					nameUtil.decompositionName(tpData.getName()));
			
			if(ptp == null){
				continue;
			}
			map.put("BASE_NE_ID", ptp.get("BASE_NE_ID"));
			map.put("BASE_PTP_ID", ptp.get("BASE_PTP_ID"));
			map.put("PTP_NAME", ptp.get("NAME"));
			if(tpData.getTransmissionParams().size()>0){
				map.put("LAYER_RATE",tpData.getTransmissionParams().get(0).getLayer());
			}else{
				map.put("LAYER_RATE",null);
			}
			map.put("IS_DEL", DataCollectDefine.FALSE);
			map.put("CREATE_TIME", new Date());
			dataList.add(map);
		}
		return dataList;
	}
	
	private Map insertPtnCtpManual(int emsConnectionId, NameAndStringValue_T[] ctpName,CrossConnectModel model){
		Map ptnCtp = null;
		//插入数据库表ptn ctp数据
		String ptpName = nameUtil.decompositionName(nameUtil.getPtpNameFromCtpName(ctpName));
		Map ptp = dataCollectMapper.selectPtpByNeSerialNoAndPtpName(emsConnectionId,nameUtil.getNeSerialNo(ctpName), ptpName);
		
		if(ptp!=null){
			Integer neId = Integer.valueOf(ptp.get("BASE_NE_ID").toString());
			Integer ptpId = Integer.valueOf(ptp.get("BASE_PTP_ID").toString());
			ptnCtp = constructPtnCtpData(emsConnectionId,neId,ptpId,ctpName,model);
		}
		if(ptnCtp!=null){
			//插入ptn ctp表
			dataCollectMapper.insertOrUpdatePtnCtp(ptnCtp);
		}
		return ptnCtp;
	}

	/**
	 * @param model
	 * @param emsConnectionId
	 * @param neId
	 * @param isUpdate
	 * @return
	 */
	@IMethodLog(desc = "DataCollectService：机架信息表对象构建")
	private Map rackModelToTable(EquipmentHolderModel model,
			int emsConnectionId, int neId, int type, boolean isUpdate) {

		Map map = new HashMap();

		// 基础信息
		map.put("NAME", model.getNameString());

		switch (type) {
		case DataCollectDefine.NMS_TYPE_T2000_FLAG:
		case DataCollectDefine.NMS_TYPE_U2000_FLAG:
			map.put("NATIVE_EMS_NAME", model.getRackNo());
			break;
		case DataCollectDefine.NMS_TYPE_E300_FLAG:
		case DataCollectDefine.NMS_TYPE_U31_FLAG:
			/*根据BD要求，DISPLAY_NAME取RACK_NO
			 *map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			*/
			map.put("NATIVE_EMS_NAME", model.getRackNo());
			break;
		case DataCollectDefine.NMS_TYPE_LUCENT_OMS_FLAG:
			map.put("NATIVE_EMS_NAME", "1");
			break;
		case DataCollectDefine.NMS_TYPE_OTNM2000_FLAG:
		case DataCollectDefine.NMS_TYPE_ALU_FLAG:
			map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
			break;
		default:
			map.put("NATIVE_EMS_NAME", model.getRackNo());
		}
		map.put("OWNER", model.getOwner());
		map.put("ALARM_REPORTING_INDICATOR", null);
		map.put("HOLDER_STATE", model.getHolderState());
		map.put("HARD_WARE_VERSION", model.getHardwareVersion());
		map.put("SERIAL_NO", model.getSerialNo());
		map.put("RACK_NO", model.getRackNo());
		map.put("IS_DEL", DataCollectDefine.FALSE);

		if (isUpdate) {
			// 更新信息
			// 显示模式为自动时需要更新
			if (map.get("DISPLAY_MODE") != null
					&& Integer.valueOf(map.get("DISPLAY_MODE").toString())
							.intValue() == DataCollectDefine.DISPLAY_MODE_AUTO) {
				map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			}
			// map.put("UPDATE_TIME", new Date());
		} else {
			// 新增信息
			// USER_LABEL用作规范名称
			map.put("USER_LABEL", map.get("NATIVE_EMS_NAME"));
			map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			map.put("BASE_RACK_ID", null);
			map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
			map.put("BASE_NE_ID", neId);
			map.put("CREATE_TIME", new Date());
			// map.put("UPDATE_TIME", null);
		}
		return map;
	}

	/**
	 * @param model
	 * @param emsConnectionId
	 * @param neId
	 * @param isUpdate
	 * @return
	 */
	@IMethodLog(desc = "DataCollectService：子架信息表对象构建")
	private Map shelfModelToTable(EquipmentHolderModel model,
			int emsConnectionId, int neId, int type, boolean isUpdate) {

		Map map = new HashMap();
		// 基础信息
		map.put("NAME", model.getNameString());
		switch (type) {
		case DataCollectDefine.NMS_TYPE_T2000_FLAG:
		case DataCollectDefine.NMS_TYPE_U2000_FLAG:
			map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
			break;
		case DataCollectDefine.NMS_TYPE_E300_FLAG:
			/*根据BD要求，DISPLAY_NAME取SHELF_NO
			 *map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			*/
			/*E300 shelf NO.(AAA）
			*“AAA”截取NATIVE_EMS_NAME
			*如果带（）,截取（）里面的内容。
			*如果不带（）,截取全部
			*/
			if(null != model.getNativeEMSName())
			{
				int indexofleft = model.getNativeEMSName().indexOf("(");
				int indexofright = model.getNativeEMSName().indexOf(")");
				//不带括号
				if(-1 == indexofleft)
				{
					map.put("NATIVE_EMS_NAME", model.getShelfNo()+"("+model.getNativeEMSName()+")");
				}
				else if( (-1 != indexofleft) && (-1 != indexofright) )//带括号
				{
					map.put("NATIVE_EMS_NAME", model.getShelfNo()
							+ model.getNativeEMSName().toString().substring(indexofleft, indexofright)+")");
				}
				else
				{
					map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
				}
			}
			else
			{
				map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
			}
			break;
		case DataCollectDefine.NMS_TYPE_U31_FLAG:
			map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
			break;
		case DataCollectDefine.NMS_TYPE_LUCENT_OMS_FLAG:
			map.put("NATIVE_EMS_NAME", "1");
			break;
		case DataCollectDefine.NMS_TYPE_OTNM2000_FLAG:
			map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
			break;
		case DataCollectDefine.NMS_TYPE_ALU_FLAG:
			String tmp=""+model.getNativeEMSName();
			if(tmp.indexOf("/")>=0)
				tmp=tmp.substring(tmp.indexOf("/")+1);
			map.put("NATIVE_EMS_NAME", tmp);
			break;
		default:
			map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
		}
		map.put("OWNER", model.getOwner());
		map.put("ALARM_REPORTING_INDICATOR", null);
		map.put("HOLDER_STATE", model.getHolderState());
		map.put("LOCATION", model.getLocation());
		map.put("VENDOR_NAME", model.getVendorName());
		map.put("HARD_WARE_VERSION", model.getHardwareVersion());
		map.put("SHELF_TYPE", model.getShelfType());
		map.put("SERIAL_NO", model.getSerialNo());
		map.put("RACK_NO", model.getRackNo());
		map.put("SHELF_NO", model.getShelfNo());
		map.put("IS_DEL", DataCollectDefine.FALSE);

		if (isUpdate) {
			// 更新信息
			// 显示模式为自动时需要更新
			if (map.get("DISPLAY_MODE") != null
					&& Integer.valueOf(map.get("DISPLAY_MODE").toString())
							.intValue() == DataCollectDefine.DISPLAY_MODE_AUTO) {
				map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			}
			// map.put("UPDATE_TIME", new Date());
		} else {
			// 新增信息
			// USER_LABEL用作规范名称
			map.put("USER_LABEL", map.get("NATIVE_EMS_NAME"));
			map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			map.put("BASE_SHELF_ID", null);
			map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
			map.put("BASE_NE_ID", neId);
			map.put("BASE_RACK_ID", null);
			map.put("CREATE_TIME", new Date());
			// map.put("UPDATE_TIME", null);
		}
		return map;
	}

	/**
	 * @param model
	 * @param emsConnectionId
	 * @param neId
	 * @param isUpdate
	 * @return
	 */
	@IMethodLog(desc = "DataCollectService：槽道信息表对象构建")
	private Map slotModelToTable(EquipmentHolderModel model,
			int emsConnectionId, int neId, int type, boolean isUpdate) {

		Map map = new HashMap();
		// 基础信息
		map.put("NAME", model.getNameString());
		switch (type) {
		case DataCollectDefine.NMS_TYPE_T2000_FLAG:
		case DataCollectDefine.NMS_TYPE_U2000_FLAG:
			//任务 #1378 华为SLOT信息入库修改 
//			原入库规则：取NATIVE_EMS_NAME 实例：1-D75S
//			修改为： 取SLOT NO. 实例：1
//			map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			map.put("NATIVE_EMS_NAME", model.getSlotNo());
			break;
		case DataCollectDefine.NMS_TYPE_E300_FLAG:
		case DataCollectDefine.NMS_TYPE_U31_FLAG:
			/*根据BD要求，DISPLAY_NAME取SLOT_NO
			 *map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			*/
			map.put("NATIVE_EMS_NAME", model.getSlotNo());
			break;
		case DataCollectDefine.NMS_TYPE_LUCENT_OMS_FLAG:
		case DataCollectDefine.NMS_TYPE_ALU_FLAG:
			map.put("NATIVE_EMS_NAME", model.getSlotNo());
			break;
		case DataCollectDefine.NMS_TYPE_OTNM2000_FLAG:
			String slotNo = model.getSlotNo();
			if(slotNo!=null&&!slotNo.isEmpty()){
				slotNo = String.format("%02X", Integer.valueOf(slotNo)%(1<<10));
			}else{
				slotNo="NA";
			}
			map.put("NATIVE_EMS_NAME", slotNo);
			break;
		default:
			map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
		}
		map.put("OWNER", model.getOwner());
		map.put("ALARM_REPORTING_INDICATOR", null);
		map.put("HOLDER_STATE", model.getHolderState());
		map.put("HARD_WARE_VERSION", model.getHardwareVersion());
		map.put("SERIAL_NO", model.getSerialNo());
		map.put("RACK_NO", model.getRackNo());
		map.put("SHELF_NO", model.getShelfNo());
		map.put("SLOT_NO", model.getSlotNo());
		map.put("IS_DEL", DataCollectDefine.FALSE);
		
		map.put("ACCEPT_EQPT_TYPE", model.getAcceptableEquipmentTypeSrting());

		if (isUpdate) {
			// 更新信息
			// 显示模式为自动时需要更新
			if (map.get("DISPLAY_MODE") != null
					&& Integer.valueOf(map.get("DISPLAY_MODE").toString())
							.intValue() == DataCollectDefine.DISPLAY_MODE_AUTO) {
				map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			}
			// map.put("UPDATE_TIME", new Date());
		} else {
			// 新增信息
			// USER_LABEL用作规范名称
			map.put("USER_LABEL", map.get("NATIVE_EMS_NAME"));
			map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			map.put("BASE_SLOT_ID", null);
			map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
			map.put("BASE_NE_ID", neId);
			map.put("BASE_RACK_ID", null);
			map.put("BASE_SHELF_ID", null);
			map.put("CREATE_TIME", new Date());
			// map.put("UPDATE_TIME", null);
		}
		return map;
	}
	
	/**
	 * @param model
	 * @param emsConnectionId
	 * @param neId
	 * @param isUpdate
	 * @return
	 */
	@IMethodLog(desc = "DataCollectService：子槽道信息表对象构建")
	private Map subSlotModelToTable(EquipmentHolderModel model,
			int emsConnectionId, int neId, int type, boolean isUpdate) {

		Map map = new HashMap();
		// 基础信息
		map.put("NAME", model.getNameString());
		switch (type) {
		case DataCollectDefine.NMS_TYPE_E300_FLAG:
		case DataCollectDefine.NMS_TYPE_U31_FLAG:
			map.put("NATIVE_EMS_NAME", model.getSlotNo());
			break;
		case DataCollectDefine.NMS_TYPE_LUCENT_OMS_FLAG:
			map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
			break;
		case DataCollectDefine.NMS_TYPE_OTNM2000_FLAG:
			map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
			break;
		case DataCollectDefine.NMS_TYPE_ALU_FLAG:
			map.put("NATIVE_EMS_NAME", model.getSubSlotNo());
			break;
		default:
			map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
		}
		map.put("OWNER", model.getOwner());
		map.put("ALARM_REPORTING_INDICATOR", null);
		map.put("HOLDER_STATE", model.getHolderState());
		map.put("HARD_WARE_VERSION", model.getHardwareVersion());
		map.put("SERIAL_NO", model.getSerialNo());
		map.put("RACK_NO", model.getRackNo());
		map.put("SHELF_NO", model.getShelfNo());
		map.put("SLOT_NO", model.getSlotNo());
		map.put("SUB_SLOT_NO", model.getSubSlotNo());
		map.put("IS_DEL", DataCollectDefine.FALSE);

		if (isUpdate) {
			// 更新信息
			// 显示模式为自动时需要更新
			if (map.get("DISPLAY_MODE") != null
					&& Integer.valueOf(map.get("DISPLAY_MODE").toString())
							.intValue() == DataCollectDefine.DISPLAY_MODE_AUTO) {
				map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			}
			// map.put("UPDATE_TIME", new Date());
		} else {
			// 新增信息
			// USER_LABEL用作规范名称
			map.put("USER_LABEL", map.get("NATIVE_EMS_NAME"));
			map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			map.put("BASE_SUB_SLOT_ID", null);
			map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
			map.put("BASE_NE_ID", neId);
			map.put("BASE_SLOT_ID", null);
			map.put("CREATE_TIME", new Date());
			// map.put("UPDATE_TIME", null);
		}
		return map;
	}

	/**
	 * @param model
	 * @param emsConnectionId
	 * @param neId
	 * @param isUpdate
	 * @return
	 */
	@IMethodLog(desc = "DataCollectService：板卡信息表对象构建")
	private Map equipModelToTable(EquipmentModel model, int emsConnectionId,
			int neId, int type, boolean isUpdate) {

		Map map = new HashMap();
		// 基础信息
		map.put("NAME", model.getNameString());
		switch (type) {
		case DataCollectDefine.NMS_TYPE_T2000_FLAG:
		case DataCollectDefine.NMS_TYPE_U2000_FLAG:
			map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
			map.put("UNIT_NAME", model.getInstalledEquipmentObjectType()
					.replaceAll("\\([^)]+\\)", ""));
			break;
		case DataCollectDefine.NMS_TYPE_E300_FLAG:
		case DataCollectDefine.NMS_TYPE_U31_FLAG:
			map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
			map.put("UNIT_NAME", model.getInstalledEquipmentObjectType());
			break;
		case DataCollectDefine.NMS_TYPE_LUCENT_OMS_FLAG:
		case DataCollectDefine.NMS_TYPE_ALU_FLAG:
			map.put("NATIVE_EMS_NAME", model.getExpectedEquipmentObjectType());
			map.put("UNIT_NAME", model.getInstalledEquipmentObjectType());
			break;
		case DataCollectDefine.NMS_TYPE_OTNM2000_FLAG:
			map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
			map.put("UNIT_NAME", model.getInstalledEquipmentObjectType());
			break;
		default:
			map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
		}
		map.put("OWNER", model.getOwner());
		map.put("ALARM_REPORTING_INDICATOR", null);
		map.put("SERVICE_STATE", model.getServiceState());

		map.put("EXPECTED_EQUIP_OBJ_TYPE",
				model.getExpectedEquipmentObjectType());
		map.put("INSTALLED_EQUIP_OBJ_TYPE",
				model.getInstalledEquipmentObjectType());
		map.put("INSTALLED_PART_NUMBER", model.getInstalledPartNumber());
		map.put("INSTALLED_SERIAL_NUMBER", model.getInstalledSerialNumber());
		map.put("HARD_WARE_VERSION", model.getHardwareVersion());
		map.put("SOFT_WARE_VERSION", model.getSoftwareVersion());
		map.put("HAS_PROTECTION",
				model.isHasProtection() ? DataCollectDefine.TRUE
						: DataCollectDefine.FALSE);

		map.put("MANUFACTURE", null);
		// 添加SFP信息--济南联通需求
		String sfpInfo = "";
		if (model.getAdditionalInfo() != null) {
			for (NameAndStringValue_T additionalInfo : model
					.getAdditionalInfo()) {
				if (additionalInfo.name.endsWith("_SFP")) {
					if (additionalInfo.name.split("_").length == 3) {
						String portNo = additionalInfo.name.split("_")[1];
						String sfpValue = additionalInfo.value;
						sfpInfo = sfpInfo + portNo + "#" + sfpValue + ";";
					}
				}
			}
		}
		map.put("RACK_NO", model.getRackNo());
		map.put("SHELF_NO", model.getShelfNo());
		map.put("SLOT_NO", model.getSlotNo());
		map.put("IS_TRANSPARENCY", model.isTransparency()?DataCollectDefine.TRUE:DataCollectDefine.FALSE);
		map.put("IS_DEL", DataCollectDefine.FALSE);

		if (isUpdate) {
			// 更新信息
			// 显示模式为自动时需要更新
			if (map.get("DISPLAY_MODE") != null
					&& Integer.valueOf(map.get("DISPLAY_MODE").toString())
							.intValue() == DataCollectDefine.DISPLAY_MODE_AUTO) {
				map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			}
			// map.put("UPDATE_TIME", new Date());
		} else {
			// 新增信息
			// USER_LABEL用作规范名称
			map.put("USER_LABEL", map.get("NATIVE_EMS_NAME"));
			map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			map.put("BASE_UNIT_ID", null);
			map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
			map.put("BASE_NE_ID", neId);
			map.put("BASE_RACK_ID", null);
			map.put("BASE_SHELF_ID", null);
			map.put("BASE_SLOT_ID", null);
			map.put("CREATE_TIME", new Date());

			map.put("SFP_INFO", sfpInfo);
			// map.put("UPDATE_TIME", null);
		}
		return map;
	}
	
	/**
	 * @param model
	 * @param emsConnectionId
	 * @param neId
	 * @param isUpdate
	 * @return
	 */
	@IMethodLog(desc = "DataCollectService：子板卡信息表对象构建")
	private Map subEquipModelToTable(EquipmentModel model, int emsConnectionId,
			Map<String,Object> ne, int type, boolean isUpdate) {
		int neId = Integer.valueOf(String.valueOf(ne.get("BASE_NE_ID")));
		int neType = ne.get("TYPE")==null?null:Integer.valueOf(String.valueOf(ne.get("TYPE")));
		Map map = new HashMap();
		// 基础信息
		map.put("NAME", model.getNameString());
		switch (type) {
		case DataCollectDefine.NMS_TYPE_E300_FLAG:
		case DataCollectDefine.NMS_TYPE_U31_FLAG:
			map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
			break;
		case DataCollectDefine.NMS_TYPE_LUCENT_OMS_FLAG:
			map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
			break;
		case DataCollectDefine.NMS_TYPE_ALU_FLAG:
			map.put("NATIVE_EMS_NAME", model.getInstalledEquipmentObjectType());
			//贝尔仅PTN显示子板卡
			if(neType!=DataCollectDefine.COMMON.NE_TYPE_PTN_FLAG)
			map.put("DISPLAY_MODE", DataCollectDefine.DISPLAY_MODE.NONE);
			break;
		default:
			map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
		}
		map.put("OWNER", model.getOwner());
		map.put("ALARM_REPORTING_INDICATOR", null);
		map.put("SERVICE_STATE", model.getServiceState());

		map.put("EXPECTED_EQUIP_OBJ_TYPE",
				model.getExpectedEquipmentObjectType());
		map.put("INSTALLED_EQUIP_OBJ_TYPE",
				model.getInstalledEquipmentObjectType());
		map.put("INSTALLED_PART_NUMBER", model.getInstalledPartNumber());
		map.put("INSTALLED_SERIAL_NUMBER", model.getInstalledSerialNumber());
		map.put("HARD_WARE_VERSION", model.getHardwareVersion());
		map.put("SOFT_WARE_VERSION", model.getSoftwareVersion());
		map.put("HAS_PROTECTION",
				model.isHasProtection() ? DataCollectDefine.TRUE
						: DataCollectDefine.FALSE);

		map.put("MANUFACTURE", null);
		map.put("RACK_NO", model.getRackNo());
		map.put("SHELF_NO", model.getShelfNo());
		map.put("SLOT_NO", model.getSlotNo());
		map.put("SUB_SLOT_NO", model.getSubSlotNo());
		map.put("IS_DEL", DataCollectDefine.FALSE);

		if (isUpdate) {
			// 更新信息
			// 显示模式为自动时需要更新
			if (map.get("DISPLAY_MODE") != null
					&& Integer.valueOf(map.get("DISPLAY_MODE").toString())
							.intValue() == DataCollectDefine.DISPLAY_MODE_AUTO) {
				map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			}
			// map.put("UPDATE_TIME", new Date());
		} else {
			// 新增信息
			// USER_LABEL用作规范名称
			map.put("USER_LABEL", map.get("NATIVE_EMS_NAME"));
			map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			map.put("BASE_SUB_UNIT_ID", null);
			map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
			map.put("BASE_NE_ID", neId);
			map.put("BASE_SUB_SLOT_ID", null);
			map.put("CREATE_TIME", new Date());
			// map.put("UPDATE_TIME", null);
		}
		return map;
	}

	/**
	 * @param neName
	 * @param ip
	 * @param model
	 */
	@IMethodLog(desc = "DataCollectService：ptp信息表对象构建")
	private Map terminationPointModelToTable(TerminationPointModel model,
			int emsConnectionId, int neId, int type, int defaultDomain, boolean isUpdate) {

		Map map = new HashMap();
		// 基础信息
		map.put("NAME", model.getNameString());
		switch (type) {
		case DataCollectDefine.NMS_TYPE_T2000_FLAG:
		case DataCollectDefine.NMS_TYPE_U2000_FLAG:
			if(model.getNativeEMSName() == null || model.getNativeEMSName().isEmpty()){
				map.put("NATIVE_EMS_NAME", model.getPortNo());
			}else{
				map.put("NATIVE_EMS_NAME", model.getPortNo()+"("+model.getNativeEMSName()+")");
			}
			break;
		case DataCollectDefine.NMS_TYPE_E300_FLAG:
		case DataCollectDefine.NMS_TYPE_U31_FLAG:
			/*1.取port no.(NATIVE_EMS_NAME)
			 * 2.NATIVE_EMS_NAME为空时，取port no. 
			 */
			if(model.getNativeEMSName() == null || model.getNativeEMSName().isEmpty()){
				map.put("NATIVE_EMS_NAME", model.getPortNo());
			}else{
				map.put("NATIVE_EMS_NAME", model.getPortNo()+"("+model.getNativeEMSName()+")");
			}
			break;
		case DataCollectDefine.NMS_TYPE_LUCENT_OMS_FLAG:
			if(model.getNativeEMSName() == null || model.getNativeEMSName().isEmpty()){
				map.put("NATIVE_EMS_NAME", model.getPortNo());
			}else{
				map.put("NATIVE_EMS_NAME", model.getPortNo()+"("+model.getNativeEMSName()+")");
			}
			break;
		case DataCollectDefine.NMS_TYPE_OTNM2000_FLAG:
		case DataCollectDefine.NMS_TYPE_ALU_FLAG:
			map.put("NATIVE_EMS_NAME", model.getPortNo());
			break;
		default:
			if(model.getNativeEMSName() == null || model.getNativeEMSName().isEmpty()){
				map.put("NATIVE_EMS_NAME", model.getPortNo());
			}else{
				map.put("NATIVE_EMS_NAME", model.getPortNo()+"("+model.getNativeEMSName()+")");
			}
		}
		map.put("OWNER", model.getOwner());
		map.put("PTP_FTP", model.getPtpOrFtp());
		// 连接状态
		map.put("CONNECTION_STATE", model.getConnectionState());
		// 映射方式
		map.put("TP_MAPPING_MODE", model.getTpMappingMode());
		// 方向 0._D_NA 1._D_BIDIRECTIONAL 2._D_SOURCE 3._D_SINK
		map.put("DIRECTION", model.getDirection());
		//
		map.put("TP_PROTECTION_ASSOCIATION", model.getTpProtectionAssociation());
		map.put("EDGE_POINT", model.isEdgePoint() ? DataCollectDefine.TRUE
				: DataCollectDefine.FALSE);
		// 速率字符串47:49:73:20:25
		map.put("LAYER_RATE", model.getLayerRateString());
		// 业务类型 1.SDH 2.WDM 3.ETH 4.ATM
		
		if(DataCollectDefine.COMMON.DOMAIN_UNKNOW_FLAG == model.getDomain()){
			//BUG #3206 修改对策：层速率是“1”的端口， DOMIAN不再填写，为空。
			if("1".equals(model.getLayerRateString().trim())){
				map.put("DOMAIN", DataCollectDefine.COMMON.DOMAIN_UNKNOW_FLAG);
			}else{
				map.put("DOMAIN", defaultDomain);
			}
		}else{
			map.put("DOMAIN", model.getDomain());
		}
		// 端口类型： MP MAC E1 E3 E4 STM-1 STM-4 STM-16 STM-64 STM-256 OTS OMS
		// OSCNI OCH OTS&OMS 参考具体BD文档 中兴截取ptptype
		map.put("PTP_TYPE", model.getPtpType());
		// 速率： 100M 1000M 10000M 2M 34M 140M 155M 622M 2.5G 10G 40G
		map.put("RATE", model.getRate());
		// 未知作用，暂时直接入库分析
		map.put("TYPE", model.getType());

		map.put("RACK_NO", model.getRackNo());
		map.put("SHELF_NO", model.getShelfNo());
		map.put("SLOT_NO", model.getSlotNo());
		map.put("SUB_SLOT_NO", model.getSubSlotNo());
		map.put("PORT_NO", model.getPortNo());
		// 端口描述 在添加外键关联时候组织数据
		map.put("PORT_DESC", null);
		
		map.put("PTN_PWID", model.getPTNTP_PWId());
		map.put("PTN_PWMODE", model.getPTNTP_PWMode());
		map.put("PTN_PWTYPE", model.getPTNTP_PWType());
		map.put("PTN_INLABEL", model.getPTNTP_InLabel());
		map.put("PTN_OUTLABEL", model.getPTNTP_OutLabel());
		map.put("PTN_PSNTYPE", model.getPTNTP_PSNType());
		map.put("PTN_VCID", model.getPTNTP_VCId());
		
		map.put("IS_DEL", DataCollectDefine.FALSE);

		if (isUpdate) {
			// 更新信息
			// 显示模式为自动时需要更新
			if (map.get("DISPLAY_MODE") != null
					&& Integer.valueOf(map.get("DISPLAY_MODE").toString())
							.intValue() == DataCollectDefine.DISPLAY_MODE_AUTO) {
				map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			}
			// map.put("UPDATE_TIME", new Date());
		} else {
			// 新增信息
			// USER_LABEL用作规范名称
			map.put("USER_LABEL", map.get("NATIVE_EMS_NAME"));
			map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			map.put("BASE_PTP_ID", null);
			map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
			map.put("BASE_NE_ID", neId);
			map.put("BASE_RACK_ID", null);
			map.put("BASE_SHELF_ID", null);
			map.put("BASE_SLOT_ID", null);
			map.put("BASE_SUB_SLOT_ID", null);
			map.put("BASE_UNIT_ID", null);
			map.put("BASE_SUB_UNIT_ID", null);
			map.put("PM_TEMPLATE_ID", null);
			map.put("CREATE_TIME", new Date());

			// 1.边界点 2.连接点
			map.put("PORT_TYPE", DataCollectDefine.COMMON.PORT_TYPE_EDGE_POINT);
			// 虚拟网桥
			map.put("MSTP_VB", null);
			// 衰耗器大小dB
			map.put("ATT", null);
			// ddf/odf子架信息
			map.put("DDF_ODF", null);
			// 光模块信息--在添加外键关联时候添加
			map.put("OPT_MODEL", null);
			// 光口标准Id
			map.put("OPT_STD_ID", null);
			// 是否有保护 0：不是 1：是
			map.put("IS_PROTECTED", DataCollectDefine.FALSE);
			// 是否已同步ctp信息 0：不是 1：是
			map.put("IS_SYNC_CTP", model.getIsSyncCtp());
			// map.put("UPDATE_TIME", null);
		}
		return map;
	}

	/**
	 * @param neName
	 * @param ip
	 * @param model
	 */
	@IMethodLog(desc = "DataCollectService：ClockSource信息表对象构建")
	private Map clockSourceStatusModelToTable(ClockSourceStatusModel model,
			int emsConnectionId, int neId, int type, boolean isUpdate) {

		Map map = new HashMap();
		// 基础信息
		map.put("NAME", model.getNameString());
		map.put("NATIVE_EMS_NAME",model.getNativeEMSName());
		switch (type) {
		case DataCollectDefine.NMS_TYPE_T2000_FLAG:
		case DataCollectDefine.NMS_TYPE_U2000_FLAG:
			map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));

			// 是否成帧 华为无
			map.put("IS_FRAME", null);
			// 是否使用SSM 华为无
			map.put("IS_SSM", null);
			// 时钟源的同步状态 --华为无
			map.put("SYNC_STATUS", null);
			// 优先级
			map.put("PRIORITY", null);
			break;
		case DataCollectDefine.NMS_TYPE_E300_FLAG:
		case DataCollectDefine.NMS_TYPE_U31_FLAG:
			map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			// 是否成帧
			map.put("IS_FRAME", model.isFrame()?DataCollectDefine.TRUE:DataCollectDefine.FALSE);
			// 是否使用SSM
			map.put("IS_SSM", model.isSsm()?DataCollectDefine.TRUE:DataCollectDefine.FALSE);
			// 时钟源的同步状态
			map.put("SYNC_STATUS", model.getSyncStatus());
			// 优先级
			map.put("PRIORITY", model.getPriority());
			break;
		case DataCollectDefine.NMS_TYPE_LUCENT_OMS_FLAG:
			map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			// 是否成帧 华为无
			map.put("IS_FRAME", null);
			// 是否使用SSM 华为无
			map.put("IS_SSM", null);
			// 时钟源的同步状态 --华为无
			map.put("SYNC_STATUS", null);
			// 优先级
			map.put("PRIORITY", null);
			break;
		case DataCollectDefine.NMS_TYPE_OTNM2000_FLAG:
		case DataCollectDefine.NMS_TYPE_ALU_FLAG:
			// FIXME 烽火 贝尔 时钟 待数据分析
			map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			// 是否成帧 华为无
			map.put("IS_FRAME", null);
			// 是否使用SSM 华为无
			map.put("IS_SSM", null);
			// 时钟源的同步状态 --华为无
			map.put("SYNC_STATUS", null);
			// 优先级
			map.put("PRIORITY", null);
			break;
		default:
			map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
		}
		// 0：CURRENT 1：BACKUP
		map.put("IS_CURRENT", model.isCurrent() ? DataCollectDefine.TRUE
				: DataCollectDefine.FALSE);
		// 时间模式
		map.put("TIMING_MODE", model.getTimingModeFlag());
		// 时钟质量
		map.put("QUALITY", model.getQualityFlag());
		// 工作模式
		map.put("WORKING_MODE", model.getWorkingModeFlag());
		map.put("IS_DEL", DataCollectDefine.FALSE);

		if (isUpdate) {
			// 更新信息
			// map.put("UPDATE_TIME", new Date());
		} else {
			// 新增信息
			map.put("BASE_CLOCK_ID", null);
			map.put("BASE_NE_ID", neId);
			map.put("CREATE_TIME", new Date());
			// map.put("UPDATE_TIME", null);
		}
		return map;
	}

	/**
	 * @param neName
	 * @param ip
	 * @param model
	 */
	@IMethodLog(desc = "DataCollectService：Link信息表对象构建")
	private Map topologicalLinkModelToTable(TopologicalLinkModel model,
			int emsConnectionId, Integer aEndNeId, int aEndPtpId,
			Integer zEndNeId, int zEndPtpId, int type,int linkType,
			boolean isUpdate) {

		Map map = new HashMap();
		// 基础信息
		map.put("NAME", model.getNameString());
		// USER_LABEL用作规范名称
		// map.put("USER_LABEL", model.getUserLabel());
		map.put("NATIVE_EMS_NAME",model.getNativeEMSName());
		map.put("OWNER", model.getOwner());
		map.put("DIRECTION", model.getDirection());
		map.put("A_EMS_CONNECTION_ID", emsConnectionId);
		map.put("A_NE_ID", aEndNeId);
		map.put("A_END_PTP", aEndPtpId);
		map.put("Z_EMS_CONNECTION_ID", emsConnectionId);
		map.put("Z_NE_ID", zEndNeId);
		map.put("Z_END_PTP", zEndPtpId);
		
		switch (type) {
		case DataCollectDefine.NMS_TYPE_T2000_FLAG:
		case DataCollectDefine.NMS_TYPE_U2000_FLAG:
			//linkType不变
			break;
		case DataCollectDefine.NMS_TYPE_E300_FLAG:
		case DataCollectDefine.NMS_TYPE_U31_FLAG:
		case DataCollectDefine.NMS_TYPE_LUCENT_OMS_FLAG:
		case DataCollectDefine.NMS_TYPE_OTNM2000_FLAG:
		case DataCollectDefine.NMS_TYPE_ALU_FLAG:
			//根据a-z网元确定linkType
			linkType = aEndNeId.intValue() == zEndNeId.intValue() ? DataCollectDefine.LINK_TYPE_INTERNAL_FLAG
				: DataCollectDefine.LINK_TYPE_EXTERNAL_FLAG;
			break;
		default:
		}
		map.put("LINK_TYPE", linkType);
		map.put("CHANGE_STATE", DataCollectDefine.LATEST_ADD);
		// map.put("IS_MANUAL", null);

		map.put("IS_DEL", DataCollectDefine.FALSE);

		if (isUpdate) {
			// 更新信息
			// 显示模式为自动时需要更新
			if (map.get("DISPLAY_MODE") != null
					&& Integer.valueOf(map.get("DISPLAY_MODE").toString())
							.intValue() == DataCollectDefine.DISPLAY_MODE_AUTO) {
				map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			}
			// map.put("UPDATE_TIME", new Date());
		} else {
			// 新增信息
			// USER_LABEL用作规范名称
			map.put("USER_LABEL", map.get("NATIVE_EMS_NAME"));
			map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
			map.put("BASE_LINK_ID", null);
			map.put("IS_MAIN", null);
			map.put("CREATE_TIME", new Date());
			// map.put("UPDATE_TIME", null);
		}
		return map;
	}

	/**
	 * @param neName
	 * @param ip
	 * @param model
	 */
	@IMethodLog(desc = "DataCollectService：T_BASE_E_PRO_GROUP 信息表对象构建")
	private Map eProtectionGroupModelToTable(EProtectionGroupModel model,
			int emsConnectionId, Integer neId, int type, boolean isUpdate) {

		Map map = new HashMap();
		// 基础信息
		map.put("NAME", model.getNameString());
		map.put("USER_LABEL", model.getUserLabel());
		map.put("NATIVE_EMS_NAME",model.getNativeEMSName());
		map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
		map.put("OWNER", model.getOwner());

		map.put("EPGP_GROUP", model.getEpgpGroup());

		map.put("EPGP_LOCATION", model.getEpgpLocation());
		map.put("E_PROTECTION_GROUP_TYPE", model.geteProtectionGroupType());
		map.put("PROTECTION_SCHEMA_STATE", model.getProtectionSchemeState());
		map.put("REVERSION_MODE", model.getReversionMode());
		map.put("TYPE", model.getType());
		map.put("WTR_TIME", model.getWtrTime());
		map.put("IS_DEL", DataCollectDefine.FALSE);

		if (isUpdate) {
			// 更新信息
			// map.put("UPDATE_TIME", new Date());
		} else {
			// 新增信息
			map.put("BASE_E_PRO_GROUP_ID", null);
			map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
			map.put("BASE_NE_ID", neId);
			map.put("CREATE_TIME", new Date());
			// map.put("UPDATE_TIME", null);
		}
		return map;
	}

	/**
	 * @param neName
	 * @param ip
	 * @param model
	 */
	@IMethodLog(desc = "DataCollectService：T_BASE_PRO_GROUP 信息表对象构建")
	private Map protectionGroupModelToTable(ProtectionGroupModel model,
			int emsConnectionId, Integer neId, int type, boolean isUpdate) {

		Map map = new HashMap();
		// 基础信息
		map.put("NAME", model.getNameString());
		map.put("USER_LABEL", model.getUserLabel());
		map.put("NATIVE_EMS_NAME",model.getNativeEMSName());
		map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
		map.put("OWNER", model.getOwner());

		map.put("PGP_GROUP", model.getPgpGroup());

		map.put("PGP_LOCATION", model.getPgpLocation());
		map.put("PROTECTION_GROUP_TYPE", model.getProtectionGroupType());
		map.put("PROTECTION_SCHEMA_STATE", model.getProtectionSchemeState());
		map.put("REVERSION_MODE", model.getReversionMode());
		map.put("RATE", model.getRate());
		map.put("SWITCH_MODE", model.getSwitchMode());
		map.put("WTR_TIME", model.getWtrTime());
		map.put("HOLD_OFF_TIME", model.getHoldOffTime());
		map.put("LOD_NUM_SWITCHES", model.getLodNumSwitches());
		map.put("LOD_DURATION", model.getLodDuration());
		map.put("SPRING_PROTOCOL", model.getSpringProtocol());
		map.put("SPRING_NODE_ID", model.getSpringNodeId());
		map.put("SWITCH_POSITION", model.getSwitchPosition());
		map.put("NON_PRE_EMPTIBLE_TRAFFIC", model.getNonPreEmptibleTraffic());
		map.put("IS_DEL", DataCollectDefine.FALSE);

		if (isUpdate) {
			// 更新信息
			// map.put("UPDATE_TIME", new Date());
		} else {
			// 新增信息
			map.put("BASE_PRO_GROUP_ID", null);
			map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
			map.put("BASE_NE_ID", neId);
			map.put("CREATE_TIME", new Date());
			// map.put("UPDATE_TIME", null);
		}
		return map;
	}

	/**
	 * @param neName
	 * @param ip
	 * @param model
	 */
	@IMethodLog(desc = "DataCollectService：T_BASE_WDM_PRO_GROUP 信息表对象构建")
	private Map wdmProtectionGroupModelToTable(WDMProtectionGroupModel model,
			int emsConnectionId, Integer neId, int type, boolean isUpdate) {

		Map map = new HashMap();
		// 基础信息
		map.put("NAME", model.getNameString());
		map.put("USER_LABEL", model.getUserLabel());
		map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
		map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
		map.put("OWNER", model.getOwner());

		map.put("WDM_PGP_GROUP", model.getWdmPgpGroup());

		map.put("WDM_PGP_LOCATION", model.getWdmPgpLocation());
		map.put("PROTECTION_GROUP_TYPE", model.getProtectionGroupType());
		map.put("PROTECTION_SCHEMA_STATE", model.getProtectionSchemeState());
		map.put("REVERSION_MODE", model.getReversionMode());
		map.put("WTR_TIME", model.getWtrTime());
		map.put("HOLD_OFF_TIME", model.getHoldOffTime());
		map.put("IS_DEL", DataCollectDefine.FALSE);

		if (isUpdate) {
			// 更新信息
			// map.put("UPDATE_TIME", new Date());
		} else {
			// 新增信息
			map.put("BASE_WDM_PRO_GROUP_ID", null);
			map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
			map.put("BASE_NE_ID", neId);
			map.put("CREATE_TIME", new Date());
			// map.put("UPDATE_TIME", null);
		}
		return map;
	}

	/**
	 * @param neName
	 * @param ip
	 * @param model
	 */
	@IMethodLog(desc = "DataCollectService：T_BASE_ETH_SVC信息表对象构建")
	private Map ethServiceModelToTable(EthServiceModel model,
			int emsConnectionId, Integer neId, int aEndPtpId, int zEndPtpId,
			String aEndPointType, String zEndPointType, int type,
			boolean isUpdate) {

		Map map = new HashMap();
		// 基础信息
		map.put("NAME", model.getNameString());
		map.put("USER_LABEL", model.getUserLabel());
		map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
		map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
		map.put("OWNER", model.getOwner());
		map.put("SERVICE_TYPE", model.getServiceType());

		map.put("DIRECTION", model.getDirection());
		map.put("ACTIVE_STATE", model.isActiveState() ? DataCollectDefine.TRUE
				: DataCollectDefine.FALSE);
		map.put("A_END_POINT", aEndPtpId);
		map.put("A_END_VLAN_ID", model.getaEndPointVlanID());
		map.put("A_END_TUNNEL", model.getaEndPointTunnel());
		map.put("A_END_VC", model.getaEndPointVc());
		map.put("A_END_POINT_TYPE", aEndPointType);
		map.put("Z_END_POINT", zEndPtpId);
		map.put("Z_END_VLAN_ID", model.getzEndPointVlanID());
		map.put("Z_END_TUNNEL", model.getzEndPointTunnel());
		map.put("Z_END_VC", model.getzEndPointVc());
		map.put("Z_END_POINT_TYPE", zEndPointType);

		map.put("IS_DEL", DataCollectDefine.FALSE);

		if (isUpdate) {
			// 更新信息
			// map.put("UPDATE_TIME", new Date());
		} else {
			// 新增信息
			map.put("BASE_ETH_SVC_ID", null);
			map.put("BASE_NE_ID", neId);
			map.put("CREATE_TIME", new Date());
			// map.put("UPDATE_TIME", null);
		}
		return map;
	}

	/**
	 * @param neName
	 * @param ip
	 * @param model
	 */
	@IMethodLog(desc = "DataCollectService：T_BASE_ETH_SVC信息表对象构建")
	private Map bindingPathModelToTable(MSTPBindingPathModel model,Integer neId,
			Integer ptpId, String ptpNameStringFromSource, Integer vcgPtpId, String vcgPtpName, Integer bindingPtpId, String bindingPtpName, 
			Long sdhCtpId, Long otnCtpId, boolean isUsed,
			boolean isUpdate) {

		Map map = new HashMap();
		// 基础信息
		map.put("DIRECTION", model.getDirection());
		map.put("TYPE", isUsed ? DataCollectDefine.TRUE
				: DataCollectDefine.FALSE);

		if (isUpdate) {
			// 更新信息
			// map.put("UPDATE_TIME", new Date());
		} else {
			// 新增信息
			map.put("BASE_BINDING_PATH_ID", null);
			map.put("BASE_NE_ID", neId);
			map.put("BASE_PTP_ID", ptpId);
			map.put("BASE_PTP_NAME", ptpNameStringFromSource);
			map.put("VCG_PTP_ID", vcgPtpId);
			map.put("VCG_PTP_NAME", vcgPtpName);
			map.put("BINDING_PTP_ID", bindingPtpId);
			map.put("BINDING_PTP_NAME", bindingPtpName);
			map.put("BASE_SDH_CTP_ID", sdhCtpId);
			map.put("BASE_OTN_CTP_ID", otnCtpId);
		}
		return map;
	}

	/**
	 * @param neName
	 * @param ip
	 * @param model
	 */
	@IMethodLog(desc = "DataCollectService：Sdh Crs信息表对象构建")
	private Map crossConnectModelToSdhTable(CrossConnectModel model,
			int emsConnectionId, Integer neId, int aEndPtpId, int aEndCtpId,
			int zEndPtpId, int zEndCtpId, String rate,boolean isUpdate) {

		Map map = new HashMap();
		// 基础信息
		map.put("ACTIVE", model.isActive() ? DataCollectDefine.TRUE
				: DataCollectDefine.FALSE);
		map.put("CC_TYPE", model.getCcType());
		map.put("CC_NAME", "");
		map.put("DIRECTION", model.getDirection());
		map.put("A_END_PTP", aEndPtpId);
		map.put("A_END_CTP", aEndCtpId);
		map.put("Z_END_PTP", zEndPtpId);
		map.put("Z_END_CTP", zEndCtpId);
		map.put("RATE", rate);
		map.put("IS_IN_CIRCUIT", DataCollectDefine.FALSE);
		map.put("CHANGE_STATE", null);
		//设置默认值为0
		map.put("CIRCUIT_COUNT", 0);
		map.put("IS_VIRTUAL", DataCollectDefine.FALSE);
		map.put("IS_USE_CREATE", null);
		map.put("PARENT_ID", null);

		map.put("IS_DEL", DataCollectDefine.FALSE);

		if (isUpdate) {
			// 更新信息
			// map.put("UPDATE_TIME", new Date());
		} else {
			// 新增信息
			map.put("BASE_SDH_CRS_ID", null);
			map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
			map.put("BASE_NE_ID", neId);
			map.put("CREATE_TIME", new Date());
			// map.put("UPDATE_TIME", null);
		}
		return map;
	}

	/**
	 * @param neName
	 * @param ip
	 * @param model
	 */
	@IMethodLog(desc = "DataCollectService：Otn Crs信息表对象构建")
	private Map crossConnectModelToOtnTable(CrossConnectModel model,
			int emsConnectionId, Integer neId, int aEndPtpId, int aEndCtpId,
			int zEndPtpId, int zEndCtpId, String aEndCtpValue,
			String zEndCtpValue, int type, boolean isUpdate) {
		//初始化参数
		Map map = initOtnCrsParam();
		// 基础信息
		map.put("ACTIVE", model.isActive() ? DataCollectDefine.TRUE
				: DataCollectDefine.FALSE);
		map.put("CC_TYPE", model.getCcType());
		map.put("CC_NAME", "");
		map.put("DIRECTION", model.getDirection());

		map.put("A_END_PTP", aEndPtpId);
		map.put("A_END_CTP", aEndCtpId);

		map.put("Z_END_PTP", zEndPtpId);
		map.put("Z_END_CTP", zEndCtpId);

		map.put("IS_FIX", model.isFixed() ? DataCollectDefine.TRUE
				: DataCollectDefine.FALSE);
		
		switch (type) {

		case DataCollectDefine.NMS_TYPE_T2000_FLAG:
		case DataCollectDefine.NMS_TYPE_U2000_FLAG:
			// HW_CTP_OS
			map.put("A_OS", nameUtil.getEquipmentNoFromTargetName(aEndCtpValue,
					DataCollectDefine.HW.HW_CTP_OS));
			map.put("Z_OS", nameUtil.getEquipmentNoFromTargetName(zEndCtpValue,
					DataCollectDefine.HW.HW_CTP_OS));
			// HW_CTP_OTS
			map.put("A_OTS", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.HW.HW_CTP_OTS));
			map.put("Z_OTS", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.HW.HW_CTP_OTS));
			// HW_CTP_OMS
			map.put("A_OMS", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.HW.HW_CTP_OMS));
			map.put("Z_OMS", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.HW.HW_CTP_OMS));
			// HW_CTP_OCH
			map.put("A_OCH", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.HW.HW_CTP_OCH));
			map.put("Z_OCH", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.HW.HW_CTP_OCH));
			// HW_CTP_ODU0
			map.put("A_ODU0", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.HW.HW_CTP_ODU0));
			map.put("Z_ODU0", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.HW.HW_CTP_ODU0));
			// HW_CTP_ODU1
			map.put("A_ODU1", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.HW.HW_CTP_ODU1));
			map.put("Z_ODU1", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.HW.HW_CTP_ODU1));
			// HW_CTP_ODU2
			map.put("A_ODU2", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.HW.HW_CTP_ODU2));
			map.put("Z_ODU2", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.HW.HW_CTP_ODU2));
			// HW_CTP_ODU3
			map.put("A_ODU3", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.HW.HW_CTP_ODU3));
			map.put("Z_ODU3", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.HW.HW_CTP_ODU3));
			// HW_CTP_OTU0
			map.put("A_OTU0", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.HW.HW_CTP_OTU0));
			map.put("Z_OTU0", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.HW.HW_CTP_OTU0));
			// HW_CTP_OTU1
			map.put("A_OTU1", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.HW.HW_CTP_OTU1));
			map.put("Z_OTU1", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.HW.HW_CTP_OTU1));
			// HW_CTP_OTU2
			map.put("A_OTU2", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.HW.HW_CTP_OTU2));
			map.put("Z_OTU2", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.HW.HW_CTP_OTU2));
			// HW_CTP_OTU3
			map.put("A_OTU3", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.HW.HW_CTP_OTU3));
			map.put("Z_OTU3", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.HW.HW_CTP_OTU3));
			// HW_CTP_DSR
			map.put("A_DSR", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.HW.HW_CTP_DSR));
			map.put("Z_DSR", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.HW.HW_CTP_DSR));
			//
			map.put("A_OAC_TYPE", null);
			map.put("Z_OAC_TYPE", null);
			//
			map.put("A_OAC_VALUE", null);
			map.put("Z_OAC_VALUE", null);

			break;

		case DataCollectDefine.NMS_TYPE_E300_FLAG:
		case DataCollectDefine.NMS_TYPE_U31_FLAG:
			//查看了下原始数据，貌似和华为一致
			// ZTE_CTP_OS
			map.put("A_OS", nameUtil.getEquipmentNoFromTargetName(aEndCtpValue,
					DataCollectDefine.ZTE.ZTE_CTP_OS));
			map.put("Z_OS", nameUtil.getEquipmentNoFromTargetName(zEndCtpValue,
					DataCollectDefine.ZTE.ZTE_CTP_OS));
			// ZTE_CTP_OTS
			map.put("A_OTS", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_OTS));
			map.put("Z_OTS", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_OTS));
			// ZTE_CTP_OMS
			map.put("A_OMS", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_OMS));
			map.put("Z_OMS", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_OMS));
			// ZTE_CTP_OCH
			map.put("A_OCH", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_OCH));
			map.put("Z_OCH", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_OCH));
			// ZTE_CTP_ODU0
			//whh研究规律 20140708
			//a端
			if(aEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_ODU0)){
			map.put("A_ODU0", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_ODU0));
			}else if(aEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_RS4)){
				map.put("A_ODU0", nameUtil.getEquipmentNoFromTargetName(
						aEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_RS4));
			}else if(aEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_RS1)){
				map.put("A_ODU0", nameUtil.getEquipmentNoFromTargetName(
						aEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_RS1));
			}else if(aEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_GE)
					&&!aEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_10GE)){
				map.put("A_ODU0", nameUtil.getEquipmentNoFromTargetName(
						aEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_GE));
			}else{
				map.put("A_ODU0", "");
			}
			//z端
			if(zEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_ODU0)){
			map.put("Z_ODU0", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_ODU0));
			}else if(zEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_RS4)){
				map.put("Z_ODU0", nameUtil.getEquipmentNoFromTargetName(
						zEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_RS4));
			}else if(zEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_RS1)){
				map.put("Z_ODU0", nameUtil.getEquipmentNoFromTargetName(
						zEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_RS1));
			}else if(zEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_GE)
					&&!zEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_10GE)){
				map.put("Z_ODU0", nameUtil.getEquipmentNoFromTargetName(
						zEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_GE));
			}else{
				map.put("Z_ODU0", "");
			}
			
			// ZTE_CTP_ODU1
			//whh研究规律 20140708
			//a端
			if(aEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_ODU1)){
			map.put("A_ODU1", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_ODU1));
			}else if(aEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_RS16)){
				map.put("A_ODU1", nameUtil.getEquipmentNoFromTargetName(
						aEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_RS16));
			}else{
				map.put("A_ODU1", "");
			}
			//z端
			if(zEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_ODU1)){
			map.put("Z_ODU1", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_ODU1));
			}else if(zEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_RS16)){
				map.put("Z_ODU1", nameUtil.getEquipmentNoFromTargetName(
						zEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_RS16));
			}else{
				map.put("Z_ODU1", "");
			}
			
			// ZTE_CTP_ODU2
			//whh研究规律 20140708
			//a端
			if(aEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_ODU2)){
			map.put("A_ODU2", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_ODU2));
			}else if(aEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_RS64)){
				map.put("A_ODU2", nameUtil.getEquipmentNoFromTargetName(
						aEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_RS64));
			}else if(aEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_10GE)){
				map.put("A_ODU2", nameUtil.getEquipmentNoFromTargetName(
						aEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_10GE));
			}else{
				map.put("A_ODU2", "");
			}
			//z端
			if(zEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_ODU2)){
			map.put("Z_ODU2", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_ODU2));
			}else if(zEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_RS64)){
				map.put("Z_ODU2", nameUtil.getEquipmentNoFromTargetName(
						zEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_RS64));
			}else if(zEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_10GE)){
				map.put("Z_ODU2", nameUtil.getEquipmentNoFromTargetName(
						zEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_10GE));
			}else{
				map.put("Z_ODU2", "");
			}
			
			// ZTE_CTP_ODU3
			//whh研究规律 20140708
			//a端
			if(aEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_ODU3)){
			map.put("A_ODU3", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_ODU3));
			}else if(aEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_RS256)){
				map.put("A_ODU3", nameUtil.getEquipmentNoFromTargetName(
						aEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_RS256));
			}else{
				map.put("A_ODU3", "");
			}
			//z端
			if(zEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_ODU3)){
			map.put("Z_ODU3", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_ODU3));
			}else if(zEndCtpValue.contains(DataCollectDefine.ZTE.ZTE_CTP_RS256)){
				map.put("Z_ODU3", nameUtil.getEquipmentNoFromTargetName(
						zEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_RS256));
			}else{
				map.put("Z_ODU3", "");
			}
			
			// ZTE_CTP_OTU0
			map.put("A_OTU0", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_OTU0));
			map.put("Z_OTU0", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_OTU0));
			// ZTE_CTP_OTU1
			map.put("A_OTU1", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_OTU1));
			map.put("Z_OTU1", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_OTU1));
			// ZTE_CTP_OTU2
			map.put("A_OTU2", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_OTU2));
			map.put("Z_OTU2", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_OTU2));
			// ZTE_CTP_OTU3
			map.put("A_OTU3", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_OTU3));
			map.put("Z_OTU3", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_OTU3));
			// ZTE_CTP_DSR
			map.put("A_DSR", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_DSR));
			map.put("Z_DSR", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.ZTE.ZTE_CTP_DSR));
			//
			map.put("A_OAC_TYPE", null);
			map.put("Z_OAC_TYPE", null);
			//
			map.put("A_OAC_VALUE", null);
			map.put("Z_OAC_VALUE", null);
			

			break;
		case DataCollectDefine.NMS_TYPE_LUCENT_OMS_FLAG:
			// FIXME 朗讯 交叉连接 待添加
			break;
		case DataCollectDefine.NMS_TYPE_OTNM2000_FLAG:
			// FIM_CTP_OS
			map.put("A_OS", nameUtil.getEquipmentNoFromTargetName(aEndCtpValue,
					DataCollectDefine.FIM.FIM_CTP_OS));
			map.put("Z_OS", nameUtil.getEquipmentNoFromTargetName(zEndCtpValue,
					DataCollectDefine.FIM.FIM_CTP_OS));
			// FIM_CTP_OTS
			map.put("A_OTS", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.FIM.FIM_CTP_OTS));
			map.put("Z_OTS", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.FIM.FIM_CTP_OTS));
			// FIM_CTP_OMS
			map.put("A_OMS", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.FIM.FIM_CTP_OMS));
			map.put("Z_OMS", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.FIM.FIM_CTP_OMS));
			// FIM_CTP_OCH
			map.put("A_OCH", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.FIM.FIM_CTP_OCH));
			map.put("Z_OCH", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.FIM.FIM_CTP_OCH));
			// FIM_CTP_ODU0
			map.put("A_ODU0", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.FIM.FIM_CTP_ODU0));
			map.put("Z_ODU0", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.FIM.FIM_CTP_ODU0));
			// FIM_CTP_ODU1
			map.put("A_ODU1", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.FIM.FIM_CTP_ODU1));
			map.put("Z_ODU1", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.FIM.FIM_CTP_ODU1));
			// FIM_CTP_ODU2
			map.put("A_ODU2", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.FIM.FIM_CTP_ODU2));
			map.put("Z_ODU2", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.FIM.FIM_CTP_ODU2));
			// FIM_CTP_ODU3
			map.put("A_ODU3", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.FIM.FIM_CTP_ODU3));
			map.put("Z_ODU3", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.FIM.FIM_CTP_ODU3));
			// FIM_CTP_OTU0
			map.put("A_OTU0", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.FIM.FIM_CTP_OTU0));
			map.put("Z_OTU0", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.FIM.FIM_CTP_OTU0));
			// FIM_CTP_OTU1
			map.put("A_OTU1", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.FIM.FIM_CTP_OTU1));
			map.put("Z_OTU1", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.FIM.FIM_CTP_OTU1));
			// FIM_CTP_OTU2
			map.put("A_OTU2", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.FIM.FIM_CTP_OTU2));
			map.put("Z_OTU2", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.FIM.FIM_CTP_OTU2));
			// FIM_CTP_OTU3
			map.put("A_OTU3", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.FIM.FIM_CTP_OTU3));
			map.put("Z_OTU3", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.FIM.FIM_CTP_OTU3));
			// FIM_CTP_DSR
			map.put("A_DSR", nameUtil.getEquipmentNoFromTargetName(
					aEndCtpValue, DataCollectDefine.FIM.FIM_CTP_DSR));
			map.put("Z_DSR", nameUtil.getEquipmentNoFromTargetName(
					zEndCtpValue, DataCollectDefine.FIM.FIM_CTP_DSR));
			//
			map.put("A_OAC_TYPE", null);
			map.put("Z_OAC_TYPE", null);
			//
			map.put("A_OAC_VALUE", null);
			map.put("Z_OAC_VALUE", null);
			
			// FIXME 烽火 交叉连接 待添加
			break;
		case DataCollectDefine.NMS_TYPE_ALU_FLAG:
			// FIXME 贝尔 交叉连接 待添加
			break;
		default:
		}

		//王剑要求添加处理，如果ODU没有值把相应OTU值赋给ODU
		if(map.get("A_ODU0")!= null &&map.get("A_ODU0").toString().isEmpty()){
			map.put("A_ODU0", map.get("A_OTU0"));
		}
		if(map.get("A_ODU1")!= null &&map.get("A_ODU1").toString().isEmpty()){
			map.put("A_ODU1", map.get("A_OTU1"));
		}
		if(map.get("A_ODU2")!= null &&map.get("A_ODU2").toString().isEmpty()){
			map.put("A_ODU2", map.get("A_OTU2"));
		}
		if(map.get("A_ODU3")!= null &&map.get("A_ODU3").toString().isEmpty()){
			map.put("A_ODU3", map.get("A_OTU3"));
		}
		if(map.get("Z_ODU0")!= null &&map.get("Z_ODU0").toString().isEmpty()){
			map.put("Z_ODU0", map.get("Z_OTU0"));
		}
		if(map.get("Z_ODU1")!= null &&map.get("Z_ODU1").toString().isEmpty()){
			map.put("Z_ODU1", map.get("Z_OTU1"));
		}
		if(map.get("Z_ODU2")!= null &&map.get("Z_ODU2").toString().isEmpty()){
			map.put("Z_ODU2", map.get("Z_OTU2"));
		}
		if(map.get("Z_ODU3")!= null &&map.get("Z_ODU3").toString().isEmpty()){
			map.put("Z_ODU3", map.get("Z_OTU3"));
		}
		
		String aType = "";
		if (map.get("A_OS") != null && !map.get("A_OS").toString().isEmpty()) {
			aType = aType+"A_OS"+",";
		} if (map.get("A_OTS") != null
				&& !map.get("A_OTS").toString().isEmpty()) {
			aType = aType+"A_OTS"+",";
		} if (map.get("A_OMS") != null
				&& !map.get("A_OMS").toString().isEmpty()) {
			aType = aType+"A_OMS"+",";
		} if (map.get("A_OCH") != null
				&& !map.get("A_OCH").toString().isEmpty()) {
			aType = aType+"A_OCH"+",";
		} if (map.get("A_ODU0") != null
				&& !map.get("A_ODU0").toString().isEmpty()) {
			aType = aType+"A_ODU0"+",";
		} if (map.get("A_ODU1") != null
				&& !map.get("A_ODU1").toString().isEmpty()) {
			aType = aType+"A_ODU1"+",";
		} if (map.get("A_ODU2") != null
				&& !map.get("A_ODU2").toString().isEmpty()) {
			aType = aType+"A_ODU2"+",";
		} if (map.get("A_ODU3") != null
				&& !map.get("A_ODU3").toString().isEmpty()) {
			aType = aType+"A_ODU3"+",";
		}
		//不拼接OTU值
//		if (map.get("A_OTU0") != null
//				&& !map.get("A_OTU0").toString().isEmpty()) {
//			aType = aType+"A_OTU0"+",";
//		} if (map.get("A_OTU1") != null
//				&& !map.get("A_OTU1").toString().isEmpty()) {
//			aType = aType+"A_OTU1"+",";
//		} if (map.get("A_OTU2") != null
//				&& !map.get("A_OTU2").toString().isEmpty()) {
//			aType = aType+"A_OTU2"+",";
//		} if (map.get("A_OTU3") != null
//				&& !map.get("A_OTU3").toString().isEmpty()) {
//			aType = aType+"A_OTU3"+",";
//		} 
		if (map.get("A_DSR") != null
				&& !map.get("A_DSR").toString().isEmpty()) {
			aType = aType+"A_DSR"+",";
		} if (map.get("A_OAC_TYPE") != null
				&& !map.get("A_OAC_TYPE").toString().isEmpty()) {
			aType = aType+"A_OAC_TYPE"+",";
		}
		if(aType.endsWith(",")){
			aType = aType.substring(0,aType.length()-1);
		}
		map.put("A_TYPE", aType);

		String zType = "";
		if (map.get("Z_OS") != null && !map.get("Z_OS").toString().isEmpty()) {
			zType = zType+"Z_OS"+",";
		} if (map.get("Z_OTS") != null
				&& !map.get("Z_OTS").toString().isEmpty()) {
			zType = zType+"Z_OTS"+",";
		} if (map.get("Z_OMS") != null
				&& !map.get("Z_OMS").toString().isEmpty()) {
			zType = zType+"Z_OMS"+",";
		} if (map.get("Z_OCH") != null
				&& !map.get("Z_OCH").toString().isEmpty()) {
			zType = zType+"Z_OCH"+",";
		} if (map.get("Z_ODU0") != null
				&& !map.get("Z_ODU0").toString().isEmpty()) {
			zType = zType+"Z_ODU0"+",";
		} if (map.get("Z_ODU1") != null
				&& !map.get("Z_ODU1").toString().isEmpty()) {
			zType = zType+"Z_ODU1"+",";
		} if (map.get("Z_ODU2") != null
				&& !map.get("Z_ODU2").toString().isEmpty()) {
			zType = zType+"Z_ODU2"+",";
		} if (map.get("Z_ODU3") != null
				&& !map.get("Z_ODU3").toString().isEmpty()) {
			zType = zType+"Z_ODU3"+",";
		}
		//不拼接OTU值
//		if (map.get("Z_OTU0") != null
//				&& !map.get("Z_OTU0").toString().isEmpty()) {
//			zType = zType+"Z_OTU0"+",";
//		} if (map.get("Z_OTU1") != null
//				&& !map.get("Z_OTU1").toString().isEmpty()) {
//			zType = zType+"Z_OTU1"+",";
//		} if (map.get("Z_OTU2") != null
//				&& !map.get("Z_OTU2").toString().isEmpty()) {
//			zType = zType+"Z_OTU2"+",";
//		} if (map.get("Z_OTU3") != null
//				&& !map.get("Z_OTU3").toString().isEmpty()) {
//			zType = zType+"Z_OTU3"+",";
//		}
		if (map.get("Z_DSR") != null
				&& !map.get("Z_DSR").toString().isEmpty()) {
			zType = zType+"Z_DSR"+",";
		} if (map.get("Z_OAC_TYPE") != null
				&& !map.get("Z_OAC_TYPE").toString().isEmpty()) {
			zType = zType+"Z_OAC_TYPE"+",";
		}
		if(zType.endsWith(",")){
			zType = zType.substring(0,zType.length()-1);
		}
		map.put("Z_TYPE", zType);

		map.put("CLIENT_TYPE", model.getClientType());

		map.put("CLIENT_RATE", model.getClientRate());

		map.put("RATE", "");
		map.put("PARENT_ID", null);
		map.put("IS_IN_CIRCUIT", DataCollectDefine.FALSE);
		map.put("CHANGE_STATE", null);
		//设置默认值为0
		map.put("CIRCUIT_COUNT", 0);
		map.put("IS_VIRTUAL", DataCollectDefine.FALSE);
		map.put("IS_DEL", DataCollectDefine.FALSE);

		if (isUpdate) {
			// 更新信息
			// map.put("UPDATE_TIME", new Date());
		} else {
			// 新增信息
			map.put("BASE_OTN_CRS_ID", null);
			map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
			map.put("BASE_NE_ID", neId);
			map.put("CREATE_TIME", new Date());
			// map.put("UPDATE_TIME", null);
		}
		return map;
	}
	
	/**
	 * @param neName
	 * @param ip
	 * @param model
	 */
	@IMethodLog(desc = "DataCollectService：T_BASE_VB 信息表对象构建")
	private Map vbModelToTable(VirtualBridgeModel model,
			int emsConnectionId, Integer neId, boolean isUpdate) {

		Map map = new HashMap();
		// 基础信息
		map.put("NAME", model.getNameString());
		map.put("USER_LABEL", model.getUserLabel());
		map.put("NATIVE_EMS_NAME", model.getNativeEMSName());
		map.put("DISPLAY_NAME", map.get("NATIVE_EMS_NAME"));
		map.put("OWNER", model.getOwner());

		map.put("VID", model.getVid());
		map.put("STP_MOD", model.getStpMode());
		map.put("BRIDGE_PRIORITY", model.getBridgePriority());
		map.put("MAC_AGING", model.getMacAging());
		map.put("HELLO_TIME", model.getHelloTime());
		map.put("MAX_AGE", model.getMaxAge());
		map.put("FOREARD_DELAY", model.getForwardDelay());
		map.put("IS_DEL", DataCollectDefine.FALSE);

		if (isUpdate) {
			// 更新信息
			// map.put("UPDATE_TIME", new Date());
		} else {
			// 新增信息
			map.put("BASE_VB_ID", null);
			map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
			map.put("BASE_NE_ID", neId);
			map.put("CREATE_TIME", new Date());
			// map.put("UPDATE_TIME", null);
		}
		return map;
	}
	
	//初始化otn crs参数，塞入null
	private Map initOtnCrsParam(){
		
		Map map = new HashMap();
		
		map.put("A_OS", null);
		map.put("Z_OS", null);

		map.put("A_OTS", null);
		map.put("Z_OTS", null);

		map.put("A_OMS", null);
		map.put("Z_OMS", null);

		map.put("A_OCH", null);
		map.put("Z_OCH", null);

		map.put("A_ODU0", null);
		map.put("Z_ODU0", null);

		map.put("A_ODU1", null);
		map.put("Z_ODU1", null);

		map.put("A_ODU2", null);
		map.put("Z_ODU2", null);

		map.put("A_ODU3", null);
		map.put("Z_ODU3", null);

		map.put("A_OTU0", null);
		map.put("Z_OTU0", null);

		map.put("A_OTU1", null);
		map.put("Z_OTU1", null);

		map.put("A_OTU2", null);
		map.put("Z_OTU2", null);

		map.put("A_OTU3", null);
		map.put("Z_OTU3", null);

		map.put("A_DSR", null);
		map.put("Z_DSR", null);
		//
		map.put("A_OAC_TYPE", null);
		map.put("Z_OAC_TYPE", null);
		//
		map.put("A_OAC_VALUE", null);
		map.put("Z_OAC_VALUE", null);
		
		return map;
	}

	// 获取交叉连接参数
	private Map<String, String> getOtnCrsParam(String ctpValue, int type) {

		Map<String, String> params = new HashMap<String, String>();

		switch (type) {

		case DataCollectDefine.NMS_TYPE_T2000_FLAG:
		case DataCollectDefine.NMS_TYPE_U2000_FLAG:
			// HW_CTP_OS
			params.put(DataCollectDefine.HW.HW_CTP_OS, nameUtil
					.getEquipmentNoFromTargetName(ctpValue,
							DataCollectDefine.HW.HW_CTP_OS));
			// HW_CTP_OTS
			params.put(DataCollectDefine.HW.HW_CTP_OTS, nameUtil
					.getEquipmentNoFromTargetName(ctpValue,
							DataCollectDefine.HW.HW_CTP_OTS));
			// HW_CTP_OMS
			params.put(DataCollectDefine.HW.HW_CTP_OMS, nameUtil
					.getEquipmentNoFromTargetName(ctpValue,
							DataCollectDefine.HW.HW_CTP_OMS));
			// HW_CTP_OCH
			params.put(DataCollectDefine.HW.HW_CTP_OCH, nameUtil
					.getEquipmentNoFromTargetName(ctpValue,
							DataCollectDefine.HW.HW_CTP_OCH));
			// HW_CTP_ODU0
			params.put(DataCollectDefine.HW.HW_CTP_ODU0, nameUtil
					.getEquipmentNoFromTargetName(ctpValue,
							DataCollectDefine.HW.HW_CTP_ODU0));
			// HW_CTP_ODU1
			params.put(DataCollectDefine.HW.HW_CTP_ODU1, nameUtil
					.getEquipmentNoFromTargetName(ctpValue,
							DataCollectDefine.HW.HW_CTP_ODU1));
			// HW_CTP_ODU2
			params.put(DataCollectDefine.HW.HW_CTP_ODU2, nameUtil
					.getEquipmentNoFromTargetName(ctpValue,
							DataCollectDefine.HW.HW_CTP_ODU2));
			// HW_CTP_ODU3
			params.put(DataCollectDefine.HW.HW_CTP_ODU3, nameUtil
					.getEquipmentNoFromTargetName(ctpValue,
							DataCollectDefine.HW.HW_CTP_ODU3));
			// HW_CTP_OTU0
			params.put(DataCollectDefine.HW.HW_CTP_OTU0, nameUtil
					.getEquipmentNoFromTargetName(ctpValue,
							DataCollectDefine.HW.HW_CTP_OTU0));
			// HW_CTP_OTU1
			params.put(DataCollectDefine.HW.HW_CTP_OTU1, nameUtil
					.getEquipmentNoFromTargetName(ctpValue,
							DataCollectDefine.HW.HW_CTP_OTU1));
			// HW_CTP_OTU2
			params.put(DataCollectDefine.HW.HW_CTP_OTU2, nameUtil
					.getEquipmentNoFromTargetName(ctpValue,
							DataCollectDefine.HW.HW_CTP_OTU2));
			// HW_CTP_OTU3
			params.put(DataCollectDefine.HW.HW_CTP_OTU3, nameUtil
					.getEquipmentNoFromTargetName(ctpValue,
							DataCollectDefine.HW.HW_CTP_OTU3));
			// HW_CTP_DSR
			params.put(DataCollectDefine.HW.HW_CTP_DSR, nameUtil
					.getEquipmentNoFromTargetName(ctpValue,
							DataCollectDefine.HW.HW_CTP_DSR));

			break;
		case DataCollectDefine.NMS_TYPE_E300_FLAG:
		case DataCollectDefine.NMS_TYPE_U31_FLAG:
			// FIXME 中兴OTN 交叉连接 待添加
			break;
		case DataCollectDefine.NMS_TYPE_LUCENT_OMS_FLAG:
			// FIXME 朗讯OTN 交叉连接 待添加
			break;
		case DataCollectDefine.NMS_TYPE_OTNM2000_FLAG:
			// FIXME 烽火OTN 交叉连接 待添加
			break;
		case DataCollectDefine.NMS_TYPE_ALU_FLAG:
			// FIXME 贝尔OTN 交叉连接 待添加
			break;
		default:
		}
		return params;

	}

	/**
	 * 插入板卡保护列表
	 * 
	 * @param eProtectionGroupId
	 * @param isProtected
	 * @param unitList
	 */
	private void insertProtectUnitList(int neId, int eProtectionGroupId,
			boolean isProtected, NameAndStringValue_T[][] unitList) {

		Map unit;
		String rackNo;
		String shelfNo;
		String slotNo;
		Map equipNo = new HashMap();
		Map protectUnit = new HashMap();

		for (NameAndStringValue_T[] unitName : unitList) {

			if (unitName.length > 0) {
				rackNo = nameUtil.getEquipmentNoFromTargetName(unitName,
						DataCollectDefine.COMMON.RACK);
				shelfNo = nameUtil.getEquipmentNoFromTargetName(unitName,
						DataCollectDefine.COMMON.SHELF);
				slotNo = nameUtil.getEquipmentNoFromTargetName(unitName,
						DataCollectDefine.COMMON.SLOT);

				equipNo.put("rackNo", rackNo);
				equipNo.put("shelfNo", shelfNo);
				equipNo.put("slotNo", slotNo);

				unit = dataCollectMapper.selectEquipByEquipNo("T_BASE_UNIT",
						neId, equipNo, DataCollectDefine.FALSE);

				protectUnit.put("BASE_E_PGP_TP_LIST_ID", null);
				protectUnit.put("BASE_E_PRO_GROUP_ID", eProtectionGroupId);
				protectUnit.put("BASE_UNIT_ID", unit.get("BASE_UNIT_ID"));
				protectUnit.put("TYPE", isProtected ? DataCollectDefine.TRUE
						: DataCollectDefine.FALSE);

				dataCollectMapper.insertProtectUnit(protectUnit);
			}
		}

	}

	/**
	 * 插入vb ptp列表
	 * 
	 * @param protectionGroupId
	 * @param pgpList
	 */
	private void insertPtpListForVB(int vbId, int neId, List<TerminationPointModel> ptpList) {

		Map ptp = null;
		List<Map> vbPtpList = new ArrayList<Map>();

		for (TerminationPointModel tp : ptpList) {
		
			Map vbPtp = new HashMap();

			String ptpNameString = nameUtil.decompositionName(nameUtil.getPtpNameFromCtpName(tp.getName()));

			ptp = dataCollectMapper.selectPtpByNeIdAndPtpName(neId,
					ptpNameString);
			
			//only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
			if(ptp == null && ptpNameString.contains("ptptype")){
				ptp = selectPtpForE300(neId,ptpNameString);
			}

			String port = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
					DataCollectDefine.COMMON.PORT);
			String pwId = "";
			if(port.startsWith("125")&&port.split("_").length>1){
				pwId = port.split("_")[1];
			}
			
			vbPtp.put("BASE_VB_LIST_ID", null);
			vbPtp.put("BASE_VB_ID", vbId);
			vbPtp.put("BASE_NE_ID", neId);
			if(ptp!=null){
				vbPtp.put("BASE_PTP_ID", ptp.get("BASE_PTP_ID"));
			}else{
				vbPtp.put("BASE_PTP_ID", null);
			}
			vbPtp.put("PTP_NAME", ptpNameString);
			vbPtp.put("PTN_PWID", pwId);
			vbPtp.put("TP_MAPPING_MODE", tp.getTpMappingMode());

			vbPtpList.add(vbPtp);
		}
		if(vbPtpList.size()>0){
			dataCollectMapper.insertVBPtpBatch(vbPtpList);
		}
	}

	/**
	 * 插入ptp保护列表
	 * 
	 * @param wdmProtectionGroupId
	 * @param pgpList
	 */
	private void insertWdmProtectPtpList(int neId, int wdmProtectionGroupId,
			NameAndStringValue_T[][] pgpList) {

		Map ptp;
		Map wdmProtectPtp = new HashMap();

		for (NameAndStringValue_T[] ptpName : pgpList) {

			String ptpNameString = nameUtil.decompositionName(ptpName);

			ptp = dataCollectMapper.selectPtpByNeIdAndPtpName(neId,
					ptpNameString);
			
			//only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
			if(ptp == null && ptpNameString.contains("ptptype")){
				ptp = selectPtpForE300(neId,ptpNameString);
			}
			//如果ptp为空，查找相应的FTP/PTP
			if(ptp == null){
				NameAndStringValue_T[] ptpNameTemp = ptpName;
				
				if(ptpNameTemp[2].name.equals(DataCollectDefine.COMMON.PTP)){
					ptpNameTemp[2].name = DataCollectDefine.COMMON.FTP;
				}else if(ptpNameTemp[2].name.equals(DataCollectDefine.COMMON.FTP)){
					ptpNameTemp[2].name = DataCollectDefine.COMMON.PTP;
				}
				ptpNameString = nameUtil.decompositionName(ptpNameTemp);

				ptp = dataCollectMapper.selectPtpByNeIdAndPtpName(neId,
						ptpNameString);
			}

			ptp.put("IS_PROTECTED", DataCollectDefine.TRUE);
			// 更新ptp保护标识
			dataCollectMapper.updatePtpById(ptp);

			wdmProtectPtp.put("BASE_WDM_PGP_TP_LIST_ID", null);
			wdmProtectPtp.put("BASE_WDM_PRO_GROUP_ID", wdmProtectionGroupId);
			wdmProtectPtp.put("BASE_PTP_ID", ptp.get("BASE_PTP_ID"));

			dataCollectMapper.insertWdmProtectPtp(wdmProtectPtp);
		}

	}

	
	/**
	 * 插入ptp保护列表
	 * 
	 * @param protectionGroupId
	 * @param pgpList
	 */
	private void insertProtectPtpList(int protectionGroupType, int factory, int neId, int protectionGroupId,
			NameAndStringValue_T[][] pgpList) {

		Map ptp;
		Map protectPtp = new HashMap();
		boolean needUpdateCrs = false;
		
		switch (factory) {
		case DataCollectDefine.FACTORY_HW_FLAG:
			//HW 1+1 MSP保护 
			if(protectionGroupType == 0){
				needUpdateCrs = true;
			}
			break;
		case DataCollectDefine.FACTORY_ZTE_FLAG:
		case DataCollectDefine.FACTORY_LUCENT_FLAG:
		case DataCollectDefine.FACTORY_FIBERHOME_FLAG:
		case DataCollectDefine.FACTORY_ALU_FLAG:
		case DataCollectDefine.FACTORY_FUJITSU_FLAG:
			break;
		default:
			break;
		}
		
		for (int i=0;i<pgpList.length;i++) {

			String ptpNameString = nameUtil.decompositionName(pgpList[i]);

			ptp = dataCollectMapper.selectPtpByNeIdAndPtpName(neId,
					ptpNameString);
			
			//only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
			if(ptp == null && ptpNameString.contains("ptptype")){
				ptp = selectPtpForE300(neId,ptpNameString);
			}

			ptp.put("IS_PROTECTED", DataCollectDefine.TRUE);
			// 更新ptp保护标识
			dataCollectMapper.updatePtpById(ptp);

			protectPtp.put("BASE_PRO_LIST_ID", null);
			protectPtp.put("BASE_PRO_GROUP_ID", protectionGroupId);
			protectPtp.put("BASE_PTP_ID", ptp.get("BASE_PTP_ID"));
			protectPtp.put("BASE_SDH_CTP_ID", null);
			protectPtp.put("BASE_OTN_CTP_ID", null);
			//最后一个ptp，标记NEED_UPDATE_CRS为true
			if(needUpdateCrs && i==pgpList.length-1){
				protectPtp.put("NEED_UPDATE_CRS", DataCollectDefine.TRUE);
			}else{
				protectPtp.put("NEED_UPDATE_CRS", DataCollectDefine.FALSE);
			}

			dataCollectMapper.insertProtectPtp(protectPtp);
		}

	}

	// granularity转换
	private String[] granularityTransform(int[] pmGranularityList, int type) {

		String[] granularityList = null;
		List<String> tempGranularityList = null;
		switch (type) {
		case DataCollectDefine.NMS_TYPE_T2000_FLAG:
		case DataCollectDefine.NMS_TYPE_U2000_FLAG:
		case DataCollectDefine.NMS_TYPE_E300_FLAG:
		case DataCollectDefine.NMS_TYPE_U31_FLAG:
		case DataCollectDefine.NMS_TYPE_OTNM2000_FLAG:
		case DataCollectDefine.NMS_TYPE_LUCENT_OMS_FLAG:
		case DataCollectDefine.NMS_TYPE_ALU_FLAG:
		default:
			// 转换周期
			tempGranularityList = new ArrayList<String>();
			for (Integer granularity : pmGranularityList) {
				tempGranularityList.add(DataCollectDefine.COMMON.GRANULARITY
						.get(granularity.intValue()));
			}
			granularityList = new String[tempGranularityList.size()];
			granularityList = (String[]) tempGranularityList
					.toArray(granularityList);
			break;
		}
		return granularityList;
	}

	// location转换
	private String[] pmLocationTransform(int[] pmLocationList, int type) {

		String[] locationList = null;
		List<String> tempPmLocationList = null;
		switch (type) {
		case DataCollectDefine.NMS_TYPE_E300_FLAG:
		case DataCollectDefine.NMS_TYPE_U31_FLAG:
			tempPmLocationList = new ArrayList<String>();
			for (Integer location : pmLocationList) {
				String loc = DataCollectDefine.ZTE.LOCATION.get(location
						.intValue());
				if (loc != null) {
					tempPmLocationList.add(loc);
				}
			}
			locationList = new String[tempPmLocationList.size()];
			locationList = (String[]) tempPmLocationList.toArray(locationList);
			break;
		//贝尔不能添加此参数 不然取不到数据
		case DataCollectDefine.NMS_TYPE_ALU_FLAG:
			locationList = new String[]{};
			break;
		case DataCollectDefine.NMS_TYPE_T2000_FLAG:
		case DataCollectDefine.NMS_TYPE_U2000_FLAG:
		case DataCollectDefine.NMS_TYPE_LUCENT_OMS_FLAG:
		case DataCollectDefine.NMS_TYPE_OTNM2000_FLAG:
		default:
			tempPmLocationList = new ArrayList<String>();
			for (Integer location : pmLocationList) {
				String loc = DataCollectDefine.COMMON.LOCATION.get(location
						.intValue());
				if (loc != null) {
					tempPmLocationList.add(loc);
				}
			}
			locationList = new String[tempPmLocationList.size()];
			locationList = (String[]) tempPmLocationList.toArray(locationList);
			break;
		}
		return locationList;
	}
	
	
	@IMethodLog(desc = "DataCollectService：生成PTP VIRTUAL对象map")
	private Map constructPtpVirtualData(int emsConnectionId, int neId,
			NameAndStringValue_T[] ptpName) {

		Map map = new HashMap();
		
		String ptpNameString = nameUtil.decompositionName(ptpName);
		String rackNo = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.COMMON.RACK);
		String shelfNo = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.COMMON.SHELF);
		String slotNo = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.COMMON.SLOT);
		String portNo = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.COMMON.PORT);
		String ptpType = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.ZTE.ZTE_PTP_TYPE);
		
		Map equipNo = new HashMap();
		equipNo.put("rackNo", rackNo);
		equipNo.put("shelfNo", shelfNo);
		equipNo.put("slotNo", slotNo);
		
		Map equip = dataCollectMapper.selectEquipByEquipNo(
				"T_BASE_UNIT", neId, equipNo,
				DataCollectDefine.FALSE);
		
		Integer id = selectPtpVirtualMinId();

		map.put("BASE_PTP_ID", id);
		map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
		map.put("BASE_NE_ID", neId);
		// 加入rackId
		map.put("BASE_RACK_ID", equip.get("BASE_RACK_ID"));
		// 加入shelfId
		map.put("BASE_SHELF_ID", equip.get("BASE_SHELF_ID"));
		// 加入slotId
		map.put("BASE_SLOT_ID", equip.get("BASE_SLOT_ID"));
		// 加入unitId
		map.put("BASE_UNIT_ID", equip.get("BASE_UNIT_ID"));
		map.put("BASE_SUB_SLOT_ID", null);
		map.put("BASE_SUB_UNIT_ID", null);
		
		map.put("PTP_TYPE", ptpType);
		map.put("RACK_NO", rackNo);
		map.put("SHELF_NO", shelfNo);
		map.put("SLOT_NO", slotNo);
		map.put("SUB_SLOT_NO", null);
		map.put("PORT_NO", portNo);
		map.put("PORT_DESC", null);
		map.put("NOTE", null);
		map.put("IS_DEL", DataCollectDefine.FALSE);
		map.put("CREATE_TIME", new Date());
		return map;
	}
	
	//获取ptp Virtual表的最小Id
	private synchronized Integer selectPtpVirtualMinId(){
		Integer id = dataCollectMapper.selectPtpVirtualMinId();
		if(id == null){
			id = -1;
		}else{
			id = id - 1;
		}
		return id;
	}
	

	@IMethodLog(desc = "DataCollectService：生成SDH CTP对象map")
	private Map constructSdhCtpData(int emsConnectionId, Integer emsType, int neId, Integer ptpId, 
			NameAndStringValue_T[] ctpName,String layerRate) {

		Map map = new HashMap();

		//没有ptpId，从ctp信息中获取
		if(ptpId == null){
			// ptp名称
			String ptpNameString = nameUtil.decompositionName(nameUtil
					.getPtpNameFromCtpName(ctpName));
			
			Map ptp = dataCollectMapper.selectPtpByNeIdAndPtpName(neId,
					ptpNameString);
			
			//only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
			if(ptp == null && ptpNameString.contains("ptptype")){
				ptp = selectPtpForE300(neId,ptpNameString);
			}
			
			//如果ptp还没有找到 返回null
			if (ptp == null) {
				// log记录 备查
				CommonException e = new CommonException(new NullPointerException(),
						MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
						"构建SDH CTP失败，对应ptp未找到，【网管ID】：" + emsConnectionId
								+ "【网元ID】：" + neId + " 【ctp名】：" + ptpNameString);
				ExceptionHandler.handleException(e);
				return null;
			}else{
				//如果ptp类型是MAC或MP 返回null 王海鸿邮件3/24 Re: 中兴E300 CTP查找PTP问题
				String ptpType = ptp.get("PTP_TYPE")!=null?ptp.get("PTP_TYPE").toString():"";
				if(DataCollectDefine.COMMON.PTP_TYPE_OTHER_MAC.equals(ptpType)||
						DataCollectDefine.COMMON.PTP_TYPE_OTHER_MP.equals(ptpType)){
					return null;
				}else{
					ptpId = Integer.valueOf(ptp.get("BASE_PTP_ID").toString());
				}
			}
			map.put("REL_PTP_TYPE", 1);
		}else{
			//关联到虚拟ptp，默认值1
			map.put("REL_PTP_TYPE", 2);
		}
		// 设置ctp Value
		String ctpValue = ctpName[3].value;

		if (ctpValue.startsWith("/")) {
			ctpValue = ctpValue.substring(1);
		}

		// 取得j k l m 参数
		String[] ctpParamter = null;
		
		if(emsType!=null){
			switch (emsType.intValue()) {
			case DataCollectDefine.NMS_TYPE_T2000_FLAG:
			case DataCollectDefine.NMS_TYPE_U2000_FLAG:
			case DataCollectDefine.NMS_TYPE_E300_FLAG:
			case DataCollectDefine.NMS_TYPE_U31_FLAG:
			case DataCollectDefine.NMS_TYPE_OTNM2000_FLAG:
				ctpParamter = FileWriterUtil.getSdhCtpParameterFromCtpName(
						ctpName[3].value).split("-");
				break;
				//朗讯暂不解析jklm
			case DataCollectDefine.NMS_TYPE_LUCENT_OMS_FLAG:
				break;
			case DataCollectDefine.NMS_TYPE_ALU_FLAG:
				String ctpParamterString = FileWriterUtil.getSdhCtpParameterFromCtpName_ALU(
						ctpName[3].value,ctpName[2].value,layerRate);
				
				if(ctpParamterString == null){
					return null;
				}else{
					// 取得j k l m 参数
					ctpParamter = ctpParamterString.split("-");
				}
				
				break;
			default:
			}
		}else{
			ctpParamter = FileWriterUtil.getSdhCtpParameterFromCtpName(
					ctpName[3].value).split("-");
		}
		

		String ctp64c = ctpParamter[0];
		String ctp16c = ctpParamter[1];
		String ctp8c = ctpParamter[2];
		String ctp4c = ctpParamter[3];

		String ctpJ = ctpParamter[4];
		String ctpK = ctpParamter[5];
		String ctpL = ctpParamter[6];
		String ctpM = ctpParamter[7];
		// 连接速率
		String connectRate = ctpParamter[8];
		// j原值
		String ctpJOriginal = ctpParamter[9];

		String displayName = ctpJOriginal + "-" + ctpK + "-" + ctpL + "-"
				+ ctpM;

		map.put("BASE_SDH_CTP_ID", null);
		map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
		map.put("BASE_NE_ID", neId);
		map.put("BASE_PTP_ID", ptpId);
		// 基础信息
		map.put("NAME", nameUtil.decompositionCtpName(ctpName));
		map.put("USER_LABEL", "");
		map.put("NATIVE_EMS_NAME", "");
		map.put("DISPLAY_NAME", displayName);
		map.put("OWNER", "");
		map.put("CONNECTION_STATE", null);
		map.put("TP_MAPPING_MODE", null);
		map.put("DIRECTION", null);
		map.put("TP_PROTECTION_ASSOCIATION", null);
		map.put("EDGE_POINT", null);
		map.put("TOP_CTP", null);
		map.put("CTP_VALUE", ctpValue);
		map.put("CTP_64C", ctp64c);
		map.put("CTP_16C", ctp16c);
		map.put("CTP_8C", ctp8c);
		map.put("CTP_4C", ctp4c);
		map.put("CTP_J_ORIGINAL", ctpJOriginal);
		map.put("CTP_J", ctpJ);
		map.put("CTP_K", ctpK);
		map.put("CTP_L", ctpL);
		map.put("CTP_M", ctpM);
		map.put("CONNECTION_TYPE", null);
		map.put("CONNECT_RATE", connectRate);
		map.put("LAYER_RATE", null);
		map.put("IS_ETH", DataCollectDefine.FALSE);
		map.put("IS_SEPARATE", DataCollectDefine.FALSE);
		map.put("CREATE_TIME", new Date());
		map.put("IS_DEL", DataCollectDefine.FALSE);
		return map;
	}

	@IMethodLog(desc = "DataCollectService：生成OTN CTP对象map")
	private Map constructOtnCtpData(int emsConnectionId, int neId,
			NameAndStringValue_T[] ctpName, TerminationPointModel model) {

		Map map = new HashMap();

		Integer ctpId = null;

		int isCtp = DataCollectDefine.TRUE;
		// ptp名称
		String ptpNameString = nameUtil.decompositionName(nameUtil
				.getPtpNameFromCtpName(ctpName));
		// ctp名称
		String ctpNameString = nameUtil.decompositionCtpName(ctpName);

		Map ptp = dataCollectMapper.selectPtpByNeIdAndPtpName(neId,
				ptpNameString);
		//only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
		if(ptp == null && ptpNameString.contains("ptptype")){
			ptp = selectPtpForE300(neId,ptpNameString);
		}
		
		//如果ptp还没有找到 返回null
		if (ptp == null) {
			// log记录 备查
			CommonException e = new CommonException(new NullPointerException(),
					MessageCodeDefine.CORBA_RUNTIME_EXCEPTION,
					"构建OTN CTP失败，对应ptp未找到，【网管ID】：" + emsConnectionId
							+ "【网元ID】：" + neId + " 【ctp名】：" + ptpNameString);
			ExceptionHandler.handleException(e);
			return null;
		}else{
			//如果ptp类型是MAC或MP 返回null 王海鸿邮件3/24 Re: 中兴E300 CTP查找PTP问题
			String ptpType = ptp.get("PTP_TYPE")!=null?ptp.get("PTP_TYPE").toString():"";
			if(DataCollectDefine.COMMON.PTP_TYPE_OTHER_MAC.equals(ptpType)||
					DataCollectDefine.COMMON.PTP_TYPE_OTHER_MP.equals(ptpType)){
				return null;
			}
		}

		String ctpValue = null;

		// 设置ctp Value
		if (ctpName.length > 3) {
			ctpValue = ctpName[3].value;
			if (ctpValue.startsWith("/")) {
				ctpValue = ctpValue.substring(1);
			}
		} else {
			isCtp = DataCollectDefine.FALSE;
			ctpId = dataCollectMapper.selectOtnCtpIdTypeIsFtp(emsConnectionId,
					Integer.valueOf(ptp.get("BASE_PTP_ID").toString()));
		}
		map.put("BASE_OTN_CTP_ID", ctpId);
		map.put("BASE_EMS_CONNECTION_ID", emsConnectionId);
		map.put("BASE_NE_ID", neId);
		
		/*zhuangjieliang
		 *Dead code 
		 * */
		/*songwanjiao
		 *ptp是通过查询表数据得到的map，有可能为空
		 *当map为空时，判断一下，否则取map数据会报错
		 * 
		if(null == ptp)
		{
			map.put("BASE_PTP_ID", null);
		}
		else
		{*/
		map.put("BASE_PTP_ID",
				Integer.valueOf(ptp.get("BASE_PTP_ID").toString()));
		/*}*/
		// 基础信息
		map.put("NAME", ctpNameString);
		map.put("USER_LABEL", model != null ? model.getUserLabel() : "");
		map.put("NATIVE_EMS_NAME", model != null ? model.getNativeEMSName()
				: "");
		map.put("DISPLAY_NAME", ctpValue);
		map.put("OWNER", model != null ? model.getOwner() : "");
		map.put("CONNECTION_STATE", model != null ? model.getConnectionState()
				: null);
		map.put("TP_MAPPING_MODE", model != null ? model.getTpMappingMode()
				: null);
		map.put("DIRECTION", model != null ? model.getDirection() : null);
		map.put("TP_PROTECTION_ASSOCIATION",
				model != null ? model.getTpProtectionAssociation() : null);
		map.put("EDGE_POINT",
				model != null ? (model.isEdgePoint() ? DataCollectDefine.TRUE
						: DataCollectDefine.FALSE) : null);
		map.put("CTP_VALUE", ctpValue);
		map.put("IS_CTP", isCtp);
		map.put("CREATE_TIME", new Date());

		map.put("IS_DEL", DataCollectDefine.FALSE);
		return map;
	}

	//为E300查找ptp
	private static Map selectPtpForE300(int neId,String ptpNameString){
		Map ptp = null;
		
		String rackNo = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.COMMON.RACK);
		String shelfNo = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.COMMON.SHELF);
		String slotNo = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.COMMON.SLOT);
		String portNo = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.COMMON.PORT);
		String ptpType = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.ZTE.ZTE_PTP_TYPE);
		String directionString = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.ZTE.ZTE_DIRECTION);
		String physicalPort = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.ZTE.ZTE_PHYSICAL_PORT);
		String channelNo = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.ZTE.ZTE_CHANEL_NO);
		
/*		BUG #1396
		现状：
		中兴E300 PTP入库时，PTPTYPE=OPM的端口，解析错误。
		PTP:/direction=sink/layerrate=1/ptptype=OPM/rack=0/shelf=3/slot=10/PhysicalPort=1/ChannelNo=23
		以上例子，无法正确识别入库。
		修改为：
		对PTPTYPE=OPM的端口,独立逻辑处理：
		1.PORT_NO=截取PhysicalPort-CHChannelNo 实例：1-CH1 1-CH2 1-CH3 1-CH80
		2.DISPLAY_NAME 不变 实例：1-CH1 1-CH2 1-CH3 1-CH80
		3.PTP_FTP 设为FTP （因为不能在树上显示）
		4.IS_SYNC_CTP 设为已经同步 (因为太多，而且底下不会有CTP)*/
		if(!physicalPort.isEmpty()&&!channelNo.isEmpty()){
			portNo = physicalPort+"-CH"+channelNo;
		}
		//默认双向
		int direction = 1;
		if(directionString.equals("src")){
			direction = 2;
		}else if(directionString.equals("sink")){
			direction = 3;
		}else if(directionString.equals("NA")){
			direction = 0;
		}
		ptp = dataCollectMapper.selectPtpForE300(neId, direction, ptpType, rackNo, shelfNo, slotNo, portNo);
		return ptp;
	}
	
	//为E300查找ctp
	private static Map selectCtpForE300(int emsConnectionId, int neId,
			String ptpNameString, String ctpNameString) {
		Map ctp = null;

		String rackNo = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.COMMON.RACK);
		String shelfNo = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.COMMON.SHELF);
		String slotNo = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.COMMON.SLOT);
		String portNo = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.COMMON.PORT);
		String ptpType = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.ZTE.ZTE_PTP_TYPE);
		String directionString = nameUtil.getEquipmentNoFromTargetName(
				ptpNameString, DataCollectDefine.ZTE.ZTE_DIRECTION);
		String physicalPort = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.ZTE.ZTE_PHYSICAL_PORT);
		String channelNo = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.ZTE.ZTE_CHANEL_NO);
		
/*		BUG #1396
		现状：
		中兴E300 PTP入库时，PTPTYPE=OPM的端口，解析错误。
		PTP:/direction=sink/layerrate=1/ptptype=OPM/rack=0/shelf=3/slot=10/PhysicalPort=1/ChannelNo=23
		以上例子，无法正确识别入库。
		修改为：
		对PTPTYPE=OPM的端口,独立逻辑处理：
		1.PORT_NO=截取PhysicalPort-CHChannelNo 实例：1-CH1 1-CH2 1-CH3 1-CH80
		2.DISPLAY_NAME 不变 实例：1-CH1 1-CH2 1-CH3 1-CH80
		3.PTP_FTP 设为FTP （因为不能在树上显示）
		4.IS_SYNC_CTP 设为已经同步 (因为太多，而且底下不会有CTP)*/
		if(!physicalPort.isEmpty()&&!channelNo.isEmpty()){
			portNo = physicalPort+"-CH"+channelNo;
		}
		// 默认双向
		int direction = 1;
		if (directionString.equals("src")) {
			direction = 2;
		} else if (directionString.equals("sink")) {
			direction = 3;
		} else if (directionString.equals("NA")) {
			direction = 0;
		}
		ctp = dataCollectMapper.selectCtpForE300(emsConnectionId, neId,
				direction, ptpType, rackNo, shelfNo, slotNo, portNo,
				ctpNameString);
		return ctp;
	}
	
	//为贝尔查找ptp
	private static Map selectPtpForALU(int neId,String ptpNameString){
		Map ptp = null;
		
		ptpNameString=ALUDataToModel.toCommonPtpName(ptpNameString);
		
		String rackNo = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.COMMON.RACK);
		String shelfNo = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.COMMON.SHELF);
		String slotNo = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.COMMON.SLOT);
		String subSlotNo = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.COMMON.SUB_SLOT);
		String portNo = nameUtil.getEquipmentNoFromTargetName(ptpNameString,
				DataCollectDefine.COMMON.PORT);
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("RACK_NO", rackNo);
		param.put("SHELF_NO", shelfNo);
		param.put("SLOT_NO", slotNo);
		param.put("SUB_SLOT_NO", subSlotNo);
		param.put("PORT_NO", portNo);
		param.put("IS_DEL", DataCollectDefine.FALSE);
		
		ptp = dataCollectMapper.selectPtpByProperties(neId, param);
		return ptp;
	}

	/**
	 * PM性能数据转换
	 * @param neId   网元ID
	 * @param pmDatas   PM数据模版列表
	 * @param collectCounter   是否采集计数值
	 * @param collectPhysical   是否采集物理量
	 * @param collectCtp   是否采集通道性能
	 * @return
	 */
	@IMethodLog(desc = "DataCollectService：PM性能数据转换")
	private List<PmDataModel> pmDataTransformation(int emsConnectionId,int neId,
			List<PmDataModel> pmDatas, boolean collectCounter,
			boolean collectPhysical, boolean collectCtp) {

		// 目标ptp
		Map targetPtp = null;
		// 目标ctp
		Map targetCtp = null;
		// 目标unit
		Map targetUnit = null;

		// 对象键
		String key = null;

		List<PmDataModel> pmDataList = new ArrayList<PmDataModel>();
		List<PmMeasurementModel> pmMeasureModelList = null;

		int factory;
		String emsType = null;
		String ptpType = null;
		String emsName = null;
		String emsGroupName = null;
		String subnetName = null;
		String neName = null;
		String areaName = null;
		String stationName = null;
		String productName = null;
//		String portDesc = null;
		String ctpName = null;
		String templateName = null;
		Integer emsGroupId = null;
		Integer neSubnetId = null;
		Integer resRoomId = null;
		Date rtrvTime;
		// 连续异常计数门限值
		int PM_EXCEPTION_COUNT_THRESHOLD = 5;

		// 初始化连续异常计数门限值
		String pmExCountThr = CommonUtil.getSystemConfigProperty("pmExceptionCountThr");
		if (pmExCountThr != null) {
			PM_EXCEPTION_COUNT_THRESHOLD = Integer.parseInt(pmExCountThr);
		}
		// 初始化性能调试输出开关
		boolean pmDebug = false;
		Map<String, Object> paramMap = dataCollectMapper.getSystemParam(DataCollectDefine.PM_DEBUG_PARAM_KEY);
		if (paramMap != null){
			String debug = paramMap.get("PARAM_VALUE").toString();
			pmDebug = debug.equals("true") ? true : false;
		}
		
		// PM性能归一化表格内容项
		Map pmStdParameterTblItem = null;
		// PM性能模版内容项
		Map pmTemplate = null;
		// PM比较值模版内容表格暂存对象
		HashMap<String, Object> pmTemplateInfoTbl = new HashMap<String, Object>();
		List<Map> pmTemplateInfoList = null;
		// 光模块标准表格暂存对象
		HashMap<String, Object> pmStdOptPortTbl = new HashMap<String, Object>();
		List<Map> pmStdOptPortList = null;
		//初始化 性能标准表
		initPmStdParameterTbl();
		// 获取PM比较值模版内容表
		pmTemplateInfoList = dataCollectMapper.selectPmTemplateInfoByAll();
		// 将PM比较值模版内容表暂存于pmTemplateInfoTbl
		// 以PM模版ID、标准PM参数名、Domain和参数类型(物理量/计数值)为关键字
		StringBuffer tempStr = new StringBuffer();
		for (Map pmTemplateInfo : pmTemplateInfoList) {
			tempStr.setLength(0);
			tempStr.append(pmTemplateInfo.get("PM_TEMPLATE_ID") != null ?
					pmTemplateInfo.get("PM_TEMPLATE_ID").toString(): "");
			tempStr.append(";");
			tempStr.append(pmTemplateInfo.get("PM_STD_INDEX"));
			tempStr.append(";");
			tempStr.append(pmTemplateInfo.get("DOMAIN"));
			tempStr.append(";");
			tempStr.append(pmTemplateInfo.get("TYPE"));
			tempStr.append(";");
			tempStr.append(pmTemplateInfo.get("GRANULARITY"));
			key = tempStr.toString();
			pmTemplateInfoTbl.put(key, pmTemplateInfo);
		}

		// 获取光口标准内容表
		pmStdOptPortList = dataCollectMapper.selectPmStdOptPortByAll();
		for (Map pmStdOptPortInfo : pmStdOptPortList) {
			key = pmStdOptPortInfo.get("PM_STD_OPT_PORT_ID").toString();
			pmStdOptPortTbl.put(key, pmStdOptPortInfo);
		}

		// 获取网管连接对象
		Map emsObject = dataCollectMapper.selectTableById(
				"T_BASE_EMS_CONNECTION", "BASE_EMS_CONNECTION_ID",
				emsConnectionId);
		factory = (Integer) emsObject.get("FACTORY");
		emsType = emsObject.get("TYPE").toString();
		emsName = emsObject.get("DISPLAY_NAME").toString();
		emsGroupId = emsObject.get("BASE_EMS_GROUP_ID") != null ? Integer.parseInt(emsObject.get("BASE_EMS_GROUP_ID").toString()) : null;
		
		// 获取EMS组对象
		if (emsGroupId != null) {
			Map emsGroupObj = dataCollectMapper.selectTableById("T_BASE_EMS_GROUP", "BASE_EMS_GROUP_ID", emsGroupId);
			emsGroupName = emsGroupObj.get("GROUP_NAME").toString();
		}
		
		// 获取网元对象
		Map neObject = dataCollectMapper.selectTableById("T_BASE_NE", "BASE_NE_ID", neId);
		neSubnetId = neObject.get("BASE_SUBNET_ID") != null ? Integer.parseInt(neObject.get("BASE_SUBNET_ID").toString()) : null;
		neName = neObject.get("DISPLAY_NAME").toString();
		productName = neObject.get("PRODUCT_NAME").toString();
		resRoomId = neObject.get("RESOURCE_ROOM_ID") != null ? Integer.parseInt(neObject.get("RESOURCE_ROOM_ID").toString()) : null;
		
		// 获取子网对象
		if (neSubnetId != null) {
			Map subnetObj = dataCollectMapper.selectTableById("T_BASE_SUBNET", "BASE_SUBNET_ID", neSubnetId);
			subnetName = subnetObj.get("DISPLAY_NAME").toString();
		}
		
		// 获取网元所在的资源区域名和站名
		if (resRoomId != null) {
			Map resObj = dataCollectMapper.selectAreaNameAndStationNameByNeId(neId);
			if (resObj != null) {
				areaName = resObj.get("AREA_NAME").toString();
				stationName = resObj.get("STATION_NAME").toString();
			}
		}

		//多线程并发状态会有冲突，不能使用静态变量
		HashMap<String, Object> syncObjectPool = new HashMap<String, Object>();

		// 循环pm性能数据
		for (PmDataModel model : pmDatas) {
			
			//无目标类型直接跳过
			if(model.getTargetType() == null){
				continue;
			}
			
			// 获取PM采集数据的采集时间
			rtrvTime = model.getRetrievalTimeDisplay(); 

			if(DataCollectDefine.COMMON.TARGET_TYPE_CTP_FLAG==
				model.getTargetType().intValue()){//解析otn/sdh ctp
				// 获取网元类型
				int neType = neObject.get("TYPE") != null ? Integer.valueOf(
						neObject.get("TYPE").toString()) : DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG;
				if (DataCollectDefine.COMMON.NE_TYPE_SDH_FLAG == neType) {
					model.setTargetType(DataCollectDefine.COMMON.TARGET_TYPE_SDH_CTP_FLAG);
				} else if(DataCollectDefine.COMMON.NE_TYPE_WDM_FLAG == neType
						||DataCollectDefine.COMMON.NE_TYPE_OTN_FLAG == neType){
					model.setTargetType(DataCollectDefine.COMMON.TARGET_TYPE_OTN_CTP_FLAG);
				}else{
					model.setTargetType(DataCollectDefine.COMMON.TARGET_TYPE_SDH_CTP_FLAG);
				}
			}
			String targetName = model.getTpNameString();

			// 解决华为波分设备采集性能值异常bug（WDM的CTP性能端口被表示成PTP）
			boolean findDataErr = false;
			if (factory == DataCollectDefine.FACTORY_HW_FLAG) {
				for (PmMeasurementModel mea : model.getPmMeasurementList()) {
					String pmOriName = mea.getPmParameterName();
					if (model.getTargetType() == DataCollectDefine.COMMON.TARGET_TYPE_PTP_FLAG && 
							(pmOriName.contains("PMP_FREQUENCY") ||
							 pmOriName.contains("PMP_PCLSWLO") ||
							 pmOriName.contains("PMP_SNR"))) {
						findDataErr = true;
					}
				}
			}
			if (findDataErr) {
				continue;
			}
			
			// 贝尔设备当前性能临时处理（端口级性能从CTP变成CTP）
			if (factory == DataCollectDefine.FACTORY_ALU_FLAG) {
				if (model.getTargetType() == DataCollectDefine.COMMON.TARGET_TYPE_SDH_CTP_FLAG) {
					model.setTargetType(DataCollectDefine.COMMON.TARGET_TYPE_PTP_FLAG);
					StringBuilder sb = new StringBuilder();
					sb.append(model.getTpName()[2].name).append(":").append(model.getTpName()[2].value);
					model.setTpNameString(sb.toString());
					targetName = sb.toString();
				}
			}
			
			model.setEmsConnectionId(emsConnectionId);
			model.setNeId(neId);
			model.setEmsGroupId(emsGroupId);
			model.setSubnetId(neSubnetId);

			targetPtp = null;
			targetUnit = null;
			
			switch (model.getTargetType()) {
			// ptp
			case DataCollectDefine.COMMON.TARGET_TYPE_PTP_FLAG:

				// 从对象池中获取对象
				key = DataCollectDefine.COMMON.TARGET_TYPE_PTP_FLAG + ";"
						+ neId + ";" + targetName;
				if (syncObjectPool.containsKey(key)) {
					targetPtp = (Map) syncObjectPool.get(key);
				} else {
					// 取得ptp
					targetPtp = dataCollectMapper.selectPtpByNeIdAndPtpName(
							neId, targetName);
					//only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
					if(targetPtp == null && targetName.contains("ptptype")){
						targetPtp = selectPtpForE300(neId,targetName);
					}else if(targetPtp == null && factory==DataCollectDefine.FACTORY_ALU_FLAG){
						targetPtp = selectPtpForALU(neId,targetName);
					}
					// 未取到ptp对象 跳过。。。
					if (targetPtp == null) {
						continue;
					}
					syncObjectPool.put(key, targetPtp);
				}
				break;

			// sdh ctp
			case DataCollectDefine.COMMON.TARGET_TYPE_SDH_CTP_FLAG:

				String ptpName = nameUtil.decompositionName(nameUtil
						.getPtpNameFromCtpName(model.getTpName()));
				// 从对象池中获取对象
				key = DataCollectDefine.COMMON.TARGET_TYPE_SDH_CTP_FLAG + ";"
						+ neId + ";" + targetName + ";" + ptpName;

				if (syncObjectPool.containsKey(key)) {
					targetCtp = (Map) syncObjectPool.get(key);
				} else {
					// 取得CTP
					targetCtp = dataCollectMapper.selectSdhCtp(emsConnectionId,
							neId, ptpName, targetName);
					// 未取到ctp对象 跳过。。。
					if (targetCtp == null) {
						continue;
					}
					syncObjectPool.put(key, targetCtp);
				}

				// 取得ptp
				targetPtp = dataCollectMapper.selectTableById("T_BASE_PTP",
						"BASE_PTP_ID",
						targetCtp.get("BASE_PTP_ID") != null ? Integer
								.valueOf(targetCtp.get("BASE_PTP_ID")
										.toString()) : null);

				// 设置关联Id
				model.setSdhCtpId(targetCtp.get("BASE_SDH_CTP_ID") != null ? Integer
						.valueOf(targetCtp.get("BASE_SDH_CTP_ID").toString())
						: null);
				if (model.getSdhCtpId() != null) {
					ctpName = targetCtp.get("DISPLAY_NAME").toString();
				}

				break;
			// otn ctp
			case DataCollectDefine.COMMON.TARGET_TYPE_OTN_CTP_FLAG:

				ptpName = nameUtil.decompositionName(nameUtil
						.getPtpNameFromCtpName(model.getTpName()));
				 //从对象池中获取对象
				 key = DataCollectDefine.COMMON.TARGET_TYPE_OTN_CTP_FLAG+";"+neId+";"+
						 targetName + ";" + ptpName;
				 if (syncObjectPool.containsKey(key)) {
					 targetCtp = (Map) syncObjectPool.get(key);
				 } else {
					 // 取得CTP
					 targetCtp = dataCollectMapper.selectOtnCtp(emsConnectionId,
								neId, ptpName, targetName);
					 
					//only for e300 ctp中的ptp名与ptp表中的名称不同，缺少layerrate
					if(targetCtp == null && ptpName.contains("ptptype")){
						targetCtp = selectCtpForE300(emsConnectionId,
								neId, ptpName, targetName);
					}
					 
					 if(targetCtp==null)
						 continue;//找不到ctp
					 syncObjectPool.put(key, targetCtp);
				 }
				 // 取得ptp
				 targetPtp = dataCollectMapper.selectTableById("T_BASE_PTP", "BASE_PTP_ID",
					 targetCtp.get("BASE_PTP_ID") != null ? Integer.valueOf(targetCtp.get("BASE_PTP_ID")
					 .toString()) : null);
				
				 // 设置关联Id
				 model.setOtnCtpId(targetCtp.get("BASE_OTN_CTP_ID") != null ? Integer
						 .valueOf(targetCtp.get("BASE_OTN_CTP_ID").toString()) : null);
				 if (model.getOtnCtpId() != null) {
					 ctpName = targetCtp.get("DISPLAY_NAME").toString();
				 }

				break;
			// equipment
			case DataCollectDefine.COMMON.TARGET_TYPE_EQUIPMENT_FLAG:
				// 取得unit
				targetUnit = dataCollectMapper.selectUnitByNeIdAndName(neId, targetName);
				if(targetUnit==null)
					 continue;//找不到unit
				break;
			// ne
			case DataCollectDefine.COMMON.TARGET_TYPE_NE_FLAG:
				// 不做什么，保证网元级性能不被过滤
				break;
			default:
				continue;//FIXME 未知类型，根据Name判别或略过。
			}
					
			// PM模版ID，可能为null
			Integer pmTemplateId = null;
			// 光口标准Id
			Integer optStdId = null;

			// PTP、CTP相关
			if (targetPtp !=null) {
				// 设置关联Id
				model.setRackId(targetPtp.get("BASE_RACK_ID") != null ? Integer
						.valueOf(targetPtp.get("BASE_RACK_ID").toString()) : null);
				model.setShelfId(targetPtp.get("BASE_SHELF_ID") != null ? Integer
						.valueOf(targetPtp.get("BASE_SHELF_ID").toString()) : null);
				model.setSlotId(targetPtp.get("BASE_SLOT_ID") != null ? Integer
						.valueOf(targetPtp.get("BASE_SLOT_ID").toString()) : null);
				model.setSubSlotId(targetPtp.get("BASE_SUB_SLOT_ID") != null ? Integer
						.valueOf(targetPtp.get("BASE_SUB_SLOT_ID").toString())
						: null);
				model.setUnitId(targetPtp.get("BASE_UNIT_ID") != null ? Integer
						.valueOf(targetPtp.get("BASE_UNIT_ID").toString()) : null);
				model.setSubUnitId(targetPtp.get("BASE_SUB_UNIT_ID") != null ? Integer
						.valueOf(targetPtp.get("BASE_SUB_UNIT_ID").toString())
						: null);
				model.setPtpId(targetPtp.get("BASE_PTP_ID") != null ? Integer
						.valueOf(targetPtp.get("BASE_PTP_ID").toString()) : null);
				// 设置PTP Type、端口描述和业务类型
				model.setPtpType((String)targetPtp.get("PTP_TYPE"));
				model.setDisplayPortDesc((String)targetPtp.get("PORT_DESC"));
				model.setDomain(targetPtp.get("DOMAIN") != null ? Integer
						.valueOf(targetPtp.get("DOMAIN").toString()) : null);
				//设置rate
				model.setRate(targetPtp.get("RATE") != null ? targetPtp.get("RATE").toString() : null);

				// 光口标准Id
				optStdId = targetPtp.get("OPT_STD_ID") != null ? Integer
						.valueOf(targetPtp.get("OPT_STD_ID").toString()) : null;
				// 区分中兴设备每信道的光功率波长用
				if (targetPtp.get("NAME").toString().contains("OPM")) {
					ptpType = "OPM";
				} else {
					ptpType = "";
				}

				// 获取PM模版ID
				pmTemplateId = targetPtp.get("PM_TEMPLATE_ID") != null ? Integer
						.valueOf(targetPtp.get("PM_TEMPLATE_ID").toString()) : null;
				model.setPmTemplateId(pmTemplateId);

				// 获取模版名
				if (pmTemplateId != null) {
					Map templateObj = dataCollectMapper.selectTableById("T_PM_TEMPLATE", "PM_TEMPLATE_ID", pmTemplateId);
					templateName = templateObj.get("TEMPLATE_NAME").toString();
				}
			
			// 板卡相关
			} else if (targetUnit != null) {
				model.setRackId(targetUnit.get("BASE_RACK_ID") != null ? Integer
						.valueOf(targetUnit.get("BASE_RACK_ID").toString()) : null);
				model.setShelfId(targetUnit.get("BASE_SHELF_ID") != null ? Integer
						.valueOf(targetUnit.get("BASE_SHELF_ID").toString()) : null);
				model.setSlotId(targetUnit.get("BASE_SLOT_ID") != null ? Integer
						.valueOf(targetUnit.get("BASE_SLOT_ID").toString()) : null);
				model.setUnitId(targetUnit.get("BASE_UNIT_ID") != null ? Integer
						.valueOf(targetUnit.get("BASE_UNIT_ID").toString()) : null);
				
				// 设置端口描述
				model.setDisplayPortDesc((String)targetUnit.get("UNIT_DESC"));
				
				// 获取PM模版ID
				pmTemplateId = targetUnit.get("PM_TEMPLATE_ID") != null ? Integer
						.valueOf(targetUnit.get("PM_TEMPLATE_ID").toString()) : null;
				model.setPmTemplateId(pmTemplateId);

				// 获取模版名
				if (pmTemplateId != null) {
					Map templateObj = dataCollectMapper.selectTableById("T_PM_TEMPLATE", "PM_TEMPLATE_ID", pmTemplateId);
					templateName = templateObj.get("TEMPLATE_NAME").toString();
				}
			}
			if (pmDebug)
				printPmData(model);
			pmMeasureModelList = new ArrayList<PmMeasurementModel>();

			// 循环pm性能参数测量列表
			for (PmMeasurementModel measurementModel : model
					.getPmMeasurementList()) {
				ptpType = ptpType == null ? "" : ptpType;
				// 以厂家，EMS类型，PTP类型，层速率，原始PM参数名为关键字查询PM参数归一化表格
				key = generatePmStdKey(String.valueOf(factory), emsType, ptpType,
						String.valueOf(model.getLayerRate()),
						measurementModel.getPmParameterName());

				if (pmStdParameterTbl.containsKey(key)) {
					if (pmDebug)
						System.out.println("    PM参数已在归一化表格中查询到...");
					pmStdParameterTblItem = (Map) pmStdParameterTbl.get(key);
					int type = (Integer) pmStdParameterTblItem.get("TYPE");
					String pmStdIndex = pmStdParameterTblItem.get(
							"PM_STD_INDEX").toString();
					// 以太网性能的location设置为无效
					if (pmStdParameterTblItem.get("PM_STD_INDEX_TYPE").toString().equals("18")) {
						measurementModel.setLocationFlag(0);
					}

					if ((type == 1) && collectPhysical) { // 物理量
						// 设置标准PM参数名
						measurementModel.setPmStdIndex(pmStdIndex);
						// 设置PM参数描述
						measurementModel.setPmdescription(pmStdParameterTblItem
								.get("PM_DESCRIPTION").toString());
						// 设置PM参数类型（1物理量/2计数值）
						measurementModel.setType(String.valueOf(type));			
						// PM性能值格式化
						String val = formatPMValue(pmStdIndex, measurementModel.getValue(), factory,
								measurementModel.getUnit());
						measurementModel.setValue(val);

						// 以ptpId、sdhCtpId、otnCtpId、unitId、targetType和标准PM参数名为Key获取基准值
						Map pmCompare = dataCollectMapper.selectPmCompareByTargetIdAndTypeAndStdIndex(
								model.getUnitId(), model.getPtpId(), model.getSdhCtpId(), model.getOtnCtpId(),
								model.getTargetType(), measurementModel.getPmStdIndex());
						if (pmCompare != null) {
							// 设置测量模版中的基准值
							measurementModel.setPmCompareValue(pmCompare.get("PM_COMPARE_VALUE") != null ?
									pmCompare.get("PM_COMPARE_VALUE").toString() : null);							
							// 光功率基准值更新规则处理
							if (pmStdIndex.contains("TPL") ||
									pmStdIndex.contains("RPL") ||
									pmStdIndex.contains("PCLSOP")) {
								
								// 如果基准值为-60dBm，且当前PM值不为-60dBm，则将当前PM值纳入基准值
								if (measurementModel.getPmCompareValue() == null ||
										(measurementModel.getPmCompareValue().equals(UNUSED_POWER_VALUE) &&
										!measurementModel.getValue().equals(UNUSED_POWER_VALUE))) {
									// 更新基准值
									pmCompare.put("PM_COMPARE_VALUE", measurementModel.getValue());
									// 更新基准值表
									dataCollectMapper.updatePmCompare(pmCompare);
									// 更新测量模版中的基准值
									measurementModel.setPmCompareValue(measurementModel.getValue());
								}
								// 如果当前PM值为-60dBm，且连续异常计数值等于给定值，则将当前基准值设为-60dBm
								if (measurementModel.getValue().equals(UNUSED_POWER_VALUE) &&
										measurementModel.getExceptionCount() == PM_EXCEPTION_COUNT_THRESHOLD) {
									// 更新基准值
									pmCompare.put("PM_COMPARE_VALUE", UNUSED_POWER_VALUE);
									// 更新基准值表
									dataCollectMapper.updatePmCompare(pmCompare);
									// 更新测量模版中的基准值
									measurementModel.setPmCompareValue(measurementModel.getValue());
								}
							}
						} else {
							// 基准值为空，将当前PM值纳入基准值
							if (!measurementModel.getValue().equals(INVALID_PM_VALUE)) {
								Map baseValue = new HashMap();
								SimpleDateFormat dateFormat = CommonUtil
										.getDateFormatter(DataCollectDefine.COMMON_FORMAT);
								//PM_COMPARE_ID
								baseValue.put("PM_COMPARE_ID", null);
								//TARGET_TYPE
								baseValue.put("TARGET_TYPE", model.getTargetType());
								//PM_STD_INDEX
								baseValue.put("PM_STD_INDEX", measurementModel.getPmStdIndex());
								//PM_DESCRIPTION
								baseValue.put("PM_DESCRIPTION", measurementModel.getPmdescription());
								//PM_COMPARE_VALUE
								baseValue.put("PM_COMPARE_VALUE", measurementModel.getValue());
								//BASE_OTN_CTP_ID
								baseValue.put("BASE_OTN_CTP_ID", model.getOtnCtpId());
								//BASE_SDH_CTP_ID
								baseValue.put("BASE_SDH_CTP_ID", model.getSdhCtpId());
								//BASE_PTP_ID
								baseValue.put("BASE_PTP_ID", model.getPtpId());
								//BASE_UNIT_ID
								baseValue.put("BASE_UNIT_ID", model.getUnitId());
								//BASE_NE_ID
								baseValue.put("BASE_NE_ID", model.getNeId());
								//DISPLAY_CTP
								baseValue.put("DISPLAY_CTP", ctpName);
								//UPDATE_TIME
								baseValue.put("UPDATE_TIME", dateFormat.format(new Date()));
								// 插入新的基准值
								dataCollectMapper.insertPmCompare(baseValue);
								// 更新测量模版中的基准值
								measurementModel.setPmCompareValue(measurementModel.getValue());
							}
						}

						// 物理量分析
						pmPhysicValueAnalysis(emsConnectionId,neId, pmTemplateId, pmTemplateInfoTbl, optStdId, pmStdOptPortTbl,
								model.getUnitId(), model.getPtpId(), model.getOtnCtpId(), model.getSdhCtpId(), measurementModel,
								model.getDomain(), rtrvTime);
						// 加入PM参数列表
						pmMeasureModelList.add(measurementModel);

					} else if ((type == 2) && collectCounter) { // 计数值
						if (!isPathPm(pmStdIndex)) { // 非通道性能
							// 设置标准PM参数名
							measurementModel.setPmStdIndex(pmStdIndex);
							// 设置PM参数描述
							measurementModel.setPmdescription(pmStdParameterTblItem
											.get("PM_DESCRIPTION").toString());
							// 设置PM参数类型（1物理量/2计数值）
							measurementModel.setType(String.valueOf(type));
							// PM性能值格式化
							String val = formatPMValue(pmStdIndex, measurementModel.getValue(), factory,
									measurementModel.getUnit());
							measurementModel.setValue(val);
							// 计数值分析
							pmCountValueAnalysis(emsConnectionId,neId, pmTemplateId, pmTemplateInfoTbl,
									model.getUnitId(), model.getPtpId(), model.getOtnCtpId(), model.getSdhCtpId(),
									measurementModel, model.getDomain(), rtrvTime, model.getGranularityFlag());
							// 加入PM参数列表
							pmMeasureModelList.add(measurementModel);

						} else { // 通道性能
							if (collectCtp) {
								// 设置标准PM参数名
								measurementModel.setPmStdIndex(pmStdIndex);
								// 设置PM参数描述
								measurementModel.setPmdescription(pmStdParameterTblItem
										.get("PM_DESCRIPTION").toString());
								// 设置PM参数类型（1物理量/2计数值）
								measurementModel.setType(String.valueOf(type));
								// PM性能值格式化
								String val = formatPMValue(pmStdIndex, measurementModel.getValue(), factory,
										measurementModel.getUnit());
								measurementModel.setValue(val);
								// 计数值分析
								pmCountValueAnalysis(emsConnectionId,neId, pmTemplateId, pmTemplateInfoTbl,
										model.getUnitId(), model.getPtpId(), model.getOtnCtpId(), model.getSdhCtpId(),
										measurementModel, model.getDomain(), rtrvTime, model.getGranularityFlag());
								// 加入PM参数列表
								pmMeasureModelList.add(measurementModel);
							}
						}

					} else {
						// 无效的PM类型，丢弃该性能参数
						if (pmDebug)
							System.out.println("    无效的PM类型！");
					}
					if (pmDebug)
						printPmMeasureList(measurementModel);
				} else {
					// 原始性能参数名不在归一化表格中，丢弃该性能参数
					if (pmDebug) {
						System.out.println("    归一化表格中未查询到该PM参数！！！");
						System.out.println("    Key:"+key);						
					}

				}

			}

			// 设置性能参数集合
			model.setPmMeasurementList(pmMeasureModelList);
			// 设置其他数据
			model.setDisplayEmsGroup(emsGroupName);
			model.setDisplayEms(emsName);
			model.setDisplaySubnet(subnetName);
			model.setDisplayNe(neName);
			model.setDisplayArea(areaName);
			model.setDisplayStation(stationName);
			model.setDisplayProductName(productName);
			//前面已经赋过值
//			model.setDisplayPortDesc(portDesc);
			model.setDisplayCtp(ctpName);
			// 防止ctp name残留影响下一个非CTP的数据
			ctpName = null;
			model.setDisplayTemplateName(templateName);
			// 添加性能
			pmDataList.add(model);
		}
//		System.out.println("返回的结果：--------------------------");
//		for (PmDataModel model : pmDataList) {
//			printPmData(model);
//			for (PmMeasurementModel mea : model.getPmMeasurementList()) {
//				printPmMeasureList(mea);
//			}
//		}
		return pmDataList;
	}

	private void printPmData(PmDataModel data) {
		System.out.println("====PmDataModel=========================");
		System.out.println("NE:"+data.getDisplayNe());
		System.out.println("NE_Id:"+data.getNeId());
		System.out.println("Ptp_Id:"+data.getPtpId());
		System.out.println("Product："+data.getDisplayProductName());
		System.out.println("Port:"+data.getDisplayPortDesc());
		System.out.println("PtpType:"+data.getPtpType());
		System.out.println("Rate:"+data.getRate());
		System.out.println("TpName:"+data.getTpNameString());
		System.out.println("========================================");
	}
	private void printPmMeasureList(PmMeasurementModel pm) {
		System.out.println("========PmMeasurementModel==================");
		System.out.println("    StdIndex:"+pm.getPmStdIndex());
		System.out.println("    PmDes:"+pm.getPmdescription());
		System.out.println("    PmParam:"+pm.getPmParameterName());
		System.out.println("    Value:"+pm.getValue());
		System.out.println("============================================");
	}
	
	//初始化性能标准表
	private void initPmStdParameterTbl(){
		//初始化 性能标准表
		if(pmStdParameterTbl == null||pmStdParameterTbl.size() == 0){
			StringBuffer tempStr = new StringBuffer();
			// 对象键
			String key = null;
			// 获取PM参数名归一化表内容
			List<Map> pmStdIndexList = dataCollectMapper.selectPmStdIndexByAll();
			// 将PM参数名归一化表暂存于pmStdParameter
			for (Map pmStdIndex : pmStdIndexList) {
				key = generatePmStdKey(pmStdIndex.get("FACTORY").toString(),
						pmStdIndex.get("EMS_TYPE").toString(), 
						pmStdIndex.get("PTP_TYPE").toString(),
						pmStdIndex.get("LAYRATE_STRING").toString(),
						pmStdIndex.get("PM_INDEX").toString());
				pmStdParameterTbl.put(key, pmStdIndex);
			}
		}
	}
	
	
	// 生成pmStd key
	private String generatePmStdKey(String factory, String emsType,
			String ptpType, String layrateString, String pmIndex) {
		StringBuffer tempStr = new StringBuffer();
		// 对象键
		String key = null;
		tempStr.setLength(0);
		tempStr.append(factory);
		tempStr.append(";");
		tempStr.append(emsType);
		tempStr.append(";");
		tempStr.append(ptpType);
		tempStr.append(";");
		tempStr.append(layrateString);
		tempStr.append(";");
		tempStr.append(pmIndex);
		key = tempStr.toString();
		return key;
	}

	/**
	 * 格式化性能数据
	 * 
	 * @param pmStandardIndex   标准PM参数名
	 * @param pmValueOriginal   原始PM值
	 * @param factory	厂家类型
	 * @return  格式化后的PM值
	 */
	// @IMethodLog(desc = "DataCollectService：格式化性能值数据")
	private String formatPMValue(String pmStandardIndex, String pmValueOriginal, int factory,
			String unit) {
		String pmValue;
		DecimalFormat numFormat;

		try {
			numFormat = getPmValueNumFormat(pmStandardIndex, unit);
			if ("NaN".equals(pmValueOriginal)) {
				pmValue = "";
			} else {
				// FEC误码率性能参数值特殊表示
				if (pmStandardIndex.equals("FEC_BEF_COR_ER") || 
					pmStandardIndex.equals("FEC_AFT_COR_ER")) {
					if (0 == Float.parseFloat(pmValueOriginal)) {
						// FEC为零时特殊表示
						pmValue = "0.0";
					} else if (factory == DataCollectDefine.FACTORY_HW_FLAG) {
						// 华为FEC误码率性能值为指数值，在此做转换成科学计数法表示
						pmValue = numFormat.format(1/Math.pow(10, Float.parseFloat(pmValueOriginal)));
					} else {
						// 原先已是科学计数法FEC值的格式化
						pmValue = numFormat.format(Float.parseFloat(pmValueOriginal));
					}
					
				// 带宽、CPU及内存等利用率的值
				} else if (pmStandardIndex.contains("BW_USG") ||
						   pmStandardIndex.contains("BW_USG") ||
						   pmStandardIndex.contains("UR-CPU") ||
						   pmStandardIndex.contains("UR-RAM")) {
					pmValue = numFormat.format(Float.parseFloat(pmValueOriginal) * 100);
					
				// 其余性能参数值
				} else {
					pmValue = numFormat.format(Float.parseFloat(pmValueOriginal));
				}
			}			
		} catch (NumberFormatException e) {
			pmValue = "";
		} catch (NullPointerException e) {
			pmValue = "";
		} catch (IllegalArgumentException e) {
			pmValue = "";
		}
		
		// 光功率值范围检查
		if ("RPL_CUR".equals(pmStandardIndex) ||
				"RPL_MAX".equals(pmStandardIndex) ||
				"RPL_MIN".equals(pmStandardIndex) ||
				"RPL_AVG".equals(pmStandardIndex) ||
				"TPL_CUR".equals(pmStandardIndex) ||
				"TPL_MAX".equals(pmStandardIndex) ||
				"TPL_MIN".equals(pmStandardIndex) ||
				"TPL_AVG".equals(pmStandardIndex) ||
				"PCLSOP_CUR".equals(pmStandardIndex) ||
				"PCLSOP_MAX".equals(pmStandardIndex) ||
				"PCLSOP_MIN".equals(pmStandardIndex)) {
			// 光功率出现空白值时固定设为"-60.00"
			if (pmValue.isEmpty()) {
				pmValue = UNUSED_POWER_VALUE;
			} else {
				// 光功率出现极大值时固定设为"-60.00"
				if (INVALID_POWER_VALUE_THRESHOLD <= Float.valueOf(pmValue) ||
					-INVALID_POWER_VALUE_THRESHOLD >= Float.valueOf(pmValue)) {
					pmValue = UNUSED_POWER_VALUE;
				}
			}
		}
		
		// 每信道光中心波长值范围检查
		if ("PCLSWL_CUR".equals(pmStandardIndex) ||
			"PCLSWL_MIN".equals(pmStandardIndex) ||
			"PCLSWL_MAX".equals(pmStandardIndex)) {
			// 有效范围检查
			if (CHANNEL_WAVE_LENGTH_MIN > Float.parseFloat(pmValue) ||
				CHANNEL_WAVE_LENGTH_MAX < Float.parseFloat(pmValue)) {
				pmValue = INVALID_PM_VALUE;
			}
		}
		
		// 信道信噪比值范围检查
		if ("PCLSSNR_CUR".equals(pmStandardIndex) ||
			"PCLSSNR_MIN".equals(pmStandardIndex) ||
			"PCLSSNR_MAX".equals(pmStandardIndex)) {
			// 有效范围检查
			if (SNR_MIN > Float.parseFloat(pmValue) ||
				SNR_MAX < Float.parseFloat(pmValue)) {
				pmValue = INVALID_PM_VALUE;
			}
		}
		
		// 温度异常值检查
		if (pmStandardIndex.contains("TEMP") || pmStandardIndex.contains("TMP")) {
			// 有效范围检查
			if (TEMPERATURE_MIN > Float.parseFloat(pmValue) ||
				TEMPERATURE_MAX < Float.parseFloat(pmValue)) {
				pmValue = INVALID_PM_VALUE;
			}
		}

		return pmValue;
	}

	/**
	 * 计数值性能数据分析
	 * 
	 * @param neId   网元ID
	 * @param pmTemplateId   性能模版ID
	 * @param pmTemplateInfoTbl  性能模版表
	 * @param ptpId   PTP ID
	 * @param otnCtpId   OTN的CTP ID
	 * @param sdhCtpId   SDH的CTP ID
	 * @param meaModel   测量数据模版
	 * @param domain   业务类型
	 * @param rtrvTime   PM数据读取时间
	 */
	// @IMethodLog(desc = "DataCollectService：计数值性能数据分析")
	private void pmCountValueAnalysis(int emsConnectionId,int neId, Integer pmTemplateId,
			HashMap<String, Object> pmTemplateInfoTbl, Integer unitId, Integer ptpId, Integer otnCtpId, Integer sdhCtpId,
			PmMeasurementModel meaModel, Integer domain, Date rtrvTime, int period) {
		Float pmValue, thresHold1, thresHold2, thresHold3, filterValue;
		String key;
		Map pmTemplate;
		// 设置显示用性能比较值的缺省值
		meaModel.setDisplayCompareValue("");
		// 判断是否有分析模版
		if (pmTemplateId != null) {
			key = pmTemplateId + ";" + meaModel.getPmStdIndex() + ";"
					+ domain + ";" + meaModel.getType() + ";" + period;
			// 判断模版是否存在
			if (pmTemplateInfoTbl.containsKey(key)) {
				pmTemplate = (Map) pmTemplateInfoTbl.get(key);
				// 设置门限值
				if (pmTemplate.get("THRESHOLD_1") != null) {
					meaModel.setThreshold1(pmTemplate.get("THRESHOLD_1")
							.toString());
				} else {
					meaModel.setThreshold1("");
				}
				if (pmTemplate.get("THRESHOLD_2") != null) {
					meaModel.setThreshold2(pmTemplate.get("THRESHOLD_2")
							.toString());
				} else {
					meaModel.setThreshold2("");
				}
				if (pmTemplate.get("THRESHOLD_3") != null) {
					meaModel.setThreshold3(pmTemplate.get("THRESHOLD_3")
							.toString());
				} else {
					meaModel.setThreshold3("");
				}
				if (pmTemplate.get("FILTER_VALUE") != null) {
					meaModel.setFilterValue(pmTemplate.get("FILTER_VALUE")
							.toString());
				} else {
					meaModel.setFilterValue("");
				}
				// 获取门限值
				thresHold1 = meaModel.getThreshold1().isEmpty() ? null : Float.valueOf(meaModel.getThreshold1());
				thresHold2 = meaModel.getThreshold2().isEmpty() ? null : Float.valueOf(meaModel.getThreshold2());
				thresHold3 = meaModel.getThreshold3().isEmpty() ? null : Float.valueOf(meaModel.getThreshold3());
				filterValue = meaModel.getFilterValue().isEmpty() ? null : Float.valueOf(meaModel.getFilterValue());
				pmValue = meaModel.getValue().isEmpty() ? null : Float.valueOf(meaModel.getValue());

				// 误码率的特殊比较
				if ("FEC_BEF_COR_ER".equals(meaModel.getPmStdIndex()) ||
						"FEC_AFT_COR_ER".equals(meaModel.getPmStdIndex())) {
					if ((pmValue != null) && (pmValue > 0)) {
						if (thresHold1 != null && (pmValue < thresHold1)) {
							// 正常数据
							meaModel.setExceptionLv(DataCollectDefine.EXCEPTION_LEVLE_NORMAL);
							// 设置连续异常计数器
							meaModel.setExceptionCount(0);
							// 设置显示用性能比较值
							meaModel.setDisplayCompareValue("<" + meaModel.getThreshold1());
	
						} else if (thresHold1 != null &&
								thresHold2 != null &&
								(pmValue >= thresHold1 && pmValue < thresHold2)) {
							// 一般预警
							meaModel.setExceptionLv(DataCollectDefine.EXCEPTION_LEVLE_WR);
							// 设置连续异常计数器
							setContinueErrCounter(emsConnectionId,neId, unitId, ptpId, otnCtpId, sdhCtpId, meaModel, rtrvTime);
							// 设置显示用性能比较值
							meaModel.setDisplayCompareValue(meaModel.getThreshold1() + "~"
									+ meaModel.getThreshold2());
	
						} else if (thresHold2 != null &&
								thresHold3 != null &&
								(pmValue >= thresHold2 && pmValue < thresHold3)) {
							// 次要预警
							meaModel.setExceptionLv(DataCollectDefine.EXCEPTION_LEVLE_MN);
							// 设置连续异常计数器
							setContinueErrCounter(emsConnectionId,neId, unitId, ptpId, otnCtpId, sdhCtpId, meaModel, rtrvTime);
							// 设置显示用性能比较值
							meaModel.setDisplayCompareValue(meaModel.getThreshold2() + "~"
									+ meaModel.getThreshold3());
	
						} else if (thresHold3 != null &&
								pmValue >= thresHold3) {
							// 重要预警
							meaModel.setExceptionLv(DataCollectDefine.EXCEPTION_LEVLE_CR);
							// 设置连续异常计数器
							setContinueErrCounter(emsConnectionId,neId, unitId, ptpId, otnCtpId, sdhCtpId, meaModel, rtrvTime);
							// 设置显示用性能比较值
							meaModel.setDisplayCompareValue("≥" + meaModel.getThreshold3());
	
						} else {
							// 未设门限值时当作正常数据
							meaModel.setExceptionLv(DataCollectDefine.EXCEPTION_LEVLE_NORMAL);
						}
					} else {
						if (pmValue != null) {
							meaModel.setExceptionLv(DataCollectDefine.EXCEPTION_LEVLE_NORMAL);
							if (pmValue == 0) { meaModel.setDisplayCompareValue("<" + meaModel.getThreshold1()); }
						}
					}

				} else { // 正常比较
				if (thresHold1 != null &&
						pmValue != null &&
						//filterValue != null &&
						(pmValue < thresHold1 || (filterValue != null ? pmValue >= filterValue : false))) {
					// 正常数据
					meaModel.setExceptionLv(DataCollectDefine.EXCEPTION_LEVLE_NORMAL);
					// 设置连续异常计数器
					meaModel.setExceptionCount(0);
					// 设置显示用性能比较值
					if (pmValue < thresHold1) {
						meaModel.setDisplayCompareValue("0~" + meaModel.getThreshold1());
					} else {
						meaModel.setDisplayCompareValue("≥" + meaModel.getFilterValue());
					}

				} else if (thresHold1 != null &&
						thresHold2 != null &&
						pmValue != null &&
						(pmValue >= thresHold1 && pmValue < thresHold2)) {
					// 一般预警
					meaModel.setExceptionLv(DataCollectDefine.EXCEPTION_LEVLE_WR);
					// 设置连续异常计数器
					setContinueErrCounter(emsConnectionId,neId, unitId, ptpId, otnCtpId, sdhCtpId, meaModel, rtrvTime);
					// 设置显示用性能比较值
					meaModel.setDisplayCompareValue(meaModel.getThreshold1() + "~" + meaModel.getThreshold2());

				} else if (thresHold2 != null &&
						thresHold3 != null &&
						pmValue != null &&
						(pmValue >= thresHold2 && pmValue < thresHold3)) {
					// 次要预警
					meaModel.setExceptionLv(DataCollectDefine.EXCEPTION_LEVLE_MN);
					// 设置连续异常计数器
					setContinueErrCounter(emsConnectionId,neId, unitId, ptpId, otnCtpId, sdhCtpId, meaModel, rtrvTime);
					// 设置显示用性能比较值
					meaModel.setDisplayCompareValue(meaModel.getThreshold2() + "~" + meaModel.getThreshold3());

				} else if (thresHold3 != null &&
						pmValue != null &&
						pmValue >= thresHold3) {
					// 重要预警
					meaModel.setExceptionLv(DataCollectDefine.EXCEPTION_LEVLE_CR);
					// 设置连续异常计数器
					setContinueErrCounter(emsConnectionId,neId, unitId, ptpId, otnCtpId, sdhCtpId, meaModel, rtrvTime);
					// 设置显示用性能比较值
					if (filterValue != null) {
						meaModel.setDisplayCompareValue(meaModel.getThreshold3() + "~" + meaModel.getFilterValue());
					} else {
						meaModel.setDisplayCompareValue("≥" + meaModel.getThreshold3());
					}

				} else {
					// 未设门限值时当作正常数据
					meaModel.setExceptionLv(DataCollectDefine.EXCEPTION_LEVLE_NORMAL);
				}
				}
			} // 模版存在
		} // 是否有分析模版
	}

	/**
	 * 物理量性能数据分析
	 * 
	 * @param neId   网元ID
	 * @param pmTemplateId   性能模版ID
	 * @param pmTemplateInfoTbl   性能模版表
	 * @param optStdId   光接口标准ID
	 * @param pmStdOptPortTbl   光接口标准表
	 * @param ptpId   PTP ID
	 * @param otnCtpId   OTN的CTP ID
	 * @param sdhCtpId   SDH的CTP ID
	 * @param meaModel   测量数据模版
	 * @param domain   业务类型
	 * @param rtrvTime   PM数据读取时间
	 */
	// @IMethodLog(desc = "DataCollectService：物理量性能数据分析")
	private void pmPhysicValueAnalysis(int emsConnectionId,int neId, Integer pmTemplateId,
			HashMap<String, Object>pmTemplateInfoTbl, Integer optStdId, HashMap<String, Object>pmStdOptPortTbl,
			Integer unitId, Integer ptpId, Integer otnCtpId, Integer sdhCtpId, PmMeasurementModel meaModel,
			Integer domain, Date rtrvTime) {
		Float pmValue, offset, upperValue, upperOffset, lowerValue, lowerOffset, compareValue;
		String key;
		Map pmTemplate;
		DecimalFormat numFormat;
		String tempStr;
		// 设置显示用性能比较值的缺省值
		meaModel.setDisplayCompareValue("");
		
		// 判断是否有分析模版
		if (pmTemplateId != null) {
			key = pmTemplateId + ";" + meaModel.getPmStdIndex()
					+ ";" + domain + ";" + meaModel.getType() + ";null";
			// 判断PM参数是否进行分析
			if (pmTemplateInfoTbl.containsKey(key)) {
				// 获取物理量数字格式
				numFormat = getPmValueNumFormat(meaModel.getPmStdIndex(), meaModel.getUnit());
				
				pmTemplate = (Map) pmTemplateInfoTbl.get(key);

				// 设置偏移量
				tempStr = pmTemplate.get("OFFSET") != null ? pmTemplate.get("OFFSET").toString() : "";			
				meaModel.setOffset(tempStr.isEmpty() ? "" : numFormat.format(Float.valueOf(tempStr)));
				tempStr = pmTemplate.get("UPPER_OFFSET") != null ? pmTemplate.get("UPPER_OFFSET").toString() : "";
				meaModel.setUpperOffset(tempStr.isEmpty() ? "" : numFormat.format(Float.valueOf(tempStr)));
				tempStr = pmTemplate.get("LOWER_OFFSET") != null ? pmTemplate.get("LOWER_OFFSET").toString() : "";
				meaModel.setLowerOffset(tempStr.isEmpty() ? "" : numFormat.format(Float.valueOf(tempStr)));
				
				// 获取、设置上下限值(SDH 光功率)
				if (isGetLimitValFromOptStd(meaModel.getPmStdIndex(), domain) && optStdId != null) {
					if (pmStdOptPortTbl.containsKey(String.valueOf(optStdId))) {
						Map optPortObj = (Map) pmStdOptPortTbl.get(String.valueOf(optStdId));
						if (meaModel.getPmStdIndex().contains("TPL")) {
							tempStr = optPortObj.get("MIN_OUT") != null ? optPortObj.get("MIN_OUT").toString() : "";
							meaModel.setLowerValue(tempStr.isEmpty() ? "" : numFormat.format(Float.valueOf(tempStr)));
							tempStr = optPortObj.get("MAX_OUT") != null ? optPortObj.get("MAX_OUT").toString() : "";
							meaModel.setUpperValue(tempStr.isEmpty() ? "" : numFormat.format(Float.valueOf(tempStr)));
						}
						else if (meaModel.getPmStdIndex().contains("RPL")) {
							tempStr = optPortObj.get("MIN_IN") != null ? optPortObj.get("MIN_IN").toString() : "";
							meaModel.setLowerValue(tempStr.isEmpty() ? "" : numFormat.format(Float.valueOf(tempStr)));
							tempStr = optPortObj.get("MAX_IN") != null ? optPortObj.get("MAX_IN").toString() : "";
							meaModel.setUpperValue(tempStr.isEmpty() ? "" : numFormat.format(Float.valueOf(tempStr)));
						}
					} // 有对应光口标准
				} 
				// 获取、设置上下限值(温度)
				if (isGetLimitValFromCompare(meaModel.getPmStdIndex())) {
					tempStr = pmTemplate.get("UPPER") != null ? pmTemplate.get("UPPER").toString() : "";
					meaModel.setUpperValue(tempStr.isEmpty() ? "" : numFormat.format(Float.valueOf(tempStr)));
					tempStr = pmTemplate.get("LOWER") != null ? pmTemplate.get("LOWER").toString() : "";
					meaModel.setLowerValue(tempStr.isEmpty() ? "" : numFormat.format(Float.valueOf(tempStr)));
				}
				
				// 获取性能值
				pmValue = (meaModel.getValue() != null && !meaModel.getValue().isEmpty() &&
						   !meaModel.getValue().equals(INVALID_PM_VALUE)) ? Float.valueOf(meaModel.getValue()) : null;
				// 获取上限值
				upperValue = (meaModel.getUpperValue() != null && !meaModel.getUpperValue().isEmpty()) ?
						Float.valueOf(meaModel.getUpperValue()) : null;
				// 获取上限偏差
				upperOffset = meaModel.getUpperOffset().isEmpty() ? null : Float.valueOf(meaModel.getUpperOffset());
				// 获取下限值
				lowerValue = (meaModel.getLowerValue() != null && !meaModel.getLowerValue().isEmpty()) ?
						Float.valueOf(meaModel.getLowerValue()) : null;			
				// 获取下限偏差
				lowerOffset = meaModel.getLowerOffset().isEmpty() ? null : Float.valueOf(meaModel.getLowerOffset());
				// 获取基准值
				compareValue = (meaModel.getPmCompareValue() != null && !meaModel.getPmCompareValue().isEmpty()) ?
						Float.valueOf(meaModel.getPmCompareValue()) : null;
				// 获取基准偏差
				offset = meaModel.getOffset().isEmpty() ? null : Float.valueOf(meaModel.getOffset());			

				// 设置异常等级
				if (upperValue != null &&
						lowerValue != null &&
						pmValue != null &&
						pmValue !=Float.parseFloat(UNUSED_POWER_VALUE) &&
						((pmValue >= upperValue) || (pmValue <= lowerValue))) {
					// 重要预警
					meaModel.setExceptionLv(DataCollectDefine.EXCEPTION_LEVLE_CR);
					// 设置连续异常计数器
					setContinueErrCounter(emsConnectionId,neId, unitId, ptpId, otnCtpId, sdhCtpId, meaModel, rtrvTime);
					// 设置显示用性能比较值
					meaModel.setDisplayCompareValue(meaModel.getLowerValue() + "~" + meaModel.getUpperValue());

				} else if (upperValue != null &&
						upperOffset != null &&
						lowerValue !=null &&
						lowerOffset != null &&
						pmValue != null &&
						pmValue !=Float.parseFloat(UNUSED_POWER_VALUE) &&
						((pmValue >= (upperValue - upperOffset)) ||
								(pmValue <= (lowerValue + lowerOffset)))) {
					// 次要预警
					meaModel.setExceptionLv(DataCollectDefine.EXCEPTION_LEVLE_MN);
					// 设置连续异常计数器
					setContinueErrCounter(emsConnectionId,neId, unitId, ptpId, otnCtpId, sdhCtpId, meaModel, rtrvTime);
					// 设置显示用性能比较值
					meaModel.setDisplayCompareValue(numFormat.format(lowerValue+lowerOffset) + "~" +
							numFormat.format(upperValue - upperOffset));

				} else if (compareValue != null &&
						offset != null &&
						pmValue != null &&
						((pmValue >= (compareValue + offset)) ||
								(pmValue <= (compareValue - offset)))) {
					// 一般预警
					meaModel.setExceptionLv(DataCollectDefine.EXCEPTION_LEVLE_WR);
					// 设置连续异常计数器
					setContinueErrCounter(emsConnectionId,neId, unitId, ptpId, otnCtpId, sdhCtpId, meaModel, rtrvTime);
					// 设置显示用性能比较值
					meaModel.setDisplayCompareValue(numFormat.format(compareValue - offset) + "~" +
							numFormat.format(compareValue + offset));

				} else {
					// 正常数据
					meaModel.setExceptionLv(DataCollectDefine.EXCEPTION_LEVLE_NORMAL);
					// 设置连续异常计数器
					meaModel.setExceptionCount(0);
					// 设置显示用性能比较值
					if (compareValue != null &&
							offset != null) {
						meaModel.setDisplayCompareValue(numFormat.format(compareValue - offset) + "~" +
							numFormat.format(compareValue + offset));
					}
				}

			} // 判断PM参数是否进行分析
		} // 是否配置了分析模版
	}

	/**
	 * 设置连续异常计数值
	 * 
	 * @param neId   网元ID
	 * @param ptpId   PTP ID
	 * @param otnCtpId   OTN的CTP ID
	 * @param sdhCtpId   SDH的CTP ID
	 * @param meaModel   测量数据模版
	 * @param rtrvTime   PM数据读取时间
	 */
	// @IMethodLog(desc = "DataCollectService：设置连续异常计数器")
	private void setContinueErrCounter(int emsConnectionId,int neId, Integer unitId, Integer ptpId, Integer otnCtpId,
			Integer sdhCtpId, PmMeasurementModel meaModel, Date rtrvTime) {
		String tableName;
		SimpleDateFormat dateFormat = CommonUtil
				.getDateFormatter(DataCollectDefine.COMMON_FORMAT);

		if (rtrvTime == null) {
			return;
		}
		// 设定读取日期为当前采集数据日期的前一天
		Calendar rtrvCal = Calendar.getInstance();		
		rtrvCal.setTime(rtrvTime);
		rtrvCal.add(Calendar.DAY_OF_MONTH, -1);
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM");
		// 组装历史性能表的名称
		tableName = "T_PM_ORIGI_DATA_" + emsConnectionId + "_" + formatter.format(rtrvCal.getTime());
		// 判断历史性能表是否存在
		Integer tblIsExistent = dataCollectMapper.getPmTableExistance(tableName, BeanUtil.getDataBaseName());		
		if (tblIsExistent != null && tblIsExistent == PM_ORI_TABLE_IS_EXISTENT ) { // 性能表存在
			String beginDate;
			String endDate;
			Calendar beginCal = Calendar.getInstance();
			Calendar endCal = Calendar.getInstance();
			
			// 设置查询的时间区间
			beginCal.setTime(rtrvCal.getTime());
			beginCal.set(Calendar.HOUR_OF_DAY, 0);
			beginCal.set(Calendar.MINUTE, 0);
			beginCal.set(Calendar.SECOND, 0);
			endCal.setTime(rtrvCal.getTime());
			endCal.set(Calendar.HOUR_OF_DAY, 23);
			endCal.set(Calendar.MINUTE, 59);
			endCal.set(Calendar.SECOND, 59);
			beginDate = dateFormat.format(beginCal.getTime());
			endDate = dateFormat.format(endCal.getTime());
			// 获取特定的历史性能
			int exceptionCount;
			List<Map> hisPmList = dataCollectMapper.selectExcCountByPtpCtpIdAndStdIndexAndTime(tableName, unitId,
												ptpId, otnCtpId, sdhCtpId, meaModel.getPmStdIndex(),
												meaModel.getLocationFlag(), beginDate, endDate);
			// 设置连续异常计数器
			if (hisPmList != null && hisPmList.size() >= 1) {
				Object countObj = hisPmList.get(0).get("EXCEPTION_COUNT");
				exceptionCount = countObj != null ? Integer.valueOf(countObj.toString()) : 0;
				meaModel.setExceptionCount(exceptionCount + 1);
			} else {
				// 指定对象的历史性能值不存在
				meaModel.setExceptionCount(1);
			}
		} else { // 指定日期的历史性能表不存在
			meaModel.setExceptionCount(1);
		}
	}

	/**
	 * 判断标准性能参数名是否为通道的性能
	 * 
	 * @param pmStdIndex
	 * @return true：通道性能 false：非通道性能
	 */
	private boolean isPathPm(String pmStdIndex) {
		boolean result = false;

		if (pathPmList.contains(pmStdIndex)) {
			result = true;
		}

		return result;
	}
	
	/**
	 * 判断是否需要从基准值表中获取上下限值
	 * 
	 * @param pmStdIndex  标准性能参数名称
	 * @return true：需要  false：不需要
	 */
	private boolean isGetLimitValFromCompare(String pmStdIndex) {
		boolean result = false;

		if (limitValInCompareList.contains(pmStdIndex)) {
			result = true;
		}

		return result;
	}
	
	/**
	 * 判断是否需要从光标准表中获取上下限值
	 * 
	 * @param pmStdIndex  标准性能参数名称
	 * @param domain  业务类型
	 * @return true：需要  false：不需要
	 */
	private boolean isGetLimitValFromOptStd(String pmStdIndex, Integer domain) {
		boolean result = false;
		// SDH的接收和发送光功率才需要从光口标准表中获取上下限值
		if (domain == DataCollectDefine.COMMON.DOMAIN_SDH_FLAG &&
				(pmStdIndex.contains("RPL_CUR") ||
				 pmStdIndex.contains("RPL_MAX") ||
				 pmStdIndex.contains("RPL_MIN") ||
				 pmStdIndex.contains("TPL_CUR") ||
				 pmStdIndex.contains("TPL_MAX") ||
				 pmStdIndex.contains("TPL_MIN"))) {
			result = true;
		}
		
		return result;
	}
	
	/**
	 * 根据标准性能参数名返回PM值的数字格式
	 * @param pmStdIndex  标准性能参数名称
	 * @return DecimalFormat  numFormat
	 */
	private DecimalFormat getPmValueNumFormat(String pmStdIndex, String unit) {
		DecimalFormat numFormat;
		// 缺省为计数值的数字格式
		if (pmStdIndex == null) {
			return new DecimalFormat(COUNT_VALUE_NUM_FORMAT);
		}
		
		// 光功率、电流值
		if (pmStdIndex.contains("TPL") ||
				pmStdIndex.contains("RPL") ||
				pmStdIndex.contains("PCLSOP") ||
				(unit != null && (unit.equals("dBm") ||	unit.equals("mA")))) {
			numFormat = new DecimalFormat(OPTICAL_POWER_NUM_FORMAT);
		}
		// 波长	
		else if (pmStdIndex.contains("PCLSWL")) {
			numFormat = new DecimalFormat(WAVELENGTH_NUM_FORMAT);
		}
		// 信噪比、温度
		else if (pmStdIndex.contains("PCLSSNR") ||
				pmStdIndex.contains("TEMP")) {
			numFormat = new DecimalFormat(SNR_NUM_FORMAT);
		}
		// FEC纠错前后误码率
		else if (pmStdIndex.contains("FEC")) {
			numFormat = new DecimalFormat(FEC_NUM_FORMAT);
		}
		// 计数值
		else {
			numFormat = new DecimalFormat(COUNT_VALUE_NUM_FORMAT);
		}
		
		return numFormat;
	}
	
	/**
	 * 判断网管是否可以采集
	 * @return
	 * @throws CommonException
	 */
	private void IsEmsCanCollect(Map paramter,int collectLevel) throws CommonException {
		// 采集命令与判断机制
		switch(getCollectStatus(paramter)){
		//禁止采集
		case DataCollectDefine.COLLECT_STATUS_FORBIDDEN:
			//采集等级为1即手工采集，无视禁止采集
			if(collectLevel == DataCollectDefine.COLLECT_LEVEL_1){
				return;
			}else{
				throw new CommonException(new NullPointerException(),
						MessageCodeDefine.CORBA_COLLECT_FOBIDDEN_EXCEPTION); 
			}
		//暂停采集
		case DataCollectDefine.COLLECT_STATUS_PAUSE:
			//暂停时间不为空，且大于当前时间则还在暂停状态
			if(getForbiddenTimeLimit(paramter)!=null&&getForbiddenTimeLimit(paramter).after(new Date())){
				//采集等级为1即手工采集，无视暂停
				if(collectLevel == DataCollectDefine.COLLECT_LEVEL_1){
					return;
				}else{
					throw new CommonException(new NullPointerException(),
							MessageCodeDefine.CORBA_COLLECT_PAUSE_EXCEPTION); 
				}
			}else{
				//更新网管采集状态为等待执行
				Map emsConnection = new HashMap<String, String>();

				emsConnection.put("COLLECT_STATUS", DataCollectDefine.COLLECT_STATUS_WAITING);
				emsConnection.put("IP", getIp(paramter));
				dataCollectMapper.updateEmsConnectionByIP(emsConnection);
			}
			break;
			default:
				break;
		}
	}
	
	
	/**
	 * 判断网元是否可以采集
	 * @param ne
	 * @return
	 * @throws CommonException
	 */
	private void IsNeCanCollect(Map ne, int collectType) throws CommonException {
		// 判断网元通信状态
		// 通信状态
		Integer communicationState = ne.get("COMMUNICATION_STATE") != null ? Integer
				.valueOf(ne.get("COMMUNICATION_STATE").toString()) : null;
		if (communicationState != null
				&& communicationState.intValue() == DataCollectDefine.NE_COMMUNICATION_STATE_UNAVAILABLE) {
			// 检查离线状态是否可以采集
			if (checkList.contains(collectType)) {
				throw new CommonException(
						new NullPointerException(),
						MessageCodeDefine.CORBA_COLLECT_NE_UNAVAILABLE_EXCEPTION);
			}
		}

		// 获取网元类型
		Integer neType = ne.get("TYPE") != null ? Integer.valueOf(ne
				.get("TYPE").toString())
				: DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG;
		// 未知网元类型不能同步
		if (neType != null
				&& neType.intValue() == DataCollectDefine.COMMON.NE_TYPE_UNKNOW_FLAG) {
			throw new CommonException(new NullPointerException(),
					MessageCodeDefine.CORBA_UNKNOW_NE_TYPE_EXCEPTION);
		}
	}

	/**
	 * 命令优先级判断 规则 命令优先级最高，时间最小的优先执行
	 * 
	 * @param commandModel
	 * @return
	 */
	private synchronized boolean IsHighestCommand(Map paramter,
			CommandModel commandModel) {
		int emsConnectionId = Integer.valueOf(paramter.get("BASE_EMS_CONNECTION_ID").toString());
		int maxThreads = (paramter.get("THREAD_NUM")+"").matches("\\d+")?Integer.valueOf(paramter.get("THREAD_NUM")+""):1;
		// 取得命令优先级对象
		CommandPriorityModel commandPriorityModel = commandPriorityMap
				.get(emsConnectionId);

		// 第一次进入
		if (commandPriorityModel == null) {
			// 创建对象
			commandPriorityModel = new CommandPriorityModel(maxThreads);
			commandPriorityMap.put(emsConnectionId, commandPriorityModel);
		}
		commandPriorityModel.setMaxThreads(maxThreads);
		// 命令列表中加入命令
		commandPriorityModel.addCmd(commandModel);
		return commandPriorityModel.activeCmd(commandModel);
	}
	
	//获取网管Id
	private Integer getEmsConnectionId(Map paramter){
		Integer target = Integer.valueOf(paramter.get("BASE_EMS_CONNECTION_ID").toString());
		return target;
	}
	//获取网管类型
	private Integer getType(Map paramter){
		Integer target = Integer.valueOf(paramter.get("TYPE").toString());
		return target;
	}
	//获取厂家
	private Integer getFactory(Map paramter){
		Integer target = Integer.valueOf(paramter.get("FACTORY").toString());
		return target;
	}
	//获取采集状态
	private Integer getCollectStatus(Map paramter){
		Integer target =paramter.get("COLLECT_STATUS") != null ? (Integer) paramter
				.get("COLLECT_STATUS"): DataCollectDefine.COLLECT_STATUS_WAITING;
		return target;
	}
	//获取超时时间
	private Integer getTimeOut(Map paramter){
		Integer target = paramter.get("TIME_OUT") != null ? (Integer) paramter
				.get("TIME_OUT") : 10;
		return target;
	}
	//获取禁止采集时间
	private Date getForbiddenTimeLimit(Map paramter){
		Date target = paramter.get("FORBIDDEN_TIME_LIMIT") != null ? (Date) paramter
				.get("FORBIDDEN_TIME_LIMIT") : null;
		return target;
	}
	//获取网管编码
	private String getEncode(Map paramter){
		String target = (String) paramter.get("ENCODE");
		if (paramter == null || paramter.isEmpty()) {
			String sysEncoding = System.getProperty("file.encoding");
			if (sysEncoding == null || sysEncoding.isEmpty()) {
				sysEncoding = DataCollectDefine.ENCODE_GBK;
			}
			target = sysEncoding;
		}
		return target;
	}
	//获取内部ems名
	private String getIntenalEmsName(Map paramter){
		String target = paramter.get("INTERNAL_EMS_NAME") != null ? (String) paramter
				.get("INTERNAL_EMS_NAME") : (String) paramter.get("EMS_NAME");
		return target;
	}
	//获取网管Ip
	private String getIp(Map paramter){
		String target = paramter.get("IP").toString();
		return target;
	}
	
	/**
	 * 返回告警原始信息Json字符串中指定Key名称的值
	 * @param msg 原始告警信息
	 * @param key 指定的字段名称
	 * @return 指定字段的值
	 */
	static String getValueOfKey(String msg, String key) {
		String result = "";
		
		int target = msg.indexOf(key);
		if (target >= 0) {
			int s = msg.indexOf(':', target) + 1;
			String flag = msg.substring(s, s+1);
			if (flag.equals("\"")) {
				result = msg.substring(s+1, msg.indexOf('"', s+1));
			} else if (flag.equals("[")) {
				result = msg.substring(s, msg.indexOf(']')+1);
			} else {
				String temp = msg.substring(s, msg.length()-1);
				int index_brace = temp.indexOf('}');
				int index_comma = temp.indexOf(',');
				if (index_brace > index_comma) {
					result = temp.substring(0, index_comma);
				} else {
					result = temp.substring(0, index_brace);
				}
			}			
		}
		
		return result;
	}

	public static void main(String args[]) throws KettleException {

//		float a = 1000.52f;
//		System.out.println("a="+a+";"+numFormat.format(a));
//		float b = Float.NaN;
//		String str = String.valueOf(b);
//		if ("NaN".equals(str)) {
//			System.out.println(b);
//		} else {System.out.println("nan");}

//		int xxx = 20;
//		System.out.println(checkList.contains(xxx));
//		int asdf = 16;
//		System.out.println(checkList.contains(asdf));
//		while(true){
//			System.out.println("xxxx");
//		}

//		Map<String, String> map = new HashMap<String, String>();
//
//		AlarmDataModel model = new AlarmDataModel();
//		model.setEmsConnectionId(1);
//		model.setObjectType(6);
//		DataCollectServiceImpl tt = new DataCollectServiceImpl();
//
//		Map alarm = tt.getAlarmAdditionInfo(model);
//		System.out.println(alarm.toString());
		// List<String> temp = null;
		// List<String> xxx = new ArrayList<String>();
		// xxx.add("1");
		// xxx.add("2");
		// xxx.add("3");
		// xxx.add("4");
		// xxx.add("5");
		// xxx.add("6");
		// xxx.add("7");
		// xxx.add("8");
		// xxx.add("9");
		// xxx.add("10");
		//
		// int start = 0;
		// int limit = 1;
		//
		// while (xxx.size() > start) {
		//
		// if (start + limit > xxx.size()) {
		// temp = xxx.subList(start, xxx.size());
		// } else {
		// temp = xxx.subList(start, start + limit);
		// }
		// for (String x : temp) {
		// System.out.println(x);
		// }
		// start = start + limit;
		// }

		// Date date = new Date();
		// CommandModel xxx = null;
		//
		// Queue<CommandModel> commandList = new PriorityQueue<CommandModel>();
		//
		// xxx = new CommandModel("woshi1",1,2,CommonUtil.getSpecifiedDay(date,
		// 1, 0));
		//
		// commandList.add(xxx);
		//
		// xxx = new CommandModel("woshi2",1,1,CommonUtil.getSpecifiedDay(date,
		// -1, 0));
		//
		// commandList.add(xxx);
		//
		// xxx = new CommandModel("woshi3",1,2,CommonUtil.getSpecifiedDay(date,
		// -2, 0));
		//
		// commandList.add(xxx);
		//
		// for(CommandModel aaa:commandList){
		// System.out.println(aaa.getName());
		// }
		// System.out.println("xxxxxxxxxx");
		// // System.out.println(commandList.peek().getName());
		// System.out.println(commandList.peek().getName());
		//
		// while(!commandList.isEmpty()){
		// System.out.println(commandList.remove().getName());
		// }
		
		//获取配置ip地址
//		String ftpIp = CommonUtil.getSystemConfigProperty(DataCollectDefine.FTP_IP);
//		if(ftpIp.equals(CommonUtil.getLocalHostName())){
//			InetAddress host=CommonUtil.getLocalHost("192.3.1.6");
//			if(host!=null)
//				ftpIp = host.getHostAddress();
//		}
//		System.out.println("FTP连接IP地址："+ftpIp);
//		String t = "EquipmentHolder:rack=1/shelf=1/slot=7/sub-slot=2:Equipment:1";
//		t = t.replaceFirst("/sub-slot="+"\\d*", "");
//		System.out.println(t);
		
//		String jobPath = "./kettle/PTN_REPORT.kjb";
//		KettleEnvironment.init();
//		// jobname 是Job脚本的路径及名称
//		JobMeta jobMeta = new JobMeta(this.getClass().getClassLoader().getResourceAsStream("kettle/PTN_REPORT.kjb"), null,null);
//
//
//		Job job = new Job(null, jobMeta);

	}

}
