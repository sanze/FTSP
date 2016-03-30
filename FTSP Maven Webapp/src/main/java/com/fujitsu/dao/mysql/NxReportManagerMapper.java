package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.fujitsu.dao.mysql.bean.ResourceUnitManageRelUnit;
import com.fujitsu.dao.mysql.bean.ResourceUnitManager;

public interface NxReportManagerMapper {

	//--------------------------THJ-----------------------------
	/**
	 * 获取板卡接口信息
	 * @param paramMap
	 * @return
	 */
	Map getUnitInterface(@Param(value = "paramMap") Map<String, String> paramMap);
	/**
	 * 获取网元下的板卡
	 * @param paramMap
	 * @return
	 */
	List<Map> getPtpByUnitId(@Param(value = "unitId") String unitId);
	/**
	 * 获取网元下的板卡
	 * @param paramMap
	 * @return
	 */
	List<Map> getUsedPtpInfo(@Param(value = "paramMap") Map<String, String> paramMap);
	/**
	 * 保存波分方向
	 * @param paramMap
	 * @param idMap
	 */
	void saveUnitInterface(@Param(value = "paramMap") Map<String, String> paramMap, 
			@Param(value = "idMap")Map idMap);
	/**
	 * 保存波分方向
	 * @param paramMap
	 * @param idMap
	 */
	void saveUnitInterfacePtp(@Param(value = "paramMap") Map<String, String> paramMap, 
			@Param(value = "idMap")Map idMap);
	/**
	 * 删除板卡关联的ptp信息
	 * @param paramMap
	 * @param idMap
	 */
	void delUnitInterfacePtp(@Param(value = "paramMap") Map<String, String> paramMap);

	/**
	 * 判断板卡接口是否存在
	 * @param paramMap
	 * @return
	 */
	int isUnitInterfaceExist(@Param(value = "paramMap") Map<String, String> paramMap);

	/**
	 * 根据NE ID范围查找板卡接口
	 * @param neList
	 * @return
	 */
	List<Map> searchUnitInterfaceByNeList(@Param(value = "neList") List<Integer> neList,
			@Param(value = "paramMap") Map<String, String> paramMap,
			@Param(value = "start") int start,
			@Param(value = "limit") int limit);

	/**
	 * 查找板卡接口
	 * @param neList
	 * @return
	 */
	Integer searchUnitInterfaceByNeListCount(@Param(value = "neList") List<Integer> neList,
			@Param(value = "paramMap") Map<String, String> paramMap);
	/**
	 * 删除板卡接口
	 * @param paramMap
	 */
	void deleteUnitInterface(@Param(value = "paramMap") Map<String, String> paramMap);


	void clearUnitInterfaceInfo(@Param(value = "paramMap") Map<String, String> paramMap);
	

	void relateOpticalStandardValue(@Param(value = "paramMap") Map<String, String> paramMap);
	/**
	 * 修改波分方向
	 * @param paramMap
	 */
	void updateUnitInterface(@Param(value = "paramMap") Map<String, String> paramMap);
	Map getWaveDirInfo(@Param(value = "waveId") String waveId);
	List<Map> selectIn(@Param(value = "TABLE_NAME") String tableName,
			@Param(value = "KEY") String key,
			@Param(value = "neList") List<Integer> neList);
	List<Map> selectEq(@Param(value = "TABLE_NAME") String tableName,
			@Param(value = "KEY") String key,
			@Param(value = "VALUE") String value);
	List<Map> searchWaveTransOUT_BasePtp(@Param(value = "waveId") String waveId);
	List<Map> searchWaveTransOUT_PM(@Param(value = "ptpList") List<String> ptpList,
            @Param(value = "paramMap") Map<String, String> paramMap);
	/**
	 * 保存波分方向
	 * @param paramMap
	 * @param idMap
	 */
	void saveOptSwitch(@Param(value = "paramMap") Map<String, String> paramMap, 
			@Param(value = "idMap")Map idMap);
	/**
	 * 保存波分方向
	 * @param paramMap
	 * @param idMap
	 */
	void saveOptSwitchPtp(@Param(value = "paramMap") Map<String, String> paramMap, 
			@Param(value = "idMap")Map idMap);
	/**
	 * 判断光开关是否存在
	 * @param paramMap
	 * @return
	 */
	int isOptSwitchExist(@Param(value = "paramMap") Map<String, String> paramMap);
	/**
	 * 根据ptpId查询业务板卡信息
	 * @param ptpId
	 * @return
	 */
	List<Map> getBusinessPtpInfo(@Param(value = "ptpId") String ptpId);
	/**
	 * 获取保存的业务板卡信息
	 * @param paramMap
	 * @return
	 */
	List<Map> getSavedBusinessPtpInfo(@Param(value = "paramMap") Map<String, String> paramMap);

	
	//--------------------------THJ-----------------------------
	
	//--------------------------WSS-----------------------------
	
	/**
	 * 获取网元下的板卡
	 * @param paramMap
	 * @return
	 */
	List<Map> getUnitByNeOrWaveDirId(@Param(value = "paramMap") Map<String, String> paramMap);

	/**
	 * 保存波分方向
	 * @param paramMap
	 * @param idMap
	 */
	void saveWaveDir(@Param(value = "paramMap") Map<String, String> paramMap, 
			@Param(value = "idMap")Map idMap);
	
	/**
	 * 判断同一个网元下是否有重名方向
	 * @param paramMap
	 * @return
	 */
	int isDirNameExist(@Param(value = "paramMap") Map<String, String> paramMap);

	/**
	 * 保存方向中的板卡信息
	 * @param idMap
	 */
	void saveUnitInfo(@Param(value = "unit") Map unit);

	/**
	 * 根据ne范围查找方向
	 * @param neList
	 * @return
	 */
	List<Map> searchWaveDirByNeList(@Param(value = "neList") List<Integer> neList,
			@Param(value = "start") int start,
			@Param(value = "limit") int limit);
	/**
	 * 根据ne范围查找方向
	 * @param neList
	 * @return
	 */
	Integer searchWaveDirByNeListCount(@Param(value = "neList") List<Integer> neList);

	/**
	 * 修改波分方向
	 * @param paramMap
	 */
	void updateWaveDir(@Param(value = "paramMap") Map<String, String> paramMap);

	/**
	 * 清除掉板卡上的方向信息
	 * @param paramMap
	 */
	void clearUnitDirInfo(@Param(value = "paramMap") Map<String, String> paramMap);

	/**
	 * 删除波分方向
	 * @param paramMap
	 */
	void deleteWaveDir(@Param(value = "paramMap") Map<String, String> paramMap);

	/**
	 * 保存报表任务-task表
	 * @param paramMap
	 * @param sysUserId
	 * @param idMap
	 */
	void saveReportSysTask(
			@Param(value = "paramMap") Map<String, String> paramMap, 
			@Param(value = "sysUserId") Integer sysUserId,
			@Param(value = "idMap") Map idMap);

	/**
	 * 保存报表任务-taskInfo表
	 * @param waveDirList
	 * @param idMap
	 */
	void saveReportSysTaskInfo(@Param(value = "targetList") List<Map> targetList, @Param(value = "idMap") Map idMap);

	/**
	 * 保存报表任务-reportParam表
	 * @param paramMap
	 * @param idMap
	 */
	void saveReportTaskParam(@Param(value = "paramMap") Map<String, String> paramMap,@Param(value = "idMap") Map idMap);
	
	/**
	 * 查询任务名称下拉框数据
	 * @param searchCond
	 * @return
	 */
	public List<Map> getTaskNameComboValue(
			@Param(value = "paramMap") Map<String, String> paramMap);

	/**
	 * 任务名combo（按权限）
	 * 
	 * @param searchCond
	 * @param rEPORT_DEFINE
	 * @param userGrps
	 * @return
	 */
	public List<Map> getTaskNameComboValuePrivilege(
			@Param(value = "paramMap") Map<String, String> paramMap,
			@Param(value = "Define") Map<String, Object> Define,
			@Param(value = "userGrps") List<Map> userGrps,
			@Param(value = "userId") Integer userId);
	/**
	 * 查询报表任务
	 * @param paramMap
	 * @param start
	 * @param limit
	 * @return
	 */
	List<Map> searchReportTask(@Param(value = "paramMap") Map<String, String> paramMap, 
			@Param(value = "start")int start,
			@Param(value = "limit")int limit);

	/**
	 * 查询报表任务(数量)
	 * @param paramMap
	 * @return
	 */
	int searchReportTaskCount(@Param(value = "paramMap") Map<String, String> paramMap);

	/**
	 * 查询任务主要信息
	 * @param paramMap
	 * @return
	 */
	public List<Map<String, Object>> searchTaskInfoForEdit(
			@Param(value = "paramMap") Map<String, String> paramMap);
	
	/**
	 * 根据ID查询波分方向（报表用）
	 * @param nodeList
	 * @return
	 */
	List<Map> searchWaveDirById(@Param(value = "nodeList") List<Map> nodeList);

	/**
	 * 更新report param表
	 * @param paramMap
	 */
	void updateReportTaskParam(@Param(value = "paramMap") Map<String, String> paramMap);
	
	/**
	 * 把IS_DEL设为false
	 * @param targetList
	 */
	void setIsDelFalseUnitManage(@Param(value = "targetList") List<Map> targetList);
	/**
	 * 把IS_DEL设为false
	 * @param targetList
	 */
	void setIsDelFalseUnitManageRel(@Param(value = "targetList") List<Map> targetList);
	
	/**
	 * 网管节点的信息补完
	 * 
	 * @param conditionMap
	 * @param regularPmAnalysisDefine
	 * @param TREE_DEFINE
	 * @return
	 */
	public List<Map<String, String>> getEmsNodeInfo(
			@Param(value = "conditionMap") Map<String, String> conditionMap);

	/**
	 * 子网节点的信息补完
	 * 
	 * @param conditionMap
	 * @param regularPmAnalysisDefine
	 * @param TREE_DEFINE
	 * @return
	 */
	public List<Map<String, String>> getSubnetNodeInfo(
			@Param(value = "conditionMap") Map<String, String> conditionMap);

	/**
	 * 网元节点的信息补完
	 * 
	 * @param conditionMap
	 * @param regularPmAnalysisDefine
	 * @param TREE_DEFINE
	 * @return
	 */
	public List<Map<String, String>> getNeNodeInfo(
			@Param(value = "conditionMap") Map<String, String> conditionMap);
	
	/**
	 * 查询task_info里的target ID list
	 * 
	 * @param conditionMap
	 * @param regularPmAnalysisDefine
	 * @param TREE_DEFINE
	 * @return
	 */
	public List<Integer> getTaskTargetIdList(
			@Param(value = "taskId") int taskId);
	/**
	 * 删除
	 * 
	 * @return
	 */
	public void deleteRecordByKey(
			@Param(value = "tableName") String tableName,
			@Param(value = "keyName") String keyName,
			@Param(value = "keyList") List<Integer> keyList);
	
	public List<Map> getCreatorComboValuePrivilege(
			@Param(value = "userGrps") List<Map> userGrps, 
			@Param(value = "Define") Map<String, Object> Define);
	
	/**
	 * @param ptpList ptpId
	 * @return [{emsIs,ptpId}]
	 */
	public List<Map> getPtpIdsWithEmsId(
			@Param(value = "ptpList") List<Integer> ptpList);
	
	/**
	 * @param tableList [{tableName:xxx,tableNodes:1,2,3,4}]
	 * @param date xxxx-xx-xx
	 * @return
	 */
	List<Map> getPmDataForPTN_IPRAN(
			@Param(value = "tableList") List<Map<String,String>> tableList,
			@Param(value = "date") String date,
			@Param(value = "pmStdIndex") String pmStdIndex);
	
	
	/**
	 * @param ptpList Array of id
	 * @return MAX_IN,MIN_IN,PTP_ID,DISPLAY_NE,DISPLAY_SUBNET,PORT_DESC
	 */
	List<Map> getOptStdDataForPTN_IPRAN(@Param(value = "ptpList") List<Integer> ptpList);

	
	/**
	 * 根据一个ptplist当做A端，去查找link的Z端
	 * @param ptpList ptpIds
	 * @return
	 */
	List<Map> getLinkByAEnd(@Param(value = "ptpList") List<Integer> ptpList);
	
	
	/**
	 * 保存PTN系统信息
	 * @param paramMap
	 * @param idMap
	 */
	void savePtnSys(@Param(value = "paramMap") Map<String, String> paramMap,
			@Param(value = "idMap") Map idMap);
	
	
	/**
	 * 判断系统名称是否重复
	 * @param paramMap
	 * @return
	 */
	int isPtnSysNameExist(
			@Param(value = "paramMap") Map<String, String> paramMap);
	
	
	/**
	 * 保存系统的端口信息
	 * @param intList
	 * @param idMap
	 */
	void savePtnSysPorts(@Param(value = "ptpList") List<Integer> intList,
			@Param(value = "idMap") Map idMap);
	
	/**
	 * 查找PTN系统列表
	 * @param paramMap
	 * @param targetIdList TODO
	 * @return
	 */
	List<Map> getPtnSysList(
			@Param(value = "paramMap") Map<String, String> paramMap,
			@Param(value = "targetIdList") List<Integer> targetIdList,
			@Param(value = "start") int start, 
			@Param(value = "limit") int limit);
	/**
	 * 查找PTN系统列表COUNT
	 * @param paramMap
	 * @return
	 */
	int getPtnSysListCount(
			@Param(value = "paramMap") Map<String, String> paramMap);

	

	
	/**
	 * 删除系统.对于报表任务的处理待定
	 * @param paramMap
	 */
	void deletePtnSys(@Param(value = "paramMap") Map<String, String> paramMap);
	
	
	/**
	 * 查找报表用峰值统计值
	 * @param targetIds
	 * @param date
	 * @return
	 */
	List<Map> getDataForPTN_FlowPeak(
			@Param(value = "targetIds") List<Integer> targetIds,
			@Param(value = "date") String date);

	/**
	 * 查找预览用系统信息
	 * @param targetIds
	 * @return
	 */
	List<Map> getSysDataForPreview(@Param(value = "targetIds") List<Integer> targetIds);
	//--------------------------WSS-----------------------------
	//--------------------------DHJ-----------------------------
	/**
	 * 根据id查询网元信息,当loadDetailInfo为TRUE时,查询网元下单元盘信息
	 * @param manageId	id
	 * @param loadUnitInfo	是否加载板卡详细信息
	 * @return ResourceUnitManager 查询成功返回ResourceUnitManager对象,否则为null
	 */
	public ResourceUnitManager getUnitInfoByUnitManageId(@Param("manageId") int manageId,@Param("type")int type,@Param("isLoad")boolean loadUnitInfo);
	
	/**
	 * 根据任务id查询网元信息,当loadDetailInfo为TRUE时,查询网元下单元盘信息
	 * @param taskId 任务id
	 * @param loadUnitInfo 是否加载板卡详细信息
	 * @return ReportTask 查询成功返回ReportTask对象,否则为null
	 */
	public List<ResourceUnitManager> getManageInfoByTaskId(@Param("taskId") int taskId,@Param("type")int type,@Param("isLoad")boolean loadUnitInfo);
	/**
	 * 删除报表任务
	 * @param taskId
	 */
	public void deleteTaskByTaskId(@Param("taskId")int taskId);
	/**
	 * 从报表任务中删除光放大器选择窗口选择的条目,同时级联删除选择的板卡信息
	 * @param manageId
	 */
	public void deleteUnitManageByManageId(@Param("manageIdList") List<Integer> manageIdList);
	/**
	 * 从t_resource_unit_manage_rel_unit表中删除单元盘记录
	 * @param manageId
	 * @param unitId
	 */
	public void deleteUnitInfo(@Param("manageId")int manageId,@Param("unitId")int unitId);
	/**
	 * 插入unit记录
	 * @param unit
	 */
	public void insertUnitInfo(@Param("unit")ResourceUnitManageRelUnit unit);
	/**
	 * 插入manage记录
	 * @param manage
	 */
	public void insertManageInfo(@Param("manage")ResourceUnitManager manage);
	/**
	 * 根据板卡id获取端口信息
	 * @param unitId
	 * @return
	 */
	List<Map> getPortByUnitId(@Param("unitId")int unitId);
	/**
	 * 根据factoryId获取设备型号信息(不包括sdh)
	 * @param factoryId
	 * @return
	 */
	List<Map> getProductNameByFactoryIdNoSDH(@Param("factoryId")int factoryId);
	/**
	 * 根据unitId获取节点详细信息
	 * @param unitIds
	 * @return
	 */
	List<Map> getNodeInfoByUnitId(@Param("unitIds")List<Integer> unitIds);
	//--------------------------DHJ-----------------------------
	

	public void updateManageInfo(@Param("manage")ResourceUnitManager manage);
	
	
	
	//--------------------------MeiK START-----------------------------
	
	public Map<String, Object> getBaseInfoForAmp(@Param(value = "targetId") int targetId);
	
	public List<Map<String, Object>> getUnitInfoForAmp(@Param(value = "targetId") int targetId);
	
	public List<Map<String, Object>> getTranPMDataForAMP(@Param(value = "ptpIds") List<Integer> ptpIds, @Param(value = "paramMap") Map<String, String> paramMap);
	
	public List<Map<String, Object>> getRecvPMDataForAMP(@Param(value = "ptpIds") List<Integer> ptpIds, @Param(value = "paramMap") Map<String, String> paramMap);
	
	public List<Map<String, Object>> getBaseInfoListForSwitch(@Param(value = "targetIds") List<Integer> targetIds);
	
	public List<Map<String, Object>> getUnitInfoListForSwitch(@Param(value = "targetIds") List<Integer> targetIds);
	
	public List<Map<String, Object>> getProUnitInfoListForSwitch(@Param(value = "unitIds") List<Integer> unitIds);
	
	public List<Map<String, Object>> getPMDataForSwitchReport(@Param(value = "ptpIds") List<Integer> ptpIds, @Param(value = "paramMap") Map<String, String> paramMap);
	
	public List<Map<String, Object>> getProGrpCountList(@Param(value = "unitIds") List<Integer> unitIds);
	
	public List<Map<String, Object>> getBaseInfoListForWave(@Param(value = "targetIds") List<Integer> targetIds);
	
	public List<Map<String, Object>> getUnitInfoListForWave(@Param(value = "targetIds") List<Integer> targetIds, @Param(value = "unitType") int unitType);
	
	public List<Map<String, Object>> getPMDataForWaveJoinReport(@Param(value = "ptpIds") List<Integer> ptpIds, @Param(value = "paramMap") Map<String, String> paramMap);
	
	public List<Map<String, Object>> getLastMonthPMDataForWaveJoinReport(@Param(value = "ptpIds") List<Integer> ptpIds, @Param(value = "paramMap") Map<String, String> paramMap);
	
	public List<Map<String, Object>> getPMDataForWaveDivReport(@Param(value = "ptpIds") List<Integer> ptpIds, @Param(value = "paramMap") Map<String, String> paramMap);
	
	public List<Map<String, Object>> getLastMonthPMDataForWaveDivReport(@Param(value = "ptpIds") List<Integer> ptpIds, @Param(value = "paramMap") Map<String, String> paramMap);
	
	//--------------------------MeiK END-----------------------------
	//获取所有ptn系统信息
	public List<Map> selectAllPtnSysInfo();
	
	/**
	 * 获取系统内link
	 * @param paramMap
	 * @return
	 */
	public List<Map> getLinksBySysId(@Param(value = "paramMap") Map<String, String> paramMap);
	
}
