package com.fujitsu.manager.resourceStockManager.serviceImpl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import com.fujitsu.IService.ICommonManagerService;
import com.fujitsu.IService.IExportExcel;
import com.fujitsu.common.CommonDefine;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.ResourceStockMapper;
import com.fujitsu.manager.resourceStockManager.service.ResourceStockService;
import com.fujitsu.util.ExportExcelUtil;

public class ResourceStockServiceImpl extends ResourceStockService{

	@Resource
	public ResourceStockMapper resourceStockMapper;
	@Resource
	private ICommonManagerService commonManagerService;
	
	@Override
	public Map<String, Object> getResourceByTreeNodes(String resourceType,
			List<Map> treeNodes,int start,int limit) {
		// TODO Auto-generated method stub
		List<Map<String,Object>> neIdList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> returnList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> resourceList = new ArrayList<Map<String,Object>>();
		Map<String,Object> paramMap = new HashMap<String,Object>();
		Map<String,Object> returnMap = new HashMap<String,Object>();
		int count=0;
		
		neIdList = getNeIdList(treeNodes);

		paramMap.put("neIdList", neIdList);
		paramMap.put("start", start);
		paramMap.put("limit", limit);
		
		if(resourceType.equals(CommonDefine.RESOURCE_STOCK.RESOURCE_NE)){			
			
			count = resourceStockMapper.countNeResourceStock(paramMap);
			
			resourceList = resourceStockMapper.getNeResourceStock(paramMap);
			for(Map<String,Object> map : resourceList){
				if(map.get("areaId") != null){
					int areaId = Integer.valueOf(String.valueOf(map.get("areaId")).toString()).intValue();
					map.put("areaName", commonManagerService
								.getMulitLevelFullName(areaId, "T_RESOURCE_AREA",
										"RESOURCE_AREA_ID", "AREA_PARENT_ID",
										"AREA_NAME"));
				}				
				//显示方式
				if(map.get("neDisplayMode") != null){
					int displayMode = Integer.valueOf(String.valueOf(map.get("neDisplayMode")).toString()).intValue();
					map.put("neDisplayMode", CommonDefine.RESOURCE_STOCK.DISPLAY_MODE.get(displayMode));
				}				
				//管理类别
				if(map.get("manageCategory") != null){
					int manageCategory = Integer.valueOf(String.valueOf(map.get("manageCategory")).toString()).intValue();
					map.put("manageCategory", CommonDefine.RESOURCE_STOCK.MGMT_CATEGORY.get(manageCategory));
				}				
				//厂家
				if(map.get("factory") != null){
					int factory = Integer.valueOf(String.valueOf(map.get("factory")).toString()).intValue();
					map.put("factory", CommonDefine.FACTORY.get(factory));
				}
				
				returnList.add(map);
			}
		}else if(resourceType.equals(CommonDefine.RESOURCE_STOCK.RESOURCE_SHELF)){
			
			count = resourceStockMapper.countShelfResourceStock(paramMap);
			
			resourceList = resourceStockMapper.getShelfResourceStock(paramMap);
			for(Map<String,Object> map : resourceList){
				if(map.get("areaId") != null){
					int areaId = Integer.valueOf(String.valueOf(map.get("areaId")).toString()).intValue();
					map.put("areaName", commonManagerService
								.getMulitLevelFullName(areaId, "T_RESOURCE_AREA",
										"RESOURCE_AREA_ID", "AREA_PARENT_ID",
										"AREA_NAME"));
				}				
				//显示方式
				if(map.get("shelfDisplayMode") != null){
					int displayMode = Integer.valueOf(String.valueOf(map.get("shelfDisplayMode")).toString()).intValue();
					map.put("shelfDisplayMode", CommonDefine.RESOURCE_STOCK.DISPLAY_MODE.get(displayMode));
				}				
				//厂家
				if(map.get("factory") != null){
					int factory = Integer.valueOf(String.valueOf(map.get("factory")).toString()).intValue();
					map.put("factory", CommonDefine.FACTORY.get(factory));
				}
				returnList.add(map);
			}
		}else if(resourceType.equals(CommonDefine.RESOURCE_STOCK.RESOURCE_UNIT)){
			
			count = resourceStockMapper.countUnitResourceStock(paramMap);
			
			resourceList = resourceStockMapper.getUnitResourceStock(paramMap);
			for(Map<String,Object> map : resourceList){
				if(map.get("areaId") != null){
					int areaId = Integer.valueOf(String.valueOf(map.get("areaId")).toString()).intValue();
					map.put("areaName", commonManagerService
								.getMulitLevelFullName(areaId, "T_RESOURCE_AREA",
										"RESOURCE_AREA_ID", "AREA_PARENT_ID",
										"AREA_NAME"));
				}				
				//显示方式
				if(map.get("unitDisplayMode") != null){
					int displayMode = Integer.valueOf(String.valueOf(map.get("unitDisplayMode")).toString()).intValue();
					map.put("unitDisplayMode", CommonDefine.RESOURCE_STOCK.DISPLAY_MODE.get(displayMode));
				}				
				//服务状态
				if(map.get("serviceState") != null){
					int serviceState = Integer.valueOf(String.valueOf(map.get("serviceState")).toString()).intValue();
					map.put("serviceState", CommonDefine.RESOURCE_STOCK.SERVICE_STATE.get(serviceState));
				}				
				//厂家
				if(map.get("factory") != null){
					int factory = Integer.valueOf(String.valueOf(map.get("factory")).toString()).intValue();
					map.put("factory", CommonDefine.FACTORY.get(factory));
				}
				returnList.add(map);
			}
		}else if(resourceType.equals(CommonDefine.RESOURCE_STOCK.RESOURCE_PTP)){
			
			count = resourceStockMapper.countPtpResourceStock(paramMap);
			
			resourceList = resourceStockMapper.getPtpResourceStock(paramMap);
			for(Map<String,Object> map : resourceList){
				if(map.get("areaId") != null){
					int areaId = Integer.valueOf(String.valueOf(map.get("areaId")).toString()).intValue();
					map.put("areaName", commonManagerService
								.getMulitLevelFullName(areaId, "T_RESOURCE_AREA",
										"RESOURCE_AREA_ID", "AREA_PARENT_ID",
										"AREA_NAME"));
				}				
				//显示方式
				if(map.get("ptpDisplayMode") != null){
					int displayMode = Integer.valueOf(String.valueOf(map.get("ptpDisplayMode")).toString()).intValue();
					map.put("ptpDisplayMode", CommonDefine.RESOURCE_STOCK.DISPLAY_MODE.get(displayMode));
				}
				//业务类型
				if(map.get("domain") != null){
					int domain = Integer.valueOf(String.valueOf(map.get("domain")).toString()).intValue();
					map.put("domain", CommonDefine.PM.DOMAIN_TYPE.get(domain));
				}
				//厂家
				if(map.get("factory") != null){
					int factory = Integer.valueOf(String.valueOf(map.get("factory")).toString()).intValue();
					map.put("factory", CommonDefine.FACTORY.get(factory));
				}
				returnList.add(map);
			}
		}

		returnMap.put("rows", returnList);
		returnMap.put("total", count);
		
		return returnMap;
	}
	
	private List<Map<String,Object>> getNeIdList(List<Map> treeNodes){
		List<Map<String,Object>> neIdList = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> idList;
		Map<String,Object> paramMap;
		Map<String,Object> neMap;
		neMap = new HashMap<String,Object>();
		neMap.put("neId", 0);
		neIdList.add(neMap);
		for(Map node : treeNodes){
			int nodeLevel = Integer.valueOf(String.valueOf(node.get("nodeLevel")).toString()).intValue();
			int nodeId = Integer.valueOf(String.valueOf(node.get("nodeId")).toString()).intValue();
			if(nodeLevel == CommonDefine.TREE.NODE.NE ){
				neMap = new HashMap<String,Object>();
				neMap.put("neId", nodeId);
				neIdList.add(neMap);
			}else if(nodeLevel == CommonDefine.TREE.NODE.SUBNET){
				idList = new ArrayList<Map<String,Object>>();
				paramMap = new HashMap<String,Object>();
				paramMap.put("subnetId", nodeId);
				idList = resourceStockMapper.getNeIdListBySubnetId(paramMap);
				neIdList.addAll(idList);
			}else if(nodeLevel == CommonDefine.TREE.NODE.EMS){
				idList = new ArrayList<Map<String,Object>>();
				paramMap = new HashMap<String,Object>();
				paramMap.put("emsId", nodeId);
				idList = resourceStockMapper.getNeIdListByEmsId(paramMap);
				neIdList.addAll(idList);
			}else if(nodeLevel == CommonDefine.TREE.NODE.EMSGROUP){
				idList = new ArrayList<Map<String,Object>>();
				paramMap = new HashMap<String,Object>();
				paramMap.put("emsGroupId", nodeId);
				idList = resourceStockMapper.getNeIdListByEmsGroupId(paramMap);
				neIdList.addAll(idList);
			}
		}
		return neIdList;

	}

	@Override
	public boolean changeDisplayMode(String displayModeType,List<Integer> neIdList,
			String resourceType) {
		// TODO Auto-generated method stub
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("displayMode", displayModeType);
		map.put("neIdList", neIdList);
		
		if(resourceType.equals(CommonDefine.RESOURCE_STOCK.RESOURCE_NE)){
			map.put("tableName", "t_base_ne");
			map.put("columnName", "BASE_NE_ID");
		}else if(resourceType.equals(CommonDefine.RESOURCE_STOCK.RESOURCE_SHELF)){
			map.put("tableName", "t_base_shelf");
			map.put("columnName", "BASE_SHELF_ID");
		}else if(resourceType.equals(CommonDefine.RESOURCE_STOCK.RESOURCE_UNIT)){
			map.put("tableName", "t_base_unit");
			map.put("columnName", "BASE_UNIT_ID");
		}else if(resourceType.equals(CommonDefine.RESOURCE_STOCK.RESOURCE_PTP)){
			map.put("tableName", "t_base_ptp");
			map.put("columnName", "BASE_PTP_ID");
		}
		Boolean result = resourceStockMapper.changeDisplayMode(map);
		return result;
	}

	@Override
	public boolean saveChangedInfo(Map<String, Object> map) {
		// TODO Auto-generated method stub
		String resourceType = String.valueOf(map.get("resourceType"));
		if(resourceType.equals(CommonDefine.RESOURCE_STOCK.RESOURCE_NE)){
			map.put("tableName", "t_base_ne");
			map.put("columnName", "BASE_NE_ID");
		}else if(resourceType.equals(CommonDefine.RESOURCE_STOCK.RESOURCE_SHELF)){
			map.put("tableName", "t_base_shelf");
			map.put("columnName", "BASE_SHELF_ID");
		}else if(resourceType.equals(CommonDefine.RESOURCE_STOCK.RESOURCE_UNIT)){
			map.put("tableName", "t_base_unit");
			map.put("columnName", "BASE_UNIT_ID");
		}else if(resourceType.equals(CommonDefine.RESOURCE_STOCK.RESOURCE_PTP)){
			map.put("tableName", "t_base_ptp");
			map.put("columnName", "BASE_PTP_ID");
		}
		boolean result = resourceStockMapper.saveChangedInfo(map);
		
		return result;
	}

	@Override
	public String exportResourceStock(String resourceType, List<Map> nodeList)throws CommonException {
		// TODO Auto-generated method stub
		String resultMessage = "";
		String name = "";
		int flag = -1;
		String path = CommonDefine.PATH_ROOT + CommonDefine.EXCEL.UPLOAD_PATH;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		
		int type = Integer.valueOf(resourceType).intValue();
		switch(type){
		case 0:
			flag = CommonDefine.EXCEL.NE_RESOURCES_TOCK_LIST_EXPORT;
			name = "网元资源存量";
			break;
		case 1:
			flag = CommonDefine.EXCEL.SHELF_RESOURCES_TOCK_LIST_EXPORT;
			name = "子架资源存量";
			break;
		case 2:
			flag = CommonDefine.EXCEL.UNIT_RESOURCES_TOCK_LIST_EXPORT;
			name = "板卡资源存量";
			break;
		case 3:
			flag = CommonDefine.EXCEL.PTP_RESOURCES_TOCK_LIST_EXPORT;
			name = "端口资源存量";
			break;
		}
		String fileName = name + "_"+ formatter.format(new Date(System.currentTimeMillis()));
		IExportExcel ex2 = new ExportExcelUtil(path, fileName, "ExoportExcel",
				1000);
		try{
			Map<String, Object> result = getResourceByTreeNodes(resourceType,nodeList,0,0);
			List<Map> rows = (List<Map>) result.get("rows");
			// 导出数据
			resultMessage = ex2.writeExcel(rows, flag, false);
		}catch(Exception e){
			e.printStackTrace();
			ex2.close();
		}
		
		return resultMessage;
	}

	@Override
	public boolean checkNeNameExit(Map<String,Object> map) {
		// TODO Auto-generated method stub
		boolean returnResult = true;
		try{
			List<Map<String,Object>> resultList = resourceStockMapper.checkNeNameExit(map);
			returnResult = (resultList.size() > 0 ? true : false);
		}catch(Exception e){
			e.printStackTrace();
			returnResult = false;
		}
		
		return returnResult;
	}
	
}