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
	public Map<String,Object> getFiberListByCableId(int cableId, int limit, int start) throws CommonException {
		List<Map<String,Object>> fiberList = resourceCableManagerMapper.getFiberListByCableId(cableId,limit,start);
		int count = resourceCableManagerMapper.countFiberList(cableId);
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("total", count);
		result.put("rows", fiberList);
		return result;
	}
}
	 
