package com.fujitsu.manager.resourceManager.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.IService.IAreaManagerService;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.AreaManagerMapper;
import com.fujitsu.dao.mysql.ResourceStatisticManagerMapper;
import com.fujitsu.manager.commonManager.service.CommonManagerService;
import com.fujitsu.manager.resourceManager.service.ResourceStatisticManagerService;
import com.fujitsu.manager.resourceManager.serviceImpl.AreaManagerImpl.AreaDef;

@Scope("prototype")
@Service
@Transactional(rollbackFor = Exception.class)
public class ResourceStatisticManagerImpl extends ResourceStatisticManagerService {
	
	@Resource
	private IAreaManagerService areaManagerImpl; 
	@Resource
	private CommonManagerService commonManagerService;
	@Resource
	private AreaManagerMapper areaManagerMapper;
	@Resource
	private ResourceStatisticManagerMapper resourceStatisticManagerMapper;
	
	/** 
	 * 网元列表
	 * @param String 
	 * @return Map<String,Object>
	 * @throws CommonException
	 */ 
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<Map> getStatistic(Map <String,Object> map,String type)
			throws CommonException {
		
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpSession session = request.getSession();
			//获取当前用户ID
			int userId = Integer.valueOf(session.getAttribute("SYS_USER_ID").toString()).intValue();
			List<Map> emsIDs = commonManagerService.getAllEmsByEmsGroupId(userId,CommonDefine.VALUE_ALL, false,true);
			String emsIdListString = constructInSearchCondition(emsIDs,"BASE_EMS_CONNECTION_ID");
			
			int parentId, parentLevel;
			parentLevel = Integer.valueOf(map.get("lvl").toString());
			parentId = Integer.valueOf(map.get("id").toString());
			String parentIds = "";
			// 1. 判断选择的节点是不是区域
			if(parentLevel < AreaDef.LEVEL.STATION){
				//查询区域Id
				List areaIds = areaManagerImpl.getSubAreaIds(parentId);
				//加入本身区域Id
				areaIds.add(parentId);
				parentIds = constructInSearchCondition(areaIds,null);
				//查询局站Id
				List stationIds = areaManagerImpl.getSubStationIDs(parentIds);
				//添加无效数据，防止查询错误
				stationIds.add(-99);
				parentIds = constructInSearchCondition(stationIds,null);
				//查询机房Id
				List roomIds = areaManagerImpl.getSubRoomIDs(parentIds);
				//添加无效数据，防止查询错误
				roomIds.add(-99);
				parentIds = constructInSearchCondition(roomIds,null);
			}else if(parentLevel == AreaDef.LEVEL.STATION){
				//查询机房Id
				parentIds = "("+parentId+")";
				List roomIds = areaManagerImpl.getSubRoomIDs(parentIds);
				parentIds = constructInSearchCondition(roomIds,null);
			}
			else{
				return null;
			}
		List<Map> dat = new ArrayList<Map>();
		if(!("()".equals(parentIds)||"()".equals(emsIdListString))){
			if(type.equals("neModel")){
				dat = resourceStatisticManagerMapper.getSubNeByIDsChart(parentIds, 
						emsIdListString, map.get("text").toString());
			}else if(type.equals("unitName")){
				dat = resourceStatisticManagerMapper.getSubUnitByIDsChart(parentIds, 
						emsIdListString, map.get("text").toString());
			}else if(type.equals("ptpType")){
				dat = resourceStatisticManagerMapper.getSubPortByIDsChart(parentIds,
						emsIdListString, map.get("text").toString());
			}
		}
		return dat;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String,Object> getStatisticGrid(List<Map<String,Object>> data ,
			String type,int start,int limit)throws CommonException {
			HttpServletRequest request = ServletActionContext.getRequest();
			HttpSession session = request.getSession();
			//获取当前用户ID
			int userId = Integer.valueOf(session.getAttribute("SYS_USER_ID").toString()).intValue();
			List<Map> emsIDs = commonManagerService.getAllEmsByEmsGroupId(userId,CommonDefine.VALUE_ALL, false,true);
			String emsIdListString = constructInSearchCondition(emsIDs,"BASE_EMS_CONNECTION_ID");
			
			Map<String, Object> rv = new HashMap<String, Object>();
			
			int parentLevel;
			int parentId; 
			String 	parentIds = "(";
			for(Map datatmp:data){
				parentLevel = Integer.valueOf(datatmp.get("lvl").toString());
				parentId = Integer.valueOf(datatmp.get("id").toString());
				
				String pIds = "";
				// 1. 判断选择的节点是不是区域
				if(parentLevel < AreaDef.LEVEL.LEVEL_AREA_MAX){
					//获取区域Id
					List areaIds = areaManagerImpl.getSubAreaIds(parentId);
					//加入本身区域Id
					areaIds.add(parentId);
					pIds = constructInSearchCondition(areaIds,null);
					//获取局站Id
					List stationIds = areaManagerImpl.getSubStationIDs(pIds);
					//添加无效数据，防止查询错误
					stationIds.add(-99);
					pIds = constructInSearchCondition(stationIds,null);
					//获取机房d
					List roomIds = areaManagerImpl.getSubRoomIDs(pIds);
					for(int i = 0; i< roomIds.size(); i++){
						parentIds += roomIds.get(i)+ ",";
					}
				}else if(parentLevel == AreaDef.LEVEL.STATION){
					//获取机房Id
					pIds = "("+parentId+")";
					List roomIds = areaManagerImpl.getSubRoomIDs(pIds);
					for(int i = 0; i< roomIds.size(); i++){
						parentIds += roomIds.get(i)+ ",";
					}
				}else{
					return null;
				}
			}
			if(parentIds.length()>2){
				parentIds=parentIds.substring(0, parentIds.length()-1);
			}
			parentIds+=")";
		List<Map<String,Object>> dat = new ArrayList<Map<String,Object>>();
		Map<String,Object> count = new HashMap<String,Object>();
		
		if(!("()".equals(parentIds)||"()".equals(emsIdListString))){
			if(type.equals("neModel")){
				dat = resourceStatisticManagerMapper.getSubNeByIDs(parentIds, emsIdListString, start,limit);
				count = resourceStatisticManagerMapper.getSubNeByIDsCount(parentIds, emsIdListString);
			}else if(type.equals("unitName")){
				dat = resourceStatisticManagerMapper.getSubUnitByIDs(parentIds, emsIdListString,start,limit);
				count = resourceStatisticManagerMapper.getSubUnitByIDsCount(parentIds, emsIdListString);
			}else if(type.equals("ptpType")){
				dat = resourceStatisticManagerMapper.getSubPortByIDs(parentIds, emsIdListString,start,limit);
				count = resourceStatisticManagerMapper.getSubPortByIDsCount(parentIds, emsIdListString);
			}
			rv.put("total", count.get("total"));
		}else{
			rv.put("total", 0);
		}
		rv.put("rows", dat);
		return rv;
	}
	
	//组装in查询语句
	private String constructInSearchCondition(List list,String conditonColumn){
		String inSearchCondition = "(";
		for(int i = 0; i< list.size(); i++){
			if(i > 0){
				inSearchCondition += ",";
			}
			
			if(Map.class.isInstance(list.get(i))){
				inSearchCondition += ((Map)list.get(i)).get(conditonColumn);
			}else{
				inSearchCondition += list.get(i);
			}
		}
		inSearchCondition = inSearchCondition +")";
		
		return inSearchCondition;
	}
}
