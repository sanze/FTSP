package com.fujitsu.manager.resourceManager.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.IResourceCableManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;

public class ResourceCableAction extends AbstractAction {

	private static final long serialVersionUID = 2280096049525344112L; 
	
	private Integer cellId; 
	private String jsonString;
	private String ids;
	
	private Map<String,Object> param=new HashMap<String,Object>();
	@Resource
	public IResourceCableManagerService resourceCableManagerService;
	
	@IMethodLog(desc = "获取光缆信息")
	public String getCables() { 
		try {
			Map<String, Object> data = resourceCableManagerService.getCables(param,start,limit);
			resultObj = JSONObject.fromObject(data); 
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		} 
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "新增光缆", type = IMethodLog.InfoType.MOD)
	public String addCables() { 
		try {  
			if(resourceCableManagerService.cablesExist(param)){
				formRlt.setReturnResult(CommonDefine.FAILED);
				formRlt.setReturnMessage("光缆已存在！");
				resultObj = JSONObject.fromObject(formRlt);
			} else {
				resourceCableManagerService.addCables(param);
				formRlt.setReturnResult(CommonDefine.SUCCESS);
				formRlt.setReturnMessage("新增光缆成功！");
				resultObj = JSONObject.fromObject(formRlt);
			}
			return RESULT_OBJ;
		} catch (CommonException e) {
			formRlt.setReturnResult(CommonDefine.FAILED);
			formRlt.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(formRlt);
			return RESULT_OBJ;
		} catch (Exception e) {
			formRlt.setReturnResult(CommonDefine.FAILED);
			formRlt.setReturnMessage(e.getMessage());
			resultObj = JSONObject.fromObject(formRlt);
			return RESULT_OBJ;
		} 
	}
	
	@IMethodLog(desc = "获取光缆属性")
	public String getCablesInfo() {
		Map<String, Object> data = new HashMap<String, Object>();
		try { 
			data = resourceCableManagerService.getCablesInfo(cellId); 
			data.put("returnResult",CommonDefine.SUCCESS); 
			resultObj = JSONObject.fromObject(data); 
		} catch (CommonException e) { 
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		} 
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "修改光缆", type = IMethodLog.InfoType.MOD)
	public String modCables() { 
		try { 
			param.put("cablesId", cellId); 
			if(resourceCableManagerService.modCablesCheck(param)){
				formRlt.setReturnResult(CommonDefine.FAILED);
				formRlt.setReturnMessage("光缆已存在！");
			}else{
				resourceCableManagerService.modCables(param);
				formRlt.setReturnResult(CommonDefine.SUCCESS);
				formRlt.setReturnMessage("修改光缆成功！");
			}
			resultObj = JSONObject.fromObject(formRlt);
			return RESULT_OBJ;
		} catch (CommonException e) {
			formRlt.setReturnResult(CommonDefine.FAILED);
			formRlt.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(formRlt);
			return RESULT_OBJ;
		} catch (Exception e) {
			formRlt.setReturnResult(CommonDefine.FAILED);
			formRlt.setReturnMessage(e.getMessage());
			resultObj = JSONObject.fromObject(formRlt);
			return RESULT_OBJ;
		} 
	}
	
	@IMethodLog(desc = "删除光缆", type = IMethodLog.InfoType.DELETE)
	public String delCables() { 
		List<Map<String, Object>> subCable = new ArrayList<Map<String,Object>>(); 
			try{
			subCable=resourceCableManagerService.getSubCable(cellId);
			if(subCable.size()==0){
				resourceCableManagerService.delCables(cellId);
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage("删除光缆成功！");
			}else{
				result.setReturnResult(CommonDefine.FAILED); 
				result.setReturnMessage("请先删除该光缆所包含的(光缆段)信息！");
			}
			resultObj = JSONObject.fromObject(result);
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return RESULT_OBJ;
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getMessage());
			resultObj = JSONObject.fromObject(result);
			return RESULT_OBJ;
		} 
	}
	
	@IMethodLog(desc = "获取光缆段列表信息")
	public String getCableList() {
		try { 
			param.put("cablesId", cellId);
			Map<String, Object> data = resourceCableManagerService.getCableList(param,start,limit);
			resultObj = JSONObject.fromObject(data); 
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		} 
		return RESULT_OBJ; 
	}
	
	@IMethodLog(desc = "获取光缆和光缆代号的列表")
	public String getAllCodeNames() {
		try {  
			Map<String, Object> data = resourceCableManagerService.getAllCodeNames();
			resultObj = JSONObject.fromObject(data); 
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		} 
		return RESULT_OBJ; 
	}
		
	
	@IMethodLog(desc = "新增光缆段", type = IMethodLog.InfoType.MOD)
	public String addCable() { 
		try {  
			param.put("cablesId",Integer.parseInt(ids));
			if(resourceCableManagerService.cableExist(param)){
				formRlt.setReturnResult(CommonDefine.FAILED);
				formRlt.setReturnMessage("光缆段已存在！");
				resultObj = JSONObject.fromObject(formRlt);
			} else {
				resourceCableManagerService.addCable(param);
				formRlt.setReturnResult(CommonDefine.SUCCESS);
				formRlt.setReturnMessage("新增光缆段成功!");
				resultObj = JSONObject.fromObject(formRlt);
			}
			return RESULT_OBJ;
		} catch (CommonException e) {
			formRlt.setReturnResult(CommonDefine.FAILED);
			formRlt.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(formRlt);
			return RESULT_OBJ;
		} catch (Exception e) {
			formRlt.setReturnResult(CommonDefine.FAILED);
			formRlt.setReturnMessage(e.getMessage());
			resultObj = JSONObject.fromObject(formRlt);
			return RESULT_OBJ;
		}  
	}
 
	@IMethodLog(desc = "修改光缆段", type = IMethodLog.InfoType.MOD)
	public String modCable() { 
		try { 
			param.put("cablesId",Integer.parseInt(ids));
			param.put("cableId", cellId);
			if(resourceCableManagerService.modCableCheck(param)){
				formRlt.setReturnResult(CommonDefine.FAILED);
				formRlt.setReturnMessage("该光缆段已存在！");
			}else{
				int result=-99;
				result=resourceCableManagerService.modifyCable(param);
				if(result==1){
					formRlt.setReturnResult(CommonDefine.SUCCESS);
					formRlt.setReturnMessage("修改光缆段成功！");
				}else if(result==0){
					formRlt.setReturnResult(CommonDefine.FAILED);
					formRlt.setReturnMessage("光纤关联链路，不可修改光纤芯数！");
				} 
			}
			resultObj = JSONObject.fromObject(formRlt);
			return RESULT_OBJ;
		} catch (CommonException e) {
			formRlt.setReturnResult(CommonDefine.FAILED);
			formRlt.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(formRlt);
			return RESULT_OBJ;
		} catch (Exception e) {
			formRlt.setReturnResult(CommonDefine.FAILED);
			formRlt.setReturnMessage(e.getMessage());
			resultObj = JSONObject.fromObject(formRlt);
			return RESULT_OBJ;
		} 
	}
	
	@IMethodLog(desc = "获取光缆段属性")
	public String getCableInfo() {
		Map<String, Object> data = new HashMap<String, Object>();
		try { 
			data = resourceCableManagerService.getCableInfo(cellId); 
			data.put("returnResult",CommonDefine.SUCCESS); 
			resultObj = JSONObject.fromObject(data); 
		} catch (CommonException e) { 
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		} 
		return RESULT_OBJ; 
	}
	
	@IMethodLog(desc = "删除光缆段信息", type = IMethodLog.InfoType.DELETE)
	public String deleteCable() {
		List<Map<String, Object>> links = new ArrayList<Map<String,Object>>(); 
		List<Map<String, Object>> odfs = new ArrayList<Map<String,Object>>(); 
		try{
			links=resourceCableManagerService.getLinkById(cellId);
			odfs=resourceCableManagerService.getOdfById(cellId);
			if(links.size()==0 && odfs.size()==0){
				resourceCableManagerService.deleteCable(cellId);
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage("删除光缆段成功！");
			}else{
				result.setReturnResult(CommonDefine.FAILED);  
				String errFields = "";
				// 根据错误的情况进行 详细情况判断 
				errFields+=(links.size()!=0?"链路,":"")+(odfs.size()!=0?"Odf":"");
				errFields="("+(",".equals(errFields.charAt(errFields.length()-1))?errFields.substring(0,errFields.length()-2):errFields)+")";
				result.setReturnMessage("请先删除该光缆段所包含的" + errFields + "等信息！"); 
			}
			resultObj = JSONObject.fromObject(result);
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
			return RESULT_OBJ;
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getMessage());
			resultObj = JSONObject.fromObject(result);
			return RESULT_OBJ;
		} 
	}
	
	@IMethodLog(desc = "获取光纤信息")
	public String getFiberResourceList() {  
		Map<String, Object> data = new HashMap<String, Object>();
		try { 
			if(cellId!=null){
				data = resourceCableManagerService.getFiberResourceList(cellId,limit,start);
			} 
			data.put("returnResult",CommonDefine.SUCCESS); 
			resultObj = JSONObject.fromObject(data); 
		} catch (CommonException e) { 
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		} 
		return RESULT_OBJ;  
	}
 
	@IMethodLog(desc = "修改光纤", type = IMethodLog.InfoType.MOD)
	public String modifyFiberResource() { 
		try { 
			JSONArray jsonArray = JSONArray.fromObject(jsonString); 
			List<Map<String, Object>> fiberList = new ArrayList<Map<String, Object>>();
			for(Object o : jsonArray){
				JSONObject jsonObject = (JSONObject) o; 
				fiberList.add((Map) jsonObject);
			} 
			resourceCableManagerService.modifyFiberResource(fiberList);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("修改光纤成功！");  
			resultObj = JSONObject.fromObject(result);
		
		} catch (CommonException e) { 
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result);
		}

		return RESULT_OBJ;
	}  
	public Integer getCellId() {
		return cellId;
	}
	public void setCellId(Integer cellId) {
		this.cellId = cellId;
		param.put("cellId", cellId);
	}
	public void setDIRECTION(int DIRECTION) { 
		param.put("DIRECTION", DIRECTION);
	}

	public void setName(String name) {
		param.put("name", name);
	}

	public void setNo(String no) {
		param.put("no", no);
	}

	public void setNote(String note) { 
		param.put("note", note);
	}

	public void setCName(String cName) {
		param.put("cName", cName);
	}

	public void setCNo(String cNo) {
		param.put("cNo", cNo);
	}

	public void setCableType(String cableType) {
		param.put("cableType",cableType);
	}

	public void setAStationId(Integer aStationId) { 
		param.put("aStationId",aStationId);
	}

	public void setZStationId(Integer zStationId) {
		param.put("zStationId",zStationId);
	}

	public void setComboCover(String comboCover) {
		param.put("comboCover", comboCover);
	} 
	
	public void setComboType(String comboType) {
		param.put("comboType", comboType);
	} 

	public void setFiberCount(String fiberCount) {
		param.put("fiberCount","".equals(fiberCount)?-99:Integer.parseInt(fiberCount));
	}   
	
	public void setLength_(double length_) {
		param.put("length_",length_);
	} 

	public void setTime(String time) {
		param.put("time",time);
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids; 
	} 

	public void setAttBuild(Double attBuild) {
		param.put("attBuild",attBuild);
	} 

	public void setAttExper(double attExper) {
		param.put("attExper",attExper);
	} 

	public void setAttTheory(double attTheory) {
		param.put("attTheory",attTheory);
	} 
	public void setAttValue(Double attValue) {
		param.put("attValue",attValue);
	}

	public String getJsonString() {
		return jsonString;
	}

	public void setJsonString(String jsonString) {
		this.jsonString = jsonString;
	}
	
	public void setOriginalValue(Integer originalValue) { 
		param.put("originalValue",originalValue);
	}
}
