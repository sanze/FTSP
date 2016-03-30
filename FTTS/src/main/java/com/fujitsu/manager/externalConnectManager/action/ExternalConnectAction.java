package com.fujitsu.manager.externalConnectManager.action;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import com.fujitsu.IService.IExternalConnectManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;

public class ExternalConnectAction extends AbstractAction{
	@Resource
	private IExternalConnectManagerService externalConnectService;
	private int cableId;        // 光缆段ID
	private int stationId;      // 局站ID
	private int rcId;           // 测试设备ID
	private int aEndId;         // A端ID
	private int zEndId;         // Z端ID
	private int connType;       // 连接类型
	private HashMap<String,Object> connectData = new HashMap<String,Object>();
	private String node;
	private String name;
	
	public String getCableInfo() {
		try {
			Map<String,Object> resultMap = externalConnectService.getCableList(stationId);
			// 将返回的结果转成JSON对象，返回前台
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
	
	public String getFiberInfo() {
		try {
			Map<String,Object> resultMap = externalConnectService.getFiberListByCableId(cableId);
			// 将返回的结果转成JSON对象，返回前台
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
	
	public String getRcInfo() {
		try {
			Map<String,Object> resultMap = externalConnectService.getRcListByStationId(stationId);
			// 将返回的结果转成JSON对象，返回前台
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
	
	public String getUnitInfo() {
		try {
			Map<String,Object> resultMap = externalConnectService.getUnitListByRcId(rcId);
			// 将返回的结果转成JSON对象，返回前台
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
	
	public String getConnectInfoByStationId() {
		try {
			Map<String,Object> resultMap = externalConnectService.getConnectInfoByStationId(stationId);
			// 将返回的结果转成JSON对象，返回前台
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
	
	public String addExternalConnect() {
		System.out.println("新增一条外部光纤连接");
		try {
			connectData.put("STATION_ID", stationId);
			connectData.put("A_END_ID", aEndId);
			connectData.put("Z_END_ID", zEndId);
			connectData.put("CONN_TYPE", connType);
			externalConnectService.addOneExternalConnect(connectData);
			formRlt.setReturnResult(CommonDefine.SUCCESS);
			formRlt.setReturnMessage("新增外部光纤连接成功！");
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
	
	public String delExternalConnect() {
		System.out.println("删除一条外部光纤连接");
		try {
			connectData.put("STATION_ID", stationId);
			connectData.put("A_END_ID", aEndId);
			connectData.put("Z_END_ID", zEndId);
			connectData.put("CONN_TYPE", connType);
			externalConnectService.delOneExternalConnect(connectData);
			formRlt.setReturnResult(CommonDefine.SUCCESS);
			formRlt.setReturnMessage("删除外部光纤连接成功！");
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
	
	public String getStationList() {
		String params[] = node.split("-");
		int parentId,parentLevel;
		try {
			parentId = Long.valueOf(params[0]).intValue();
			parentLevel = Long.valueOf(params[1]).intValue();
			//判断是直接点的节点还是点的显示所有机房
			boolean showAll = "showAll".equals(params[2]);
			Map<String,Object> resultMap = externalConnectService.getStationList(parentId, parentLevel,
					showAll,name,start,limit);
			// 将返回的结果转成JSON对象，返回前台
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
	
	public int getCableId() {
		return cableId;
	}

	public void setCableId(int id) {
		this.cableId = id;
	}
	
	public int getStationId() {
		return stationId;
	}
	
	public void setStationId(int staId) {
		this.stationId = staId;
	}
	
	public int getRcId() {
		return rcId;
	}
	
	public void setRcId(int rcid) {
		this.rcId = rcid;
	}
	
	public int getAEndId() {
		return aEndId;
	}
	
	public void setAEndId(int aId) {
		this.aEndId = aId;
	}
	
	public int getZEndId() {
		return zEndId;
	}
	
	public void setZEndId(int zId) {
		this.zEndId = zId;
	}
	
	public int getConnType() {
		return connType;
	}
	
	public void setConnType(int type) {
		this.connType = type;
	}

	public String getNode() {
		return node;
	}

	public void setNode(String node) {
		this.node = node;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
