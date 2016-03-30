package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;

public interface IInspectManagerService {

	/*-------------------------------------包机人--------------------------------------------*/
	
	/**
	 * 包机人查询:查询所有包机人的信息
	 * 返回的Map中分别为List<Map>类型的包机人信息、String类型的包机人信息条数
	 * 
	 * @param startNumber pageSize - 分页参数
	 * @return Map<String,Object>
	 * @throws CommonException
	 */
	public Map<String,Object> getInspectEngineerList (int start, int limit) throws CommonException;

	/**
	 * 包机人所属区域信息加载
	 * 返回的List<Map>是需加载的区域信息
	 * 
	 * @param int level - 区域级别
	 * @return List<Map>
	 * @throws CommonException
	 */
	public List<Map> getAreaList (int level) throws CommonException;
	
	/**
	 * 判断包机人工号是否重复
	 * 返回是否存在信息：true 存在/false 不存在
	 * 
	 * @param JobNo - 包机人工号
	 * @param engineerId - 包机人id
	 * @return Boolean true/false
	 * @throws CommonException
	 */
	public Boolean checkJobNoExist (Map map) throws CommonException;
	
	/**
	 * 新增包机人
	 * 返回的String,新增包机人是否成功
	 * 
	 * @param Map - 包机人信息
	 * @return String
	 * @throws CommonException
	 */
	public void addInspectEngineer (Map map, List<String> inspectEquipList) throws CommonException;
	
	/**
	 * 修改包机人
	 * 返回的String,修改包机人是否成功
	 * 
	 * @param Map - 包机人信息
	 * @return String
	 * @throws CommonException
	 */
	public void updateInspectEngineer (Map map, List<String> inspectEquipList,List<String> inspectEquipNameList) throws CommonException;
	
	
	/**
	 * 修改包机人/巡检任务页面初始化
	 * 返回的List<Map>是巡检设备列表
	 * 
	 * @param int id - 包机人ID/巡检任务ID
	 * @param int flag - 标志信息：1包机人/2巡检任务
	 * @return List<Map>
	 * @throws CommonException
	 */
	public List<Map> getInspectEquipList (int id, int flag) throws CommonException;
	
	/**
	 * 修改包机人页面初始化
	 * 返回的Map是包机人基本信息
	 * 
	 * @param int engineerId - 包机人ID
	 * @return Map<String,Object>
	 * @throws CommonException
	 */
	public Map<String,Object> getInspectEngineerInfo (int engineerId) throws CommonException;
	
	/**
	 * 删除包机人
	 * 返回删除包机人是否成功信息
	 * 
	 * @param List<Integer> engineerIdList - 包机人Id列表
	 * @return void
	 * @throws CommonException
	 */
	public void deleteInspectEngineer (List<Integer> engineerIdList) throws CommonException;
	
	/**
	 * 导出包机人
	 * 返回删除包机人是否成功信息
	 * 
	 * @param List<Integer> engineerIdList - 包机人Id列表
	 * @return void
	 * @throws CommonException
	 */
	public CommonResult exportInspectEngineer (List<Integer> engineerIdList) throws CommonException;
	/*-------------------------------------巡检报告--------------------------------------------*/
	
	/**
	 * 巡检报告页面初始化，筛选时间数据获取
	 * 返回的List<Map>是巡检报告Combobox时间数据
	 * 
	 * @return List<Map>
	 * @throws CommonException
	 */
	public List<Map> getDateLimitList () throws CommonException;
	
	/**
	 * 巡检报告页面初始化:查询用户有权限查看的所有巡检报告
	 * 返回的Map中分别为List<Map>类型的巡检报告详细信息、String类型的巡检报告信息条数
	 * 
	 * @param Map - 当前用户Id,分页参数,巡检时间，任务名称，创建人
	 * @return Map<String,Object>
	 * @throws CommonException
	 */
	public Map<String,Object> getInspectReportList (Map map) throws CommonException;
	
	/**
	 * 删除巡检报告
	 * 返回删除巡检报告是否成功信息
	 * 
	 * @param List<Integer> reportIdList - 巡检报告Id列表
	 * @return void
	 * @throws CommonException
	 */
	public void deleteInspectReport (List<Integer> reportIdList) throws CommonException;
	
	/*-------------------------------------巡检任务--------------------------------------------*/
	
	/**
	 * 巡检任务查询:查询所有巡检任务
	 * 返回的Map中分别为List<Map>类型的巡检任务信息、String类型的巡检任务信息条数
	 * 
	 * @param start limit - 分页参数
	 * @return Map<String,Object>
	 * @throws CommonException
	 */
	public Map<String,Object> getInspectTaskList (int start, int limit) throws CommonException;
	
	/**
	 * 判断巡检任务名是否重复
	 * 返回是否存在信息：true 存在/false 不存在
	 * 
	 * @param taskName - 巡检任务名  taskId - 巡检任务Id
	 * @return Boolean true/false
	 * @throws CommonException
	 */
	public Boolean checkTaskNameExist (Map map) throws CommonException;
	
	/**
	 * 获取操作权限组列表:查询所有操作权限组的信息
	 * 返回的Map中分别为List<Map>类型的用户组信息、String类型的用户组条数
	 * 
	 * @return Map<String,Object>
	 * @throws CommonException
	 */
	public Map<String,Object> getPrivilegeList () throws CommonException;
	
	/**
	 * 获取当前登录用户所在组ID
	 * 返回的Map中是登录用户所在分组的Id
	 * 
	 * @param userId - 当前登录用户的Id
	 * @return Map<String,Object>
	 * @throws CommonException
	 */
	public Map<String,Object> getCurrentUserGroup (int userId) throws CommonException;
	
	/**
	 * 新增巡检任务
	 * 返回的String,新增巡检任务是否成功
	 * 
	 * @param Map - 巡检任务信息
	 * @return String
	 * @throws CommonException
	 */
	public void addInspectTask (Map map, List<String> inspectEquipList, List<String> inspectEquipNameList) throws CommonException;
	
	public void addInspectTaskJob (Map map) throws CommonException;
	
	/**
	 * 修改巡检任务页面初始化
	 * 返回的List<Map>是巡检任务信息列表
	 * 
	 * @param int inspectTaskId - 巡检任务ID
	 * @return List<Map>
	 * @throws CommonException
	 */
	public Map<String,Object> getInspectTaskInfo (int inspectTaskId) throws CommonException;
	
	/**
	 * 修改巡检任务保存
	 * 返回的String,修改巡检任务是否成功
	 * 
	 * @param Map - 巡检任务信息
	 * @return String
	 * @throws CommonException
	 */
	public void updateInspectTask (Map map, List<String> inspectEquipList, List<String> inspectEquipNameList) throws CommonException;
	
	/**
	 * 删除巡检任务
	 * 返回删除巡检任务是否成功信息
	 * 
	 * @param List<Integer> taskIdList - 巡检任务Id列表
	 * @return void
	 * @throws CommonException
	 */
	public void deleteInspectTask (List<Integer> taskIdList) throws CommonException;
	
	/**
	 * 立即执行巡检任务
	 * 返回立即执行巡检任务是否成功信息
	 * 
	 * @param int inspectTaskId - 巡检任务id
	 * @return void
	 * @throws CommonException
	 */
	public void startTaskImmediately (String inspectTaskId) throws CommonException;
	
	/**
	 * 巡检任务启用、挂起
	 * 返回启用、挂起巡检任务是否成功信息
	 * 
	 * @param int inspectTaskId - 巡检任务Id
	 * @return void
	 * @throws CommonException
	 */
	public void changeInspectTaskStatus (List<Integer> taskIdList,int statusFlag) throws CommonException;
	
	/**
	 * 巡检任务执行情况信息获取
	 * 返回的Map中分别为List<Map>类型的巡检任务执行情况信息
	 * 
	 * @param inspectTaskId - 巡检任务Id
	 * @return Map<String,Object>
	 * @throws CommonException
	 */
	public Map<String,Object> getTaskRunDetial (int inspectTaskId) throws CommonException;
	
	/**取得保护组
	 * @param neId
	 * @param SCHEMA_STATE
	 * @return
	 * @throws CommonException
	 */
	public List<Map> getProtectGroups(int neId,List<Integer> SCHEMA_STATE) throws CommonException;
	/**取得设备保护组
	 * @param neId
	 * @param SCHEMA_STATE
	 * @return
	 * @throws CommonException
	 */
	public List<Map> getEProtectGroups(int neId,List<Integer> SCHEMA_STATE) throws CommonException;
	/**取得WDM保护组
	 * @param neId
	 * @param SCHEMA_STATE
	 * @return
	 * @throws CommonException
	 */
	public List<Map> getWDMProtectGroups(int neId,List<Integer> SCHEMA_STATE) throws CommonException;
	/**取得被保护设备
	 * @param category 保护类别
	 * @param pgId 保护组ID
	 * @param param 参数
	 * @return
	 * @throws CommonException
	 */
	public List<Map<String,Object>> getProtectedList(Integer category,Integer pgId,Map<String, Object> param) throws CommonException;
	/**取得时钟源
	 * @param neId
	 * @return
	 * @throws CommonException
	 */
	public List<Map> getClockSources(int neId) throws CommonException;
	
	/**取得资源信息
	 * @param roomId
	 * @return 区域、局站、机房信息
	 * @throws CommonException
	 */
	public Map getResourceInfoByRoom(int roomId) throws CommonException;
	
	/**取得包机人信息
	 * @param nodes
	 * @return List<Map>
	 * @throws CommonException
	 */
	public List<Map> getEngineerByNodes(List<Map> nodes) throws CommonException;
	
	/**取得端口类型
	 * @param neId
	 * @return List<Map>
	 * @throws CommonException
	 */
	public List<Map> getPtpTypeByNe(int neId) throws CommonException;
	
	/**统计指定网元指定类型的端口数
	 * @param neId
	 * @param ptpType
	 * @return int
	 * @throws CommonException
	 */
	public int CountNePtpByType(int neId,String ptpType) throws CommonException;

	/**统计具有交叉连接的端口数
	 * @param neId
	 * @return int
	 * @throws CommonException
	 */
	public int CountNePtpInUSE(int neId) throws CommonException;

	/**统计端口数
	 * @param neId
	 * @return int
	 * @throws CommonException
	 */
	public int CountNePtp(int neId) throws CommonException;

	/**统计具有交叉连接的通道数
	 * @param neId
	 * @return int
	 * @throws CommonException
	 */
	public int CountNeCtpInUSE(int neId) throws CommonException;

	/**统计通道数
	 * @param neId
	 * @return int
	 * @throws CommonException
	 */
	public int CountNeCtp(int neId) throws CommonException;
	
	public String getReportUrl(Integer reportId,boolean zip,String[] selectList) throws CommonException;
}