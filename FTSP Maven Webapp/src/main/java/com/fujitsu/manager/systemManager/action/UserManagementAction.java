package com.fujitsu.manager.systemManager.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.ISystemManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.model.UserModel;
import com.opensymphony.xwork2.ModelDriven;

public class UserManagementAction extends AbstractAction implements ModelDriven<UserModel>{
	@Resource
	public ISystemManagerService systemManagerService;
	public UserModel userModel=new UserModel();
	@Override
	public UserModel getModel(){
		  return userModel;
	}
	
	/**
	 * 获取所有用户组
	 * @return
	 */
	@IMethodLog(desc = "系统管理查询")
	public String getUserGroup(){
		String returnString = RESULT_OBJ;
		try {
			Map<String, Object> userGroupList = systemManagerService.getUserGroup(Integer.parseInt(userModel.getUserGroupId()));
			resultObj = JSONObject.fromObject(userGroupList);
		} catch (CommonException e) {
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;		
	}
	
	/**
	 * 获取某一用户组的用户清单
	 * @return
	 */
	@IMethodLog(desc = "用户信息查询")
	public String getUserListByGroupId(){
		String returnString = RESULT_OBJ;
		try {
			Map<String,Object> userList = systemManagerService.getUserListByGroupId(Integer.parseInt(userModel.getUserGroupId()),start,limit);
			resultObj = JSONObject.fromObject(userList);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}
	
	
	/**
	 * 获取数据库中现存的职位列表
	 * @return
	 */
	@IMethodLog(desc = "现存的职位列表")
	public String getPosition()
	{
		String returnString = RESULT_OBJ;
		try {
			Map<String,Object> positionList = systemManagerService.getPosition();
			resultObj = JSONObject.fromObject(positionList);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}
	
	/**
	 * 获取数据库中现存的部门列表
	 * @return
	 */
	@IMethodLog(desc = "现存的部门列表")
	public String getDepartment()
	{
//		String returnString = RESULT_OBJ;
		try {
			Map<String,Object> departmentList = systemManagerService.getDepartment();
			resultObj = JSONObject.fromObject(departmentList);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
//			returnString = RESULT_OBJ;;
		}
		return RESULT_OBJ;
	}
	
	/**
	 * 获取数据库中现存的设备域列表
	 * @return
	 */
	@IMethodLog(desc = "现存的设备域列表")
	public String getDeviceDomain()
	{
		String returnString = RESULT_OBJ;
		try {
			Map<String,Object> deviceDomainList = systemManagerService.getDeviceDomain();
			resultObj = JSONObject.fromObject(deviceDomainList);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}
	
	/**
	 * 获取数据库中现存的权限域列表
	 * @return
	 */
	@IMethodLog(desc = "现存的权限域列表")
	public String getAuthDomain(){
		String returnString = RESULT_OBJ;
		try {
			Map<String,Object> authDomainList = systemManagerService.getAuthDomain();
			resultObj = JSONObject.fromObject(authDomainList);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}
	/**
	 * 删除一个用户
	 * @return
	 */
	public String deleteUser(){
		try {
			 for(int i=0;i<userModel.getSysUserIdList().size();i++){
				 	HashMap<String, Object> map = new HashMap<String, Object>(); 
			    	map.put("sysUserId",userModel.getSysUserIdList().get(i));
				    systemManagerService.deleteUser(map);
			    }			
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage("");			
				resultObj = JSONObject.fromObject(result);

			} catch (CommonException e) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(e.getErrorMessage());			
				resultObj = JSONObject.fromObject(result);
			}
			return RESULT_OBJ;
	}
	/**
	 * 新增一个用户
	 * @return
	 */
	@IMethodLog(desc = "新增一个用户", type = IMethodLog.InfoType.MOD)
	public String addUser(){
		Map m = new HashMap();
		//验证用户信息
		Map validateResult=validateUserInformation();
		boolean isExists=(Boolean)validateResult.get("success");
		if(!isExists){
			resultObj = JSONObject.fromObject(validateResult);
			return RESULT_OBJ;
		}
		try {
			systemManagerService.addUser(userModel);
			m.put("success", true);
			m.put("msg", "保存成功！");
		} catch (CommonException e) {
			e.printStackTrace();
			m.put("success", false);
			m.put("msg", "保存失败！");
		}
		resultObj = JSONObject.fromObject(m);
		return RESULT_OBJ;
	}
	
	
	//验证用户信息,主要验证登录名，工号，手机号码唯一性
	private Map validateUserInformation() {
		return systemManagerService.validateUserInformation(userModel);
	}

	/**
	 * 重置用户密码
	 * @return
	 */
	@IMethodLog(desc = "重置用户密码", type = IMethodLog.InfoType.MOD)
	public String reSetPassword(){
		try {
			 for(int i=0;i<userModel.getSysUserIdList().size();i++){
				 	HashMap<String, Object> map = new HashMap<String, Object>(); 
				 	map.put("PASSWORD", CommonDefine.PASSWORD);
			    	map.put("SYS_USER_ID",userModel.getSysUserIdList().get(i));
				    systemManagerService.updateUserPassword(map);
			    }			
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage("");			
				resultObj = JSONObject.fromObject(result);

			} catch (CommonException e) {
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(e.getErrorMessage());			
				resultObj = JSONObject.fromObject(result);
			}
			return RESULT_OBJ;	
	}

	/**
	 * 修改一个用户
	 * @return
	 */
	@IMethodLog(desc = "修改一个用户", type = IMethodLog.InfoType.MOD)
	public String modifyUser(){
		Map m = new HashMap();
		//验证用户信息
		Map validateResult=validateUserInformation();
		boolean isExists=(Boolean)validateResult.get("success");
		if(!isExists){
			resultObj = JSONObject.fromObject(validateResult);
			return RESULT_OBJ;
		}
		try {
			systemManagerService.updateUser(userModel);
			m.put("success", true);
			m.put("msg", "修改成功");
		} catch (CommonException e) {
			m.put("success", false);
			m.put("msg", "修改失败");
		}
		resultObj = JSONObject.fromObject(m);
		return RESULT_OBJ;
	}
	
	
	
	
	/**
	 * 修改一个用户密码
	 * @return
	 */
	@IMethodLog(desc = "修改一个用户密码", type = IMethodLog.InfoType.MOD)
	public String saveModifyPass(){
		Map m = new HashMap();
		if(userModel.getSysUserId()==null || "0".equals(userModel.getSysUserId())){
			m.put("success", false);
			m.put("msg", "修改的用户不存在,请重试或联系管理员!");
			resultObj = JSONObject.fromObject(m);
			return RESULT_OBJ;
		}
		//判断输入的原密码是否正确
		boolean oldPassIsTrue=systemManagerService.confirmOldPassIsTrue(userModel);
		if(!oldPassIsTrue){
			m.put("success",false);
			m.put("msg", "原始密码输入不正确");
			resultObj = JSONObject.fromObject(m);
			return RESULT_OBJ;
		}
		//验证用户信息
		try {
			systemManagerService.saveModifyPass(userModel);
			m.put("success", true);
			m.put("msg", "修改密码成功");
		} catch (Exception e) {
			e.printStackTrace();
			m.put("success", false);
			m.put("msg", "修改密码失败");
		}
		resultObj = JSONObject.fromObject(m);
		return RESULT_OBJ;
	}
	
	
	/**
	 * 详情
	 * @return
	 */
	@IMethodLog(desc = "用户详情")
	public String getDetailByUserId(){
		String returnString = RESULT_OBJ;
		try {
			Map<String,Object> authDomainList = systemManagerService.getCurrentUserDetail(Integer.parseInt(userModel.getSysUserId()));
			resultObj = JSONObject.fromObject(authDomainList);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}
	
	
	@IMethodLog(desc = "获取密码有效期")
	public String getPasswordValidity(){
		Map<String,Object> rd = new HashMap<String,Object>();
		List<Map<String,Object>> returnData = new ArrayList<Map<String,Object>>();
		Map<String,Object> m1=new HashMap<String,Object>();
		m1.put("id","0");
		m1.put("validity","长期");
		returnData.add(m1);
		m1=new HashMap<String,Object>();
		m1.put("id","1");
		m1.put("validity","三个月");
		returnData.add(m1);
		m1=new HashMap<String,Object>();
		m1.put("id","2");
		m1.put("validity","六个月");
		returnData.add(m1);
		m1=new HashMap<String,Object>();
		m1.put("id","3");
		m1.put("validity","十二个月");
		returnData.add(m1);
		rd.put("rows", returnData);
		resultObj = JSONObject.fromObject(rd);
		return RESULT_OBJ;
	}
	
	
}
