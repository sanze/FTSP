package com.fujitsu.IService;

import java.util.List;
import java.util.Map;
import java.util.Date;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.model.FilterAlarmParametersModel;
/**
 * @author liuXin
 * 
 */
public interface ICutoverManagerService {
	/**
	 * 分页获取割接任务列表
	 * @param startTime
	 * 			割接任务开始时间
	 * @param endTime
	 * 			割接任务结束时间
	 * @param status
	 * 			割接任务状态
	 * @param cutoverTaskName
	 * 			割接任务名称
	 * @param startNumber
	 * 			开始页码
	 * @param pageSize
	 * 			页面大小
	 * @return "SYS_TASK_ID", "TASK_NAME", "START_TIME_ESTIMATE", "END_TIME_ESTIMATE", "STATUS",
			"CREATE_PERSON", "CREATE_TIME", "DISCRIPTION", "START_TIME_ACTUAL","END_TIME_ACTUAL"
	 * @throws CommonException
	 */
	public Map getCutoverTask(String startTime, String endTime, String status,
			String cutoverTaskName, String currentUserId,int startNumber, int pageSize)
			throws CommonException;
	/**
	 * 判断割接任务名是否重复
	 * 返回是否存在信息：true 存在/false 不存在
	 * 
	 * @param taskName - 割接任务名  taskId - 割接任务Id
	 * @return Boolean true/false
	 * @throws CommonException
	 */
	public Boolean checkTaskNameExist (Map map) throws CommonException;
	/**
	 * 根据割接任务id查询割接任务的设备列表
	 * @param cutOverTaskId
	 *             割接任务id
	 * @return TARGET_TYPE，TARGET_ID，DISPLAY_NAME
	 * @throws CommonException
	 */
	public List<Map> getCutoverEquipList(int cutOverTaskId) throws CommonException;
	/**
	 * 添加割接任务
	 * @param createTime
	 *             创建时间
	 * @param cutoverTaskId
	 *             割接任务id （因为与修改共用参数，此处id为空）
	 * @param taskName
	 *             割接任务名称
	 * @param taskDescription
	 *             任务描述
	 * @param startTime
	 *             割接预计开始时间
	 * @param endTime
	 *             割接预计结束时间
	 * @param status
	 *             割接任务状态
	 * @param taskStatus
	 *             保存割接任务类型：1：按网元端口割接，2：按链路割接，3：按复用段割接，4：按光缆割接        
	 * @param filterAlarm
	 *             是否过滤告警
	 * @param snapshot
	 *             是否自动采集快照，提前多少小时采集快照 
	 * @param cutoverEquipList
	 *             割接设备列表
	 * @param cutoverEquipNameList
	 *             割接设备名称列表
	 * @throws CommonException
	 */
	public Map addCutoverTask(Date createTime,String cutoverTaskId, String taskName,
			String taskDescription, String startTime, String endTime,
			String status, String taskStatus,String filterAlarm, String autoUpdateCompareValue,String snapshot,
			List<String> cutoverEquipList, List<String> cutoverEquipNameList,List<String> privilegeList)
			throws CommonException;
	/**
	 * 初始化任务信息
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public List<Map> initTaskInfo(Map<String, Object> map) throws CommonException;
	/**
	 * 修改割接任务
	 * @param cutoverTaskId
	 *             割接任务id 
	 * @param taskName
	 *             割接任务名称
	 * @param taskDescription
	 *             任务描述
	 * @param startTime
	 *             割接预计开始时间
	 * @param endTime
	 *             割接预计结束时间
	 * @param status
	 *             割接任务状态
	 * @param filterAlarm
	 *             是否过滤告警
	 * @param snapshot
	 *             是否自动采集快照，提前多少小时采集快照 
	 * @param cutoverEquipList
	 *             割接设备列表
	 * @param cutoverEquipNameList
	 *             割接设备名称列表
	 * @throws CommonException
	 */

	public void modifyCutoverTask(Date createTime,String cutoverTaskId, String taskName,
			String taskDescription, String startTime, String endTime,
			String status, String taskStatus,String filterAlarm, String autoUpdateCompareValue,String snapshot,
			List<String> cutoverEquipList, List<String> cutoverEquipNameList,
			List<String> privilegeList)
			throws CommonException;
	/**
	 * 删除割接任务
	 */
	public void deleteTask(List<Integer> taskIdList) throws CommonException;
	/**
	 * 查询链路信息
	 * @param emsId
	 *         网管id
	 * @param linkType
	 *         链路类型：1外部链路，2内部链路
	 * @param linkNameOrId
	 *         链路名称或链路号
	 * @return
	 */
	public Map getLink(String emsId, String emsGroupId,String linkType, String linkNameOrId,int userId,int startNumber,int pageSize)
			throws CommonException;
	/**
	 * 根据割接任务id查询割接任务的link列表
	 * @param cutOverTaskId
	 *             割接任务id
	 * @return BASE_LINK_ID,DISPLAY_NAME
	 * @throws CommonException
	 */
	public List<Map> getCutoverLinkList(int cutOverTaskId)
			throws CommonException;
	
	/**
	 * 影响电路查询
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> searchCircuitsInfluenced(Map map)
			throws CommonException;
	/**
	 * 端口性能值查询
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> searchPmValue(int cutoverTaskId,int start,int limit)
			throws CommonException;
	
	/**
	 * 割接前快照
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void snapshotBefore(String cutoverTaskId,int userId)
			throws CommonException;
	
	/**
	 * 查询相关告警
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map getCurrentAlarms(String cutoverTaskId, String alarmType,int startNumber,int pageSize)
			throws CommonException;
	
	/**
	 * 割接后快照
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void snapshotAfter(String cutoverTaskId, int userId)
			throws CommonException;
	
	/**
	 * 割接任务完成，需要1.标示当前时间为割接任务完成时间。
	 * 2.释放过滤告警。（如果有过滤告警）
	 * 3.将割接后采集的端口物理量更新为基准值。（如果点击“割接完成“按钮时有割接后快照值）
	 * 4.生成割接报告。
	 * @param cutOverTaskId
	 *             割接任务id
	 * 
	 * @throws CommonException
	 */
	public void cutoverComplete(String cutOverTaskId,Integer userId)
			throws CommonException;
	
	/**
	 * 过滤告警
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void filterAlarm(String cutoverTaskId, int userId)
			throws CommonException;
	
	/**
	 * 初始化割接任务进度状态
	 * @param cutoverTaskId
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map initCutoverTaskProcess(String cutoverTaskId)
			throws CommonException;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String downloadPmResult(String cutoverTaskId) throws CommonException;
	/**
	 * 查询所有割接任务对应的过滤告警时间，释放告警时间，网元id列表
	 * @return
	 * 返回的list中每一条数据都包含：
	 *     startTime
	 *        过滤告警时间 
	 *     endTime
	 *        释放告警时间
	 *     neIdList
	 *         网元id列表
	 * @throws CommonException
	 */
	//public List<FilterAlarmParametersModel> getFilterAlarmParameters() throws CommonException;
	
	public List<FilterAlarmParametersModel> getFilterAlarmParameters(Integer neId) throws CommonException;

	/**
	 * 割接报告导出
	 */
	public CommonResult downLoadReport(int cutoverTaskId)throws CommonException;
	
	/**
	 * 割接报告导出
	 */
	public void generateReport(int cutoverTaskId,int userId)throws CommonException;
	
	/**
	 * 查询割接期间告警
	 */
	@SuppressWarnings( { "unchecked", "rawtypes" })
	public Map getAlarmsDuringCutover(String cutoverTaskId, int usrId,
			int start, int limit) throws CommonException;
	/**
	 * 导出数据
	 * @param cutoverTaskId
	 * @param flag
	 * @return
	 * @throws CommonException
	 */
	public String exportExcel(int cutoverTaskId, int flag) throws CommonException;
	/**
	 * 查询割接任务参数
	 * @param cutoverTaskId
	 * @return
	 * @throws CommonException
	 */
	public List<Map> getCutoverTaskParameter(int cutoverTaskId) throws CommonException;
	/**
	 * 新增割接任务电路冲突检测
	 * @param cutoverEquipList
	 * @return
	 */
//	public Map<String,Object> addCutoverTaskPreCheck(List<String> cutoverEquipList) throws CommonException;
	
	/**
	 * 查询割接前异常性能（割接前评估用）
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> searchPmValueBefore(int cutoverTaskId,int start,int limit)
			throws CommonException;
	
	/**
	 * 查询割接前异常告警（割接前评估用）
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> searchAlarmBefore(int cutoverTaskId,int start,int limit)
			throws CommonException;
	
	/**
	 * 查询割接前异常倒换事件（割接前评估用）
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> searchEventBefore(int cutoverTaskId,int start,int limit)
			throws CommonException;
	
	/**
	 * 查询割接后异常性能（割接后评估用）
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> searchPmValueAfter(int cutoverTaskId,int start,int limit)
			throws CommonException;
	
	/**
	 * 查询割接后异常告警（割接后评估用）
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> searchAlarmAfter(int cutoverTaskId,int start,int limit)
			throws CommonException;
	
	/**
	 * 查询割接后异常倒换事件（割接后评估用）
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> searchEventAfter(int cutoverTaskId,int start,int limit)
			throws CommonException;
	/**
	 * 查询评估参数
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> getEvaluationConfig()
			throws CommonException;
	
	/**
	 * 修改评估参数
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> modifyEvaluationConfig(Map<String, String> searchCondition)
			throws CommonException;
	
	/**
	 * 存在差值端口性能值查询
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> searchPmValueWithDifference(int cutoverTaskId)
			throws CommonException;
	/**
	 * 更新基准值
	 * @param pmIdList
	 * @return
	 * @throws CommonException
	 */
	public Map<String,Object> updateCompareValue(List<Integer> pmIdList) throws CommonException;
	
	/**
	 * 评估割接结果
	 * @param pmIdList
	 * @return
	 * @throws CommonException
	 */
	public Map<String,Object> evaluate(Map searchCondition) throws CommonException;
}
