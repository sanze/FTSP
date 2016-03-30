package com.fujitsu.manager.viewManager.action;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.ILinkEvalTopoManagerService;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;

public class LinkEvalTopoAction extends AbstractAction {
	@Resource
	public ILinkEvalTopoManagerService  linkEvalTopoManagerService;
	
	private int netLevel;
	private int sysId;
	private String evalTime;
	private List<String> positionArray;
	
	@IMethodLog(desc = "链路评估拓扑系统数据")
	public String getSystemList() throws CommonException {
		try{
			Map<String,Object> data =  linkEvalTopoManagerService.getSystemList(netLevel, this.getCurrentUserId());
			resultObj = JSONObject.fromObject(data);
			System.out.println(resultObj);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());			
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "链路评估拓扑图页面初始化数据")
	public String getLinkEvalTopoData() throws CommonException {
		try{
			Map<String,Object> data =  linkEvalTopoManagerService.getLinkEvalTopoData(sysId, evalTime,
					this.getCurrentUserId()); 
			resultObj = JSONObject.fromObject(data);
			System.out.println(resultObj);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());			
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "链路评估拓扑图保存网元座标", type = IMethodLog.InfoType.MOD)
	public String savePosition() {
		
		result.setReturnResult(CommonDefine.SUCCESS);
		result.setReturnMessage("保存布局成功！");
		try {
			linkEvalTopoManagerService.savePosition(this.positionArray);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		resultObj = JSONObject.fromObject(result);
		
		return RESULT_OBJ;
	}
	
	public int getNetLevel() {
		return netLevel;
	}
	public void setNetLevel(int netLevel) {
		this.netLevel = netLevel;
	}
	public int getSysId() {
		return sysId;
	}
	public void setSysId(int sysId) {
		this.sysId = sysId;
	}
	public String getEvalTime() {
		return evalTime;
	}
	public void setEvalTime(String evalTime) {
		this.evalTime = evalTime;
	}
	public List<String> getPositionArray() {
		return positionArray;
	}
	public void setPositionArray(List<String> positionArray) {
		this.positionArray = positionArray;
	}
}
