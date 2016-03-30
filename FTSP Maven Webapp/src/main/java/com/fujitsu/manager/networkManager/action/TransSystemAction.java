package com.fujitsu.manager.networkManager.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.ITransSystemService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.manager.commonManager.action.DownloadAction;
import com.fujitsu.manager.networkManager.serviceImpl.TransSystemServiceImpl;

@SuppressWarnings("serial")
public class TransSystemAction extends DownloadAction{
	@Resource
	public ITransSystemService transSystemService;
	private Map<String,String> paramMap=new HashMap<String,String>(); 
	private List<String> modifyList;
	private List<Integer> intList;
	
	
	@IMethodLog(desc = "传输系统：查询传输系统")
	public String queryTransmissionSystem() {
		
		try {
			Map<String, Object> data = transSystemService
							.queryTransmissionSystem(
							TransSystemServiceImpl.arrangeCondsForQueryTransmissionSystem(paramMap), sysUserId, start, limit);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	//--------------------------------3333333333333-----------------------
	@SuppressWarnings("rawtypes")
	@IMethodLog(desc = "传输系统：获取网元信息和所属区域信息")
	public String getNeInfoWithArea() {
		try {
			List<Map> nodeList = ListStringtoListMap(this.modifyList);
			Map<String, Object> data = transSystemService
					.getNeInfoWithArea(nodeList,sysUserId,intList);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}
	@IMethodLog(desc = "传输系统：获得网元范围内的LINK")
	public String getLinkBetweenNe() {
		try {
			Map<String, Object> data = transSystemService
					.getLinkBetweenNe(intList,paramMap);
			resultObj = JSONObject.fromObject(data);
			
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}

	@IMethodLog(desc = "传输系统：删除传输系统", type = IMethodLog.InfoType.DELETE)
	public String deleteTransmissionSystem() {
		
		try {
			CommonResult data = transSystemService
							.deleteTransmissionSystem(paramMap);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "传输系统：新增传输系统-主", type = IMethodLog.InfoType.MOD)
	public String newTransSystem() {
		
		try {
			CommonResult data = transSystemService
							.newTransSystem(paramMap,intList);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "传输系统：保存传输系统-网元", type = IMethodLog.InfoType.MOD)
	public String saveTransSystemNe() {
		
		try {
			CommonResult data = transSystemService
					.saveTransSystemNe(intList,paramMap);
			resultObj = JSONObject.fromObject(data);
			
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "传输系统：保存传输系统-link", type = IMethodLog.InfoType.MOD)
	public String saveTransSystemLink() {
		
		try {
			CommonResult data = transSystemService
					.saveTransSystemLink(paramMap);
			resultObj = JSONObject.fromObject(data);
			
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "传输系统：获取一个传输系统的信息")
	public String getTransSystem() {
		
		try {
			Map<String, Object> data = transSystemService
							.getTransSystem(paramMap);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	
	@IMethodLog(desc = "传输系统：获得传输系统内的LINK")
	public String getTransSysLink() {
		try {
			Map<String, Object> data = transSystemService
					.getTransSysLink(paramMap);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "传输系统：修改传输系统-主", type = IMethodLog.InfoType.MOD)
	public String updateTransSystem() {
		
		try {
			CommonResult data = transSystemService
							.updateTransSystem(paramMap);
			resultObj = JSONObject.fromObject(data);

		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "传输系统：修改传输系统-网元", type = IMethodLog.InfoType.MOD)
	public String updateTransSystemNe() {
		
		try {
			CommonResult data = transSystemService
					.saveTransSystemNe(intList,paramMap);
			resultObj = JSONObject.fromObject(data);
			
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "传输系统：修改传输系统-LINK", type = IMethodLog.InfoType.MOD)
	public String updateTransSystemLink() {
		
		try {
			CommonResult data = transSystemService
					.saveTransSystemLink(paramMap);
			resultObj = JSONObject.fromObject(data);
			
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "传输系统：检查网元能否删除", type = IMethodLog.InfoType.DELETE)
	public String checkIfNeDeletable() {
		
		try {
			CommonResult data = transSystemService
					.checkIfNeDeletable(intList,paramMap);
			resultObj = JSONObject.fromObject(data);
			
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "传输系统：传输系统-自动发现")
	public String autoFindSystem() {
		try {
			List<Map> nodeList = ListStringtoListMap(this.modifyList);
			CommonResult data = transSystemService.autoFindSystem(nodeList);
			resultObj = JSONObject.fromObject(data);
			
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	public Map<String, String> getParamMap() {
		return paramMap;
	}
	public void setParamMap(Map<String, String> paramMap) {
		this.paramMap = paramMap;
	}
	public List<String> getModifyList() {
		return modifyList;
	}
	public void setModifyList(List<String> modifyList) {
		this.modifyList = modifyList;
	}
	public List<Integer> getIntList() {
		return intList;
	}
	public void setIntList(List<Integer> intList) {
		this.intList = intList;
	}
	
	
}
