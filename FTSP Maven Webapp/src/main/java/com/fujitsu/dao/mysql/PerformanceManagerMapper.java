package com.fujitsu.dao.mysql;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/**
 * @author ZhongLe
 * 
 */
public interface PerformanceManagerMapper {
	/**
	 * 获取所有网管分组
	 * 
	 */
	public List<Map> getBaseEmsGroups();

	public int getEmsCountForIndex(@Param(value = "emses") String emses,
			@Param(value = "Define") Map Define,
			@Param(value = "taskStatus") Integer taskStatus,
			@Param(value = "result") String result);

	/**
	 * 分页获取网管
	 * 
	 * @param emsGroupId
	 *            分组ID
	 * @param startNumber
	 * @param pageSize
	 * @param Define
	 *            预定义常量
	 * @return BASE_EMS_GROUP_ID GROUP_NAME
	 */
	public List<Map> getEmsList(
			@Param(value = "emsGroupId") Integer emsGroupId,
			@Param(value = "startNumber") int startNumber,
			@Param(value = "pageSize") int pageSize,
			@Param(value = "thisDefine") Map thisDefine,
			@Param(value = "userId") int userId,
			@Param(value = "Define") Map Define);

	/**
	 * 获取网管总数
	 * 
	 * @param emsGroupId
	 *            分组ID
	 * @param Define
	 *            预定义常量
	 * @return 总数
	 */
	public int getEmsCount(@Param(value = "emsGroupId") Integer emsGroupId,
			@Param(value = "thisDefine") Map thisDefine,
			@Param(value = "userId") int userId,
			@Param(value = "Define") Map Define);

	/**
	 * 更新网管(t_base_ems_connection)信息
	 * 
	 * @param startTime
	 *            COLLEC_START_TIME
	 * @param endTime
	 *            COLLEC_END_TIME
	 * @param collectSource
	 *            COLLECT_SOURCE
	 * @param emsCoonId
	 *            BASE_EMS_CONNECTION_ID
	 */
	public void modifyEmsInfo(@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime,
			@Param(value = "collectSource") int collectSource,
			@Param(value = "emsCoonId") int emsCoonId);

	/**
	 * 获取网元型号
	 * 
	 * @param emsId
	 *            网管Id
	 * @param type
	 *            网元类型(SDH,WDM etc)
	 * @return
	 */
	public List<Map> getProductNames(@Param(value = "emsId") int emsId,
			@Param(value = "type") int type);

	/**
	 * 分页获取网元
	 * 
	 * @param emsId
	 *            网管ID
	 * @param type
	 *            网元类型(SDH,WDM etc)
	 * @param productName
	 *            网元型号
	 * @param startNumber
	 * @param pageSize
	 * @param Define
	 *            预定义常量
	 * @return EMS_DISPLAY_NAME BASE_NE_ID DISPLAY_NAME TYPE PRODUCT_NAME
	 *         NE_LEVEL COLLECT_NUMBIC COLLECT_PHYSICAL COLLECT_CTP
	 */
	public List<Map> getNeList(@Param(value = "emsId") int emsId,
			@Param(value = "type") int type,
			@Param(value = "productName") String productName,
			@Param(value = "subIds") String subIds,
			@Param(value = "startNumber") int startNumber,
			@Param(value = "pageSize") int pageSize,
			@Param(value = "Define") Map Define);
	
	public List<Map> getSubIds(@Param(value = "subIds") String subIds);
	/**
	 * 获取网元总数
	 * 
	 * @param emsId
	 *            网管ID
	 * @param type
	 *            网元类型(SDH,WDM etc)
	 * @param productName
	 *            网元型号
	 * @param Define
	 *            预定义常量
	 * @return
	 */
	public int getNeCount(@Param(value = "emsId") int emsId,
			@Param(value = "type") int type,
			@Param(value = "productName") String productName,
			@Param(value = "subIds") String subIds,
			@Param(value = "Define") Map Define);

	/**
	 * 更新网元信息
	 * 
	 * @param neId
	 *            BASE_NE_ID
	 * @param neLevel
	 *            NE_LEVEL
	 * @param collectNumbic
	 *            COLLECT_NUMBIC
	 * @param collectPhysical
	 *            COLLECT_PHYSICAL
	 * @param collectCtp
	 *            COLLECT_CTP
	 */
	public void modifyNeInfo(@Param(value = "neId") int neId,
			@Param(value = "neLevel") int neLevel,
			@Param(value = "collectNumbic") int collectNumbic,
			@Param(value = "collectPhysical") int collectPhysical,
			@Param(value = "collectCtp") int collectCtp);

	/**
	 * 分页获取网元状态
	 * 
	 * @param emsId
	 *            网管ID
	 * @param type
	 *            网元类型(SDH,WDM etc)
	 * @param productName
	 *            网元型号
	 * @param startNumber
	 * @param pageSize
	 * @param Define
	 *            预定义常量
	 * @return EMS_DISPLAY_NAME BASE_NE_ID DISPLAY_NAME TYPE PRODUCT_NAME
	 *         NE_LEVEL LAST_COLLECT_TIME COLLECT_INTERVAL COLLECT_RESULT
	 */
	public List<Map> getNeStateList(
			@Param(value="neList") List<Map> neList,
			@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime,
			@Param(value = "startNumber") int startNumber,
			@Param(value = "pageSize") int pageSize,
			@Param(value = "Define") Map Define);

	/**
	 * 获取网元总数
	 * 
	 * @param emsId
	 *            网管ID
	 * @param type
	 *            网元类型(SDH,WDM etc)
	 * @param productName
	 *            网元型号
	 * @param Define
	 *            预定义常量
	 * @return
	 */
	public int getNeStateCount(
			@Param(value="neList") List<Map> neList,
			@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime,
			@Param(value = "Define") Map Define);

	/**
	 *  查询neList对应的采集记录
	 * @param neList
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public List<Map> getNeStateListMulti(
			@Param(value="neList") List<Map> neList,
			@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime);
	/**
	 * 获取网管采集状态
	 * 
	 * @param emsId
	 * @return
	 */
	public Integer getTaskCollectStatus(@Param(value = "taskId") int taskId);

	/**
	 * 获取网管采集状态
	 * 
	 * @param emsId
	 * @return
	 */
	public Integer getTaskCollectResult(@Param(value = "taskId") int taskId);

	/**
	 * 从网管ID获取任务ID
	 * 
	 * @param taskId
	 * @param Define
	 * @return
	 */
	public Integer getTaskIdFromEmsId(@Param(value = "emsId") int emsId,
			@Param(value = "Define") Map Define);

	/**
	 * 获取网管采集信息
	 * 
	 * @param emsId
	 * @param Define
	 * @return COLLEC_END_TIME COLLECT_SOURCE
	 */
	public List<Map> getEmsCollectInfo(@Param(value = "emsId") int emsId,
			@Param(value = "defineTrue") int defineTrue);

	/**
	 * 获取网元采集列表
	 * 
	 * @param emsId
	 * @param neLevel
	 * @param Define
	 * @return BASE_NE_ID COLLECT_NUMBIC COLLECT_PHYSICAL COLLECT_CTP
	 */
	public List<Map> getNeCollectList(@Param(value = "emsId") int emsId,
			@Param(value = "neLevel") int neLevel,
			@Param(value = "defineTrue") int defineTrue);

	/**
	 * 第二次获取网元采集列表
	 * 
	 * @param belongToDate
	 * @param emsId
	 * @param neLevel
	 * @param defineFalse
	 * @param defineTrue
	 * @return
	 */
	public List<Map> getNeCollectListTwice(
			@Param(value = "belongToDate") String belongToDate,
			@Param(value = "emsId") int emsId,
			@Param(value = "neLevel") int neLevel,
			@Param(value = "defineFalse") int defineFalse,
			@Param(value = "defineTrue") int defineTrue);

	/**
	 * 获取网元暂停时间
	 * 
	 * @param emsId
	 * @return
	 */
	public String getTaskForbiddenTime(@Param(value = "taskId") int taskId);

	/**
	 * 将某网管下所有网元采集状态清空
	 * 
	 * @param emsId
	 */
	public void updateAllNeCollectResult(@Param(value = "emsId") int emsId);

	/**
	 * 更新网元性能同步状态
	 * 
	 * @param neId
	 * @param collectResult
	 * @param collectInterval
	 * @param lastCollectTime
	 */
	public void updateNeCollectInfo(@Param(value = "neId") int neId,
			@Param(value = "collectResult") String collectResult,
			@Param(value = "collectInterval") Integer collectInterval,
			@Param(value = "lastCollectTime") Timestamp lastCollectTime);

	/**
	 * 插入任务计数信息
	 * 
	 * @param taskInfo
	 *            包含`TASK_ID`, `EMS_CONNECTION_ID`, `NE_ID`, `ACTION_RESULT`,
	 *            `BELONG_TO_DATE`, `FAILED_REASON`
	 */
	public void insertTaskCountInfo(@Param(value = "taskId") int taskId,
			@Param(value = "emsId") int emsId,
			@Param(value = "taskInfo") Map<String, Object> taskInfo);

	/**
	 * 更新任务计数信息
	 * 
	 * @param taskInfo
	 *            包含`ACTION_RESULT`,`FAILED_REASON`,`TASK_COUNT_INFO_ID`
	 */
	public void updateTaskCountInfo(
			@Param(value = "taskInfo") Map<String, Object> taskInfo);

	/**
	 * 更新网管任务状态
	 * 
	 * @param emsId
	 * @param collectStatus
	 */
	public void updateEmsJobStatus(@Param(value = "taskId") int taskId,
			@Param(value = "taskStatus") int taskStatus);

	/**
	 * 更新网管采集状态与结果
	 * 
	 * @param emsId
	 * @param result
	 */
	public void updateEmsCollectStatus(@Param(value = "taskId") int taskId,
			@Param(value = "result") int result);

	/**
	 * 更新网管暂停时间
	 * 
	 * @param emsId
	 * @param forbidenTimeLimit
	 */
	public void insertTaskForbiddenTime(@Param(value = "taskId") int taskId,
			@Param(value = "forbidenTimeLimit") String forbidenTimeLimit);

	/**
	 * 删除指定任务暂停时间
	 * 
	 * @param taskId
	 */
	public void deleteTaskForbiddenTime(@Param(value = "taskId") int taskId);

	/**
	 * 若表不存在,创建性能表
	 * 
	 * @param tableName
	 */
	public void createPmTableIfNotExist(
			@Param(value = "tableName") String tableName);

	/**
	 * 更新任务的始末时间和下次执行时间
	 * 
	 * @param startTime
	 * @param endTime
	 * @param nextTime
	 * @param taskId
	 */
	public void updateTaskTime(@Param(value = "startTime") Timestamp startTime,
			@Param(value = "endTime") Timestamp endTime,
			@Param(value = "nextTime") Timestamp nextTime,
			@Param(value = "taskId") int taskId);

	/**
	 * 获取任务上次执行时间
	 * 
	 * @param taskId
	 * @return
	 */
	public Timestamp getTaskLastStartTime(@Param(value = "taskId") int taskId);

	/**
	 * 获取某一采集结果的网元计数
	 * 
	 * @param date
	 * @param taskId
	 * @param result
	 * @return
	 */
	public Integer getNeResultCount(@Param(value = "date") Timestamp date,
			@Param(value = "result") int result,
			@Param(value = "taskId") int taskId);

	/**
	 * 插入任务
	 * 
	 * @param task
	 * @param Define
	 */
	public void insertTask(@Param(value = "task") Map task,
			@Param(value = "Define") Map Define);

	/**
	 * 插入任务目标
	 * 
	 * @param task
	 * @param Define
	 */
	public void insertTaskTarget(@Param(value = "task") Map task,
			@Param(value = "Define") Map Define);

	/**
	 * 删除任务
	 * 
	 * @param taskId
	 */
	public void deleteTask(@Param(value = "taskId") int taskId);

	/**
	 * 删除任务目标
	 * 
	 * @param taskId
	 */
	public void deleteTaskTarget(@Param(value = "taskId") int taskId);

	/**
	 * 获取性能标准参数
	 * 
	 * @param pmStdIndexIdString
	 * @return
	 */
	public List<String> getPmStdIndexes(
			@Param(value = "pmStdIndexIdString") String pmStdIndexIdString,
			@Param(value = "maxMin") boolean maxMin);

	/**
	 * 获取性能标准参数
	 * 
	 * @param pmStdIndexIdString
	 * @return
	 */
	public List<String> getPmStdIndexes_new(
			@Param(value = "pmStdIndexIds") List<String> pmStdIndexIds,
			@Param(value = "maxMin") boolean maxMin);


	/**
	 * 获取ptpId
	 * 
	 * @param conditionMap
	 *            包含各级节点ID及网元类型
	 * @return BASE_PTP_ID,BASE_EMS_CONNECTION_ID
	 */
	public List<Map> getPtpId(@Param(value = "conditionMap") Map conditionMap,
			@Param(value = "Define") Map Define);

	/**
	 * 获取UnitId
	 * 
	 * @param conditionMap
	 *            包含各级节点ID及网元类型
	 * @return BASE_PTP_ID,BASE_EMS_CONNECTION_ID
	 */
	public List<Map> getUnitId(@Param(value = "conditionMap") Map conditionMap,
			@Param(value = "Define") Map Define);

	/**
	 * 获取neId
	 * 
	 * @param conditionMap
	 *            包含各级节点ID及网元类型
	 * @return BASE_PTP_ID,BASE_EMS_CONNECTION_ID
	 */
	public List<Map> getNeId(@Param(value = "conditionMap") Map conditionMap,
			@Param(value = "Define") Map Define);

	/**
	 * 获取网元类型
	 * 
	 * @param conditionMap
	 * @param Define
	 * @return
	 */
	public List<Integer> getPmSearchNeType(
			@Param(value = "conditionMap") Map conditionMap,
			@Param(value = "Define") Map Define);

	/**
	 * 插入当前性能
	 * 
	 * @param mapList
	 *            包含所有列的Map列表 (SEARCH_TAG)
	 */
	public void insertCurrentTempPm(
			@Param(value = "mapList") List<Map<String, Object>> mapList,
			@Param(value = "toTableName") String toTableName);

	/**
	 * 查询并插入历史性能
	 * 
	 * @param userId
	 *            当前用户名
	 * @param fromTableName
	 *            来源表
	 * @param conditionMap
	 */
	public void insertHistoryTempPm(@Param(value = "userId") int userId,
			@Param(value = "fromTableName") String fromTableName,
			@Param(value = "toTableName") String toTableName,
			@Param(value = "searchTag") int searchTag,
			@Param(value = "conditionMap") Map<String, Object> conditionMap);

	/**
	 * 删除该用户名下所有临时性能值
	 * 
	 * @param tableName
	 *            表名
	 * @param userId
	 *            用户ID
	 */
	public void deleteTempPm(@Param(value = "tableName") String tableName,
							@Param(value = "userId") int userId,
							@Param(value = "searchType") int searchType);

	/**
	 * 删除该用户名下所有临时性能值
	 * 
	 * @param tableName
	 *            表名
	 */
	public void deleteTempPmForInit(@Param(value = "tableName") String tableName);

	/**
	 * 从临时表中获取性能值
	 * 
	 * @param tableName
	 *            表名
	 * @param exception
	 *            是否异常数据
	 * @param userId
	 *            用户ID
	 * @param startNumber
	 * @param pageSize
	 * @return 所有列
	 */
	public List<Map> getTempPmList(
			@Param(value = "tableName") String tableName,
			@Param(value = "exception") int exception,
			@Param(value = "userId") int userId,
			@Param(value = "searchTag") Integer searchTag,
			@Param(value = "startNumber") int startNumber,
			@Param(value = "pageSize") int pageSize);

	/**
	 * 临时表中性能记录总数
	 * 
	 * @param tableName
	 *            表名
	 * @param exception
	 *            是否异常数据
	 * @param userId
	 *            用户ID
	 * @return
	 */
	public int getTempPmCount(@Param(value = "tableName") String tableName,
			@Param(value = "exception") int exception,
			@Param(value = "userId") int userId,
			@Param(value = "searchTag") int searchTag);

	/**
	 * 获取一个网管分组下所有网管对象Id
	 * 
	 * @param emsGroupId
	 * @return
	 */
	public List<Integer> getEmsIdsFromEmsGroupId(
			@Param(value = "emsGroupId") int emsGroupId);

	/**
	 * 查询该子网下所有子网
	 * 
	 * @param parentSubnetId
	 *            父子网ID
	 * @return 子子网ID集合
	 */
	public List<Integer> getSubnetList(
			@Param(value = "parentSubnetId") int parentSubnetId);

	/**
	 * 当前性能的模板信息
	 * 
	 * @param templateId
	 * @param pmStdIndex
	 * @param domain
	 * @return
	 */
	public Map getCurrentPmTempleteInfo(
			@Param(value = "templateId") int templateId,
			@Param(value = "pmStdIndex") String pmStdIndex,
			@Param(value = "domain") int domain);

	/**
	 * 判断原始性能表是否存在
	 * 
	 * @param tableName
	 * @return
	 */
	public Integer getPmTableExistance(
			@Param(value = "tableName") String tableName,
			@Param(value = "schemaName") String schemaName);

	/**
	 * 分页获取比较值列表
	 * 
	 * @param conditionMap
	 * @param startNumber
	 * @param pageSize
	 * @return
	 */
	public List<Map> getCompareValueList(
			@Param(value = "conditionMap") Map<String, String> conditionMap,
			@Param(value = "startNumber") int startNumber,
			@Param(value = "pageSize") int pageSize);

	/**
	 * 获取比较值统计数
	 * 
	 * @param conditionMap
	 * @return
	 */
	public int getCompareValueListCount(
			@Param(value = "conditionMap") Map<String, String> conditionMap);

	/**
	 * 插入比较值
	 * 
	 * @param mapList
	 */
	public void insertPmCompare(
			@Param(value = "mapList") List<Map<String, Object>> mapList);

	/**
	 * 更新比较值
	 * 
	 * @param mapList
	 *            PM_COMPARE_ID必填
	 */
	public void modifyPmCompare(@Param(value = "map") Map<String, Object> map);

	/**
	 * 获取一条比较值
	 * 
	 * @param map
	 *            包含TARGET_TYPE PM_STD_INDEX 可能包含BASE_OTN_CTP_ID BASE_SDH_CTP_ID
	 *            BASE_PTP_ID BASE_UNIT_ID
	 * @return
	 */
	public List<Map> getCompareValue(
			@Param(value = "map") Map<String, Object> map);

	/**
	 * 由节点获取neId
	 * 
	 * @param conditionMap
	 *            包含子网/网管/网元ID
	 * @return
	 */
	public List<Integer> getNeIdsFromNodes(
			@Param(value = "conditionMap") Map<String, String> conditionMap);

	/**
	 * 由网元ID获取显示名称
	 * 
	 * @param neId
	 * @return
	 */
	public String getNeDisplayName(@Param(value = "neId") Integer neId);

	/**
	 * 获取采集任务目标及目标类型
	 * 
	 * @param taskId
	 *            任务Id
	 * @return TARGET_Id TARGET_TYPE
	 */
	public List<Map> getTaskTargetIds(@Param(value = "taskId") int taskId);

	/**
	 * 获取采集任务目标网元列表
	 * 
	 * @param Define
	 * @param conditionMap
	 *            包含各级ID列表
	 * @return NE_ID BASE_EMS_CONNECTION_ID
	 */
	public List<Map> getTaskTargetNeIds(@Param(value = "Define") Map Define,
			@Param(value = "conditionMap") Map<String, String> conditionMap);

	/**
	 * 获取有某一采集结果的网元计数
	 * 
	 * @param neIdString
	 *            网元范围
	 * @param actionResult
	 *            采集结果
	 * @param currentDate
	 *            日期
	 * @return
	 */
	public List<Integer> getCollectResultNeIds(
			@Param(value = "neIdString") String neIdString,
			@Param(value = "actionResult") Integer actionResult,
			@Param(value = "currentDate") Timestamp currentDate);

	/**
	 * 获取网元采集结果计数
	 * 
	 * @param neIdString
	 *            网元ID列表
	 * @param currentDate
	 *            当前日期
	 * @return
	 */
	public Integer getCollectResultNeCount(
			@Param(value = "neIdString") String neIdString,
			@Param(value = "currentDate") Timestamp currentDate);

	/**
	 * 获取报表任务所需要的性能参数列表
	 * 
	 * @param taskId
	 * @return
	 */
	public Map getTaskPmIndexes(@Param(value = "taskId") Integer taskId);

	/**
	 * 分类获取性能计数
	 * 
	 * @param tableName
	 *            原始性能数据表名
	 * @param neIdString
	 *            网元ID列表
	 * @param exctptionLevel
	 *            异常等级
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Integer getTaskPmCount(@Param(value = "tableName") String tableName,
			@Param(value = "neIdString") String neIdString,
			@Param(value = "exctptionLevel") int exctptionLevel,
			@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime,
			@Param(value = "sdhPm") String sdhPm,
			@Param(value = "wdmPm") String wdmPm);

	/**
	 * 获取任务端口Id列表
	 * 
	 * @param multiSecId
	 *            复用段ID列表
	 * @return PTP_ID BASE_EMS_CONNECTION_ID
	 */
	public List<Map> getTaskTargetPtpIds(
			@Param(value = "multiSecId") String multiSecId);

	/**
	 * 获取输入、输出光功率对应端口列表
	 * 
	 * @param multiSecId
	 * @param pmType
	 * @return
	 */
	public List<Integer> getTaskTargetPtpIdsForPM(
			@Param(value = "multiSecId") String multiSecId,
			@Param(value = "pmType") int pmType);

	/**
	 * 获取某一种采集结果的端口Id
	 * 
	 * @param multiSecId
	 *            复用段ID
	 * @param actionResult
	 *            结果
	 * @param currentDate
	 *            日期
	 * @return PTP_ID
	 */
	public List<Integer> getCollectResultPtpIds(
			@Param(value = "multiSecId") String multiSecId,
			@Param(value = "ptpId") String ptpId,
			@Param(value = "actionResult") Integer actionResult,
			@Param(value = "currentDate") Timestamp currentDate);

	/**
	 * 获取端口采集结果计数
	 * 
	 * @param multiSecId
	 *            复用段ID
	 * @param currentDate
	 *            当前日期
	 * @return
	 */
	public Integer getCollectResultPtpCount(
			@Param(value = "multiSecId") String multiSecId,
			@Param(value = "ptpId") String ptpId,
			@Param(value = "currentDate") Timestamp currentDate);

	/**
	 * 分类获取复用段性能计数
	 * 
	 * @param tableName
	 *            原始性能数据表名
	 * @param ptpIdString
	 *            端口ID列表
	 * @param exctptionLevel
	 *            异常等级
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public Integer getMultiSecTaskPmCount(
			@Param(value = "tableName") String tableName,
			@Param(value = "ptpIdString") String ptpIdString,
			@Param(value = "exctptionLevel") int exctptionLevel,
			@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime,
			@Param(value = "pmStdIndex") String pmStdIndex);

	/**
	 * 获取采集失败复用段Id列表
	 * 
	 * @param multiSecId
	 *            复用段Id
	 * @param actionResult
	 *            结果
	 * @param currentDate
	 *            日期
	 * @return
	 */
	public List<Integer> getFailedMultiSecIds(
			@Param(value = "multiSecId") String multiSecId,
			@Param(value = "actionResult") Integer actionResult,
			@Param(value = "currentDate") Timestamp currentDate);

	/**
	 * 获取复用段ID列表
	 * 
	 * @param turnkLineIdString
	 *            干线ID列表
	 * @return
	 */
	public List<Integer> getMultiSecIds(
			@Param(value = "turnkLineIdString") String turnkLineIdString);

	/**
	 * 获取所有网管ID
	 * 
	 * @param defineFalse
	 * @return
	 */
	public Integer getCollectResultNeCountWithAuthority(
			@Param(value = "emsIds") String emsIds,
			@Param(value = "actionResult") Integer actionResult,
			@Param(value = "currentDate") Timestamp currentDate);

	// ***********************************咯咯咯咯咯咯***************************************
	/**
	 * 从txt文件load数据到数据库
	 * 
	 * @param filePath
	 */
	public void loadPmData(@Param(value = "filePath") String filePath,
			@Param(value = "tableName") String tableName);

	/**
	 * 加载性能模板的Store
	 * 
	 */
	public List<Map> getTemplates(@Param(value = "factory") int factory,
			@Param(value = "Define") Map Define);

	/**
	 * 查询模板详细信息
	 * 
	 */
	public List<Map> getTemplatesInfo(@Param(value = "factory") int factory,
			@Param(value = "Define") Map Define,
			@Param(value = "start") int start, @Param(value = "limit") int limit);

	/**
	 * 查询模板详细信息-count
	 * 
	 */
	public int getTemplatesInfoCount(@Param(value = "factory") int factory,
			@Param(value = "Define") Map Define);

	/**
	 * 查找所选节点的模板应用信息
	 * 
	 */
	public List<Map> searchPtpTemplate(
			@Param(value = "conditionMap") Map conditionMap,
			@Param(value = "Define") Map Define);

	/**
	 * 查找所选节点的模板应用信息的条目数
	 * 
	 */
	public int searchPtpTemplateCount(
			@Param(value = "conditionMap") Map conditionMap,
			@Param(value = "Define") Map Define);

	/**
	 * 查找所选节点的模板应用信息
	 * 
	 */
	public List<Map> searchUnitTemplate(
			@Param(value = "conditionMap") Map conditionMap,
			@Param(value = "Define") Map Define);

	/**
	 * 查找所选节点的模板应用信息的条目数
	 * 
	 */
	public int searchUnitTemplateCount(
			@Param(value = "conditionMap") Map conditionMap,
			@Param(value = "Define") Map Define);
	
	/**
	 * 保存节点的模板应用信息
	 * 
	 */
	public void saveUnitTemplate(@Param(value = "map") Map map);

	/**
	 * 保存节点的模板应用信息
	 * 
	 */
	public void savePtpTemplate(@Param(value = "map") Map map);

	/**
	 * 解除端口模板设置
	 * 
	 */
	public void cancelUnitTemplate(@Param(value = "unitId") Long unitId);

	/**
	 * 解除端口模板设置
	 * 
	 */
	public void cancelPtpTemplate(@Param(value = "ptpId") Long ptpId);

	/**
	 * 模板应用
	 * 
	 */
	public void applyTemplate(@Param(value = "conditionMap") Map conditionMap,
			@Param(value = "Define") Map Define);

	/**
	 * 模板应用于板卡
	 * 
	 */
	public void applyTemplateForUnit(@Param(value = "conditionMap") Map conditionMap,
			@Param(value = "Define") Map Define);

	/**
	 * 获取厂家
	 * 
	 */
	public List<Integer> getFactory(
			@Param(value = "conditionMap") Map conditionMap,
			@Param(value = "Define") Map Define);

	/**
	 * 批量解除端口模板设置
	 * 
	 */
	public List<Map> getPtpIdForBatchDetach(
			@Param(value = "conditionMap") Map conditionMap,
			@Param(value = "Define") Map Define);

	/**
	 * 批量解除端口模板设置
	 * 
	 */
	public void cancelTemplateBatch(
			@Param(value = "ptpIdList") List<Map> ptpIdList);

	
	/**
	 * 批量解除端口模板设置
	 * 
	 */
	public List<Map> getUnitIdForBatchDetach(
			@Param(value = "conditionMap") Map conditionMap,
			@Param(value = "Define") Map Define);

	/**
	 * 批量解除端口模板设置
	 * 
	 */
	public void cancelTemplateBatchUnit(
			@Param(value = "unitIdList") List<Map> unitIdList);

	/**
	 * 获取模板计数值详情
	 * 
	 */
	public List<Map> getNumberic(@Param(value = "templateId") int templateId,
			@Param(value = "type") int type);

	/**
	 * 获取模板计数值详情
	 * 
	 */
	public List<Map> getPhysical(@Param(value = "templateId") int templateId,
			@Param(value = "type") int type);

	/**
	 * 复制一条参考模板的记录
	 */
	public void newTemplate(@Param(value = "templateId") int templateId,
			@Param(value = "templateName") String templateName,
			@Param(value = "Define") Map Define,
			@Param(value = "idMap") Map idMap);

	/**
	 * 复制一条参考模板详细的记录
	 */
	public void newTemplateDetail(@Param(value = "templateId") int templateId,
			@Param(value = "newId") Long newId);

	/**
	 * 复制一条参考模板详细的记录
	 */
	public void saveNumberic(@Param(value = "map") Map map);

	/**
	 * 复制一条参考模板详细的记录
	 */
	public void savePhysical(@Param(value = "map") Map map);

	/**
	 * 删除模板记录
	 */
	public void deleteTemplate(@Param(value = "templateId") Long templateId);

	/**
	 * 删除模板详细记录
	 */
	public void deleteTemplateDetail(
			@Param(value = "templateId") Long templateId);

	/**
	 * 解除模板
	 * 
	 * @param Define
	 * 
	 */
	public void detachTemplateUnit(@Param(value = "templateId") Long templateId,
			@Param(value = "userId") Integer userId,
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 解除模板
	 * 
	 * @param Define
	 * 
	 */
	public void detachTemplate(@Param(value = "templateId") Long templateId,
			@Param(value = "userId") Integer userId,
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 解除模板
	 */
	public int checkIfTemplateApplied(
			@Param(value = "templateId") Long templateId,
			@Param(value = "Define") Map Define);

	/**
	 * 获取一条PM_STD_INDEX在一个时间区间内的性能值记录
	 * 
	 */
	public List<Map> generateDiagramNend(
			@Param(value = "searchCond") Map searchCond,
			@Param(value = "pmIndex") String pmIndex,
			@Param(value = "tableNameList") List<String> tableNameList);

	/**
	 * 获取一条PM_STD_INDEX在一个时间区间内的性能值记录
	 * 
	 */
	public List<String> getDiagramCategories(
			@Param(value = "searchCond") Map searchCond,
			@Param(value = "pmStdIndex") String[] pmStdIndex,
			@Param(value = "tableNameList") List<String> tableNameList);

	/**
	 * 获取对端PTPID
	 * 
	 */
	public Map getPtpIdFend(@Param(value = "ptpIdNear") Integer ptpIdNear,
			@Param(value = "linkType") Integer linkType);

	/**
	 * 加载光口标准combo
	 * 
	 */
	public List<Map> getOptStdComboValue(
			@Param(value = "searchCond") Map searchCond);

	/**
	 * 加载光口标准combo
	 * 
	 */
	public List<Map> getOptModelComboValue(@Param(value = "Define") Map Define,
			@Param(value = "searchCond") Map<String, String> searchCond);

	/**
	 * 搜索ptp的光口标准应用信息
	 * 
	 */
	public List<Map> searchPtpOptModelInfo(@Param(value = "Define") Map Define,
			@Param(value = "conditionMap") Map<String, String> conditionMap);

	/**
	 * 搜索ptp的光口标准应用信息count
	 * 
	 */
	public int searchPtpOptModelInfoCount(@Param(value = "Define") Map Define,
			@Param(value = "conditionMap") Map<String, String> conditionMap);

	/**
	 * 保存节点的光口标准应用信息
	 * 
	 */
	public void savePtpOptStdApplication(@Param(value = "map") Map map);

	/**
	 * 查询光口标准详细
	 * 
	 * @param searchCond
	 * @return
	 */
	public List<Map> searchOptStdDetail(
			@Param(value = "conditionMap") Map<String, String> searchCond);

	/**
	 * 查询光口标准详细total
	 * 
	 * @param searchCond
	 * @return
	 */
	public int searchOptStdDetailCount(
			@Param(value = "conditionMap") Map<String, String> searchCond);

	/**
	 * 修改光口标准内容
	 * 
	 * @param conditionMap
	 */
	public void saveOptStdDetail(@Param(value = "map") Map map);

	/**
	 * 新增光口标准
	 * 
	 * @param searchCond
	 */
	public void saveNewOptStd(
			@Param(value = "map") Map<String, String> searchCond);

	/**
	 * 删除光口标准
	 * 
	 * @param condList
	 */
	public void deleteOptStd(@Param(value = "optStdId") Long optStdId);

	/**
	 * 查询节点上的ptpType
	 * 
	 * @param conditionMap
	 * @param regularPmAnalysisDefine
	 * @return
	 */
	public List<Map> getOptModelFromNodes(
			@Param(value = "conditionMap") Map<String, String> conditionMap,
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 获取光口标准值
	 * 
	 * @param searchCond
	 * @return
	 */
	public Map getOptStdInfo(
			@Param(value = "conditionMap") Map<String, String> searchCond);

	public List<Long> getPtpIdForBatchOptStdApply(
			@Param(value = "conditionMap") Map<String, String> conditionMap,
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 应用光口标准到树上
	 * 
	 * @param conditionMap
	 * @param Define
	 */
	public void applyPtpOptStdBatch(@Param(value = "ptpIds") List<Long> ptpIds,
			@Param(value = "conditionMap") Map<String, String> conditionMap);

	/**
	 * 检查光口标准名称是否存在
	 * 
	 * @param searchCond
	 * @return
	 */
	public int checkOptStdName(
			@Param(value = "conditionMap") Map<String, String> searchCond);

	/**
	 * 检测标准是否已应用
	 * 
	 * @param searchCond
	 * @return
	 */
	public int checkIfStdApplied(
			@Param(value = "conditionMap") Map<String, String> searchCond);

	/**
	 * 解除已经应用了的光口标准
	 * 
	 * @param optStdId
	 * @param regularPmAnalysisDefine
	 */
	public void detachOptStd(@Param(value = "optStdId") Long optStdId,
			@Param(value = "Define") Map<String, Object> regularPmAnalysisDefine);

	/**
	 * 根据选择节点查找ptpId。去掉了几张表的关联
	 * 
	 * @param conditionMap
	 * @param regularPmAnalysisDefine
	 * @param domain
	 *            TODO
	 * @return
	 */
	public List<Long> getPtpIdListByDomain(
			@Param(value = "conditionMap") Map<String, String> conditionMap,
			@Param(value = "Define") Map<String, Object> regularPmAnalysisDefine,
			@Param(value = "domain") int domain);

	/**
	 * 在基准值表中查找最大输出功率的记录
	 * 
	 * @param ptpIds
	 * @param targetType
	 * @param pmStdIndex
	 * @return
	 */
	public List<Map> getOutMaxList(@Param(value = "ptpIds") List<Long> ptpIds,
			@Param(value = "targetType") int targetType,
			@Param(value = "outMax") String outMax,
			@Param(value = "inMax") String inMax);

	/**
	 * 自动应用光口标准
	 * 
	 * @param m
	 */
	public void autoApplyOptStd(@Param(value = "conditionMap") Map m);

	/**
	 * 查询用户组信息
	 * 
	 * @return
	 */
	public List<Map> getPrivilegeList();

	/**
	 * 判断模板名是否重复
	 * 
	 * @param templateName
	 * @param regularPmAnalysisDefine
	 */
	public int isTemplateNameExist(
			@Param(value = "templateName") String templateName,
			@Param(value = "Define") Map<String, Object> regularPmAnalysisDefine);

	/**
	 * 网管节点的信息补完
	 * 
	 * @param conditionMap
	 * @param regularPmAnalysisDefine
	 * @param TREE_DEFINE
	 * @return
	 */
	public List<Map<String, String>> getEmsNodeInfo(
			@Param(value = "conditionMap") Map<String, String> conditionMap,
			@Param(value = "Define") Map<String, Object> regularPmAnalysisDefine,
			@Param(value = "treeDefine") Map<String, Object> TREE_DEFINE);

	/**
	 * 子网节点的信息补完
	 * 
	 * @param conditionMap
	 * @param regularPmAnalysisDefine
	 * @param TREE_DEFINE
	 * @return
	 */
	public List<Map<String, String>> getSubnetNodeInfo(
			@Param(value = "conditionMap") Map<String, String> conditionMap,
			@Param(value = "Define") Map<String, Object> regularPmAnalysisDefine,
			@Param(value = "treeDefine") Map<String, Object> TREE_DEFINE);

	/**
	 * 网元节点的信息补完
	 * 
	 * @param conditionMap
	 * @param regularPmAnalysisDefine
	 * @param TREE_DEFINE
	 * @return
	 */
	public List<Map<String, String>> getNeNodeInfo(
			@Param(value = "conditionMap") Map<String, String> conditionMap,
			@Param(value = "Define") Map<String, Object> regularPmAnalysisDefine,
			@Param(value = "treeDefine") Map<String, Object> TREE_DEFINE);

	/**
	 * 查询本次选中的节点共有多少网元
	 * 
	 * @param conditionMap
	 * @param regularPmAnalysisDefine
	 * @return
	 */
	public int getCountOfNeUnderThisNode(
			@Param(value = "conditionMap") Map<String, String> conditionMap,
			@Param(value = "Define") Map<String, Object> regularPmAnalysisDefine);

	/**
	 * 保存报表任务（网元）主要信息
	 * 
	 * @param searchCond
	 * @param currentUserId
	 */
	public void saveNeSysTask(
			@Param(value = "saveParams") Map<String, String> searchCond,
			@Param(value = "currentUserId") int currentUserId,
			@Param(value = "taskType") int taskType,
			@Param(value = "idMap") Map idMap);

	/**
	 * 保存报表任务（网元）节点信息
	 * 
	 * @param nodeList
	 * @param idMap
	 */
	public void saveNeSysTaskInfo(
			@Param(value = "nodeList") List<Map> nodeList,
			@Param(value = "idMap") Map<String, Long> idMap);

	/**
	 * 保存报表任务（网元）其他参数
	 * 
	 * @param paramMap
	 * @param idMap
	 *            TODO
	 */
	public void saveNeTaskParam(
			@Param(value = "saveParams") Map<String, String> searchCond,
			@Param(value = "idMap") Map<String, Long> idMap);

	/**
	 * 查询报表任务
	 * 
	 * @param searchCond
	 * @param regularPmAnalysisDefine
	 * @return
	 */
	public List<Map<String, String>> searchReportTask(
			@Param(value = "searchCond") Map<String, String> searchCond,
			@Param(value = "Define") Map<String, Object> regularPmAnalysisDefine);

	/**
	 * 给首页的接口
	 * 
	 * @param searchCond
	 * @param regularPmAnalysisDefine
	 * @return
	 */
	public int getPMReportParamForFP(
			@Param(value = "searchCond") Map<String, String> searchCond,
			@Param(value = "Define") Map<String, Object> regularPmAnalysisDefine);

	/**
	 * 查询报表任务count
	 * 
	 * @param searchCond
	 * @param regularPmAnalysisDefine
	 * @return
	 */
	public int searchReportTaskCount(
			@Param(value = "searchCond") Map<String, String> searchCond,
			@Param(value = "Define") Map<String, Object> regularPmAnalysisDefine);

	/**
	 * 通过用户ID获取用户名
	 * 
	 * @param creatorId
	 * @return
	 */
	public String getUserNameById(@Param(value = "creatorId") Integer creatorId);

	/**
	 * 查询创建人下拉框数据
	 * 
	 * @param userId
	 * @param regularPmAnalysisDefine
	 * @return
	 */
	public List<Map> getCreatorComboValue(
			@Param(value = "userId") Integer userId,
			@Param(value = "Define") Map<String, Object> regularPmAnalysisDefine);

	/**
	 * 查询任务名称下拉框数据
	 * 
	 * @param searchCond
	 * @param regularPmAnalysisDefine
	 * @param dataSrc
	 * @return
	 */
	public List<Map> getTaskNameComboValue(
			@Param(value = "searchCond") Map<String, String> searchCond,
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 删除报表任务
	 * 
	 * @param searchCond
	 * @param Define
	 */
	public void deleteReportTask(
			@Param(value = "searchCond") Map<String, String> searchCond,
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 报表任务判重
	 * 
	 * @param searchCond
	 * @param Define
	 * @return
	 */
	public int checkTaskNameDuplicate(
			@Param(value = "searchCond") Map<String, String> searchCond,
			@Param(value = "Define") Map<String, Object> Define,
			@Param(value = "taskTypes") int[] taskTypes);

	/**
	 * 获取网管分组
	 * 
	 * @param Define
	 * @return
	 */
	public List<Map> getEmsGroup(
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 获取网管
	 * 
	 * @param searchCond
	 * @param Define
	 * @return
	 */
	public List<Map> getEms(
			@Param(value = "searchCond") Map<String, String> searchCond,
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 获取干线
	 * 
	 * @param searchCond
	 * @param Define
	 * @return
	 */
	public List<Map> getTrunkLine(
			@Param(value = "searchCond") Map<String, String> searchCond,
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 获取复用段信息
	 * 
	 * @param searchCond
	 * @param Define
	 * @return
	 */
	public List<Map> searchMS(
			@Param(value = "searchCond") Map<String, String> searchCond,
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 获取干线信息
	 * 
	 * @param searchCond
	 * @param Define
	 * @return
	 */
	public List<Map> searchTL(
			@Param(value = "searchCond") Map<String, String> searchCond,
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 获取干线信息
	 * 
	 * @param searchCond
	 * @param Define
	 * @return
	 */
	public List<Map> searchTLMS(@Param(value = "list") List<Map> returnList,
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 保存复用段报表任务主要信息
	 * 
	 * @param searchCond
	 * @param currentUserId
	 * @param taskType
	 * @param idMap
	 */
	public void saveMSSysTask(
			@Param(value = "saveParams") Map<String, String> searchCond,
			@Param(value = "currentUserId") int currentUserId,
			@Param(value = "taskType") int taskType,
			@Param(value = "idMap") Map idMap);

	/**
	 * 保存复用段报表任务param信息
	 * 
	 * @param searchCond
	 * @param idMap
	 */
	public void saveMSTaskParam(
			@Param(value = "saveParams") Map<String, String> searchCond,
			@Param(value = "idMap") Map<String, Long> idMap);

	/**
	 * 保存复用段报表任务节点信息
	 * 
	 * @param nodeList
	 * @param idMap
	 */
	public void saveMSSysTaskInfo(
			@Param(value = "nodeList") List<Map<String, String>> nodeList,
			@Param(value = "idMap") Map<String, Long> idMap);

	/**
	 * 查询任务主要信息(MS)
	 * 
	 * @param searchCond
	 * @return
	 */
	public List<Map<String, String>> searchMSTaskInfoForEdit(
			@Param(value = "searchCond") Map<String, String> searchCond);

	/**
	 * 查询任务主要信息(NE)
	 * 
	 * @param searchCond
	 * @return
	 */
	public List<Map<String, Object>> searchNETaskInfoForEdit(
			@Param(value = "searchCond") Map<String, String> searchCond);

	/**
	 * 查询任务节点信息
	 * 
	 * @param searchCond
	 * @return
	 */
	public List<Map> searchTaskNodesForEdit(
			@Param(value = "searchCond") Map<String, String> searchCond);

	/**
	 * 补全复用段的信息
	 * 
	 * @param taskNodes
	 * @param Define
	 * @return
	 */
	public List<Map> searchMSNodesInfo(
			@Param(value = "taskNodes") List<Map> taskNodes,
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 补全干线的信息
	 * 
	 * @param taskNodes
	 * @param Define
	 * @return
	 */
	public List<Map> searchTLNodesInfo(
			@Param(value = "taskNodes") List<Map> taskNodes,
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 更新复用段任务
	 * 
	 * @param saveParams
	 */
	public void updateMSSysTask(
			@Param(value = "saveParams") Map<String, String> saveParams);

	/**
	 * 更新复用段任务PARAM
	 * 
	 * @param saveParams
	 */
	public void updateMSTaskParam(
			@Param(value = "saveParams") Map<String, String> saveParams);

	/**
	 * 删除任务节点
	 * 
	 * @param saveParams
	 */
	public void deleteNodesForUpdate(
			@Param(value = "saveParams") Map<String, String> saveParams);

	/**
	 * 更新网元任务
	 * 
	 * @param saveParams
	 */
	public void updateNESysTask(
			@Param(value = "saveParams") Map<String, String> saveParams);

	/**
	 * 更新网元任务PARAM
	 * 
	 * @param saveParams
	 */
	public void updateNETaskParam(
			@Param(value = "saveParams") Map<String, String> saveParams);

	/**
	 * 将子网转为PTP信息
	 * 
	 * @param Define
	 * @param conditionMap
	 * @return
	 */
	public List<Map> processSubnetToNe(@Param(value = "Tree") Map Tree,
			@Param(value = "conditionMap") Map<String, String> conditionMap,
			@Param(value = "Define") Map Define);

	/**
	 * 把ne附加上ems信息
	 * 
	 * @param Define
	 * @param conditionMap
	 * @return
	 */
	public List<Map> processNe(@Param(value = "Tree") Map Tree,
			@Param(value = "conditionMap") Map<String, String> conditionMap,
			@Param(value = "Define") Map Define);

	/**
	 * 查询性能（日报）
	 * 
	 * @param tableNodesList
	 * @param taskInfo
	 * @return
	 */
	public List<Map> searchPMForNeDaily(
			@Param(value = "tableNodesList") List<Map<String, Object>> tableNodesList,
			@Param(value = "taskInfo") Map taskInfo,
			@Param(value = "start") Long start);

	/**
	 * 查询性能计数（日报）
	 * 
	 * @param tableNodesList
	 * @param taskInfo
	 * @return
	 */
	public Long searchPMForNeDailyCount(
			@Param(value = "tableNodesList") List<Map<String, Object>> tableNodesList,
			@Param(value = "taskInfo") Map taskInfo);

	/**
	 * 查询性能（月报）
	 * 
	 * @param tableNodesList
	 * @param taskInfo
	 * @return
	 */
	public List<Map> searchPMForNeMonthly(
			@Param(value = "tableNodesList") List<Map<String, Object>> tableNodesList,
			@Param(value = "taskInfo") Map<String, Object> taskInfo,
			@Param(value = "start") Long start);

	/**
	 * 查询性能计数（月报）
	 * 
	 * @param tableNodesList
	 * @param taskInfo
	 * @return
	 */
	public Long searchPMForNeMonthlyCount(
			@Param(value = "tableNodesList") List<Map<String, Object>> tableNodesList,
			@Param(value = "taskInfo") Map<String, Object> taskInfo);

	/**
	 * 查询报表统计信息
	 * 
	 * @param tableNodesList
	 * @param taskInfo
	 * @return
	 */
	public List<Map> searchReportAnalysis(
			@Param(value = "map") Map<String, String> map);

	/**
	 * 查询检测失败网元
	 * 
	 * @param tableNodesList
	 * @param taskInfo
	 * @return
	 */
	public List<Map> searchCollectFailedNeInfo(
			@Param(value = "map") Map<String, String> map,
			@Param(value = "Define") Map Define);

	/**
	 * 查询检测失败端口
	 * 
	 * @param tableNodesList
	 * @param taskInfo
	 * @return
	 */
	public List<Map> searchCollectFailedPtpInfo(
			@Param(value = "map") Map<String, String> map,
			@Param(value = "Define") Map Define);

	/**
	 * 查询检测失败复用段
	 * 
	 * @param map
	 * @param Define
	 * @return
	 */
	public List<Map> searchCollectFailedMSInfo(
			@Param(value = "map") Map<String, String> map,
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 获取该用户所属于的用户组
	 * 
	 * @param userId
	 * @param Define
	 * @return
	 */
	public List<Map> getUserGroupByUserId(
			@Param(value = "userId") Integer userId,
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 创建人combo（按权限）
	 * 
	 * @param userGrps
	 * @param Define
	 * @return
	 */
	public List<Map> getCreatorComboValuePrivilege(
			@Param(value = "userGrps") List<Map> userGrps,
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 任务名combo（按权限）
	 * 
	 * @param searchCond
	 * @param rEPORT_DEFINE
	 * @param userGrps
	 * @return
	 */
	public List<Map> getTaskNameComboValuePrivilege(
			@Param(value = "searchCond") Map<String, String> searchCond,
			@Param(value = "Define") Map<String, Object> Define,
			@Param(value = "userGrps") List<Map> userGrps,
			@Param(value = "userId") Integer userId);

	/**
	 * 查询CSVFilePath
	 * 
	 * @param searchCond
	 * @return
	 */
	public String getCSVFilePathByReportId(
			@Param(value = "searchCond") Map<String, String> searchCond);

	/**
	 * 查询CSVFilePath-失败文件
	 * 
	 * @param searchCond
	 * @return
	 */
	public String getFailedCSVFilePathByReportId(
			@Param(value = "searchCond") Map<String, String> searchCond);

	/**
	 * 获取一个报表包含的网管的最晚采集时间
	 * 
	 * @param Define
	 * @param taskNodes
	 * @return
	 */
	public String getCollectEndTimeNe(
			@Param(value = "Define") Map<String, Object> Define,
			@Param(value = "nodeList") Map<String, String> taskNodes);

	/**
	 * 查询所有报表任务
	 * 
	 * @param Define
	 * @param nodeList
	 * @return
	 */
	public List<Map> getAllReportTask(
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 获取一个报表包含的网管的最晚采集时间
	 * 
	 * @param Define
	 * @param nodeType
	 * @param nodeList
	 * @return
	 */
	public String getCollectEndTimeMS(
			@Param(value = "Define") Map<String, Object> Define,
			@Param(value = "nodeType") int nodeType,
			@Param(value = "nodeList") List<Map> nodeList);

	/**
	 * 保存复用段割接任务
	 * 
	 * @param searchCond
	 * @param userId
	 * @param taskType
	 * @param idMap
	 */
	public void saveMSCutoverTask(
			@Param(value = "saveParams") Map<String, String> saveParams,
			@Param(value = "userId") Integer userId,
			@Param(value = "taskType") int taskType,
			@Param(value = "idMap") Map<String, Long> idMap);

	/**
	 * 保存复用段割接任务的复用段ID
	 * 
	 * @param condList
	 * @param idMap
	 * @param targetType
	 */
	public void saveMSCutoverTaskNodesInfo(
			@Param(value = "msIdList") List<Long> condList,
			@Param(value = "idMap") Map<String, Long> idMap,
			@Param(value = "targetType") int targetType);

	/**
	 * 查询复用段割接任务
	 * 
	 * @param searchCond
	 * @param userId
	 * @param start
	 * @param limit
	 * @param Define
	 * @return
	 */
	public List<Map> searchCutoverTask(
			@Param(value = "searchCond") Map<String, String> searchCond,
			@Param(value = "userId") Integer userId,
			@Param(value = "start") int start,
			@Param(value = "limit") int limit,
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 查询复用段割接任务(count)
	 * 
	 * @param searchCond
	 * @param userId
	 * @param start
	 * @param limit
	 * @param Define
	 * @return
	 */
	public int searchCutoverTaskCount(
			@Param(value = "searchCond") Map<String, String> searchCond,
			@Param(value = "userId") Integer userId,
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 加载任务名combo
	 * 
	 * @param searchCond
	 * @param Define
	 * @param userId
	 * @return
	 */
	public List<Map> loadTaskNameCombo(
			@Param(value = "searchCond") Map<String, String> searchCond,
			@Param(value = "Define") Map<String, Object> Define,
			@Param(value = "userId") Integer userId);

	/**
	 * 查找任务下的复用段
	 * 
	 * @param searchCond
	 * @param Define
	 * @return
	 */
	public List<Map> getMSById(
			@Param(value = "searchCond") Map<String, String> searchCond,
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 更新复用段割接任务
	 * 
	 * @param searchCond
	 * @param Define
	 */
	public void updateMSCutoverTask(
			@Param(value = "searchCond") Map<String, String> searchCond,
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 先删掉节点信息
	 * 
	 * @param searchCond
	 */
	public void deleteMSCutoverTaskInfo(
			@Param(value = "searchCond") Map<String, String> searchCond);

	/**
	 * 删除割接任务主要信息
	 * 
	 * @param map
	 */
	public void deleteMSCutoverTask(
			@Param(value = "searchCond") Map<String, String> map);

	/**
	 * 返回该任务ID下不在用户权限范围内的node
	 * 
	 * @@@分权分域到网元@@@
	 * @param map
	 */
	public List<Map> findNotInUserDomain(
			@Param(value = "userId") Integer userId,
			@Param(value = "map") Map map, @Param(value = "Define") Map Define);

	/**
	 * 删除割接任务主要信息
	 * 
	 * @@@分权分域到网元@@@
	 * @param map
	 */
	public void deleteNodesOutOfDomain(
			@Param(value = "map") Map<String, String> map,
			@Param(value = "nodelist") List<Map> nodelist);

	/**
	 * 获取创建人
	 * 
	 * @@@分权分域到网元@@@
	 * @param map
	 */
	public Integer getCreatorByTaskId(
			@Param(value = "map") Map<String, String> map);

	/**
	 * 检查任务下的对象数量
	 * 
	 * @@@分权分域到网元@@@
	 * @param map
	 */
	public Integer checkNodeCount(@Param(value = "map") Map<String, String> map);

	// TODO
	// ***********************************咯咯咯咯咯咯***************************************
	/**
	 * 获取网管集合
	 * 
	 * @param emsConnectionIds
	 *            网管Id集合
	 * @param neIds
	 *            网元Id集合
	 * @param linkType
	 *            link类型集合
	 * @param isDel
	 * @return
	 */
	public List selectEmsListByEmsGroupId(
			@Param(value = "emsGroupId") int emsGroupId,
			@Param(value = "isDel") Integer isDel);

	/**
	 * 获取网元集合
	 * 
	 * @param subnets
	 *            子网id集合
	 * @param isDel
	 * @return
	 */
	public List selectNeListBySubnetIds(
			@Param(value = "subnets") List<Integer> subnets,
			@Param(value = "isDel") Integer isDel);

	/**
	 * 根据查询条件获取link集合数量
	 * 
	 * @param targetType
	 *            目标类型 1.网管 2.网元
	 * @param ids
	 *            目标集合
	 * @param linkId
	 *            链路编号
	 * @param linkName
	 *            链路名称
	 * @param linkType
	 *            link类型集合
	 * @param isDel
	 * @return
	 */
	public int selectExternalLinkCountByConditions(
			@Param(value = "targetType") Integer targetType,
			@Param(value = "ids") List<Integer> ids,
			@Param(value = "linkId") Integer linkId,
			@Param(value = "linkName") String linkName,
			@Param(value = "linkType") Integer[] linkType,
			@Param(value = "isMain") Integer isMain,
			@Param(value = "isDel") Integer isDel);

	/**
	 * 获取link集合
	 * 
	 * @param targetType
	 *            目标类型 1.网管 2.网元
	 * @param ids
	 *            目标集合
	 * @param linkId
	 *            链路编号
	 * @param linkName
	 *            链路名称
	 * @param linkType
	 *            link类型集合
	 * @param start
	 * @param limit
	 * @param isDel
	 * @return
	 */
	public List selectExternalLinkListByConditions(
			@Param(value = "targetType") Integer targetType,
			@Param(value = "ids") List<Integer> ids,
			@Param(value = "linkId") Integer linkId,
			@Param(value = "linkName") String linkName,
			@Param(value = "linkType") Integer[] linkType,
			@Param(value = "start") Integer start,
			@Param(value = "limit") Integer limit,
			@Param(value = "isMain") Integer isMain,
			@Param(value = "isDel") Integer isDel);

	/**
	 * 查询ptp相关信息 包含key{ emsName,neName,productName,ptpName,ptpType,rate,
	 * groupName,PARENT_SUBNET,BASE_SUBNET_ID,subnetName,
	 * AREA_PARENT_ID,RESOURCE_AREA_ID,areaName, stationName,roomName}
	 * 
	 * @param ptpId
	 * @param type
	 *            0.发送光功率 1.接收光功率
	 * @return
	 */
	public Map selectPtpRelatedInfoByPtpId(
			@Param(value = "ptpId") Integer ptpId,
			@Param(value = "type") int type);

	/**
	 * 获取link pm信息
	 * 
	 * @param linkId
	 * @param collectDate
	 */
	public Map selectLinkPmByLinkIdAndCollectDate(
			@Param(value = "linkId") Integer linkId,
			@Param(value = "collectDate") String collectDate);

	/**
	 * @param data
	 */
	public void insertOffsetParam(Map data);

	/**
	 * @param data
	 */
	public void updateLinkById(Map data);

	/**
	 * @param data
	 */
	public void updateOffsetParamById(Map data);

	/**
	 * 查询外部link中包含的ptpId集合
	 * 
	 * @param linkType
	 * @param isDel
	 * @return key:ptpId,emsConnectionId
	 */
	public List selectPtpIdListFromExternalLink(
			@Param(value = "linkType") Integer[] linkType,
			@Param(value = "isMain") Integer isMain,
			@Param(value = "isDel") Integer isDel);

	/**
	 * 查询数据库中存在指定表数量
	 * 
	 * @param tableName
	 * @return
	 */
	public int selectTableCount(@Param(value = "tableName") String tableName,
			@Param(value = "dataBaseName") String dataBaseName);

	/**
	 * 查询link相关ptp的性能数据
	 * 
	 * @param tableName
	 * @param ptpIdList
	 * @param pmStdIndexList
	 * @param collectDate
	 * @return key:ptpId,pmValue,pmStdIndex
	 */
	public List selectLinkHistoryPm(
			@Param(value = "tableName") String tableName,
			@Param(value = "ptpIdList") List<Integer> ptpIdList,
			@Param(value = "pmStdIndexList") String[] pmStdIndexList,
			@Param(value = "collectDate") String collectDate);

	/**
	 * 获取link集合
	 * 
	 * @param linkType
	 *            in条件 1.外部link 2.内部link 3.手工link
	 * @return
	 */
	public List selectLinkList(@Param(value = "linkType") Integer[] linkType,
			@Param(value = "isMain") Integer isMain,
			@Param(value = "isDel") Integer isDel);

	/**
	 * 批量插入link收发光功率信息
	 * 
	 * @param insertLinkPmBatch
	 */
	public void insertLinkPmBatch(List<Map> linkPms);

	// ------------------------- MeiKai Start -------------------------

	/**
	 * 查询性能报表列表
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> getReportInfoList(
			@Param(value = "map") Map<String, Object> map,
			@Param(value = "userGrps") List<Map> userGrps);

	/**
	 * 查询性能报表列表条数
	 * 
	 * @param map
	 * @param userGrps
	 * @return
	 */
	public Map<String, Object> getReportInfoListCount(
			@Param(value = "map") Map<String, Object> map,
			@Param(value = "userGrps") List<Map> userGrps);

	/**
	 * 通过报表Id列表获取报表信息列表
	 * 
	 * @param reportIds
	 * @return
	 */
	public List<Map> getReportListByReportIds(
			@Param(value = "reportIds") List<Integer> reportIds);

	/**
	 * 通过id查询性能报表信息
	 * 
	 * @param pmReportId
	 * @return
	 */
	public Map getPmReportByPmReportId(
			@Param(value = "pmReportId") int pmReportId);

	/**
	 * 通过PM_REPORT_ID删除t_pm_report_analysis表中的记录
	 * 
	 * @param pmReportId
	 */
	public void deletePMReportAnalysisByPMReportId(
			@Param(value = "pmReportId") int pmReportId);

	/**
	 * 通过PM_REPORT_ID删除t_pm_report_info表中的记录
	 * 
	 * @param pmReportId
	 */
	public void deletePMReportByPMReportId(
			@Param(value = "pmReportId") int pmReportId);

	// ------------------------- MeiKai End -------------------------
	/**
	 * 保存PM定制报表导出的信息
	 * 
	 * @param map
	 * @param idMap
	 */
	public void savePmExportInfo(@Param(value = "map") Map map,
			@Param(value = "idMap") Map idMap);

	/**
	 * 获取pm的中文
	 * 
	 * @param trim
	 * @param dEFINE
	 * @return
	 */
	public String getPmDescription(
			@Param(value = "pmStdIndex") String pmStdIndex,
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 查找复用段信息
	 * 
	 * @param nodeList
	 * @param targetType
	 * @param Define
	 * @return
	 */
	public List<Map> searchMultiplexSection(
			@Param(value = "nodeList") List<Map> nodeList,
			@Param(value = "targetType") String targetType,
			@Param(value = "Define") Map<String, Object> Define);

	/**
	 * 保存pm统计信息
	 * 
	 * @param exportInfo
	 * @param Define
	 * @param idMap
	 */
	public void savePmAnalysisInfo(
			@Param(value = "exportInfo") Map<String, Object> exportInfo,
			@Param(value = "Define") Map<String, Object> Define,
			@Param(value = "idMap") Map idMap);

	/**
	 * 报表任务执行状态修改
	 * 
	 * @param map
	 *            （flag：start，end；result：1,2；taskId）
	 */
	public void taskStatusUpdate(@Param(value = "map") Map map);

	/**
	 * 我所属于的用户组
	 * 
	 */
	public List<Integer> myUserGroup(@Param(value = "userId") int userId);

	/**
	 * 获取任务的更新时间
	 * 
	 */
	public Date getTaskUpdateTime(@Param(value = "taskId") int taskId);
	
	/**
	 * 查询并插入历史性能
	 * 
	 * @param userId
	 *            当前用户名
	 * @param fromTableName
	 *            来源表
	 * @param conditionMap
	 */
	public void insertHistoryTempPmCircuit(@Param(value = "userId") int userId,
			@Param(value = "fromTableName") String fromTableName,
			@Param(value = "toTableName") String toTableName,
			@Param(value = "searchTag") int searchTag,
			@Param(value = "conditionMap") Map<String, Object> conditionMap);

	
	
	public List<Map> getNeByEmsByPage(
			@Param(value = "emsId") int emsId, 
			@Param(value = "productName") String productName,
			@Param(value = "subnetIdStr") String subnetIdStr,
			@Param(value = "type") Integer type,
			@Param(value = "start") int startNumber, 
			@Param(value = "limit") int pageSize,
			@Param(value = "Define") Map<String, Object> regularPmAnalysisDefine);
	
	public Integer getNeByEmsByPageCount(
			@Param(value = "emsId") int emsId, 
			@Param(value = "productName") String productName,
			@Param(value = "subnetIdStr") String subnetIdStr,
			@Param(value = "type") Integer type,
			@Param(value = "Define") Map<String, Object> regularPmAnalysisDefine);
	
	public void insertTaskCountInfoBatch(@Param(value = "neList") List<Map> neList);
	
	/**
	 * 获取性能分组
	 * @param pmStdIndex
	 * @return
	 */
	public Map getPmStdIndexType(@Param(value = "pmStdIndex") String pmStdIndex); 
}