package com.fujitsu.manager.resourceManager.action;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IMethodLog;
import com.fujitsu.IService.IResourceManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.util.CommonUtil;
@SuppressWarnings("serial")
public class ResourceAction extends AbstractAction{
	
	           
	@Resource 
	public IResourceManagerService resourceManagerService; 
	private String node;  
	private String ids; 
	private Integer cellId;;
	
	private HashMap<String,Object> param=new HashMap<String,Object>();
	/**
	 * 查询RTU/CTU
	 * @return
	 */
	public String queryRC(){ 
		int nodeId=0,level=0;
		try { 
			if(node!=null && !("").equals(node)){
				String params[] = node.split("-");
				nodeId = Long.valueOf(params[0]).intValue();
				level = Long.valueOf(params[1]).intValue();
			}  
			Map<String, Object> resultMap = resourceManagerService.queryRC(param,nodeId,level, start, limit);
			resultObj = JSONObject.fromObject(resultMap);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getErrorMessage());
			resultObj = JSONObject.fromObject(result); 
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getMessage());
			resultObj = JSONObject.fromObject(result); 
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "新增测试设备", type = IMethodLog.InfoType.MOD)
	public String addRC() { 
		try {  
			param.put("roomId",Integer.parseInt(ids));
			if(resourceManagerService.RCExists(param)){
				formRlt.setReturnResult(CommonDefine.FAILED);
				formRlt.setReturnMessage("测试设备已存在！");
				resultObj = JSONObject.fromObject(formRlt);
			} else {
				resourceManagerService.addRC(param);
				formRlt.setReturnResult(CommonDefine.SUCCESS);
				formRlt.setReturnMessage("新增设备成功！");
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
	
	@IMethodLog(desc = "获取测试设备属性")
	public String geRCInfo() {  
		try { 
			Map<String, Object> map = resourceManagerService.getRCInfo(cellId);
			resultObj = JSONObject.fromObject(map);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getMessage());
			resultObj = JSONObject.fromObject(result);
			return RESULT_OBJ;
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "修改测试设备", type = IMethodLog.InfoType.MOD)
	public String modRC() { 
		try { 
			param.put("rcId", cellId); 
			if(resourceManagerService.modRCCheck(param)){
				formRlt.setReturnResult(CommonDefine.FAILED);
				formRlt.setReturnMessage("测试设备已存在！");
			}else{
				resourceManagerService.modRC(param);
				formRlt.setReturnResult(CommonDefine.SUCCESS);
				formRlt.setReturnMessage("修改设备成功！");
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

	@IMethodLog(desc = "测试面板数据的获取")
	public String queryRCCard(){	
		try { 
			Map<String, Object> map = resourceManagerService.queryRCCard(cellId);
			resultObj = JSONObject.fromObject(map);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getMessage());
			resultObj = JSONObject.fromObject(result);
			return RESULT_OBJ;
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "Flex中取板卡属性")
	public String getTestEquipAttr(){	
		try { 
			Map<String, Object> map = resourceManagerService.getTestEquipAttr(cellId);
			resultObj = JSONObject.fromObject(map);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getMessage());
			resultObj = JSONObject.fromObject(result);
			return RESULT_OBJ;
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "单元盘信息更新", type = IMethodLog.InfoType.MOD)
	public String syncRtuAllCardInfo(){	 
		try { 
			param.put("rcId", cellId); 
			boolean isReachable = CommonUtil.isReachable(param.get("ip").toString());
			if(isReachable){
				if(resourceManagerService.syncRtuAllCardInfo(param)){
					//同步成功的前提下获取面板数据
					Map<String, Object> map = resourceManagerService.queryRCCard(cellId);
					resultObj = JSONObject.fromObject(map);
					return RESULT_OBJ;
				} 
			}else{
				Map<String, Object> map = resourceManagerService.queryRCCard(cellId); 
				map.put("returnResult",CommonDefine.FAILED);
				map.put("returnMessage","网络中断!");	
				resultObj = JSONObject.fromObject(map);
			} 
			return RESULT_OBJ;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("同步失败!");	
			resultObj = JSONObject.fromObject(result);
			return RESULT_OBJ;
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("同步失败!");	
			resultObj = JSONObject.fromObject(result);
			return RESULT_OBJ;
		}
	} 
	
	@IMethodLog(desc = "获取设备和服务器时间") 
	public String getEqptAndServerTime(){	
		Map<String, Object> map  = new HashMap<String, Object>();
		try {
			param.put("rcId", cellId); 
			map = resourceManagerService.getEqptAndServerTime(param);
			resultObj = JSONObject.fromObject(map);
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("获取设备和服务器时间失败!");	
			resultObj = JSONObject.fromObject(result);
			return RESULT_OBJ;
		} 
		return RESULT_OBJ;
	}
	 
	@IMethodLog(desc = "删除设备", type = IMethodLog.InfoType.DELETE)
	public String deleteRC(){	
		Map<String, Object> map  = new HashMap<String, Object>();
		try {
			if(resourceManagerService.isPortUsed(cellId)){
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage("设备端口使用中，无法删除!");	
				resultObj = JSONObject.fromObject(result);
				return RESULT_OBJ;
			}else if(resourceManagerService.testRouteExist(cellId)){
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage("设备存在测试路由，无法删除!");	
				resultObj = JSONObject.fromObject(result);
				return RESULT_OBJ;
			}else{
				resourceManagerService.deleteRC(cellId);
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage("删除成功!");	
				resultObj = JSONObject.fromObject(result);
				return RESULT_OBJ;
			}  
		} catch (Exception e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage("删除设备失败!");	
			resultObj = JSONObject.fromObject(result);
			return RESULT_OBJ;
		}  
	} 
	public void setName(String name) { 
		param.put("name", name);
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}
  
	public void setComboFactory(String comboFactory) { 
		param.put("comboFactory", "".equals(comboFactory)?-99:Integer.parseInt(comboFactory));
	} 
	public void setComboType(String comboType) { 
		param.put("comboType", "".equals(comboType)?-99:Integer.parseInt(comboType));
	} 

	public void setNumber(String number) { 
		param.put("number", number);
	}
 
	public void setIp(String ip) { 
		param.put("ip", ip);
	}
 
	public void setPort(String port) { 
		param.put("port", port);
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	} 

	public void setNote(String note) { 
		param.put("note", note);
	}
  
	public void setStatus(String status) { 
		param.put("status", "".equals(status)?-99:Integer.parseInt(status));
	} 

	public void setTimeOut(String timeOut) { 
		param.put("timeOut", timeOut);
	}

	public Integer getCellId() {
		return cellId;
	}

	public void setCellId(Integer cellId) {
		this.cellId = cellId;
		param.put("cellId", cellId);
	} 
	public void setShelfId(Integer shelfId) { 
		param.put("shelfId", shelfId);
	} 
}
