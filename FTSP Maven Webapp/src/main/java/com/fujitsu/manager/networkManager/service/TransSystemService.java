package com.fujitsu.manager.networkManager.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.fujitsu.IService.ITransSystemService;
import com.fujitsu.abstractService.AbstractService;
import com.fujitsu.common.CommonException;
import com.fujitsu.dao.mysql.TransSystemMapper;

abstract public class TransSystemService extends AbstractService implements
		ITransSystemService {

	public static String[] PROTECTION_GROUP_TYPE = { "1+1 MSP", "1:N MSP",
			"2F BLSR", "4F BLSR", "1+1 ATM", "1:N ATM" };
	
	@Resource
	protected TransSystemMapper transSystemMapper;
	
	/*public Map<String,Object> getAllTransmissionSystem(Map<String,Object> param)throws CommonException{
		Map<String,Object> dataMap=new HashMap<String,Object>();
		List<Map<String,Object>> rowsList=transSystemMapper.getAllTransmissionSystem(param,0,0);
		dataMap.put("total", rowsList.size());
		dataMap.put("rows", rowsList);
		return dataMap;
	}*/
	public Map<String,Object> getAllLink(Map<String,Object> param)throws CommonException{
		Map<String,Object> dataMap=new HashMap<String,Object>();
		List<Map<String,Object>> rowsList=transSystemMapper.getAllLink(param,0,0);
		dataMap.put("total", rowsList.size());
		dataMap.put("rows", rowsList);
		return dataMap;
	}
}
