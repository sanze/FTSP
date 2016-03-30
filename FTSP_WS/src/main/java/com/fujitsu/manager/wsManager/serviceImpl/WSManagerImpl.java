package com.fujitsu.manager.wsManager.serviceImpl;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebService;

import net.sf.json.JSONArray;

import com.fujitsu.dao.mysql.WSManagerMapper;
import com.fujitsu.manager.wsManager.service.WSManagerService;

@WebService
public class WSManagerImpl extends WSManagerService{
	@Resource 
	private WSManagerMapper mapper;
	
	
	@Override
	public String getEmsList() {
		
		List<HashMap> data = mapper.getEmsList();
		
		JSONArray result = JSONArray.fromObject(data);
		
		return	result.toString();
	}

	@Override
	public String getNeList(Integer emsId, String neName) {
		List<HashMap> data = mapper.getNeList(emsId, neName);
		
		JSONArray result = JSONArray.fromObject(data);
		
		return	result.toString();
	}

	@Override
	public String getUnitList(Integer neId, String unitName) {
		List<HashMap> data = mapper.getUnitList(neId, unitName);
		
		JSONArray result = JSONArray.fromObject(data);
		
		return	result.toString();
	}

	@Override
	public String getPtpList(Integer unitId) {
		List<HashMap> data = mapper.getPtpList(unitId);
		
		JSONArray result = JSONArray.fromObject(data);
		
		return	result.toString();
	}

}
