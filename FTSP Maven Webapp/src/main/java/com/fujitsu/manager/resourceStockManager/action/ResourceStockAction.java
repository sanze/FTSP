package com.fujitsu.manager.resourceStockManager.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.abstractAction.DownloadAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.MessageCodeDefine;
import com.fujitsu.manager.resourceStockManager.service.ResourceStockService;

public class ResourceStockAction extends DownloadAction{

	/**
	 * a static final serialVersionUID field of type long
	 */
	private static final long serialVersionUID = -613483495950361414L;
	
	private String resourceType;
	private List<String> treeNodes;
	private String displayModeType;
	private String neId;
	private List<Integer> neIdList;
	private String resourceId;
	private String originalName;
	private String standardName;
	private String displayMode = null;
	private String manageCategory = null;
	private String note;
	
	@Resource
	public ResourceStockService resourceStockService;
		
	public String search(){
		
		try{
			List<Map> nodeList = ListStringtoListMap(this.treeNodes);
			
			Map<String,Object> data = resourceStockService.getResourceByTreeNodes(resourceType,nodeList,start,limit);

			resultObj = JSONObject.fromObject(data);
		}catch(Exception e){
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.CHANGE_DISPLAYMODE_FAILED));
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}
	
	public String changeDisplayMode(){
		try{
			
			resourceStockService.changeDisplayMode(displayModeType,neIdList,resourceType);
			
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(getText(MessageCodeDefine.CHANGE_DISPLAYMODE_SUCCESS));
			resultObj = JSONObject.fromObject(result);
		}catch(Exception e){
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.CHANGE_DISPLAYMODE_FAILED));
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	public String checkNeNameExit(){
		try{
			Map<String,Object> map = new HashMap<String,Object>();			
			map.put("standardName", standardName);
			map.put("resourceId", Integer.valueOf(resourceId).intValue());
			boolean isExit = resourceStockService.checkNeNameExit(map);
			
			result.setReturnResult(isExit == true ? CommonDefine.TRUE : CommonDefine.FALSE);
			resultObj = JSONObject.fromObject(result);
		}catch(Exception e){
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(getText(MessageCodeDefine.CHANGE_DISPLAYMODE_FAILED));
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	public String saveChangedInfo(){
		try{
			Map<String,Object> map = new HashMap<String,Object>();
			
			map.put("resourceType", resourceType);
			map.put("resourceId", Integer.valueOf(resourceId).intValue());
			//map.put("originalName", originalName);
			map.put("standardName", standardName);
			map.put("displayMode", Integer.valueOf(displayMode).intValue());
			if(manageCategory != null){
				map.put("manageCategory", Integer.valueOf(manageCategory).intValue());
			}else{
				map.put("manageCategory",null);
			}
			
			map.put("note", note);
			
			resourceStockService.saveChangedInfo(map);
			
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("修改成功！");
			resultObj = JSONObject.fromObject(result);
		}catch(Exception e){
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("修改失败！");
			resultObj = JSONObject.fromObject(result);
		}
		
		return RESULT_OBJ;
	}
	
	public String exportResourceStock(){
		
		List<Map> nodeList = ListStringtoListMap(this.treeNodes);
		try{
			String filePath = resourceStockService.exportResourceStock(resourceType,nodeList);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(filePath);
			resultObj = JSONObject.fromObject(result);
		}catch(Exception e){
			e.printStackTrace();
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("导出失败！");
			resultObj = JSONObject.fromObject(result);
		}
		return RESULT_OBJ;
	}


	public String getResourceType() {
		return resourceType;
	}


	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}


	public List<String> getTreeNodes() {
		return treeNodes;
	}


	public void setTreeNodes(List<String> treeNodes) {
		this.treeNodes = treeNodes;
	}


	public String getDisplayModeType() {
		return displayModeType;
	}


	public void setDisplayModeType(String displayModeType) {
		this.displayModeType = displayModeType;
	}

	public String getNeId() {
		return neId;
	}

	public void setNeId(String neId) {
		this.neId = neId;
	}

	public List<Integer> getNeIdList() {
		return neIdList;
	}

	public void setNeIdList(List<Integer> neIdList) {
		this.neIdList = neIdList;
	}

	public String getResourceId() {
		return resourceId;
	}

	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}

	public String getOriginalName() {
		return originalName;
	}

	public void setOriginalName(String originalName) {
		this.originalName = originalName;
	}

	public String getDisplayMode() {
		return displayMode;
	}

	public void setDisplayMode(String displayMode) {
		this.displayMode = displayMode;
	}

	public String getManageCategory() {
		return manageCategory;
	}

	public void setManageCategory(String manageCategory) {
		this.manageCategory = manageCategory;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getStandardName() {
		return standardName;
	}

	public void setStandardName(String standardName) {
		this.standardName = standardName;
	}
	
	
}