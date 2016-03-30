package com.fujitsu.manager.resourceManager.serviceImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.common.CommonResult;
import com.fujitsu.common.FieldNameDefine;
import com.fujitsu.dao.mysql.AreaManagerMapper;
import com.fujitsu.manager.resourceManager.service.AreaManagerService;
import com.fujitsu.util.ExportExcelUtil;
@Service
@Transactional(rollbackFor = Exception.class)
public class AreaManagerImpl extends AreaManagerService {
	@Resource
	private AreaManagerMapper areaManagerMapper;
	@Resource
	private ICommonManagerService commonManagerService;
	public static class AreaDef {
		public static class LEVEL {
			public static final int ROOT= 0;
			public static final int AREA_LEVEL_1=1;
			public static final int AREA_LEVEL_2= 2;
			public static final int AREA_LEVEL_3= 3;
			public static final int AREA_LEVEL_4= 4;
			public static final int AREA_LEVEL_5= 5;
			public static final int AREA_LEVEL_6= 6;
			public static final int AREA_LEVEL_7= 7;
			public static final int AREA_LEVEL_8= 8;
			public static final int AREA_LEVEL_9= 9;
			public static final int AREA_LEVEL_10= 10;
			public static final int STATION= 11;
			public static final int ROOM= 12;
			public static final int NE= 13;
			public static int LEVEL_MAX= 12;
			public static int LEVEL_AREA_MAX= 8;
		}
	}
//	protected static Map<String, Object> AREA_DEFINE = new HashMap<String, Object>();
//	static {
//		AREA_DEFINE.put("ROOT", AreaDef.LEVEL.ROOT);
//		AREA_DEFINE.put("AREA_LEVEL_1", AreaDef.LEVEL.AREA_LEVEL_1);
//		AREA_DEFINE.put("AREA_LEVEL_2", AreaDef.LEVEL.AREA_LEVEL_2);
//		AREA_DEFINE.put("AREA_LEVEL_3", AreaDef.LEVEL.AREA_LEVEL_3);
//		AREA_DEFINE.put("AREA_LEVEL_4", AreaDef.LEVEL.AREA_LEVEL_4);
//		AREA_DEFINE.put("STATION", AreaDef.LEVEL.STATION);
//		AREA_DEFINE.put("ROOM", AreaDef.LEVEL.ROOM);
//	}
	@Override
	public List<Map<String, Object>> getSubArea(int parentId, int parentLevel,
			int maxLevel) throws CommonException {
		//Map<String, Object> rv = new HashMap<String, Object>();
		List<Map<String, Object>> areaTreeNodes = null;
		int nextLvl=parentLevel<AreaDef.LEVEL.LEVEL_AREA_MAX
				||parentLevel>AreaDef.LEVEL.AREA_LEVEL_10?parentLevel+1:
				parentLevel-AreaDef.LEVEL.LEVEL_AREA_MAX+AreaDef.LEVEL.AREA_LEVEL_10+1;
		if(nextLvl<=maxLevel){
			try {
				//分情况进行查询
				List<Map<String, Object>> row = null;
				if(parentLevel < AreaDef.LEVEL.LEVEL_AREA_MAX){
					row = areaManagerMapper.getSubArea(parentId);
				}else if(parentLevel == AreaDef.LEVEL.LEVEL_AREA_MAX){
					row = areaManagerMapper.getSubStation(parentId);
				}else if(parentLevel == AreaDef.LEVEL.STATION){
					row = areaManagerMapper.getSubRoom(parentId);
				}
				
				areaTreeNodes = new ArrayList<Map<String, Object>>();
				
				//数据组装
				packNodes(row, areaTreeNodes, parentId, nextLvl, maxLevel);
				//如果需要显示局站，则查询区域下的局站
				if(parentLevel < AreaDef.LEVEL.LEVEL_AREA_MAX && maxLevel >= AreaDef.LEVEL.STATION){
					List<Map<String, Object>> stationRows = null;
					stationRows = areaManagerMapper.getSubStation(parentId);
					if(stationRows!=null){
						//数据组装
						packNodes(stationRows, areaTreeNodes, parentId, AreaDef.LEVEL.STATION, maxLevel);
					}
				}
			} catch (Exception e) {
				throw new CommonException(e, -1, "获取资源树失败！");
			}
			return areaTreeNodes;
		}else{
			return new ArrayList<Map<String, Object>>();
		}
	}

	/**
	 * 把原始数据打包成节点数据
	 * @param src		原始数据
	 * @param dst		目标数据
	 * @param curLevel	当前级别
	 * @param maxLevel	最大级别
	 */
	private void packNodes(List<Map<String, Object>> src, List<Map<String, Object>> dst, int parentId, int curLevel, int maxLevel){
		String[] idFields = {
				"RESOURCE_AREA_ID",
				"RESOURCE_AREA_ID","RESOURCE_AREA_ID","RESOURCE_AREA_ID","RESOURCE_AREA_ID","RESOURCE_AREA_ID",
				"RESOURCE_AREA_ID","RESOURCE_AREA_ID","RESOURCE_AREA_ID","RESOURCE_AREA_ID","RESOURCE_AREA_ID",
				"RESOURCE_STATION_ID","RESOURCE_ROOM_ID"};
		String[] datFields = {"AREA_NAME",
				"AREA_NAME","AREA_NAME","AREA_NAME","AREA_NAME","AREA_NAME",
				"AREA_NAME","AREA_NAME","AREA_NAME","AREA_NAME","AREA_NAME",
				"STATION_NAME",
				"ROOM_NAME"};
		
		String idField = idFields[curLevel];
		String textField = datFields[curLevel];
		if(src!=null){
			for(Map<String, Object> dat:src){
				Map<String, Object> node = new HashMap<String, Object>();
				//节点ID
				node.put("id", dat.get(idField) + "-" + curLevel + "-" + parentId);
//				System.out.println("ID = " + dat.get(idField) + "-" + curLevel + "-" + parentId);
//				System.out.println("text = " + dat.get(textField));
				//节点文字
				String text = dat.get(textField)+"";
				if(curLevel <= AreaDef.LEVEL.LEVEL_AREA_MAX &&
				   !"".equals(CommonDefine.TREE.LEVEL_NAME[curLevel].trim())){
					text += "(" + CommonDefine.TREE.LEVEL_NAME[curLevel] + ")";
				}
				node.put("text", text);
				//是否叶子节点
				node.put("leaf", curLevel==maxLevel);
				//是否勾选
				node.put("checked", "none");
				//节点样式
				node.put("cls", "");
				dst.add(node);
			}
		}
	}
	@Override
	public List<String> getAreaProperty() throws CommonException {
		try {
			List<String> rv = new ArrayList<String>();
			rv.add(CommonDefine.TREE.LEVEL_NAME[0]);
			Map paramMap = commonManagerService.getSysParam("AREA_LEVEL");
			if (paramMap != null&&!paramMap.isEmpty()) {
				Object paramObj=paramMap.get("PARAM_VALUE");
				if(paramObj!=null){
					String[] params=paramObj.toString().split(",");
					rv.addAll(Arrays.asList(params));
					AreaDef.LEVEL.LEVEL_AREA_MAX=params.length;
					for(int i=0;i<params.length;i++){
						CommonDefine.TREE.LEVEL_NAME[i+1]=params[i];
					}
					for(int i=AreaDef.LEVEL.STATION;i<=AreaDef.LEVEL.LEVEL_MAX;i++){
						if(AreaDef.LEVEL.LEVEL_MAX>=i){
							rv.add(CommonDefine.TREE.LEVEL_NAME[i]);
						}
					}
					return rv;
				}
			}
			return rv;			
		} catch (Exception e) {
			throw new CommonException(e,-1111,"获取层级属性失败！");
		}
	}
	@Override
	public void setAreaProperty(String newLevelName) throws CommonException {
		try {
			if(newLevelName==null||"".equals(newLevelName.trim())){
				newLevelName=" ";
				for(int i=0;i<AreaDef.LEVEL.LEVEL_AREA_MAX;i++){
					newLevelName+=", ";
				}
			}
			while(newLevelName.endsWith(",-")){
				newLevelName=newLevelName.substring(0,newLevelName.length()-2);
			}
			newLevelName=newLevelName.replaceAll(",,",", ,");
			if(newLevelName.startsWith(",")){
				newLevelName=" "+newLevelName;
			}
			if(newLevelName.endsWith(",")){
				newLevelName=newLevelName+" ";
			}
			Map<String, String> map = new HashMap<String, String>();
			map.put("PARAM_KEY", "AREA_LEVEL");
			map.put("PARAM_VALUE", newLevelName);
			commonManagerService.setSysParam(map);
			String[] names = newLevelName.split(",");
			for (int i = 0; i < names.length; i++) {
				CommonDefine.TREE.LEVEL_NAME[i+1]=names[i];
			}
			AreaDef.LEVEL.LEVEL_AREA_MAX=names.length;
		} catch (CommonException e) {
			throw e;
		} catch (Exception e) {
			throw new CommonException(e,-1111,"设置层级属性失败！");
		}
	}
	@Override
	public void addArea(HashMap<String, Object> map) throws CommonException {
		try {
			areaManagerMapper.addArea(map);
		} catch (Exception e) {
			throw new CommonException(e, -1, "添加"+FieldNameDefine.AREA_NAME+"失败！");
		}
	}
	@Override
	public boolean areaExists(HashMap<String, Object> map)throws CommonException{
		boolean flag = true;
		try {
			List<Map<String, Object>> lst = areaManagerMapper.areaExists(map);
			if(lst == null || lst.size() == 0){
				flag = false;
			}
		} catch (Exception e) {
			throw new CommonException(e, -1, "添加"+FieldNameDefine.AREA_NAME+"失败！");
		}
		return flag;
	}
	@Override
	public void addStation(HashMap<String, Object> map) throws CommonException {
		try {
			areaManagerMapper.addStation(map);
		} catch (Exception e) {
			throw new CommonException(e, -1, "添加"+FieldNameDefine.STATION_NAME+"失败！");
		}
	}
	@Override
	public void addRoom(HashMap<String, Object> map) throws CommonException {
		try {
			areaManagerMapper.addRoom(map);
		} catch (Exception e) {
			throw new CommonException(e, -1, "添加机房失败！");
		}
	}
	@Override
	public void modArea(HashMap<String, Object> map) throws CommonException {
		try {
			areaManagerMapper.modArea(map);
		} catch (Exception e) {
			throw new CommonException(e, -1, "修改资源树失败！");
		}
	}
	@Override
	public void delArea(HashMap<String, Object> map) throws CommonException {
		try {
			areaManagerMapper.delArea(map);
		} catch (Exception e) {
			throw new CommonException(e, -1, "删除资源树失败！");
		}
	}
	@Override
	public void delStation(HashMap<String, Object> map) throws CommonException {
		try {
			areaManagerMapper.delStation(map);
		} catch (Exception e) {
			throw new CommonException(e, -1, "删除资源树失败！");
		}
	}
	@Override
	public void delRoom(HashMap<String, Object> map) throws CommonException {
		try {
			areaManagerMapper.delRoom(map);
		} catch (Exception e) {
			throw new CommonException(e, -1, "删除资源树失败！");
		}
	}
	@Override
	public Map<String, Object> getAreaGrid(int parentId, int parentLevel)
			throws CommonException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> rv = new HashMap<String, Object>();
		String[] parents = new String[AreaDef.LEVEL.LEVEL_AREA_MAX];
		try {
			if(parentLevel < AreaDef.LEVEL.LEVEL_AREA_MAX){
				int pId = parentId;
				Map<String, Object> area = null;
				//pId = Integer.parseInt(area.get("AREA_PARENT_ID").toString());
				//获取各个级别父节点名称
				for(int i = parentLevel; i > 0; i--){
					area = areaManagerMapper.getParentArea(pId);
					parents[i] = area.get("AREA_NAME").toString();
//					System.out.println("lvl = " + i);
					pId = Integer.parseInt(area.get("AREA_PARENT_ID").toString());
//					System.out.println("ID = " + pId + " Name = " + area.get("AREA_NAME"));
				}
//				System.out.println(parents.toString());
				List<Map<String, Object>> areas = areaManagerMapper.getSubArea(parentId);
				for(Map<String, Object> ar : areas){
					Map<String, Object> tmp = new HashMap<String, Object>();
					for(int i = 1; i <= parentLevel; i++){
						tmp.put("level" + i, parents[i]);
					}
					tmp.put("level" + (parentLevel + 1), ar.get("AREA_NAME"));
					tmp.put("level", parentLevel + 1);
					list.add(tmp);
				}
			}
			rv.put("total", list.size());
			rv.put("rows", list);
		} catch (Exception e) {
			 e.printStackTrace();
			throw new CommonException(e,-123123,"aslkjdfhkadhf");
		}
		return rv;
	}
	
	@Override
	public Map<String, Object> getRelatedNE(int stationId,int roomId)
			throws CommonException {
		Map<String, Object> rv = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> dat = areaManagerMapper.getRelatedNE(stationId,roomId);
			rv.put("total", dat.size());
			rv.put("rows", dat);
			return rv;
		} catch (Exception e) {
			throw new CommonException(e, -1, "获取关联网元失败！");
		}
	}
	
	/**
	 * @@@分权分域到网元@@@
	 */
	@Override
	public Map<String, Object> getRelatedNEAuth(Integer userId,int stationId,int roomId)
			throws CommonException {
		Map<String, Object> rv = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> dat = areaManagerMapper.getRelatedNEAuth(stationId,roomId,userId,CommonDefine.TREE.TREE_DEFINE);
			rv.put("total", dat.size());
			rv.put("rows", dat);
			return rv;
		} catch (Exception e) {
			throw new CommonException(e, -1, "获取关联网元失败！");
		}
	}
	
	
	/**
	 * @@@分权分域到网元@@@
	 */
	@Override
	public void clearRelatedNE(Integer userId,Map<String, Object> map) throws CommonException {
		try {
			areaManagerMapper.clearRelatedNE(map,userId,CommonDefine.TREE.TREE_DEFINE);
		} catch (Exception e) {
			throw new CommonException(e, -1, "清除关联网元失败！");
		}
	}
	@Override
	public void updateRelatedNE(Map<String, Object> map) throws CommonException {
		try {
			areaManagerMapper.updateRelatedNE(map);
		} catch (Exception e) {
			throw new CommonException(e, -1, "更新关联网元失败！");
		}
	}
	@Override
	public Map<String, Object> getAreaInfo(int nodeId, int level) throws CommonException {
		Map<String, Object> rv = new HashMap<String, Object>();
		try {
			if(level == AreaDef.LEVEL.STATION){
				rv = areaManagerMapper.getStationInfo(nodeId);
			}else if(level == AreaDef.LEVEL.ROOM){
				rv = areaManagerMapper.getRoomInfo(nodeId);
			}else if(level > AreaDef.LEVEL.ROOT&&
					level <= AreaDef.LEVEL.LEVEL_AREA_MAX){
				rv = areaManagerMapper.getAreaInfo(nodeId);
			}
			return rv;
		} catch (Exception e) {
			throw new CommonException(e, -1, "获取资源信息失败！");
		}
	}
	@Override
	public void modStation(HashMap<String, Object> map) throws CommonException {
		try {
			areaManagerMapper.modStation(map);
		} catch (Exception e) {
			throw new CommonException(e, -1, "修改"+FieldNameDefine.STATION_NAME+"信息失败！");
		}
	}
	@Override
	public void modRoom(HashMap<String, Object> map) throws CommonException {
		try {
			areaManagerMapper.modRoom(map);
		} catch (Exception e) {
			throw new CommonException(e, -1, "修改机房信息失败！");
		}
	}
	@Override
	public boolean stationExists(HashMap<String, Object> map)throws CommonException {
		List<Map<String, Object>> lst = areaManagerMapper.getStationByIdName(map);
		if(lst != null && lst.size()>0){
			return true;
		}
		return false;
	}
	@Override
	public boolean roomExists(HashMap<String, Object> map) throws CommonException{
		List<Map<String, Object>> lst = areaManagerMapper.getRoomByIdName(map);
		if(lst != null && lst.size()>0){
			return true;
		}
		return false;
	}

	@Override
	public List<Map<String, Object>> getSubRoom(int nodeId)throws CommonException {
		return areaManagerMapper.getSubRoom(nodeId);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getSubAreaIds(int parentId)throws CommonException {
		List ids = new ArrayList();
		String areaIds = "(" + parentId + ")";

		for(int i = 0; i < AreaDef.LEVEL.LEVEL_AREA_MAX && areaIds.length() > 2; i++){
//			System.out.println("areaIds = " + areaIds);
			List<Map<String, Object>> tmp = areaManagerMapper.getSubAreaByParentIds(areaIds);
			areaIds = "(";
			int j;
			for (j = 0; j < tmp.size(); j++) {
				areaIds += tmp.get(j).get("RESOURCE_AREA_ID").toString() + ", ";
				ids.add(tmp.get(j).get("RESOURCE_AREA_ID").toString());
			}
			if(j==0)break;
			areaIds = areaIds.substring(0, areaIds.length()-2) + ")"; 
		}

		return ids;
	}
	
	@Override
	public List getSubStationIDs(String areaIds)throws CommonException {
		List ids = new ArrayList();
		List<Map<String, Object>> tmp = areaManagerMapper.getSubStationIDs(areaIds);

		for (int j = 0; j < tmp.size(); j++) {
			ids.add(tmp.get(j).get("RESOURCE_STATION_ID").toString());
		}

		return ids;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getSubRoomIDs(String stationIds) {
		List ids = new ArrayList();
		try{
		List<Map<String, Object>> tmp = areaManagerMapper.getSubRoomIDs(stationIds);
		for (int j = 0; j < tmp.size(); j++) {
			ids.add(tmp.get(j).get("RESOURCE_ROOM_ID").toString());
		}
		}catch (Exception e) {
			e.printStackTrace();
		}

		

		return ids;
	}

	@Override
	public List<Map<String, Object>> getAreaInfoByParentIds(String areaIds) throws CommonException{
		return areaManagerMapper.getAreaInfoByParentIds(areaIds);
	}

	//获取区域的 子局站 Grid列表
	@Override
	@SuppressWarnings("rawtypes")
	public Map<String, Object> getStationGrid(int parentId, boolean showAll,String name,int start,int limit)
			throws CommonException {
		Map<String, Object> rv = new HashMap<String, Object>();
		// 1. 获取下属所有区域的ID
		showAll=parentId==0?false:showAll;
		List ids = new ArrayList();
		if(showAll){
			ids = getSubAreaIds(parentId);
		}
//		System.out.println(ids);
		String parentIds = "(" + parentId;
		for(int i = 0; i< ids.size(); i++){
			parentIds += "," + ids.get(i);
		}
		parentIds += ")";
//		System.out.println("parentIds = " + parentIds);
		// 2. 获取区域ID对应的局站信息
		List<Map<String,Object>> dat = new ArrayList<Map<String,Object>>();
		int total=0;
//		if(!"()".equals(parentIds)){
		dat = areaManagerMapper.getSubStationByIDs(parentIds,name,start,limit);
		total = areaManagerMapper.countSubStationByIDs(parentIds,name);
//		}
		rv.put("total",total);
		rv.put("rows", dat);
		return rv;
	}
	//获取局站的 子机房 Grid列表
	@Override
	public Map<String, Object> getRoomGrid(int parentId, int parentLevel, boolean showAll,
			String name,int start,int limit) throws CommonException {
		Map<String, Object> rv = new HashMap<String, Object>();
		String parentIds = "";
		showAll=parentId==0?false:showAll;
		// 1. 判断选择的节点是不是区域
		if(parentLevel <= AreaDef.LEVEL.LEVEL_AREA_MAX){
			//区域则先获取所有区域下的局站
			//先获取所有区域
			List areaIds = new ArrayList();
			if(showAll){
				areaIds = getSubAreaIds(parentId);
			}
			parentIds = "(" + parentId;
			for(int i = 0; i< areaIds.size(); i++){
				parentIds += "," + areaIds.get(i);
			}
			parentIds += ")";
//			System.out.println("AreaIDs = " + parentIds);
			List stationIds = getSubStationIDs(parentIds);
			parentIds = "(";
			for(int i = 0; i< stationIds.size(); i++){
				if(i > 0){
					parentIds += ",";
				}
				parentIds += stationIds.get(i);
			}
			parentIds += ")";
//			System.out.println("StationIDs = " + parentIds);
		}else if(parentLevel == AreaDef.LEVEL.STATION){
			//否则为局站，直接获取信息
			parentIds = "(" + parentId + ")";
		}else{
			return null;
		}
		List<Map<String,Object>> dat = new ArrayList<Map<String,Object>>();
		int total=0;
		if(!"()".equals(parentIds)){
			dat = areaManagerMapper.getSubRoomByIDs(parentIds,name,start,limit);
			total = areaManagerMapper.countSubRoomByIDs(parentIds,name);
		}
		rv.put("total", total);
		rv.put("rows", dat);
		return rv;
	}

	/**
	 * @@@分权分域到网元@@@
	 */
	@Override
	public Map<String, Object> getNeGrid(Integer userId,Map<String,Object> param,
			int ifRelate,int start,int limit)throws CommonException {  
		Map<String, Object> rv = new HashMap<String, Object>();
		String parentIds = "";
		String stationIds_="",roomIds_="";
		// 1. 判断选择的节点是不是区域
		if(ifRelate==1){
			int parentId = Integer.valueOf(param.get("parentId").toString());
			int parentLevel = Integer.valueOf(param.get("parentLevel").toString());
			Boolean showAll = parentId==0?false:Boolean.valueOf(param.get("showAll").toString()); 
			if(parentLevel <= AreaDef.LEVEL.LEVEL_AREA_MAX){
				//如果是区域则先获取所有区域下的局站
				//先获取所有区域
				List areaIds = new ArrayList();
				if(showAll){
					areaIds = getSubAreaIds(parentId);
				}
				parentIds = "(" + parentId;
				for(int i = 0; i< areaIds.size(); i++){
					parentIds += "," + areaIds.get(i);
				}
				parentIds += ")";
	//			System.out.println("AreaIDs = " + parentIds);
				List stationIds = getSubStationIDs(parentIds);
				parentIds = "(";
				for(int i = 0; i< stationIds.size(); i++){
					if(i > 0){
						parentIds += ",";
					}
					parentIds += stationIds.get(i);
				}
				parentIds += ")";
				stationIds_ = parentIds;
	//			System.out.println("StationIDs = " + parentIds);
				if(AreaDef.LEVEL.LEVEL_MAX>AreaDef.LEVEL.STATION){
					List roomIds = getSubRoomIDs(parentIds);
					parentIds = "(";
					for(int i = 0; i< roomIds.size(); i++){
						if(i > 0){
							parentIds += ",";
						}
						parentIds += roomIds.get(i);
					}
					parentIds += ")";
					roomIds_=parentIds;
				}
	//			System.out.println("RoomIDs = " + parentIds);
			}else if(parentLevel == AreaDef.LEVEL.STATION){
				if(AreaDef.LEVEL.LEVEL_MAX>AreaDef.LEVEL.STATION){
					//如果是局站则获取下面所有机房
					List roomIds = getSubRoomIDs("("+parentId+")");
					parentIds = "(";
					for(int i = 0; i< roomIds.size(); i++){
						if(i > 0){
							parentIds += ",";
						}
						parentIds += roomIds.get(i);
					}
					parentIds += ")";
					roomIds_=parentIds;
				}else{
					parentIds = "("+parentId+")";
					stationIds_ = parentIds;
				}
//				System.out.println("StationIDs = " + parentIds);
	
			}else if(parentLevel == AreaDef.LEVEL.ROOM) {
				//为机房，直接获取信息
				parentIds = "(" + parentId + ")";
				roomIds_=parentIds;
			}else{
				return null;
			}
			param.put("roomIds",roomIds_);
			param.put("stationIds",stationIds_); 
		}
	
		List<Map<String,Object>> dat = new ArrayList<Map<String,Object>>();
		int total = 0;
		if(!"()".equals(parentIds)){
			dat = areaManagerMapper.getSubNeByIDs(userId,CommonDefine.TREE.TREE_DEFINE,ifRelate,param,start,limit);
			total= areaManagerMapper.countSubNeByIDs(userId,CommonDefine.TREE.TREE_DEFINE,ifRelate,param);
		}
		rv.put("total", total);
		rv.put("rows", dat);
		return rv;
	}

	@Override
	public List<Map<String, Object>> getCable(int nodeId)throws CommonException {
		List<Map<String, Object>> rv;
		try {
			rv = areaManagerMapper.getCable(nodeId);
		} catch (Exception e) {
			throw new CommonException(e, -1, "获取"+FieldNameDefine.STATION_NAME+"下属光缆失败！");
		}
		return rv;
	} 
	
	@Override
	public List<Map<String, Object>> getDdfs(int nodeId)throws CommonException {
		List<Map<String, Object>> rv;
		try {
			rv = areaManagerMapper.getDdfs(nodeId);
		} catch (Exception e) {
			throw new CommonException(e, -1, "获取机房下属DDF失败！");
		}
		return rv;
	} 
	@Override
	public List<Map<String, Object>> getOdfs(int nodeId)throws CommonException {
		List<Map<String, Object>> rv;
		try {
			rv = areaManagerMapper.getOdfs(nodeId);
		} catch (Exception e) {
			throw new CommonException(e, -1, "获取机房下属ODF失败！");
		}
		return rv;
	} 
	
	@Override
	public CommonResult exportInfo(List<Map> dat, int exportType, String[] columns) throws CommonException {
		CommonResult result = new CommonResult();
		ExportExcelUtil ex = new ExportExcelUtil(CommonDefine.PATH_ROOT+CommonDefine.EXCEL.TEMP_DIR,"DEAULT");
		if(dat.size() >0){
			formData(exportType, dat);
		} 
		//Header的Map ID必须加上偏移量  AREA_RESOURCE_BASE + exportType
		String destination=ex.writeExcel(dat, columns, CommonDefine.EXCEL.AREA_RESOURCE_BASE + exportType, false);
		if(destination != null){
			result.setReturnResult(CommonDefine.SUCCESS);
			result.setReturnMessage(destination);
		}else{
			result.setReturnResult(CommonDefine.FAILED);
		} 
		return result;
	}
	
	//规范导出内容
	public void formData(int flag, List<Map> rows) {
		if (flag == 5) { 
			transformData(rows, "stationType", new String[] { "火电站", "市县局", "500KV变电站",
					"220KV变电站", "110KV变电站", "35KV变电站及供电所", "110KV集控站" }); 
		}
	}
	public void transformData(List<Map> rows, String cell,String[] displayName) { 
		for (Map data : rows) {
			if (data.get(cell) != null && !"".equals(data.get(cell).toString())) {
				data.put(cell, displayName[Integer.parseInt(data.get(cell).toString())]);
			}
		}
	}
	
	@Override
	public boolean modRoomCheck(HashMap<String, Object> map) throws CommonException{
		List<Map<String, Object>> lst = areaManagerMapper.modRoomCheck(map);
		if(lst != null && lst.size()>0){
			return true;
		}
		return false;
	}
	@Override
	public boolean modStationCheck(HashMap<String, Object> map) throws CommonException{
		List<Map<String, Object>> lst = areaManagerMapper.modStationCheck(map);
		if(lst != null && lst.size()>0){
			return true;
		}
		return false;
	}
	
	@Override
	public void neRelateTo(int nodeId,int prtId,int level,String ids) throws CommonException {
		try {
			areaManagerMapper.neRelateTo(nodeId,prtId,level,ids);
		} catch (Exception e) {
			throw new CommonException(e, -1, "更新网元关联信息失败！");
		}
	}  
	
	@Override
	public void cancelRelateTo(String ids) throws CommonException{
		try {
			areaManagerMapper.cancelRelateTo(ids);
		} catch (Exception e) {
			throw new CommonException(e, -1, "取消关联信息失败！");
		}
	}  
	
	@Override
	public boolean  getRelatedEquip(int cellId) throws CommonException{
		boolean flag = true; 
		try {
			List<Map<String, Object>> rv=areaManagerMapper.getRelatedEquip(cellId);
			if(rv == null || rv.size() == 0){
				flag = false;
			}
		} catch (Exception e) {
			throw new CommonException(e, -1, "获取机房关联设备错误！");
		}
		return flag; 
	}   
	
	@SuppressWarnings("rawtypes")
	public List<Map<String, Object>> getStationListByAreaId(Integer areaId) throws CommonException {
		
		List<Map<String, Object>> rv = new ArrayList<Map<String, Object>>();
		if(areaId == null) {
			rv = areaManagerMapper.getAllStation();
		} else {
			List areaIds = getSubAreaIds(areaId);
			if(areaIds ==null){
				areaIds = new ArrayList();
			}
			areaIds.add(areaId);
			rv = areaManagerMapper.getStationListByAreaIds(areaIds);
		}
		return rv;
	}

	@Override
	public Map<String, Object> getTopAreaNameAndId() throws CommonException {
		Map<String,Object> areaMap = null;
		areaMap = areaManagerMapper.getTopAreaNameAndId();
		if (areaMap != null) {
			List<String> level = getAreaProperty();
			String name = areaMap.get("NAME").toString();
			name = name + "(" + level.get(1) + ")";
			areaMap.put("NAME", name);
		}
		return areaMap;
	}

}
