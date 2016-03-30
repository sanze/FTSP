package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.ProtectionSwtichDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.StateDataModel;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.TCADataModel;
import com.fujitsu.model.LinkAlterModel;
import com.fujitsu.model.LinkAlterResultModel;
import com.fujitsu.model.NeAlterModel;

/**
 * @author xuxiaojun
 * 
 */
public interface IDataCollectService {

//	/**
//	 * 初始化网管参数
//	 * @param emsConnectionId
//	 * @throws CommonException
//	 */
//	public void initParameter(int emsConnectionId) throws CommonException;

	/**
	 * 启动corba连接
	 * 
	 * @return
	 */
	public int startCorbaConnect(Map paramter) throws CommonException;

	/**
	 * 断开corba连接
	 * @return
	 */
	public boolean disCorbaConnect(Map paramter) throws CommonException;
	
	/**
	 * 启动telnet连接
	 * @return
	 */
	public int startTelnetConnect(Map paramter)
			throws CommonException;

	/**
	 * 断开telnet连接
	 * @return
	 */
	public boolean disTelnetConnect(Map paramter)
			throws CommonException;
	
	/**
	 * 登录telnet网元
	 * @param neId
	 * @return
	 */
	public int logonTelnetNe(Map paramter,int neId);
	
	/**
	 * 退出登录telnet网元
	 * @param neId
	 * @return
	 */
	public int logoutTelnetNe(Map paramter,int neId);
	
	/**
	 * 同步网管信息
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncEmsInfo(Map paramter,int commandLevel) throws CommonException;
	

	/**
	 * 获取网元变更列表
	 * @param commandLevel
	 * @return
	 * @throws CommonException
	 */
	public List<NeAlterModel> getNeAlertList(Map paramter,int commandLevel)
			throws CommonException;
	
	/**
	 * 同步网元列表，仅仅同步网元信息
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNeList(Map paramter,int commandLevel) throws CommonException;

	
	/**
	 * 同步snc数据
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncSNC(Map paramter,int commandLevel) throws CommonException;
	
	/**
	 * 同步route数据
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncRoute(Map paramter,int commandLevel) throws CommonException;
	/**
	 * @param neId
	 * @param isSyncPtp
	 * @param isSyncMstp
	 * @param isSyncInternalLink
	 * @param isSyncEprotection
	 * @param isSyncProtection
	 * @param isSyncWdmProtection
	 * @param isSyncClock
	 * @param isSyncCtp
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncSingleNeData(Map paramter,int neId, boolean isSyncPtp,
			boolean isSyncMstp, boolean isSyncInternalLink,
			boolean isSyncEprotection, boolean isSyncProtection,
			boolean isSyncWdmProtection, boolean isSyncClock,
			boolean isSyncCtp, int commandLevel) throws CommonException;

	/**
	 * 同步网元设备信息，包含rack shelf slot subslot unit subunit
	 * @param neId
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNeEquipmentOrHolder(Map paramter,int neId, int commandLevel)
			throws CommonException;

	/**
	 * 同步网元ptp信息
	 * @param neId
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNePtp(Map paramter,int neId, int commandLevel) throws CommonException;

//	/**
//	 * 
//	 * @param neId
//	 * @param commandLevel
//	 * @throws CommonException
//	 */
//	public void syncNeMstpEndPoints(int neId, int commandLevel)
//			throws CommonException;

	/**
	 * 同步网元内部link
	 * @param neId
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNeInternalLink(Map paramter,int neId, int commandLevel)
			throws CommonException;

	/**
	 * 同步网元板卡保护信息
	 * @param neId
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNeEProtectionGroup(Map paramter,int neId, int commandLevel)
			throws CommonException;

	/**
	 * 同步网元环保护
	 * @param neId
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNeProtectionGroup(Map paramter,int neId, int commandLevel)
			throws CommonException;

	/**
	 * 同步wdm网元环保护
	 * @param neId
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNeWDMProtectionGroup(Map paramter,int neId, int commandLevel)
			throws CommonException;
	
	
	/**
	 * 同步网元ctp信息
	 * @param neId
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNeCtp(Map paramter,int neId, int commandLevel) throws CommonException;
	
	/**
	 * 网元附加数据处理
	 * @param paramter
	 * @param neId
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncExtendInfo_NE(Map paramter,int neId, int commandLevel)
			throws CommonException;
//	/**
//	 * 同步网元sdh ctp信息
//	 * @param neId
//	 * @param commandLevel
//	 * @throws CommonException
//	 */
//	public void syncNeSdhCtp(int neId, int commandLevel) throws CommonException;
//
//	/**
//	 * 同步网元otn ctp信息
//	 * @param neId
//	 * @param commandLevel
//	 * @throws CommonException
//	 */
//	public void syncNeOtnCtp(int neId, int commandLevel) throws CommonException;


	/**
	 * 同步网元交叉连接信息
	 * @param neId
	 * @param layerRateList 层速率集合 设置为null则表示查询所有
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNeCRS(Map paramter,int neId, short[] layerRateList, int commandLevel)
			throws CommonException;
	
	/**
	 * 同步从route渠道获取的交叉连接
	 * @param paramter
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncCRSFromRoute(Map paramter, int commandLevel)
			throws CommonException;
	
	
//	/**
//	 * 同步网元sdh 交叉连接信息
//	 * @param neId
//	 * @param layerRateList 层速率集合
//	 * @param commandLevel
//	 * @throws CommonException
//	 */
//	public void syncNeSdhCRS(int neId, short[] layerRateList, int commandLevel)
//			throws CommonException;
//
//	/**
//	 * 同步网元OTN 交叉连接信息
//	 * @param neId
//	 * @param layerRateList
//	 * @param commandLevel
//	 * @throws CommonException
//	 */
//	public void syncNeOtnCRS(int neId, short[] layerRateList, int commandLevel)
//			throws CommonException;
	
	/**
	 * 获取网元同步信息，包括是否需要先同步网元，link变更信息等
	 * @return
	 * @throws CommonException
	 */
	public LinkAlterResultModel getLinkAlterList(Map paramter,int commandLevel) throws CommonException;

	/**
	 * 同步外部link
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncLink(Map paramter,List<LinkAlterModel> syncList,int commandLevel) throws CommonException;

	/**
	 * @param neId
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNeVBs(Map paramter, int neId, int commandLevel) throws CommonException;

	/**
	 * 同步网元以太网信息
	 * @param neId
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNeEthService(Map paramter,int neId, int commandLevel)
			throws CommonException;

	/**
	 * 同步网元bindingPath信息
	 * @param neId
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNeBindingPath(Map paramter,int neId, int commandLevel)
			throws CommonException;

	/**
	 * 同步端口bindingPath信息
	 * @param neId
	 * @param ptpId
	 * @param ptpNameString
	 * @param emsName
	 * @param neSerialNo
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncPtpBindingPath(Map paramter,int neId, Integer ptpId, String ptpNameString,
			String internalEmsName, String neSerialNo, int commandLevel)
			throws CommonException;

	/**
	 * 同步网元时钟信息
	 * @param neId
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNeClock(Map paramter,int neId, int commandLevel) throws CommonException;

	/**
	 * 获取网元当前性能
	 * @param neId
	 * @param layerRateList 层速率集合
	 * @param pmLocationList 位置集合
	 * @param granularityList 周期集合
	 * @param collectNumbic 是否采集计数值
	 * @param collectPhysical 是否采集物理量
	 * @param collectCtp 是否采集通道性能 WDM通道（HW每信道中心波长 ZTE ChannelNO） SDH通道 ctp性能
	 * @param commandLevel
	 * @return
	 * @throws CommonException
	 */
	public List<PmDataModel> getCurrentPmData_Ne(Map paramter,int neId,
			short[] layerRateList, int[] pmLocationList, int[] granularityList,
			boolean collectNumbic, boolean collectPhysical, boolean collectCtp,
			int commandLevel) throws CommonException;

	/**
	 * 获取ptp列表的当前性能数据
	 * @param ptpIdList ptpid集合
	 * @param layerRateList 层速率集合
	 * @param pmLocationList 位置集合
	 * @param granularityList 周期集合
	 * @param collectNumbic 是否采集计数值
	 * @param collectPhysical 是否采集物理量
	 * @param collectCtp 是否采集通道性能 WDM通道（HW每信道中心波长 ZTE ChannelNO） SDH通道 ctp性能
	 * @param commandLevel
	 * @return
	 * @throws CommonException
	 */
	public List<PmDataModel> getCurrentPmData_PtpList(Map paramter,
			List<Integer> ptpIdList, short[] layerRateList,
			int[] pmLocationList, int[] granularityList, boolean collectNumbic,
			boolean collectPhysical, boolean collectCtp, int commandLevel)
			throws CommonException;
	
	
	
	/**
	 * 采集网元历史性能，采集时间段为：time前一天--》time 
	 * @param neId
	 * @param time 采集时间 格式yyyyMMddhhmmss
	 * @param layerRateList
	 * @param pmLocationList
	 * @param pmGranularityList
	 * @param collectNumbic
	 * @param collectPhysical
	 * @param collectCtp
	 * @param commandLevel
	 * @return
	 * @throws CommonException
	 */
	public List<PmDataModel> getHistoryPmData_Ne(Map paramter,int neId,String time,
			short[] layerRateList, int[] pmLocationList,
			int[] pmGranularityList, boolean collectNumbic,
			boolean collectPhysical, boolean collectCtp, int commandLevel)
			throws CommonException;
	
	
	
	/**
	 * 获取ptn报表数据
	 * @param paramter
	 * @param ptnSysName
	 * @param neList
	 * @param time
	 * @param commandLevel
	 */
	public void getPtnReportData(Map paramter, String ptnSysId, String ptnSysName, List<Integer> neList,
			String time, int commandLevel) throws CommonException;
	
	/**
	 * 获取网管当前告警
	 * @param objectType 对象源集合 对象类型 1.EMSGROUP 2.EMS 3.SUBNET 4.NE 5.Shelf 6.Equipment 7.ptp 8.SDH-CTP 9.OTN-CTP
	 * @param perceivedSeverity 告警等级集合
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncAllEMSAndMEActiveAlarms(Map paramter,int[] objectType,
			int[] perceivedSeverity, int commandLevel) throws CommonException;

	/**
	 * 同步网元当前告警
	 * @param neId
	 * @param perceivedSeverity 告警等级集合
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncAllActiveAlarms(Map paramter,int neId,
			int[] perceivedSeverity, int commandLevel) throws CommonException;
	
//	/**
//	 * 同步fd数据
//	 * @param paramter
//	 * @param commandLevel
//	 * @throws CommonException
//	 */
//	public void syncAllFlowDomains(Map paramter, int commandLevel) throws CommonException;
	
	/**
	 * 同步fdfr数据
	 * @param paramter
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncAllFdfrs(Map paramter, int commandLevel) throws CommonException;
	
	/**
	 * 同步linkOfFdfrs数据
	 * @param paramter
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncLinkOfFdfrs(Map paramter, int commandLevel) throws CommonException;
	
	/**
	 * NT_STATE_CHANGE消息解析,更新状态
	 * @param stateData 归一化的状态信息
	 * @throws CommonException
	 */
	public void updateState(StateDataModel stateData) throws CommonException;
	
	/**
	 * 插入tca数据
	 * @param model
	 * @throws CommonException
	 */
	public void insertTCAData(TCADataModel model)  throws CommonException;
	
	/**
	 * 插入保护倒换数据
	 * @param model
	 * @throws CommonException
	 */
	public void insertProtectionSwitchData(ProtectionSwtichDataModel model)  throws CommonException;


}
