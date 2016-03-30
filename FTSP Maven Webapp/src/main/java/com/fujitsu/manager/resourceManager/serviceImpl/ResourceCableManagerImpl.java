package com.fujitsu.manager.resourceManager.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.ResourceCableManagerMapper;
import com.fujitsu.manager.resourceManager.service.ResourceCableManagerService;

@Service
@Transactional(rollbackFor = Exception.class)
public class ResourceCableManagerImpl extends ResourceCableManagerService {

	@Resource
	private ResourceCableManagerMapper resourceCableManagerMapper; 
	@Override 
	public Map<String, Object> getCables(Map<String,Object> map,int start,int limit)
			throws CommonException {
		Map<String, Object> rtnMap = new HashMap<String,Object>();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		int total=0;
		try{
			result = resourceCableManagerMapper.getCables(map,start,limit);
			total = resourceCableManagerMapper.getCablesCount(map); 
			rtnMap.put("total", total);
			rtnMap.put("rows", result);
		}catch (Exception e) {
			throw new CommonException(e, -1, "获取光缆信息列表失败！");
		}
		return rtnMap;
	}
	 
	@Override 
	public void addCables(Map<String,Object> map) throws CommonException{  
		try{ 
			resourceCableManagerMapper.addCables(map);
		}catch (Exception e) {
			throw new CommonException(e, -1, "新增光缆失败！");
		}	 
	}
	 
	@Override
	public boolean cablesExist(Map<String,Object> map)throws CommonException{
		boolean flag = true;
		try {
			List<Map<String, Object>> lst = resourceCableManagerMapper.cablesExist(map);
			if(lst == null || lst.size() == 0){
				flag = false;
			}
		} catch (Exception e) {
			throw new CommonException(e, -1, "查询光缆是否存在失败！");
		}
		return flag;
	}
	
	@Override
	public Map<String,Object> getCablesInfo(int cablesId) throws CommonException{ 
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			data = resourceCableManagerMapper.getCablesInfoById(cablesId);
		}catch (Exception e) {
			throw new CommonException(e, -1, "获取指定光缆信息失败！");
		}	
		return data;
	}
	
	@Override
	public boolean modCablesCheck(Map<String,Object> map) throws CommonException{
		List<Map<String, Object>> lst = resourceCableManagerMapper.modCablesCheck(map);
		if(lst != null && lst.size()>0){
			return true;
		}
		return false;
	}
	
	@Override
	public void modCables(Map<String,Object> map) throws CommonException {
		try{
			resourceCableManagerMapper.modCables(map); 
		}catch (Exception e) {
			throw new CommonException(e, -1, "修改指定光缆信息失败！");
		}	
	} 
	
	@Override
	public List<Map<String, Object>> getSubCable(int cablesId) 
			throws CommonException{
		try{
			return resourceCableManagerMapper.getSubCable(cablesId);
		}catch (Exception e) {
			throw new CommonException(e, -1, "获取指定光缆段信息失败！");
		}	
	}
	
	@Override
	public void delCables(int cablesId)throws CommonException {  
		try{ 
			resourceCableManagerMapper.delCables(cablesId); 
		}catch(Exception e){
			throw new CommonException(e, -1, "删除指定光缆失败！");
		}  
	}
	
	@Override
	public Map<String, Object> getCableList(Map<String,Object> map,int start,int limit)
			throws CommonException {
		Map<String, Object> rtnMap = new HashMap<String,Object>();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		int total=0;
		try{
			result = resourceCableManagerMapper.getCableList(map,start,limit);
			total = resourceCableManagerMapper.getCableListCount(map); 
			rtnMap.put("total", total);
			rtnMap.put("rows", result);
		}catch (Exception e) {
			throw new CommonException(e, -1, "获取光缆段信息列表失败！");
		}
		return rtnMap;
	}
	 
	@Override
	public Map<String,Object> getAllCodeNames() throws CommonException {
		Map<String, Object> rtnMap = new HashMap<String,Object>();
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		int total=0;
		try{
			result = resourceCableManagerMapper.getAllCodeNames(); 
			rtnMap.put("total", result.size());
			rtnMap.put("rows", result);
		}catch (Exception e) {
			throw new CommonException(e, -1, "获取所有光缆信息(ID、名称和代号)失败！");
		}
		return rtnMap;
	}
	
	@Override
	public boolean cableExist(Map<String,Object> map)throws CommonException{
		boolean flag = true;
		try {
			List<Map<String, Object>> lst = resourceCableManagerMapper.cableExist(map);
			if(lst == null || lst.size() == 0){
				flag = false;
			}
		} catch (Exception e) {
			throw new CommonException(e, -1, "查询光缆段是否存在失败！");
		}
		return flag;
	}
	
	@Override
	public void addCable(Map<String,Object> map)throws CommonException{  
		try{
			int cnt = 0;
			resourceCableManagerMapper.addCable(map);  
			cnt = Integer.parseInt(map.get("fiberCount").toString()); 
			for(int i=0;i<cnt;i++){  
				resourceCableManagerMapper.addFiber(Integer.parseInt(map.get("newId").toString()),String.valueOf(i+1),map);
			}  
		}catch (Exception e) {
			throw new CommonException(e, -1, "新增光缆段失败！");
		}	 
	} 
	
	@Override
	public Map<String,Object> getCableInfo(int cableId) throws CommonException {
		Map<String, Object> data = new HashMap<String,Object>();
		try{
			data = resourceCableManagerMapper.getCableInfo(cableId);
		}catch (Exception e) {
			throw new CommonException(e, -1, "获取指定光缆段信息失败！");
		}	
		return data;
	}
	
	@Override
	public boolean modCableCheck(Map<String,Object> map) throws CommonException{
		List<Map<String, Object>> lst = resourceCableManagerMapper.modCableCheck(map);
		if(lst != null && lst.size()>0){
			return true;
		}
		return false;
	}
	
	@Override
	public int modifyCable(Map<String,Object> map) throws CommonException {  
		try{
			int originalValue = Integer.valueOf(map.get("originalValue").toString());
			int fiberCount = Integer.valueOf(map.get("fiberCount").toString());
			int cnt = 0;
			if(fiberCount>originalValue){
				cnt = fiberCount-originalValue; 
				for(int i=0;i<cnt;i++){  
					resourceCableManagerMapper.addFiber(Integer.parseInt(map.get("cableId").toString()),
							String.valueOf(originalValue+1),map);
					originalValue++;
				}  
			}else if(fiberCount<originalValue){
				cnt = originalValue-fiberCount; 
				int count=0;
				for(int i=originalValue;i>fiberCount;i--){  
					count=resourceCableManagerMapper.countFiberRelateLink(Integer.parseInt(map.get("cableId").toString()),i);
					if(count>0){
						 return 0; 
					}
				} 
				for(int i=originalValue;i>fiberCount;i--){  
					resourceCableManagerMapper.deleteFiberList(Integer.parseInt(map.get("cableId").toString()),i);
				} 
			} 
			resourceCableManagerMapper.modifyCable(map);  
		}catch (Exception e) {
			throw new CommonException(e, -1, "修改光缆段失败！");
		} 
		return 1;
	}  
	
	@Override
	public List<Map<String, Object>> getLinkById(int cableId) throws CommonException{
		try{
			return resourceCableManagerMapper.getLinkById(cableId);
		}catch (Exception e) {
			throw new CommonException(e, -1, "获取链路信息失败！");
		}	
	}
	
	@Override
	public List<Map<String, Object>> getOdfById(int cableId) throws CommonException{
		try{
			return resourceCableManagerMapper.getOdfById(cableId);
		}catch (Exception e) {
			throw new CommonException(e, -1, "获取Odf信息失败！");
		}	
	}
	
	@Override
	public void deleteCable(int cableId) throws CommonException { 
		try{ 
			//删除和光缆段和未关联的光纤
			resourceCableManagerMapper.deleteFiber(cableId); 
			resourceCableManagerMapper.deleteCable(cableId);  
		}catch(Exception e){
			throw new CommonException(e, -1, "删除指定光缆段失败！");
		}  
	} 

	@Override
	public Map<String,Object> getFiberResourceList(int cableId,int limit,int start) 
			throws CommonException { 
		List<Map<String,Object>> fiberList = resourceCableManagerMapper.getFiberList(cableId,limit,start);
		int count = resourceCableManagerMapper.countFiberList(cableId); 
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total", count);
		result.put("rows", fiberList);
		
		return result;
	}
	@Override
	public Map<String,Object> getFiberListByCableId(int cableId, int limit, int start) throws CommonException {
		List<Map<String,Object>> fiberList = resourceCableManagerMapper.getFiberListByCableId(cableId,limit,start);
		int count = resourceCableManagerMapper.countFiberList(cableId);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total", count);
		result.put("rows", fiberList);
		return result;
	}
	
 	@Override
	public void modifyFiberResource(List<Map<String,Object>> fiberList)  throws CommonException { 
		try{ 
			for(Map o: fiberList){
				resourceCableManagerMapper.modifyFiberResource(o); 
			} 
		}catch(Exception e){
			throw new CommonException(e, -1, "修改光纤失败！");
		}	 
	}  
}
	 
