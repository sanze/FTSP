package com.fujitsu.manager.faultManager.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.IService.IAlarmConvergeServiceProxy;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.AlarmConvergeMapper;
import com.fujitsu.manager.faultManager.service.AlarmConvergeService;
import com.fujitsu.model.AlarmConvergeModel;
import com.fujitsu.util.SpringContextUtil;

@Service
@Transactional(rollbackFor = Exception.class)
public class AlarmConvergeServiceImpl extends AlarmConvergeService{ 
	@Resource
	AlarmConvergeMapper alarmConvergeMapper; 
	
	private IAlarmConvergeServiceProxy alarmConvergeService;
	
	@Override
	public List<Map>  getApplyEquips(List<Integer> ids)throws CommonException { 
		return alarmConvergeMapper.getApplyEquips(ids);  
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Map<String,Object>  searchAlarmConverge(int start,int limit)
			throws CommonException {   
        List<Map> dataRtn = new ArrayList();    
		int total = 0;
		dataRtn = alarmConvergeMapper.searchAlarmConverge(start,limit);
		total =  alarmConvergeMapper.countAlarmConverge(); 
		Map<String,Object> result =new HashMap<String,Object>();
		if(total==0){
			result.put("rows", new ArrayList());
		}else{
			result.put("rows", dataRtn);
		}
	
 		result.put("total", total);
		return result;  
	} 
	
	@Override
	public Map getConvergeTime()throws CommonException { 
		String paramKey="ALARM_CONVERGE_TIMER";
		return alarmConvergeMapper.getConvergeTime(paramKey);  
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void setConvergeTime(Map map)throws CommonException {   
			alarmConvergeMapper.setConvergeTime(map);   
	} 
	
	@SuppressWarnings("rawtypes")
	@Override 
	public int addAlarmConvergeRules(Map<String, Object> paramMap,List<Integer>ids) 
			throws CommonException {
		/** t_alarm_converge（告警收敛） **/      
		AlarmConvergeModel alarmConvergeModel = new AlarmConvergeModel(); 
		alarmConvergeModel.setRULE_NAME(paramMap.get("RULE_NAME").toString()); 
		alarmConvergeModel.setSTATUS(CommonDefine.ALARM_COMVERGE_STATUS_ENABLE); 
		alarmConvergeModel.setDESCRIPTION(paramMap.get("DESCRIPTION").toString());
		HttpServletRequest request = ServletActionContext.getRequest();
		alarmConvergeModel.setMODIFIER(request.getSession().getAttribute("USER_NAME").toString());
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		alarmConvergeModel.setUPDATE_TIME(sf.format(new Date()));
 		alarmConvergeMapper.addAlarmConverge(alarmConvergeModel);
		
		/** t_alarm_converge_scope（告警收敛范围）  **/
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//收敛id
		modelMap.put("convergeId", alarmConvergeModel.getConvergeId());
		modelMap.put("ids", ids);
		alarmConvergeMapper.addConvergeScope(modelMap);
		
		/** t_alarm_converge_equipment（告警收敛设备）  **/
		List<String> proNames = new ArrayList<String>();
		String eqStr = paramMap.get("applyEquip").toString();
		if(!"".equals(eqStr)){
			String [] eqArr=eqStr.split(";");
			for (int i = 0; i < eqArr.length; i++) {
				proNames.add(eqArr[i].split("\\.")[1]);
			} 
			modelMap.put("proNames",proNames);  
			alarmConvergeMapper.addConvergeEquipment(modelMap); 
		}
		
		/** t_alarm_converge_condition（告警收敛条件）  **/
		List<Map> param = new ArrayList<Map>();
		String conCheck = paramMap.get("condCheck").toString();
		String [] conArr=conCheck.split(";");
		for (int i = 0; i < conArr.length; i++) {
			Map<String,Object> data = new HashMap<String,Object>(); 
			data.put("convergeId",alarmConvergeModel.getConvergeId());  
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
		alarmConvergeMapper.addConvergeCondition(param);
		
		/**t_alarm_converge_action（告警收敛执行动作）**/ 
		param = new ArrayList<Map>();
		String actCheck = paramMap.get("actCheck").toString(); 
		String [] actArr=actCheck.split(";");
		for (int i = 0; i < actArr.length; i++) {
			Map<String,Object> data = new HashMap<String,Object>(); 
			data.put("convergeId",alarmConvergeModel.getConvergeId());  
			String [] arr = actArr[i].split("\\.");
			data.put("STATUS",Integer.valueOf(arr[0]));  
			data.put("ACTION_TYPE",Integer.valueOf(arr[1]));  
			data.put("ALARMS",arr[2]); 
			param.add(data);
		} 
		alarmConvergeMapper.addConvergeAction(param);
		
		return alarmConvergeModel.getConvergeId();
	} 
	
	@Override
	public void deleteConvergeRules(String convergeIds) throws CommonException {
		/** 调用告警收敛接口，删除一条或多条告警收敛规则 **/
		String[] strIds = convergeIds.split(",");
		int[] ruleIds = new int[strIds.length];
		for (int i=0; i<ruleIds.length; i++) {
			ruleIds[i] = Integer.parseInt(strIds[i]);
		}
		alarmConvergeService = SpringContextUtil.getAlarmConvergeServiceProxy();
		alarmConvergeService.deleteAlarmConverge(ruleIds);	
		
		alarmConvergeMapper.deleteConvergeRules(convergeIds);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public  Map<String,Object>  getAlarmConvergeDetailById(int convergeId)throws CommonException{ 
		Map<String, Object> dataRtn = new HashMap<String, Object>(); 
		//规则名称、描述、适用范围、适用设备 
		Map<String, Object> textMap = alarmConvergeMapper.getAlarmConvergeTextField(convergeId); 
		// 适用范围
		List<Map> emsList = alarmConvergeMapper.getAlarmConvergeEms(convergeId);
		String applyStr="",applyEmsIdsStr="";
		List <Integer> applyEmsIds = new ArrayList();
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
		dataRtn.put("applyEmsIds",applyEmsIds);
		//适用设备
		List<Map> equipList = alarmConvergeMapper.getAlarmConvergeEquips(convergeId);
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
		// 条件Grid
		List<Map> condList = alarmConvergeMapper.getAlarmConvergeCond(convergeId);
		// 执行动作Grid
		List<Map> actList = alarmConvergeMapper.getAlarmConvergeAction(convergeId);
 
		dataRtn.put("textMap", textMap); 
		dataRtn.put("condList", condList);
		dataRtn.put("actList", actList); 
		return dataRtn; 
	}   
	

	@Override
	public void modifyAlarmConvergeRules(Map<String, Object> paramMap,List<Integer>ids) 
			throws CommonException {
		/** t_alarm_converge（告警收敛） **/      
		AlarmConvergeModel alarmConvergeModel = new AlarmConvergeModel(); 
		alarmConvergeModel.setRULE_NAME(paramMap.get("RULE_NAME").toString());  
		alarmConvergeModel.setDESCRIPTION(paramMap.get("DESCRIPTION").toString());
		HttpServletRequest request = ServletActionContext.getRequest();
		alarmConvergeModel.setMODIFIER(request.getSession().getAttribute("USER_NAME").toString());
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		alarmConvergeModel.setUPDATE_TIME(sf.format(new Date()));
		alarmConvergeModel.setConvergeId(Integer.valueOf(paramMap.get("CONVERGE_ID").toString()));
 		alarmConvergeMapper.updateAlarmConverge(alarmConvergeModel); 
 		
		// 删除关联关系
 		Map<String,Object> delete = new HashMap<String,Object> ();
 		delete.put("ID_VALUE", alarmConvergeModel.getConvergeId()); 
		delete.put("ID_NAME", "CONVERGE_ID"); 
		
		delete.put("NAME", "t_alarm_converge_scope"); 
		alarmConvergeMapper.deleteTableById(delete);
		delete.put("NAME", "t_alarm_converge_equipment"); 
		alarmConvergeMapper.deleteTableById(delete);
		delete.put("NAME", "t_alarm_converge_condition"); 
		alarmConvergeMapper.deleteTableById(delete);
		delete.put("NAME", "t_alarm_converge_action"); 
		alarmConvergeMapper.deleteTableById(delete); 
		
		// 重新插入关联关系
		/** t_alarm_converge_scope（告警收敛范围）  **/
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//收敛id
		modelMap.put("convergeId", alarmConvergeModel.getConvergeId());
		modelMap.put("ids", ids);
		alarmConvergeMapper.addConvergeScope(modelMap);
		
		/** t_alarm_converge_equipment（告警收敛设备）  **/
		List<String> proNames = new ArrayList<String>();
		String eqStr = paramMap.get("applyEquip").toString();
		if(!"".equals(eqStr)){
			String [] eqArr=eqStr.split(";");
			for (int i = 0; i < eqArr.length; i++) {
				proNames.add(eqArr[i].split("\\.")[1]);
			} 
			modelMap.put("proNames",proNames);  
			alarmConvergeMapper.addConvergeEquipment(modelMap); 
		}
		
		/** t_alarm_converge_condition（告警收敛条件）  **/
		List<Map> param = new ArrayList<Map>();
		String conCheck = paramMap.get("condCheck").toString();
		String [] conArr=conCheck.split(";");
		for (int i = 0; i < conArr.length; i++) {
			Map<String,Object> data = new HashMap<String,Object>(); 
			data.put("convergeId",alarmConvergeModel.getConvergeId());  
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
		alarmConvergeMapper.addConvergeCondition(param);
		
		/**t_alarm_converge_action（告警收敛执行动作）**/ 
		param = new ArrayList<Map>();
		String actCheck = paramMap.get("actCheck").toString(); 
		String [] actArr=actCheck.split(";");
		for (int i = 0; i < actArr.length; i++) {
			Map<String,Object> data = new HashMap<String,Object>(); 
			data.put("convergeId",alarmConvergeModel.getConvergeId());  
			String [] arr = actArr[i].split("\\.");
			data.put("STATUS",Integer.valueOf(arr[0]));  
			data.put("ACTION_TYPE",Integer.valueOf(arr[1]));  
			data.put("ALARMS",arr[2]); 
			param.add(data);
		} 
		alarmConvergeMapper.addConvergeAction(param); 
	} 
	
	
	@Override
	public void changeConvergeRuleStatus(int STATUS,List<Integer> convergeIds) 
			throws CommonException {		
 		/** 调用告警收敛服务接口，启用或挂起一条或多条告警收敛规则 **/
		alarmConvergeService = SpringContextUtil.getAlarmConvergeServiceProxy();
		int[] ruleIds = new int[convergeIds.size()];
		for (int i=0; i<ruleIds.length; i++) {
			ruleIds[i] = convergeIds.get(i);
		}
		if (STATUS == CommonDefine.ALARM_COMVERGE_STATUS_ENABLE) {
			// 启用
			alarmConvergeService.startAlarmConvergeById(ruleIds);	
		} else if (STATUS == CommonDefine.ALARM_COMVERGE_STATUS_PENDING) {
			// 挂起
			alarmConvergeService.stopAlarmConvergeById(ruleIds);
		}
	} 
}
