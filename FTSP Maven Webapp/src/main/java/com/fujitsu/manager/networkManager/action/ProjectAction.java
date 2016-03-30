package com.fujitsu.manager.networkManager.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.ITransSystemService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;

@SuppressWarnings("serial")
public class ProjectAction extends AbstractAction { 
	@Resource
	public ITransSystemService transSystemService;
	
	private Map<String,Object> param=new HashMap<String,Object>();

	@IMethodLog(desc = "传输系统:查询传输系统")
	public String getAllProject(){
		try{
			Map<String,Object>  datasMap=transSystemService.queryTransmissionSystem(param,sysUserId,0,0);
			resultObj=JSONObject.fromObject(datasMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "传输系统:查询传输系统")
	public String getAllLink(){
		try{
			Map<String,Object>  datasMap=transSystemService.getAllLink(param);
			resultObj=JSONObject.fromObject(datasMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	public void setNetLevelId(Integer val){
		param.put("netLevel", val);
	}
	public void setRESOURCE_TRANS_SYS_ID(Integer val){
		param.put("RESOURCE_TRANS_SYS_ID", val);
	}
	public void setA_NE_ID(Integer val){
		param.put("A_NE_ID", val);
	}
	public void setZ_NE_ID(Integer val){
		param.put("Z_NE_ID", val);
	}
	public void setNeList(List<Integer> val){
		param.put("neList", val);
	}
	public Map<String, Object> getParam() {
		return param;
	}
	public void setParam(Map<String, Object> param) {
		this.param = param;
	}
}
