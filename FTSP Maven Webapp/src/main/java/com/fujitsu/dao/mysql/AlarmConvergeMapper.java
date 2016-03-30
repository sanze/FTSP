package com.fujitsu.dao.mysql; 
import java.util.List;
import java.util.Map; 
import org.apache.ibatis.annotations.Param; 
import com.fujitsu.model.AlarmConvergeModel;

public interface AlarmConvergeMapper { 
	@SuppressWarnings("rawtypes")
	public List<Map> getApplyEquips(@Param (value="ids") List<Integer> ids); 
	
	@SuppressWarnings("rawtypes")
	public List<Map> searchAlarmConverge(@Param (value="start") int start,
										  @Param (value="limit")int limit);  
	public int countAlarmConverge(); 
	
	@SuppressWarnings("rawtypes")
	public Map getConvergeTime(@Param (value="paramKey") String paramKey);  
	
	@SuppressWarnings("rawtypes")
	public void setConvergeTime(@Param (value="map") Map map);   
	 
	public void addAlarmConverge(@Param (value="alarmConvergeModel") AlarmConvergeModel alarmConvergeModel);  
 
	@SuppressWarnings("rawtypes")
	public void addConvergeScope(@Param (value="map") Map map);  
	
	@SuppressWarnings("rawtypes")
	public void addConvergeEquipment(@Param (value="map") Map map);  
	
	@SuppressWarnings("rawtypes")
	public void addConvergeCondition(@Param (value="param") List<Map> param);  
	
	@SuppressWarnings("rawtypes")
	public void addConvergeAction(@Param (value="param") List<Map> param);  
	
	public void deleteConvergeRules(@Param (value="convergeIds") String convergeIds);
	 
	public Map<String,Object> getAlarmConvergeTextField(@Param (value="convergeId") int convergeId); 
	
	@SuppressWarnings("rawtypes")
	public List<Map> getAlarmConvergeEms(@Param (value="convergeId") int convergeId); 
	
	@SuppressWarnings("rawtypes")
	public List<Map> getAlarmConvergeCond(@Param (value="convergeId") int convergeId); 
	
	@SuppressWarnings("rawtypes")
	public List<Map> getAlarmConvergeAction(@Param (value="convergeId") int convergeId); 
	
	@SuppressWarnings("rawtypes")
	public List<Map> getAlarmConvergeEquips(@Param (value="convergeId") int convergeId); 
	
	@SuppressWarnings("rawtypes")
	public void deleteTableById(@Param (value="map") Map map); 
	
	public void updateAlarmConverge(@Param (value="alarmConvergeModel") AlarmConvergeModel alarmConvergeModel);  
	
	public void updateConvergeRuleStatus(@Param (value="STATUS") int STATUS,
					@Param (value="convergeIds") List<Integer>  convergeIds);  
 }
