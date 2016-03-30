package com.fujitsu.manager.systemManager.action;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import net.sf.json.JSONObject;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.ISystemManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.model.UserGroupModel;
import com.opensymphony.xwork2.ModelDriven;
public class UserGroupManagementAction extends AbstractAction implements ModelDriven<UserGroupModel>{
	private static final long serialVersionUID = -1584948162970054220L;
	public UserGroupModel userGroupModel=new UserGroupModel();
	@Resource
	public ISystemManagerService systemManagerService;
	@Override
	public UserGroupModel getModel(){
		  return userGroupModel;
	}
	/**
	 * 获取所有用户组信息
	 * 
	 */
	@IMethodLog(desc = "用户组查询")
	public String getUserGroup()
	{
		String returnString = RESULT_OBJ;
		try {
			Map<String, Object> groupDetailMap = systemManagerService.getGroupDetailList(start, limit);
			resultObj = JSONObject.fromObject(groupDetailMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;		
	}
	
	/*
	 * 删除用户组
	 * 返回删除用户组是否成功信息
	 * @return
	 *	SUCCESS resultObj - CommonResult 返回删除成功信息
	 *	ERROR resultObj - CommonResult 返回异常信息
	 * */
	public String deleteGroup(){
		try {
			if(userGroupModel.getSysUserGroupIdList()!=null){
			    for(int i=0;i<userGroupModel.getSysUserGroupIdList().size();i++){
			    	HashMap<String, Object> map = new HashMap<String, Object>();
			    	map.put("sysUserGroupId",userGroupModel.getSysUserGroupIdList().get(i));
				    systemManagerService.deleteGroup(map);
			    }	
			}
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("删除成功");			
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());			
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	public String getAllGroupUserList()
	{
		String returnString = RESULT_OBJ;
		try {
			Map<String, Object> allGroupUserMap = systemManagerService.getAllGroupUserList(userGroupModel);
			resultObj = JSONObject.fromObject(allGroupUserMap);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
		}

	@IMethodLog(desc = "当前用户组的详情")
	public String getCurrentGroupDetail()
	{
		String returnString = RESULT_OBJ;
		try {
			Map<String,Object> currentGroupUser = systemManagerService.getCurrentGroupDetailById(userGroupModel.getSysUserGroupId());
			System.out.println("currentGroupUser:"+currentGroupUser);
			if(currentGroupUser!=null){
				resultObj = JSONObject.fromObject(currentGroupUser);
			}
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}
	
	/**
	 * 新增一个用户组
	 * @return
	 */
	@IMethodLog(desc = "新增一个用户组", type = IMethodLog.InfoType.MOD)
	public String addGroup(){
		Map m = new HashMap();
		//验证组名是否存在
		try {
			Map validateResult=validateUserGroupName();
			if(!(Boolean)validateResult.get("success")){
				resultObj = JSONObject.fromObject(validateResult);
				return RESULT_OBJ;
			}
		} catch (CommonException e1) {
			m.put("success", false);
			m.put("msg", "验证失败！");
			resultObj = JSONObject.fromObject(m);
			return RESULT_OBJ;
		}
		try {
			systemManagerService.addGroup(userGroupModel);
			m.put("success", true);
			m.put("msg", "保存成功！");
		} catch (CommonException e) {
			m.put("success", false);
			m.put("msg", "保存失败！");
		}
		resultObj = JSONObject.fromObject(m);
		return RESULT_OBJ;
	}
	
	private Map validateUserGroupName() throws CommonException {
		return systemManagerService.validateUserGroupName(userGroupModel);
	}
	/**
	 * 修改一个用户组
	 * @return
	 */
	@IMethodLog(desc = "修改一个用户组", type = IMethodLog.InfoType.MOD)
	public String modifyGroup(){
		try {
			systemManagerService.updateGroup(userGroupModel);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("修改成功");	
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());			
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	
	
	
	
	}




	
	
	
	
	
	
	
