package com.fujitsu.service;

import java.util.List;

import globaldefs.NameAndStringValue_T;

import com.fujitsu.common.CommonException;

/**
 * @author zhuangjieliang
 * 
 */
public class EMSSession extends org.omg.CORBA.portable.ObjectImpl{

	public String[] acknowledgeAlarms(List<String> alarmList)
			throws CommonException {
		return new String[0];
	}
	public Object[] getAllInternalTopologicalLinks(
			NameAndStringValue_T[] meName) throws CommonException{
		return new Object[0];
	}

	public Object[] getHistoryPMData(NameAndStringValue_T[] name,
			String startTime, String endTime,short[] layerRateList,
			String[] pmLocationList,String[] pmGranularityList)
			throws CommonException{
		return new Object[0];
	}

	/** #查询EMS性能的保持时间# */
	public Object getHoldingTime() throws CommonException{
		return new Object();
	}

	/** #查询网元性能的能力# */
	public Object[] getMEPMcapabilities(NameAndStringValue_T[] neName) throws CommonException{
		return new Object[0];
	}

	/**	########ProtectionMgr##### */
	/** #查询ME下所有保护组信息# --中兴特有*/
	public Object getMEconfigData(
			NameAndStringValue_T[] neName) throws CommonException{
		return new Object[0];
	}
	
	
	/** #查询ME下所有保护组信息# */
	public Object[] getAllProtectionGroups(
			NameAndStringValue_T[] neName) throws CommonException{
		return new Object[0];
	}

	/** #查询所有设备保护组信息# */
	public Object[] getAllEProtectionGroups(
			NameAndStringValue_T[] neName) throws CommonException{
		return new Object[0];
	}

	public Object[] getAllWDMProtectionGroups(
			NameAndStringValue_T[] neName) throws CommonException{
		return new Object[0];
	}
	
	/** #查询指定保护组信息# */
	public Object getProtectionGroup(NameAndStringValue_T[] pgName) throws CommonException{
		return new Object();
	}

	/** #查询指定设备保护组信息# */
	public Object getEProtectionGroup(NameAndStringValue_T[] epgName) throws CommonException{
		return new Object();
	}

	public Object getWDMProtectionGroup(NameAndStringValue_T[] wpgpName) throws CommonException{
		return new Object();
	}
	
	/** #获取指定对象的保护数据#参数reliableSinkCtpOrGroupName */
	public Object[] retrieveSwitchData(NameAndStringValue_T[] pgName) throws CommonException{
		return new Object[0];
	}

	/** #获取指定对象的设备保护数据#参数ePGPName */
	public Object[] retrieveESwitchData(NameAndStringValue_T[] epgName) throws CommonException{
		return new Object[0];
	}
	
	public Object[] retrieveWDMSwitchData(NameAndStringValue_T[] wpgpName) throws CommonException{
		return new Object[0];
	}

	public Object[] getAllMstpEndPointNames(NameAndStringValue_T[] meName) throws CommonException{
		return new Object[0];
	}
	public Object[] getAllMstpEndPoints(NameAndStringValue_T[] meName) throws CommonException{
		return new Object[0];
	}
	
	public Object[] getAllEthService(NameAndStringValue_T[] meName) throws CommonException{
		return new Object[0];
	}
	
	public Object[] getAllVBNames(NameAndStringValue_T[] meName) throws CommonException{
		return new Object[0];
	}
	public Object[] getAllVBs(NameAndStringValue_T[] meName) throws CommonException{
		return new Object[0];
	}
	public Object getBindingPath(NameAndStringValue_T[] ptpName) throws CommonException{
		return new Object[0];
	}
	public Object[] getAllVLANs(NameAndStringValue_T[] meName) throws CommonException{
		return new Object[0];
	}
	
	/** #查询时钟源# */
	public Object[] getObjectClockSourceStatus(NameAndStringValue_T[] meName) throws CommonException{
		return new Object[0];
	}

	public Object[] getRoute(NameAndStringValue_T[] sncName) throws CommonException{
		return new Object[0];
	}
	
	/** #查询时钟源# */
	public Object[] getAllEthernetSubnetworkConnections() throws CommonException{
		return new Object[0];
	}
	
	@Override
	public String[] _ids() {
		return new String[]{
				"IDL:mtnm.tmforum.org/service/EMSSession:1.0"
		};
	}
}
