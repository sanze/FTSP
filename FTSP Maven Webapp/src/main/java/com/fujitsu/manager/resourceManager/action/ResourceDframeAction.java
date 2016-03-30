package com.fujitsu.manager.resourceManager.action;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.IResourceDframeManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.manager.commonManager.action.DownloadAction;

@SuppressWarnings("serial")
public class ResourceDframeAction extends DownloadAction { 
	private Map<String,String> conMap; 
	private String CABLE_NAME;
	private List<String> dataList;
	private int RESOURCE_ROOM_ID;
	private int RESOURCE_CABLE_ID; 
	private List<String> sourceIds;
	private List<String> targetIds; 
	@Resource
	public IResourceDframeManagerService resourceDframeManagerService; 
	
	/**
	 * @@@分权分域到网元@@@
	 */
	@IMethodLog(desc = "ODF架初始化")
	public String getOdfList() {
		//获取用户ID
				
		try {   
			Map<String,Object> data = resourceDframeManagerService.getOdfList(conMap,start,limit,sysUserId);
			resultObj = JSONObject.fromObject(data); 
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return  RESULT_OBJ; 
		} 
	} 
	
	@IMethodLog(desc = "ODF的查询条件“用途”combo")
	public String getUseableList() {
		try {   
			Map<String,Object> data = resourceDframeManagerService.getUseableList();
			resultObj = JSONObject.fromObject(data); 
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return  RESULT_OBJ; 
		} 
	} 
	
	@IMethodLog(desc = "查询条件“光缆”的联想combo")
	public String getCableNameList() {
		try {    
			Map<String,Object> data = resourceDframeManagerService.getCableNameList(CABLE_NAME,
					start,limit);
			resultObj = JSONObject.fromObject(data); 
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return  RESULT_OBJ; 
		} 
	} 
	
	@IMethodLog(desc = "查询条件“光纤芯号”的combo")
	public String getFiberNameList() {
		try {    
			Map<String,Object> data = resourceDframeManagerService.getFiberNameList(RESOURCE_CABLE_ID);
			resultObj = JSONObject.fromObject(data); 
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return  RESULT_OBJ; 
		} 
	} 
	
	@IMethodLog(desc = "增加ODF子架", type = IMethodLog.InfoType.MOD)
	public String addODF() {
		String returnString = RESULT_OBJ; 
		List<Map> odfList = ListStringtoListMap(this.dataList); 
		try {    
			resultObj = JSONObject.fromObject(resourceDframeManagerService.addOdfs(odfList,RESOURCE_ROOM_ID));
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}
	
	@IMethodLog(desc = "删除ODF子架", type = IMethodLog.InfoType.DELETE)
	public String deleteOdf() {
		String returnString = RESULT_OBJ;
		List<Map> sourceIds = ListStringtoListMap(this.dataList); 
		try {    
			result.setReturnResult(CommonDefine.SUCCESS); 
			resultObj = JSONObject.fromObject(resourceDframeManagerService.deleteOdfs(sourceIds));
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}
	
	@IMethodLog(desc = "ODF修改", type = IMethodLog.InfoType.MOD)
	public String modifyODF() {
		try {   
			resultObj = JSONObject.fromObject(resourceDframeManagerService.modifyODF(conMap));
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return  RESULT_OBJ; 
		} 
	} 
	
	@IMethodLog(desc = "获取关联的ODF架信息")
	public String getRelateOdfList() {
		try {   
			Map<String,Object> data = resourceDframeManagerService.getRelateOdfList(RESOURCE_ROOM_ID);
			resultObj = JSONObject.fromObject(data); 
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return  RESULT_OBJ; 
		} 
	} 
	
	@IMethodLog(desc = "ODF关联ODF架信息")
	public String associateOdfWithOdf(){ 
		result.setReturnResult(CommonDefine.SUCCESS); 
		try {   
			List<Map> sourceIds = ListStringtoListMap(this.sourceIds); 
			List<Map> targetIds = ListStringtoListMap(this.targetIds); 
			resultObj = JSONObject.fromObject(resourceDframeManagerService.associateOdfWithOdf(sourceIds,targetIds)); 
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return  RESULT_OBJ; 
		}  
	}
	
	@IMethodLog(desc = "ODF关联端口信息")
	public String associateOdfWithPtp(){ 
		result.setReturnResult(CommonDefine.SUCCESS); 
		try {   
			List<Map> sourceIds = ListStringtoListMap(this.sourceIds); 
			List<Map> targetIds = ListStringtoListMap(this.targetIds); 
			resultObj = JSONObject.fromObject(resourceDframeManagerService.associateOdfWithPtp(sourceIds,targetIds)); 
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return  RESULT_OBJ; 
		}  
	}
	
	@IMethodLog(desc = "ODF删除关联", type = IMethodLog.InfoType.DELETE)
	public String deleteOdfRelate(){ 
		result.setReturnResult(CommonDefine.SUCCESS); 
		try {   
			List<Map> sourceIds = ListStringtoListMap(this.sourceIds); 
			resultObj = JSONObject.fromObject(resourceDframeManagerService.deleteOdfRelate(sourceIds)); 
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return  RESULT_OBJ; 
		}  
	}
	
	@IMethodLog(desc = "ODF导出") 
	public String odfExport() {  
		try { 
			CommonResult data =  resourceDframeManagerService.odfExport(conMap);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		} 
		return RESULT_OBJ; 
	} 
	
	/**
	 * @@@分权分域到网元@@@
	 */
	@IMethodLog(desc = "DDF架初始化")
	public String getDdfList() {
		//获取用户ID
		try {   
			Map<String,Object> data = resourceDframeManagerService.getDdfList(conMap,start,limit,sysUserId);
			resultObj = JSONObject.fromObject(data); 
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return  RESULT_OBJ; 
		} 
	} 
	
	@IMethodLog(desc = "DDF的查询条件“用途”combo")
	public String getDdfUseableList() {
		try {   
			Map<String,Object> data = resourceDframeManagerService.getDdfUseableList();
			resultObj = JSONObject.fromObject(data); 
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return  RESULT_OBJ; 
		} 
	}  
	
	@IMethodLog(desc = "增加DDF子架", type = IMethodLog.InfoType.MOD)
	public String addDDF() {
		String returnString = RESULT_OBJ; 
		List<Map> ddfList = ListStringtoListMap(this.dataList); 
		try {    
			resultObj = JSONObject.fromObject(resourceDframeManagerService.addDdfs(ddfList,RESOURCE_ROOM_ID));
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	}
	
	@IMethodLog(desc = "删除DDF子架", type = IMethodLog.InfoType.DELETE)
	public String deleteDdf() {
		String returnString = RESULT_OBJ;
		List<Map> sourceIds = ListStringtoListMap(this.dataList); 
		try {    
			result.setReturnResult(CommonDefine.SUCCESS); 
			resultObj = JSONObject.fromObject(resourceDframeManagerService.deleteDdfs(sourceIds));
			returnString = RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			returnString = RESULT_OBJ;
		}
		return returnString;
	} 
	
	@IMethodLog(desc = "修改DDF子架", type = IMethodLog.InfoType.MOD)
	public String modifyDDF() {
		try {    
			result.setReturnResult(CommonDefine.SUCCESS); 
			resultObj = JSONObject.fromObject(resourceDframeManagerService.modifyDDF(conMap));
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return RESULT_OBJ;
		} 
	}    
	
	@IMethodLog(desc = "DDF关联端口信息")
	public String associateDdfWithPtp(){ 
		result.setReturnResult(CommonDefine.SUCCESS); 
		try {   
			List<Map> sourceIds = ListStringtoListMap(this.sourceIds); 
			List<Map> targetIds = ListStringtoListMap(this.targetIds); 
			resultObj = JSONObject.fromObject(resourceDframeManagerService.associateDdfWithPtp(sourceIds,targetIds)); 
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return  RESULT_OBJ; 
		}  
	}
	
	@IMethodLog(desc = "DDF删除关联", type = IMethodLog.InfoType.DELETE)
	public String deleteDdfRelate(){ 
		result.setReturnResult(CommonDefine.SUCCESS); 
		try {   
			List<Map> sourceIds = ListStringtoListMap(this.sourceIds); 
			resultObj = JSONObject.fromObject(resourceDframeManagerService.deleteDdfRelate(sourceIds)); 
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return  RESULT_OBJ; 
		}  
	}
	
	@IMethodLog(desc = "获取条线管理的DDF架信息")
	public String getRelateDdfList() {
		try {   
			Map<String,Object> data = resourceDframeManagerService.getRelateDdfList(RESOURCE_ROOM_ID);
			resultObj = JSONObject.fromObject(data); 
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return  RESULT_OBJ; 
		} 
	} 
	
	@IMethodLog(desc = "DDF设置跳线", type = IMethodLog.InfoType.MOD)
	public String associateDdfWithDdf(){ 
		result.setReturnResult(CommonDefine.SUCCESS); 
		try {   
			List<Map> sourceIds = ListStringtoListMap(this.sourceIds); 
			List<Map> targetIds = ListStringtoListMap(this.targetIds); 
			resultObj = JSONObject.fromObject(resourceDframeManagerService.associateDdfWithDdf(sourceIds,targetIds)); 
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return  RESULT_OBJ; 
		}  
	}
	
	@IMethodLog(desc = "DDF删除跳线", type = IMethodLog.InfoType.DELETE)
	public String deleteDdfJumpLine() { 
		result.setReturnResult(CommonDefine.SUCCESS); 
		try {   
			List<Map> sourceIds = ListStringtoListMap(this.sourceIds); 
			resultObj = JSONObject.fromObject(resourceDframeManagerService.deleteDdfJumpLine(sourceIds)); 
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return  RESULT_OBJ; 
		}  
	}
	
	@IMethodLog(desc = "DDF导出") 
	public String ddfExport() { 
		try { 
			CommonResult data =  resourceDframeManagerService.ddfExport(conMap);
			resultObj = JSONObject.fromObject(data);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ; 
	}
	
	public Map<String, String> getConMap() {
		return conMap;
	}

	public void setConMap(Map<String, String> conMap) {
		this.conMap = conMap;
	} 

	public IResourceDframeManagerService getResourceDframeManagerService() {
		return resourceDframeManagerService;
	}

	public void setResourceDframeManagerService(
			IResourceDframeManagerService resourceDframeManagerService) {
		this.resourceDframeManagerService = resourceDframeManagerService;
	}

	public String getCABLE_NAME() {
		return CABLE_NAME;
	}

	public void setCABLE_NAME(String cABLE_NAME) {
		CABLE_NAME = cABLE_NAME;
	}

	public List<String> getDataList() {
		return dataList;
	}

	public void setDataList(List<String> dataList) {
		this.dataList = dataList;
	}

	public int getRESOURCE_ROOM_ID() {
		return RESOURCE_ROOM_ID;
	}

	public void setRESOURCE_ROOM_ID(int rESOURCE_ROOM_ID) {
		RESOURCE_ROOM_ID = rESOURCE_ROOM_ID;
	}

	public int getRESOURCE_CABLE_ID() {
		return RESOURCE_CABLE_ID;
	}

	public void setRESOURCE_CABLE_ID(int rESOURCE_CABLE_ID) {
		RESOURCE_CABLE_ID = rESOURCE_CABLE_ID;
	}

	public List<String> getSourceIds() {
		return sourceIds;
	}

	public void setSourceIds(List<String> sourceIds) {
		this.sourceIds = sourceIds;
	}

	public List<String> getTargetIds() {
		return targetIds;
	}

	public void setTargetIds(List<String> targetIds) {
		this.targetIds = targetIds;
	} 
	
}

