package com.fujitsu.IService;

import java.util.List;

import com.fujitsu.common.CommonException;
import com.fujitsu.manager.dataCollectManager.corbaDataModel.PmDataModel;
import com.fujitsu.model.LinkAlterModel;
import com.fujitsu.model.LinkAlterResultModel;
import com.fujitsu.model.NeAlterModel;

/**
 * @author xuxiaojun
 * 
 */
public interface IDataCollectServiceProxy {

	/**
	 * 初始化网管参数
	 * @param emsConnectionId
	 * @throws CommonException
	 */
	public void initParameter(int emsConnectionId) throws CommonException;

	/**
	 * 启动corba连接
	 * 
	 * @return
	 */
	public int startCorbaConnect() throws CommonException;

	/**
	 * 断开corba连接
	 * @return
	 */
	public boolean disCorbaConnect() throws CommonException;
	
	/**
	 * 启动telnet连接
	 * @return
	 */
	public int startTelnetConnect()
			throws CommonException;

	/**
	 * 断开telnet连接
	 * @return
	 */
	public boolean disTelnetConnect()
			throws CommonException;
	
	/**
	 * 登录telnet网元
	 * @param neId
	 * @return
	 */
	public int logonTelnetNe(int neId);
	
	/**
	 * 退出登录telnet网元
	 * @param neId
	 * @return
	 */
	public int logoutTelnetNe(int neId);
	
	/**
	 * 同步网管信息
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncEmsInfo(int commandLevel) throws CommonException;
	

	/**
	 * 获取网元变更列表
	 * @param commandLevel
	 * @return
	 * @throws CommonException
	 */
	public List<NeAlterModel> getNeAlertList(int commandLevel)
			throws CommonException;
	
	/**
	 * 同步网元列表，仅仅同步网元信息
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNeList(int commandLevel) throws CommonException;

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
	public void syncSingleNeData(int neId, boolean isSyncPtp,
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
	public void syncNeEquipmentOrHolder(int neId, int commandLevel)
			throws CommonException;

	/**
	 * 同步网元ptp信息
	 * @param neId
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNePtp(int neId, int commandLevel) throws CommonException;

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
	public void syncNeInternalLink(int neId, int commandLevel)
			throws CommonException;

	/**
	 * 同步网元板卡保护信息
	 * @param neId
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNeEProtectionGroup(int neId, int commandLevel)
			throws CommonException;

	/**
	 * 同步网元环保护
	 * @param neId
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNeProtectionGroup(int neId, int commandLevel)
			throws CommonException;

	/**
	 * 同步wdm网元环保护
	 * @param neId
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNeWDMProtectionGroup(int neId, int commandLevel)
			throws CommonException;
	
	/**
	 * 同步vb数据
	 * @param neId
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNeVBs(int neId, int commandLevel) throws CommonException;
	
	
	/**
	 * 同步网元ctp信息
	 * @param neId
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNeCtp(int neId, int commandLevel) throws CommonException;
	

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
	public void syncNeCRS(int neId, short[] layerRateList, int commandLevel)
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
	public LinkAlterResultModel getLinkAlterList(int commandLevel) throws CommonException;

	/**
	 * 同步外部link
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncLink(List<LinkAlterModel> syncList,int commandLevel) throws CommonException;

//	/**
//	 * @param neId
//	 * @param commandLevel
//	 * @throws CommonException
//	 */
//	public void syncNeVBs(int neId, int commandLevel) throws CommonException;

	/**
	 * 同步网元以太网信息
	 * @param neId
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNeEthService(int neId, int commandLevel)
			throws CommonException;

	/**
	 * 同步网元bindingPath信息
	 * @param neId
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNeBindingPath(int neId, int commandLevel)
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
	public void syncPtpBindingPath(int neId, int ptpId, String ptpNameString,
			String internalEmsName, String neSerialNo, int commandLevel)
			throws CommonException;

	/**
	 * 同步网元时钟信息
	 * @param neId
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncNeClock(int neId, int commandLevel) throws CommonException;

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
	public List<PmDataModel> getCurrentPmData_Ne(int neId,
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
	public List<PmDataModel> getCurrentPmData_PtpList(
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
	public List<PmDataModel> getHistoryPmData_Ne(int neId,String time,
			short[] layerRateList, int[] pmLocationList,
			int[] pmGranularityList, boolean collectNumbic,
			boolean collectPhysical, boolean collectCtp, int commandLevel)
			throws CommonException;

	/**
	 * 获取网管当前告警
	 * @param objectType 对象源集合 对象类型 1.EMSGROUP 2.EMS 3.SUBNET 4.NE 5.Shelf 6.Equipment 7.ptp 8.SDH-CTP 9.OTN-CTP
	 * @param perceivedSeverity 告警等级集合
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncAllEMSAndMEActiveAlarms(int[] objectType,
			int[] perceivedSeverity, int commandLevel) throws CommonException;

	/**
	 * 同步网元当前告警
	 * @param neId
	 * @param perceivedSeverity 告警等级集合
	 * @param commandLevel
	 * @throws CommonException
	 */
	public void syncAllActiveAlarms(int neId,
			int[] perceivedSeverity, int commandLevel) throws CommonException;
	
}
