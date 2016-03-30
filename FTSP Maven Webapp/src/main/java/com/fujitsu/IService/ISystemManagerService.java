package com.fujitsu.IService;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.manager.systemManager.model.LogModel;
import com.fujitsu.model.UserGroupModel;
import com.fujitsu.model.UserModel;



public interface ISystemManagerService {

	/**
	 * @param username(用户账号)
	 * @return 用户信息
	 */
	
	//用户管理，获取所有用户组
		public Map<String,Object> getUserGroup(int userGroupId) throws CommonException;
		//获取某用户组的所有用户信息
		public Map<String,Object> getUserListByGroupId(int userGroupId,int start, int limit) throws CommonException;
		// 获取部门列表
		public Map<String,Object> getDepartment() throws CommonException;
		//获取职务列表
		public Map<String,Object> getPosition() throws CommonException;
		//获取全部权限域
		public Map<String,Object> getDeviceDomain() throws CommonException;
		//获取全部设备域
		public Map<String,Object> getAuthDomain() throws CommonException;
		//获取新增用户ID
		public Integer getNewUserID(Map<String, Object> map)throws CommonException;
		//新增用户
		public void addUser(UserModel userModel) throws CommonException;
		//修改用户
		public void updateUser(UserModel userModel) throws CommonException;
		//获取当前用户信息
		public Map<String,Object> getCurrentUserDetail(int sysUserId) throws CommonException;
		//删除用户
		public void deleteUser(Map<String, Object> map) throws CommonException;
		//修改密码
		public void  updateUserPassword(Map<String, Object> map)throws CommonException;
		//判断用户的工号，登录名，手机号是否重复
		public Integer  userValidate(Map<String, Object> map)throws CommonException;
		
		
		
		
		
		
		//用户组信息查询
		public Map<String,Object> getGroupDetailList(int start, int limit) throws CommonException;
		//新增用户组时，获取全部用户
		public Map<String,Object> getAllGroupUserList(UserGroupModel userGroupModel) throws CommonException;
		//新增用户组
		public void addGroup(UserGroupModel userGroupModel) throws CommonException;
		//修改用户组时，修改某用户组的信息
		public void updateGroup(UserGroupModel userGroupModel) throws CommonException;
		//删除用户组
		public void deleteGroup (Map<String, Object> map) throws CommonException;
		//获取用户组详情 
		public Map<String,Object> getCurrentGroupDetailById (int userGroupId)throws CommonException;
		public Map validateUserInformation(UserModel userModel);
		public boolean confirmOldPassIsTrue(UserModel userModel);
		public void saveModifyPass(UserModel userModel);
		
		/**
		 * 登陆
		 * @param userModel
		 * @return
		 */
		public CommonResult login(String userName, String password)throws CommonException;
		
		/**
		 * 注销用户
		 * @return
		 */
		public CommonResult logout(Integer userId,Date time)throws CommonException;
		public Map validateUserGroupName(UserGroupModel userGroupModel)throws CommonException;
		/**
		 * 锁定用户
		 * @param userName 用户名
		 * @param password 密码
		 * @throws CommonException
		 */
		public void lock()throws CommonException;
		/**
		 * 解锁用户
		 * @param userName 用户名
		 * @param password 密码
		 * @throws CommonException
		 */
		public void unlock(String userName, String password)throws CommonException;
		/**
		 * Method name: getAllServers <BR>
		 * Description: 查询所有服务器<BR>
		 * Remark: 2014-02-23<BR>
		 * @author CaiJiaJia
		 * @return Map<String, Object><BR>
		 */
		public Map<String, Object> getAllServers() throws CommonException;
		
		/**
		 * 获取首页数据
		 * @param userId
		 * @return
		 * @throws CommonException
		 */
		public Map<String, Object> getParamFP(int userId) throws CommonException;
		/**
		 * 获取首页性能数据，单独抽出来，以防数据过大影响显示
		 * @param userId
		 * @return
		 * @throws CommonException
		 */
		public Map<String, Object> getIndexPmInfo(int intValue) throws CommonException ;
		public Map getBackupSettingValue(String key) throws CommonException ;
		public void saveBackupSettingValues(Map manuValue) throws CommonException ;
		public void changeDatabackupToCancel(String autoSettingValuePri) throws CommonException ;
		
		/**
		 * Method name: getJournals <BR>
		 * Description: 查询日志信息<BR>
		 * Remark: 2013-12-19<BR>
		 * @author CaiJiaJia
		 * @return Map<String, Object><BR>
		 * @throws ParseException
		 */
		public Map<String, Object> getJournals(LogModel model,int start,int limit)throws CommonException, ParseException;
}
