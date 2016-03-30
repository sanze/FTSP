package com.fujitsu.manager.dataCollectManager.serviceImpl.ZTEU31Corba;

import globaldefs.NameAndStringValue_T;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.omg.CORBA.TypeCodePackage.BadKind;

import ZTE_U31.CosNotification.StructuredEvent;
import ZTE_U31.clocksource.ClockSource_T;
import ZTE_U31.emsMgr.EMS_T;
import ZTE_U31.equipment.EquipmentOrHolder_T;
import ZTE_U31.ethernet.VB_T;
import ZTE_U31.managedElement.ManagedElement_T;
import ZTE_U31.managedElementManager.MEConfigData_T;
import ZTE_U31.mstpcommon.EthernetService_T;
import ZTE_U31.mstpcommon.VCGBinding_T;
import ZTE_U31.multiLayerSubnetwork.MultiLayerSubnetwork_T;
import ZTE_U31.performance.PMData_T;
import ZTE_U31.performance.PMTPSelect_T;
import ZTE_U31.protection.EProtectionGroup_T;
import ZTE_U31.protection.ProtectionGroupList_THelper;
import ZTE_U31.protection.ProtectionGroup_T;
import ZTE_U31.subnetworkConnection.CrossConnect_T;
import ZTE_U31.subnetworkConnection.SubnetworkConnection_T;
import ZTE_U31.terminationPoint.TerminationPoint_T;
import ZTE_U31.topologicalLink.TopologicalLink_T;

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
import com.fujitsu.manager.dataCollectManager.corbaDataModel.SubnetworkConnectionModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TerminationPointModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TopologicalLinkModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.VirtualBridgeModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.WDMProtectionGroupModel;
import com.fujitsu.manager.dataCollectManager.service.EMSCollectService;
import com.fujitsu.manager.dataCollectManager.serviceImpl.VEMS.IZTEEMSSession;
import com.fujitsu.manager.dataCollectManager.serviceImpl.VEMS.ZTEVEMSSession;
import com.fujitsu.model.HistoryPmFileGetResult;
import com.fujitsu.util.CommonUtil;
import com.fujitsu.util.FtpUtils;

/**
 * @author xuxiaojun
 *  
 */
public class ZTEU31ServiceImpl extends EMSCollectService {
	boolean isVirtualEms = true;
	private IZTEEMSSession emsSession;

	@IMethodLog(desc = "DataCollectService：ZTE初始化连接参数")
	public void initParameter(String corbaName, String corbaPassword,
			String corbaIp, String corbaPort, String emsName,
			String internalEmsName, String encode,int iteratorNum) throws CommonException {
		
		super.initParameter(corbaName, corbaPassword,
				corbaIp, corbaPort, emsName,
				internalEmsName);
		
		zteU31DataToModel = new ZTEU31DataToModel(encode);
		
		isVirtualEms = DataCollectDefine.EMS_NAME_VEMS.equals(emsName);
		this.emsSession = isVirtualEms?
				ZTEVEMSSession.newInstance(corbaName, corbaPassword, corbaIp,
						corbaPort, emsName, encode):
				ZTEU31EMSSession.newInstance(corbaName, corbaPassword, corbaIp,
				corbaPort, emsName,encode,iteratorNum);
		
	}

	@IMethodLog(desc = "DataCollectService：ZTE启动corba连接")
	public int startCorbaConnect() throws CommonException{

		int result;

		//启动连接
		if (isVirtualEms||CommonUtil.isReachable(corbaIp)) {
			result = DataCollectDefine.CONNECT_STATUS_EXCEPTION_FLAG;
			emsSession.connect();
			result = DataCollectDefine.CONNECT_STATUS_NORMAL_FLAG;
		} else {
			result = DataCollectDefine.CONNECT_STATUS_INTERRUPT_FLAG;
		}
		return result;
	}

	@IMethodLog(desc = "DataCollectService：ZTE启动telnet连接")
	public int startTelnetConnect(int neId, boolean isGateWayNe)
			throws CommonException
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@IMethodLog(desc = "DataCollectService：ZTE断开corba连接")
	public boolean disCorbaConnect() throws CommonException
	{

		boolean result = true;

		try
		{
			emsSession.endSession();
		}
		catch (Exception e)
		{
			result = false;
		}
		return result;
	}

	@IMethodLog(desc = "DataCollectService：zte断开telnet连接")
	public boolean disTelnetConnect(int neId, boolean isGateWayNe)
			throws CommonException
	{
		// TODO Auto-generated method stub
		return false;
	}
	
	@IMethodLog(desc = "DataCollectService：ZTE获取网管信息")
	public EmsDataModel getEMS() throws CommonException {

		EMS_T ems = emsSession.getEMS();

		EmsDataModel data = zteU31DataToModel.EmsDataToModel(ems);

		return data;
	}

	@IMethodLog(desc = "DataCollectService：ZTEU31获取所有网元名称列表")
	public NameAndStringValue_T[][] getAllManagedElementNames()
			throws CommonException
	{
		System.out.println("U31");
		NameAndStringValue_T[][] data = emsSession.getAllManagedElementNames();

		return data;
	}

	@IMethodLog(desc = "DataCollectService：ZTEU31获取所有网元列表")
	public List<ManagedElementModel> getAllManagedElements()
			throws CommonException
	{

		List<ManagedElementModel> data = new ArrayList<ManagedElementModel>();

		//调用session函数取得网元数据
		ManagedElement_T[] mes = emsSession.getAllManagedElements();

		//将网元数据转换为通用模板格式,供上层调用
		for (ManagedElement_T me : mes)
		{
			ManagedElementModel model = zteU31DataToModel
					.ManagedElementDataToModel(me);
			data.add(model);
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：ZTEU31获取网元所有设备信息")
	public List<EquipmentOrHolderModel> getAllEquipment(String neName)
			throws CommonException
	{

		List<EquipmentOrHolderModel> data = new ArrayList<EquipmentOrHolderModel>();

		//调用session函数取得网元数据
		EquipmentOrHolder_T[] equipmentOrHolders = emsSession
				.getAllEquipment(nameUtil.constructNeName(internalEmsName, neName));

		//将网元数据转换为通用模板格式,供上层调用
		for (EquipmentOrHolder_T equipmentOrHolder : equipmentOrHolders)
		{
			EquipmentOrHolderModel model = zteU31DataToModel
					.EquipmentOrHolderDataToModel(equipmentOrHolder);
			data.add(model);
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：ZTEU31获取网元所有ptp信息")
	public List<TerminationPointModel> getAllPTPs(String neName)
			throws CommonException
	{
		List<TerminationPointModel> data = new ArrayList<TerminationPointModel>();
		
		//调用session函数取得网元数据
		TerminationPoint_T[] ptps = emsSession.getAllPTPs(
				nameUtil.constructNeName(internalEmsName, neName));
		
		//将网元数据转换为通用模板格式,供上层调用
		for (TerminationPoint_T ptp : ptps)
		{
			TerminationPointModel model = zteU31DataToModel.TerminationPointDataToModel(ptp);
			data.add(model);
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：ZTEU31获取网元所有mstp信息")
	public List<TerminationPointModel> getAllMstpEndPoints(String neName)
			throws CommonException {
		List<TerminationPointModel> data = new ArrayList<TerminationPointModel>();
		//中兴无MSTP
		return data;
	}
	
	@IMethodLog(desc = "DataCollectService：ZTE获取网元当前性能数据信息")
	public List<PmDataModel> getCurrentPmData_Ne(String neName,
			short[] layerRateList, String[] pmLocationList,
			String[] granularityList) throws CommonException
	{
		// 采集数据集合
		List<PmDataModel> data = new ArrayList<PmDataModel>();
		
		// 采集当前性能数据
		PMData_T[] pmDataList = emsSession.getAllCurrentPMData(nameUtil
				.constructNeName(internalEmsName, neName), layerRateList,
				pmLocationList, granularityList);
		
		// 筛选数据
		if (pmDataList != null)
		{
			for (PMData_T pmData : pmDataList)
			{
				PmDataModel model = zteU31DataToModel.PMDataToModel(pmData);
				data.add(model);
			}
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：ZTE获取网元历史性能数据信息")
	public List<PmDataModel> getHistoryPmData_Ne(String targetDisplayName,
			String neName, String startTime, String endTime,
			short[] layerRateList, String[] pmLocationList,
			String[] pmGranularityList, String ip, int port,
			String userName,String password,int emsType,boolean needAnalysisPm) throws CommonException {
		
		// 采集数据集合
		List<PmDataModel> data = new ArrayList<PmDataModel>();
		
		switch(emsType){
		case DataCollectDefine.NMS_TYPE_E300_FLAG:
			data = getHistoryPmForE300(
					neName, startTime, endTime,
					layerRateList, pmLocationList,
					pmGranularityList);
			break;
		case DataCollectDefine.NMS_TYPE_U31_FLAG:
			data = getHistoryPmDataForU31(targetDisplayName,
					neName, startTime, endTime,
					layerRateList, pmLocationList,
					pmGranularityList, ip, port,
					userName, password,needAnalysisPm);
			break;
		}
		return data;
		
	}
	
	//获取历史性能For E300
	private List<PmDataModel> getHistoryPmForE300(
			String neName, String startTime, String endTime,
			short[] layerRateList, String[] pmLocationList,
			String[] pmGranularityList) throws CommonException{
		// 采集数据集合
		List<PmDataModel> data = new ArrayList<PmDataModel>();
		
		// 采集历史性能数据
		PMData_T[] pmDataList = emsSession.getHistoryPMData(nameUtil
				.constructNeName(internalEmsName, neName), startTime,endTime,
				layerRateList, pmLocationList, pmGranularityList);
		
		// 筛选数据
		if (pmDataList != null)
		{
			Date retrievalTimeDisplay;
			for (PMData_T pmData : pmDataList)
			{
				PmDataModel model = zteU31DataToModel.PMDataToModel(pmData);
				//E300 返回性能时间是性能的结束时间与其他网管不同，需要减一天处理
				retrievalTimeDisplay = CommonUtil.getSpecifiedDay(model.getRetrievalTimeDisplay(), -1, 0);
				model.setRetrievalTimeDisplay(retrievalTimeDisplay);
				
				data.add(model);
			}
		}
		return data;
	}
	//获取历史性能For U31
	private List<PmDataModel> getHistoryPmDataForU31(String fileName,
			String neName, String startTime, String endTime,
			short[] layerRateList, String[] pmLocationList,
			String[] pmGranularityList, String ip, int port, String userName,
			String password,boolean needAnalysisPm) throws CommonException {

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
						fileName, DataCollectDefine.FACTORY_ZTE_FLAG, startTime, endTime);
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
	
	//采集历史性能并判断是否成功
	private HistoryPmFileGetResult isFileTransferSuccess(String fileName,String neName, String startTime,String endTime,short[] layerRateList, 
			String[] pmLocationList,String[] pmGranularityList,String ip,
			int port,String userName,String password,FtpUtils ftpUtils) throws CommonException{
		
		//历史文件获取结果
		HistoryPmFileGetResult result = new HistoryPmFileGetResult();
		
		String ftpIpAndFileName = constructFtpDestination(ip,port,fileName,DataCollectDefine.FACTORY_ZTE_FLAG);
		
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

	@IMethodLog(desc = "DataCollectService：ZTE获取ptp当前性能数据信息")
	public List<PmDataModel> getCurrentPmData_Ptp(String neName,
			String ptpName, short[] layerRateList, String[] pmLocationList,
			String[] granularityList) throws CommonException
	{

		// 采集数据集合
		List<PmDataModel> data = new ArrayList<PmDataModel>();
		// 采集当前性能数据
		PMData_T[] pmDataList = emsSession.getAllCurrentPMData(nameUtil
				.constructName(ptpName, internalEmsName, neName), layerRateList,
				pmLocationList, granularityList);
		// 筛选数据
		if (pmDataList != null)
		{
			for (PMData_T pmData : pmDataList)
			{
				PmDataModel model = zteU31DataToModel.PMDataToModel(pmData);
				data.add(model);
			}
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：ZTEU31获取ptp集合当前性能数据信息")
	public Map<String, List<PmDataModel>> getCurrentPmData_PtpList(
			List<String> ptpNameList, short[] layerRateList,
			String[] pmLocationList, String[] granularityList)
			throws CommonException
	{

		Map<String, List<PmDataModel>> data = new HashMap<String, List<PmDataModel>>();

		List<PMTPSelect_T> selectTPList = new ArrayList<PMTPSelect_T>();

		for (String ptpName : ptpNameList)
		{
			// 端口对象组装
			PMTPSelect_T selectTP = new PMTPSelect_T(nameUtil.constructName(
					ptpName.split("::")[1], internalEmsName, ptpName.split("::")[0]),
					layerRateList, pmLocationList, granularityList);
			selectTPList.add(selectTP);
		}
		// 采集性能数据
		PMData_T[] pmDataList = emsSession.getAllCurrentPMData(selectTPList);

		for (PMData_T pmData : pmDataList)
		{
			String neName = nameUtil.getNeSerialNo(nameUtil
					.getMeNameFromPtpName(pmData.tpName));
			String ptpName = nameUtil.decompositionName(pmData.tpName);
			String key = neName + "::" + ptpName;
			PmDataModel model = zteU31DataToModel.PMDataToModel(pmData);

			if (data.containsKey(key))
			{
				data.get(key).add(model);
			}
			else
			{
				List<PmDataModel> value = new ArrayList<PmDataModel>();
				value.add(model);
				data.put(key, value);
			}
		}
		return data;
	}
	
	@IMethodLog(desc = "DataCollectService：ZTEU31获取当前网络告警信息getAllEMSAndMEActiveAlarms")
	public List<AlarmDataModel> getAllEMSAndMEActiveAlarms()
			throws CommonException
	{

		List<AlarmDataModel> data = new ArrayList<AlarmDataModel>();

		//采集告警信息
		StructuredEvent[] alarms = emsSession.getAllEMSAndMEActiveAlarms();

		for (StructuredEvent alarm : alarms)
		{
			String head = alarm.header.fixed_header.event_type.type_name;
			
			//逐条告警转换成通用格式
			if (DataCollectDefine.COMMON.NT_ALARM.equals(head))
			{
				AlarmDataModel model = zteU31DataToModel.AlarmDataToModel(alarm);
				data.add(model);
			}
			if (DataCollectDefine.COMMON.NT_TCA.equals(head))
			{
				AlarmDataModel model = zteU31DataToModel.TCADataToAlarmModel(alarm);
				data.add(model);
			}
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：ZTEU31获取当前网络告警信息getAllEMSSystemActiveAlarms")
	public List<AlarmDataModel> getAllEMSSystemActiveAlarms()
			throws CommonException
	{

		List<AlarmDataModel> data = new ArrayList<AlarmDataModel>();

		StructuredEvent[] alarms = emsSession.getAllEMSSystemActiveAlarms();

		for (StructuredEvent alarm : alarms)
		{
			String head = alarm.header.fixed_header.event_type.type_name;
			if (DataCollectDefine.COMMON.NT_ALARM.equals(head))
			{
				AlarmDataModel model = zteU31DataToModel.AlarmDataToModel(alarm);
				data.add(model);
			}
			if (DataCollectDefine.COMMON.NT_TCA.equals(head))
			{
				AlarmDataModel model = zteU31DataToModel.TCADataToAlarmModel(alarm);
				data.add(model);
			}
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：ZTEU31获取当前网络告警信息getAllActiveAlarms")
	public List<AlarmDataModel> getAllActiveAlarms(String neName)
			throws CommonException
	{

		List<AlarmDataModel> data = new ArrayList<AlarmDataModel>();

		StructuredEvent[] alarms = emsSession.getAllActiveAlarms(nameUtil
				.constructNeName(internalEmsName, neName));

		for (StructuredEvent alarm : alarms)
		{
			String head = alarm.header.fixed_header.event_type.type_name;
			if (DataCollectDefine.COMMON.NT_ALARM.equals(head))
			{
				AlarmDataModel model = zteU31DataToModel.AlarmDataToModel(alarm);
				data.add(model);
			}
			if (DataCollectDefine.COMMON.NT_TCA.equals(head))
			{
				AlarmDataModel model = zteU31DataToModel.TCADataToAlarmModel(alarm);
				data.add(model);
			}
		}
		return data;
	}
	
	@IMethodLog(desc = "DataCollectService：ZTEU31确认告警列表")
	public String[] acknowledgeAlarms(List<String> alarmList)
			throws CommonException
	{
		//待完成
		throw new CommonException(null,MessageCodeDefine.CORBA_UNSUPPORTED_COMMAND_EXCEPTION);
		//return emsSession.acknowledgeAlarms(alarmList);
	}

	@IMethodLog(desc = "DataCollectService：ZTEU31获取网元以太网业务")
	public List<EthServiceModel> getAllEthService(String neName)
			throws CommonException
	{
		EthernetService_T[] allEthService = emsSession.getAllEthService(nameUtil
				.constructNeName(internalEmsName, neName));
		List<EthServiceModel> data = new ArrayList<EthServiceModel>();

		for (EthernetService_T ethService : allEthService)
		{
			EthServiceModel model = zteU31DataToModel.EthServiceDataToModel(ethService);
			data.add(model);
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：ZTEU31获取虚拟网桥信息")
	public List<VirtualBridgeModel> getAllVBs(String neName)
			throws CommonException {

		VB_T[] allVBs = emsSession.getAllVBs(nameUtil.constructNeName(internalEmsName, neName));
		List<VirtualBridgeModel> data = new ArrayList<VirtualBridgeModel>();

		for (VB_T vb : allVBs) {
			VirtualBridgeModel model = zteU31DataToModel
					.VirtualBridgeDataToModel(vb);
			data.add(model);
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：ZTEU31获取网元电保护组信息")
	public List<EProtectionGroupModel> getAllEProtectionGroups(String neName)
			throws CommonException
	{
		EProtectionGroup_T[] dataList = emsSession
				.getAllEProtectionGroups(nameUtil.constructNeName(internalEmsName,
						neName));
		List<EProtectionGroupModel> data = new ArrayList<EProtectionGroupModel>();

		for (EProtectionGroup_T tempData : dataList)
		{
			EProtectionGroupModel model = zteU31DataToModel
					.EProtectionGroupDataToModel(tempData);
			data.add(model);
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：ZTEU31获取网元保护组信息")
	public List<ProtectionGroupModel> getAllProtectionGroups(String neName)
			throws CommonException{
		MEConfigData_T configData = emsSession.getMEconfigData(nameUtil
				.constructNeName(internalEmsName, neName));
		// 获取保护数据
		ProtectionGroup_T[] protectionGroupList = null;
		//any数据获取
		try {
			if(configData.configDatas[0].value.type().name().equals("ProtectionGroupList_T")){
				protectionGroupList = ProtectionGroupList_THelper
						.read(configData.configDatas[0].value.create_input_stream());
			}
		} catch (BadKind e1) {
		}

		List<ProtectionGroupModel> data = new ArrayList<ProtectionGroupModel>();

		if(protectionGroupList!=null){
			for (ProtectionGroup_T pg : protectionGroupList) {
				ProtectionGroupModel model = zteU31DataToModel
						.ProtectionGroupDataToModel(pg);
				data.add(model);
			}
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：ZTEU31获取网元时钟信息")
	public List<ClockSourceStatusModel> getObjectClockSourceStatus(String neName)
			throws CommonException
	{
		ClockSource_T[] dataList = emsSession.getObjectClockSourceStatus(
				nameUtil.constructNeName(emsName, neName));
		
		List<ClockSourceStatusModel> data = new ArrayList<ClockSourceStatusModel>();

		if(null == dataList)
		{
			System.out.println("取时钟数据不成功，返回空串");
			return data;
		}
		for (ClockSource_T tempData : dataList)
		{
			ClockSourceStatusModel model = zteU31DataToModel.ClockSourceDataToModel(tempData);
			data.add(model);
		}
		
		return data;
	}

	@IMethodLog(desc = "DataCollectService：ZTEU31获取网元间link信息")
	public List<TopologicalLinkModel> getAllTopologicalLinks()
			throws CommonException
	{
		//获取外部link
		List<TopologicalLinkModel> data = getLinkFromTopologicalLinks(1,null);
		
		return data;
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
		MultiLayerSubnetwork_T[] subnetWorks = emsSession.getAllTopLevelSubnetworks();

		for (MultiLayerSubnetwork_T subnet : subnetWorks)
		{
			// 取得网络link信息
			TopologicalLink_T[] linkList = emsSession.getAllTopologicalLinks(subnet.name);

			for (TopologicalLink_T link : linkList)
			{
				linkListData.add(link);
			}
		}

		List<TopologicalLinkModel> data = new ArrayList<TopologicalLinkModel>();

		for (TopologicalLink_T link : linkListData)
		{
			TopologicalLinkModel model = zteU31DataToModel
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

	@IMethodLog(desc = "DataCollectService：ZTEU31获取网元内部link信息")
	public List<TopologicalLinkModel> getAllInternalTopologicalLinks(
			String neName) throws CommonException
	{
		List<TopologicalLinkModel> data = new ArrayList<TopologicalLinkModel>();
		//以网元为单位获取内部link，e300支持，U31好像不支持，所以要加入异常捕获
		try{
			TopologicalLink_T[] links = emsSession
					.getAllTopologicalLinks(nameUtil.constructNeName(
							internalEmsName, neName));

			for (TopologicalLink_T link : links)
			{
				TopologicalLinkModel model = zteU31DataToModel
						.TopologicalLinkDataToModel(link);
				data.add(model);
			}
		}catch(Exception e){
			//不做处理
		}
		//添加外部link中的内部link
		data.addAll(getLinkFromTopologicalLinks(2,neName));
		//link去重
		data = distinctLinkData(data);

		return data;
	}
	
	@IMethodLog(desc = "DataCollectService：ZTEU31获取网元SNC数据")
	@Override
	public List<SubnetworkConnectionModel> getAllSubnetworkConnections() throws CommonException
	{
		MultiLayerSubnetwork_T[] subnetworks = emsSession.getAllTopLevelSubnetworks();
		
		List<SubnetworkConnectionModel> data = new ArrayList<SubnetworkConnectionModel>();

		for(MultiLayerSubnetwork_T subnetwork:subnetworks){
			SubnetworkConnection_T[]  sncList = emsSession.getAllSubnetworkConnections(subnetwork.name);
			
			for(SubnetworkConnection_T snc:sncList){
				SubnetworkConnectionModel model = zteU31DataToModel.SNCDataToModel(snc);
				model.setSubnetworkName(subnetwork.name[subnetwork.name.length-1].value);
				data.add(model);
			}
		}
		return data;
	}
	
	@IMethodLog(desc = "DataCollectService：ZTEU31获取Route数据")
	@Override
	public List<CrossConnectModel> getRoute(boolean needSort) throws CommonException
	{
		List<SubnetworkConnectionModel> sncList = getAllSubnetworkConnections();
		
		List<CrossConnectModel> data = new ArrayList<CrossConnectModel>();

		for(SubnetworkConnectionModel snc:sncList){
			CrossConnect_T[]  routeList = emsSession.getRoute(snc.getName());
			
			if(needSort){
				//暂存数据
				List<CrossConnectModel> sortedData = new ArrayList<CrossConnectModel>();
				List<CrossConnectModel> pw = new ArrayList<CrossConnectModel>();
				List<CrossConnectModel> srcOrDest = new ArrayList<CrossConnectModel>();
				List<CrossConnectModel> normal = new ArrayList<CrossConnectModel>();
				List<CrossConnectModel> original = new ArrayList<CrossConnectModel>();
				
				for(CrossConnect_T route:routeList){
					CrossConnectModel model = zteU31DataToModel.CCDataToModel(route);
					model.setBelongedTrail(snc.getName()[2].value);
					original.add(model);
					if(model.getRouteType() == ZTEU31DataToModel.ROUTE_TYPE_SRC_OR_DEST){
						srcOrDest.add(model);
					}else if(model.getRouteType() == ZTEU31DataToModel.ROUTE_TYPE_PW){
						pw.add(model);
					}else{
						normal.add(model);
					}
				}
				//对路由中数据按规则进行排序
				if(srcOrDest.size()>0){
					sortedData = sortRouteDetailData(snc,pw,srcOrDest,normal,original);
				}
				//
				data.addAll(sortedData);
			}else{
				for(CrossConnect_T route:routeList){
					CrossConnectModel model = zteU31DataToModel.CCDataToModel(route);
					model.setBelongedTrail(snc.getName()[2].value);
					data.add(model);
				}
			}
		}
		return data;
	}
	
	// 对路由中数据进行排序
	public static List<CrossConnectModel> sortRouteDetailData(
			SubnetworkConnectionModel snc,
			List<CrossConnectModel> pwData,
			List<CrossConnectModel> srcOrDestData,
			List<CrossConnectModel> normalData,
			List<CrossConnectModel> originalData) {
		//排序容器
		List<CrossConnectModel> sortedData = new ArrayList<CrossConnectModel>();
		
		CrossConnectModel src = null;
		CrossConnectModel dest = null;
		
//		CrossConnectModel pwSrc = null;
//		CrossConnectModel pwDest = null;
		//获取头元素 非pw情况
		if(pwData.size() == 0){
			if(srcOrDestData.size() == 1){
				src = srcOrDestData.get(0);
			}else if(srcOrDestData.size() == 2){
				if(srcOrDestData.get(0).getaEndNameList()[0][0].value.equals(snc.getaEndTP()[0].value) &&
						srcOrDestData.get(0).getaEndNameList()[0][1].value.equals(snc.getaEndTP()[1].value) &&
						srcOrDestData.get(0).getaEndNameList()[0][2].value.equals(snc.getaEndTP()[2].value)&&
						srcOrDestData.get(0).getaEndNameList()[0][3].value.equals(snc.getaEndTP()[3].value)){
					src = srcOrDestData.get(0);
					dest = srcOrDestData.get(1);
				}else{
					src = srcOrDestData.get(1);
					dest = srcOrDestData.get(0);
				}
			}else{
				//数据错误
			}
		}else{
			//pw情况
			if(srcOrDestData.size() == 1){
				src = srcOrDestData.get(0);
			}else if(srcOrDestData.size() == 2){
				//比较至网元
				if(srcOrDestData.get(0).getaEndNameList()[0][0].value.equals(snc.getaEndTP()[0].value) &&
						srcOrDestData.get(0).getaEndNameList()[0][1].value.equals(snc.getaEndTP()[1].value) &&
						srcOrDestData.get(0).getaEndNameList()[0][2].value.equals(snc.getaEndTP()[2].value)){
					src = srcOrDestData.get(0);
					dest = srcOrDestData.get(1);
				}else{
					src = srcOrDestData.get(1);
					dest = srcOrDestData.get(0);
				}
			}
		}
		//排序
		sortedData = sortRouteDetailData(src,normalData);
		//无法排序 打印数据
		if(sortedData == null){
			for(CrossConnectModel cc:originalData){
				System.out.println("*******************originalData error sorted: "+cc.getSrcNextHopIP()+"__"+cc.getDestNextHopIP());
			}
		}
		//插入头尾数据
		if(src!=null){
			sortedData.add(0, src);
		}
		if(dest!=null){
			sortedData.add(dest);
		}
		if(pwData.size()>0){
			if(pwData.get(0).getaEndNameList()[0][2].value.equals(sortedData.get(0).getaEndNameList()[0][2].value)){
				sortedData.add(0, pwData.get(0));
				sortedData.add(pwData.get(1));
			}else{
				sortedData.add(0, pwData.get(1));
				sortedData.add(pwData.get(0));
			}
		}
		int sequence = 1;
		for(CrossConnectModel cc:sortedData){
			cc.setSequence(sequence);
			sequence++;
		}
		return sortedData;
	}
	
	// 排序
	private static List<CrossConnectModel> sortRouteDetailData(
			CrossConnectModel src, List<CrossConnectModel> normalData) {
		List<CrossConnectModel> sortedData = new ArrayList<CrossConnectModel>();
		while (normalData.size() > 0) {
			CrossConnectModel next = getNextCC(src, normalData);
			if (next == null) {
				sortedData = null;
				break;
			}
			src = next;
			sortedData.add(next);
			normalData.remove(next);
		}
		return sortedData;
	}
	
	// 获取下一跳
	private static CrossConnectModel getNextCC(CrossConnectModel srcIpNum,
			List<CrossConnectModel> pool) {
		CrossConnectModel model = null;
		for (CrossConnectModel cc : pool) {
			if (srcIpNum.getSrcNextHopIPNum() + 1 == cc.getSrcNextHopIPNum()
					|| srcIpNum.getSrcNextHopIPNum() - 1 == cc
							.getSrcNextHopIPNum()
					|| srcIpNum.getSrcNextHopIPNum() + 1 == cc
							.getDestNextHopIPNum()
					|| srcIpNum.getSrcNextHopIPNum() - 1 == cc
							.getDestNextHopIPNum()) {
				model = cc;
				break;
			} else if (srcIpNum.getDestNextHopIPNum() != null) {
				if (srcIpNum.getDestNextHopIPNum() + 1 == cc
						.getSrcNextHopIPNum()
						|| srcIpNum.getDestNextHopIPNum() - 1 == cc
								.getSrcNextHopIPNum()
						|| srcIpNum.getDestNextHopIPNum() + 1 == cc
								.getDestNextHopIPNum()
						|| srcIpNum.getDestNextHopIPNum() - 1 == cc
								.getDestNextHopIPNum()) {
					model = cc;
					break;
				}
			}
		}
		return model;
	}
	
	public static void main(String args[]){
		CrossConnectModel o1 = new CrossConnectModel();
		o1.setSrcNextHopIP("10.4.15.2");
		o1.setDestNextHopIP("10.4.14.1");
		
		CrossConnectModel o2 = new CrossConnectModel();
		o2.setSrcNextHopIP("10.4.18.1");
		o2.setDestNextHopIP("10.4.16.1");
		
		CrossConnectModel o3 = new CrossConnectModel();
		o3.setSrcNextHopIP("10.4.16.2");
		o3.setDestNextHopIP("10.4.15.1");
		
		CrossConnectModel o4 = new CrossConnectModel();
		o4.setSrcNextHopIP("10.4.14.2");
		o4.setDestNextHopIP("10.4.13.1");
		
		List<CrossConnectModel> normal = new ArrayList<CrossConnectModel>();
		normal.add(o1);
		normal.add(o2);
		normal.add(o3);
		normal.add(o4);
		
		CrossConnectModel o5 = new CrossConnectModel();
		o5.setSrcNextHopIP("10.4.13.2");
		CrossConnectModel o6 = new CrossConnectModel();
		o6.setSrcNextHopIP("10.4.18.2");
		List<CrossConnectModel> srcOrDestData = new ArrayList<CrossConnectModel>();
		srcOrDestData.add(o5);
		srcOrDestData.add(o6);
		
//		CrossConnectModel o5 = new CrossConnectModel();
//		o5.setSrcNextHopIP("10.4.13.2");
//		CrossConnectModel o6 = new CrossConnectModel();
//		o6.setSrcNextHopIP("10.4.18.2");
//		List<CrossConnectModel> srcOrDestData = new ArrayList<CrossConnectModel>();
//		srcOrDestData.add(o5);
//		srcOrDestData.add(o6);
		
//		sortRouteDetailData(new ArrayList<CrossConnectModel>(),srcOrDestData,normal);
	}

	@IMethodLog(desc = "DataCollectService：ZTEU31获取网元内部交叉连接信息")
	public List<CrossConnectModel> getCRS(String neName,
			short[] connectionRateList) throws CommonException
	{

		List<CrossConnectModel> data = new ArrayList<CrossConnectModel>();

		CrossConnect_T[] ccs = emsSession.getAllCrossConnections(nameUtil
				.constructNeName(internalEmsName, neName), new short[]{});
		
		for (CrossConnect_T cc : ccs)
		{
			CrossConnectModel model = zteU31DataToModel.CCDataToModel(cc);
			data.add(model);
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：ZTEU31获取指定端口CTP信息")
	public List<TerminationPointModel> getContainedPotentialTPs(
			NameAndStringValue_T[] ptpName) throws CommonException
	{

		List<TerminationPointModel> data = new ArrayList<TerminationPointModel>();

		TerminationPoint_T[] tps = emsSession
				.getContainedPotentialTPs(ptpName);

		for (TerminationPoint_T tp : tps)
		{
			TerminationPointModel model = zteU31DataToModel
					.TerminationPointDataToModel(tp);
			data.add(model);
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：ZTEU31获取bindingPath信息")
	public List<MSTPBindingPathModel> getBindingPath(
			NameAndStringValue_T[] ptpName) throws CommonException {
		List<MSTPBindingPathModel> data = new ArrayList<MSTPBindingPathModel>();
		VCGBinding_T bindingPath = emsSession.getBindingPath(ptpName);
		
		if(bindingPath!=null){
			MSTPBindingPathModel model = zteU31DataToModel
					.MSTPBindingPathToModel(bindingPath);
			data.add(model);
		}
		return data;
	}


	/**
	 * 获取网元wdm环保护
	 * 
	 * @param neName
	 * @return
	 * @throws CommonException
	 */
	public List<WDMProtectionGroupModel> getAllWDMProtectionGroups(String neName)
			throws CommonException {
		List<WDMProtectionGroupModel> listWDMProtectionGroupModel = new ArrayList<WDMProtectionGroupModel>();
		return listWDMProtectionGroupModel;
	}

}

