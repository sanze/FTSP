package com.fujitsu.manager.systemManager.action;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.IProcessModuleManageService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;

public class ProcessModuleManageAction extends AbstractAction {
	@Resource
	private IProcessModuleManageService moduleManageService;
	
	private String ids;
	private int flag;
	
	public int getFlag() {
		return flag;
	}
	public void setFlag(int flag) {
		this.flag = flag;
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	@IMethodLog(desc = "查询模块进程分页元素")
	public String getProcessModuleData(){
		try {
			Map<String, Object> emsGroupMap = moduleManageService.getProcessModuleManageData(start,limit);
			resultObj = JSONObject.fromObject(emsGroupMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "修改进程模块关闭或者启动", type = IMethodLog.InfoType.MOD)
	public String changeState(){
		boolean isChange = moduleManageService.changeState(ids,flag);
		Map map=new HashMap();
		if(isChange){
			map.put("success",true);
		}else{
			map.put("success",false);
		}
		// 将返回的结果转成JSON对象，返回前台
		resultObj = JSONObject.fromObject(map);
		return RESULT_OBJ;
	}
}
