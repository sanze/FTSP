package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;

/**
 * @Description：
 * @author cao senrong
 * @date 2015-1-15
 * @version V1.0
 */
public interface IPlanManagerService {

	/**
	 * @function:计划查询
	 * @data:2015-1-15
	 * @author cao senrong
	 * @return
	 * List<Map> 
	 * @throws CommonException 
	 *
	 */
	public List<Map> getPlanList(Map map) throws CommonException;
	
	/**
	 * @function:关联路由
	 * @data:2015-1-15
	 * @author cao senrong
	 * @param map
	 * @return
	 * List<Map> 
	 *
	 */
	public List<Map> getRouteListByPlanId(Map map) throws CommonException ;
	
	/**
	 * @function:
	 * @data:2015-1-19
	 * @author cao senrong
	 * @param map
	 * @throws CommonException
	 * void 
	 *
	 */
	public void updateTestPlanStatusStartUp(Map map) throws CommonException;
	
	/**
	 * @function:
	 * @data:2015-1-19
	 * @author cao senrong
	 * @param map
	 * @throws CommonException
	 * void 
	 *
	 */
	public void updateTestPlanStatusPending(Map map) throws CommonException;
	
	/**
	 * @function:修改测试参数
	 * @data:2015-1-16
	 * @author cao senrong
	 * @param map
	 * @return
	 * @throws CommonException
	 * Map 
	 *
	 */
	public void modifyTestRoutePara(Map map) throws CommonException;
	
	/**
	 * @function:批量更新测试路由的参数（周期和衰耗基准值偏差）
	 * @data:2015-1-16
	 * @author cao senrong
	 * @param map
	 * @return
	 * @throws CommonException
	 * Map 
	 *
	 */
	public void modifyTestRouteValueBatch(List<Map> mapList) throws CommonException;
	
	
	
	
	
	/**
	 * @function:是否打开触发测试
	 * @data:2015-1-19
	 * @author cao senrong
	 * @param map
	 * @return
	 * @throws CommonException
	 * Map 
	 *
	 */
	public Map getAlarmTriggerStatus() throws CommonException;
	
	/**
	 * @function:修改触发测试打开状态
	 * @data:2015-1-22
	 * @author cao senrong
	 * @param map
	 * @throws CommonException
	 * void 
	 *
	 */
	public void modifyAlarmTriggerStstus(Map map) throws CommonException ;
	/**
	 * @function:
	 * @data:2015-1-16
	 * @author cao senrong
	 * @return
	 * @throws CommonException
	 * List<Map> 
	 *
	 */
	public List<Map> getTriggerAlarm() throws CommonException;
	
	/**
	 * @function:添加或更新触发测试告警
	 * @data:2015-1-16
	 * @author cao senrong
	 * @param map
	 * @return
	 * @throws CommonException
	 * Map 
	 *
	 */
	public void modifyTriggerAlarm(Map map) throws CommonException;
	
	public void InitAlarm2Route() throws CommonException;
	
	public List<Map> getWaveLengthList(Map map) throws CommonException;
	
	public int getOTDRType(Map map) throws CommonException;
	
	public List<Map> getRangeList(Map map) throws CommonException;
	
	public List<Map> getPluseWidthList(Map map) throws CommonException;

	public String generateDiagram(Map<String, String> displayCond) throws CommonException;
	
}
