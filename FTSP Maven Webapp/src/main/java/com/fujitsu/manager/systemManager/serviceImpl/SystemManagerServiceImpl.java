package com.fujitsu.manager.systemManager.serviceImpl;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.IService.IAlarmManagementService;
import com.fujitsu.IService.IPerformanceManagerService;
import com.fujitsu.IService.ISouthConnectionService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.dao.mysql.CircuitManagerMapper;
import com.fujitsu.dao.mysql.AlarmManagementMapper;
import com.fujitsu.dao.mysql.PerformanceManagerMapper;
import com.fujitsu.dao.mysql.SystemManagerMapper;
import com.fujitsu.handler.MessageHandler;
import com.fujitsu.manager.systemManager.model.LogModel;
import com.fujitsu.manager.systemManager.service.SystemManagerService;
import com.fujitsu.manager.systemManager.util.Decryption;
import com.fujitsu.model.UserGroupModel;
import com.fujitsu.model.UserModel;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

@Service
@Transactional(rollbackFor = Exception.class)
public class SystemManagerServiceImpl extends SystemManagerService {
	@Resource
	private SystemManagerMapper systemManagerMapper;

	@Resource
	private CircuitManagerMapper circuitManagerMapper;
	
	@Resource
	private ISouthConnectionService southConnectionService;
	
	@Resource
	private IAlarmManagementService alarmManagementService;
	
	@Resource
	private ICommonManagerService commonManagerService;
	
	@Resource
	private IPerformanceManagerService performanceManagerService;
	
	@Resource
	private PerformanceManagerMapper performanceManagerMapper;
	
	@Resource
	private AlarmManagementMapper alarmManagementMapper;
	
	@Resource
	private Mongo mongo;
	
	@Override
	public Map<String, Object> getUserGroup(int userGroupId)
			throws CommonException {
		Map<String, Object> userGroupMap = new HashMap<String, Object>();
		List<Map<String, Object>> returnData = new ArrayList<Map<String, Object>>();
		returnData = systemManagerMapper.getUserGroup(userGroupId);
		Map<String, Object> root = new HashMap<String, Object>();
		root.put("GROUP_NAME", "全部");
		root.put("SYS_USER_GROUP_ID", "0");
		returnData.add(0, root);
		userGroupMap.put("rows", returnData);
		return userGroupMap;
	}

	@Override
	public Map<String, Object> getUserListByGroupId(int userGroupId, int start,
			int limit) throws CommonException {
		Map<String, Object> userMap = new HashMap<String, Object>();
		List<Map<String, Object>> returnData = new ArrayList<Map<String, Object>>();
		returnData = systemManagerMapper.getUserListByGroupId(userGroupId,
				start, limit);
		int total = systemManagerMapper.countUserList(userGroupId);
		userMap.put("rows", returnData);
		userMap.put("total", total);
		return userMap;
	}

	@Override
	public Map<String, Object> getDepartment() throws CommonException {
		Map<String, Object> departmentMap = new HashMap<String, Object>();
		List<Map<String, Object>> returnData = new ArrayList<Map<String, Object>>();
		returnData = systemManagerMapper.getDepartment();
		departmentMap.put("rows", returnData);
		return departmentMap;
	}
 
	@Override
	public Map<String, Object> getPosition() throws CommonException {
		Map<String, Object> positionMap = new HashMap<String, Object>();
		List<Map<String, Object>> returnData = new ArrayList<Map<String, Object>>();
		returnData = systemManagerMapper.getPosition();
		positionMap.put("rows", returnData);
		return positionMap;
	}

	@Override
	public Map<String, Object> getDeviceDomain() throws CommonException {
		Map<String, Object> deviceDomainMap = new HashMap<String, Object>();
		List<Map<String, Object>> returnData = new ArrayList<Map<String, Object>>();
		returnData = systemManagerMapper.getDeviceDomain();
		deviceDomainMap.put("rows", returnData);
		deviceDomainMap.put("total", returnData.size());
		return deviceDomainMap;
	}

	@Override
	public Map<String, Object> getAuthDomain() throws CommonException {
		Map<String, Object> authDomainMap = new HashMap<String, Object>();
		List<Map<String, Object>> returnData = new ArrayList<Map<String, Object>>();
		returnData = systemManagerMapper.getAuthDomain();
		authDomainMap.put("rows", returnData);
		authDomainMap.put("total", returnData.size());
		return authDomainMap;
	}

	@Override
	public Integer getNewUserID(Map<String, Object> map) throws CommonException {
		Integer result = systemManagerMapper.getNewUserID(map);
		return result;
	}

	@Override
	public void addUser(UserModel userModel) throws CommonException {
		systemManagerMapper.addUserBaseDetail(userModel);
		// 删除用户的权限域
		systemManagerMapper.deleteUserAuthDomain(userModel);
		// 增加用户的权限域
		System.out.println(userModel.getAuthDomainList().size());
		if (userModel.getAuthDomainList() != null) {
			for (int i = 0; i < userModel.getAuthDomainList().size(); i++) {
				String s = userModel.getAuthDomainList().get(i);
				if (s != null && !"".equals(s)) {
					Map<String, Object> paramsMap = new HashMap<String, Object>();
					paramsMap.put("sysUserId", userModel.getSysUserId());
					paramsMap.put("sysAuthDomainId", s);
					systemManagerMapper.addUserRefAuth(paramsMap);
				}
			}
		}
		// 删除用户的设备域
		systemManagerMapper.deleteUserDeviceDomain(userModel);
		// 增加用户的设备域
		if (userModel.getDeviceDomainList() != null) {
			for (int i = 0; i < userModel.getDeviceDomainList().size(); i++) {
				String s = userModel.getDeviceDomainList().get(i);
				if (s != null && !"".equals(s)) {
					Map<String, Object> paramsMap = new HashMap<String, Object>();
					paramsMap.put("sysUserId", userModel.getSysUserId());
					paramsMap.put("sysDeviceDomainId", s);
					systemManagerMapper.addUserRefDevice(paramsMap);
				}
			}
		}

	}

	@Override
	public void updateUser(UserModel userModel) throws CommonException {
		systemManagerMapper.updateUserBaseDetail(userModel);
		// 删除用户的权限域
		systemManagerMapper.deleteUserAuthDomain(userModel);
		// 增加用户的权限域
		if (userModel.getAuthDomainList() != null) {
			for (int i = 0; i < userModel.getAuthDomainList().size(); i++) {
				String s = userModel.getAuthDomainList().get(i);
				if (s != null && !"".equals(s)) {
					Map<String, Object> paramsMap = new HashMap<String, Object>();
					paramsMap.put("sysUserId", userModel.getSysUserId());
					paramsMap.put("sysAuthDomainId", userModel
							.getAuthDomainList().get(i));
					systemManagerMapper.addUserRefAuth(paramsMap);
				}
			}
		}
		// 删除用户的设备域
		systemManagerMapper.deleteUserDeviceDomain(userModel);
		// 增加用户的设备域
		if (userModel.getDeviceDomainList() != null) {
			for (int i = 0; i < userModel.getDeviceDomainList().size(); i++) {
				String s = userModel.getDeviceDomainList().get(i);
				if (s != null && !"".equals(s)) {
					Map<String, Object> paramsMap = new HashMap<String, Object>();
					paramsMap.put("sysUserId", userModel.getSysUserId());
					paramsMap.put("sysDeviceDomainId", userModel
							.getDeviceDomainList().get(i));
					systemManagerMapper.addUserRefDevice(paramsMap);
				}
			}
		}

	}

	@Override
	public Map<String, Object> getCurrentUserDetail(int sysUserId)
			throws CommonException {
		Map<String, Object> currentUserDetailMap = new HashMap<String, Object>();
		List<Map<String, Object>> returnData = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> returnData2 = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> returnData3 = new ArrayList<Map<String, Object>>();
		returnData = systemManagerMapper.getCurrentUserBaseDetail(sysUserId);
		currentUserDetailMap.put("currentBaseDetail", returnData);
		returnData2 = systemManagerMapper.getCurrrentAuthDomain(sysUserId);
		if (!isNullList(returnData2)) {
			currentUserDetailMap.put("currentAuthDomain", returnData2);
		}
		returnData3 = systemManagerMapper.getCurrrentDeviceDomain(sysUserId);
		if (!isNullList(returnData3)) {
			currentUserDetailMap.put("currentDeviceDomain", returnData3);
		}
		return currentUserDetailMap;
	}

	@Override
	public void deleteUser(Map<String, Object> map) throws CommonException {
		systemManagerMapper.deleteUser(map);
	}

	@Override
	public void updateUserPassword(Map<String, Object> map)
			throws CommonException {
		systemManagerMapper.updateUserInfo(map);
	}

	@Override
	public Integer userValidate(Map<String, Object> map) throws CommonException {
		Integer result = systemManagerMapper.userValidate(map);
		return result;
	}

	/*
	 * 用户组管理相关方法
	 */
	// 获取用户组信息
	@Override
	public Map<String, Object> getGroupDetailList(int start, int limit)
			throws CommonException {
		Map<String, Object> groupDetail = new HashMap<String, Object>();
		List<Map<String, Object>> result = systemManagerMapper
				.getGroupDetailList(start, limit);
		int total = systemManagerMapper.countGroupList(groupDetail);
		groupDetail.put("rows", result);
		groupDetail.put("total", total);
		return groupDetail;
	}

	// 新增用户组时，获取全部用户
	@Override
	public Map<String, Object> getAllGroupUserList(UserGroupModel userGroupModel)
			throws CommonException {
		Map<String, Object> allGroupUser = new HashMap<String, Object>();
		List<Map<String, Object>> result = systemManagerMapper
				.getAllGroupUserList(userGroupModel.getSaveType(),
						userGroupModel.getSysUserGroupId() + "");
		allGroupUser.put("rows", result);
		return allGroupUser;
	}

	// 新增用户组
	@Override
	@Transactional
	public void addGroup(UserGroupModel userGroupModel) throws CommonException {
		systemManagerMapper.addGroupDetail(userGroupModel);
		Integer key = userGroupModel.getSysUserGroupId();
		if (key != null) {// 插入组和用户的关系
			for (int i = 0; i < userGroupModel.getUserList().size(); i++) {
				Map<String, Object> paramsMap = new HashMap<String, Object>();
				paramsMap.put("sysUserGroupId", key);
				paramsMap.put("sysUserId", userGroupModel.getUserList().get(i));
				systemManagerMapper.addRefOfUserGroup(paramsMap);
			}

		}
	}

	// 修改用户组时，修改某用户组的信息
	@Override
	@Transactional
	public void updateGroup(UserGroupModel userGroupModel)
			throws CommonException {
		systemManagerMapper.updatePreGroupBase(userGroupModel);
		systemManagerMapper.deleteGroupUser(userGroupModel);
		if (userGroupModel.getUserList() != null) {
			for (int i = 0; i < userGroupModel.getUserList().size(); i++) {
				Map<String, Object> paramsMap = new HashMap<String, Object>();
				paramsMap.put("sysUserGroupId",
						userGroupModel.getSysUserGroupId());
				paramsMap.put("sysUserId", userGroupModel.getUserList().get(i));
				systemManagerMapper.addRefOfUserGroup(paramsMap);
			}
		}
	}

	// 删除用户组
	@Override
	@Transactional
	public void deleteGroup(Map<String, Object> map) throws CommonException {
		systemManagerMapper.deleteGroupAllUser(map);
		systemManagerMapper.deleteGroup(map);
	}

	// 获取用户组详情
	@Override
	public Map<String, Object> getCurrentGroupDetailById(int userGroupId)
			throws CommonException {
		Map<String, Object> currentGroupDetail = new HashMap<String, Object>();
		// 获取组包含的用户
		List<Map<String, Object>> currentGroupUser = systemManagerMapper
				.getCurrentGroupDetailById(userGroupId);
		System.out.println("currentGroupUser..................:"
				+ currentGroupUser.size());
		if (!isNullList(currentGroupUser)) {// 判断集合不为空且结合中的元素不为空
			currentGroupDetail.put("currentGroupUser", currentGroupUser);
		}
		// 获取组信息
		List<Map<String, Object>> currentBaseDetail = systemManagerMapper
				.getModiPreGroupBaseById(userGroupId);
		if (!isNullList(currentBaseDetail)) {
			currentGroupDetail.put("currentBaseDetail", currentBaseDetail);
		}
		return currentGroupDetail;
	}

	private boolean isNullList(List<Map<String, Object>> lists) {
		if (lists == null) {
			return true;
		}
		for (Map<String, Object> m : lists) {
			if (m == null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Map validateUserInformation(UserModel userModel) {
		Map m = new HashMap();
		if (userModel.getSysUserId() != null
				&& !"0".equals(userModel.getSysUserId())) {
			int mps = systemManagerMapper.validateUserInformation("modify",
					userModel.getSysUserId(), userModel.getTelephone());
			if (mps > 0) {
				m.put("success", false);
				m.put("msg", "电话号码已存在");
				return m;
			}
			int mlns = systemManagerMapper.validateUserInformation("modify",
					userModel.getSysUserId(), userModel.getLoginName());
			if (mlns > 0) {
				m.put("success", false);
				m.put("msg", "登陆名已存在");
				return m;
			}
			m.put("success", true);
			return m;
		}

		int lns = systemManagerMapper.validateUserInformation("save", "",
				userModel.getLoginName());
		if (lns > 0) {
			m.put("success", false);
			m.put("msg", "登陆名已存在");
			return m;
		}
		int jns = systemManagerMapper.validateUserInformation("save", "",
				userModel.getJobNumber());
		if (jns > 0) {
			m.put("success", false);
			m.put("msg", "工号已存在");
			return m;
		}
		int tps = systemManagerMapper.validateUserInformation("save", "",
				userModel.getTelephone());
		if (tps > 0) {
			m.put("success", false);
			m.put("msg", "手机号已存在");
			return m;
		}
		m.put("success", true);
		return m;
	}

	@Override
	public boolean confirmOldPassIsTrue(UserModel userModel) {
		String oldPass = systemManagerMapper.getOldPassByUserId(userModel
				.getSysUserId());
		if (oldPass != null && oldPass.equals(userModel.getOldPass())) {
			return true;
		}
		return false;
	}

	@Override
	public void saveModifyPass(UserModel userModel) {
		systemManagerMapper.saveModifyPass(userModel);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.ISystemManagerService#login(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public CommonResult login(String userName, String password)
			throws CommonException {
		CommonResult result = new CommonResult();

		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();

		// 检查license--
		try{
			checkLicense();
		}catch(CommonException e){
			result.setReturnMessage(MessageHandler
					.getErrorMessage(e.getErrorCode()));
			result.setReturnResult(CommonDefine.FAILED);
			return result;
		}

		// 更新服务器文件目录地址
		// updateSystemParamter(CommonDefine.SYS_PARAM_KEY_SERVER_FILE_PATH);

		//
		Map user = null;
		Map select = null;
		List<Map> rows = systemManagerMapper.getUserByNameAndPass(userName,
				password);
		String loginTimeLastTime = "";//上次登录时间
		if (rows.size() > 0) {
			user = rows.get(0);
			if(user.get("LOGIN_TIME") != null){
				loginTimeLastTime = this.transDateForFP(user.get("LOGIN_TIME"));
			}
			if(user.get("LOGIN_TIME")!=null&&//非首次登陆
				(user.get("LOGOUT_TIME")==null||
				((Date)user.get("LOGOUT_TIME")).before((Date)user.get("LOGIN_TIME")))){//退出时间比登陆时间早,则重设
				user.put("LOGOUT_TIME", new Date(session.getLastAccessedTime()));
			}
			user.put("LOGIN_TIME", new Date());
			
			systemManagerMapper.updateUserInfo(user);
		}
		if (user != null) {

			// 取得权限域
			select = hashMapSon("t_sys_user_ref_auth", "SYS_USER_ID",
					user.get("SYS_USER_ID"), null, null, null);
			List<Map> list_auth = circuitManagerMapper.getByParameter(select);
			String authDomain = "";
			for (Map map : list_auth) {
				authDomain += map.get("SYS_AUTH_DOMAIN_ID") + ",";
			}
			if (authDomain.length() > 1) {
				authDomain = authDomain.substring(0, authDomain.length() - 1);
			}
			// 取得设备域
			select = hashMapSon("t_sys_user_ref_device", "SYS_USER_ID",
					user.get("SYS_USER_ID"), null, null, null);
			List<Map> list_device = circuitManagerMapper.getByParameter(select);
			String deviceDomain = "";
			for (Map map : list_device) {
				deviceDomain += map.get("SYS_DEVICE_DOMAIN_ID") + ",";
			}
			if (deviceDomain.length() > 1) {
				deviceDomain = deviceDomain.substring(0,
						deviceDomain.length() - 1);
			}

			// //遍历map，赋值
			// for (Object key : user.keySet()) {
			// session.setAttribute(key.toString(), user.get(key));
			// }
			session.setAttribute("SYS_USER_ID", user.get("SYS_USER_ID"));
			session.setAttribute("USER_NAME", user.get("USER_NAME"));
			session.setAttribute("LOGIN_NAME", user.get("LOGIN_NAME"));
			session.setAttribute("JOB_NUMBER", user.get("JOB_NUMBER"));
			session.setAttribute("TELEPHONE", user.get("TELEPHONE"));
			session.setAttribute("TIME_OUT", user.get("TIME_OUT"));
			session.setAttribute("IS_LOCKED", false);

			session.setAttribute("authDomain", authDomain);
			session.setAttribute("deviceDomain", deviceDomain);
			// session.setAttribute("privilege", privilegeString);
			session.setAttribute("clientIP", request.getRemoteAddr());
			session.setAttribute("loginTimeLastTime", loginTimeLastTime);
			
			//add by LuYunLong 2014-1-24 第一次登录的时候，强制弹出修改密码窗口
			if(CommonDefine.PASSWORD.equals(password)){
				result.setReturnResult(CommonDefine.FIRST_CHECK);
				result.setReturnMessage(user.get("SYS_USER_ID")+"");
			}else{
				result.setReturnResult(CommonDefine.SUCCESS);
			}
			// 插入操作log 第二个参数 填写操作结果
			// Define.LOG_RESULT_SUCCESS LOG_RESULT_FAILED
			// LOG_RESULT_PART_SUCCESS
			// insertLog("用户登录", Define.LOG_RESULT_SUCCESS);

		} else {
			result.setReturnMessage(MessageHandler
					.getErrorMessage(MessageCodeDefine.LOGIN_ERROR));
			result.setReturnResult(CommonDefine.FAILED);

			//
			// insertLog("用户登录", Define.LOG_RESULT_FAILED);
		}
		// log.info(resultObj);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fujitsu.IService.ISystemManagerService#logout()
	 */
	@Override
	public CommonResult logout(Integer userId,Date logoutTime) {
		CommonResult result = new CommonResult();

//		HttpServletRequest request = ServletActionContext.getRequest();
//		HttpSession session = request.getSession();

		//更新退出登录时间
		if(userId!=null){
//			int userId = Integer.valueOf(session.getAttribute("SYS_USER_ID").toString());
			Map user = systemManagerMapper.getUserById(userId);
			if(user!=null){
				user.put("LOGOUT_TIME", logoutTime);
				systemManagerMapper.updateUserInfo(user);
			}
		}
//		session.removeAttribute("SYS_USER_ID");
//		session.removeAttribute("USER_NAME");
//		session.removeAttribute("LOGIN_NAME");
//		session.removeAttribute("JOB_NUMBER");
//		session.removeAttribute("TELEPHONE");
//		session.removeAttribute("TIME_OUT");
//		session.removeAttribute("IS_LOCKED");
//		session.removeAttribute("clientIP");
//		session.removeAttribute("authDomain");
//		session.removeAttribute("deviceDomain");
		result.setReturnResult(CommonDefine.SUCCESS);
		return result;
	}

	@Override
	public Map validateUserGroupName(UserGroupModel userGroupModel)
			throws CommonException {
		Map m = new HashMap();
		int mps = systemManagerMapper.validateUserGroupName(userGroupModel
				.getGroupName());
		if (mps > 0) {
			m.put("success", false);
			m.put("msg", "用户组名称已存在");
			return m;
		}
		m.put("success", true);
		return m;
	}

	@Override
	public void lock() throws CommonException {

		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpSession session = request.getSession();
			session.setAttribute("IS_LOCKED", true);
		} catch (Exception e) {
			throw new CommonException(e, -99999, "用户超时锁定失败！");
		}
	}

	@Override
	public void unlock(String userName, String password) throws CommonException {
		try {
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpSession session = request.getSession();
			List<Map> rows = systemManagerMapper.getUserByNameAndPass(userName,
					password);
			if (rows.size() > 0) {
				session.setAttribute("IS_LOCKED", false);
			} else {
				throw new CommonException(new Exception(), -99999,
						"用户解除超时锁定失败！");
			}
		} catch (Exception e) {
			throw new CommonException(e, -99999, "用户解除超时锁定失败！");
		}
	}

	@Override
	public Map<String, Object> getAllServers() throws CommonException{
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 查询所有的服务器
		List<Map<String, Object>> serverList = systemManagerMapper.getAllServers();
		valueMap.put("rows", serverList);
		return valueMap;
	}
	
	/**
	 * 将数据库时间转换为首页展示的式样
	 * @param o
	 * @return
	 */
	private String transDateForFP(Object o) {
		
		String date= null;
		
		SimpleDateFormat spf = new SimpleDateFormat();
		spf.applyPattern("yyyy");
		String loginYear = spf.format(o);
		spf.applyPattern("M");
		String loginMonth = spf.format(o);
		spf.applyPattern("d");
		String loginDay = spf.format(o);
		spf.applyPattern("HH:mm:ss");
		String loginHMS = spf.format(o);
		
		date = loginYear + "年" + loginMonth + "月" + loginDay + "日 " + loginHMS;
		
		return date;
	}
	
	/**
	 * 获取首页所需的用户信息
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	private Map<String, Object> getUserInfoForFP(int userId) throws CommonException {
		
		Map<String, Object> return_result = new HashMap<String, Object>();
		
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpSession session = request.getSession();
		
		String loginTime = session.getAttribute("loginTimeLastTime") != null ?
				session.getAttribute("loginTimeLastTime").toString() : "";
		
		String logoutTime = "";
		
		Map user = systemManagerMapper.getUserById(userId);
		SimpleDateFormat spf = new SimpleDateFormat();
//		if(user.get("LOGIN_TIME") != null){
//			spf.applyPattern("yyyy");
//			String loginYear = spf.format(user.get("LOGIN_TIME"));
//			spf.applyPattern("M");
//			String loginMonth = spf.format(user.get("LOGIN_TIME"));
//			spf.applyPattern("d");
//			String loginDay = spf.format(user.get("LOGIN_TIME"));
//			spf.applyPattern("HH:mm:ss");
//			String loginHMS = spf.format(user.get("LOGIN_TIME"));
//			
//			loginTime = loginYear + "年" + loginMonth + "月" + loginDay + "日 " + loginHMS;
//		}
		
		if(user.get("LOGOUT_TIME") != null){
			spf.applyPattern("yyyy");
			String logoutYear = spf.format(user.get("LOGOUT_TIME"));
			spf.applyPattern("M");
			String logoutMonth = spf.format(user.get("LOGOUT_TIME"));
			spf.applyPattern("d");
			String logoutDay = spf.format(user.get("LOGOUT_TIME"));
			spf.applyPattern("HH:mm:ss");
			String logoutHMS = spf.format(user.get("LOGOUT_TIME"));
			
			logoutTime = logoutYear + "年" + logoutMonth + "月" + logoutDay + "日 " + logoutHMS;
		}
		
		return_result.put("userName", user.get("USER_NAME").toString());
		return_result.put("loginTime", loginTime);
		return_result.put("logoutTime", logoutTime);
		
		return return_result;
	}
	
	/**
	 * 首页获取任务部分的数据
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	private Map<String, Object> getParamTaskForFP(int userId) throws CommonException {
		
		Map<String, Object> return_result = new HashMap<String, Object>();
		
		return_result.putAll(this.getEMSSYNCParamForFP(userId));
		return_result.putAll(this.getAlarmAutoSYNCParamForFP(userId));
		return_result.putAll(this.getPMCollParamForFP(userId));
		return_result.putAll(this.getPMReportParamForFP(userId));
		return_result.putAll(this.getCutOverParamForFP(userId));
		return_result.putAll(this.getCircuitAutoNewParamForFP(userId));
		return_result.putAll(this.getDataBackupParamForFP(userId));
		
		return return_result;
	}
	
	/**
	 * 获取昨天的起始时间
	 * @return
	 * @throws CommonException
	 */
	private Map<String, Object> getYesterday() throws CommonException {
		
		Map<String, Object> return_result = new HashMap<String, Object>();
		
		Date dNow = new Date();   //当前时间
		Date dBefore = new Date();		
		Calendar calendar = Calendar.getInstance(); //得到日历
		calendar.setTime(dNow);//把当前时间赋给日历
		calendar.add(Calendar.DAY_OF_MONTH, -1);  //设置为前一天
		dBefore = calendar.getTime();   //得到前一天的时间
		
		SimpleDateFormat sdfStart = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		SimpleDateFormat sdfEnd = new SimpleDateFormat("yyyy-MM-dd 23:59:59");	
		
		return_result.put("endTime", sdfEnd.format(dBefore));
		return_result.put("startTime", sdfStart.format(dBefore));
		
		return return_result;
	}
	
	/**
	 * 获取用户相关的taskId（注意：此方法向返回结果中手动加入了一个id为0的值）
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	private List<Integer> getTaskIdList(int userId) throws CommonException {
		
		List<Integer> taskIdList = new ArrayList<Integer>();
		
		//获取当前用户可见的网管
		List<Map> emsList = commonManagerService.getAllEmsByEmsGroupId(userId, CommonDefine.VALUE_ALL, false, true);
		List<Integer> emsIds = new ArrayList<Integer>();
		emsIds.add(0);//手动加入一个0，避免数据库查询出错
		for(Map ems : emsList){
			emsIds.add(Integer.parseInt(ems.get("BASE_EMS_CONNECTION_ID").toString()));
		}
		List<Map<String, Object>> taskIds = systemManagerMapper.getTaskIdsByEMSIDList(emsIds);
		taskIdList.add(0);//防止数据库查询报错
		for(Map<String, Object> taskId : taskIds){
			taskIdList.add(Integer.parseInt(taskId.get("SYS_TASK_ID").toString()));
		}
		
		return taskIdList;
	}
	
	/**
	 * 获取割接任务数据
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	private Map<String, Object> getCutOverParamForFP(int userId) throws CommonException {
		
		Map<String, Object> return_result = new HashMap<String, Object>();
		
		Map<String, Object> select = new HashMap<String, Object>();
		select.put("taskIds", getTaskIdList(userId));
		
		List<Map> groups = performanceManagerMapper.getUserGroupByUserId(userId,null);
		
		//cutoverTask.js中的定义 1-等待 2-进行中 3-完成 4-挂起
		String[] statusTotal = {"1","2","3","4"};
		String[] statusStart = {"1","2"};
		String[] statusSuccess= {"3"};
		
		Map<String, Object> taskNum = systemManagerMapper.
							getCutoverTaskNum(groups,userId,statusTotal);
		Map<String, Object> startStatus = systemManagerMapper.
							getCutoverTaskNum(groups,userId,statusStart);
		Map<String, Object> success = systemManagerMapper.
							getCutoverTaskNum(groups,userId,statusSuccess);
		
		return_result.put("cutOverTaskNum", taskNum.get("total").toString());
		return_result.put("cutOverStartStatus", startStatus.get("total").toString());
		return_result.put("cutOverSuccess", success.get("total").toString());
		
		return return_result;
	}
	
	/**
	 * 获取性能采集任务数据
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	private Map<String, Object> getPMCollParamForFP(int userId) throws CommonException {
		
		Map<String, Object> return_result = new HashMap<String, Object>();
		
//		Map<String, Object> select = new HashMap<String, Object>();
//		select.put("taskIds", getTaskIdList(userId));
//		
//		Map<String, Object> taskNum = systemManagerMapper.getTaskNum(select, 
//				CommonDefine.QUARTZ.JOB_PM);
//		Map<String, Object> startStatus = systemManagerMapper.getTaskStartStatusNum(select, 
//				CommonDefine.QUARTZ.JOB_PM);
//		Map<String, Object> success = systemManagerMapper.getTaskSuccessNum(select, 
//				CommonDefine.QUARTZ.JOB_PM);
		
		return_result.put("PMCollTaskNum", performanceManagerService.
				getPMCollParamForFP(CommonDefine.ALL_TASK_FIRST_PAGE, userId));
		return_result.put("PMCollStartStatus", performanceManagerService.
				getPMCollParamForFP(CommonDefine.START_TASK_FIRST_PAGE, userId));
		return_result.put("PMCollSuccess", performanceManagerService.
				getPMCollParamForFP(CommonDefine.SUCCESS_TASK_FIRST_PAGE, userId));
		
		return return_result;
	}
	
	/**
	 * 获取告警自动同步任务数据
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	private Map<String, Object> getAlarmAutoSYNCParamForFP(int userId) throws CommonException {
		
		Map<String, Object> return_result = new HashMap<String, Object>();

		Map<String, Object> select = new HashMap<String, Object>();
		
		//获取当前用户可见的网管
		List<Map> emsList = commonManagerService.getAllEmsByEmsGroupId(userId, CommonDefine.VALUE_ALL, false, true);
		List<Integer> emsIds = new ArrayList<Integer>();
		emsIds.add(0);//手动加入一个0，避免数据库查询出错
		for(Map ems : emsList){
			emsIds.add(Integer.parseInt(ems.get("BASE_EMS_CONNECTION_ID").toString()));
		}
		List<Map<String,Object>> rl =alarmManagementService.getAutoAlarmSyncByEmsId(emsIds);
//		select.put("taskIds", getTaskIdList(userId));
//		
//		Map<String, Object> taskNum = systemManagerMapper.getTaskNum(select, 
//				CommonDefine.QUARTZ.JOB_ALARMSYNCH);
//		Map<String, Object> startStatus = systemManagerMapper.getTaskStartStatusNum(select, 
//				CommonDefine.QUARTZ.JOB_ALARMSYNCH);
//		Map<String, Object> success = systemManagerMapper.getTaskSuccessNum(select, 
//				CommonDefine.QUARTZ.JOB_ALARMSYNCH);
		int countStart=0;
		int countSuccess=0;
		for (Map<String, Object> map : rl) {
			if(map.get("TASK_STATUS")!=null&&map.get("TASK_STATUS").toString().equals("1")){
				countStart++;
				if(map.get("EXECUTE_STATUS")!=null&&map.get("EXECUTE_STATUS").toString().equals("1")){
					countSuccess++;
				}
			}
		}
		
		return_result.put("alarmAutoSYNCTaskNum", emsList.size());
		return_result.put("alarmAutoSYNCStartStatus", countStart);
		return_result.put("alarmAutoSYNCSuccess", countSuccess);
		
		return return_result;
	}
	
	/**
	 * 获取网管自动同步任务数据
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	private Map<String, Object> getEMSSYNCParamForFP(int userId) throws CommonException {
		
		Map<String, Object> return_result = new HashMap<String, Object>();

		Map<String, Object> select = new HashMap<String, Object>();
		select.put("taskIds", getTaskIdList(userId));
		
		Map<String,Object> taskNum = systemManagerMapper.getTaskNum(select, 
				CommonDefine.QUARTZ.JOB_BASE);
		Map<String, Object> startStatus = systemManagerMapper.getTaskStartStatusNum(select, 
				CommonDefine.QUARTZ.JOB_BASE);
		Map<String, Object> success = systemManagerMapper.getTaskSuccessNum(select, 
				CommonDefine.QUARTZ.JOB_BASE);
		
		return_result.put("EMSSYNCTaskNum", taskNum.get("taskNum").toString());
		return_result.put("EMSSYNCStartStatus", startStatus.get("startStatusNum").toString());
		return_result.put("EMSSYNCSuccess", success.get("successNum").toString());
		
		return return_result;
	}
	
	/**
	 * 获取电路自动生成任务数据
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	private Map<String, Object> getCircuitAutoNewParamForFP(int userId) throws CommonException {
		
		Map<String, Object> return_result = new HashMap<String, Object>();

		Map<String, Object> select = new HashMap<String, Object>();
		select.put("taskIds", getTaskIdList(userId));
		
		Map<String, Object> taskNum = systemManagerMapper.getTaskNum(select, 
				CommonDefine.QUARTZ.JOB_CIRCUIT);
		Map<String, Object> startStatus = systemManagerMapper.getTaskStartStatusNum(select, 
				CommonDefine.QUARTZ.JOB_CIRCUIT);
		Map<String, Object> success = systemManagerMapper.getTaskSuccessNum(select, 
				CommonDefine.QUARTZ.JOB_CIRCUIT);
		
		return_result.put("circuitAutoNewTaskNum", taskNum.get("taskNum").toString());
		return_result.put("circuitAutoNewStartStatus", startStatus.get("startStatusNum").toString());
		return_result.put("circuitAutoNewSuccess", success.get("successNum").toString());
		
		return return_result;
	}
	
	/**
	 * 获取数据库备份任务数据
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	private Map<String, Object> getDataBackupParamForFP(int userId) throws CommonException {
		
		Map<String, Object> return_result = new HashMap<String, Object>();

		Map<String, Object> select = new HashMap<String, Object>();
		
		Map autoValuePri=getBackupSettingValue(CommonDefine.AUTO_SETTING_VALUE_PRI);
		Map autoValueSec=getBackupSettingValue(CommonDefine.AUTO_SETTING_VALUE_SEC);
		int countall=0;
		int countstart=0;
		int countsucess=0;
		if(autoValuePri!=null&&autoValuePri.get("PARAM_VALUE")!=null){
			countall++;
			String a=autoValuePri.get("PARAM_VALUE").toString().split(",")[1];
			if(a.equals("0")){
				countstart++;
				if(autoValuePri.get("SHOW_ORDER")!=null){
					if(autoValuePri.get("SHOW_ORDER").toString().equals("0")){
						countsucess++;
					}
				}
			}
		}
		if(autoValueSec!=null&&autoValueSec.get("PARAM_VALUE")!=null){
			countall++;
			String a=autoValueSec.get("PARAM_VALUE").toString().split(",")[1];
			if(a.equals("0")){
				countstart++;
				if(autoValueSec.get("SHOW_ORDER")!=null){
					if(autoValueSec.get("SHOW_ORDER").toString().equals("0")){
						countsucess++;
					}
				}
			}
		}

		//TODO
		return_result.put("dataBackupTaskNum", countall);
		return_result.put("dataBackupStartStatus", countstart);
		return_result.put("dataBackupSuccess", countsucess);
		
		return return_result;
	}
	
	/**
	 * 获取性能报表生成任务数据
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	private Map<String, Object> getPMReportParamForFP(int userId) throws CommonException {
		
		Map<String, Object> return_result = new HashMap<String, Object>();
		
//		Map<String, String> searchCond = new HashMap<String, String>();
//		Map<String, Object> taskData = performanceManagerService.searchReportTask(searchCond, userId);
//		List<Map<String, String>> taskList = (ArrayList<Map<String, String>>)taskData.get("rows");
//		List<Integer> taskIds = new ArrayList<Integer>();
//		taskIds.add(0);//手动加入一个0，避免数据库查询出错
//		for(Map map : taskList){
//			taskIds.add(Integer.parseInt(map.get("taskId").toString()));
//		}
//		
//		Map<String, Object> select = new HashMap<String, Object>();
//		select.put("taskIds", taskIds);
//		
//		Map<String, Object> taskNum = systemManagerMapper.getPMReportTaskNum(select);
//		Map<String, Object> startStatus = systemManagerMapper.getPMReportStartStatusNum(select);
//		Map<String, Object> success = systemManagerMapper.getPMReportSuccessNum(select);
		
		return_result.put("pmReportTaskNum", performanceManagerService.
				getPMReportParamForFP(CommonDefine.ALL_TASK_FIRST_PAGE, userId));
		return_result.put("pmReportStartStatus", performanceManagerService.
				getPMReportParamForFP(CommonDefine.START_TASK_FIRST_PAGE, userId));
		return_result.put("pmReportSuccess", performanceManagerService.
				getPMReportParamForFP(CommonDefine.SUCCESS_TASK_FIRST_PAGE, userId));
		
		return return_result;
	}
	
	/**
	 * 获取首页数据
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getParamFP(int userId) throws CommonException {
		
		Map<String, Object> return_result = new HashMap<String, Object>();
//		StopWatch sw = new StopWatch("做性能测试的小明");
//		sw.start("getUserInfoForFP");
		return_result.putAll(getUserInfoForFP(userId));
//		sw.stop();
//		sw.start("getParamFromDataCollectionFP");
		return_result.putAll(southConnectionService.getParamFromDataCollectionFP(userId));
//		sw.stop();
//		sw.start("getCurrentAlarmCountForFP");
		return_result.putAll(alarmManagementService.getCurrentAlarmCountForFP());
//		sw.stop();
//		sw.start("getParamTaskForFP");
		return_result.putAll(getParamTaskForFP(userId));
//		sw.stop();
//		System.out.println(sw.prettyPrint());
		
		return return_result;
	}
	/**
	 * 获取首页数据
	 * @param userId
	 * @return
	 * @throws CommonException
	 */
	public Map<String, Object> getIndexPmInfo(int userId) throws CommonException {
		
		Map<String, Object> return_result = new HashMap<String, Object>();
//		StopWatch sw = new StopWatch("做性能测试的小萌");
//		sw.start("getIndexPagePmInfo");
		return_result.putAll(performanceManagerService.getIndexPagePmInfo(userId));
//		sw.stop();
//		System.out.println(sw.prettyPrint());
		
		return return_result;
	}
	
	//license检查
	private boolean checkLicense() throws CommonException{
		// 检查是否需要检测license
		if (checkNeedToCheckLicense()) {
			// 检测license信息
			if (!checkLicenseExist()) {
				throw new CommonException(new NullPointerException(),
						MessageCodeDefine.LICENSE_NOT_EXIST);
			}
			if (!checkLicenseInfo()) {
				throw new CommonException(new NullPointerException(),
						MessageCodeDefine.LICENSE_OUT_OF_TIME);
			}
		}
		return true;
	}
	
	private boolean checkLicenseExist() {
		HttpServletRequest request = ServletActionContext.getRequest();
		// 检测是否有license文件
		String filePath = CommonDefine.PATH_ROOT + "../../../license.txt";
		File dir = new File(filePath);
		if (!dir.exists()) {
			return false;
		} else {
			saveLicenseInfo(filePath);
		}
		if (CommonDefine.LICENSE == null) {
			return false;
		}
		return true;
	}
	
	// 保存license信息
	private void saveLicenseInfo(String filePath) {
		Decryption decry = Decryption.getInstance();
		try {
			File dir = new File(filePath);
			
			if (CommonDefine.LICENSE == null) {
				CommonDefine.LICENSE = decry.decryption(filePath);
				//license更新时间
				CommonDefine.LICENSE.put(CommonDefine.LICENSE_KEY_LAST_MODIFIED, String.valueOf(dir.lastModified()));
			}else{
				long lastModified = Long.valueOf(CommonDefine.LICENSE.get(CommonDefine.LICENSE_KEY_LAST_MODIFIED));
				//license已更新，重新读取
				if(dir.lastModified()>lastModified){
					CommonDefine.LICENSE = decry.decryption(filePath);
					//license更新时间
					CommonDefine.LICENSE.put(CommonDefine.LICENSE_KEY_LAST_MODIFIED, String.valueOf(dir.lastModified()));
				}
			}
		} catch (Exception e) {
			CommonDefine.LICENSE = null;
		}
	}
	
	/**
	 * 检测license是否过期
	 * 
	 * @return
	 */
	private boolean checkLicenseInfo() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date startDate = null;
		// 取得license可用日期
		String validateTime = CommonDefine.LICENSE
				.get(CommonDefine.LICENSE_KEY_VALIDATE_TIME);
		// 取得license激活日期
		String startTime = CommonDefine.LICENSE.get(CommonDefine.LICENSE_KEY_START_TIME);
		try {
			startDate = dateFormat.parse(startTime);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		Date endDate = new Date();
		// 比较时间
		if (validateTime != null && startDate != null) {
			if (daysOfTwo(startDate, endDate) <= Long.parseLong(validateTime)) {
				return true;
			} else {
				return false;
			}
		} else
			return false;
	}
	
	// 计算天数差
	private long daysOfTwo(Date startDate, Date endDate) {

		long number = (endDate.getTime() - startDate.getTime())
				/ (1000 * 60 * 60 * 24);
		double number_s = (endDate.getTime() - startDate.getTime())
				% (1000 * 60 * 60 * 24);
		if (number_s > 0) {
			number = number + 1;
		}
		return number;
	}

	@Override
	public Map getBackupSettingValue(String key) throws CommonException {
		return alarmManagementMapper.getSystemParam(key);
	}

	@Override
	public void saveBackupSettingValues(Map manuValue) throws CommonException {
		alarmManagementMapper.deleteAlarmParam(manuValue);
		alarmManagementMapper.modifyAlarmParam(manuValue);
	}

	@Override
	public void changeDatabackupToCancel(String param) throws CommonException {
		alarmManagementMapper.changeDatabackupToCancel(param);
	}
	
	@Override
	public Map<String, Object> getJournals(LogModel model,int start,int limit)throws CommonException, ParseException {
		Map<String, Object> valueMap = new HashMap<String, Object>();
		// 定义时间格式转换器
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		// 获取数据库连接
		DBCollection conn = mongo.getDB(CommonDefine.MONGODB_NAME).getCollection(CommonDefine.T_JOURNAL);
		// 封装查询条件
		BasicDBObject condition = new BasicDBObject();
		
		Map param=new HashMap();
		if(!"".equals(model.getUserGroupId())){
			param.put("groupId",model.getUserGroupId());
		}
		if(!"".equals(model.getUserGroupName())){
			param.put("groupName",model.getUserGroupName());
		}
		if(!"".equals(model.getUserId())){
			param.put("userId",model.getUserId());
		}
		if(!"".equals(model.getUserName())){
			param.put("userName",model.getUserName());
		}
		
		//根据查询条件获取所有用户及其对应的组
		List<Map> users=systemManagerMapper.getAllUserToGroupByCondition(param);
		if(users==null || users.size()==0){
			return valueMap;
		}
		
		List userIds=new ArrayList();
		for(Map m:users){
			userIds.add(Integer.parseInt(m.get("sys_user_Id").toString()));
		}
		
		condition.put("USER_ID", new BasicDBObject("$in",userIds));

		// 操作时间
		if(!"".equals(model.getStartDate())){
			model.setStartDate(model.getStartDate()+" 00:00:00");
			model.setEndDate(model.getEndDate()+" 23:59:59");
			condition.put("CREATE_TIME", new BasicDBObject("$gte",sf.parse(model.getStartDate())).append("$lte",sf.parse(model.getEndDate())));
		}
		System.out.println(condition.toString());
		// 关键字
		if(!"".equals(model.getLogKeyword())){
			// 实现SQL语法 like的条件 
			Pattern pattern = Pattern.compile("^.*" + model.getLogKeyword() + ".*$", Pattern.CASE_INSENSITIVE); 
			condition.put("OPERATION", pattern);
		}
		// 查询结果总数
		int count = conn.find(condition).count();
		// 查询详细信息
		DBCursor journal = conn.find(condition).skip(start).limit(limit);
		List<DBObject> list = new ArrayList<DBObject>();
		while (journal.hasNext()) {
			DBObject dbo = journal.next();
			String userId=dbo.get("USER_ID")==null?"":dbo.get("USER_ID").toString();
			Map m=getUserByUserIdFromLists(userId,users);
			if(m!=null){
				dbo.put("USER_NAME",m.get("user_name")==null?"":m.get("user_name"));
				dbo.put("USER_GROUP_NAME",m.get("group_name")==null?"":m.get("group_name"));
			}
			dbo.put("USER_ID",dbo.get("USER_ID")==null?"":dbo.get("USER_ID"));
			dbo.put("OPERATION",dbo.get("OPERATION")==null?"":dbo.get("OPERATION"));
			dbo.put("ACTION_NAME",dbo.get("ACTION_NAME")==null?"":dbo.get("ACTION_NAME"));
			dbo.put("METHOD_NAME",dbo.get("METHOD_NAME")==null?"":dbo.get("METHOD_NAME"));
			dbo.put("CREATE_TIME",sf.format(dbo.get("CREATE_TIME")));
			list.add(dbo);
		}
		
		valueMap.put("total", count);
		valueMap.put("rows", list);
		return valueMap;
	}
	
	private Map getUserByUserIdFromLists(String userId, List<Map> users) {
		for(Map user:users){
			if(userId.equals(user.get("sys_user_Id").toString())){
				return user;
			}
		}
		return null;
	}
}
