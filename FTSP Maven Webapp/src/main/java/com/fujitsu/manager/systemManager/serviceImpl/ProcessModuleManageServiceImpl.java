package com.fujitsu.manager.systemManager.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.ProcessModuleManageMapper;
import com.fujitsu.manager.systemManager.service.ProcessModuleManageService;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProcessModuleManageServiceImpl extends
		ProcessModuleManageService {
	@Resource
	private ProcessModuleManageMapper processModuleManageMapper;
	@Override
	public Map<String, Object> getProcessModuleManageData(int startNumber,
			int pageSize) throws CommonException {
		
		Map<String, Object> map=new HashMap<String, Object>();
		Map<String, Object> returnMap=new HashMap<String, Object>();
		List<Map<String, Object>> enigneerList = new ArrayList<Map<String, Object>>();
		int total=processModuleManageMapper.countProcessModuleDataList(map);
		map.put("startNumber", startNumber);
		map.put("pageSize", pageSize);
		enigneerList=processModuleManageMapper.selectProcessModuleDataList(map);
		returnMap.put("rows", enigneerList);
		returnMap.put("total", total);
		return returnMap;
	}
	@Override
	public boolean changeState(String ids,int flag) {
		// TODO Auto-generated method stub
		String[] idarr=ids.split(",");
		for (String string : idarr) {
			Map<String,Object> map =new HashMap<String, Object>();
			map.put("ID", string);
			map.put("STATE", flag);
			processModuleManageMapper.updateState(map);
		}
		return true;
	}

}
