package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.fujitsu.model.FaultAnalysisModel;
import com.fujitsu.model.AlarmAutoSynchModel;
import com.fujitsu.model.AlarmFilterModel;
import com.fujitsu.model.AlarmShieldModel;

public interface AlarmManagementMapper {
	/**
	 * Method name: getAllEmsGroups <BR>
	 * Description: 查询所有的网管分组<BR>
	 * Remark: 2013-11-15<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
//	public List<Map<String, Object>> getAllEmsGroups();
	/**
	 * Method name: getAllEmsGroupsNoAll <BR>
	 * Description: 查询所有网管分组(不包括全部)<BR>
	 * Remark: 2014-01-24<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
//	public List<Map<String, Object>> getAllEmsGroupsNoAll();
	/**
	 * Method name: getAllEmsByEmsGroupId <BR>
	 * Description: 查询某个网管分组下的所有网管<BR>
	 * Remark: 2013-11-15<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
//	public List<Map<String, Object>> getAllEmsByEmsGroupId(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: getAllEmsByEmsGroupIdNoAll <BR>
	 * Description: 查询某个网管分组下的所有网管(不包括全部)<BR>
	 * Remark: 2014-01-24<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
//	public List<Map<String, Object>> getAllEmsByEmsGroupIdNoAll(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: getAllNeByEmsIdAndNename <BR>
	 * Description: 模糊查询某个网管下的所有网元<BR>
	 * Remark: 2013-11-19<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
//	public List<Map<String, Object>> getAllNeByEmsIdAndNename(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: getAlarmFilterCountByUserId <BR>
	 * Description: 根据创建人ID查询过滤器总数<BR>
	 * Remark: 2013-12-24<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getAlarmFilterCountByUserId(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: getAlarmFilterByUserId <BR>
	 * Description: 根据创建人ID查询过滤器信息<BR>
	 * Remark: 2013-12-24<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAlarmFilterByUserId(@Param(value="map") Map<String, Object> paramMap,
			@Param(value = "start") int start,@Param(value = "limit") int limit);
	/**
	 * Method name: getAlarmFilterSummaryByUserId <BR>
	 * Description: 根据创建人ID查询过滤器摘要信息<BR>
	 * Remark: 2014-08-14<BR>
	 * @author Gutao
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAlarmFilterSummaryByUserId(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: getAlarmFilterEnableByUserId <BR>
	 * Description: 根据创建人ID查询已启用的过滤器信息<BR>
	 * Remark: 2014-02-14<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAlarmFilterEnableByUserId(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: addAlarmFilter <BR>
	 * Description: 新增当前告警过滤器<BR>
	 * Remark: 2013-12-25<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void addAlarmFilter(@Param(value="alarmFilterModel") AlarmFilterModel alarmFilterModel);
	/**
	 * Method name: addAlarmFilterComReport <BR>
	 * Description: 新增综告接口过滤器<BR>
	 * Remark: 2014-01-26<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void addAlarmFilterComReport(@Param(value="alarmFilterModel") AlarmFilterModel alarmFilterModel);
	/**
	 * Method name: addAlarmFilterRelation <BR>
	 * Description: 新增告警过滤器名称关联关系<BR>
	 * Remark: 2014-01-02<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void addAlarmFilterRelation(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: addAlarmFilterTypeRelation <BR>
	 * Description: 新增告警过滤器类型关联关系<BR>
	 * Remark: 2014-01-02<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void addAlarmFilterTypeRelation(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: addAlarmFilterLevelRelation <BR>
	 * Description: 新增告警过滤器级别关联关系<BR>
	 * Remark: 2014-01-02<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void addAlarmFilterLevelRelation(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: addAlarmFilterAffectRelation <BR>
	 * Description: 新增告警过滤器业务影响关联关系<BR>
	 * Remark: 2014-01-02<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void addAlarmFilterAffectRelation(@Param(value="map") Map<String, Object> paramMap);
	
	/**
	 * Method name: addAlarmFilterResourceRelation <BR>
	 * Description: 新增告警过滤器源关联关系<BR>
	 * Remark: 2014-01-09<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void addAlarmFilterResourceRelation(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: addAlarmFilterResourceTypeRelation <BR>
	 * Description: 新增告警过滤器源类型关联关系<BR>
	 * Remark: 2014-01-09<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void addAlarmFilterResourceTypeRelation(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: addAlarmFilterPtpModelRelation <BR>
	 * Description: 新增告警过滤器端口型号关联关系<BR>
	 * Remark: 2014-01-09<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void addAlarmFilterPtpModelRelation(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: getDetailByNode_emsGroupId <BR>
	 * Description: 根据网管分组ID，查询详细信息<BR>
	 * Remark: 2014-01-07<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getDetailByNode_emsGroupId(@Param(value="emsGroupId") int emsGroupId);
	/**
	 * Method name: getDetailByNode_emsId <BR>
	 * Description: 根据网管ID，查询详细信息<BR>
	 * Remark: 2014-01-07<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getDetailByNode_emsId(@Param(value="emsId") int emsId);
	/**
	 * Method name: getDetailByNode_subnetId <BR>
	 * Description: 根据子网ID，查询详细信息<BR>
	 * Remark: 2014-01-07<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getDetailByNode_subnetId(@Param(value="subnetId") int subnetId);
	/**
	 * Method name: getDetailByNode_neId <BR>
	 * Description: 根据网元ID，查询详细信息<BR>
	 * Remark: 2014-01-07<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getDetailByNode_neId(@Param(value="neId") int neId);
	/**
	 * Method name: getDetailByNode_shelfId <BR>
	 * Description: 根据子架ID，查询详细信息<BR>
	 * Remark: 2014-01-07<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getDetailByNode_shelfId(@Param(value="shelfId") int shelfId);
	/**
	 * Method name: getDetailByNode_unitId <BR>
	 * Description: 根据板卡ID，查询详细信息<BR>
	 * Remark: 2014-01-07<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getDetailByNode_unitId(@Param(value="unitId") int unitId);
	/**
	 * Method name: getDetailByNode_subunitId <BR>
	 * Description: 根据子板卡ID，查询详细信息<BR>
	 * Remark: 2014-01-07<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getDetailByNode_subunitId(@Param(value="subunitId") int subunitId);
	/**
	 * Method name: getDetailByNode_ptpId <BR>
	 * Description: 根据端口ID，查询详细信息<BR>
	 * Remark: 2014-01-07<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getDetailByNode_ptpId(@Param(value="ptpId") int ptpId);
	/**
	 * Method name: getAllNeModelByFactory <BR>
	 * Description: 查询某厂家的所有网元型号<BR>
	 * Remark: 2014-01-08<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public List<Map<String, Object>> getAllNeModelByFactory(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: getAlarmFilterFirstDetail_MainById <BR>
	 * Description: 根据ID查询第一个窗口的名称、描述 、选择源<BR>
	 * Remark: 2014-01-09<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getAlarmFilterFirstDetail_MainById(@Param(value="filterId") int filterId);
	/**
	 * Method name: getAlarmFilterFirstDetail_AlarmNameById <BR>
	 * Description: 根据ID查询第一个窗口的已选告警名称<BR>
	 * Remark: 2014-01-09<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAlarmFilterFirstDetail_AlarmNameById(@Param(value="filterId") int filterId);
	/**
	 * Method name: getAlarmFilterFirstDetail_AlarmTypeById <BR>
	 * Description: 根据ID查询第一个窗口的告警类型<BR>
	 * Remark: 2014-01-09<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAlarmFilterFirstDetail_AlarmTypeById(@Param(value="filterId") int filterId);
	/**
	 * Method name: getAlarmFilterFirstDetail_AlarmLevelById <BR>
	 * Description: 根据ID查询第一个窗口的告警级别<BR>
	 * Remark: 2014-01-09<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAlarmFilterFirstDetail_AlarmLevelById(@Param(value="filterId") int filterId);
	/**
	 * Method name: getAlarmFilterFirstDetail_AlarmAffectlById <BR>
	 * Description: 根据ID查询第一个窗口的业务影响<BR>
	 * Remark: 2014-01-09<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAlarmFilterFirstDetail_AlarmAffectlById(@Param(value="filterId") int filterId);
	/**
	 * Method name: getAlarmFilterSecondtDetailById <BR>
	 * Description: 根据ID查询第二个窗口信息<BR>
	 * Remark: 2014-01-09<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAlarmFilterSecondtDetailById(@Param(value="filterId") int filterId);
	/**
	 * Method name: getAlarmFilterThirdNeModelById <BR>
	 * Description: 根据ID查询第三个窗口网元型号信息<BR>
	 * Remark: 2014-01-14<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAlarmFilterThirdNeModelById(@Param(value="filterId") int filterId);
	/**
	 * Method name: getAlarmFilterThirdPtpModelById <BR>
	 * Description: 根据ID查询第三个窗口端口型号信息<BR>
	 * Remark: 2014-01-14<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAlarmFilterThirdPtpModelById(@Param(value="filterId") int filterId);
	/**
	 * Method name: getAlarmFilterThirdPtpAlarmStatusById <BR>
	 * Description: 根据ID查询第三个窗口通道告警信息<BR>
	 * Remark: 2014-01-14<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getAlarmFilterThirdPtpAlarmStatusById(@Param(value="filterId") int filterId);
	/**
	 * Method name: deleteAlarmFilter <BR>
	 * Description: 删除当前告警过滤器<BR>
	 * Remark: 2014-01-14<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void deleteAlarmFilter(@Param(value="filterId") int filterId);
	/**
	 * Method name: deleteAlarmFilterRelation <BR>
	 * Description: 删除告警过滤器名称关联关系<BR>
	 * Remark: 2014-01-14<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void deleteAlarmFilterRelation(@Param(value="filterId") int filterId);
	/**
	 * Method name: deleteAlarmFilterTypeRelation <BR>
	 * Description: 删除告警过滤器类型关联关系<BR>
	 * Remark: 2014-01-14<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void deleteAlarmFilterTypeRelation(@Param(value="filterId") int filterId);
	/**
	 * Method name: deleteAlarmFilterLevelRelation <BR>
	 * Description: 删除告警过滤器级别关联关系<BR>
	 * Remark: 2014-01-14<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void deleteAlarmFilterLevelRelation(@Param(value="filterId") int filterId);
	/**
	 * Method name: deleteAlarmFilterAffectRelation <BR>
	 * Description: 删除告警过滤器业务影响关联关系<BR>
	 * Remark: 2014-01-14<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void deleteAlarmFilterAffectRelation(@Param(value="filterId") int filterId);
	/**
	 * Method name: deleteAlarmFilterResourceRelation <BR>
	 * Description: 删除告警过滤器源(设备)关联关系<BR>
	 * Remark: 2014-01-14<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void deleteAlarmFilterResourceRelation(@Param(value="filterId") int filterId);
	/**
	 * Method name: deleteAlarmFilterResourceTypeRelation <BR>
	 * Description: 删除告警过滤器源类型(网元型号)关联关系<BR>
	 * Remark: 2014-01-14<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void deleteAlarmFilterResourceTypeRelation(@Param(value="filterId") int filterId);
	/**
	 * Method name: deleteAlarmFilterPtpModelRelation <BR>
	 * Description: 删除告警过滤器端口型号关联关系<BR>
	 * Remark: 2014-01-14<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void deleteAlarmFilterPtpModelRelation(@Param(value="filterId") int filterId);
	/**
	 * Method name: updateAlarmFilter <BR>
	 * Description: 更新当前告警过滤器<BR>
	 * Remark: 2014-01-14<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void updateAlarmFilter(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: updateAlarmFilterComReport <BR>
	 * Description: 更新综告接口过滤器<BR>
	 * Remark: 2014-01-27<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void updateAlarmFilterComReport(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: updateAlarmFilterEnable <BR>
	 * Description: 更新当前告警过滤器状态为启用<BR>
	 * Remark: 2014-01-15<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void updateAlarmFilterEnable(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: updateAlarmFilterPending <BR>
	 * Description: 更新当前告警过滤器状态为挂起<BR>
	 * Remark: 2014-01-15<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void updateAlarmFilterPending(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: getAllAlarmShieldCounts <BR>
	 * Description: 查询告警屏蔽器总数<BR>
	 * Remark: 2014-01-15<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getAlarmShieldCounts(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: getAllAlarmShields <BR>
	 * Description: 查询所有告警屏蔽器信息<BR>
	 * Remark: 2014-01-15<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAllAlarmShield(@Param(value="map") Map<String, Object> paramMap,@Param(value = "start") int start,@Param(value = "limit") int limit);
	/**
	 * Method name: getAlarmShieldCountsByCondition <BR>
	 * Description: 根据条件，查询告警屏蔽器总数<BR>
	 * Remark: 2014-01-20<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getAlarmShieldCountsByCondition(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: getAllAlarmShieldByCondition <BR>
	 * Description: 根据条件，查询所有告警屏蔽器信息<BR>
	 * Remark: 2014-01-20<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAllAlarmShieldByCondition(@Param(value="map") Map<String, Object> paramMap,@Param(value = "start") int start,@Param(value = "limit") int limit);
	/**
	 * Method name: addAlarmShield <BR>
	 * Description: 新增告警屏蔽器<BR>
	 * Remark: 2014-01-15<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void addAlarmShield(@Param(value="alarmShieldModel") AlarmShieldModel alarmShieldModel);
	
	/**
	 * Method name: addAlarmShieldRelation <BR>
	 * Description: 新增告警屏蔽器名称关联关系<BR>
	 * Remark: 2014-01-15<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void addAlarmShieldRelation(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: addAlarmShieldTypeRelation <BR>
	 * Description: 新增告警屏蔽器类型关联关系<BR>
	 * Remark: 2014-01-15<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void addAlarmShieldTypeRelation(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: addAlarmShieldLevelRelation <BR>
	 * Description: 新增告警屏蔽器级别关联关系<BR>
	 * Remark: 2014-01-15<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void addAlarmShieldLevelRelation(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: addAlarmShieldAffectRelation <BR>
	 * Description: 新增告警屏蔽器业务影响关联关系<BR>
	 * Remark: 2014-01-15<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void addAlarmShieldAffectRelation(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: addAlarmFilterResourceRelation <BR>
	 * Description: 新增告警过滤器源关联关系<BR>
	 * Remark: 2014-01-09<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void addAlarmShieldResourceRelation(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: deleteAlarmShield <BR>
	 * Description: 删除告警屏蔽器<BR>
	 * Remark: 2014-01-16<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void deleteAlarmShield(@Param(value="shieldId") int shieldId);
	/**
	 * Method name: deleteAlarmShieldRelation <BR>
	 * Description: 删除告警屏蔽器名称关联关系<BR>
	 * Remark: 2014-01-16<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void deleteAlarmShieldRelation(@Param(value="shieldId") int shieldId);
	/**
	 * Method name: deleteAlarmShieldTypeRelation <BR>
	 * Description: 删除告警屏蔽器类型关联关系<BR>
	 * Remark: 2014-01-16<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void deleteAlarmShieldTypeRelation(@Param(value="shieldId") int shieldId);
	/**
	 * Method name: deleteAlarmShieldLevelRelation <BR>
	 * Description: 删除告警屏蔽器级别关联关系<BR>
	 * Remark: 2014-01-16<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void deleteAlarmShieldLevelRelation(@Param(value="shieldId") int shieldId);
	/**
	 * Method name: deleteAlarmShieldAffectRelation <BR>
	 * Description: 删除告警屏蔽器业务影响关联关系<BR>
	 * Remark: 2014-01-16<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void deleteAlarmShieldAffectRelation(@Param(value="shieldId") int shieldId);
	/**
	 * Method name: deleteAlarmShieldResourceRelation <BR>
	 * Description: 删除告警屏蔽器源(设备)关联关系<BR>
	 * Remark: 2014-01-16<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void deleteAlarmShieldResourceRelation(@Param(value="shieldId") int shieldId);
	/**
	 * Method name: getAlarmShieldFirstDetail_MainById <BR>
	 * Description: 根据ID查询屏蔽器第一个窗口的名称、描述 <BR>
	 * Remark: 2014-01-16<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getAlarmShieldFirstDetail_MainById(@Param(value="shieldId") int shieldId);
	/**
	 * Method name: getAlarmShieldFirstDetail_AlarmNameById <BR>
	 * Description: 根据ID查询屏蔽器第一个窗口的已选告警名称<BR>
	 * Remark: 2014-01-16<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAlarmShieldFirstDetail_AlarmNameById(@Param(value="shieldId") int shieldId);
	/**
	 * Method name: getAlarmShieldFirstDetail_AlarmTypeById <BR>
	 * Description: 根据ID查询屏蔽器第一个窗口的告警类型<BR>
	 * Remark: 2014-01-16<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAlarmShieldFirstDetail_AlarmTypeById(@Param(value="shieldId") int shieldId);
	/**
	 * Method name: getAlarmShieldFirstDetail_AlarmLevelById <BR>
	 * Description: 根据ID查询屏蔽器第一个窗口的告警级别<BR>
	 * Remark: 2014-01-16<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAlarmShieldFirstDetail_AlarmLevelById(@Param(value="shieldId") int shieldId);
	/**
	 * Method name: getAlarmShieldFirstDetail_AlarmAffectlById <BR>
	 * Description: 根据ID查询屏蔽器第一个窗口的业务影响<BR>
	 * Remark: 2014-01-16<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAlarmShieldFirstDetail_AlarmAffectlById(@Param(value="shieldId") int shieldId);
	/**
	 * Method name: getAlarmShieldSecondtDetailById <BR>
	 * Description: 根据ID查询屏蔽器第二个窗口信息<BR>
	 * Remark: 2014-01-16<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAlarmShieldSecondtDetailById(@Param(value="shieldId") int shieldId);
	/**
	 * Method name: updateAlarmShield <BR>
	 * Description: 更新告警过滤器<BR>
	 * Remark: 2014-01-16<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void updateAlarmShield(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: updateAlarmShieldEnable <BR>
	 * Description: 更新告警屏蔽器状态为启用<BR>
	 * Remark: 2014-01-17<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void updateAlarmShieldEnable(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: updateAlarmShieldPending <BR>
	 * Description: 更新告警屏蔽器状态为挂起<BR>
	 * Remark: 2014-01-17<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void updateAlarmShieldPending(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: getAlarmNameByFactoryFromShield <BR>
	 * Description: 查询屏蔽器中某厂家的所有告警名称<BR>
	 * Remark: 2014-01-21<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAlarmNameByFactoryFromShield(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: getEmsIdByneId <BR>
	 * Description: 根据网元ID,查询网管ID<BR>
	 * Remark: 2014-01-21<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getEmsIdByneId(@Param(value="neId") int neId);
	/**
	 * Method name: getEmsByEmsGroupId <BR>
	 * Description: 根据网管分组ID，查询网管<BR>
	 * Remark: 2014-01-21<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getEmsByEmsGroupId(@Param(value="emsGroupId") int emsGroupId);
	/**
	 * Method name: getAllEms <BR>
	 * Description: 查询所有的网管<BR>
	 * Remark: 2014-01-21<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAllEms();
	/**
	 * Method name: getAlarmAutoConfirmByEmsGroup <BR>
	 * Description: 根据网管ID,查询告警自动确认设置<BR>
	 * Remark: 2014-01-22<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAlarmAutoConfirmByEmsGroup(
			@Param(value="map") Map<String, Object> paramMap,
			@Param(value="userId") Integer userId,
			@Param(value="Define") Map Define,
			@Param(value = "start") int start,
			@Param(value = "limit") int limit);
	public Map<String, Object> getAlarmAutoConfirmCountByEmsGroup(
			@Param(value="map") Map<String, Object> paramMap,
			@Param(value="userId") Integer userId,
			@Param(value="Define") Map Define);
	/**
	 * Method name: getAlarmAutoConfirm <BR>
	 * Description: 查询所有告警自动确认设置<BR>
	 * Remark: 2014-01-22<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAlarmAutoConfirm();
	/**
	 * Method name: addAlarmAutoConfirm <BR>
	 * Description: 新增告警自动确认设置<BR>
	 * Remark: 2014-01-22<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void addAlarmAutoConfirm(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: updateAlarmAutoConfirm <BR>
	 * Description: 更新告警自动确认设置<BR>
	 * Remark: 2014-01-22<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void updateAlarmAutoConfirm(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * @@@分权分域到网元@@@
	 * Method name: getAlarmRedefineByEmsGroup <BR>
	 * Description: 根据网管分组ID,查询告警重定义设置<BR>
	 * Remark: 2014-01-23<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAlarmRedefineByEmsGroup(@Param(value="map") Map<String, Object> paramMap,@Param(value = "start") int start,@Param(value = "limit") int limit,@Param(value = "userId") int userId ,@Param(value = "Define") Map Define);
	/**
	 * Method name: addAlarmRedefine <BR>
	 * Description: 新增告警重定义设置<BR>
	 * Remark: 2014-01-23<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void addAlarmRedefine(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: deleteAlarmRedefine <BR>
	 * Description: 删除告警重定义设置<BR>
	 * Remark: 2014-01-23<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void deleteAlarmRedefine(@Param(value="redefineId") int redefineId);
	/**
	 * Method name: getAlarmRedefineById <BR>
	 * Description: 根据ID,查询告警及事件重定义<BR>
	 * Remark: 2014-01-24<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getAlarmRedefineById(@Param(value="redefineId") int redefineId);
	/**
	 * Method name: modifyAlarmRedefine <BR>
	 * Description: 修改告警重定义设置<BR>
	 * Remark: 2014-01-24<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void modifyAlarmRedefine(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: updateAlarmRedefineEnable <BR>
	 * Description: 更新告警重定义状态为启用<BR>
	 * Remark: 2014-01-24<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void updateAlarmRedefineEnable(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: updateAlarmRedefinePending <BR>
	 * Description: 更新告警重定义状态为挂起<BR>
	 * Remark: 2014-01-17<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void updateAlarmRedefinePending(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: modifyAlarmParam <BR>
	 * Description: 更新告警参数设置<BR>
	 * Remark: 2014-01-27<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public void modifyAlarmParam(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * Method name: getSystemParam <BR>
	 * Description: 查询系统参数设置<BR>
	 * Remark: 2014-01-28<BR>
	 * @author CaiJiaJia
	 * @return Map<String, Object><BR>
	 */
	public Map<String, Object> getSystemParam(@Param(value="paramKey") String paramKey);
	
	/**
	 * 
	 * Method name: updateAlarmAutoSynch <BR>
	 * Description: 更新自动同步 <BR>
	 * Remark: <BR>
	 * @param paramMap  void<BR>
	 */
	public void updateAlarmAutoSynch(@Param(value="map") Map<String, Object> paramMap);
	/**
	 * @@@分权分域到网元@@@
	 * Method name: getAlarmAutoSynchByEmsGroup <BR>
	 * Description: 查询自动同步 <BR>
	 * Remark: <BR>
	 * @param paramMap
	 * @param limit 
	 * @param start 
	 * @return  List<Map<String,Object>><BR>
	 */
	public List<Map<String, Object>> getAlarmAutoSynchByEmsGroup(@Param(value="map") Map<String, Object> paramMap,@Param(value="start") int start,@Param(value="limit") int limit,@Param(value = "userId") int userId ,@Param(value = "Define") Map Define);
	/**
	 * @@@分权分域到网元@@@
	 * @param paramMap
	 * @param userId
	 * @param Define
	 * @return
	 */
	public Map<String, Object> getAlarmAutoSynchCountByEmsGroup(@Param(value="map") Map<String, Object> paramMap,@Param(value = "userId") int userId ,@Param(value = "Define") Map Define);
	
	/**
	 * 
	 * Method name: addAlarmAutoSynch <BR>
	 * Description: 新增字段同步 <BR>
	 * Remark: <BR>
	 * @param map  void<BR>
	 */
	public void addAlarmAutoSynch(@Param(value="alarmAutoSynchModel") AlarmAutoSynchModel alarmAutoSynchModel);
	
	/**
	 * 
	 * Method name: getAlarmNormlizedByFactory <BR>
	 * Description: 根据网管ID查询归一化设置 <BR>
	 * Remark: <BR>
	 * @param paramMap
	 * @return  List<Map<String,Object>><BR>
	 */
	public List<Map<String, Object>> getAlarmNormlizedByFactory(@Param(value="map") Map<String, Object> paramMap,@Param(value = "start") int start,@Param(value = "limit") int limit);
	
	/**
	 * 
	 * Method name: getAlarmnormlizedById <BR>
	 * Description: 根据id查找归一化 <BR>
	 * Remark: <BR>
	 * @param redefineId
	 * @return  Map<String,Object><BR>
	 */
	public Map<String, Object> getAlarmNormlizedById(@Param(value="redefineId") int redefineId);
	
	/**
	 * 
	 * Method name: addAlarmNormlized <BR>
	 * Description: 新增归一化规则 <BR>
	 * Remark: <BR>
	 * @param paramMap  void<BR>
	 */
	public void addAlarmNormlized(@Param(value="map") Map<String, Object> paramMap);
	
	/**
	 * 
	 * Method name: deleteAlarmNormlized <BR>
	 * Description: 删除归一化规则 <BR>
	 * Remark: <BR>
	 * @param redefineId  void<BR>
	 */
	public void deleteAlarmNormlized(@Param(value="redefineId") int redefineId);
	
	/**
	 * 
	 * Method name: modifyAlarmNormlized <BR>
	 * Description: 修改归一化 <BR>
	 * Remark: <BR>
	 * @param paramMap  void<BR>
	 */
	public void modifyAlarmNormlized(@Param(value="map") Map<String, Object> paramMap);
	
	/**
	 * 
	 * Method name: updateLatestAlarmAutoSynchTime <BR>
	 * Description: 更新最近一次同步时间 <BR>
	 * Remark: <BR>
	 * @param paramMap  void<BR>
	 */
	public void updateLatestAlarmAutoSynchTime(@Param(value="map") Map<String, Object> paramMap);
	
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
	 * Description: 根据延时时间查找可自动同步告警 <BR>
	 * Remark: <BR>
	 * @param delayTime
	 * @return  List<Map<String,Object>><BR>
	 */
	public List<Map<String, Object>> getCanDelaySync(@Param(value="delayTime") int delayTime);
	/**
	 * Method name: getUserListByUserGroupId <BR>
	 * Description: 查询某用户组的所有用户信息<BR>
	 * Remark: 2014-02-28<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public List<Map<String,Object>> getUserListByUserGroupId(@Param(value = "userGroupId") Integer userGroupId);
	/**
	 * Method name: getAlarmColor <BR>
	 * Description: 查询告警灯颜色信息<BR>
	 * Remark: 2014-03-05<BR>
	 * @author CaiJiaJia
	 * @return void<BR>
	 */
	public List<Map<String,Object>> getAlarmColor();
	
	/**
	 * 
	 * Method name: getAlarmAutoSynchExist <BR>
	 * Description: 获取已经存在的自动同步设置 <BR>
	 * Remark: <BR>
	 * @return  List<Map<String,Object>><BR>
	 */
	public List<Map<String,Object>> getAlarmAutoSynchExist();
	public void deleteAlarmParam(@Param(value="map") Map<String, Object> paramMap);
	public Map<String, Object> getAlarmNormlizedCountByFactory(@Param(value="map") Map<String, Object> paramMap);

	/**
	 * 获取指定PTP端口的Link信息
	 */
	public List<Map<String,Object>> getLinkByPtpId(@Param(value="ptpId") int ptpId);
	
	/**
	 * 获取指定网元的台站名称
	 */
	public Map<String, Object> getStationNameByNeId(@Param(value="neId") int neId);
	
	/**
	 * 插入故障记录
	 */
	public void addFault(@Param(value="faultModel") FaultAnalysisModel alm);
	
	/**
	 * 增加故障的告警信息
	 * 
	 * @param alarmList
	 */
	public void addFaultAlarmInfo(@Param(value = "faultAlarmInfoList")
					List<Map<String, Object>> faultAlarmInfoList);
	
	/**
	 * 获取指定故障的告警ID列表
	 * @param  faultId
	 */
	public List<Integer> getAlarmIdByFaultId(@Param(value="faultId") int faultId);
	
	/**
	 * 获取未确认的故障记录
	 */
	public List<Map<String, Object>> getFaultByNoAck();
	
	/**
	 * 获取指定端口的光口标准内容(T_PM_STD_OPT_PORT)
	 * @param ptpId
	 */
	public Map<String, Object> getPmStdOptPortByPtpId(@Param(value="ptpId") int ptpId);
	/**
	 * @@@分权分域到网元@@@
	 * @param paramMap
	 * @param userId
	 * @param Define
	 * @return
	 */
	public Map<String, Object> getAlarmRedefineCountByEmsGroup(@Param(value="map") Map<String, Object> paramMap,@Param(value = "userId") int userId ,@Param(value = "Define") Map Define);
	public List<Map<String, Object>> findAlarmAutoSynch(@Param(value="emsConnectionId") int emsConnectionId);
	public void deleteAlarmAutoSynch(@Param(value="emsConnectionId") int emsConnectionId);
	public void changeDatabackupToCancel(@Param(value="param") String param);
	public void updateDBbackup(@Param(value="map")Map<String,Object> manuValue);
	public List<Map<String, Object>> getAutoAlarmSyncByEmsId(@Param(value = "emsIds")List<Integer> emsIds);
	
	/**
	 * Method name: getEmsIdListByUserId <BR>
	 * Description: 获取指定用户的设备域（网管） <BR>
	 * Remark: <BR>
	 * @author Gutao
	 * @param userId
	 * @return  List<Map<String,Object>> Key:BASE_EMS_CONNECTION_ID<BR>
	 */
	public List<Map<String, Object>> getEmsIdListByUserId(@Param(value="userId") Integer userId,
			@Param(value = "Define") Map Define);
	/**
	 * Method name: getEmsIdListByUserId <BR>
	 * Description: 获取指定用户的设备域（子网） <BR>
	 * Remark: <BR>
	 * @author Gutao
	 * @param userId
	 * @return  List<Map<String,Object>> Key:BASE_SUBNET_ID<BR>
	 */
	public List<Map<String, Object>> getSubnetIdListByUserId(@Param(value="userId") Integer userId,
			@Param(value = "Define") Map Define);
	/**
	 * Method name: getEmsIdListByUserId <BR>
	 * Description: 获取指定用户的设备域（网元） <BR>
	 * Remark: <BR>
	 * @author Gutao
	 * @param userId
	 * @return  List<Map<String,Object>> Key:BASE_NE_ID<BR>
	 */
	public List<Map<String, Object>> getNeIdListByUserId(@Param(value="userId") Integer userId,
			@Param(value = "Define") Map Define);
	public List<Map<String, Object>> getProtectionSwitch(
			@Param(value="map") Map<String, Object> paramMap,
			@Param(value = "start") int start,
			@Param(value = "limit") int limit);
	public List<Map<String, Object>> getPmExceedData(
			@Param(value="map") Map<String, Object> paramMap,
			@Param(value = "start")  int start,
			@Param(value = "limit") int limit);            
	public List<Map> getSubnetIds(@Param(value = "subIds") String subIds);
	/**
	 * 获取保护倒换的数量
	 * @param paramMap
	 * @return
	 */
	public int getProtectionSwitchCount(@Param(value="map") Map<String, Object> paramMap);
    
	/**获取与指定光缆段相关的ptpId
	 * @param cableIds
	 * @return
	 */
	public List<Map<String, Object>> getPtpListByCableIds(@Param(value = "cableIds") List<String> cableIds);
	
	/**
	 * 删除临时表中给gis地图缓存的告警
	 * @param type
	 * @param id
	 */
	public void deleteTempAlarmForGis(@Param(value = "type") int type, @Param(value = "id") int id);
	
	public void deleteTempAlarmListForGis(@Param(value = "list") List<Map<String, Integer>> list);
	
	/**
	 * 向临时表中增加给gis地图缓存的告警
	 * @param type
	 * @param id
	 * @param severity
	 */
	public void addTempAlarmForGis(@Param(value = "type") int type, @Param(value = "id") int id, @Param(value = "severity") int severity);
	
	public void addTempAlarmListForGis(@Param(value = "list")  List<Map<String, Integer>> list);
	
	public void updateTempAlarmForGis(@Param(value = "type") int type, @Param(value = "id") int id, @Param(value = "severity") int severity);
	
	/**
	 * 获取给gis地图缓存的告警
	 * @return
	 */
	public Map<String, Object> getTempAlarmForGis(@Param(value = "type") int type, @Param(value = "id") int id);
	
	/**
	 * 通过ptpId获取cableId
	 * @param ptpId
	 * @return
	 */
	public List<Integer> getCableIdByPtpId(@Param(value = "ptpId") int ptpId);
	
	/**
	 * 判断给地图模块准备的临时表是否存在
	 * @param tableName
	 * @param schemaName
	 * @return
	 */
	public int isTempTableExistForGis(@Param(value = "tableName") String tableName,
			@Param(value = "schemaName") String schemaName);
	
	/**
	 * 删除地图临时表
	 */
	public void dropGisTempTable();
	
	/**
	 * 创建地图临时表
	 */
	public void createGisTempTable();
}
