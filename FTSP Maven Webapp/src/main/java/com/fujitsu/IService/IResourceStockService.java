package com.fujitsu.IService;

import java.util.List;
import java.util.Map;

import com.fujitsu.common.CommonException;

public interface  IResourceStockService{
	
	public Map<String,Object> getResourceByTreeNodes(String resourceType,List<Map> treeNodes,int start,int limit);
	
	public boolean changeDisplayMode(String displayModeType,List<Integer> neIdList,String resourceType);
	
	public boolean checkNeNameExit(Map<String,Object> map);
	
	public boolean saveChangedInfo(Map<String,Object> map);
	
	public String exportResourceStock(String resourceType,List<Map> nodeList)throws CommonException;
	
}