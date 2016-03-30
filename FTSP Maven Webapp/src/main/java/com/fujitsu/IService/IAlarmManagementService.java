package com.fujitsu.IService;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.fujitsu.common.CommonException;
import com.fujitsu.model.CurrentAlarmModel;

public interface IAlarmManagementService {
	/**
	 * Method name: getAllEmsGroups <BR>
	 * Description: 查询所有的网管分组<BR>
	 * Remark: 2013-11-15<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
//	public Map<String, Object> getAllEmsGroups() throws CommonException;
	/**
	 * Method name: getAllEmsGroupsNoAll <BR>
	 * Description: 查询所有网管分组(不包括全部)<BR>
	 * Remark: 2014-01-24<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
//	public Map<String, Object> getAllEmsGroupsNoAll() throws CommonException;
	/**
	 * Method name: getAllEmsByEmsGroupId <BR>
	 * Description: 查询某个网管分组下的所有网管<BR>
	 * Remark: 2013-11-15<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
//	public Map<String, Object> getAllEmsByEmsGroupId(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: getAllEmsByEmsGroupIdNoAll <BR>
	 * Description: 查询某个网管分组下的所有网管(不包括全部)<BR>
	 * Remark: 2014-01-24<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
//	public Map<String, Object> getAllEmsByEmsGroupIdNoAll(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: getAllNeByEmsIdAndNename <BR>
	 * Description: 模糊查询某个网管下的所有网元<BR>
	 * Remark: 2013-11-19<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
//	public Map<String, Object> getAllNeByEmsIdAndNename(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: getCurrentAlarmCount <BR>
	 * Description: 根据网管分组ID、网管ID、网元ID、告警级别，查询某告警级别的当前告警数<BR>
	 * Remark: 2013-11-26<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
//	public Map<String, Object> getCurrentAlarmCount(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: getCurrentAlarms <BR>
	 * Description: 根据网管分组ID、网管ID、网元ID、告警级别、分页信息、过滤器信息，查询当前告警信息<BR>
	 * Remark: 2013-11-25<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getCurrentAlarms(Map<String, Object> paramMap,int start, int limit, Integer userId) throws CommonException;
	/**
	 * Method name: getHistoryAlarms <BR>
	 * Description: 根据网元ID、首次发生时间、清除时间、分页信息，查询历史告警信息<BR>
	 * Remark: 2013-11-20<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR> 
	 * @throws ParseException 
	 */
	public Map<String, Object> getHistoryAlarms(Map<String, Object> paramMap,int start, int limit) throws CommonException, ParseException;
	/**
	 * Method name: getAlarmDetail <BR>
	 * Description: 根据告警ID、告警种类(当前、历史)，查询告警详情<BR>
	 * Remark: 2013-11-26<BR>
	 * @author CaiJiaJia
	 * @return JSONObject<BR> 
	 */
	public JSONObject getAlarmDetail(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: getAlarmNameByFactory <BR>
	 * Description: 查询某厂家的所有告警名称<BR>
	 * Remark: 2013-12-12<BR>
	 * @author CaiJiaJia
	 * @return List<String><BR>
	 */
	public List<String> getAlarmNameByFactory(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: getAlarmNameByFactoryFromShield <BR>
	 * Description: 查询屏蔽器中某厂家的所有告警名称<BR>
	 * Remark: 2014-01-21<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getAlarmNameByFactoryFromShield(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: getCurrentAlarms_High <BR>
	 * Description: 高级查询当前告警信息<BR>
	 * Remark: 2013-12-16<BR>
	 * @author CaiJiaJia
	 * @return String<BR>
	 */
	public Map<String, Object> getAlarms_High(Map<String, Object> paramMap,int start, int limit) throws CommonException, ParseException;
	/**
	 * Method name: getAlarmFiltersByUserId <BR>
	 * Description: 根据创建人ID,查询告警过滤器信息<BR>
	 * Remark: 2013-12-24<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR> 
	 * @throws ParseException 
	 */
	public Map<String, Object> getAlarmFiltersByUserId(Map<String, Object> paramMap,int start, int limit) throws CommonException, ParseException;
	/**
	 * Method name: getAlarmFiltersSummaryByUserId <BR>
	 * Description: 根据创建人ID,查询告警过滤器摘要信息<BR>
	 * Remark: 2014-08-14<BR>
	 * @author Gutao
	 * @return Map<String, Object>(FILTER_ID,FILTER_NAME)<BR> 
	 * @throws CommonException 
	 */
	public Map<String, Object> getAlarmFiltersSummaryByUserId(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: getAlarmFiltersComReportByUserId <BR>
	 * Description: 根据创建人ID,查询综告接口过滤器信息<BR>
	 * Remark: 2014-01-26<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR> 
	 * @throws ParseException 
	 */
	public Map<String, Object> getAlarmFiltersComReportByUserId(Map<String, Object> paramMap,int start, int limit) throws CommonException, ParseException;
	/**
	 * Method name: addAlarmFilter <BR>
	 * Description: 新增当前告警过滤器<BR>
	 * Remark: 2013-12-25<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void addAlarmFilter(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: addAlarmFilterComReport <BR>
	 * Description: 新增综告接口过滤器<BR>
	 * Remark: 2014-01-26<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void addAlarmFilterComReport(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: getDetailByNodeLevel <BR>
	 * Description: 根据不同节点级别查询信息<BR>
	 * Remark: 2014-01-06<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getDetailByNodeLevel(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: getAllNeModelByFactory <BR>
	 * Description: 查询某厂家的所有网元型号<BR>
	 * Remark: 2014-01-08<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getAllNeModelByFactory(Map<String, Object> paramMap) throws CommonException;
	
	/**
	 * Method name: getAlarmFilterFirstDetailById <BR>
	 * Description: 根据ID，查询过滤器的第一个窗口信息<BR>
	 * Remark: 2014-01-09<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getAlarmFilterFirstDetailById(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: getAlarmFilterSecondDetailById <BR>
	 * Description: 根据ID，查询过滤器的第二个窗口信息<BR>
	 * Remark: 2014-01-09<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getAlarmFilterSecondDetailById(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: getAlarmFilterThirdDetailById <BR>
	 * Description: 根据ID，查询过滤器d第三个窗口信息<BR>
	 * Remark: 2014-01-09<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getAlarmFilterThirdDetailById(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: modifyAlarmFilter <BR>
	 * Description: 修改当前告警过滤器<BR>
	 * Remark: 2014-01-14<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void modifyAlarmFilter(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: modifyAlarmFilterComReport <BR>
	 * Description: 修改综告接口过滤器<BR>
	 * Remark: 2014-01-27<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void modifyAlarmFilterComReport(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: deleteAlarmFilter <BR>
	 * Description: 删除当前告警过滤器<BR>
	 * Remark: 2014-01-14<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void deleteAlarmFilter(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: updateAlarmFilterStatus <BR>
	 * Description: 更新当前告警过滤器状态<BR>
	 * Remark: 2014-01-15<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void updateAlarmFilterStatus(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: getAllAlarmShields <BR>
	 * Description: 查询所有告警屏蔽<BR>
	 * Remark: 2014-01-15<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getAllAlarmShield(Map<String, Object> paramMap,int start, int limit) throws CommonException, ParseException;
	/**
	 * Method name: addAlarmShield <BR>
	 * Description: 新增告警屏蔽器<BR>
	 * Remark: 2014-01-15<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void addAlarmShield(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: getSimpleByNodeLevel <BR>
	 * Description: 根据不同节点级别查询简单信息<BR>
	 * Remark: 2014-01-15<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getSimpleByNodeLevel(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: deleteAlarmShield <BR>
	 * Description: 删除告警屏蔽器<BR>
	 * Remark: 2014-01-16<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void deleteAlarmShield(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: getAlarmShieldFirstDetailById <BR>
	 * Description: 根据ID，查询屏蔽器的第一个窗口信息<BR>
	 * Remark: 2014-01-16<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getAlarmShieldFirstDetailById(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: getAlarmShieldSecondDetailById <BR>
	 * Description: 根据ID，查询屏蔽器的第二个窗口信息<BR>
	 * Remark: 2014-01-16<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getAlarmShieldSecondDetailById(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: modifyAlarmShield <BR>
	 * Description: 修改告警屏蔽器<BR>
	 * Remark: 2014-01-16<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void modifyAlarmShield(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: updateAlarmShieldStatus <BR>
	 * Description: 更新告警屏蔽器状态<BR>
	 * Remark: 2014-01-17<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void updateAlarmShieldStatus(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: alarmSynch <BR>
	 * Description: 告警同步<BR>
	 * Remark: 2014-01-21<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void alarmSynch(Map<String, Object> paramMap) throws CommonException;
	/**
	 * @@@分权分域到网元@@@
	 * Method name: getAlarmAutoConfirmByEmsGroup <BR>
	 * Description: 根据网管分组ID,查询告警自动确认设置<BR>
	 * Remark: 2014-01-22<BR>
	 * @author CaiJiaJia
	 * @param limit 
	 * @param start 
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getAlarmAutoConfirmByEmsGroup(Integer sysUserId,Map<String, Object> paramMap, int start, int limit) throws CommonException;
	/**
	 * Method name: modifyAlarmAutoConfirm <BR>
	 * Description: 修改告警自动确认<BR>
	 * Remark: 2014-01-22<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void modifyAlarmAutoConfirm(List<Map<String, Object>> list) throws CommonException;
	/**
	 * Method name: confirmSet <BR>
	 * Description: 确认设置<BR>
	 * Remark: 2014-01-23<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void confirmSet(Map<String, Object> paramMap) throws CommonException;
	/**
	 * @@@分权分域到网元@@@
	 * Method name: getAlarmAutoConfirmByEmsGroup <BR>
	 * Description: 根据网管分组ID,查询告警重定义设置<BR>
	 * Remark: 2014-01-23<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getAlarmRedefineByEmsGroup(Integer sysUserId,Map<String, Object> paramMap,int start, int limit) throws CommonException;
	/**
	 * Method name: addAlarmRedefine <BR>
	 * Description: 新增告警重定义设置<BR>
	 * Remark: 2014-01-23<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void addAlarmRedefine(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: deleteAlarmRedefine <BR>
	 * Description: 删除告警重定义设置<BR>
	 * Remark: 2014-01-23<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void deleteAlarmRedefine(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: getAlarmRedefineById <BR>
	 * Description: 根据ID,查询告警及事件重定义<BR>
	 * Remark: 2014-01-24<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getAlarmRedefineById(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: modifyAlarmRedefine <BR>
	 * Description: 修改告警重定义设置<BR>
	 * Remark: 2014-01-24<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void modifyAlarmRedefine(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: updateAlarmRedefineStatus <BR>
	 * Description: 更新告警重定义状态<BR>
	 * Remark: 2014-01-24<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void updateAlarmRedefineStatus(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: modifyAlarmPush <BR>
	 * Description: 更新告告警推送设置<BR>
	 * Remark: 2014-01-27<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void modifyAlarmPush(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: getAlarmPush <BR>
	 * Description: 查询告警推送设置<BR>
	 * Remark: 2014-01-28<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getAlarmPush() throws CommonException;
	/**
	 * Method name: modifyAlarmConfirmShift <BR>
	 * Description: 更新告警自动确认、转移设置<BR>
	 * Remark: 2014-01-28<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void modifyAlarmConfirmShift(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: alarmAutoConfirm <BR>
	 * Description: 执行告警自动确认<BR>
	 * Remark: 2014-02-11<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void alarmAutoConfirm() throws CommonException;
	/**
	 * Method name: alarmAutoShift <BR>
	 * Description: 执行告警自动转移<BR>
	 * Remark: 2014-02-12<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void alarmAutoShift() throws CommonException;
	/**
	 * Method name: getAlarmConfirmShift <BR>
	 * Description: 查询告警自动确认、转移设置<BR>
	 * Remark: 2014-01-28<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getAlarmConfirmShift(Map<String, Object> paramMap) throws CommonException;
	/**
	 * 
	 * Method name: modifyAlarmAutoSynch <BR>
	 * Description: 更新自动同步 <BR>
	 * Remark: <BR>
	 * @param list
	 * @throws CommonException  void<BR>
	 */
	public void modifyAlarmAutoSynch(List<Map<String, Object>> list) throws CommonException;
	
	/**
	 * @@@分权分域到网元@@@
	 * Method name: getAlarmAutoSynchByEmsGroup <BR>
	 * Description: 查询自动同步 <BR>
	 * Remark: <BR>
	 * @param paramMap
	 * @param limit 
	 * @param start 
	 * @return
	 * @throws CommonException  Map<String,Object><BR>
	 */
	public Map<String, Object> getAlarmAutoSynchByEmsGroup(Integer sysUserId,Map<String, Object> paramMap, int start, int limit) throws CommonException;
	
	/**
	 * 
	 * Method name: getAlarmRedefineByFactory <BR>
	 * Description: 根据厂家ID查询归一化设置 <BR>
	 * Remark: <BR>
	 * @param paramMap
	 * @return
	 * @throws CommonException  Map<String,Object><BR>
	 */
	public Map<String, Object> getAlarmNormlizedByFactory(Map<String, Object> paramMap, int start, int limit) throws CommonException;
	
	/**
	 * 
	 * Method name: getAlarmNormlizedById <BR>
	 * Description: 根据id查找归一化 <BR>
	 * Remark: <BR>
	 * @param paramMap
	 * @return
	 * @throws CommonException  Map<String,Object><BR>
	 */
	public Map<String, Object> getAlarmNormlizedById(Map<String, Object> paramMap) throws CommonException;
	
	/**
	 * 
	 * Method name: addAlarmNormlized <BR>
	 * Description: 新增归一化规则 <BR>
	 * Remark: <BR>
	 * @param paramMap
	 * @throws CommonException  void<BR>
	 */
	public void addAlarmNormlized(Map<String, Object> paramMap) throws CommonException;
	
	/**
	 * 
	 * Method name: deleteAlarmNormlized <BR>
	 * Description: 删除归一化规则 <BR>
	 * Remark: <BR>
	 * @param paramMap
	 * @throws CommonException  void<BR>
	 */
	public void deleteAlarmNormlized(Map<String, Object> paramMap) throws CommonException;
	
	/**
	 * 
	 * Method name: modifyAlarmNormlized <BR>
	 * Description: 修改归一化规则 <BR>
	 * Remark: <BR>
	 * @param paramMap
	 * @throws CommonException  void<BR>
	 */
	public void modifyAlarmNormlized(Map<String, Object> paramMap) throws CommonException;
	
	
	/**
	 * Method name: alarmAutoSynch <BR>
	 * Description: 告警自动同步<BR>
	 * Remark: 2014-02-10<BR>
	 * @return void<BR>
	 */
	public void alarmAutoSynch(Map<String, Object> paramMap) throws CommonException;
	
	/**
	 * 
	 * Method name: getDistinctDelayTime <BR>
	 * Description: 查找延时的种类 10 15 20 <BR>
	 * Remark: <BR>
	 * @return  List<Map<String,String>><BR>
	 */
	public List<Map<String, String>> getDistinctDelayTime();
	/**
	 * 
	 * Method name: getCanDelaySync <BR>
	 * Description: 根据延时时间查找对应同步网管 <BR>
	 * Remark: <BR>
	 * @param i
	 * @return  List<Map><BR>
	 */
	public List<Map<String,Object>> getCanDelaySync(int i);
	/**
	 * Method name: alarmManualConfirm <BR>
	 * Description: 执行告警手动确认<BR>
	 * Remark: 2014-02-14<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void alarmManualConfirm(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: alarmAntiConfirm <BR>
	 * Description: 执行告警反确认<BR>
	 * Remark: 2014-02-14<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void alarmAntiConfirm(Map<String, Object> paramMap) throws CommonException;
	/**
	 * Method name: getReportAlarmByEms <BR>
	 * Description: 报表模块查询告警数据接口<BR>
	 * Remark: 2014-02-14<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getReportAlarmByEms(int emsGroupId,int[] emsId,String tableName,int start,int limit,int time);
	/**
	 * Method name: getCurrentAlarmByNeIdForView <BR>
	 * Description: 接口->视图->于彩青<BR>
	 * Remark: 2014-02-18<BR>
	 * @author CaiJiaJia
	 * @return List<CurrentAlarmModel><BR>
	 */
	public List<CurrentAlarmModel> getCurrentAlarmByNeIdForView(int neId); 
	/**
	 * Method name: getCurrentAlarmByEmsIdForView <BR>
	 * Description: 接口->视图->梅凯<BR>
	 * Remark: 2014-02-19<BR>
	 * @author CaiJiaJia
	 * @return List<CurrentAlarmModel><BR>
	 */
	public List<CurrentAlarmModel> getCurrentAlarmByEmsIdForView(int emsId);
	/**
	 * Method name: getCurrentAlarmBySubnetForView <BR>
	 * Description: 接口->视图->梅凯<BR>
	 * Remark: 2014-02-19<BR>
	 * @author CaiJiaJia
	 * @return List<CurrentAlarmModel><BR>
	 */
	public List<CurrentAlarmModel> getCurrentAlarmBySubnetForView(List<Integer> subnetIdList); 
	/**
	 * Method name: getCurrentAlarmForCircuit <BR>
	 * Description: 接口->电路->戴慧军<BR>
	 * Remark: 2014-02-19<BR>
	 * @author CaiJiaJia
	 * @return Map<String,Object><BR>
	 */
	public Map<String,Object> getCurrentAlarmForCircuit(List<Integer> neIdList,List<Integer> ptpIdList,int start,int limit);
	/**
	 * Method name: getCurrentAlarmByNeIdListForCutover <BR>
	 * Description: 接口->割接->刘鑫<BR>
	 * Remark: 2014-02-24<BR>
	 * @author CaiJiaJia
	 * @return Map<String,Object><BR>
	 */
	public Map<String,Object> getCurrentAlarmByNeIdListForCutover(List<Integer> neIdList);
	/**
	 * Method name: getCurrentAlarmByNeIdListAndTimeForCutover <BR>
	 * Description: 接口->割接->刘鑫<BR>
	 * Remark: 2014-02-24<BR>
	 * @author CaiJiaJia
	 * @return Map<String,Object><BR>
	 */
	public Map<String,Object> getCurrentAlarmByNeIdListAndTimeForCutover(List<Integer> neIdList,Date startTime,Date endTime,int start,int limit);
	/**
	 * Method name: getAllCurrentAlarmCount <BR>
	 * Description: 查询所有告警级别的当前告警数<BR>
	 * Remark: 2014-02-24<BR>
	 * @author CaiJiaJia
	 * @return Map<String,Object><BR>
	 */
	public Map<String, Object> getAllCurrentAlarmCount(Map<String, Object> paramMap)  throws CommonException;
	/**
	 * Method name: getCurrentAlarmCountForFP <BR>
	 * Description: 获取所有告警级别的当前告警数用于首页统计<BR>
	 * Remark: 2014-03-17<BR>
	 * @author Gutao
	 * @return Map<String,Object><BR>
	 */
	public Map<String, Object> getCurrentAlarmCountForFP()  throws CommonException;	
	/**
	 * Method name: getAlarmColorSet <BR>
	 * Description: 查询告警颜色设置<BR>
	 * Remark: 2014-03-06<BR>
	 * @author CaiJiaJia
	 * @return Map<String,Object><BR>
	 */
	public Map<String, Object> getAlarmColorSet()  throws CommonException;
	/**
	 * 
	 * Method name: getAlarmByIds <BR>
	 * Description: 根据ID找告警 <BR>
	 * Remark: <BR>
	 * @param paramMap
	 * @return
	 * @throws CommonException  Map<String,Object><BR>
	 */
	public Map<String, Object> getAlarmByIds(Map<String, Object> paramMap) throws CommonException;
	
	//注释补全
	public Map<String, Object> getAlarmMonthDataByEms(Map<String, Object> paramMap) throws CommonException;
	
	//注释补全
	public List<Map> generateAlarmFromMonodb(Map<String, Object> paramMap) throws CommonException;
	
	/**
	 * Method name: getAllCurrentAlarmCountForCircuit <BR>
	 * Description: 获取告警计数（用于电路详情）
	 * @param neIdList
	 * @param ptpIdList
	 * @return Map<String, Object> Key:PS_CRITICAL,PS_MAJOR,PS_MINOR,PS_WARNING,PS_CLEARED <BR>
	 */
	public Map<String, Object> getAllCurrentAlarmCountForCircuit(List<Integer> neIdList, List<Integer> ptpIdList);
	
	
	//根据网管删除当前告警
	public boolean deleteAlarmByEmsIds(List<Integer> emsIds);
	
	/**
	 * 
	 * Method name: deleteAlarmAutoSynchSetting <BR>
	 * Description: 删除自动同步 <BR>
	 * Remark: <BR>
	 * @param emsConnectionId  void<BR>
	 */
	public void deleteAlarmAutoSynchSetting(int emsConnectionId) throws CommonException;
	Map<String, Object> generateData(Map param);
	public void updateAlarmAutoSynchExcuteStatus(Map<String, Object> map);
	public void updateDBbackup(Map<String,Object> manuValue);
	public List<Map<String, Object>> getAutoAlarmSyncByEmsId(List<Integer> emsIds);
	
	/**
	 * Method name: alarmReversal <BR>
	 * Description: 执行告警反转<BR>
	 * Remark: 2014-08-19<BR>
	 * @author GuTao
	 * @return void<BR>
	 */
	public void alarmReversal(Map<String, Object> paramMap) throws CommonException;
	
	/**
	 * Method name: antiAlarmReversal <BR>
	 * Description: 取消告警反转<BR>
	 * Remark: 2014-08-20<BR>
	 * @author GuTao
	 * @return void<BR>
	 */
	public void antiAlarmReversal(Map<String, Object> paramMap) throws CommonException;
		/**
	 * 获取保护倒换数据
	 * @param paramMap
	 * @param start
	 * @param limit
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getProtectionSwitch(
			Map<String, Object> paramMap, int start, int limit) throws CommonException;
	/**
	 * 获取性能越限数据
	 * @param paramMap
	 * @param start
	 * @param limit
	 * @return
	 * @throws CommonException
	 */
	public List<Map<String, Object>> getPmExceedData(
			Map<String, Object> paramMap, int start, int limit) throws CommonException;
	
	/**
	 * 主告警展开收敛告警
	 * @param _id 
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getAlarmHavingConverge(int _id) throws CommonException;
	
	/**
	 * 查询电路相关的告警
	 * @param circuitInfoId 电路id
	 * @param circuitType   电路类型：1.普通电路，2.OTN电路
	 * @param start
	 * @param limit start,limit 任一个等于-1则不分页
	 * @param isConverge 为true时，查询告警加入告警收敛的查询条件
	 * @param needPtpLevel  是否需要查询ptp等级的告警
	 * @param needCtplevel  是否需要查询ctp等级的告警
	 * @param needNeLevel   是否需要查询网元级告警
	 * @param needEquipLevel  是否需要查询板卡级告警
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getCurrentAlarmForCircuit(int circuitInfoId,
			int circuitType, int start,int limit,boolean isConverge,boolean needPtpLevel, boolean needCtpLevel,
			boolean needNeLevel, boolean needEquipLevel) throws CommonException;
	

	
	/**查询电路list相关的告警
	 * @param circuitList 每个map包含：circuitInfoId 电路id；circuitType   电路类型：1.普通电路，2.OTN电路
	 * @param start
	 * @param limit
	 * @param isConverge
	 * @param needPtpLevel
	 * @param needCtpLevel
	 * @param needNeLevel
	 * @param needEquipLevel
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getCurrentAlarmForCircuit(List<Map> circuitList, int start,int limit,boolean isConverge,boolean needPtpLevel, boolean needCtpLevel,
			boolean needNeLevel, boolean needEquipLevel) throws CommonException;
	
	/**根据传入的网元，端口，ctp，板卡的id的list信息
	 * @param neIdList
	 * @param ptpIdList
	 * @param ctpIdList
	 * @param equipIdList
	 * @param start
	 * @param limit
	 * @param isConverge
	 * @param needPtpLevel
	 * @param needCtpLevel
	 * @param needNeLevel
	 * @param needEquipLevel
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getCurrentAlarmForCircuit(
			List<Integer> neIdList, List<Integer> ptpIdList,
			List<Integer> ctpIdList, List<Integer> equipIdList, int start,
			int limit, boolean isConverge, boolean needPtpLevel,
			boolean needCtpLevel, boolean needNeLevel, boolean needEquipLevel)
			throws CommonException;
	
	
	/**获取指定光缆段的告警状态
	 * @param cableIdList
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Integer> getCableSectionAlarmState(List<String> cableIdList) throws CommonException;
	
	/**获取指定局站的告警级别
	 * @param stationIdList
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Integer> getStationAlarmState(List<String> stationIdList) throws CommonException;
	
}
