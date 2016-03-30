package com.fujitsu.dao.mysql;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.fujitsu.common.CommonException;
import com.fujitsu.model.UserGroupModel;
import com.fujitsu.model.UserModel;


public interface SystemManagerMapper {
	public Map getUserbyName(@Param(value = "username") String username);
	//用户管理，获取所有用户组
	public List<Map<String,Object>> getUserGroup(@Param(value = "userGroupId") int userGroupId);
	//获取某用户组用户数量
	public int countUserList(@Param(value = "userGroupId") int userGroupId);
	//获取某用户组的所有用户信息
	public List<Map<String,Object>> getUserListByGroupId(@Param(value = "userGroupId") int userGroupId, @Param(value = "start")int start, @Param(value = "limit")int limit);
	// 获取部门列表
	public List<Map<String,Object>> getDepartment();
	//获取职务列表
	public List<Map<String,Object>> getPosition();
	//获取全部权限域
	public List<Map<String,Object>> getDeviceDomain();
	//获取全部设备域
	public List<Map<String,Object>> getAuthDomain();
	//获取新增用户的ID
	public Integer getNewUserID(@Param(value = "map")Map map);
	//获取用户
	public Map getUserById(@Param(value = "userId")int userId);
	//新增用户基础信息
	public void addUserBaseDetail(@Param(value = "userModel")UserModel userModel);
	//新增用户权限域
	public void addUserRefAuth(@Param(value = "map")Map map);
	//新增用户设备域
	public void addUserRefDevice(@Param(value = "map")Map map);
	//修改用户基本信息
	public void updateUserBaseDetail(@Param(value = "userModel")UserModel userModel);
	//删除用户设备域
	public void deleteUserDeviceDomain(@Param(value = "userModel")UserModel userModel);
	//删除用户权限域
	public void deleteUserAuthDomain(@Param(value = "userModel")UserModel userModel);
	// 删除用户
	public Integer deleteUser(@Param(value = "map")Map map);
	//更新用户信息
	public void updateUserInfo(@Param(value = "map")Map map);
	//判断用户的工号，登录名，手机号是否重复
	public int userValidate(@Param(value = "map")Map map);
	//获取当前用户的权限域 
	public List<Map<String,Object>> getCurrrentAuthDomain(@Param(value = "sysUserId") int sysUserId);
	//获取当前用户的设备域 
	public List<Map<String,Object>> getCurrrentDeviceDomain(@Param(value = "sysUserId") int sysUserId);
	//获取当前用户的基本信息
	public List<Map<String,Object>> getCurrentUserBaseDetail(@Param(value = "sysUserId") int sysUserId);

	/**
	 * 用户登陆
	 * @return
	 */
	public List<Map> getUserByNameAndPass(@Param(value = "userName") String userName,@Param(value = "password") String password);

	
	public List<Map<String,Object>> getGroupDetailList( @Param(value = "start")int start, @Param(value = "limit")int limit);
	public int countGroupList(@Param(value = "map")Map map);
	public List<Map<String,Object>> getAllGroupUserList(@Param(value = "saveType") String saveType,@Param(value = "userGroupId") String userGroupId);
	public Integer addGroupDetail(@Param(value = "userGroupModel")UserGroupModel userGroupModel);
	public void addRefOfUserGroup(@Param(value = "map")Map map);
	public List<Map<String,Object>> getModiPreGroupBaseById(@Param(value = "userGroupId") int userGroupId);
	public void updatePreGroupBase(@Param(value = "userGroupModel")UserGroupModel userGroupModel);
	public void deleteGroupUser (@Param(value = "userGroupModel")UserGroupModel userGroupModel);
	public void deleteGroupAllUser (@Param(value = "map")Map map);
	public void deleteGroup (@Param(value = "map")Map map);
	public List<Map<String,Object>> getCurrentGroupDetailById(@Param(value = "userGroupId") int userGroupId);
	public int validateUserInformation(@Param(value = "sign") String sign,@Param(value = "userId") String userId,@Param(value = "value") String value);
	public String getOldPassByUserId(@Param(value = "userId") String userId);
	public void saveModifyPass(@Param(value = "userModel")UserModel userModel);
	public int validateUserGroupName(@Param(value = "groupName")String groupName);
	
	/**
	 * 获取接入服务参数
	 * @param emsConnectionId
	 * @return
	 */
	public Map selectSvcRecordByEmsconnectionId(@Param(value = "emsConnectionId")int emsConnectionId);
	
	/**
	 * 获取当前用户权限管理域详细信息
	 * @param userId
	 * @return
	 */
	public List<Map> getCurrrentAuthDomainDetail(@Param(value = "userId") int userId);
	
	/**
	 *  获取当前用户设备管理域详细信息
	 * @param userId
	 * @return
	 */
	public List<Map> getCurrrentDeviceDomainDetail(@Param(value = "userId") int userId);
	/**
	 * Method name: getAllServers <BR>
	 * Description: 查询所有服务器<BR>
	 * Remark: 2014-02-23<BR>
	 * @author CaiJiaJia
	 * @return List<Map<String, Object>><BR>
	 */
	public List<Map<String, Object>> getAllServers() throws CommonException;
	
	/**
	 * 通过网管ID列表获取taskIds
	 * @param emsIds
	 * @return
	 */
	public List<Map<String, Object>> getTaskIdsByEMSIDList(@Param(value = "emsIds") List<Integer> emsIds);
	
	
	/**
	 * 获取任务启用状态个数
	 * @param taskIds
	 * @param taskType
	 * @return
	 */
	public Map<String, Object> getTaskStartStatusNum(@Param(value = "map") Map<String, Object> select, 
			@Param(value = "taskType") int taskType);
	
	/**
	 * 获取成功执行个数
	 * @param taskIds
	 * @param taskType
	 * @return
	 */
	public Map<String, Object> getTaskSuccessNum(@Param(value = "map") Map<String, Object> select, 
			@Param(value = "taskType") int taskType);
	
	/**
	 * 获取任务数
	 * @param select
	 * @param taskType
	 * @return
	 */
	public Map<String, Object> getTaskNum(@Param(value = "map") Map<String, Object> select, 
			@Param(value = "taskType") int taskType);
	
	/**
	 * 获取性能报表任务数
	 * @param select
	 * @return
	 */
	public Map<String, Object> getPMReportTaskNum(@Param(value = "map") Map<String, Object> select);
	
	/**
	 * 获取性能报表启用任务数
	 * @param select
	 * @return
	 */
	public Map<String, Object> getPMReportStartStatusNum(@Param(value = "map") Map<String, Object> select);
	
	/**
	 * 获取性能报表生成任务成功执行个数
	 * @param taskIds
	 * @return
	 */
	public Map<String, Object> getPMReportSuccessNum(@Param(value = "map") Map<String, Object> select);
	
	
	/**
	 * 获取割接任务不同状态任务的数量
	 * @param userGrps
	 * @param userId
	 * @param taskStatus
	 * @return
	 */
	public Map<String, Object> getCutoverTaskNum(@Param(value = "userGrps") List<Map> userGrps,
			@Param(value = "userId") int userId, @Param(value = "taskStatus") String[] taskStatus);
	
	/**
	 * 获取割接任务通知信息
	 * @param startTime 预期开始查询时间
	 * @param endTime 预期结束查询时间
	 * @return 割接任务名、预计开始时间
	 */
	public List<Map<String, Object>> getCutoverTaskInfoForNotice(
			@Param(value = "startTime") String startTime,
			@Param(value = "endTime") String endTime);
	
	public int userGroupisExists(@Param(value = "type") int type,@Param(value = "groupId") String groupId);
	public int userisExists(@Param(value = "type") int type,@Param(value = "userId") String string);
	public List<Map> getAllUserToGroupByCondition(@Param(value = "map") Map map);
}