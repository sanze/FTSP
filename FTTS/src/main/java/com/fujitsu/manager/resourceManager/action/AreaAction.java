package com.fujitsu.manager.resourceManager.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.fujitsu.IService.IAreaManagerService;
import com.fujitsu.abstractAction.AbstractAction;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.FieldNameDefine;
import com.fujitsu.IService.IMethodLog;
import com.fujitsu.manager.resourceManager.serviceImpl.AreaManagerImpl;
import com.fujitsu.manager.resourceManager.serviceImpl.AreaManagerImpl.AreaDef;

@SuppressWarnings("serial")
public class AreaAction extends AbstractAction { 
	@Resource
	public IAreaManagerService areaManagerService;
	private String node;
	private String areaName;
	private String parentArea;
	private String name;
	private String management;
	private String address;
	private String roomType;
	private String phone;
	private String note;
	private String ids;
	private int exportType;
	private int newParentId;
	private int maxLevel;
	
	private HashMap<String,Object> param=new HashMap<String,Object>();
	
	@IMethodLog(desc = "获取资源树的子节点")
	public String getSubArea() {
		//System.out.println("---------- getSubArea executed! ------------");
		String params[] = node.split("-");
		int parentId,parentLevel;
		try {
			parentId = Long.valueOf(params[0]).intValue();
			parentLevel = Long.valueOf(params[1]).intValue();
			List<Map<String, Object>> rv = areaManagerService.getSubArea(parentId, parentLevel, maxLevel);
			resultArray = JSONArray.fromObject(rv);
			return RESULT_ARRAY;
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

	@IMethodLog(desc = "获取资源树的 子节点 Grid列表")
	public String getAreaGrid() {
		String params[] = node.split("-");
		int parentId,parentLevel;
		try {
			parentId = Long.valueOf(params[0]).intValue();
			parentLevel = Long.valueOf(params[1]).intValue();
			Map<String, Object> rv = areaManagerService.getAreaGrid(parentId, parentLevel);
			resultObj = JSONObject.fromObject(rv);
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
	@IMethodLog(desc = "获取区域的 子局站 Grid列表")
	public String getStationGrid() {
		String params[] = node.split("-");
		int parentId;
		try {
			parentId = Long.valueOf(params[0]).intValue();
			//判断是直接点的节点还是点的显示所有局站
			boolean showAll = "showAll".equals(params[2]);
			Map<String, Object> rv = areaManagerService.getStationGrid(parentId, showAll,name,start,limit);
			resultObj = JSONObject.fromObject(rv);
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
	@IMethodLog(desc = "获取局站/区域的 子机房 Grid列表")
	public String getRoomGrid() {
		String params[] = node.split("-");
		int parentId,parentLevel;
		try {
			parentId = Long.valueOf(params[0]).intValue();
			parentLevel = Long.valueOf(params[1]).intValue();
			//判断是直接点的节点还是点的显示所有机房
			boolean showAll = "showAll".equals(params[2]);
			Map<String, Object> rv = areaManagerService.getRoomGrid(parentId, parentLevel, showAll);
			resultObj = JSONObject.fromObject(rv);
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
	
//	/**
//	 * @@@分权分域到网元@@@
//	 * @return
//	 */
//	@IMethodLog(desc = "获取区域/局站/机房的 子网元 Grid列表")
//	public String getNeGrid() {
//		String params[] = node.split("-");
//		int parentId,parentLevel;
//		try {
//			parentId = Long.valueOf(params[0]).intValue();
//			parentLevel = Long.valueOf(params[1]).intValue();
//			//判断是直接点的节点还是点的显示所有网元 
//			boolean showAll = "showAll".equals(params[2]);
//			Map<String, Object> rv = areaManagerService.getNeGrid(sysUserId,parentId, parentLevel, showAll);
//			resultObj = JSONObject.fromObject(rv);
//			return RESULT_OBJ;
//		} catch (CommonException e) {
//			result.setReturnResult(CommonDefine.FAILED);
//			result.setReturnMessage(e.getErrorMessage());
//			resultObj = JSONObject.fromObject(result);
//			return RESULT_OBJ;
//		} catch (Exception e) {
//			result.setReturnResult(CommonDefine.FAILED);
//			result.setReturnMessage(e.getMessage());
//			resultObj = JSONObject.fromObject(result);
//			return RESULT_OBJ;
//		}
//	}
	@IMethodLog(desc = "添加区域")
	public String addArea() {
		String params[] = node.split("-");
		int parentId,parentLevel;
		try {
			// 设置字段
			parentId = Long.valueOf(params[0]).intValue();
			parentLevel = Long.valueOf(params[1]).intValue();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("areaName", areaName);
			map.put("areaLevel", (parentLevel + 1));
			map.put("areaParentId", parentId);
			boolean f = areaManagerService.areaExists(map);
//			System.out.println("Exist = " + f);
			if(!f){
				areaManagerService.addArea(map);
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage("新增"+FieldNameDefine.AREA_NAME+"成功！");
			}else{
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(FieldNameDefine.AREA_NAME+"已存在！");
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
	@IMethodLog(desc = "添加局站")
	public String addStation() {
		try {
			if(areaManagerService.stationExists(param)){
				formRlt.setReturnResult(CommonDefine.FAILED);
				formRlt.setReturnMessage(FieldNameDefine.STATION_NAME+"已存在！");
				resultObj = JSONObject.fromObject(formRlt);
			} else {
				areaManagerService.addStation(param);
				formRlt.setReturnResult(CommonDefine.SUCCESS);
				formRlt.setReturnMessage("新增"+FieldNameDefine.STATION_NAME+"成功！");
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
	@IMethodLog(desc = "添加机房")
	public String addRoom() {
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentId", newParentId);
			map.put("name", name);
			map.put("roomType", roomType);
			map.put("management", management);
			map.put("phone", phone);
			map.put("note", note);
			if(areaManagerService.roomExists(map)){
				formRlt.setReturnResult(CommonDefine.FAILED);
				formRlt.setReturnMessage("机房已存在！");
			}else{
				areaManagerService.addRoom(map);
				formRlt.setReturnResult(CommonDefine.SUCCESS);
				formRlt.setReturnMessage("新增机房成功！");
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
	@IMethodLog(desc = "获取资源树的级别名称")
	public String getAreaProperty() {
		try {
			List<String> map = areaManagerService.getAreaProperty();
			resultArray = JSONArray.fromObject(map);
			return RESULT_ARRAY;
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getMessage());
			resultObj = JSONObject.fromObject(result);
			return RESULT_OBJ;
		}
	}
	@IMethodLog(desc = "获取资源树的级别名称")
	public String getAreaInfo() {
		String params[] = node.split("-");
		int parentId=0,parentLevel=0;
		try {
			parentId = Long.valueOf(params[0]).intValue();
			parentLevel = Long.valueOf(params[1]).intValue();
			Map<String, Object> map = areaManagerService.getAreaInfo(parentId, parentLevel);
			resultObj = JSONObject.fromObject(map);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getMessage());
			resultObj = JSONObject.fromObject(result);
			return RESULT_OBJ;
		}
		return RESULT_OBJ;
	}
	@IMethodLog(desc = "设置资源树的级别名称", type = IMethodLog.InfoType.MOD)
	public String setAreaProperty() {
		try {
			areaManagerService.setAreaProperty(areaName);
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage("级别名称修改成功！");
			resultObj = JSONObject.fromObject(result);
		} catch (CommonException e) {
			result.setReturnResult(CommonDefine.FAILED);
			result.setReturnMessage(e.getMessage());
			resultObj = JSONObject.fromObject(result);
			return RESULT_OBJ;
		}
		return RESULT_OBJ;
	}
	
	@IMethodLog(desc = "修改资源树的子节点", type = IMethodLog.InfoType.MOD)
	public String modArea() {
		String params[] = node.split("-");
		int nodeId;
		try {
			nodeId = Long.valueOf(params[0]).intValue();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("areaId", nodeId);
			map.put("areaName", areaName);
			map.put("areaParentId", newParentId);
			boolean f = areaManagerService.areaExists(map);
//			System.out.println("Exist = " + f);
			if(!f){
				areaManagerService.modArea(map);
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage("修改"+FieldNameDefine.AREA_NAME+"成功！");
			}else{
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage(FieldNameDefine.AREA_NAME+"已存在！");
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
	@IMethodLog(desc = "修改资源树的子节点-局站", type = IMethodLog.InfoType.MOD)
	public String modStation() {
		String params[] = node.split("-");
		int nodeId, prtId;
		try {
			nodeId = Long.valueOf(params[0]).intValue();
			prtId = Long.valueOf(params[2]).intValue();
			param.put("stationId", nodeId);
			param.put("parentId", prtId);
//			boolean f = areaManagerService.stationExists(map);
			if(areaManagerService.modStationCheck(param)){
				formRlt.setReturnResult(CommonDefine.FAILED);
				formRlt.setReturnMessage(FieldNameDefine.STATION_NAME+"已存在！");
			}else{
				areaManagerService.modStation(param);
				formRlt.setReturnResult(CommonDefine.SUCCESS);
				formRlt.setReturnMessage("修改"+FieldNameDefine.STATION_NAME+"成功！");
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
	@IMethodLog(desc = "修改资源树的子节点", type = IMethodLog.InfoType.MOD)
	public String modRoom() {
		String params[] = node.split("-");
		int nodeId, prtId;
		try {
			nodeId = Long.valueOf(params[0]).intValue();
			prtId = Long.valueOf(params[2]).intValue();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("roomId", nodeId);
			map.put("roomName", name);
			map.put("parentId", prtId);
			map.put("name", name);
			map.put("roomType", roomType);
			map.put("management", management);
			map.put("phone", phone);
			map.put("note", note);

			if(areaManagerService.modRoomCheck(map)){
				formRlt.setReturnResult(CommonDefine.FAILED);
				formRlt.setReturnMessage("机房已存在！");
			}else{
				areaManagerService.modRoom(map);
				formRlt.setReturnResult(CommonDefine.SUCCESS);
				formRlt.setReturnMessage("修改机房成功！");
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
	
	@IMethodLog(desc = "删除资源树的子节点", type = IMethodLog.InfoType.DELETE)
	public String delArea() {
		String params[] = node.split("-");
		int nodeId, nodeLevel;
		try {
			nodeId = Long.valueOf(params[0]).intValue();
			nodeLevel = Long.valueOf(params[1]).intValue();
			List<Map<String, Object>> rv = areaManagerService.getSubArea(nodeId, nodeLevel, 6);
			if(rv.size()==0){
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("areaId", nodeId);
				areaManagerService.delArea(map);
				
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage("删除"+FieldNameDefine.AREA_NAME+"成功！");
			}else{
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage("请先删除该"+FieldNameDefine.AREA_NAME+"的下级"+FieldNameDefine.AREA_NAME+"及所包含"+FieldNameDefine.STATION_NAME+"、机房、纤缆等信息！");
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
	@IMethodLog(desc = "删除 局站", type = IMethodLog.InfoType.DELETE)
	public String delStation() {
		String params[] = node.split("-");
		int nodeId;
		try {
			nodeId = Long.valueOf(params[0]).intValue();
			List<Map<String, Object>> subRooms = new ArrayList<Map<String,Object>>();
			if(AreaManagerImpl.AreaDef.LEVEL.LEVEL_MAX>=AreaManagerImpl.AreaDef.LEVEL.ROOM)
				subRooms=areaManagerService.getSubRoom(nodeId);
			List<Map<String, Object>> subCables = areaManagerService.getCable(nodeId);
			if(subRooms.size()==0 && subCables.size()==0){
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("stationId", nodeId);
				areaManagerService.delStation(map);
				
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage("删除"+FieldNameDefine.STATION_NAME+"成功！");
			}else{
				result.setReturnResult(CommonDefine.FAILED);
				String errFields = "";
				// 根据错误的情况进行 详细情况判断
				errFields = (subRooms.size()!=0? 
						(subCables.size()!=0?"<机房、纤缆>":"<机房>"):
							(subCables.size()!=0?"<纤缆>":"ERR_BRANCH"));
				result.setReturnMessage("请先删除该"+FieldNameDefine.STATION_NAME+"所包含的" + errFields + "等信息！");
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
	@IMethodLog(desc = "删除 机房", type = IMethodLog.InfoType.DELETE)
	public String delRoom() {
		String params[] = node.split("-");
		int nodeId;
		try {
			nodeId = Long.valueOf(params[0]).intValue();
			//
			Map<String, Object> rv = areaManagerService.getRelatedNE(nodeId);
			int cnt = Integer.parseInt(rv.get("total").toString());
			if(cnt == 0){
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("roomId", nodeId);
				areaManagerService.delRoom(map);
//				areaManagerService.clearRelatedNE(map);
				result.setReturnResult(CommonDefine.SUCCESS);
				result.setReturnMessage("删除机房成功！");
			}else{
				result.setReturnResult(CommonDefine.FAILED);
				result.setReturnMessage("请先删除该机房 所关联的网元等信息！");
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
	

//	/**
//	 * @@@分权分域到网元@@@
//	 * @return
//	 */
//	@IMethodLog(desc = "获取已关联到机房的网元列表")
//	public String getRelatedNE() {
//		try { 
//			int id = Integer.valueOf(node).intValue();
//			Map<String, Object> map = areaManagerService.getRelatedNEAuth(sysUserId,id);
//			resultObj = JSONObject.fromObject(map);
//			System.out.println();
//		} catch (CommonException e) {
//			result.setReturnResult(CommonDefine.FAILED);
//			result.setReturnMessage(e.getMessage());
//			resultObj = JSONObject.fromObject(result);
//			return RESULT_OBJ;
//		}
//		return RESULT_OBJ;
//	}
	
//	/**
//	 * @@@分权分域到网元@@@
//	 * @return
//	 */
//	@IMethodLog(desc = "更新关联到机房的网元列表", type = IMethodLog.InfoType.MOD)
//	public String updateRelatedNE() {
//		try {
////			System.out.println("roomId @ " + node);
////			System.out.println("nes @ " + ids);
//			int neId = Integer.valueOf(node).intValue();
//			Map<String, Object> map =new HashMap<String, Object>();
//			map.put("roomId", neId);
//			map.put("ids", "(" + ids + ")");
//			//先清除旧的关联
//			areaManagerService.clearRelatedNE(sysUserId,map);
//			if(ids.length()>0){
//				//添加新的关联
//				areaManagerService.updateRelatedNE(map);
//			}
//			result.setReturnResult(CommonDefine.SUCCESS);
//			result.setReturnMessage("关联网元成功！");
//			resultObj = JSONObject.fromObject(result);
//		} catch (CommonException e) {
//			result.setReturnResult(CommonDefine.FAILED);
//			result.setReturnMessage(e.getMessage());
//			resultObj = JSONObject.fromObject(result);
//			return RESULT_OBJ;
//		}
//		return RESULT_OBJ;
//	}
//	@IMethodLog(desc = "导出")
//	public String exportInfo() {
//		try {
//			List<Map> dat = new ArrayList<Map>();
//			String[] header = null;
//			if(exportType<=AreaDef.LEVEL.LEVEL_AREA_MAX){
//				//getAreaGrid
//				getAreaGrid();
////				exportType = 4;
//				List<String> headerArea = new ArrayList<String>();
//				for(int i=0;i<AreaDef.LEVEL.LEVEL_AREA_MAX;i++){
//					headerArea.add("level"+(i+1));
//				}
//				headerArea.add("level");
//				headerArea.add("levelName");
//				header=new String[headerArea.size()];
//				header = headerArea.toArray(header);
//			}else if(exportType==AreaDef.LEVEL.STATION){
//				//getStationGrid
//				getStationGrid();
//				String[] headerStation = {"areaName", "stationName", "address", "management", "phone", "note"};
//				header = headerStation;
//			}else if(exportType==AreaDef.LEVEL.ROOM){
//				//getRoomGrid
//				getRoomGrid();
//				String[] headerRoom = {"stationName", "roomName", "roomType", "management", "phone", "note"};
//				header = headerRoom;
//			}else if (exportType==AreaDef.LEVEL.NE) {
//				//getNeGrid
//				getNeGrid();
//				String[] headerNe = {"areaName", "stationName", "roomName", "emsGroup", "emsName", "neName", "neModel"};
//				header = headerNe;
//			}
//			int total = Integer.parseInt(resultObj.get("total").toString());
//			JSONArray arr = resultObj.getJSONArray("rows");
////			System.out.println(resultObj.toString());
//			//数据还原（从JSONObject到List<Map>）
//			for (int i = 0; i < total; i++) {
//				Map e = new HashMap();
//				JSONObject obj = arr.getJSONObject(i);
//				for (int j = 0; j < header.length; j++) {
//					Object value = obj.get(header[j]);
//					e.put(header[j], value);
//				}
//				if(exportType <= AreaDef.LEVEL.LEVEL_AREA_MAX){
//					//如果是区域信息，则补上区域级别名称
//					int level = obj.getInt("level");
//					for (int j = 0; j < AreaDef.LEVEL.LEVEL_AREA_MAX; j++) {
//						if(e.get(header[j])!=null&&
//							CommonDefine.TREE.LEVEL_NAME.length>j+1&&
//							CommonDefine.TREE.LEVEL_NAME[j+1]!=null&&
//							!CommonDefine.TREE.LEVEL_NAME[j+1].trim().isEmpty()){
//							e.put(header[j], e.get(header[j]) + "(" + CommonDefine.TREE.LEVEL_NAME[j+1] + ")");
//						}
//					}
//					e.put("levelName", CommonDefine.TREE.LEVEL_NAME[level]);
//				}
//				dat.add(e);
//			}
//			if(exportType<=AreaDef.LEVEL.LEVEL_AREA_MAX){
//				exportType=4;
//			}else if(exportType==AreaDef.LEVEL.STATION){
//				exportType=5;
//			}else if(exportType==AreaDef.LEVEL.ROOM){
//				exportType=6;
//			}else if(exportType==AreaDef.LEVEL.NE){
//				exportType=7;
//			}
////			System.out.println(resultObj.toString());
//			CommonResult data = areaManagerService.exportInfo(dat, exportType,header);
//			result.setReturnResult(CommonDefine.SUCCESS);
//			result.setReturnMessage("导出数据成功！");
//			resultObj = JSONObject.fromObject(data);
////			System.out.println("RV　＝　" + resultObj.toString());
//		} catch (Exception e) {
//			result.setReturnResult(CommonDefine.FAILED);
//			result.setReturnMessage(e.getMessage());
//			resultObj = JSONObject.fromObject(result);
//			return RESULT_OBJ;
//		}
//		return RESULT_OBJ;
//	}

	public int getMaxLevel() {
		return maxLevel;
	}
	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public int getNewParentId() {
		return newParentId;
	}
	public void setNewParentId(int newParentId) {
		this.newParentId = newParentId;
		param.put("parentId", newParentId);
	}
	public String getIds() {
		return ids;
	}
	public void setIds(String ids) {
		this.ids = ids;
	}
	public String getParentArea() {
		return parentArea;
	}
	public void setParentArea(String parentArea) {
		this.parentArea = parentArea;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
		param.put("stationName", name);
	}
	public String getManagement() {
		return management;
	}
	public void setManagement(String management) {
		this.management = management;
		param.put("management", management);
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
		param.put("address", address);
	}
	public String getRoomType() {
		return roomType;
	}
	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
		param.put("phone", phone);
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
		param.put("note", note);
	}

	public int getExportType() {
		return exportType;
	}

	public void setExportType(int exportType) {
		this.exportType = exportType;
	}
}
