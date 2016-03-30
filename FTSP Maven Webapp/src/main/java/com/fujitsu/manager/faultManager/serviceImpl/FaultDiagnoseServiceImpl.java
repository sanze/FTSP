package com.fujitsu.manager.faultManager.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.fujitsu.IService.IFaultDiagnoseServiceProxy;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.dao.mysql.FaultDiagnoseMapper;
import com.fujitsu.manager.faultManager.service.FaultDiagnoseService;
import com.fujitsu.util.SpringContextUtil;

public class FaultDiagnoseServiceImpl extends FaultDiagnoseService {

	@Resource
	public FaultDiagnoseMapper faultDiagnoseMapper;
	
	public IFaultDiagnoseServiceProxy faultDiagnoseServiceProxy;
	
	public Map<String, Object> getFaultDiagnoseRules(int start, int limit)  throws CommonException {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		try {
		
			int total = faultDiagnoseMapper.getFaultDiagnoseRulesCount();
			List<Map<String, Object>> resultList = faultDiagnoseMapper.getFaultDiagnoseRules(start, limit);
			
			data.put("total", total);
			data.put("rows", resultList);
		
		} catch (Exception e){
			throw new CommonException(e,1);
		}
		
		return data;
	}
	
	public Map<String, Object> getFaultDiagnoseDetailById(Map<String, String> paramMap) throws CommonException {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		try {
			int diagnoseId = Integer.parseInt(paramMap.get("diagnoseId"));
			
			Map<String, Object> textMap = new HashMap<String, Object>();
			// 适用范围
			List<Map<String, Object>> emsList = faultDiagnoseMapper.getFaultDiagnoseEms(diagnoseId);
			String applyStr="" ,applyEmsIdsStr="";
			List <Integer> applyEmsIds = new ArrayList<Integer>();
			if(!emsList.isEmpty()){
				for(int i=0;i<emsList.size();i++){
					applyStr+=emsList.get(i).get("EMS_NAME").toString()+",";  
					applyEmsIdsStr+=emsList.get(i).get("nodeId")+"."+
							emsList.get(i).get("GROUP_NAME")+"."+emsList.get(i).get("EMS_NAME")+",";
					applyEmsIds.add(Integer.valueOf(emsList.get(i).get("nodeId").toString()));
				}
				applyStr = applyStr.substring(0, applyStr.lastIndexOf(",")); 
				applyEmsIdsStr = applyEmsIdsStr.substring(0, applyEmsIdsStr.lastIndexOf(","));  
			} 
			textMap.put("applyStr","网管:"+applyStr);
			textMap.put("applyEmsIdsStr",applyEmsIdsStr);
			//适用设备
			List<Map<String, Object>> equipList = faultDiagnoseMapper.getFaultDiagnoseEquips(diagnoseId);
			String equipStr="",eqStr=""; 
			if(!equipList.isEmpty()){
				for(int i=0;i<equipList.size();i++){ 
					equipStr+=equipList.get(i).get("factoryStr").toString()
							+equipList.get(i).get("PRODUCT_NAME")+",";
					eqStr+=equipList.get(i).get("FACTORY")+"."+
							equipList.get(i).get("PRODUCT_NAME")+";";
					 
				}
				equipStr = equipStr.substring(0, equipStr.lastIndexOf(",")); 
				eqStr = eqStr.substring(0, eqStr.lastIndexOf(";"));  
			} 
			textMap.put("equipStr",equipStr);
			textMap.put("eqStr",eqStr);
			
			Map<String, Object> faultDiagnoseMap = faultDiagnoseMapper.getFaultDiagnoseDetailById(diagnoseId);
			List<Map<String, Object>> condMapList = faultDiagnoseMapper.getCondByDiagnoseId(diagnoseId);
			List<Map<String, Object>> actionMapList = faultDiagnoseMapper.getActionByDiagnoseId(diagnoseId);
			
			data.put("textMap", textMap);
			data.put("applyEmsIds",applyEmsIds);
			data.put("faultDiagnoseMap", faultDiagnoseMap);
			data.put("condMapList", condMapList);
			data.put("actionMapList", actionMapList);
		
		} catch (Exception e){
			throw new CommonException(e,1);
		}
		
		return data;
	}
	
	public Map<String, Object> getApplyScope(Map<String, String> paramMap) throws CommonException {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		try {
			int diagnoseId = Integer.parseInt(paramMap.get("diagnoseId"));
			
			List<Map<String, Object>> applyScopeList = faultDiagnoseMapper.getApplyScope(diagnoseId);
			data.put("rows", applyScopeList);
			
		} catch (Exception e){
			throw new CommonException(e,1);
		}
		
		return data;
	}
	
	public CommonResult changeFaultDiagnoseRuleStatus(Map<String, String> paramMap, List<Integer> diagnoseIds) throws CommonException {
		
		CommonResult result = new CommonResult();
		
		try {
			int type = Integer.parseInt(paramMap.get("type"));
			/** 调用故障诊断服务接口，启用或挂起一条或多条故障诊断规则 **/
			faultDiagnoseServiceProxy = SpringContextUtil.getFaultDiagnoseServiceProxy();
			int[] ruleIds = new int[diagnoseIds.size()];
			for (int i=0; i<ruleIds.length; i++) {
				ruleIds[i] = diagnoseIds.get(i);
			}
			if (type == CommonDefine.FAULT_DIAGNOSE_MANAGEMENT.FAULT_DIAGNOSE_STATUS_ENABLE) {
				// 启用
				faultDiagnoseServiceProxy.startFaultDiagnoseById(ruleIds);	
			} else if (type == CommonDefine.FAULT_DIAGNOSE_MANAGEMENT.FAULT_DIAGNOSE_STATUS_PENDING) {
				// 挂起
				faultDiagnoseServiceProxy.stopFaultDiagnoseById(ruleIds);
			}
			
			result.setReturnResult(CommonDefine.SUCCESS);
			
		} catch (Exception e){
			throw new CommonException(e,1);
		}
		
		return result;
	}
	
	public CommonResult manualActionRules(List<Integer> diagnoseIds) throws CommonException {
		
		CommonResult result = new CommonResult();
		
		try {
			
			/** 调用故障诊断服务接口，启用或挂起一条或多条故障诊断规则 **/
			faultDiagnoseServiceProxy = SpringContextUtil.getFaultDiagnoseServiceProxy();
			
			boolean rlt = true;
			for (int ruleId : diagnoseIds) {
				rlt &= faultDiagnoseServiceProxy.manualSatarFaultDiagnose(ruleId);
			}
			if (rlt) {
				result.setReturnResult(CommonDefine.SUCCESS);
			} else {
				result.setReturnResult(CommonDefine.FAILED);
			}
			
		} catch (Exception e) {
			throw new CommonException(e,1);
		}
		
		return result;
	}
	
	public CommonResult isServerStarted() throws CommonException {
		
		CommonResult result = new CommonResult();
		
		try {
			
			SpringContextUtil.getFaultDiagnoseServiceProxy();
			result.setReturnResult(CommonDefine.SUCCESS);
			
		} catch (Exception e) {
			throw new CommonException(e,1);
		}
		
		return result;
	}
	
	public Map<String, Object> getApplyEquips(List<Integer> ids) throws CommonException {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		try {
			
			List<Map<String, Object>> applyEquipList = faultDiagnoseMapper.getApplyEquips(ids);
			data.put("rows", applyEquipList);
			
		} catch (Exception e){
			throw new CommonException(e,1);
		}
		
		return data;
	}
	
	public void modifyFaultDiagnoseRule(Map<String, Object> paramMap, List<Integer> ids) throws CommonException {
		
		/** t_alarm_converge（告警收敛） **/
//		AlarmConvergeModel alarmConvergeModel = new AlarmConvergeModel(); 
//		alarmConvergeModel.setRULE_NAME(paramMap.get("RULE_NAME").toString());  
//		alarmConvergeModel.setDESCRIPTION(paramMap.get("DESCRIPTION").toString());
//		HttpServletRequest request = ServletActionContext.getRequest();
//		alarmConvergeModel.setMODIFIER(request.getSession().getAttribute("USER_NAME").toString());
//		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		alarmConvergeModel.setUPDATE_TIME(sf.format(new Date()));
//		alarmConvergeModel.setConvergeId(Integer.valueOf(paramMap.get("CONVERGE_ID").toString()));
// 		alarmConvergeMapper.updateAlarmConverge(alarmConvergeModel);
 		
		int diagnoseId = Integer.valueOf(paramMap.get("diagnoseId").toString());
		
		// 删除关联关系
 		Map<String,Object> delete = new HashMap<String,Object>();
 		delete.put("ID_VALUE", diagnoseId);
		delete.put("ID_NAME", "DIAGNOSE_ID");
		
		delete.put("NAME", "t_fault_diagnose_scope");
		faultDiagnoseMapper.deleteTableById(delete);
		delete.put("NAME", "t_fault_diagnose_equipment");
		faultDiagnoseMapper.deleteTableById(delete);
		delete.put("NAME", "t_fault_diagnose_condition");
		faultDiagnoseMapper.deleteTableById(delete);
//		delete.put("NAME", "t_alarm_converge_action"); 
//		alarmConvergeMapper.deleteTableById(delete);
		
		// 重新插入关联关系
		/** t_fault_diagnose_scope（故障诊断适用范围）  **/
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//收敛id
		modelMap.put("diagnoseId", diagnoseId);
		modelMap.put("ids", ids);
		faultDiagnoseMapper.addFaultDiagnoseScope(modelMap);
		
		/** t_fault_diagnose_equipment（故障诊断适用设备）  **/
		List<String> proNames = new ArrayList<String>();
		String eqStr = paramMap.get("applyEquip").toString();
		if(!"".equals(eqStr)){
			String [] eqArr=eqStr.split(";");
			for (int i = 0; i < eqArr.length; i++) {
				proNames.add(eqArr[i].split("\\.")[1]);
			} 
			modelMap.put("proNames",proNames);  
			faultDiagnoseMapper.addFaultDiagnoseEquipment(modelMap);
		}
		
		/** t_fault_diagnose_condition（故障诊断条件）  **/
		List<Map<String, Object>> param = new ArrayList<Map<String, Object>>();
		String conCheck = paramMap.get("condCheck").toString();
		String [] conArr=conCheck.split(";");
		for (int i = 0; i < conArr.length; i++) {
			Map<String, Object> data = new HashMap<String,Object>(); 
			data.put("diagnoseId", diagnoseId);  
			String [] arr = conArr[i].split("\\.");
			data.put("ALARM_NAME",arr[0]);  
			data.put("OBJECT_TYPE",Integer.valueOf(arr[1]));  
			if(arr.length>2){
				data.put("LEVEL",arr[2]); 
			}else{
				data.put("LEVEL",null); 
			}
			param.add(data);
		}
		faultDiagnoseMapper.addFaultDiagnoseCondition(param);
		
		/**t_alarm_converge_action（告警收敛执行动作）**/ 
//		param = new ArrayList<Map>();
//		String actCheck = paramMap.get("actCheck").toString(); 
//		String [] actArr=actCheck.split(";");
//		for (int i = 0; i < actArr.length; i++) {
//			Map<String,Object> data = new HashMap<String,Object>(); 
//			data.put("convergeId",alarmConvergeModel.getConvergeId());  
//			String [] arr = actArr[i].split("\\.");
//			data.put("STATUS",Integer.valueOf(arr[0]));  
//			data.put("ACTION_TYPE",Integer.valueOf(arr[1]));  
//			data.put("ALARMS",arr[2]); 
//			param.add(data);
//		} 
//		alarmConvergeMapper.addConvergeAction(param);
	}
	
	public CommonResult setFaultDiagnoseParam(Map<String, String> paramMap) throws CommonException {
		
		CommonResult result = new CommonResult();
		
		try {
			
			if(paramMap.get("timer") != null && !"".equals(paramMap.get("timer").toString())){
				faultDiagnoseMapper.setSysParam("FAULT_DIAGNOSE_TIMER", paramMap.get("timer").toString());
			}
			if(paramMap.get("pushFlag") != null && !"".equals(paramMap.get("pushFlag").toString())){
				faultDiagnoseMapper.setSysParam("FAULT_DIAGNOSE_PUSH", paramMap.get("pushFlag").toString());
			}
			
			result.setReturnResult(CommonDefine.SUCCESS);
			
		} catch (Exception e) {
			throw new CommonException(e,1);
		}
		
		return result;
	}
	
	public Map<String, Object> getFaultDiagnoseParam() throws CommonException {
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		try {
			
			List<Map<String, Object>> timerList = faultDiagnoseMapper.getSysParam("FAULT_DIAGNOSE_TIMER");
			List<Map<String, Object>> pushFlagList = faultDiagnoseMapper.getSysParam("FAULT_DIAGNOSE_PUSH");
			if(!timerList.isEmpty()){
				Map<String, Object> timerMap = timerList.get(0);
				if(timerMap.get("PARAM_VALUE") != null && !"".equals(timerMap.get("PARAM_VALUE").toString())){
					data.put("timer", timerMap.get("PARAM_VALUE").toString());
				}
			}
			if(!pushFlagList.isEmpty()){
				Map<String, Object> pushFlagMap = pushFlagList.get(0);
				if(pushFlagMap.get("PARAM_VALUE") != null && !"".equals(pushFlagMap.get("PARAM_VALUE").toString())){
					data.put("pushFlag", pushFlagMap.get("PARAM_VALUE").toString());
				}
			}
			data.put("returnResult", CommonDefine.SUCCESS);
			
		} catch (Exception e){
			throw new CommonException(e,1);
		}
		
		return data;
	}
}
