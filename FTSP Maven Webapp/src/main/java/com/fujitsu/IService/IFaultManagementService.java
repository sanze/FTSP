package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;

public interface IFaultManagementService {

	public Map<String, Object> getFaultList(Map<String, String> paramMap, int start, int limit) throws CommonException;
	
	public CommonResult deleteFaultRecord(Map<String, String> paramMap) throws CommonException;
	
	public Map<String, Object> getFaultInfoByFaultIdAndType(Map<String, String> paramMap) throws CommonException;
	
	public Map<String, Object> getFaultAlarmList(Map<String, String> paramMap, int start, int limit) throws CommonException;
	
	public Map<String, Object> getEquipFaultLocationInfo(Map<String, String> paramMap) throws CommonException;
	
	public CommonResult deleteFaultAlarm(Map<String, String> paramMap) throws CommonException;
	
//	public CommonResult addFaultAlarm(List<Map<String, Object>> list) throws CommonException;
	
	public Map<String, Object> getTransformSystemList() throws CommonException;
	
	public Map<String, Object> refreshFaultAlarm(Map<String, String> paramMap) throws CommonException;
	
	public Map<String, Object> getCableList() throws CommonException;
	
	public Map<String, Object> getCableSectionList(Map<String, String> paramMap) throws CommonException;
	
	public Map<String, Object> getFaultReasonList(Map<String, String> paramMap) throws CommonException;
	
	public CommonResult saveFaultInfo(Map<String, String> paramMap, List<String> paramMapList) throws CommonException;
	
	public void updateFaultInfo_Main() throws CommonException;
	
	public CommonResult faultConfirm(Map<String, String> paramMap, int userId) throws CommonException;
	
	public CommonResult faultRecovery(Map<String, String> paramMap) throws CommonException;
	
	public CommonResult faultArchive(Map<String, String> paramMap) throws CommonException;
	
	public CommonResult getFaultInfoForFP() throws CommonException;
}
