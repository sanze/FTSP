package com.fujitsu.IService;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.ExportResult;
import com.fujitsu.model.OpticalPathMonitorModel;

/**
 * @author ZhongLe
 * 
 */
public interface IPerformanceManagerService {

	/**
	 * 获取所有网管分组列表
	 * 
	 * @return 包含BASE_EMS_GROUP_ID GROUP_NAME
	 * @throws CommonException
	 */
	public Map getBaseEmsGroups() throws CommonException;

	/**
	 * 分页获取网管列表
	 * 
	 * @param emsGroupId
	 *            分组ID
	 * @param userId
	 *            当前用户ID
	 * @param startNumber
	 *            开始页码
	 * @param pageSize
	 *            页面大小
	 * @return BASE_EMS_GROUP_ID GROUP_NAME
	 * @throws CommonException
	 */
	public Map getEmsList(int emsGroupId, int userId, int startNumber,
			int pageSize) throws CommonException;

	/**
	 * 保存修改的网管列表
	 * 
	 * @param emsList
	 *            COLLEC_START_TIME COLLEC_END_TIME COLLECT_SOURCE
	 *            BASE_EMS_CONNECTION_ID
	 * @throws CommonException
	 */
	public void updateEmsList(List<Map> emsList) throws CommonException;

	/**
	 * 获取网元型号
	 * 
	 * @param emsId
	 *            网管Id
	 * @param type
	 *            网元类型(SDH,WDM etc)
	 * @return PRODUCT_NAME
	 * @throws CommonException
	 */
	public Map getProductNames(int emsId, int type) throws CommonException;

	/**
	 * 获取网元列表
	 * 
	 * @param emsId
	 *            网管ID
	 * @param type
	 *            网元类型(SDH,WDM etc)
	 * @param productName
	 *            网元型号
	 * @param @param startNumber 开始页码
	 * @param pageSize
	 *            页面大小
	 * @return EMS_DISPLAY_NAME BASE_NE_ID DISPLAY_NAME TYPE PRODUCT_NAME
	 *         NE_LEVEL COLLECT_NUMBIC COLLECT_PHYSICAL COLLECT_CTP
	 * @throws CommonException
	 */
	public Map getNeList(int emsId, int type, String productName,String subIds,
			int startNumber, int pageSize) throws CommonException;

	/**
	 * 保存修改的网元列表
	 * 
	 * @param neList
	 *            网元信息列表 BASE_NE_ID NE_LEVEL COLLECT_NUMBIC COLLECT_PHYSICAL
	 *            COLLECT_CTP
	 * @throws CommonException
	 */
	public void updateNeList(List<Map> neList) throws CommonException;

	/**
	 * 获取网元执行状态列表
	 * 
	 * @param emsId
	 *            网管ID
	 * @param type
	 *            网元类型(SDH,WDM etc)
	 * @param productName
	 *            网元型号
	 * @param subnetIdStr
	 * 			  一串子网ID
	 * @param startTime
	 * 			  开始时间
	 * @param endTime
	 * 			  结束时间
	 * @param startNumber
	 *            开始页码
	 * @param pageSize
	 *            页面大小
	 * @return EMS_DISPLAY_NAME BASE_NE_ID DISPLAY_NAME TYPE PRODUCT_NAME
	 *         NE_LEVEL LAST_COLLECT_TIME COLLECT_INTERVAL COLLECT_RESULT
	 * @throws CommonException
	 */
	public Map getNeStateListMulti(int emsId,Integer type, String productName,String subnetIdStr,String startTime,String endTime,
			int startNumber, int pageSize) throws CommonException;

	
	/**
	 * 单网元的执行状态
	 * @param neId
	 * @param startTime
	 * @param endTime
	 * @param startNumber
	 * @param pageSize
	 * @return
	 * @throws CommonException
	 */
	public Map getNeStateList(Integer neId,String startTime,String endTime,
			int startNumber, int pageSize) throws CommonException;
	/**
	 * 修改任务执行状态
	 * 
	 * @param taskInfoList
	 *            任务信息,包含SYS_TASK_ID与TASK_STATUS
	 * @throws CommonException
	 */
	public void changeTaskStatus(List<Map> taskInfoList) throws CommonException;

	/**
	 * 查询任务执行状态
	 * 
	 * @param emsId
	 *            网管ID
	 * @return 执行状态代码
	 * @throws CommonException
	 */
	public Integer getTaskStatus(int emsId) throws CommonException;

	/**
	 * 查询任务执行状态
	 * 
	 * @param emsId
	 *            网管ID
	 * @return 执行状态代码
	 * @throws CommonException
	 */
	public Integer getTaskCollectResult(int emsId) throws CommonException;

	/**
	 * 查询任务暂停时限
	 * 
	 * @param emsId
	 *            网管ID
	 * @return 暂停到什么时间
	 * @throws CommonException
	 */
	public Timestamp getForbiddenTimeLimit(int taskId) throws CommonException;

	/**
	 * 暂停任务
	 * 
	 * @param emsId
	 *            网管ID
	 * @param pauseTime
	 *            需要暂停多久(单位分钟)
	 * @throws CommonException
	 */
	public void pauseTask(int emsId, int pauseTime) throws CommonException;

	/**
	 * 恢复任务
	 * 
	 * @param emsId
	 *            网管ID
	 * @throws CommonException
	 */
	public void resumeTask(int emsId) throws CommonException;

	/**
	 * 新增网管时插入任务
	 * 
	 * @param emsId
	 * @param displayName
	 *            网管显示名称
	 * @throws CommonException
	 */
	public void insertPmTask(int emsId, String displayName)
			throws CommonException;

	/**
	 * 删除网管时删除任务
	 * 
	 * @param emsId
	 * @throws CommonException
	 */
	public void deletePmTask(int emsId) throws CommonException;

	/**
	 * 获取当前性能使用的模板信息
	 * 
	 * @param templateId
	 *            模板Id
	 * @param pmStdIndex
	 *            性能代号
	 * @param domain
	 * @return
	 * @throws CommonException
	 */
	public Map getCurrentPmTempleteInfo(int templateId, String pmStdIndex,
			int domain) throws CommonException;

	/**
	 * 查询并保存当前性能
	 * @param maxMin
	 * @param isSDH 是否为SDH性能查询
	 * @param nodeList tree上节点列表
	 * @param granularity 周期
	 * @param tpLevel tp等级
	 * @param pmStdIndexTypes 性能类型
	 * @param currentUserId
	 * @param searchType 查询类型【重保性能/性能查询】
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getCurrentPmData(boolean maxMin, boolean isSDH,
			List<Map> nodeList, int granularity, List<String> tpLevel,
			List<String> pmStdIndexTypes, int currentUserId,int searchType) throws CommonException;

	/**
	 * 查询并保存历史性能
	 * @param maxMin
	 * @param isSDH 是否为SDH性能查询
	 * @param nodeList tree上节点列表
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @param tpLevel tp等级
	 * @param pmStdIndexTypes 性能类型
	 * @param currentUserId
	 * @param searchType 查询类型【重保性能/性能查询】
	 * @return
	 * @throws CommonException
	 */
	public int getHistoryPmData(boolean maxMin, boolean isSDH, List<Map> nodeList,
			String startTime, String endTime, List<String> tpLevel,
			List<String> pmStdIndexTypes, int currentUserId,int searchType) throws CommonException;

	/**
	 * 從臨時表中查詢記錄
	 * 
	 * @param tableName
	 *            表名
	 * @param exception
	 *            是否异常性能数据?
	 * @param userId
	 *            当期那用户Id
	 * @param searchTag
	 *            插入时返回的查询标识
	 * @param startNumber
	 * @param pageSize
	 * @return
	 * @throws CommonException
	 */
	public Map getTempPmDataByPage(String tableName, int exception, int userId,
			Integer searchTag, int startNumber, int pageSize)
			throws CommonException;

	/**
	 * 從臨時表中导出記錄
	 * 
	 * @param tableName
	 *            表名
	 * @param exception
	 *            是否异常性能数据?
	 * @param userId
	 *            当期那用户Id
	 * @param searchTag
	 *            插入时返回的查询标识
	 * @return
	 */
	public String getPmExportedExcelPath(String tableName, int exception,
			int userId, Integer searchTag);

	/**
	 * 分页获取比较值
	 * 
	 * @param nodeList
	 *            节点列表
	 * @param startNumber
	 * @param pageSize
	 * @return
	 * @throws CommonException
	 */
	public Map getCompareValueByPage(List<Map> nodeList, int startNumber,
			int pageSize) throws CommonException;

	/**
	 * 更新比较值(页面手工填写)
	 * 
	 * @param compareValueList
	 *            PM_COMPARE_ID,PM_COMPARE_VALUE
	 * @throws CommonException
	 */
	public void updateCompareValueList(List<Map> compareValueList)
			throws CommonException;

	/**
	 * 生成比较值
	 * 
	 * @param nodeList
	 *            节点列表 (网管/子网/网元)
	 * @param overwrite
	 *            是否覆盖
	 * @param processKey
	 *            进度条
	 * @throws CommonException
	 */
	public String generateCompareValue(List<Map> nodeList, int overwrite,
			String processKey) throws CommonException;

	/**
	 * 设置性能值为比较值
	 * 
	 * @param pmList
	 *            每个Map包含:TARGET_TYPE,PM_STD_INDEX,PM_DESCRIPTION,PM_VALUE,
	 *            BASE_OTN_CTP_ID,BASE_SDH_CTP_ID,BASE_PTP_ID,BASE_UNIT_ID,
	 *            BASE_NE_ID,DISPLAY_CTP
	 * @throws CommonException
	 */
	public void setCompareValueFromPm(List<Map> pmList) throws CommonException;

	/**
	 * 获取昨天的性能采集状况
	 * 
	 * @param userId
	 * @return collectFailedNe collectSucceedNe pmException1 pmException2
	 *         pmException3
	 */
	public Map getIndexPagePmInfo(int userId) throws CommonException;

	/**
	 * 计算报表的统计信息
	 * 
	 * @param taskId
	 *            任务ID
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @param stepPath
	 *            步径
	 * @return
	 * @throws CommonException
	 */
	public Map calculateReportCountInfo(int taskId, Date startTime,
			Date endTime, int stepPath) throws CommonException;

	public Map calculateReportCountInfo(int taskId, Date startTime, Date endTime)
			throws CommonException;

	// ***********************************咯咯咯咯咯咯***************************************

	/**
	 * 获取所有模板列表
	 * 
	 * @param condMap
	 *            可能含有：factory，needAll，needNull
	 * @return Map 包括模板ID，模板名
	 * @throws CommonException
	 */
	public Map getTemplates(Map condMap) throws CommonException;

	/**
	 * 获取模板列表详细
	 * 
	 * @param factory
	 *            厂家
	 * @param start
	 *            分页参数
	 * @param limit
	 *            分页参数
	 * @return
	 */
	public Map getTemplatesInfo(int factory, int start, int limit)
			throws CommonException;

	/**
	 * 查询端口模板信息
	 * 
	 * @param nodeList
	 * @param searchCond
	 * 
	 * @return Map 端口信息和所用模板
	 * @throws CommonException
	 */
	public Map searchPtpTemplate(List<Map> nodeList,
			Map<String, String> searchCond) throws CommonException;

	/**
	 * 保存修改的端口模板应用信息
	 * 
	 * @param list
	 *            模板Id和端口Id
	 */
	public void savePtpTemplate(List<Map> list, int level)
			throws CommonException;

	/**
	 * 解除端口模板设置
	 * 
	 * @param list
	 *            包含模板ID
	 */
	public void cancelPtpTemplate(List<Long> list, int level) throws CommonException;
	
	

	/**
	 * 模板应用
	 * 
	 * @param list
	 *            包含选择的nodeID和nodeLevel
	 * @param searchCond
	 * @throws CommonException
	 */
	public boolean applyTemplate(List<Map> list, Map<String, String> searchCond)
			throws CommonException;

	/**
	 * 批量解除模板
	 * 
	 * @param list
	 *            包含选择的nodeID和nodeLevel
	 * @param searchCond
	 * @throws CommonException
	 */
	public void cancelTemplateBatch(List<Map> list, Map searchCond)
			throws CommonException;

	/**
	 * 获取模板计数值详细信息
	 * 
	 * @param templateId
	 * @return
	 * @throws CommonException
	 */
	public Map getNumberic(int templateId) throws CommonException;

	/**
	 * 获取模板物理量详细信息
	 * 
	 * @param templateId
	 * @return
	 * @throws CommonException
	 */
	public Map getPhysical(int templateId) throws CommonException;

	/**
	 * 新增一个模板，拷贝自参考模板
	 * 
	 * @param templateId
	 * @param templateName
	 * @throws CommonException
	 */
	public String newTemplate(int templateId, String templateName)
			throws CommonException;

	/**
	 * 保存模板计数值
	 * 
	 * @param list
	 *            计数值参数
	 */
	public void saveNumberic(List<Map> list) throws CommonException;

	/**
	 * 保存模板物理量
	 * 
	 * @param list
	 *            计数值参数
	 */
	public void savePhysical(List<Map> list) throws CommonException;

	/**
	 * 删除模板
	 * 
	 * @param templateIdList
	 *            要删除的id
	 * @throws CommonException
	 */
	public boolean deleteTemplate(List<Long> templateIdList)
			throws CommonException;

	/**
	 * 根据选中的模板来解除模板
	 * 
	 * @param templateIdList
	 * @param userId
	 * @throws CommonException
	 */
	public void detachTemplate(List<Long> templateIdList, Integer userId)
			throws CommonException;

	/**
	 * 生成本端趋势图
	 * 
	 * @param searchCond
	 *            (ptpid,ctpid,needLimit,pmStdIndex)
	 * @return
	 * @throws CommonException
	 */
	public String generateDiagramNend(Map<String, String> searchCond)
			throws CommonException;

	/**
	 * 生成远端趋势图
	 * 
	 * @param searchCond
	 *            (ptpid,ctpid,needLimit,pmStdIndex)
	 * @return
	 * @throws CommonException
	 */
	public String generateDiagramFend(Map<String, String> searchCond)
			throws CommonException;

	/**
	 * 加载光口标准combo
	 * 
	 * @param searchCond
	 *            (domain,ptpType)
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getOptStdComboValue(
			Map<String, String> searchCond) throws CommonException;

	/**
	 * @return 加载光模块combo
	 * @throws CommonException
	 */
	public Map<String, Object> getOptModelComboValue(
			Map<String, String> searchCond) throws CommonException;

	/**
	 * 搜索ptp的光口标准应用信息
	 * 
	 * @param nodeList
	 * @param emsIds
	 * @param searchCond
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> searchPtpOptModelInfo(List<Map> nodeList,
			List<Long> emsIds, Map<String, String> searchCond)
			throws CommonException;

	/**
	 * 保存修改的光口标准应用信息
	 * 
	 * @param list
	 *            标准Id和端口Id
	 */
	public void savePtpOptStdApplication(List<Map> list) throws CommonException;

	/**
	 * 查询光口标准详细
	 * 
	 * @param searchCond
	 * @return
	 */
	public Map searchOptStdDetail(Map<String, String> searchCond)
			throws CommonException;

	/**
	 * 修改光口标准内容
	 * 
	 * @param optStdDetailList
	 * @throws CommonException
	 */
	public void saveOptStdDetail(List<Map> optStdDetailList)
			throws CommonException;

	/**
	 * 新增光口标准
	 * 
	 * @param searchCond
	 * @throws CommonException
	 */
	public void saveNewOptStd(Map<String, String> searchCond)
			throws CommonException;

	/**
	 * 删除光口标准
	 * 
	 * @param condList
	 * @param searchCond
	 */
	public void deleteOptStd(List<Long> condList, Map<String, String> searchCond)
			throws CommonException;

	/**
	 * 查询所选节点上的光模块
	 * 
	 * @param nodeList
	 * @param searchCond
	 * @param condList
	 * @return
	 * @throws CommonException
	 */
	public Map getOptModelFromNodes(List<Map> nodeList,
			Map<String, String> searchCond, List<Long> emsIds)
			throws CommonException;

	/**
	 * 获取光口标准值
	 * 
	 * @param searchCond
	 * @return
	 * @throws CommonException
	 */
	public Map getOptStdInfo(Map<String, String> searchCond)
			throws CommonException;

	/**
	 * 将光口标准应用到节点上
	 * 
	 * @param nodeList
	 * @param emsIds
	 * @param searchCond
	 * @throws CommonException
	 */
	public void applyPtpOptStdBatch(List<Map> nodeList, List<Long> emsIds,
			Map<String, String> searchCond) throws CommonException;

	/**
	 * 检测新的标准名是否存在
	 * 
	 * @param searchCond
	 * @return
	 * @throws CommonException
	 */
	public boolean checkOptStdName(Map<String, String> searchCond)
			throws CommonException;

	/**
	 * 检测标准是否已应用
	 * 
	 * @param searchCond
	 * @return
	 * @throws CommonException
	 */
	public int checkIfStdApplied(Map<String, String> searchCond)
			throws CommonException;

	/**
	 * 自动应用光口标准
	 * 
	 * @param nodeList
	 * @param emsIds
	 * @param processBarKey
	 * @return 
	 */
	public String autoApplyPtpOptStd(List<Map> nodeList, List<Long> emsIds,
			String processBarKey) throws CommonException;

	/**
	 * 查找用户组信息
	 * 
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getPrivilegeList() throws CommonException;

	/**
	 * 补全node信息
	 * 
	 * @param modifyList
	 * @return
	 */
	public Map<String, Object> getNodeInfo(List<Map> nodeList)
			throws CommonException;

	/**
	 * 保存报表任务（网元）
	 * 
	 * @param nodeList
	 * @param condList
	 * @param currentUserId
	 * @return
	 */
	public int saveNeReportTask(List<Map> nodeList,
			Map<String, String> searchCond, int currentUserId)
			throws CommonException;

	/**
	 * 查询报表任务
	 * 
	 * @param searchCond
	 * @param userId
	 * @return
	 */
	public Map<String, Object> searchReportTask(Map<String, String> searchCond,
			Integer userId) throws CommonException;

	/**
	 * 查询创建人下拉框数据
	 * 
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getCreatorComboValue(Integer userId)
			throws CommonException;

	/**
	 * 查询创建人下拉框数据（privilege为依据）
	 * 
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getCreatorComboValuePrivilege(Integer userId)
			throws CommonException;

	/**
	 * 查询任务名称下拉框数据
	 * 
	 * @param searchCond
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getTaskNameComboValue(
			Map<String, String> searchCond) throws CommonException;

	/**
	 * 删除报表任务
	 * 
	 * @param searchCond
	 */
	public void deleteReportTask(Map<String, String> searchCond)
			throws CommonException;

	/**
	 * 报表任务名判重
	 * 
	 * @param searchCond
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	public Long checkTaskNameDuplicate(Map<String, String> searchCond,
			Integer userId) throws CommonException;

	/**
	 * 通用任务名判重
	 * 
	 * @param searchCond
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	public Long checkTaskNameDuplicate(Map<String, String> searchCond,
			Integer userId, int[] taskTypes) throws CommonException;

	/**
	 * 获取网管分组
	 * 
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getEmsGroup() throws CommonException;

	/**
	 * 获取网管
	 * 
	 * @param searchCond
	 * @return
	 */
	public Map<String, Object> getEms(Map<String, String> searchCond)
			throws CommonException;

	/**
	 * 获取干线
	 * 
	 * @param searchCond
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getTrunkLine(Map<String, String> searchCond)
			throws CommonException;

	/**
	 * 获取复用段信息
	 * 
	 * @param searchCond
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> searchMS(Map<String, String> searchCond)
			throws CommonException;

	/**
	 * 获取干线信息
	 * 
	 * @param searchCond
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> searchTL(Map<String, String> searchCond)
			throws CommonException;

	/**
	 * 保存复用段报表任务
	 * 
	 * @param nodeList
	 * @param searchCond
	 * @param currentUserId
	 * @throws CommonException
	 */
	public void saveMSReportTask(List<Map> nodeList,
			Map<String, String> searchCond, int currentUserId)
			throws CommonException;

	/**
	 * 获得修改的报表任务信息
	 * 
	 * @param searchCond
	 * @return
	 */
	public Map<String, Object> initMSReportTaskInfo(
			Map<String, String> searchCond) throws CommonException;

	/**
	 * 更新任务信息-MS任务
	 * 
	 * @param nodeList
	 * @param searchCond
	 * @throws CommonException
	 */
	public void updateMSReportTask(List<Map> nodeList,
			Map<String, String> searchCond) throws CommonException;

	/**
	 * 初始化新任务信息-NE任务
	 * 
	 * @param searchCond
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> initNEReportTaskInfo(
			Map<String, String> searchCond) throws CommonException;

	/**
	 * 更新任务信息-NE任务
	 * 
	 * @param nodeList
	 * @param searchCond
	 * @throws CommonException
	 */
	public void updateNeReportTask(List<Map> nodeList,
			Map<String, String> searchCond) throws CommonException;

	/**
	 * 测试用
	 * 
	 * @param searchCond
	 * @throws CommonException
	 */
	public void searchPMForReportNeDaily(Map<String, String> searchCond)
			throws CommonException;

	/**
	 * 测试用
	 * 
	 * @param searchCond
	 * @throws CommonException
	 */
	public void searchPMForReportNeMonthly(Map<String, String> searchCond)
			throws CommonException;

	/**
	 * 查询报表的统计信息（网元）
	 * 
	 * @param searchCond
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> searchNeReportAnalysis(
			Map<String, String> searchCond) throws CommonException;

	/**
	 * 查询报表的统计信息（复用段）
	 * 
	 * @param searchCond
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> searchMSReportAnalysis(
			Map<String, String> searchCond) throws CommonException;

	/**
	 * 查询检测失败网元
	 * 
	 * @param searchCond
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> searchCollectFailedNeInfo(
			Map<String, String> searchCond) throws CommonException;

	/**
	 * 查询检测失败端口
	 * 
	 * @param searchCond
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> searchCollectFailedPtpInfo(
			Map<String, String> searchCond) throws CommonException;

	/**
	 * 查询检测失败复用段
	 * 
	 * @param searchCond
	 * @return
	 */
	public Map<String, Object> searchCollectFailedMSInfo(
			Map<String, String> searchCond) throws CommonException;

	/**
	 * 任务名combo加载（按照报表权限）
	 * 
	 * @param searchCond
	 * @return
	 * @throws CommonException
	 */
	Map<String, Object> getTaskNameComboValuePrivilege(
			Map<String, String> searchCond, Integer userId)
			throws CommonException;

	/**
	 * 查询报表详细信息Ne
	 * 
	 * @param searchCond
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> searchReportDetailNePm(
			Map<String, String> searchCond, int start, int limit)
			throws CommonException;

	/**
	 * 导出并下载报表的统计数据
	 * 
	 * @param searchCond
	 * @param exportData
	 * @return
	 * @throws CommonException
	 */
	public String exportAndDownloadPmAnalysisInfo(
			Map<String, String> searchCond, List<Map> exportData)
			throws CommonException;

	/**
	 * 查询复用段报表的详细性能情况
	 * 
	 * @param searchCond
	 * @param start
	 * @param limit
	 * @return
	 * @throws CommonException
	 */
	Map<String, Object> searchReportDetailMSPm(Map<String, String> searchCond,
			int start, int limit) throws CommonException;

	/**
	 * 整理nodelist
	 * 
	 * @param nodeList
	 * @return
	 */
	public Map<String, String> nodeListClassify(List<Map> nodeList);

	/**
	 * 该方法遍历所有报表任务，根据 网管的采集结束时间设定报表 定时任务的执行时间。
	 * 
	 * @throws CommonException
	 * 
	 * @throws JobExecutionException
	 */
	void controlReportTaskTime() throws CommonException;
	
	/**
	 * 建立QUARTZ任务
	 * 
	 * @param taskId
	 * @param hour
	 * @param periodType
	 * @param taskType
	 * @param delay
	 * @throws CommonException
	 */
	public void addOrEditReportQuartzTask(int taskId, int hour,
			int periodType, int taskType, int delay) throws CommonException;
	/**
	 * 建立QUARTZ任务
	 * 
	 * @param taskId
	 * @param hour
	 * @param periodType
	 * @param taskType
	 * @param delay
	 * @throws CommonException
	 */
	public void addOrEditReportQuartzTask(int taskId, int hour,int minute,
			int periodType, int taskType, int delay,Class Job) throws CommonException;

	/**
	 * 保存复用段割接任务
	 * 
	 * @param condList
	 * @param searchCond
	 * @param userId
	 * @return
	 */
	public void saveMSCutoverTask(List<Long> condList,
			Map<String, String> searchCond, Integer userId)
			throws CommonException;

	/**
	 * 查询复用段割接任务
	 * 
	 * @param searchCond
	 * @param userId
	 * @param limit
	 * @param start
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> searchCutoverTask(
			Map<String, String> searchCond, Integer userId, int limit, int start)
			throws CommonException;

	/**
	 * 加载任务名下拉框
	 * 
	 * @param searchCond
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> loadTaskNameCombo(
			Map<String, String> searchCond, Integer userId)
			throws CommonException;

	/**
	 * 获取任务中包含的复用段
	 * 
	 * @param searchCond
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getMSById(Map<String, String> searchCond)
			throws CommonException;

	/**
	 * 修改割接任务
	 * 
	 * @param searchCond
	 * @param condList
	 * @throws CommonException
	 */
	public void updateMSTask(Map<String, String> searchCond, List<Long> condList)
			throws CommonException;

	/**
	 * 删除MS割接任务
	 * 
	 * @param condList
	 * @throws CommonException
	 */
	public void deleteCutoverTask(List<Long> condList) throws CommonException;

	// TODO
	// ***********************************咯咯咯咯咯咯***************************************
	/**
	 * 查找光路衰耗信息
	 * 
	 * @param opticalPathMonitorModel
	 *            查询条件
	 * @param startNumber
	 * @param pageSize
	 * @return
	 * @throws CommonException
	 */
	public Map searchOpticalPath(
			OpticalPathMonitorModel opticalPathMonitorModel, int startNumber,
			int pageSize) throws CommonException;

	/**
	 * 修改链路att属性
	 * 
	 * @param list
	 * @throws CommonException
	 */
	public void modifyAttForLink(List<OpticalPathMonitorModel> list)
			throws CommonException;

	/**
	 * 刷新当前值
	 * 
	 * @param list
	 * @throws CommonException
	 */
	public void fulshCurrentPm(List<OpticalPathMonitorModel> list)
			throws CommonException;

	/**
	 * 获取偏差值信息
	 * 
	 * @return
	 * @throws CommonException
	 */
	public Map getOffsetValue() throws CommonException;

	/**
	 * 修改偏差值信息
	 * 
	 * @throws CommonException
	 */
	public void modifyOffsetValue(String upperOffset, String downOffset)
			throws CommonException;

	/**
	 * @param nodeList
	 * @param searchCond
	 * @param currentUserId
	 * @return
	 * @throws CommonException
	 */
	ExportResult generateNeReportImmediately(List<Map> nodeList,
			Map<String, String> searchCond, int currentUserId)
			throws CommonException;

	// ------------------------- MeiKai Start -------------------------

	/**
	 * 获取性能报表列表
	 * 
	 * @param map
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getReportInfoList(Map<String, Object> map,
			int userId) throws CommonException;

	/**
	 * 打包性能报表
	 * 
	 * @param filePathList
	 * @return
	 * @throws CommonException
	 */
	public CommonResult zipReport(List<String> filePathList,String fileName)
			throws CommonException;

	/**
	 * 判断用户是否有删除性能报表的权限
	 * 
	 * @param pmReportIdList
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	public CommonResult preDeleteReport(List<Integer> pmReportIdList, int userId)
			throws CommonException;

	/**
	 * 删除性能报表
	 * 
	 * @param pmReportIdList
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	public CommonResult deleteReport(List<Integer> pmReportIdList, int userId)
			throws CommonException;

	/**
	 * 查询复用段信息
	 * 
	 * @param searchCond
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> searchMultiplexSection(
			Map<String, String> searchCond) throws CommonException;

	/**
	 * 获取我的用户组
	 * 
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	public List<Integer> myUserGroup(Integer userId) throws CommonException;
	
	/**
	 * 根据nodeList获取网元ID列表
	 * @param nodeList
	 * @return
	 */
	public List<Integer> getNeIdsFromNodes(List<Map> nodeList);

	// ------------------------- MeiKai End -------------------------
	
	/**
	 * 将节点分网管筛选
	 * 
	 * @param nodeList
	 * @return
	 */
	public Map<Integer, Map<String, Object>> getConditionsFromNodesGroupByEmsIds(
			List<Map> nodeList);
	
	/**
	 * 变成[nodeId，nodeLevel，emsId]的格式
	 * 
	 * @param taskNodes
	 * @return
	 */
	public List<Map> processAllNodesToStdFormat(List<Map> taskNodes);

	public int getPMReportParamForFP(int flag,int userId) throws CommonException;
	
	public int getPMCollParamForFP(int flag,int userId) throws CommonException;

	/**
	 * 
	 * @param ptpInfoList 端口信息（包括emsId，unitId，neId，ptpId）
	 * @param neInfoList  网元信息（包括emsId，neId）
	 * @param sysUserId   用户Id
	 * @param granularity 周期15min/24h
	 * @param needUnitPm  是否查询板卡级性能
	 * @param needPtpPm   是否查询端口级性能
	 * @param needCtpPm   是否查询时隙性能
	 * @author 333
	 * @return searchTag
	 */
	public Map searchCurrentDataIntoTempTableCir(List<Map> ptpInfoList,
			List<Map> neInfoList, 
			Integer sysUserId, Integer granularity, 
			boolean needUnitPm, boolean needPtpPm, boolean needCtpPm,int searchType) throws CommonException;

	/**
	 * 电路用历史性能
	 * @param ptpInfoList	端口信息（包括emsId，unitId，neId，ptpId）
	 * @param neInfoList	网元信息（包括emsId，neId）
	 * @param startTime
	 * @param endTime
	 * @param currentUserId
	 * @param searchType
	 * @param needNeLevelPm
	 * @param needUnitLevelPm
	 * @param needPtpLevelPm
	 * @author 333
	 * @return searchTag
	 * @throws CommonException
	 */
	int getHistoryPmDataCir(List<Map> ptpInfoList, List<Map> neInfoList,
			String startTime, String endTime, int currentUserId,
			int searchType, boolean needNeLevelPm, boolean needUnitLevelPm,
			boolean needPtpLevelPm) throws CommonException;


	/**
	 * @param mapList 数据集
	 * @param key1 需要被分类的字段
	 * @param key2 分类依据的字段
	 * @param c  1:Integer,2:Long,3:String
	 * @author 333
	 * @return  [{key2_1:"key1_1,key1_2,key1_3"},{key2_2:"key1_4,key1_5"}]
	 */
	public Map<String, List> key1ByKey2(List<Map> mapList,String key1,String key2,int c);

	/**
	 * 获取性能分组
	 * @param searchCond
	 * @return
	 * @throws CommonException 
	 */
	public String getPmStdIndexType(Map<String, String> searchCond) throws CommonException;

}
