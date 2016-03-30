package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;
import java.util.Date;
import org.apache.ibatis.annotations.Param;

/**
 * @author liuXin
 * 
 */
public interface CutoverManagerMapper {

	/**
	 * 万能查询法
	 * 
	 * @param NAME
	 *            表名
	 * @param ID_NAME
	 *            字段名
	 * @param ID_VALUE
	 *            字段值
	 * @param ID_NAME_2
	 *            字段名2
	 * @param ID_VALUE_2
	 *            字段值2
	 */

	public List<Map> getByParameter(@Param(value = "map")
	Map map);

	/**
	 * 分页获取割接任务列表
	 * 
	 * @param startTime
	 *            割接任务开始时间
	 * @param endTime
	 *            割接任务结束时间
	 * @param status
	 *            割接任务状态 1.等待，2.进行中，3完成，4.挂起，5.等待&进行中
	 * @param cutoverTaskName
	 *            割接任务名称
	 * @param startNumber
	 * @param pageSize
	 * @return "SYS_TASK_ID", "TASK_NAME", "START_TIME_ESTIMATE",
	 *         "END_TIME_ESTIMATE", "STATUS", "CREATE_PERSON", "CREATE_TIME",
	 *         "DISCRIPTION"
	 */
	public List<Map> getCutoverTask(@Param(value = "startTime")
	String startTime, @Param(value = "endTime")
	String endTime, @Param(value = "status")
	String status, @Param(value = "cutoverTaskName")
	String cutoverTaskName, 
	@Param(value = "userGrps") List<Map> userGrps,
	@Param(value = "userId") Integer userId,
	@Param(value = "startNumber")
	int startNumber, @Param(value = "pageSize")
	int pageSize);

	/**
	 * 获取割接任务总数
	 * 
	 * @param startTime
	 *            割接任务开始时间
	 * @param endTime
	 *            割接任务结束时间
	 * @param status
	 *            割接任务状态 1.等待，2.进行中，3完成，4.挂起，5.等待&进行中
	 * @param cutoverTaskName
	 *            割接任务名称
	 * @return 总数
	 */
	public int getCutoverTaskCount(@Param(value = "startTime")
	String startTime, @Param(value = "endTime")
	String endTime, @Param(value = "status")
	String status, @Param(value = "cutoverTaskName")
	String cutoverTaskName,@Param(value = "userGrps") List<Map> userGrps,
	@Param(value = "userId") Integer userId);

	/**
	 * 查询割接任务参数
	 * 
	 * @param cutoverTaskId
	 *            割接任务id
	 * @return PARAMETER_NAME,PARAMETER_VALUE
	 */
	public List<Map> getCutoverTaskParameter(@Param(value = "cutoverTaskId")
	int cutoverTaskId);

	/**
	 * 查询割接任务名称是否已经存在（根据判断与参数割接任务名称相同名称的割接任务数目）
	 * 
	 * @param cutoverTaskId
	 *            割接任务id
	 * @param taskName
	 *            割接任务名称
	 * @return
	 */
	public int getcutoverTaskExitList(@Param(value = "cutoverTaskId")
	int cutoverTaskId, @Param(value = "taskName")
	String taskName);

	/**
	 * 查询割接任务设备列表
	 * 
	 * @param cutoverTaskId
	 *            割接任务id
	 * @return
	 */
	public List<Map> getCutoverEquipList(@Param(value = "cutoverTaskId")
	int cutoverTaskId);

	/**
	 * 保存割接任务
	 * 
	 * @param task
	 *            参数包含Date createTime,String taskName, String taskDescription,
	 *            String startTime, String endTime
	 */
	public void saveCutoverTask(@Param(value = "task")
	Map task);

	/**
	 * 保存割接任务设备
	 * 
	 * @param equipList
	 */
	public void saveCutoverTaskInfo(@Param(value = "equipList")
	List<Map> equipList);

	/**
	 * 初始化任务信息
	 * 
	 * @param map
	 * @return
	 */
	public List<Map> initTaskInfo(@Param(value = "cutoverTaskId")
	int cutoverTaskId);

	/**
	 * 保存割接任务参数信息
	 * 
	 * @param taskParamList
	 */
	public void saveCutoverTaskParameter(@Param(value = "taskParamList")
	List<Map> taskParamList);

	/**
	 * 修改割接任务
	 * 
	 * @param task
	 */
	public void updateCutoverTask(@Param(value = "task")
	Map task);

	/**
	 * 删除割接任务参数
	 * 
	 * @param map
	 */
	public void deleteTaskParam(@Param(value = "map")
	Map map);

	/**
	 * 删除割接任务设备信息
	 * 
	 * @param map
	 */
	public void deleteTaskInfo(@Param(value = "map")
	Map map);
	/**
	 * 删除割接任务性能信息
	 * 
	 * @param map
	 */
	public void deleteTaskPerformance(@Param(value = "map")
	Map map);
	/**
	 * 删除割接任务告警信息
	 * 
	 * @param map
	 */
	public void deleteTaskAlarm(@Param(value = "map")
	Map map);
	/**
	 * 删除割接任务
	 * 
	 * @param map
	 */
	public void deleteTask(@Param(value = "map")
	Map map);

	/**
	 * 删除割接任务参数
	 * 
	 * @param cutoverTaskId
	 */
	public void deleteTaskParamSingle(@Param(value = "cutoverTaskId")
	int cutoverTaskId);

	/**
	 * 查询链路信息
	 * 
	 * @param emsConnectionId
	 *            网管id
	 * @param type
	 *            链路类型：1外部，2内部
	 * @param id
	 *            链路id
	 * @param linkNameOrId
	 *            链路名称
	 * @param startNumber
	 *            起始值
	 * @param pageSize
	 *            分页大小
	 * @return
	 */
	public List<Map> getLink(@Param(value = "emsConnectionId")
	int emsConnectionId, @Param(value = "emsGroup")
	int emsGroup, @Param(value = "emsIdList")
	List<Integer> emsIdList, @Param(value = "type")
	int type, @Param(value = "id")
	int id, @Param(value = "linkNameOrId")
	String linkNameOrId, @Param(value = "startNumber")
	int startNumber, @Param(value = "pageSize")
	int pageSize);

	/**
	 * 查询链路总数
	 * 
	 * @param emsConnectionId
	 *            网管id
	 * @param type
	 *            链路类型：1外部，2内部
	 * @param id
	 *            链路id
	 * @param linkNameOrId
	 *            链路名称
	 * @return 总数
	 */
	public int getLinkCount(@Param(value = "emsConnectionId")
	int emsConnectionId, @Param(value = "emsGroup")
	int emsGroup, @Param(value = "emsIdList")
	List<Integer> emsIdList, @Param(value = "type")
	int type, @Param(value = "id")
	int id, @Param(value = "linkNameOrId")
	String linkNameOrId);

	/**
	 * 相关性查询
	 */

	public List<Map> selectCircuitAbout(@Param(value = "map")
	Map map);

	/**
	 * 获得相关性查询总数
	 */

	public Map circuitAboutTotal(@Param(value = "map")
	Map map);

	/**
	 * 查询端口性能值
	 * 
	 * @param cutoverTaskId
	 * @param startNumber
	 * @param pageSize
	 * @return
	 */
	public List<Map> searchPmValue(@Param(value = "cutoverTaskId")
	int cutoverTaskId, @Param(value = "startNumber")
	int startNumber, @Param(value = "pageSize")
	int pageSize);

	/**
	 * 查询端口性能值总数
	 * 
	 * @param cutoverTaskId
	 * @return
	 */
	public int getPmValueCount(@Param(value = "cutoverTaskId")
	int cutoverTaskId);

	/**
	 * 根据端口id查询link，从而查询link两端ptp的id及其相应的ems的id
	 * 
	 * @param cutoverPortIds
	 * @return
	 */
	public List<Map> searchPortsInLink(@Param(value = "cutoverPortIds")
	List cutoverPortIds);

	/**
	 * 根据link id 获取两端ptp的id及其相应的ems的id
	 * 
	 * @param cutoverPortIds
	 * @return
	 */
	public List<Map> searchPortsByLink(@Param(value = "linkIds")
	List linkIds);

	/**
	 * 根据各个级别的设备查询所属的端口id
	 * 
	 * @param targetType
	 * @param equipIds
	 * @return
	 */
	public List searchPorts(@Param(value = "targetType")
	int targetType, @Param(value = "equipIds")
	List equipIds);

	/**
	 * 保存性能值（前
	 * 
	 * @param pmValueList
	 */
	public void savePmValueBefore(@Param(value = "pmValueList")
	List<Map> pmValueList);
	/**
	 * 保存性能值（后
	 * 
	 * @param pmValueList
	 */
	public void savePmValueAfter(@Param(value = "pmValueList")
	List<Map> pmValueList);
	/**
	 * 查询相关告警
	 * 
	 * @param cutoverTaskId
	 * @param startNumber
	 * @param pageSize
	 * @return
	 */
	public List<Map> getCurrentAlarms(@Param(value = "cutoverTaskId")
	int cutoverTaskId, @Param(value = "alarmType")
	int alarmType, @Param(value = "startNumber")
	int startNumber, @Param(value = "pageSize")
	int pageSize);

	/**
	 * 查询相关告警总数
	 * 
	 * @param cutoverTaskId
	 * @return
	 */
	public int getCurrentAlarmsCount(@Param(value = "cutoverTaskId")
	int cutoverTaskId, @Param(value = "alarmType")
	int alarmType);

	/**
	 * 查询割接任务开始时间等参数，供过滤、释放告警用
	 * 
	 * @return
	 */
	public List<Map> getFilterAlarmParameters();

	/**
	 * 查询割接任务相关信息
	 * 
	 * @param cutoverTaskId
	 * @return
	 */
	public List<Map> getCutoverTaskList(@Param(value = "cutoverTaskId")
	int cutoverTaskId);

	/**
	 * 查询割接任务param相关信息
	 * 
	 * @param cutoverTaskId
	 * @return
	 */
	public List<Map> getCutoverTaskParamList(@Param(value = "cutoverTaskId")
	int cutoverTaskId);

	public List<Map> getCutoverEquipmentList(@Param(value = "map")
	Map map);

	public List<Map> getEquipListByEmsId(@Param(value = "emsId")
	int emsId);

	public List<Map> getEquipListByNeId(@Param(value = "neId")
	int neId);

	public List<Map> getEquipListByPtpId(@Param(value = "ptpId")
	int ptpId);

	public List<Map> getCutoverLinkList(@Param(value = "map")
	Map map);

	public List<Map> getLinkListByLinkId(@Param(value = "linkId")
	int linkId);

	public List<Map> getCutoverPMList(@Param(value = "cutoverTaskId")
	int cutoverTaskId);

	public List<Map> getCutoverAfterList(@Param(value = "map")
	Map map);

	public void saveCutoverAlarms(@Param(value = "cutoverTaskId")
	int cutoverTaskId, @Param(value = "snapshotTime")
	Date snapshotTime, @Param(value = "snapshotTimeFlag")
	int snapshotTimeFlag, @Param(value = "alarmsList")
	List alarmsList);
	/**
	 * 删除指定割接任务的指定名称的参数信息
	 * @param cutoverTaskId
	 * @param paramNameList
	 */
	public void deleteSpecifiedParam(@Param(value = "cutoverTaskId")
	int cutoverTaskId, @Param(value = "paramNameList")
	List<String> paramNameList);
	/**
	 * 查询割接任务链路列表
	 * @param cutoverTaskId
	 * @return
	 */
	public List<Map> getCutoverEqptLinkList(@Param(value = "cutoverTaskId")
			int cutoverTaskId);
	
	/**
	 * 根据链路id的list查询链路列表
	 * @param nodeList
	 * @return
	 */
	public List<Map> getLinkListByLinkIdList(@Param(value = "nodeList")
			List nodeList);
	/**
	 * 查询所有未完成割接任务的ID
	 * @return
	 */
	public List<Map> getAllUnfinishedTask();
	
	/**
	 * 根据链路id的list查询链路列表
	 * @param nodeList
	 * @return
	 */
	public List<Map> getCircuitsByIdList(@Param(value = "duplicateCircuitIdList")
			List duplicateCircuitIdList);
	
	/**
	 * 根据链路id的list查询链路列表
	 * @param nodeList
	 * @return
	 */
	public List<Map> getOTNCircuitsByIdList(@Param(value = "duplicateOTNCircuitIdList")
			List duplicateOTNCircuitIdList);
	
	/**
	 * 查询割接前异常性能（割接前评估用）
	 * @param nodeList
	 * @return
	 */
	public List<Map> searchPmValueBefore(@Param(value = "cutoverTaskId")
			Integer cutoverTaskId,@Param(value = "startNumber")
			Integer start,@Param(value = "pageSize")
			Integer limit);
	
	/**
	 * 查询割接前异常告警（割接前评估用）
	 * @param nodeList
	 * @return
	 */
	public List<Map> searchAlarmBefore(@Param(value = "cutoverTaskId")
			Integer cutoverTaskId,@Param(value = "startNumber")
			Integer start,@Param(value = "pageSize")
			Integer limit);
	/**
	 * 查询割接前异常性能数量（割接前评估用）
	 * @param cutoverTaskId
	 * @return
	 */
	public int searchPmValueBeforeCount(@Param(value = "cutoverTaskId")
			Integer cutoverTaskId);
	/**
	 * 查询割接前异常告警数量（割接前评估用）
	 * @param cutoverTaskId
	 * @return
	 */
	public int searchAlarmBeforeCount(@Param(value = "cutoverTaskId")
			Integer cutoverTaskId);
	
	/**
	 * 查询割接后异常性能（评分不为0，割接后评估用）
	 * @param nodeList
	 * @return
	 */
	public List<Map> searchPmValueAfter(@Param(value = "cutoverTaskId")
			Integer cutoverTaskId,@Param(value = "startNumber")
			Integer start,@Param(value = "pageSize")
			Integer limit);
	
	/**
	 * 查询割接后异常告警（割接后评估用）
	 * @param nodeList
	 * @return
	 */
	public List<Map> searchAlarmAfter(@Param(value = "cutoverTaskId")
			Integer cutoverTaskId,@Param(value = "startNumber")
			Integer start,@Param(value = "pageSize")
			Integer limit);
	/**
	 * 查询割接后异常性能数量（割接后评估用）
	 * @param cutoverTaskId
	 * @return
	 */
	public int searchPmValueAfterCount(@Param(value = "cutoverTaskId")
			Integer cutoverTaskId);
	/**
	 * 查询割接后异常告警数量（割接后评估用）
	 * @param cutoverTaskId
	 * @return
	 */
	public int searchAlarmAfterCount(@Param(value = "cutoverTaskId")
			Integer cutoverTaskId);
	/**
	 * 根据id列表查询割接性能
	 * @param pmIdList
	 * @return
	 */
	public List<Map> searchPmValueByIdList(@Param(value = "pmIdList")
			List<Integer> pmIdList);
	/**
	 * 更新性能数据的评分
	 * @param pmList
	 */
	public void setPMEvaluationScore(@Param(value = "pmList")
			List<Map> pmList);
	/**
	 * 保存割接影响电路信息的方法
	 * @param cutoverCircuitList
	 */
	public void saveCutoverCircuit(@Param(value = "cutoverCircuitList")
			List<Map> cutoverCircuitList);
	/**
	 * 查询未完成任务的影响电路
	 * @return
	 */
	public List<Map> searchCircuitOfUnfinishedTask();
	/**
	 * 查询影响电路的id
	 * @param map
	 * @return
	 */
	public List<Map> selectAllCircuitAbout(@Param(value = "map")
			Map map);
}