package com.fujitsu.manager.dataCollectManager.serviceImpl.FIMCorba;

import globaldefs.NameAndStringValue_T;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import FENGHUO.CosNotification.StructuredEvent;
import FENGHUO.emsMgr.EMS_T;
import FENGHUO.equipment.EquipmentOrHolder_T;
import FENGHUO.managedElement.ManagedElement_T;
import FENGHUO.multiLayerSubnetwork.MultiLayerSubnetwork_T;
import FENGHUO.performance.PMData_T;
import FENGHUO.performance.PMTPSelect_T;
import FENGHUO.subnetworkConnection.CrossConnect_T;
import FENGHUO.terminationPoint.TerminationPoint_T;
import FENGHUO.topologicalLink.TopologicalLink_T;

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
import com.fujitsu.manager.dataCollectManager.corbaDataModel.FdfrModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.FlowDomainModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.MSTPBindingPathModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ManagedElementModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ProtectionGroupModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TerminationPointModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TopologicalLinkModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.VirtualBridgeModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.WDMProtectionGroupModel;
import com.fujitsu.manager.dataCollectManager.service.EMSCollectService;
import com.fujitsu.manager.dataCollectManager.serviceImpl.VEMS.FIMVEMSSession;
import com.fujitsu.manager.dataCollectManager.serviceImpl.VEMS.IFIMEMSSession;
import com.fujitsu.model.HistoryPmFileGetResult;
import com.fujitsu.util.CommonUtil;
import com.fujitsu.util.FtpUtils;

public class FIMServiceImpl extends EMSCollectService {
	boolean isVirtualEms = true;
	private IFIMEMSSession emsSession;

	@IMethodLog(desc = "DataCollectService：FIM初始化连接参数")
	public void initParameter(String corbaName, String corbaPassword,
			String corbaIp, String corbaPort, String emsName,
			String internalEmsName,String encode,int iteratorNum) throws CommonException {
		
		super.initParameter(corbaName, corbaPassword,
				corbaIp, corbaPort, emsName,
				internalEmsName);

		fimDataToModel = new FIMDataToModel(encode);

		isVirtualEms = DataCollectDefine.EMS_NAME_VEMS.equals(emsName);
		this.emsSession = isVirtualEms?
				FIMVEMSSession.newInstance(corbaName, corbaPassword, corbaIp,
						corbaPort, emsName, encode):
				FIMEMSSession.newInstance(corbaName, corbaPassword, corbaIp,
						corbaPort, emsName, encode,iteratorNum);
	}

	@IMethodLog(desc = "DataCollectService：FIM启动corba连接")
	public int startCorbaConnect() throws CommonException {

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

	@IMethodLog(desc = "DataCollectService：FIM启动telnet连接")
	public int startTelnetConnect(int neId, boolean isGateWayNe)
			throws CommonException {
		// TODO Auto-generated method stub
		return 0;
	}

	@IMethodLog(desc = "DataCollectService：FIM断开corba连接")
	public boolean disCorbaConnect() throws CommonException {

		boolean result = true;

		try {
			emsSession.endSession();
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	@IMethodLog(desc = "DataCollectService：FIM断开telnet连接")
	public boolean disTelnetConnect(int neId, boolean isGateWayNe)
			throws CommonException {
		// TODO Auto-generated method stub
		return false;
	}
	
	@IMethodLog(desc = "DataCollectService：FIM获取网管信息")
	public EmsDataModel getEMS() throws CommonException {

		EMS_T ems = emsSession.getEMS();

		EmsDataModel data = fimDataToModel.EmsDataToModel(ems);

		return data;
	}

	@IMethodLog(desc = "DataCollectService：FIM获取所有网元名称列表")
	public NameAndStringValue_T[][] getAllManagedElementNames()
			throws CommonException {

		NameAndStringValue_T[][] data = emsSession.getAllManagedElementNames();

		return data;
	}

	@IMethodLog(desc = "DataCollectService：FIM获取所有网元列表")
	public List<ManagedElementModel> getAllManagedElements()
			throws CommonException {

		List<ManagedElementModel> data = new ArrayList<ManagedElementModel>();

		ManagedElement_T[] mes = emsSession.getAllManagedElements();

		for (ManagedElement_T me : mes) {
			ManagedElementModel model = fimDataToModel
					.ManagedElementDataToModel(me);
			data.add(model);
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：FIM获取网元所有设备信息")
	public List<EquipmentOrHolderModel> getAllEquipment(String neName)
			throws CommonException {

		List<EquipmentOrHolderModel> data = new ArrayList<EquipmentOrHolderModel>();

		EquipmentOrHolder_T[] equipmentOrHolders = emsSession
				.getAllEquipment(nameUtil.constructNeName(internalEmsName, neName));

		for (EquipmentOrHolder_T equipmentOrHolder : equipmentOrHolders) {
			EquipmentOrHolderModel model = fimDataToModel
					.EquipmentOrHolderDataToModel(equipmentOrHolder);
			data.add(model);
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：FIM获取网元所有ptp信息")
	public List<TerminationPointModel> getAllPTPs(String neName)
			throws CommonException {
		List<TerminationPointModel> data = new ArrayList<TerminationPointModel>();
		TerminationPoint_T[] ptps = emsSession.getAllPTPs(nameUtil
				.constructNeName(internalEmsName, neName));
		for (TerminationPoint_T ptp : ptps) {
			TerminationPointModel model = fimDataToModel
					.TerminationPointDataToModel(ptp);
			data.add(model);
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：FIM获取网元所有mstp信息")
	public List<TerminationPointModel> getAllMstpEndPoints(String neName)
			throws CommonException {
		return new ArrayList<TerminationPointModel>();
		//throw new CommonException(null,MessageCodeDefine.CORBA_UNSUPPORTED_COMMAND_EXCEPTION);
	}

	@IMethodLog(desc = "DataCollectService：FIM获取网元当前性能数据信息")
	public List<PmDataModel> getCurrentPmData_Ne(String neName,
			short[] layerRateList, String[] pmLocationList,
			String[] granularityList) throws CommonException {

		// 采集数据集合
		List<PmDataModel> data = new ArrayList<PmDataModel>();
		// 采集当前性能数据
		PMData_T[] pmDataList = emsSession.getAllCurrentPMData(
				nameUtil.constructNeName(internalEmsName, neName), 
				layerRateList, pmLocationList, granularityList);
		// 筛选数据
		if (pmDataList != null) {
			for (PMData_T pmData : pmDataList) {
				PmDataModel model = fimDataToModel.PMDataToModel(pmData);
				data.add(model);
			}
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：FIM获取网元历史性能数据信息")
	public List<PmDataModel> getHistoryPmData_Ne(String fileName,
			String neName, String startTime,String endTime,short[] layerRateList, 
			String[] pmLocationList,String[] pmGranularityList,String ip,
			int port, String userName,String password,int emsType,boolean needAnalysisPm)
			throws CommonException {
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
		//传送成功后解析数据
		if (result.isResult()) {
			if(needAnalysisPm){
				pmDataList = getPmDataFromFtp(ip, port, userName, password,
						fileName, DataCollectDefine.FACTORY_FIBERHOME_FLAG, startTime, endTime);
			}
		}else{
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
		
		String ftpIpAndFileName = constructFtpDestination(ip,port,fileName,DataCollectDefine.FACTORY_FIBERHOME_FLAG);
		
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

	@IMethodLog(desc = "DataCollectService：FIM获取ptp当前性能数据信息")
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
				PmDataModel model = fimDataToModel.PMDataToModel(pmData);
				data.add(model);
			}
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：FIM获取ptp集合当前性能数据信息")
	public Map<String, List<PmDataModel>> getCurrentPmData_PtpList(
			List<String> ptpNameList, short[] layerRateList,
			String[] pmLocationList, String[] granularityList)
			throws CommonException {
		Map<String, List<PmDataModel>> data = new HashMap<String, List<PmDataModel>>();

		List<PMTPSelect_T> selectTPList = new ArrayList<PMTPSelect_T>();

		for (String ptpName : ptpNameList) {
			// 端口对象组装
			//性能监控的速率层次layerRateList只支持空, pMLocationList－只支持空
			PMTPSelect_T selectTP = new PMTPSelect_T(nameUtil.constructName(
					ptpName.split("::")[1], internalEmsName, ptpName.split("::")[0]),
					new short[]{}, new String[]{}, granularityList);
			selectTPList.add(selectTP);
		}
		// 采集性能数据
		PMData_T[] pmDataList = emsSession.getAllCurrentPMData(selectTPList);

		for (PMData_T pmData : pmDataList) {
			String neName = nameUtil.getNeSerialNo(nameUtil
					.getMeNameFromPtpName(pmData.tpName));
			String ptpName = nameUtil.decompositionName(pmData.tpName);
			String key = neName + "::" + ptpName;
			PmDataModel model = fimDataToModel.PMDataToModel(pmData);

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

	@IMethodLog(desc = "DataCollectService：FIM获取当前网络告警信息getAllEMSAndMEActiveAlarms")
	public List<AlarmDataModel> getAllEMSAndMEActiveAlarms()
			throws CommonException {

		List<AlarmDataModel> data = new ArrayList<AlarmDataModel>();

		StructuredEvent[] alarms = emsSession.getAllEMSAndMEActiveAlarms();

		for (StructuredEvent alarm : alarms) {
			String head = alarm.header.fixed_header.event_type.type_name;
			if (DataCollectDefine.COMMON.NT_ALARM.equals(head)) {
				AlarmDataModel model = fimDataToModel.AlarmDataToModel(alarm);
				data.add(model);
			}
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：FIM获取当前网络告警信息getAllEMSSystemActiveAlarms")
	public List<AlarmDataModel> getAllEMSSystemActiveAlarms()
			throws CommonException {

		List<AlarmDataModel> data = new ArrayList<AlarmDataModel>();

		StructuredEvent[] alarms = emsSession.getAllEMSSystemActiveAlarms();

		for (StructuredEvent alarm : alarms) {
			String head = alarm.header.fixed_header.event_type.type_name;
			if (DataCollectDefine.COMMON.NT_ALARM.equals(head)) {
				AlarmDataModel model = fimDataToModel.AlarmDataToModel(alarm);
				data.add(model);
			}
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：FIM获取当前网络告警信息getAllActiveAlarms")
	public List<AlarmDataModel> getAllActiveAlarms(String neName)
			throws CommonException {

		List<AlarmDataModel> data = new ArrayList<AlarmDataModel>();

		StructuredEvent[] alarms = emsSession.getAllActiveAlarms(nameUtil
				.constructNeName(internalEmsName, neName));

		for (StructuredEvent alarm : alarms) {
			String head = alarm.header.fixed_header.event_type.type_name;
			if (DataCollectDefine.COMMON.NT_ALARM.equals(head)) {
				AlarmDataModel model = fimDataToModel.AlarmDataToModel(alarm);
				data.add(model);
			}
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：FIM确认告警列表")
	public String[] acknowledgeAlarms(List<String> alarmList)
			throws CommonException {
		throw new CommonException(null,MessageCodeDefine.CORBA_UNSUPPORTED_COMMAND_EXCEPTION);
		//FIXME 烽火 确认告警 参数及返回值 待转换
		//emsSession.acknowledgeAlarms(new AlarmOrTCAIdentifier_T[]{});
		//return new String[]{};
	}

	@IMethodLog(desc = "DataCollectService：FIM获取网元以太网业务")
	public List<EthServiceModel> getAllEthService(String neName)
			throws CommonException {
		return new ArrayList<EthServiceModel>();
		//throw new CommonException(null,MessageCodeDefine.CORBA_UNSUPPORTED_COMMAND_EXCEPTION);
	}

	@IMethodLog(desc = "DataCollectService：FIM获取虚拟网桥信息")
	public List<VirtualBridgeModel> getAllVBs(String neName)
			throws CommonException {
		return new ArrayList<VirtualBridgeModel>();
		//throw new CommonException(null,MessageCodeDefine.CORBA_UNSUPPORTED_COMMAND_EXCEPTION);
	}

	@IMethodLog(desc = "DataCollectService：FIM获取网元电保护组信息")
	public List<EProtectionGroupModel> getAllEProtectionGroups(String neName)
			throws CommonException {
		/*EProtectionGroup_T[] dataList = emsSession
				.getAllEProtectionGroups(nameUtil.constructNeName(internalEmsName,
						neName));
		List<EProtectionGroupModel> data = new ArrayList<EProtectionGroupModel>();

		for (EProtectionGroup_T tempData : dataList) {
			EProtectionGroupModel model = fimDataToModel
					.EProtectionGroupDataToModel(tempData);
			data.add(model);
		}
		return data;*/
		//FIXME 烽火 保护 待数据分析
		return new ArrayList<EProtectionGroupModel>();
	}

	@IMethodLog(desc = "DataCollectService：FIM获取网元保护组信息")
	public List<ProtectionGroupModel> getAllProtectionGroups(String neName)
			throws CommonException {
		/*ProtectionGroup_T[] dataList = emsSession
				.getAllProtectionGroups(nameUtil.constructNeName(internalEmsName,
						neName));
		List<ProtectionGroupModel> data = new ArrayList<ProtectionGroupModel>();

		for (ProtectionGroup_T tempData : dataList) {
			ProtectionGroupModel model = fimDataToModel
					.ProtectionGroupDataToModel(tempData);
			data.add(model);
		}
		return data;*/
		//FIXME 烽火 保护 待数据分析
		return new ArrayList<ProtectionGroupModel>();
	}

	@IMethodLog(desc = "DataCollectService：FIM获取网元WDM保护组信息")
	public List<WDMProtectionGroupModel> getAllWDMProtectionGroups(String neName)
			throws CommonException {
		return new ArrayList<WDMProtectionGroupModel>();
		//throw new CommonException(null,MessageCodeDefine.CORBA_UNSUPPORTED_COMMAND_EXCEPTION);
	}

	@IMethodLog(desc = "DataCollectService：FIM获取网元时钟信息")
	public List<ClockSourceStatusModel> getObjectClockSourceStatus(String neName)
			throws CommonException {
		//FIXME 烽火 时钟 待数据分析
		return new ArrayList<ClockSourceStatusModel>();
		//throw new CommonException(null,MessageCodeDefine.CORBA_UNSUPPORTED_COMMAND_EXCEPTION);
	}

	@IMethodLog(desc = "DataCollectService：FIM获取网元间link信息")
	public List<TopologicalLinkModel> getAllTopologicalLinks()
			throws CommonException {
		
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
			TopologicalLinkModel model = fimDataToModel
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

	@IMethodLog(desc = "DataCollectService：FIM获取网元内部link信息")
	public List<TopologicalLinkModel> getAllInternalTopologicalLinks(
			String neName) throws CommonException {
		List<TopologicalLinkModel> data = new ArrayList<TopologicalLinkModel>();
		//添加外部link中的内部link
		data.addAll(getLinkFromTopologicalLinks(2,neName));

		return data;
	}

	@IMethodLog(desc = "DataCollectService：FIM获取网元内部交叉连接信息")
	public List<CrossConnectModel> getCRS(String neName,
			short[] connectionRateList) throws CommonException {

		List<CrossConnectModel> data = new ArrayList<CrossConnectModel>();

		CrossConnect_T[] ccs = emsSession.getAllCrossConnections(
				nameUtil.constructNeName(internalEmsName, neName), connectionRateList);

		for (CrossConnect_T cc : ccs) {
			CrossConnectModel model = fimDataToModel.CCDataToModel(cc);
			data.add(model);
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：FIM获取指定端口CTP信息")
	public List<TerminationPointModel> getContainedPotentialTPs(
			NameAndStringValue_T[] ptpName) throws CommonException {

		List<TerminationPointModel> data = new ArrayList<TerminationPointModel>();

		TerminationPoint_T[] tps = emsSession
				.getContainedPotentialTPs(ptpName);

		for (TerminationPoint_T tp : tps) {
			TerminationPointModel model = fimDataToModel
					.TerminationPointDataToModel(tp);
			data.add(model);
		}
		return data;
	}

	@IMethodLog(desc = "DataCollectService：FIM获取bindingPath信息")
	public List<MSTPBindingPathModel> getBindingPath(
			NameAndStringValue_T[] ptpName) throws CommonException {
		return new ArrayList<MSTPBindingPathModel>();
		//throw new CommonException(null,MessageCodeDefine.CORBA_UNSUPPORTED_COMMAND_EXCEPTION);
	}

}
