package com.fujitsu.manager.dataCollectManager.serviceImpl.LUCENTCorba;

import globaldefs.NameAndStringValue_T;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import LUCENT.CosNotification.StructuredEvent;
import LUCENT.emsMgr.EMS_T;
import LUCENT.equipment.EquipmentOrHolder_T;
import LUCENT.managedElement.ManagedElement_T;
import LUCENT.multiLayerSubnetwork.MultiLayerSubnetwork_T;
import LUCENT.performance.PMData_T;
import LUCENT.performance.PMTPSelect_T;
import LUCENT.protection.ProtectionGroup_T;
import LUCENT.terminationPoint.TerminationPoint_T;
import LUCENT.topologicalLink.TopologicalLink_T;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.DataCollectDefine;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.handler.ExceptionHandler;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.AlarmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ClockSourceStatusModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.CrossConnectModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EProtectionGroupModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EmsDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EquipmentOrHolderModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.EthServiceModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.MSTPBindingPathModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ManagedElementModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ProtectionGroupModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TerminationPointModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TopologicalLinkModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.VirtualBridgeModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.WDMProtectionGroupModel;
import com.fujitsu.manager.dataCollectManager.service.EMSCollectService;
import com.fujitsu.manager.dataCollectManager.serviceImpl.VEMS.ILUCENTEMSSession;
import com.fujitsu.manager.dataCollectManager.serviceImpl.VEMS.LUCENTVEMSSession;
import com.fujitsu.model.HistoryPmFileGetResult;
import com.fujitsu.util.CommonUtil;
import com.fujitsu.util.FtpUtils;

public class LUCENTServiceImpl extends EMSCollectService {
	
	boolean isVirtualEms = true;
	private ILUCENTEMSSession emsSession;

	@IMethodLog(desc = "DataCollectService：lucent初始化连接参数")
	public void initParameter(String corbaName, String corbaPassword,
			String corbaIp, String corbaPort, String emsName,
			String internalEmsName,String encode, int iteratorNum) throws CommonException
	{
		super.initParameter(corbaName, corbaPassword,
				corbaIp, corbaPort, emsName,
				internalEmsName);
		
		lucentDataToModel = new LUCENTDataToModel(encode);
		
		isVirtualEms = DataCollectDefine.EMS_NAME_VEMS.equals(emsName);
		this.emsSession = isVirtualEms?
				LUCENTVEMSSession.newInstance(corbaName, corbaPassword, corbaIp,
						corbaPort, emsName, encode):
				LUCENTEMSSession.newInstance(corbaName, corbaPassword, corbaIp,
				corbaPort, emsName,encode, iteratorNum);
	}
	
	/**
	 * lucent启动 corba连接*/
	public int startCorbaConnect() throws CommonException {
		
		int result;

		//启动连接
		if (CommonUtil.isReachable(corbaIp)) {
			result = DataCollectDefine.CONNECT_STATUS_EXCEPTION_FLAG;
			emsSession.connect();
			result = DataCollectDefine.CONNECT_STATUS_NORMAL_FLAG;
		} else {
			result = DataCollectDefine.CONNECT_STATUS_INTERRUPT_FLAG;
		}
		return result;
	}

	@IMethodLog(desc = "DataCollectService：LUCENT启动telnet连接")
	public int startTelnetConnect(int neId, boolean isGateWayNe)
			throws CommonException {
		// TODO Auto-generated method stub
		return 0;
	}	
	

	@IMethodLog(desc = "DataCollectService：LUCENT断开corba连接")
	public boolean disCorbaConnect() throws CommonException {

		boolean result = true;

		try {
			emsSession.endSession();
		} catch (Exception e) {
			result = false;
		}
		return result;
	}
	
	@IMethodLog(desc = "DataCollectService：LUCENT断开telnet连接")
	public boolean disTelnetConnect(int neId, boolean isGateWayNe)
			throws CommonException {
		// TODO Auto-generated method stub
		return false;
	}
	
		
	@IMethodLog(desc = "DataCollectService：LUCENT获取网管信息")
	public EmsDataModel getEMS() throws CommonException {

		EMS_T ems = emsSession.getEMS();

		EmsDataModel data = lucentDataToModel.EmsDataToModel(ems);

		return data;
	}
	
	@IMethodLog(desc = "DataCollectService：HW获取所有网元名称列表")
	public NameAndStringValue_T [][] getAllManagedElementNames()
			throws CommonException {

		NameAndStringValue_T [][] data = emsSession.getAllManagedElementNames();

		return data;
	}
	
	@IMethodLog(desc = "DataCollectService：LUCENT获取所有网元列表")
	public List<ManagedElementModel> getAllManagedElements()
			throws CommonException
	{

		List<ManagedElementModel> data = new ArrayList<ManagedElementModel>();

		ManagedElement_T[] mes = emsSession.getAllManagedElements();

		for (ManagedElement_T me : mes)
		{
			ManagedElementModel model = lucentDataToModel.ManagedElementDataToModel(me);
			data.add(model);
		}
		return data;
	}


	// 取得网元所属的设备信息 包含shelf slot equip ptp 返回失败网元Id
	public List<EquipmentOrHolderModel> getAllEquipment(String Name) throws CommonException
	{
		// 组装网元名
		List<EquipmentOrHolderModel> data = new ArrayList<EquipmentOrHolderModel>();
		
		NameAndStringValue_T[] neName = nameUtil.constructNeName(internalEmsName, Name);			
		// 取得网元所属的设备信息
		EquipmentOrHolder_T[] equipmentOrHolderList = emsSession.getAllEquipment(neName);
		// 取得网元所属的ptp信息
		//TerminationPoint_T[] ptpTempList = emsSession.getAllPTPs(neName);
		// 判断是否正确取得数据
		if (equipmentOrHolderList == null)
		{
			System.out.println("设备信息取得失败！网元:" + Name);
			return data;
		} 
		else 
		{
			//LUCENT无rack类型,但数据库中必须要用rack数据，此处增加一条数据（借用采集的一条数据），把类型修改为rack并增加到库中。
			//取第一条采集的数据
			EquipmentOrHolder_T equipmentOrHolder_temp = equipmentOrHolderList[0];
			//保存该数据的类型，以备后面恢复使用
			String holderType = equipmentOrHolder_temp.holder().holderType;
			//将类型修改为rack
			equipmentOrHolder_temp.holder().holderType = DataCollectDefine.COMMON.RACK;	
			//经过转换后添加到返回列表中
			EquipmentOrHolderModel model_temp = lucentDataToModel
					.EquipmentOrHolderDataToModel(equipmentOrHolder_temp);
			data.add(model_temp);
			
			//用之前保存的类型恢复
			equipmentOrHolder_temp.holder().holderType = holderType;
//			以下为正常逻辑处理
			for (EquipmentOrHolder_T equipmentOrHolder : equipmentOrHolderList)
			{
				EquipmentOrHolderModel model = lucentDataToModel
						.EquipmentOrHolderDataToModel(equipmentOrHolder);
				data.add(model);
			}
			return data;
		}
	}

	@IMethodLog(desc = "DataCollectService：LUCENT获取网元所有ptp信息")
	public List<TerminationPointModel> getAllPTPs(String neName)
			throws CommonException
	{
		List<TerminationPointModel> data = new ArrayList<TerminationPointModel>();
		TerminationPoint_T[] ptps = emsSession.getAllPTPs(nameUtil.constructNeName(internalEmsName, neName));
		for (TerminationPoint_T ptp : ptps)
		{
//			System.out.println("name:"+ptp.name[2].value);
			if(ptp.name[2].value.length() == 0)
				continue;
			
			TerminationPointModel model = lucentDataToModel.TerminationPointDataToModel(ptp);
			data.add(model);
		}
		return data;
	}
	
	@IMethodLog(desc = "DataCollectService：LUCENT获取指定端口CTP信息")
	public List<TerminationPointModel> getContainedPotentialTPs(
			NameAndStringValue_T[] ptpName) throws CommonException
	{

		List<TerminationPointModel> data = new ArrayList<TerminationPointModel>();

		TerminationPoint_T[] tps = emsSession
				.getContainedPotentialTPs(ptpName);

		for (TerminationPoint_T tp : tps)
		{
			TerminationPointModel model = lucentDataToModel
					.TerminationPointDataToModel(tp);
			data.add(model);
		}
		return data;
	}
	
	@IMethodLog(desc = "DataCollectService：LUCENT获取网元保护组信息")
	public List<ProtectionGroupModel> getAllProtectionGroups(String neName)
			throws CommonException
	{
		ProtectionGroup_T[] dataList = emsSession
		.getAllProtectionGroups(nameUtil.constructNeName(internalEmsName, neName));
		List<ProtectionGroupModel> data = new ArrayList<ProtectionGroupModel>();
		
		for (ProtectionGroup_T tempData : dataList) {
			ProtectionGroupModel model = 
				lucentDataToModel.ProtectionGroupDataToModel(tempData);
			data.add(model);
		}
		return data;
	}
	
	@IMethodLog(desc = "DataCollectService：LUCENT获取网元内部交叉连接信息")
	public List<CrossConnectModel> getCRS(String neName,
			short[] connectionRateList) throws CommonException
	{

		List<CrossConnectModel> data = new ArrayList<CrossConnectModel>();

		//朗讯交叉连接后续解析会出问题，BD未明确如何解析数据，先返回空数据
/*		CrossConnect_T[] ccs = emsSession.getAllCrossConnections(nameUtil
				.constructNeName(internalEmsName, neName), connectionRateList);

		System.out.println("CRS:"+ ccs.length);
		for (CrossConnect_T cc : ccs)
		{
			CrossConnectModel model = lucentDataToModel.CCDataToModel(cc);
			data.add(model);
		}*/
		return data;
	}
	
	@IMethodLog(desc = "DataCollectService：LUCENT获取网元间link信息")
	public List<TopologicalLinkModel> getAllTopologicalLinks()
			throws CommonException
	{
		//获取外部link
		List<TopologicalLinkModel> data = getLinkFromTopologicalLinks(1,null);
		
		return data;
	}

	/**
	 * 取得指定网元性能数据
	 * 
	 * @param emsConnectionId
	 *            网络连接Id
	 * @param TaskDetailInfoModel
	 *            必须填写字段 neId,layRate,cycle 网元集合 集合均为同一网管下的网元
	 * @param collectCount
	 *            采集次数
	 * @return 返回取得数据失败网元列表
	 */
	/*
	public List<TaskDetailInfoModel> collectAndStoreData(
			String emsConnectionId, List<TaskDetailInfoModel> neList,
			long collectCount) {
		log.info("LUCENT start collectAndStoreData .....");
		// 取得连接对象
		TEmsConnection connection = (TEmsConnection) commonDAOService
				.getObject(TEmsConnection.class, "emsConnectionId", Long
						.valueOf(emsConnectionId));

		nameUtil = new LUCENTNameAndStringValueUtil(emsConnectionId);

		NameAndStringValue_T[] neName = null;
		String layRateString = null;
		String cycle = null;

		// corba连接参数
		String corbaName = connection.getUserName();
		String corbaPassword = connection.getPassword();
		String corbaIp = connection.getIp();
		String corbaPort = connection.getPort();
		long corbaType = connection.getType();
		String emsFactoryName = connection.getEmsName();

		// 采集间隔时间
		long intervalTime = connection.getIntervalTime();
		// 采集超时时间
		long timeOut = connection.getTimeOut();

		// 采集数据集合
		List<PMData_T> pmDataList = null;

		for (TaskDetailInfoModel taskInfo : neList) 
		{
			//初始化执行状态
			taskInfo.setActionResult(DataCollectDefine.TRUE);
			// 取得neId
			Long neId = Long.valueOf(taskInfo.getNeId());
			// 取得网元对象
			TNe ne = (TNe) commonDAOService.getNe("neId", neId);
			// 取得网元名
			neName = nameUtil.constructNeName(ne.getNeName());
			// 取得层速率字符串
			layRateString = taskInfo.getLayRate();
			// 采集周期
			cycle = taskInfo.getCycle();
			
			//采集开始日期--使用任务的下次采集时间
			SimpleDateFormat parser = DataCollectDefine.getDateFormatter((DataCollectDefine.COMMON_SIMPLE_FORMAT));
			Date belongToDate = new Date();
			try {
				belongToDate = parser.parse(taskInfo.getHistoryPmStartDate());
			} catch (ParseException e) {
				log.error((e));
			}
			
			// 构建ExecutorService对象
			ExecutorService executor = Executors.newFixedThreadPool(5);
			// 构建LUCENT采集进程
			LUCENTCollectCurrentPmThread collectCurrentPmThread = new LUCENTCollectCurrentPmThread(
					corbaName, corbaPassword, corbaIp, corbaPort, corbaType,
					emsFactoryName, neName, cycle,emsConnectionId);
			// 添加采集进程
			FutureTask<List<PMData_T>> future = new FutureTask<List<PMData_T>>(
					collectCurrentPmThread);
			// 执行采集进程
			executor.execute(future);
			try {
				// 设置采集超时时间
				pmDataList = future.get(timeOut * 1000, TimeUnit.MILLISECONDS);
			} catch (TimeoutException e) {
				taskInfo.setErrorReason("采集pm数据异常--数据采集超时！");
				taskInfo.setActionResult(DataCollectDefine.FALSE);
//				if (collectCount == NMSDefine.COLLECT_FLAG_FIRST) {
//
//				} else {
//					System.out.println("采集pm数据超时,网元：" + ne.getNeDisplayName());
//					log.error("采集pm数据超时！目标ID：" + ne.getNeDisplayName());
//				}
				System.out.println("采集pm数据超时,网元：" + ne.getNeDisplayName());
				log.error("采集pm数据超时！目标ID：" + ne.getNeDisplayName());
				continue;
			} 
			catch (InterruptedException e) 
			{
				taskInfo.setErrorReason("采集pm数据异常--InterruptedException！");
				taskInfo.setActionResult(DataCollectDefine.FALSE);
//				if (collectCount == NMSDefine.COLLECT_FLAG_FIRST) {
//
//				} else {
//					System.out.println("InterruptedException:采集pm数据异常！目标网元："
//							+ ne.getNeDisplayName());
//					log.error("InterruptedException:采集pm数据异常！目标网元："
//							+ ne.getNeDisplayName());
//					log.error(Define.getExceptionTrace(e));
//				}
				System.out.println("InterruptedException:采集pm数据异常！目标网元："
						+ ne.getNeDisplayName());
				log.error("InterruptedException:采集pm数据异常！目标网元："
						+ ne.getNeDisplayName());
				log.error(Define.getExceptionTrace(e));
				continue;
			} 
			catch (ExecutionException e) 
			{
				taskInfo.setErrorReason("采集pm数据异常--ExecutionException！");
				taskInfo.setActionResult(DataCollectDefine.FALSE);
//				if (collectCount == NMSDefine.COLLECT_FLAG_FIRST) {
//
//				} else {
//					System.out.println("ExecutionException:采集pm数据异常！目标网元："
//							+ ne.getNeDisplayName());
//					log.error("ExecutionException:采集pm数据异常！目标网元："
//							+ ne.getNeDisplayName());
//					log.error(Define.getExceptionTrace(e));
//				}
				System.out.println("ExecutionException:采集pm数据异常！目标网元："
						+ ne.getNeDisplayName());
				log.error("ExecutionException:采集pm数据异常！目标网元："
						+ ne.getNeDisplayName());
				log.error((e));
				continue;
			} 
			catch (Exception e) 
			{
				taskInfo.setErrorReason("采集pm数据异常--Exception！");
				taskInfo.setActionResult(DataCollectDefine.FALSE);
//				if (collectCount == NMSDefine.COLLECT_FLAG_FIRST) {
//
//				} else {
//					System.out.println("Exception:采集pm数据异常！目标网元："
//							+ ne.getNeDisplayName());
//					log.error("Exception:采集pm数据异常！目标网元："
//							+ ne.getNeDisplayName());
//					log.error(Define.getExceptionTrace(e));
//				}
				System.out.println("Exception:采集pm数据异常！目标网元："
						+ ne.getNeDisplayName());
				log.error("Exception:采集pm数据异常！目标网元："
						+ ne.getNeDisplayName());
				log.error(Define.getExceptionTrace(e));
				continue;
			} 
			finally 
			{
				executor.shutdownNow();
			}

			if (pmDataList != null) 
			{
				// 保存原始数据
				if (!saveOriginalData(pmDataList, connection, neId,
						layRateString, belongToDate)) 
				{
					System.out
							.println("保存pm数据异常！目标网元：" + ne.getNeDisplayName());
					log.error("保存pm数据异常！目标网元：" + ne.getNeDisplayName());
					taskInfo.setErrorReason("保存pm数据异常！");
					taskInfo.setActionResult(Define.FLAG_FALSE);
				}
			} 
			else 
			{
				System.out.println("pm数据为空！目标网元：" + ne.getNeDisplayName());
				log.error("pm数据为空！目标网元：" + ne.getNeDisplayName());
			}

			// 进程休眠指定间隔时间后继续执行采集保存操作
			try 
			{
				Thread.sleep(intervalTime * 1000);
			} 
			catch (InterruptedException e) 
			{
				System.out.println("InterruptedException:进程休眠执行错误!");
				log.error("InterruptedException:进程休眠执行错误!");
			}
		}
		return neList;
	}
*/
	/**
	 * 取得指定网元性能数据
	 * 
	 * @param emsConnectionId
	 *            网络连接Id
	 * @param TaskDetailInfoModel
	 *            必须填写字段 neId,layRate,cycle 网元集合 集合均为同一网管下的网元
	 * @return List<TPmOriginalData>
	 */
	/*
	public boolean getAndStoreCurrentPMData(String emsConnectionId,
			TaskDetailInfoModel needToCollectionNeInfo,Date requestTime) {
		log.info("LUCENT start getCurrentPMData .....");
		// 取得连接对象
		TEmsConnection connection = (TEmsConnection) commonDAOService
				.getObject(TEmsConnection.class, "emsConnectionId", Long
						.valueOf(emsConnectionId));

		nameUtil = new LUCENTNameAndStringValueUtil(emsConnectionId);

		NameAndStringValue_T[] neName = null;
		String layRateString = null;
		String cycle = null;

		// corba连接参数
		String corbaName = connection.getUserName();
		String corbaPassword = connection.getPassword();
		String corbaIp = connection.getIp();
		String corbaPort = connection.getPort();
		long corbaType = connection.getType();
		String emsFactoryName = connection.getEmsName();

		// //采集间隔时间
		// long intervalTime = connection.getIntervalTime();
		// 采集超时时间
		long timeOut = connection.getTimeOut();

		// 采集数据集合
		List<PMData_T> pmDataList = null;

		// 取得neId
		Long neId = Long.valueOf(needToCollectionNeInfo.getNeId());
		// 取得网元对象
		TNe ne = (TNe) commonDAOService.getNe("neId", neId);
		// 取得网元名
		neName = nameUtil.constructNeName(ne.getNeName());
		// 取得层速率字符串
		layRateString = needToCollectionNeInfo.getLayRate();

		// 采集周期
		cycle = needToCollectionNeInfo.getCycle();

		// 构建ExecutorService对象
		ExecutorService executor = Executors.newSingleThreadExecutor();
		// 构建LUCENT采集进程
		LUCENTCollectCurrentPmThread collectCurrentPmThread = new LUCENTCollectCurrentPmThread(
				corbaName, corbaPassword, corbaIp, corbaPort, corbaType,
				emsFactoryName, neName, cycle,emsConnectionId);
		// 添加采集进程
		FutureTask<List<PMData_T>> future = new FutureTask<List<PMData_T>>(
				collectCurrentPmThread);
		// 执行采集进程
		executor.execute(future);
		try {
			// 设置采集超时时间
			pmDataList = future.get(timeOut * 1000, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			System.out.println("采集pm数据超时,网元：" + ne.getNeDisplayName());
			log.error("采集pm数据超时！目标ID：" + ne.getNeDisplayName());
		} catch (InterruptedException e) {
			System.out.println("InterruptedException:采集pm数据异常！目标网元："
					+ ne.getNeDisplayName());
			log.error("InterruptedException:采集pm数据异常！目标网元："
					+ ne.getNeDisplayName());
			log.error(Define.getExceptionTrace(e));
		} catch (ExecutionException e) {
			System.out.println("ExecutionException:采集pm数据异常！目标网元："
					+ ne.getNeDisplayName());
			log.error("ExecutionException:采集pm数据异常！目标网元："
					+ ne.getNeDisplayName());
			log.error(Define.getExceptionTrace(e));
		} catch (Exception e) {
			System.out.println("Exception:采集pm数据异常！目标网元："
					+ ne.getNeDisplayName());
			log.error("Exception:采集pm数据异常！目标网元：" + ne.getNeDisplayName());
			log.error(Define.getExceptionTrace(e));
		} finally {
			executor.shutdownNow();
		}

		if (pmDataList != null) {
			List<TPmCurrentData> TPmCurrentDataList = new ArrayList<TPmCurrentData>();
			// 组装originalData
			WaveConfig waveConfig = new WaveConfig(NMSDefine.WAVE_NONE, 9999,
					false);
			for (PMData_T item:pmDataList) {
				List<TPmOriginalData> originalDataList=getOriginalData(item,
						waveConfig, connection, neId, layRateString, null);
				for(TPmOriginalData obj1:originalDataList){
					TPmCurrentData tPmCurrentData = new TPmCurrentData();
					// tPmCurrentData.setPmOriginalDataId(obj1.getPmOriginalDataId());
					tPmCurrentData.setPtpId(obj1.getTPtp().getPtpId());
					tPmCurrentData.setEmsConnectionId(obj1
							.getTEmsConnection().getEmsConnectionId());
					tPmCurrentData.setLayerRate(obj1.getLayerRate());
					tPmCurrentData.setOchNo(obj1.getOchNo());
					tPmCurrentData.setPmLocation(obj1.getPmLocation());
					tPmCurrentData
							.setGranularity(obj1.getGranularity());
					tPmCurrentData.setPmIndex(obj1.getPmIndex());
					tPmCurrentData.setPmStandardIndex(obj1
							.getPmStandardIndex());
					tPmCurrentData.setPmDescription(obj1
							.getPmDescription());
					tPmCurrentData.setPmValue(obj1.getPmValue());
					tPmCurrentData.setRetrievalTime(obj1
							.getRetrievalTime());
					tPmCurrentData.setRequestTime(requestTime);
					tPmCurrentData.setBelongToDate(obj1
							.getBelongToDate());

					TPmCurrentDataList.add(tPmCurrentData);
				}
				// 将数据保存到当前性能数据存储表中
				commonDAOService.storeObjectList(TPmCurrentDataList);
			}
		}
		return true;
	}
*/
	/**
	 * 取得网元变更列表
	 * 
	 * @param emsConnectionId
	 */
	/*
	public DataModel getNeAlterList(String emsConnectionId) {
		log.info("LUCENT start getNeAlterList .....");
		// 取得emsSession
		ILUCENTEMSSession emsSession = getEmsSession(emsConnectionId);
		// 取得名字组装工具类
		nameUtil = new LUCENTNameAndStringValueUtil(emsConnectionId);

		// 添加查询条件
		List<String> properties = new ArrayList<String>();
		properties.add("TEmsConnection.emsConnectionId");
		properties.add("isDel");
		List<Object> values = new ArrayList<Object>();
		values.add(Long.valueOf(emsConnectionId));
		values.add(Define.FLAG_FALSE);

		// 数据库表中取得网元列表
		List<Object> neInDatabaseList = commonDAOService.getObject(TNe.class,
				properties, values, 0, 0).getRows();

		ManagedElement_T[] neInNmsList = null;

		DataModel data = new DataModel();

		try {
			// 网管上取得网元列表
			neInNmsList = emsSession.getAllManagedElements();
		} catch (Exception e) {

		}
		if (neInNmsList != null) {
			// 保存网元信息
			neInNmsList = syncNeData(neInNmsList, emsConnectionId);
			if (neInNmsList != null) {
				// 取得网元变更列表
				List<Object> rows = new ArrayList<Object>();
				if (neInNmsList != null) {
					rows.addAll(getAddedNe(neInDatabaseList, neInNmsList,
							emsConnectionId));
					rows.addAll(getDeletedNe(neInDatabaseList, neInNmsList,
							emsConnectionId));
				}
				data = new DataModel();
				data.setRows(rows);
				data.setTotal(rows.size());
			}
		}else {
			data.setTotal(-1);
		}
		return data;
	}
*/
	/**
	 * 同步选中的网元
	 * 
	 * @param SyncNeInfoModel
	 *            中应包含neId neSerialNo
	 * @param emsConnectionId
	 */
/*
	public boolean syncSelectedNe(List<SyncNeInfoModel> neList,
			String emsConnectionId) {
		// 进度条
		putProcessParameter(emsConnectionId, "syncNe",0L);
				
		log.info("LUCENT start syncSelectedNe .....");
		boolean result = true;

		// 取得名字组装工具类
		nameUtil = new LUCENTNameAndStringValueUtil(emsConnectionId);

		// 取得连接对象
		TEmsConnection connection = (TEmsConnection) systemDAOService
				.getObject(TEmsConnection.class, "emsConnectionId", Long
						.valueOf(emsConnectionId));

		// 取得emsSession
		ILUCENTEMSSession emsSession = getEmsSession(emsConnectionId);

		try {
			double count = 0;
			// 循环需要同步网元列表，取得基础信息,并保存
			for (int i = 0; i < neList.size(); i++) {
				if (getIsCanceled(emsConnectionId, "syncNe")) {
					// 更新网元同步状态
					updateSyncStatus(neList.get(i), emsConnectionId,
							NMSDefine.SYNC_STATUS_FAILED);
					if (i == neList.size() - 1) {
						// 设置进度百分比
						putProcessParameter(emsConnectionId, "syncNe", 1);
					}
				} else {
					count++;
					// getEqptData
					String syncFailedNe = getEqptData(Long.valueOf(neList
							.get(i).getNeId()), neList.get(i).getNeSerialNo(),
							emsConnectionId, emsSession);
					if (syncFailedNe == null) {
						// 添加子架节点
						addShelfNode(neList.get(i), connection);

						// 添加板卡节点
						addEquipNode(neList.get(i), connection);

						// 添加ptp节点
						addPtpNode(neList.get(i), connection);
						// 更新网元同步状态
						updateSyncStatus(neList.get(i), emsConnectionId,
								NMSDefine.SYNC_STATUS_ON);
					} else {
						// 更新网元同步状态
						updateSyncStatus(neList.get(i), emsConnectionId,
								NMSDefine.SYNC_STATUS_FAILED);
					}

					// 设置进度百分比
					putProcessParameter(emsConnectionId, "syncNe", Double
							.valueOf(count / neList.size()));
				}
			}
			result = true;
		} catch (Exception e) {
			log.error("同步网元异常！" + e);
			log.error(Define.getExceptionTrace(e));
			result = false;
		}

		return result;
	}
*/
	
	/**
	 * 取得指定网元下 指定端口集合的性能值
	 * 
	 * @param List
	 *            <Long> 端口集合
	 * @param String
	 *            周期
	 * @param flag
	 *            获取输入输出光功率：0 获取所有性能数据：1
	 * @return 返回端口集合数据
	 */
/*	
	public List<SortOfCurrentPmData> getSortOfCurrentPmData(
			List<Long> ptpIdList, String cycle, long flag) {

		List<SortOfCurrentPmData> currentPmDataList = null;

		// 端口容器
		List<TPtp> ptpList = new ArrayList<TPtp>();
		// 性能参数容器
		List<PMData_T> pmDataList = new ArrayList<PMData_T>();
		// 需要采集的端口容器
		List<PMTPSelect_T> selectTPs = new ArrayList<PMTPSelect_T>();
		// 朗讯采集数据接口对象
		ILUCENTEMSSession emsSession = null;
		// 连接对象
		TEmsConnection connection = null;

		TPtp tempPtp = null;

		for (Long ptpId : ptpIdList) {
			tempPtp = (TPtp) commonDAOService.getPtp("ptpId", ptpId);

			if (tempPtp == null) {
				continue;
			}
			ptpList.add(tempPtp);
			// 获取连接
			if (connection == null) {
				connection = tempPtp.getTEmsConnection();
			}
			// 获取采集数据接口对象
			if (emsSession == null) {
				// corba连接参数
				String corbaName = connection.getUserName();
				String corbaPassword = connection.getPassword();
				String corbaIp = connection.getIp();
				String corbaPort = connection.getPort();
				long corbaType = connection.getType();
				String emsFactoryName = connection.getEmsName();
				String emsConnectionId = String.valueOf(connection.getEmsConnectionId());
				// 取得emsSession
				if (NMSDefine.GetLUCENTVEMSStatus()) {
					emsSession = new LUCENTVEMSSession(corbaName,
							corbaPassword, corbaIp, corbaPort, corbaType,
							emsFactoryName,emsConnectionId);
				} else {
					emsSession = new LUCENTEMSSession(corbaName, corbaPassword,
							corbaIp, corbaPort, corbaType, emsFactoryName);
				}

				// 获取名称处理工具
				nameUtil = new LUCENTNameAndStringValueUtil(connection
						.getEmsConnectionId().toString());
			}

			// 端口对象组装
			PMTPSelect_T selectTP = new PMTPSelect_T(nameUtil.constructName(
					tempPtp.getPtpName(), tempPtp.getTNe().getNeName()),
					new short[] {},
					// NMSDefine.HW_STATIC_LAYER_RATE_LIST,
					NMSDefine.LUCENT_STATIC_LOCATION_LIST, new String[] { cycle });

			selectTPs.add(selectTP);
		}

		try {
			if (emsSession != null) {
				// 采集当前性能数据
				PMData_T[] pmData = emsSession.getAllCurrentPMData(selectTPs);
				// 筛选数据
				if (pmData != null) {
					for (int j = 0; j < pmData.length; j++) {
						pmDataList.add(pmData[j]);
					}
				}
			}
		} catch (Exception e) {
			log.error(Define.getExceptionTrace(e));
		} finally {
			// emsSession.endSession();
		}

		try {
			// 取得输入输出光功率性能列表
			if (flag == 0) {
				currentPmDataList = new ArrayList<SortOfCurrentPmData>();
			}
			// 取得所有性能列表
			else if (flag == 1) {
				currentPmDataList = getPmDataList(connection
						.getEmsConnectionId().toString(), ptpList, pmDataList);
			}
		} catch (Exception e) {
			log.error(Define.getExceptionTrace(e));
		}
		return currentPmDataList;
	}
*/	
	
	@IMethodLog(desc = "DataCollectService：LUCENT获取网元时钟信息")
	public List<ClockSourceStatusModel> getObjectClockSourceStatus(String neName)
			throws CommonException {
		List<ClockSourceStatusModel> data = new ArrayList<ClockSourceStatusModel>();
		//不支持
		return data;
	}
	
	/**
	 * 取得当前网管告警，并保存进数据库
	 * 
	 * @param emsConnectionId
	 *            网络连接Id
	 * @param targetId
	 *            目标Id
	 * @return
	 */
	@IMethodLog(desc = "DataCollectService：LUCENT获取当前网络告警信息")
	public List<AlarmDataModel> getAllEMSAndMEActiveAlarms()
			throws CommonException {

		List<AlarmDataModel> data = new ArrayList<AlarmDataModel>();

		StructuredEvent[] alarms = emsSession.getAllEMSAndMEActiveAlarms();

		for (StructuredEvent alarm : alarms) {
			String head = alarm.header.fixed_header.event_type.type_name;
			if (DataCollectDefine.COMMON.NT_ALARM.equals(head)) {
				AlarmDataModel model = lucentDataToModel.AlarmDataToModel(alarm);
				data.add(model);
			}
		}
		return data;
	}
	
	@IMethodLog(desc = "DataCollectService：LUCENT获取当前网络告警信息getAllEMSSystemActiveAlarms")
	public List<AlarmDataModel> getAllEMSSystemActiveAlarms()
			throws CommonException {

		List<AlarmDataModel> data = new ArrayList<AlarmDataModel>();

		StructuredEvent[] alarms = emsSession.getAllEMSSystemActiveAlarms();

		for (StructuredEvent alarm : alarms) {
			String head = alarm.header.fixed_header.event_type.type_name;
			if (DataCollectDefine.COMMON.NT_ALARM.equals(head)) {
				AlarmDataModel model = lucentDataToModel.AlarmDataToModel(alarm);
				data.add(model);
			}
		}
		return data;
	}
	
	@IMethodLog(desc = "DataCollectService：LUCENT获取当前网络告警信息getAllActiveAlarms")
	public List<AlarmDataModel> getAllActiveAlarms(String neName)
			throws CommonException {

		List<AlarmDataModel> data = new ArrayList<AlarmDataModel>();

		StructuredEvent[] alarms = emsSession.getAllActiveAlarms(nameUtil
				.constructNeName(internalEmsName, neName));

		for (StructuredEvent alarm : alarms) {
			String head = alarm.header.fixed_header.event_type.type_name;
			if (DataCollectDefine.COMMON.NT_ALARM.equals(head)) {
				AlarmDataModel model = lucentDataToModel.AlarmDataToModel(alarm);
				data.add(model);
			}
		}
		return data;
	}
	
	
	
	@IMethodLog(desc = "DataCollectService:LUCENT确认告警列表")
	public String[] acknowledgeAlarms(List<String> alarmList)
			throws CommonException {
		//Lucent无确认告警列表接口
		//return emsSession.acknowledgeAlarms(alarmList);
		String[] arrayalarm=null;
		return arrayalarm;
		
	}
	
	/**
	 * 从外部link中获取外部link数据或内部link数据
	 * @param linkType 1 外部link 2 内部link
	 * @param neName 目标网元
	 * @return
	 * @throws CommonException
	 */
	private List<TopologicalLinkModel> getLinkFromTopologicalLinks(
			int linkType, String neName) throws CommonException {

		List<TopologicalLink_T> linkListData = new ArrayList<TopologicalLink_T>();
		// 取得子网信息
		MultiLayerSubnetwork_T[] subnetWorks = emsSession
				.getAllTopLevelSubnetworks();
		
		for (MultiLayerSubnetwork_T subnet : subnetWorks) {
			// 取得网络link信息
			TopologicalLink_T[] linkList = emsSession
					.getAllTopologicalLinks(subnet.name);

			for (TopologicalLink_T link : linkList) {
				linkListData.add(link);
			}
		}
		
		List<TopologicalLinkModel> data = new ArrayList<TopologicalLinkModel>();

		for (TopologicalLink_T link : linkListData) {
			TopologicalLinkModel model = lucentDataToModel
					.TopologicalLinkDataToModel(link);
			// 1 外部link 2 内部link
			switch (linkType) {
			case 1:
				if (!model.getaEndNESerialNo()
						.equals(model.getzEndNESerialNo())) {
					data.add(model);
				}
				break;
			case 2:
				// a=z=neName
				if (model.getaEndNESerialNo().equals(model.getzEndNESerialNo())
						&& model.getaEndNESerialNo().equals(neName)) {
					data.add(model);
				}
				break;
			}
		}
		return data;
	}
	
	//采集历史性能并判断是否成功
		private HistoryPmFileGetResult isFileTransferSuccess(String fileName,String neName, String startTime,String endTime,short[] layerRateList, 
				String[] pmLocationList,String[] pmGranularityList,String ip,
				int port,String userName,String password,FtpUtils ftpUtils) throws CommonException{
			
			//历史文件获取结果
			HistoryPmFileGetResult result = new HistoryPmFileGetResult();
			
			String ftpIpAndFileName = constructFtpDestination(ip,port,fileName,DataCollectDefine.FACTORY_LUCENT_FLAG);
			
			try{
				emsSession.getHistoryPMData(nameUtil.constructNeName(internalEmsName, neName),
						ftpIpAndFileName,
						userName, password, startTime, endTime,layerRateList,pmLocationList,pmGranularityList);
			}catch(Exception e){
				//设置返回状态
				String errorMessage = "EMS采集历史性能错误！";
				if(CommonException.class.isInstance(e)){
					errorMessage = ((CommonException)e).getErrorMessage();
				}
				result.setResult(false);
				result.setNeedRecollect(false);
				result.setErrorMessage(errorMessage);
				ExceptionHandler.handleException(e);
				return result;
			}
			//是否传输完成标识
			boolean fileTransferComplete = isFileTransferComplete(ftpUtils, fileName);
			String errorMessage = "性能文件获取失败，对应ftp地址为：" + ip;
			result.setResult(fileTransferComplete);
			result.setNeedRecollect(!fileTransferComplete);
			result.setErrorMessage(errorMessage);
			return result;
		}

	
	@IMethodLog(desc = "DataCollectService:LUCENTHW获取网元当前性能数据信息")
	public List<PmDataModel> getCurrentPmData_Ne(String neName,
			short[] layerRateList, String[] pmLocationList,
			String[] granularityList) throws CommonException {

		// 采集数据集合
		List<PmDataModel> data = new ArrayList<PmDataModel>();
		// 采集当前性能数据
		PMData_T[] pmDataList = emsSession.getAllCurrentPMData(
				nameUtil.constructNeName(internalEmsName, neName), layerRateList,
				pmLocationList, granularityList);
		// 筛选数据
		if (pmDataList != null) {
			for (PMData_T pmData : pmDataList) {
				PmDataModel model = lucentDataToModel.PMDataToModel(pmData);
				data.add(model);
			}
		}
		return data;
	}
	
	@IMethodLog(desc = "DataCollectService：HW获取ptp当前性能数据信息")
	public List<PmDataModel> getCurrentPmData_Ptp(String neName,
			String ptpName, short[] layerRateList, String[] pmLocationList,
			String[] granularityList) throws CommonException {

		// 采集数据集合
		List<PmDataModel> data = new ArrayList<PmDataModel>();
		// 采集当前性能数据
		PMData_T[] pmDataList = emsSession.getAllCurrentPMData(
				nameUtil.constructName(ptpName, internalEmsName, neName),
				layerRateList, pmLocationList, granularityList);
		// 筛选数据
		if (pmDataList != null) {
			for (PMData_T pmData : pmDataList) {
				PmDataModel model = lucentDataToModel.PMDataToModel(pmData);
				data.add(model);
			}
		}
		return data;
	}


	@IMethodLog(desc = "DataCollectService:LUCENT获取ptp集合当前性能数据信息")
	public Map<String, List<PmDataModel>> getCurrentPmData_PtpList(
			List<String> ptpNameList, short[] layerRateList,
			String[] pmLocationList, String[] granularityList)
			throws CommonException {

		Map<String, List<PmDataModel>> data = new HashMap<String, List<PmDataModel>>();

		List<PMTPSelect_T> selectTPList = new ArrayList<PMTPSelect_T>();

		for (String ptpName : ptpNameList) {
			// 端口对象组装
			PMTPSelect_T selectTP = new PMTPSelect_T(nameUtil.constructName(
					ptpName.split("::")[1], internalEmsName, ptpName.split("::")[0]),
					layerRateList, pmLocationList, granularityList);
			selectTPList.add(selectTP);
		}
		// 采集性能数据
		PMData_T[] pmDataList = emsSession.getAllCurrentPMData(selectTPList);

		for (PMData_T pmData : pmDataList) {
			String neName = nameUtil.getNeSerialNo(nameUtil
					.getMeNameFromPtpName(pmData.tpName));
			String ptpName = nameUtil.decompositionName(pmData.tpName);
			String key = neName + "::" + ptpName;
			PmDataModel model = lucentDataToModel.PMDataToModel(pmData);

			if (data.containsKey(key)) {
				data.get(key).add(model);
			} else {
				List<PmDataModel> value = new ArrayList<PmDataModel>();
				value.add(model);
				data.put(key, value);
			}
		}
		return data;
	}
	
	@IMethodLog(desc = "DataCollectService：lucent获取网元历史性能数据信息")
	public List<PmDataModel> getHistoryPmData_Ne(String fileName,
			String neName, String startTime,String endTime,short[] layerRateList, 
			String[] pmLocationList,String[] pmGranularityList,String ip,
			int port,String userName,String password,int emsType,boolean needAnalysisPm) throws CommonException {
		
		//获取网管对应ftp ip键值
		String key = "FTP_"+corbaIp;

		List<PmDataModel> pmDataList = new ArrayList<PmDataModel>();

		//获取ftp工具类
		FtpUtils ftpUtils = new FtpUtils(ip,port,userName,password);
		//先删除文件，以免影响结果
		if(ftpUtils.checkFileExist(fileName)){
			ftpUtils.deleteFile(fileName);
		}
		
		// 是否传输完成结果
		HistoryPmFileGetResult result = isFileTransferSuccess(fileName, neName,
				startTime, endTime, layerRateList, pmLocationList,
				pmGranularityList, ip, port, userName, password,ftpUtils);

		// 如果没有成功上传ftp文件，重新获取一次ip地址，以防修改情况
		if (!result.isResult() && result.isNeedRecollect()) {
			InetAddress host=CommonUtil.getLocalHost(corbaIp);
			if(host!=null){
				//判断新获取的ip是否与之前不同
				if(!host.getHostAddress().equals(ip)){
					ip = host.getHostAddress();
					//重新连接ftp
					ftpUtils = new FtpUtils(ip,port,userName,password);
					//重新采集一次
					result = isFileTransferSuccess(fileName,
						neName, startTime, endTime, layerRateList,
						pmLocationList, pmGranularityList, ip, port,
						userName, password,ftpUtils);
					//如果传送成功 记下新ip地址
					if(result.isResult()){
						CommonUtil.writeFtpIpMappingConfigProperty(key, ip);
					}
				}
			}
		}
		// 传送成功后解析数据
		if (result.isResult()) {
			if(needAnalysisPm){
				pmDataList = getPmDataFromFtp(ip, port, userName, password,
						fileName, DataCollectDefine.FACTORY_LUCENT_FLAG, startTime, endTime);
			}
		} else {
			throw new CommonException(new NullPointerException(),
					MessageCodeDefine.CORBA_FILE_DOWNLOAD_EXCEPTION,
					result.getErrorMessage());
		}
		// 测试用
		// List<PmDataModel> pmDataList = new ArrayList<PmDataModel>();
		return pmDataList;
	}
	
/*	
	public boolean getAndStoreAlarms(String emsConnectionId,long targetId) {

		boolean result = true;

		List<TCurrentAlarm> alarmList = new ArrayList<TCurrentAlarm>();

		TCurrentAlarm alarm = null;
		
		TCurrentAlarm alarmTemp = null;

		// 取得连接对象
		TEmsConnection connection = (TEmsConnection) commonDAOService
				.getObject(TEmsConnection.class, "emsConnectionId", Long
						.valueOf(emsConnectionId));

		// 取得网管信息
		ILUCENTEMSSession emsSession = getEmsSession(emsConnectionId);

		try {
			// 取得告警对象列表
			StructuredEvent[] notifications = emsSession
					.getAllEMSAndMEActiveAlarms();

			if(notifications!=null&&notifications.length>0){
				
				// 初始化当前告警结束时间
				DataModel currentAlarmList = faultDAOService.getAlarmList(Long
						.valueOf(emsConnectionId), true, 0, 0);
				if (currentAlarmList.getRows() != null
						&& currentAlarmList.getRows().size() > 0) {
					for (Object obj : currentAlarmList.getRows()) {
						alarmTemp = (TCurrentAlarm) obj;
						alarmTemp.setEndTimeFtsp(new Date());
						alarmTemp.setEndTime(new Date());

						alarmList.add(alarmTemp);
					}
				}
				faultDAOService.storeObjectList(alarmList);
				
				int count=0;
				// 解析告警信息
				for (StructuredEvent notification : notifications) {
					alarm = constructAlarm(notification, Long
							.valueOf(emsConnectionId), connection.getIp());

					if (alarm != null) {
						// 保存告警
						commonDAOService.storeObject(alarm);
					}
					else{
						count++;
					}
				}
				int allAlarmNum=((notifications==null)?0:notifications.length);
				if(count==0){
					System.out.println("所有告警入库成功，入库告警总数： "+allAlarmNum);
				}
				else{
					System.out.println("部分告警入库失败，网管采集告警总数： "+allAlarmNum+
							", 入库告警数： "+(allAlarmNum-count)+", 舍弃告警数： "+count);
				}
			}else{
				result = false;
			}
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
			log.error(Define.getExceptionTrace(e));
		}
		return result;
	}
*/	
	
	/**
	 * 构建告警对象，目前采取策略是 判断告警序列号和目标两者一致则更新告警信息，如果不一致则为新告警
	 * 
	 * @param notification
	 * @param currentAlarm
	 * @return TCurrentAlarm
	 */
/*	
	public TCurrentAlarm constructAlarm(Object notificationObj,
			long emsConnectionId, String ip) {
		try{
			StructuredEvent notification = (StructuredEvent) notificationObj;
			
			TCurrentAlarm alarm = null;
			// 构造名称工具
			LUCENTNameAndStringValueUtil nameUtil = new LUCENTNameAndStringValueUtil(String
					.valueOf(emsConnectionId));
			// 目标名称
			NameAndStringValue_T[] objectName = NamingAttributes_THelper
					.read(notification.filterable_data[1].value
							.create_input_stream());
			// 目标类型
			ObjectType_T objectType = ObjectType_THelper
					.read(notification.filterable_data[4].value
							.create_input_stream());
			// 告警级别
			PerceivedSeverity_T ps = PerceivedSeverity_THelper
					.read(notification.filterable_data[11].value
							.create_input_stream());
			// 是否对业务造成影响
			ServiceAffecting_T serviceAffecting = ServiceAffecting_THelper
					.read(notification.filterable_data[12].value
							.create_input_stream());
			// // 影响终端端口列表
			// NameAndStringValue_T[] affectedTPList = NamingAttributes_THelper
			// .read(notification.filterable_data[13].value
			// .create_input_stream());
			// 时间类型
			NameAndStringValue_T[] eventType = NamingAttributes_THelper
					.read(notification.filterable_data[15].value
							.create_input_stream());
			//与告警对象唯一指定一条告警信息--仅华为，朗讯适用
			String probableCauseQualifier= notification.filterable_data[10].value.extract_string();
			// 告警目标名称
			String targetName = nameUtil.decompositionName(objectName);
			if(targetName == null){
				return null;
			}
			// 告警序列号
			String alarmSerialNo = eventType[0].value;
	
			alarm = commonDAOService.getCurrentAlarm(emsConnectionId,
					null, targetName, probableCauseQualifier);
			//clear告警未在数据库中找到对应原告警则不入库,解决网管发送无关clear告警问题
			if((alarm == null)&&(ps.value() == PerceivedSeverity_T._PS_CLEARED)){
				return null;
			}
			// 如果alarm为空或前一次同步告警时关联告警目标失败,创建告警对象，填入相关信息
			if (alarm == null||
				alarm.getObjectType()==null||
				alarm.getObjectType().intValue()!=objectType.value()) {
				// 网元告警
				if (objectType.value() == ObjectType_T._OT_MANAGED_ELEMENT) {
					// 取得网元对象
					TNe ne = commonDAOService.getNe(emsConnectionId, targetName);
					if (ne != null) {
						alarm=alarmConstructor(alarm,
								Define.ALARM_OBJECT_TYPE_MANAGED_ELEMENT,
								alarmSerialNo,targetName,ne.getNeId(),ne.getNeId());
					} else if (alarm==null){
						alarm=alarmConstructor(alarm,
								Define.ALARM_OBJECT_TYPE_EMS,
								alarmSerialNo,targetName,emsConnectionId,null);
					}
				}
				// ptp告警
				else if (objectType.value() == ObjectType_T._OT_PHYSICAL_TERMINATION_POINT) {
					// 取得网元序列号
					String neSerialNo = nameUtil.getNeSerialNo(nameUtil
							.getMeNameFromPtpName(objectName));
					// 取得网元对象
					TNe ne = commonDAOService.getNe(emsConnectionId, neSerialNo);
					if (ne != null) {
						// 取得ptp对象
						TPtp ptp = commonDAOService
								.getPtp(ne.getNeId(), targetName);
						if (ptp != null) {
							alarm=alarmConstructor(alarm,
									Define.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT,
									alarmSerialNo,targetName,ptp.getPtpId(),ne.getNeId());
						} else if (alarm==null){//未找到关联的ptp则将告警关联到网元
							alarm=alarmConstructor(alarm,
									Define.ALARM_OBJECT_TYPE_MANAGED_ELEMENT,
									alarmSerialNo,targetName,ne.getNeId(),ne.getNeId());
						}
					} else if (alarm==null){//未找到关联的网元则将告警关联到网管
						alarm=alarmConstructor(alarm,
								Define.ALARM_OBJECT_TYPE_EMS,
								alarmSerialNo,targetName,emsConnectionId,null);
					}
				}
				// ctp告警
				else if (objectType.value() == ObjectType_T._OT_CONNECTION_TERMINATION_POINT) {
					NameAndStringValue_T[] ptpName = nameUtil
							.getPtpNameFromTpName(objectName);
		
					String ptpNameString = nameUtil.decompositionName(ptpName);
		
					String ctpValue = nameUtil.getCtpValueFromCtpName(objectName);
		
					// 取得网元序列号
					String neSerialNo = nameUtil.getNeSerialNo(nameUtil
							.getMeNameFromPtpName(ptpName));
					// 取得网元对象
					TNe ne = commonDAOService.getNe(emsConnectionId, neSerialNo);
					if (ne != null) {
						// 取得ptp对象
						TPtp ptp = commonDAOService.getPtp(ne.getNeId(),
								ptpNameString);
						if (ptp != null) {
							TCtp ctp = commonDAOService.getCtp(ptp.getPtpId(),ctpValue);
	
							if (ctp != null) {
								alarm=alarmConstructor(alarm,
										Define.ALARM_OBJECT_TYPE_CONNECTION_TERMINATION_POINT,
										alarmSerialNo,targetName,ctp.getCtpId(),ne.getNeId());
							} else if (alarm==null){
								alarm=alarmConstructor(alarm,
										Define.ALARM_OBJECT_TYPE_PHYSICAL_TERMINATION_POINT,
										alarmSerialNo,targetName,ptp.getPtpId(),ne.getNeId());
							}
						} else if (alarm==null){
							alarm=alarmConstructor(alarm,
									Define.ALARM_OBJECT_TYPE_MANAGED_ELEMENT,
									alarmSerialNo,targetName,ne.getNeId(),ne.getNeId());
						}
					} else if (alarm==null){//未找到关联的网元则将告警关联到网管
						alarm=alarmConstructor(alarm,
								Define.ALARM_OBJECT_TYPE_EMS,
								alarmSerialNo,targetName,emsConnectionId,null);
					}
				}
				// 板卡容器告警
				else if (objectType.value() == ObjectType_T._OT_EQUIPMENT_HOLDER) {
					if(targetName.contains(NMSDefine.SLOT)){
						// 取得网元序列号
						String neSerialNo = nameUtil.getNeSerialNo(nameUtil
								.getMeNameFromSlotName(objectName));
						// 取得网元对象
						TNe ne = commonDAOService.getNe(emsConnectionId, neSerialNo);
						if (ne != null) {
		
							// 取得slot对象
							TSlot slot = commonDAOService.getSlot(ne.getNeId(),
									targetName);
							if (slot != null) {
								alarm=alarmConstructor(alarm,
										Define.ALARM_OBJECT_TYPE_SLOT,
										alarmSerialNo,targetName,slot.getSlotId(),ne.getNeId());
							} else if (alarm==null){
								alarm=alarmConstructor(alarm,
										Define.ALARM_OBJECT_TYPE_MANAGED_ELEMENT,
										alarmSerialNo,targetName,ne.getNeId(),ne.getNeId());
							}
						} else if (alarm==null){//未找到关联的网元则将告警关联到网管
							alarm=alarmConstructor(alarm,
									Define.ALARM_OBJECT_TYPE_EMS,
									alarmSerialNo,targetName,emsConnectionId,null);
						}
					}
					else if(targetName.contains(NMSDefine.SHELF)){
						// 取得网元序列号
						String neSerialNo = nameUtil.getNeSerialNo(nameUtil
								.getMeNameFromShelfName(objectName));
						// 取得网元对象
						TNe ne = commonDAOService.getNe(emsConnectionId, neSerialNo);
						if (ne != null) {
							// 取得shelf对象
							TShelf shelf = commonDAOService.getShelf(ne.getNeId(),
									targetName);
							if (shelf != null) {
								alarm=alarmConstructor(alarm,
										Define.ALARM_OBJECT_TYPE_SHELF,
										alarmSerialNo,targetName,shelf.getShelfId(),ne.getNeId());

							} else if (alarm==null){
								alarm=alarmConstructor(alarm,
										Define.ALARM_OBJECT_TYPE_MANAGED_ELEMENT,
										alarmSerialNo,targetName,ne.getNeId(),ne.getNeId());
							}
						} else if (alarm==null){//未找到关联的网元则将告警关联到网管
							alarm=alarmConstructor(alarm,
									Define.ALARM_OBJECT_TYPE_EMS,
									alarmSerialNo,targetName,emsConnectionId,null);
						}
					}
					else if(targetName.contains(NMSDefine.RACK)){
						// 取得网元序列号
						String neSerialNo = nameUtil.getNeSerialNo(nameUtil
								.getMeNameFromRackName(objectName));
						// 取得网元对象
						TNe ne = commonDAOService.getNe(emsConnectionId, neSerialNo);
						if (ne != null) {
							// 取得slot对象
							TRack rack = commonDAOService.getRack(ne.getNeId(),
									targetName);
							if (rack != null) {
								alarm=alarmConstructor(alarm,
										Define.ALARM_OBJECT_TYPE_RACK,
										alarmSerialNo,targetName,rack.getRackId(),ne.getNeId());

							} else if (alarm==null){
								alarm=alarmConstructor(alarm,
										Define.ALARM_OBJECT_TYPE_MANAGED_ELEMENT,
										alarmSerialNo,targetName,ne.getNeId(),ne.getNeId());
							}
						} else if (alarm==null){//未找到关联的网元则将告警关联到网管
							alarm=alarmConstructor(alarm,
									Define.ALARM_OBJECT_TYPE_EMS,
									alarmSerialNo,targetName,emsConnectionId,null);
						}
					}
					else if (alarm==null){
						alarm=alarmConstructor(alarm,
								Define.ALARM_OBJECT_TYPE_EMS,
								alarmSerialNo,targetName,emsConnectionId,null);
					}
				}
				// 板卡告警
				else if (objectType.value() == ObjectType_T._OT_EQUIPMENT) {
					// 取得网元序列号
					String neSerialNo = nameUtil.getNeSerialNo(nameUtil
							.getMeNameFromEquipName(objectName));
					// 取得网元对象
					TNe ne = commonDAOService.getNe(emsConnectionId, neSerialNo);
					if (ne != null) {
						// 取得ptp对象
						TEquip equip = commonDAOService
								.getEquip(ne.getNeId(), targetName);
						if (equip != null) {
							alarm=alarmConstructor(alarm,
									Define.ALARM_OBJECT_TYPE_EQUIPMENT,
									alarmSerialNo,targetName,equip.getEquipId(),ne.getNeId());
						} else if (alarm==null){
							alarm=alarmConstructor(alarm,
									Define.ALARM_OBJECT_TYPE_MANAGED_ELEMENT,
									alarmSerialNo,targetName,ne.getNeId(),ne.getNeId());
						}
					} else if (alarm==null){//未找到关联的网元则将告警关联到网管
						alarm=alarmConstructor(alarm,
								Define.ALARM_OBJECT_TYPE_EMS,
								alarmSerialNo,targetName,emsConnectionId,null);
					}
				}
				// 网管告警
				else// if (objectType.value() == ObjectType_T._OT_EMS||
					//objectType.value() == ObjectType_T._OT_MULTILAYER_SUBNETWORK||
					//objectType.value() == ObjectType_T._OT_TOPOLOGICAL_LINK||
					//objectType.value() == ObjectType_T._OT_SUBNETWORK_CONNECTION||
					//objectType.value() == ObjectType_T._OT_TERMINATION_POINT_POOL||
					//objectType.value() == ObjectType_T._OT_PROTECTION_GROUP||
					//objectType.value() == ObjectType_T._OT_TRAFFIC_DESCRIPTOR||
					//objectType.value() == ObjectType_T._OT_AID) 
					 {
					alarm=alarmConstructor(alarm,
							Define.ALARM_OBJECT_TYPE_EMS,
							alarmSerialNo,targetName,emsConnectionId,null);
				}
			}
	
			// 如果告警不为空，补全相关信息
			if (alarm != null&&(alarm.getCurrentAlarmId()==null||ps.value() == PerceivedSeverity_T._PS_CLEARED)) {
				// 俩时间的格式化
				String emsTime = notification.filterable_data[5].value.extract_string();
				String neTime = notification.filterable_data[6].value.extract_string();
	
				SimpleDateFormat parser = null;
				SimpleDateFormat format = Define.getDateFormatter(Define.COMMON_FORMAT);
				
				//设置采集时间
				String emsRetrievalTime = format.format(new Date());
				String neRetrievalTime = format.format(new Date());
				
				try {
					if (emsTime.contains(".000Z")) {
						parser = Define
								.getDateFormatter(Define.STANDARD_TIME_FORMAT_ZTE);
						emsRetrievalTime = format.format(parser
								.parse(emsTime + "+0000"));
						neRetrievalTime = format.format(parser
								.parse(neTime + "+0000"));
					}else if(emsTime.contains(".0Z")){
						parser = Define.getDateFormatter((Define.STANDARD_TIME_FORMAT_HW));
						emsRetrievalTime = format.format(parser
								.parse(emsTime + "+0000"));
						neRetrievalTime = format.format(parser
								.parse(neTime + "+0000"));
					}else if(emsTime.contains(".0")){
						parser = Define.getDateFormatter((Define.STANDARD_TIME_FORMAT_LUCENT));
						emsRetrievalTime = format.format(parser
								.parse(emsTime+"+0800"));
						neRetrievalTime = format.format(parser
								.parse(neTime+"+0800"));
					}else{
						parser = Define.getDateFormatter((Define.RETRIEVAL_TIME_FORMAT));
						emsRetrievalTime = format.format(parser
								.parse(emsTime));
						neRetrievalTime = format.format(parser
								.parse(neTime));
					}
					
				} catch (ParseException e) {
					e.printStackTrace();
				}
				if (ps.value() != PerceivedSeverity_T._PS_CLEARED) {
					// 告警产生日期时间
					alarm.setRetrievalTime(new Date());
					alarm.setEndTimeFtsp(null);
					alarm.setEndTime(null);
					// 木有被return掉的话接下来要保存了
					// emsConnectionId
					alarm.setEmsConnectionId(emsConnectionId);
					// 告警序列号等
					// for (int j = 0; j < eventType.length; j++) {
					// System.out.println("affectedTPList is:" + eventType[j].name+":"+
					// HWNameAndStringValueUtil.Stringformat(eventType[j].value, ip));
					// }
					if(eventType.length>1){
						alarm.setAlarmReason(nameUtil.Stringformat(
								eventType[1].value));
					}
					// 告警类型
					alarm.setPerceivedSeverity((long) ps.value());
		
					alarm.setNotificationId(notification.filterable_data[0].value
							.extract_string());
					// 转码
					String nativeEMSName = notification.filterable_data[2].value
							.extract_string();
					nativeEMSName = nameUtil.Stringformat(nativeEMSName);

					alarm.setNativeEmsName(nativeEMSName);
					alarm.setNateiveProbableCause(notification.filterable_data[3].value
							.extract_string());
					
					alarm.setEmsTime(emsRetrievalTime);
					alarm.setNeTime(neRetrievalTime);
		
					alarm.setIsClearable(String
							.valueOf(notification.filterable_data[7].value
									.extract_boolean()));
					alarm.setLayerRate(String
							.valueOf(notification.filterable_data[8].value
									.extract_short()));
					alarm.setProbableCause(notification.filterable_data[9].value
							.extract_string());
					alarm
							.setProbableCauseQualifier(probableCauseQualifier);
					serviceAffecting = ServiceAffecting_THelper
							.read(notification.filterable_data[12].value
									.create_input_stream());
					alarm.setServiceAffecting((long) serviceAffecting.value());
					// affectedTPList, only for print
					// for (int j = 0; j < affectedTPList.length; j++) {
					// System.out.println("affectedTPList is:" +
					// affectedTPList[j].value);
					// }
					// additionalInfo
					alarm.setAdditionalInfo(notification.filterable_data[14].value
							.extract_string());
					// objectTypeQualifier
					alarm.setObjectTypeQualifier(notification.filterable_data[16].value
							.extract_string());
				} else {
					// 只收到了告警消失通知
					alarm.setEndTimeFtsp(new Date());
					alarm.setEndTime(format.parse(emsRetrievalTime));
				}
				if(eventType.length>4){
					alarm.setAffirmState(nameUtil.Stringformat(eventType[4].value));
				}
			}
			return alarm;
		}catch(Exception e){
			log.error("构建告警信息出错");
			log.error(Define.getExceptionTrace(e));
			return null;
		}
	}
*/	
	/**
	 * 分拣端口性能数据，返回性能集合
	 * 
	 * @param emsConnectionId
	 *            <String> 网络连接ID
	 * @param List
	 *            <TPtp> ptp集合，非懒加载ptp
	 * @param List
	 *            <PMData_T> 性能集合
	 * @return 返回端口集合数据
	 */
/*	
	private List<SortOfCurrentPmData> getPmDataList(String emsConnectionId,
			List<TPtp> ptpList, List<PMData_T> pmDataList) {
		// 性能集合
		List<SortOfCurrentPmData> pmDataListAll = new ArrayList<SortOfCurrentPmData>();
		// 一个端口pm性能模型
		SortOfCurrentPmData currentPmData = null;
		// 一个端口pm性能集合
		List<PmModel> pmList = null;
		// 性能模型
		PmModel pmModel = null;

		// 遍历ptp列表
		for (TPtp ptp : ptpList) {
			// 如果性能数据为空--返回空性能数据列表
			if (pmDataList == null || pmDataList.size() == 0) {

				currentPmData = new SortOfCurrentPmData();

				currentPmData.setEmsConnectionId(emsConnectionId);
				currentPmData.setNeId(ptp.getTNe().getNeId().toString());
				currentPmData.setPtpId(ptp.getPtpId().toString());
				pmList = new ArrayList<PmModel>();
				currentPmData.setPmList(pmList);
				pmDataListAll.add(currentPmData);
			} else {

				currentPmData = new SortOfCurrentPmData();
				currentPmData.setEmsConnectionId(emsConnectionId);
				currentPmData.setNeId(ptp.getTNe().getNeId().toString());
				currentPmData.setPtpId(ptp.getPtpId().toString());

				pmList = new ArrayList<PmModel>();
				// 遍历性能数据列表
				for (PMData_T pmData : pmDataList) {
					// ptp名字
					NameAndStringValue_T[] ptpName = nameUtil
							.getPtpNameFromTpName(pmData.tpName);
					// 网元名字
					NameAndStringValue_T[] meName = nameUtil
							.getMeNameFromPtpName(ptpName);
					// 取得ptpName
					String ptpNameString = nameUtil.decompositionName(ptpName);
					// 取得网元序列号
					String neSerialNo = nameUtil.getNeSerialNo(meName);
					// 获得匹配的性能
					if (ptp.getPtpName().equals(ptpNameString)
							&& ptp.getTNe().getNeName().equals(neSerialNo)) {
						// 设置层速率及采集时间
						currentPmData.setLayerRate(pmData.layerRate);
						// 俩时间的格式化
						String time = pmData.retrievalTime;

						SimpleDateFormat parser = null;
						SimpleDateFormat format = Define.getDateFormatter(Define.COMMON_FORMAT);
						
						//设置采集时间
						String retrievalTime = format.format(new Date());
						
						try {
							if (time.contains(".000Z")) {
								parser = Define
										.getDateFormatter(Define.STANDARD_TIME_FORMAT_ZTE);
								retrievalTime = format.format(parser
										.parse(time + "+0000"));
							}else if(time.contains(".0Z")){
								parser = Define.getDateFormatter((Define.STANDARD_TIME_FORMAT_HW));
								retrievalTime = format.format(parser
										.parse(time + "+0000"));
							}else if(time.contains(".0")){
								parser = Define.getDateFormatter((Define.STANDARD_TIME_FORMAT_LUCENT));
								retrievalTime = format.format(parser
										.parse(time+"+0800"));
							}else{
								parser = Define.getDateFormatter((Define.RETRIEVAL_TIME_FORMAT));
								retrievalTime = format.format(parser
										.parse(time));
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
						currentPmData.setRetrievalTime(retrievalTime);
						
						// 遍历性能列表
						for (int j = 0; j < pmData.pmMeasurementList.length; j++) {

							pmModel = new PmModel();
							// pm性能代号
							String pmIndex = pmData.pmMeasurementList[j].pmParameterName;

							// 取得pmIndex相关配置信息
							ItemSelectInfo item = CommonService
									.getPmIndexAbout(Define.CONFIG_FILE_LUCENT,
											pmIndex, pmData.layerRate);

							// 没有相关配置信息，跳过
							if (item == null) {
								continue;
							}
							// 设置性能相关参数
							pmModel.setPmIndex(pmIndex);
							pmModel.setPmStandardIndex(item
									.getPmStandardIndex());
							pmModel.setPmDescription(item.getPmDescription());
							// 格式化性能数据
							String pmValue = formatPMValue(item
									.getPmStandardIndex(),
									pmData.pmMeasurementList[j].value);
							pmModel.setPmValue(pmValue);
							// 添加性能参数集合
							pmList.add(pmModel);
						}
					}
				}
				// 添加至性能列表
				currentPmData.setPmList(pmList);
				// 添加至性能集合
				pmDataListAll.add(currentPmData);
			}

		}
		return pmDataListAll;
	}

	// 取得emsSession
	private ILUCENTEMSSession getEmsSession(String emsConnectionId) {
		// 取得连接对象
		TEmsConnection connection = (TEmsConnection) commonDAOService
				.getObject(TEmsConnection.class, "emsConnectionId", Long
						.valueOf(emsConnectionId));
		// 取得emsSession
		ILUCENTEMSSession emsSession;
		// 取得emsSession
		if (NMSDefine.GetLUCENTVEMSStatus()) {
			emsSession = new LUCENTVEMSSession(connection.getUserName(),
					connection.getPassword(), connection.getIp(),
					connection.getPort(), connection.getType(),
					connection.getEmsName(),emsConnectionId);
		} else {
			emsSession = new LUCENTEMSSession(connection.getUserName(),
					connection.getPassword(), connection.getIp(),
					connection.getPort(), connection.getType(),
					connection.getEmsName());
		}
		return emsSession;
	}

	// 保存网元信息
	private ManagedElement_T[] syncNeData(ManagedElement_T[] neInNmsList,
			String emsConnectionId) {
		List<TNe> neList = new ArrayList<TNe>();
		List<ManagedElement_T> neArrayList = new ArrayList<ManagedElement_T>();

		// 取得连接对象
		TEmsConnection connection = (TEmsConnection) commonDAOService
				.getObject(TEmsConnection.class, "emsConnectionId", Long
						.valueOf(emsConnectionId));

		// 取得需要屏蔽的网元类型列表
		ResourceBundle bundle = ResourceBundle.getBundle(Define.SYSTEM_CONFIG_FILE);
		for (ManagedElement_T me : neInNmsList) {
			TNe ne = null;
			// 判断网元是否需要屏蔽
			try {
				if (bundle.getString(me.productName).equals("false")) {
					continue;
				}
			} catch (Exception e) {

			}

			neArrayList.add(me);
			// 取得neName
			String neSerialNo = nameUtil.getNeSerialNo(me.name);

			// ------------------------更新网元表中网元数据 --------------------------
			ne = commonDAOService.getNe(Long.valueOf(emsConnectionId),
					neSerialNo);

			// 如果存在此网元更新信息，不存在新建网元对象
			if (ne != null) {
				ne.setUpdateTime(new Date());
			} else {
				ne = new TNe();
				ne.setCreateTime(new Date());
			}
			ne.setTEmsConnection(connection);
			ne.setNeName(neSerialNo);
			ne.setIsVirtualNe(Define.FLAG_FALSE);
			ne.setIsDel(Define.FLAG_FALSE);
			ne.setIsTelnet(Define.FLAG_FALSE);
			ne.setNeDisplayName(nameUtil.Stringformat(me.nativeEMSName));
			ne.setNeModel(me.productName);
			ne.setLocation(me.location);
			ne.setHardWareVersion(me.version);
			ne.setSoftWareVersion(me.version);
			ne.setFactory(NMSDefine.FACTORY_LUCENT);
			// 取交叉连接数据时需要填入层速率信息
			ne.setSuportRates(decompositionLayRates(me.supportedRates));
			// ne.setNeType(me.);
			neList.add(ne);
		}

		// 保存基础数据
		commonDAOService.storeObjectList(neList);
		int size = neArrayList.size();
		ManagedElement_T[] returnlist = new ManagedElement_T[size];
		return neArrayList.toArray(returnlist);
	}

	// 取得新增的网元
	private List<SyncNeInfoModel> getAddedNe(List<Object> neInDatabaseList,
			ManagedElement_T[] neInNmsList, String emsConnectionId) {
		HashMap<String, TNe> neMap = new HashMap<String, TNe>();
		List<SyncNeInfoModel> data = new ArrayList<SyncNeInfoModel>();
		LUCENTNameAndStringValueUtil nameUtil = new LUCENTNameAndStringValueUtil(
				emsConnectionId);

		// 将数据库中的网元放入map中
		for (Object object : neInDatabaseList) {
			TNe ne = (TNe) object;
			neMap.put(ne.getNeName(), ne);
		}

		// 筛选出新增网元
		for (ManagedElement_T ne : neInNmsList) {
			if (!neMap.containsKey(nameUtil.getNeSerialNo(ne.name))) {
				SyncNeInfoModel newAddedNe = new SyncNeInfoModel();
				newAddedNe.setNeDisplayName(nameUtil
						.Stringformat(ne.nativeEMSName));
				newAddedNe.setNeModel(ne.productName);
				newAddedNe.setNeSerialNo(nameUtil.getNeSerialNo(ne.name));
				newAddedNe.setNeStatusFlag(String
						.valueOf(NMSDefine.NE_ADDED_FLAG));
				newAddedNe.setNeStatusDisplay(NMSDefine.NE_ADDED_FLAG_DISPLAY);
				data.add(newAddedNe);
			}
		}
		return data;
	}

	// 取得被删除的网元
	private List<SyncNeInfoModel> getDeletedNe(List<Object> neInDatabaseList,
			ManagedElement_T[] neInNmsList, String emsConnectionId) {
		HashMap<String, ManagedElement_T> neMap = new HashMap<String, ManagedElement_T>();
		List<SyncNeInfoModel> data = new ArrayList<SyncNeInfoModel>();
		LUCENTNameAndStringValueUtil nameUtil = new LUCENTNameAndStringValueUtil(
				emsConnectionId);

		// 网管上取得网元放入map中
		for (ManagedElement_T ne : neInNmsList) {
			neMap.put(nameUtil.getNeSerialNo(ne.name), ne);
		}

		// 筛选出新增网元
		for (Object object : neInDatabaseList) {
			TNe ne = (TNe) object;
			if (!neMap.containsKey(ne.getNeName())) {
				SyncNeInfoModel deletedNe = new SyncNeInfoModel();
				deletedNe.setNeDisplayName(ne.getNeDisplayName());
				deletedNe.setNeModel(ne.getNeModel());
				deletedNe.setNeSerialNo(ne.getNeName());
				deletedNe.setNeStatusFlag(String
						.valueOf(NMSDefine.NE_DELETED_FLAG));
				deletedNe.setNeStatusDisplay(NMSDefine.NE_DELETED_FLAG_DISPLAY);
				data.add(deletedNe);
			}
		}
		return data;
	}

	// 修改subSlot的删除标识
	private void updateSubSlotDeleteFlag(Long neId, Long emsConnectionId) {
		List<String> properties = new ArrayList<String>();
		List<Object> values = new ArrayList<Object>();

		// 添加查询条件
		properties.add("emsConnectionId");
		properties.add("neId");
		values.add(emsConnectionId);
		values.add(neId);

		// subSlot 列表
		List<Object> subSlotList = commonDAOService.getObject(TSubSlot.class,
				properties, values, 0, 0).getRows();
		for (Object obj : subSlotList) {
			TSubSlot shelf = (TSubSlot) obj;
			shelf.setIsDel(Define.FLAG_TRUE);
		}
		commonDAOService.storeObjectList(subSlotList);
	}

	// 获取ptp层速率字符串
	private String getInterfaceRateSrting(
			LayeredParameters_T[] transmissionParams) {
		String interfaceRate = "";
		StringBuilder tempString = new StringBuilder();
		for (int i = 0; i < transmissionParams.length; i++) {
			tempString.append(String.valueOf(transmissionParams[i].layer));
			if (i != transmissionParams.length - 1) {
				tempString.append(":");
			}
		}
		interfaceRate = tempString.toString();
		return interfaceRate;
	}

	// 获取ptp速率等级
	private String getInterfaceRate(LayeredParameters_T[] transmissionParams) {
		String interfaceRate = "";
		for (int i = 0; i < transmissionParams.length; i++) {
			LayeredParameters_T layerParameter = transmissionParams[i];
			// stm-1
			if (layerParameter.layer == NMSDefine.LAYER_VALUE_25
					|| layerParameter.layer == NMSDefine.LAYER_VALUE_20
					|| layerParameter.layer == NMSDefine.LAYER_VALUE_73) {
				interfaceRate = NMSDefine.INTERFACE_RATE_STM1;
				break;
			}
			// stm-4
			if (layerParameter.layer == NMSDefine.LAYER_VALUE_26
					|| layerParameter.layer == NMSDefine.LAYER_VALUE_21
					|| layerParameter.layer == NMSDefine.LAYER_VALUE_74) {
				interfaceRate = NMSDefine.INTERFACE_RATE_STM4;
				break;
			}
			// stm-16
			if (layerParameter.layer == NMSDefine.LAYER_VALUE_27
					|| layerParameter.layer == NMSDefine.LAYER_VALUE_22
					|| layerParameter.layer == NMSDefine.LAYER_VALUE_76) {
				interfaceRate = NMSDefine.INTERFACE_RATE_STM16;
				break;
			}
			// stm-64
			if (layerParameter.layer == NMSDefine.LAYER_VALUE_28
					|| layerParameter.layer == NMSDefine.LAYER_VALUE_23
					|| layerParameter.layer == NMSDefine.LAYER_VALUE_77) {
				interfaceRate = NMSDefine.INTERFACE_RATE_STM64;
				break;
			}

			// 45M
			if (layerParameter.layer == NMSDefine.LAYER_VALUE_84
					|| layerParameter.layer == NMSDefine.LAYER_VALUE_4) {
				interfaceRate = NMSDefine.INTERFACE_RATE_45M;
				break;
			}

			// 140M
			if (layerParameter.layer == NMSDefine.LAYER_VALUE_85
					|| layerParameter.layer == NMSDefine.LAYER_VALUE_8) {
				interfaceRate = NMSDefine.INTERFACE_RATE_140M;
				break;
			}

			// 34M
			if (layerParameter.layer == NMSDefine.LAYER_VALUE_83
					|| layerParameter.layer == NMSDefine.LAYER_VALUE_7) {
				interfaceRate = NMSDefine.INTERFACE_RATE_34M;
				break;
			}

			// 2M
			if (layerParameter.layer == NMSDefine.LAYER_VALUE_80
					|| layerParameter.layer == NMSDefine.LAYER_VALUE_5) {
				interfaceRate = NMSDefine.INTERFACE_RATE_2M;
				break;
			}

			// GE&&FE
			if (layerParameter.layer == NMSDefine.LAYER_VALUE_96) {
				i++;
				layerParameter = transmissionParams[i];
				if (layerParameter.layer == NMSDefine.LAYER_VALUE_87
						|| layerParameter.layer == NMSDefine.LAYER_VALUE_47) {
					interfaceRate = NMSDefine.INTERFACE_RATE_GE;
					break;
				}
				if (layerParameter.layer == NMSDefine.LAYER_VALUE_97) {
					interfaceRate = NMSDefine.INTERFACE_RATE_FE;
					break;
				}

			}
		}
		return interfaceRate;
	}

	// 获取端口类型
	// private String getPortType1(LayeredParameters_T[] transmissionParams) {
	// String portType1 = null;
	// for (int i = 0; i < transmissionParams.length; i++) {
	// LayeredParameters_T layerParameter = transmissionParams[i];
	// if (layerParameter.layer == NMSDefine.LAYER_VALUE_5
	// || layerParameter.layer == NMSDefine.LAYER_VALUE_15
	// || layerParameter.layer == NMSDefine.LAYER_VALUE_20
	// || layerParameter.layer == NMSDefine.LAYER_VALUE_21
	// || layerParameter.layer == NMSDefine.LAYER_VALUE_22
	// || layerParameter.layer == NMSDefine.LAYER_VALUE_23
	// || layerParameter.layer == NMSDefine.LAYER_VALUE_25
	// || layerParameter.layer == NMSDefine.LAYER_VALUE_26
	// || layerParameter.layer == NMSDefine.LAYER_VALUE_27
	// || layerParameter.layer == NMSDefine.LAYER_VALUE_28
	// || layerParameter.layer == NMSDefine.LAYER_VALUE_73
	// || layerParameter.layer == NMSDefine.LAYER_VALUE_74
	// || layerParameter.layer == NMSDefine.LAYER_VALUE_76
	// || layerParameter.layer == NMSDefine.LAYER_VALUE_77
	// || layerParameter.layer == NMSDefine.LAYER_VALUE_80) {
	// portType1 = NMSDefine.TYPE_SDH;
	// break;
	// }
	//
	// if (layerParameter.layer == NMSDefine.LAYER_VALUE_96) {
	// i++;
	// layerParameter = transmissionParams[i];
	// if (layerParameter.layer == NMSDefine.LAYER_VALUE_87
	// || layerParameter.layer == NMSDefine.LAYER_VALUE_47
	// || layerParameter.layer == NMSDefine.LAYER_VALUE_97
	// || layerParameter.layer == NMSDefine.LAYER_VALUE_46) {
	// portType1 = NMSDefine.TYPE_ETH;
	// break;
	// }
	// if (layerParameter.layer == NMSDefine.LAYER_VALUE_98
	// || layerParameter.layer == NMSDefine.LAYER_VALUE_99) {
	// portType1 = NMSDefine.TYPE_ENCAPSULATION;
	// break;
	// }
	// }
	// }
	// return portType1;
	// }

//	// 取得计数值比较值对象列表
//	private List<TPortConfig> getPortConfigNumberic(TPtp ptpInfo) {
//		Long[] layRateList1 = null;
//		Long[] layRateList2 = null;
//		// pmIndex构建 固定6个性能参数
//		String[] pmIndexList = new String[] { NMSDefine.HW_PMP_BBE,
//				NMSDefine.HW_PMP_CSES, NMSDefine.HW_PMP_ES,
//				NMSDefine.HW_PMP_SES, NMSDefine.HW_PMP_UAS };
//		String[] pmIndexListWithOFS = new String[] { NMSDefine.HW_PMP_BBE,
//				NMSDefine.HW_PMP_CSES, NMSDefine.HW_PMP_ES,
//				NMSDefine.HW_PMP_SES, NMSDefine.HW_PMP_UAS,
//				NMSDefine.HW_PMP_OFS };
//		List<TPortConfig> portConfigList = new ArrayList<TPortConfig>();
//		// 速率
//		String interfaceRate = ptpInfo.getInterfaceRate();
//
//		// STM-1
//		if (interfaceRate.equals(NMSDefine.INTERFACE_RATE_STM1)) {
//
//			layRateList1 = new Long[] { NMSDefine.LAYER_VALUE_25 };
//
//			layRateList2 = new Long[] { NMSDefine.LAYER_VALUE_20 };
//
//			portConfigList.addAll(constructPortConfigNumberic(ptpInfo,
//					layRateList1, pmIndexList));
//
//			portConfigList.addAll(constructPortConfigNumberic(ptpInfo,
//					layRateList2, pmIndexListWithOFS));
//		}
//		
//		// STM-4
//		if (interfaceRate.equals(NMSDefine.INTERFACE_RATE_STM4)) {
//
//			layRateList1 = new Long[] { NMSDefine.LAYER_VALUE_26 };
//
//			layRateList2 = new Long[] { NMSDefine.LAYER_VALUE_21 };
//
//			portConfigList.addAll(constructPortConfigNumberic(ptpInfo,
//					layRateList1, pmIndexList));
//
//			portConfigList.addAll(constructPortConfigNumberic(ptpInfo,
//					layRateList2, pmIndexListWithOFS));
//
//		}
//		// STM-16
//		if (interfaceRate.equals(NMSDefine.INTERFACE_RATE_STM16)) {
//
//			layRateList1 = new Long[] { NMSDefine.LAYER_VALUE_27 };
//
//			layRateList2 = new Long[] { NMSDefine.LAYER_VALUE_22 };
//
//			portConfigList.addAll(constructPortConfigNumberic(ptpInfo,
//					layRateList1, pmIndexList));
//
//			portConfigList.addAll(constructPortConfigNumberic(ptpInfo,
//					layRateList2, pmIndexListWithOFS));
//
//		}
//		// STM-64
//		if (interfaceRate.equals(NMSDefine.INTERFACE_RATE_STM64)) {
//
//			layRateList1 = new Long[] { NMSDefine.LAYER_VALUE_28 };
//
//			layRateList2 = new Long[] { NMSDefine.LAYER_VALUE_23 };
//
//			portConfigList.addAll(constructPortConfigNumberic(ptpInfo,
//					layRateList1, pmIndexList));
//
//			portConfigList.addAll(constructPortConfigNumberic(ptpInfo,
//					layRateList2, pmIndexListWithOFS));
//
//		}
//		// STM-256
//		if (interfaceRate.equals(NMSDefine.INTERFACE_RATE_STM256)) {
//
//			layRateList1 = new Long[] { NMSDefine.LAYER_VALUE_91 };
//
//			layRateList2 = new Long[] { NMSDefine.LAYER_VALUE_90 };
//
//			portConfigList.addAll(constructPortConfigNumberic(ptpInfo,
//					layRateList1, pmIndexList));
//
//			portConfigList.addAll(constructPortConfigNumberic(ptpInfo,
//					layRateList2, pmIndexListWithOFS));
//		}
//
//		return portConfigList;
//	}

//	// 构建端口比较值对象
//	private List<TPortConfig> constructPortConfigNumberic(TPtp ptpInfo,
//			Long[] layRateList, String[] pmIndexList) {
//		List<TPortConfig> portConfigList = new ArrayList<TPortConfig>();
//		TPortConfig portConfig = null;
//
//		List<String> properties = new ArrayList<String>();
//		List<Object> values = new ArrayList<Object>();
//
//		for (Long layRate : layRateList) {
//			for (String pmIndex : pmIndexList) {
//
//				// 添加查询条件
//				properties.clear();
//				values.clear();
//				// emsConnectionId 及neName
//				properties.add("TPtp.ptpId");
//				properties.add("layerRate");
//				properties.add("ochNo");
//				properties.add("granularity");
//				properties.add("pmLocation");
//				properties.add("pmIndex");
//				values.add(ptpInfo.getPtpId());
//				values.add(layRate);
//				values.add("none");
//				values.add(NMSDefine.GRANULARITY_24HOUR);
//				values.add(NMSDefine.LUCENT_PM_LOCATION
//						.get(NMSDefine.LUCENT_PM_LOCATION_1));
//				values.add(pmIndex);
//
//				List<Object> rows = systemDAOService.getObject(
//						TPortConfig.class, properties, values, 0, 0).getRows();
//				// 查询是否已存在此端口比较值，如存在不作修改
//				if (rows.size() == 0) {
//					portConfig = new TPortConfig();
//
//					portConfig.setTEmsConnection(ptpInfo.getTEmsConnection());
//
//					portConfig.setTPtp(ptpInfo);
//
//					// 设置pmIndex
//					portConfig.setPmIndex(pmIndex);
//
//					// 取得pmIndex相关配置信息--使用华为文档
//					ItemSelectInfo item = getPmIndexAbout("PmIndexAboutHW.xml",
//							pmIndex, layRate);
//					// 没有相关配置信息，跳过
//					if (item == null) {
//						continue;
//					}
//					// 设置pmStandardIndex
//					portConfig.setPmStandardIndex(item.getPmStandardIndex());
//
//					// 设置pm描述
//					portConfig.setPmDescription(item.getPmDescription());
//
//					// 设置比较值及越线值
//					portConfig.setOffsetValue1(item.getValue1());
//					portConfig.setOffsetValue2(item.getValue2());
//					portConfig.setOffsetValue3(item.getValue3());
//
//					// 越线值设定
//					portConfig.setAlarmOffsetValue(item.getValue4());
//
//					// 1.计数值
//					portConfig
//							.setPortConfigFlag(NMSDefine.PORT_CONFIG_NUMBERIC);
//
//					// 设置och号
//					portConfig.setOchNo("none");
//					// 设置层速率
//					portConfig.setLayerRate(layRate);
//					// 设置周期
//					portConfig.setGranularity(NMSDefine.GRANULARITY_24HOUR);
//					// 远端 近端
//					portConfig.setPmLocation(NMSDefine.LUCENT_PM_LOCATION
//							.get(NMSDefine.LUCENT_PM_LOCATION_1));
//					// pm值
//					portConfig.setPmValue("0");
//					// // 单位
//					// portConfig.setUnit(pmMeasurement.unit);
//					// 创建时间
//					portConfig.setCreateTime(new Date());
//
//					portConfigList.add(portConfig);
//				}
//			}
//		}
//		return portConfigList;
//	}
	// 添加表的外键关联关系
	private void addSlotTableForignKey(TNe ne) {
		List<String> properties = new ArrayList<String>();
		List<Object> values = new ArrayList<Object>();
		properties.add("TNe.neId");
		values.add(ne.getNeId());
		properties.add("isDel");
		values.add(Define.FLAG_FALSE);
		int startNum = 0;
		int pageSize = LUCENTEMSSession.howMany;
		boolean uend = true;
		do{
			DataModel data = commonDAOService.getObject(TSlot.class,properties,values,startNum,pageSize);
			if(data==null)return;
			List list = data.getRows();
			if(list==null)return;
			addSlotTableForignKey(list,ne);
			startNum+=pageSize;
			if(data==null||data.getTotal()<=startNum)
				uend=false;
		}while(uend);
	}
	// 添加表的外键关联关系
	private void addSubSlotTableForignKey(TNe ne) {
		List<String> properties = new ArrayList<String>();
		List<Object> values = new ArrayList<Object>();
		properties.add("TNe.neId");
		values.add(ne.getNeId());
		properties.add("isDel");
		values.add(Define.FLAG_FALSE);
		int startNum = 0;
		int pageSize = LUCENTEMSSession.howMany;
		boolean uend = true;
		do{
			DataModel data = commonDAOService.getObject(TSubSlot.class,properties,values,startNum,pageSize);
			if(data==null)return;
			List list = data.getRows();
			if(list==null)return;
			addSubSlotTableForignKey(list,ne);
			startNum+=pageSize;
			if(data==null||data.getTotal()<=startNum)
				uend=false;
		}while(uend);
	}
	// 添加表的外键关联关系
	private void addEquipTableForignKey(TNe ne) {
		List<String> properties = new ArrayList<String>();
		List<Object> values = new ArrayList<Object>();
		properties.add("TNe.neId");
		values.add(ne.getNeId());
		properties.add("isDel");
		values.add(Define.FLAG_FALSE);
		int startNum = 0;
		int pageSize = LUCENTEMSSession.howMany;
		boolean uend = true;
		do{
			DataModel data = commonDAOService.getObject(TEquip.class,properties,values,startNum,pageSize);
			if(data==null)return;
			List list = data.getRows();
			if(list==null)return;
			addEquipTableForignKey(list,ne);
			startNum+=pageSize;
			if(data==null||data.getTotal()<=startNum)
				uend=false;
		}while(uend);
	}
	// 添加表的外键关联关系
	private void addPtpTableForignKey(TNe ne) {
		List<String> properties = new ArrayList<String>();
		List<Object> values = new ArrayList<Object>();
		properties.add("TNe.neId");
		values.add(ne.getNeId());
		properties.add("isDel");
		values.add(Define.FLAG_FALSE);
		int startNum = 0;
		int pageSize = LUCENTEMSSession.howMany;
		boolean uend = true;
		do{
			DataModel data = commonDAOService.getObject(TPtp.class,properties,values,startNum,pageSize);
			if(data==null)return;
			List list = data.getRows();
			if(list==null)return;
			addPtpTableForignKey(list,ne);
			startNum+=pageSize;
			if(data==null||data.getTotal()<=startNum)
				uend=false;
		}while(uend);
	}

	// 添加slot表的外键关联关系
	private void addSlotTableForignKey(List<TSlot> slotList, TNe ne) {
		for (TSlot slot : slotList) {
			// 取得shelfName
			String shelfName = nameUtil.decompositionName(nameUtil
					.getShelfNameFromSlotName(nameUtil.constructName(slot
							.getSlotName(), "")));

			// 取得shelf对象
			TShelf shelf = commonDAOService.getShelf(ne.getNeId(), shelfName);

			// 设置shelf外键
			if (shelf != null) {
				slot.setTShelf(shelf);
			}

			// 如果没有找到上层设备信息，设置删除标记为true
			if (shelf == null) {
				slot.setIsDel(Define.FLAG_TRUE);
			}
		}
		commonDAOService.storeObjectList(slotList);
	}

	// 添加子槽道外键关联
	private void addSubSlotTableForignKey(List<TSubSlot> subSlotList, TNe ne) {
		for (TSubSlot subSlot : subSlotList) {
			// 取得shelfName
			String shelfName = nameUtil.decompositionName(nameUtil
					.getShelfNameFromSlotName(nameUtil.constructName(subSlot
							.getSubSlotName(), "")));
			// 取得shelf对象
			TShelf shelf = commonDAOService.getShelf(ne.getNeId(), shelfName);
			// 设置shelf外键
			if (shelf != null) {
				subSlot.setShelfId(shelf.getShelfId());
			}

			// 取得slotName
			String slotName = nameUtil.decompositionName(nameUtil
					.getSlotNameFromSubSlotName(nameUtil.constructName(subSlot
							.getSubSlotName(), "")));
			// 取得slot对象
			TSlot slot = commonDAOService.getSlot(ne.getNeId(), slotName);
			// 设置slot外键
			if (slot != null) {
				subSlot.setSlotId(slot.getSlotId());
			}
		}
		commonDAOService.storeObjectList(subSlotList);
	}

	// 添加Equip表的外键关联关系
	private void addEquipTableForignKey(List<TEquip> equipList, TNe ne) {
		for (TEquip equip : equipList) {
			// 取得shelfName
			String shelfName = nameUtil.decompositionName(nameUtil
					.getShelfNameFromEquipName(nameUtil.constructName(equip
							.getEquipName(), "")));
			// 取得shelf对象
			TShelf shelf = commonDAOService.getShelf(ne.getNeId(), shelfName);
			// 设置shelf外键
			if (shelf != null) {
				equip.setTShelf(shelf);
			}
			// 取得slotName
			String slotName = nameUtil.decompositionName(nameUtil
					.getSlotNameFromEquipName(nameUtil.constructName(equip
							.getEquipName(), "")));
			// 取得slot对象
			TSlot slot = commonDAOService.getSlot(ne.getNeId(), slotName);
			// 设置slot外键
			if (slot != null) {
				equip.setTSlot(slot);
			}

			// 如果没有找到上层设备信息，设置删除标记为true
			if (shelf == null || slot == null) {
				equip.setIsDel(Define.FLAG_TRUE);
			}
		}
		commonDAOService.storeObjectList(equipList);
	}

	// 添加ptp表的外键关联关系
	private void addPtpTableForignKey(List<TPtp> ptpList, TNe ne) {
		// 区分网元类型
		if (!ne.getNeModel().equals(NMSDefine.LUCENT_LUNITE)) {
			// ftp无法一一对应
			for (TPtp ptp : ptpList) {
				String equipDisplayName = ptp.getSlotNo();
				// 取得TEquip对象
				TEquip equip = null;
				if (equipDisplayName.contains(",")) {
					equip = lucentDAOService.getLucentEquip(ne.getNeId(),
							equipDisplayName.split(",")[0]);
					if (equip == null) {
						equip = lucentDAOService.getLucentEquip(ne.getNeId(),
								equipDisplayName.split(",")[0]
										.replace("P", "S"));
					}
				}
				if (equip == null) {
					String tempName = equipDisplayName.replace(".", "-").split(
							"-")[0];
					tempName = tempName.replace("P", "S");
					equip = lucentDAOService.getLucentEquip(ne.getNeId(),
							tempName);
				}
				// 从subSlot中查找
				if (equip == null) {
					equip = lucentDAOService.getLucentEquipFromSubSlot(ne
							.getNeId(), equipDisplayName);
				}
				if (equip != null) {
					ptp.setTEquip(equip);
					if (equip.getTSlot() != null) {
						ptp.setTSlot(equip.getTSlot());
					}
					if (equip.getTShelf() != null) {
						ptp.setTShelf(equip.getTShelf());
					}
				}

				// 如果没有找到上层设备信息，设置删除标记为true
				if (equip == null) {
					ptp.setIsDel(Define.FLAG_TRUE);
				}
			}
		} else {
			for (TPtp ptp : ptpList) {
				String slotNo = ptp.getSlotNo();
				TEquip equip = null;
				equip = lucentDAOService.getLucentEquipLunite(ne.getNeId(),
						slotNo);
				if (equip != null) {
					ptp.setTEquip(equip);
					if (equip.getTSlot() != null) {
						ptp.setTSlot(equip.getTSlot());
					}
					if (equip.getTShelf() != null) {
						ptp.setTShelf(equip.getTShelf());
					}
				}
				// 如果没有找到上层设备信息，设置删除标记为true
				if (equip == null) {
					ptp.setIsDel(Define.FLAG_TRUE);
				}
			}
		}
		commonDAOService.storeObjectList(ptpList);
	}

	// 取得TPmOriginalData对象
	private List<TPmOriginalData> getOriginalData(PMData_T data,
			WaveConfig waveConfig, TEmsConnection connection, long neId,
			String layRateString, Date belongToDate) {

		List<TPmOriginalData> originalDataList = new ArrayList<TPmOriginalData>();

		// List<String> keyList = null;

		// 取得ptpName
		String ptpName = nameUtil.decompositionName(nameUtil
				.getPtpNameFromTpName(data.tpName));

		// 取得TPtp对象
		TPtp ptp = commonDAOService.getPtp(neId, ptpName);

		// 初始化och号
		String ochNo = "none";

		Object[] subAry = splitAry(data.pmMeasurementList, waveConfig
				.getDistance());

		if (ptp != null) {
			for (Object obj : subAry) {
				PMMeasurement_T[] aryItem = (PMMeasurement_T[]) obj;

				// keyList = new ArrayList<String>();

				// get 端口设置信息
				for (PMMeasurement_T pmMeasurement : aryItem) {
					// for test:
					// String pmStandardIndex = pmMeasurement.pmParameterName;
					
					
					//取得pmIndex相关配置信息
					ItemSelectInfo item = CommonService.getPmIndexAbout(Define.CONFIG_FILE_LUCENT,pmMeasurement.pmParameterName,Long
							.valueOf(data.layerRate));
					
					//没有相关配置信息，跳过
					if(item == null){
						continue;
					}
					// 设置pmStandardIndex
					String pmStandardIndex = item.getPmStandardIndex();
					
					 //设置pm描述
					String pmDescription = item.getPmDescription();

					//如果没有取得性能标准描述 则认为是不需要的数据 过滤掉
					//如果没有速率也认为是不需要的数据，过滤掉
					if(pmStandardIndex == null||pmStandardIndex.isEmpty()||
							ptp.getInterfaceRate() == null ||
							ptp.getInterfaceRate().isEmpty()){
						System.out.println(pmMeasurement.pmParameterName+"_"+data.layerRate);
						continue;
					}

					//layRateString 中包含端口的速率等级 并且包含性能参数则继续处理
//					if(layRateString.contains(ptp.getInterfaceRate())&& 
//							layRateString.contains(pmStandardIndex)){
					//layRateString 中包含性能参数则继续处理
					if(layRateString.contains(pmStandardIndex)){

						// 取得原始数据
						TPmOriginalData originalData = constructOriginalData(
								connection, ptp, ochNo, Long
										.valueOf(data.layerRate),
								pmMeasurement, pmStandardIndex,pmDescription, belongToDate);

						originalDataList.add(originalData);
					}
				}
			}
		} else {
			log.error("LUCENT getOriginalData error ptp not found ptpName is:"
					+ ptpName);
		}
		return originalDataList;
	}

	// 分割性能数据数组 按 每波占用数据长度
	private Object[] splitAry(PMMeasurement_T[] ary, int subSize) {
		int count = ary.length % subSize == 0 ? ary.length / subSize
				: ary.length / subSize + 1;
		List<List<PMMeasurement_T>> subAryList = new ArrayList<List<PMMeasurement_T>>();
		for (int i = 0; i < count; i++) {
			int index = i * subSize;
			List<PMMeasurement_T> list = new ArrayList<PMMeasurement_T>();
			int j = 0;
			while (j < subSize && index < ary.length) {
				list.add(ary[index++]);
				j++;
			}
			subAryList.add(list);
		}
		Object[] subAry = new Object[subAryList.size()];

		for (int i = 0; i < subAryList.size(); i++) {
			List<PMMeasurement_T> subList = subAryList.get(i);

			PMMeasurement_T[] subAryItem = new PMMeasurement_T[subList.size()];
			for (int j = 0; j < subList.size(); j++) {
				subAryItem[j] = subList.get(j);
			}
			subAry[i] = subAryItem;
		}
		return subAry;
	}

	// 组装原始数据
	private TPmOriginalData constructOriginalData(TEmsConnection connection,
			TPtp ptp, String ochNo, long layerRate,
			PMMeasurement_T pmMeasurement, String pmStandardIndex,
			String pmDescription ,Date belongToDate) {
		TPmOriginalData originalData = new TPmOriginalData();

		originalData.setTEmsConnection(connection);
		originalData.setTPtp(ptp);

		// 设置pmIndex
		originalData.setPmIndex(pmMeasurement.pmParameterName);

		// 设置pmStandardIndex
		originalData.setPmStandardIndex(pmStandardIndex);

		// 设置och号
		originalData.setOchNo(ochNo);
		// 设置层速率
		originalData.setLayerRate(layerRate);
		// 设置周期
		originalData.setGranularity(NMSDefine.GRANULARITY_24HOUR);
		// 设置pm描述
		originalData.setPmDescription(pmDescription);
		// 远端 近端
		originalData.setPmLocation(NMSDefine.LUCENT_PM_LOCATION
				.get(pmMeasurement.pmLocation));

		// pm值
		originalData.setPmValue(formatPMValue(pmStandardIndex,
				pmMeasurement.value));
		// 单位
		originalData.setUnit(pmMeasurement.unit);
		// 创建时间
		originalData.setRetrievalTime(new Date());
		// 更新时间
		originalData.setBelongToDate(belongToDate);

		return originalData;
	}

	// 筛选异常数据
	private List<TPmExceptionData> filterExceptionData(
			List<TPmOriginalData> pmOriginalDataList, Date belongToDate) {

		List<TPmExceptionData> exceptionDataList = new ArrayList<TPmExceptionData>();
		TPmExceptionData exceptionData = null;
		TPortConfig portConfig = null;

		String pmIndex;
		Double pmValue;
		Double pmStandardValue;
		Double maxValue_Level1 = 0.0;
		Double maxValue_Level2 = 0.0;
		Double maxValue_Level3 = 0.0;
		Double minValue_Level1 = 0.0;
		Double minValue_Level2 = 0.0;
		Double minValue_Level3 = 0.0;
		Double offsetValue_Level1 = 0.0;
		Double offsetValue_Level2 = 0.0;
		Double offsetValue_Level3 = 0.0;

		Double alarmOffsetValue = 999999999.0;

		long alarmLevel = NMSDefine.ALARM_LEVEL_1;

		List<String> properties = new ArrayList<String>();
		List<Object> values = new ArrayList<Object>();

		for (TPmOriginalData originalData : pmOriginalDataList) {
			// 重置告警限制值
			alarmOffsetValue = 999999999.0;
			
			// 添加查询条件
			properties.clear();
			values.clear();
			// emsConnectionId 及neName
			properties.add("TPtp.ptpId");
			properties.add("layerRate");
			properties.add("ochNo");
			properties.add("granularity");
			properties.add("pmLocation");
			properties.add("pmIndex");
			values.add(originalData.getTPtp().getPtpId());
			values.add(originalData.getLayerRate());
			values.add(originalData.getOchNo());
			values.add(originalData.getGranularity());
			values.add(originalData.getPmLocation());
			values.add(originalData.getPmIndex());

			// 性能值
			pmValue = Double.valueOf(originalData.getPmValue());
			// 性能描述符
			pmIndex = originalData.getPmIndex();

			// 取得比较值对象
			List<Object> rows = systemDAOService.getObject(TPortConfig.class,
					properties, values, 0, 0).getRows();

			if (rows.size() > 0) {
				portConfig = (TPortConfig) rows.get(0);
				// when pmStandardValue is null skip it
				if (portConfig.getPmValue() == null
						|| !(portConfig.getPmValue().length() > 0)) {
					continue;
				}
				//如果端口比较值为-60，则重新修正比较值为当前原始数据的值，并且不把此原始数据作为异常数据处理
				else if(portConfig.getPmValue().equals(NMSDefine.PM_VALUE_NOT_USE_60)){
					portConfig.setPmValue(pmValue.toString());
					systemDAOService.storeObject(portConfig);
					continue;
				}
				else {
					pmStandardValue = Double.valueOf(portConfig.getPmValue());
				}

				if (portConfig.getOffsetValue1() != null) {
					offsetValue_Level1 = Double.valueOf(portConfig
							.getOffsetValue1());
				}
				if (portConfig.getOffsetValue2() != null) {
					offsetValue_Level2 = Double.valueOf(portConfig
							.getOffsetValue2());
				}
				if (portConfig.getOffsetValue3() != null) {
					offsetValue_Level3 = Double.valueOf(portConfig
							.getOffsetValue3());
				}

				maxValue_Level1 = pmStandardValue + offsetValue_Level1;
				minValue_Level1 = pmStandardValue - offsetValue_Level1;
				maxValue_Level2 = pmStandardValue + offsetValue_Level2;
				minValue_Level2 = pmStandardValue - offsetValue_Level2;
				maxValue_Level3 = pmStandardValue + offsetValue_Level3;
				minValue_Level3 = pmStandardValue - offsetValue_Level3;
				if (portConfig.getAlarmOffsetValue() != null) {
					alarmOffsetValue = Double.valueOf(portConfig
							.getAlarmOffsetValue());
				}
			} else {
				// 如果没有取到相应的比较值，则查看配置文件中是否应该有此比较值，如果有则新建
				// 取得pmIndex相关配置信息
				ItemSelectInfo item = CommonService.getPmIndexAbout(
						Define.CONFIG_FILE_HW, originalData.getPmIndex(),
						originalData.getLayerRate());
				if (item != null) {

					portConfig = new TPortConfig();

					portConfig.setTEmsConnection(originalData
							.getTEmsConnection());

					portConfig.setTPtp(originalData.getTPtp());

					// 设置pmIndex
					portConfig.setPmIndex(item.getPmIndex());

					// 设置pmStandardIndex
					portConfig.setPmStandardIndex(item.getPmStandardIndex());

					// 设置pm描述
					portConfig.setPmDescription(item.getPmDescription());

					// 设置比较值及越线值
					portConfig.setOffsetValue1(item.getValue1());
					portConfig.setOffsetValue2(item.getValue2());
					portConfig.setOffsetValue3(item.getValue3());

					// 越线值设定
					portConfig.setAlarmOffsetValue(item.getValue4());

					// 1.计数值 2.物理量
					portConfig.setPortConfigFlag(Long.valueOf(item
							.getPortConfigFlag()));
					// 设置och号
					portConfig.setOchNo(originalData.getOchNo());
					// 设置层速率
					portConfig.setLayerRate(originalData.getLayerRate());
					// 设置周期
					portConfig.setGranularity(NMSDefine.GRANULARITY_24HOUR);

					// 远端 近端
					portConfig.setPmLocation(originalData.getPmLocation());
					// pm值
					portConfig.setPmValue(originalData.getPmValue());
					// // 单位
					// portConfig.setUnit(pmMeasurement.unit);
					// 创建时间
					portConfig.setCreateTime(new Date());

					systemDAOService.storeObject(portConfig);
				}
				continue;
			}

			// 剔除性能值越线的异常数据
			if (pmValue > alarmOffsetValue) {
				continue;
			}

			// 当前值不再区间之内为异常数据
			if (pmIndex.equals(NMSDefine.HW_PMP_RPL)
					|| pmIndex.equals(NMSDefine.HW_PMP_TPL)
					|| pmIndex.equals(NMSDefine.HW_PMP_PCLSOP)
					|| pmIndex.equals(NMSDefine.HW_PMP_PCLSOP_CUR)) {
				if (pmValue > maxValue_Level1 || pmValue < minValue_Level1) {

					alarmLevel = NMSDefine.ALARM_LEVEL_1;

					if (pmValue > maxValue_Level2 || pmValue < minValue_Level2) {
						alarmLevel = NMSDefine.ALARM_LEVEL_2;
					}
					if (pmValue > maxValue_Level3 || pmValue < minValue_Level3) {
						alarmLevel = NMSDefine.ALARM_LEVEL_3;
					}

					exceptionData = consturctPmExceptionData(originalData,
							portConfig.getPmValue(), portConfig
									.getOffsetValue1(), portConfig
									.getOffsetValue2(), portConfig
									.getOffsetValue3(), alarmLevel,
							belongToDate);

				}
			}
			// 最大值大于比较值和偏差值和为异常数据
			// 信噪比最大值无需判断PMP_SNR_MAX
			else if (pmIndex.equals(NMSDefine.HW_PMP_RPL_MAX)
					|| pmIndex.equals(NMSDefine.HW_PMP_TPL_MAX)
					|| pmIndex.equals(NMSDefine.HW_PMP_CLSOPMAX)
					|| pmIndex.equals(NMSDefine.HW_PMP_PCLSOP_MAX)) {
				if (pmValue > maxValue_Level1) {

					alarmLevel = NMSDefine.ALARM_LEVEL_1;

					if (pmValue > maxValue_Level2) {
						alarmLevel = NMSDefine.ALARM_LEVEL_2;
					}
					if (pmValue > maxValue_Level3) {
						alarmLevel = NMSDefine.ALARM_LEVEL_3;
					}

					exceptionData = consturctPmExceptionData(originalData,
							portConfig.getPmValue(), portConfig
									.getOffsetValue1(), portConfig
									.getOffsetValue2(), portConfig
									.getOffsetValue3(), alarmLevel,
							belongToDate);

				}

			}
			// 最小值小于比较值和偏差数据之差为异常数据
			else if (pmIndex.equals(NMSDefine.HW_PMP_RPL_MIN)
					|| pmIndex.equals(NMSDefine.HW_PMP_TPL_MIN)
					|| pmIndex.equals(NMSDefine.HW_PMP_CLSOPMIN)
					|| pmIndex.equals(NMSDefine.HW_PMP_PCLSOP_MIN)
					|| pmIndex.equals(NMSDefine.HW_PMP_SNR_MIN)
					|| pmIndex.equals(NMSDefine.HW_PMP_SNR)
					|| pmIndex.equals(NMSDefine.FEC_BEF_COR_ER)
					|| pmIndex.equals(NMSDefine.FEC_AFT_COR_ER)) {
				if (pmValue < minValue_Level1) {

					alarmLevel = NMSDefine.ALARM_LEVEL_1;

					if (pmValue < minValue_Level2) {
						alarmLevel = NMSDefine.ALARM_LEVEL_2;
					}
					if (pmValue < minValue_Level3) {
						alarmLevel = NMSDefine.ALARM_LEVEL_3;
					}
					exceptionData = consturctPmExceptionData(originalData,
							portConfig.getPmValue(), portConfig
									.getOffsetValue1(), portConfig
									.getOffsetValue2(), portConfig
									.getOffsetValue3(), alarmLevel,
							belongToDate);
				}

			}

			// 最大值大于比较值和偏差值和为异常数据 (计数值)
			else if (pmIndex.equals(NMSDefine.HW_PMP_BBE)
					|| pmIndex.equals(NMSDefine.HW_PMP_ES)
					|| pmIndex.equals(NMSDefine.HW_PMP_ESR)
					|| pmIndex.equals(NMSDefine.HW_PMP_UAS)
					|| pmIndex.equals(NMSDefine.HW_PMP_SES)
					|| pmIndex.equals(NMSDefine.HW_PMP_OFS)
					|| pmIndex.equals(NMSDefine.HW_PMP_CSES)) {
				if (pmValue > maxValue_Level1) {

					alarmLevel = NMSDefine.ALARM_LEVEL_1;

					if (pmValue > maxValue_Level2) {
						alarmLevel = NMSDefine.ALARM_LEVEL_2;
					}
					if (pmValue > maxValue_Level3) {
						alarmLevel = NMSDefine.ALARM_LEVEL_3;
					}

					exceptionData = consturctPmExceptionData(originalData,
							portConfig.getPmValue(), portConfig
									.getOffsetValue1(), portConfig
									.getOffsetValue2(), portConfig
									.getOffsetValue3(), alarmLevel,
							belongToDate);
				}
			}

			// 如果不為空 添加异常数据
			if (exceptionData != null) {
				exceptionDataList.add(exceptionData);
			}
		}
		return exceptionDataList;
	}

	// 保存原始数据
	private boolean saveOriginalData(List<PMData_T> pmDataList,
			TEmsConnection connection, long neId, String layRateString,
			Date belongToDate) {
		try {
			List<TPmOriginalData> pmOriginalDataList = new ArrayList<TPmOriginalData>();

			List<TPmExceptionData> exceptionDataList = new ArrayList<TPmExceptionData>();

			WaveConfig waveConfig = new WaveConfig(NMSDefine.WAVE_NONE, 9999,
					false);
			for (int i = 0; i < pmDataList.size(); i++) {
				pmOriginalDataList
						.addAll(getOriginalData(pmDataList.get(i), waveConfig,
								connection, neId, layRateString, belongToDate));
			}
			// for test,PortConfig is empty now
			// 筛选异常数据
			exceptionDataList = filterExceptionData(pmOriginalDataList,
					belongToDate);
			// 保存原始数据及异常数据
			commonDAOService.storeObjectList(pmOriginalDataList);
			commonDAOService.storeObjectList(exceptionDataList);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(Define.getExceptionTrace(e));
			return false;
		}
		return true;
	}
	
	
	
	private void printAlarm(String head, StructuredEvent notification) {
		if ("NT_ALARM".equals(head)) {
			// varible
			NameAndStringValue_T[] objectName;
			ObjectType_T objectType;
			PerceivedSeverity_T perceivedSeverity;
			ServiceAffecting_T serviceAffecting;
			NameAndStringValue_T[] affectedTPList;
			NameAndStringValue_T[] EventType;

			System.out
					.println("*******************************  NT_ALARM  ******************************");
			// notificationId
			System.out.println("notificationId is:"
					+ notification.filterable_data[0].value.extract_string());
			// name
			objectName = NamingAttributes_THelper
					.read(notification.filterable_data[1].value
							.create_input_stream());
			for (int j = 0; j < objectName.length; j++) {
				System.out.println("objectName.name is:" + objectName[j].name);
				System.out
						.println("objectName.value is:" + objectName[j].value);
			}
			// nativeEMSName
			System.out.println("nativeEMSName is:"
					+ notification.filterable_data[2].value.extract_string());
			// nativeProbableCause
			System.out.println("nativeProbableCause is:"
					+ notification.filterable_data[3].value.extract_string());
			// objectType
			objectType = ObjectType_THelper
					.read(notification.filterable_data[4].value
							.create_input_stream());
			System.out.println("objectType is:" + objectType.value());
			// emsTime
			System.out.println("emsTime is:"
					+ notification.filterable_data[5].value.extract_string());
			// neTime
			System.out.println("neTime is:"
					+ notification.filterable_data[6].value.extract_string());
			// isClearable
			System.out.println("isClearable is:"
					+ notification.filterable_data[7].value.extract_boolean());
			// layerRate
			System.out.println("layerRate is:"
					+ notification.filterable_data[8].value.extract_short());
			// probableCause
			System.out.println("probableCause is:"
					+ notification.filterable_data[9].value.extract_string());
			// probableCauseQualifier
			System.out.println("probableCauseQualifier is:"
					+ notification.filterable_data[10].value.extract_string());
			// perceivedSeverity
			perceivedSeverity = PerceivedSeverity_THelper
					.read(notification.filterable_data[11].value
							.create_input_stream());
			System.out.println("perceivedSeverity is:"
					+ perceivedSeverity.value());
			// serviceAffecting
			System.out.println();
			serviceAffecting = ServiceAffecting_THelper
					.read(notification.filterable_data[12].value
							.create_input_stream());
			System.out.println("serviceAffecting is:"
					+ serviceAffecting.value());
			// affectedTPList
			System.out.println();
//			affectedTPList = NamingAttributes_THelper
//					.read(notification.filterable_data[13].value
//							.create_input_stream());
//			for (int j = 0; j < affectedTPList.length; j++) {
//				System.out.println("affectedTPList is:"
//						+ affectedTPList[j].value);
//			}
			// additionalInfo
			System.out.println("additionalInfo is:"
					+ notification.filterable_data[14].value.extract_string());
			// EventType
			EventType = NamingAttributes_THelper
					.read(notification.filterable_data[15].value
							.create_input_stream());
			for (int j = 0; j < EventType.length; j++) {
				System.out.println("EventType is:" + EventType[j].value);
			}
			// objectTypeQualifier
			System.out.println("objectTypeQualifier is:"
					+ notification.filterable_data[16].value.extract_string());
		}

		if ("NT_TCA".equals(head)) {
			// varible
			NameAndStringValue_T[] objectName;
			ObjectType_T objectType;
			PerceivedSeverity_T perceivedSeverity;
			PMThresholdType_T thresholdType;

			System.out
					.println("*******************************  NT_TCA  ******************************");
			// notificationId
			System.out.println();
			System.out.println("notificationId is:"
					+ notification.filterable_data[0].value.extract_string());
			// name
			objectName = NamingAttributes_THelper
					.read(notification.filterable_data[1].value
							.create_input_stream());
			for (int j = 0; j < objectName.length; j++) {
				System.out.println("name.name is:" + objectName[j].name);
				System.out.println("name.value is:" + objectName[j].value);
			}
			// nativeEMSName
			System.out.println("nativeEMSName is:"
					+ notification.filterable_data[2].value.extract_string());
			// objectType
			objectType = ObjectType_THelper
					.read(notification.filterable_data[3].value
							.create_input_stream());
			System.out.println("objectType is:" + (objectType.value()));
			// emsTime
			System.out.println("emsTime is:"
					+ notification.filterable_data[4].value.extract_string());
			// neTime
			System.out.println("neTime is:"
					+ notification.filterable_data[5].value.extract_string());
			// isClearable
			System.out.println("isClearable is:"
					+ notification.filterable_data[6].value.extract_boolean());
			// perceivedSeverity
			perceivedSeverity = PerceivedSeverity_THelper
					.read(notification.filterable_data[7].value
							.create_input_stream());
			System.out.println("perceivedSeverity is:"
					+ perceivedSeverity.value());
			// layerRate
			System.out.println("layerRate is:"
					+ notification.filterable_data[8].value.extract_short());
			// granularity
			System.out.println("granularity is:"
					+ notification.filterable_data[9].value.extract_string());
			// pmParameterName
			System.out.println("pmParameterName is:"
					+ notification.filterable_data[10].value.extract_string());
			// pmLocation
			System.out.println("pmLocation is:"
					+ notification.filterable_data[11].value.extract_string());
			// thresholdType
			thresholdType = PMThresholdType_THelper
					.read(notification.filterable_data[12].value
							.create_input_stream());
			System.out.println("thresholdType is:" + thresholdType.value());
			// value
			System.out.println("value is:"
					+ notification.filterable_data[13].value.extract_float());
			// unit
			System.out.println("unit is:"
					+ notification.filterable_data[14].value.extract_string());
			// additionalInfo
			objectName = NamingAttributes_THelper
					.read(notification.filterable_data[15].value
							.create_input_stream());
			for (int j = 0; j < objectName.length; j++) {
				System.out.println("additionalInfo is:" + objectName[j].value);
			}
		}
	}
*/
	/*
	@IMethodLog(desc = "DataCollectService：HW获取网元间link信息")
	public List<TopologicalLinkModel> getAllTopologicalLinks()
			throws CommonException {
		
		List<TopologicalLink_T> linkListData = new ArrayList<TopologicalLink_T>();
		// 取得子网信息
		MultiLayerSubnetwork_T[] subnetWorks = emsSession
				.getAllTopLevelSubnetworks();
		
		for (MultiLayerSubnetwork_T subnet : subnetWorks) {
			// 取得网络link信息
			TopologicalLink_T[] linkList = emsSession
					.getAllTopologicalLinks(subnet.name);

			for (TopologicalLink_T link : linkList) {
				linkListData.add(link);
			}
		}
		
		List<TopologicalLinkModel> data = new ArrayList<TopologicalLinkModel>();

		for (TopologicalLink_T link : linkListData) {
			TopologicalLinkModel model = lucentDataToModel
					.TopologicalLinkDataToModel(link);
			data.add(model);
		}
		return data;
	}
	*/
	@IMethodLog(desc = "DataCollectService：LUCENT获取网元内部link信息")
	public List<TopologicalLinkModel> getAllInternalTopologicalLinks(
			String neName) throws CommonException {
		List<TopologicalLinkModel> data = new ArrayList<TopologicalLinkModel>();
		//添加外部link中的内部link
		data.addAll(getLinkFromTopologicalLinks(2,neName));
		
		return data;
	}
	
	@IMethodLog(desc = "DataCollectService：LUCENT获取虚拟网桥信息")
	public List<VirtualBridgeModel> getAllVBs(String neName)
			throws CommonException {
		List<VirtualBridgeModel> data = new ArrayList<VirtualBridgeModel>();
		/*
		 * LUCENT无此功能，不用获取
		 */
		System.out.println("LUCENT不支持同步AllVBs");
		return data;
	}
	
	@IMethodLog(desc = "DataCollectService：LUCENT获取网元所有mstp信息")
	public List<TerminationPointModel> getAllMstpEndPoints(String neName)
			throws CommonException {
		List<TerminationPointModel> data = new ArrayList<TerminationPointModel>();
		/*
		 * LUCENT无此功能，不用获取
		 */
		System.out.println("LUCENT不支持同步MstpEndPoints");
		return data;
	}
	
	public List<MSTPBindingPathModel> getBindingPath(
			NameAndStringValue_T[] endPointName) throws CommonException {
		List<MSTPBindingPathModel> datas = new ArrayList<MSTPBindingPathModel>();
		/*
		 * LUCENT无此方法
		 */
		System.out.println("LUCENT不支持同步BindingPath");
		return datas;
	}
	
	@IMethodLog(desc = "DataCollectService：LUCENT获取网元以太网业务")
	public List<EthServiceModel> getAllEthService(String neName)
			throws CommonException {			
		List<EthServiceModel> data = new ArrayList<EthServiceModel>();
		/*
		 * LUCENT无T_ETHSERVICE
		 */
		System.out.println("LUCENT不支持同步T_ETHSERVICE");
		return data;
	}
	
	@IMethodLog(desc = "DataCollectService：LUCENT获取网元WDM保护组信息")
	public List<WDMProtectionGroupModel> getAllWDMProtectionGroups(String neName)
			throws CommonException {
		List<WDMProtectionGroupModel> data = new ArrayList<WDMProtectionGroupModel>();
		/*
		 * LUCENT不支持OTN/WDM保护
		 */
		System.out.println("LUCENT不支持OTN/WDM保护信息");
		return data;
	}
	
	@IMethodLog(desc = "DataCollectService：LUCENT获取网元电保护组信息")
	public List<EProtectionGroupModel> getAllEProtectionGroups(String neName)
			throws CommonException {
		List<EProtectionGroupModel> data = new ArrayList<EProtectionGroupModel>();
		/*
		 * LUCENT不支持同步设备保护组信息
		 */
		System.out.println("LUCENT不支持同步设备保护组信息");
		return data;
	}
}
