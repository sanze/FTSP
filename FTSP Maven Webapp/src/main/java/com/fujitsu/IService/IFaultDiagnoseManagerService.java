package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;

public interface IFaultDiagnoseManagerService {
	
	public Map<String, Object> getFaultDiagnoseRules(int start, int limit) throws CommonException;
	
	public Map<String, Object> getFaultDiagnoseDetailById(Map<String, String> paramMap) throws CommonException;

	public Map<String, Object> getApplyScope(Map<String, String> paramMap) throws CommonException;
	
	public CommonResult changeFaultDiagnoseRuleStatus(Map<String, String> paramMap, List<Integer> diagnoseIds) throws CommonException;
	
	public CommonResult manualActionRules(List<Integer> diagnoseIds) throws CommonException;
	
	public CommonResult isServerStarted() throws CommonException;
	
	public Map<String, Object> getApplyEquips(List<Integer> ids) throws CommonException;
	
	public void modifyFaultDiagnoseRule(Map<String, Object> param, List<Integer> ids) throws CommonException;
	
	public CommonResult setFaultDiagnoseParam(Map<String, String> paramMap) throws CommonException;
	
	public Map<String, Object> getFaultDiagnoseParam() throws CommonException;
}
